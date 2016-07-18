package xmax.crs.Amadeus;

import xmax.TranServer.TranServerException;
import xmax.TranServer.ConfigTranServer;
import xmax.crs.GnrcCrs;
import xmax.crs.GnrcCrsQuery;
import xmax.crs.GdsResponseException;
import xmax.util.xml.DOMutil;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;
import xmax.util.FileStore;
import xmax.util.RegExpMatch;

import APIv2.AmadeusAPI;
import APIv2.APIproxy;
import APIv2.APIproxy.Reply;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.w3c.dom.Document;

//for debug purposes only:
// import xmax.util.FileStore;
// import java.io.*;


/**
 ***********************************************************************
 * This class encapsulates all the data and operations needed to connect
 * to the Amadeus XML server through the Amadeus XML C API.
 * 
 * @author   Philippe Paravicini
 * @version  1.0 Copyright (c) 2001
 *
 * @see APIv2.AmadeusAPI
 ***********************************************************************
 */
public class AmadeusAPICrsConnectionImpl implements AmadeusAPICrsConnection
{

  /** a response code of '0', indicating a successful exchange */
  private static final int OK = 0;
  /** a response code of '5', indicating a Response Time-Out */
  // private static final int RESP_TIME_OUT    =  5;
  /** a response code of '7', indicating a Conversation Rejected Error */
  private static final int CONV_REJECTED    =  7;
  /** a response code of '16', indicating a Functional Error */
  private static final int FUNCTIONAL_ERROR = 16;

  /** points to the GnrcCrs object that instantiated this instance */
  public AmadeusAPICrs crsOwner;

  /** 
   * stores the instance of the AmadeusAPI class that we use to interract
   * with the Amadeus C XML dll
   */
  private AmadeusAPI amadeus;

  /** 
   * IP address of the Amadeus XML server to which we are connecting;
   * this information is specified in the configuration file.
   */
  private String serverIP;

  /** 
   * Port Number of the Amadeus XML server to which we are connecting;
   * this information is specified in the configuration file.
   */
  private int portNumber;

  /** 
   * Corporate ID provided by Amadeus to connect to their server;
   * this information is specified in the configuration file.
   */
  private String corporateID;

  /** 
   * User Name provided by Amadeus to connect to their server;
   * this information is specified in the configuration file.
   */
  private String userID;

  /** 
   * Password provided by Amadeus to connect to their server;
   * this information is specified in the configuration file.
   */
  private String password;

  /** tells us whether we are still connected */
  private boolean isConnected;

  /** tells us the time at which the connection's status was last verified */
  private long lastStatus;
  
  /** indicates whether we want to use static responses from local file system (for debugging) */
  private boolean useStaticFile;
  /** static request file directory (for debugging) */ 
  private String staticRequestDirectory;
  /** static response file directory (for debugging) */
  private String staticResponseDirectory;
  /** list of sorted response files */
  private File[] staticResponseFiles;
  /** static response index (for debugging) */
  private int staticResponseIndex;

  /** 
   * Specifies the interval of time, in milliseconds, after which the status of
   * the connection should be actively refreshed; see {@link isConnected}
   */
  private long testConnectionInterval;

  /**  The reply returned by the AmadeusAPI */ 
  private APIproxy.Reply reply;

  /** The query that sends each sendReceive */
  private GnrcCrsQuery crsQuery;

  /** Used for creating trace files for the Amadeus Server */
  private boolean doTrace;

  /** Stores the location of the trace files */
  String trace_path;

  /** Indicates that we should output conversations to the console */
  private boolean outputToConsole;

  private final static int RESPONSE_TIMEOUT = 
    ConfigTranServer.application.getIntProperty("responseTimeout",120);


  /**
   ***********************************************************************
   * The constructor: creates a new instance of the AmadeusAPI class
   * to be used by this connection class, and stores the connection
   * parameters supplied; it's very important that all connection parameters
   * be initialized; otherwise, the method openConnection, when 
   * calling openConversationByCorporateID, will hang for an undetermined
   * amount of time (I did not wait long enough to find out how long).
   ***********************************************************************
   */
  protected AmadeusAPICrsConnectionImpl(AmadeusAPICrs owner)
    {
    crsOwner = owner;
    amadeus  = AmadeusAPI.getAmadeusAPIInstance();

    serverIP    = crsOwner.getProperty("serverIP");
    portNumber  = Integer.parseInt(crsOwner.getProperty("port","20002"));
    corporateID = crsOwner.getProperty("corporateID");
    userID      = crsOwner.getProperty("userID");
    password    = crsOwner.getProperty("password");

    doTrace     =
      Boolean.valueOf(crsOwner.getProperty("doTrace")).booleanValue();

    // create tracing directory if we are tracing Conversations
    if (doTrace) 
      {
      final String sLogDir =
        ConfigTranServer.logging.getProperty("currentLogDir","logs");
      trace_path  = sLogDir + "/trace";
      createTraceDir(trace_path);
      }

    outputToConsole =
      ConfigTranServer.logging.getBooleanProperty("outputToConsole",false);

    amadeus.setTrace(doTrace,trace_path);

    testConnectionInterval = 
      ConfigTranServer.application.getLongProperty("testConnInterval",60);

    this.useStaticFile           = ConfigTranServer.application.getBooleanProperty("useTestHarness", false);
    this.staticRequestDirectory  = ConfigTranServer.application.getStringProperty("requestDir", ".");
    this.staticResponseDirectory = ConfigTranServer.application.getStringProperty("responseDir", ".");
        
    if (this.useStaticFile)
    {
    	this.staticResponseFiles = getStaticResponseFiles(this.staticResponseDirectory);
    	this.staticResponseIndex = 0;
    }
	  
    isConnected = false;
    reply       = null;

    } // end AmadeusAPICrsConnection constructor
  

  /** 
   ***********************************************************************
   * This method calls AmadeusAPI.openConversationByCorporateID and 
   * checks to see whether it successfully obtained a reply, which 
   * it stores in the field by the same name; this reply object will
   * contain the conversation handle needed for further communications
   * with the Amadeus XML Server.
   *
   * @see APIv2.AmadeusAPI.openConversationByCorporateID
   * @see APIv2.APIproxy.Reply
   ***********************************************************************
   */
  public void openConnection() 
      throws TranServerException, GdsResponseException
    {
    if (outputToConsole) {
      System.out.println("Server IP : " + serverIP);
      System.out.println("Port      : " + portNumber);
      System.out.println("CorpID    : " + corporateID);
      System.out.println("User Name : " + userID);
      System.out.println("Password  : " + password);
      System.out.println("Attempting to connect to Amadeus...");
    }

    if(! (serverIP instanceof String && serverIP.length() > 0) )
      throw new TranServerException("missing connection parameter: serverIP");

    if(! (corporateID instanceof String && corporateID.length() > 0) )
      throw new TranServerException("missing connection parameter: corporateID");

    if(! (userID instanceof String && userID.length() > 0) )
      throw new TranServerException("missing connection parameter: userID");

    if(! (password instanceof String && password.length() > 0) )
      throw new TranServerException("missing connection parameter: password");

    // obtain a conversation handle:
    reply = amadeus.openConversationByCorporateID(
        serverIP, portNumber, corporateID, userID, password);

    lastStatus = System.currentTimeMillis();

    // if we were able to connect, obtained a reply,
    // and the reply does not contain an error
    if ( reply instanceof APIproxy.Reply && reply.returnCode == OK )
      {
      isConnected = true;
      String sMsg = "Opened Conversation - API version: " + getVersion();
      if (outputToConsole) 
        {
        System.out.println(sMsg);
        System.out.println();
        }
      AppLog.LogWarning(sMsg,null,getConvHandle());
      }
    else
      {
      // a returnCode of '5' corresponds to a Time Out error.
      // we may want to handle this error in a special kind of way
      isConnected = false;
      if (outputToConsole) 
        {
        System.out.println("Failed to open Conversation with AmadeusAPI Server");
        System.out.println("Error Code  : " + reply.returnCode);
        System.out.println("Error String: " + amadeus.getErrorStr(reply.returnCode) );
        System.out.println("Dump Buffer : " + reply.dumpBuffer);
        System.out.println();
        }
      throw new GdsResponseException(
          "AmadeusAPI returned error: " +
          amadeus.getErrorStr(reply.returnCode), 
          xmax.TranServer.GnrcConvControl.STS_1A_DLL_ERR + reply.returnCode);
      }

    } // end openConnection


  /** 
   ***********************************************************************
   * Calls the AmadeusAPI.closeConversationByCorporateID to close the 
   * connection to the Amadeus XML server; note that there is no way
   * of checking whether the conversation was actually closed; if for some
   * reason the conversation was not closed, we assume that after a period 
   * of inactivity the conversation will time-out and any resources 
   * consumed will be freed.
   ***********************************************************************
   */
  public void closeConnection()
    {
    amadeus.closeConversation(reply.context, serverIP, portNumber);  
    isConnected = false;
    AppLog.LogWarning("Closed Conversation",null,getConvHandle());
    }


  /** 
   ***********************************************************************
   * This method refreshes the status of the connection if need be, and returns
   * its status. 
   * <p>
   * Each reply received from the Amadeus server contains a code that
   * characterizes the status of the connection as of that reply. Depending on
   * the last such reply code received while doing a {@link sendAndReceive} or 
   * {@link openConnection}, the private field isConnected is set to
   * <code>true</code> or <code>false</code> accordingly.</p>
   * <p>If the connection status has not been refreshed for an amount of time
   * greater than <code>testConnectionInterval</code> a cryptic time request is
   * sent to the Amadeus Server for the sole purpose of refreshing the
   * connection status. Note that this incurs an estimated 1 to 2 second
   * penalty incurred while blocking for a response.
   ***********************************************************************
   */
  public boolean isConnected()
    {
    // if we already know that we are connected, say so
    if(!isConnected) return(false);

    // if we're returning local file data, skip the test
    if (useStaticFile) return (true);
    
    // convert the testConnectionInternal seconds to millis
    long timeToTest = testConnectionInterval * 1000;

    // if need be, refresh the status of the connection by sending a simple
    // cryptic request for the system time
    try
      {
      if ( (System.currentTimeMillis() - lastStatus) > timeToTest )
        {
        String sTest = crsOwner.sendRecvCryptic("DD");
        }
       }
    catch (Exception e) {isConnected = false; }

    return(isConnected);
    }


  /** 
   ***********************************************************************
   * This method sends an XML command to the Amadeus XML server, 
   * and is returned an XML response, along with any error information;
   * the reply and response are logged using the {@link Logger} interface.
   * <p>
   * Responses from the AmadeusAPI come wrapped within an {@link
   * APIv2.APIproxy.Reply} which maps to a C <code>struct</code> in the
   * original interface. One of the fields of this <code>Reply</code> is the
   * <code>returnCode</code> which provides information on the status of the
   * request, as categorized by the named constants in this class (OK,
   * RESP_TIME_OUT, etc...).</p>
   * <p>
   * One of the return codes is <code>FUNCTIONAL_ERROR</code>, which
   * indicates that the request was received and a response was returned, but
   * that the original request contained some sort of syntax error, the
   * 'functional' error.  With this error code comes attached one of the most 
   * irritating idiosyncrasies of the API.  For some queries in the 'classic'
   * interface (as opposed to the 'cryptic' or 'powered' interfaces) such as
   * the <code>PNR_Ignore_Query</code> or the
   * <code>Queue_PlaceOnSingle_Query</code> a successful response is returned
   * within a <code>FUNCTIONAL_ERROR</code> structure!</p>
   * <p>
   * Hence, the class of errors denoted by a <code>FUNCTIONAL_ERROR</code> does
   * not always imply the existence of an error, and such errors cannot be
   * handled at the <code>sendAndReceive</code> level, but must be read and
   * handled by the application on a per query basis.</p>
   * 
   *
   * @see APIv2.AmadeusAPI.sendAndReceiveXml
   * @see APIv2.APIproxy.Reply
   * @see xmax.util.FileLogger
   * @see xmax.util.AppLog
    @see xmax.util.LoggingEvent
   ***********************************************************************
   */
  private void sendAndReceive() throws GdsResponseException
    {
    try
      {
      final String sConvName;
      if ( crsOwner instanceof GnrcCrs )
        sConvName = crsOwner.getConnectionName();
      else
        sConvName = null;

      // debug statement
      // FileStore.Write(new File("e:\\testing\\xml\\1debug_query.xml"),crsQuery.Request);

      // output the request to the console
      if (outputToConsole) 
        {
        System.out.println("Sending/Receiving Query to Amadeus:");
        System.out.println(crsQuery.Request);
        System.out.println();
        }

      // log the request
      crsQuery.RequestTimeStamp = System.currentTimeMillis();
      crsOwner.setLastSentTime();
      AppLog.LogEvent(crsQuery.Request,LoggingEvent.APP_TO_HOST,null,sConvName);

      // the non-timed-out send and receive
      //reply = amadeus.sendAndReceiveXml(reply.context, crsQuery.Request, serverIP, portNumber);

      SendReceiveRunner sendReceiveRunner = new SendReceiveRunner();
      Thread sendReceiveThread = new Thread (sendReceiveRunner);

      // make sure that errand threads do not keep the JVM around during testing
      sendReceiveThread.setDaemon(true);

      // do the actual sendAndReceive
      sendReceiveThread.start();

      // this causes the current thread to wait at most RESPONSE_TIMEOUT
      // seconds for the host to respond       
      try {
        sendReceiveThread.join(RESPONSE_TIMEOUT * 1000);
        }
      catch (InterruptedException ie) { }
      
      // if we did not get a response, throw a time out error
      if (sendReceiveRunner.getLastReply() == null)
        {
        //sendReceiveThread.interrupt();
        throw new GdsResponseException(
            "The request to the Amadeus Host timed out after " +
            RESPONSE_TIMEOUT + " seconds" );
        }
        
      // if we did not time out, retrieve the reply
      reply = sendReceiveRunner.getLastReply();

      crsOwner.setLastRecvTime();
      crsQuery.Response = reply.xmlString;
      crsQuery.ResponseTimeStamp = crsOwner.getLastRecvTime();
      lastStatus = crsOwner.getLastRecvTime();

      // log the response
      AppLog.LogEvent(crsQuery.Response,LoggingEvent.HOST_TO_APP,null,sConvName);

      // debug statement
      // FileStore.Write(new File("e:\\testing\\xml\\1debug_reply.xml"),reply.xmlString);

      if ( reply instanceof APIproxy.Reply && reply.returnCode == OK )
        {
        if (outputToConsole)
          {
          System.out.println("Reply received");
          System.out.println(reply.xmlString);
          }
        }
      
      // functional errors are those that indicate that the query contained a
      // syntax error.  Nevertheless, sometimes a valid response is
      // returned within an error statement, such as is the case, for example,
      // when issuing a 'Queue_PlaceOnSingle_Query' query.
      else if (reply instanceof APIproxy.Reply && 
               reply.returnCode == FUNCTIONAL_ERROR)
        {
        if (outputToConsole)
          {
          System.out.println("Reply received with possible errors");
          System.out.println(reply.xmlString);
          }
        }
      else 
        {
        String NEWLINE = System.getProperty("line.separator");
        String err;
        err  = "AmadeusAPI returned Error Code: " + reply.returnCode + " - ";
        err += "Error String: " + amadeus.getErrorStr(reply.returnCode);
        //err  = "Error Code  : " + reply.returnCode + NEWLINE;
        //err += "Error String: " + amadeus.getErrorStr(reply.returnCode) + NEWLINE;
        //err += "Dump Buffer : " + reply.dumpBuffer + NEWLINE;
        //AppLog.LogError(err,null,crsOwner);
        if (outputToConsole)
          {
          System.out.println("An error occurred while sending and receiving to Amadeus");
          System.out.println(reply.xmlString);
          System.out.println();
          System.out.println(err);
          }

        // the conversation came to an end, for some reason; setting isConnected
        // to 'false' will cause the connection to reconnect on the next query
        if ( reply.returnCode == CONV_REJECTED )
          isConnected = false;

        final int iErrCode = 
          xmax.TranServer.GnrcConvControl.STS_1A_DLL_ERR + reply.returnCode;

        throw new
          GdsResponseException(err,crsQuery.Request,crsQuery.Response,iErrCode);
        }
      }  // end try
    catch (GdsResponseException e)
      {
      //AppLog.LogError(e.toString(),null,crsOwner);
      if ( crsQuery instanceof GnrcCrsQuery )
        throw e;
      else
        throw new GdsResponseException("crsQuery undefined - " + e.toString(), e.getErrorNumber());
      // AppLog.LogError(e.toString(),null);
      // System.err.println(e.getMessage());
      // e.printStackTrace();
      }
    
    if(outputToConsole) System.out.println();

    } // end sendAndReceive


  /** 
   ***********************************************************************
   * This method sends an XML command to the Amadeus XML server, 
   * and is returned an XML response, along with any error information;
   * the reply and response are logged using the {@link Logger} interface;
   * note that as of Jun. 5 2001, this is a temporary implementation that
   * will change as the connectivity operations of the GnrcCrs class are
   * abstracted through the GnrcCrsConnection class.
   *
   * @see APIv2.AmadeusAPI.sendAndReceiveXml
   * @see APIv2.APIproxy.Reply
   * @see xmax.util.FileLogger
   * @see xmax.util.AppLog
   * @see xmax.util.LoggingEvent
   ***********************************************************************
   */
  public String sendAndReceive(String xmlRequest) throws GdsResponseException
    {
    crsQuery = new GnrcCrsQuery(xmlRequest);
    sendAndReceive();
    return(crsQuery.Response);

    } // end sendAndReceive


  /** 
   ***********************************************************************
   * This method overloads the sendAndReceive method to send and receive
   * xmlQuery/xmlResponses in the form of dom documents.
   ***********************************************************************
   */
  public Document sendAndReceive(Document domQuery) throws GdsResponseException
    {
    crsQuery = new GnrcCrsQuery( DOMutil.domToString(domQuery) );
    sendAndReceive();
    return( DOMutil.stringToDom(crsQuery.Response) );
    } // end sendAndReceive

  
  /**
   ***********************************************************************
   * This method rummages through the <code>dumpBuffer</code> field of the
   * {@link reply} object, and retrieves the Amadeus API conversation handle
   * associated with this connection.
   * The Amadeus API uses the <code>context</code> field to uniquely identify
   * conversations; this field is actually a String representing a serialized
   * object or other programming construct. As such, it is not very useful.
   * Evidently, the Amadeus API also uses a conversation handle to maintain
   * state.  This conversation handle is not accessible through any of the CAI
   * functions, but can be retrieved by performing a regexp match on the
   * dumpBuffer field.
   ***********************************************************************
   */
  public String getConvHandle()
    {
    if (reply instanceof APIproxy.Reply &&
         reply.dumpBuffer instanceof String)
    {

    String handle = extractHandle(reply.dumpBuffer);
    // return(Thread.currentThread().getName() + "-" + handle);
    return handle;
    }
    else
      return("undefined");

    } // end getConvHandle


  /**
   ***********************************************************************
   * This method matches a pattern such as: <code>hdle = 6DCDE320E56C4</code>,
   * and returns the alphanumeric portion on the right of the equal sign; it is
   * used by {@link getConvHandle} to retrieve the conversation handle from an
   * Amadeus API conversation from the dumpBuffer.
   ***********************************************************************
   */
  private static String extractHandle(final String sDumpBuffer)
    {
    // (?s) treat string to be matched as single line
    // [A-Z0-9]* all-caps alphanumeric string 
    final String HANDLE_PATTERN = "(?s)hdle = ([A-Z0-9]*)";
    final String[] sFields = RegExpMatch.getMatchPatterns(sDumpBuffer,HANDLE_PATTERN);
    if ( sFields instanceof String[] )
      {
      final String sHandle = sFields[1];
      return(sHandle);
      }

    return("undefined");
    } // end extractConvHandle


  /**
   ***********************************************************************
   * This method rummages through the <code>dumpBuffer</code> field of the
   * {@link reply} object, and retrieves the Amadeus API version number
   * of the dll through which we are connecting
   ***********************************************************************
   */
  private String getVersion()
    {
    if (reply instanceof APIproxy.Reply &&
         reply.dumpBuffer instanceof String)
      return(extractVersion(reply.dumpBuffer));
    else
      return("undefined");

    } // end getVersion


  /**
   ***********************************************************************
   * This method matches a pattern such as: <code>version = 2.1.10.1</code>,
   * and returns the version number on the right of the equal sign
   ***********************************************************************
   */
  private static String extractVersion(final String sDumpBuffer)
    {
    // (?s) treat string to be matched as single line
    // [0-9,]* match sequence of numbers and commas
    final String VERSION_PATTERN = "(?s)vers. = ([0-9,]*)";
    final String[] sFields = RegExpMatch.getMatchPatterns(sDumpBuffer,VERSION_PATTERN);
    if ( sFields instanceof String[] )
      {
      final String sVersion = sFields[1];
      return(sVersion);
      }

    return("undefined");
    } // end extractVersion


  /*
  public static void main(String[] args)
    {
    File f = new File("e:\\dumpbuf.txt");
    try {
      String dumpbuf = FileStore.Read(f);
  
      String vers = extractVersion(dumpbuf);
      vers = vers;
    }
    catch (Exception e) {}
    } // end main
  */


  /**
   ***********************************************************************
   * Creates the specified subdirectory to store the trace files created by
   * Amadeus
   ***********************************************************************
   */
  private static void createTraceDir(String path)
    {
    File traceDir = new File(path);
    if (traceDir.exists() == false)
      traceDir.mkdirs();
    } // end createTraceDir

  /** returns the last time on which a status was received from Amadeus */
  // private long getLastStatus() {return lastStatus; }

  
  private static File[] getStaticResponseFiles(final String aResponseDirectory)
  {
	  if (aResponseDirectory == null)
	  {
		  return null;
	  }
	  
	  final File responseDir = new File(aResponseDirectory);
	  if (responseDir.exists() == false)
	  {
		  return null;
	  }
	  
	  if (responseDir.isDirectory() == false)
	  {
		  return null;
	  }
	  
	  final File[] responseFiles = responseDir.listFiles();
	  
	  // sort the response files by name
	  Arrays.sort(responseFiles);
	  return responseFiles;
  }

  private File getNextStaticResponseFile()
  {
	  final String DIRECT_FILE_NAME = "GETFROMAMADEUS";
	  
	  if (this.useStaticFile == false)
	  {
		  return null;
	  }
	  
	  // if we don't already have some response files, get them 
	  if (this.staticResponseFiles == null)
	  {
		  this.staticResponseFiles = getStaticResponseFiles(this.staticResponseDirectory);
		  this.staticResponseIndex = 0;
	  }
	  

	  if (this.staticResponseFiles instanceof File[])
	  {
		  if ( (this.staticResponseIndex >= 0) && (this.staticResponseIndex < this.staticResponseFiles.length) )
		  {
			  final File requestFile = this.staticResponseFiles[this.staticResponseIndex++];
			  final String sRequestFilename = requestFile.getName().toUpperCase();
			  if (sRequestFilename.toUpperCase().contains(DIRECT_FILE_NAME))
			  {
				  return null;
			  }
			  else
			  {
				  return requestFile;
			  }
		  }
	  }

	  
	  return null;
  }
  
  
  private File getNextStaticRequestFile(final File aResponseFile)
  {
	  if (this.useStaticFile == false)
	  {
		  return null;
	  }
	  
	  if (this.staticRequestDirectory == null)
	  {
		  return null;
	  }
	  
	  final File requestDir = new File(this.staticRequestDirectory);
	  if (requestDir.exists() == false)
	  {
		  return null;
	  }
	  
	  if (requestDir.isDirectory() == false)
	  {
		  return null;
	  }
	  
	  
	  final String sFileName = aResponseFile.getName() + ".request.xml";
	  final File requestFile = new File(requestDir, sFileName);
	  return requestFile;
  }  
  
  
  /**
   ***********************************************************************
   * This runnable inner class is used to wrap the sendReceive operation to
   * Amadeus so that we may time it out in the event that it hangs
   * 
   * @author   Philippe Paravicini
   * @version  $Revision: 18$ - $Date: 08/02/2002 1:14:58 PM$
   ***********************************************************************
   */
  class SendReceiveRunner implements Runnable
  {
    private Reply lastReply = null;

    /** returns the last reply provided by Amadeus */
    public Reply getLastReply() {return lastReply;}

    public void run()
      {
      /*
      // used to debug timeout
      try {
        Thread.sleep( (RESPONSE_TIMEOUT + 2) * 1000) ;
        }
      catch (InterruptedException ie) {}
      */

    	// check if we're doing static responses
    	final File respFile = getNextStaticResponseFile();
    	if (respFile instanceof File)
    	{
    		System.out.println("Using static response file " + respFile.getAbsolutePath());
    		try
    		{
	    		// see if you need to write the request to file
	    		final File reqFile = getNextStaticRequestFile(respFile);
	    		if (reqFile instanceof File)
	    		{
					FileStore.Write(reqFile, crsQuery.Request);
	    		}
	    		
	    		// read the response from file
	    		final String sResp = FileStore.Read(respFile);
	   
	    		final Object lastContext = (reply != null) ? reply.context : null;
	    		lastReply = new Reply(OK, null, lastContext, sResp);
    		}
    		catch (IOException e)
    		{
    			e.printStackTrace();
    		}
    		
    		return;
        }
    	
      // send it to Amadeus (this is the normal behavior)	
      lastReply = amadeus.sendAndReceiveXml(reply.context, crsQuery.Request, serverIP, portNumber);
      }
  } // end class SendReceiveRunner


public String getStaticRequestDirectory()
{
	return staticRequestDirectory;
}


public void setStaticRequestDirectory(String staticRequestDirectory)
{
	this.staticRequestDirectory = staticRequestDirectory;
}


public String getStaticResponseDirectory()
{
	return staticResponseDirectory;
}


public void setStaticResponseDirectory(String staticResponseDirectory)
{
	this.staticResponseDirectory = staticResponseDirectory;
}


public int getStaticResponseIndex()
{
	return staticResponseIndex;
}


public void setStaticResponseIndex(int staticResponseIndex)
{
	this.staticResponseIndex = staticResponseIndex;
}


public boolean isUseStaticFile()
{
	return useStaticFile;
}


public void setUseStaticFile(boolean useStaticFile)
{
	this.useStaticFile = useStaticFile;
}

} // end class AmadeusAPICrsConnection

