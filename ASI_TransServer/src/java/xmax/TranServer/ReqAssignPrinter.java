package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAssignPrinter extends ReqTranServer implements Serializable
{
 public String PrinterName;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqAssignPrinter(final String aCrsCode, final String aPrinterName)
    {
    super(aCrsCode);
    PrinterName = aPrinterName;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Assigning to printer " + PrinterName,null,aCrs.getConnectionName());
    aCrs.AssignPrinter(PrinterName);
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
