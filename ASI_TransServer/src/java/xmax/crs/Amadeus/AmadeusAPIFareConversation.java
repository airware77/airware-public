package xmax.crs.Amadeus;

import xmax.TranServer.GnrcFormat;
import xmax.TranServer.ReqGetFare;
import xmax.TranServer.ReqIssueTicket;
import xmax.TranServer.TranServerException;

import xmax.crs.PNR;
import xmax.crs.PNRFare;
import xmax.crs.GdsResponseException;
import xmax.crs.GetPNR.PNRItinAirSegment;
import xmax.crs.GetPNR.PNRNameElement;
import xmax.crs.GetPNR.PNRRemark;

import xmax.util.xml.DOMutil;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.apache.xerces.dom.DocumentImpl;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.StringTokenizer;
import xmax.util.RegExpMatch;

/**
 ***********************************************************************
 * This class contains the methods used to build a faring request, and parse a
 * faring response from the Amadeus API.
 *
 * @author   Philippe Paravicini
 * @version  $Revision: 14$ - $Date: 2/13/2003 7:04:36 PM$
 *
 * @see
 ***********************************************************************
 */
public class AmadeusAPIFareConversation
{
  /**
   ***********************************************************************
   * The method called by {@link AmadeusAPICrs} to execute a faring
   * conversation
   ***********************************************************************
   */
  public static void getFareForPNR (final AmadeusAPICrs aCRS,
                                    final ReqGetFare aRequest) throws Exception
    {
    // retrieve PNR if needed
    if ( GnrcFormat.IsNull( aRequest.pnr.getPNRData() ) )
      {
      aCRS.Ignore();
      aCRS.GetPNRAllSegments(aRequest.getLocator(),aRequest.pnr,true);
      }

    // get the appropriate fare request
    Document domQuery;
    if (aRequest.getFareType().equals(ReqGetFare.FARE_LOWEST))
      //domQuery = buildQuery_DisplayLowestApplicable(aRequest);
      domQuery = build_PricePNRwithLowerFares(aRequest);
    else if (aRequest.getFareType().equals(ReqGetFare.FARE_ALT_CONTRACT))
      domQuery = build_PricePNRWithBookingClass_ALT(aRequest);
		else
			//domQuery = buildQuery_PriceItinerary(aRequest);
			domQuery = build_PricePNRWithBookingClass(aRequest);

    final Document domReply = aCRS.connection.sendAndReceive(domQuery);

    // parse the response
		readFares(domReply,aRequest);

	} // end getFareForPNR


  /**
   ***********************************************************************
   * This method builds a query to issue a
   * <code>FarePlus_PriceItinerary_Query</code> to the Amadeus API Host; it is
   * the equivalent of a cryptic <code>FQQ</code> query, and is the closest
   * thing to a <code>FXX</code> or an <code>FXP</code>.
   ***********************************************************************
   */
  public static Document buildQuery_PriceItinerary(ReqGetFare aRequest)
    throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element root = domQuery.createElement("FarePlus_PriceItinerary_Query");
    domQuery.appendChild(root);

    // specify whether we are storing this fare in the PNR
    if (aRequest.isStored())
      DOMutil.addTextElement(root,"TSTFlag","T");
    else
      DOMutil.addTextElement(root,"TSTFlag","F");

    // this node is misleading; according to the documentation, it is the
    // equivalent to an '/LO' switch in an FQQ query
    // Defect Alert! when setting this flag to 'F', we get a blank Ticket_ImagePlus_Reply
    DOMutil.addTextElement(root,"LowestFareOption","T");

    // This node is necessary to prevent 'UNABLE TO FARE - PRICE MANUALLY'
    // errors, code "18", which are the result of Amadeus memory overruns.
    // This node is the equivalent of the 'R,*PTC' modifier, as in FXX/R,*PTC,
    // which forces the system to price the PTCs specified in the PNR, rather
    // than to search all fare data
    DOMutil.addTextElement(root,"OtherRestrictions","PTC");

    appendNode_CAPI_FXXSegmentPlus(domQuery, aRequest);
    appendNode_CAPI_FXXPsgrPlus(domQuery, aRequest);
    appendNode_PricingTicketingIndicator(domQuery);

    return domQuery;

    } // end buildQuery_PriceItinerary


	 /**
   ***********************************************************************
   * This method builds a query to issue a
   * <code>PoweredFare_PricePNRWithBookingClass</code> to the Amadeus API Host;
   * it is the equivalent of a FarePlus_PriceItinerary_Query.
   ***********************************************************************
   */
  public static Document build_PricePNRWithBookingClass(ReqGetFare aRequest)
    throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element root = domQuery.createElement("PoweredFare_PricePNRWithBookingClass");
    domQuery.appendChild(root);
    appendNode_paxSegReference(domQuery, aRequest);

    Element elml = domQuery.createElement("overrideInformation");

    Element elmll = domQuery.createElement("attributeDetails");
		//DOMutil.addTextElement(domQuery, elmll,"attributeType","RP");
		//elml.appendChild(elmll);
    //elmll = domQuery.createElement("attributeDetails");
    DOMutil.addTextElement(domQuery, elmll,"attributeType","RU");
    elml.appendChild(elmll);
    //elmll = domQuery.createElement("attributeDetails");
		//DOMutil.addTextElement(domQuery, elmll,"attributeType","RLO");
		//elml.appendChild(elmll);

		root.appendChild(elml);
    //appendNode_paxSegReference(domQuery, aRequest);

		System.out.println("AmadeusAPIFareConversation.build_PricePNRWithBookingClass");
	  // get the current number of additional parameters
    final int iNumWT_Parms;
    if ( aRequest.WT_Parms instanceof String[] )
      iNumWT_Parms = aRequest.WT_Parms.length;
    else
      iNumWT_Parms = 0;

		// Withhold Canadian Tax
		if (iNumWT_Parms > 0)
		{
		System.out.println("AmadeusAPIFareConversation.build_PricePNRWithBookingClass: <"+ aRequest.WT_Parms.length + ">");
		elml = domQuery.createElement("taxDetails");
		DOMutil.addTextElement(domQuery, elml,"taxQualifier","7");
		elmll = domQuery.createElement("taxIdentification");
		DOMutil.addTextElement(domQuery, elmll,"taxIdentifier","WHT");
		elml.appendChild(elmll);
		elmll = domQuery.createElement("taxType");
		DOMutil.addTextElement(domQuery, elmll,"isoCountry","CA");
		elml.appendChild(elmll);
		for ( int i = 0; i < iNumWT_Parms; i++ )
      {
			System.out.println("AmadeusAPIFareConversation.build_PricePNRWithBookingClass: loop: <" + aRequest.WT_Parms[i] + ">");
			if ( aRequest.WT_Parms[i].length() > 0 )
				DOMutil.addTextElement(domQuery, elml,"taxNature",aRequest.WT_Parms[i]);
      }
		root.appendChild(elml);
		}

		System.out.println("build_PricePNRWithBookingClass: ");
    return domQuery;

    } // end build_PricePNRWithBookingClass

	/**
   ***********************************************************************
   * This method builds a query to issue a
   * <code>PoweredFare_PricePNRWithBookingClass</code> to the Amadeus API Host;
	 * WITHOUT RU or PTC
   * it is the equivalent of a FarePlus_PriceItinerary_Query.
   ***********************************************************************
   */
  public static Document build_PricePNRWithBookingClass_ALT(ReqGetFare aRequest)
    throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element root = domQuery.createElement("PoweredFare_PricePNRWithBookingClass");
    domQuery.appendChild(root);
    appendNode_paxSegReference(domQuery, aRequest);

    Element elml = domQuery.createElement("overrideInformation");

    Element elmll = domQuery.createElement("attributeDetails");
    //DOMutil.addTextElement(domQuery, elmll,"attributeType","NOP");
		DOMutil.addTextElement(domQuery, elmll,"attributeType","RU");
    elml.appendChild(elmll);
		root.appendChild(elml);

		System.out.println("AmadeusAPIFareConversation.build_PricePNRWithBookingClass_ALT");
	  // get the current number of additional parameters
    final int iNumWT_Parms;
    if ( aRequest.WT_Parms instanceof String[] )
      iNumWT_Parms = aRequest.WT_Parms.length;
    else
      iNumWT_Parms = 0;

		// Withhold Canadian Tax
		if (iNumWT_Parms > 0)
		{
		System.out.println("AmadeusAPIFareConversation.build_PricePNRWithBookingClass_ALT: <"+ aRequest.WT_Parms.length + ">");
		elml = domQuery.createElement("taxDetails");
		DOMutil.addTextElement(domQuery, elml,"taxQualifier","7");
		elmll = domQuery.createElement("taxIdentification");
		DOMutil.addTextElement(domQuery, elmll,"taxIdentifier","WHT");
		elml.appendChild(elmll);
		elmll = domQuery.createElement("taxType");
		DOMutil.addTextElement(domQuery, elmll,"isoCountry","CA");
		elml.appendChild(elmll);
		for ( int i = 0; i < iNumWT_Parms; i++ )
      {
			System.out.println("AmadeusAPIFareConversation.build_PricePNRWithBookingClass_ALT: loop: <" + aRequest.WT_Parms[i] + ">");
			if ( aRequest.WT_Parms[i].length() > 0 )
				DOMutil.addTextElement(domQuery, elml,"taxNature",aRequest.WT_Parms[i]);
      }
		root.appendChild(elml);
		}

		System.out.println("build_PricePNRWithBookingClass_ALT: ");
    return domQuery;

    } // end build_PricePNRWithBookingClass_ALT


  /**
   ***********************************************************************
   * This method issues a <code>FarePlus_DisplayLowestApplicable_Query</code>
   * to the Amadeus API Host; it is the equivalent to an <code>FXA</code>
   * cryptic call; the PassengerSelect feature of this query is disabled
   * because it fails when trying to fare PNRs with contract fares
   ***********************************************************************
   */
  public static Document buildQuery_DisplayLowestApplicable(ReqGetFare aRequest)
    throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element root = domQuery.createElement("FarePlus_DisplayLowestApplicableFare_Query");
    domQuery.appendChild(root);

    if (aRequest.isStored())
      DOMutil.addTextElement(root,"TSTFlag","T");
    else
      DOMutil.addTextElement(root,"TSTFlag","F");

    appendNode_CAPI_FXXSegmentPlus(domQuery, aRequest);
    //appendNode_PricingTicketingIndicator(domQuery);

    return domQuery;

    } // end buildQuery_PriceItinerary


	/**
   ***********************************************************************
   * This method issues a <code>PoweredFare_PricePNRwithLowerFares/code>
   * to the Amadeus API Host; it is the equivalent to
   * <code>FarePlus_DisplayLowestApplicableFare_Query</code>;
   * the PassengerSelect feature of this query is disabled
   * because it fails when trying to fare PNRs with contract fares
   ***********************************************************************
   */
  public static Document build_PricePNRwithLowerFares(ReqGetFare aRequest)
    throws Exception
    {
    Document domQuery = new DocumentImpl();
    Element root = domQuery.createElement("PoweredFare_PricePNRWithLowerFares");
    domQuery.appendChild(root);

    appendNode_paxSegReference(domQuery, aRequest);

    Element elml = domQuery.createElement("overrideInformation");
    Element elmll = domQuery.createElement("attributeDetails");

    DOMutil.addTextElement(elmll,"attributeType","NOP");

    elml.appendChild(elmll);
    root.appendChild(elml);
    //appendNode_paxSegReference(domQuery, aRequest);

    System.out.println("build_PricePNRwithLowerFares: ");
    return domQuery;

    } // end build_PricePNRwithLowerFares


  /**
   ***********************************************************************
   * In the event that the fare requests specifies that we fare a specific
   * subset of the segments in the Passenger Name Record (PNR), this method appends
   * as many <code>CAPI_FXXSegmentPlus</code> as there are segments to fare;
   * The segment numbers provided in the request indicate the relative order in
   * which the air segments appear in the PNR, not necessarely their line
   * number in the PNR; Hence, we must first retrieve the air segments
   * specified in the request, and then extract their corresponding line number
   * as it appears in the PNR.
   ***********************************************************************
   */
  public static void appendNode_CAPI_FXXSegmentPlus(Document domQuery, ReqGetFare aRequest)
    throws Exception
    {
    // if we are doing a segment select
    final int[] arySegmentNum = aRequest.getSegmentNumbers();
    if (arySegmentNum instanceof int[])
      {
      Element root = domQuery.getDocumentElement();
      final PNRItinAirSegment[] aryAirSegs = aRequest.pnr.getItinAirSegments();

      for (int i=0; i < arySegmentNum.length; i++)
        {
        Element el1 = domQuery.createElement("CAPI_FXXSegmentPlus");
        root.appendChild(el1);
        int iSegPosition = arySegmentNum[i] - 1;
        int iLineNum = -1;

        try {
          iLineNum = aryAirSegs[iSegPosition].SegmentNumber;
          }
        catch (ArrayIndexOutOfBoundsException obe) {
          throw new TranServerException(
              "The segment number that you provided: '" + arySegmentNum[i] +
              "' does not exist in the PNR");
          }

        DOMutil.addTextElement(el1,"Segment_Nb",iLineNum);
        }
      }

    } // end appendNode_CAPI_FXXSegmentPlus

  /**
   ***********************************************************************
   * In the event that the fare requests specifies that we fare a specific
   * subset of the segments in the Passenger Name Record (PNR), this method appends
   * as many <code>FlightInformation</code> as there are segments to fare;
   * The segment numbers provided in the request indicate the relative order in
   * which the air segments appear in the PNR, not necessarely their line
   * number in the PNR; Hence, we must first retrieve the air segments
   * specified in the request, and then extract their corresponding line number
   * as it appears in the PNR.
   ***********************************************************************
   */
  public static void appendNode_FlightInformation(Document domQuery, ReqGetFare aRequest)
    throws Exception
    {
    System.out.println("appendNode_FlightInformation:Begin:");
    // if we are doing a segment select
    final int[] arySegmentNum = aRequest.getSegmentNumbers();
    if (arySegmentNum instanceof int[])
      {
      Element root = domQuery.getDocumentElement();
      Element el = domQuery.createElement("FlightInformation");
      root.appendChild(el);
      Element ell = domQuery.createElement("itinerarySegReference");
      el.appendChild(ell);

      final PNRItinAirSegment[] aryAirSegs = aRequest.pnr.getItinAirSegments();

      for (int i=0; i < arySegmentNum.length; i++)
        {
        Element el1l = domQuery.createElement("refDetails");
        int iSegPosition = arySegmentNum[i] - 1;
        int iLineNum = -1;
        System.out.println("appendNode_FlightInformation:iSegPosition: " + iSegPosition);
        try {
          iLineNum = aryAirSegs[iSegPosition].SegmentNumber;
          }
        catch (ArrayIndexOutOfBoundsException obe) {
          throw new TranServerException(
              "The segment number that you provided: '" + arySegmentNum[i] +
              "' does not exist in the PNR");
          }
        DOMutil.addTextElement(el1l,"refQualifier","S");
        DOMutil.addTextElement(el1l,"refNumber",iLineNum);
        System.out.println("appendNode_FlightInformation:iLineNum: " + iLineNum);
        ell.appendChild(el1l);
        }
      }
    System.out.println("appendNode_FlightInformation:End:");
    } // end appendNode_FlightInformation

  /**
   ***********************************************************************
   * In the event that the fare requests specifies that we fare a specific
   * subset of the segments in the Passenger Name Record (PNR), this method appends
   * as many <code>paxSegReference</code> as there are segments to fare;
   * The segment numbers provided in the request indicate the relative order in
   * which the air segments appear in the PNR, not necessarely their line
   * number in the PNR; Hence, we must first retrieve the air segments
   * specified in the request, and then extract their corresponding line number
   * as it appears in the PNR.
   ***********************************************************************
   */
  public static void appendNode_paxSegReference(Document domQuery, ReqGetFare aRequest)
    throws Exception
    {
    System.out.println("appendNode_paxSegReference:Begin:");
    // if we are doing a segment select
    final int[] arySegmentNum = aRequest.getSegmentNumbers();
		//final int[] arySegmentNum = aRequest.getrefNumbers();
    if (arySegmentNum instanceof int[])
      {
      Element root = domQuery.getDocumentElement();
      Element el = domQuery.createElement("paxSegReference");
      root.appendChild(el);

      final PNRItinAirSegment[] aryAirSegs = aRequest.pnr.getItinAirSegments();

      for (int i=0; i < arySegmentNum.length; i++)
        {
        Element el1 = domQuery.createElement("refDetails");
        int iSegPosition = arySegmentNum[i] - 1;
        int iLineNum = -1;
				String sTemp;
        System.out.println("appendNode_paxSegReference:iSegPosition: " + iSegPosition);
        try {
					sTemp = aryAirSegs[iSegPosition].CrsSegmentID;
					iLineNum  = Integer.parseInt(sTemp.trim());
          }
        catch (ArrayIndexOutOfBoundsException obe) {
          throw new TranServerException(
              "The segment number that you provided: '" + arySegmentNum[i] +
              "' does not exist in the PNR");
          }

        DOMutil.addTextElement(el1,"refQualifier","S");
        DOMutil.addTextElement(el1,"refNumber",iLineNum);
        System.out.println("appendNode_paxSegReference:iLineNum: " + iLineNum);
        el.appendChild(el1);
        }
      }
    System.out.println("appendNode_paxSegReference:End:");
    } // end appendNode_paxSegReference

  /**
   ***********************************************************************
   * In the event that the fare requests specifies that we fare a specific
   * subset of the passengers in the Passenger Name Record (PNR), this method
   * appends as many <code>CAPI_FXXPsgrPlus</code> as there are passengers to
   * fare;
   ***********************************************************************
   */
  public static void appendNode_CAPI_FXXPsgrPlus(Document domQuery, ReqGetFare aRequest)
    {
    // if we are doing a passenger and/or PTC select
    PNRNameElement[] aryPsgr = aRequest.getNames();
    if (aryPsgr instanceof PNRNameElement[])
      {
      Element root = domQuery.getDocumentElement();
      for (int i=0; i < aryPsgr.length; i++)
        {
        // add the passenger to be fared, if available
        if (GnrcFormat.NotNull(aryPsgr[i].getPassengerID()))
          {
          Element el1 = domQuery.createElement("CAPI_FXXPsgrPlus");
          root.appendChild(el1);

          String sNum = (aRequest.pnr.getPsgrNum(aryPsgr[i].getPassengerID())) + "";
          DOMutil.addTextElement(el1,"PassengerNumber",sNum);
          }
        /*
        // The following makes the faring request fail when using contract PTC codes
        // add the PTC to be fared, if available
        if (aryPsgr[i].PTC instanceof String &&
            aryPsgr[i].PTC.length() > 0)
          {
          String sPTC =
            AmadeusGetPNRFareConversation.getAmadeusPTC(aryPsgr[i].PTC);
          DOMutil.addTextElement(el1,"PTC1",sPTC);
          }
        */
        }
      }
    } // end appendNode_CAPI_FXXPsgrPlus


  /**
   ***********************************************************************
   * <p>This method was added for Non Standard Private Fares. Node is
   * <code>CAPI_TicketingIndicatorsPlus</code>
   * <code>PricingTicketingIndicator</code>
   * <code>RU (where RU indicates Unifares)</code>
   * </p>

   ***********************************************************************
   */
  public static void appendNode_PricingTicketingIndicator(Document domQuery)
    {
      Element root = domQuery.getDocumentElement();
      Element elml = domQuery.createElement("CAPI_TicketingIndicatorsPlus");
      root.appendChild(elml);
      //Element elmll = domQuery.createElement("PricingTicketingIndicator");
      DOMutil.addTextElement(elml,"PricingTicketingIndicator","RU");
      //elml.appendChild(elmll);
    } // end appendNode_PricingTicketingIndicator


  /**
   ***********************************************************************
   * <p>This method retrieves the fare groups from a
   * <code>Ticket_ImageReply_Plus</code> and reads the fares contained therein;
   * in the case of a response to a
   * <code>FarePlus_Display_LowestApplicable_Query</code>, things get a little
   * more complicated, as indicated below.</p>
   *<p>
   * DisplayLowestApplicable query cannot be restricted to a partial list of
   * passengers on the PNR.  In the case of an environment with Fare Contracts,
   * they may also return more FareGroups than there are PTC groups in the PNR:
   * the contract fare groups in addition to lower published fare groups, as
   * follows.</p>
   * Assuming that a PNR contains five passengers, 2 children (CHD) A and
   * B, and 3 adults C,D and E.  A DisplayLowest may return:
   * <ul>
   *  <li>most common scenario: the contract fares are the lowest,
   *    DisplayLowest returns:<br/>
   *      AB  : contract, return this<br/>
   *      CDE : contract, return this
   *  </li>
   *  <li>there are contract fares for each PTC group, DisplayLowest
   *    returns:<br/>
   *      AB  : contract<br/>
   *      CDE : contract<br/>
   *      AB  : lower CHD published fare, return this<br/>
   *      CDE : lower ADT published fare, return this
   *  </li>
   *  <li>there is an adult fare that is lower than all contract fares,
   *    DisplayLowest returns:<br/>
   *      AB  : contract <br/>
   *      CDE : contract <br/>
   *      ABCDE : lower ADT published fare, return this<br/>
   *  </li>
   * <ul>
   *
   * @see extractFareGroups
   ***********************************************************************
   */
  static void readFares(Document domReply, ReqGetFare request)
    throws Exception
    {
    Element root = domReply.getDocumentElement();
    PNR pnr = request.pnr;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to fare PNR: " + sErr);
      }
    else if ( !(root.getTagName().equals("PoweredFare_PricePNRWithBookingClassReply")) &&
      !(root.getTagName().equals("PoweredFare_PricePNRWithLowerFaresReply")))
      throw new GdsResponseException(
          "Unable to fare PNR: unrecognized Amadeus response");

    FareGroup[] replyFareList = extractFareGroups(domReply, request);

    if (replyFareList == null)
      {
      String sErr = read_CAPI_Messages(domReply);
      throw new GdsResponseException("No fares returned: " + sErr);
      //request.saveException( new GdsResponseException(
      //      "No fares returned: " + sErr));
      }

    if (request.getFareType().equals(ReqGetFare.FARE_LOWEST) == false)
      {
      for (int i=0; i < replyFareList.length ; i++)
        request.pnr.addFare(replyFareList[i].getFare());
      }
    else
      {
      PNRNameElement[] psgrList = null;

      if (request.getNames() != null)
        psgrList = request.getNames();
      else
        psgrList = request.pnr.getNames();

      FareGroup[] pnrPtcGroups  = FareGroup.groupNames(psgrList);

      // if we have a lower fare for all the ptc groups
      if ( replyFareList.length == (pnrPtcGroups.length * 2) )
        {
        for (int f=pnrPtcGroups.length; f < replyFareList.length; f++)
          request.pnr.addFare(replyFareList[f].getFare());
        System.out.println("AmAPIFareConv:Lowest 1:");
        }
      // if the lower fares are returned as a single ADT fare group,
      // return the last fare listed
      else if ( replyFareList.length == (pnrPtcGroups.length + 1) )
        {
        request.pnr.addFare(replyFareList[replyFareList.length - 1].getFare());
        System.out.println("AmAPIFareConv:Lowest 2:");
        }
      // if the fare groups and ptc groups match, or otherwise
      else
        {
        for (int f=0; f < replyFareList.length; f++)
          request.pnr.addFare(replyFareList[f].getFare());
          System.out.println("AmAPIFareConv:Lowest 3:");
        }
      System.out.println("Lowest: length: " + replyFareList.length);
      for (int f=0; f < replyFareList.length; f++)
          System.out.println("AmAPIFareConv:Lowest: replyFareList: " + replyFareList[f].getFare());
      }

    } // end readFares


  /**
   ***********************************************************************
   * <p>This method parses the XML response received from Amadeus and populates
   * the appropriate fields in the {@link PNR} object provided; the reply
   * returned is a <code>Ticket_ImagePlus_Reply</code> which does not directly
   * relate the fares returned to Passenger Type Codes (PTC);</p>
   * <p>
   * Therefore, when parsing the reply, it is necessary to first group the
   * passengers in the PNR (or in the request if passenger selecting) by PTC,
   * and then match these groups to the fares as they appear in the
   * <code>Ticket_ImagePlus_Reply</code>;</p>
   * <p>
   * when doing a LOWEST fare call, we perform a
   * <code>FarePlus_DisplayLowestApplicableFare_Query</code> which may fail
   * when doing passenger select; hence, the passenger list passed in the
   * request is ignored when performing a LOWEST fare call, and the
   * <code>ignorePsgrGroups</code> flag in this method is used to warn us that
   * we should also ignore the passenger list passed in the request when
   * parsing the <code>Ticket_ImagePlus_Reply</code>.</p>
   ***********************************************************************
   */
  /*
  public static void readFares(Document domReply, ReqGetFare
      aRequest, boolean ignorePsgrGroups)
      throws Exception
    {
    Element root = domReply.getDocumentElement();
    PNR pnr = aRequest.pnr;

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "Unable to fare PNR: " + sErr);
      }
    else if ( !(root.getTagName().equals("Ticket_ImagePlus_Reply")) )
      throw new GdsResponseException(
          "Unable to fare PNR: unrecognized Amadeus response");

    //try
    //  {
    // first get the passengers from the PNR so that we may get a list of the
    // PTC codes in the PNR, as these are not provided on the Fare reply
    PNRNameElement[] psgrList = null;

    if (ignorePsgrGroups == false) {
      psgrList = aRequest.getNames();
    }

    if (psgrList instanceof PNRNameElement[] == false)
      psgrList = pnr.getNames();
    else
      {
      for (int i=0; i < psgrList.length; i++)
        {
        // retrieve the information for each of the passengers from the pnr
        psgrList[i] = pnr.getName(psgrList[i].getPassengerID());
        }
      }

    // group the passengers by PTC code
    Vector ptcGroupsList = FareGroup.groupNames(psgrList);

    int[] arySegmentList = aRequest.getSegmentNumbers();

    // enumerate through the CAPI_Ticket_ImagePlus nodes
    NodeList fareList =
      domReply.getElementsByTagName("CAPI_Ticket_ImagePlus");

    if ( (fareList instanceof NodeList) == false )
      return;

    NodeList taxList =
      domReply.getElementsByTagName("CAPI_TI_TaxesPlus");

    String sLastTaxKey = "";
    int taxCounter = 0;
    Element elmFare, elmTax;


    if (taxList instanceof NodeList)
      {
      // the TI_Taxes_Key node relates the tax lines to the fare lines -
      // there may be multiple tax lines per fare line
      // we initialize this values for use in the loop below
      elmTax = (Element)taxList.item(0);
      sLastTaxKey = DOMutil.getTagValue(elmTax,"TI_Taxes_Key");
      }

    Enumeration psgrGroup = ptcGroupsList.elements();

    for (int i=0; i < fareList.getLength(); i++)
      {
      elmFare = (Element)fareList.item(i);

      long lBaseFare;
      String sEquivAmount = DOMutil.getTagValue(elmFare,"EquivAmount");

      if (sEquivAmount != null)
        lBaseFare = PNRFare.strToAmount(sEquivAmount);
      else
        lBaseFare = PNRFare.strToAmount(
            DOMutil.getTagValue(elmFare,"BaseFareAmount"));

      // if we find an accompanying infant, ignore that line
      String sInfFlag = DOMutil.getTagValue(elmFare,"Infant_Indicator");

      if (sInfFlag instanceof String && sInfFlag.equals("I") && lBaseFare==0 )
        continue;

      else // match this line with the next PTC group and tax lines
        {
        // read the current fare line
        FareGroup ptcGroup = (FareGroup)psgrGroup.nextElement();
        String sPTC = ptcGroup.PTC();
        PNRFare pnrFare = new PNRFare(sPTC);
        int iNumPsgrs = ptcGroup.countNames();
        String sFareLadder = DOMutil.getTagValue(elmFare,"FareCalculation");

        // create a new PNRFare corresponding to this Fare Line
        pnrFare.addFare(iNumPsgrs,lBaseFare,arySegmentList,sFareLadder);
        pnrFare.setNativePTC(sPTC);
        pnrFare.setGenericPTC(AmadeusGetPNRFareConversation.getGenericPTC(sPTC));

        // read the tax line(s) corresponding to this fare line
        if ( taxList instanceof NodeList)
          {
          while ( taxCounter < taxList.getLength() )
            {
            elmTax = (Element)taxList.item(taxCounter);
            // make sure that this tax line corresponds to the current fare
            if (DOMutil.getTagValue(elmTax,"TI_Taxes_Key").equals(sLastTaxKey) )
              {
              String sDescription = DOMutil.getTagValue(elmTax,"TaxDesignator");
              long lTaxAmount =
                PNRFare.strToAmount(DOMutil.getTagValue(elmTax,"TaxAmount"));
              pnrFare.addTax(iNumPsgrs,lTaxAmount,arySegmentList,sDescription);
              taxCounter++;
              }
            else // we have reached a tax group belonging to the next fare
              {
              sLastTaxKey = DOMutil.getTagValue(elmTax,"TI_Taxes_Key");
              break;
              }
            } // end while
          }

        pnr.addFare(pnrFare);
        }
      } // end for

    // make sure you get some fares back
    final PNRFare[] pnrFareList = pnr.getFares();
    if ( (pnrFareList instanceof PNRFare[]) == false )
      {
      String sErr = read_CAPI_Messages(domReply);
      aRequest.saveException( new GdsResponseException(
            "No fares returned: " + sErr));
      }
    //  }
    //// this is here only for testing purposes
    //catch (Exception e)
    //{String s = e.toString();
    //  s=s;}

    } // end readFares
  */


  /**
   ***********************************************************************
   * This method reads a <code>CAPI_Messages</code> node of the faring response
   * from the Amadeus API, which typically contains any errors or warnings
   *
   * @param domReply the DOM document containing a
   * <code>Ticket_ImagePlus_Reply</code>.
   *
   * @returns
   *  a String that concatenates the contents of the reply:
   *  LineType, and the Error Text.
   *
   ***********************************************************************
   */
  public static String read_CAPI_Messages(Document domReply)
    {
    Element el = (Element)domReply.getElementsByTagName(
       "Ticket_ImagePlus_Reply").item(0);

    if (el instanceof Element)
      {
      el = (Element)el.getElementsByTagName("CAPI_Messages").item(0);

      String sErr;
      sErr  = "LineType: "  + DOMutil.getTagValue(el,"LineType")  + " | ";
      sErr += DOMutil.getTagValue(el,"Text");

      return(sErr);
      }
    else
      return("");

    } // end readError_MessagesOnly_Reply

  /**
   ***********************************************************************
   * This method performs the following operations:
   * <ul>
   *  <li>retrievesthe PNR, </li>
   *  <li>deletes any Commission (FM), Tour Code (), Endorsement, and Form of
   *      Payment (FOP) remarks in the PNR, </li>
   *  <li>adds any Commission, Tour Code, Endorsement, and Form of Payment
   *      remarks specified in the Ticketing request, and a ReceivedFrom remark</li>
   *  <li>and finally calls {@link AmadeusGetPNRFareConversation#getTicketCommand}
   *      to generate the <code>TTP</code> command string for ticketing a
   *      Passenger Name Record (PNR), and executes the command.</li>
   * </ul>
   ***********************************************************************
   */
  public static void issueTicket(
      final AmadeusAPICrs aCRS, final ReqIssueTicket aRequest) throws Exception
    {
		String sInfResponse = null;

		// retrieve the PNR
    aRequest.pnr = new PNR();
    aCRS.Ignore();
    aCRS.GetPNRAllSegments(aRequest.getLocator(),aRequest.pnr,true);

    PNRRemark[] pnrRemarks = aRequest.pnr.getRemarks();
    Vector vDelRmks = new Vector();

    for (int i=0; i < pnrRemarks.length ; i++)
      {
      if (isTicketRemarkToBeCleared(pnrRemarks[i]))
        vDelRmks.add(pnrRemarks[i]);
      } // end for

    Document domQuery = AmadeusAPIBuildPNRConversation.buildQuery_cancelRemarks(
          (PNRRemark[])(vDelRmks.toArray(
              new PNRRemark[vDelRmks.size()]) ));

    Document domReply = aCRS.connection.sendAndReceive(domQuery);

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

    // the Form of Payment can be added in the Ticket command, but because of
    // issues with the ticketing command, we decided to add it separately
    if ( GnrcFormat.NotNull(aRequest.FOP) )
      aCRS.AddFOP(aRequest.FOP);

    // clear any Transitional Stored Tickets (TSTs - stored fares) that may
    // have been associated with this PNR
    aCRS.HostTransaction("TTE/ALL");

    // construct the command for FXP faring then issuing a ticket
    final String sRequestFare  =
      AmadeusGetPNRFareConversation.getFXPFareCommand(aRequest);
	aCRS.HostTransaction(sRequestFare);

    // send a Receive From
    aCRS.AddReceiveBy(aRequest.RequestedBy);

    // get TST number(s)
    // String sTQTResponse = aCRS.HostTransaction("TQT");
    // String sTstNumbers = getTstNumbers(sTQTResponse);

    // construct the command for issuing a ticket
    String sRequest  =
      AmadeusGetPNRFareConversation.getTicketCommand(aRequest);
    /*
    if (sTstNumbers != null)
    {
      sRequest = sRequest + "/T" + sTstNumbers;
    }
    */
    String sResponse = aCRS.HostTransaction(sRequest);
    System.out.println("AmadeusAPIFareConversation.issueTicket: " + sResponse.trim());

    // if we get a continuity warning, as may be the case if the itinerary
    // contains an 'ARNK' segment (itinerary is not continuous) for example,
    // ignore the warning and re-issue the ticketing command
    if ( sResponse.indexOf("CHECK SEGMENT CONTINUITY") >= 0 )
      sResponse = aCRS.HostTransaction(sRequest);

    // The API has a bug which causes it to generate a 'SIMULATANEOUS CHANGES
    // TO PNR' error, even if the ticket was successfully generated - this
    // behavior, although the most prevalent, is not consistent, and hence we
    // must also look out for a success message
    if ( sResponse.indexOf("OK PROCESSED") >= 0 ||
        sResponse.indexOf("SIMULTANEOUS CHANGES TO PNR") >= 0 ||
        sResponse.indexOf("RP/") >= 0 ||
        sResponse.indexOf("OK ETICKET") >= 0)
		  {
			final String sInfRequest  =
        AmadeusGetPNRFareConversation.getInfTktCommand(aRequest);
      if ( GnrcFormat.NotNull(sInfRequest) ) sInfResponse = aCRS.HostTransaction(sInfRequest);

			aCRS.Ignore();
		  }
    else
      {
      if (ReqIssueTicket.isTicketingEnabled())
        {
        throw new GdsResponseException(
            "Unable to issue ticket: " + sResponse, sRequest, sResponse);
        }
      else
        {
        // if we are in a testing environment, Ignore without throwing an exception
				final String sInfRequest  =
				  AmadeusGetPNRFareConversation.getInfTktCommand(aRequest);
        if ( GnrcFormat.NotNull(sInfRequest) ) sInfResponse = aCRS.HostTransaction(sInfRequest);
				aCRS.Ignore();
        }
      }

    } //  end issueTicket

  /**
   ***********************************************************************
   * This method returns true if the remark passed is one of the four remarks
   * that must be cleared prior to ticketing:
   * <ul>
   *  <li>Tour Code</li>
   *  <li>Endorsement</li>
   *  <li>Commission</li>
   *  <li>Form of Payment</li>
   * </ul>
   ***********************************************************************
   */
  static boolean isTicketRemarkToBeCleared(PNRRemark remark)
    {
    if ( remark.TypeCode.equals("FM") || remark.TypeCode.equals("FT") ||
         remark.TypeCode.equals("FE") || remark.TypeCode.equals("FP") )
      return true;
    else
      return false;
    } // end isTicket


  /**
   ***********************************************************************
   * Builds the AmadeusAPI XML query to ticket a PNR
   * not yet implemented
   ***********************************************************************
   */
  public static Document buildQuery_DocPrt_IssueTicket_Query(
      ReqIssueTicket aRequest) throws TranServerException
    {
    Document domQuery = new DocumentImpl();
    Element root = domQuery.createElement("DocPrt_IssueTicket_Query");
    domQuery.appendChild(root);

    // get the list of selected passengers
    final String sPsgrList = "";

    DOMutil.addTextElement(root,"RepriceTST","/R,*ptc");

    // get the list of selected segments
    final String sSegList = getSegmentList( aRequest.getSegments(), aRequest.pnr );

    if ( GnrcFormat.NotNull(sSegList) )
      DOMutil.addTextElement(root,"SegmentSelect",sSegList);

    return domQuery;

    } // end buildQuery_DocPrt_IssueTicket_Query

  /**
   ***********************************************************************
   * Returns a list of comma separated PNR line numbers corresponding to the
   * passengers provided
   ***********************************************************************
   */
  /*
  protected static String getPsgrList(
      final PNRNameElement[] aPsgrList, final PNR aPNR)
    {
    StringBuffer psgrList = new StringBuffer();

    if ( aPsgrList instanceof PNRNameElement[] )
      {
      PNRNameElement name;
      for ( int i = 0; i < aPsgrList.length; i++ )
        {
        try
          {
          if ( aPsgrList[i] instanceof PNRNameElement )
            {
            int iPsgrNum = aPNR.getPsgrNum(aPsgrList[i].getPassengerID());
            if (iPsgrNum > 0)
              psgrList.append(String.valueOf(iPsgrNum));
            }
          }
        catch (Exception e)
          {}
        }
      }

    return psgrList.toString();

    } // end getPsgrNumList
  */

  /**
   ***********************************************************************
   * Calls {@link AmadeusGetPNRFareConversation#getSegmentList} to return a
   * comma separated list of PNR line numbers corresponding to the segment
   * list provided
   ***********************************************************************
   */
  protected static String getSegmentList(
      final PNRItinAirSegment[] aSegmentList, PNR pnr)
    throws TranServerException
    {
    return AmadeusGetPNRFareConversation.getSegmentList(aSegmentList,pnr);
    } // end getSegmentList


  /**
   ***********************************************************************
   * This method reads through a <code>Ticket_ImagePlus_Reply</code> and
   * matches the
   ***********************************************************************
   */
  private static FareGroup[] extractFareGroups(Document domReply, ReqGetFare request)
    throws Exception
    {
    // enumerate through the PoweredFARE nodes
    NodeList fareList =
      domReply.getElementsByTagName("fareList");

    if (fareList.getLength() == 0)
      return null;

    int[] segList = request.getSegmentNumbers();

    ArrayList fareGroupList = new ArrayList();

    // read through the fare list
    for (int fareKey = 0; fareKey < fareList.getLength(); fareKey++)
      {
      NodeList fareDataSupInformation =
        domReply.getElementsByTagName("fareDataSupInformation");

			long lBaseFare = 0;
      long lOtherFare = 0;
			String sFareDataQualifier =  "";
      String sEquivAmount = "";

			// read through fare data to get base fare
			for (int fareData = 0; fareData < fareDataSupInformation.getLength(); fareData++)
			  {
			  Element elmFare = (Element)fareDataSupInformation.item(fareData);

			  // retrieve the fare information from the fare node
        sFareDataQualifier = DOMutil.getTagValue(elmFare,"fareDataQualifier");
        sEquivAmount = DOMutil.getTagValue(elmFare,"EquivAmount");
        if ( sFareDataQualifier.equals("B") )
          {
          lBaseFare = PNRFare.strToAmount(DOMutil.getTagValue(elmFare,"fareAmount"));
          }
        else
          {
          lOtherFare = PNRFare.strToAmount(DOMutil.getTagValue(elmFare,"fareAmount"));
          }
				} // fare data loop

      System.out.println("AmAPIFareConv:extractFareGroups: Qualifier/Base/Other" + sFareDataQualifier + "/" + lBaseFare + "/" +
        lOtherFare + "/fareKey:" + fareKey + "/fareListLen:" + fareList.getLength());

      // read the fare ladder line(s) corresponding to this fare
      String sFareLadder = " ";
      NodeList fareLadders =
        domReply.getElementsByTagName("attributeDetails");  /* otherPricingInfo */
      if ( fareLadders.getLength() > 0)
        {
        for (int l=0; l < fareLadders.getLength(); l++)
          {
          Element elmFareLadder = (Element)fareLadders.item(l);
          // make sure that this tax line corresponds to the current fare
          sFareLadder = DOMutil.getTagValue(elmFareLadder,"attributeDescription");
          System.out.println("AmAPIFareConv:extractFareGroups: l:" + l + "sFareLadder:" + sFareLadder);
          } // end for
        } // end if

      // get PTC for fares
			String sFarePTC = "ADT";
      NodeList segmentInformation =
        domReply.getElementsByTagName("segmentInformation");
      if ( segmentInformation.getLength() > 0)
        {
        for (int l=0; l < segmentInformation.getLength(); l++)
          {
          Element elmFarePTC = (Element)segmentInformation.item(l);
          // make sure that this tax line corresponds to the current fare
          sFarePTC = DOMutil.getTagValue(elmFarePTC,"discTktDesignator");
          System.out.println("AmAPIFareConv:extractFareGroups: sFarePTC:" + l + ":" + sFarePTC);
          } // end for
        } // end if

			// create a fare group
			FareGroup fareGroup = new FareGroup(sFarePTC);
      fareGroup.getFare().setGenericPTC(
          AmadeusGetPNRFareConversation.getGenericPTC(sFarePTC));

			// find the passengers to whom this fare applies, and their PTC
      PNRNameElement[] psgrList =
        getPsgrsInFareGroup(sFarePTC,request.pnr);

      System.out.println("AmAPIFareConv:extractFareGroups: find psgr:");
			fareGroup.addNames(psgrList);
      fareGroup.getFare().addFare(psgrList.length,lBaseFare,segList,sFareLadder);

      // read the tax line(s) corresponding to this fare line
      NodeList taxList =
        domReply.getElementsByTagName("taxInformation");
      if ( taxList.getLength() > 0)
        {
        for (int t=0; t < taxList.getLength(); t++)
          {
          Element elmTax = (Element)taxList.item(t);
          // make sure that this tax line corresponds to the current fare
          String sDescription = DOMutil.getTagValue(elmTax,"isoCountry");
          String staxNature = DOMutil.getTagValue(elmTax,"taxNature");
          long lTaxAmount =
            PNRFare.strToAmount(DOMutil.getTagValue(elmTax,"fareAmount"));
          System.out.println("AmAPIFareConv:extractFareGroup: t/sDescription/lTaxAmount:" + t + "/" + sDescription + "/" + lTaxAmount);
          fareGroup.getFare().addTax(psgrList.length,lTaxAmount,segList,sDescription);
          } // end for
        }

      fareGroupList.add(fareGroup);
      } // end for

    System.out.println("AmAPIFareConv:extractFareGroups: fareGroupList.size():" + fareGroupList.size());
    FareGroup[] fareGroups = new FareGroup[fareGroupList.size()];

    return (FareGroup[])fareGroupList.toArray(fareGroups);

    } // end extractFareGroups


  /**
   ***********************************************************************
   * Given a PTC from <fareList><segmentInformation><fareQualifier>
	 * <fareBasisDetails>discTktDesignator>, return all the
   * passengers specified on that PNR for that PTC
   ***********************************************************************
   */
  private static PNRNameElement[] getPsgrsInFareGroup(
      String sFarePTC, PNR pnr)
    {
    ArrayList psgrList = new ArrayList();
		PNRNameElement[] namesList = pnr.getNames();
		System.out.println("getPsgrsInFareGroup: namesList.length:" + namesList.length);

    // load the psgrs for the PTC
    for (int i = 0; i < namesList.length; i++)
      {
			PNRNameElement nameElement = pnr.getName(i);
			if ( sFarePTC.compareTo(nameElement.PTC) == 0 )
			  {
				String sPName = nameElement.LastName;

				/**String sFirstChar = sFarePTC.substring(0,1);
				 if ((sFirstChar == "C") || (sFirstChar == "P"))
				   {
				   String sChdAge = sFarePTC.substring.substring(1);
				   }
				 http://www.j2mepolish.org/javadoc/build/de/enough/polish/util/StringUtil.html
				 private static final boolean isNumeric(final String s)
				   {
           final char[] numbers = s.toCharArray();
           for (int x = 0; x < numbers.length; x++)
				     {
             final char c = numbers[x];
             if ((c >= '0') && (c <= '9')) continue;
             return false; // invalid
             }
           return true; // valid
           }
				*/
        psgrList.add(nameElement);
			  System.out.println("getPsgrsInFareGroup: sPName:" + sPName);
				}
			}
    System.out.println("getPsgrsInFareGroup: psgrList.size():" + psgrList.size());
    if ( (psgrList == null) || (psgrList.size() == 0) )
      {
      psgrList.add(pnr.getName(0));
			System.out.println("getPsgrsInFareGroup: Name(0):" + pnr.getName(0).LastName);
      }
    PNRNameElement[] psgrs = new PNRNameElement[psgrList.size()];

    return (PNRNameElement[])psgrList.toArray(psgrs);
    } // end getPsgrsInGroup

  /**
   ***********************************************************************
   * This method derives the appropriate Passenger Type Code for a group of
   * passengers, as follows: if the same PTC is shared by all passengers, that
   * PTC is returned, otherwise the catch-all <code>ADT</code> is returned.
   ***********************************************************************
   */
  private static String getPsgrGroupPTC(PNRNameElement[] psgrList)
    {
		System.out.println("getPsgrGroupPTC: psgrList:" + psgrList);
		String sPTC;

    if (psgrList == null)
      return null;

		System.out.println("getPsgrGroupPTC: psgrList.length:" + psgrList.length);

		if ( psgrList.length > 0)
		{
			sPTC = psgrList[0].PTC;
		}
		else
		{
			return null;
		}

    for (int i=0; i < psgrList.length ; i++)
      {
      if (psgrList[i].PTC.equals(sPTC) == false)
        return "ADT";
      } // end for

    return sPTC;

    } // end getPsgrGroupPTC

     /**** sro
  ****/
  private static String getTstNumbers(final String aAmadeusResponse) throws Exception
  {

      final SortedSet tstList = new TreeSet();

      // parse the response to get the last TST number
       final StringTokenizer lines = new StringTokenizer(aAmadeusResponse,"\r\n");

       while( lines.hasMoreTokens() )
       {
          final String sLine = lines.nextToken().trim();

          if (sLine.startsWith("T"))
          {
              continue;   // skip this line since it's the header
          }
          else if (sLine.startsWith("DELETED"))
          {
              break;
          }
          else
          {
              final String[] fields = RegExpMatch.getMatchPatterns(sLine,"^\\s*(\\d+)\\s+");
              {
                  if ( fields instanceof String[] )
                  {
                      final String sTstNum = fields[1];
                      tstList.add(sTstNum);
                  }
              }
          }
       }


      if (tstList.size() == 0)
      {
          return null;

      }


       // create a string response
      final StringBuffer sBuf = new StringBuffer();
      final String[] sTstArray = (String[] )tstList.toArray(new String[0]);
      for (int i = 0; i < tstList.size(); i++)
      {
          if (i > 0)
          {
              sBuf.append(',');
          }
          sBuf.append(sTstArray[i]);
      }

      return sBuf.toString();

  } // getTstNumbers

} // end class AmadeusAPIFareConversation