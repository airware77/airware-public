package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.PNR;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddCommission extends ReqTranServer implements Serializable
{
 public String  Locator;
 public float   fAmount;
 public boolean isPercent;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddCommission(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    Locator     = aLocator;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // throw new TranServerException("Add commmission command is no longer supported.  Set commission when ticketing.");

    if (isPercent)
      AppLog.LogInfo("Adding commission of " + fAmount + " percent",null,aCrs.getConnectionName());
    else
      AppLog.LogInfo("Adding fixed commission of " + fAmount,null,aCrs.getConnectionName());

    if (GnrcFormat.NotNull(Locator))
      {
      boolean leaveOpen = true;
      aCrs.GetPNRAllSegments(Locator,new PNR(),leaveOpen);
      }

    aCrs.AddCommission(fAmount,isPercent);
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



