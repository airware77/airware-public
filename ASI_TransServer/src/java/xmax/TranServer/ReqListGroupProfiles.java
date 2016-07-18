package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;
import java.util.Vector;

public class ReqListGroupProfiles extends ReqTranServer implements Serializable
{
 public String   PseudoCity;
 public String[] Groups;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqListGroupProfiles(final String aCrsCode, final String aPseudoCity)
   {
   super(aCrsCode);
   PseudoCity = aPseudoCity;
   }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Getting list of group profiles for " + PseudoCity,null,aCrs.getConnectionName());
    aCrs.GetGroupProfileList(this);
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
