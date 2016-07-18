package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import xmax.crs.GetPNR.*;
import java.io.Serializable;

public class ReqCxlRemark extends ReqTranServer implements Serializable
{
 public String Locator;
 public String Section;
 public String Service;
 public String Text;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqCxlRemark(final String aCrsCode, final String aLocator, final String aSection)
    {
    super(aCrsCode);
    Locator = aLocator;
    Section = aSection;
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Canceling remark - section=" + Section + ", service=" + Service + ", Text=" + Text,null,aCrs.getConnectionName());

    if ( Section.equals("TKT") )
      aCrs.CancelTicket(Locator,Text);
    else if ( Section.equals("PHNE") )
      aCrs.CancelPhone(Locator,Text);
    else if ( Section.equals("SSR") )
      {
      final PNRSsrRemark remark = new PNRSsrRemark(Service);
      remark.RemarkText = Text;
      aCrs.CancelRemark(Locator,remark);
      }
    else if ( Section.equals("OSI") )
      {
      final PNROsiRemark remark = new PNROsiRemark(Text);
      aCrs.CancelRemark(Locator,remark);
      }
    else
      {
      final PNRGeneralRemark remark = new PNRGeneralRemark(Text);
      aCrs.CancelRemark(Locator,remark);
      }

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
