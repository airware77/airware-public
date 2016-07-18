package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqBlockDelete extends ReqTranServer implements Serializable
{
 private String locator;
 private String carrierCode;

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqBlockDelete(final String aCrsCode, final String aLocator, final String aCarrierCode)
    {
    super(aCrsCode);
    locator = aLocator;
    carrierCode = aCarrierCode;
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Deleting block",null,aCrs.getConnectionName());
    aCrs.blockDelete(locator,carrierCode);
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

  /**
   ***********************************************************************
   * Get functions
   ***********************************************************************
   */
  public String getLocator()  {   return(locator); }
  public String getCarrierCode() { return(carrierCode); }


}
