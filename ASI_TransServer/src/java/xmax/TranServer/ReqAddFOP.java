package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.PNR;

import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddFOP extends ReqTranServer implements Serializable
{
 public String Locator;
 public String FOPRemark;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddFOP(final String aCrsCode, final String aLocator, final String aFOPData)
    {
    super(aCrsCode);
    Locator   = aLocator;
    FOPRemark = aFOPData;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // throw new TranServerException("Add form of payment command is no longer supported.  Set form of payment when ticketing.");
    AppLog.LogInfo("Adding form of payment '" + FOPRemark + "'",null,aCrs.getConnectionName());

    if (GnrcFormat.NotNull(Locator))
      {
      boolean leaveOpen = true;
      aCrs.GetPNRAllSegments(Locator,new PNR(),leaveOpen);
      }

    aCrs.AddFOP(FOPRemark);
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
