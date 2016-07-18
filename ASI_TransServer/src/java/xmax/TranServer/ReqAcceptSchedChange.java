package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAcceptSchedChange extends ReqTranServer implements Serializable
{
 public String Locator;
 public String ReceiveBy;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqAcceptSchedChange(final String aCrsCode, final String aLocator, final String aReceiveBy)
   {
   super(aCrsCode);
   Locator   = aLocator;
   ReceiveBy = aReceiveBy;
   }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Accepting schedule changes for locator " + Locator,null,aCrs.getConnectionName());
    aCrs.AcceptSchedChange(Locator,ReceiveBy);
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
      throw new TranServerException("Cannot open log file for Queue PNR.  Crs Code is null");

    if ( GnrcFormat.IsNull(Locator) )
      throw new TranServerException("Cannot open log file for Queue PNR.  Locator is null");

    sLogName.append(getCrsCode() + Locator + ".log");
    return( sLogName.toString() );
    }

}
