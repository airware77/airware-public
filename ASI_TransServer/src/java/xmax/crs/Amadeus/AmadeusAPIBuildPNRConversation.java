package xmax.crs.Amadeus;

import xmax.crs.PNR;
import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.crs.Block;
import xmax.crs.GdsResponseException;
import xmax.crs.GetPNR.*;
import xmax.crs.Availability.DestAvailability;

import xmax.TranServer.GnrcFormat;
import xmax.TranServer.TranServerException;
import xmax.util.DateTime;
import xmax.util.xml.DOMutil;
import xmax.util.Log.AppLog;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.apache.xerces.dom.DocumentImpl;

import java.util.Vector;
import java.util.ArrayList;
import java.text.DecimalFormat;

/**
 ***********************************************************************
 * This class handles the details of the many functions needed to build
 * a Passenger Name Record (PNR) through the Amadeus XML API.
 *
 * @author   Philippe Paravicini
 ***********************************************************************
 */
public class AmadeusAPIBuildPNRConversation
{
  /**
   ***********************************************************************
   * This method builds a DOM query to be sent to the Amadeus XML server
   * to add a Phone field to a Passenger Name Record (PNR).
   *
   * @param aCrs
   *   the AmadeusAPICrs object used to connect to the Amadeus Server
   * @param sPhone
   *   the phone String that we wish to add to the PNR
   ***********************************************************************
   */
  public static void addPhone (AmadeusAPICrs aCrs, String sPhone) throws Exception
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sPhone) )
      throw new TranServerException("Must specify a valid Phone Number");

    Document domQuery = buildQuery_addPhone(sPhone);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
        domReply, aCrs, "Unable to add Contact to PNR", true);

    /*
    Element root = domReply.getDocumentElement();
    String sErr = null;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException("Unable to add Contact to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException("Unable to add Contact to PNR: unrecognized Amadeus response");
    */

    PNR pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);

    if (!(pnr.hasRemark(new PNRPhoneRemark(sPhone))) )
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "Unable to add Contact to PNR" + sPNR_Errs);
      }

    } // end addPhone


  /**
   ***********************************************************************
   * This method builds a DOM query to be sent to the Amadeus XML server
   * to add a ReceivedBy field to a Passenger Name Record (PNR).
   *
   * @param aCrs
   *   the AmadeusAPICrs object used to connect to the Amadeus Server
   * @param sName
   *   the name String that we wish to add to the PNR
   ***********************************************************************
   */
  public void addReceivedBy (AmadeusAPICrs aCrs, String sName) throws Exception
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sName) )
      throw new TranServerException("Must specify a valid 'Received By' Name");

    Document domQuery = buildQuery_addReceivedBy(sName.toUpperCase());
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
        domReply, aCrs, "Unable to add 'Received By' name to PNR", true);

    /*
    Element root = domReply.getDocumentElement();
    String sErr = null;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to add 'Received By' name to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to add 'Received By' name to PNR: unrecognized Amadeus response");
    */

    PNR pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);

    if (!(pnr.hasRemark(new PNRReceiveByRemark(sName.toUpperCase()))) )
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "Unable to add 'Received By' name to PNR " + sPNR_Errs);
      }

    /*
    PNRRemark[] remarks = pnr.getRemarks();
    boolean isSuccess = false;

    if (remarks instanceof PNRRemark[])
      {
      for (int i=0; i < remarks.length; i++)
        {
        if ( remarks[i] instanceof PNRReceiveByRemark &&
             remarks[i].RemarkText instanceof String &&
             remarks[i].RemarkText.equals(sName) )
          {
          isSuccess = true;
          break;
          }
        } // end for
      }

    if (!isSuccess)
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "Unable to add 'Received By' to PNR: " + sPNR_Errs);
      }
    */

    } // end addReceivedBy


  /**
   ***********************************************************************
    * This method builds a DOM query to be sent to the Amadeus XML server
    * to add a Ticketing Instructions to a Passenger Name Record (PNR); as
    * it stands, this method ignores the TicketRemark passed and adds
    * to the PNR an 'OK' ticket-at-will instruction; in order to add a
    * different ticketing instructions, the TicketRemark parameter would
    * have to be structured further.
    *
    * @param aCrs
    *   the AmadeusAPICrs object used to connect to the Amadeus Server
    * @param aTicketRemark
    *   ignored at this time
   ***********************************************************************
   */
  public void addTicket(final AmadeusAPICrs aCrs,
                        final String sTicketRemark) throws Exception
   {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sTicketRemark) )
      throw new TranServerException("Must specify a valid Ticket Remark");

    Document domQuery = buildQuery_addTicket(sTicketRemark);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
        domReply, aCrs, "Unable to add Ticket Remark to PNR", true);

    /*
    Element root = domReply.getDocumentElement();
    String sErr = null;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to add Ticket Remark to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to add Ticket Remark to PNR: unrecognized Amadeus response");
    */
    PNR pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);

    if (!pnr.hasRemark(new PNRTicketRemark(sTicketRemark)))
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "Unable to add Ticket Remark to PNR: " + sPNR_Errs);
      }

   } // end addTicket

  /**
   ***********************************************************************
   * This method is used to add a Corporate Header to a Passenger Name Record
   * (PNR); the airlines prefer that this information appear on the first line
   * of the PNR, and hence, it is best to issue this command before adding
   * passenger names to the PNR.
   ***********************************************************************
   */
  public void addCorpHeader (AmadeusAPICrs aCrs,
                             String sGroupName,
                             int iNumSeats) throws Exception
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sGroupName) )
      throw new TranServerException(
          "Must specify a valid Group Name to add a Corporate Header to a PNR");

    if ( !(iNumSeats > 0) )
      throw new TranServerException(
          "Must specify a valid number of Passengers in order to add a Corporate Header to a PNR");

    Document domQuery = buildQuery_addCorpHeader(sGroupName, iNumSeats);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
        domReply, aCrs, "Unable to add Corporate Header to PNR", true);

    /*
    // check for errors
    Element root = domReply.getDocumentElement();
    String sErr = null;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to add Corporate Header to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to add Corporate Header to PNR: unrecognized Amadeus response");
    */

    PNR pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);

    // make sure that the Group Header was added
    PNRGroupHeader groupHeader = pnr.getGroupHeader();
    boolean isSuccess = false;

    if (groupHeader instanceof PNRGroupHeader)
      if (groupHeader.headerText.equals(sGroupName))
        isSuccess = true;

    if (!isSuccess)
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "Group Name not found in PNR response " + sPNR_Errs);
      }

    } // end addCorpHeader


  /**
   ***********************************************************************
   * This method builds a DOM query to be sent to the Amadeus XML server
   * to add a Name to a Passenger Name Record (PNR).
   *
   * @param aCrs
   *   the AmadeusAPICrs object used to connect to the Amadeus Server
   * @param aName
   *   a PNRNameElement object containing the info of the passenger
   *   to be added to the PNR
   ***********************************************************************
   */
  public void addName (AmadeusAPICrs aCrs, PNRNameElement aName)
      throws Exception
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(aName.LastName) )
      throw new TranServerException(
          "Must specify a valid Passenger Last Name to add a Passenger to a PNR");

    if ( GnrcFormat.IsNull(aName.FirstName) )
      throw new TranServerException(
          "Must specify a valid Passenger First Name to add a Passenger to a PNR");

    Document domQuery = buildQuery_addName(aName);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
        domReply, aCrs, "Unable to add Name to PNR", true);

    /*
    Element root = domReply.getDocumentElement();

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException("Unable to add Name to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException("Unable to add Name to PNR: unrecognized Amadeus response");

    */

    // parse the pnr
    PNR pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);

    // make sure that the name was added
    PNRNameElement[] names = pnr.getNames();
    boolean isSuccess = false;
    if (names != null)
      {
      for (int i=0; i < names.length; i++)
        {
        String pnrPsgrID = names[i].getPassengerID();
        if ( pnrPsgrID.equals(aName.getPassengerID()) )
          {
          isSuccess = true;
          break;
          }
        } // end for
      }

    if (!isSuccess)
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "Passenger not found in PNR response " + sPNR_Errs);
      }
    } // end addName


   /**
    ***********************************************************************
    * This method iterates over the array of {@link PNRItinSegment} provided,
    * and groups the segments according to whether they are 'passive' segments
    * sold from the Airline's inventory, or 'live' segments sold from general
    * availability. Passive segments and non-contiguous live segments are sold
    * individually, while contiguous live segments are grouped together to be
    * sold as part of one itinerary.
    * <p>
    * This grouping ensures that the proper inventory levels are accessed when
    * selling the segments. For, example an availability search for a SFOMIA
    * trip may reveal inventory existing for an SFOATL-ATLMIA itinerary,
    * whereas individual availability requests for the itineraries SFOATL and
    * ATLMIA may not reveal the same inventory.</p>
    * <p>
    * When using a Terminal Address, this process is achieved by first doing an
    * availability for SFOMIA and then 'Short-Selling' the line containing the
    * desired SFOATL-ATLMIA itinerary.</p>
    * <p>
    * The AmadeusAPI PoweredPNR does not have the concept of 'Short Sells':
    * itineraries are always sold by specifying the relevant information on all
    * segments (what would be considered a 'Long Sell' when using a TA).
    * Nevertheless, the PoweredPNR_addMultiElements request can group several
    * segments within an &lt;originDestination&gt; node that specifies the end
    * points of the itinerary and which enables combined segments to be sold as
    * such, rather than selling each itinerary separately.  This ensures that
    * inventory for the whole itinerary is accessed.</p>
    ***********************************************************************
    */
   public static void addPnrElements(
       final AmadeusAPICrs aCrs, final PNRItinSegment[] segList) throws Exception
    {
    if ( (segList instanceof PNRItinSegment[]) == false )
      return;

    Vector vSegmentGroups = PNRItinAirSegment.groupContiguousLiveSegments(segList);

    for (int i=0; i < vSegmentGroups.size() ; i++)
      {
      Document domReply = null;
      Element root = null;
      String sErr = "";

      if (vSegmentGroups.get(i) instanceof PNRItinArunkSegment[])
        {
        AmadeusBuildPNRConversation.addArunk(aCrs);
        /*
          PNRItinArunkSegment arunkSeg = ((PNRItinArunkSegment[])vSegmentGroups.get(i))[0];
          Document domQuery = new DocumentImpl();
          buildQuery_addArunkSegment(domQuery,arunkSeg);
          domReply = aCrs.connection.sendAndReceive(domQuery);

          // check what we get back
          root = domReply.getDocumentElement();
          // if we did not get a proper reply, issue an error
          if ( root.getTagName().equals("MessagesOnly_Reply") )
            {
            sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
            throw new GdsResponseException(
                "Unable to add Arunk Segment to PNR: " + sErr);
            }

          else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
            throw new GdsResponseException(
                "Unable to add Arunk Segment to PNR: unrecognized Amadeus response");
        */
        }
      else if (vSegmentGroups.get(i) instanceof PNRItinAirSegment[])
        {
        PNRItinAirSegment[] aryAirSegs = (PNRItinAirSegment[])vSegmentGroups.get(i);

        // if we are selling a Live segment or non-managed airline block segment
        if (aryAirSegs[0].BlockType.equals(Block.MANAGED) == false )
          {
          Document domQuery = new DocumentImpl();
          buildQuery_sellAirSegments(domQuery,aryAirSegs);
          domReply = aCrs.connection.sendAndReceive(domQuery);
          }
        else // we are selling nego
          {
          domReply = sellNegoAirSegment(aCrs, aryAirSegs[0]);
          }

        /*
        // check what we get back
        root = domReply.getDocumentElement();
        // if we did not get a proper reply, issue an error
        if ( root.getTagName().equals("MessagesOnly_Reply") )
          {
          sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
          throw new GdsResponseException(
              "Unable to add Air Segment to PNR: " + sErr);
          }

        else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
          throw new GdsResponseException(
              "Unable to add Air Segment to PNR: unrecognized Amadeus response");
        */

        domReply = validateReply(
           domReply, aCrs, "Unable to add Air Segment to PNR", true);

        // verify that all segments were added
        PNR pnr = new PNR();
        AmadeusAPIParsePNR.parsePNR(domReply, pnr);
        boolean isSuccess = true;
        for (int j=0; j < aryAirSegs.length; j++)
          {
          PNRItinAirSegment segSold = aryAirSegs[j];
          String sCarrier = segSold.Carrier;
          int iFlight     = segSold.FlightNumber;

          PNRItinAirSegment pnrSeg;
          pnrSeg = pnr.getItinSegmentByFlight(sCarrier,iFlight);

          if (!(pnrSeg instanceof PNRItinAirSegment))
            {
            isSuccess = false;
            sErr += "Unable to add Flight " + sCarrier + iFlight + " | ";
            }
          else
            {
            if (segSold.isScheduled == false &&
                (segSold.DepartureDateTime != pnrSeg.DepartureDateTime ||
                 segSold.ArrivalDateTime   != pnrSeg.ArrivalDateTime)) {
              isSuccess = false;
              sErr += "Unable to add Times to Unscheduled Flight " +
                      sCarrier + iFlight + " | ";
              }
            // send the status of the flight back to the client
            segSold.Status = pnrSeg.Status;

            // in the event that we are able to add some but not all the
            // segments, we message the client as to which ones were added.
            sErr += "Successfully added Flight " + sCarrier + " " + iFlight + " | ";
            }
          } // end for

        if (!isSuccess)
          {
          String sPNR_Errs = pnr.getErrorsConcatenated();
          throw new GdsResponseException(
              "Unable to add Air Segment to PNR: " + sErr + sPNR_Errs);
          }
        }
      } // end for

    } // end addPnrElements (segments)


  /**
   ***********************************************************************
   * This method builds and executes a <code>PNR_ChangeName_Query</code>
   * in order to change the name of a passenger in a Passenger Name
   * Record;<br/>
   * note that everytime that we change a passenger's name, that passenger
   * drops to the bottom of the passenger list on the PNR, which may cause the
   * passenger numbers to change; hence, it is necessary to retrieve the PNR
   * prior to each changeName operation to ensure that the correct passenger
   * element is changed.
   *
   * @throws GdsResponseException  if the Global Distribution System
   *   was unable to change the name on the PNR
   ***********************************************************************
   */
  public static void changePnrElements(final AmadeusAPICrs aCrs,
        final String aLocator, final PNRNameElement[] aOldNames,
        final PNRNameElement[] aNewNames, final String aReceiveBy) throws Exception
   {
   // check the input parms
   if ( (aLocator instanceof String) == false )
     throw new TranServerException("Must specify a locator to change names");

   if ( (aOldNames instanceof PNRNameElement[]) == false )
     throw new TranServerException("Invalid old name list");

   if ( (aNewNames instanceof PNRNameElement[]) == false )
     throw new TranServerException("Invalid new name list");

   if ( aOldNames.length != aNewNames.length )
     throw new TranServerException(
         "Old and new name lists must have the same number of entries");

   // build the command Query
   PNRNameElement currentName;
   //String sCommand;
   String sResponse;
   for ( int i = 0; i < aOldNames.length; i++ )
     {
     // every time that we change a passenger's name, that passenger drops to
     // the bottom of the passenger list on the PNR, which may cause the
     // passenger numbers to change; hence, it is necessary to retrieve the PNR
     // prior to each changeName operation to ensure that the correct passenger
     // element is changed.
     PNR pnr = new PNR();
     if (i == 0) // retrieve the pnr by locator for the first change
       {
       aCrs.Ignore();
       aCrs.GetPNRAllSegments(aLocator,pnr,true);
       }
     else // redisplay the pnr on subsequent changes
       aCrs.GetPNRFromAAA(pnr);

     // determine the name number of the old name
     currentName = pnr.getName(aOldNames[i].getPassengerID());
     if ( (currentName instanceof PNRNameElement) == false )
       throw new TranServerException(
           "Unable to find passenger ID " + aOldNames[i].getPassengerID() +
           " on PNR " + aLocator);

     String sNewName =
       AmadeusBuildPNRConversation.getChangeNameString(aNewNames[i],currentName);

     StringBuffer sQuery = new StringBuffer();

     sQuery.append("<PNR_ChangeName_Query>");
     sQuery.append("<ElementNum>");
     sQuery.append(pnr.getPsgrNum(currentName.getPassengerID()));
     sQuery.append("</ElementNum>");
     sQuery.append("<NewData>" + sNewName + "</NewData>");
     sQuery.append("</PNR_ChangeName_Query>");

     Document domQuery = DOMutil.stringToDom(sQuery.toString());
     Document domReply = aCrs.connection.sendAndReceive(domQuery);

     /*
     domReply = validateReply(
        domReply, aCrs, "Unable to change name on PNR to " + sNewName, true);
     */

     Element root = domReply.getDocumentElement();

     if ( root.getTagName().equals("MessagesOnly_Reply") )
       {
       String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
       throw new GdsResponseException(
           "Unable to change names on PNR: " + sNewName + " - " + sErr);
       }
     }

   } // end changePnrElements (names)


  /**
   ***********************************************************************
   * builds a PoweredPNR_AddMultiElements query that adds several remarks at
   * once
   ***********************************************************************
   */
  public static void addPnrElements(final AmadeusAPICrs aCrs, String sLocator,
      PNRRemark[] aryRemark, final String sReceivedFrom) throws Exception
    {
    PNR pnr = null;

    if (aryRemark.length < 0)
      throw new TranServerException("No remarks to add");

    if (GnrcFormat.NotNull(sLocator))
      {
      pnr = new PNR();
      aCrs.Ignore();
      aCrs.GetPNRAllSegments(sLocator,pnr,true);
      }

    Document domQuery = new DocumentImpl();
    Element root, elm11;

    // create the root element and pnrActions Nodes
    root  = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    // append all the appropriate remark nodes
    for (int i=0; i < aryRemark.length; i++)
      {
      PNRRemark remark = aryRemark[i];


      /*
      // retrieve the pnr from the AAA if need be
      if ( pnr == null &&
           (remark.isNameAssociated() || remark.isSegmentAssociated()) )
        {
        pnr = new PNR();
        aCrs.GetPNRFromAAA(pnr);
        }
      */

      // build the appropriate remark
      if ( remark instanceof PNRSsrRemark)
        {
        PNRSsrRemark rmk = (PNRSsrRemark)remark;
        elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","SSR");
        appendNode_serviceRequest(
            elm11,rmk.Code,rmk.Carrier,rmk.RemarkText);

        if (GnrcFormat.NotNull(rmk.CrsPsgrRef))
          appendNode_referenceForDataElement(elm11,"PT",rmk.CrsPsgrRef);
        }

      else if ( remark instanceof PNROsiRemark )
        {
        PNROsiRemark rmk = (PNROsiRemark)remark;
        elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","OS");

        // "3" stands for literal
        // "P28" identifies 'Other Service Information'
        appendNode_freetextData(
            elm11,"3","P28",rmk.Carrier,rmk.RemarkText);

        if (GnrcFormat.NotNull(rmk.CrsPsgrRef))
          appendNode_referenceForDataElement(elm11,"PT",rmk.CrsPsgrRef);
        }

      else if ( remark instanceof PNRItinRemark ||
                remark instanceof PNRPocketItinRemark)
        {
        //domQuery = buildQuery_addItinRemark(aRemark);
        elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","RI");
        appendNode_miscellaneousRemark(elm11,"RI","R",remark.RemarkText);

        if (GnrcFormat.NotNull(remark.CrsPsgrRef))
          appendNode_referenceForDataElement(elm11,"PT",remark.CrsPsgrRef);
        }

      else if ( remark instanceof PNRInvoiceRemark )
        {
        //domQuery = buildQuery_addInvoiceRemark(aRemark);
        elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","RI");
        appendNode_miscellaneousRemark(elm11,"RI","F",remark.RemarkText);

        if (GnrcFormat.NotNull(remark.CrsPsgrRef))
          appendNode_referenceForDataElement(elm11,"PT",remark.CrsPsgrRef);
        }

      /*
      //not used by Airware
      else if ( aRemark instanceof PNRAddressRemark )
        domQuery = buildQuery_addAddressRemark(aRemark);
      */

      else // a general remark
        {
        elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","RM");
        appendNode_miscellaneousRemark(elm11,"RM",null,remark.RemarkText);

        if (GnrcFormat.NotNull(remark.CrsPsgrRef))
          appendNode_referenceForDataElement(elm11,"PT",remark.CrsPsgrRef);
        }

      } // end for

    // if we have a valid DOM query
    if (!(domQuery instanceof Document))
      throw new GdsResponseException("Invalid addRemark request");

    // execute the command
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
       domReply, aCrs, "Unable to add Remark to PNR", true);

    /*
    // recycle the variable 'root' to point to the root of the reply
    root = domReply.getDocumentElement();
    String sErr;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to add Remark to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to add Remark to PNR: unrecognized Amadeus response");
    */

    // make sure that all the remarks were added
    pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);
		System.out.println("AmadeusAPIBuildPNRConversation: addPnrElements: Unable to add Remark to PNR=<" + pnr.getErrorsConcatenated() + ">");
    // this is not a fail proof way to catch whether the remark was properly
    // added - for example, for a VGML (veggie meal request) adding a single
    // ssr remark for the whole pnr results in the API adding one SSR remark
    // for each segment with the carrier code of the segment
    // In a situation where the same type of SSR (same service code) is added
    // twice, but the second fails, this method may not detect that error.
    /*for (int i=0; i < aryRemark.length; i++)
      {
      if ( !pnr.hasRemark(aryRemark[i]) )
        {
        String sPNR_Errs = pnr.getErrorsConcatenated();
        throw new GdsResponseException(
            "Unable to add Remark to PNR: " + sPNR_Errs);
        }
      } */

    } // end addPnrElements (remarks)


  /**
   ***********************************************************************
   * This method is used to change the itinerary of one or more passengers in a
   * Passenger Name Record (PNR), as described below.
   * <p>
   * If the passenger list is not specified, or if the passenger list includes
   * all passengers on the PNR, the PNR is modified without creating a new
   * PNR.</p>
   * <p>
   * On the other hand, if the passenger list provided only includes a subset
   * of passengers on the PNR, the designated passengers are first split from
   * the PNR, and the modifications are performed on the split PNR.</p>
   * <p>
   * The split PNR is only saved after the modifications have been successfully
   * performed.  If an error occurs while cancelling the existing segments or
   * adding the new segments, the whole operation fails, and the original PNR
   * is left as it was originally.</p>
   *
   * @param aLocator
   *   the locator identifying the PNR to be modified
   *
   * @param aryPsgrList
   *   the list of passengers which itinerary will be modified; if
   *   <code>null</code>, modify all passengers
   *
   * @param aOldSegments
   *   the segments to be cancelled from the existing PNR
   *
   * @param aNewSegments
   *   the new segments to be added to the PNR
   *
   * @param aReceiveBy
   *   the name of the person requesting the change
   *
   * @param aNewLocator
   *   String Buffer that will hold the split locator, if need be to split the
   *   PNR
   ***********************************************************************
   */
  public static void changeItinerary(final AmadeusAPICrs aCrs,final String aLocator,
      final PNRNameElement[] aryPsgrList, final PNRItinSegment[] aryOldSegments,
      final PNRItinSegment[] aryNewSegments, final String aReceiveBy,
      StringBuffer aNewLocator)
    throws Exception
    {
    // get the existing PNR
    final PNR pnr = new PNR();
    boolean leaveOpen = true;
    aCrs.Ignore();
    aCrs.GetPNRAllSegments(aLocator,pnr,leaveOpen);

    boolean splitPNR = false;

    // make sure that all passengers provided are in the PNR,
    // and check whether we need to split the PNR
    if (aryPsgrList.length > 0)
      {
      for (int i=0; i < aryPsgrList.length; i++)
        {
        // retrieve the passenger by it's PassengerID from the PNR
        // and read the CrsPsgrID
        PNRNameElement psgr = pnr.getName(aryPsgrList[i].getPassengerID());

        if (psgr == null)
          {
          throw new TranServerException(
              " Failed to find passenger: " + aryPsgrList[i].getPassengerID() +
              " in Passenger Name Record: " + pnr.getLocator() );
          }
        }

      // Ok, all passengers in the list are in the PNR
      // now check whether the list is a subset of the passenger in the pnr
      // (this assumes that the passenger list provided contains no duplicates)
      if (pnr.getNames().length > aryPsgrList.length)
        splitPNR = true;
      }

    if (splitPNR == false)
      {
      if (aryOldSegments.length > 0)
        cancelAirSegments(aCrs, pnr, aryOldSegments);

      if (aryNewSegments.length > 0)
        addPnrElements(aCrs, aryNewSegments);

      aCrs.EndTransaction();
      }
    else
      {
      int NUM_UNASSIGNED = 0;
      PNR associatePnr = new PNR();
      splitPNR(aCrs, pnr, associatePnr, NUM_UNASSIGNED, aryPsgrList);

      if (aryOldSegments.length > 0)
        cancelAirSegments(aCrs, associatePnr, aryOldSegments);

      if (aryNewSegments.length > 0)
        addPnrElements(aCrs, aryNewSegments);

      endTransact_AssociatePNR(aCrs,aLocator,aryPsgrList);

      // End Transact the Parent PNR and retrieve
      // the Record Locator for the Associate PNR
      boolean retrieve = true;
      String sNewLocator = aCrs.EndTransaction("TRANSERVER",!retrieve);

      if (sNewLocator.equals(aLocator))
        {
        throw new GdsResponseException(
            " Unable to retrieve locator for Associate PNR, " +
            "Parent Locator was returned instead");
        }
      aNewLocator.append(sNewLocator);
      }

    } // end changeItinerary

  /**
   ***********************************************************************
   * This method adds Special Service Requests (SSR), Other Service
   * Instructions (OSI) and other Remarks to a Passenger Name Record (PNR).
   ***********************************************************************
   */
  public static void addRemark(final AmadeusAPICrs aCrs,
                               final PNRRemark aRemark) throws Exception
    {
    Document domQuery;
    if ( aRemark instanceof PNRSsrRemark)
      domQuery = buildQuery_addSsrRemark( (PNRSsrRemark)aRemark );

    else if ( aRemark instanceof PNROsiRemark )
      domQuery = buildQuery_addOsiRemark( (PNROsiRemark)aRemark );

    else if ( aRemark instanceof PNRItinRemark ||
              aRemark instanceof PNRPocketItinRemark)
      domQuery = buildQuery_addItinRemark(aRemark);

    else if ( aRemark instanceof PNRInvoiceRemark )
      domQuery = buildQuery_addInvoiceRemark(aRemark);

    // not used by Airware
    // else if ( aRemark instanceof PNRAddressRemark )
    //   domQuery = buildQuery_addAddressRemark(aRemark);

    else
      domQuery = buildQuery_addGeneralRemark(aRemark);


    // if we have a valid DOM query
    if (!(domQuery instanceof Document))
      throw new GdsResponseException("Invalid addRemark request");

    // execute the command
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
       domReply, aCrs, "Unable to add Remark to PNR", true);

    /*
    // recycle the variable 'root' to point to the root of the reply
    Element root = domReply.getDocumentElement();
    String sErr;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to add Remark to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to add Remark to PNR: unrecognized Amadeus response");
    */

    PNR pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);
		System.out.println("AmadeusAPIBuildPNRConversation: addRemark: Unable to add Remark to PNR=<" + pnr.getErrorsConcatenated() + ">");
    // this is not a fail proof way to catch whether the remark was properly
    // added - for example, for a VGML (veggie meal request) adding a single
    // ssr remark for the whole pnr results in the API adding one SSR remark
    // for each segment with the carrier code of the segment
    // In a situation where the same type of SSR (same service code) is added
    // twice, but the second fails, this method may not detect that error.
    /*if ( !pnr.hasRemark(aRemark) )
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "Unable to add Remark to PNR: " + sPNR_Errs);
      }*/

  } // end addRemark


  /**
   ***********************************************************************
   * This method adds a Form of Payment (FOP) Remark to a Passenger Name Record
   * (PNR); the maximum length of the FOP is string is 70 characters;
   * the Amadeus API requires that a payment type be specified, and
   * can be either:
   * <ul>
   *   <li><code>CA</code> : Cash          </li>
   *   <li><code>CC</code> : Credit Card   </li>
   *   <li><code>CK</code> : Check         </li>
   *   <li><code>MS</code> : Miscellaneous </li>
   * </ul>
   *
   * Only the first three are valid for ticketing; this method attempts to
   * determine the type of FOP based on the string passed to it; if it
   * cannot determine the FOP type, it adds the FOP as a Miscellaneous FOP.
   ***********************************************************************
   */
  public void addFormOfPayment(AmadeusAPICrs aCrs, String sFOP) throws Exception
    {
    if ( sFOP.length() < 2 )
      throw new TranServerException(
          "You did not specify a valid Form Of Payment (FOP)." +
          " You must specify either 'CC' (credit card)," +
          " 'CA' (cash), 'CH' (check) or 'MS' (miscellaneus)");

    // Map the first two characters of the request string
    // to the appropriate type of Amadeus Code
    String sType = sFOP.substring(0,2);
    if ( sType.equals("CA") )      // cash
      // regardless of the text provided, Amadeus always returns the string
      // 'CASH' when adding a 'CA' type payment; hence, we do so as well so that
      // we can check below that the remark was properly added
      sFOP = "CASH";
    else if ( sType.equals("CC") ) // credit card
      ; // do nothing
    else if ( sType.equals("CH") ) // check
      {
      sType = "CK";
      // regardless of the text provided, Amadeus always returns the string
      // 'CHECK' when adding a 'CK' type payment; hence, we do so as well so
      // that we can check below that the remark was properly added
      sFOP  = "CHECK";
      }
    else if ( sType.equals("MS") ) // miscellaneous
      ; // do nothing
    else
      throw new TranServerException(
          "The form of payment code that you specified: '" + sType +
          "' is not valid. " +
          " You must specify either 'CC' (credit card)," +
          " 'CA' (cash), 'CH' (check) or 'MS' (miscellaneus)");

    Document domQuery = buildQuery_addFormOfPayment(sType,sFOP);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    domReply = validateReply(
       domReply, aCrs, "Unable to add Form of Payment (FOP) to PNR", true);

    /*
    Element root = domReply.getDocumentElement();

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException("Unable to add FOP to PNR: " + sErr);
      }
    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to add FOP to PNR: unrecognized Amadeus response");

    */

    // parse the pnr
    PNR pnr = new PNR();
    AmadeusAPIParsePNR.parsePNR(domReply, pnr);

    // make sure that the FOP was added
    if (!(pnr.hasRemark(new PNRFopRemark(sFOP))) )
      {
      String sPNR_Errs = pnr.getErrorsConcatenated();
      throw new GdsResponseException(
          "unable to add FOP to PNR: " + sPNR_Errs);
      }
    } // end addFormOfPayment


  /**
   ***********************************************************************
    * This method builds a DOM query to be sent to the Amadeus XML server
    * to add a Commission Amount to a Passenger Name Record (PNR);
    *
    * @param aCrs
    *   the AmadeusAPICrs object used to connect to the Amadeus Server
    * @param fCommission
    *   the commission percentage or literal amount, typically expressed
    *   with 2 decimal points
    * @param isPercent
    *   a flag indicating whether the fCommission amount passed is a percent or
    *   is a literal amount (i.e. $5.00 or %5.00)
   ***********************************************************************
   */
  public static void addCommission (final AmadeusAPICrs aCrs,
      final float fCommission, final boolean isPercent ) throws Exception
   {
    // make sure that we have the required elements
    if ( fCommission < 0 )
      throw new TranServerException(
          "The Commission Amount that you specified: " + fCommission + " is not valid");

    final DecimalFormat CommissionFormat = new DecimalFormat("0.00");

    final String sCommand;
    if ( isPercent )
      sCommand = "FM" + CommissionFormat.format(fCommission);
    else
      sCommand = "FM" + CommissionFormat.format(fCommission) + "A";

    final String sResponse = aCrs.HostTransaction(sCommand).trim();
    if ( sResponse.indexOf("RP/") < 0 )
      throw new GdsResponseException("Unable to add commission",sCommand,sResponse);

    /*
    String sCommission;
    //if (fCommission > 0)
      sCommission = String.valueOf(fCommission);
    //else
      //sCommission = "0";

    Document domQuery = buildQuery_addCommission(sCommission,isPercent);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    Element root = domReply.getDocumentElement();
    String sErr = null;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to add Commission to PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to add Commission to PNR: unrecognized Amadeus response");

    else
      {
      PNR pnr = new PNR();
      AmadeusAPIParsePNR.parsePNR(domReply, pnr);

      // if a fixed amount is provided, Amadeus attaches a "A" to the amount
      if (!isPercent)
        sCommission += "A";

      if (!pnr.hasRemark(new PNRGeneralRemark(sCommission)))
        {
        String sPNR_Errs = pnr.getErrorsConcatenated();
        throw new GdsResponseException(
            "Unable to add Commission to PNR: " + sPNR_Errs);
        }
     } // end else
    */

    } // end addCommission


  /**
   ***********************************************************************
   * Given a PNR that has been retrieved and a group of segments, this method
   * cancels the segments from that PNR, but does not end the transaction;
   * hence, this method may be used in more complex transactions where it is
   * not desirable to end the transaction immediately after cancelling the
   * segments, such as when modifying segments on a split pnr.
   *
   * @throws GdsResponseException  if the Global Distribution System
   *   was unable to remove the air segments from the PNR
   ***********************************************************************
   */
  public static void cancelAirSegments(final AmadeusAPICrs aCrs,
     PNR pnr, final PNRItinSegment[] aAirSegs) throws Exception
    {
    Document domQuery = buildQuery_cancelAirSegments(pnr, aAirSegs);
    // String debug = DOMutil.domToString(domQuery);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    Element root = domReply.getDocumentElement();

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      // if we cancel all segments the response will come back within an error
      // message
      if (sErr.indexOf("ITINERARY CANCELLED") > 0)
        {
        return;
        }
      else
        throw new GdsResponseException("Unable to cancel Air Segments on PNR: " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to cancel Air Segments on PNR: unrecognized Amadeus response");
    else // we received a PoweredPNR_PNRReply
      {
      PNR pnr_rcvd = new PNR();
      AmadeusAPIParsePNR.parsePNR(domReply, pnr_rcvd);

      // make sure that the Segment(s) was deleted
      PNRItinAirSegment airseg;
      String sErr = "";
      for ( int i = 0; i < aAirSegs.length; i++ )
        {
        PNRItinAirSegment aAirSeg = (PNRItinAirSegment)aAirSegs[i];
        airseg = pnr_rcvd.getItinSegmentByFlight(aAirSeg.Carrier,aAirSeg.FlightNumber);
        if ( airseg instanceof PNRItinAirSegment )
          sErr = " Unable to cancel " + aAirSeg.Carrier + aAirSeg.FlightNumber;
        }

      if (sErr.length() > 0)
        throw new GdsResponseException( sErr + pnr.getErrorsConcatenated() );
      }
    }


  /**
   ***********************************************************************
   * This method retrieves the PNR specified, and builds and executes the DOM
   * query needed to cancel a group of air segments; it ends the transaction
   * if the query was successfully executed.
   *
   * @throws GdsResponseException  if the Global Distribution System
   *   was unable to remove the air segments from the PNR
   ***********************************************************************
   */
  public static void cancelAirSegments(final AmadeusAPICrs aCrs,
                                       final String aLocator,
                                       final PNRItinSegment[] aAirSegs,
                                       final String aReceiveBy) throws Exception
    {
    final PNR pnr = new PNR();
    if (GnrcFormat.NotNull(aLocator))
      {
      boolean leaveOpen = true;
      aCrs.Ignore();
      aCrs.GetPNRAllSegments(aLocator,pnr,leaveOpen);
      }
    else
      aCrs.GetPNRFromAAA(pnr);

    cancelAirSegments(aCrs,pnr,aAirSegs);
    aCrs.EndTransaction(aReceiveBy);

    } // end cancelAirSegments

  /**
   ***********************************************************************
   * This method creates the Query needed to cancel the whole itinerary in a
   * Passenger Name Record (PNR); this is equivalent to the <code>XI</code>
   * cryptic code command; optionally, a PNR locator can be passed, and the PNR
   * corresponding to such locator will be retrieved before the cancel command
   * is sent.
   ***********************************************************************
   */
  public static void cancelItinerary(AmadeusAPICrs aCrs,
                                     String sLocator,
                                     String sReceivedBy) throws Exception
    {
    // this should not be necessary, but it's safer
    if (sLocator instanceof String)
       aCrs.LoadPNRIntoAAA(sLocator);

    Document domQuery = new DocumentImpl();
    // "null"  The locator should go here, and it should not be necessary
    // to retrieve the PNR before cancelling it.  Nonetheless, this
    // does not always work, for reasons unknown, and it is safer to
    // retrieve the PNR and then cancel it.
    // "10" Process and End Transaction
    // "I" XI type cancel
    buildNode_PoweredPNR_cancelElements(domQuery,null,"10","I");
    // String debug = DOMutil.domToString(domQuery);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    Element root = domReply.getDocumentElement();

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException("Unable to cancel itinerary PNR: " + sErr);
      }
    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to cancel itinerary on PNR: unrecognized Amadeus response");
    } // end cancelItinerary


  /**
   ***********************************************************************
   * This method implements the Airware cancel command verb; the process goes
   * as follows:
   * - a remark object is passed to the cancelRemark method
   * - all remarks are retrieved from the pnr
   * - the method loops through the remarks on the pnr, and for each remark
   *   that matches the remark passed, the cancelCommand is sent
   *
   ***********************************************************************
   */
  public static void cancelRemark(final AmadeusAPICrs aCrs,
     final String aLocator, final PNRRemark aRemark) throws Exception
    {
    // get the existing PNR
    PNR pnr = new PNR();
    aCrs.Ignore();
    aCrs.GetPNRAllSegments(aLocator,pnr,true);

    // determine the line number of the remark
    PNRRemark[] remarks = pnr.getRemarks();

    if ( remarks instanceof PNRRemark[] )
      {
      // store the deleted remarks in this vector
      Vector vDeletedRemarks = new Vector();
      Document domQuery = null;
      Document domReply = null;

      for (int i=0; i < remarks.length ; i++ )
        {
        if ( AmadeusBuildPNRConversation.isSelectedRemark(aRemark,remarks[i]) )
          {
          vDeletedRemarks.add(remarks[i]);
          // for the time being we send each cancel command one at a time, but
          // we want to have the option of cancelling multiple remarks at once,
          // so we need to pass an array to the buildQuery_cancelRemarks method
          PNRRemark[] delRemark = {remarks[i]};
          domQuery = buildQuery_cancelRemarks(delRemark);
          domReply = aCrs.connection.sendAndReceive(domQuery);

          Element root = domReply.getDocumentElement();

          // if we did not get a proper reply, issue an error
          if ( root.getTagName().equals("MessagesOnly_Reply") )
            {
            String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
            throw new GdsResponseException("Unable to cancel PNR remark: " + sErr);
            }
          else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
            throw new GdsResponseException(
                "Unable to cancel remark on PNR: unrecognized Amadeus response");
          }
        } // end for

      // check that all remarks were properly deleted
      if (domReply instanceof Document)
        {
        pnr = new PNR();
        pnr = AmadeusAPIParsePNR.parsePNR(domReply);
        remarks = pnr.getRemarks();

        for (int i=0; i < vDeletedRemarks.size(); i++)
          {
          PNRRemark delRemark = (PNRRemark)vDeletedRemarks.get(i);
          for (int j=0; j < remarks.length; j++)
            {
            if (AmadeusBuildPNRConversation.isSelectedRemark( delRemark,remarks[j]) )
              {
              throw new GdsResponseException(
                " Unable to delete remark: " + delRemark.CrsMessageID + " | " +
                delRemark.getRemarkType() + " | " + delRemark.getRemarkText());
              }
            } // end for
          } // end for
        }
      }

    } // end cancelRemark


  /**
   ***********************************************************************
   * This method implements the Airware cancel command verb; the process goes
   * as follows:
   * <ul>
   *  <li>an array of remarks is passed to the cancelRemark method</li>
   *  <li>all remarks are retrieved from the pnr</li>
   *  <li>the method loops through the remarks on the pnr, and for each remark
   *      that matches the remark passed, the cancelCommand is sent</li>
   * </ul>
   ***********************************************************************
   */
  public static void cancelRemarks(final AmadeusAPICrs aCrs,
     final String aLocator, final PNRRemark[] delRemarks) throws Exception
    {
    if (delRemarks == null)
      throw new TranServerException("No Remarks to Cancel");

    // get the existing PNR
    PNR pnr = new PNR();
    if (GnrcFormat.NotNull(aLocator))
      {
      aCrs.Ignore();
      aCrs.GetPNRAllSegments(aLocator,pnr,true);
      }
    else
      aCrs.GetPNRFromAAA(pnr);

    // determine the line number of the remark
    PNRRemark[] pnrRemarks = pnr.getRemarks();

    if ( pnrRemarks instanceof PNRRemark[] )
      {
      /*
      // turn the array into a list so that we can remove items matched
      ArrayList pnrRmkList = new ArrayList();
      for (int i=0; i < pnrRemarks.length ; i++)
        pnrRmkList.add(pnrRemarks[i]);

      // store the remarks to be deleted in this vector
      Vector vDeleteRemarks = new Vector();

      for (int i=0; i < delRemarks.length ; i++ )
        {
        for (int j=0; j < pnrRmkList.size(); j++)
          {
          PNRRemark rmk2del   = delRemarks[i];
          PNRRemark pnrRemark = (PNRRemark)pnrRmkList.get(j);
          if ( isSelectedRemark(rmk2del,pnrRemark) )
            {
            vDeleteRemarks.add(pnrRemark);
            // remove the pnr remark matched in the event that we have duplicate
            // remarks on the pnr - this ensures that if two duplicates are to
            // be erased, the first remark on the pnr is not selected twice -
            // this also speeds processing
            pnrRmkList.remove(j);
            // break; // we now delete all remarks matching a pattern
            }
          }
        }
      */

      // if one of the remarks to be deleted does not exist on the PNR,
      // continue processing without
      /*
      if (vDeleteRemarks.size() != delRemarks.length)
        throw new TranServerException(
            "Unable to find in the PNR all the remarks to be deleted");
      */

      // if there are no remarks to delete (none was found in the PNR)
      // return without complaining

      // if (vDeleteRemarks.size() == 0)
      //   return;

      // Document domQuery = buildQuery_cancelRemarks(
      //    (PNRRemark[])(vDeleteRemarks.toArray(new PNRRemark[vDeleteRemarks.size()])));

      // retrieve all the remarks from the PNR that match the remarks provided
      PNRRemark[] rmk2Delete = getMatchingRemarks(delRemarks,pnrRemarks);

      if (rmk2Delete.length == 0)
        return;

      Document domQuery = buildQuery_cancelRemarks(rmk2Delete);
      Document domReply = aCrs.connection.sendAndReceive(domQuery);

      domReply = validateReply(
         domReply, aCrs, "Unable to cancel PNR Remark", true);

      /*
      Element root = domReply.getDocumentElement();

      // if we did not get a proper reply, issue an error
      if ( root.getTagName().equals("MessagesOnly_Reply") )
        {
        String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
        throw new GdsResponseException("Unable to cancel PNR remark: " + sErr);
        }
      else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
        throw new GdsResponseException(
            "Unable to cancel remark on PNR: unrecognized Amadeus response");
      */

      // check that all pnrRemarks were properly deleted
      if (domReply instanceof Document)
        {
        pnr = new PNR();
        pnr = AmadeusAPIParsePNR.parsePNR(domReply);
        pnrRemarks = pnr.getRemarks();

        for (int i=0; i < rmk2Delete.length; i++)
          {
          PNRRemark delRemark = rmk2Delete[i];
          for (int j=0; j < pnrRemarks.length; j++)
            {
            if (AmadeusBuildPNRConversation.isSelectedRemark( delRemark,pnrRemarks[j]) )
              {
              throw new GdsResponseException(
                " Unable to delete remark: " + delRemark.CrsMessageID + " | " +
                delRemark.getRemarkType() + " | " + delRemark.getRemarkText());
              }
            } // end for
          } // end for
        }
      } // end if

    } // end cancelRemarks


  /**
   ***********************************************************************
   * This method builds and executes the <code>PoweredPNR_Split</code> needed
   * to split a PNR, but does not save the associated PNR (child PNR) or it's
   * parent PNR; this method is useful when further operations must be
   * performed on the PNR after it is split.
   ***********************************************************************
   */
  public static void splitPNR(final AmadeusAPICrs aCrs, PNR pnr, PNR associatePnr,
      final int aNumUnassigned, final PNRNameElement[] aNames) throws Exception
    {
    Document domQuery;

    // build the split query
    domQuery = buildQuery_PoweredPNR_Split(aNumUnassigned,aNames,pnr);

    // String debug = DOMutil.domToString(domQuery);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    Element root = domReply.getDocumentElement();

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
         " Unable to split PNR " + pnr.getLocator() + ": " + sErr);
      }

    else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
      throw new GdsResponseException(
          "Unable to split PNR " + pnr.getLocator() + ": " + "unrecognized Amadeus response");

    else // we received a PoweredPNR_PNRReply
      {
      //PNR associatePNR = new PNR();
      AmadeusAPIParsePNR.parsePNR(domReply, associatePnr);

      // make sure that we were returned an Associate PNR
      String sMsg = AmadeusAPIParsePNR.readNode_freetextData(domReply);
      if (sMsg.indexOf("ASSOCIATE PNR") < 0)
        {
        String sErr = associatePnr.getErrorsConcatenated();
        throw new GdsResponseException(" Unable to retrieve Associate PNR:" + sErr);
        }

      boolean isOK = true;
      String sLostPassengers = "";
      // make sure that it contains all the passengers in aNames[]
      if (aNames instanceof PNRNameElement[])
        {
        for (int i=0; i < aNames.length; i++)
          {
          PNRNameElement pnrName =
            associatePnr.getName(aNames[i].getPassengerID());
          if (pnrName instanceof PNRNameElement == false )
            sLostPassengers += aNames[i].getPassengerID() + ",";
          }

        if (sLostPassengers.length() > 0)
          {
          String sErr = associatePnr.getErrorsConcatenated();
          throw new GdsResponseException(
              " Unable to find passenger: " + sLostPassengers +
              " in Associate PNR - PNR errors: " + sErr );
          }
        }
      }
    } // end splitPNR

  /**
   ***********************************************************************
   * This method builds and executes the <code>PoweredPNR_PNRReply</code>
   * needed to split a PNR, and then saves the associated PNR (child PNR) and
   * its parent PNR.
   *
   * @throws GdsResponseException  if the Global Distribution System
   *   was unable to split the PNR
   ***********************************************************************
   */
  public static void splitPNR(final AmadeusAPICrs aCrs, final String aLocator,
                       final int aNumUnassigned, final PNRNameElement[] aNames,
                       final String aReceiveBy, final StringBuffer aNewLocator)
    throws Exception
    {
    // get the existing PNR
    final PNR pnr = new PNR();
    boolean leaveOpen = true;
    aCrs.Ignore();
    aCrs.GetPNRAllSegments(aLocator,pnr,leaveOpen);

    splitPNR(aCrs, pnr,new PNR(), aNumUnassigned, aNames);

    // End Transact the Associate PNR
    AmadeusAPIBuildPNRConversation.endTransact_AssociatePNR(aCrs,aLocator,aNames);

    // End Transact the Parent PNR and retrieve
    // the Record Locator for the Associate PNR
    boolean retrieve = true;
    String sNewLocator = aCrs.EndTransaction("TRANSERVER",!retrieve);

    if (sNewLocator.equals(aLocator))
      {
      throw new GdsResponseException(
          " Unable to retrieve locator for Associate PNR, " +
          "Parent Locator was returned instead");
      }
    aNewLocator.append(sNewLocator);

    } // end splitPNR


  /**
   ***********************************************************************
   * This method saves an 'Associate PNR' that was split from it's original
   * 'Parent PNR'; it must be called after the 'Associate PNR' is returned
   * following the original 'split' command; upon issuing the endTransact
   * command to the Associate PNR, the Parent PNR is returned, but without the
   * passengers or seats that were split from it; after the end Transact
   * command is issued, this method checks that the passengers or unassigned
   * seats were properly removed from the original PNR.
   *
   * @param sLocator
   * the locator of the original PNR, needed to verify that this the parent PNR
   * was properly returned
   *
   * @param names
   * an array of PNRNameElement objects containing the passengers that were
   * to be split from the original PNR, used to verify that these passengers
   * are no longer in the split PNR
   *
   * @throws GdsResponseException if an error occurred while saving the
   * Associate PNR
   ***********************************************************************
   */
  public static void endTransact_AssociatePNR(
      AmadeusAPICrs aCrs,String sLocator,PNRNameElement[] names) throws Exception
    {
    // create the document query
    DocumentImpl domQuery = new DocumentImpl();
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    Element elm1 = domQuery.createElement("pnrActions");
    // "14" means End Transaction on Associate PNR
    DOMutil.addTextElement(elm1,"optionCode","14");
    root.appendChild(elm1);
    domQuery.appendChild(root);

    // execute the command
    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    // recycle the root variable to point to the root of the reply
    root = domReply.getDocumentElement();

    // if we did not get a PNR_Reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(" Error when saving Associate PNR: " + sErr);
      }

    else if ( root.getTagName().equals("PoweredPNR_PNRReply") == false)
      throw new GdsResponseException(
          " Unable to save associate PNR, unrecognized response from Amadeus");
    else
      {
      PNR pnr = AmadeusAPIParsePNR.parsePNR(domReply);

      // Make sure that we were returned the original PNR
      String sLoctr = AmadeusAPIParsePNR.getLocator(domReply);
      String sMsg = AmadeusAPIParsePNR.readNode_freetextData(domReply);
      if (! (sLoctr.equals(sLocator) && sMsg.indexOf("PARENT PNR") >= 0) )
        {
        throw new GdsResponseException(
           " Unable to retrieve Parent PNR when saving associate PNR: " +
           pnr.getErrorsConcatenated());
        }

      // Make sure that none of the passengers split
      // from the Parent PNR are still around
      String hangerOns = "";
      if (names instanceof PNRNameElement[])
        {
        for (int i=0; i < names.length; i++)
          {
          if (pnr.getName(names[i].getPassengerID()) instanceof PNRNameElement)
            hangerOns += names[i].getPassengerID() + ",";
          } // end for

        if (hangerOns.length() > 0)
          {
          throw new GdsResponseException(
              " Unable to remove passengers: " + hangerOns + " from Parent PNR" +
              " - PNR Errors: " + pnr.getErrorsConcatenated());
          }
        }
      }

    } // end endTransact_AssociatePNR


  /**
   ***********************************************************************
   * This method is used as part of {@link addPnrElements} to sell flights from
   * NegoSpace blocks; this requires displaying the block, and issuing a query
   * to short sell from the block; in the case of Unscheduled Flights, it also
   * requires adding the Departure and Arrival times through a cryptic call
   ***********************************************************************
   */
  private static Document sellNegoAirSegment(
      AmadeusAPICrs aCrs, PNRItinAirSegment negoSeg) throws Exception
  {
    // retrieve a block availability by block locator
    Document domQuery =
      buildQuery_displayBlockAvail(negoSeg.RemoteLocator);

    Document domReply = aCrs.connection.sendAndReceive(domQuery);

    Element root = domReply.getDocumentElement();

    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to retrieve avail for NegoSpace block: " +
          negoSeg.RemoteLocator + " containing flight: " +
          negoSeg.Carrier + " " + negoSeg.FlightNumber +
          " - error: " + sErr);
      }

    if (AmadeusAPIBlockConversation.negoIsNotAvailable(domReply))
      {
      throw new GdsResponseException(
          "NegoSpace block: " + negoSeg.RemoteLocator + " containing flight: " +
          negoSeg.Carrier + " " + negoSeg.FlightNumber + " does not exist");
      }


    // sell from availability - this returns a PNR_Reply
    // rather than a PoweredPNR_PNRReply
    domQuery = buildQuery_sellFromBlockAvail(negoSeg);
    domReply = aCrs.connection.sendAndReceive(domQuery);

    root = domReply.getDocumentElement();

    if ( root.getTagName().equals("PoweredAir_SellFromAvailabilityReply") )
      System.out.println("buildQuery_sellFromBlockAvail:returned ok.");
    else if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException("Unable to Sell Flight " +
          negoSeg.Carrier + " " + negoSeg.FlightNumber +
          " from Block Availability: " + sErr);
      }
    else if ( !(root.getTagName().equals("PNR_Reply")) )
      throw new GdsResponseException("Unable to Sell Flight " +
          negoSeg.Carrier + " " + negoSeg.FlightNumber +
          " from Block Availability: unrecognized Amadeus response");

    // if this is an Unscheduled Flight, add the Departure/Arrival times
    // through a Cryptic Command
    if (negoSeg.isScheduled == false)
      {
      // get the Departure/Arrival Time from the request
      String sDepTime = xmax.util.DateTime.fmtLongDateTime(
          negoSeg.DepartureDateTime, "HHmm");

      String sArrTime = xmax.util.DateTime.fmtLongDateTime(
          negoSeg.ArrivalDateTime, "HHmm");

      // retrieve the pnr to get the segment line number
      domQuery =
        AmadeusAPIGetPNRConversation.buildQuery_redisplayPNR();
      domReply = aCrs.connection.sendAndReceive(domQuery);

      PNR pnr = new PNR();
      AmadeusAPIParsePNR.parsePNR(domReply, pnr);

      // build and execute the cryptic command - e.g.: 3/16001739
      int lineNum = pnr.getItinSegmentByFlight(
          negoSeg.Carrier,negoSeg.FlightNumber).SegmentNumber;
      String sAddTimes = lineNum + "/" + sDepTime + sArrTime;

      aCrs.sendRecvCryptic(sAddTimes);
      }

    // redisplay the PNR in a PoweredPNR_PNRReply format, so that we can
    // check below that the segment was added
    domQuery =
      AmadeusAPIGetPNRConversation.buildQuery_redisplayPNR();
    domReply = aCrs.connection.sendAndReceive(domQuery);

    return domReply;

  } // end sellNegoAirSegment


  /**
   ***********************************************************************
   * This method builds a DOM query to be sent to the Amadeus XML server
   * to add a Phone field to a Passenger Name Record (PNR).
   *
   * @param aCrs
   *   the AmadeusAPICrs object used to connect to the Amadeus Server
   * @param sPhone
   *   the phone String that we wish to add to the PNR
   ***********************************************************************
   */
  private static Document buildQuery_addPhone (String sPhone)
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sPhone) )
      return(null);

    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm11;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","AP");
    /*
    // the line above is the equivalent of:
    // the dataElementsMaster Node
    elm1 = domQuery.createElement("dataElementsMaster");
    // this is a necessary 'dummy' marker that contains no data
    elm11 = domQuery.createElement("marker1");
    elm1.appendChild(elm11);

    elm11 = domQuery.createElement("dataElementsIndiv");
    elm111 = domQuery.createElement("elementManagementData");
    elm1111 = domQuery.createElement("reference");
    // "OT" means 'PNR Element Reference provided by the Client'
    DOMutil.addTextElement(elm1111,"qualifier","OT");
    // According to the documentation, this element can be used to make
    // an association with a line number on the PNR (which could be entered
    // later).  This element is mandatory, and its use remains obscure
    // as of this writing.
    DOMutil.addTextElement(elm1111,"number","1");
    elm111.appendChild(elm1111);
    // "AP" stands for 'Add Phone'
    DOMutil.addTextElement(elm111,"segmentName","AP");

    elm11.appendChild(elm111);
    */

    appendNode_freetextData(elm11,"3","6",null,sPhone);

    return(domQuery);

    } // end buildQuery_addPhone


  /**
   ***********************************************************************
   * This method builds a DOM query to be sent to the Amadeus XML server
   * to add a Received From to a Passenger Name Record (PNR).
   *
   * @param aCrs
   *   the AmadeusAPICrs object used to connect to the Amadeus Server
   * @param sName
   *   the ReceivedBy String that we wish to add to the PNR
   ***********************************************************************
   */
  private static Document buildQuery_addReceivedBy (String sName)
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sName) )
      return(null);

    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm11;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","RF");

    /*
    elm111 = domQuery.createElement("freetextData");
    elm1111 = domQuery.createElement("freetextDetail");
    // "3" means Literal Text
    DOMutil.addTextElement(elm1111,"subjectQualifier","3");
    // "P22" stands for 'Received From'
    DOMutil.addTextElement(elm1111,"type","P22");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"longFreetext",sName);
    */

    appendNode_freetextData(elm11,"3","P22",null,sName);

    return(domQuery);

    } // end buildQuery_addReceivedBy


  /**
   ***********************************************************************
    * This method builds a DOM query to be sent to the Amadeus XML server
    * to add a Ticketing Instructions to a Passenger Name Record (PNR); as
    * it stands, this method ignores the TicketRemark passed and adds
    * to the PNR an 'OK' ticket-at-will instruction; in order to add a
    * different ticketing instructions, the TicketRemark parameter would
    * have to be structured further.
    *
    * @param aCrs
    *   the AmadeusAPICrs object used to connect to the Amadeus Server
    * @param aTicketRemark
    *   ignored at this time
   ***********************************************************************
   */
  public static Document buildQuery_addTicket(final String sTicketRemark)
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sTicketRemark) )
      return(null);

    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm11, elm111, elm1111;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","TK");

    elm111 = domQuery.createElement("ticketElement");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("ticket");
    elm111.appendChild(elm1111);
    DOMutil.addTextElement(elm1111,"indicator","OK");

    /*
    // "TL" means Time Limit
    DOMutil.addTextElement(elm1111,"indicator","TL");
    // date in European format: ddMMyy
    DOMutil.addTextElement(elm1111,"date","151201");
    // time in hhmm military format
    DOMutil.addTextElement(elm1111,"time","2300");
    // officeID must be 9 characters long
    DOMutil.addTextElement(elm1111,"officeId","MIA1S1101");
    */
    return(domQuery);
   } // end buildQuery_addTicket


  /**
   ***********************************************************************
   * This method builds a DOM query to be sent to the Amadeus XML server
   * to add a Group Header field to a Passenger Name Record (PNR).
   *
   * @param aCrs
   *   the AmadeusAPICrs object used to connect to the Amadeus Server
   * @param sPhone
   *   the phone String that we wish to add to the PNR
   ***********************************************************************
   */
  private static Document buildQuery_addCorpHeader (String sGroupName,
                                                    int iNumSeats)
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sGroupName) || iNumSeats <= 0 )
      return(null);

    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm1, elm11, elm111, elm1111;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    elm1 = buildNode_travellerInfo(domQuery,"PR","1","NG");

    appendNode_travellerInformation(domQuery,elm1,sGroupName,"G",iNumSeats);

    return(domQuery);

    } // end buildQuery_addCorpHeader


  /**
   ***********************************************************************
   * This method returns a query to add a Passenger Name to a PNR
   ***********************************************************************
   */
  private static Document buildQuery_addName(PNRNameElement aName )
    {

    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111, elm1111;
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    elm1 = domQuery.createElement("pnrActions");
    // "0" means add Remark / Element and do no processing
    // (as opposed to a save and retrieve, for example)
    DOMutil.addTextElement(elm1,"optionCode","0");
    root.appendChild(elm1);

    elm1 = domQuery.createElement("travellerInfo");
    elm11 = domQuery.createElement("elementManagementPassenger");

    elm111 = domQuery.createElement("reference");
    // "PR" for Passenger Reference given by the Client
    DOMutil.addTextElement(elm111,"qualifier","PR");
    DOMutil.addTextElement(elm111,"number","1");
    elm11.appendChild(elm111);

    // "NM" stands for Name
    DOMutil.addTextElement(elm11,"segmentName","NM");

    elm1.appendChild(elm11);

		elm11 = domQuery.createElement("passengerData");
    elm111 = domQuery.createElement("travellerInformation");
    // the traveller element loosely corresponds to a 'family' in Airware
    elm1111 = domQuery.createElement("traveller");
    DOMutil.addTextElement(elm1111,"surname",aName.LastName);
    DOMutil.addTextElement(elm1111,"quantity","1");
    elm111.appendChild(elm1111);

    elm1111 = domQuery.createElement("passenger");
    DOMutil.addTextElement(elm1111,"firstName",aName.FirstName);
    DOMutil.addTextElement(elm1111,"type",aName.PTC);
    if ( GnrcFormat.NotNull(aName.InfantName) )
      DOMutil.addTextElement(elm1111,"infantIndicator","3");

    // when entering the identification code, Amadeus requires
    // that we prefix it with either 'CR' or 'ID' - we use the latter
    if ( GnrcFormat.NotNull(aName.getPassengerID()))
      DOMutil.addTextElement(
          domQuery,elm1111,"identificationCode","ID" + aName.getPassengerID());

    elm111.appendChild(elm1111);

		// create new travellerInformation for infant
		if ( GnrcFormat.NotNull(aName.InfantName) )
      {
			elm11.appendChild(elm111);        // close parent travellerInformation
			elm1.appendChild(elm11);        	// close parent passengerData

			elm11 = domQuery.createElement("passengerData");
			elm111 = domQuery.createElement("travellerInformation");
			elm1111 = domQuery.createElement("traveller");
      DOMutil.addTextElement(elm1111,"surname",aName.LastName);
			elm111.appendChild(elm1111);

			elm1111 = domQuery.createElement("passenger");
      DOMutil.addTextElement(elm1111,"firstName",aName.InfantName);
      DOMutil.addTextElement(elm1111,"type","INF");
      elm111.appendChild(elm1111);
			elm11.appendChild(elm111);

			elm111 = domQuery.createElement("dateOfBirth");
			elm1111 = domQuery.createElement("dateAndTimeDetails");
			DOMutil.addTextElement(elm1111,"qualifier","706");
			DOMutil.addTextElement(elm1111,"date",aName.InfantDOB);
			elm111.appendChild(elm1111);
      }

		elm11.appendChild(elm111);
		elm1.appendChild(elm11);
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);

    return domQuery;

    } // end buildQuery_addName


  /**
   ***********************************************************************
   * This method accepts an array of segments and builds a query to sell those
   * segments as combined segments, where the departure city of the first
   * segment and the arrival city of the last segment in the array correspond
   * to the origin and destination of the itinerary; this makes it possible to
   * access the inventory reserved for the combined segments rather than
   * accessing the inventory on a segment by segment basis.
   *
   * @see PNRItinAirSegment.groupContinuousLiveSegments
   ***********************************************************************
   */
  private static void buildQuery_sellAirSegments(Document domQuery,
                                         final PNRItinAirSegment[] airSegs)
    {
    // if we do not have a root element, this method will build it
    // otherwise it returns a reference to the root element
    Element root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    PNRItinAirSegment firstSeg = airSegs[0];
    PNRItinAirSegment lastSeg  = airSegs[airSegs.length-1];

    Element elm1 = buildNode_originDestinationDetails(
        domQuery,firstSeg.DepartureCityCode,lastSeg.ArrivalCityCode);

    for (int i=0; i < airSegs.length ; i++) {
      appendNode_itineraryInfo(domQuery,elm1,airSegs[i],"SR","1");
      }
    } // end buildQuery_sellAirSegment


  /**
   ***********************************************************************
   * This method accepts an Arunk segment and builds a query to add that
   * arunk segment to the PNR
   ***********************************************************************
   */
  private static void buildQuery_addArunkSegment(
      Document domQuery, final PNRItinArunkSegment arunkSeg)
    {
    // if we do not have a root element, this method will build it
    // otherwise it returns a reference to the root element
    Element root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    //Element elm1 = buildNode_originDestinationDetails(
    //     domQuery,arunkSeg.DepartureCityCode,arunkSeg.ArrivalCityCode);

    Element elm1 = buildNode_originDestinationDetails(
        domQuery,"","");

    appendNode_itineraryInfo(domQuery,elm1,arunkSeg,"SR","1");

    } // end buildQuery_addArunkSegment


  /**
   ***********************************************************************
   * This method returns a query to add an Invoice Remark to a PNR
   ***********************************************************************
   */
  private static Document buildQuery_addInvoiceRemark (PNRRemark invcRemark)
    {
    if ( GnrcFormat.IsNull(invcRemark.RemarkText) )
      return(null);

    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm1, elm11, elm111, elm1111;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","RI");

    appendNode_miscellaneousRemark(
        elm11,"RI","F",invcRemark.RemarkText);

    // if this Remark is to be associated with a specific Passenger
    // it will have been populated with the CrsPsgrRef (Passenger Tattoo)
    // parsed from the PNR that identifies the passenger
    if (invcRemark.CrsPsgrRef instanceof String)
      appendNode_referenceForDataElement(elm11,"PT",invcRemark.CrsPsgrRef);

    // if this Remark is to be associated with a specific Segment
    // it will have been populated with the CrsSegmentID (Segment Tattoo)
    // parsed from the PNR that identifies the segment
    if (invcRemark.CrsSegmentRef instanceof String)
      appendNode_referenceForDataElement(elm11,"ST",invcRemark.CrsSegmentRef);

    return(domQuery);

    } // end buildQuery_addInvoiceRemark


  /**
   ***********************************************************************
   * This method returns a query to add a General Remark to a PNR; it is
   * the catch all for other more specific remark types that have not been
   * implemented; the maximum length of a General Remark that can be passed
   * to the Amadeus API is 128 chars, which is also the maximum length
   * permitted by Airware.
   ***********************************************************************
   */
  private static Document buildQuery_addGeneralRemark (PNRRemark aRemark)
    {
    // if there is nothing to add, do nothing
    if ( GnrcFormat.IsNull(aRemark.RemarkText) )
      return(null);

    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm11;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","RM");

    appendNode_miscellaneousRemark(
        elm11,"RM",null,aRemark.RemarkText);

    // if this Remark is to be associated with a specific Passenger
    // it will have been populated with the CrsPsgrRef (Passenger Tattoo)
    // parsed from the PNR that identifies the passenger
    if (aRemark.CrsPsgrRef instanceof String)
      appendNode_referenceForDataElement(elm11,"PT",aRemark.CrsPsgrRef);

    // if this Remark is to be associated with a specific Segment
    // it will have been populated with the CrsSegmentID (Segment Tattoo)
    // parsed from the PNR that identifies the segment
    if (aRemark.CrsSegmentRef instanceof String)
      appendNode_referenceForDataElement(elm11,"ST",aRemark.CrsSegmentRef);

    /*
    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111, elm1111;
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    elm1 = domQuery.createElement("pnrActions");
    // "0" means add Remark / Element and do no processing
    // (as opposed to a save and retrieve, for example)
    DOMutil.addTextElement(elm1,"optionCode","0");
    root.appendChild(elm1);

    // the messageActionDetails node
    elm1 = domQuery.createElement("dataElementsMaster");
    // a dummy field
    elm11 = domQuery.createElement("marker1");
    elm1.appendChild(elm11);

    elm11 = domQuery.createElement("dataElementsIndiv");

    elm111 = domQuery.createElement("elementManagementData");
    elm1111 = domQuery.createElement("reference");
    // "OT" means 'PNR Element Reference provided by the Client'
    DOMutil.addTextElement(elm1111,"qualifier","OT");
    // this is used to create references internal to each message
    // see Amadeus documentation
    DOMutil.addTextElement(elm1111,"number","1");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"segmentName","RM");
    elm11.appendChild(elm111);

    elm111 = domQuery.createElement("miscellaneousRemark");
    elm1111 = domQuery.createElement("remarks");
    // "RI" stands for Remark for Itinerary
    DOMutil.addTextElement(elm1111,"type","RM");
    // the category can be used to categorize the type of remark
    // and to group like remarks on the PNR
    // "R" here has no particular significance
    DOMutil.addTextElement(elm1111,"category","R");
    DOMutil.addTextElement(elm1111,"freetext", aRemark.RemarkText);

    elm111.appendChild(elm1111);
    elm11.appendChild(elm111);


    // if this Remark is to be associated with a specific Passenger
    // it will have been populated with the CrsPsgrID (Passenger Tattoo)
    // parsed from the PNR that identifies the passenger
    if (aRemark.CrsPsgrRef instanceof String)
      {
      elm111 = domQuery.createElement("referenceForDataElement");
      elm1111 = domQuery.createElement("reference");
      // "PT"  stands for 'Passenger Tattoo'
      DOMutil.addTextElement(elm1111,"qualifier","PT");
      DOMutil.addTextElement(elm1111,"number",aRemark.CrsPsgrRef);

      elm111.appendChild(elm1111);
      elm11.appendChild(elm111);
      }

    // if this Remark is to be associated with a specific Segment
    // it will have been populated with the CrsSegmentID (Segment Tattoo)
    // parsed from the PNR that identifies the passenger
    if (aRemark.CrsSegmentRef instanceof String)
      {
      elm111 = domQuery.createElement("referenceForDataElement");
      elm1111 = domQuery.createElement("reference");
      // "PT"  stands for 'Passenger Tattoo'
      DOMutil.addTextElement(elm1111,"qualifier","ST");
      DOMutil.addTextElement(elm1111,"number",aRemark.CrsSegmentRef);

      elm111.appendChild(elm1111);
      elm11.appendChild(elm111);
      }

    elm1.appendChild(elm11);
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);
    */

    return(domQuery);

    } // end buildQuery_addGeneralRemark


  /**
   ***********************************************************************
   * This method returns a query to add an Special Service Request
   ***********************************************************************
   */
  private static Document buildQuery_addSsrRemark (PNRSsrRemark ssrRemark)
    {
    /*
    if ( aRemark instanceof PNRSsrRemark )
      {
      final PNRSsrRemark SSRRemark = (PNRSsrRemark )aRemark;
      sCommand.append("SR " + SSRRemark.Code);

      // add the remark text
      if ( GnrcFormat.NotNull(SSRRemark.RemarkText) )
        sCommand.append( " - " + SSRRemark.RemarkText);

      // add name assignment
      if ( SSRRemark.FamilyNumber > 0 )
        sCommand.append(" /P" + SSRRemark.FamilyNumber);
      }
    */

    // if we have nothing to add, return null
    if ( GnrcFormat.IsNull(ssrRemark.RemarkText) &&
         GnrcFormat.IsNull(ssrRemark.Code) )
      return(null);

    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111, elm1111;
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    elm1 = domQuery.createElement("pnrActions");
    // "0" means add Remark / Element and do no processing
    // (as opposed to a save and retrieve, for example)
    DOMutil.addTextElement(elm1,"optionCode","0");
    root.appendChild(elm1);

    // the messageActionDetails node
    elm1 = domQuery.createElement("dataElementsMaster");
    // a dummy field
    elm11 = domQuery.createElement("marker1");
    elm1.appendChild(elm11);

    elm11 = domQuery.createElement("dataElementsIndiv");

    elm111 = domQuery.createElement("elementManagementData");
    elm1111 = domQuery.createElement("reference");
    // "OT" means 'PNR Element Reference provided by the Client'
    DOMutil.addTextElement(elm1111,"qualifier","OT");
    // this is used to create references internal to each message
    // see Amadeus documentation
    DOMutil.addTextElement(elm1111,"number","1");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"segmentName","SSR");
    elm11.appendChild(elm111);

    // the ssr request per se
    elm111 = domQuery.createElement("serviceRequest");
    elm1111 = domQuery.createElement("ssr");

    // if we have either a Service Code or Free Text
    // add the corresponding serviceRequest tag;
    // note that this may cause the request to bomb, but it's up to the client
    // to provide the proper combination of Service Code and Free Text
    if ( GnrcFormat.NotNull(ssrRemark.Code) )
      DOMutil.addTextElement(elm1111,"type", ssrRemark.Code);

    if ( GnrcFormat.NotNull(ssrRemark.Carrier) )
      DOMutil.addTextElement(elm1111,"companyId",ssrRemark.Carrier);

    //DOMutil.addTextElement(elm1111,"indicator","P01");

    if ( GnrcFormat.NotNull(ssrRemark.RemarkText) )
      DOMutil.addTextElement(elm1111,"freetext", ssrRemark.RemarkText);


    elm111.appendChild(elm1111);
    elm11.appendChild(elm111);


    // if this Remark is to be associated with a specific Passenger
    // it will have been populated with the CrsID (Passenger Tattoo)
    // parsed from the PNR that identifies the passenger
    if (ssrRemark.CrsPsgrRef instanceof String)
      {
      elm111 = domQuery.createElement("referenceForDataElement");
      elm1111 = domQuery.createElement("reference");
      // "PT"  stands for 'Passenger Tattoo'
      DOMutil.addTextElement(elm1111,"qualifier","PT");
      DOMutil.addTextElement(elm1111,"number",ssrRemark.CrsPsgrRef);

      elm111.appendChild(elm1111);
      elm11.appendChild(elm111);
      }

    elm1.appendChild(elm11);
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);

    return(domQuery);
    } // end buildQuery_addSSRremark


  /**
   ***********************************************************************
   * This method returns a query to add an Other Service Information Remark
   ***********************************************************************
   */
  private static Document buildQuery_addOsiRemark (PNROsiRemark osiRemark)
    {
    /*
    final PNROsiRemark OSIRemark = (PNROsiRemark )aRemark;
    sCommand.append("OS " + OSIRemark.Carrier + " "  + OSIRemark.RemarkText);

    if ( OSIRemark.FamilyNumber > 0 )
      sCommand.append(" /P" + OSIRemark.FamilyNumber);
    */

    // if we have nothing to add, return null
    if ( GnrcFormat.IsNull(osiRemark.RemarkText) )
      return(null);

    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111, elm1111;
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    elm1 = domQuery.createElement("pnrActions");
    // "0" means add Remark / Element and do no processing
    // (as opposed to a save and retrieve, for example)
    DOMutil.addTextElement(elm1,"optionCode","0");
    root.appendChild(elm1);

    // the messageActionDetails node
    elm1 = domQuery.createElement("dataElementsMaster");
    // a dummy field
    elm11 = domQuery.createElement("marker1");
    elm1.appendChild(elm11);

    elm11 = domQuery.createElement("dataElementsIndiv");

    elm111 = domQuery.createElement("elementManagementData");
    elm1111 = domQuery.createElement("reference");
    // "OT" means 'PNR Element Reference provided by the Client'
    DOMutil.addTextElement(elm1111,"qualifier","OT");
    // this is used to create references internal to each message
    // see Amadeus documentation
    DOMutil.addTextElement(elm1111,"number","1");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"segmentName","OS");
    elm11.appendChild(elm111);

    elm111 = domQuery.createElement("freetextData");
    elm1111 = domQuery.createElement("freetextDetail");
    // "3" stands for 'literal text'
    DOMutil.addTextElement(elm1111,"subjectQualifier","3");
    // "P28" stands for 'Other Service Information'
    DOMutil.addTextElement(elm1111,"type","P28");

    if ( GnrcFormat.NotNull(osiRemark.Carrier) )
      DOMutil.addTextElement(elm1111,"companyId",osiRemark.Carrier);

    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"longFreetext", osiRemark.RemarkText);

    elm11.appendChild(elm111);


    // if this Remark is to be associated with a specific Passenger
    // it will have been populated with the CrsID (Passenger Tattoo)
    // parsed from the PNR that identifies the passenger
    if (osiRemark.CrsPsgrRef instanceof String)
      {
      elm111 = domQuery.createElement("referenceForDataElement");
      elm11.appendChild(elm111);

      elm1111 = domQuery.createElement("reference");
      elm111.appendChild(elm1111);
      // "PT"  stands for 'Passenger Tattoo'
      DOMutil.addTextElement(elm1111,"qualifier","PT");
      DOMutil.addTextElement(elm1111,"number",osiRemark.CrsPsgrRef);

      }

    elm1.appendChild(elm11);
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);

    return(domQuery);

    } // end buildQuery_addOsiRemark


  /**
   ***********************************************************************
   * This method returns a query to add an Itinerary Remark or a
   * Pocket Itinerary Remark
   ***********************************************************************
   */
  private static Document buildQuery_addItinRemark (PNRRemark itinRemark)
    {
    if ( GnrcFormat.IsNull(itinRemark.RemarkText) )
      return(null);

    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111, elm1111;
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    elm1 = domQuery.createElement("pnrActions");
    // "0" means add Remark / Element and do no processing
    // (as opposed to a save and retrieve, for example)
    DOMutil.addTextElement(elm1,"optionCode","0");
    root.appendChild(elm1);

    // the messageActionDetails node
    elm1 = domQuery.createElement("dataElementsMaster");
    // a dummy field
    elm11 = domQuery.createElement("marker1");
    elm1.appendChild(elm11);

    elm11 = domQuery.createElement("dataElementsIndiv");

    elm111 = domQuery.createElement("elementManagementData");
    elm1111 = domQuery.createElement("reference");
    // "OT" means 'PNR Element Reference provided by the Client'
    DOMutil.addTextElement(elm1111,"qualifier","OT");
    // this is used to create references internal to each message
    // see Amadeus documentation
    DOMutil.addTextElement(elm1111,"number","1");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"segmentName","RI");
    elm11.appendChild(elm111);

    elm111 = domQuery.createElement("miscellaneousRemark");
    elm1111 = domQuery.createElement("remarks");
    // "RI" stands for Remark for Itinerary
    DOMutil.addTextElement(elm1111,"type","RI");
    // This tag is used to group remarks in the PNR
    // "R" holds no particular significance
    // see Amadeus documentation
    DOMutil.addTextElement(elm1111,"category","R");
    DOMutil.addTextElement(elm1111,"freetext", itinRemark.RemarkText);

    elm111.appendChild(elm1111);
    elm11.appendChild(elm111);


    // if this Remark is to be associated with a specific Passenger
    // it will have been populated with the CrsPsgrID (Passenger Tattoo)
    // parsed from the PNR that identifies the passenger
    if (itinRemark.CrsPsgrRef instanceof String)
      {
      elm111 = domQuery.createElement("referenceForDataElement");
      elm1111 = domQuery.createElement("reference");
      // "PT"  stands for 'Passenger Tattoo'
      DOMutil.addTextElement(elm1111,"qualifier","PT");
      DOMutil.addTextElement(elm1111,"number",itinRemark.CrsPsgrRef);

      elm111.appendChild(elm1111);
      elm11.appendChild(elm111);
      }

    // if this Remark is to be associated with a specific Segment
    // it will have been populated with the CrsSegmentID (Segment Tattoo)
    // parsed from the PNR that identifies the passenger
    if (itinRemark.CrsSegmentRef instanceof String)
      {
      elm111 = domQuery.createElement("referenceForDataElement");
      elm1111 = domQuery.createElement("reference");
      // "PT"  stands for 'Passenger Tattoo'
      DOMutil.addTextElement(elm1111,"qualifier","ST");
      DOMutil.addTextElement(elm1111,"number",itinRemark.CrsSegmentRef);

      elm111.appendChild(elm1111);
      elm11.appendChild(elm111);
      }

    elm1.appendChild(elm11);
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);

    return(domQuery);

    } // end buildQuery_addItinRemark


  /**
   ***********************************************************************
   * This method returns a query to add an Address Remark to a PNR; as
   * of this writing, jun-26-2001, the Native Ascii provides only an
   * unstructured string of text when adding an address; the Amadeus API,
   * on the other hand, wants to know the different components of the address
   * (street, city, state, country, etc...); as a compromise, we are storing
   * the string passed by the Native Interface in a single tag that is
   * limited to a 50 character length.
   ***********************************************************************
   */
   /*
  private static Document buildQuery_addAddressRemark (PNRRemark addrRemark)
    {
    if ( GnrcFormat.IsNull(addrRemark.RemarkText) )
      return(null);

    // limit the text to 50 characters
    addrRemark.RemarkText = addrRemark.RemarkText.substring(0,50);

    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111, elm1111;
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    elm1 = domQuery.createElement("pnrActions");
    // "0" means add Remark / Element and do no processing
    // (as opposed to a save and retrieve, for example)
    DOMutil.addTextElement(elm1,"optionCode","0");
    root.appendChild(elm1);

    // the messageActionDetails node
    elm1 = domQuery.createElement("dataElementsMaster");
    // a dummy field
    elm11 = domQuery.createElement("marker1");
    elm1.appendChild(elm11);

    elm11 = domQuery.createElement("dataElementsIndiv");

    elm111 = domQuery.createElement("elementManagementData");
    elm1111 = domQuery.createElement("reference");
    // "OT" means 'PNR Element Reference provided by the Client'
    DOMutil.addTextElement(elm1111,"qualifier","OT");
    // this is used to create references internal to each message
    // see Amadeus documentation
    DOMutil.addTextElement(elm1111,"number","1");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"segmentName","AM");
    elm11.appendChild(elm111);

    elm111 = domQuery.createElement("structuredAddress");
    elm1111 = domQuery.createElement("address");
    // "A1" stands for Remark for Itinerary
    DOMutil.addTextElement(elm1111,"optionA1","A1");
    DOMutil.addTextElement(elm1111,"optionTextA1",addrRemark.RemarkText);

    elm111.appendChild(elm1111);
    elm11.appendChild(elm111);
    elm1.appendChild(elm11);
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);

    return(domQuery);

    } // end buildQuery_addAddressRemark
   */

  /**
   ***********************************************************************
   * This method builds a DOM query to be sent to the Amadeus XML server
   * to add a Form Of Payment instruction to a Passenger Name Record (PNR).
   *
   * @param sFOP
   *   the Form of Payment instruction that we wish to add to the PNR
   ***********************************************************************
   */
  private static Document buildQuery_addFormOfPayment(String sType, String sFOP)
      throws TranServerException
    {
    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm11, elm111, elm1111;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");
    elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","FP");

    elm111 = domQuery.createElement("formOfPayment");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("fop");
    elm111.appendChild(elm1111);
    DOMutil.addTextElement(elm1111,"identification",sType);
    if (!sType.equals("CC"))
      {
      DOMutil.addTextElement(elm1111,"freetext", sFOP);
      }
    else
      {
      try
        {
        String ccCode = sFOP.substring(2,4);
        int endCCnum = sFOP.indexOf("/");
        String ccNum = sFOP.substring(4,endCCnum);
        String ccExp = sFOP.substring(endCCnum+1,sFOP.length());

        DOMutil.addTextElement(elm1111,"creditCardCode",ccCode);
        DOMutil.addTextElement(elm1111,"accountNumber", ccNum);
        DOMutil.addTextElement(elm1111,"expiryDate", ccExp);
        }
      catch (Exception e)
        {
        throw new TranServerException(
            " Invalid Credit Card FOP Information: " + sFOP);
        }
      }

    return(domQuery);

    } // end buildQuery_addFormOfPayment

  /**
   ***********************************************************************
    * This method builds a DOM query to be sent to the Amadeus XML server
    * to add a Commission Remark Instructions to a Passenger Name Record (PNR);
    *
    * @param sCommission
    *   The commission amount or percentage
    * @param isPercent
    *   a flag that indicates whether we are dealing with a literal amount or a
    *   percentage
   ***********************************************************************
   */
  public static Document buildQuery_addCommission(final String sCommission,
      boolean isPercent)
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sCommission) )
      return(null);

    DocumentImpl domQuery = new DocumentImpl();
    Element root, elm11, elm111, elm1111;

    // create the root element and pnrActions Nodes
    root = buildNode_PoweredPNR_AddMultiElements(domQuery,"0");

    elm11 = buildNode_dataElementsIndiv(domQuery,"OT","1","FM");

    elm111 = domQuery.createElement("commission");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("commissionInfo");
    elm111.appendChild(elm1111);
    if (isPercent)
      DOMutil.addTextElement(elm1111,"percentage",sCommission);
    else
      DOMutil.addTextElement(elm1111,"amount",sCommission);

    return(domQuery);

   } // end buildQuery_addCommission


  /**
   ***********************************************************************
   * This method returns the DOM query needed to cancel one or many air
   * segments from a Passenger Name Record (PNR).
   ***********************************************************************
   */
  public static Document buildQuery_cancelAirSegments(
      PNR pnr, PNRItinSegment[] aAirSegs)  throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element el1;
    // null: we are not passing a locator, but acting on the one in the AAA
    // "0" stands for no special processing
    // "E" stands for the cryptic "XE"
    el1 = buildNode_PoweredPNR_cancelElements(domQuery,null,"0","E");

    PNRItinAirSegment airseg;
    for ( int i = 0; i < aAirSegs.length; i++ )
      {
      PNRItinAirSegment aAirSeg = (PNRItinAirSegment)aAirSegs[i];
      airseg = pnr.getItinSegmentByFlight(aAirSeg.Carrier,aAirSeg.FlightNumber);
      if ( airseg instanceof PNRItinAirSegment )
        appendNode_element(el1,"ST",airseg.CrsSegmentID);
      else
        throw new TranServerException(
            "The air segment to be cancelled: '" +
            aAirSeg.Carrier + aAirSeg.FlightNumber + "' is not in the PNR");
      }

    return(domQuery);

    } // end buildQuery_cancelAirSegments


  /**
   ***********************************************************************
   * This method returns the DOM query needed to cancel one or many air
   * segments from a Passenger Name Record (PNR); typically, we would use an
   * option code of '0' in this method, which would not EndTransact the PNR;
   * Unfortunately, Amadeus treats SSR remarks differently from other remarks
   * in this respect it does not delete them from the PNR when sending an
   * option code of "0" (it only changes the status to 'XX', and only deletes
   * the remark when EndTransacting the PNR);  For this reason, it is necessary
   * to send an option code of '11' which EndTransacts and Redisplays, and
   * removes the remark in one operation
   ***********************************************************************
   */
  public static Document buildQuery_cancelRemarks( PNRRemark[] aryRemarks)
    throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element el1;

    // Typically we would pass the code "0" for no-processing, and would leave
    // it up to the application or the user to EndTransact at some other time.
    // Nevertheless, Amadeus treats SSR remarks differently, and does not
    // delete them from the PNR when sending an option code of "0" (it only
    // changes the status to 'XX', and only deletes the remark when
    // EndTransacting the PNR).
    // For this reason, it is necessary to send an option code of '11' which
    // EndTransacts and Redisplays, and removes the remark in one operation

    // null: we are not passing a locator, but acting on the one in the AAA
    // "11" stands for no end transaction and redisplay
    // "E" stands for the cryptic "XE"
    el1 = buildNode_PoweredPNR_cancelElements(domQuery,null,"11","E");

    //PNRRemark remark;
    for ( int i = 0; i < aryRemarks.length; i++ )
      {
      //remark = (PNRRemark)aryRemarks[i];
      appendNode_element(el1,"OT",aryRemarks[i].CrsMessageID);
      }
    return(domQuery);

    } // end buildQuery_cancelAirSegments


  /**
   ***********************************************************************
   * This method builds the DOM query needed to issue a Split PNR command
   * through the PoweredPNR interface; we need to pass the parent {@link
   * PNR} to be split so that we can retrieve the appropriate CrsID for each
   * passenger
   *
   * @param names
   *   an array of the passengers to be split from the PNR
   *
   * @param pnr
   *   the parent PNR from which the passengers will be split; this is needed
   *   in order to extract the internal ID assigned to the passengers by
   *   Amadeus
   ***********************************************************************
   */
  public static Document buildQuery_PoweredPNR_Split(
      PNRNameElement[] names, PNR pnr) throws Exception
    {
    return buildQuery_PoweredPNR_Split(0,names,pnr);
    } // end buildQuery_PoweredPNR_Split


  /**
   ***********************************************************************
   * This method builds the DOM query needed to issue a Split PNR command
   * through the PoweredPNR interface; we need to pass the parent {@link
   * PNR} to be split so that we can retrieve the appropriate CrsID for each
   * passenger
   *
   * @param iNumUnassigned
   *   the number of unassigned seats to split from the PNR; if this number is
   *   less than 0, this parameter is ignored
   *
   * @param names
   *   an array of the passengers to be split from the PNR
   *
   * @param pnr
   *   the parent PNR from which the passengers will be split; this is needed
   *   in order to extract the internal ID assigned to the passengers by
   *   Amadeus
   ***********************************************************************
   */
  public static Document buildQuery_PoweredPNR_Split(
      int iNumUnassigned, PNRNameElement[] names, PNR pnr) throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element root, el1, el11;

    root = domQuery.createElement("PoweredPNR_Split");
    domQuery.appendChild(root);

    el1 = domQuery.createElement("splitDetails");
    root.appendChild(el1);

    el11 = domQuery.createElement("passenger");
    el1.appendChild(el11);
    // "PT": Passenger Tattoo
    DOMutil.addTextElement(el11,"type","PT");

    if (iNumUnassigned > 0)
      DOMutil.addTextElement(el11,"quantity",iNumUnassigned);

    if (names instanceof PNRNameElement[])
      {
      for (int i=0; i < names.length; i++)
        {
        // retrieve the passenger by it's PassengerID from the PNR
        // and read the CrsPsgrID
        PNRNameElement psgr = pnr.getName(names[i].getPassengerID());
        String tattoo = "";

        if (psgr instanceof PNRNameElement)
          tattoo = psgr.CrsPsgrID;

        if (tattoo instanceof String && tattoo.length() > 0)
          DOMutil.addTextElement(el11,"tattoo",tattoo);
        else
          throw new TranServerException(
              " failed to find passenger: " + names[i].getPassengerID() +
              " in Passenger Name Record: " + pnr.getLocator() );
        }
      }

    return(domQuery);

    } // end buildQuery_PoweredPNR_Split

  /**
   ***********************************************************************
   * This method builds the root element and <code>pnrActions</code> Node of a
   * <code>PoweredPNR_AddMultiElements</code> document, which are shared
   * by all requests to add elements to a PNR; note that this method assumes
   * that the document passed is empty and does not already contain a root
   * element; it simply adds a new root element and a pnrActions node and
   * does not check to see whether one exists already; it returns the
   * root element.
   *
   * @param domQuery
   *   an empty DOM document to be used to create a query to the Amadeus API
   *
   * @param sActionCode
   *   a parameter to the <code>optionCode</code> tag within the
   *   <code>pnrActions</code> tag which specifies the nature of the
   *   action to be performed by the query.  The most common codes
   *   used by this application include:
   *   <ul>
   *     <li><code> 0</code>:  add Element/Remark and do no processing</li>
   *     <li><code>11</code>:  End Transaction and retrieve</li>
   *     <li><code>20</code>:  Ignore (no retrieve)</li>
   *   </ul>
   * See Amadeus API documentation for more detail on this.
   ***********************************************************************
   */
  private static Element buildNode_PoweredPNR_AddMultiElements(Document domQuery,
                                                             String sActionCode)
    {
    Element root = domQuery.getDocumentElement();

    if ( root instanceof Element &&
         root.getTagName().equals("PoweredPNR_AddMultiElements") )
      return(root);
    else
      {
      // create a document object and our top level Element
      root = domQuery.createElement("PoweredPNR_AddMultiElements");
      Element elm1 = domQuery.createElement("pnrActions");
      DOMutil.addTextElement(elm1,"optionCode","0");

      root.appendChild(elm1);
      domQuery.appendChild(root);

      return(root);
      }

    } // end buildNode_PoweredPNR_AddMultiElements

  /**
   ***********************************************************************
   * creates base DOM document for cancelling elements by using a
   * PoweredPNR_Cancel query; it returns the <code>cancelElements</code> to
   * which can be appended individual <code>element</code> nodes representing
   * the segments to be deleted.
   ***********************************************************************
   */
  private static Element buildNode_PoweredPNR_cancelElements(
      Document domQuery, String sLocator, String sActionCode, String sEntryType)
    {
    Element root, el1, el11;
    root = domQuery.getDocumentElement();

    if (!(root instanceof Element))
      {
      // create a document object and our top level Element
      root = domQuery.createElement("PoweredPNR_Cancel");
      domQuery.appendChild(root);

      // providing a PNR locator is optional, so only add this node if one was
      // passed
      if (sLocator instanceof String)
        {
        el1 = domQuery.createElement("reservationInfo");
        root.appendChild(el1);
        el11 = domQuery.createElement("reservation");
        el1.appendChild(el11);
        DOMutil.addTextElement(el11,"controlNumber",sLocator);
        }

      el1 = domQuery.createElement("pnrActions");
      root.appendChild(el1);
      DOMutil.addTextElement(el1,"optionCode",sActionCode);
      }

    el1 = domQuery.createElement("cancelElements");
    root.appendChild(el1);
    DOMutil.addTextElement(el1,"entryType",sEntryType);

    return(el1);

    } // end buildNode_PoweredPNR_AddMultiElements


  /**
   ***********************************************************************
   * This method builds an <code>originDestinationDetails</code> tag
   * which contains air segment information.
   ***********************************************************************
   */
  private static Element buildNode_originDestinationDetails(Document domQuery,
                                                            String   sDepCity,
                                                            String   sArrCity)

    {
    // return the root of the document, or create a new one if necessary
    Element root = buildNode_PoweredPNR_AddMultiElements(domQuery, "0");

    // We need to create an originDestinationDetails Tag for each segment.
    Element elm1 = domQuery.createElement("originDestinationDetails");
    root.appendChild(elm1);

    Element elm11 = domQuery.createElement("originDestination");
    elm1.appendChild(elm11);
    DOMutil.addTextElement(elm11,"origin"     ,sDepCity);
    DOMutil.addTextElement(elm11,"destination",sArrCity);

    return elm1;

    } // end buildNode_originDestinationDetails


  /**
   ***********************************************************************
   * This method adds and returns an individual <code>dataElementsIndiv</code>
   * node to the <code>dataElementsMaster</code> section of a
   * <code>PoweredPNR_AddMultiElements</code> query; this type of node is
   * used to add all types of Remarks to a PNR, and to add elements such as
   * Form Of Payment, Commission, etc.
   * <p>
   * This method also adds the <code>elementManagementData</code> tag that is
   * used to identify this <code>dataElementsIndiv</code> within the PNR.
   * If necessary, it also creates the necessary <code>dataElementsMaster</code>
   * containing structure. Hence, this method can be called  multiple times
   * to add many <code>dataElementsIndiv</code> nodes, within a single
   * <code>dataElementsMaster</code> structure.
   *
   * @param domQuery
   *   the DOM document that will contain the query, and that must contain a
   *   <code>PoweredPNR_AddMultiAddElements</code> root.
   *
   * @param sQualifier
   *   the 'qualifier' that identifies which type of reference is being
   *   made by the reference number.  See Amadeus API documentation.
   *
   * @param sRefNumber
   *   a reference number which identifies this node within the PNR; the
   *   type of reference varies according to the value of the sQualifier.
   *   See Amadeus API documentation.
   *
   * @param sSegmentName
   *   the type of element (for example, 'RM' for remark, 'FP' for Form
   *   of Payment, etc...)
   *
   * @return
   *   the <code>dataElementsIndiv</code> just created and added to the document,
   *   to which can be added the custom elements specific to each type of request
   ***********************************************************************
   */
  private static Element buildNode_travellerInfo(Document domQuery,
                                                 String sQualifier,
                                                 String sRefNumber,
                                                 String sSegmentName)
    {
    Element root, elm1, elm11, elm111, elm1111;

    // return the root of the document, or create a new one if necessary
    root = buildNode_PoweredPNR_AddMultiElements(domQuery, "0");

    elm1 = domQuery.createElement("travellerInfo");
    root.appendChild(elm1);

    elm11 = domQuery.createElement("elementManagementPassenger");
    elm1.appendChild(elm11);

    elm111 = domQuery.createElement("reference");
    elm11.appendChild(elm111);
    DOMutil.addTextElement(elm111,"qualifier",sQualifier);
    DOMutil.addTextElement(elm111,"number","1");

    DOMutil.addTextElement(elm11,"segmentName",sSegmentName);

    return(elm1);

    } // end buildNode_travellerInfo


  /**
   ***********************************************************************
   * This method adds and returns an individual <code>dataElementsIndiv</code>
   * node to the <code>dataElementsMaster</code> section of a
   * <code>PoweredPNR_AddMultiElements</code> query; this type of node is
   * used to add all types of Remarks to a PNR, and to add elements such as
   * Form Of Payment, Commission, etc.
   * <p>
   * This method also adds the <code>elementManagementData</code> tag that is
   * used to identify this <code>dataElementsIndiv</code> within the PNR.
   * If necessary, it also creates the necessary <code>dataElementsMaster</code>
   * containing structure. Hence, this method can be called  multiple times
   * to add many <code>dataElementsIndiv</code> nodes, within a single
   * <code>dataElementsMaster</code> structure.
   *
   * @param domQuery
   *   the DOM document that will contain the query, and that must contain a
   *   <code>PoweredPNR_AddMultiAddElements</code> root.
   *
   * @param sQualifier
   *   the 'qualifier' that identifies which type of reference is being
   *   made by the reference number.  See Amadeus API documentation.
   *
   * @param sRefNumber
   *   a reference number which identifies this node within the PNR; the
   *   type of reference varies according to the value of the sQualifier.
   *   See Amadeus API documentation.
   *
   * @param sSegmentName
   *   the type of element (for example, 'RM' for remark, 'FP' for Form
   *   of Payment, etc...)
   *
   * @return
   *   the <code>dataElementsIndiv</code> just created and added to the document,
   *   to which can be added the custom elements specific to each type of request
   ***********************************************************************
   */
  protected static Element buildNode_dataElementsIndiv(Document domQuery,
                                                     String sQualifier,
                                                     String sRefNumber,
                                                     String sSegmentName)
    {
    Element root, elm1, elm11, elm111, elm1111;

    elm1 = (Element)domQuery.getElementsByTagName("dataElementsMaster").item(0);

    // if this node does not exist yet, create it
    if ( !(elm1 instanceof Element) )
      {
      root = domQuery.getDocumentElement();
      elm1 = domQuery.createElement("dataElementsMaster");
      root.appendChild(elm1);

      // a necessary dummy field
      elm11 = domQuery.createElement("marker1");
      elm1.appendChild(elm11);
      }

    elm11 = domQuery.createElement("dataElementsIndiv");
    elm1.appendChild(elm11);

    elm111 = domQuery.createElement("elementManagementData");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("reference");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm1111,"qualifier",sQualifier);
    // this is used to create references internal to each message
    // see Amadeus documentation
    DOMutil.addTextElement(elm1111,"number",sRefNumber);

    DOMutil.addTextElement(elm111,"segmentName",sSegmentName);

    return(elm11);

    } // end buildNode_dataElementsIndiv


  /**
   ***********************************************************************
   * This method appends a <code>travellerInformation</code> node to a
   * containing <code>travellerInfo</code> node, and is used to add
   * a Group Header to a Passenger Name Record (PNR).
   ***********************************************************************
   */
  private static void appendNode_travellerInformation(Document domQuery,
                                                      Element elm1,
                                                      String sGroupName,
                                                      String sQualifier,
                                                      int iNumSeats)
    {
    Element elm11, elm111;

    elm11 = domQuery.createElement("travellerInformation");
    elm1.appendChild(elm11);

    // the traveller element loosely corresponds to a 'family' in Airware
    elm111 = domQuery.createElement("traveller");
    elm11.appendChild(elm111);
    DOMutil.addTextElement(elm111,"surname",sGroupName);
    DOMutil.addTextElement(elm111,"qualifier",sQualifier);
    DOMutil.addTextElement(elm111,"quantity",iNumSeats);

    } // end appendNode_travellerInformation


  /**
   ***********************************************************************
   * This method appends a <code>itineraryInfo</code> node for an air segment
   * to a containing <code>originDestinationDetails</code> node.
   ***********************************************************************
   */
  private static void appendNode_itineraryInfo(Document domQuery, Element elm1,
      PNRItinAirSegment aAirSeg, String sQualifier, String sRefNum)
    {
    Element elm11, elm111, elm1111, elm11111;

    // the itineraryInfo: the big one
    elm11 = domQuery.createElement("itineraryInfo");
    elm1.appendChild(elm11);

    elm111 = domQuery.createElement("elementManagementItinerary");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("reference");
    // "SR" means ?
    DOMutil.addTextElement(elm1111,"qualifier",sQualifier);
    // the following number is used for internal references within the message
    // see Amadeus documentation for more on this
    DOMutil.addTextElement(elm1111,"number",sRefNum);
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"segmentName","AIR");

    elm111 = domQuery.createElement("airAuxItinerary");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("travelProduct");
    elm111.appendChild(elm1111);

    elm11111 = domQuery.createElement("product");
    elm1111.appendChild(elm11111);

    String sDepDate = "";
    sDepDate = DateTime.fmtLongDateTime(aAirSeg.DepartureDateTime, "ddMMyy");
    DOMutil.addTextElement(elm11111,"depDate",sDepDate);

    // for non-scheduled flights, we must specify all the date/time info
    // for scheduled flights it is safest to only specify the date
    if (aAirSeg.isScheduled == false)
      {
      String sDepTime = "";
      String sArrDate = "";
      String sArrTime = "";
      sDepTime = DateTime.fmtLongDateTime(aAirSeg.DepartureDateTime, "HHmm");
      sArrDate = DateTime.fmtLongDateTime(aAirSeg.ArrivalDateTime,   "ddMMyy");
      sArrTime = DateTime.fmtLongDateTime(aAirSeg.ArrivalDateTime,   "HHmm");
      DOMutil.addTextElement(elm11111,"depTime",sDepTime);
      DOMutil.addTextElement(elm11111,"arrDate",sArrDate);
      DOMutil.addTextElement(elm11111,"arrTime",sArrTime);
      }

    elm11111 = domQuery.createElement("boardpointDetail");
    elm1111.appendChild(elm11111);
    DOMutil.addTextElement(elm11111,"cityCode",aAirSeg.DepartureCityCode);

    elm11111 = domQuery.createElement("offpointDetail");
    elm1111.appendChild(elm11111);
    DOMutil.addTextElement(elm11111,"cityCode",aAirSeg.ArrivalCityCode);

    elm11111 = domQuery.createElement("company");
    elm1111.appendChild(elm11111);
    DOMutil.addTextElement(elm11111,"identification",aAirSeg.Carrier);

    elm11111 = domQuery.createElement("productDetails");
    elm1111.appendChild(elm11111);
    DOMutil.addTextElement(elm11111,"identification",aAirSeg.FlightNumber);
    DOMutil.addTextElement(elm11111,"classOfService",aAirSeg.InventoryClass);

    elm111 = domQuery.createElement("messageAction");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("business");
    elm111.appendChild(elm1111);
    DOMutil.addTextElement(elm1111,"function","1");

    elm111 = domQuery.createElement("relatedProduct");
    elm11.appendChild(elm111);
    DOMutil.addTextElement(
        domQuery,elm111,"quantity",String.valueOf(aAirSeg.NumberOfSeats) );

    /*
    // Since Airware adds a group header to all PNRs,
    // the AmadeusAPI requires that we enter a 'SG' action code
    // (Sell from Group)
    String sActionCode;
    if (aAirSeg.ActionCode.equals("NN"))
      sActionCode = "SG";
    else
      sActionCode = aAirSeg.ActionCode;
    DOMutil.addTextElement(elm111,"status",sActionCode);
    */

    DOMutil.addTextElement(elm111,"status",aAirSeg.ActionCode);

    elm111 = domQuery.createElement("selectionDetailsAir");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("selection");
    elm111.appendChild(elm1111);
    // Booking Code for Air Segment: P10 Basic Booking
    // (no other choice given in documentation)
    DOMutil.addTextElement(elm1111,"option","P10");

    // determine whether we are dealing with a passive sell
    //  final String[] PASSIVE_STATUS_CODES = {"GK","GL","PK","PL"};
    //  if ( GnrcParser.containedWithin(aAirSeg.ActionCode,PASSIVE_STATUS_CODES) )

    if (aAirSeg.RemoteLocator instanceof String)
      {
      //DOMutil.addTextElement(elm1111,"status",aAirSeg.ActionCode);
      if ( GnrcFormat.NotNull(aAirSeg.RemoteLocator) )
        {
        elm111 = domQuery.createElement("reservationInfoSell");
        elm11.appendChild(elm111);
        elm1111 = domQuery.createElement("reservation");
        elm111.appendChild(elm1111);
        DOMutil.addTextElement(
            domQuery,elm1111,"controlNumber",aAirSeg.RemoteLocator);
        }
      }

    } // end appendNode_itineraryInfo (for Air Segments)


  /**
   ***********************************************************************
   * This method appends a <code>itineraryInfo</code> node for an Arunk segment
   * to a containing <code>originDestinationDetails</code> node.
   ***********************************************************************
   */
  private static void appendNode_itineraryInfo(Document domQuery, Element elm1,
      PNRItinArunkSegment aAirSeg, String sQualifier, String sRefNum)
    {
    Element elm11, elm111, elm1111, elm11111;

    // the itineraryInfo: the big one
    elm11 = domQuery.createElement("itineraryInfo");
    elm1.appendChild(elm11);

    elm111 = domQuery.createElement("elementManagementItinerary");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("reference");
    // "SR" means ?
    DOMutil.addTextElement(elm1111,"qualifier",sQualifier);
    // the following number is used for internal references within the message
    // see Amadeus documentation for more on this
    DOMutil.addTextElement(elm1111,"number",sRefNum);
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm111,"segmentName","AIR");

    elm111 = domQuery.createElement("airAuxItinerary");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("travelProduct");
    elm111.appendChild(elm1111);

    elm11111 = domQuery.createElement("productDetails");
    elm1111.appendChild(elm11111);
    DOMutil.addTextElement(elm11111,"identification","ARNK");

    elm111 = domQuery.createElement("messageAction");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("business");
    elm111.appendChild(elm1111);
    DOMutil.addTextElement(elm1111,"function","1");
    } // end appendNode_itineraryInfo (for Arunk segments)


  /**
   ***********************************************************************
   * This method appends a <code>freetextData</code> node to a
   * containing <code>dataElementsIndiv</code> node.
   ***********************************************************************
   */
  protected static void appendNode_freetextData(
      Element elm11, String subjectQualifier, String type, String carrier, String longFreetext)
    {
    Document domQuery = elm11.getOwnerDocument();
    Element elm111, elm1111;

    elm111 = domQuery.createElement("freetextData");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("freetextDetail");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm1111,"subjectQualifier",subjectQualifier);
    DOMutil.addTextElement(elm1111,"type",type);

    if (GnrcFormat.NotNull(carrier))
      DOMutil.addTextElement(elm1111,"companyId",carrier);

    // note that this is being added to elm111 rather than elm1111
    DOMutil.addTextElement(elm111,"longFreetext",longFreetext);

    } // end appendNode_freetextData


  /**
   ***********************************************************************
   * Appends the node needed to add Special Service Requests (SSR) remarks
   ***********************************************************************
   */
  private static void appendNode_serviceRequest(
      Element elm11, String sCode, String sCarrier, String sText )
    {
    Document domQuery = elm11.getOwnerDocument();
    Element elm111, elm1111;

    elm111  = domQuery.createElement("serviceRequest");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("ssr");
    elm111.appendChild(elm1111);

    // if we have either a Service Code or Free Text
    // add the corresponding serviceRequest tag;
    // note that this may cause the request to bomb, but it's up to the client
    // to provide the proper combination of Service Code and Free Text
    if ( GnrcFormat.NotNull(sCode) )
      DOMutil.addTextElement(elm1111,"type", sCode);

    if ( GnrcFormat.NotNull(sCarrier) )
      DOMutil.addTextElement(elm1111,"companyId",sCarrier);

    if ( GnrcFormat.NotNull(sText) )
      DOMutil.addTextElement(elm1111,"freetext", sText);

    } // end appendNode_serviceRequest


  /**
   ***********************************************************************
   * This appends a <code>miscellaneousRemark</code> node to a
   * <code>dataElementsIndiv</code>.
   ***********************************************************************
   */
  private static void appendNode_miscellaneousRemark(
      Element elm11, String type, String category, String freetext)
    {
    Document domQuery = elm11.getOwnerDocument();
    Element elm111, elm1111;

    elm111 = domQuery.createElement("miscellaneousRemark");
    elm11.appendChild(elm111);

    elm1111 = domQuery.createElement("remarks");
    elm111.appendChild(elm1111);

    DOMutil.addTextElement(elm1111,"type",type);
    if (category != null)
      DOMutil.addTextElement(elm1111,"category",category);
    DOMutil.addTextElement(elm1111,"freetext",freetext);

    } // end appendNode_miscellaneousRemark


  /**
   ***********************************************************************
   * This method appends a node <code>dataElementsIndiv</code> that is used
   * to associate a Remark or Passenger Number Record (PNR) to a Segment
   * or Passenger.
   *
   * @param domQuery
   *   the DOM document that will contain the query
   *
   * @param elm11
   *   the <code>dataElementsIndiv</code> element that must be associated
   *   to another element on the PNR
   *
   * @param qualifier
   *   the 'qualifier' that identifies the type of reference that is being
   *   made by the reference number.  See Amadeus API documentation.
   *
   * @param number
   *   a Segment or Passenger reference which identifies the other element to which
   *   the Remark or PNR element must be associated; the type of reference
   *   varies according to the value of the sQualifier. See Amadeus API documentation.
   ***********************************************************************
   */
  private static void appendNode_referenceForDataElement(
      Element elm11, String qualifier, String number)
    {
    Document domQuery = elm11.getOwnerDocument();
    Element elm111, elm1111;

    elm111 = domQuery.createElement("referenceForDataElement");
    elm1111 = domQuery.createElement("reference");

    DOMutil.addTextElement(elm1111,"qualifier",qualifier);
    DOMutil.addTextElement(elm1111,"number",number);

    elm111.appendChild(elm1111);
    elm11.appendChild(elm111);

    } // end appendNode_referenceForDataElement


  /**
   ***********************************************************************
   * This method is used to append and individual <code>element</code> node to
   * to <code>cancelElements</code> section of a <code>PoweredPNR_Cancel</code>
   * query.
   ***********************************************************************
   */
  private static void appendNode_element(
      Element el1, String identifier, String number)
    {
    Document domQuery = el1.getOwnerDocument();
    Element el11;
    el11 = domQuery.createElement("element");
    el1.appendChild(el11);
    DOMutil.addTextElement(el11,"identifier",identifier);
    DOMutil.addTextElement(el11,"number",number);
    } // end appendNode_element

  /**
   ***********************************************************************
   * This method determines whether the air segment is a Block Space Group
   * (BSG) segment.
   *
   * <p>BSG is a Sabre concept. Its Amadeus equivalent is
   * Negospace.  These concepts denote blocks of inventory that are sold
   * in bulk to a Cruise Line or other large Travel Agents. These flights have
   * already been sold and reserved for the holder of the inventory.</p>
   *
   * <p>When building a Passenger Name Record (PNR) with these flights,
   * this reserved inventory is sold 'passively'. This means that
   * the flight is added to the PNR, but is not reserved with the
   * airline, since, presumably, the seat has already been reserved and would
   * otherwise have been sold twice.
   *
   * <p>It is then up to the holder of the inventory to reconcile in their
   * internal systems the segments sold passively against those seats held
   * in inventory.</p>
   ***********************************************************************
   */
  private boolean isPassiveSegment(final PNRItinAirSegment aAirSeg)
    {
    final String[] PASSIVE_STATUS_CODES = {"GK","GL","PK","PL"};

    // figure out which sell command to run
    if ( GnrcParser.containedWithin(aAirSeg.ActionCode,PASSIVE_STATUS_CODES) )
      return(false);
    else if ( GnrcFormat.NotNull(aAirSeg.RemoteLocator) )
      return(true);
    else
      return(false);

    } // end isPassiveSegment


  /**
   ***********************************************************************
   * Updates any segments that match the given flight criteria with
   * the status code
   ***********************************************************************
   */
  private void updateSegmentStatus(final PNRItinSegment[] aSegList,
                                   final String aLocator,
                                   final String aStatusCode)
    {

    // set the status codes for any air segments that match the given criteria
    if ( (aSegList instanceof PNRItinSegment[]) && (aLocator instanceof String) )
      {
      PNRItinAirSegment airseg = null;
      for ( int i = 0; i < aSegList.length; i++ )
       {
       if ( aSegList[i] instanceof PNRItinAirSegment )
         {
         airseg = (PNRItinAirSegment )aSegList[i];
         if ( aLocator.equals(airseg.RemoteLocator) )
           airseg.Status = aStatusCode;
         }
       } // end for
      }

    } // updateSegmentStatus


  private static Document buildQuery_displayBlockAvail(String sRemoteLocator)
    throws TranServerException
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sRemoteLocator) )
      throw new TranServerException(
        "Must specify a Remote Locator to display block availability");

    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111;
    Element root = domQuery.createElement("PoweredAir_MultiAvailability");

    // the messageActionDetails node
    elm1 = domQuery.createElement("messageActionDetails");
    root.appendChild(elm1);
    elm11 = domQuery.createElement("functionDetails");
    elm1.appendChild(elm11);

    DOMutil.addTextElement(elm11,"actionCode","44");

    // the requestSection node
    elm1 = domQuery.createElement("requestSection");
    root.appendChild(elm1);

    // availabilityProductInfo
    elm11 = domQuery.createElement("availabilityProductInfo");
    elm1.appendChild(elm11);

    // availabilityOptions
    elm11 = domQuery.createElement("negoSpaceDetails");
    elm1.appendChild(elm11);
    DOMutil.addTextElement(elm11,"recordLocator",sRemoteLocator);

    // availabilityOptions
    elm11 = domQuery.createElement("availabilityOptions");
    elm1.appendChild(elm11);

    elm111 = domQuery.createElement("productTypeDetails");
    elm11.appendChild(elm111);
    DOMutil.addTextElement(elm111,"typeOfRequest","TT");

    // add the root to the top level document
    domQuery.appendChild(root);

    return(domQuery);

    } // end buildQuery_displayBlockAvail


  /**
   ***********************************************************************
   * Air_SellFromAvailability_Query deprecated to PoweredAir_SellFromAvailability
   *  build new PoweredAir_SellFromAvailability for nego segment
   ***********************************************************************
   */
  private static Document buildQuery_sellFromBlockAvail(PNRItinAirSegment seg)
    throws TranServerException
    {
    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111, elm1111;
    Element root = domQuery.createElement("PoweredAir_SellFromAvailability");
    domQuery.appendChild(root);

    // the itineraryDetails node
    elm1 = domQuery.createElement("itineraryDetails");
    root.appendChild(elm1);

    // the originDestinationDetails node
    elm11 = domQuery.createElement("originDestinationDetails");
    elm1.appendChild(elm11);

    // add origin and destination
    DOMutil.addTextElement(elm11,"origin",seg.DepartureCityCode);
    DOMutil.addTextElement(elm11,"destination",seg.ArrivalCityCode);

    // the segmentInformation node
    elm11 = domQuery.createElement("segmentInformation");
    elm1.appendChild(elm11);

    // the travelProductInformation node
    elm111 = domQuery.createElement("travelProductInformation");
    elm11.appendChild(elm111);

    // the flightDate node
    elm1111 = domQuery.createElement("flightDate");
    elm111.appendChild(elm1111);

    // add depart date
    String sDepDate = "";
    sDepDate = DateTime.fmtLongDateTime(seg.DepartureDateTime, "ddMMyy");
    DOMutil.addTextElement(elm1111,"departureDate",sDepDate);

    // the boardPointDetails node
    elm1111 = domQuery.createElement("boardPointDetails");
    elm111.appendChild(elm1111);

    // add depart city - trueLocationId
    DOMutil.addTextElement(elm1111,"trueLocationId",seg.DepartureCityCode);

    // the offpointDetails node
    elm1111 = domQuery.createElement("offpointDetails");
    elm111.appendChild(elm1111);

    // add arrive city - trueLocationId
    DOMutil.addTextElement(elm1111,"trueLocationId",seg.ArrivalCityCode);

    // the companyDetails node
    elm1111 = domQuery.createElement("companyDetails");
    elm111.appendChild(elm1111);

    // add marketingCompany city
    DOMutil.addTextElement(elm1111,"marketingCompany",seg.Carrier);

    // the flightIdentification node
    elm1111 = domQuery.createElement("flightIdentification");
    elm111.appendChild(elm1111);

    // add flightNumber
    DOMutil.addTextElement(elm1111,"flightNumber",seg.FlightNumber);

    // add bookingClass
    DOMutil.addTextElement(elm1111,"bookingClass",seg.InventoryClass);
    elm111.appendChild(elm1111);

    // add itemNumber
    DOMutil.addTextElement(elm111,"itemNumber","1");

    // the relatedproductInformation node
    elm111 = domQuery.createElement("relatedproductInformation");
    elm11.appendChild(elm111);

    // add number of seats
    DOMutil.addTextElement(elm111,"quantity",seg.NumberOfSeats);

    // add statusCode
    if ( GnrcFormat.NotNull(seg.ActionCode) )
      DOMutil.addTextElement(elm111,"statusCode",seg.ActionCode);

    /*
    // we always sell from the first line because the locator retrieves
    // only one Block
    DOMutil.addTextElement(root,"AvailabilityLine","1");
    DOMutil.addTextElement(root,"NumOfSeats",seg.NumberOfSeats);
    DOMutil.addTextElement(root,"Classes",   seg.InventoryClass);
    if ( GnrcFormat.NotNull(seg.ActionCode) )
      DOMutil.addTextElement(root,"Status",  seg.ActionCode); */
    return(domQuery);

    } // end buildQuery_sellFromBlockAvail


  /**
   ***********************************************************************
   * Matches a remark passed by Airware to a Remark found in a Passenger Name
   * Record (PNR) returned by Amadeus
   *
   * @param needle the remark to be matched
   * @param haystack a remark returned by Amadeus
   *
   * @see AmadeusBuildPNRConversation#isSelectedRemark
   ***********************************************************************
   */
  protected static boolean isSelectedRemark(
      final PNRRemark needle, final PNRRemark haystack)
    {
    return AmadeusBuildPNRConversation.isSelectedRemark(needle,haystack);
    } // end isSelectedRemark


  /**
   ***********************************************************************
   * Matches an array of remarks passed by Airware to a Remark found in a
   * Passenger Name Record (PNR) returned by Amadeus
   *
   * @param needle the remark to be matched
   * @param haystack a remark returned by Amadeus
   *
   * @see AmadeusBuildPNRConversation#isSelectedRemark
   ***********************************************************************
   */
  protected static PNRRemark[] getMatchingRemarks(
      PNRRemark[] needle, PNRRemark[] haystack)
    {
    return AmadeusBuildPNRConversation.getMatchingRemarks(needle,haystack);
    } // end getMatchingRemarks


  /**
   ***********************************************************************
   * This method checks that the reply is not a 'MessagesOnly_Reply'; if so,
   * it throws a {@link GdsResponseException} containing the error message;
   * if the boolean retryOnUnableToDisplay is true, and the error message
   * encountered is a 'UNABLE TO DISPLAY', the method attempts to redisplay
   * the PNR once to determine whether this was only a temporary problem.
   *
   * @throws GdsResponseException if the reply contains an error
   *
   * @param domReply
   *  the Amadeus reply to be validated
   *
   * @param sErrMsg
   *   an error message prefix to add to prepend to the error message returned
   *   by Amadeus, if any
   *
   * @param retryOnUnableToDisplay
   *   when true, this methods attempts to redisplay the PNR when encountering
   *   an 'UNABLE TO DISPLAY' error message
   ***********************************************************************
   */
  private static Document validateReply(
      Document domReply, AmadeusAPICrs aCrs, String sErrMsg, boolean retryOnUnableToDisplay)
    throws GdsResponseException
    {
    Element root     = null;
    String  sErr     = null;
    boolean firstTry = true;

    while(true)
      {
      root = domReply.getDocumentElement();
      // if we did not get a proper reply, issue an error
      if ( root.getTagName().equals("MessagesOnly_Reply") )
        {
        sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
        if (sErr.indexOf("UNABLE TO DISPLAY") >= 0  && firstTry)
          {
          Document domQuery = AmadeusAPIGetPNRConversation.buildQuery_redisplayPNR();
          domReply = aCrs.connection.sendAndReceive(domQuery);
          firstTry = false;
          continue;
          }
        else
          throw new GdsResponseException(" " + sErrMsg + ": " + sErr);
        }
      else if ( !(root.getTagName().equals("PoweredPNR_PNRReply")) )
        throw new GdsResponseException(" " + sErrMsg + ": unrecognized Amadeus response");
      else
        break;
      }

    return domReply;
    } // end validateReply

} // end class AmadeusAPIBuildPNRConversation