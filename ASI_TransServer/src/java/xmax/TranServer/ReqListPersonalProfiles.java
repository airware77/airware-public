package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;
import java.util.Vector;

public class ReqListPersonalProfiles extends ReqTranServer implements Serializable
{
 public String   PseudoCity;
 public String   Group;
 public String[] Names;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqListPersonalProfiles(final String aCrsCode, final String aPseudoCity, final String aGroupName)
   {
   super(aCrsCode);
   PseudoCity = aPseudoCity;
   Group      = aGroupName;
   }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Getting list of personal profiles for group " + Group + " Pseudo city " + PseudoCity,null,aCrs.getConnectionName());
    aCrs.GetPersonalProfileList(this);
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
