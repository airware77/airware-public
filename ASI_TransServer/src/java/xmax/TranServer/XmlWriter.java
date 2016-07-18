package xmax.TranServer;

import xmax.crs.GdsResponseException;
import java.text.DecimalFormat;
import xmax.crs.PNR;
import xmax.crs.PNRFare;
import xmax.crs.Flifo.FlightInfo;
import xmax.crs.Flifo.FlightSegment;
import xmax.util.xml.DOMutil;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.apache.xerces.dom.*;
import org.apache.xerces.dom.AttrImpl;
import xmax.crs.GetPNR.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class XmlWriter
{
 private static final SimpleDateFormat fmtXmlDate     = new SimpleDateFormat("yyyy-MM-dd");
 private static final SimpleDateFormat fmtXmlTime     = new SimpleDateFormat("HH:mm:ss");
 private static final SimpleDateFormat fmtXmlDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  /**
   ***********************************************************************
   * This function returns a string suitable for sending to the client
   ***********************************************************************
   */
  public static Document getResponseDoc(final Document aDocReq, final ReqTranServer aRequest)
    {
    try
      {
      if ( aRequest.hasExceptions() )
        {
        addErrorResponseNodes(aDocReq,aRequest);
        return(aDocReq);
        }

      if ( aRequest instanceof ReqGetPNR )
        {
        reqGetPNR(aDocReq,(ReqGetPNR )aRequest);
        return(aDocReq);
        }

      if ( aRequest instanceof ReqGetFlifo )
        {
        final Document docResp = reqGetFlifo( (ReqGetFlifo )aRequest );
        if ( (docResp instanceof Document) == false )
          throw new Exception("Invalid document object for flifo");
        return(docResp);
        }

      if ( aRequest instanceof ReqGetStatus )
        {
        final Document docResp = reqGetStatus( (ReqGetStatus )aRequest );
        if ( (docResp instanceof Document) == false )
          throw new Exception("Invalid document object for get connection status");
        return(docResp);
        }

      if ( aRequest instanceof ReqEndSession )
        {
        reqDefaultResponse(aDocReq);
        return(aDocReq);
        }

      }
    catch (Exception e)
      {
      addErrorResponseNodes(aDocReq,aRequest);
      return(aDocReq);
      }

    addErrorResponseNodes(aDocReq,"Unrecognized request object " + aRequest.getClass().getName() );
    return(aDocReq);
    }

  /**
   ***********************************************************************
   * This function returns a string suitable for sending to the client
   ***********************************************************************
   */
  public static Document createErrorResponseDoc(final String aErrorText)
    {
    final Document doc = new DocumentImpl();

    final Element root = doc.createElement("GdsTransaction");
    doc.appendChild(root);

    final Element request = doc.createElement("Request");
    root.appendChild(request);

    final Element response = doc.createElement("Response");
    root.appendChild(response);

    addErrorResponseNodes(doc,aErrorText);

    return(doc);
    }

  /**
   ***********************************************************************
   * This function returns a string suitable for sending to the client
   ***********************************************************************
   */
  public static void addErrorResponseNodes(final Document aDoc, final ReqTranServer aRequest)
    {
    final Exception firstException = aRequest.getFirstException();

    final String sErrorText;
    final String sGdsErrorText;
    final int iErrorNumber;
    if ( firstException instanceof GdsResponseException )
      {
      final GdsResponseException gdsException = (GdsResponseException )firstException;
      sErrorText    = gdsException.getMessage();
      sGdsErrorText = gdsException.getResponse();
      iErrorNumber = ((GdsResponseException )firstException).getErrorNumber();
      }
    else if ( firstException instanceof TranServerException )
      {
      sErrorText    = firstException.toString();
      sGdsErrorText = "";
      iErrorNumber = ((TranServerException )firstException).getErrorNumber();
      }
    else if ( firstException instanceof Exception )
      {
      sErrorText    = firstException.toString();
      sGdsErrorText = "";
      iErrorNumber = GnrcConvControl.STS_CRS_ERR;
      }
    else
      {
      sErrorText    = "Unknown exception";
      sGdsErrorText = "";
      iErrorNumber = GnrcConvControl.STS_CRS_ERR;
      }

    addErrorResponseNodes(aDoc,sErrorText,iErrorNumber,sGdsErrorText);
    }


  public static void addErrorResponseNodes(final Document aDoc,
                                            final String aErrorText)
    {
    addErrorResponseNodes(aDoc,aErrorText,GnrcConvControl.STS_CRS_ERR,null);
    }


  private static void addErrorResponseNodes(final Document aDoc,
                                             final String aErrorText,
                                             final int aStatus,
                                             final String aGdsErrorText)
    {
    // create root node
    final Element nodeResponse = aDoc.createElement("Response");
    aDoc.getDocumentElement().appendChild(nodeResponse);

    DOMutil.addTextElement(nodeResponse,"Status",aStatus);
    DOMutil.addTextElement(nodeResponse,"ErrorText",aErrorText);
    if ( GnrcFormat.NotNull(aGdsErrorText) )
      DOMutil.addTextElement(nodeResponse,"GdsErrorText",aGdsErrorText);
    }

  /**
   ***********************************************************************
   * Get PNR
   ***********************************************************************
   */
  private static void reqDefaultResponse(final Document aDoc) throws Exception
    {
    // create root node
    final Element nodeResponse = aDoc.createElement("Response");
    aDoc.getDocumentElement().appendChild(nodeResponse);
    DOMutil.addTextElement(nodeResponse,"Status",GnrcConvControl.STATUS_OK);
    }

  /**
   ***********************************************************************
   * Get PNR
   ***********************************************************************
   */
  private static void reqGetPNR(final Document aDoc, final ReqGetPNR aRequest) throws Exception
    {
    if ( aRequest.pnr instanceof PNR )
      {
      // create root node
      final Element nodeResponse = aDoc.createElement("Response");
      aDoc.getDocumentElement().appendChild(nodeResponse);

      DOMutil.addTextElement(nodeResponse,"Status",GnrcConvControl.STATUS_OK);

      final Element nodePNR = aDoc.createElement("PNR");
      nodeResponse.appendChild(nodePNR);

      // general PNR data
      addPnrGeneralInfo(nodePNR,aRequest.pnr);
      addPnrNames(nodePNR,aRequest.pnr);
      addPnrItinSegments(nodePNR,aRequest.pnr);
      addPnrPhone(nodePNR,aRequest.pnr);
      addPnrTicket(nodePNR,aRequest.pnr);
      addPnrReceiveBy(nodePNR,aRequest.pnr);
      addPnrRemarks(nodePNR,aRequest.pnr);
      addPnrFares(nodePNR,aRequest.pnr);
      addPnrErrors(nodePNR,aRequest.pnr);
      }
    }

  /**
   ***********************************************************************
   * Add general PNR info
   ***********************************************************************
   */
  private static void addPnrGeneralInfo(final Element aRoot, final PNR aPnr)
    {
    DOMutil.addTextElement(aRoot,"CrsCode",aPnr.getCrs());
    DOMutil.addTextElement(aRoot,"PseudoCity",aPnr.getPseudoCity());
    DOMutil.addTextElement(aRoot,"Locator",aPnr.getLocator());
    DOMutil.addTextElement(aRoot,"Agent",aPnr.getAgentSign());
    }

  /**
   ***********************************************************************
   * Add PNR names
   ***********************************************************************
   */
  private static void addPnrNames(final Element aRoot, final PNR aPnr) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();
    final Element nodeNames = doc.createElement("NameSection");
    aRoot.appendChild(nodeNames);

    final PNRNameElement[] names = aPnr.getNames();
    Element nameNode;
    if ( names instanceof PNRNameElement[] )
      {
      for ( int i = 0; i < names.length; i++ )
        {
        nameNode = doc.createElement("Name");
        nodeNames.appendChild(nameNode);

        DOMutil.addTextElement(nameNode,"FamilyNumber", aPnr.getFamilyNum(names[i]) );
        DOMutil.addTextElement(nameNode,"MemberNumber", aPnr.getMemberNum(names[i]) );
        DOMutil.addTextElement(nameNode,"LastName", names[i].LastName );
        DOMutil.addTextElement(nameNode,"FirstName", names[i].FirstName );

        if ( GnrcFormat.NotNull(names[i].Title) )
          DOMutil.addTextElement(nameNode,"Title", names[i].Title );
        if ( GnrcFormat.NotNull(names[i].PTC) )
          DOMutil.addTextElement(nameNode,"PassengerType", names[i].PTC );
        if ( GnrcFormat.NotNull(names[i].getPassengerID()) )
          DOMutil.addTextElement(nameNode,"NameRemark", names[i].getPassengerID() );
        if ( GnrcFormat.NotNull(names[i].InfantName) )
          DOMutil.addTextElement(nameNode,"InfantName", names[i].InfantName );
        }
      }

    }

  /**
   ***********************************************************************
   * Add PNR itinerary segments
   ***********************************************************************
   */
  private static void addPnrItinSegments(final Element aRoot, final PNR aPnr) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();
    final Element nodeItin = doc.createElement("ItinerarySection");
    aRoot.appendChild(nodeItin);

    final PNRItinSegment[] segments = aPnr.getSegments();
    Element segmentNode;
    Attr SegNumAttr;
    if ( segments instanceof PNRItinSegment[] )
      {
      for ( int i = 0; i < segments.length; i++ )
        {
        if ( segments[i] instanceof PNRItinAirSegment )
          {
          segmentNode = doc.createElement("AirSegment");
          nodeItin.appendChild(segmentNode);
          addPnrAirSegment(segmentNode,(PNRItinAirSegment )segments[i]);
          }
        else if ( segments[i] instanceof PNRItinHotelSegment )
          {
          segmentNode = doc.createElement("HotelSegment");
          nodeItin.appendChild(segmentNode);
          addPnrHotelSegment(segmentNode,(PNRItinHotelSegment )segments[i]);
          }
        else if ( segments[i] instanceof PNRItinCarSegment )
          {
          segmentNode = doc.createElement("CarSegment");
          nodeItin.appendChild(segmentNode);
          addPnrCarSegment(segmentNode,(PNRItinCarSegment )segments[i]);
          }
        else if ( segments[i] instanceof PNRItinArunkSegment )
          {
          segmentNode = doc.createElement("ArunkSegment");
          nodeItin.appendChild(segmentNode);
          }
        else
          {
          segmentNode = doc.createElement("ItinSegment");
          nodeItin.appendChild(segmentNode);
          DOMutil.addTextElement(segmentNode,"RawText",segments[i].RawData);
          }

        segmentNode.setAttribute("SegmentNum",Integer.toString(segments[i].SegmentNumber));
        }
      }

    }

  /**
   ***********************************************************************
   * Add PNR air segment
   ***********************************************************************
   */
  private static void addPnrAirSegment(final Element aRoot, final PNRItinAirSegment aSegment) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();

    // departure info
    final Element nodeDep    = doc.createElement("Departure");

    DOMutil.addTextElement(nodeDep,"CityCode",aSegment.DepartureCityCode);
    if ( GnrcFormat.NotNull(aSegment.DepTerminal) )
      DOMutil.addTextElement(nodeDep,"Terminal",aSegment.DepTerminal);
    if ( GnrcFormat.NotNull(aSegment.DepGate) )
      DOMutil.addTextElement(nodeDep,"Gate",aSegment.DepGate);
    DOMutil.addDateTimeElement(nodeDep,"DateScheduled",aSegment.DepartureDateTime);
    DOMutil.addDateTimeElement(nodeDep,"DateEstimated",aSegment.DepEstDateTime);
    DOMutil.addDateTimeElement(nodeDep,"DateGate",aSegment.DepGateOutDateTime);
    DOMutil.addDateTimeElement(nodeDep,"DateField",aSegment.DepFieldOffDateTime);

    // arrival info
    final Element nodeArr    = doc.createElement("Arrival");

    DOMutil.addTextElement(nodeArr,"CityCode",aSegment.ArrivalCityCode);
    if ( GnrcFormat.NotNull(aSegment.DepTerminal) )
      DOMutil.addTextElement(nodeArr,"Terminal",aSegment.ArrTerminal);
    if ( GnrcFormat.NotNull(aSegment.DepGate) )
      DOMutil.addTextElement(nodeArr,"Gate",aSegment.ArrGate);
    DOMutil.addDateTimeElement(nodeArr,"DateScheduled",aSegment.ArrivalDateTime);
    DOMutil.addDateTimeElement(nodeArr,"DateEstimated",aSegment.ArrEstDateTime);
    DOMutil.addDateTimeElement(nodeArr,"DateGate",aSegment.ArrGateInDateTime);
    DOMutil.addDateTimeElement(nodeArr,"DateField",aSegment.ArrFieldOnDateTime);

    // flight info
    final Element nodeFlight = doc.createElement("Flight");

    DOMutil.addTextElement(nodeFlight,"Carrier",aSegment.Carrier);
    DOMutil.addTextElement(nodeFlight,"FlightNum",aSegment.FlightNumber);
    nodeFlight.appendChild(nodeDep);
    nodeFlight.appendChild(nodeArr);

    if ( GnrcFormat.NotNull(aSegment.CodeShareCarrCd) )
      DOMutil.addTextElement(nodeFlight,"OperatedByCarrier",aSegment.CodeShareCarrCd);
    else if ( GnrcFormat.NotNull(aSegment.CodeShareCarrDesc) )
      DOMutil.addTextElement(nodeFlight,"OperatedByCarrier",aSegment.CodeShareCarrDesc);

    if ( GnrcFormat.NotNull(aSegment.CodeShareCarrFlgt) )
      DOMutil.addTextElement(nodeFlight,"OperatedAsFlight",aSegment.CodeShareCarrFlgt);

    if ( GnrcFormat.NotNull(aSegment.Equipment) )
      DOMutil.addTextElement(nodeFlight,"Equipment",aSegment.Equipment);

    if ( GnrcFormat.NotNull(aSegment.ChangeOfGaugeEquipment) )
      DOMutil.addTextElement(nodeFlight,"ChangeOfGaugeEquipment",aSegment.ChangeOfGaugeEquipment);

    if ( GnrcFormat.NotNull(aSegment.ChangeOfGaugeCity) )
      DOMutil.addTextElement(nodeFlight,"ChangeOfGaugeCity",aSegment.ChangeOfGaugeCity);

    DOMutil.addTextElement(nodeFlight,"NumStops",aSegment.NumStops);

    if ( aSegment.Miles > 0 )
      DOMutil.addTextElement(nodeFlight,"Miles",aSegment.Miles);

    if ( GnrcFormat.NotNull(aSegment.Meals) )
      DOMutil.addTextElement(nodeFlight,"Meals",aSegment.Meals);

    if ( aSegment.ElapsedMinutes > 0 )
      DOMutil.addTextElement(nodeFlight,"FlightMinutes",aSegment.ElapsedMinutes);

    if ( GnrcFormat.NotNull(aSegment.OnTimePerformance) )
      DOMutil.addTextElement(nodeFlight,"OnTime",aSegment.OnTimePerformance);

    if ( GnrcFormat.NotNull(aSegment.DelayCode) )
      DOMutil.addTextElement(nodeFlight,"DelayCode",aSegment.DelayCode);

    // segment info
    aRoot.appendChild(nodeFlight);
    DOMutil.addTextElement(aRoot,"InvClass",aSegment.InventoryClass);
    DOMutil.addTextElement(aRoot,"SegmentStatus",aSegment.Status);
    DOMutil.addTextElement(aRoot,"NumSeats",aSegment.NumberOfSeats);

    if ( GnrcFormat.NotNull(aSegment.RemoteLocator) )
      DOMutil.addTextElement(aRoot,"RemoteLocator",aSegment.RemoteLocator);
    }

  /**
   ***********************************************************************
   * Add PNR hotel segment
   ***********************************************************************
   */
  private static void addPnrHotelSegment(final Element aRoot, final PNRItinHotelSegment aSegment) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();

    // hotel info
    final Element nodeHotel = doc.createElement("Hotel");

    DOMutil.addTextElement(nodeHotel,"ChainCode",aSegment.ChainCode);

    if ( GnrcFormat.NotNull(aSegment.PropertyCode) )
      DOMutil.addTextElement(nodeHotel,"PropertyCode",aSegment.PropertyCode);

    if ( GnrcFormat.NotNull(aSegment.Name) )
      DOMutil.addTextElement(nodeHotel,"PropertyName",aSegment.Name);

    if ( GnrcFormat.NotNull(aSegment.CityCode) )
      DOMutil.addTextElement(nodeHotel,"CityCode",aSegment.CityCode);

    if ( GnrcFormat.NotNull(aSegment.CityName) )
      DOMutil.addTextElement(nodeHotel,"CityName",aSegment.CityName);

    if ( GnrcFormat.NotNull(aSegment.Phone) )
      DOMutil.addTextElement(nodeHotel,"PhoneNumber",aSegment.Phone);

    if ( GnrcFormat.NotNull(aSegment.Fax) )
      DOMutil.addTextElement(nodeHotel,"Fax",aSegment.Fax);

    if ( aSegment.Address instanceof String[] )
      {
      for ( int i = 0; i < aSegment.Address.length; i++ )
        DOMutil.addTextElement(nodeHotel,"Address",aSegment.Address[i]);
      }

    if ( GnrcFormat.NotNull(aSegment.PostalCode) )
      DOMutil.addTextElement(nodeHotel,"PostalCode",aSegment.PostalCode);

    // segment info
    aRoot.appendChild(nodeHotel);
    DOMutil.addTextElement(aRoot,"SegmentStatus",aSegment.SegmentStatus);
    DOMutil.addTextElement(aRoot,"NumRooms",aSegment.NumRooms);
    DOMutil.addDateTimeElement(aRoot,"DateCheckIn",aSegment.CheckInDate);
    DOMutil.addDateTimeElement(aRoot,"DateCheckOut",aSegment.CheckOutDate);

    if ( GnrcFormat.NotNull(aSegment.RoomType) )
      DOMutil.addTextElement(aRoot,"RoomType",aSegment.RoomType);

    if ( GnrcFormat.NotNull(aSegment.ResName) )
      DOMutil.addTextElement(aRoot,"ReservationName",aSegment.ResName);

    if ( GnrcFormat.NotNull(aSegment.ConfirmationNumber) )
      DOMutil.addTextElement(aRoot,"Confirmation",aSegment.ConfirmationNumber);

    if ( GnrcFormat.NotNull(aSegment.Rate) )
      DOMutil.addTextElement(aRoot,"Rate",aSegment.Rate);

    DOMutil.addTextElement(aRoot,"RateGuaranteed",aSegment.RateGuaranteed);

    if ( GnrcFormat.NotNull(aSegment.Guarantee) )
      DOMutil.addTextElement(aRoot,"Guarantee",aSegment.Guarantee);

    if ( GnrcFormat.NotNull(aSegment.CancelPolicy) )
      DOMutil.addTextElement(aRoot,"CancellationPolicy",aSegment.CancelPolicy);
    }

  /**
   ***********************************************************************
   * Add PNR car segment
   ***********************************************************************
   */
  private static void addPnrCarSegment(final Element aRoot, final PNRItinCarSegment aSegment) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();

    // car info
    final Element nodeCar = doc.createElement("Car");

    DOMutil.addTextElement(nodeCar,"ChainCode",aSegment.CompanyCode);

    if ( GnrcFormat.NotNull(aSegment.CompanyName) )
      DOMutil.addTextElement(nodeCar,"ChainName",aSegment.CompanyName);

    if ( GnrcFormat.NotNull(aSegment.LocationCode) )
      DOMutil.addTextElement(nodeCar,"LocationCode",aSegment.LocationCode);

    if ( aSegment.Address instanceof String[] )
      {
      for ( int i = 0; i < aSegment.Address.length; i++ )
        DOMutil.addTextElement(nodeCar,"Address",aSegment.Address[i]);
      }

    if ( aSegment.Phone instanceof String[] )
      {
      for ( int i = 0; i < aSegment.Phone.length; i++ )
      DOMutil.addTextElement(nodeCar,"PhoneNumber",aSegment.Phone[i]);
      }

    if ( GnrcFormat.NotNull(aSegment.HoursOfOperation) )
      DOMutil.addTextElement(nodeCar,"Hours",aSegment.HoursOfOperation);


    // pickup info
    final Element nodePickup = doc.createElement("Pickup");

    if ( GnrcFormat.NotNull(aSegment.PickupCityCode) )
      DOMutil.addTextElement(nodePickup,"CityCode",aSegment.PickupCityCode);

    DOMutil.addDateTimeElement(nodePickup,"Date",aSegment.PickUpDateTime);

    if ( GnrcFormat.NotNull(aSegment.PickupCityName) )
      DOMutil.addTextElement(nodePickup,"LocationName",aSegment.PickupCityName);

    // dropoff info
    final Element nodeDropoff = doc.createElement("DropOff");

    if ( GnrcFormat.NotNull(aSegment.DropoffCityCode) )
      DOMutil.addTextElement(nodeDropoff,"CityCode",aSegment.DropoffCityCode);

    DOMutil.addDateTimeElement(nodeDropoff,"Date",aSegment.DropoffDateTime);

    if ( GnrcFormat.NotNull(aSegment.DropoffCharge) )
      DOMutil.addTextElement(nodeDropoff,"Charge",aSegment.DropoffCharge);


    // segment info
    aRoot.appendChild(nodeCar);
    DOMutil.addTextElement(aRoot,"SegmentStatus",aSegment.SegmentStatus);
    DOMutil.addTextElement(aRoot,"NumCars",aSegment.NumCars);
    aRoot.appendChild(nodePickup);
    aRoot.appendChild(nodeDropoff);

    if ( GnrcFormat.NotNull(aSegment.CarTypeCode) )
      DOMutil.addTextElement(aRoot,"CarType",aSegment.CarTypeCode);

    if ( GnrcFormat.NotNull(aSegment.Rate) )
      DOMutil.addTextElement(aRoot,"Rate",aSegment.Rate);

    if ( GnrcFormat.NotNull(aSegment.RateCode) )
      DOMutil.addTextElement(aRoot,"RateCode",aSegment.RateCode);

    DOMutil.addTextElement(aRoot,"RateGuaranteed",aSegment.RateGuaranteed);

    if ( GnrcFormat.NotNull(aSegment.GuaranteeInfo) )
      DOMutil.addTextElement(aRoot,"Guarantee",aSegment.GuaranteeInfo);

    if ( GnrcFormat.NotNull(aSegment.Confirmation) )
      DOMutil.addTextElement(aRoot,"Confirmation",aSegment.Confirmation);
    }

  /**
   ***********************************************************************
   * Add PNR phone lines
   ***********************************************************************
   */
  private static void addPnrPhone(final Element aRoot, final PNR aPnr) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();
    final Element nodePhones = doc.createElement("PhoneSection");
    aRoot.appendChild(nodePhones);

    final PNRRemark[] remarks = aPnr.getRemarks();
    Element phoneNode;
    if ( remarks instanceof PNRRemark[] )
      {
      PNRPhoneRemark rmkPhone;
      for ( int i = 0; i < remarks.length; i++ )
        {
        if ( remarks[i] instanceof PNRPhoneRemark )
          {
          rmkPhone = (PNRPhoneRemark )remarks[i];
          phoneNode = doc.createElement("Phone");
          nodePhones.appendChild(phoneNode);
          DOMutil.addTextElement(phoneNode,"PhoneNumber",rmkPhone.RemarkText);
          addPnrRemarkAssignment(phoneNode,rmkPhone);

          switch ( rmkPhone.type )
            {
            case PNRPhoneRemark.HOME_TYPE   : {
                                              phoneNode.setAttribute("type","Home");
                                              break;
                                              }
            case PNRPhoneRemark.WORK_TYPE   : {
                                              phoneNode.setAttribute("type","Work");
                                              break;
                                              }
            case PNRPhoneRemark.AGENCY_TYPE : {
                                              phoneNode.setAttribute("type","Agency");
                                              break;
                                              }
            case PNRPhoneRemark.FAX_TYPE    : {
                                              phoneNode.setAttribute("type","Fax");
                                              break;
                                              }
            case PNRPhoneRemark.CELL_TYPE   : {
                                              phoneNode.setAttribute("type","Cell");
                                              break;
                                              }
            case PNRPhoneRemark.PAGER_TYPE  : {
                                              phoneNode.setAttribute("type","Pager");
                                              break;
                                              }
            }

          }
        }
      }

    }

  /**
   ***********************************************************************
   * Add PNR ticket instruction
   ***********************************************************************
   */
  private static void addPnrTicket(final Element aRoot, final PNR aPnr) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();
    final Element nodeTicket = doc.createElement("TicketSection");
    aRoot.appendChild(nodeTicket);

    final PNRRemark[] remarks = aPnr.getRemarks();
    if ( remarks instanceof PNRRemark[] )
      {
      for ( int i = 0; i < remarks.length; i++ )
        {
        if ( remarks[i] instanceof PNRTicketRemark )
          DOMutil.addTextElement(nodeTicket,"Text",remarks[i].RemarkText);
        }
      }
    }

  /**
   ***********************************************************************
   * Add PNR receive by remark
   ***********************************************************************
   */
  private static void addPnrReceiveBy(final Element aRoot, final PNR aPnr) throws Exception
    {
    final PNRRemark[] remarks = aPnr.getRemarks();
    if ( remarks instanceof PNRRemark[] )
      {
      for ( int i = 0; i < remarks.length; i++ )
        {
        if ( remarks[i] instanceof PNRReceiveByRemark )
          {
          DOMutil.addTextElement(aRoot,"ReceiveBy",remarks[i].RemarkText);
          return;
          }
        }
      }
    }

  /**
   ***********************************************************************
   * Add PNR remarks
   ***********************************************************************
   */
  private static void addPnrRemarks(final Element aRoot, final PNR aPnr) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();
    final Element nodeRemarks = doc.createElement("RemarkSection");
    aRoot.appendChild(nodeRemarks);

    final PNRRemark[] remarks = aPnr.getRemarks();
    Element rmkNode;
    if ( remarks instanceof PNRRemark[] )
      {
      for ( int i = 0; i < remarks.length; i++ )
        {
        if ( remarks[i] instanceof PNRGeneralRemark )
          rmkNode = doc.createElement("GeneralRemark");
        else if ( remarks[i] instanceof PNRItinRemark )
          rmkNode = doc.createElement("ItineraryRemark");
        else if ( remarks[i] instanceof PNRInvoiceRemark )
          rmkNode = doc.createElement("InvoiceRemark");
        else if ( remarks[i] instanceof PNRPocketItinRemark )
          rmkNode = doc.createElement("PocketItinRemark");
        else if ( remarks[i] instanceof PNRSsrRemark )
          {
          rmkNode = doc.createElement("SSR");
          final PNRSsrRemark ssr = (PNRSsrRemark )remarks[i];
          DOMutil.addTextElement(rmkNode,"SsrCode",ssr.Code);
          }
        else if ( remarks[i] instanceof PNRSeatRemark )
          {
          rmkNode = doc.createElement("SeatAssignment");
          final PNRSeatRemark seat = (PNRSeatRemark )remarks[i];
          DOMutil.addTextElement(rmkNode,"SeatNumber",seat.Seat);
          DOMutil.addTextElement(rmkNode,"Smoking",seat.Smoking);
          if ( GnrcFormat.NotNull(seat.SeatStatus) )
            DOMutil.addTextElement(rmkNode,"SeatStatus",seat.SeatStatus);
          if ( GnrcFormat.NotNull(seat.BoardingStatus) )
            DOMutil.addTextElement(rmkNode,"BoardingStatus",seat.BoardingStatus);
          }
        else if ( remarks[i] instanceof PNRFreqFlyRemark )
          rmkNode = doc.createElement("FrequentFlier");
        else if ( remarks[i] instanceof PNROsiRemark )
          rmkNode = doc.createElement("OSI");
        else if ( remarks[i] instanceof PNRAddressRemark )
          {
          rmkNode = doc.createElement("AddressRemark");
          final PNRAddressRemark addr = (PNRAddressRemark )remarks[i];
          switch ( addr.type )
            {
            case PNRAddressRemark.HOME_ADDRESS:
                                              {
                                              rmkNode.setAttribute("type","Home");
                                              break;
                                              }
            case PNRAddressRemark.WORK_ADDRESS:
                                              {
                                              rmkNode.setAttribute("type","Work");
                                              break;
                                              }
            case PNRAddressRemark.BILLING_ADDRESS:
                                              {
                                              rmkNode.setAttribute("type","Billing");
                                              break;
                                              }
            case PNRAddressRemark.DELIVERY_ADDRESS:
                                              {
                                              rmkNode.setAttribute("type","Delivery");
                                              break;
                                              }
            case PNRAddressRemark.AGENCY_ADDRESS:
                                              {
                                              rmkNode.setAttribute("type","Agency");
                                              break;
                                              }
            }
          }
        else
          rmkNode = null;

        if ( rmkNode instanceof Element )
          {
          if ( remarks[i].MessageNumber > 0 )
            DOMutil.addTextElement(rmkNode,"LineNumber",remarks[i].MessageNumber);

          if ( GnrcFormat.NotNull(remarks[i].RemarkText) )
            DOMutil.addTextElement(rmkNode,"Text",remarks[i].RemarkText);

          addPnrRemarkAssignment(rmkNode,remarks[i]);
          nodeRemarks.appendChild(rmkNode);
          }
        }
      }

    }


  /**
   ***********************************************************************
   * Add PNR fare info
   ***********************************************************************
   */
  private static void addPnrFares(final Element aRoot, final PNR aPnr) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();
    final Element nodeFares = doc.createElement("FareSection");
    aRoot.appendChild(nodeFares);

    final PNRFare[] fares = aPnr.getFares();
    if ( fares instanceof PNRFare[] )
      {
      Element fareNode;
      Element taxNode;
      long iTaxAmount;
      long iFare;
      String sTaxName;
      String sAmount;
      for ( int i = 0; i < fares.length; i++ )
        {
        fareNode = doc.createElement("Fare");

        iFare = fares[i].getBaseFare();
        sAmount = getFareString(iFare);

        DOMutil.addTextElement(fareNode,"PassengerType",fares[i].getGenericPTC());
        DOMutil.addTextElement(fareNode,"NumSeats",fares[i].getNumPsgrs());
        DOMutil.addTextElement(fareNode,"BaseFare",sAmount);

        for ( int iTaxNum = 0; iTaxNum < fares[i].getNumTaxes(); iTaxNum++ )
          {
          taxNode = doc.createElement("Tax");

          iTaxAmount = fares[i].getTaxAmountByIndex(iTaxNum);
          sAmount = getFareString(iTaxAmount);
          sTaxName   = fares[i].getTaxNameByIndex(iTaxNum);

          DOMutil.addTextElement(taxNode,"Amount",sAmount);
          DOMutil.addTextElement(taxNode,"TaxCode",sTaxName);

          fareNode.appendChild(taxNode);
          }

        DOMutil.addTextElement(fareNode,"FareLadder",fares[i].getRawData());
        nodeFares.appendChild(fareNode);
        }
      }
    }

  /**
   ***********************************************************************
   * Format the fare string
   ***********************************************************************
   */
  private static String getFareString(final long aAmount) throws Exception
    {
    float fAmount = aAmount;
    fAmount /= 100;
    final DecimalFormat fmtAmount = new DecimalFormat("0.00");
    return( fmtAmount.format(fAmount).toString() );
    }

  /**
   ***********************************************************************
   * Add PNR remark assignment
   ***********************************************************************
   */
  private static void addPnrRemarkAssignment(final Element aRoot, final PNRRemark aRemark) throws Exception
    {
    final Document doc = aRoot.getOwnerDocument();
    final Element nodeAssign = doc.createElement("Assignment");

    if ( aRemark.FamilyNumber > 0 )
      DOMutil.addTextElement(nodeAssign,"FamilyNumber",aRemark.FamilyNumber);

    if ( aRemark.MemberNumber > 0 )
      DOMutil.addTextElement(nodeAssign,"MemberNumber",aRemark.MemberNumber);

    if ( GnrcFormat.NotNull(aRemark.FirstName) )
      DOMutil.addTextElement(nodeAssign,"FirstName",aRemark.FirstName);

    if ( GnrcFormat.NotNull(aRemark.LastName) )
      DOMutil.addTextElement(nodeAssign,"LastName",aRemark.LastName);

    if ( aRemark.ItinSegment > 0 )
      DOMutil.addTextElement(nodeAssign,"SegmentNum",aRemark.ItinSegment);

    if ( aRemark instanceof PNRSsrRemark )
      {
      final PNRSsrRemark ssr = (PNRSsrRemark )aRemark;
      if ( GnrcFormat.NotNull(ssr.Carrier) )
        DOMutil.addTextElement(nodeAssign,"Carrier",ssr.Carrier);
      }
    else if ( aRemark instanceof PNROsiRemark )
      {
      final PNROsiRemark osi = (PNROsiRemark )aRemark;
      if ( GnrcFormat.NotNull(osi.Carrier) )
        DOMutil.addTextElement(nodeAssign,"Carrier",osi.Carrier);
      }
    else if ( aRemark instanceof PNRFreqFlyRemark )
      {
      final PNRFreqFlyRemark ff = (PNRFreqFlyRemark )aRemark;
      if ( GnrcFormat.NotNull(ff.Carrier) )
        DOMutil.addTextElement(nodeAssign,"Carrier",ff.Carrier);
      }
    else if ( aRemark instanceof PNRSeatRemark )
      {
      final PNRSeatRemark seat = (PNRSeatRemark )aRemark;
      if ( GnrcFormat.NotNull(seat.Carrier) )
        DOMutil.addTextElement(nodeAssign,"Carrier",seat.Carrier);
      if ( seat.FlightNum > 0 )
        DOMutil.addTextElement(nodeAssign,"FlightNum",seat.FlightNum);
      }

    // add the assignment node, if it has data
    if ( nodeAssign.hasChildNodes() )
      aRoot.appendChild(nodeAssign);
    }

  /**
   ***********************************************************************
   * Add PNR error messages
   ***********************************************************************
   */
  private static void addPnrErrors(final Element aRoot, final PNR aPnr) throws Exception
    {
    final String[] errors = aPnr.getErrors();
    if ( errors instanceof String[] )
      {
      final Document doc = aRoot.getOwnerDocument();
      final Element nodeErrors = doc.createElement("ErrorSection");
      aRoot.appendChild(nodeErrors);

      for ( int i = 0; i < errors.length; i++ )
        DOMutil.addTextElement(nodeErrors,"ErrorText",errors[i]);
      }
    }

  /**
   ***********************************************************************
   * Get Flifo
   ***********************************************************************
   */
  private static Document reqGetFlifo(final ReqGetFlifo aRequest) throws Exception
    {
    final FlightInfo flight = aRequest.Flight;
    if ( (flight instanceof FlightInfo) == false )
      return(null);

    final Document docResp = new DocumentImpl();
    final Element root = docResp.createElement("AirDetailsRS");
    docResp.appendChild(root);

    final String sDepCity;
    if ( GnrcFormat.NotNull(aRequest.DepCity) )
      sDepCity = aRequest.DepCity;
    else
      sDepCity = flight.getDepCity();

    final String sArrCity;
    if ( GnrcFormat.NotNull(aRequest.ArrCity) )
      sArrCity = aRequest.ArrCity;
    else
      sArrCity = flight.getArrCity();

    final int iTotalMiles = flight.getAirMiles(sDepCity,sArrCity);
    final int iTotalTime  = flight.getAirMinutes(sDepCity,sArrCity);
    final int iTotalHours = iTotalTime / 60;
    final int iRemMinutes = iTotalTime % 60;

    if ( iTotalTime > 0 )
      docResp.getDocumentElement().setAttribute("TotalFlightTime","P0Y0M0DT" + Integer.toString(iTotalHours) + "H" + Integer.toString(iRemMinutes) + "M0S");
    if ( iTotalMiles > 0 )
      docResp.getDocumentElement().setAttribute("TotalMiles",Integer.toString(iTotalMiles));

    // add GDS response to warning message if no flight segments are returned
    if ( flight.getNumSegments() == 0 )
      DOMutil.addTextElement(docResp.getDocumentElement(),"WarningString",flight.FlightSchedResponse);

    Element nodeLeg;
    for ( int i = 0; i < flight.getNumSegments(); i++ )
      {
      nodeLeg = docResp.createElement("Leg");
      nodeLeg.setAttribute("SequenceNumber",Integer.toString(i + 1));

      addFlightSegmentData( flight.getFlightSegment(i), nodeLeg );

      docResp.getDocumentElement().appendChild(nodeLeg);
      }


    return(docResp);
    }

  /**
   ***********************************************************************
   * Get Flifo
   ***********************************************************************
   */
  private static void addFlightSegmentData(final FlightSegment aSeg, final Element aLegNode) throws Exception
    {
    // add general flight segment info
    aLegNode.setAttribute("VendorID",aSeg.Carrier);
    if ( GnrcFormat.NotNull(aSeg.CodeShareCarrierCode) )
      aLegNode.setAttribute("OperatorID",aSeg.CodeShareCarrierCode);
    else if ( GnrcFormat.NotNull(aSeg.CodeShareCarrierName) )
      aLegNode.setAttribute("OperatorID",aSeg.CodeShareCarrierName);
    aLegNode.setAttribute("FlightNumber",Integer.toString(aSeg.FlightNum));
    aLegNode.setAttribute("OriginAirportID",aSeg.DepartCity);
    aLegNode.setAttribute("DestinationAirportID",aSeg.ArriveCity);
    if ( aSeg.AirMinutes > 0 )
      {
      final int iHours      = aSeg.AirMinutes / 60;
      final int iRemMinutes = aSeg.AirMinutes % 60;
      final String sTime = "P0Y0M0DT" + Integer.toString(iHours) + "H" + Integer.toString(iRemMinutes) + "M0S";
      aLegNode.setAttribute("JourneyDuration",sTime);
      }
    if ( GnrcFormat.NotNull(aSeg.OnTimePerformance) )
      aLegNode.setAttribute("OnTimeRate",aSeg.OnTimePerformance);
    if ( GnrcFormat.NotNull(aSeg.EquipmentCode) )
      aLegNode.setAttribute("EQPCode",aSeg.EquipmentCode);


    // add cabin info

    if ( GnrcFormat.NotNull(aSeg.MealCode) )
      {
      final String sMeal;
      if ( aSeg.MealCode.startsWith("/") && (aSeg.MealCode.length() >= 2) )
        sMeal = aSeg.MealCode.substring(1,2);
      else
        sMeal = aSeg.MealCode.substring(0,1);

      final Element nodeCabin = aLegNode.getOwnerDocument().createElement("Cabin");
      nodeCabin.setAttribute("CabinType","Economy");
      nodeCabin.setAttribute("MealCode",aSeg.MealCode.substring(0,1));
      aLegNode.appendChild(nodeCabin);
      }

    {      // add departure date/time
    final Element nodeOriginDateTime = aLegNode.getOwnerDocument().createElement("OriginDateTime");
    DOMutil.addDateElement(nodeOriginDateTime,"Date",aSeg.DepSchedDateTime);
    DOMutil.addTimeElement(nodeOriginDateTime,"Time",aSeg.DepSchedDateTime);
    aLegNode.appendChild(nodeOriginDateTime);
    }


    {      // add arrival date/time
    final Element nodeDestinationDateTime = aLegNode.getOwnerDocument().createElement("DestinationDateTime");
    DOMutil.addDateElement(nodeDestinationDateTime,"Date",aSeg.ArrSchedDateTime);
    DOMutil.addTimeElement(nodeDestinationDateTime,"Time",aSeg.ArrSchedDateTime);
    aLegNode.appendChild(nodeDestinationDateTime);
    }

    }

  /**
   ***********************************************************************
   * Get Connection status
   ***********************************************************************
   */
  private static Document reqGetStatus(final ReqGetStatus aRequest) throws Exception
    {
    final Document docResp = new DocumentImpl();
    final Element root = docResp.createElement("ConnectDetailsRS");
    docResp.appendChild(root);

    docResp.getDocumentElement().setAttribute("SoftwareVersion",aRequest.Version);

    // add gateway info
    final Element nodeGateway    = docResp.createElement("InnosysGateway");
    if ( GnrcFormat.NotNull(aRequest.GatewayServer) )
      nodeGateway.setAttribute("ServerIP",aRequest.GatewayServer);
    if ( aRequest.GatewayPort > 0 )
      nodeGateway.setAttribute("ServerPort",Integer.toString(aRequest.GatewayPort));
    if ( GnrcFormat.NotNull(aRequest.TaName) )
      nodeGateway.setAttribute("TaName",aRequest.TaName);
    if ( aRequest.TaNumber > 0 )
      nodeGateway.setAttribute("TaNumber",Integer.toString(aRequest.TaNumber));
    docResp.getDocumentElement().appendChild(nodeGateway);


    // add GDS connection info
    final Element nodeConnection = docResp.createElement("GdsConnection");
    if ( GnrcFormat.NotNull(aRequest.HostCode) )
      nodeConnection.setAttribute("CrsCode",aRequest.HostCode);
    if ( GnrcFormat.NotNull(aRequest.SignOn) )
      nodeConnection.setAttribute("SignOn",aRequest.SignOn);
    if ( GnrcFormat.NotNull(aRequest.Password) )
      nodeConnection.setAttribute("Password",aRequest.Password);
    if ( GnrcFormat.NotNull(aRequest.PseudoCity) )
      nodeConnection.setAttribute("PseudoCity",aRequest.PseudoCity);
    docResp.getDocumentElement().appendChild(nodeConnection);


    // additional properties
    final String sProps;
    if ( aRequest.properties instanceof Properties )
      {
      final Enumeration keys = aRequest.properties.propertyNames();
      String sName;
      String sValue;
      final StringBuffer propBuf = new StringBuffer();
      while ( keys.hasMoreElements() )
        {
        sName = (String )keys.nextElement();
        sValue = aRequest.properties.getProperty(sName);
        propBuf.append(sName + '=' + sValue + ',');
        }
      sProps = propBuf.toString();
      }
    else
      sProps = null;

    if ( GnrcFormat.NotNull(sProps) )
      DOMutil.addTextElement(docResp.getDocumentElement(),"AdditionalProperties",sProps);


    return(docResp);
    }

  /**
   ***********************************************************************
   * Takes a long (type) representation of a date and returns it in yyyyMMdd
   * format; if the long passed is 0 it returns 8 spaces.
   ***********************************************************************
   */
  private static String formatXmlDate(final long aDate)
    {
    if ( aDate > 0 )
      {
      final String sText = fmtXmlDate.format( new Date(aDate) ).toString();
      return(sText);
      }
    else
      return(null);
    }


  private static String formatXmlTime(final long aTime)
    {
    if ( aTime > 0 )
      {
      final String sText = fmtXmlTime.format( new Date(aTime) ).toString();
      return(sText);
      }
    else
      return(null);
    }


  private static String formatXmlDateTime(final long aDateTime)
    {
    if ( aDateTime > 0 )
      {
      final String sText = fmtXmlDateTime.format( new Date(aDateTime) ).toString();
      return(sText);
      }
    else
      return(null);
    }


}
