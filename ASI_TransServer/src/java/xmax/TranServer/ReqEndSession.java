package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqEndSession extends ReqTranServer implements Serializable
{
  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqEndSession(final String aCrsCode)
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
    AppLog.LogInfo("Running end session",null,aCrs.getConnectionName());
   // aCrs.Disconnect();
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
