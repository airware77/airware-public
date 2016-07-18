package xmax.TranServer;

import xmax.crs.GnrcCrs;
import java.io.Serializable;

public class ReqDisableLogForwarding extends ReqTranServer implements Serializable
{
 String TaName;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqDisableLogForwarding()
    {
    super(null);
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
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
