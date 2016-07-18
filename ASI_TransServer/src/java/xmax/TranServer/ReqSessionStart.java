package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;
import xmax.util.DateTime;
import java.io.Serializable;

public class ReqSessionStart extends ReqTranServer implements Serializable
{
 public String NativeAsciiRequest;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqSessionStart(final String aCrsCode)
   {
   super(aCrsCode);
   }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // the STARTSES ascii command gets logged in runJavaObjectRequest
    // *after* the conversation with the Host is established, since the
    // FileLogger for the connection is created after a connection is
    // established.
    // The following logging statement outputs to the log the time on which the
    // Request object was first instantiated
    String TIME_FORMAT = "HH:mm:ss.SSS";
    String sStart = DateTime.fmtLongDateTime(getStartTime(),TIME_FORMAT);
    long sDuration = System.currentTimeMillis() - getStartTime();

    AppLog.LogWarning( 
        "Start Session Request from Client received at: " + sStart +
        " - Init time: " + sDuration + " ms",
        null, getCrs().getConnectionName());

    aCrs.SessionStart();
    }

  /** 
   ***********************************************************************
   * Get a log file name to use for this request
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    return(null);
    }

}
