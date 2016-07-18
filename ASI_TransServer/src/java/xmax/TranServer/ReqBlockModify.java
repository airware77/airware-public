package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqBlockModify extends ReqTranServer implements Serializable
{
 private String locator;
 private String carrierCode;
 private int numAllocated;

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqBlockModify(final String aCrsCode, final String aLocator, final String aCarrierCode, final int aNumAllocated)
    {
    super(aCrsCode);
    locator      = aLocator;
    carrierCode  = aCarrierCode;
    numAllocated = aNumAllocated;
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Modifying block " + getLocator(),null,aCrs.getConnectionName());
    aCrs.blockModify(locator,carrierCode,numAllocated);
    AppLog.LogInfo("Successfully modified block " + getLocator(),null,aCrs.getConnectionName());
    }

  /**
   ***********************************************************************
   * Get a log file name to use for this request
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {        
    final StringBuffer sLogName = new StringBuffer();
    if ( GnrcFormat.NotNull(aLogDirectory) )
      sLogName.append(aLogDirectory);

    sLogName.append("/Block/");
    sLogName.append(getCrsCode() + getLocator() + ".log");
    return( sLogName.toString() );
    }

  /**
   ***********************************************************************
   * Get functions
   ***********************************************************************
   */
  public String getLocator()       {   return(locator);      }
  public String getCarrierCode()   {   return(carrierCode);  }
  public int    getNumAllocated()  {   return(numAllocated); }

} // end ReqBlockModify
