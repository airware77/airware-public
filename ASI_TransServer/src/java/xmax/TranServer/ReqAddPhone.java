package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddPhone extends ReqTranServer implements Serializable
{
 public String Locator;
 public String PhoneRemark;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddPhone(final String aCrsCode, final String aLocator, final String aPhoneData)
    {
    super(aCrsCode);
    Locator     = aLocator;
    PhoneRemark = aPhoneData;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Adding phone remark '" + PhoneRemark + "'",null,aCrs.getConnectionName());
    aCrs.AddPhone(PhoneRemark);
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
