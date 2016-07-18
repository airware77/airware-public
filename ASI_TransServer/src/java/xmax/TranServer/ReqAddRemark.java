package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import xmax.crs.GetPNR.*;
import xmax.crs.PNR;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class ReqAddRemark extends ReqTranServer implements Serializable
{
 public  String Locator;
 private List remarkList;

 /**
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddRemark(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    Locator    = aLocator;
    remarkList = new ArrayList();
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void addRemark(final String aSection, final String aText, final String aServiceCode,
                        final String aCarrier, final String aPsgrID)
    {
    final PNRRemark newRemark;
    if ( aSection.equals(PNRRemark.SSR_REMARK) )
      {
      final PNRSsrRemark ssr = new PNRSsrRemark(aServiceCode,aCarrier);
      ssr.RemarkText = aText;
      if ( GnrcFormat.IsNull(ssr.Carrier) )
        ssr.Carrier = PNRRemark.ALL_CARRIERS;
      newRemark = ssr;
      }
    else if ( aSection.equals(PNRRemark.OSI_REMARK) )
      {
      final PNROsiRemark osi = new PNROsiRemark(aText,aCarrier);
      if ( GnrcFormat.IsNull(osi.Carrier) )
        osi.Carrier = PNRRemark.ALL_CARRIERS;
      newRemark = osi;
      }
    else if ( aSection.equals(PNRRemark.ITINERARY_REMARK) || aSection.equals("ITIN") )
      newRemark = new PNRItinRemark(aText);
    else if ( aSection.equals(PNRRemark.POCKET_ITIN_REMARK) || aSection.equals("PKT") )
      newRemark = new PNRPocketItinRemark(aText);
    else if ( aSection.equals(PNRRemark.INVOICE_REMARK) || aSection.equals("INV"))
      newRemark = new PNRInvoiceRemark(aText);
    else if ( aSection.equals(PNRRemark.FREEFORM_REMARK) || aSection.equals("FREE"))
      newRemark = new PNRFreeFormRemark(aText);
    else
      newRemark = new PNRGeneralRemark(aText);

    newRemark.setPsgrID(aPsgrID);
    remarkList.add(newRemark);
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // get name association info
    PNRRemark remark;
    PNR pnr = null;
    PNRNameElement name;
    boolean leaveOpen = true;
    String sMessage;

    if (GnrcFormat.NotNull(Locator))
      {
      pnr = new PNR();
      aCrs.GetPNRAllSegments(Locator,pnr,leaveOpen);
      }

    for ( int i = 0; i < remarkList.size(); i++ )
      {
      remark = (PNRRemark )remarkList.get(i);

      // check if you need to get name association info
      if ( GnrcFormat.NotNull(remark.getPsgrID()) )
        {
        // see if you need to re-retrieve the PNR
        if ( (pnr instanceof PNR) == false )
          {
          pnr = new PNR();
          aCrs.GetPNRFromAAA(pnr);
          }

        // find the passenger with the given ID
        name = pnr.getName(remark.getPsgrID());
        if ( name instanceof PNRNameElement )
          {
          remark.LastName     = name.LastName;
          remark.FirstName    = name.FirstName;
          remark.FamilyNumber = pnr.getFamilyNum(name);
          remark.MemberNumber = pnr.getMemberNum(name);
          remark.NameNumber   = pnr.getPsgrNum(name);
          remark.CrsPsgrRef   = name.CrsPsgrID;
          }
        }

      // log the remark
      if ( remark instanceof PNRSsrRemark )
        sMessage = "Adding " + remark.getRemarkType() + " remark: '" + ((PNRSsrRemark )remark).Code + "-" + remark.getRemarkText() + "'";
      else
        sMessage = "Adding " + remark.getRemarkType() + " remark: '" + remark.getRemarkText() + "'";
      AppLog.LogInfo(sMessage,null,aCrs.getConnectionName());
      }


    // add the remarks
    final PNRRemark[] remarkArray = (PNRRemark[] )remarkList.toArray( new PNRRemark[remarkList.size()] );
    if ( remarkArray.length == 1 )
      aCrs.AddRemark(remarkArray[0]);
    else if ( remarkArray.length > 1 )
      aCrs.addPnrElements(Locator,remarkArray,RequestedBy);
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
