package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.BlockMessage;

import xmax.util.Log.FileLogger;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;

//import com.oroinc.text.regex.*;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 ***********************************************************************
 * This request is used to read block messages from the queue of a 
 * Computer Reservation System (CRS); this is currently implemented only for
 * Amadeus NegoSpace; since the format for the messages from different CRSs may
 * be very different, the object storing the message is specific to each CRS.
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 10/25/2002 4:36:14 PM$
 ***********************************************************************
 */
public class ReqBlockReadMessage extends ReqTranServer implements Serializable
{
 /** the name of the queue from which we should retrieve the block messages */
 public String  queueName;

 /** the name of the queue category containing the messages */
 public String queueCategory;

 /** 
  * by default, messages are removed from the queue; during testing, it is
  * helpful to leave the message in the queue by setting this flag to true 
  */
 public boolean leaveMsgInQueue = false;

 /** 
  * A list of {@link BlockMessage} objects that stores the queue messages; more
  * than one message may be returned in one queue reading
  */
 public List blockMessageList;

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public ReqBlockReadMessage(final String aCrsCode) throws TranServerException
    {
    super(aCrsCode);

    if (aCrsCode.equals("1A"))
      blockMessageList = new ArrayList();
    else
      throw new TranServerException(
          "Reading Block Messages for CRS: " + aCrsCode + " is not supported");
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Retrieving next message from queue: " + queueName ,
        null,aCrs.getConnectionName());

    aCrs.blockReadMessage(queueName, queueCategory, leaveMsgInQueue, blockMessageList);

    AppLog.LogInfo("Successfully retrieved message from queue: " + queueName ,
        null,aCrs.getConnectionName());
    } // end runRequest


  /** 
   ***********************************************************************
   * Get a log file name to use for this request; for example, if the queue
   * name is 99/4, this returns a logFileName of the form:<br/>
   * <code>1A_queue_99-4.log</code>
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    final StringBuffer sLogName = new StringBuffer();
    if ( GnrcFormat.NotNull(aLogDirectory) )
      sLogName.append(aLogDirectory);

    sLogName.append("/Locators/");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException(
          "Cannot open log file for Reading Block Messages From Queue.  Crs Code is null");

    if ( GnrcFormat.NotNull(queueName) )
      {
      sLogName.append(getCrsCode() + "_queue_" + cleanQueueName(queueName) + ".log");
      return( sLogName.toString() );
      }
    else
      return(null);
    }


  /**
   ***********************************************************************
   * Replace any forward slashes '/' in the queue name with dashes '-'; used in
   * getLogFileName
   ***********************************************************************
   */
  private static String cleanQueueName(String sName)
    {
	  return sName.replace('/', '-').trim();
	  
	  /*
    // create the perl regex objects
    PatternMatcher  matcher  = new Perl5Matcher();
    Pattern         pattern  = null;
    PatternCompiler compiler = new Perl5Compiler();
  
    // match forward slashes
    try {
      pattern = compiler.compile("\\/");
    } catch (Exception e) {}

  
    // substitute forward slashes with dashes '-'
    String result = Util.substitute(
      matcher, pattern, new Perl5Substitution("-"), 
      sName, Util.SUBSTITUTE_ALL).trim();

    return result;
    */
    } // end cleanQueueName
}  // end class ReqBlockReadMessage
