package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqFreeForm extends ReqTranServer implements Serializable
{
 public String Request;
 public String Response;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqFreeForm(final String aCrsCode, final String aRequest)
    {
    super(aCrsCode);
    Request   = aRequest;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Running free form command " + Request,null,aCrs.getConnectionName());

    final StringBuffer sResponse = new StringBuffer();
    aCrs.FreeForm(Request,sResponse);

    Response = sResponse.toString();
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
