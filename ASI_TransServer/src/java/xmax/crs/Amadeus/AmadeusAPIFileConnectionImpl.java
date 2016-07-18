package xmax.crs.Amadeus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.w3c.dom.Document;

import xmax.TranServer.ConfigTranServer;
import xmax.TranServer.TranServerException;
import xmax.crs.GdsResponseException;
import xmax.crs.GnrcCrsQuery;
import xmax.util.FileStore;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;
import xmax.util.xml.DOMutil;


public class AmadeusAPIFileConnectionImpl implements AmadeusAPICrsConnection
{
	  // root directory for XML requests
	  private File requestDir;
	  
	  // root directory for XML responses
	  private File responseDir;
	  
	  // list of files in the response directory
	  private File[] responseFiles;
	  
	  // the index of the last response file returned
	  private int responseFileIndex;
	  
	  /** points to the GnrcCrs object that instantiated this instance */
	  public AmadeusAPICrs crsOwner;

	  /** tells us whether we are still connected */
	  private boolean isConnected;

	  /** The query that sends each sendReceive */
	  private GnrcCrsQuery crsQuery;

	  /** Used for creating trace files for the Amadeus Server */
	  private boolean doTrace;

	  /** Stores the location of the trace files */
	  private String trace_path;

	  /** Indicates that we should output conversations to the console */
	  private boolean outputToConsole;


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
	  protected AmadeusAPIFileConnectionImpl(final AmadeusAPICrs owner)
	    {
		this.isConnected = false;
	    this.crsOwner = owner;
	    this.requestDir = null;
	    this.responseDir = null;
	    this.outputToConsole = ConfigTranServer.logging.getBooleanProperty("outputToConsole",false);

	    // create tracing directory if we are tracing Conversations
	    this.doTrace = Boolean.valueOf(crsOwner.getProperty("doTrace")).booleanValue();
	    if (doTrace) 
	      {
	      final String sLogDir = ConfigTranServer.logging.getProperty("currentLogDir","logs");
	      trace_path  = sLogDir + "/trace";
	      createTraceDir(trace_path);
	      }
	    } 
	  

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
	  public void openConnection() throws TranServerException, GdsResponseException
	    {
		  	try
		  	{
		  		this.isConnected = false;
			  	final String sRequestDir = crsOwner.getProperty("requestDir"); 
			    this.requestDir = getDirectory(sRequestDir);
			    
			  	final String sResponseDir = crsOwner.getProperty("responseDir"); 
			    this.responseDir = getDirectory(sResponseDir);
			    
				// get a sorted list of files in the response directory
				this.responseFiles = this.responseDir.listFiles();
				Arrays.sort(this.responseFiles);
				this.responseFileIndex = 0;
				
				if (this.responseFiles.length == 0)
				{
					throw new IllegalArgumentException("Response directory " + sResponseDir + " is empty");
				}
			    
				
			    this.isConnected = true;
			    if (outputToConsole) 
		        	{
			    	System.out.println("requestDir : " + this.requestDir.getAbsolutePath());
			    	System.out.println("responseDir : " + this.responseDir.getAbsolutePath());
			    	System.out.println();
		        	}
		  	}
		  	catch (Exception e)
		  	{
			    if (outputToConsole) 
	        	{
			    	System.out.println("openConnection error : " + e.toString());
			    	System.out.println();
	        	}
		  		
		  		throw new TranServerException(e.toString());
		  	}
	    } 

	  
	  private File getDirectory(final String aDirectoryName)
	  {
		  if (aDirectoryName == null)
		  {
		        throw new IllegalArgumentException("Null directory name");
		  }
			  
	      if( aDirectoryName.length() == 0 )
		  {
		        throw new IllegalArgumentException("Empty directory name");
		  }
	    	  
		  final File dir = new File(aDirectoryName);
		  
		  if (outputToConsole) 
        	{
	    	System.out.println("opening directory " + dir.getAbsolutePath());
	    	System.out.println();
        	}
		  
		  
		  if (dir.exists() == false)
		  {
		        throw new IllegalArgumentException("Directory " + aDirectoryName + " does not exist");
		  }
		  
		  if (dir.isDirectory() == false)
		  {
		        throw new IllegalArgumentException(aDirectoryName + " is not a directory");
		  }
			  
		  return dir;
	  }

	  
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
		  this.isConnected = false;
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
	    return this.isConnected;
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
	 * @throws  
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
	      // write the request to file and console
	      if (outputToConsole) 
	        {
	        System.out.println("Sending/Receiving Query to Amadeus:");
	        System.out.println(crsQuery.Request);
	        System.out.println();
	        }

	      // log the request
	      crsQuery.RequestTimeStamp = System.currentTimeMillis();
	      crsOwner.setLastSentTime();
	      AppLog.LogEvent(crsQuery.Request,LoggingEvent.APP_TO_HOST,null,"sConvName");
	      
	      // Store the request to file
	      final File outFile = getRequestFile();
		  FileStore.Write(outFile,crsQuery.Request);
	      
	     
	      
	      // read the response from file
	      crsQuery.Response = null;
	      final File inFile = getResponseFile();
		  crsQuery.Response = FileStore.Read(inFile);
	      crsOwner.setLastRecvTime();
	      crsQuery.ResponseTimeStamp = crsOwner.getLastRecvTime();
	      
	      if ( crsQuery.Response == null )
	      {
	        throw new GdsResponseException("Error occurred sending request",crsQuery.Request,crsQuery.Response,xmax.TranServer.GnrcConvControl.STS_1A_DLL_ERR);
	      }
	      
	      if (outputToConsole)
	      {
	          System.out.println("Reply received");
	          System.out.println(crsQuery.Response);
	      }
	      
	      // log the response
	      AppLog.LogEvent(crsQuery.Response,LoggingEvent.HOST_TO_APP,null,"sConvName");
	      } 
	    catch (GdsResponseException e)
	      {
	    	throw e;
	      } 
	    catch (IOException e)
	      {
	        throw new GdsResponseException(e.toString());
	      } 
	    } 


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
	  public String sendAndReceive(final String xmlRequest) throws GdsResponseException
	    {
	    crsQuery = new GnrcCrsQuery(xmlRequest);
	    sendAndReceive();
	    return(crsQuery.Response);
	    }


	  /** 
	   ***********************************************************************
	   * This method overloads the sendAndReceive method to send and receive
	   * xmlQuery/xmlResponses in the form of dom documents.
	   ***********************************************************************
	   */
	  public Document sendAndReceive(final Document domQuery) throws GdsResponseException
	    {
		  final String sXmlRequest =  DOMutil.domToString(domQuery); 
		  
		  final String sXmlResponse = this.sendAndReceive(sXmlRequest);
		  
	      return( DOMutil.stringToDom(sXmlResponse) );
	    } 
	  
	  
	  /**
	   * Get a new file to write the XML request to
	   * @return
	   */
	  private File getRequestFile()
	  {
		  try
		  {
			  final File reqFile = File.createTempFile("req", ".xml", this.requestDir);
			  
			  if (outputToConsole) 
			  {
		    	System.out.println("Writing request to file " + reqFile.getAbsolutePath());
		    	System.out.println();
			  }
			  
			  
			  return reqFile;
		  }
		  catch (Exception e)
		  {
			  throw new RuntimeException(e.toString());
		  }
	  }
	  
	  
	  /**
	   * Get a next file to read a response from
	   * @return
	   */
	  private File getResponseFile()
	  {
		  if (responseFileIndex >= responseFiles.length)
		  {
			  throw new IllegalStateException("Response file index out of bounds: " + responseFileIndex);
		  }
		  
		  final File responseFile = responseFiles[responseFileIndex];
		  
		  if (outputToConsole) 
		  {
	    	System.out.println("Reading response from file " + responseFile.getAbsolutePath());
	    	System.out.println();
		  }
		  
		  // advance to the next file
		  responseFileIndex++;
		  if (responseFileIndex >= responseFiles.length)
		  {
			  responseFileIndex = 0;
		  }
		  
		  
		  
		  return responseFile;
	  }
	  
	  
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
	      return "file";
	    } 


	  /**
	   ***********************************************************************
	   * Creates the specified subdirectory to store the trace files created by
	   * Amadeus
	   ***********************************************************************
	   */
	  private static void createTraceDir(final String path)
	    {
	    final File traceDir = new File(path);
	    
	    if (traceDir.exists() == false)
	      traceDir.mkdirs();
	    } 


}
