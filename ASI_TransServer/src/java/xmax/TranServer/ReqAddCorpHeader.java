package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddCorpHeader extends ReqTranServer implements Serializable
{
 public String Locator;
 public String GroupName;
 public int    NumSeats;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqAddCorpHeader(final String aCrsCode, final String aGroupName, final int aNumSeats)
    {
    super(aCrsCode);
    GroupName = aGroupName;
    NumSeats  = aNumSeats;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Adding corporate header for group " + GroupName + ", Number of seats = " + NumSeats,null,aCrs.getConnectionName());
    aCrs.AddCorpHeader(GroupName,NumSeats);
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
