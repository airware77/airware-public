package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqSessionEnd extends ReqTranServer implements Serializable
{
 public String NativeAsciiRequest;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqSessionEnd(final String aCrsCode)
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
    AppLog.LogInfo("Running End Session command",null,aCrs.getConnectionName());
    aCrs.SessionEnd();
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
