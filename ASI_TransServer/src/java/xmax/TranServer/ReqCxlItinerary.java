package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqCxlItinerary extends ReqTranServer implements Serializable
{
 public String Locator;

 /**
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqCxlItinerary(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    Locator = aLocator;
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Canceling entire itinerary for locator " + Locator,null,aCrs.getConnectionName());
    aCrs.cancelItinerary(Locator,RequestedBy);
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

    sLogName.append("\\Locators\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for cancel itinerary.  Crs Code is null");

    if ( GnrcFormat.IsNull(Locator) )
      throw new TranServerException("Cannot open log file for cancel itinerary.  Locator is null");

    sLogName.append(getCrsCode() + Locator + ".log");
    return( sLogName.toString() );
    }

}
