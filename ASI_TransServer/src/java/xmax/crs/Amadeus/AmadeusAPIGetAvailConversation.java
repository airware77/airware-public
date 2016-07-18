package xmax.crs.Amadeus;


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.DOMException;

import org.apache.xerces.dom.DocumentImpl;

import xmax.crs.Availability.*;
import xmax.crs.GnrcParser;
import xmax.crs.GdsResponseException;
import xmax.TranServer.ReqGetAvail;
import xmax.TranServer.GnrcFormat;
import xmax.TranServer.TranServerException;
import xmax.util.xml.DOMutil;
import xmax.util.RegExpMatch;

import java.text.SimpleDateFormat;

// for debug
import xmax.util.FileStore;
import java.io.File;

/**
 ***********************************************************************
 * A conversation with the Amadeus API aimed at fulfilling a 
 * 'Get Availability' request
 * 
 * 
 * @author   Philippe Paravicini
 * @author   David Fairchild
 * @version  1.0 Copyright (c) 2001
 *
 * @see 
 ***********************************************************************
 */
public class AmadeusAPIGetAvailConversation
{
  /** the Amadeus Equipment code for a train */
  public static final String TRAIN_CODE = "TRN";


  /**
   ***********************************************************************
   * This class calls:<br>
   * {@link getAvailRequest} to build the xml string  to be sent to the 
   * Amadeus server,<br>
   * then calls {@link AmadeusAPICrs.HostTransaction} to execute the request,<br>
   * and finally calls {@link readAvail} to parse the xml response and
   * populate the response into the DestAvailability object
   *
   * @param aCrs
   *   the AmadeusAPICrs object through which we are connecting to the Amadeus server
   *
   * @param aAvail
   *   the DestAvailability object that contains the request's paramaters
   *   and which will store the response
   *
   * @see getAvailRequest
   * @see readAvail
   * @see xmax.crs.Amadeus.AmadeusAPICrs.HostTransaction
   ***********************************************************************
   */
  public void getAvailability(final AmadeusAPICrs aCrs,
                              final DestAvailability aAvail)
                                throws Exception
    {
    Document domQuery = getAvailRequest(aAvail);
    //String sQuery = DOMutil.domToString(domQuery);

    if ( !(domQuery instanceof Document) )
      throw new TranServerException("Unable to create availability request");

    Document domMoveDown = getMoveDownRequest(domQuery);
    //String sMD    = DOMutil.domToString(domMoveDown);

    if ( !(domMoveDown instanceof Document) )
      throw new TranServerException("Unable to create 'Move Down' request");

    int iNumReqUnfilled = 0;
    int iNumItins       = 0;
    final String NEWLINE = System.getProperty("line.separator");

    Document domReply;
    boolean triedOnce = false;

    while ( aAvail.isComplete() == false )
      {

      String sQuery = DOMutil.domToString(domQuery);
      String sReply = aCrs.connection.sendAndReceive(sQuery);
      domReply      = DOMutil.stringToDom(sReply);

      if ( aAvail.RawData instanceof String )
        aAvail.RawData += NEWLINE + sReply;
      else
        aAvail.RawData = sReply;

      readAvail(domReply, aAvail);

      if( hasNoMoreItins(domReply) )
        {
        if (aAvail.getNumGoodItins() == 0 && 
            aAvail.getErrors().toString() == "")
          {
          aAvail.addError("No Flights Found");
          }
        break;
        }

      // if an error is detected in the body of the response, 
      String sError = getError(domReply);
      if( sError instanceof String )
        {
        // if we get some sort of 'RETRY' message, go to sleep and 
        // try again in 30 seconds, if we have not already tried
        if (sError.indexOf("RETRY") >= 0 && triedOnce == false)
          {
          Thread.sleep(30*1000);
          triedOnce = true;
          continue;
          }
        else
          {
          // if the error relates to a specific flight, log the error,
          // and keep going with subsequent move-downs
          aAvail.addError(sError);
          break;
          }
        }

      // keep track of the times that we requested for more itins, but we
      // did not get an itin that fits the criteria provided in the Request
      if ( iNumItins == aAvail.getNumGoodItins() )
        iNumReqUnfilled++;
      else
        {
        iNumReqUnfilled = 0;
        iNumItins = aAvail.getNumGoodItins();
        }

      if ( iNumReqUnfilled > 5) break;

      domQuery = domMoveDown;
      }

    } // end getAvailability

  /**
   ***********************************************************************
   * This method builds a DOM query that will be passed to the 
   * Amadeus XML server, based on the DestAvailability request paramaters
   * 
   * @param aAvail  
   *   the DestAvailability object that contains the request's paramaters
   *   and which will store the response
   ***********************************************************************
   */
  private Document getAvailRequest(DestAvailability aAvail) throws TranServerException
    {
    // make sure that we have the required elements
    if ( GnrcFormat.IsNull(aAvail.ReqDepDate) )
      throw new TranServerException("Must specify a departure date for availability");
 
    if ( GnrcFormat.IsNull(aAvail.ReqDepCity) )
      throw new TranServerException("Must specify a departure city for availability");
 
    if ( GnrcFormat.IsNull(aAvail.ReqArrCity) )
      throw new TranServerException("Must specify an arrival city for availability");

    String sDepDate = "";
    // convert the date format from ddMMM to ddMMyy
    sDepDate = AmadeusAPICrs.fmt_ddMMM_To_ddMMyy(aAvail.ReqDepDate);

    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element elm1, elm11, elm111;
    Element root = domQuery.createElement("PoweredAir_MultiAvailability");

    // the messageActionDetails node
    elm1 = domQuery.createElement("messageActionDetails");
    elm11 = domQuery.createElement("functionDetails");
    DOMutil.addTextElement(domQuery,elm11,"actionCode","44");
    elm1.appendChild(elm11);
    root.appendChild(elm1);

    // the requestSection node
    elm1 = domQuery.createElement("requestSection");

    //// availabilityProductInfo
    elm11 = domQuery.createElement("availabilityProductInfo");

    elm111 = domQuery.createElement("availabilityDetails");
    DOMutil.addTextElement(domQuery,elm111,"departureDate",sDepDate);
    if ( GnrcFormat.NotNull(aAvail.ReqDepTime) )
      DOMutil.addTextElement(domQuery,elm111,"departureTime",aAvail.ReqDepTime);
    elm11.appendChild(elm111);

    elm111 = domQuery.createElement("departureLocationInfo");
    DOMutil.addTextElement(domQuery,elm111,"cityAirport",aAvail.ReqDepCity);
    elm11.appendChild(elm111);

    elm111 = domQuery.createElement("arrivalLocationInfo");
    DOMutil.addTextElement(domQuery,elm111,"cityAirport",aAvail.ReqArrCity);
    elm11.appendChild(elm111);

    elm1.appendChild(elm11);

    // if we are targeting a specific class of service or a private fare
    if ( GnrcFormat.NotNull(aAvail.ReqClassOfService) )
      {
      elm11  = domQuery.createElement("optionClass");
      elm111 = domQuery.createElement("productClassDetails");
      DOMutil.addTextElement(elm111,"serviceClass",aAvail.ReqClassOfService);
      elm11.appendChild(elm111);
      elm1.appendChild(elm11);
      }

    // use Amadeus' airline filtering
    /*
    if ( GnrcFormat.NotNull(aAvail.ReqDirectAccessCarrier) &&
         aAvail.ReqAvailType.equals(ReqGetAvail.AVAIL_FILTER) )
      {
      elm11 = domQuery.createElement("airlineOrFlightOption");
      elm111 = domQuery.createElement("flightIdentification");
      DOMutil.addTextElement(domQuery,elm111,"airlineCode",aAvail.ReqDirectAccessCarrier);
      elm11.appendChild(elm111);
      elm1.appendChild(elm11);
      }
    */

    // availabilityOptions
    elm11 = domQuery.createElement("availabilityOptions");

    elm111 = domQuery.createElement("productTypeDetails");
    DOMutil.addTextElement(domQuery,elm111,"typeOfRequest","TN");
    elm11.appendChild(elm111);

    // use Biased Avail
    if ( GnrcFormat.NotNull(aAvail.ReqDirectAccessCarrier) &&
         aAvail.ReqAvailType.equals(ReqGetAvail.AVAIL_FILTER) )
      {
      elm111 = domQuery.createElement("optionInfo");
      DOMutil.addTextElement(domQuery,elm111,"type","BIA");
      DOMutil.addTextElement(domQuery,elm111,"arguments",aAvail.ReqDirectAccessCarrier);
      elm11.appendChild(elm111);
      }

    // use Direct Access Avail calls
    if ( GnrcFormat.NotNull(aAvail.ReqDirectAccessCarrier) &&
         aAvail.ReqAvailType.equals(ReqGetAvail.AVAIL_DIRECT) ) 
      {
      elm111 = domQuery.createElement("optionInfo");
      DOMutil.addTextElement(domQuery,elm111,"type","DIR");
      DOMutil.addTextElement(domQuery,elm111,"arguments",aAvail.ReqDirectAccessCarrier);
      elm11.appendChild(elm111);
      }

    elm1.appendChild(elm11);

    root.appendChild(elm1); // end requestSection

    // add the root to the top level document  
    domQuery.appendChild(root);
    //System.out.println(DOMutil.domToString(domQuery));

    return(domQuery);

    } // end getAvailRequest


  /**
   ***********************************************************************
   * This method builds a Move Down request; 
   ***********************************************************************
   */
  private Document getMoveDownRequest(Document domQuery) throws DOMException
    {
    Document domMoveDown = (Document)domQuery.cloneNode(true);
    Node nodeAction = domMoveDown.getElementsByTagName("actionCode").item(0);
    nodeAction.getFirstChild().setNodeValue("55");
    return(domMoveDown);

    /*
    // The following gives a 'DISPLAY NOT SCROLLABLE ERROR'
    // create a document object and our top level Element
    DocumentImpl domMoveDown = new DocumentImpl();
    Element elm1, elm11;
    Element root = domMoveDown.createElement("PoweredAir_MultiAvailability");

    // the messageActionDetails node
    elm1 = domMoveDown.createElement("messageActionDetails");
    elm11 = domMoveDown.createElement("functionDetails");
    DOMutil.addTextElement(domMoveDown,elm11,"actionCode","55");
    elm1.appendChild(elm11);
    root.appendChild(elm1);
    domMoveDown.appendChild(root);
    return(domMoveDown);
    */

    } // end getMoveDownRequest


  /**
   ***********************************************************************
   * This method parses the XML reply from the Amadeus XML server, 
   * and populates the DestAvailability object.
   * 
   * @param sXmlResponse  
   *   the xml String response returned by the server
   ***********************************************************************
   */
  private void readAvail(Document domReply, DestAvailability destAvail) 
    {
    NodeList flightNodes = domReply.getElementsByTagName("flightInfo");

    ItinAvailability itinAvail = null;

    int iFlightNodesNum = flightNodes.getLength();

    for (int i=0; i < flightNodes.getLength(); i++)
      {
      Element elmSegment = (Element)flightNodes.item(i);

      // parse the current segment
      FlightAvailability flightAvail = scanFlightSegment(elmSegment);

      if( isFirstSegment(elmSegment) )
        {
        // add the prior available itinAvail
        if ( itinAvail != null && itinAvail.getNumSegments() > 0
             && hasTrainSegment(itinAvail) == false)
          destAvail.addItin(itinAvail);

        // create a new itinAvail for the next batch
        itinAvail = new ItinAvailability();
        }
      
      itinAvail.addFlightAvailability(flightAvail);
      } // end for

    // add the last itinAvail created
    if (itinAvail instanceof ItinAvailability &&
        itinAvail.getNumSegments() > 0        && 
        hasTrainSegment(itinAvail) == false)
      {
      destAvail.addItin(itinAvail);
      }


    } // end readAvail


  /**
   ***********************************************************************
   * This method parses a flight node within an XML string sent by the 
   * Amadeus XML server, and creates and populates a FlightAvailability object,
   * according to the mapping and business rules stated below.
   *
   * @param flightNode  
   *   a Node defined by <code>flightInfo</code> tags
   ***********************************************************************
   */
  private FlightAvailability scanFlightSegment(Element segment)
    {
    FlightAvailability flightAvail = new FlightAvailability();

    Element basicFlightInfo = 
      (Element)segment.getElementsByTagName("basicFlightInfo").item(0);

    Element adtnlFlightInfo = 
      (Element)segment.getElementsByTagName("additionalFlightInfo").item(0);

    Element el;

    String sLineNum = DOMutil.getTagValue(basicFlightInfo, "lineItemNumber");
    if (sLineNum instanceof String)
      flightAvail.LineNum   = new Integer(sLineNum).intValue();

    // Required fields - a FlightAvail is useless without them
    el = (Element)basicFlightInfo.getElementsByTagName("marketingCompany").item(0);
    flightAvail.Carrier   = DOMutil.getTagValue(el, "identifier");
    if ( !(flightAvail.Carrier instanceof String) ) return(null);

    el = (Element)basicFlightInfo.getElementsByTagName("flightIdentification").item(0);
    flightAvail.FlightNum = Integer.parseInt( DOMutil.getTagValue(el, "number") );
    if ( flightAvail.FlightNum == 0 ) return(null);

    el = (Element)basicFlightInfo.getElementsByTagName("departureLocation").item(0);
    flightAvail.DepCity   = DOMutil.getTagValue(el, "cityAirport");
    if ( !(flightAvail.DepCity instanceof String) ) return(null);

    el = (Element)basicFlightInfo.getElementsByTagName("arrivalLocation").item(0);
    flightAvail.ArrCity   = DOMutil.getTagValue(el, "cityAirport");
    if ( !(flightAvail.ArrCity instanceof String) ) return(null);

    el = (Element)basicFlightInfo.getElementsByTagName("flightDetails").item(0);
    String sDepDate = DOMutil.getTagValue(el,"departureDate");
    String sDepTime = DOMutil.getTagValue(el,"departureTime"); 
    String sArrDate = DOMutil.getTagValue(el,"arrivalDate");
    String sArrTime = DOMutil.getTagValue(el,"arrivalTime");

    // convert the Departure/Arrival Date & Time Strings into a long - 
    // these fields are required as well
    // Note that dates are provided as ddMMyy or 241201 (dec 24 2001)
    // in Powered_Availability, while in Flifo they are returned as
    // ddMMMyyyy or 24DEC2001, lovely eh?
    try 
      {
      flightAvail.DepDate = AmadeusAPICrs.fmtDateTimeToLong(sDepDate,sDepTime, "ddMMyyHHmm");
      flightAvail.ArrDate = AmadeusAPICrs.fmtDateTimeToLong(sArrDate,sArrTime, "ddMMyyHHmm");
      }
    catch(Exception e) { return(null); }

    // And now the Optional Fields:

    // is this flight a Code Share?
    el = (Element)basicFlightInfo.getElementsByTagName("operatingCompany").item(0);
    if (el instanceof Element)
      {
      flightAvail.hasSharedCarr = true;
      flightAvail.SharedCarrCode = DOMutil.getTagValue(el,"identifier");
      }

    // get the extended flight information
    el = (Element)adtnlFlightInfo.getElementsByTagName("flightDetails").item(0);
    flightAvail.Equipment = DOMutil.getTagValue(el,"typeOfAircraft");
    flightAvail.NumStops = 
      new Integer(DOMutil.getTagValue(el,"numberOfStops")).intValue();

    flightAvail.EquipChangeCity = DOMutil.getTagValue(el,"firstChangeOfGauge");
    if ( GnrcFormat.NotNull(flightAvail.EquipChangeCity) )
      flightAvail.hasEquipChange = true;

    NodeList productFacilities = adtnlFlightInfo.getElementsByTagName("productFacilities");
    for (int i=0; i < productFacilities.getLength(); i++)
      {
      el = (Element)productFacilities.item(i);
      if ( DOMutil.getTagValue(el,"type").equals("M") )
        {
        flightAvail.Meal = "M";
        break;
        }
      } // end for

    // Last but not least, scan the inventory classes for this segment
    // and add them to the flightAvail object
    scanClasses(flightAvail,segment);
  
    return(flightAvail);

    } // end scanFlightSegment


  /**
   ***********************************************************************
   * Returns true if the <code>lineItemNumber</code> of the 
   * <code>flightInfo</code> that was passed is not null, thereby indicating
   * that this is the first segment in the itinerary.
   * 
   * @param elmSegment  
   *   an Element defined by <code>flightInfo</code> tags
   ***********************************************************************
   */
  private boolean isFirstSegment(Element segment) 
    {
    if(DOMutil.getTagValue(segment, "lineItemNumber") instanceof String)
      return(true);
    else
      return(false);

    } // end isFirstSegment


  /**
   ***********************************************************************
   * This method parses the classes found in the segment
   * {@link org.w3c.dom.Element Element} passed, and adds them 
   * to the {@link FlightAvailability} provided, as described below.
   *
   * An <code>infoOnClasses</code> Element returned by the Amadeus API can
   * contain multiple <code>productClassDetail</code> Elements representing the
   * different inventory classes available.
   *
   * <p>A <code>productClassDetail</code> element can contain the following elements:
   * <ul>
   *   <li><code>serviceClass</code>: Class of Service, 1 Character code</li>
   *   <li><code>availabilityStatus</code>: 1 to 3 alpha or numeric Character code</li>
   *   <li><code>modifier</code>: "1" and/or "N" </li>
   * </ul>
   *
   * <p>For more on the meaning of these codes consult the Amadeus API documentation.</p>
   *
   * <p>These three codes are concatenated into a string and added to the 
   * FlightAvailability object via the 
   * {@link FlightAvailability.addAvailability} method
   * 
   * @param flightAvail  
   *   the FlightAvailability to be populated
   *
   * @param segment
   *   a segment Element contained within <code>flightInfo</code> tags
   *   returned by the Amadeus API 'Powered_MultiAvailability'
   ***********************************************************************
   */
  private void scanClasses(FlightAvailability flightAvail, Element segment) 
    {
    NodeList classNodes = segment.getElementsByTagName("productClassDetail");
    Element elmClass;
    String sInvClass;
    String str;

    for (int i=0; i < classNodes.getLength() ; i++)
      {
      elmClass = (Element)classNodes.item(i); 

      sInvClass  = DOMutil.getTagValue(elmClass, "serviceClass");
      if (! (sInvClass instanceof String) ) break;

      str = DOMutil.getTagValue(elmClass, "availabilityStatus");
      if (str instanceof String ) sInvClass += str;

      str = DOMutil.getTagValue(elmClass, "modifier");
      if (str instanceof String) sInvClass += str;

      flightAvail.addAvailability(sInvClass);
      } // end for

    } // end scanClasses


  /**
   ***********************************************************************
   * This method checks an Amadeus dom Response and returns <code>true</code>
   * if it contains a string pattern that indicates that there are no more
   * itins to display for a given availability request.
   * As of this writing, there is no clear documentation as to what the 
   * possible error messages might be. 
   * This method detects the following error codes: 
   * <ul>
   *   <li><code>A7V</code> contained within a  
   *       <pre><cityPairErrorOrWarningInfo><error></pre> tag.</li>
   *   <li><code>ZZZ</code> contained within a 
   *       <pre><flightErrorWarningInfo><error></pre> tag.</li>
   * </ul>
   * which indicate that there are no more itineraries to display.
   ***********************************************************************
   */
  private boolean hasNoMoreItins(Document domReply)
    {
    Element elmError;
    elmError  = (Element)domReply.getElementsByTagName("cityPairErrorOrWarningInfo").item(0);
    if (elmError instanceof Element)
      {
      elmError = (Element)elmError.getElementsByTagName("error").item(0);
      if (elmError instanceof Element)
        {
        String sErrorCode = DOMutil.getTagValue(elmError, "code");
        if ( sErrorCode.equals("A7V") )
          return(true);
        }
      }
    /*
     // this detects a type of error generated by doing an availability through DL
     elmError = (Element)domReply.getElementsByTagName("flightErrorWarningInfo").item(0);
     if (elmError instanceof Element)
       {
       elmError = (Element)elmError.getElementsByTagName("error").item(0);
       if (elmError instanceof Element)
         {
         String sErrorCode = DOMutil.getTagValue(elmError, "code");
         if ( sErrorCode.equals("ZZZ") )
           return(true);
         }
       }
  
     The following code indicates that the display is not scrollable, but
     such error should be trapped at the 'sendAndReceive' level, since such a
     response should return an error code as well. Hence, it is redundant here
     and has been commented out.
        
     if ( domReply.getDocumentElement().getTagName().equals("MessagesOnly_Reply") )
       {
       elmError = (Element)domReply.getDocumentElement().getElementsByTagName("ErrorCode").item(0);
       // error 10792 : REQUESTED DISPLAY NOT SCROLLABLE
       if (elmError.getFirstChild().getNodeValue().equals("10792"))
           return(true);
       }
    */
    return(false);

    } // end hasNoMoreItins


  /**
   ***********************************************************************
   * This method checks for the existence of errors within a
   * PoweredAir_MultiAvailabilityReply from the Amadeus API, and returns
   * the first Error that it encounters, or <code>null</code> if it does
   * not detect any error.
   * <p>
   * The API deals with errors in very diverse manners, thus providing for lots
   * of coding excitement.  As far as I can tell, if the response returned
   * is a MessagesOnly_Reply, such reply will be accompanied by an error code,
   * and will thus be trapped in {@link AmadeusAPICrsConnection.sendAndReceive}.</p>
   * <p>
   * Nevertheless, it is possible that the response may contain some sort of
   * less severe error (such as 'LOST RESPONSE - RETRY') which will not 
   * generate an error code on the reply, but which should cause the 
   * {@link #getAvailability} method to break, as any subsequent response
   * will merely repeat the error.</p>
   * <p>
   * Since the availability relies on Direct Access queries, some of the
   * errors may be specific to a specific airline.  The errors detected
   * in the method herewith were derived from empirical observation of the
   * errors returned by the Amadeus API, and may not be include all the errors
   * that could be returned by the scantily documented Amadeus API.</p>
   ***********************************************************************
   */
  private String getError(Document domReply)
    {
    Element elmErrorSection, el;
    String sErr, code, type, freeText, codedIndicator, typeOfInfo;

    // check for errorOrWarningSection
    elmErrorSection = (Element)domReply.getElementsByTagName(
        "errorOrWarningSection").item(0);
    if (elmErrorSection instanceof Element)
      {
      el = (Element)elmErrorSection.getElementsByTagName("error").item(0);
      if (el instanceof Element)
        {
        code = DOMutil.getTagValue(el, "code");
        //type = DOMutil.getTagValue(el, "type");
        if ( code instanceof String )
          {
          el = (Element)elmErrorSection.getElementsByTagName(
              "textInformation").item(0);
          freeText = DOMutil.getTagValue(el, "freeText");

          /*
          el = (Element)elmErrorSection.getElementsByTagName(
              "freeTextQualification").item(0);
          codedIndicator = DOMutil.getTagValue(el, "codedIndicator");
          typeOfInfo     = DOMutil.getTagValue(el, "typeOfInfo");
          */

          sErr = "errorOrWarning" +
                 " - code: "           + code +
                 // " - type: "           + type +
                 // " - codedIndicator: " + codedIndicator +
                 // " - typeOfInfo: "     + typeOfInfo +
                 " - freeText: "       + freeText;
          return(sErr);
          }
        }
      }

    // check for cityPairErrorOrWarning
    elmErrorSection = (Element)domReply.getElementsByTagName(
        "cityPairErrorOrWarning").item(0);
    if (elmErrorSection instanceof Element)
      {
      el = (Element)elmErrorSection.getElementsByTagName("error").item(0);
      if (el instanceof Element)
        {
        code = DOMutil.getTagValue(el, "code");
        //type = DOMutil.getTagValue(el, "type");
        if ( code instanceof String )
          {
          el = (Element)elmErrorSection.getElementsByTagName(
              "cityPairErrorOrWarningText").item(0);
          freeText = DOMutil.getTagValue(el, "freeText");

          /*
          el = (Element)elmErrorSection.getElementsByTagName(
              "freeTextQualification").item(0);
          codedIndicator = DOMutil.getTagValue(el, "codedIndicator");
          typeOfInfo     = DOMutil.getTagValue(el, "typeOfInfo");
          */

          sErr = "cityPairErrorOrWarning" +
                 " - code: "           + code +
          /*
                 " - type: "           + type +
                 " - codedIndicator: " + codedIndicator +
                 " - typeOfInfo: "     + typeOfInfo +
          */
                 " - freeText: "       + freeText;
          return(sErr);
          }
        }
      }

    /*
    // The flightErrorWarningSection contains errors, warnings, and sometimes even
    // marketing messages.  Most of these seem to be non-fatal type of errors, and
    // it appears that they can be safely ignored, unlike the two types of errors
    // above.

    // check for flightErrorWarningSection
    elmErrorSection = (Element)domReply.getElementsByTagName(
        "flightErrorWarningSection").item(0);
    if (elmErrorSection instanceof Element)
      {
      el = (Element)elmErrorSection.getElementsByTagName("error").item(0);
      if (el instanceof Element)
        {
        code = DOMutil.getTagValue(el, "code");
        //type = DOMutil.getTagValue(el, "type");
        if ( code instanceof String )
          {
          el = (Element)elmErrorSection.getElementsByTagName(
              "flightErrorWarningText").item(0);
          freeText = DOMutil.getTagValue(el, "freeText");

          // ignore 'OPERATED BY' warnings
          try
            { 
            if ( RegExpMatch.matches(freeText, "OPERATED BY") )
              return(null);
            }
          catch (Exception e)
            {
            System.err.println(e.getMessage());
            e.printStackTrace();
            }

        //  el = (Element)elmErrorSection.getElementsByTagName(
        //      "freeTextQualification").item(0);
        //  codedIndicator = DOMutil.getTagValue(el, "codedIndicator");
        //  typeOfInfo     = DOMutil.getTagValue(el, "typeOfInfo");

          sErr = "flightErrorWarningText" +
                 " - code: "           + code +
                 // " - type: "           + type +
                 // " - codedIndicator: " + codedIndicator +
                 // " - typeOfInfo: "     + typeOfInfo +
                 " - freeText: "       + freeText;
          return(sErr);
          }
        }
      }
    */

    return(null);

    } // end getError


  /**
   ***********************************************************************
   * Checks to see whether any of the {@link FlightAvailability} segments
   * included in the {@link ItinAvailability} provided contains a train segment
   ***********************************************************************
   */
  public boolean hasTrainSegment(ItinAvailability itinAvail)
    {
    FlightAvailability[] aryFlight = itinAvail.getFlightAvailabilities();

    for (int i=0; i < aryFlight.length ; i++)
      {
      if (aryFlight[i].Equipment.equals(TRAIN_CODE))
        return true;
      } 

    return false;
    } // end hasTrainSegment


/*
 ***********************************************************************
 * The following methods were written to use the old avail API functions
 * which have been deprecated in favor of the PoweredAir_MultiAvailability
 * functions.  They have been left just in case for reference, but should
 * be obliterated from the source code at a suitable time.
 ***********************************************************************
 */

//  /**
//   ***********************************************************************
//   * This method parses the XML reply from the Amadeus XML server, 
//   * and populates the DestAvailability object.
//   * 
//   * @param sXmlResponse  
//   *   the xml String response returned by the server
//   ***********************************************************************
//   */
//  private void readAvail(Document domReply, DestAvailability destAvail) 
//    {
//    NodeList flightNodes = domReply.getElementsByTagName("CAPI_Flights");
//
//    ItinAvailability itinAvail = null;
//    FlightAvailability flightAvail = null;
//    Element elmSegment = null;
//
//    for (int i=0; i < flightNodes.getLength(); i++)
//      {
//      elmSegment = (Element)flightNodes.item(i);
//
//      // parse the current segment
//      flightAvail = scanFlightSegment(elmSegment);
//
//      if( isFirstSegment(elmSegment) )
//        {
//        itinAvail = new ItinAvailability();
//        destAvail.addItin(itinAvail);
//        }
//      
//      itinAvail.addFlightAvailability(flightAvail);
//      } // end for
//
//    } // end readAvail


//  /**
//   ***********************************************************************
//   * This method builds a DOM query that will be passed to the 
//   * Amadeus XML server, based on the DestAvailability request paramaters
//   * 
//   * @param aAvail  
//   *   the DestAvailability object that contains the request's paramaters
//   *   and which will store the response
//   ***********************************************************************
//   */
//  private Document getAvailRequest(DestAvailability aAvail) throws TranServerException
//    {
//    // make sure that we have the required elements
//    if ( GnrcFormat.IsNull(aAvail.ReqDepDate) )
//      throw new TranServerException("Must specify a departure date for availability");
// 
//    if ( GnrcFormat.IsNull(aAvail.ReqDepCity) )
//      throw new TranServerException("Must specify a departure city for availability");
// 
//    if ( GnrcFormat.IsNull(aAvail.ReqArrCity) )
//      throw new TranServerException("Must specify an arrival city for availability");
//
//    // create a document object and our top level Element
//    Document domQuery = new DocumentImpl();
//    Element root = domQuery.createElement("Air_Availability_Query");
//    Element item;
//    
//    String sDepDate = "";
//    try
//      {
//      // convert the date format from ddMMM to ddMMMyyyy
//      sDepDate = AmadeusAPICrs.fmt_ddMMM_To_ddMMMyyyy(aAvail.ReqDepDate);
//      }
//    catch(ParseException e)
//      {
//      throw new TranServerException("Unable to properly parse the departure date for availability");
//      }
//
//    // add the mandatory elements
//    item = domQuery.createElement("DepartureDate");
//    item.appendChild( domQuery.createTextNode(sDepDate) );
//    root.appendChild(item);
//
//    item = domQuery.createElement("FromCity");
//    item.appendChild( domQuery.createTextNode(aAvail.ReqDepCity) );
//    root.appendChild(item);
//
//    item = domQuery.createElement("ToCity");
//    item.appendChild( domQuery.createTextNode(aAvail.ReqArrCity) );
//    root.appendChild(item);
//
//    //add the optional elements
//    if ( GnrcFormat.NotNull(aAvail.ReqDirectAccessCarrier) )
//      {
//      item = domQuery.createElement("DirectAccess");
//      item.appendChild( domQuery.createTextNode("T") );
//      root.appendChild(item);
//
//      item = domQuery.createElement("AirlinePref1");
//      item.appendChild( domQuery.createTextNode(aAvail.ReqDirectAccessCarrier) );
//      root.appendChild(item);
//      }
//
//    if ( GnrcFormat.NotNull(aAvail.ReqDepTime) )
//      {
//      item = domQuery.createElement("DepartureTime");
//      item.appendChild( domQuery.createTextNode(aAvail.ReqDepTime) );
//      root.appendChild(item);
//      }
//
//    // add the root to the top level document  
//    domQuery.appendChild(root);
//
//    return(domQuery);
//
//    } // end getAvailRequest

//  /**
//   ***********************************************************************
//   * This method parses a flight node within an XML string sent by the 
//   * Amadeus XML server, and creates and populates a FlightAvailability object,
//   * according to the mapping and business rules stated below.
//   *
//   * Mappings and Business Rules:<br>
//   * Required fields are <b>bolded</b>. The method returns a null value
//   * if any of the required fields are missing.
//   * <table>
//   * <!-- Visualize a 3-column HTML table with the following table headers  -->
//   * <tr>
//   *   <th>FlightAvail. Field</th><th>Amadeus API Element</th>
//   *   <th>Business Rules</th>
//   * </tr>
//   * <tr>
//   *   <td class=code>LineNum</td><td class=code>LineNum</td>
//   *   <td>The existence of a <code>LineNum</code> tag within a <code>CAPI_Flight</code> 
//   *       indicates that this is the first segment in an itin. Following segments
//   *       that are returned without a LineNum belong to the same itin, and the
//   *       TranServer populates this Line Number in the <code>LineNum</code> field 
//   *       of each <code>FlightAvailability</code> object.</td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>Carrier</b></td><td class=code><b>Airline1</b></td>
//   *   <td>The carrier that 'sells' the ticket is listed as Airline1; in
//   *       the case of a CodeShare, the 'operating' airline would be listed
//   *       in <code>Airline2</code> or <code>Airline3</code></td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>FlightNum</b></td><td class=code><b>FlightNum</b></td>
//   *   <td></td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>DepCity</b></td><td class=code><b>From</b></td>
//   *   <td></td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>ArrCity</b></td><td class=code><b>To</b></td>
//   *   <td></td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>DepDate</b></td><td class=code><b>DepartureDate/DepartureTime</b></td>
//   *   <td>The Departure Date and Time Strings retured are concatenated, converted
//   *       to a <code>long</code> date, and stored as such in the 
//   *       <code>FlightAvailability</code> object</td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>ArrDate</b></td><td class=code><b>ArrivalDate/ArrivalTime</b></td>
//   *   <td>Stored as a <code>long</code> as for <code>DepDate</code>.  If the
//   *       reply returned from Amadeus does not contain an <code>ArrivalDate</code>
//   *       tag (indicating that the flight arrives on the same day that it leaves),
//   *       the <code>DepartureDate</code> is used for creating the <code>DepDate</code> field.
//   *   </td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>Equipment/hasEquipChange</b></td><td class=code><b>Equipment</b></td>
//   *   <td>If the value returned for this field is <code>CHG</code> or <code>EQV</code>
//   *       then this indicates that there is a change of equipment.  In such case,
//   *       the value of <code>FlightAvailability.Equipment</code> is set to <code>null</code>
//   *       and the value of <code>hasEquipChange</code> is set to <code>true</code>.
//   *       This eventually triggers a flifo request for this segment in the
//   *       method <code>ReqGetAvail.getExtendedSegmentInfo</code>.
//   *   </td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>Meal</b></td><td class=code><b>MealService</b></td>
//   *   <td>In the screen-scraping Amadeus version, the <code>Meal</code> field
//   *       is only set if the segment is flifoed.</td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>NumStops</b></td><td class=code><b>NumOfStops</b></td>
//   *   <td></td>
//   * </tr>
//   * <tr>
//   *   <td class=code>isCharter</b></td><td class=code>?</b></td>
//   *   <td>This value is not set in this method.</td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>SharedCarrCode</b></td><td class=code><b>Airline2/CodeSharing</b></td>
//   *   <td>If the <code>CodeSharing</code> returns a value, then this indicates
//   *       that this segment is operated by another airline, which is listed in
//   *       the <code>Airline2</code> field.
//   *   </td>
//   * </tr>
//   * <tr>
//   *   <td class=code><b>hasSharedCarr</b></td><td class=code><b>CodeSharing</b></td>
//   *   <td>If the <code>CodeSharing</code> tag is returned with a value, then
//   *       this indicates that this segment is operated by another airline. In such
//   *       case the <code>hasSharedCarr</code> flag is set to <code>true</code>, 
//   *       which will eventually trigger a flifo request for this segment in the
//   *       method <code>ReqGetAvail.getExtendedSegmentInfo</code>.
//   *   </td>
//   * </tr>
//   * </table>
//   * 
//   * @param flightNode  
//   *   a Node defined by <code>CAPI_Flights</code> tags
//   ***********************************************************************
//   */
//  private FlightAvailability scanFlightSegment(Element segment)
//    {
//    FlightAvailability flightAvail = new FlightAvailability();
//
//    String sLineNum = DOMutil.getTagValue(segment, "LineNum");
//    if (sLineNum instanceof String)
//      flightAvail.LineNum   = new Integer(sLineNum).intValue();
//
//    // Required fields - a FlightAvail is useless without them
//    flightAvail.Carrier   = DOMutil.getTagValue(segment, "Airline1");
//    if ( !(flightAvail.Carrier instanceof String) ) return(null);
//
//    flightAvail.FlightNum = DOMutil.getTagValue(segment, "FlightNum");
//    if ( !(flightAvail.FlightNum instanceof String) ) return(null);
//
//    flightAvail.DepCity   = DOMutil.getTagValue(segment, "From");
//    if ( !(flightAvail.DepCity instanceof String) ) return(null);
//
//    flightAvail.ArrCity   = DOMutil.getTagValue(segment, "To");
//    if ( !(flightAvail.ArrCity instanceof String) ) return(null);
//
//    // convert the Departure Date & Time Strings into a long - 
//    // these fields are required as well
//    String sDepDate = DOMutil.getTagValue(segment,"DepartureDate").substring(0,5);
//    String sDepTime = DOMutil.getTagValue(segment,"DepartureTime"); 
//
//    try 
//      {
//      flightAvail.DepDate = GnrcParser.ScanCRSDateTimeString(sDepDate,sDepTime);
//      }
//    catch(Exception e) { return(null); }
//
//    // convert the Arrival Date (if one was returned) & Time Strings into a long
//    String sArrDate = DOMutil.getTagValue(segment,"ArrivalDate");
//    String sArrTime = DOMutil.getTagValue(segment,"ArrivalTime");
//    try
//      {
//      if (sArrDate instanceof String)
//        flightAvail.ArrDate = GnrcParser.ScanCRSDateTimeString(sArrDate,sArrTime);
//      else
//        flightAvail.ArrDate = GnrcParser.ScanCRSDateTimeString(sDepDate,sArrTime);
//      }
//    catch(Exception e) { return(null); }
//
//
//    // And now the Optional Fields:
//
//    // Meal is also set during a flifo
//    flightAvail.Meal      = DOMutil.getTagValue(segment,"MealService");
//    flightAvail.NumStops  = new Integer(DOMutil.getTagValue(segment,"NumOfStops")).intValue();
//
//    // What kind of equipment do we have? a change of Equipment
//    // will trigger a flifo in ReqGetAvail.getExtendedSegmentInfo
//    String sEquipment = DOMutil.getTagValue(segment,"Equipment");
//    if ( sEquipment.equals("CHG") || sEquipment.equals("EQV") )
//      {
//      flightAvail.Equipment = null;
//      flightAvail.hasEquipChange = true;
//      flightAvail.needsFlifo = true;
//      }
//    else
//      flightAvail.Equipment = sEquipment;
//       
//    // is this segment a CodeShare that is operated by a different airline?
//    // if so, this will trigger a flifo in ReqGetAvail.getExtendedSegmentInfo
//    if ( DOMutil.getTagValue(segment,"CodeSharing") != null )
//      {
//      flightAvail.SharedCarrCode = DOMutil.getTagValue(segment,"Airline2");
//      flightAvail.hasSharedCarr = true;
//      flightAvail.needsFlifo    = true;
//      }
//
//    // Last but not least, scan the inventory classes from this segment
//    // and add them to the flightAvail object
//    scanClasses(flightAvail,segment);
//  
//    return(flightAvail);
//
//    } // end scanFlightSegment
//
//
//  /**
//   ***********************************************************************
//   * Returns true if the <code>LineNumber</code> of the 
//   * <code>CAPI_Flights</code> that was passed is not null, thereby indicating
//   * that this is the first segment in the itinerary.
//   * 
//   * @param elmSegment  
//   *   an Element defined by <code>CAPI_Flights</code> tags
//   ***********************************************************************
//   */
//  private boolean isFirstSegment(Element segment) 
//    {
//    if(DOMutil.getTagValue(segment, "LineNum") instanceof String)
//      return(true);
//    else
//      return(false);
//
//    } // end isFirstSegment


} // end class AmadeusAPIGetAvailConversation

