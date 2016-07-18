package xmax.crs.Amadeus;

import java.lang.String;

import xmax.TranServer.GnrcFormat;
import xmax.TranServer.ReqGetFare;
import xmax.TranServer.ReqIssueTicket;
import xmax.TranServer.TranServerException;
import xmax.TranServer.ConfigTranServer;

import xmax.crs.PNR;
import xmax.crs.PNRFare;
import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.crs.GdsResponseException;
import xmax.crs.GetPNR.PNRNameElement;
import xmax.crs.GetPNR.PNRItinAirSegment;
import xmax.crs.GetPNR.*;

import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;

import java.util.StringTokenizer;
import java.util.Vector;
import java.text.DecimalFormat;

/**
 ***********************************************************************
 * This class handles the conversation (exchange of requests and responses)
 * that takes place between a client seeking faring information and
 * the Amadeus Computer Reservation System (CRS).
 *
 * <p>Having a valid Passenger Name Record (PNR) locator is a
 * prerequisite to obtaining Faring information.
 *
 * <p>The conversation takes place mainly in the method GetFareForPNR.
 * The other methods support different aspects of the
 * conversation, such as building the command to the CRS and parsing the
 * response from the CRS.
 *
 *
 * @author   David Fairchild
 * @author   Philippe Paravicini
 * @version  1.x Copyright (c) 1999
 * @see      #getFareForPNR
 * @see      #getFareCommand
 * @see      #readFares
 ***********************************************************************
 */

public class AmadeusGetPNRFareConversation
{
  /**
   ***********************************************************************
   * This method accepts a generic CRS object and a generic ReqGetFare request,
   * and calls other methods that: build and pass a command to the CRS
   * based on the request, parse the response to extract the appropriate
   * information, and store this information in the ReqGetFare object.
   *
   * <p>To do so, it calls the following methods:
   * <ul>
   *   <li> getFareCommand: builds the command to be passed to Amadeus.
   *   <li> getAmadeusHostResponses: sendReceive cryptic command
   *   <li> readFares: parses the response and stores the information
   *        in the ReqGetFare object.
   * </ul>
   *
   * @param aCRS
   *   The CRS connection object that is being used
   *
   * @param aRequest
   *   The ReqGetFare object that is used to store the information extracted
   *
   * @see      #getFareCommand
   * @see      #readFares
   ***********************************************************************
   */

  public void getFareForPNR(final GnrcCrs aCRS, final ReqGetFare aRequest)
    throws Exception
    {

    // retrieve PNR if needed
    if ( GnrcFormat.IsNull( aRequest.pnr.getPNRData() ) )
      aCRS.GetPNRAllSegments(aRequest.getLocator(),aRequest.pnr,true);

    // construct the command for getting adult fares
		/*
		final String sRequest         = getFareCommand(aRequest);
    final String[] sResponseLines = ((AmadeusCrs)aCRS).getAmadeusHostResponses(sRequest);
    final String sResponse        = GnrcParser.getCombinedHostResponse(sResponseLines);

    readFares(sResponse,aRequest.pnr,aRequest);
		*/
    }

  /**
   ***********************************************************************
   * This method builds the Fare cryptic code command to be passed
   * to an Amadeus TA, based on the client's request.
   *
   * <p>This method will issue one of the following kind of cryptic code prefixes:
   * <ul>
   *   <li><code>FXX</code>: Fare without storing fare
   *   <li><code>FXP</code>: Fare and store fare in PNR
   *   <li><code>FXL</code>: Fare lowest available
   * </ul>
   *
   * <p>It will then append one of the followind modifiers, dependending
   * on the request:
   * <ul>
   *  <li> <code>/S4,5</code>: Fare selected segments
   *  <li> <code>/P1,3</code>: Fare selected passengers
   *  <li> <code>/RADT</code>: Fare a specific Passenger Type Code
   *  <li> <code>/P1/RADT //P2/RC10</code>Fare specific passengers at specific PTCs
   * </ul>
   *
   * @param aRequest
   *   The ReqGetFare object passed by the client.
   *
   * @return   a String containing the appropriate command to be
   *          passed to the CRS by the low-level connectivity interface
   *
   * @see      #getPTCList
   * @see      #getNameList
   * @see      #getSegmentList
   * @see      #getSegmentList
   ***********************************************************************
   */

  private String getFareCommand(final ReqGetFare aRequest) throws Exception
    {
    final StringBuffer sCommand = new StringBuffer();

    if ( aRequest.StoreFares )
      sCommand.append("FXP");
    else if ( aRequest.isLowest() )
      sCommand.append("FXL");
    else
      sCommand.append("FXX");

    // get the list of selected segments
    final String sSegList = getSegmentList(
        aRequest.getSegments(), aRequest.pnr );
    if ( GnrcFormat.NotNull(sSegList) )
      {
      sCommand.append("/S");
      sCommand.append(sSegList);
      }

//    // get the list of selected passengers and Passenger Type Codes
    final String sPsgrPTCmodifier =
      get_Psgr_PTC_Modifier(aRequest.getNames(), aRequest.pnr);

    if (GnrcFormat.NotNull(sPsgrPTCmodifier) )
      sCommand.append(sPsgrPTCmodifier);

//    The following was moved to get_Psgr_PTC_Modifier because it is used in the
//    issueTicket method as well
//
//    // get the list of selected passengers and Passenger Type Codes
//    final Vector vctrPsgrNumList = getPsgrNumList(aRequest.getNames(),aRequest.pnr);
//    final Vector vctrPTCList = getPTCList(aRequest.getNames());
//    final int intNumPsgr = vctrPsgrNumList.size();
//    final int intNumPTC  = vctrPTCList.size();
//
//    // if we have only received a passenger list without their PTCs
//    // issue a command such as: FXX/P1,3
//    if ( (intNumPsgr > 0) && (intNumPTC == 0) )
//      {
//      sCommand.append("/P");
//      for (int i=0; i < vctrPsgrNumList.size(); i++)
//        {
//        sCommand.append((String)vctrPsgrNumList.elementAt(i));
//        sCommand.append(",");
//        } // end for
//      if ( sCommand.toString().endsWith(",") )
//        {
//        sCommand.deleteCharAt(sCommand.length()-1 );
//        }
//      }
//
//    // if we have only received a request to fare a PTC only,
//    // issue a command such as: FXX/RADT
//    else if ( (intNumPsgr == 0) && (intNumPTC == 1) )
//      {
//      sCommand.append("/R");
//      sCommand.append( (String)vctrPTCList.elementAt(0) );
//      }
//
//    // if we have both a passenger list and a PTC list
//    // fare the passenger at each PTC fare
//    // this generates a command such as: FXX/P1/RADT//P2/RC10
//    // where: P1 = psgr 1
//    //       /RADT = fare adult
//    //       //  = passenger separator
//    //       etc...
//    else if ( (intNumPsgr > 0) && (intNumPTC > 0) )
//      {
//      if (intNumPsgr != intNumPTC)
//        {
//        throw new TranServerException (
//            "The number of Passengers is different from the number of Passenger Type Codes");
//        }
//      else
//        {
//        for (int i=0; i < intNumPsgr; i++)
//          {
//          sCommand.append("/P");
//          sCommand.append( (String)vctrPsgrNumList.elementAt(i) );
//          sCommand.append("/R");
//          sCommand.append( (String)vctrPTCList.elementAt(i) );
//          sCommand.append("/");
//          } // end for
//
//        if ( sCommand.toString().endsWith("/") )
//          {
//          sCommand.deleteCharAt(sCommand.length()-1);
//          }
//        } // end else
//
//      } // end else if

    return( sCommand.toString() );

    }  // end getFareCommand


  /**
   ***********************************************************************
   * This method determines which type of format was received from
   * Amadeus, and calls the appropriate parsing routine; see below
   * for more.
   *
   * When a request is made to Amadeus that includes multiple passengers,
   * such as FXX or FXX/P1,3 , Amadeus returns a summary view which
   * itemizes all the passengers but totals most of the charges and does
   * not provide a fare ladder.
   *
   * <p>On the other hand, when a request for a single passenger is made,
   * such as FXX/P1 or FXX/P2/S5,6 , Amadeus returns a detailed view
   * which includes a fare ladder and the itemized taxes for that passenger.
   *
   * <p> This method begins parsing the request to identify which of the
   * two views was returned, and calls the appropriate routine to parse
   * the view returned.
   *
   * @param anInputString
   *   The string response that was returned by Amadeus
   *
   * @param aPNR
   *   the Passenger Name Record corresponding to this transaction
   *
   * @param aRequest
   *   the ReqGetFare request object handling this request
   *
   * @see #readFaresSummary
   * @see #readFaresDetail
   * @see PNR
   * @see ReqGetFare
   ***********************************************************************
   */

  private void readFares(final String aInputString, final PNR aPNR,
                                   final ReqGetFare aRequest)
    {
    try
      {
      final StringTokenizer lines = new StringTokenizer(aInputString,"\r\n");

      // matches the line that precedes the per passenger fare line items
      // where we begin parsing
      final String HEADER_SUMRY = "PASSENGER *PTC *NP *FARE";
      // matches the total line where we stop parsing
      final String HEADER_DETAIL = "AL *FLGT *BK *DATE *TIME *FARE *BASIS";

      String sLine;
      // determine whether we got back a 'Summary' or a 'detailed' response
      while( lines.hasMoreTokens() )
        {
        sLine = lines.nextToken();

        if ( RegExpMatch.matches(sLine,HEADER_SUMRY) )
          {
          readFaresSummary(aInputString, aPNR, aRequest);
          return;
          }

        if ( RegExpMatch.matches(sLine,HEADER_DETAIL) )
          {
          readFaresDetail(aInputString, aPNR, aRequest);
          return;
          }
        } // end while

        aRequest.saveException( new GdsResponseException("Unexpected Format Received","",aInputString) );

      }
    catch (Exception e) {}

    } // end readFares


  /**
   ***********************************************************************
   * Read a fare response from Amadeus that is in Summary format; when a request
   * is made to Amadeus that includes multiple passengers,
   * such as FXX or FXX/P1,3 , Amadeus returns a summary view which
   * itemizes all the passengers but totals most of the charges and does
   * not provide a fare ladder.
   *
   * @see #readFares
   * @see #readFaresDetail
   ***********************************************************************
   */
  private void readFaresSummary(final String aInputString, final PNR aPNR,
                                   final ReqGetFare aRequest)
    {
    try
      {
      final StringTokenizer lines = new StringTokenizer(aInputString,"\r\n");

      // matches the line that precedes the per passenger fare line items
      // where we begin parsing
      final String HEADER = "PASSENGER *PTC *NP *FARE";
      // matches the total line where we stop parsing
      final String TOTALS_LINE = "^ *TOTALS [ 0-9]*";

      int[] arySegmentList = aRequest.getSegmentNumbers();

      String sLine;
      boolean isLineItem = false;

      // we use this vector to store a PNRFare object
      // for each PTC code that is present in the fare response
      Vector vctrPNRFares = new Vector();

      while ( lines.hasMoreTokens() )
        {
        sLine = lines.nextToken();

        if ( RegExpMatch.matches(sLine,HEADER) )
          {
          // set to parse next line
          isLineItem = true;
          continue;
          }

        if ( RegExpMatch.matches(sLine,TOTALS_LINE) )
          {
          // stop parsing
          isLineItem = false;
          break;
          }

       if (isLineItem)
         {

         // extract and store the information that we need from each line
         final String strNativePTC  = sLine.substring(21,24).trim();
         final String strGenericPTC = getGenericPTC(strNativePTC);
         final String strTemp       = sLine.substring(25,30).trim();
         final int intNumPsgrs      = Integer.parseInt( sLine.substring(25,30).trim() );
         final String strFare       = sLine.substring(31,41).trim();
         final String strTax        = sLine.substring(41,49).trim();

         // loop through vctrPNRFares and find out whether
         // we have already stored a PNRFare with this PTC
         boolean isNewPTC = true;
         for (int i=0; i <= vctrPNRFares.size() - 1; i++)
           {
           PNRFare curntPNRFare = (PNRFare)vctrPNRFares.elementAt(i);
           if ( strGenericPTC == curntPNRFare.getGenericPTC() )
             {  // if so, add this line item to it
             curntPNRFare.addFare(intNumPsgrs, strFare, arySegmentList);
             curntPNRFare.addTax(intNumPsgrs, strTax, arySegmentList);
             isNewPTC = false;
             break;
             }
           }

         // if we don't already have a PNRFare with this PTC,
         // create and initialize a new PNRFare, and add it to the vector
         if (isNewPTC)
           {
           PNRFare newFare = new PNRFare(strGenericPTC, strNativePTC);
           newFare.isContract = aRequest.isContract();
           newFare.isExempt   = aRequest.isExempt();
           newFare.isLowest   = aRequest.isLowest();
           newFare.addFare(intNumPsgrs, strFare, arySegmentList);
           newFare.addTax(intNumPsgrs, strTax, arySegmentList);
           vctrPNRFares.addElement(newFare);
           }
         } // end if(isLineItem)

       }  // end while

        // add the PNRFares compiled to the PNR
        PNRFare[] aryPNRFares = new PNRFare[vctrPNRFares.size()];
        vctrPNRFares.toArray(aryPNRFares);
        aPNR.setFares(aryPNRFares);



      // make sure you get some fares back
      final PNRFare[] fareList = aPNR.getFares();
      if ( (fareList instanceof PNRFare[]) == false )
        {
        aRequest.saveException( new GdsResponseException("No fares were returned","",aInputString) );
        }

      }
    catch (Exception e)
      {}

    }   // end readFaresSummary()


  /**
   ***********************************************************************
   * Reads a fare from Amadeus that was returned in Detail format; when a
   * request for a single passenger is made, such as FXX/P1 or FXX/P2/S5,6 ,
   * Amadeus returns a detailed view which includes a fare ladder and the
   * itemized taxes for that passenger.
   *
   * @see #readFares
   * @see #readFaresSummary
   ***********************************************************************
   */
  private void readFaresDetail(final String aInputString, final PNR aPNR,
                                   final ReqGetFare aRequest)
    {
    try
      {
      final StringTokenizer lines = new StringTokenizer(aInputString,"\r\n");
      // match the line with the number of passengers, for example: 01  MARK/BRENT
      final String NUM_PSGRS = "^[0-9]{2} ";

      // match the total fare line, for example: USD  2260.46     23APR01 ...
      final String TOTAL_FARE = "USD +[0-9]*\\.[0-9]{2} *[0-9]{2}[A-Z]{3}[0-9]{2}";

      // match tax line, for example: USD   169.54US
      final String TAX_LINE = "USD +[0-9]*\\.[0-9]{2}[A-Z]{2}";

      // matches the total line where we stop parsing, for example: PAGE  2/2
      final String END_RESP = "PAGE +([0-9])/\1";

      int[] arySegmentList = aRequest.getSegmentNumbers();


      // We are getting a detail view because the request specified
      // one passenger only. Hence, get the  passenger information for the first
      // (and presumably only) passenger on the request.
      final PNRNameElement thePsgr;
      // default to the Adult PTC in case that we can't get it in the following
      String strGenericPTC = "ADT";
      if (aRequest.getNames() instanceof PNRNameElement[])
        {
        thePsgr = aRequest.getNames()[0];
        // try to get the PTC from the request
        if (!thePsgr.PTC.equals(""))
          {
          strGenericPTC = thePsgr.PTC;
          }
        // if that did not work, try to get it from the PNR
        else if (aPNR.getName(thePsgr.getPassengerID()).PTC instanceof String )
          {
          if (!aPNR.getName(thePsgr.getPassengerID()).PTC.equals(""))
            strGenericPTC = aPNR.getName(thePsgr.getPassengerID()).PTC ;
          }
        }  // end if

      final String strNativePTC = getAmadeusPTC(strGenericPTC);

      String sLine;
      PNRFare pnrFare = new PNRFare(strGenericPTC,strNativePTC);
      int intNumPsgrs = 1;
      while ( lines.hasMoreTokens() )
        {
        sLine = lines.nextToken();

        // the number of passengers should be 1, but don't assume anything
        if ( RegExpMatch.matches(sLine,NUM_PSGRS) )
          {
          intNumPsgrs = Integer.parseInt(sLine.substring(1,3).trim());
          continue;
          }

        if ( RegExpMatch.matches(sLine,TOTAL_FARE) )
          {
          final String strTotalBaseFare = sLine.substring(5,12).trim();
          pnrFare.addFare(intNumPsgrs, strTotalBaseFare, arySegmentList);
          continue;
          }

        if ( RegExpMatch.matches(sLine, TAX_LINE) )
          {
          final String strTaxDef = sLine.substring(5,14).trim();
          pnrFare.addTax(intNumPsgrs, strTaxDef, arySegmentList);
          continue;
          }

        if ( RegExpMatch.matches(sLine, END_RESP) )
          break;

       }  // end while

       aPNR.addFare(pnrFare);

      // make sure you get some fares back
      final PNRFare[] fareList = aPNR.getFares();
      if ( (fareList instanceof PNRFare[]) == false )
        {
        aRequest.saveException( new GdsResponseException("No fares were returned","",aInputString) );
        }

      }
    catch (Exception e)
      {}

    }   // end readFaresDetail()


  /**
   ***********************************************************************
   * This method calls {@link getTicketCommand} to generate the
   * <code>TTP</code> command string for ticketing a Passenger Name Record
   * (PNR), and executes the command.
   ***********************************************************************
   */
  public static void issueTicket(final GnrcCrs aCRS, final ReqIssueTicket aRequest) throws Exception
    {
    // retrieve the PNR
    aRequest.pnr = new PNR();
    aCRS.GetPNRAllSegments(aRequest.getLocator(),aRequest.pnr,true);

    // we cannot add the commission on the Ticket command, unlike other GDss
    // hence, we need to add it first on a separate transaction
    boolean isPercent;

    if ( aRequest.CommissionAmount > 0 ) {
      isPercent = false;
      aCRS.AddCommission(aRequest.CommissionAmount, isPercent);
      }
    else {
      isPercent = true;
      aCRS.AddCommission(aRequest.CommissionPercent, isPercent);
      }

    // we cannot add the tour code on the Ticket Command, unlike other GDSs
    // hence, add tour code on a separated transaction
    if ( GnrcFormat.NotNull(aRequest.TourCode) )
      aCRS.AddTourCode(aRequest.TourCode);

    // we cannot add the Endorsement on the Ticket Command, unlike other GDSs
    // hence, add Endorsement on a separated transaction
    if ( GnrcFormat.NotNull(aRequest.EndorsementInfo) )
      aCRS.AddEndorsement(aRequest.EndorsementInfo);

    // we cannot add the Endorsement on the Ticket Command, unlike other GDSs
    // hence, add Endorsement on a separated transaction
    if ( GnrcFormat.NotNull(aRequest.FOP) )
      aCRS.AddFOP(aRequest.FOP);

    // send a Receive From
    aCRS.AddReceiveBy(aRequest.RequestedBy);

    // construct the command for issuing a ticket
    final String sRequest  = getTicketCommand(aRequest);
    String sResponse = aCRS.HostTransaction(sRequest);

    // if we get a continuity warning, as may be the case if the itinerary
    // contains an 'ARNK' segment (itinerary is not continuous) for example,
    // ignore the warning and re-issue the ticketing command
    if ( sResponse.indexOf("CHECK SEGMENT CONTINUITY") >= 0 )
      sResponse = aCRS.HostTransaction(sRequest);

    if ( (sResponse.indexOf("RP/") >= 0 ||
          sResponse.indexOf("OK ETICKET") >= 0)  == false)
      {
      throw new GdsResponseException("Unable to issue ticket",sRequest,sResponse);
      //aCRS.AddReceiveBy(aRequest.RequestedBy);
      //aCRS.EndTransaction();
      }

    } //  end issueTicket


  /**
   ***********************************************************************
   * builds the ticket command based on the input request
   ***********************************************************************
   */
  public static String getTicketCommand(final ReqIssueTicket aRequest)
    throws Exception
    {
      // start with basic issue ticket command
	final StringBuffer sCommand = new StringBuffer();
      sCommand.append("TTP");

	/* per Lori DCL, alway use R,U.  don't append *PTC
         per Samantha Amadues, don't use the PAX or R,U
	if (aRequest.getFareType() == aRequest.FARE_CONTRACT)
        sCommand.append("/PAX/R,U");
	if (aRequest.getFareType() == aRequest.FARE_ALT_CONTRACT)
        sCommand.append("/PAX/R,U"); 

	// format out withholding tax parms
	if ( aRequest.WT_Parms instanceof String[] )
	  for ( int i = 0; i < aRequest.WT_Parms.length; i++ )
        {
        if ( aRequest.WT_Parms[i].length() > 0 )
				  sCommand.append(",WT-" + aRequest.WT_Parms[i]);
				}; */
		
    if ( aRequest.TicketType.equals(aRequest.TICKET_TYPE_ETKT) )
      sCommand.append("/ET");
    else if ( aRequest.TicketType.equals(aRequest.ETKT_TRUE) )
      sCommand.append("/ET");
    else if ( aRequest.TicketType.equals(aRequest.TICKET_TYPE_PAPER) )
      sCommand.append("/PT");
    else if ( aRequest.TicketType.equals(aRequest.ETKT_FALSE) )
      sCommand.append("/PT");

    /* disabled for the time being
    if ( aRequest.printMiniItin )
      sCommand.append("/IMP");

    if ( aRequest.printInvoice )
      sCommand.append("/INV");

   */

    // get the list of Psgrs (PTCs are currently disabled)
    final String sPsgr_PTC = get_Psgr_PTC_Modifier(
        aRequest.getNames(),aRequest.pnr);

    if ( GnrcFormat.NotNull(sPsgr_PTC) )
      sCommand.append(sPsgr_PTC);

    // get the list of selected segments
    final String sSegList = getSegmentList( aRequest.getSegments(), aRequest.pnr );
    if ( GnrcFormat.NotNull(sSegList) )
      {
      sCommand.append("/S");
      sCommand.append(sSegList);
      }

    /*
    // set the validating carrier
    if ( GnrcFormat.NotNull(aRequest.ValidatingCarrier) )
      sCommand.append("/V" + aRequest.ValidatingCarrier);

    // set the form of payment
    if ( GnrcFormat.NotNull(aRequest.FOP) )
      sCommand.append("/FP" + aRequest.FOP);
    */

		System.out.println("AmadeusGetPNRFareConversation.getTicketCommand: " + sCommand.toString());
		return( sCommand.toString() );
    }

	/**
   ***********************************************************************
   * builds the FXP fare command based on the input request
   ***********************************************************************
   */
  public static String getFXPFareCommand(final ReqIssueTicket aRequest)
    throws Exception
    {
    // start with basic issue ticket command
		final StringBuffer sCommand = new StringBuffer();
    sCommand.append("FXP");

		// per Lori DCL, alway use R,U.  don't append *PTC
		if (aRequest.getFareType() == aRequest.FARE_CONTRACT)
      sCommand.append("/R,U");
		if (aRequest.getFareType() == aRequest.FARE_ALT_CONTRACT)
      sCommand.append("/R,U");

		// format out withholding tax parms take out from TTP added to FXP
	if ( aRequest.WT_Parms instanceof String[] )
	  for ( int i = 0; i < aRequest.WT_Parms.length; i++ )
        {
        if ( aRequest.WT_Parms[i].length() > 0 )
				  sCommand.append(",WT-" + aRequest.WT_Parms[i]);
				};

		// get the list of Psgrs (PTCs are currently disabled)
    final String sPsgr_PTC = get_Psgr_PTC_Modifier(
        aRequest.getNames(),aRequest.pnr);

    if ( GnrcFormat.NotNull(sPsgr_PTC) )
      sCommand.append(sPsgr_PTC);

    // get the list of selected segments
    final String sSegList = getSegmentList( aRequest.getSegments(), aRequest.pnr );
    if ( GnrcFormat.NotNull(sSegList) )
      {
        sCommand.append("/S");
        sCommand.append(sSegList);
      }
		System.out.println("AmadeusGetPNRFareConversation.getFXPFareCommand: " + sCommand.toString());
		return( sCommand.toString() );
    }

	/**
   ***********************************************************************
   * builds the ticket command based on the input request
   ***********************************************************************
   */
  public static String getInfTktCommand(final ReqIssueTicket aRequest)
    throws Exception
    {
    // start with basic issue ticket command
		final StringBuffer sCommand = new StringBuffer();

		// get the list of Psgrs w/ infant no seat tkt
    final String sPsgr_INF = get_Psgr_wINF(
        aRequest.getNames(),aRequest.pnr,aRequest.Req_RawPsgrId);

		if ( GnrcFormat.NotNull(sPsgr_INF) )
		{
			sCommand.append("TTP");
			sCommand.append(sPsgr_INF);
		}

		System.out.println("AmadeusGetPNRFareConversation.getInfTktCommand: " + sCommand.toString());
		return( sCommand.toString() );
    }

	/**
   ***********************************************************************
   * Given an array of PNRItinAirSegment in a ticket or fare request that
   * contains the relative position of segments within the PNR, and the PNR in
   * which the segments are contained, this method generates a
   * generates a comma-separated list of selected segments with the line number
   * of the air segment in the PNR; for example, sending the array {1,3,5} to
   * get the numbers of the first, third and fifth air segment on the pnr may
   * return: 4,6,8 in the case where the first air segments appears on the 4th
   * line of the PNR
   *
   * @param aSegmentList
   *   an array of PNRItinAirSegment  objects that corresponds
   *   to the segments to be fared
   *
   * @see  PNRItinAirSegment
   ***********************************************************************
   */
  protected static String getSegmentList(
      final PNRItinAirSegment[] aSegmentList, PNR pnr)
    throws TranServerException
    {
    final StringBuffer sSegList = new StringBuffer();

    if ( aSegmentList instanceof PNRItinAirSegment[] )
      {
      PNRItinAirSegment[] pnrAirSegs = pnr.getItinAirSegments();
      for ( int i = 0; i < aSegmentList.length; i++ )
        {
        if ( aSegmentList[i].SegmentNumber > 0 )
          {
          int segIndex = aSegmentList[i].SegmentNumber - 1;
          int segNum = -1;
          try {
            segNum = pnrAirSegs[segIndex].SegmentNumber;
            }
          catch (ArrayIndexOutOfBoundsException  obe) {
            throw new TranServerException(
                "The segment number that you provided: '" +
                aSegmentList[i].SegmentNumber +
                "' does not exist in the PNR");
            }
          sSegList.append(pnrAirSegs[segIndex].SegmentNumber + ",");
          }
        }

      if ( sSegList.toString().endsWith(",") )
        sSegList.delete( sSegList.length() - 1, sSegList.length() );
      }

    if ( sSegList.length() > 0 )
      return( sSegList.toString() );
    else
      return(null);
    }

 /*
 ***********************************************************************
   * getFXPSegmentList
   * From the getpnr, read through the pnr for all air segments and 
   *  format out the segment number for the FXP faring command
   *
   * @param apnr   
   *
   * @see  getSegmentList
   ***********************************************************************
 */ 
   protected static String getFXPSegmentList(PNR pnr) throws TranServerException
    {
    final StringBuffer sSegList = new StringBuffer();

    
      PNRItinAirSegment[] pnrAirSegs = pnr.getItinAirSegments();
      if (pnrAirSegs instanceof PNRItinAirSegment[] )
      {
          for (int i = 0; i < pnrAirSegs.length; i++)
          {
              final PNRItinAirSegment airSeg = pnrAirSegs[i];
              if (airSeg.SegmentNumber > 0)
              {
                  sSegList.append(airSeg.SegmentNumber + ",");   
              }
          }
          
          // remove the trailing comma
          if ( sSegList.toString().endsWith(",") )
          {
              sSegList.delete( sSegList.length() - 1, sSegList.length() );
          }
      }


    if ( sSegList.length() > 0 )
      return( sSegList.toString() );
    else
      return(null);
    }
 
  /**
   ***********************************************************************
   * Returns a vector of strings containing the number of the selected passengers,
   * where the numbers correspond to the passenger numbers on the appropriate
   * Passenger Namer Record (PNR).
   *
   * @param aPsgrList
   *   an array of PNRNameElement  objects that corresponds
   *   to the passengers to be fared
   *
   * @param aPNR
   *   the PNR object corresponding to the appropriate Passenger Name Record
   *
   * @see  PNR
   * @see  PNRNameElement
   ***********************************************************************
   */
  protected static Vector getPsgrNumList(
      final PNRNameElement[] aPsgrList, final PNR aPNR)
    {
    // a request from Airware contains at most 20 passenger IDs
    final Vector vctrPsgrNumList = new Vector();

    if ( aPsgrList instanceof PNRNameElement[] )
      {
      PNRNameElement name;
      for ( int i = 0; i < aPsgrList.length; i++ )
        {
        try
          {
          name = aPNR.getName(aPsgrList[i].getPassengerID());

          if ( name instanceof PNRNameElement )
            {
            int iPsgrNum = aPNR.getPsgrNum(name.getPassengerID());
            vctrPsgrNumList.addElement( String.valueOf(iPsgrNum) );
            }
          }
        catch (Exception e)
          {}
        }
      }
      return(vctrPsgrNumList);
    }

	/**
   ***********************************************************************
   * Returns a vector of strings containing the number of the selected passengers,
   * where the numbers correspond to the passenger numbers on the appropriate
   * Passenger Namer Record (PNR).
   *
   * @param aPsgrList
   *   an array of PNRNameElement  objects that corresponds
   *   to the passengers to be fared
   *
   * @param aPNR
   *   the PNR object corresponding to the appropriate Passenger Name Record
   *
   * @see  PNR
   * @see  PNRNameElement
   ***********************************************************************
   */
  protected static Vector getStrictPsgrNumList(
      final PNRNameElement[] aPsgrList, final PNR aPNR)
    {
		// a request from Airware contains at most 20 passenger IDs
    final Vector vctrPsgrNumList = new Vector();

    if ( aPsgrList instanceof PNRNameElement[] )
      {
      PNRNameElement name;
      for ( int i = 0; i < aPsgrList.length; i++ )
        {
        try
          {
          name = aPNR.getName(aPsgrList[i].getPassengerID());

          if ( name instanceof PNRNameElement )
            {
            int iPsgrNum = aPNR.getPsgrNum(name.getStrictPassengerID());
            vctrPsgrNumList.addElement( String.valueOf(iPsgrNum) );
            }
          }
        catch (Exception e)
          {}
        }
      }

			return(vctrPsgrNumList);
    }

	/**
   ***********************************************************************
   * Generates a list of Amadeus PTC codes for the listed passengers
   ***********************************************************************
   */
  private static Vector getPTCList(final PNRNameElement[] aNameList)
    {
    // a request from Airware contains at most 20 Passenger Type Codes
    final Vector vctrPTCList = new Vector();

    // convert each generic PTC into its corresponding Amadeus PTC
    String sGenericPTC;
    String sAmadeusPTC;

    if ( aNameList instanceof PNRNameElement[] )
      {
      for ( int i = 0; i < aNameList.length; i++ )
        {
        sGenericPTC = aNameList[i].PTC;
        sAmadeusPTC = getAmadeusPTC(sGenericPTC);
        if ( GnrcFormat.NotNull(sAmadeusPTC) )
          {
          vctrPTCList.addElement( getAmadeusPTC(sGenericPTC) );
          }
        } // end for
      }

      return(vctrPTCList);

    } // end getPTCList

  /**
   ***********************************************************************
   * Generates a list of Amadeus Passenger Type Codes (PTC) codes for the
   * listed passengers, to be used, for example, when creating the ticketing
   * string
   ***********************************************************************
   */
  private static String get_Psgr_PTC_Modifier(
      final PNRNameElement[] aNameList, final PNR pnr) throws Exception
    {
    // get the list of selected passengers and Passenger Type Codes
    final Vector vctrPsgrNumList = getPsgrNumList(aNameList,pnr);
    final int intNumPsgr = vctrPsgrNumList.size();
    //final Vector vctrPTCList = getPTCList(aNameList);
    //final int intNumPTC  = vctrPTCList.size();
    final int intNumPTC  = 0;

    StringBuffer sModifier = new StringBuffer();

    // if we have only received a passenger list without their PTCs
    // issue a modifier such as: /P1,3
    if ( (intNumPsgr > 0) && (intNumPTC == 0) )
      {
      sModifier.append("/P");
      for (int i=0; i < vctrPsgrNumList.size(); i++)
        {
        sModifier.append((String)vctrPsgrNumList.elementAt(i));
        sModifier.append(",");
        } // end for
      if ( sModifier.toString().endsWith(",") )
        {
        sModifier.deleteCharAt(sModifier.length()-1 );
        }
      }

    /*
    // if we have only received a request to fare a PTC only,
    // issue a modifier such as: /RADT
    else if ( (intNumPsgr == 0) && (intNumPTC == 1) )
      {
      sModifier.append("/R");
      sModifier.append( (String)vctrPTCList.elementAt(0) );
      }

    // if we have both a passenger list and a PTC list
    // fare the passenger at each PTC fare
    // this generates a modifier such as: /P1/RADT//P2/RC10
    // where: P1 = psgr 1
    //       /RADT = fare adult
    //       //  = passenger separator
    //       etc...
    else if ( (intNumPsgr > 0) && (intNumPTC > 0) )
      {
      if (intNumPsgr != intNumPTC)
        {
        throw new TranServerException (
            "The number of Passengers is different from the number of Passenger Type Codes");
        }
      else
        {
        for (int i=0; i < intNumPsgr; i++)
          {
          sModifier.append("/P");
          sModifier.append( (String)vctrPsgrNumList.elementAt(i) );
          sModifier.append("/R");
          sModifier.append( (String)vctrPTCList.elementAt(i) );
          sModifier.append("/");
          } // end for

        if ( sModifier.toString().endsWith("/") )
          {
          sModifier.deleteCharAt(sModifier.length()-1);
          }
        } // end else
      } // end else if
    */

    return(sModifier.toString());

    } // end get_Psgr_PTC_Modifier

	/**
   ***********************************************************************
   * Generates a list of Amadeus Passenger Indexes and INF for the
   * listed passengers, to be used, for example, when creating the ticketing
   * string
   ***********************************************************************
   */
  private static String get_Psgr_wINF(
      final PNRNameElement[] aNameList, final PNR pnr, final String[] aReq_RawPsgrId) throws Exception
    {
		boolean foundInf = false;

		//PNRNameElement name;
		PNRNameElement name = new PNRNameElement();

		// get the list of selected passengers
    final Vector vctrPsgrNumList = getStrictPsgrNumList(aNameList,pnr);
    final int intNumPsgr = vctrPsgrNumList.size();

    StringBuffer sModifier = new StringBuffer();

    // if we have only received a passenger list
    // issue a modifier such as: /P1,3
		if ( intNumPsgr > 0 )
      {
        for (int i=0; i < vctrPsgrNumList.size(); i++)
        {
				// determine if this psgr has a ticketable infant
				System.out.println("AmadeusGetPNRFareConversation.get_Psgr_wINF: i/aNameList len: " + i + "/" + aNameList.length);
				System.out.println("AmadeusGetPNRFareConversation.get_Psgr_wINF: aReq_RawPsgrId[i]/length " + aReq_RawPsgrId[i] + "/" + aReq_RawPsgrId[i].length());

				if ( aReq_RawPsgrId[i].length() >= 20 && aReq_RawPsgrId[i].substring(17,20).toUpperCase().equals("INF") )
				  {
					if ( !foundInf )
					  {
						foundInf = true;
						sModifier.append("/INF/P");
					  }
					sModifier.append((String)vctrPsgrNumList.elementAt(i));
				  sModifier.append(",");
					}
        } // end for

			if ( sModifier.toString().endsWith(",") )  sModifier.deleteCharAt(sModifier.length()-1 );
      }

    return(sModifier.toString());

    } // end get_Psgr_wINF

	/**
   ***********************************************************************
   * Converts an Amadeus Passenger Type Code (PTC) into a generic Airware PTC
   *
   ***********************************************************************
   */
  public static String getGenericPTC(final String anAmadeusPTC)
    {
    // infant PTCs used by Amadeus
    final String[] INFANT_PTCS    = {"INF"};

    // child PTCs used by Amadeus, match string such as C08 or CNN
    final String CHILD_PTC     = "CHD|C[0-9][0-9]|CNN";

    try
      {
      if ( isListed(anAmadeusPTC,INFANT_PTCS) )
        return("INF");
      else if ( RegExpMatch.matches(anAmadeusPTC,CHILD_PTC) )
        return("CHD");
      else
        return("ADT");
      }
    catch (Exception e)
      {return("ADT");}
    }

  /**
   ***********************************************************************
   * Converts a generic Passenger Type Code into an Amadeus specific PTC.
   *
   * @return
   *   A string character Passenger Type code.
   *   This is a very simple function that maps the airware Passenger Type
   *   Codes ADT and INF to ADT and INF, respectively, in Amadeus. <br>
   *   Amadeus Child PTC is of the form C<NN> where NN represents the age of the
   *   child.  In this case we return a hard-coded C10, as we have no way
   *   of knowing the age of the child at this time.
   ***********************************************************************
   */
  protected static String getAmadeusPTC(final String aGenericPTC)
    {
    if( GnrcFormat.NotNull(aGenericPTC) )
      {
      // Amadeus child PTC is of the form C<NN> where NN represents the age
      // of the child
      if (aGenericPTC == "CHD")
        return ("C10");
      else if (aGenericPTC == "INF")
        return ("INF");
      else
        return ("ADT");
      }
    return(null);
    }

  /**
   ***********************************************************************
   * Returns true if the given search string is listed in the array
   * of strings
   ***********************************************************************
   */
  private static boolean isListed(final String aSearchString, final String[] aList)
    {
    return(false);
    }

}  // end AmadeusGetPNRFareConversation
