package xmax.TranServer;

import java.net.*;
import java.io.*;
import java.io.EOFException;
import xmax.util.HttpUtils;

import xmax.util.Log.*;
import xmax.util.xml.DOMutil;
import xmax.util.TypedProperties;
import xmax.crs.GnrcCrs;
import xmax.crs.Amadeus.AmadeusAPICrs;
import xmax.TranServer.TranServerException;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.w3c.dom.Document;

import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 ***********************************************************************
 * This class is instantiated every time that the TranListener service
 * receives a request from a client; each instance manages an individual
 * connection with a client.
 *
 * <p>Three possible types of request interfaces can be accepted
 * by TranClientConnection :
 * <ul><li>The Airware Native Fixed Length Interface
 *     <li>A TransactionServer native object Request
 *     <li>The Airware XML Interface (not implemented yet)
 * </ul>
 *
 * @author	 David Fairchild
 * @version  1.x Copyright (c) 1999
 *
 * @see      TranListener
 * @see      #CrsConnection
 * @see      handleAsciiSocketRequest
 * @see      sendReceiveNativeAscii
 * @see      handleJavaObjectSocketRequest
 * @see      runJavaObjectRequest
 ***********************************************************************
 */

public class TranClientConnection implements Runnable
{
 private boolean Stop;
 private SocketLogger socketlogger;
 private Socket ClientSocket;
 protected GnrcCrs crsHost;
 private FileLogger TermLogFile;
 private File RequestLogFile;
 // new stuff for input streams
 private BufferedInputStream inBuffer;
 private OutputStream        outstream;
 private ObjectInputStream   objReader;
 private ObjectOutputStream  objWriter;
 private BufferedReader xmlReader;
 private PrintWriter    xmlWriter;
 private static final int UNKNOWN_CONNECTION     = 0;
 private static final int NATIVE_CONNECTION      = 1;
 private static final int JAVA_OBJECT_CONNECTION = 2;
 private static final int XML_CONNECTION         = 3;
 private int connectionType;
 protected static List gdsList;
 private static int iNumTranClientConnections = 0;

  /**
   ***********************************************************************
   * The constructor accepts a Socket object as its only parameter
   ***********************************************************************
   */
  public TranClientConnection(final Socket aSocket)
    {
    ClientSocket   = aSocket;
    connectionType = UNKNOWN_CONNECTION;
    Stop           = false;
    incThreadCount();

    if ( (gdsList instanceof List) == false )
      {
      gdsList = new ArrayList();

      // set up timer to check for idle GDS connections every 10 seconds
      final int INACTIVITY_CHECK_TIMEOUT = 10 * 1000;
      final Timer inactivityTimer = new Timer(INACTIVITY_CHECK_TIMEOUT, new CloseExpiredConnections() );
      inactivityTimer.start();
      }
    }

  /**
   ***********************************************************************
   *  The method which runs all connections between Airware and the GDS world
   ***********************************************************************
   */
  public void run()
    {
    try
      {
      setClientSocket(ClientSocket);

      // handle requests as they come in
      while ( Stop == false )
        {
        if ( ClientSocket instanceof Socket )
          {
          if ( connectionType == JAVA_OBJECT_CONNECTION )
            handleJavaObjectSocketRequest();
          else if ( connectionType == XML_CONNECTION )
            handleXmlSocketRequest();
          else if ( connectionType == NATIVE_CONNECTION )
            handleAsciiSocketRequest();
          }

        Thread.yield();
        }
      }
    catch (SocketCloseException e)
      {
      // this is a normal completion for the TA implementation
      final String sMsg = 
				"Apparent Normal Completion of TranClientConnection.run(): " + e.toString();
      if (crsHost instanceof GnrcCrs)
        AppLog.LogWarning(sMsg, getClientIP(), crsHost.getConnectionName());
      else
        AppLog.LogWarning(sMsg, getClientIP(), "null crs");
      }
    catch (Exception e)
      {
			String sMsg = "Unexpected Exception in TranClientConnection.run(): " + e.toString();
      if (crsHost instanceof GnrcCrs)
        AppLog.LogError(sMsg,getClientIP(),crsHost.getConnectionName());
      else
        AppLog.LogError(sMsg,getClientIP(),null);
			System.err.println(sMsg);
			e.printStackTrace();
      }

    // do cleanup
    decThreadCount();
    finalize();
    }

  /**
   ***********************************************************************
   * This method disconnects this TranClientConnection from a back-end host
   ***********************************************************************
   */
  private void disconnectFromCrsHost()
    {
    try
      {
      if ( TermLogFile instanceof FileLogger )
        {
        AppLog.removeLogger(TermLogFile);
        TermLogFile = null;
        }

      if ( crsHost instanceof GnrcCrs )
        {
        removeGdsFromList(crsHost);
        crsHost.Disconnect();
        crsHost = null;
        Thread.sleep(2000);     // wait two seconds
        }
      }
    catch (Exception e)
      {
      AppLog.LogError(
          "Exception occured while disconnecting from crsHost: "
          + e.toString());
      }
    }


  /**
   ***********************************************************************
   * This procedure removes the crs connection from the pool of connections
   ***********************************************************************
   */
  private synchronized void removeGdsFromList(final GnrcCrs aCrs)
    {
    if ( aCrs instanceof GnrcCrs )
      {
      while ( gdsList.contains(aCrs) )
        gdsList.remove(aCrs);
      }
    }

  /**
   ***********************************************************************
   * This method retrieves an array of {@link GnrcCrs} objects corresponding
   * to the HostCode passed; it then loops through this array and attempts
   * to open the Terminal Address (TA) associated with the GnrcCrs object;
   * it returns the first GnrcCrs object for which it was able to open a TA.
   *
   * @see xmax.crs.GnrcCrs
   * @see xmax.crs.GnrcCrs.openTA
   * @see getSignOns
   * @see getSingleSignOn
   ***********************************************************************
   */
  private void connectToCrsHost(final String aHostCode) throws Exception
    {
    if ( crsHost instanceof GnrcCrs )
      disconnectFromCrsHost();

    if ( GnrcFormat.IsNull(aHostCode) )
      throw new TranServerException("Host type not set");

    // make an array of all the connections for the given host code
    String sType;
    final TypedProperties[] conns = ConfigTranServer.getSignOns("hostCode",aHostCode);

    if (conns == null)
      throw new TranServerException(
          "No GDS of type '" + aHostCode + 
          "' found in the configuration file. Unable to connect to GDS.");

    StringBuffer connErrs = new StringBuffer();

    for ( int i = 0; i < conns.length; i++ )
      {
      sType = conns[i].getProperty("type","");

      // create the appropriate instance of a GnrcCrs object
   //   if ( sType.equals("SabreCrs") || aHostCode.equals("AA") )
   //     crsHost= new SabreCrs(conns[i]);
   //   else if ( sType.equals("ApolloCrs") || aHostCode.equals("1V") )
   //     crsHost = new ApolloCrs(conns[i]);
   //   else if ( sType.equals("WorldspanDirCrs") )
   //     crsHost = new WorldspanDirCrs(conns[i]);
   //   else if ( sType.equals("WorldspanCrs") || aHostCode.equals("1P") )
   //     crsHost = new WorldspanCrs(conns[i]);
      if ( sType.equals("AmadeusAPICrs"))
        crsHost = new AmadeusAPICrs(conns[i]);
   //   else if ( sType.equals("AmadeusCrs") || aHostCode.equals("1A") )
   //     crsHost = new AmadeusCrs(conns[i]);
      else
        crsHost = null;

      // try to connect to this crs
      if ( crsHost instanceof GnrcCrs )
        {
        try
          {
          crsHost.Connect();
          addGdsToList(crsHost);
          setFileLogger();
          return;
          }
        catch (Exception e)
          {
          crsHost.Disconnect();
          connErrs.append("Try" + (i+1) +": ");
          connErrs.append(e.toString() + " - ");
          AppLog.LogWarning(
              "Connection '" + crsHost.getConnectionName() + 
              "' is not available - " + e.toString() );
          }
        }
      } // end for

    crsHost = null;
    throw new TranServerException(
        "Unable to find available TA for host " + aHostCode 
        + ": " + connErrs.toString());
    }

  /**
   ***********************************************************************
   * This procedure adds the crs connection to the pool of connections
   ***********************************************************************
   */
  private synchronized void addGdsToList(final GnrcCrs aCrs)
    {
    if ( aCrs instanceof GnrcCrs )
      {
      if ( gdsList.contains(aCrs) == false )
        gdsList.add(aCrs);
      }
    }

  /**
   ***********************************************************************
   * This function returns the GDS connection with the given session ID
   *
   * @see xmax.crs.GnrcCrs
   ***********************************************************************
   */
  private void setToExistingGdsConnection(final int aSessionID)
    {
    for ( int i = 0; i < gdsList.size(); i++ )
      {
      crsHost = (GnrcCrs )gdsList.get(i);
      if ( crsHost instanceof GnrcCrs )
        {
        if ( crsHost.getSessionID() == aSessionID )
          {
          setFileLogger();
          return;
          }
        }
      }

    crsHost = null;
    }


  /**
   ***********************************************************************
   * This procedure waits to establish a connection to the CRS host
   ***********************************************************************
   */
  private void setFileLogger()
    {
    if ( TermLogFile instanceof FileLogger )
      AppLog.removeLogger(TermLogFile);

    // setup the terminal file logger
    final String sTaName = crsHost.getConnectionName();
    try
      {
      // Check whether a FileLogger for the TA has already been created.
      // This is needed for the AmadeusAPICrs which has a single TA
      // name but may have multiple connections/conversations attached to it.
      // This is not a issue in the case of Innosys TAs where there is a
      // one-to-one correspondence between TAs and Connections.
      if (!AppLog.hasFileLogger(sTaName))
        {
        TermLogFile = createConnectionLogger(sTaName);
        AppLog.addLogger(TermLogFile);
        }
      }
    catch (Exception e)
      {
      AppLog.LogError("Unable to setup FileLogger for connection " + sTaName + ": " + e);
      }

    }

  /**
   ***********************************************************************
   * This method sets the socket for this thread to use in communicating
   * with the client
   ***********************************************************************
   */
  private void setClientSocket(final Socket aSocket) throws Exception
    {
    ClientSocket = aSocket;

    if ( ClientSocket instanceof Socket )
      {
      inBuffer     = new BufferedInputStream( ClientSocket.getInputStream() );
      outstream    = ClientSocket.getOutputStream();

      AppLog.LogWarning(
          "Client socket opened - waiting for first request " +
          "to determine connection type...",getClientIP(),null);

      if ( connectionType == UNKNOWN_CONNECTION )
        connectionType = getConnectionType(inBuffer);

      if ( connectionType == JAVA_OBJECT_CONNECTION )
        {
        objReader = new ObjectInputStream(inBuffer);
        objWriter = new ObjectOutputStream(outstream);
        }
      else if ( connectionType == XML_CONNECTION )
        {
        xmlReader = new BufferedReader( new InputStreamReader(inBuffer) );
        xmlWriter = new PrintWriter(outstream);
        }

      AppLog.LogWarning(
          "First request received through socket - connection type is: " +
          connectionType , getClientIP(),null);
      }
    else
      {
      AppLog.LogWarning("client socket is null",getClientIP(),null);
      ClientSocket = null;
      inBuffer     = null;
      outstream    = null;
      objReader    = null;
      objWriter    = null;
      xmlReader    = null;
      xmlWriter    = null;
      }
    }

  /**
   ***********************************************************************
   * This function reads the objReader of type ObjectInputStream,
   * casts it to a ReqTranServer object, calls runJavaObjectRequest,
   * and writes the returned ObjectOutputStream through the objWriter.
   *
   * @see  #objReader
   * @see  #objWriter
   * @see  sendReceiveNativeAscii
   * @see  readNativeAscii
   * @see  writeNativeAscii
   ***********************************************************************
   */
  private void handleJavaObjectSocketRequest() throws Exception
    {
    try
      {
      final ReqTranServer tRequest = (ReqTranServer )objReader.readObject();
      runJavaObjectRequest(tRequest);
      objWriter.writeObject(tRequest);
      }
    catch ( EOFException e )
      {
      throw new SocketCloseException("Java object socket Closed");
      }
    catch ( Exception e )
      {
      throw(e);
      }
    }

  /**
   ***********************************************************************
   * This method accepts a ReqTranServer request object,
   * and runs the request through the appropriate CrsConnection;
   * running the request calls a given Conversation from the chosen,
   * and this Conversation populates the Response fields in the Request object.
   *
   * <b>Connecting to a Computer Reservation System (CRS)</b><br>
   * If this is the first time that the client is connecting to a Computer
   * Reservation System (that is to say: CrsConnection = null) then we
   * connect to whatever CRS was passed through the request's CrsCode by
   * calling the method {@link connectToCrsHost}.
   * The CrsCode passed through the CrsCode is either:
   * <ul>
   *   <li>the code passed in the request string</li>
   *   <li>if a code was not passed in the request string, use the default code
   *       {@link ConfigTranServer#DEFAULT_HOST} which is read from
   *       the configuration file: {@link ConfigTranServer#CONFIG_FILENAME}</li>
   *   <li>the default code <code>AA</code> (Sabre) hard-coded in
   *       {@link NativeAsciiReader.getDefaultCrsCode}</li>
   * </ul>
   *
   * <p>On the other hand, if the client has already connected once before
   * (that is to say: CrsConnection is not null) but the CrsCode requested
   * in the request string is different from that of the open connection,
   * then connect to the CrsCode requested in the request string.</p>
   *
   * <p>Otherwise, test the previously opened connection.  If it's open, proceed.
   * If the connection was closed (unexpectedly), reconnect.
   *
   * @see  ReqTranServer
   * @see  #CrsConnection
   * @see  xmax.crs.GnrcCrs
   * @see  connectToCrsHost
   * @see  handleJavaObjectSocketRequest
   ***********************************************************************
   */

  protected void runJavaObjectRequest(final ReqTranServer aRequest)
    {
    FileLogger filelogger = null;
    try
      {
      if ( aRequest instanceof ReqEndSession )
        {
        pleaseStop();
        String sMsg = "Connection closed at Client's request";
        if (crsHost instanceof GnrcCrs)
          AppLog.LogWarning(sMsg, getClientIP(), crsHost.getConnectionName());
        else
          AppLog.LogWarning(sMsg, getClientIP(), "null crs");
        return;
        }

      // check TA connection
      if ( (crsHost instanceof GnrcCrs) == false )
        connectToCrsHost(aRequest.getCrsCode());         // first time connect
      else if ( crsHost.getHostCode().equals(aRequest.getCrsCode()) == false )
        connectToCrsHost(aRequest.getCrsCode());         // different CRS
      else if ( crsHost.TestHostConnection() == false )
        connectToCrsHost(aRequest.getCrsCode());         // lost an existing connection

      // open up the log file for this request
      final String sLogDir = ConfigTranServer.logging.getProperty("currentLogDir","logs");
      final String sLogFileName = aRequest.getLogFileName(sLogDir);
      filelogger  = aRequest.startLogging(sLogFileName,crsHost);

      // if this is an Ascii or XML request, log the incoming string
      if (aRequest.AsciiRequest != null)
        AppLog.LogEvent(aRequest.AsciiRequest,LoggingEvent.CLIENT_TO_APP,
                        getClientIP(),crsHost.getConnectionName());

      // run the request
      crsHost.runUserRequest(aRequest);
      }
    catch (Exception e)
      {
      final String sConnectionName;
      if ( crsHost instanceof GnrcCrs )
        sConnectionName = crsHost.getConnectionName();
      else
        sConnectionName = null;

      if ( aRequest instanceof ReqTranServer )
        aRequest.saveException(e);
      }
    finally
      {
      if ( aRequest instanceof ReqTranServer )
        aRequest.stopLogging(filelogger);
      }
    }

  /**
   ***********************************************************************
   * This method reads the inBuffer input stream in Native Airware ascii format,
   * calls the sendReceiveNativeAscii, and returns the response as
   * the outstream OutputStream.
   *
   * @see  #inBuffer
   * @see  #outstream
   * @see  HandleAsciiRequest
   * @see  readNativeAscii
   * @see  writeNativeAscii
   ***********************************************************************
   */
  private void handleAsciiSocketRequest() throws Exception
    {
    final String sRequestString = readNativeAscii(inBuffer);
    final String sResponse = sendReceiveNativeAscii(sRequestString);
    writeNativeAscii(outstream,sResponse);
    }

  /**
   ***********************************************************************
   * This method reads the inBuffer input stream in XML format,
   * calls the sendReceiveXml, and returns the response as
   * the outstream OutputStream.
   *
   * @see  #inBuffer
   * @see  #outstream
   * @see  HandleAsciiRequest
   * @see  readNativeAscii
   * @see  writeNativeAscii
   ***********************************************************************
   */
  private void handleXmlSocketRequest() throws Exception
    {
    final String COOKIE_NAME = "SESSIONID";

    try
      {
      // get request from client
      final String[] headers   = HttpUtils.readHttpHeaders(xmlReader);
      final String sSessionID  = HttpUtils.getCookieValue(headers,COOKIE_NAME);
      if ( sSessionID instanceof String )
        {
        final int iSessionID = Integer.parseInt(sSessionID);
        if ( iSessionID > 0 )
          setToExistingGdsConnection(iSessionID);
        else
          crsHost = null;
        }
      else
        crsHost = null;

      final int iContentLength = HttpUtils.getContentLength(headers);
      final String sRequest    = HttpUtils.readHttpData(xmlReader,iContentLength);

      // forward request to GDS host
      final String sResponse = sendReceiveXml(sRequest);

      // send response to client
      final int STATUS_OK = 200;
      final String[] SESSION_COOKIE = new String[1];
      if ( this.Stop )
        SESSION_COOKIE[0] = "Set-Cookie: " + COOKIE_NAME + " = 0";
      else
        SESSION_COOKIE[0] = "Set-Cookie: " + COOKIE_NAME + " = " + crsHost.getSessionID();
      HttpUtils.writeHttpData(xmlWriter,STATUS_OK,sResponse,SESSION_COOKIE);
      }
    catch (SocketException se)
      {
      throw new SocketCloseException("HTTP/XML socket closed");
      }
    catch (Exception e)
      {
      throw(e);
      }
    }

  /**
   ***********************************************************************
   * This method 'transforms' a Native ascii request
   * into a ReqTranServer request object, calls the runJavaObjectRequest method,
   * and formats and returns the response in Native Airware ascii format.
   *
   * @param   aRequestString    in Native Airware format
   * @return  String            the corresponding response in Native Airware format
   * @see     runJavaObjectRequest
   * @see     handleAsciiSocketRequest
   ***********************************************************************
   */
  public String sendReceiveNativeAscii(final String aRequestString)
    {
    final String sConnectionName;
    if ( crsHost instanceof GnrcCrs )
      sConnectionName = crsHost.getConnectionName();
    else
      sConnectionName = null;


    // log the incoming string: moved to runJavaObjectRequest so that we can log first request
    //AppLog.LogEvent(aRequestString,LoggingEvent.CLIENT_TO_APP,getClientIP(),sConnectionName);

    // parse up the input request and create a request object
    ReqTranServer request = null;
    try
      {
      // get the request object
      request = NativeAsciiReader.getRequestObject(aRequestString,crsHost);
      request.AsciiRequest = aRequestString;
      }
    catch (Exception e)
      {
      final String sErrorResponse = NativeAsciiWriter.getErrorResponseString(e.toString(),aRequestString);
      AppLog.LogEvent(aRequestString,LoggingEvent.CLIENT_TO_APP,getClientIP(),sConnectionName);
      AppLog.LogEvent(sErrorResponse,LoggingEvent.APP_TO_CLIENT,getClientIP(),sConnectionName);
      return(sErrorResponse);
      }


    // run the request
    if ( request instanceof ReqEnableLogForwarding )
      {
      final String sTaName = ((ReqEnableLogForwarding )request).TaName;
      try
        {
        socketlogger = new SocketLogger(ClientSocket,LoggingEvent.ALL_MESSAGES,sTaName);
        AppLog.addLogger(socketlogger);
        }
      catch (Exception e)
        {
        request.saveException(e);
        }
      }
    else if ( request instanceof ReqDisableLogForwarding )
      AppLog.removeLogger(socketlogger);
    else
      runJavaObjectRequest(request);

    // create the string response
    final String sResponse = NativeAsciiWriter.getResponseString(request);
    request.AsciiResponse  = sResponse;

    // log the outgoing string
    if (crsHost instanceof GnrcCrs)
    {
      AppLog.LogEvent(sResponse,LoggingEvent.APP_TO_CLIENT,getClientIP(),crsHost.getConnectionName());
      AppLog.LogEvent("Message Response size = " + sResponse.length(),LoggingEvent.APP_TO_CLIENT,getClientIP(),crsHost.getConnectionName());
    }
    else
      AppLog.LogError(sResponse,getClientIP(),null);

    return(sResponse);

    } // end sendReceiveNativeAscii


  /**
   ***********************************************************************
   * This method 'transforms' an XML request
   * into a ReqTranServer request object, calls the runJavaObjectRequest method,
   * and formats and returns the response in XML format.
   *
   * @param   aRequestString    in XML format
   * @return  String            the corresponding response in Native Airware format
   * @see     runJavaObjectRequest
   * @see     handleAsciiSocketRequest
   ***********************************************************************
   */
  public String sendReceiveXml(final String aRequestString)
    {
    final String sConnectionName;
    if ( crsHost instanceof GnrcCrs )
      sConnectionName = crsHost.getConnectionName();
    else
      sConnectionName = null;

    // log the incoming string: moved to runJavaObjectRequest
    //AppLog.LogEvent(aRequestString,LoggingEvent.CLIENT_TO_APP,getClientIP(),sConnectionName);

    // parse up the input request and create a request object
    ReqTranServer request = null;
    Document docReq = null;
    try
      {
      // get the request object
      docReq = DOMutil.stringToDom(aRequestString);
      request = XmlReader.getRequestObject(docReq,crsHost);
      request.AsciiRequest = aRequestString;
      }
    catch (Exception e)
      {
      final Document docError = XmlWriter.createErrorResponseDoc( e.toString() );
      final String sErrorResponse = DOMutil.domToString(docError);
      AppLog.LogEvent(sErrorResponse,LoggingEvent.APP_TO_CLIENT,getClientIP(),sConnectionName);
      return(sErrorResponse);
      }


    // run the request
    if ( request instanceof ReqEnableLogForwarding )
      {
      final String sTaName = ((ReqEnableLogForwarding )request).TaName;
      try
        {
        socketlogger = new SocketLogger(ClientSocket,LoggingEvent.ALL_MESSAGES,sTaName);
        AppLog.addLogger(socketlogger);
        }
      catch (Exception e)
        {
        request.saveException(e);
        }
      }
    else if ( request instanceof ReqDisableLogForwarding )
      AppLog.removeLogger(socketlogger);
    else
      runJavaObjectRequest(request);

    // create the string response
    // final Document docResp = XmlWriter.addResponseInfo(docReq,request);
    final Document docResp = XmlWriter.getResponseDoc(docReq,request);
    final String sResponse = DOMutil.domToString(docResp);
    request.AsciiResponse  = sResponse;

    // log the outgoing string
    if (crsHost instanceof GnrcCrs)
      AppLog.LogEvent(sResponse,LoggingEvent.APP_TO_CLIENT,getClientIP(),crsHost.getConnectionName());
    else
      AppLog.LogError(sResponse,getClientIP(),null);

    return(sResponse);

    } // sendReceiveXml


  /**
   ***********************************************************************
   * This procedure writes the given response out the socket port
   ***********************************************************************
   */
  private void writeNativeAscii(final OutputStream aOutstream, 
                                final String aResponse) 
      throws IOException, TranServerException
    {
    if (aOutstream == null)
      throw new IOException("outstream passed to 'writeNativeAscii' is null");
    else if (GnrcFormat.IsNull(aResponse))
      throw new TranServerException("response passed to 'writeNativeAscii' is null");
    else
      {
      final int iLength = aResponse.length();
      final byte[] lengthbuf = new byte[4];

      lengthbuf[3] = (byte )(iLength & 0xff);
      lengthbuf[2] = (byte )((iLength >> 8)  & 0xff);
      lengthbuf[1] = (byte )((iLength >> 16) & 0xff);
      lengthbuf[0] = (byte )((iLength >> 24) & 0xff);

      aOutstream.write( lengthbuf );
      aOutstream.write( aResponse.getBytes() );
      aOutstream.flush();
      }
    }

  /**
   ***********************************************************************
   * This function reads the objReader of type ObjectInputStream,
   * casts it to a ReqTranServer object, calls runJavaObjectRequest,
   * and writes the returned ObjectOutputStream through the objWriter.
   *
   * @see  #objReader
   * @see  #objWriter
   * @see  sendReceiveNativeAscii
   * @see  readNativeAscii
   * @see  writeNativeAscii
   ***********************************************************************
   */
  private String readNativeAscii(final InputStream aInStream) throws Exception
    {
    if ( (aInStream instanceof BufferedInputStream) == false )
      throw new TranServerException("readNativeAscii: invalid input stream");

    boolean isBuffered = aInStream.markSupported();
    int iNumAvail = aInStream.available();

    // get the length of the packet to be read
    final int LENGTH_FIELD_SIZE = 4;
    final byte[] lengthbuf = new byte[4];
    int iBytesRead = 0;
    try {
      iBytesRead = aInStream.read(lengthbuf);
    }
    catch (SocketException e){
      throw new TranServerException(
          "Unable to read incoming ascii request " +
          "(could be caused by TA unavailability): " + e);
    }

    // if Airware closed the socket connection voluntarely, 
    // this would signal normal completion
    // it could also signal that the socket was closed abnormally
    if ( iBytesRead < 0 )
      throw new SocketCloseException("Native ascii socket closed");

    if ( iBytesRead != LENGTH_FIELD_SIZE )
      throw new TranServerException(
          "readNativeAscii: Could not read expected number of characters. " +
          "Actually received " + iBytesRead + " characters");

    isBuffered = false;
    iNumAvail = 0;

    final int iReportedLength = ((int )(lengthbuf[0] & 0xff) << 24) | ((int )(lengthbuf[1] & 0xff) << 16) | ((int )(lengthbuf[2] & 0xff) << 8) | (int )(lengthbuf[3] & 0xff);

    final byte[] inbuf = new byte[iReportedLength];

    int iTotalReceived = 0;                  // sum of all read calls
    int iNumReceived   = 0;                  // number of bytes received in each read call
    int iNumRemaining  = iReportedLength;    // number of bytes left to receive
    while ( iTotalReceived < iReportedLength )
      {
      // read the packet
      iNumRemaining = iReportedLength - iTotalReceived;
      iNumReceived = aInStream.read(inbuf,iTotalReceived,iNumRemaining);

      // this signals normal completion as well
      if ( iNumReceived < 0 )
        throw new SocketCloseException(
            "Native ascii socket closed while reading data packet");

      iTotalReceived += iNumReceived;
      }

    // make sure you got the reported number of characters
    if ( iTotalReceived != iReportedLength )
      throw new TranServerException(
          "readNativeAscii: failed to receive packet. iNumRead = " + 
          iTotalReceived + " Expected length = " + iReportedLength);

    final String sRequest = new String( inbuf );
    return( sRequest );

    } // end readNativeAscii

  /**
   ***********************************************************************
   * detects if the given socket stream is connected to an ObjectOutputStream
   ***********************************************************************
   */
  private int getConnectionType(final InputStream aInStream) throws Exception
    {
    int iType = UNKNOWN_CONNECTION;

    //AppLog.LogWarning("Getting Connection Type...",getClientIP(),null);

    if ( aInStream.markSupported() == false )
      return(UNKNOWN_CONNECTION);

    // attempt to read enough of the header to recognize an object stream or an XML document
    final int iNumAvailable = aInStream.available();

    //AppLog.LogWarning(
    //    "num bytes available: " + iNumAvailable,getClientIP(),null);

    aInStream.mark(100);
    try
      {
      // read the magic number
      final byte[] magicBuf   = new byte[2];
      aInStream.read(magicBuf);
      //AppLog.LogWarning("read magic number OK",getClientIP(),null);
      final short iMagicNum   = (short )(((short )(magicBuf[0] & 0xff) << 8)   | (short )(magicBuf[1] & 0xff));

      // read the version number
      final byte[] versionBuf = new byte[2];
      aInStream.read(versionBuf);
      //AppLog.LogWarning("read version number OK",getClientIP(),null);

      final short iVersionNum = (short )(((short )(versionBuf[0] & 0xff) << 8) | (short )(versionBuf[1] & 0xff));

      // compare
      if ( (iMagicNum == ObjectStreamConstants.STREAM_MAGIC) &&
           (iVersionNum == ObjectStreamConstants.STREAM_VERSION) )
        iType = JAVA_OBJECT_CONNECTION;
      else
        {
        //AppLog.LogWarning("Ascii connection detected...",getClientIP(),null);
        aInStream.reset();

        final int iAvailable = aInStream.available();
        //AppLog.LogWarning("num bytes available: " + iAvailable,getClientIP(),null);

        final int iSize;
        if ( iAvailable >= 20 )
          iSize = 20;
        else
          iSize = iAvailable;

        // get the first few bytes and see if the string "HTTP" is in there
        final byte[] b_array = new byte[iSize];
        aInStream.read(b_array);
        final String sTest = new String(b_array);
        //AppLog.LogWarning("HTTP testing: " + sTest,getClientIP(),null);


        if ( sTest.indexOf("HTTP") >= 0 )
          iType = XML_CONNECTION;
        else
          iType = NATIVE_CONNECTION;
        }
      }
    finally
      {
      // be sure the stream is reset back to where it was
      aInStream.reset();
      //AppLog.LogWarning("InputStream reset",getClientIP(),null);
      }

    return(iType);
    }


  /**
   ***********************************************************************
   * This procedure allows an outside process to stop the thread
   ***********************************************************************
   */
  public void pleaseStop()
    {
    this.Stop = true;
    }

  /**
   ***********************************************************************
   * This function creates a string array from a given comma delimited
   * string
   ***********************************************************************
   */
  public String getClientIP()
    {
    if ( ClientSocket instanceof Socket )
      {
      final InetAddress client_address = ClientSocket.getInetAddress();
      if ( client_address instanceof InetAddress )
        {
        final String sAddress = client_address.getHostAddress();
        if ( sAddress instanceof String )
          return(sAddress);
        }
      }

    return("");
    }

  /**
   ***********************************************************************
   * This method adds a File Logger for the named connection
   ***********************************************************************
   */
  public static FileLogger createConnectionLogger(final String sConnectionName) throws java.io.IOException
    {
    final String sLogDir = ConfigTranServer.logging.getProperty("currentLogDir","logs");

    final String sTerminalLogName = 
      sLogDir + "/Terminal/" + Thread.currentThread().getName() +
      "-" + sConnectionName + ".log"; // e.g. T1003-23490EOXOE.log

    final File logfile = new File(sTerminalLogName);

    final int[] allowed_messages  = {
      LoggingEvent.WARNING,
      LoggingEvent.ERROR,
      LoggingEvent.DEBUG,
      LoggingEvent.CLIENT_TO_APP,
      LoggingEvent.APP_TO_HOST,
      LoggingEvent.HOST_TO_APP,
      LoggingEvent.APP_TO_CLIENT
      };

    final FileLogger connectionLogger = new FileLogger(logfile,allowed_messages,sConnectionName);
    return(connectionLogger);
    } // end addConnectionLogger

  /**
   ***********************************************************************
   * synchronized methods
   ***********************************************************************
   */
  private synchronized void incThreadCount()
    {
    iNumTranClientConnections++;
    }

  private synchronized void decThreadCount()
    {
    if ( iNumTranClientConnections > 0 )
      iNumTranClientConnections--;
    }

  /**
   ***********************************************************************
   * This method adds a File Logger for the named connection
   ***********************************************************************
   */
  public static int getNumClients()
    {
    return(iNumTranClientConnections);
    }

  /**
   ***********************************************************************
   * Final method
   ***********************************************************************
   */
  public void finalize()
    {
      try
        {
        if ( TermLogFile instanceof FileLogger )
          {
          AppLog.removeLogger(TermLogFile);
          TermLogFile = null;
          }

        // make sure the host connection is dropped
        if ( crsHost instanceof GnrcCrs )
          {
          if ( Stop )
            {
            removeGdsFromList(crsHost);
            crsHost.Disconnect();
            crsHost = null;
            }
          else if ( connectionType == XML_CONNECTION )
            {
            addGdsToList(crsHost);
            }
          else
            {
            removeGdsFromList(crsHost);
            crsHost.Disconnect();
            crsHost = null;
            }
          }

        if ( ClientSocket instanceof Socket )
          {
          final OutputStream out = ClientSocket.getOutputStream();
          if ( out instanceof OutputStream )
            out.close();

          /*
          final InputStream in = ClientSocket.getInputStream();
          if ( in instanceof InputStream )
            in.close();
          */
          ClientSocket.close();
          ClientSocket = null;
          }

        }
      catch (NullPointerException np) {
        StringWriter sw = new StringWriter();
        PrintWriter  pw = new PrintWriter(sw);
        pw.println("NullPointer in TranClientConnection.finalize");
        pw.println("Don't worry, it's not as serious as it sounds: ");
        np.printStackTrace(pw);
        pw.flush();
        AppLog.LogError(sw.toString());
      }

      catch (Exception e)
        {
				String sMsg = 
					"Exception occuring in TranClientConnection.finalize(): " +
					e.toString();

        AppLog.LogError(sMsg);
				System.err.println(sMsg);
				e.printStackTrace();
        // JOptionPane.showMessageDialog(null,e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

  /**
   ***********************************************************************
   * Class that checks for GDS connections that have been idle for 30 minutes
   * or more (this will be configurable)
   ***********************************************************************
   */
  class CloseExpiredConnections implements ActionListener
  {
  public void actionPerformed(final ActionEvent e)
    {
    // get the maximum number of idle minutes
    final int iMaxMinutes = 
      ConfigTranServer.application.getIntProperty("inactivityTimeout",30);

    // convert to seconds 
    final long MAX_MSECS  = iMaxMinutes * 60 * 1000;   
    final long SYSTIME    = System.currentTimeMillis();

    GnrcCrs testCrs;
    for ( int i = 0; i < gdsList.size(); i ++ )
      {
      testCrs = (GnrcCrs )gdsList.get(i);
      if ( testCrs instanceof GnrcCrs )
        {
        if ( (MAX_MSECS > 0) && (testCrs.getLastSentTime() > 0) )
          {
          final long inactivityTime = SYSTIME - testCrs.getLastSentTime();
          if ( inactivityTime > MAX_MSECS )
            {
            try
              {
              removeGdsFromList(testCrs);
              testCrs.Disconnect();
              }
            catch (Exception ex)
              { 
              AppLog.LogError(
                  "Exception during crs inactivity disconnect: "
                  + e.toString());
              }
            }
          }
        }
      } // end for
    }   // end actionPerformed
  }     // end inner class CloseExpiredConnections

  public GnrcCrs getCrsHost()
  {
	return crsHost;
  }

  
} // end class TranClientConnection

/**
 ***********************************************************************
 * raise this exception when the socket is closed by the client
 ***********************************************************************
 */

class SocketCloseException extends Exception
{
  SocketCloseException(final String aErrorMessage)
  {
   super(aErrorMessage);
  }

}  // SocketCloseException Class
