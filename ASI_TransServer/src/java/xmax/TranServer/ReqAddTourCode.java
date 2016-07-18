package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddTourCode extends ReqTranServer implements Serializable
{
 public String Locator;
 public String TourCode;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddTourCode(final String aCrsCode, final String aLocator, final String aTourCode)
    {
    super(aCrsCode);
    Locator  = aLocator;
    TourCode = aTourCode;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // throw new TranServerException("Add tour code command is no longer supported.  Set tour code when ticketing.");
    AppLog.LogInfo("Adding tour code '" + TourCode + "'",null,aCrs.getConnectionName());

    if (GnrcFormat.NotNull(Locator))
      {
      boolean leaveOpen = true;
      aCrs.GetPNRAllSegments(Locator,new xmax.crs.PNR(),leaveOpen);
      }

    aCrs.AddTourCode(TourCode);
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
