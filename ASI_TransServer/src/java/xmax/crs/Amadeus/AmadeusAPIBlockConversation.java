package xmax.crs.Amadeus;

import xmax.TranServer.TranServerException;
import xmax.TranServer.GnrcConvControl;

import xmax.crs.GdsResponseException;
import xmax.crs.Block;
import xmax.crs.BlockFlight;
import xmax.crs.BlockMessage;
import xmax.crs.Flifo.FlightInfo;
import xmax.crs.Flifo.FlightSegment;
import xmax.crs.GnrcCrs;
import xmax.crs.Amadeus.negospace.*;

import xmax.util.DateTime;
import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;
import xmax.util.MatchError;
import xmax.util.xml.DOMutil;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

//import com.oroinc.text.regex.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.List;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.*;


/**
 ***********************************************************************
 * This class performs the different conversations needed to build, retrieve,
 * change, and cancel a NegoSpace inventory Block on the Amadeus Computer
 * Reservation System, through its XML API.
 *
 * @author   Philippe Paravicini
 * @version  $Revision: 20$ - $Date: 2/13/2003 7:03:43 PM$
 *
 * @see Block
 * @see BlockInfo
 ***********************************************************************
 */
public class AmadeusAPIBlockConversation
{

  /** the Amadeus Date format: ddMMyy */
  private static final String AMADEUS_DATE_FMT = "ddMMyy";

  /** Text formatter for date fields in queries to Amadeus */
  private static final SimpleDateFormat fmtAmadeusDate =
    new SimpleDateFormat(AMADEUS_DATE_FMT);

  /**
   * TourName is a required field when creating a NegoSpace block, which we are
   * not using; hence we are providing it as 15 dashes which get converted into
   * blanks in the NegoSpace system
   */
  public static final String TOUR_NAME_PADDING = "---------------";

  /** The text returned upon successfully creating a NegoSpace block */
  private static final String MSG_CREATE_OK = "OK - NEW NEGOTIATED SPACE CREATED";

  /** The text returned upon successfully deleting a NegoSpace block */
  private static final String MSG_DELETE_OK = "OK - NEGOTIATED SPACE DELETED";

  /** The text returned upon successfully modifying a NegoSpace block */
  private static final String MSG_MODIFY_OK = "OK - NEGOTIATED SPACE UPDATED";

  /** The generic airline to use when retrieving a block by locator */
  private static final String ANY_AIRLINE   = "ZZ";

  /** The character that identifies a block as active */
  private static final String STATUS_ACTIVE = "A";

  /** The character that identifies a block as pending creation */
  private static final String STATUS_PENDING_CREATE = "C";

  /**
   ***********************************************************************
   * <p>Create a new block with the given parameters; Amadeus does not immediately
   * return a block locator; for this reason, this method first creates the
   * block and then issues a retrieve query using the same
   * flight/depDate/class/TourRef information in order to display the block and
   * retrieve the locator; see below with the perils involved in this
   * operation.</p>
   * <p>
   * In Amadeus, blocks are uniquely identified by the flight number, the
   * departure date, the inventory class and the Tour Reference. Two blocks
   * with the same information cannot be active at the same time.</p>
   * <p>
   * Nevertheless, if a block is deleted, a new block containing the same
   * information may be created; retrieving such block by using the identifying
   * information described above will return both the deleted block and the
   * active block.</p>
   ***********************************************************************
   */
  public static void createBlock(final AmadeusAPICrs aCRS, final Block aBlock)
      throws Exception
    {
    int iNumFlights = aBlock.getNumFlights();

    // make sure that we have flights to add to the block
    if (iNumFlights == 0)
      throw new TranServerException(
          "Provide at least one flight to create a NegoSpace block");

    BlockFlight[] flightList = aBlock.getFlightList();

    for (int i=0; i < iNumFlights; i++)
      {
      String sQuery = "";
      if (aBlock.isActive())
        sQuery = buildQuery_createBlock(aCRS, aBlock, i);
      else
        sQuery = buildQuery_createPassiveBlock(aCRS, aBlock, i);

      String sReply = aCRS.connection.sendAndReceive(sQuery);

      StatusInformationType statusInfo;
			RecordLocatorType recordLocator;
			String rloc = "";

      //  parse the appropriate reply, and retrieve the status information
      try {
        if (aBlock.isActive())
          {
					PoweredAir_CreateNegoSpaceReply createReply = new PoweredAir_CreateNegoSpaceReply();
          createReply = createReply.unmarshalPoweredAir_CreateNegoSpaceReply(new StringReader(sReply));
          statusInfo  = createReply.getStatusInformation();
					recordLocator = createReply.getRecordLocator();
					if ( recordLocator != null ) rloc = recordLocator.getReservation().getControlNumber();
          }
        else
          {
					PoweredAir_CreatePassiveNegoSpaceReply createPassiveReply = new PoweredAir_CreatePassiveNegoSpaceReply();
          createPassiveReply = createPassiveReply.unmarshalPoweredAir_CreatePassiveNegoSpaceReply(new StringReader(sReply));
          statusInfo         = createPassiveReply.getStatusInformation();
					recordLocator      = createPassiveReply.getRecordLocator();
					if ( recordLocator != null ) rloc = recordLocator.getReservation().getControlNumber();
          }
        }
      catch (Exception e) {
        throw new GdsResponseException(
            "Unable to unmarshal Create Block Reply: " + e);
        }

      // make sure that the block was properly created
      String sMsg = retrieveMessages(statusInfo);

      if (sMsg.indexOf(MSG_CREATE_OK) < 0)
        throw new GdsResponseException( "Unable to create block" + sMsg);
      else
        {
        // if we have a success message, retrieve the block by Airline,
        // flight number, class of service, departure date and tour name,
        // in order to retrieve the locator
        sQuery = buildQuery_retrieveByBlockFlight(aBlock, i);
        sReply = aCRS.connection.sendAndReceive(sQuery);

        PoweredAir_DisplayNegoSpaceReply dispReply =
          new PoweredAir_DisplayNegoSpaceReply();

        try {
          dispReply = dispReply.unmarshalPoweredAir_DisplayNegoSpaceReply(
              new StringReader(sReply));
          }
        catch (Exception e) {
          throw new GdsResponseException(
              "Unable to unmarshal DisplayNegoSpaceReply when retrieving by Flight: " + e);
          }

        //String rloc = "";
				System.out.println("AmadeusAPIBlockConversation: rloc: "  + rloc);
        try {
          // A DisplayNegoSpaceReply may contain multiple blocks, i.e. multiple
          // negotiatedSpaceInfo nodes.
          // In particular, if a block was previously deleted, and a new block
          // is created with the same information (flight, depDate,class, Tour
          // Ref), they will both appear on the display:
          //   - one with an 'X' deleted status, and
          //   - one with an 'A' active status or 'C' creation pending status.
          // Hence, we must iterate through the reply and return the locator
          // for the first active block that we find.
          for (int j=0; j < dispReply.getNegotiatedSpaceInfoCount(); j++)
            {
            NegoDetailsType negoDetails =
              dispReply.getNegotiatedSpaceInfo(j).getNegotiatedSpaceDetails();

            String status = negoDetails.getAllotmentStatus();

            if (status.equals(STATUS_ACTIVE) ||
                status.equals(STATUS_PENDING_CREATE))
              {
              rloc = negoDetails.getNegoRloc();
              break;
              }
            } // end for
          if (rloc.equals(""))
            throw new GdsResponseException(
                "No blocks found with status (A)ctive or (C)reation pending");
          }
        catch (Exception e) {
          throw new GdsResponseException(
              "Unable to retrieve locator after creating block: " +
              e.toString());
          }

        aBlock.getFlight(i).setBlockLocator(rloc);
        }

      } // end for

    } // end createBlock


  /**
   ***********************************************************************
   * Retrieve information on the given block
   ***********************************************************************
   */
  public static void displayBlock(final AmadeusAPICrs aCRS, final String aLocator, final String aCarrierCode, final Block aBlock)
    throws Exception
    {
    String sQuery = buildQuery_retrieveByLocator(aLocator, aCarrierCode);
    String sReply = aCRS.connection.sendAndReceive(sQuery);

    PoweredAir_DisplayNegoSpaceReply dispReply =
      new PoweredAir_DisplayNegoSpaceReply();

    // turn reply into object
    try {
      dispReply = dispReply.unmarshalPoweredAir_DisplayNegoSpaceReply(
          new StringReader(sReply));
      }
    catch (Exception e) {
      throw new GdsResponseException(
          "Unable to unmarshal DisplayNegoSpaceReply: " + e);
      }

    parseBlock(dispReply, aBlock);
    } // end displayBlock


  /**
   ***********************************************************************
   * Modify an existing block with the new parameters
   ***********************************************************************
   */
  public static void changeBlock(final AmadeusAPICrs aCRS,
      final String aLocator, final String aCarrierCode, final int iNumAllocated) throws Exception
    {
    String sQuery = buildQuery_retrieveByLocator(aLocator, aCarrierCode);
    String sReply = aCRS.connection.sendAndReceive(sQuery);

    PoweredAir_DisplayNegoSpaceReply dispReply =
      new PoweredAir_DisplayNegoSpaceReply();

    // unmarshall the display reply
    try {
      dispReply = dispReply.unmarshalPoweredAir_DisplayNegoSpaceReply(
          new StringReader(sReply));
      }
    catch (Exception e) {
      throw new GdsResponseException(
          "Unable to unmarshal DisplayNegoSpaceReply " + e);
      }

    // make sure that we received a proper reply
    if (dispReply.getNegotiatedSpaceInfoCount() == 0)
      {
      String sMsg = retrieveMessages(dispReply.getStatusInformation());
      throw new GdsResponseException(
          "Unable to find block " + aLocator + sMsg);
      }

    sQuery = buildQuery_changeBlock(dispReply, iNumAllocated);
    sReply = aCRS.connection.sendAndReceive(sQuery);

    PoweredAir_ChangeNegoSpaceReply changeReply =
      new PoweredAir_ChangeNegoSpaceReply();

    // unmarshall the change reply
    try {
      changeReply = changeReply.unmarshalPoweredAir_ChangeNegoSpaceReply(
          new StringReader(sReply));
      }
    catch (Exception e) {
      throw new GdsResponseException(
          "Unable to unmarshal ChangeNegoSpaceReply: " + e);
      }

    // make sure that the block was properly changed
    String sMsg = retrieveMessages(changeReply.getStatusInformation());

     if (sMsg.indexOf(MSG_MODIFY_OK) < 0)
       throw new GdsResponseException( "Unable to delete block" + sMsg);

    } // end changeBlock

  /**
   ***********************************************************************
   * This method is used to delete a single Negospace block, identified by the
   * block locator; as of this writing, there is no direct way to delete a
   * block by record locator; hence, the block must first be retrieved, and
   * the retrieved information is used to create the query to delete the block.
   ***********************************************************************
   */
  public static void deleteBlock(
      final AmadeusAPICrs aCRS, final String aLocator, final String aCarrierCode) throws Exception
    {
    String sQuery = buildQuery_retrieveByLocator(aLocator, aCarrierCode);
    String sReply = aCRS.connection.sendAndReceive(sQuery);

    PoweredAir_DisplayNegoSpaceReply dispReply =
      new PoweredAir_DisplayNegoSpaceReply();

    // unmarshall the display reply
    try {
      dispReply = dispReply.unmarshalPoweredAir_DisplayNegoSpaceReply(
          new StringReader(sReply));
      }
    catch (Exception e) {
      throw new GdsResponseException(
          "Unable to unmarshal DisplayNegoSpaceReply " + e);
      }

    // make sure that we received a proper reply
    if (dispReply.getNegotiatedSpaceInfoCount() == 0)
      {
      String sMsg = retrieveMessages(dispReply.getStatusInformation());
      throw new GdsResponseException(
          "Unable to find block " + aLocator + sMsg);
      }

    sQuery = buildQuery_deleteBlock(dispReply);
    sReply = aCRS.connection.sendAndReceive(sQuery);

    PoweredAir_CancelNegoSpaceReply cancelReply =
      new PoweredAir_CancelNegoSpaceReply();

    // unmarshall the cancel reply
    try {
      cancelReply = cancelReply.unmarshalPoweredAir_CancelNegoSpaceReply(
          new StringReader(sReply));
      }
    catch (Exception e) {
      throw new GdsResponseException(
          "Unable to unmarshal CancelNegoSpaceReply: " + e);
      }

    // make sure that the block was properly deleted
    String sMsg = retrieveMessages(cancelReply.getStatusInformation());

    if (sMsg.indexOf(MSG_DELETE_OK) < 0)
      throw new GdsResponseException( "Unable to delete block" + sMsg);

    } // end deleteBlock


  /**
   ***********************************************************************
   * Reads the next NegoSpace message from the specified queue
   ***********************************************************************
   */
  public static void readQueueMessage(
      GnrcCrs aCrs, String queueName, String queueCategory,
      boolean leaveMsgInQueue, List blockMessageList) throws Exception
    {

    final int MAX_MOVEDOWNS = 5;
    String sQueueList = buildQueueList(aCrs, queueName, MAX_MOVEDOWNS);

    if (sQueueList.equals(""))
      throw new GdsResponseException(
          "Unable to find Queue: " + queueName, GnrcConvControl.STS_QUEUE_EMPTY);

    String sQueueCat = readMostUrgentQueueCategory(
        queueName, queueCategory, sQueueList);

    if (sQueueCat.equals(""))
      throw new GdsResponseException(
          "The Queue subcategory '" + queueCategory + "' is empty or does not exist",
          GnrcConvControl.STS_QUEUE_EMPTY);

    // query queue
    //String sQueueMessage = aCrs.HostTransaction("QS" + queueName + sQueueCat);
    String sQueueMessage = xmax.crs.GnrcParser.getCombinedHostResponse(
          ((AmadeusAPICrs)aCrs).getAllHostResponses("QS" + queueName + sQueueCat));

    //System.out.println(sQueueMessage);


    // parse message
    parseQueueMessage(sQueueMessage, blockMessageList);

    String sResp = "";

    // remove message from queue unless otherwise specified
    if (leaveMsgInQueue == false)
      sResp = aCrs.HostTransaction("QN");

    // signoff from queue
    sResp = aCrs.HostTransaction("QI");

    if (blockMessageList.size() == 0)
      {
      if (sQueueMessage.indexOf("QUEUE EMPTY") >= 0) {
        throw new GdsResponseException(
            "Queue is empty: " + sQueueMessage, GnrcConvControl.STS_QUEUE_EMPTY);
        }
      else {
        throw new GdsResponseException(
            "The queue message did not contain a block message");
        }
      }

    } // end readQueueMessage


  /**
   ***********************************************************************
   * This method performs a 'list queues' 'QT' cryptic command and performs at
   * most N move-downs to build a complete listing of the named queue section;
   * a queue listing may begin in one screen and end in the next Moved-Down
   * screen; if the named queue is not found it returns the empty string
   ***********************************************************************
   */
  private static String buildQueueList(GnrcCrs aCrs, String sQueueName, int iMaxMoveDowns)
    throws Exception
    {
    final String MORE_REQUEST    = "MD";
    final String MORE_RESPONSE   = "(?s)[-#\\)\\|][\r\n]?>?$";
    final String NOMORE_RESPONSE = "(NOTHING TO SCROLL|NO MORE|END OF SCROLL|END OF DISPLAY)";

    String sRequest = "QT";
    String sResp = "";
    StringBuffer queueList = new StringBuffer("");
    int iNumMoveDowns = 0;

    boolean inQueueSection    = false;
    boolean foundQueueSection = false;

    // matches a Q at the beginning of a line
    // followed by 1 to 2 numbers or spaces
    // such as: Q97 or Q 0
    final String QPATTERN = "^Q[0-9 ]{1,2}";

    while ( iNumMoveDowns < iMaxMoveDowns)
      {
      iNumMoveDowns++;
      sResp = aCrs.HostTransaction(sRequest);

      final StringTokenizer lines = new StringTokenizer(sResp,"\r\n");
      String sLine = "";

      while ( lines.hasMoreTokens() )
        {
        sLine = lines.nextToken().trim();

        MatchInfo matchQ      = null;
        String    matchString = null;

        try {
          matchQ = RegExpMatch.getFirstMatch(sLine,QPATTERN);
          matchString = matchQ.MatchString;
        }
        catch (Exception e) {}

        // if we are at the beginning of a Queue Section
        if (matchQ != null)
          {
          if(matchString.substring(1,matchString.length()).trim().equals(sQueueName))
            {
            // if we found the queue we are looking for
            inQueueSection    = true;
            foundQueueSection = true;
            }
          else
            {
            // if we were in the right queue and come upon a new queue section,
            // our job is done
            if (foundQueueSection)
              return queueList.toString();
            }
          }

        // if we are in the queue section append all lines
        // except the 'More' or 'No More' indicators
        if (inQueueSection &&
            RegExpMatch.matches(sLine,   MORE_RESPONSE) == false &&
            RegExpMatch.matches(sLine, NOMORE_RESPONSE) == false )
          {
            queueList.append(sLine + "\r\n");
          }
        } // end while


      // if we are explicitly messaged that we cannot move down again
      if ( RegExpMatch.matches(sResp,NOMORE_RESPONSE) )
        break;

      // if we are not messaged that we can move down
      if ( RegExpMatch.matches(sResp,MORE_RESPONSE) == false )
        break;

      // move down again
      sRequest = MORE_REQUEST;
      }

    return queueList.toString();
    } // end buildQueueList


  /**
   ***********************************************************************
   * Given a {@link PoweredAir_DisplayNegoSpaceReply} object, retrieve the
   * information contained within it and store it in the {@link Block} object
   * provided
   ***********************************************************************
   */
  public static void parseBlock(
      final PoweredAir_DisplayNegoSpaceReply dispReply, Block block)
    throws GdsResponseException
    {
    if(dispReply.getNegotiatedSpaceInfoCount() == 0)
      {
      throw new GdsResponseException(
          "NegotiatedSpaceInfo node missing in response; " +
          "check the block's locator" +
          retrieveMessages(dispReply.getStatusInformation()));
      }

    //try
    //{
    //FileWriter fwrite;
    //fwrite = new FileWriter("NegoLogd.log");

    //fwrite.write("NegoLog Start\r\n");
    //fwrite.write("AmadeusAPIBlockConversation.parseBlock Start\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock Start");

    // get the NegotiatedSpaceDetails node where most of the block info is displayed
    NegoDetailsType negoDetails =
      dispReply.getNegotiatedSpaceInfo(0).getNegotiatedSpaceDetails();

    // get the information that we need to create the BlockFlight
    // and it's corresponding FlightInfo
    FlightDetailsType flightDetails = negoDetails.getFlightDetails();

    String sCarrier = flightDetails.getAirlineCode();
    int iFlightNum  = flightDetails.getFlightNumber();
		System.out.println("parseBlock:Carr/Flgt: " + sCarrier + "/" + iFlightNum);

    FlightInfo   flightInfo = new FlightInfo(sCarrier,iFlightNum);
    FlightSegment flightSeg = new FlightSegment();
    flightInfo.addFlightSegment(flightSeg);

    String sInvClass = flightDetails.getIdentifierOfClass();
		System.out.println("parseBlock:sInvClass: " + sInvClass);

    int iNumSeatAlloc = 0;
    try {
      iNumSeatAlloc = negoDetails.getSeatQuantity().getNumberOfAllocatedSeat();
      }
    catch (NullPointerException ne) {
      throw new GdsResponseException(
          "Unable to retrieve Number of Seats Allocated: " + ne);
      }

		System.out.println("parseBlock:iNumSeatAlloc: " + iNumSeatAlloc);
		BlockFlight blockFlight = new BlockFlight(flightInfo, sInvClass, iNumSeatAlloc);
    block.addFlight(blockFlight);

    // deal with all the dates

    // the productInfo node, as described in the documentation, should only
    // contain the flightDepartureDate;
    // Nevertheless, the AmadeusAPI returns the commencementDateForSale and the
    // expiryNegoDate within a productInfo node, rather than in the
    // productDateInfo node specified in the documentation;
    // since both of these nodes are optional, we have to perform all the
    // gyrations below to make sure that we don't miss anything

    // get the first productInfo node
    ProductInfoType prodInfo1 = negoDetails.getProductInfo(0);

    String sDepDate       = null;
    String sStartSellDate = null;
    String sStopSellDate  = null;

    if (prodInfo1 != null)
      {
      // it should contain the flightDepartureDate...
      sDepDate = prodInfo1.getFlightDepartureDate();
			System.out.println("parseBlock:sDepDate1: " + sDepDate);

      // if so, proceed to the second productInfo node
      if (sDepDate != null && sDepDate.length() > 0)
        {
				System.out.println("parseBlock:before PT get: ");
        ProductInfoType prodInfo2 = negoDetails.getProductInfo(0);
				System.out.println("parseBlock:after PT get: ");
        if (prodInfo2 != null)
          {
          sStartSellDate = prodInfo2.getCommencementDateForSale();
          sStopSellDate  = prodInfo2.getExpiryNegoDate();
					System.out.println("parseBlock:sStopSellDateP2: " + sStopSellDate);
          }
        }
      // if the first node did not contain the flightDepartureDate
      // try to retrieve it from the first node
      else
        {
        sStartSellDate = prodInfo1.getCommencementDateForSale();
        sStopSellDate  = prodInfo1.getExpiryNegoDate();
				System.out.println("parseBlock:sStopSellDateP1: " + sStopSellDate);
        }
      }
		System.out.println("parseBlock:sStartSellDate1`: " + sStartSellDate);
    // now that we have the dates in string form, convert them to a long
    long d;
    if (sStartSellDate != null)
      {
      d = xmax.util.DateTime.fmtDateToLong(sStartSellDate, AMADEUS_DATE_FMT);
      block.setStartSellDate(d);
      }

    // StopSellDate or ExpiryDate can contain 'OPEN' for 'UA'
    //  if this occurs, StopSellDate will be the DepDate
    //fwrite.write("AmadeusAPIBlockConversation.parseBlock sStopSellDate=" + sStopSellDate + "\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock sStopSellDate=" + sStopSellDate);
		System.out.println("parseBlock:sStopSellDate1`: " + sStopSellDate);
    if (sStopSellDate != null)
      {
      if (isNumeric(sStopSellDate.substring(0,1)))
        {
        d = xmax.util.DateTime.fmtDateToLong(sStopSellDate, AMADEUS_DATE_FMT);
        block.setStopSellDate(d);
        //fwrite.write("AmadeusAPIBlockConversation.parseBlock sStopSellDate is number=" + sStopSellDate.substring(1,2) + "\r\n");
        //System.out.println("AmadeusAPIBlockConversation.parseBlock sStopSellDate is number=" + sStopSellDate.substring(1,2));
        }
      else if (sDepDate != null)
        {
        d = xmax.util.DateTime.fmtDateToLong(sDepDate, AMADEUS_DATE_FMT);
        block.setStopSellDate(d);
        //fwrite.write("AmadeusAPIBlockConversation.parseBlock substitute date=" + sDepDate + "\r\n");
        //System.out.println("AmadeusAPIBlockConversation.parseBlock substitute date=" + sDepDate);
        }
      else
        {
        block.setStopSellDate(0);
        //fwrite.write("AmadeusAPIBlockConversation.parseBlock is 0 \r\n");
        //System.out.println("AmadeusAPIBlockConversation.parseBlock is 0");
        }
      }

    long lDepDate;

    //fwrite.write("AmadeusAPIBlockConversation.parseBlock sDepDate=" + sDepDate + "\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock sDepDate=" + sDepDate);
    if (sDepDate != null)
      {
      lDepDate = xmax.util.DateTime.fmtDateToLong(sDepDate, AMADEUS_DATE_FMT);
      flightSeg.DepSchedDateTime = lDepDate;
      }

    // feeww! done with dates - on to other things
		//
		System.out.println("parseBlock:sDepDate: " + sDepDate);

    // populate the rest of the information in either the Block, BlockFlight,
    // FlightInfo, or FlightSegment objects, as appropriate

    //fwrite.write("AmadeusAPIBlockConversation.parseBlock numberofsoldseats\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock numberofsoldseats");
    blockFlight.setNumSold(negoDetails.getSeatQuantity().getNumberOfSoldSeat());
    //fwrite.write("AmadeusAPIBlockConversation.parseBlock pseudocitycode\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock pseudocitycode");
    block.setPseudoCityCode(negoDetails.getOwnerId());
		System.out.println("parseBlock:PseudoCityCode: " + negoDetails.getOwnerId());

    //fwrite.write("AmadeusAPIBlockConversation.parseBlockadditionalinfo\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlockadditionalinfo");
    AdditionalInfoType adtnInfo = negoDetails.getAdditionalInfo();
    if (adtnInfo != null)
      {
      // changed by NCL 05-03-2002
      //blockFlight.setRemoteCarrierCode(adtnInfo.getTourName().substring(0,2));
      //blockFlight.setCarrierLocator(adtnInfo.getTourName().substring(2));

      // changed again 11-26-2002
      //block.setTourReference(adtnInfo.getTourReference());
      //blockFlight.setRemoteCarrierCode(adtnInfo.getTourReference().substring(0,2));
      //blockFlight.setCarrierLocator(adtnInfo.getTourReference().substring(2));

      //fwrite.write("AmadeusAPIBlockConversation.parseBlock tourname\r\n");
      //System.out.println("AmadeusAPIBlockConversation.parseBlock tourname");
      block.setTourName(adtnInfo.getTourName());
      block.setTourReference(adtnInfo.getTourReference());
			System.out.println("parseBlock:adtnInfo.getTourName(): " + adtnInfo.getTourName());
      }

    //fwrite.write("AmadeusAPIBlockConversation.parseBlock blocklocator\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock blocklocator");
    blockFlight.setBlockLocator(negoDetails.getNegoRloc());
    //fwrite.write("AmadeusAPIBlockConversation.parseBlock allotmentstatus\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock allotmentstatus");
    blockFlight.setAllotmentStatus(negoDetails.getAllotmentStatus());

    //blockFlight.setAllotmentStatus(negoDetails.getAllotmentStatus());
		System.out.println("parseBlock:AllotmentStatus: " + negoDetails.getAllotmentStatus());

    // get the itinerary begin and end points
    LocationDetailsType locationDetails =
      dispReply.getNegotiatedSpaceInfo(0).getLocationDetails();

    //fwrite.write("AmadeusAPIBlockConversation.parseBlock origin/destination\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock origin/destination");
    flightSeg.DepartCity = locationDetails.getOrigin();
    flightSeg.ArriveCity = locationDetails.getDestination();

		System.out.println("parseBlock:flightSeg.ArriveCity: " + flightSeg.ArriveCity);

    //fwrite.write("AmadeusAPIBlockConversation.parseBlock End\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseBlock End");
    //fwrite.close();
    //}
    //catch (Exception e) {}

    } // end parseBlock


  /**
   ***********************************************************************
   * Given a string containing the response to a cryptic 'QT' (list queues)
   * command, this method extracts the Queue Category containing messages for
   * the blocks with the nearest departing flights;
   ***********************************************************************
   */
  protected static String readMostUrgentQueueCategory(
      String queueName, String queueCategory, String sQueueList)
    {
    final StringTokenizer lines = new StringTokenizer(sQueueList,"\r\n");
    String sLine = "";

    // matches a Q at the beginning of a line
    // followed by 1 to 2 numbers or spaces
    // such as: Q97 or Q 0
    final String QPATTERN = "^Q[0-9 ]{1,2}";
    boolean inQueueSection    = false;
    boolean foundQueueSection = false;

    // queue categories contain a single-digit integer that indicates the
    // urgency of the queue category, with 1 being the highest urgency -
    // we initialize this with a value representing an urgency lower than
    // the lowest urgency possible (presumably 9)
    int iMostUrgent = 10;
    String catName = "";

    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken().trim();

      MatchInfo matchQ      = null;
      String    matchString = null;

      try {
        matchQ = RegExpMatch.getFirstMatch(sLine,QPATTERN);
        matchString = matchQ.MatchString;
      }
      catch (Exception e) {}

      // if we have found a queue name
      if (matchQ != null)
        if(matchString.substring(1,matchString.length()).trim().equals(queueName))
          {
          // if we found the queue
          inQueueSection    = true;
          foundQueueSection = true;
          }
        else
          {
          // if we were in the right queue and come upon a new queue section
          // return whatever we have found so far
          if (foundQueueSection)
            return catName;
          }

      if (inQueueSection)
        {
        String sCatParsed = "";
        if (sLine.length() >= 21)
          sCatParsed = sLine.substring(18,21).trim();

        if (sCatParsed.equals(queueCategory))
          {
          String sUrgency = sLine.substring(23,24);

          // if the queue is further categorized make sure
          // that we get the most urgent queue
          try {
            if (RegExpMatch.matches(sUrgency, "[0-9]"))
              {
              int iUrgency = Integer.parseInt(sUrgency);
              if (iUrgency < iMostUrgent)
                {
                iMostUrgent = iUrgency;
                catName = queueCategoryNameToCryptic(sLine.substring(17,24).trim());
                }
              }
            else {
              catName = queueCategoryNameToCryptic(sLine.substring(17,24).trim());
              }
            }
          catch (xmax.util.MatchError e)
            {
            e.toString();
            }
          }
        }
      } // end while

    return (catName);
    } // end readMostUrgentQueueCategory


  /**
   ***********************************************************************
   * Given a cryptic response containing a block message from a queue, and an
   * empty {@link BlockMessage} object, this method populates the BlockMessage
   * object with information from the queue message
   ***********************************************************************
   */
  protected static void parseQueueMessage(
      String sQueueMsg, List blockMessageList)
    {
    final StringTokenizer lines = new StringTokenizer(sQueueMsg,"\r\n");
    String sLine = "";
    int iBlockMsgIdx = -1;

    BlockMessage  blockMessage = null;
    Block         block        = null;
    BlockFlight   blockFlight  = null;
    FlightInfo    flightInfo   = null;
    FlightSegment flightSeg    = null;
    String        sDepDate     = "";

    //try
    //{
    //FileWriter fwrite;
    //fwrite = new FileWriter("NegoLog.log");
    //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage Start\r\n");
    //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage Start\r\n");

    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken().trim();

      // if we encounter the beginning of a message
      if (sLine.startsWith("$$"))
        {
        blockMessageList.add(new BlockMessage("1A"));
        iBlockMsgIdx++;
        blockMessage = (BlockMessage)blockMessageList.get(iBlockMsgIdx) ;
        block        = blockMessage.getBlock();
        blockFlight  = block.getFlight(0);
        flightInfo   = blockFlight.getFlightInfo();
        flightSeg    = flightInfo.getFlightSegment(0);

        // String action = sLine.substring(2,16).trim();
        // String msgLoc = sLine.substring(16,22).trim();

        blockMessage.setAction(sLine.substring(2,16).trim());
        }

      //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage FLTN\r\n");
      //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage FLTN\r\n");
      if (sLine.startsWith("FLTN"))
        {
        // String carrier = sLine.substring(5,7).trim();
        // String flightNum = sLine.substring(8,12).trim();

        flightInfo.setCarrier(sLine.substring(5,7).trim());
        flightInfo.setFlightNum(sLine.substring(8,12).trim());

        // String sellDate = sLine.substring(19,26).trim();

        block.setStartSellDate(
            xmax.util.DateTime.fmtDateToLong(sLine.substring(19,26), "ddMMMyy"));

        // String owner;

        if (sLine.substring(33,34).equals("[") &&
            sLine.substring(43,44).equals("]"))
          // owner = sLine.substring(34,43).trim();
          block.setPseudoCityCode(sLine.substring(34,43).trim());
        else
          // owner = sLine.substring(33,44).trim();
          block.setPseudoCityCode(sLine.substring(33,44).trim());

        // String numSeats = sLine.substring(50,53).trim();
        blockFlight.setNumAllocated(Integer.parseInt(sLine.substring(50,53)));

        String numPending;
        if (sLine.length() == 59)
          // numPending = sLine.substring(55,58).trim();
          blockFlight.setNumPending(Integer.parseInt(sLine.substring(55,58)));
        }

      //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage DATE\r\n");
      //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage DATE\r\n");
      if (sLine.startsWith("DATE"))
        {
        // the departure date is needed in the 'TIME' section, if it exists
        sDepDate         = sLine.substring(5,12);
        String reduxDate = sLine.substring(19,26).trim();
        String reduxPerc = sLine.substring(33,36).trim();
        String numSold   = sLine.substring(50,53).trim();

        // this may be overwritten in the 'TIME' section
        flightSeg.DepSchedDateTime =
            xmax.util.DateTime.fmtDateToLong(sDepDate, "ddMMMyy");

        // this may be overwritten in the 'TIME' section
        flightSeg.ArrSchedDateTime =
            xmax.util.DateTime.fmtDateToLong(sDepDate, "ddMMMyy");

        if (!reduxDate.equals("") && !reduxDate.equals("*******"))
          block.setReductionDate(
              xmax.util.DateTime.fmtDateToLong(reduxDate, "ddMMMyy"));

        if (!reduxPerc.equals(""))
          block.setReductionPercent(Byte.parseByte(reduxPerc));

        if (!numSold.equals(""))
          blockFlight.setNumSold(Integer.parseInt(numSold));
        }

      //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage CYPR\r\n");
      //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage CYPR\r\n");
      if (sLine.startsWith("CYPR"))
        {
        /*
        String depCity   = sLine.substring(5,8);
        String arrCity   = sLine.substring(8,11);
        String expDate   = sLine.substring(19,26);
        String numUnsold = sLine.substring(50,53).trim();
        */

        flightSeg.DepartCity = sLine.substring(5,8);
        flightSeg.ArriveCity = sLine.substring(8,11);

        String sDay = sLine.substring(19,20);
        //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage sDay+sStopSellDate= " + sDay + " " +  sLine.substring(19,26) + "\r\n");
        //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage sDay+sStopSellDate= " + sDay + " " +  sLine.substring(19,26) + "\r\n");
        if (isNumeric(sDay))
          {
          block.setStopSellDate(
            xmax.util.DateTime.fmtDateToLong(sLine.substring(19,26), "ddMMMyy"));
          }
        else if (sDepDate != null)
          {
          //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage sDepDate= " + sDepDate + "\r\n");
          //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage sDepDate= " + sDepDate + "\r\n");
          block.setStopSellDate(
            xmax.util.DateTime.fmtDateToLong(sDepDate, "ddMMMyy"));
          }
        else
          {
          block.setStopSellDate(0);
          //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage = 0\r\n");
          //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage =0\r\n");
          }

        blockFlight.setNumUnsold(Integer.parseInt(sLine.substring(50,53)));

        // String numPendingUnsold;
        if (sLine.length() == 59)
          // numPendingUnsold = sLine.substring(55,58).trim();
          blockFlight.setNumPendingUnsold(Integer.parseInt(sLine.substring(55,58)));
        }

      //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage CLSS+TOUR+TREF\r\n");
      //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage CLSS+TOUR+TREF\r\n");
      if (sLine.startsWith("CLSS"))
        {
        /*
        String classOfServ = sLine.substring(5,6);
        String remLoc = sLine.substring(19,27).trim();
        String crsFlightNum = sLine.substring(33,37).trim();
        String blockLoc = sLine.substring(50,56);
        */

        blockFlight.setClassOfService(sLine.substring(5,6));
        blockFlight.setCarrierLocator(sLine.substring(19,27).trim());
        blockFlight.setCrsFlightId(sLine.substring(33,37));
        blockFlight.setBlockLocator(sLine.substring(50,56).trim());
        }

      if (sLine.startsWith("TOUR"))
        {
        /*
        String TourName = sLine.substring(5,20).trim();
        String authCode = sLine.substring(33,39).trim();
        String sellType = sLine.substring(50,51);
        String sellMeth = sLine.substring(52,58).trim();
        */

        //block.setMemo(sLine.substring(5,20).trim());
        block.setTourName(sLine.substring(5,20).trim());
        //block.crsProperty.setProperty("AuthCode",sLine.substring(33,39).trim());
        block.setAuthCode(sLine.substring(33,39).trim());
        block.crsProperty.setProperty("SellType",sLine.substring(50,51).trim());
        // the line length is either '58' or '59' depending on whether
        // the SellMethod is 'ACTIVE' or 'PASSIVE' respectively
        block.crsProperty.setProperty("SellMethod",sLine.substring(52,sLine.length()).trim());
        }

      if (sLine.startsWith("TREF"))
        {
        // String TRef = sLine.substring(5,sLine.length() ).trim();
        block.crsProperty.setProperty("TourRef",sLine.substring(5,sLine.length()).trim());
        }

      //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage LNK\r\n");
      //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage LNK\r\n");
      if (sLine.startsWith("LNK"))  // 'LINKED' blocks
        {
        String carrier        = sLine.substring(5,7).trim();
        int    iFlightNum     = Integer.parseInt(sLine.substring(8,12).trim());
        String classOfService = sLine.substring(13,14);
        String depCity        = sLine.substring(15,18);
        String arrCity        = sLine.substring(18,21);
        String blockLoc       = sLine.substring(22,28).trim();

        FlightSegment lnkFlightSeg  = new FlightSegment();
        lnkFlightSeg.DepartCity = depCity;
        lnkFlightSeg.ArriveCity = arrCity;

        FlightInfo lnkFlightInfo = new FlightInfo(carrier,iFlightNum);
        lnkFlightInfo.addFlightSegment(lnkFlightSeg);

        BlockFlight lnkBlockFlight  = new BlockFlight();
        lnkBlockFlight.setFlightInfo(lnkFlightInfo);
        lnkBlockFlight.setClassOfService(classOfService);
        lnkBlockFlight.setBlockLocator(blockLoc);

        block.addFlight(lnkBlockFlight);

        // linkType can be (I)nformative or (M)andatory
        // String linkType = sLine.substring(50,51);
        // String linkID   = sLine.substring(52,56);
        }

      if (sLine.startsWith("EVNT"))
        {
        // String event = sLine.substring(5,sLine.length() ).trim();
        block.crsProperty.setProperty("Event",sLine.substring(5,sLine.length()).trim());
        }

      if (sLine.startsWith("STAT"))
        {
        // String blockStatusCode = sLine.substring(5,6);
        // String blockStatus     = sLine.substring(7,sLine.length());
        block.crsProperty.setProperty("Status",sLine.substring(5,6));
        block.crsProperty.setProperty("StatusText",sLine.substring(7,sLine.length()));
        }

      //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage TIME\r\n");
      //System.out.println("AmadeusAPIBlockConversation.parseQueueMessage TIME\r\n");
      if (sLine.startsWith("TIME"))
        {
        // by default, set the arrival date to the departure date
        // (without the time) that we parsed above in the 'DATE' section
        long lArrDate = flightSeg.DepSchedDateTime;

        // add the time, if available, to the departure datetime
        String TIME_PATTERN = "[0-9]{4}";
        String depTime = sLine.substring(5,9).trim();
        // The Departure Date 'sDepDate' was parsed above in the 'DATE' section
        try {
          if (RegExpMatch.matches(depTime, TIME_PATTERN)) {
            flightSeg.DepSchedDateTime = xmax.util.DateTime.fmtDateToLong(
                "" + sDepDate + depTime, "ddMMMyyHHmm");
            }
        } catch (MatchError e) {
          System.out.println(
              "error validating departure time in block message: " + e.toString());
        }

        String arrTime    = sLine.substring(10,sLine.length());
        boolean arrTimeOK = false;

        try {
          arrTimeOK = RegExpMatch.matches(arrTime, TIME_PATTERN);
        } catch (MatchError e) {
          System.out.println(
              "error validating arrival time in block message: " + e.toString());
        }

        if (arrTimeOK)
          {
          // determine if the arrival day is different from the departure day
          String offsetSign = "";
          int    offsetNum  = 0;

          if (sLine.length() >= 17)
            {
            offsetSign = sLine.substring(15,16);
            offsetNum  = Integer.parseInt(sLine.substring(16,17));
            block.crsProperty.setProperty("DayIndic", sLine.substring(15,17));
            }

          if (offsetSign.equals("") == false && offsetNum != 0 )
            {
            if (offsetSign.equals("+"))  lArrDate += offsetNum*24*60*60*1000;
            if (offsetSign.equals("-"))  lArrDate -= offsetNum*24*60*60*1000;
            }

          // compute the arrival datetime
          int iArrHour   = Integer.parseInt(sLine.substring(10,12).trim());
          int iArrMinute = Integer.parseInt(sLine.substring(12,14).trim());

          flightSeg.ArrSchedDateTime =
            lArrDate + (iArrHour*60*60*1000) + (iArrMinute*60*1000);
          }
        } // end 'TIME' section

      } // end while
    //fwrite.write("AmadeusAPIBlockConversation.parseQueueMessage End\r\n");
    //fwrite.close();
    //}
    //catch (Exception e) {}

    } // end parseQueueMessage

  /**
   ***********************************************************************
   * This method accepts a <code>Block</code> object, instantiates a
   * {@link PoweredAir_CreateNegoSpace} object, calls {@link
   * buildNode_NegotiatedSpaceInformation} to create the main
   * negotiatedSpaceInfo node that maps the Block information into a suitable
   * AmadeusAPI query, and returns an xml string suitable for sending to
   * the AmadeusAPI.
   ***********************************************************************
   */
  public static String buildQuery_createBlock(AmadeusAPICrs crs, Block block,int index)
    throws TranServerException
    {
    PoweredAir_CreateNegoSpace createNego = new PoweredAir_CreateNegoSpace();
    createNego.setNegotiatedSpaceInformation(
        buildNode_NegotiatedSpaceInformation(crs, block, index));
    try
      {
      StringWriter sw = new StringWriter();
      createNego.marshal(sw);
      //aryQuery[i] = sw.toString();
      return(sw.toString());
      }
    catch (Exception e)
      {
      throw new TranServerException(
          "Unable to create CreateNegoSpace query: " + e);
      }

    } // end buildQuery_createBlock


  /**
   ***********************************************************************
   * This method accepts a <code>Block</code> object, instantiates a
   * {@link PoweredAir_CreatePassiveNegoSpace} object, calls {@link
   * buildNode_NegotiatedSpaceInformation} to create the main
   * negotiatedSpaceInfo node that maps the Block information into a suitable
   * AmadeusAPI query, and returns an xml string suitable for sending to
   * the AmadeusAPI.
   ***********************************************************************
   */
  public static String buildQuery_createPassiveBlock(AmadeusAPICrs crs, Block block,int index)
    throws TranServerException
    {
    PoweredAir_CreatePassiveNegoSpace createPassiveNego = new PoweredAir_CreatePassiveNegoSpace();
    createPassiveNego.setNegotiatedSpaceInformation(
        buildNode_NegotiatedSpaceInformation(crs, block, index));

    try
      {
      StringWriter sw = new StringWriter();
      createPassiveNego.marshal(sw);
      //aryQuery[i] = sw.toString();
      return(sw.toString());
      }
    catch (Exception e)
      {
      throw new TranServerException(
          "Unable to create CreatePassiveNegoSpace query: " + e);
      }

    } // end buildQuery_createPassiveBlock


  /**
   ***********************************************************************
   * This method builds a {@link NegotiatedSpaceInformation} node from the
   * {@link Block} object; this method is used in both
   * buildQuery_CreateNegoSpace and buildQuery_CreatePassiveNegoSpace.
   ***********************************************************************
   */
  public static NegotiatedSpaceInformation buildNode_NegotiatedSpaceInformation(AmadeusAPICrs crs, Block block,int index)
    throws TranServerException
    {
    BlockFlight blockFlight = block.getFlight(index);

    NegotiatedSpaceInformation negotiatedSpaceInformation = new NegotiatedSpaceInformation();

    // 1. create the negotiatedSpaceDetails node, one of the three main nodes
    NegoDetailsType negotiatedSpaceDetails   = new NegoDetailsType();
    negotiatedSpaceInformation.setNegotiatedSpaceDetails(negotiatedSpaceDetails);

    // create and populate the flightDetails node
    FlightDetailsType flightDetails = new FlightDetailsType();
    negotiatedSpaceDetails.setFlightDetails(flightDetails);

    flightDetails.setAirlineCode(blockFlight.getFlightInfo().getCarrier());
    flightDetails.setFlightNumber(blockFlight.getFlightInfo().getFlightNum());
    flightDetails.setIdentifierOfClass(blockFlight.getClassOfService());

    // create and populate the productInfo node
    Date d = new Date(blockFlight.getFlightInfo().getDepSchedDate());
    ProductInfoType productInfo = new ProductInfoType();
    productInfo.setFlightDepartureDate(fmtAmadeusDate.format(d));
    negotiatedSpaceDetails.addProductInfo(productInfo);

    // if we have a start sell date or stop sell date
    // create and populate the productDateInfo node
    if (block.getStartSellDate() > 0 || block.getStopSellDate()  > 0 )
      {
      ProductDateInfoType productDateInfo = new ProductDateInfoType();
      negotiatedSpaceDetails.setProductDateInfo(productDateInfo);

      d.setTime( block.getStartSellDate() );
      if (d.compareTo(new Date()) > 0)  // date is later than today
        productDateInfo.setCommencementDateForSale(fmtAmadeusDate.format(d));

      d.setTime( block.getStopSellDate() );
      productDateInfo.setExpiryNegoDate(fmtAmadeusDate.format(d));
      }

    // create and populate the seatQuantity node
    SeatQuantityType seatQuantity = new SeatQuantityType();
    negotiatedSpaceDetails.setSeatQuantity(seatQuantity);

    seatQuantity.setNumberOfAllocatedSeat(blockFlight.getNumAllocated());

    // populate the ownerID node
    String sPseudoCity = block.getOwnerID();
    if (sPseudoCity == null || sPseudoCity.length() == 0)
    {
    	sPseudoCity = crs.getProperties().getProperty("ownerID");
    }
    if (sPseudoCity == null || sPseudoCity.length() == 0)
    {
      throw new TranServerException( "<ownerID> node missing from the Transaction Server configuration file and block create message");
    }
    negotiatedSpaceDetails.setOwnerId(sPseudoCity);

    // create and populate the additionalInfo node
    AdditionalInfoType additionalInfo = new AdditionalInfoType();

    negotiatedSpaceDetails.setAdditionalInfo(additionalInfo);

    if (block.getReductionDate() > 0)
      additionalInfo.setNegoReductionDate(
          fmtAmadeusDate.format(new Date(block.getReductionDate())));

    if (block.getReductionPercent() > 0)
      additionalInfo.setPercentageOfReduction(block.getReductionPercent());


    // changed by NCL 05-03-2002
    //additionalInfo.setTourReference(block.getTourReference());
    // changed again 11-26-2002
    additionalInfo.setTourName(block.getTourName());

    // concatenate the Carrier's Crs Code and the remote locator
    // to derive a unique Carrier Remote Locator (managing locator)
    // additionalInfo.setTourName(   // changed by NCL 05-03-2002

    // additionalInfo.setTourReference(
    //    blockFlight.getRemoteCarrierCode() + blockFlight.getCarrierLocator());

    // changed again 11-26-2002
    if (block.getTourReference() instanceof String)
      additionalInfo.setTourReference(block.getTourReference());

    if (block.getAuthCode() instanceof String)
      additionalInfo.setAuthorizationCode(block.getAuthCode());


    // populate the handlingTable node
    String handlingTable = block.getHandlingTable();
    if (handlingTable == null || handlingTable.length() == 0)
    {
    	handlingTable = crs.getProperties().getProperty("handlingTable");
    }
    if (handlingTable == null || handlingTable.length() == 0)
    {
      throw new TranServerException( "<handlingTable> node missing from the Transaction Server configuration file and block create message");
    }
    negotiatedSpaceDetails.setHandlingTable(handlingTable);


    // 2. create the locationDetails node, the second of the three main nodes
    LocationDetailsType locationDetails = new LocationDetailsType();
    negotiatedSpaceInformation.setLocationDetails(locationDetails);

    locationDetails.setOrigin(blockFlight.getFlightInfo().getDepCity());
    locationDetails.setDestination(blockFlight.getFlightInfo().getArrCity());

    // 3. link this block to other blocks (we'll wait for this one)

    return negotiatedSpaceInformation;

    } // end buildQuery_createBlock


  /**
   ***********************************************************************
   * This method returns a NegotiatedSpaceInfo node that we can add to a
   * PoweredAir_DeleteNegoSpace or PoweredAir_ChangeNegoSpace to identify the
   * block to be acted upon;
   * The Amadeus API does not make it possible to delete and change blocks by
   * simply providing a remote locator; hence it is necessary to first retrieve
   * the block, and then use the information in the
   * PoweredAir_DisplayNegoSpaceReply returned to issue the deleteBlock or
   * changeBlock command; unfortunately, because of inconsistencies in the
   * AmadeusAPI, we first have to fix some of these inconsistencies.
   ***********************************************************************
   */
  public static NegotiatedSpaceInfo buildNode_NegotiatedSpaceInfo(
      PoweredAir_DisplayNegoSpaceReply dispReply)
    {
    // get the negoSpaceInfo node from the reply
    NegotiatedSpaceInfo negoSpaceInfo = dispReply.getNegotiatedSpaceInfo(0);

    // fix a few things
    NegoDetailsType negoDetails = negoSpaceInfo.getNegotiatedSpaceDetails();

    // the DisplayNegoSpaceReply may have 2 productInfo nodes;
    // (the second one should have been called productDateInfo); remove this
    // node because it's not needed, and will cause validation to fail
		int siz_prod_info = negoDetails.getProductInfoCount();
		System.out.println("buildNode_NegotiatedSpaceInfo: before getProductInfo: siz_prod_info " + siz_prod_info);
		if ( siz_prod_info > 1)
		  negoDetails.removeProductInfo(negoDetails.getProductInfo(1));
		System.out.println("buildNode_NegotiatedSpaceInfo: after getProductInfo");

		
		
    // these nodes (or sub-nodes thereoff) are not needed
    // and are not valid to identify the block in a delete/change query
    negoDetails.setSeatQuantity(null);
    negoDetails.setAdditionalInfo(null);
    negoDetails.setAllotmentStatus(null);
    negoDetails.setSearchOption(null);
	negoDetails.setProductDateInfo(null);    // DMF fix 11/13/2011

    return negoSpaceInfo;

    } // end buildNode_NegotiatedSpaceInfo

  /**
   ***********************************************************************
   * After a NegoSpace block is created, the AmadeusAPI does not return the
   * locator that identifies the block; hence, when created, the block must be
   * displayed using the information from the flight just added in order to
   * retrieve the locator; this method returns the query to redisplay the
   * Amadeus negospace block corresponding to a given <code>BlockFlight</code>
   * in a <code>Block</code> object.
   ***********************************************************************
   */
  public static String buildQuery_retrieveByBlockFlight(Block block, int index)
    throws TranServerException
    {
    BlockFlight blockFlight = block.getFlight(index);

    // create the top level node and its only direct child node
    PoweredAir_DisplayNegoSpace dispNego    = new PoweredAir_DisplayNegoSpace();
    NegoDisplayRequest negoDisplayRequest = new NegoDisplayRequest();
    dispNego.setNegoDisplayRequest(negoDisplayRequest);

    //// create the negotiatedSpaceDetails node, one of the three main nodes
    NegoDetailsType negotiatedSpaceDetails   = new NegoDetailsType();
    negoDisplayRequest.setNegotiatedSpaceDetails(negotiatedSpaceDetails);

    // create and populate the flightDetails node
    FlightDetailsType flightDetails = new FlightDetailsType();
    negotiatedSpaceDetails.setFlightDetails(flightDetails);

    flightDetails.setAirlineCode(blockFlight.getFlightInfo().getCarrier());
    flightDetails.setFlightNumber(blockFlight.getFlightInfo().getFlightNum());
    flightDetails.setIdentifierOfClass(blockFlight.getClassOfService());

    // create and populate the productInfo node (the departure date)
    long d = blockFlight.getFlightInfo().getDepSchedDate();
    ProductInfoType productInfo = new ProductInfoType();
    productInfo.setFlightDepartureDate(fmtAmadeusDate.format(new Date(d)));
    negotiatedSpaceDetails.addProductInfo(0,productInfo);

    // populate the ownerID node
    negotiatedSpaceDetails.setOwnerId(block.getPseudoCityCode());

    // create and populate the additionalInfo node
    AdditionalInfoType additionalInfo = new AdditionalInfoType();

    negotiatedSpaceDetails.setAdditionalInfo(additionalInfo);

    // changed by NCL 05-03-2002
    //additionalInfo.setTourName(
    //    blockFlight.getRemoteCarrierCode() + blockFlight.getCarrierLocator());
    //additionalInfo.setTourReference(block.getTourReference());

    /* changed 11-26-2002
      // TOUR_NAME_PADDING (15 dashes: '-') gets added when creating the block
      // and represents an empty field (because the tourName field is 'required')
      // when retrieving the block, we need to leave the tourName field blank
      if (block.getMemo().equals(TOUR_NAME_PADDING))
        additionalInfo.setTourName("");
      else
        //additionalInfo.setTourName(block.getMemo());

    additionalInfo.setTourReference(
        blockFlight.getRemoteCarrierCode() + blockFlight.getCarrierLocator());
    */

    additionalInfo.setTourName(block.getTourName());
    additionalInfo.setTourReference(block.getTourReference());


    // 2. create the locationDetails node, the second of the three main nodes
    LocationDetailsType locationDetails = new LocationDetailsType();
    negoDisplayRequest.setLocationDetails(locationDetails);

    locationDetails.setOrigin(blockFlight.getFlightInfo().getDepCity());
    locationDetails.setDestination(blockFlight.getFlightInfo().getArrCity());

    try
      {
      StringWriter sw = new StringWriter();
      dispNego.marshal(sw);
      //aryQuery[i] = sw.toString();
      return(sw.toString());
      }
    catch (Exception e)
      {
      throw new TranServerException(
          "Unable to create DisplayNegoSpace query using BlockFlight: " + e);
      }

    } // end buildQuery_retrieveByBlockFlight


  /**
   ***********************************************************************
   * Given an Amadeus NegoSpace Block locator, this method builds the query to
   * retrieve the block by locator
   ***********************************************************************
   */
  public static String buildQuery_retrieveByLocator(String sLocator, final String aAirlineCode)
      throws TranServerException
    {
    if (sLocator == null || sLocator == "")
      throw new TranServerException(
          "Unable to create DisplayNegoSpace query: locator is null or empty");

    if (aAirlineCode == null || aAirlineCode == "")
        throw new TranServerException(
            "Unable to create DisplayNegoSpace query: carrier code is null or empty");
    
    // create the top level node and its only direct child node
    PoweredAir_DisplayNegoSpace dispNego    = new PoweredAir_DisplayNegoSpace();
    NegoDisplayRequest negoDisplayRequest   = new NegoDisplayRequest();
    dispNego.setNegoDisplayRequest(negoDisplayRequest);

    // create the negotiatedSpaceDetails node, one of the three main sub-nodes
    NegoDetailsType negotiatedSpaceDetails  = new NegoDetailsType();
    negoDisplayRequest.setNegotiatedSpaceDetails(negotiatedSpaceDetails);

    // provide a generic airline code
    FlightDetailsType flightDetails = new FlightDetailsType();
    negotiatedSpaceDetails.setFlightDetails(flightDetails);

    // flightDetails.setAirlineCode(ANY_AIRLINE);
    flightDetails.setAirlineCode(aAirlineCode);

    // provide the Amadeus locator
    negotiatedSpaceDetails.setNegoRloc(sLocator);

    try
      {
      StringWriter sw = new StringWriter();
      dispNego.marshal(sw);
      //aryQuery[i] = sw.toString();
      return(sw.toString());
      }
    catch (Exception e)
      {
      throw new TranServerException(
          "Unable to create DisplayNegoSpace query using Locator '" +
          sLocator + "': " + e);
      }

    } // end buildQuery_retrieveByLocator


  /**
   ***********************************************************************
   * builds a query to change a block based on the information contained in a
   * block display; this is necessary because there is no way to change a block
   * directly using a locator, and because Amadeus requires that we specify the
   * field <code>nego1Aid</code> which is a sequential key (0,1,2...)
   * that distinguishes between multiple blocks which have the same
   * flight/departure date/invClass.
   ***********************************************************************
   */
  public static String buildQuery_changeBlock(
    PoweredAir_DisplayNegoSpaceReply dispReply, int iNumAllocated)
      throws TranServerException
    {
    PoweredAir_ChangeNegoSpace changeNego = new PoweredAir_ChangeNegoSpace();

    // add the new allocation
    NewNegoData newNegoData = new NewNegoData();
    newNegoData.setSeatQuantity(new SeatQuantityType());
    newNegoData.getSeatQuantity().setNumberOfAllocatedSeat(iNumAllocated);
    changeNego.setNewNegoData(newNegoData);

    // add the node that will identify the nego to be changed
    changeNego.addNegotiatedSpaceInfo(buildNode_NegotiatedSpaceInfo(dispReply));

    // work around a PoweredAir_ChangeNegoSpace typo:
    // flig-h-t-Details node is mispelled: flig-t-h-Details

    // retrieve the content of the properly spelled node
    FlightDetailsType flightDetails = changeNego.getNegotiatedSpaceInfo(0).getNegotiatedSpaceDetails().getFlightDetails();

    // remove the properly spelled node
    changeNego.getNegotiatedSpaceInfo(0).getNegotiatedSpaceDetails().setFlightDetails(flightDetails);
    changeNego.getNegotiatedSpaceInfo(0).getNegotiatedSpaceDetails().setFligthDetails(null);
    changeNego.getNegotiatedSpaceInfo(0).getNegotiatedSpaceDetails().setProductDateInfo(null);

    // add the contents of the properly saved node to the mispelled node
   // changeNego.getNegotiatedSpaceInfo(0).getNegotiatedSpaceDetails().setFligthDetails(flightDetails);

    try
      {
      StringWriter sw = new StringWriter();
      changeNego.marshal(sw);
      //aryQuery[i] = sw.toString();
      return(sw.toString());
      }
    catch (Exception e)
      {
      throw new TranServerException(
          "Unable to create DeleteNegoSpace query using a DisplayNegoSpaceReply: " + e);
      }

    } // end buildQuery_deleteBlock

  /**
   ***********************************************************************
   * builds a query to delete a block based on the information contained in a
   * block display; this is necessary because there is no way to delete a block
   * directly using a locator, and because Amadeus requires that we specify the
   * field <code>nego1Aid</code> which is a sequential key (0,1,2...)
   * that distinguishes between multiple blocks which have the same
   * flight/departure date/invClass.
   ***********************************************************************
   */
  public static String buildQuery_deleteBlock(
    PoweredAir_DisplayNegoSpaceReply dispReply) throws TranServerException
    {
    PoweredAir_CancelNegoSpace cancelNego = new PoweredAir_CancelNegoSpace();
		System.out.println("buildQuery_deleteBlock");
    cancelNego.addNegotiatedSpaceInfo(buildNode_NegotiatedSpaceInfo(dispReply));

    try
      {
      StringWriter sw = new StringWriter();
      cancelNego.marshal(sw);
      //aryQuery[i] = sw.toString();
      return(sw.toString());
      }
    catch (Exception e)
      {
      throw new TranServerException(
          "Unable to create DeleteNegoSpace query using a DisplayNegoSpaceReply: " + e);
      }

    } // end buildQuery_deleteBlock


  /**
   ***********************************************************************
   * Utility method that retrieves and concatenates the free text returned in a
   * <code>textInformation</code> node; if the {@link TextInformationType}
   * provided is <code>null</code>, this method returns a <code>null</code>.
   ***********************************************************************
   */
  public static String concatenateFreeText(TextInformationType textInfo)
    {
    if (textInfo == null)
      return null;

    Enumeration freeTextList = textInfo.enumerateFreeText();
    StringBuffer freeText = new StringBuffer();

    while(freeTextList.hasMoreElements())
      {
      freeText.append((String)freeTextList.nextElement());
      freeText.append(" ");
      }

    // remove the last appended space
    if (freeText.length() > 0)
      freeText.deleteCharAt(freeText.length()-1);

    return(freeText.toString());

    } // end concatenateFreeText

  /**
   ***********************************************************************
   * Given a <code>statusInformation</code> containing possible errors, this
   * method concatenates the error text and the error codes contained in the
   * node; if the StatusInformationType object that is passed is
   * <code>null</code>, the method returns an error message to that effect;
   * note that this method refers to messages originating from an operation on
   * a block, and has nothing to do with reading messages originating from a
   * Negospace Queue
   ***********************************************************************
   */
  public static String retrieveMessages(StatusInformationType statusInfo)
    {
    if (statusInfo == null)
      return(" - No errors returned in reply: statusInformation node returned is null");

    StringBuffer error = new StringBuffer();

    String sError = concatenateFreeText(statusInfo.getTextInformation());

    if (sError != null)
      error.append(" - Amadeus Message: " + sError + " - ");

    ErrorOrWarningInfoType errorOrWarning = null;
    if (statusInfo.getErrorOrWarningInfo() != null)
      errorOrWarning = statusInfo.getErrorOrWarningInfo();
    // the PoweredAir_DisplayNegoSpaceReply has a typo
    else if (statusInfo.getErroOrWarningInfo() != null)
      errorOrWarning = statusInfo.getErroOrWarningInfo();

    if (errorOrWarning != null)
      {
      error.append("Code: ");
      error.append(errorOrWarning.getError().getCode());
      error.append(" - ");
      error.append("Type: ");
      error.append(errorOrWarning.getError().getType());
      error.append(" - ");
      error.append("ListResp: ");
      error.append(errorOrWarning.getError().getListResponsible());
      }

    return(error.toString());

    } // end retrieveMessages


  /**
   ***********************************************************************
   * Removes any periods or spaces in a queue category name retrieved from a
   * cryptic queue list display (QT), and returns a string suitable for
   * starting the queue category through a cryptic command; for example:
   * 'C 10.D1' is returned as 'C10D1'
   ***********************************************************************
   */
  private static String queueCategoryNameToCryptic(String sName)
    {
	  final StringTokenizer tk = new StringTokenizer(sName, " .");

	  final StringBuffer sBuf = new StringBuffer();
	  while ( tk.hasMoreTokens() )
	  {
		  sBuf.append(tk.nextToken());
	  }

	  return sBuf.toString();

    // create the perl regex objects
	  /*
    PatternMatcher  matcher  = new Perl5Matcher();
    Pattern         pattern  = null;
    PatternCompiler compiler = new Perl5Compiler();

    // match any space or period
    try {
      pattern = compiler.compile("[ .]");
    } catch (Exception e) {}


    // remove space or periods
    String result = Util.substitute(
      matcher, pattern, new Perl5Substitution(""),
      sName, Util.SUBSTITUTE_ALL).trim();

    return result;
    */
    } // end queueCategoryNameToCryptic

  /**
   ***********************************************************************
   * Checks to see whether the PoweredAir_MultiAvailabilityReply returned by
   * Amadeus contains the message 'NO NEGOTIATED SPACE IS AVAILABLE' which
   * indicates that the block requested can no longer be sold, for example as
   * the result of a flight replacement or schedule change
   ***********************************************************************
   */
  public static boolean negoIsNotAvailable(Document multiAvailReply)
    {
    Element root = multiAvailReply.getDocumentElement();
    Element el = (Element)root.getElementsByTagName("singleCityPairInfo").item(0);
    el = (Element)el.getElementsByTagName("cityPairFreeFlowText").item(0);

    return (DOMutil.getTagValue(el,"freeText").indexOf(
          "NO NEGOTIATED SPACE IS AVAILABLE") >= 0);
    } // end negoIsNotAvailable

  /**
   ***********************************************************************
   * Checks to see if string contains all digits
   ***********************************************************************
   */
  private static final boolean isNumeric(final String s)
    {
    System.out.println("AmadeusAPIBlockConversation.isNumeric");

    final char[] numbers = s.toCharArray();
    for (int x = 0; x < numbers.length; x++)
    {
    final char c = numbers[x];
    if ((c >= '0') && (c <= '9')) continue;
    return false; // invalid
    }
    return true; // valid
} // end isNumeric

} // end class AmadeusAPIBlockConversation