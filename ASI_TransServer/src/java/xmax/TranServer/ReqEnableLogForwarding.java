package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import xmax.util.Log.*;
import java.net.Socket;
import java.io.Serializable;

public class ReqEnableLogForwarding extends ReqTranServer implements Serializable
{
 public String TaName;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqEnableLogForwarding(final String aTaName)
    {
    super(null);
    TaName = aTaName;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
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
