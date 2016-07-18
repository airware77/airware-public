package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqIgnore extends ReqTranServer implements Serializable
{
 public String NativeAsciiRequest;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqIgnore(final String aCrsCode)
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
    AppLog.LogInfo("Running ignore command",null,aCrs.getConnectionName());
    aCrs.Ignore();
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
