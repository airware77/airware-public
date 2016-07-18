package xmax.crs.Amadeus;

import xmax.crs.PNR;
import xmax.crs.GdsResponseException;
import xmax.TranServer.TranServerException;
import xmax.TranServer.GnrcFormat;

import xmax.crs.GetPNR.*;
import xmax.crs.BaseCrs;

import xmax.util.xml.DOMutil;
import xmax.util.RegExpMatch;
import xmax.util.FileStore;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;

/**
 ***********************************************************************
 * This class is the XML API equivalent of the AmadeusParsePNR class, it
 * is used to parse a <code>PoweredPNR_PNRReply</code> response from
 * Amadeus contain Passenger Name Record (PNR) information; see below
 * for the general structure of such a document.
 * <p>
 * A <code>PoweredPNR_PNRReply</code> document can be roughly divided
 * into 4 different sections:</p>
 * <ul>
 *  <li><p>The first section is composed by the
 *    the two nodes <code>reservationInfo</code> and <code>pnrActions</code>
 *    which are used to identify the PNR as a whole, and to characterize
 *    the nature of the last action taken on a PNR, respectively.</p></li>
 *  <li><p>The second section includes the node <code>travellerInfo</code>.
 *    This node contains passenger and group header information.</p></li>
 *  <li><p>The third section is composed by the node
 *    <code>originDestinationDetails</code> and contains air, hotel and car
 *    segments, as well as passenger associations to these segments.</p></li>
 *  <li><p>The fourth section is composed by the node
 *    <code>dataElementsMaster</code>, which contains all remaining information
 *    found in a Passenger Name Record (remarks, Form of Payment, Commission,etc.)
 * </ul>
 * <p>Each of the latter three sections is uniquely identified with a Node titled,
 * respectively:
 * <ul>
 *  <li><code>elementManagementPassenger</code></li>
 *  <li><code>elementManagementItinerary</code></li>
 *  <li><code>elementManagementData</code></li>
 * </ul>
 * which are used to identify and reference the elements of the PNR, each of
 * which elements corresponds to a PNR 'line' when the PNR is viewed from
 * a traditional Terminal Address. These identifier nodes, although named
 * differently, all share the same structure, and are represented by the inner
 * class {@link elementManagementNode}.  See that class, and the Amadeus
 * documentation, for a more detailed description of that structure.
 *
 * @author   Philippe Paravicini
 * @author   David Fairchild (Original AmadeusParsePNR class)
 * @version  $Revision: 28$ - $Date: 1/27/2003 5:05:17 PM$
 ***********************************************************************
 */
public class AmadeusAPIParsePNR
{
  /**
   ***********************************************************************
   * This is the main method called to parse a Passenger Name Record (PNR)
   * reply from the Amadeus API; this method reads a reply in the form of
   * a DOM document, which root tag must be a PoweredPNR_PNRReply tag, and
   * stores all the information extracted into a {@link PNR} object.
   *
   * @param aDomQuery
   *   a PoweredPNR_PNRReply from Amadeus
   * @param aPNR
   *   the PNR object that stores the reply's information
   * @param isTestEnv
   *   if we are in a test environment, we hard code the remote locators
   *   when retrieving a PNR so that they can be imported by Airware
   *
   * @see scanPnrID
   * @see scanNameSection
   * @see scanItinSection
   * @see scanRemarks
   ***********************************************************************
   */
  public static void parsePNR(Document domReply, PNR aPNR, boolean isTestEnv)
    {
    // save the domReply as a string
    aPNR.setPNRData(DOMutil.domToString(domReply));
    aPNR.setReadDate();

    aPNR.setCrs(BaseCrs.AMADEUS_CODE);
    Element el;
    PNRError pnrError = null;

    // scans the pnrHeader and securityInformation sections
    scanPnrID(domReply, aPNR);

    // scans any general errors (non name,segment or remark specific)
    // contained in the PNR
    PNRError[] aryErrs = readErrors_generalErrorInfo(domReply);
    if (aryErrs instanceof PNRError[])
      aPNR.addErrors(aryErrs);

    // scans the travellerInfo section
    scanNameSection(domReply, aPNR);

    // scans the originDestinationDetails section
    scanItinSection(domReply, aPNR, isTestEnv);

    // scans the dataElementsMaster section
    scanRemarks(domReply, aPNR);

    /*
    scanHeaderRemarks(domReply, aPNR);
    scanGeneralFacts(domReply, aPNR);     // read SSRs, OSIs
    scanGeneralRemarks(domReply, aPNR);   // read general remarks
    scanPhoneRemarks(domReply, aPNR);
    scanTicketRemarks(domReply, aPNR);
    scanAddressRemarks(domReply, aPNR);
    scanFopRemarks(domReply, aPNR);
    */

    } // end parsePNR


  /**
   ***********************************************************************
   * Calls the parsePNR method with the 'isTestEnv' variable to false; setting
   * this variable is only necessary when doing a getPNR
   ***********************************************************************
   */
  public static void parsePNR(Document domReply, PNR aPNR)
    {
    parsePNR(domReply,aPNR,false);
    } // end parsePNR

  /**
   ***********************************************************************
   * Same as parsePNR above, but returns a PNR
   ***********************************************************************
   */
  public static PNR parsePNR(Document domReply,boolean isTestEnv)
    {
    PNR pnr = new PNR();
    parsePNR(domReply,pnr,isTestEnv);
    return(pnr);
    } // end parsePNR


  /**
   ***********************************************************************
   * Same as parsePNR above, but returns a PNR and assumes that we are not in a
   * test environment
   ***********************************************************************
   */
  public static PNR parsePNR(Document domReply)
    {
    PNR pnr = new PNR();
    parsePNR(domReply,pnr,false);
    return(pnr);
    } // end parsePNR

  /**
   ***********************************************************************
   * This method reads either a PNR_Reply or a PoweredPNR_PNRReply in the form
   * of a DOM document, as returned by the AmadeusAPI, and extracts the
   * Passenger Name Record (PNR) locator, if one is available; if one is not
   * available, the method returns <code>null</code>.
   ***********************************************************************
   */
  public static String getLocator(Document domReply)
    {
    Element root = domReply.getDocumentElement();

    if (root.getTagName().equals("PoweredPNR_PNRReply") )
      {
      Element el = (Element)root.getElementsByTagName("pnrHeader").item(0);
      el = (Element)el.getElementsByTagName("reservationInfo").item(0);
      el = (Element)el.getElementsByTagName("reservation").item(0);

      return DOMutil.getTagValue(el,"controlNumber");
      }
    else if (root.getTagName().equals("PNR_Reply"))
      {
      Element el = (Element)root.getElementsByTagName(
        "CAPI_PNR_RecordLocatorInfo").item(0);
      return DOMutil.getTagValue(el,"RecordLocator");
      }
    else
      return(null);

    } // end getLocator


  /**
   ***********************************************************************
   * This method parses the DOM <code>PoweredPNR_PNRReply</code> provided,
   * extracts the Passenger Name Record (PNR) locator, the officeID and the
   * AgentID, and populates the PNR object provided.
   *
   * @param domReply
   *   a DOM object corresponding to an XML PoweredPNR_PNRReply from Amadeus
   * @param aPNR
   *   the PNR object into which the information will be stored
   ***********************************************************************
   */
  private static void scanPnrID(Document domReply, PNR aPNR)
    {
    aPNR.setLocator( getLocator(domReply) );
    Element el = (Element)domReply.getElementsByTagName("responsibilityInformation").item(0);
    aPNR.setPseudoCity( DOMutil.getTagValue(el, "officeId") );
    aPNR.setAgentSign(  DOMutil.getTagValue(el, "agentId") );
    } // end scanPnrID

  /**
   ***********************************************************************
   * This method parses the DOM PoweredPNR_PNRReply provided, extracts
   * Passenger Information, and populates the PNR object provided.
   *
   * @param domReply
   *   a DOM object corresponding to an XML PoweredPNR_PNRReply from Amadeus
   * @param aPNR
   *   the PNR object into which the information will be stored
   ***********************************************************************
   */
  protected static void scanNameSection(Document domReply, PNR aPNR)
    {
    NodeList psgrList = domReply.getElementsByTagName("travellerInfo");

    for (int i=0; i < psgrList.getLength(); i++)
      {
      Element travellerInfo = (Element)psgrList.item(i);
      Element el;

      PNRGroupHeader groupHeader = null;
      PNRFamilyElement family    = null;
      Object pnrElement          = null;
      try
        {
        ElementManagementNode elmMan =
          scanElementManagementNode(travellerInfo);

        if (elmMan.segmentName.equals("NG"))
          {
          groupHeader = scanGroupHeader(travellerInfo,elmMan);
          pnrElement = groupHeader;
          }
        else
          {
          family = scanSingleFamily(travellerInfo, elmMan);
          pnrElement = family;
          }

        PNRError pnrError = null;

        if ( elmMan.status instanceof String &&
             elmMan.status.equals("ERR") )
          {
          try
            { pnrError = readError_elementErrorNode(travellerInfo,pnrElement);}
          catch (TranServerException e)
            {System.err.println(e.getMessage());}

          if (pnrError instanceof PNRError)
            aPNR.addError(pnrError);
          }
        else if (groupHeader instanceof PNRGroupHeader)
          aPNR.setGroupHeader(groupHeader);
        else if (family instanceof PNRFamilyElement)
          aPNR.addFamily(family);
        else
          aPNR.addError(
            "Unable to read travellerInfo Node #" + (i+1) + " - Reason Unknown");
        }
      catch (Exception e)
        {aPNR.addError("Unable to scan travellerInfo Node: " + e.toString());}

      }  // end for

    } // end scanNameSection


  /**
   ***********************************************************************
   * This method scans the contents of a group header contained within the
   * travellerInfo section of a PoweredPNR_PNRReply document, and extracts the
   * information to a PNRGroupHeader; if the element contains an error, it
   * stores the error in {@link PNR#ErrList}, else it populates the {@link
   * PNR#GroupHeader} with a reference to this object.
   ***********************************************************************
   */
  private static PNRGroupHeader scanGroupHeader(Element travellerInfo,
      ElementManagementNode elmMan)
    {
    //// scan the tag containing traveller Information
    // note that in the Amadeus structure the tag 'travellerInfo'
    // contains the tag 'travellerInformation'
    Element travellerInformation = (Element)travellerInfo.getElementsByTagName(
        "travellerInformation").item(0);

    Element el;

    el = (Element)travellerInformation.getElementsByTagName("traveller").item(0);
    String sHeaderText = DOMutil.getTagValue(el, "surname");

    String  sQuantity = DOMutil.getTagValue(el,"quantity");

    PNRGroupHeader groupHeader = new PNRGroupHeader(sHeaderText,sQuantity);

    groupHeader.elementNumber = Integer.parseInt(elmMan.lineNumber);

    if (elmMan.refNumber instanceof String)
      groupHeader.crsElementID  = elmMan.refNumber;

    return(groupHeader);
    } // end scanGroupHeader

  /**
   ***********************************************************************
   * This method scans the contents of a travellerInfo section within a
   * PoweredPNR_PNRReply, and extracts the information contained therein
   * to a PNRFamilyElement.
   ***********************************************************************
   */
  private static PNRFamilyElement scanSingleFamily(
      Element travellerInfo, ElementManagementNode elmMan)
    {
    final PNRNameElement psgr = new PNRNameElement();
    psgr.CrsPsgrID     = elmMan.refNumber;
    psgr.CrsLineNumber = elmMan.lineNumber;

    //// scan the tag containing traveller Information
    // note that in the Amadeus structure the tag 'travellerInfo'
    // contains the tag 'travellerInformation'
    Element travellerInformation = (Element)travellerInfo.getElementsByTagName(
        "travellerInformation").item(0);

    Element el;

    el = (Element)travellerInformation.getElementsByTagName("traveller").item(0);
    psgr.LastName  = DOMutil.getTagValue(el, "surname");
    // this we will add to the PNRFamilyElement created near the end
    String sFamilySeatNum = DOMutil.getTagValue(el,"quantity");

    el = (Element)travellerInformation.getElementsByTagName("passenger").item(0);
    String sNameData = DOMutil.getTagValue(el, "firstName");

    final java.util.StringTokenizer nameTokens = new java.util.StringTokenizer(sNameData);

    if (nameTokens.hasMoreTokens())
      {
      psgr.FirstName = nameTokens.nextToken();
      psgr.NumSeats = 1;
      }

    if (nameTokens.hasMoreTokens())
      psgr.Title = nameTokens.nextToken();

    String sPTC = DOMutil.getTagValue(el, "type");

    if (sPTC instanceof String)
      {
      try
        {
        if ( sPTC.equals("PFA") || sPTC.equals("ADT") )
          psgr.PTC = PNRNameElement.PTC_ADULT;
        else if ( sPTC.equals("MIL") )
          psgr.PTC = PNRNameElement.PTC_MILITARY;
        else if ( sPTC.equals("SNR") )
          psgr.PTC = PNRNameElement.PTC_SENIOR;
        else if ( sPTC.equals("CHD") )
          psgr.PTC = PNRNameElement.PTC_CHILD;
        else if ( RegExpMatch.matches(sPTC,"^P[0-9][0-9]") )
          {
          final String sAge = sPTC.substring(1,3);
          psgr.Age = Integer.parseInt(sAge);
          psgr.PTC = PNRNameElement.PTC_CHILD;
          }
        } // end try
      catch (Exception e)
        {
        System.err.println(e.getMessage());
        e.printStackTrace();
        }
      }
    // if Amadeus does not return a PTC, hard-code PTC_ADULT
    else
      {
      psgr.PTC = PNRNameElement.PTC_ADULT;
      }


    String sID = DOMutil.getTagValue(el,"identificationCode");
    // we must add an 'ID' prefix when entering the identificationCode
    // to the PNR, hence strip it here.
    if (sID instanceof String)
      psgr.setPassengerID(sID.substring(2));

    String hasInfant = DOMutil.getTagValue(el,"infantIndicator");
    if ( hasInfant instanceof String )
      {
      // an accompanying infant without a seat
      if ( hasInfant.equals("1") || hasInfant.equals("766") )
        {
        // get the second passenger element
        el = (Element)travellerInformation.getElementsByTagName("passenger").item(1);
        psgr.InfantName = DOMutil.getTagValue(el, "firstName");
        }
      // an infant with a seat
      else if (hasInfant.equals("767"))
        {
        el = (Element)travellerInformation.getElementsByTagName("passenger").item(1);
        psgr.InfantName = DOMutil.getTagValue(el, "firstName");
        psgr.NumSeats++;
        }
      }

    final PNRFamilyElement family = new PNRFamilyElement(psgr);
    /*
    // if we are scanning a group header
    if (isGroupHeader)
      {
      family.isCorpHeader = isGroupHeader;
      if ( sFamilySeatNum instanceof String)
        family.setGroupNumSeats(Integer.parseInt(sFamilySeatNum));
      }
    else
      // total all seats used by passengers
      // the 'getNumSeats()' totals all seats in the children
      // PNRNameElement
      family.setGroupNumSeats(family.getNumSeats());
    */

    family.RawData  = DOMutil.domToString(travellerInfo);
    return(family);

    } // end scanSingleFamily

  /**
   ***********************************************************************
   * This method scans the section tagged <code>dataElementsMaster</code>
   * which contains individual remarks inside <code>dataElementsIndiv</code>
   * tags; it reads the segmentName of each such remark, and calls the
   * appropriate method to scan that remark.
   *
   * @see scanPhoneRemark
   ***********************************************************************
   */
  protected static void scanRemarks(Document domReply, PNR aPNR)
    {
    NodeList remarkList = domReply.getElementsByTagName("dataElementsIndiv");

    for (int i=0; i < remarkList.getLength(); i++)
      {
      try
        {
        Element elmRemark = (Element)remarkList.item(i);

        // scan the 'header' node for this remark
        ElementManagementNode elmMan = scanElementManagementNode(elmRemark);

        PNRRemark remark = null;

        //depending on the type of segment, call the appropriate method
        if ( elmMan.segmentName.equals("AP") )
          remark = getPhoneRemark(elmRemark,elmMan);

        else if ( elmMan.segmentName.equals("RF") )
          remark = getReceivedByRemark(elmRemark,elmMan);

        else if ( elmMan.segmentName.equals("TK") )
          remark = getTicketRemark(elmRemark,elmMan);

        else if ( elmMan.segmentName.equals("SSR") )
          remark = getSsrRemark(elmRemark,elmMan);

        else if ( elmMan.segmentName.equals("OS") )
          remark = getOsiRemark(elmRemark,elmMan);

        else if ( elmMan.segmentName.equals("RI") )
          remark = getItinInvoiceRemark(elmRemark,elmMan);

        else if ( elmMan.segmentName.equals("FP") )
          remark = getFormOfPaymentRemark(elmRemark,elmMan);

        else if ( elmMan.segmentName.equals("FM") ||   // Commission remark
                  elmMan.segmentName.equals("FT") ||   // Tour Code remark
                  elmMan.segmentName.equals("FE") )    // Endorsement Info remark
          remark = get_otherDataFreetext_Remark(elmRemark, elmMan);

        // 'SP' is an informational node that provides references to other PNRs
        else if ( elmMan.segmentName.equals("SP") )
          remark = null;

        else
          remark = getGeneralRemark(elmRemark,elmMan);

        if (remark instanceof PNRRemark)
          {
          // get the psgr association, if any
          remark.setPsgrID(readPassengerAssociation(elmRemark,aPNR));

          // if the remark was flagged as having an error, don't add it to
          // the PNR object, but reference it in the PNR error list
          if ( (elmMan.status instanceof String) && elmMan.status.equals("ERR"))
            {
            PNRError pnrError = readError_elementErrorNode(elmRemark,remark);
            if (pnrError instanceof PNRError)
              aPNR.addError(pnrError);
            }
          // add the remark to the PNR object if no error was found
          else
            {
            aPNR.addRemark(remark);
            }
          }

        } // end try

      catch (Exception e)
        { aPNR.addError("Unable to scan Remark section: " + e.toString() ); }

      } // end for

    } // end scanRemarks


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>AP</code>;
   * note that due to the structure of a PoweredPNR_PNRReply document,
   * it's easy to read the line number at the time that we read
   * the segmentName in <code>scanRemarks</code> and then pass it
   * as a parameter; hence, the sLineNum parameter has no special functional
   * significance.
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getPhoneRemark(Element elmRemark,
      ElementManagementNode elmMan)
    {
    // create a hash to map Amadeus Phone Types to
    // the types defined in PNRPhoneRemark
    Hashtable phoneType = new Hashtable();
    phoneType.put( "3",   new Integer(PNRPhoneRemark.WORK_TYPE)   );
    phoneType.put( "4",   new Integer(PNRPhoneRemark.HOME_TYPE)   );
    phoneType.put( "6",   new Integer(PNRPhoneRemark.AGENCY_TYPE) );
    phoneType.put( "P01", new Integer(PNRPhoneRemark.FAX_TYPE)    );

    Element el;


    // search for a phone number
    el = (Element)elmRemark.getElementsByTagName("otherDataFreetext").item(0);
    String sPhone = DOMutil.getTagValue(el,"longFreetext");

    //if ( !(sPhone instanceof String) )
    //  throw new GdsResponseException(
    //      "The phone segment did not contain a phone number");

    // if we have a phone number create a phone remark object
    PNRPhoneRemark phoneRemark = new PNRPhoneRemark(sPhone);
    phoneRemark.CrsMessageID = elmMan.refNumber;
    phoneRemark.TypeCode     = elmMan.segmentName;

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      phoneRemark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    // see if we can determine the type of phone number
    el = (Element)el.getElementsByTagName("freetextDetail").item(0);
    String sType = DOMutil.getTagValue(el,"type");
    if ( sType instanceof String)
      {
      Integer type = (Integer)phoneType.get(sType);
      if (type instanceof Integer)
        phoneRemark.type = type.intValue();
      }

    return(phoneRemark);

    } // end getPhoneRemark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>FP</code>;
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getFormOfPaymentRemark(
      Element elmRemark, ElementManagementNode elmMan)
    {
    // search for Form Of Payment instruction
    Element el = (Element)elmRemark.getElementsByTagName("otherDataFreetext").item(0);
    String sFOP = DOMutil.getTagValue(el,"longFreetext");

    // if Amadeus prefixed the FOP with an asterisk, strip it so that we can
    // properly ascertain later that the right FOP was added
    if ((sFOP instanceof String) && sFOP.indexOf("*") == 0)
      sFOP = sFOP.substring(1);

    PNRFopRemark fopRemark = new PNRFopRemark(sFOP);
    fopRemark.CrsMessageID = elmMan.refNumber;
    fopRemark.TypeCode     = elmMan.segmentName;

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      fopRemark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    return(fopRemark);

    } // end getFormOfPaymentRemark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>FM</code>
   * (Commission), <code>FT</code> (Tour Code), or <code>FE</code>
   * (Endorsement Info);
   * it returns a {@link PNRGeneralRemark}.
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark get_otherDataFreetext_Remark(
      Element elmRemark, ElementManagementNode elmMan)
    {
    // search for Form Of Payment instruction
    Element el = (Element)elmRemark.getElementsByTagName("otherDataFreetext").item(0);
    String sText = DOMutil.getTagValue(el,"longFreetext");

    PNRGeneralRemark remark = new PNRGeneralRemark(sText);

    remark.CrsMessageID = elmMan.refNumber;
    remark.TypeCode     = elmMan.segmentName;

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      remark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    return(remark);

    } // end get_otherDataFreetext_Remark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>RF</code>;
   * this element only appears on the <code>PoweredPNR_PNRReply</code>
   * document right after the ReceivedBy remark is added; it then disappears
   * from the Passenger Name Record.
   * <p>
   * Note that due to the structure of a PoweredPNR_PNRReply document,
   * it's easy to read the line number at the time that we read
   * the segmentName in <code>scanRemarks</code> and then pass it
   * as a parameter; hence, the sLineNum parameter has no special functional
   * significance.</p>
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getReceivedByRemark(Element elmRemark,
      ElementManagementNode elmMan)
    {
    Element el;

    // retrieve the ReceivedBy name
    el = (Element)elmRemark.getElementsByTagName("otherDataFreetext").item(0);
    String sName = DOMutil.getTagValue(el,"longFreetext");

    PNRReceiveByRemark recByRemark = new PNRReceiveByRemark(sName);
    recByRemark.CrsMessageID = elmMan.refNumber;
    recByRemark.TypeCode     = elmMan.segmentName;

    return(recByRemark);

    } // end getReceivedByRemark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>TK</code>;
   * note that due to the structure of a PoweredPNR_PNRReply document,
   * it's easy to read the line number at the time that we read
   * the segmentName in <code>scanRemarks</code> and then pass it
   * as a parameter; hence, the sLineNum parameter has no special functional
   * significance.
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getTicketRemark(
      Element elmRemark, ElementManagementNode elmMan)
    {
    Element el;

    el = (Element)elmRemark.getElementsByTagName("ticket").item(0);

    PNRTicketRemark ticketRemark =
      new PNRTicketRemark( DOMutil.getTagValue(el,"indicator") );

    ticketRemark.CrsMessageID = elmMan.refNumber;
    ticketRemark.TypeCode     = elmMan.segmentName;

    String sFreeText = DOMutil.getTagValue(el,"freetext");

    if (sFreeText instanceof String)
      ticketRemark.RemarkText += " - " + sFreeText;

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      ticketRemark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    return(ticketRemark);

    } // end getTicketRemark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>SSR</code>;
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getSsrRemark(
      Element elmRemark, ElementManagementNode elmMan)
    {
    Element el;

    el = (Element)elmRemark.getElementsByTagName("ssr").item(0);
    String sType = DOMutil.getTagValue(el,"type");
    String sCarrier = DOMutil.getTagValue(el,"companyId");
    PNRSsrRemark ssrRemark;

    if (sType instanceof String)
      {
      if (sCarrier instanceof String)
        ssrRemark = new PNRSsrRemark(sType,sCarrier);
      else
        ssrRemark = new PNRSsrRemark(sType);
      }
    else
      ssrRemark = new PNRSsrRemark();

    // store the internal remark identifier
    ssrRemark.CrsMessageID = elmMan.refNumber;
    ssrRemark.TypeCode     = elmMan.segmentName;

    // store the remark's content
    ssrRemark.RemarkText = DOMutil.getTagValue(el,"freeText");

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      ssrRemark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    return(ssrRemark);

    } // end getSsrRemark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>OS</code>;
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getOsiRemark(
      Element elmRemark, ElementManagementNode elmMan)
    {
    Element el;

    el = (Element)elmRemark.getElementsByTagName("otherDataFreetext").item(0);
    String sText = DOMutil.getTagValue(el,"longFreetext");

    el = (Element)el.getElementsByTagName("freetextDetail").item(0);
    String sCarrier = DOMutil.getTagValue(el,"companyId");

    PNROsiRemark osiRemark = new PNROsiRemark(sText,sCarrier);
    osiRemark.CrsMessageID = elmMan.refNumber;
    osiRemark.TypeCode     = elmMan.segmentName;

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      osiRemark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    return(osiRemark);

    } // end getOsiRemark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> with a segmentName of <code>RI</code>;
   * this type of segment contains both Itinerary and Invoice remarks, which
   * are distinguished by the <code>category</code> tag.
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getItinInvoiceRemark(
      Element elmRemark, ElementManagementNode elmMan)
    {
    Element el;

    el = (Element)elmRemark.getElementsByTagName("miscellaneousRemarks").item(0);
    el = (Element)el.getElementsByTagName("remarks").item(0);
    String sCategory = DOMutil.getTagValue(el,"category");
    String sText = DOMutil.getTagValue(el,"freetext");

    PNRRemark remark;

    if ( (sCategory instanceof String) &&
         (sCategory.equals("R") || sCategory.equals("I")) )
      remark = new PNRItinRemark(sText);

    else if ( (sCategory instanceof String) && sCategory.equals("F") )
      remark = new PNRInvoiceRemark(sText);

    else
      remark = new PNRGeneralRemark(sText);

    remark.CrsMessageID = elmMan.refNumber;
    remark.TypeCode     = elmMan.segmentName;

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      remark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    return(remark);

    } // end getItinInvoiceRemark


  /**
   ***********************************************************************
   * This method is called from the {@link scanRemarks} to parse a
   * <code>dataElementsIndiv</code> Remark that is not recognized as
   * one of the more specific Remarks parsed by this class; these types
   * of remarks are returned as General Remarks.
   *
   * @see scanRemarks
   ***********************************************************************
   */
  private static PNRRemark getGeneralRemark(
      Element elmRemark, ElementManagementNode elmMan)
    {
    Element el;

    el = (Element)elmRemark.getElementsByTagName("miscellaneousRemarks").item(0);
    el = (Element)el.getElementsByTagName("remarks").item(0);
    String sText = DOMutil.getTagValue(el,"freetext");

    PNRGeneralRemark remark = new PNRGeneralRemark(sText);

    remark.CrsMessageID = elmMan.refNumber;
    remark.TypeCode     = elmMan.segmentName;

    // store the line number if one was passed
    if (elmMan.lineNumber instanceof String)
      remark.MessageNumber = Integer.parseInt(elmMan.lineNumber);

    return(remark);
    } // end getGeneralRemark


  /**
   ***********************************************************************
   * This method searches the <code>PoweredPNR_PNRReply</code>DOM document
   * passed for <code>itineraryInfo</code> nodes, and calls
   * {@link #scanSingleItinSegment} on each node thus found; if the
   * <code>isTestEnv</code> is <code>true</code>, we hard code the remote
   * locators so that the PNR can be successfully imported into Airware.
   *
   * @see #scanSingleItinSegment
   ***********************************************************************
   */
  protected static void scanItinSection(Document domReply, PNR aPNR,
                                        boolean isTestEnv)
    {
    //try
    //  {
    // a temporary that we must sort before adding to the PNR
    ArrayList segList = new ArrayList();
    PNRItinSegment segment;
    NodeList itinList = domReply.getElementsByTagName("itineraryInfo");

    if (itinList instanceof NodeList)
      {
      for (int i=0; i < itinList.getLength(); i++)
        {
        try
          {
          Element elmItinInfo = (Element)itinList.item(i);

          ElementManagementNode elmMan =
            scanElementManagementNode(elmItinInfo);

          segment = scanSingleItinSegment( elmItinInfo, elmMan, isTestEnv );

          if ( elmMan.status instanceof String &&
               elmMan.status.equals("ERR") )
            {
            PNRError pnrError = null;
            try {
              pnrError = readError_elementErrorNode(elmItinInfo,segment);
              }
            catch (Exception e) {
              System.err.println(e.getMessage());
              e.printStackTrace();
              }
            if (pnrError instanceof PNRError)
              aPNR.addError(pnrError);
            }

          else if ( segment instanceof PNRItinSegment )
            //aPNR.addSegment(segment);
            segList.add(segment);
          else
            aPNR.addError("Unable to read Air Itinerary #" + (i+1));
          }
        catch (Exception e)
          {
          aPNR.addError("Unable to scan itin segment: " + e.toString() );
          }
        } // end for
      } // end if
    else
      aPNR.addError("No itinerary segments present in the PNR");

    if (segList.size() > 0)
      {
      Collections.sort(segList,new PNRItinSegmentLineNumComparator());
      for (int i=0; i < segList.size() ; i++)
        {
        aPNR.addSegment((PNRItinSegment)segList.get(i));
        } // end for
      }
    //  }
    //catch (Exception e)
    //  {
    //  aPNR.addError("Unable to scan itinerary section: " + e.toString() );
    //  }

    } // end scanItinSection

  /**
   ***********************************************************************
   * This method is called from {@link #scanItinSection} to determine
   * the type of segment found within each <code>itineraryInfo</code>
   * and to call the appropriate method to parse such a segment;
   *
   * @see #scanItinSection
   * @see #scanItinAirSegment
   ***********************************************************************
   */
  private static PNRItinSegment scanSingleItinSegment(Element itineraryInfo,
       ElementManagementNode elmMan, boolean isTestEnv)
    throws GdsResponseException, TranServerException
    {
    if ( !(elmMan.segmentName instanceof String) )
      return(null);

    else if ( elmMan.segmentName.equals("AIR") )
      if (isArunkSegment(itineraryInfo))
        return new PNRItinArunkSegment();
      else
        return scanItinAirSegment(itineraryInfo,elmMan,isTestEnv);
    else
      return(null);
    /*
    if ( RegExpMatch.matches(aItinString,HTL_SEGMENT_START) )
      return( scanItinHotelSegment(aItinString) );
    else if ( RegExpMatch.matches(aItinString,CAR_SEGMENT_START) )
      return( scanItinCarSegment(aItinString) );
    else if ( RegExpMatch.matches(aItinString,ARUNK_SEGMENT_START) )
      return( scanItinArunkSegment(aItinString) );
    else
      return( scanItinOtherSegment(aItinString) );
    */

    } // end scanSingleItinSegment


  /**
   ***********************************************************************
   * Determines whether the given itineraryInfo node corresponds to that of an
   * ARNK segment (Segment Unknown Remark)
   ***********************************************************************
   */
  private static boolean isArunkSegment(Element itineraryInfo)
    {
    Element travelProduct = (Element)itineraryInfo.getElementsByTagName("travelProduct").item(0);

    if (travelProduct != null)
      {
      Element productDetails =
        (Element)travelProduct.getElementsByTagName("productDetails").item(0);

      if (productDetails != null && DOMutil.getTagValue(productDetails,"identification").equals("ARNK") )
          return true;
      }

    return false;
    } // end isArunkSegment


  /**
   ***********************************************************************
   * This method is called from {@link #scanSingleItinSegment} to retrieve
   * the information for an Air itinerary segment found in a
   * <code>PoweredPNR_PNRReply</code> DOM reply from the Amadeus API
   *
   * @see #scanSingleItinSegment
   ***********************************************************************
   */
  private static PNRItinSegment scanItinAirSegment(Element itineraryInfo,
                             ElementManagementNode elmMan, boolean isTestEnv)
      throws GdsResponseException, TranServerException
    {
    final PNRItinAirSegment ais = new PNRItinAirSegment();
    Element el;

    // save the raw data
    ais.RawData = DOMutil.domToString(itineraryInfo);

    // final String sDayOfWeek  = GnrcParser.getSubstring(aItinString,20,21).trim();
    ais.isCodeShare = false;

    //// read the relatedProduct node first to make sure
    //// that the segment has not already flown
    el = (Element)itineraryInfo.getElementsByTagName("relatedProduct").item(0);
    ais.Status = DOMutil.getTagValue(el, "status");
    if( !(ais.Status instanceof String) )
      throw new GdsResponseException("Invalid itineraryInfo node: no status provided");
    // "B" means that this segment has flown
    else if ( ais.Status.equals("B") )
      return(null);

    // set the number of seats
    ais.NumberOfSeats = Integer.parseInt(DOMutil.getTagValue(el, "quantity"));
    if ( !(ais.NumberOfSeats > 0) )
      throw new GdsResponseException("Unable to scan air segment. Error scanning number of seats.");

    //// scan the segment number
    ais.CrsSegmentID = elmMan.refNumber;

    if ( !(elmMan.lineNumber instanceof String) )
      throw new GdsResponseException("Unable to scan air segment.  Error scanning segment number");
    else
      ais.SegmentNumber = Integer.parseInt(elmMan.lineNumber);

    //// scan the travelProduct node
    Element travelProduct = (Element)itineraryInfo.getElementsByTagName("travelProduct").item(0);

    // scan the carrier
    el = (Element)travelProduct.getElementsByTagName("companyDetail").item(0);
    ais.Carrier = DOMutil.getTagValue(el,"identification");
    if ( !(ais.Carrier instanceof String) )
      throw new GdsResponseException("Unable to scan air segment " + ais.SegmentNumber + ".  Error scanning Carrier");

    // scan the flight number and inventory class
    el = (Element)travelProduct.getElementsByTagName("productDetails").item(0);
    try
      {
      ais.FlightNumber = Integer.parseInt( DOMutil.getTagValue(el,"identification").trim() );
      if ( ais.FlightNumber == 0 )
        throw new GdsResponseException("Unable to scan flight number");
      }
    catch (Exception e)
      {
      throw new GdsResponseException("Unable to scan air segment " + ais.SegmentNumber + ".  Error scanning FlightNumber");
      }

    // If the classOfService is for a night flight, it may consist of a
    // 2-letter code such as KN - in such case, unfortunately, Amadeus returns
    // the 'N' in the classOfService, and the 'K' in the description node.
    // 'N' can also be valid inventory class.  Hence, if the classOfService is
    // 'N' and a description exists, concatenate the description and the
    // classOfService
    String invClass = DOMutil.getTagValue(el,"classOfService");
    if (invClass.equals("N")  &&
        DOMutil.getTagValue(el,"description") instanceof String)
        ais.InventoryClass = DOMutil.getTagValue(el,"description") + invClass;
    else
      ais.InventoryClass = invClass;


    // scan the city pair
    el = (Element)travelProduct.getElementsByTagName("boardpointDetail").item(0);
    ais.DepartureCityCode = DOMutil.getTagValue(el,"cityCode");
    if ( !(ais.DepartureCityCode instanceof String) )
      throw new GdsResponseException("Unable to scan air segment " + ais.SegmentNumber + ".  Error scanning DepartureCityCode");
    el = (Element)travelProduct.getElementsByTagName("offpointDetail").item(0);
    ais.ArrivalCityCode   = DOMutil.getTagValue(el,"cityCode");
    if ( !(ais.ArrivalCityCode instanceof String) )
      throw new GdsResponseException("Unable to scan air segment " + ais.SegmentNumber + ".  Error scanning ArrivalCityCode");

    // scan the departure date and time
    el = (Element)travelProduct.getElementsByTagName("product").item(0);
    String sDate, sTime;
    try
      {
      sDate = DOMutil.getTagValue(el,"depDate");
      sTime = DOMutil.getTagValue(el,"depTime");
      if ( !(sTime instanceof String) )
        sTime = "0001";
      ais.DepartureDateTime = AmadeusAPICrs.fmtDateTimeToLong(sDate,sTime,"ddMMyyHHmm");
      }
    catch (Exception e)
      {
      throw new GdsResponseException("Unable to scan air segment " + ais.SegmentNumber + ". Error scanning departure date/time.");
      }
    // scan the arrival date and time
    try
      {
      sDate = DOMutil.getTagValue(el,"arrDate");
      sTime = DOMutil.getTagValue(el,"arrTime");
      if ( !(sTime instanceof String) )
        sTime = "0001";
      ais.ArrivalDateTime = AmadeusAPICrs.fmtDateTimeToLong(sDate,sTime,"ddMMyyHHmm");
      }
    catch (Exception e)
      {
      throw new GdsResponseException("PNR parse error: Unable to scan air segment " + ais.SegmentNumber + ". Error scanning arrival date/time.");
      }

    // scan the eTicketing indicator
    el = (Element)travelProduct.getElementsByTagName("typeDetail").item(0);
    if (el != null && DOMutil.getTagValue(el,"detail").equals("ET")) {
      ais.is_eTicketeable = true;
    }

    //// done with the travelProduct node


    if (isTestEnv)
      ais.RemoteLocator = "TEST-RLR";
    else
      {
      //// scan the itineraryReservationInfo node
      el = (Element)itineraryInfo.getElementsByTagName(
          "itineraryReservationInfo").item(0);
      if (el instanceof Element)
        {
        el = (Element)itineraryInfo.getElementsByTagName("reservation").item(0);
        ais.RemoteLocator = DOMutil.getTagValue(el,"controlNumber");
        if (!(ais.RemoteLocator instanceof String))
          ais.RemoteLocator = "UNKNOWN ";
        }
      }

    //// scan the flightDetail node
    Element flightDetail =
      (Element)itineraryInfo.getElementsByTagName("flightDetail").item(0);
    if (flightDetail instanceof Element)
      {
      //el = (Element)itineraryInfo.getElementsByTagName("productDetails").item(0);
      //ais. = DOMutil.getTagValue(el,"weekDay");
      el = (Element)itineraryInfo.getElementsByTagName("departureInformation").item(0);
      ais.DepTerminal = DOMutil.getTagValue(el,"departTerminal");
      }

    Element selectionDetails =
      (Element)itineraryInfo.getElementsByTagName("selectionDetails").item(0);

    // determine whether this segment was sold from a block
    if (selectionDetails instanceof Element)
      {
      //el = (Element)itineraryInfo.getElementsByTagName("productDetails").item(0);
      //ais. = DOMutil.getTagValue(el,"weekDay");
      el = (Element)selectionDetails.getElementsByTagName("selection").item(0);
      if (el instanceof Element)
        {
        String sBlockIndic = DOMutil.getTagValue(el,"option");
        if (sBlockIndic instanceof String && sBlockIndic.equals("P3"))
          {
          ais.BlockType = xmax.crs.Block.MANAGED;
          // populate the locator to a default value of 'NOTFOUND'
          // In the event that an exception is raised before we are able
          // to read the block locators, this will let the client (Airware)
          // know that this was a segment sold from block
          ais.BlockLocator = xmax.crs.Block.LOCTR_NOT_FOUND;
          }
        }
      }

    return(ais);
    } // end scanItinAirSegment

  /**
   ***********************************************************************
   * This method scans the node that uniquely identifies the Passenger
   * Name Record element passed;the top-level tags of the element passed
   * must be one of:
   * <ul>
   *  <li><code>travellerInfo</code></li>
   *  <li><code>itineraryInfo</code></li>
   *  <li><code>dataElementsIndiv</code></li>
   * </ul>
   *
   * @return an ElementManagementNode containing the identifier information
   * @throws
   *  GdsResponseException if an improper element was passed, or if
   *  the segment did not contain a segment name or line number
   *
   * @see AmadeusAPIParsePNR
   * @see ElementManagementNode
   ***********************************************************************
   */
  public static ElementManagementNode
      scanElementManagementNode(Element pnrElement) throws GdsResponseException
    {
    Element el;
    if ( pnrElement.getTagName().equals("travellerInfo") )
      el = (Element)pnrElement.getElementsByTagName("elementManagementPassenger").item(0);

    else if ( pnrElement.getTagName().equals("itineraryInfo") )
      el = (Element)pnrElement.getElementsByTagName("elementManagementItinerary").item(0);

    else if ( pnrElement.getTagName().equals("dataElementsIndiv") )
      el = (Element)pnrElement.getElementsByTagName("elementManagementData").item(0);

    else
      throw new GdsResponseException("Unrecognized Node");

    String segName = DOMutil.getTagValue(el,"segmentName");
    String lineNum = DOMutil.getTagValue(el,"lineNumber");

    if (! (segName instanceof String) )
      throw new GdsResponseException("Element did not contain Segment Name");

    // Certain Elements, such as 'RF' (Received From) or 'SP' (split PNR
    // reference records) do not contain line numbers. Hence, we put an empty
    // string as the LineNumber so that we don't have a null String floating
    // around.
    if (!(lineNum instanceof String))
      lineNum = "";

    ElementManagementNode elmMan = new ElementManagementNode(segName,lineNum);

    elmMan.status = DOMutil.getTagValue(el,"status");

    if (el.getElementsByTagName("reference") instanceof NodeList)
      {
      el = (Element)el.getElementsByTagName("reference").item(0);

      elmMan.refQualifier = DOMutil.getTagValue(el,"qualifier");
      elmMan.refNumber    = DOMutil.getTagValue(el,"number");
      }

    return(elmMan);

    } // end scanElementManagementNode


  /**
   ***********************************************************************
   * This method reads a <code>MessagesOnly_Reply</code> response from the
   * Amadeus API, which typically indicates that an Error or Warning has
   * occurred.
   *
   * @param domReply the DOM document containing the MessagesOnly_Reply
   *
   * @returns
   *  a String that concatenates the contents of the reply:
   *  LineType, ErrorCode, and the Error Text.
   *
   ***********************************************************************
   */
  public static String readError_MessagesOnly_Reply(Document domReply)
    {
    Element el = (Element)domReply.getElementsByTagName(
       "MessagesOnly_Reply").item(0);

    if (el instanceof Element)
      {
      el = (Element)el.getElementsByTagName("CAPI_Messages").item(0);

      String sErr;
      sErr  = "LineType: "  + DOMutil.getTagValue(el,"LineType")  + " | ";
      sErr += "ErrorCode: " + DOMutil.getTagValue(el,"ErrorCode") + " | ";
      sErr += DOMutil.getTagValue(el,"Text");

      return(sErr);
      }
    else
      return("");

    } // end readError_MessagesOnly_Reply


  /**
   ***********************************************************************
   * This method parses the DOM <code>PoweredPNR_PNRReply</code> provided,
   * parses the <code>generalErrorInfo</code> tag, if one exists, and
   * stores the errors in the {@link PNR#ErrList} vector of {@link PNRError}
   * objects.
   *
   * @param domReply
   *   a DOM object corresponding to an XML PoweredPNR_PNRReply from Amadeus
   * @param aPNR
   *   the PNR object into which the information will be stored
   ***********************************************************************
   */
  private static PNRError[] readErrors_generalErrorInfo(Document domReply)
    {
    NodeList errList = domReply.getElementsByTagName("generalErrorInfo");
    Element el;

    if (errList instanceof NodeList)
      {
      PNRError[] aryPnrErrors = new PNRError[errList.getLength()];
      for (int i=0; i < errList.getLength(); i++)
        {
        Element elmErr = (Element)errList.item(i);
        aryPnrErrors[i] = new PNRError();
        PNRError pnrError = aryPnrErrors[i];

        el = (Element)elmErr.getElementsByTagName("messageErrorInformation").item(0);
        el = (Element)el.getElementsByTagName("errorDetail").item(0);

        String sQualifier = DOMutil.getTagValue(el,"qualifier");
        // concatenate the qualifier, error code, and responsibleAgency
        // into one error code string separated by pipes
        pnrError.crsErrorCode  = sQualifier + " | ";
        pnrError.crsErrorCode += DOMutil.getTagValue(el,"errorCode") + " | ";
        pnrError.crsErrorCode += DOMutil.getTagValue(el,"responsibleAgency");

        el = (Element)elmErr.getElementsByTagName("messageErrorText").item(0);
        // the errors may come in multiple lines represented by multiple 'text'
        // tags, so concatenate all such text tags
        NodeList errTextList = el.getElementsByTagName("text");
        for (int j=0; j < errTextList.getLength(); j++)
          {
          Node nodeText = errTextList.item(j).getFirstChild();
          if (nodeText instanceof Text)
            pnrError.crsError += nodeText.getNodeValue() + " - ";
          }

        // the following three error codes are more or less documented
        // in the Amadeus API
        if (sQualifier.equals("EC") || sQualifier.equals("ZZZ"))
          pnrError.nativeError = "Error: " + pnrError.crsError;
        else if (sQualifier.equals("WEC"))
          pnrError.nativeError = "Warning: " + pnrError.crsError;
        else if (sQualifier.equals("INF"))
          pnrError.nativeError = "Info: " + pnrError.crsError;
        // in the event that we encounter a non-documented error code:
        else
          pnrError.nativeError = "Unknown Error: " + pnrError.crsError;

        } // end for
      return(aryPnrErrors);
      }

    return(null);
    } // end readErrors_generalErrorInfo


  /**
   ***********************************************************************
   * <p>This method parses the error node that contains error information
   * that is specific to a passenger, segment, or remark in the Passenger
   * Name Record; this method should be called after either of the following
   * have been parsed and were found to contain an error:</p>
   * <p><ul>
   *  <li><code>travellerInfo</code></li>
   *  <li><code>itineraryInfo</code></li>
   *  <li><code>dataElementsIndiv</code></li>
   * </ul></p>
   * <p>
   * The errors are stored in the following nodes, respectively:</p>
   * <ul>
   *  <li><code>nameError</code></li>
   *  <li><code>errorInfo</code></li>
   *  <li><code>elementErrorInformation</code></li>
   * </ul>
   * They all share the same structure, which is parsed by this method.</p>
   *
   * @param pnrElement
   *   this is a DOM Element from the <code>PoweredPNR_PNRReply</code>
   *   containing the error. The name of the Element must be one of the
   *   three elements listed on the first list above.
   *
   * @param pnrObject
   *   this is a native Transerver object, either {@link PNRNameElement},
   *   {@link PNRItinSegment}, {@link @PNRRemark}, or their subclasses,
   *   which resulted from parsing the erroneous pnrElement above.
   *
   * @return
   *   if an error node is found, a PNRError is returned that contains
   *   the Amadeus error information, and a reference to the PNR element
   *   object that had the error
   *
   * @throws
   *   a TranServerException if the wrong type of element was passed
   *   to the method.
   *
   * @see AmadeusAPIParsePNR
   * @see PNRError
   * @see scanElementManagementNode
   *
   ***********************************************************************
   */
  private static PNRError readError_elementErrorNode(
      Element pnrElement, Object pnrObject)  throws TranServerException
    {
    Element elmErr, el;
    String sErrTextNodeName;

    String tagName = pnrElement.getTagName();

    if ( pnrElement.getTagName().equals("travellerInfo") )
      {
      elmErr = (Element)pnrElement.getElementsByTagName("nameError").item(0);
      // this is unknow at this time, and is just a wild guess
      sErrTextNodeName = "nameErrorFreeText";
      }

    else if ( pnrElement.getTagName().equals("itineraryInfo") )
      {
      elmErr = (Element)pnrElement.getElementsByTagName("errorInfo").item(0);
      sErrTextNodeName = "errorfreeFormText";
      }

    else if ( pnrElement.getTagName().equals("dataElementsIndiv") )
      {
      elmErr = (Element)pnrElement.getElementsByTagName("elementErrorInformation").item(0);
      sErrTextNodeName = "elementErrorText";
      }

    else
      throw new TranServerException("Unrecognized Node");

    if (elmErr instanceof Element)
      {
      PNRError pnrError = new PNRError();

      // reference the erroneous PNR thingy in the PNRError object
      pnrError.pnrElement = pnrObject;

      // now get the skinny on the error
      el = (Element)elmErr.getElementsByTagName("errorInformation").item(0);
      el = (Element)elmErr.getElementsByTagName("errorDetail").item(0);

      String sQualifier = DOMutil.getTagValue(el,"qualifier");
      // concatenate the qualifier, error code, and responsibleAgency
      // into one error code string separated by pipes
      pnrError.crsErrorCode  = sQualifier + " | ";
      pnrError.crsErrorCode += DOMutil.getTagValue(el,"errorCode") + " | ";
      pnrError.crsErrorCode += DOMutil.getTagValue(el,"responsibleAgency");

      el = (Element)elmErr.getElementsByTagName(sErrTextNodeName).item(0);
      NodeList textNodes = el.getElementsByTagName("text");
      for (int i=0; i < textNodes.getLength(); i++)
        {
        String sErrLine = textNodes.item(i).getFirstChild().getNodeValue();
        pnrError.crsError += sErrLine + " ";
        }

      // the following three error codes are more or less documented
      // in the Amadeus API
      if (sQualifier.equals("EC") || sQualifier.equals("ZZZ"))
        pnrError.nativeError = "Error: " + pnrError.crsError;
      else if (sQualifier.equals("WEC"))
        pnrError.nativeError = "Warning: " + pnrError.crsError;
      else if (sQualifier.equals("INF"))
        pnrError.nativeError = "Info: " + pnrError.crsError;
      // in the event that we encounter a non-documented error code
      else
        pnrError.nativeError = "Unknown Error: " + pnrError.crsError;

      return pnrError;
      }
    else
      return null;

    } // end readError_elementErrorNode


  /**
   ***********************************************************************
   * This method parses the <code>elementErrorInformation</code> node that
   * contains error information that is specific to a specific remark; this
   * method should be called after a <code>dataElementsIndiv</code> has been
   * parsed, and a remark was found containing an error.
   ***********************************************************************
   */
  /*
  private static PNRError readError_elementErrorInformation(Element elmRemark,
                                                            PNRRemark remark)
    {
    Element el;
    Element elmErr = (Element)elmRemark.getElementsByTagName(
        "elementErrorInformation").item(0);

    if (elmErr instanceof Element)
      {
      PNRError pnrError = new PNRError();

      // reference the erroneous remark in the PNRError object
      pnrError.pnrElement = remark;

      // now get the skinny on the error
      el = (Element)elmErr.getElementsByTagName("errorInformation").item(0);
      el = (Element)elmErr.getElementsByTagName("errorDetail").item(0);

      String sQualifier = DOMutil.getTagValue(el,"qualifier");
      // concatenate the qualifier, error code, and responsibleAgency
      // into one error code string separated by pipes
      pnrError.crsErrorCode  = sQualifier + " | ";
      pnrError.crsErrorCode += DOMutil.getTagValue(el,"errorCode") + " | ";
      pnrError.crsErrorCode += DOMutil.getTagValue(el,"responsibleAgency");

      el = (Element)elmErr.getElementsByTagName("elementErrorText").item(0);
      NodeList textNodes = el.getElementsByTagName("text");
      for (int i=0; i < textNodes.getLength(); i++)
        {
        String sErrLine = textNodes.item(i).getFirstChild().getNodeValue();
        pnrError.crsError += sErrLine + " ";
        }

      // the following three error codes are more or less documented
      // in the Amadeus API
      if (sQualifier.equals("EC") || sQualifier.equals("ZZZ"))
        pnrError.nativeError = "Error: " + pnrError.crsError;
      else if (sQualifier.equals("WEC"))
        pnrError.nativeError = "Warning: " + pnrError.crsError;
      else if (sQualifier.equals("INF"))
        pnrError.nativeError = "Info: " + pnrError.crsError;
      // in the event that we encounter a non-documented error code
      else
        pnrError.nativeError = "Unknown Error: " + pnrError.crsError;

      return pnrError;
      }
    else
      return null;

    } // end readError_elementErrorInformation
  */

  /**
   ***********************************************************************
   * This method parses the <code>nameError</code> node that
   * contains error information that is specific to a specific passenger; this
   * method should be called after a <code>travellerInfo</code> element has been
   * parsed, and a passenger element containing an error was found.
   ***********************************************************************
   */
  /*
  private static PNRError readError_nameError(Element elmPsgr,
                                              PNRFamilyElement family)
    {
    Element el;
    Element elmErr = (Element)elmPsgr.getElementsByTagName(
        "nameError").item(0);

    if (elmErr instanceof Element)
      {
      PNRError pnrError = new PNRError();

      // reference the erroneous name in the PNRError object
      pnrError.pnrElement = family;

      // now get the skinny on the error
      el = (Element)elmErr.getElementsByTagName("errorInformation").item(0);
      el = (Element)elmErr.getElementsByTagName("errorDetail").item(0);

      String sQualifier = DOMutil.getTagValue(el,"qualifier");
      // concatenate the qualifier, error code, and responsibleAgency
      // into one error code string separated by pipes
      pnrError.crsErrorCode  = sQualifier + " | ";
      pnrError.crsErrorCode += DOMutil.getTagValue(el,"errorCode") + " | ";
      pnrError.crsErrorCode += DOMutil.getTagValue(el,"responsibleAgency");

      el = (Element)elmErr.getElementsByTagName("elementErrorText").item(0);
      NodeList textNodes = el.getElementsByTagName("text");
      for (int i=0; i < textNodes.getLength(); i++)
        {
        String sErrLine = textNodes.item(i).getFirstChild().getNodeValue();
        pnrError.crsError += sErrLine + " ";
        }

      // the following three error codes are more or less documented
      // in the Amadeus API
      if (sQualifier.equals("EC") || sQualifier.equals("ZZZ"))
        pnrError.nativeError = "Error: " + pnrError.crsError;
      else if (sQualifier.equals("WEC"))
        pnrError.nativeError = "Warning: " + pnrError.crsError;
      else if (sQualifier.equals("INF"))
        pnrError.nativeError = "Info: " + pnrError.crsError;
      // in the event that we encounter a non-documented error code
      else
        pnrError.nativeError = "Unknown Error: " + pnrError.crsError;

      return pnrError;
      }
    else
      return null;

    } // end readError_nameError
  */
  /**
   ***********************************************************************
   * This method reads and returns the <code>longFreetext<code> nodes that
   * exist under a <code>freetextData</code> node in a
   * <code>PoweredPNR_PNRReply</code>; note that this method does not check
   * whether the document passed is of the right type: it merely searches for
   * the first <code>freetextData</code> node that it encounters in the
   * document.
   *
   * @return A concatenation of the strings found in the
   * <code>longFreetext</code> nodes, or the empty string if any of the nodes
   * specified above nodes could not be found in the document
   ***********************************************************************
   */
  public static String readNode_freetextData(Document domReply)
    {
    String sMsg="";
    Element el;
    //el = (Element)domReply.getElementsByTagName("freetextData").item(0);
    //NodeList textNodes = el.getElementsByTagName("longFreetext");
    NodeList textNodes = domReply.getElementsByTagName("freetextData");
    if (textNodes instanceof NodeList)
      {
      for (int i=0; i < textNodes.getLength(); i++)
        {
        el = (Element)textNodes.item(i);
        //String sMsgLine = textNodes.item(i).getFirstChild().getNodeValue();
        String sMsgLine = DOMutil.getTagValue(el,"longFreetext");
        sMsg += sMsgLine + " ";
        }
      }
    return(sMsg);

    } // end readNode_freetextData

  /**
   ***********************************************************************
   * Reads the <code>referenceForDataElement</code> node that is contained in
   * the <code>dataElementsIndiv</code> passed, retrieves the internal
   * passenger reference, and compares it to the {@link PNRNameElement} objects
   * in the {@link PNR} to extract the Passenger ID to which a given remark is
   * associated; returns the empty string if there is no Passenger Association
   ***********************************************************************
   */
  private static String readPassengerAssociation(Element elmRemark, PNR pnr)
    {
    if (elmRemark == null || pnr == null)
      return "";

    Element referenceForDataElement =
      (Element)elmRemark.getElementsByTagName("referenceForDataElement").item(0);

    if (referenceForDataElement == null)
      return "";

    NodeList refNodes =
      referenceForDataElement.getElementsByTagName("reference");

    if (refNodes != null)
      {
      Element el;
      for (int j=0; j < refNodes.getLength() ; j++)
        {
        el = (Element)refNodes.item(j);
        if (DOMutil.getTagValue(el,"qualifier").equals("PT"))
          {
          String psgrRef = DOMutil.getTagValue(el,"number");
          PNRNameElement psgr = getPassenger(psgrRef,pnr);
          if (psgr != null && GnrcFormat.NotNull(psgr.getPassengerID()))
            return psgr.getPassengerID();
          }

        } // end for
      }

    return "";

    } // end readPassengerAssociation


  /**
   ***********************************************************************
   * Given an AmadeusAPI passenger reference number contained in a
   * <code>referenceForDataElement</code> &gt; <code>reference</code> node, and
   * a PNR this method returns the {@link PNRNameElement} of the passenger
   * identified by the reference number.
   ***********************************************************************
   */
  private static PNRNameElement getPassenger(String sCrsRef, PNR pnr)
    {
    if (sCrsRef == null || pnr == null)
      return null;

    PNRNameElement[] psgrList = pnr.getNames();

    for (int i=0; i < psgrList.length ; i++)
      {
      if (psgrList[i].CrsPsgrID != null &&
          psgrList[i].CrsPsgrID.equals(sCrsRef))
        return psgrList[i];
      } // end for

    return null;

    } // end convertCrsReftoPsgrID



  /**
   ***********************************************************************
   * This method processes the passenger ID that is stored in the Amadeus
   * system; it strips the 'ID' prefix if needed, and checks for the existence
   * of Group ID, which it strips as well
   ***********************************************************************
   */
  /*
  private static String parsePassengerID(String sPnrPsgrID)
    {
    if ((sPnrPsgrID instanceof String) == false)
      return null;

    String ID_PATTERN = "ID([0-9]*)-?";
    return RegExpMatch.getFirstMatch(sPnrPsgrID, ID_PATTERN);
    } // end parsePassengerID
  */


} // end class AmadeusAPIParsePNR

