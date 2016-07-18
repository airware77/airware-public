package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddEndorsement extends ReqTranServer implements Serializable
{
 public String Locator;
 public String Endorsement;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddEndorsement(final String aCrsCode, final String aLocator, final String aEndorsementData)
    {
    super(aCrsCode);
    Locator     = aLocator;
    Endorsement = aEndorsementData;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // throw new TranServerException("Add endorsement command is no longer supported.  Set endorsement when ticketing.");
    AppLog.LogInfo("Adding endorsement '" + Endorsement + "'",null,aCrs.getConnectionName());

    if (GnrcFormat.NotNull(Locator))
      {
      boolean leaveOpen = true;
      aCrs.GetPNRAllSegments(Locator,new xmax.crs.PNR(),leaveOpen);
      }

    aCrs.AddEndorsement(Endorsement);
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
