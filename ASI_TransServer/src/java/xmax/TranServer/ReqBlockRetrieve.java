package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.Block;
import xmax.util.Log.AppLog;
import java.io.Serializable;
 public class ReqBlockRetrieve extends ReqTranServer implements Serializable
{
 private String locator;
 private String carrierCode;
 private Block block;
 

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqBlockRetrieve(final String aCrsCode, final String aLocator, final String aCarrierCode)
    {
    super(aCrsCode);
    locator = aLocator;
    carrierCode = aCarrierCode;
    block   = new Block();
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Retrieving block: " + locator,null,aCrs.getConnectionName());
    aCrs.blockGet(locator, carrierCode, block);
    AppLog.LogInfo("Retrieved block: " + locator,null,aCrs.getConnectionName());
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

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException(
          "Cannot open log file for BlockRetrieve:  Crs Code is null");

    if ( GnrcFormat.NotNull(locator) )
      {
      sLogName.append(getCrsCode() + locator + ".log");
      return( sLogName.toString() );
      }
    else
      return(null);
    } // end getLogFileName

  /**
   ***********************************************************************
   * Get functions
   ***********************************************************************
   */
  public String getLocator()  {   return(locator);  }
  public Block  getBlock()    {   return(block);    }
  public String getCarrierCode() { return(carrierCode); }


}
