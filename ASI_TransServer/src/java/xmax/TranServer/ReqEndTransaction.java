package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqEndTransaction extends ReqTranServer implements Serializable
{
 public String Locator;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqEndTransaction(final String aCrsCode)
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
    AppLog.LogInfo("Running end transaction",null,aCrs.getConnectionName());
    Locator = aCrs.EndTransaction();
    AppLog.LogInfo("End transaction successful - Locator = " + Locator,null,aCrs.getConnectionName());
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
