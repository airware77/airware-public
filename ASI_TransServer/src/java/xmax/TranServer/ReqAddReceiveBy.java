package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddReceiveBy extends ReqTranServer implements Serializable
{
 public String Locator;
 public String Name;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddReceiveBy(final String aCrsCode, final String aLocator, final String aName)
    {
    super(aCrsCode);
    Locator = aLocator;
    Name    = aName;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Adding receive by '" + Name + "'",null,aCrs.getConnectionName());
    aCrs.AddReceiveBy(Name);
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
