package xmax.crs.Amadeus;

import java.util.Vector;
import java.text.*;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.apache.xerces.dom.DocumentImpl;

import xmax.crs.Flifo.*;
import xmax.crs.GnrcParser;
import xmax.crs.GnrcCrs;
import xmax.crs.GdsResponseException;
import xmax.TranServer.GnrcFormat;
import xmax.TranServer.TranServerException;
import xmax.util.xml.DOMutil;
import xmax.util.Log.AppLog;

/**
 ***********************************************************************
 * This class is used to handle the details of passing a Flight Information
 * (Flifo) query and reading the corresponding reply from the Amadeus Server.
 *
 * @author   Philippe Paravicini
 * @version  1.0 Copyright (c) 2001
 *
 * @see
 ***********************************************************************
 */
public class AmadeusAPIFlightServiceConversation
{

  public boolean isMultiDayFlgt = false;

  /**
   ***********************************************************************
   * This method calls {@link getFlifoRequest} to generate an XML Query
   * in the Amadeus API format, executes the request by passing the
   * string to {@link AmadeusAPICrs.HostTransaction}, and parses the reply
   * through {@link getFlightSegments}.
   ***********************************************************************
   */
  public void getFlightInfo(final AmadeusAPICrs aCrs,
                            final String sCarrier,
                            final int sFlightNum,
                            final String sDepDate,
                            final String sDepCity,
                            final String sArrCity,
                            FlightInfo flightInfo) throws Exception
    {
    System.out.println("AmadeusAPIFlightServiceConversation.GetFlightInfo: sCarriers/sFlightNum/sDepDate/sDepCity/sArrCity: "
      + sCarrier + "/" + sFlightNum + "/" + sDepDate + "/" + sDepCity + "/" + sArrCity);

    // build XML query
    Document domQuery =
      getFlifoRequest(sCarrier, sFlightNum, sDepDate, sDepCity, sArrCity);

    if ( !(domQuery instanceof Document) )
      throw new TranServerException("Unable to create Flifo request");

    // execute XML query
    String sQry = DOMutil.domToString(domQuery);
    // Document domReply = aCrs.HostTransaction(domQuery);
    Document domReply = aCrs.connection.sendAndReceive(domQuery);
    flightInfo.FlightSchedResponse = DOMutil.domToString(domReply);

    Element root = domReply.getDocumentElement();

    // if we did not get a proper reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = read_CAPI_Messages(domReply);
      throw new GdsResponseException(
          "Unable to Flifo: " + sErr);
      }
   //else if ( !(root.getTagName().equals("Air_FlightInfo_Reply")) )
    else if ( !(root.getTagName().equals("PoweredAir_FlightInfoReply")) )
      throw new GdsResponseException(
          "Unable to Flifo: unrecognized Amadeus response");

    // parse the reply via getFlightSegments
    final FlightSegment [] segments = getFlightSegments(
        sCarrier, sFlightNum, sDepDate, domReply);
        //System.out.println("AmadeusAPIFlghtSrvcConv.GetFlightInfo: segments: " + segments.length);

    if ((segments != null) && (segments.length > 0))
    {
	    // Make sure that the departure date returned is the one that we requested
	    // If the flight requested does not fly on that date, Amadeus may simply
	    // return a flight departing on a different date - this is not desirable
	    final String returnedDate =
	      xmax.util.DateTime.fmtLongDateTime(
	          segments[0].DepSchedDateTime,"ddMMM").toUpperCase();
	    System.out.println("AmadeusAPIFlghtSrvcConv.GetFlightInfo: returnedDate/sDepDate/sDepCity: " + returnedDate + "/"
	      + sDepDate + "/" + sDepCity);
	
	    if (sDepDate.equals(returnedDate) == false)
	      {
	      String sMsg = read_CAPI_Messages(domReply);
	      throw new GdsResponseException(
	          "Mismatched Date Returned - Amadeus Message: " + sMsg);
	      }
    }
    else
    {    // not able to scan any segments
    	String sErrorMessage = readErrorMessages(domReply);
    	if (sErrorMessage == null)
    	{
    		sErrorMessage = "No flight segments found";
    	}
        throw new GdsResponseException("Unable to Flifo: Amadeus error message = " + sErrorMessage);
    }

    // Determine if Previous Day processing needed
    if (sDepCity == null && isMultiDayFlgt(sDepDate, segments))
      {
       //System.out.println("AmadeusAPIFlghtSrvcConv Subvert Date");
       getPrevFlightInfo(aCrs,sCarrier,sFlightNum,sDepDate,null,null,flightInfo);
      }
    else
      {
    // add the retrieved segments to our FlightInfo object
    //System.out.println("AmadeusAPIFlghtSrvcConv Regular Processing");
    flightInfo.setFlightSegments(segments);

    // if applicable do a getDayOfFlightInfo using cryptic code
    // we enclose it in a try-catch clause so that, if parsing is broken for a
    // given airline, a DayOfFlifo failure will not cause the flight to be
    // marked as Non-operational (No-Op)
    try
      {
      if ( flightInfo.isOkToFlifo() )
        {
        final StringBuffer sFlightStr = new StringBuffer("");
        getDayOfFlightInfo(aCrs,sCarrier,sFlightNum,sDepDate,sFlightStr);
        flightInfo.DayOfFlifoResponse = GnrcParser.addLineFeeds( sFlightStr.toString() );
        final AmadeusParseFlifo Parser = new AmadeusParseFlifo();
        Parser.setDayOfFlightSegments(flightInfo, flightInfo.DayOfFlifoResponse);
        //System.out.println("AmadeusAPIFlghtSrvcConv.setDayOfFlightSegments: " + flightInfo.DayOfFlifoResponse);
        }
    }
    catch (Exception e)
      {
      AppLog.LogError(
          "Exception occurred when doing DayOfFlifo for flight: " +
          sCarrier + sFlightNum + " on date: " + sDepDate + " - " + e.toString());
      }
      } // else

    // GetMiles function does not appear to be implemented in the Amadeus API


    } // end getFlightInfo


    /**
       ***********************************************************************
       * This method calls {@link getFlifoRequest} to generate an XML Query
       * in the Amadeus API format, executes the request by passing the
       * string to {@link AmadeusAPICrs.HostTransaction}, and parses the reply
       * through {@link getFlightSegments}.
       ***********************************************************************
       */
      public void getPrevFlightInfo(final AmadeusAPICrs aCrs,
                                    final String sCarrier,
                                    final int sFlightNum,
                                    final String sDepDate,
                                    final String sDepCity,
                                    final String sArrCity,
                                    FlightInfo flightInfo) throws Exception
        {
        String sCalcDepDate = AmadeusAPICrs.fmt_ddMMM_To_ddMMyy(sDepDate);
        //System.out.println("AmadeusAPIFlightServiceConversation.getPrevFlightInfo:1: sCalcDepDate: " + sCalcDepDate);
        String sDay     = sCalcDepDate.substring(0,2);
        String sMonth   = sCalcDepDate.substring(2,4);
        String sYear    = sCalcDepDate.substring(4,6);
        int iYear       = Integer.parseInt(sYear);
        int iMonth      = Integer.parseInt(sMonth);
        int iDay        = Integer.parseInt(sDay);
        String sPrevDepDt = getPrevDay(iYear, iMonth, iDay);
        System.out.println("AmadeusAPIFlightServiceConversation.getPrevDay:sPrevDepDt: " + sPrevDepDt);

        // build XML query
        Document domQuery =
          getFlifoRequest(sCarrier, sFlightNum, sPrevDepDt, sDepCity, sArrCity);
          //System.out.println("AmadeusAPIFlightServiceConversation.getPrevFlightInfo:2: sPrevDepDt: " + sPrevDepDt);
        if ( !(domQuery instanceof Document) )
          throw new TranServerException("Unable to create Flifo request");

        // execute XML query
        String sQry = DOMutil.domToString(domQuery);
        // Document domReply = aCrs.HostTransaction(domQuery);
        Document domReply = aCrs.connection.sendAndReceive(domQuery);
        flightInfo.FlightSchedResponse = DOMutil.domToString(domReply);

        Element root = domReply.getDocumentElement();

        // if we did not get a proper reply, issue an error
        if ( root.getTagName().equals("MessagesOnly_Reply") )
          {
          String sErr = read_CAPI_Messages(domReply);
          throw new GdsResponseException(
              "Unable to Flifo: " + sErr);
          }
      //else if ( !(root.getTagName().equals("Air_FlightInfo_Reply")) )
        else if ( !(root.getTagName().equals("PoweredAir_FlightInfoReply")) )
          throw new GdsResponseException(
              "Unable to Flifo: unrecognized Amadeus response");

        // parse the reply via getFlightSegments
        final FlightSegment [] segments = getFlightSegments(
            sCarrier, sFlightNum, sPrevDepDt, domReply);
            //System.out.println("AmadeusAPIFlghtSrvcConv.getPrevFlightInfo: segments: " + segments.length);

        if ((segments != null) && (segments.length > 0))
        {
	        // Make sure that the departure date returned is the one that we requested
	        // If the flight requested does not fly on that date, Amadeus may simply
	        // return a flight departing on a different date - this is not desirable
	        final String returnedDate =
	          xmax.util.DateTime.fmtLongDateTime(
	              segments[0].DepSchedDateTime,"ddMMM").toUpperCase();
	        //System.out.println("AmadeusAPIFlghtSrvcConv.getPrevFlightInfo: returnedDate: " + returnedDate);
	
	        if (sPrevDepDt.equals(returnedDate) == false)
	          {
	          String sMsg = read_CAPI_Messages(domReply);
	          throw new GdsResponseException(
	              "Mismatched Date Returned - Amadeus Message: " + sMsg);
	          }
        }

        // add the retrieved segments to our FlightInfo object
        flightInfo.setFlightSegments(segments);

        // if applicable do a getDayOfFlightInfo using cryptic code
        // we enclose it in a try-catch clause so that, if parsing is broken for a
        // given airline, a DayOfFlifo failure will not cause the flight to be
        // marked as Non-operational (No-Op)
        try
          {
          if ( flightInfo.isOkToFlifo() )
            {
            final StringBuffer sFlightStr = new StringBuffer("");
            getDayOfFlightInfo(aCrs,sCarrier,sFlightNum,sPrevDepDt,sFlightStr);
            flightInfo.DayOfFlifoResponse = GnrcParser.addLineFeeds( sFlightStr.toString() );
            final AmadeusParseFlifo Parser = new AmadeusParseFlifo();
            Parser.setDayOfFlightSegments(flightInfo, flightInfo.DayOfFlifoResponse);
            //System.out.println("AmadeusAPIFlghtSrvcConv.setDayOfFlightSegments: " + flightInfo.DayOfFlifoResponse);
            }
        }
        catch (Exception e)
          {
          AppLog.LogError(
              "Exception occurred when doing DayOfFlifo for flight: " +
              sCarrier + sFlightNum + " on date: " + sPrevDepDt + " - " + e.toString());
          }

        // GetMiles function does not appear to be implemented in the Amadeus API


        } // end getPrevFlightInfo


  /**
   ***********************************************************************
   * This method builds a DOM Document containing the XML query that will
   * be passed to the Amadeus XML server, based on the Flight Info
   * request paramaters.
   *
   * @param sCarrier    the carrier code
   * @param sFlightNum  the flight number
   * @param sDepDate    the departure date, in ddMMMyyyy format
   ***********************************************************************
   */
  private Document getFlifoRequest(String sCarrier, int sFlightNum,
      String sDepDate, final String sDepCity, final String sArrCity)
    throws TranServerException
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(sCarrier) )
      throw new TranServerException("Must specify a valid Carrier Code to create Flifo Query");

    if ( sFlightNum == 0 )
      throw new TranServerException("Must specify a valid Flight Number to create Flifo Query");

    if ( GnrcFormat.IsNull(sDepDate) )
      throw new TranServerException("Must specify a valid Departure Date to create Flifo Query");

    // create a document object and our top level Element
    Document domQuery = new DocumentImpl();
    Element elm1, elm11, elm111;
    Element root = domQuery.createElement("PoweredAir_FlightInfo");
    Element item;

    // Add the elements

    // the generalFlightInfo node
    elm1 = domQuery.createElement("generalFlightInfo");

     // the flightDate node
     elm11 = domQuery.createElement("flightDate");

      // convert the date format from ddMMM to Powered Format ddMMyy
      sDepDate = AmadeusAPICrs.fmt_ddMMM_To_ddMMyy(sDepDate);
      DOMutil.addTextElement(domQuery,elm11,"departureDate",sDepDate);
      elm1.appendChild(elm11);

     // the boardPointDetals and offPointDetails nodes
     if (sDepCity != null && sArrCity != null)
       {
        elm11 = domQuery.createElement("boardPointDetails");
        DOMutil.addTextElement(domQuery,elm11,"trueLocationId",sDepCity);
        elm1.appendChild(elm11);

        elm11 = domQuery.createElement("offPointDetails");
        DOMutil.addTextElement(domQuery,elm11,"trueLocationId",sArrCity);
        elm1.appendChild(elm11);
       }

     // the companyDetails node
     elm11 = domQuery.createElement("companyDetails");
     DOMutil.addTextElement(domQuery,elm11,"marketingCompany",sCarrier);
     elm1.appendChild(elm11);

     // the flightIdentification node
     elm11 = domQuery.createElement("flightIdentification");
     DOMutil.addTextElement(domQuery,elm11,"flightNumber",Integer.toString(sFlightNum));
     elm1.appendChild(elm11);

    // add the generalFlightInfo level root
    root.appendChild(elm1);
    // add the root to the top level document
    domQuery.appendChild(root);

    return( domQuery );

    } // end getFlifoRequest


  /**
   ***********************************************************************
   * This method parses an Amadeus API Air_FlightInfo_Reply, and returns
   * an array of {@link FlightSegment} objects that can be associated with a
   * {@link FlightInfo object}.
   ***********************************************************************
   */
  protected FlightSegment[] getFlightSegments(final String  aCarrier,
                                              final int     aFlightNumber,
                                              final String  sDepDate,
                                              Document domFlifoReply) throws Exception
    {
    //NodeList nodes = domFlifoReply.getElementsByTagName("group4");
    NodeList nodes = domFlifoReply.getElementsByTagName("boardPointAndOffPointDetails");

    Element elmStop = null;
    FlightStop stopInfo = null;
    final Vector stopList = new Vector();

    for (int i=0; i < nodes.getLength(); i++)
      {
      elmStop = (Element)nodes.item(i);
      stopInfo = scanStopData(elmStop);
      stopList.add(stopInfo);
      } // end for

    if (stopList.size() < 2) return(null);


    // convert vector into an array of stops
    final FlightStop[] stopArray = new FlightStop[ stopList.size() ];
    stopList.toArray(stopArray);

    // convert array of stop objects to array of flight segments
    final FlightInfo flifo = AmadeusParseFlifo.stopsToSegments(aCarrier, aFlightNumber, stopArray);



    // read comments
 // nodes = domFlifoReply.getElementsByTagName("CAPI_Comments");
    nodes = domFlifoReply.getElementsByTagName("interactiveFreeText");
    readComments(flifo, nodes);

    // read operations
    // This has not yet been implemented in the Amadeus API.
    // In any event, the same information appears to be gathered
    // when doing a 'DayOf' Flifo

    return( flifo.getFlightSegments() );

    } // end getFlightSegments


  /**
   ***********************************************************************
   * This method populates 'stop' or 'leg' data into a FlightStop object
   ***********************************************************************
   */
  private static FlightStop scanStopData(Element stopElm)
    {
    FlightStop stop = new FlightStop();

    // make sure that we have a city
    stop.CityCode   = DOMutil.getTagValue(stopElm, "trueLocationId");
    if ( !(stop.CityCode instanceof String) ) return(null);

    // retrieve and convert the DateTime strings
    try
      {
  //  String ddMMMyyyy, HHmm;
      String ddMMyy, HHmm;

      // convert the arrival DateTime
   // ddMMMyyyy = DOMutil.getTagValue(stopElm, "arrivalDate");
      ddMMyy    = DOMutil.getTagValue(stopElm, "arrivalDate");
      HHmm      = DOMutil.getTagValue(stopElm, "arrivalTime");

  //  if ( ddMMMyyyy instanceof String && HHmm instanceof String)
  //    stop.ArriveDateTime = AmadeusAPICrs.fmtDateTimeToLong(ddMMMyyyy, HHmm, "ddMMMyyyyHHmm");
      if ( ddMMyy instanceof String && HHmm instanceof String)
        stop.ArriveDateTime = AmadeusAPICrs.fmtDateTimeToLong(ddMMyy, HHmm, "ddMMyyHHmm");

      // convert the departure DateTime
      ddMMyy    = DOMutil.getTagValue(stopElm, "departureDate");
      HHmm      = DOMutil.getTagValue(stopElm, "departureTime");

  //  if ( ddMMMyyyy instanceof String && HHmm instanceof String)
  //    stop.DepartDateTime = AmadeusAPICrs.fmtDateTimeToLong(ddMMMyyyy, HHmm, "ddMMyyHHmm");
      if ( ddMMyy instanceof String && HHmm instanceof String)
        stop.DepartDateTime = AmadeusAPICrs.fmtDateTimeToLong(ddMMyy, HHmm, "ddMMyyHHmm");
      }
    catch(Exception e)
      {
      System.err.println(e.getMessage());
      e.printStackTrace();
      }

    // make sure that we gathered at least a departure or arrival DateTime
    if ( stop.ArriveDateTime == 0 && stop.DepartDateTime == 0 )
      return(null);

    // retrieve the meal information from the inventory classes
    /* NodeList invtrNodes = stopElm.getElementsByTagName("CAPI_ClassesInfo");
       if (invtrNodes instanceof NodeList)
       stop.MealService = scanMealService(invtrNodes); */

    // scan the equipment
    stop.Equipment = DOMutil.getTagValue(stopElm, "equipment");

    // scan the duration of this leg
    String t = DOMutil.getTagValue(stopElm, "duration");
    if (t instanceof String)
      stop.FlightMinutes = GnrcParser.getElapsedMinutes(t);

    // voila!
    return(stop);

    } // end scanStopData


  /**
   ***********************************************************************
   * This method scans the inventory nodes and retrieves a list of the
   * unique meal codes appearing in the inventory classes; the list is
   * formatted as a string of meal codes separated by a '/',
   * for example: <code>/D/M</code>
   ***********************************************************************
   */
  private static String scanMealService(NodeList invtrNodes)
    {
    String mealCodeStr = "";
    String prevCode = "";

    for (int i=0; i < invtrNodes.getLength(); i++)
      {
      Element invClass = (Element)invtrNodes.item(i);
      String code = DOMutil.getTagValue(invClass, "MealService");
      if ( (code instanceof String) && !code.equals(prevCode) )
        {
        mealCodeStr += code + "/";
        prevCode     = code;
        }
      } // end for

    // remove the last '/'
    if ( mealCodeStr.length() > 0 )
      mealCodeStr = mealCodeStr.substring(0, mealCodeStr.length()-1);

    return(mealCodeStr);

    } // end scanMealService


  /**
   ***********************************************************************
   * This method iterates over the comment nodes in the Air_FlightInfo_Reply,
   * extracts the comment for each individual line, and calls
   * {@link AmadeusParseFlifo.readSingleComment} to add the comment in the
   * appropriate field of the FlightInfo object.
   ***********************************************************************
   */
  private static void readComments(FlightInfo aFlifo, NodeList commentNodes)
    {

    for (int i=0; i < commentNodes.getLength() ; i++)
      {
      Element commentElm = (Element)commentNodes.item(i);
  //  String sComment = DOMutil.getTagValue(commentElm, "Comment");
      String sComment = DOMutil.getTagValue(commentElm, "freeText");
      AmadeusParseFlifo.readSingleComment(aFlifo, sComment);
      } // end for

    } // end readComments


  /**
   ***********************************************************************
   * Reads the text message that appears within a <code>CAPI_Messages</code>
   * node
   ***********************************************************************
   */
  private static String read_CAPI_Messages(Document domReply)
    {
 // Element el =
 //   (Element)domReply.getElementsByTagName("CAPI_Messages").item(0);
    Element el =
     (Element)domReply.getElementsByTagName("group1").item(0);

    if (el instanceof Element)
      // return(DOMutil.getTagValue(el,"Text"));
      return(DOMutil.getTagValue(el,"interactiveFreeText"));
    else
      return("");
    } // end read_CAPI_Messages

  
  /**
   ***********************************************************************
   * Reads the text message that appears within a <code>CAPI_Messages</code>
   * node
   ***********************************************************************
   */
  private static String readErrorMessages(final Document domReply)
    {
		final NodeList responseErrors = domReply.getElementsByTagName("responseError");
		if ((responseErrors == null) || (responseErrors.getLength() == 0) )
		{
			return null;
		}
		
		
	    		
	    final Element nodeResponseError = (Element)responseErrors.item(0);
	    final NodeList nodeList = nodeResponseError.getElementsByTagName("interactiveFreeText");
		if ((nodeList == null) || (nodeList.getLength() == 0) )
		{
			return null;
		}
	   
		final Element nodeFreeText = (Element )nodeList.item(0);
	    final String sFreeText = DOMutil.getTagValue(nodeFreeText, "freeText");
		if ((sFreeText != null) && (sFreeText.length() > 0))
		{
			return sFreeText;
		}
		else
		{
			return null;
		}
    }

  
  /**
   ***********************************************************************
   * This method is called within 72 hours of a flight's departure to retrieve
   * detailed Flight Information.
   *
   * <p>This method is implemented by using a cryptic command through the
   * Amadeus API.  Hence, the response returned is not in a structured
   * format. The string returned is subsequently parsed and processed
   * by the same method which parses the screen-scraping implementation
   * of this Conversation.
   *
   * <p>The method first builds the cryptic code command
   * for issuing a Direct Access Flifo request from the carrier, such as:
   * <code>1AADO1902/18APR</code> (flifo flight AA 1902 departing on 18APR).
   *
   * <p>It then defines several response patterns needed to page down the
   * response, which it then passes to {@link GnrcCrs.getAllHostResponses}
   *
   * @param aCRS
   *   the Computer Reservation System to which the command will be passed;
   *   in this case it should be an AmadeusAPICrs object.
   * @param aCarrier
   *   the Carried Code
   * @param aFlightNum
   *   the flight number
   * @param aDepDate
   *   a departure date in ddMMM crs format
   * @param aFlightData
   *   the string buffer in which the response will be returned
   ***********************************************************************
   */
  private void getDayOfFlightInfo(final AmadeusAPICrs aCrs,
                                  final String aCarrier,
                                  final int aFlightNum,
                                  final String aDepDate,
                                  final StringBuffer aFlightData) throws Exception
    {
    // Define the relevant cryptic code commands and TA response patterns

    /*
    final String FLIFO_REQUEST     = "<Cryptic_GetScreen_Query><Command>" +
                                     "1"  + aCarrier +
                                     "DO" + aFlightNum +
                                     "/"  + aDepDate +
                                     "</Command></Cryptic_GetScreen_Query>";
    */

    // e.g. : 1CODO1687/17MAY
    final String FLIFO_REQUEST =
      "1" + aCarrier + "DO" + aFlightNum + "/" + aDepDate;

    String sFlifoResponse = aCrs.HostTransaction(FLIFO_REQUEST);
    //String sXmlReply = aCrs.connection.sendAndReceive(FLIFO_REQUEST);

    /*
    final Document flifoReply = DOMutil.stringToDom(sXmlReply);
    Node n = flifoReply.getElementsByTagName("Response").item(0);

    String sFlifoResponse = n.getFirstChild().getNodeValue();
    */

    aFlightData.setLength(0);
    aFlightData.append(sFlifoResponse);

    } // end GetDayOfFlightInfo

  /**
   ***********************************************************************
   * Determine if this flight (carrier/flgt nr) is a multi-day flight
   *  with the first leg depart date different than the last leg arrive date
   ***********************************************************************
   */
  public boolean isMultiDayFlgt(final String sDepDate, final FlightSegment [] segs)
    {
      System.out.println("AmadeusAPIFlghtSrvcConv.isMultiDayFlgt: segs.length: " + segs.length);
      if (segs instanceof FlightSegment[])
      {
         if (segs.length == 1)          // single leg
         {
           final String frstDepDt = xmax.util.DateTime.fmtLongDateTime(
             segs[0].DepSchedDateTime,"ddMMM").toUpperCase();
           final String lstArrDt = xmax.util.DateTime.fmtLongDateTime(
             segs[0].ArrSchedDateTime,"ddMMM").toUpperCase();
           System.out.println("AmadeusAPIFlghtSrvcConv.isMultiDayFlgt1: frstDepDt/lstArrDt: " + frstDepDt + "/" + lstArrDt);
           if (frstDepDt.equals(lstArrDt) == false)
              return(true);
         }
         else if (segs.length > 1)      // multi leg
         {
           final String frstDepDt = xmax.util.DateTime.fmtLongDateTime(
             segs[0].DepSchedDateTime,"ddMMM").toUpperCase();
           final String lstArrDt = xmax.util.DateTime.fmtLongDateTime(
             segs[segs.length-1].ArrSchedDateTime,"ddMMM").toUpperCase();
           System.out.println("AmadeusAPIFlghtSrvcConv.isMultiDayFlgt2: frstDepDt/lstArrDt: " + frstDepDt + "/" + lstArrDt);
           if (frstDepDt.equals(lstArrDt) == false)
              return(true);
         }
      }

      return(false);

    } // end isMultiDayFlgt

  /**
   ***********************************************************************
   * Get Previous day date in ddMMM format
   ***********************************************************************
   */
  public String getPrevDay (final int iyy, final int imm, final int idd)
    {
        SimpleDateFormat format = new SimpleDateFormat("ddMMM, yyyy");
        int iyyyy = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
        //System.out.println("getPrevDay:yyyy/mm/dd: " + iyyyy + "/" + imm + "/" + idd);
        final GregorianCalendar gcCalcDay = new GregorianCalendar( iyyyy, imm-1, idd, 10, 0 );
        gcCalcDay.add(GregorianCalendar.DATE,-1);
        Date dCalcDay = gcCalcDay.getTime();
        final String sPrevDay = format.format(dCalcDay);
        //System.out.println("getPrevDay:sPrevDay: " + sPrevDay);
        String sDay = sPrevDay.substring(0,2);
        String sMON = sPrevDay.substring(2,5);
        String sRetnDepDt = sDay + sMON.toUpperCase();
        return(sRetnDepDt);
    }  // end getPrevDay


} // end class AmadeusAPIFlightServiceConversation