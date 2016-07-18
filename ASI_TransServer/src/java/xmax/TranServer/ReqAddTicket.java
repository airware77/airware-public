package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddTicket extends ReqTranServer implements Serializable
{
 public String Locator;
 public String TicketRemark;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddTicket(final String aCrsCode, final String aLocator, final String aTicketData)
    {
    super(aCrsCode);
    Locator      = aLocator;
    TicketRemark = aTicketData;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Adding ticket remark '" + TicketRemark + "'",null,aCrs.getConnectionName());
    aCrs.AddTicket(TicketRemark);
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
