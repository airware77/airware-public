package xmax.TranServer;

import xmax.TranServer.GnrcConvControl;

import xmax.crs.PNR;
import xmax.crs.PNRFare;
import xmax.crs.GetPNR.PNRItinAirSegment;
import xmax.crs.GdsResponseException;
import xmax.crs.GnrcParser;
import xmax.crs.Tax;
import xmax.crs.Block;
import xmax.crs.BlockFlight;
import xmax.crs.BlockMessage;
import xmax.crs.GnrcCrs;
import xmax.crs.ConnectTimes;
import xmax.crs.ConnectTimesAirport;
import xmax.crs.TicketInformation;

import xmax.crs.GetPNR.*;
import xmax.crs.Flifo.FlightInfo;
import xmax.crs.Flifo.FlightSegment;
import xmax.crs.Availability.*;
import xmax.crs.profiles.*;

import xmax.util.DateTime;

import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Enumeration;
import java.util.List;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NativeAsciiWriter
{

  /**
   ***********************************************************************
   * This function returns a string suitable for sending to the client
   ***********************************************************************
   */
  public static String getResponseString(final ReqTranServer aRequest)
    {
    try
      {
      if ( aRequest.hasExceptions() )
        return( getErrorResponseString(aRequest) );

      if ( aRequest instanceof ReqGetPNR )
        return( reqGetPNR( (ReqGetPNR )aRequest) );
      if ( aRequest instanceof ReqGetFlifo )
        return( reqGetFlifo( (ReqGetFlifo )aRequest) );
      if ( aRequest instanceof ReqGetAvail )
        return( reqGetAvail( (ReqGetAvail )aRequest) );
      if ( aRequest instanceof ReqGetFare )
        return( reqGetFare( (ReqGetFare )aRequest) );

      // PNR building responses
      if ( aRequest instanceof ReqAddAirSeg )
        return( reqAddAirSeg( (ReqAddAirSeg )aRequest) );
      if ( aRequest instanceof ReqAddCommission )
        return( reqDefault(GnrcConvControl.AWR_ADD_COMM_RESP) );
      if ( aRequest instanceof ReqAddCorpHeader )
        return( reqDefault(GnrcConvControl.AWR_GRP_HDR_RESP) );
      if ( aRequest instanceof ReqAddEndorsement )
        return( reqDefault(GnrcConvControl.AWR_ADD_ENDORSE_RESP) );
      if ( aRequest instanceof ReqAddFOP )
        return( reqDefault(GnrcConvControl.AWR_ADD_FOP_RESP) );
      if ( aRequest instanceof ReqAddNames )
        return( reqDefault(GnrcConvControl.AWR_ADD_NAME_RESP) );
      if ( aRequest instanceof ReqAddPhone )
        return( reqDefault(GnrcConvControl.AWR_ADD_PHONE_RESP) );
      if ( aRequest instanceof ReqAddReceiveBy )
        return( reqDefault(GnrcConvControl.AWR_RCV_PNR_RESP) );
      if ( aRequest instanceof ReqAddRemark )
        return( reqDefault(GnrcConvControl.AWR_ADD_REMARK_RESP) );
      if ( aRequest instanceof ReqAddTicket )
        return( reqDefault(GnrcConvControl.AWR_ADD_TICKET_RESP) );
      if ( aRequest instanceof ReqAddTourCode )
        return( reqDefault(GnrcConvControl.AWR_ADD_TOURCODE_RESP) );
      if ( aRequest instanceof ReqChangeName )
        return( reqDefault(GnrcConvControl.AWR_CHG_NAME_RESP) );
      if ( aRequest instanceof ReqAcceptSchedChange )
        return( reqDefault(GnrcConvControl.AWR_ACCEPT_CHG_RESP) );
      if ( aRequest instanceof ReqCxlRemark )
        return( reqDefault(GnrcConvControl.AWR_CXL_REMARK_RESP) );
      if ( aRequest instanceof ReqEndTransaction )
        return( reqEndTransaction( (ReqEndTransaction )aRequest) );
      if ( aRequest instanceof ReqIssueTicket )
        return( reqIssueTicket((ReqIssueTicket)aRequest) );
      if ( aRequest instanceof ReqGetTicketInfo )
          return( reqGetTicketInfo((ReqGetTicketInfo)aRequest) );
      if ( aRequest instanceof ReqQueuePNR )
        return( reqDefault(GnrcConvControl.AWR_QUEUE_PNR_RESP) );
      if ( aRequest instanceof ReqAssignPrinter )
        return( reqDefault(GnrcConvControl.SET_PRN_RESP) );
      if ( aRequest instanceof ReqBuildProfile )
        return( reqDefault(GnrcConvControl.SET_PER_PROF_RESP) );
      if ( aRequest instanceof ReqBuildPnr )
        return( reqDefault( "R" + aRequest.AsciiRequest.substring(1,8) ) );
      if ( aRequest instanceof ReqSplitPNR )
        return( reqSplitPNR( (ReqSplitPNR )aRequest ) );
      if ( aRequest instanceof ReqChangePnrItin )
        return( reqChangePnrItin( (ReqChangePnrItin )aRequest ) );
      if ( aRequest instanceof ReqCxlAirSegment )
        return( reqDefault(GnrcConvControl.CXL_SEG_RESP) );
      if ( aRequest instanceof ReqCxlItinerary )
        return( reqDefault(GnrcConvControl.CXL_ITIN_RESP) );

      // logging responses
      if ( aRequest instanceof ReqEnableLogForwarding )
        return( reqDefault(GnrcConvControl.START_LOG_RESP) );
      if ( aRequest instanceof ReqDisableLogForwarding )
        return( reqDefault(GnrcConvControl.END_LOG_RESP) );

      // other
      if ( aRequest instanceof ReqIgnore )
        return( reqIgnore( (ReqIgnore )aRequest) );
      if ( aRequest instanceof ReqSessionStart )
        return( reqSessionStart( (ReqSessionStart )aRequest) );
      if ( aRequest instanceof ReqSessionEnd )
        return( reqSessionEnd( (ReqSessionEnd )aRequest) );
      if ( aRequest instanceof ReqGetStatus )
        return( reqGetStatus( (ReqGetStatus )aRequest) );
      if ( aRequest instanceof ReqGetHotelInfo )
        return( reqGetHotelInfo( (ReqGetHotelInfo )aRequest) );
      if ( aRequest instanceof ReqFreeForm )
        return( reqFreeForm( (ReqFreeForm )aRequest) );
      if ( aRequest instanceof ReqGetConnectTimes )
        return( reqGetConnectTimes( (ReqGetConnectTimes )aRequest) );
      if ( aRequest instanceof ReqGetConnectTimesAirport )
        return( reqGetConnectTimesAirport( (ReqGetConnectTimesAirport )aRequest) );
      if ( aRequest instanceof ReqListBranches )
        return( reqListBranches( (ReqListBranches )aRequest) );
      if ( aRequest instanceof ReqListGroupProfiles )
        return( reqListGroupProfiles( (ReqListGroupProfiles )aRequest) );
      if ( aRequest instanceof ReqListPersonalProfiles )
        return( reqListPersonalProfiles( (ReqListPersonalProfiles )aRequest) );
      if ( aRequest instanceof ReqGetProfile )
        return( reqGetProfile( (ReqGetProfile )aRequest) );

      if ( aRequest instanceof ReqBlockBuild )
        return( reqBlockBuild( (ReqBlockBuild)aRequest ) );
      if ( aRequest instanceof ReqBlockRetrieve )
        return( reqBlockRetrieve( (ReqBlockRetrieve)aRequest ) );
      if ( aRequest instanceof ReqBlockModify )
        return( reqDefault(GnrcConvControl.MOD_BLK_RESP) );
      if ( aRequest instanceof ReqBlockDelete )
        return( reqDefault(GnrcConvControl.DEL_BLK_RESP) );
      if ( aRequest instanceof ReqBlockReadMessage )
        return(reqBlockReadMessage( (ReqBlockReadMessage)aRequest ));

      }
    catch (Exception e)
      {
      return( getErrorResponseString(aRequest) );
      }

    return( getErrorResponseString("Unrecognized request object " + aRequest.getClass().getName(),null) );
    }

  /**
   ***********************************************************************
   * Default response to ascii commands
   ***********************************************************************
   */
  private static String reqDefault(final String aOperation)
    {
    // format the output response
    final DecimalFormat StatusFormat = new DecimalFormat("00000");
    final String sResponse = setWidth(aOperation,8) +
                             StatusFormat.format(GnrcConvControl.STATUS_OK);

    return(sResponse);
    }

  /**
   ***********************************************************************
   * This function returns a string suitable for sending to the client
   ***********************************************************************
   */
  public static String getErrorResponseString(final ReqTranServer aRequest)
    {
    final Exception firstException = aRequest.getFirstException();
    final String sOperation = aRequest.AsciiRequest.substring(0,8);

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

    final String sResponse = getErrorResponseString(sErrorText,
                                                    iErrorNumber,
                                                    sOperation,
                                                    aRequest.AsciiRequest,
                                                    sGdsErrorText);
    return( sResponse );
    }


  public static String getErrorResponseString(final String aErrorText,
                                              final String aClientRequest)
    {
    final String sOperation;
    if ( aClientRequest instanceof String )
      sOperation = aClientRequest.substring(0,8);
    else
      sOperation = "";

    return( getErrorResponseString(aErrorText,GnrcConvControl.STS_CRS_ERR,sOperation,aClientRequest,null) );
    }


  private static String getErrorResponseString(final String aErrorText,
                                               final int aStatus,
                                               final String aOperation,
                                               final String aClientRequest,
                                               final String aGdsErrorText)
    {
    final DecimalFormat pnrStatusFormat = new DecimalFormat("00000");

    final String sResponse = setWidth(aOperation,8) +
                             pnrStatusFormat.format(aStatus) +
                             setWidth(aErrorText,1000) +
                             setWidth(aGdsErrorText,1000) +
                             setWidth(aClientRequest,1000);
    return(sResponse);
    }

  /**
   ***********************************************************************
   * Get PNR error string
   ***********************************************************************
   */
  private static String getPNRErrorResponseString(final ReqGetPNR aRequest,
                                                 final int aStatus, final String aErrorMessage)
    {
    if ( aRequest.commType == aRequest.COMM_NATIVE_AIRWARE )
      {
      final String sResponse = formatPNRAirwareError(aRequest.pnr.getLocator(),aStatus,aErrorMessage);
      return(sResponse);
      }
    else
      {
      final String sTitle = formatPNRTitle(aRequest,1004);
      final String sError = formatPNRError(aRequest.getCrsCode(),aRequest.Locator,"",aStatus, aErrorMessage);

      final String sResponse = sTitle + sError;
      return(sResponse);
      }
    }

  /**
   ***********************************************************************
   * Get PNR
   ***********************************************************************
   */
  private static String reqGetPNR(final ReqGetPNR aRequest)
    {
    StringBuffer out = new StringBuffer();
    if ( aRequest.pnr instanceof PNR )
      {
      if ( aRequest.commType == aRequest.COMM_NATIVE_AIRWARE )
        {
        if ( GnrcFormat.NotNull(aRequest.QueueName) && aRequest.pnr.hasNoData() )
          return(formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"RLOC","QEUEMPTY"));

        out.append( formatPNRAirwareHeader( GnrcConvControl.STATUS_OK,"RLOC",aRequest.pnr.getLocator()) );
        out.append( formatPNRAirwareGroupHeader(aRequest.pnr) );
        out.append( formatPNRAirwareNames(aRequest.pnr) );
        out.append( formatPNRAirwareItin(aRequest.pnr, aRequest.isPNR2) );

        String sRemarks = "";
        if (aRequest.ReturnRemarks)
          out.append( formatPNRAirwareRemarks(aRequest.pnr) );

        // final String sResponse = sTitle + sNames + sItin;
        return(out.toString());
        }
      else
        {
        if ( GnrcFormat.NotNull(aRequest.QueueName) && aRequest.pnr.hasNoData() )
          {
          return(formatPNRTitle(aRequest,1001));
          }
        else
          {
          /*
          final String sTitle   = formatPNRTitle(aRequest,0);
          final String sGroup   = formatPNRGroupHeader(aRequest.pnr);
          final String sNames   = formatPNRNames(aRequest.pnr);
          final String sItin    = formatPNRItin(aRequest.pnr);
          */

          out.append( formatPNRTitle(aRequest,0) );
          out.append( formatPNRGroupHeader(aRequest.pnr) );
          out.append( formatPNRNames(aRequest.pnr) );
          out.append( formatPNRItin(aRequest.pnr) );

          if (aRequest.ReturnRemarks)
            out.append( formatPNRRemarks(aRequest.pnr) );

          out.append( formatPNRErrors(aRequest.pnr) );
          return(out.toString());
          }

        }
      }
    else
      return(null);
    }

  /**
   ***********************************************************************
   * Formats the title section of the PNR response
   ***********************************************************************
   */

  /*
  private static String formatPNRTitle(final ReqGetPNR aRequest)
    {
    if ( GnrcFormat.NotNull(aRequest.QueueName) && aRequest.pnr.hasNoData() )
      return( formatPNRTitle(aRequest,1001) );
    else
      return( formatPNRTitle(aRequest,0) );
    }
  */

  /**
   ***********************************************************************
   * This method formats the Title section of a Passenger Name Record (PNR)
   * Response and returns a 38 character fixed-width string, formatted as
   * indicated below.
   *
   * @param aRequest
   *  the corresponding ReqGetPNR object containing the PNR information
   *
   * @param aStatus
   *  ???
   *
   * @return a 38 character Fixed length string formatted as follows:<br>
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   7      8     - the type of request, as specified in {@link xmax.crs.Generic.GnrcConvControl}
   *    8 -  11      4     - the string 'RLOC'
   *   12 -  15      4     - a Status Number, either '0000' or '1001'
   *   16 -  17      2     - a carrier code
   *   18 -  25      8     - a PNR locator
   *   26 -  35     10     - a pseudo-city code
   *   36 -  37      2     - an agent's code
   * </pre>
   *
   ***********************************************************************
   */
  private static String formatPNRTitle(final ReqGetPNR aRequest, final int aStatus)
    {
    // set PNR ids
    String sCrsCode    = "";
    String sLocator    = "";
    String sPseudoCity = "";
    String sAgentSign  = "";
    try
      {
      if ( aRequest.pnr instanceof PNR )
        {
        sCrsCode    = aRequest.pnr.getCrs();
        sLocator    = aRequest.pnr.getLocator();
        sPseudoCity = aRequest.pnr.getPseudoCity();
        sAgentSign  = aRequest.pnr.getAgentSign();
        }

      if ( GnrcFormat.IsNull(sCrsCode) )
        sCrsCode = aRequest.getCrsCode();

      if ( GnrcFormat.IsNull(sLocator) )
        sLocator = aRequest.Locator;
      }
    catch (Exception e)
      {
      }

    // set operation code
    final String sOperation;
    if ( GnrcFormat.NotNull(aRequest.QueueName) )
      sOperation = GnrcConvControl.GET_QUEUE_PNR_RESP;
    else
      sOperation = GnrcConvControl.GET_PNR_RESP;


    // format output string
    final DecimalFormat pnrStatusFormat = new DecimalFormat("0000");
    final String sTitle  = setWidth(sOperation,8) +
                           setWidth("RLOC",4)   +
                           pnrStatusFormat.format(aStatus) +
                           setWidth(sCrsCode,2) +
                           setWidth(sLocator,8) +
                           setWidth(sPseudoCity,10) +
                           setWidth(sAgentSign,5);

    return( sTitle );
    } // formatPNRTitle()

  /**
   ***********************************************************************
   * This method formats the information contained in a Passenger Name Record
   * Group Header.
   *
   * @param pnr
   *
   * @return a 30 character Fixed length string formatted as follows:<br>
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   3      4     - the string 'CORP'
   *    4 -   6      3     - the number of seats available
   *    7 -   9      3     - the number of seats booked
   *   10 -  29     20     - the text of the Group Header
   * </pre><br>
   *
   ***********************************************************************
   */
  private static String formatPNRGroupHeader(PNR pnr)
    {
    PNRGroupHeader groupHeader = pnr.getGroupHeader();
    String sName = "";
    final DecimalFormat SeatFormat  = new DecimalFormat("000");

    if (groupHeader instanceof PNRGroupHeader)
      {
      sName = setWidth("CORP",4) +
              SeatFormat.format(pnr.getNumSeatsAvailable()) +
              SeatFormat.format(pnr.getNumSeatsBooked()) +
              setWidth(groupHeader.headerText,20);

      }
    return(sName);
    } // end formatPNRGroupHeader

  /**
   ***********************************************************************
   * This method formats the Group Header and names section of a Passenger Name
   * Record (PNR) Response and returns a fixed-width string, formatted as
   * indicated below.
   *
   * @param aPNR
   *   the PNR object corresponding to the Passenger Name Record to be formatted
   *
   * @return a  122 character Fixed length string formatted as follows<br>
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   3      4     - the string 'NAME'
   *    4 -   5      2     - the family ID number for this passenger
   *    6 -   7      2     - the family member ID number for this passenger
   *    8 -  37     30     - the Last Name of the passenger
   *   38 -  57     20     - the First name of the passenger
   *   58 -  58      1     - the Middle initial of the passenger
   *   59 -  65      7     - the Title of the passenger
   *   57 -  68      3     - the Passenger Type Code (PTC) of the passenger
   *   60 -  71      3     - the Age of the passenger
   *   63 -  96     25     - the PassengerID of this passenger (as set by Airware)
   *   88 - 121     25     - the name of an infant accompanying this passenger, if any
   * </pre><br>
   ***********************************************************************
   */
  private static String formatPNRNames(final PNR aPNR)
    {
    final StringBuffer sOutStr = new StringBuffer("");

    // get PNR family records
    PNRFamilyElement[] Families = null;
    try
      {
      Families = aPNR.getFamilies();
      }
    catch (Exception e)
      {
      sOutStr.append( formatPNRError(aPNR.getCrs(),aPNR.getLocator(),"",1004,"Unable to read PNR Names - " + e.toString()) );
      Families = null;
      }


    if ( Families instanceof PNRFamilyElement[] )
      {
      final DecimalFormat PsgrNumFormat = new DecimalFormat("00");
      final DecimalFormat AgeFormat     = new DecimalFormat("000");
      PNRNameElement Member;
      PNRNameElement Infant;
      String sName;
      String sInfantName;
      int iNumAvail;
      final DecimalFormat SeatFormat  = new DecimalFormat("000");

      // for each family
      for ( int iFamNum = 0; iFamNum < Families.length; iFamNum++ )
        {
        /*
        if ( Families[iFamNum].isCorpHeader || Families[iFamNum].isBSGHeader )
          {
          iNumAvail = Families[iFamNum].getNumSeats() - Families[iFamNum].NumBooked;
          if ( iNumAvail < 0 )
            iNumAvail = 0;

          sName = setWidth("CORP",4) +
                  SeatFormat.format( iNumAvail ) +
                  SeatFormat.format(Families[iFamNum].NumBooked) +
                  setWidth(Families[iFamNum].getLastName(),20);

          sOutStr.append(sName);
          }
        else if ( Families[iFamNum].FamilyMembers instanceof PNRNameElement[] )
        */
        if ( Families[iFamNum].FamilyMembers instanceof PNRNameElement[] )
          {
          // for each member of the family
          for ( int iMemberNum = 0; iMemberNum < Families[iFamNum].FamilyMembers.length; iMemberNum++ )
            {
            Member = Families[iFamNum].FamilyMembers[iMemberNum];

            if ( Member.isInfant() == false )
              {
              try
                {
                Infant = aPNR.getInfantName(Member);
                }
              catch (Exception e)
                {
                Infant = null;
                }

              if ( Infant instanceof PNRNameElement )
                sInfantName = Infant.FirstName;
              else
                sInfantName = Member.InfantName;

              sName = setWidth("NAME",4)   +
                      PsgrNumFormat.format(iFamNum + 1) +
                      PsgrNumFormat.format(iMemberNum + 1) +
                      setWidth(Member.LastName,30) +
                      setWidth(Member.FirstName,20) +
                      setWidth(Member.MiddleName,1) +
                      setWidth(Member.Title,8) +
                      setWidth(Member.PTC,3) +
                      AgeFormat.format(Member.Age) +
                      setWidth(Member.getPassengerID(),25) +
                      setWidth(sInfantName,25);     // infant info

              sOutStr.append(sName);
              }
            }
          }
        }
      }

    return( sOutStr.toString() );
    } // formatPNRNames()

  /**
   ***********************************************************************
   * Format all the PNR itinerary segments
   ***********************************************************************
   */
  private static String formatPNRItin(final PNR aPNR)
    {
    final StringBuffer sOutStr = new StringBuffer("");

    try
      {
      // get PNR itinerary segments
      final PNRItinSegment[] Segments = aPNR.getSegments();

      // format each itinerary segment
      if ( Segments instanceof PNRItinSegment[] )
        {
        String sItinSegment;
        for ( int i = 0; i < Segments.length; i++ )
          {
          try
            {
            if ( Segments[i] instanceof PNRItinCarSegment )
              sItinSegment = formatPNRCarSegment( (PNRItinCarSegment )Segments[i]);
            else if ( Segments[i] instanceof PNRItinHotelSegment )
              sItinSegment = formatPNRHotelSegment( (PNRItinHotelSegment)Segments[i]);
            else if ( Segments[i] instanceof PNRItinAirSegment )
              sItinSegment = formatPNRAirSegment( (PNRItinAirSegment)Segments[i]);
            else if ( Segments[i] instanceof PNRItinArunkSegment )
              sItinSegment = formatPNRArunkSegment( (PNRItinArunkSegment )Segments[i]);
            else
              sItinSegment = formatPNRUnknownSegment(Segments[i]);

            sOutStr.append(sItinSegment);
            }
          catch (Exception e)
            {
            final String sError = formatPNRError(aPNR.getCrs(),aPNR.getLocator(),"",1004,e.toString());
            sItinSegment        = formatPNRUnknownSegment(Segments[i] );
            sOutStr.append(sError);
            sOutStr.append(sItinSegment);
            }
          }
        }

      }
    catch (Exception e)
      {
      final String sError = formatPNRError(aPNR.getCrs(),aPNR.getLocator(),"",1004,"Unable to read PNR Itinerary - " + e.toString());
      sOutStr.append(sError);
      }

    return( sOutStr.toString() );
    } // formatPNRItin()

  /**
   ***********************************************************************
   * This method formats an
   * {@link xmax.crs.GetPNR.PNRItinAirSegment "Air Segment"}
   * of a Passenger Name Record (PNR) and returns a nn character fixed-width
   * string, formatted as indicated below.
   *
   * @param aAirSegment
   *  the PNRItinAirSegment to be output as a string
   *
   * @return a nn character Fixed length string formatted as follows:<br>
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   3      4     - the string 'AIR' padded with a space
   *    4 -   5      2     - the Segment ID number for this segment
   *    6 -   8      3     - the Carrier Code, padded with a space
   *    9 -  13      5     - the Flight Number
   *   14 -  25     12     - the Departure Date and Time in yyyyMMddHHmm format
   *   26 -  28      3     - the Departure City code
   *   29 -  40     12     - the Arrival Date and Time in yyyyMMddHHmm format
   *   41 -  43      3     - the Arrival City code
   *   44 -  45      2     - the Inventory Class code
   *   46 -  47      2     - the Segment's Status
   *   48 -  50      3     - the Number of Seats
   *   52 -  51      1     - the Code Share flag: 'T' or 'F'
   *   53 -  54      3     - the Code Share Carrier Code
   *   55 -  79     25     - the Code Share Carrier Description
   *   80 -  84      5     - the Code Share Carrier Flight
   *   85 -  85      1     - the Number of Stops on this segment
   *   86 -  89      4     - the Number of Miles for this segment
   *   90 -  92      3     - the Equipment for this flight
   *   93 - 102     10     - the Meals for this flight
   *  103 - 108      6     - the Departure Terminal
   *  109 - 114      6     - the Arrival Terminal
   *  115 - 122      8     - the Remote Locator
   *  123 - 125      3     - the Change of Gauge City
   *  126 - 128      3     - the Change of Gauge Equipment
   *  129 - 138     10     - On Time Performance for this segment
   *  139 - 139      1     - the Flight Status 'X' = cancelled 'O' = ok
   *  140 - 141      2     - the hours part of the flights' duration
   *  142 - 143      2     - the minutes part of the flights' duration
   *  144 - 149      6     - the Departure Gate
   *  150 - 155      6     - the Arrival Gate
   *  156 - 167     12     - the Estimated Departure Date and Time in yyyyMMddHHmm format
   *  168 - 179     12     - the Estimated Arrival Date and Time in yyyyMMddHHmm format
   *  180 - 191     12     - the Departure Gate Out Date and Time
   *  192 - 203     12     - the Departure Field Off Date and Time
   *  204 - 215     12     - the Arrival Gate In Date and Time
   *  216 - 225     10     - a Delay Code
   * </pre>
   *
   * @see xmax.crs.PNR
   * @see xmax.crs.GetPNR.PNRItinSegment
   * @see xmax.crs.GetPNR.PNRItinAirSegment
   ***********************************************************************
   */
  private static String formatPNRAirSegment(final PNRItinAirSegment aAirSegment) throws Exception
    {
    try
      {
      final DecimalFormat SegmentFormat  = new DecimalFormat("00");
      final DecimalFormat SeatFormat     = new DecimalFormat("000");
      final DecimalFormat NumStopsFormat = new DecimalFormat("0");
      final DecimalFormat MilesFormat    = new DecimalFormat("0000");
      final DecimalFormat HoursFormat    = new DecimalFormat("00");
      final DecimalFormat MinutesFormat  = new DecimalFormat("00");
      final DecimalFormat FlightNumFormat = new DecimalFormat("00000");

      final int iHours   = aAirSegment.ElapsedMinutes / 60;
      final int iMinutes = aAirSegment.ElapsedMinutes % 60;

      // set flags
      final String sCodeShareFlag;
      if ( aAirSegment.isCodeShare )
        sCodeShareFlag = "T";
      else
        sCodeShareFlag = "F";

      final String sFlightStatus;
      if ( aAirSegment.isCanceled )
        sFlightStatus = "X";
      else
        sFlightStatus = "O";

      final String sFlightInfo = setWidth("AIR",4)   +
                     SegmentFormat.format(aAirSegment.SegmentNumber) +
                     setWidth(aAirSegment.Carrier,3) +
                     setWidth( Integer.toString(aAirSegment.FlightNumber),5) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.DepartureDateTime ) +
                     setWidth(aAirSegment.DepartureCityCode,3) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.ArrivalDateTime ) +
                     setWidth(aAirSegment.ArrivalCityCode,3) +
                     setWidth(aAirSegment.InventoryClass,2) +
                     setWidth(aAirSegment.Status,2) +
                     SeatFormat.format(aAirSegment.NumberOfSeats) +
                     setWidth(sCodeShareFlag,1) +
                     setWidth(aAirSegment.CodeShareCarrCd,3) +
                     setWidth(aAirSegment.CodeShareCarrDesc,25) +
                     setWidth(aAirSegment.CodeShareCarrFlgt,5) +
                     NumStopsFormat.format(aAirSegment.NumStops) +
                     MilesFormat.format(aAirSegment.Miles) +
                     setWidth(aAirSegment.Equipment,3) +
                     setWidth(aAirSegment.Meals,10) +
                     setWidth(aAirSegment.DepTerminal,6) +
                     setWidth(aAirSegment.ArrTerminal,6) +
                     setWidth(aAirSegment.RemoteLocator,8) +
                     setWidth(aAirSegment.ChangeOfGaugeCity,3) +
                     setWidth(aAirSegment.ChangeOfGaugeEquipment,3) +
                     setWidth(aAirSegment.OnTimePerformance,10) +
                     setWidth(sFlightStatus,1) +
                     HoursFormat.format(iHours) +
                     MinutesFormat.format(iMinutes) +
                     setWidth(aAirSegment.DepGate,6) +
                     setWidth(aAirSegment.ArrGate,6) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.DepEstDateTime ) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.ArrEstDateTime ) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.DepGateOutDateTime ) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.DepFieldOffDateTime ) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.ArrFieldOnDateTime ) +
                     GnrcFormat.FormatLongDateTime( aAirSegment.ArrGateInDateTime ) +
                     setWidth(aAirSegment.DelayCode,10);

      return(sFlightInfo);
      }
    catch (Exception e)
      {
      throw new TranServerException("Unable to format air itinerary record for " + aAirSegment.Carrier + aAirSegment.FlightNumber +  " error = " + e.toString());
      }

    } // formatPNRAirSegment()

  /**
   ***********************************************************************
   * Format a PNR car segment
   ***********************************************************************
   */
  private static String formatPNRCarSegment(final PNRItinCarSegment aCarSegment) throws Exception
    {
    try
      {
      final DecimalFormat SegmentFormat     = new DecimalFormat("00");
      final DecimalFormat NumCarsFormat     = new DecimalFormat("00");
     // final SimpleDateFormat TravelDateTime = new SimpleDateFormat("yyyyMMddHHmm");

      // set rate flag
      final String sRateGuaranteed;
      if ( aCarSegment.RateGuaranteed )
        sRateGuaranteed = "Y";
      else
        sRateGuaranteed = "N";

      final String sCarInfo = setWidth("CAR",4)   +
                     SegmentFormat.format(aCarSegment.SegmentNumber) +
                     setWidth(aCarSegment.Confirmation,20) +
                     setWidth(aCarSegment.DropoffCharge,20) +
                     setWidth(aCarSegment.DropoffCityCode,3) +
                     setWidth(aCarSegment.DropoffCityName,50) +
                     GnrcFormat.FormatLongDateTime(aCarSegment.DropoffDateTime) +
                     setWidth(aCarSegment.GuaranteeInfo,30) +
                     setWidth( aCarSegment.getAddressLine(0), 40) +
                     setWidth( aCarSegment.getAddressLine(1), 40) +
                     setWidth( aCarSegment.getAddressLine(2), 40) +
                     setWidth( aCarSegment.getAddressLine(3), 40) +
                     setWidth( aCarSegment.getPhoneLine(0), 20) +
                     setWidth( aCarSegment.getPhoneLine(1), 20) +
                     setWidth(aCarSegment.PickupCityCode,3) +
                     setWidth(aCarSegment.PickupCityName,50) +
                     GnrcFormat.FormatLongDateTime(aCarSegment.PickUpDateTime) +
                     setWidth(aCarSegment.LocationDescription,50) +
                     setWidth(aCarSegment.Rate,50) +
                     setWidth(aCarSegment.RateCode,20) +
                     setWidth(sRateGuaranteed,1) +
                     setWidth(aCarSegment.SegmentStatus,2) +
                     setWidth(aCarSegment.CompanyCode,2) +
                     setWidth(aCarSegment.CarTypeCode,6) +
                     setWidth(aCarSegment.LocationCode,10) +
                     setWidth(aCarSegment.CompanyName,50) +
                     NumCarsFormat.format(aCarSegment.NumCars) +
                     setWidth(aCarSegment.HoursOfOperation,40);

      return(sCarInfo);
      }
    catch (Exception e)
      {
      throw new TranServerException("Unable to format car rental itinerary record - " + e.toString());
      }

    } // formatPNRCarSegment()

  /**
   ***********************************************************************
   * Format a PNR hotel segment
   ***********************************************************************
   */
  private static String formatPNRHotelSegment(final PNRItinHotelSegment aHotelSegment) throws Exception
    {
    try
      {
      final DecimalFormat SegmentFormat  = new DecimalFormat("00");
      final DecimalFormat NumRoomsFormat = new DecimalFormat("00");

      final String sRateGuaranteed;
      if ( aHotelSegment.RateGuaranteed )
        sRateGuaranteed = "Y";
      else
        sRateGuaranteed = "N";

      final String sConfirmationStatus;
      if ( GnrcFormat.IsNull(aHotelSegment.ConfirmationNumber) )
        sConfirmationStatus = "N";
      else
        sConfirmationStatus = "Y";

      final String sHotelInfo = setWidth("HTL",4)   +
                     SegmentFormat.format(aHotelSegment.SegmentNumber) +
                     GnrcFormat.FormatLongDateTime( aHotelSegment.CheckInDate ) +
                     GnrcFormat.FormatLongDateTime( aHotelSegment.CheckOutDate ) +
                     setWidth(aHotelSegment.CancelPolicy,50) +
                     setWidth(aHotelSegment.Rate,20) +
                     setWidth(sRateGuaranteed,1) +
                     setWidth(aHotelSegment.ConfirmationNumber,20) +
                     setWidth(aHotelSegment.RoomType,8) +
                     setWidth(aHotelSegment.ChainCode,2) +
                     setWidth(aHotelSegment.SegmentStatus,2) +
                     NumRoomsFormat.format(aHotelSegment.NumRooms) +
                     setWidth(aHotelSegment.getAddressLine(0),50) +
                     setWidth(aHotelSegment.getAddressLine(1),50) +
                     setWidth(aHotelSegment.getAddressLine(2),50) +
                     setWidth(aHotelSegment.PropertyCode,10) +
                     setWidth(aHotelSegment.CityCode,3) +
                     setWidth(aHotelSegment.CityName,20) +
                     setWidth(aHotelSegment.Fax,20) +
                     setWidth(aHotelSegment.Name,50) +
                     setWidth(aHotelSegment.Phone,20) +
                     setWidth(aHotelSegment.PostalCode,20) +
                     setWidth(aHotelSegment.ResName,50) +
                     setWidth(aHotelSegment.Guarantee,20);

      return(sHotelInfo);
      }
    catch (Exception e)
      {
      throw new TranServerException("Unable to format hotel itinerary record - " + e.toString());
      }

    } // formatPNRHotelSegment()

  /**
   ***********************************************************************
   * Format a PNR Arunk Segment
   ***********************************************************************
   */
  private static String formatPNRArunkSegment(final PNRItinArunkSegment aItinSegment)
    {
    final DecimalFormat SegmentFormat = new DecimalFormat("00");
    final String sItinInfo = setWidth("ARNK",4)   +
                               SegmentFormat.format(aItinSegment.SegmentNumber) +
                               setWidth(aItinSegment.RawData,200);
    return(sItinInfo);
    } // formatPNRArunkSegment()

  /**
   ***********************************************************************
   * Format an unknown PNR itinerary segment
   ***********************************************************************
   */
  private static String formatPNRUnknownSegment(final PNRItinSegment aItinSegment)
    {
    final DecimalFormat SegmentFormat = new DecimalFormat("00");
    final String sItinInfo = setWidth("UNK",4)   +
                             SegmentFormat.format(aItinSegment.SegmentNumber) +
                             setWidth(aItinSegment.RawData,200);
    return(sItinInfo);
    } // formatPNRUnknownSegment()

  /**
   ***********************************************************************
   * Format the remarks for a PNR
   ***********************************************************************
   */
  private static String formatPNRRemarks(final PNR aPNR)
    {
    final StringBuffer sOutStr = new StringBuffer("");

    try
      {
      // get all remarks
      final PNRRemark[] remarkArray = aPNR.getRemarks();

      String sRemarkData;
      if ( remarkArray instanceof PNRRemark[] )
        {
        for ( int i = 0; i < remarkArray.length; i++ )
          {
          try
            {
            if ( remarkArray[i] instanceof PNRHeaderRemark )
              sRemarkData = formatPNRHeaderRemark( (PNRHeaderRemark )remarkArray[i] );
            else if ( remarkArray[i] instanceof PNRSsrRemark )
              sRemarkData = formatPNRGFactRemark( remarkArray[i],aPNR);
            else if ( remarkArray[i] instanceof PNROsiRemark )
              sRemarkData = formatPNRGFactRemark( remarkArray[i],aPNR);
            else if ( remarkArray[i] instanceof PNRSeatRemark )
              sRemarkData = formatPNRSeatRemark( (PNRSeatRemark)remarkArray[i] );
            else
              sRemarkData = formatPNRGeneralRemark(remarkArray[i]);
            }
          catch (Exception e)
            {
            sRemarkData = formatPNRError(aPNR.getCrs(),aPNR.getLocator(),"",1004,"Unable to format remark '" + remarkArray[i].RemarkText + "': " + e.toString() );
            }

          sOutStr.append(sRemarkData);
          }
        }
      }
    catch (Exception e)
      {
      final String sError = formatPNRError(aPNR.getCrs(),aPNR.getLocator(),"",1004,"Unable to read PNR remarks - " + e.toString());
      sOutStr.append(sError);
      }

    return( sOutStr.toString() );

    } // end formatPNRRemarks()

  /**
   ***********************************************************************
   * This method formats the header of a Passenger Name Record (PNR) Remark,
   * and returns an 86 character fixed-width format in the format
   * detailed below.
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   3      4     - the string 'HEAD', the Amadeus HEADER_REMARK
   *    4 -   5      2     - the message number ID (line number) for this remark
   *    6 -  85      2     - the remark's content
   * </pre><br>
   ***********************************************************************
   */
  private static String formatPNRHeaderRemark(final PNRHeaderRemark aRemark)
    {
    if ( aRemark instanceof PNRHeaderRemark )
      {
      final DecimalFormat LineNumFormat = new DecimalFormat("00");
      final String sHeaderLine =  setWidth(aRemark.HEADER_REMARK,4) +
                                  LineNumFormat.format(aRemark.MessageNumber) +
                                  setWidth(aRemark.RemarkText,80);
      return(sHeaderLine);
      }
    else
      return("");

    } // formatPNRHeaderRemark()

  /**
   ***********************************************************************
   * This method formats a General Remark from of a Passenger Name Record (PNR),
   * and returns a 204 character fixed-width format in the format
   * detailed below.
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   3      4     - the string 'RMKS' - Airware Section
   *    4 -  13     10     - the Remark Type, see {link @getPNRRemarkType}
   *   14 - 141    128     - The remark's content. In the case of a PNRSsrRemark
   *                         the content is prefixed with the string 'SSR-'
   *  142 - 143      2     - the Segment Number (line number) to which the
                             remark applies
   *  144 - 173     30     - the Last Name of the passenger
   *  174 - 203     30     - the First Name of the passenger
   * </pre><br>
   *
   * <p>In the special case of a PNRSsrRemark, the remark's content is prefixed
   *  SSR code, as defined by the airlines, that corresponds to the
   * type of Special Service Request. For a list of these codes see the
   * {@link xmax.crs.GetPNR.PNRSsrRemark PNRSsrRemark} class.
   *
   * @see getPNRRemarkType
   * @see xmax.crs.GetPNR.PNRRemark
   ***********************************************************************
   */
  private static String formatPNRGeneralRemark(final PNRRemark aRemark)
    {
    if ( aRemark instanceof PNRRemark )
      {
      final DecimalFormat SegmentNumFormat = new DecimalFormat("00");
      final String sRemarkType = getPNRRemarkType(aRemark);

      final String sMessage;
      if ( aRemark instanceof PNRSsrRemark )
        sMessage = ((PNRSsrRemark )aRemark).Code + " - " + aRemark.RemarkText;
      else
        sMessage = aRemark.RemarkText;

      final String sRemarkInfo = setWidth("RMKS",4)   +
                                 setWidth(sRemarkType,10) +
                                 setWidth(sMessage,128) +
                                 SegmentNumFormat.format(aRemark.ItinSegment) +
                                 setWidth(aRemark.LastName,30) +
                                 setWidth(aRemark.FirstName,30);

      return(sRemarkInfo);
      }
    else
      return("");
    } // formatPNRGeneralRemark

  /**
   ***********************************************************************
   * This function returns a remark type code based on the subclass of the
   * {@link xmax.crs.GetPNR.PNRRemark PNRRemark} object that is passed to
   * it (see below for a list of the possible subclasses of PNRRemark); the
   * codes returned herewith apply only to eItin, which has a 10 char field
   * for remark type, rather than the 4 char field for Airware.
   *
   * This code is stored in the following PNRRemark constant attributes:
   * <pre>
   *   PNRRemark.SSR_REMARK         = "SSR"
   *   PNRRemark.OSI_REMARK         = "OSI"
   *   PNRRemark.ADDRESS_REMARK     = "ADRS"
   *   PNRRemark.PHONE_REMARK       = "PHNE"
   *   PNRRemark.INVOICE_REMARK     = "INVOICE"
   *   PNRRemark.ITINERARY_REMARK   = "ITINERARY"
   *   PNRRemark.POCKET_ITIN_REMARK = "PKT_ITIN"
   *   PNRRemark.TICKET_REMARK      = "TKT"
   *   PNRRemark.FOP_REMARK         = "FOP"
   *   PNRRemark.GENERAL_REMARK     = "GENERAL"
   * </pre>
   *
   * @see xmax.crs.GetPNR.PNRRemark
   * @see xmax.crs.GetPNR.PNRSsrRemark
   * @see xmax.crs.GetPNR.PNROSIRemark
   * @see xmax.crs.GetPNR.PNRAddressRemark
   * @see xmax.crs.GetPNR.PNRPhoneRemark
   * @see xmax.crs.GetPNR.PNRInvoiceRemark
   * @see xmax.crs.GetPNR.PNRItinRemark
   * @see xmax.crs.GetPNR.PNRPocketItinRemark
   * @see xmax.crs.GetPNR.PNRTicketRemark
   * @see xmax.crs.GetPNR.PNRFopRemark
   ***********************************************************************
   */
  public static String getPNRRemarkType(final PNRRemark aRemark)
    {
    if ( aRemark instanceof PNRSsrRemark )
      return( PNRRemark.SSR_REMARK );
    else if ( aRemark instanceof PNROsiRemark )
      return( PNRRemark.OSI_REMARK );
    else if ( aRemark instanceof PNRAddressRemark )
      return( PNRRemark.ADDRESS_REMARK );
    else if ( aRemark instanceof PNRPhoneRemark )
      return( PNRRemark.PHONE_REMARK );
    else if ( aRemark instanceof PNRInvoiceRemark )
      return( PNRRemark.INVOICE_REMARK );
    else if ( aRemark instanceof PNRItinRemark )
      return( PNRRemark.ITINERARY_REMARK );
    else if ( aRemark instanceof PNRPocketItinRemark )
      return( PNRRemark.POCKET_ITIN_REMARK );
    else if ( aRemark instanceof PNRTicketRemark )
      return( PNRRemark.TICKET_REMARK );
    else if ( aRemark instanceof PNRFopRemark )
      return( PNRRemark.FOP_REMARK );
    else
      return( PNRRemark.GENERAL_REMARK );
    }

  /**
   ***********************************************************************
   * This function returns a remark type code based on the subclass of the
   * {@link xmax.crs.GetPNR.PNRRemark PNRRemark} object that is passed to
   * it (see below for a list of the possible subclasses of PNRRemark); the
   * codes returned herewith apply only to Airware, which has a 4 char field
   * for remark type, rather than the 10 char field for eItin.
   *
   * This code is stored in the following PNRRemark constant attributes:
   * <pre>
   *   PNRRemark.ADDRESS_REMARK     = "ADRS"
   *   PNRRemark.AWR_GENERAL_REMARK = "GENE"
   *   PNRRemark.AWR_INVOICE_REMARK = "INVO"
   *   PNRRemark.AWR_ITIN_REMARK    = "ITIN"
   *   PNRRemark.OSI_REMARK         = "OSI"
   *   PNRRemark.SSR_REMARK         = "SSR"
   *   PNRRemark.AWR_PHONE_REMARK   = "PHNE"
   *   PNRRemark.TICKET_REMARK      = "TKT"
   *   PNRRemark.FOP_REMARK         = "FOP"
   * </pre>
   *
   * @see xmax.crs.GetPNR.PNRRemark
   * @see xmax.crs.GetPNR.PNRSsrRemark
   * @see xmax.crs.GetPNR.PNROSIRemark
   * @see xmax.crs.GetPNR.PNRAddressRemark
   * @see xmax.crs.GetPNR.PNRPhoneRemark
   * @see xmax.crs.GetPNR.PNRInvoiceRemark
   * @see xmax.crs.GetPNR.PNRItinRemark
   * @see xmax.crs.GetPNR.PNRPocketItinRemark
   * @see xmax.crs.GetPNR.PNRTicketRemark
   * @see xmax.crs.GetPNR.PNRFopRemark
   ***********************************************************************
   */
  public static String getAirwarePNRRemarkType(final PNRRemark aRemark)
    {
    if ( aRemark instanceof PNRSsrRemark )
      return( PNRRemark.SSR_REMARK );
    else if ( aRemark instanceof PNROsiRemark )
      return( PNRRemark.OSI_REMARK );
    else if ( aRemark instanceof PNRAddressRemark )
      return( PNRRemark.ADDRESS_REMARK );
    else if ( aRemark instanceof PNRPhoneRemark )
      return( PNRRemark.PHONE_REMARK );
    else if ( aRemark instanceof PNRInvoiceRemark )
      return( PNRRemark.AWR_INVOICE_REMARK );
    else if ( aRemark instanceof PNRItinRemark )
      return( PNRRemark.AWR_ITIN_REMARK );
    else if ( aRemark instanceof PNRTicketRemark )
      return( PNRRemark.TICKET_REMARK );
    else if ( aRemark instanceof PNRFopRemark )
      return( PNRRemark.FOP_REMARK );
    else
      return( PNRRemark.AWR_GENERAL_REMARK );
    } // end getAirwarePNRRemarkType

  /**
   ***********************************************************************
   * This method accepts a Special Service Request remark (SSR) or
   * Other Service Information remark (OSI) from a Passenger Name Record (PNR),
   * reads the carrier code to whom the remark is addressed, and attaches
   * a remark to each segment that is flown by that carrier; the remark is then
   * formatted by the function
   * {@list xmax.Transerver.NativeAsciiWriter.formatPNRGeneralRemark
   * formatPNRGeneral Remark} .
   *
   * If the remark is a seat assignment SSR, that is to say it's code is either
   * 'SEAT', 'NSST', or 'SMST', the method returns an empty string.
   *
   * @see xmax.crs.GetPNR.PNRRemark
   * @see xmax.crs.GetPNR.PNRSsrRemark
   * @see formatPNRGeneralRemark
   ***********************************************************************
   */
  private static String formatPNRGFactRemark(final PNRRemark aRemark, final PNR aPNR)
    {
    // if this is a seat assignment SSR, return nothing
    if ( aRemark instanceof PNRSsrRemark )
      {
      final String[] SEAT_ASSIGNMENT_CODES = {"SEAT","NSST","SMST"};
      if ( GnrcParser.itemIndex( ((PNRSsrRemark )aRemark).Code,SEAT_ASSIGNMENT_CODES) >= 0 )
        return("");
      }

    // get the itinerary segments
    PNRItinSegment[] segments = null;
    try
      {
      segments = aPNR.getSegments();
      }
    catch (Exception e)
      {
      segments = null;
      }

    // format the remarks
    if ( (aRemark.ItinSegment == 0) && (segments instanceof PNRItinSegment[]) )
      {
      // list an SSR line for each matching air segment
      final StringBuffer sOutStr = new StringBuffer("");
      String sRemarkLine;
      PNRItinAirSegment ais;

      // determine which carrier the remark is for
      final String sRemarkCarrier;
      if ( aRemark instanceof PNRSsrRemark )
        sRemarkCarrier = ((PNRSsrRemark )aRemark).Carrier;
      else if ( aRemark instanceof PNROsiRemark )
        sRemarkCarrier = ((PNROsiRemark )aRemark).Carrier;
      else
        sRemarkCarrier = "";


      // create a remark for every itinerary air segment for the given carrier
      for ( int i = 0; i < segments.length; i++ )
        {
        if ( segments[i] instanceof PNRItinAirSegment )
          {
          ais = (PNRItinAirSegment )segments[i];
          if ( sRemarkCarrier.equals(ais.Carrier) || sRemarkCarrier.equals("YY") || sRemarkCarrier.equals(aPNR.getCrs()) )
            {
            aRemark.ItinSegment = ais.SegmentNumber;
            sRemarkLine = formatPNRGeneralRemark(aRemark);
            sOutStr.append(sRemarkLine);
            }

          }
        }

      return( sOutStr.toString() );
      }
    else
      {
      final String sRemarkLine = formatPNRGeneralRemark(aRemark);
      return(sRemarkLine);
      }

    } // formatPNRGFactRemark

  /**
   ***********************************************************************
   * This method formats a Seat Remark from a Passenger Name Record (PNR),
   * and returns a 110 character fixed-width format in the format
   * detailed below.
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   3      4     - the string 'SEAT'
   *    4 -   5      2     - the Family ID Number
   *    6 -   7      2     - the Member ID Number within the Family
   *    8 -  37     30     - the Last Name of the passenger
   *   38 -  67     30     - the first Name of the passenger
   *   68 -  69      2     - the segment ID number (line number)
   *   70 -  72      3     - the Carrier Code
   *   73 -  77      5     - the Flight Number
   *   78 -  87     10     - the Seat Information
   *   88 -  97     10     - the Status of the Seat
   *   98 -  98      1     - 'T' or 'F' flag that indicates whether
                             this is a smoking sit
   *   99 - 109     10     - The Boarding Status of the seat
   * </pre><br>
   *
   * @see xmax.crs.GetPNR.PNRSeatRemark
   ***********************************************************************
   */
  private static String formatPNRSeatRemark(final PNRSeatRemark aSeatRemark)
    {
    if ( aSeatRemark instanceof PNRSeatRemark )
      {
      final DecimalFormat LastNameNumFormat  = new DecimalFormat("00");
      final DecimalFormat FirstNameNumFormat = new DecimalFormat("00");
      final DecimalFormat SegmentNumFormat   = new DecimalFormat("00");

      final String sSmokingFlag;
      if ( aSeatRemark.Smoking )
        sSmokingFlag = "T";
      else
        sSmokingFlag = "F";

      final String sSeatInfo = setWidth("SEAT",4)   +
                       LastNameNumFormat.format(aSeatRemark.FamilyNumber) +
                       FirstNameNumFormat.format(aSeatRemark.MemberNumber) +
                       setWidth(aSeatRemark.LastName,30) +
                       setWidth(aSeatRemark.FirstName,20) +
                       SegmentNumFormat.format(aSeatRemark.ItinSegment) +
                       setWidth(aSeatRemark.Carrier,3) +
                       setWidth( Integer.toString(aSeatRemark.FlightNum),5) +
                       setWidth(aSeatRemark.Seat,10) +
                       setWidth(aSeatRemark.SeatStatus,10) +
                       setWidth(sSmokingFlag,1) +
                       setWidth(aSeatRemark.BoardingStatus,10);

      return(sSeatInfo);
      }
    else
      return("");
    } // formatPNRSeatRemark()

  /**
   ***********************************************************************
   * This method retrieves an error array from the Passenger Name Record
   * (PNR) object which it is passed, runs these errors through the
   * method {@link formatPNRError formatPNRError} , and concatenates
   * and returns these error messages.
   ***********************************************************************
   */
  private static String formatPNRErrors(final PNR aPNR)
    {
    final StringBuffer sOutStr = new StringBuffer("");

    try
      {
      // get all errors
      final String[] errorArray = aPNR.getErrors();

      String sErrorData;
      if ( errorArray instanceof String[] )
        {
        for ( int i = 0; i < errorArray.length; i++ )
          {
          sErrorData = formatPNRError(aPNR.getCrs(),aPNR.getLocator(),"",1004,errorArray[i]);
          sOutStr.append(sErrorData);
          }
        }
      }
    catch (Exception e)
      {
      final String sError = formatPNRError(aPNR.getCrs(),aPNR.getLocator(),"",1004,"Unable to read PNR errors - " + e.toString());
      sOutStr.append(sError);
      }

    return( sOutStr.toString() );
    } // formatPNRErrors()

  /**
   ***********************************************************************
   * This method formats an Error Message retrieved from of a
   * Passenger Name Record (PNR) object, and returns a 1028 character
   * fixed-width format in the format detailed below.
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -    3      4     - the string 'ERR '
   *    4 -    7      4     - the Error Status Code
   *    8 -    9      2     - the Computer Reservation System (CRS) code
   *   10 -   17      8     - the PNR locator
   *   18 -   27     10     - the PseudoCity code
   *   28 - 1027   1000     - the Error Message
   * </pre><br>
   ***********************************************************************
   */
  private static String formatPNRError(final String aCrsCode,
                                         final String aLocator,
                                         final String aPseudoCity,
                                         final int aStatus,
                                         final String aErrorMessage)
    {
    final DecimalFormat StatusFormat  = new DecimalFormat("0000");
    final String sOutStr = setWidth("ERR",4) +
                           StatusFormat.format(aStatus) +
                           setWidth(aCrsCode,2) +
                           setWidth(aLocator,8) +
                           setWidth(aPseudoCity,10) +
                           setWidth(aErrorMessage,1000);

    return(sOutStr);
    }  // formatPNRError

  /**
   ***********************************************************************
   * Get Flifo
   ***********************************************************************
   */
  private static String reqGetFlifo(final ReqGetFlifo aRequest)
    {
    String sCalcDepCity, sCalcArrCity;
    sCalcDepCity = aRequest.DepCity;
    sCalcArrCity = aRequest.ArrCity;

    FlightInfo fInfo = aRequest.Flight;
    try
      {
      // make sure you have a flight object
      if ( (fInfo instanceof FlightInfo) == false )
        {
        fInfo = new FlightInfo(aRequest.Carrier,aRequest.FlightNum);
        throw new FlifoUnrecognizedException("No flight data");
        }

      // get the CRS response
      String sCrsFlifoResponse = "";
      if ( GnrcFormat.NotNull(fInfo.FlightSchedResponse) && GnrcFormat.NotNull(fInfo.DayOfFlifoResponse) )
        {
        sCrsFlifoResponse = fInfo.FlightSchedResponse + "\r\n\r\n" + fInfo.DayOfFlifoResponse;
        }
      else if ( GnrcFormat.NotNull(fInfo.FlightSchedResponse) )
        sCrsFlifoResponse = fInfo.FlightSchedResponse;

      sCrsFlifoResponse = GnrcParser.trimLinesRight(sCrsFlifoResponse);

      // check if flight is canceled
      if ( fInfo.isCanceled(aRequest.DepCity,aRequest.ArrCity) )
        throw new FlifoFlightCanceledException(sCrsFlifoResponse);

      // check that some segments were returned
      {
      final FlightSegment[] segs = fInfo.getFlightSegments();
      if ( (segs instanceof FlightSegment[]) == false )
        throw new FlifoUnrecognizedException(sCrsFlifoResponse);

      //System.out.println("NativeAsciiWriter: Request Dep/Arr City:1: " + aRequest.DepCity +  "/" + aRequest.ArrCity);
      if ((segs.length > 1) & (GnrcFormat.IsNull(aRequest.DepCity) || GnrcFormat.IsNull(aRequest.ArrCity)) )
        {
         for (int i=0; i < segs.length; i++)
           {
            String LstArvDate =
              xmax.util.DateTime.fmtLongDateTime(
              segs[i].ArrSchedDateTime,"ddMMM").toUpperCase();
            System.out.println("NativeAsciiWriter: LstArvDate: " + LstArvDate);
            System.out.println("NativeAsciiWriter: DepartCity: " + segs[i].DepartCity);
            System.out.println("NativeAsciiWriter: ArriveCity: " + segs[i].ArriveCity);
            System.out.println("NativeAsciiWriter: Out/In: " + segs[i].DepGateOutDateTime + "/" + segs[i].ArrGateInDateTime);
            System.out.println("NativeAsciiWriter: Off: " + segs[i].DepFieldOffDateTime);
            sCalcDepCity = segs[i].DepartCity;
            sCalcArrCity = segs[i].ArriveCity;
            if (GnrcFormat.NotNull(aRequest.DepCity) && (aRequest.DepCity.equals(sCalcDepCity)))
              break;
            else if (GnrcFormat.NotNull(aRequest.ArrCity) && (aRequest.ArrCity.equals(sCalcArrCity)))
              break;
           }
        }
        System.out.println("NativeAsciiWriter: Request Dep/Arr City:2: " + sCalcDepCity +  "/" + sCalcArrCity);
      }

      // make sure city pair is served
      if ( GnrcFormat.NotNull(aRequest.DepCity) )
        {
        if ( fInfo.hasDepCity(aRequest.DepCity) == false )
          throw new FlifoCityPairException("No flight segments depart from " + aRequest.DepCity,sCrsFlifoResponse);
        }

      if ( GnrcFormat.NotNull(aRequest.ArrCity) )
        {
        if ( fInfo.hasArrCity(aRequest.ArrCity) == false )
          throw new FlifoCityPairException("No flight segments arrive into " + aRequest.ArrCity,sCrsFlifoResponse);
        }

      // format the output string
      final String sOutStr;
      if ( aRequest.commType == aRequest.COMM_NATIVE_AIRWARE )
        {
        sOutStr = formatAirwareFlifo(fInfo,sCalcDepCity,sCalcArrCity,
                                       FlifoException.FLIGHT_OPERATIONAL,sCrsFlifoResponse,"");
        }
      else
        {
        sOutStr = formatFlifo(fInfo,sCalcDepCity,sCalcArrCity,
                                FlifoException.FLIGHT_OPERATIONAL,sCrsFlifoResponse,"");
        }

      return(sOutStr);
      }
    catch (Exception e)
      {
      final String sFlightStatus;
      final String sCrsResponse;
      if ( e instanceof FlifoException )
        {
        sFlightStatus = ((FlifoException )e).getErrorCode();
        sCrsResponse  = ((FlifoException )e).getFlifoResponse();
        }
      else
        {
        sFlightStatus = FlifoException.PARSING_ERROR;
        sCrsResponse  = "";
        }

      final String sOutStr;
      if ( aRequest.commType == aRequest.COMM_NATIVE_AIRWARE )
        {
        sOutStr = formatAirwareFlifo(fInfo,sCalcDepCity,sCalcArrCity,
                                       sFlightStatus,sCrsResponse,e.toString());
        }
      else
        {
         sOutStr = formatFlifo(fInfo,sCalcDepCity,sCalcArrCity,sFlightStatus,
                                 sCrsResponse,e.toString());
        }

      return(sOutStr);
      }

    }

  /**
   ***********************************************************************
   * Format a Flight string
   ***********************************************************************
   */
  private static String formatFlifo(final FlightInfo aFlight, final String aDepCity, final String aArrCity,
                                      final String aStatusCode, final String aCrsResponse, String aErrorText)
    {
    final DecimalFormat NumStopsFormat = new DecimalFormat("0");
    final DecimalFormat MilesFormat    = new DecimalFormat("00000");
    final DecimalFormat StatusFormat   = new DecimalFormat("0000");
    final DecimalFormat HoursFormat    = new DecimalFormat("00");
    final DecimalFormat MinutesFormat  = new DecimalFormat("00");

    // figure the flight time
    final int iElapsedHours   = aFlight.getElapsedMinutes(aDepCity,aArrCity) / 60;
    final int iElapsedMinutes = aFlight.getElapsedMinutes(aDepCity,aArrCity) % 60;

    // set the code share flag
    final String sCodeShareFlag;
    if ( aFlight.isCodeShare(aDepCity,aArrCity) )
      sCodeShareFlag = "T";
    else
      sCodeShareFlag = "F";

    // set the departure city if needed
    final String sDepCity;
    if ( GnrcFormat.IsNull(aDepCity) )
      sDepCity = aFlight.getDepCity();
    else
      sDepCity = aDepCity;

    // set the arrival city if needed
    final String sArrCity;
    if ( GnrcFormat.IsNull(aArrCity) )
      sArrCity = aFlight.getArrCity();
    else
      sArrCity = aArrCity;


    final String sOutStr =     setWidth(GnrcConvControl.GET_FLIGHT_INFO_RESP,8)   +
                               StatusFormat.format(0) +
                               setWidth(aFlight.getCarrier(),3) +
                               setWidth( Integer.toString(aFlight.getFlightNum()),5) +
                               setWidth(sDepCity,3) +
                               setWidth(sArrCity,3) +
                               GnrcFormat.FormatLongDateTime( aFlight.getDepSchedDate(aDepCity) ) +
                               GnrcFormat.FormatLongDateTime( aFlight.getArrSchedDate(aArrCity) ) +
                               setWidth(aStatusCode,1) +
                               setWidth(aFlight.getMeal(aDepCity,aArrCity),10) +
                               setWidth(aFlight.getEquipment(aDepCity,aArrCity),3) +
                               HoursFormat.format(iElapsedHours) +
                               MinutesFormat.format(iElapsedMinutes) +
                               MilesFormat.format(aFlight.getAirMiles(aDepCity,aArrCity)) +
                               setWidth(aFlight.getDelayCode(aDepCity,aArrCity),10) +
                               NumStopsFormat.format(aFlight.getNumStops(aDepCity,aArrCity)) +
                               setWidth(aFlight.getDepTerm(aDepCity),6) +
                               setWidth(aFlight.getArrTerm(aArrCity),6) +
                               setWidth(aFlight.getDepGate(aDepCity),6) +
                               setWidth(aFlight.getArrGate(aArrCity),6) +
                               GnrcFormat.FormatLongDateTime( aFlight.getDepEstDate(aDepCity) ) +
                               GnrcFormat.FormatLongDateTime( aFlight.getArrEstDate(aArrCity) ) +
                               GnrcFormat.FormatLongDateTime( aFlight.getDepOutGateDate(aDepCity) ) +
                               GnrcFormat.FormatLongDateTime( aFlight.getDepOffFieldDate(aDepCity) ) +
                               GnrcFormat.FormatLongDateTime( aFlight.getArrOnFieldDate(aArrCity) ) +
                               GnrcFormat.FormatLongDateTime( aFlight.getArrInGateDate(aArrCity) ) +
                               setWidth(aFlight.getChangeOfGaugeCity(aDepCity,aArrCity),3) +
                               setWidth(aFlight.getChangeOfGaugeEquipment(aDepCity,aArrCity),3) +
                               setWidth(aFlight.getOnTimePerformance(aDepCity,aArrCity),10) +
                               setWidth(sCodeShareFlag,1) +
                               setWidth(aFlight.getCodeShareCarrierCode(aDepCity,aArrCity),3) +
                               setWidth(aFlight.getCodeShareCarrierName(aDepCity,aArrCity),25) +
                               setWidth(aFlight.getCodeShareFlight(aDepCity,aArrCity),5) +
                               setWidth(aCrsResponse,1000) +
                               setWidth(aErrorText,500);

    return(sOutStr);
    }

  /**
   ***********************************************************************
   * Add air segment
   ***********************************************************************
   */
  private static String reqAddAirSeg(final ReqAddAirSeg aRequest)
    {
    final StringBuffer sResponse = new StringBuffer();

    final DecimalFormat StatusFormat = new DecimalFormat("00000");
    sResponse.append( setWidth(GnrcConvControl.AWR_ADD_AIRSEG_RESP,8) + StatusFormat.format(GnrcConvControl.STATUS_OK));

    // append each segment status
    final PNRItinSegment[] segs = aRequest.getSegments();
    if ( segs instanceof PNRItinSegment[] )
      {
      PNRItinAirSegment airseg = null;
      for ( int i = 0; i < segs.length; i++ )
        {
        if ( segs[i] instanceof PNRItinAirSegment )
          {
          airseg = (PNRItinAirSegment )segs[i];
          sResponse.append( setWidth(airseg.Status,4) );
          }
        else if ( segs[i] instanceof PNRItinArunkSegment )
          sResponse.append("ARNK");
        else
          sResponse.append("????");
        }
      }

    return( sResponse.toString() );
    }

  /**
   ***********************************************************************
   * End Transaction
   ***********************************************************************
   */
  private static String reqEndTransaction(final ReqEndTransaction aRequest)
    {
    // format the output response
    final DecimalFormat StatusFormat = new DecimalFormat("00000");
    final String sResponse = setWidth(GnrcConvControl.AWR_END_XACT_RESP,8) +
                             StatusFormat.format(GnrcConvControl.STATUS_OK) +
                             setWidth(aRequest.Locator,8);

    return(sResponse);
    }

  /**
   ***********************************************************************
   * FreeForm response
   ***********************************************************************
   */
  private static String reqFreeForm(final ReqFreeForm aRequest)
    {
    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    final String sOpCode;
    if ( aRequest.commType == aRequest.COMM_NATIVE_AIRWARE )
      sOpCode = GnrcConvControl.AWR_FREEFORM_RESP;
    else
      sOpCode = GnrcConvControl.FREEFORM_RESP;


    final String sResponse = setWidth(sOpCode,8) +
                             StatusFormat.format(GnrcConvControl.STATUS_OK) +
                             setWidth(aRequest.Response,10000);
    return(sResponse);
    }

  /**
   ***********************************************************************
   * response for ignore command
   ***********************************************************************
   */
  private static String reqIgnore(final ReqIgnore aRequest)
    {
    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    final String sOpCode;
    if ( GnrcFormat.NotNull(aRequest.NativeAsciiRequest) )
      sOpCode = aRequest.NativeAsciiRequest;
    else
      sOpCode = GnrcConvControl.AWR_IGNORE_RESP;


    final String sResponse = setWidth(sOpCode,8) +
                             StatusFormat.format(GnrcConvControl.STATUS_OK);
    return(sResponse);
    }

  /**
   ***********************************************************************
   * response for Start Session command
   ***********************************************************************
   */
  private static String reqSessionStart(final ReqSessionStart aRequest)
    {
    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    final String sOpCode;
    if ( GnrcFormat.NotNull(aRequest.NativeAsciiRequest) )
      sOpCode = aRequest.NativeAsciiRequest;
    else
      sOpCode = GnrcConvControl.AWR_IGNORE_RESP;


    final String sResponse = setWidth(sOpCode,8) +
                             StatusFormat.format(GnrcConvControl.STATUS_OK);
    return(sResponse);
    }

  /**
   ***********************************************************************
   * response for End Session command
   ***********************************************************************
   */
  private static String reqSessionEnd(final ReqSessionEnd aRequest)
    {
    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    final String sOpCode;
    if ( GnrcFormat.NotNull(aRequest.NativeAsciiRequest) )
      sOpCode = aRequest.NativeAsciiRequest;
    else
      sOpCode = GnrcConvControl.AWR_IGNORE_RESP;


    final String sResponse = setWidth(sOpCode,8) +
                             StatusFormat.format(GnrcConvControl.STATUS_OK);
    return(sResponse);
    }

  /**
   ***********************************************************************
   * Availability response
   ***********************************************************************
   */
  private static String reqGetAvail(final ReqGetAvail aRequest) throws Exception
    {
    if ( (aRequest.avail instanceof DestAvailability) == false )
      throw new TranServerException("Invalid DestAvailability object");

    // check if this if for the airware format
    if ( aRequest.commType == aRequest.COMM_NATIVE_AIRWARE )
      return( formatAirwareDestAvailability(aRequest.avail) );

    // use regular availability response
    final StringBuffer sResponse = new StringBuffer();

    // format the itineraries
    final ItinAvailability[] itins = aRequest.avail.getItinArray();
    if ( itins instanceof ItinAvailability[] )
      {
      String sItinAvail;
      for ( int i = 0; i < itins.length; i++ )
        {
        if ( aRequest.avail.itinFitsCriteria(itins[i]) )
          {
          sItinAvail = formatItinAvailability(itins[i],i + 1);
          sResponse.append(sItinAvail);
          }
        }
      }

    // add raw data
    final int MAX_WIDTH = 1000;
    String sSegment;
    for ( int i = 0; i < aRequest.avail.RawData.length(); i += MAX_WIDTH )
      {
      if ( (i + MAX_WIDTH) >= aRequest.avail.RawData.length() )
        sSegment = GnrcConvControl.GET_AVAIL3_RESP +
                   setWidth(aRequest.avail.RawData.substring(i),MAX_WIDTH);
      else
        sSegment = GnrcConvControl.GET_AVAIL3_RESP +
                   aRequest.avail.RawData.substring(i,i + MAX_WIDTH);

      sResponse.append(sSegment);
      }

    return( sResponse.toString() );
    }

  /**
   ***********************************************************************
   * Availability response
   * This function returns a formatted string for a single itinerary
   ***********************************************************************
   */
  private static String formatItinAvailability(final ItinAvailability aItin, final int aItinIndex)
    {
    final StringBuffer sOutString = new StringBuffer();

    final FlightAvailability[] flights = aItin.getFlightAvailabilities();
    if ( flights instanceof FlightAvailability[] )
      {
      final DecimalFormat NumStopsFormat = new DecimalFormat("0");
      final DecimalFormat IndexFormat    = new DecimalFormat("000");
      final DecimalFormat StatusFormat   = new DecimalFormat("00000");

      FlightAvailability fseg;
      String sFlightSegment;
      String sIsCharter;
      String sClassInfo;

      for ( int iSegNum = 0; iSegNum < flights.length; iSegNum++ )
        {
        fseg = flights[iSegNum];

        if ( fseg.isCharter )
          sIsCharter = "T";
        else
          sIsCharter = "F";

        sFlightSegment = setWidth(GnrcConvControl.GET_AVAIL1_RESP,8)   +
                         IndexFormat.format(aItinIndex) +            // itin index     (1-based)
                         IndexFormat.format(iSegNum + 1) +           // segment index  (1-based)
                         setWidth(fseg.Carrier,3) +
                         setWidth( Integer.toString(fseg.FlightNum),5) +
                         setWidth(fseg.DepCity,3) +
                         setWidth(fseg.ArrCity,3) +
                         GnrcFormat.FormatLongDateTime( fseg.DepDate ) +
                         GnrcFormat.FormatLongDateTime( fseg.ArrDate ) +
                         NumStopsFormat.format( fseg.NumStops ) +
                         setWidth(fseg.Meal,10) +
                         setWidth(fseg.Equipment,3) +
                         setWidth(sIsCharter,1) +
                         setWidth(fseg.SharedCarrCode,2) +
                         setWidth(fseg.SharedCarrDesc,25) +
                         setWidth(fseg.SharedCarrFlight,5) +
                         setWidth(fseg.EquipChangeCity,3) +
                         setWidth(fseg.EquipChangeCode,3);

        sClassInfo = formatClassAvailability(fseg,aItinIndex,iSegNum + 1);

        sOutString.append( sFlightSegment );
        sOutString.append( sClassInfo );
        }
      }

    return( sOutString.toString() );
    }

  /**
   ***********************************************************************
   * Availability response
   * This function returns a formatted string for a single itinerary
   ***********************************************************************
   */
  private static String formatClassAvailability(final FlightAvailability aFlight,
                                           final int aItinIndex,
                                           final int aSegmentIndex)
    {
    final StringBuffer sOutString = new StringBuffer("");

    final InvClassAvailability[] inv_classes = aFlight.getInvClassAvailability();
    if ( inv_classes instanceof InvClassAvailability[] )
      {
      InvClassAvailability inv_clos;
      String sClassInfo;
      final DecimalFormat NumSeatsFormat = new DecimalFormat("000");
      final DecimalFormat IndexFormat    = new DecimalFormat("000");
      final DecimalFormat StatusFormat   = new DecimalFormat("00000");
      final DecimalFormat SeqFormat      = new DecimalFormat("000");

      for ( int i = 0; i < inv_classes.length; i++ )
        {
        inv_clos = inv_classes[i];

        sClassInfo = setWidth(GnrcConvControl.GET_AVAIL2_RESP,8)   +
                     IndexFormat.format(aItinIndex) +            // itin index (1-based)
                     IndexFormat.format(aSegmentIndex) +         // segment index (1-based)
                     SeqFormat.format( i + 1 ) +                 // class index (1-based)
                     setWidth( inv_clos.getInvClass() ,2) +
                     NumSeatsFormat.format( inv_clos.getNumSeats() );

        sOutString.append( sClassInfo );
        }
      }

    return( sOutString.toString() );
    }

  /**
   ***********************************************************************
   * Connection Times
   ***********************************************************************
   */
  private static String reqGetConnectTimes(final ReqGetConnectTimes aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();

    sOutStr.append( GnrcFormat.SetWidth(GnrcConvControl.GET_CONN_TM_RESP,8) );
    final DecimalFormat StatusFormat  = new DecimalFormat("00000");
    sOutStr.append( StatusFormat.format(GnrcConvControl.STATUS_OK) );


    final DecimalFormat minutesFormat = new DecimalFormat("000");
    String sConnection;
    String sInType;
    String sOutType;
    ConnectTimes connect;
    for ( int i = 0; i < aRequest.numConnections(); i++ )
      {
      connect = aRequest.getConnection(i);
      if ( (connect.InBound instanceof FlightSegment) && (connect.OutBound instanceof FlightSegment) )
        {
        sConnection = GnrcFormat.SetWidth( connect.InBound.Carrier,2 ) +
                      GnrcFormat.SetWidth(  Integer.toString(connect.InBound.FlightNum),5 ) +
                      GnrcFormat.FormatLongDate( connect.InBound.DepSchedDateTime ) +
                      GnrcFormat.SetWidth( connect.InBound.DepartCity,3 ) +
                      GnrcFormat.SetWidth( connect.InBound.ArriveCity,3 ) +
                      GnrcFormat.SetWidth( connect.InBound.DepartCountry,3 ) +
                      GnrcFormat.SetWidth( connect.InBound.ArriveCountry,3 ) +
                      minutesFormat.format( connect.MinimumConnectMinutes);

        sOutStr.append(sConnection);

        if ( (i + 1) == aRequest.numConnections() )
          {
          sConnection = GnrcFormat.SetWidth( connect.OutBound.Carrier,2 ) +
                        GnrcFormat.SetWidth(  Integer.toString(connect.OutBound.FlightNum),5 ) +
                        GnrcFormat.FormatLongDate( connect.OutBound.DepSchedDateTime ) +
                        GnrcFormat.SetWidth( connect.OutBound.DepartCity,3 ) +
                        GnrcFormat.SetWidth( connect.OutBound.ArriveCity,3 ) +
                        GnrcFormat.SetWidth( connect.OutBound.DepartCountry,3 ) +
                        GnrcFormat.SetWidth( connect.OutBound.ArriveCountry,3 ) +
                        minutesFormat.format(0);

          sOutStr.append(sConnection);
          }
        }
      }

    return( sOutStr.toString() );
    }

  /**
   ***********************************************************************
   * Get connect times
   * This function returns the appropriate response for a connect time request
   ***********************************************************************
   */
  private static String formatMinutes(final int aElapsedMinutes)
    {
    if ( aElapsedMinutes > 0 )
      {
      final int iHours   = aElapsedMinutes / 60;
      final int iMinutes = aElapsedMinutes % 60;

      final DecimalFormat numFormat = new DecimalFormat("00");

      final String sTimeStr = numFormat.format(iHours) + numFormat.format(iMinutes) + "00";
      return(sTimeStr);
      }
    else
      return("      ");
    }

  /**
   ***********************************************************************
   * Connection Times
   ***********************************************************************
   */
  private static String reqGetConnectTimesAirport(
      final ReqGetConnectTimesAirport aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();

    sOutStr.append( GnrcFormat.SetWidth(GnrcConvControl.GET_AIRPORT_TM_RESP,8) );
    final DecimalFormat StatusFormat  = new DecimalFormat("00000");
    sOutStr.append( StatusFormat.format(GnrcConvControl.STATUS_OK) );

    final DecimalFormat minutesFormat = new DecimalFormat("000");

    ConnectTimesAirport[] airportList = aRequest.getAirportConnectTimesList();

    for ( int i = 0; i < airportList.length; i++ )
      {
      ConnectTimesAirport airport = airportList[i];

      sOutStr.append(GnrcFormat.SetWidth(airport.getAirportCode(),3));

      // append the four connection time (dd, di, id, ii) pairs (online-offline)
      // where d=Domestic i=International
      for (int j=0; j < 4; j++)
        {
        if (airport.onlineTimes[j] == -1)
          sOutStr.append(GnrcFormat.SetWidth("",3));
        else
          sOutStr.append(minutesFormat.format(airport.onlineTimes[j]));

        if (airport.offlineTimes[j] == -1)
          sOutStr.append(GnrcFormat.SetWidth("",3));
        else
          sOutStr.append(minutesFormat.format(airport.offlineTimes[j]));
        }
      } // end for - airportList

    return( sOutStr.toString() );
    }

  /**
   ***********************************************************************
   * Get Fares
   ***********************************************************************
   */
  private static String reqGetFare(final ReqGetFare aRequest) throws Exception
    {
    //    if ( aRequest.commType == aRequest.COMM_NATIVE_AIRWARE )
    //      throw new TranServerException("Fares for the original Airware format are not supported yet");

    // make sure you get some fares back
    final PNRFare[] fareList = aRequest.pnr.getFares();
    if ( (fareList instanceof PNRFare[]) == false )
      throw new TranServerException("No fares were returned");


    // construct response
    final DecimalFormat StatusFormat      = new DecimalFormat("00000");
    final DecimalFormat NumPsgrFormat     = new DecimalFormat("000");
    final DecimalFormat BaseAmountFormat  = new DecimalFormat("000000000");
    final DecimalFormat TaxAmountFormat   = new DecimalFormat("0000000");

    // set the store flag
    final String sStoreFlag;
    if ( aRequest.StoreFares )
      sStoreFlag = "T";
    else
      sStoreFlag = "F";

    final StringBuffer sResponse = new StringBuffer();

    sResponse.append( setWidth(GnrcConvControl.GET_FARE_RESP,8) );
    sResponse.append( StatusFormat.format(GnrcConvControl.STATUS_OK) );
    sResponse.append( setWidth(aRequest.getCrsCode(),2) );
    sResponse.append( setWidth(aRequest.getLocator(),8) );
    sResponse.append( setWidth(aRequest.getFareType(),1) );
    sResponse.append( setWidth(sStoreFlag,1) );

    final String NULL_FARE = setWidth("",351);

    // The current fixed-width API for Airware can only accept up to
    // 4 different fare types.  Hence, the magical 4 hard-coding herewith.

    for ( int i = 0; i < 4; i++ )
      {
      if ( i < fareList.length )
        {

        PNRFare fare    = fareList[i];
				Tax[] taxTotals = fareList[i].getTaxTotals();
        String sPTC     = fare.getGenericPTC();
        int iNumPsgrs   = fare.getNumPsgrs();
        long BaseFare   = fare.getBaseFare();
        System.out.println("NativeAsciiWriter:reqGetFare: " + BaseFare);

        // create the taxes string
        String sTaxes = "";
        for (int j=0; j < taxTotals.length ; j++)
          {
          sTaxes += taxTotals[j].type;
          sTaxes += TaxAmountFormat.format(taxTotals[j].amount);
          } // end for

        final String sPTCFare = setWidth( sPTC,3) +
                     NumPsgrFormat.format( iNumPsgrs ) +
                     BaseAmountFormat.format( BaseFare ) +
                     setWidth(sTaxes, 36) +
                     setWidth( fare.getRawData(), 300);

        sResponse.append( sPTCFare );
        }
      else
        sResponse.append( NULL_FARE );
      }

    return( sResponse.toString() );
    }

  /**
   ***********************************************************************
   * Get Hotel information
   ***********************************************************************
   */
  private static String reqGetHotelInfo(final ReqGetHotelInfo aRequest) throws Exception
    {
    if ( (aRequest.HotelInformation instanceof String) == false )
      throw new TranServerException("No hotel information was returned by host");

    final StringBuffer sResponse = new StringBuffer();

    // make a header
    final String sSegmentHeader = setWidth(GnrcConvControl.GET_HOTEL_INFO_RESP,8) +
                                  setWidth(aRequest.getCrsCode(),2) +
                                  setWidth(aRequest.HotelChain,2) +
                                  setWidth(aRequest.PropertyCode,10) +
                                  setWidth("ALL",20);

    // pass back the entire response in 500 char chunks
    final int MAX_WIDTH = 500;
    String sSegment;
    for ( int i = 0; i < aRequest.HotelInformation.length(); i += MAX_WIDTH )
      {
      if ( (i + MAX_WIDTH) >= aRequest.HotelInformation.length() )
        sSegment = setWidth(aRequest.HotelInformation.substring(i),MAX_WIDTH);
      else
        sSegment = aRequest.HotelInformation.substring(i,i + MAX_WIDTH);

      sResponse.append(sSegmentHeader);
      sResponse.append(sSegment);
      }

    return( sResponse.toString() );
    }

  /**
   ***********************************************************************
   * Get Status information
   ***********************************************************************
   */
  private static String reqGetStatus(final ReqGetStatus aRequest)
    {
    final DecimalFormat PortFormat     = new DecimalFormat("00000");
    final DecimalFormat TaNumFormat    = new DecimalFormat("00000");
    final DecimalFormat HostTypeFormat = new DecimalFormat("00000");

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

    final String sOutString = setWidth(GnrcConvControl.GET_STATUS_RESP,8) +
                              setWidth(aRequest.GatewayServer,20) +
                              PortFormat.format(aRequest.GatewayPort) +
                              setWidth(aRequest.TaName,20) +
                              TaNumFormat.format( aRequest.TaNumber ) +
                              HostTypeFormat.format( aRequest.HostType ) +
                              setWidth(aRequest.HostCode,2) +
                              setWidth(aRequest.SignOn,20) +
                              setWidth(aRequest.Password,20) +
                              setWidth(aRequest.PseudoCity,5) +
                              setWidth(aRequest.Version,50) +
															setWidth(aRequest.APIVersion,25) +
                              setWidth(sProps,200);

    return(sOutString);
    }


  /**
   ***********************************************************************
   * If ticketing is disabled, this method appends a string indicating such
   * fact to the response code
   ***********************************************************************
   */
  private static String reqIssueTicket(ReqIssueTicket aRequest)
    {
    if (ReqIssueTicket.isTicketingEnabled() == true)
      return( reqDefault(GnrcConvControl.AWR_TKT_PNR_RESP) );
    else
      {
      final DecimalFormat StatusFormat = new DecimalFormat("00000");

      String sCommand = "";
      try {
				System.out.println("NativeAsciiWrite.reqIssueTicket: ");
				sCommand = xmax.crs.Amadeus.AmadeusGetPNRFareConversation.getTicketCommand(aRequest);
				final String sInfRequest  =
          xmax.crs.Amadeus.AmadeusGetPNRFareConversation.getInfTktCommand(aRequest);
        if ( GnrcFormat.NotNull(sInfRequest) ) sCommand += "\n" + sInfRequest;

      } catch (Exception e) { }

      final String sResponse =
        setWidth(GnrcConvControl.AWR_TKT_PNR_RESP,8) + StatusFormat.format(GnrcConvControl.STATUS_OK) +
        " Ticketing is currently disabled - ticketing command: '" + sCommand + "'";

      return(sResponse);
      }

    } // end reqIssueTicket

  
  /**
   ***********************************************************************
   * If ticketing is disabled, this method appends a string indicating such
   * fact to the response code
   ***********************************************************************
   */   
  private static String reqGetTicketInfo(ReqGetTicketInfo aRequest) throws Exception
    {
      final DecimalFormat StatusFormat = new DecimalFormat("00000");
      final DecimalFormat SequenceFormat = new DecimalFormat("00");
      final DecimalFormat AmountFormat =    new DecimalFormat("00000.00"); 
      final DecimalFormat NegativeAmountFormat = new DecimalFormat("0000.00");    
      final DecimalFormat TaxAmountFormat = new DecimalFormat("0000.00");
      
 
      class FormatAmount{
    	
    	  public String fmt(final BigDecimal aAmount)
    	  {
    		  if (aAmount == null)
    		  {
    			  return "00000.00";
    		  }
    		  else if (aAmount.signum() < 0)
    		  {
    			  return NegativeAmountFormat.format(aAmount);  
    		  }
    		  else
    		  {
    			  return AmountFormat.format(aAmount);
    		  }
    	  }
      }
      final FormatAmount formatAmount = new FormatAmount();
      
      final StringBuilder sBuf = new StringBuilder();
      
      // operation and status   (length is 8+5 = 13 chars)
      sBuf.append( setWidth(GnrcConvControl.AWR_TKT_INFO_CMD,8)  );
      sBuf.append( StatusFormat.format(GnrcConvControl.STATUS_OK) );
      
      // for each ticket  (each ticket section takes 1388 chars)
      for (TicketInformation tktInformation : aRequest.getTicketInformation())
      {
    	  // this section is 37 chars  (now 20)
    	//  sBuf.append( setWidth(tktInformation.getAuthID(), 15)  );
        //  sBuf.append( SequenceFormat.format(tktInformation.getAuthSequence()) );  //  2 chars
    	  sBuf.append( setWidth(tktInformation.getPsgrID(), 20)  );
    	  
          // add segment info    ((3+3+8) * 10 = 140 chars) -  added fields, now its 37 * 10 = 370 chars
      	  for (Integer iSegNum : tktInformation.getSegments())
      	  {
      		  final PNRItinAirSegment segment = (PNRItinAirSegment )aRequest.getPnr().getItinSegment(iSegNum);
      		  
      		  final String sFlightNum = Integer.toString(segment.FlightNumber);
      		  
          	  sBuf.append( setWidth(segment.Carrier, 3)  );
         	  sBuf.append( setWidth(sFlightNum, 5)  );
        	  sBuf.append( setWidth(segment.DepartureCityCode, 3)  );
         	  sBuf.append( setWidth(segment.ArrivalCityCode, 3)  );
         	  sBuf.append( GnrcFormat.FormatLongDate( segment.DepartureDateTime ) );
         	  sBuf.append( setWidth(" ", 15) );     // place holder for the fare basis calculation
      	  }
      	  
          // add blanks as needed
          for (int i = tktInformation.getSegments().size(); i < 10; i++)
          {
         	  sBuf.append( setWidth("", 37) );
          }
    	
          // this section is 398 chars
    	  sBuf.append( setWidth(tktInformation.getTicketNumber(), 15)  );
          sBuf.append( formatAmount.fmt(tktInformation.getTicketValue()) );  //  8 chars
          sBuf.append( tktInformation.isEticket() ? "T" : "F");
       	  sBuf.append( setWidth(tktInformation.getCurrencyCode(), 3)  );
       	  sBuf.append( GnrcFormat.FormatLongDate( tktInformation.getTicketDate().getTime() ) ); 
       	  sBuf.append( setWidth(tktInformation.getAgency(), 8)  );
       	  sBuf.append( setWidth(tktInformation.getOfficeID(), 9)  );
       	  sBuf.append( setWidth(tktInformation.getAgent(), 6)  );
       	  sBuf.append( setWidth(tktInformation.getItem(), 6)  );
          sBuf.append( formatAmount.fmt(tktInformation.getCcAmount()) );
          sBuf.append( formatAmount.fmt(tktInformation.getCashAmount()) );
          sBuf.append( formatAmount.fmt(tktInformation.getTaxAmount()) );
          sBuf.append( formatAmount.fmt(tktInformation.getFeeAmount()) );
          sBuf.append( formatAmount.fmt(tktInformation.getCommissionAmount()) );
      	  sBuf.append( setWidth(tktInformation.getDocumentNumber(), 14)  );
      	  sBuf.append( setWidth(tktInformation.getPsgrName(), 60)  );
      	  sBuf.append( setWidth(tktInformation.getTourCode(), 20)  );
      	  sBuf.append( setWidth(tktInformation.getInvoice(), 20)  );
       	  sBuf.append( setWidth(tktInformation.getFop1(), 60)  );
      	  sBuf.append( setWidth(tktInformation.getFop2(), 60)  );
      	  sBuf.append( setWidth(tktInformation.getFop3(), 60)  );
      	  
      	  
      	  // add the taxes  ((7+2) * 8 = 72 chars)
      	  for (String sTaxCode : tktInformation.getTaxes().keySet())
      	  {
      		  final BigDecimal taxAmount = tktInformation.getTaxes().get(sTaxCode);
              sBuf.append( TaxAmountFormat.format(taxAmount) );
         	  sBuf.append( setWidth(sTaxCode, 2)  );
      	  }
      	  
          // add blanks as needed
          for (int i = tktInformation.getTaxes().size(); i < 8; i++)
          {
         	  sBuf.append( setWidth("", 9) );
          }
      	  
       	  
          // this section is 741 chars
      	  sBuf.append( setWidth(tktInformation.getExchangeValue(), 60)  );
      	  sBuf.append( setWidth(tktInformation.getNewTicket(), 60)  );
      	  sBuf.append( setWidth(tktInformation.getOrigin(), 60)  );
      	  sBuf.append( setWidth(tktInformation.getPurchaser(), 60)  );
      	  sBuf.append( setWidth(tktInformation.getFareLadder(), 500)  );
          sBuf.append( tktInformation.isAutomated() ? "T" : "F");
          
          final int iLength = sBuf.length();
      }
      
      
      // add blanks as needed
      /*
      for (int i = aRequest.getTicketInformation().size(); i < 20; i++)
      {
     	  sBuf.append( setWidth("", 1601)  );
      }
       */
      
      return(sBuf.toString());
     

    } // end reqIssueTicket

  

  /**
   ***********************************************************************
   * Get PNR for Airware
   ***********************************************************************
   */
  private static String formatPNRAirwareHeader(final int aStatus, final String aSection, final String aLocator)
    {
    final DecimalFormat StatusFormat = new DecimalFormat("00000");
        
    final String sTitle  = setWidth(GnrcConvControl.AWR_GET_PNR_RESP,8) +
                           StatusFormat.format(aStatus) +
                           setWidth(aSection,4)   +
                           setWidth(aLocator,8);

    return( sTitle );
    } // HeaderString()

  /**
   ***********************************************************************
   * This method formats the information contained in a Passenger Name Record
   * Group Header.
   *
   * @param pnr
   *
   * @return a 51 char string formatted as follows:<br>
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   8      8     - the string defined by GncrConvControl.AWR_GET_PNR_RESP
   *    8 -  13      5     - the response status code
   *   13 -  17      4     - the string 'CORP'
   *   17 -  25      8     - the record locator
   *   25 -  28      3     - the number of seats available
   *   28 -  31      3     - the number of seats booked
   *   31 -  51     20     - the text of the Group Header
   * </pre><br>
   *
   ***********************************************************************
   */
  private static String formatPNRAirwareGroupHeader(PNR pnr)
    {
    PNRGroupHeader groupHeader = pnr.getGroupHeader();
    String sName = "";
    final DecimalFormat SeatFormat  = new DecimalFormat("000");

    if (groupHeader instanceof PNRGroupHeader)
      {
      sName = formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"CORP",pnr.getLocator()) +
              SeatFormat.format(pnr.getNumSeatsAvailable()) +
              SeatFormat.format(pnr.getNumSeatsBooked()) +
              setWidth(groupHeader.headerText,20);

      }
    return(sName);
    } // end formatPNRGroupHeader

  /**
   ***********************************************************************
   * Get PNR for Airware
   ***********************************************************************
   */
  private static String formatPNRAirwareNames(final PNR aPNR)
    {
    final StringBuffer sOutStr = new StringBuffer("");

  //  final String sCorpHeader = getCorpHeader(aPNR);

    // get PNR family records
    try
      {

      final PNRFamilyElement[] Families = aPNR.getFamilies();
      if ( Families instanceof PNRFamilyElement[] )
        {
        final DecimalFormat SeatFormat  = new DecimalFormat("000");

        PNRNameElement Member;
        PNRNameElement Infant;
        String sName;
        String sInfantName;
        String sPsgrName;
        int iNumAvail;

        // for each family
        for ( int iFamNum = 0; iFamNum < Families.length; iFamNum++ )
          {
          /*
          if ( Families[iFamNum].isCorpHeader )
            {
            iNumAvail = Families[iFamNum].getNumSeats() - Families[iFamNum].NumBooked;
            if ( iNumAvail < 0 )
              iNumAvail = 0;

            sName = formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"CORP",aPNR.getLocator()) +
                      SeatFormat.format( iNumAvail ) +
                      SeatFormat.format(Families[iFamNum].NumBooked) +
                      setWidth(Families[iFamNum].getLastName(),20);

            sOutStr.append(sName);
            }
          else
            {
          */
            // for each member of the family
            for ( int iMemberNum = 0; iMemberNum < Families[iFamNum].FamilyMembers.length; iMemberNum++ )
              {
              Member = Families[iFamNum].FamilyMembers[iMemberNum];

              if ( Member.isInfant() == false )
                {
                sPsgrName = Member.LastName + "/" + Member.FirstName;

                Infant = aPNR.getInfantName(Member);
                if ( Infant instanceof PNRNameElement )
                  sInfantName = Infant.FirstName;
                else
                  sInfantName = "";

                sName = formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"NAME",aPNR.getLocator()) +
                        setWidth(Member.getPassengerID(),20) +
                        setWidth(Member.CrsPsgrID,3) +
                        setWidth(Member.PTC,3) +
                        setWidth(sPsgrName,60) +
                        setWidth("",40) +
                        setWidth(sInfantName,30);

                sOutStr.append(sName);
                }
              }
            //}

          }
        }

      }
    catch (Exception e)
      {
      sOutStr.append( formatPNRAirwareError(aPNR.getLocator(),GnrcConvControl.STS_ERR_GET_PNR,"Unable to read PNR Names - " + e.toString()) );
      }

    return( sOutStr.toString() );
    } // getAirwareNames()


  /**
   ***********************************************************************
   * formats itinerary segment information into an ASCII string format readable
   * by Airware; isPNR2 is a boolean to indicate that we are using the GETPNR2
   * verb, to which we are migrating; after the migration is complete, the
   * former GETPNR verb will be replaced by GETPNR2, which will be renamed
   * GETPNR; at that time the isPNR2 flag will disappear
   ***********************************************************************
   */
  private static String formatPNRAirwareItin(final PNR aPNR, boolean isPNR2)
    {
    final StringBuffer sOutStr = new StringBuffer("");

    // get PNR itinerary segments
    try
      {

      final PNRItinSegment[] Segments = aPNR.getSegments();
      if ( Segments instanceof PNRItinSegment[] )
        {
        for ( int i = 0; i < Segments.length; i++ )
          {
          if ( Segments[i] instanceof PNRItinAirSegment )
            {
            if (isPNR2 == false)
              sOutStr.append( formatPNRAirwareFlightSeg(
                    aPNR.getLocator(), (PNRItinAirSegment)Segments[i]) );
            else
              // we are migrating to this format - this is a temporary hack
              // which will be removed after the migration is completed
              sOutStr.append( formatPNR2AirwareFlightSeg(
                    aPNR.getLocator(), (PNRItinAirSegment)Segments[i]) );
            }
          /*
          else if (Segments[i] instanceof PNRItinArunkSegment)
            {
              sOutStr.append( formatPNR2ArunkSeg( aPNR.getLocator() ));
            }
          */
          }
        }
      }
    catch (Exception e)
      {
      sOutStr.append( formatPNRAirwareError(
            aPNR.getLocator(),GnrcConvControl.STS_ERR_GET_PNR,
            "Unable to read PNR Itinerary - " + e.toString()) );
      }


    return( sOutStr.toString() );
    } // getAirwareItin()


  /**
   ***********************************************************************
   * GetPNR for Airware
   ***********************************************************************
   */
  private static String formatPNRAirwareFlightSeg(final String aLocator, final PNRItinAirSegment aAirSegment)
    {
    // print out only the air segments
    final StringBuffer sOutStr = new StringBuffer("");

    try
      {
      final DecimalFormat SeatFormat = new DecimalFormat("000");

      final String sFlightInfo = formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"ITIN",aLocator) +
                                 setWidth(aAirSegment.Carrier,3) +
                                 setWidth( Integer.toString(aAirSegment.FlightNumber),5) +
                                 GnrcFormat.FormatAirwareDateTime(aAirSegment.DepartureDateTime) +
                                 setWidth(aAirSegment.DepartureCityCode,3) +
                                 GnrcFormat.FormatAirwareDateTime(aAirSegment.ArrivalDateTime) +
                                 setWidth(aAirSegment.ArrivalCityCode,3) +
                                 setWidth(aAirSegment.InventoryClass,2) +
                                 setWidth(aAirSegment.Status,2) +
                                 SeatFormat.format(aAirSegment.NumberOfSeats);

      sOutStr.append(sFlightInfo);

      if ( GnrcFormat.NotNull(aAirSegment.RemoteLocator) )
        {
        final String sRemoteRloc = formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"RMTL",aLocator) +
                                   setWidth(aAirSegment.Carrier,2) +
                                   "/" +
                                   setWidth(aAirSegment.RemoteLocator,8) +
                                   setWidth("",36);

        sOutStr.append(sRemoteRloc);
        }
      }
    catch (Exception e)
      {
      sOutStr.append( formatPNRAirwareError(aLocator,GnrcConvControl.STS_ERR_GET_PNR,"Unable to format air itinerary record for " + aAirSegment.Carrier + aAirSegment.FlightNumber +  " error = " + e.toString()) );
      }

    return( sOutStr.toString() );
    } // getAirwareFlightSeg()


  private static String formatPNR2AirwareFlightSeg(
      final String aLocator, final PNRItinAirSegment aAirSegment)
    {
    // print out only the air segments
    final StringBuffer sOutStr = new StringBuffer("");

    try
      {
      final DecimalFormat SeatFormat = new DecimalFormat("000");

      sOutStr.append(formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"ITIN",aLocator));
      sOutStr.append(setWidth(aAirSegment.Carrier,3));
      sOutStr.append(setWidth( Integer.toString(aAirSegment.FlightNumber),5));
      sOutStr.append(GnrcFormat.FormatAirwareDateTime(aAirSegment.DepartureDateTime));
      sOutStr.append(setWidth(aAirSegment.DepartureCityCode,3));
      sOutStr.append(GnrcFormat.FormatAirwareDateTime(aAirSegment.ArrivalDateTime));
      sOutStr.append(setWidth(aAirSegment.ArrivalCityCode,3));
      sOutStr.append(setWidth(aAirSegment.InventoryClass,2));
      sOutStr.append(setWidth(aAirSegment.Status,2));
      sOutStr.append(SeatFormat.format(aAirSegment.NumberOfSeats));

      sOutStr.append(setWidth(aAirSegment.Carrier,2));
      sOutStr.append("/");
      sOutStr.append(setWidth(aAirSegment.RemoteLocator,8));

      // this will be replaced by code that formats the managed block locator
      sOutStr.append(setWidth(aAirSegment.BlockCrsCode,2)); // the Block Manager Crs Code
      sOutStr.append("/");
      sOutStr.append(setWidth(aAirSegment.BlockLocator,8)); // the Managed Block Locator
      String eTicketFlag = (aAirSegment.is_eTicketeable) ? "T":"F";
      sOutStr.append(setWidth(eTicketFlag,1));
      sOutStr.append(setWidth("F",1)); // ARNK flag
      sOutStr.append(setWidth("",7)); // filler
      }
    catch (Exception e)
      {
      sOutStr.append( formatPNRAirwareError(
            aLocator,GnrcConvControl.STS_ERR_GET_PNR,
            "Unable to format air itinerary record for " +
            aAirSegment.Carrier + aAirSegment.FlightNumber +
            " error = " + e.toString()) );
      }

    return( sOutStr.toString() );
    } // getAirwareFlightSeg()


  /** formats an ARNK segment (filler segment) in PNR2 format */
  private static String formatPNR2ArunkSeg(final String aLocator)
    {

    // print out only the air segments
    final StringBuffer sOutStr = new StringBuffer("");

    try
      {
      sOutStr.append(formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"ITIN",aLocator));
      sOutStr.append(setWidth("",59));
      sOutStr.append(setWidth("T",1));
      sOutStr.append(setWidth("",7));  // filler
      }
    catch (Exception e)
      {
      sOutStr.append( formatPNRAirwareError(
            aLocator,GnrcConvControl.STS_ERR_GET_PNR,
            "Unable to format arunk itinerary record error = " + e.toString()) );
      }

    return( sOutStr.toString() );
    }


  /**
   ***********************************************************************
   *
   ***********************************************************************
   */
  private static String formatPNRAirwareError(
      final String aLocator, final int aStatus, final String aErrorMessage)
    {
    final String sOutStr =
      formatPNRAirwareHeader(aStatus,"ERR",aLocator) + setWidth(aErrorMessage,1000);
    return(sOutStr);
    }

  /**
   ***********************************************************************
   * Formats an availability response for Airware
   ***********************************************************************
   */
  private static String formatAirwareDestAvailability(final DestAvailability aAvail)
    {
    final StringBuffer sResponse = new StringBuffer();

    final ItinAvailability[] itins = aAvail.getItinArray();
    String sItinAvail;
    if ( itins instanceof ItinAvailability[] )
      {
      for ( int i = 0; i < itins.length; i++ )
        {
        if ( aAvail.itinFitsCriteria(itins[i]) )
          {
          sItinAvail = formatAirwareItinAvailability(itins[i],i + 1);
          sResponse.append(sItinAvail);
          }
        }
      }

    if ( sResponse.length() == 0 )
      {
      final DecimalFormat StatusFormat = new DecimalFormat("0000");
      //final String sError = setWidth(GnrcConvControl.AWR_AVL_ITIN_RESP,8) +
      //                      StatusFormat.format(GnrcConvControl.STS_NO_ITINS);

      sResponse.append(setWidth(GnrcConvControl.AWR_AVL_ITIN_RESP,8));
      sResponse.append(StatusFormat.format(GnrcConvControl.STS_NO_ITINS));

      if (aAvail.getErrors() instanceof String)
        sResponse.append(aAvail.getErrors());

      //sResponse.append(sError);
      }

    return( sResponse.toString() );
    }

  /**
   ***********************************************************************
   * This function returns a formatted string for a single itinerary
   ***********************************************************************
   */
  private static String formatAirwareItinAvailability(final ItinAvailability aItin, final int aItinIndex)
    {
    final StringBuffer sOutString = new StringBuffer();

    final FlightAvailability[] flights = aItin.getFlightAvailabilities();
    if ( flights instanceof FlightAvailability[] )
      {
      final DecimalFormat NumStopsFormat = new DecimalFormat("0");
      final DecimalFormat IndexFormat    = new DecimalFormat("000");
      final DecimalFormat StatusFormat   = new DecimalFormat("00000");

      FlightAvailability fseg;
      String sFlightSegment;
      String sIsCharter;
      String sClassInfo;

      for ( int iSegNum = 0; iSegNum < flights.length; iSegNum++ )
        {
        fseg = flights[iSegNum];

        if ( fseg.isCharter )
          sIsCharter = "T";
        else
          sIsCharter = "F";

        sFlightSegment = setWidth(GnrcConvControl.AWR_AVL_ITIN_RESP,8)   +
                         StatusFormat.format(GnrcConvControl.STATUS_OK) +
                         IndexFormat.format(iSegNum + 1) +            // itin index     (1-based)
                         setWidth(fseg.Carrier,3) +
                         setWidth( Integer.toString(fseg.FlightNum),5) +
                         setWidth(fseg.DepCity,3) +
                         setWidth(fseg.ArrCity,3) +
                         GnrcFormat.FormatAirwareDateTime( fseg.DepDate ) +
                         GnrcFormat.FormatAirwareDateTime( fseg.ArrDate ) +
                         NumStopsFormat.format( fseg.NumStops ) +
                         setWidth(fseg.Meal,4) +
                         setWidth(fseg.Equipment,3) +
                         setWidth(sIsCharter,1) +
                         setWidth(fseg.SharedCarrCode,2) +
                         setWidth(fseg.SharedCarrDesc,25) +
                         setWidth(fseg.SharedCarrFlight,5) +
                         setWidth(fseg.EquipChangeCity,3) +
                         setWidth(fseg.EquipChangeCode,3);

        sClassInfo = formatAirwareClassAvailability(fseg,aItinIndex,iSegNum + 1);

        sOutString.append( sFlightSegment );
        sOutString.append( sClassInfo );
        }
      }

    return( sOutString.toString() );
    }

  /**
   ***********************************************************************
   * This function returns a formatted string for a single itinerary
   ***********************************************************************
   */
  private static String formatAirwareClassAvailability(final FlightAvailability aFlight,
                                                  final int aItinIndex,
                                                  final int aSegmentIndex)
    {
    final StringBuffer sOutString = new StringBuffer("");

    final InvClassAvailability[] inv_classes = aFlight.getInvClassAvailability();
    if ( inv_classes instanceof InvClassAvailability[] )
      {
      InvClassAvailability inv_clos;
      String sClassInfo;
      final DecimalFormat NumSeatsFormat = new DecimalFormat("000");
      final DecimalFormat IndexFormat    = new DecimalFormat("000");
      final DecimalFormat StatusFormat   = new DecimalFormat("00000");
      final DecimalFormat SeqFormat      = new DecimalFormat("000");

      for ( int i = 0; i < inv_classes.length; i++ )
        {
        inv_clos = inv_classes[i];

        sClassInfo = setWidth(GnrcConvControl.AWR_AVL_CLASS_RESP,8)   +
                     StatusFormat.format(GnrcConvControl.STATUS_OK) +
                     IndexFormat.format(aSegmentIndex) +            // segment index (1-based)
                     SeqFormat.format( i + 1 ) +                    // class index (1-based)
                     setWidth( inv_clos.getInvClass() ,2) +
                     NumSeatsFormat.format( inv_clos.getNumSeats() ) +
                     setWidth("1010101",7);  // hard-coded for backward compatibility
                                             // with AirMac, per John Crowley 5-jun-2001

        sOutString.append( sClassInfo );
        }
      }

    return( sOutString.toString() );
    }

  /** 
   ***********************************************************************
   * This function returns the appropriate response for a Flifo request
   ***********************************************************************
   */
  public static String formatAirwareFlifo(
      final FlightInfo aFlight, final String aDepCity, 
      final String aArrCity, final String aStatusCode, 
      final String aCrsResponse, String aErrorText)
    {
    final DecimalFormat NumStopsFormat = new DecimalFormat("0");
    final DecimalFormat MilesFormat    = new DecimalFormat("00000");
    final DecimalFormat StatusFormat   = new DecimalFormat("00000");
    final DecimalFormat HoursFormat    = new DecimalFormat("00");
    final DecimalFormat MinutesFormat  = new DecimalFormat("00");


    // figure the flight time
    final int iElapsedHours   = aFlight.getElapsedMinutes(aDepCity,aArrCity)/60;
    final int iElapsedMinutes = aFlight.getElapsedMinutes(aDepCity,aArrCity)%60;

    // set the valid data flag
    final String sValid;
    if ( GnrcFormat.NotNull(aCrsResponse) )
      sValid = "T";
    else
      sValid = "F";

    // set departure time variable
    final long iDepTime;
    if (aFlight.getDepOutGateDate(aDepCity) > 0)
    {
    	iDepTime = aFlight.getDepOutGateDate(aDepCity);
    }
    else if (aFlight.getDepEstDate(aDepCity) > 0)
    {
    	iDepTime = aFlight.getDepEstDate(aDepCity);
    }
    else
    {
        iDepTime = aFlight.getDepSchedDate(aDepCity);
    }

    
    
    // set arrival time variable
    final long iArrTime;
    if (aFlight.getArrInGateDate(aArrCity) > 0)
    {
    	iArrTime = aFlight.getArrInGateDate(aArrCity);
    }
    else if (aFlight.getArrEstDate(aArrCity) > 0)
    {
    	iArrTime = aFlight.getArrEstDate(aArrCity);
    }
    else
    {
        iArrTime = aFlight.getArrSchedDate(aArrCity);
    }

    
    
    
    // set the departure city if needed
    final String sDepCity;
    if ( GnrcFormat.IsNull(aDepCity) )
      sDepCity = aFlight.getDepCity();
    else
      sDepCity = aDepCity;

    // set the arrival city if needed
    final String sArrCity;
    if ( GnrcFormat.IsNull(aArrCity) )
      sArrCity = aFlight.getArrCity();
    else
      sArrCity = aArrCity;

    final int iStatus;
    if ( GnrcFormat.IsNull(aErrorText) )
      iStatus = GnrcConvControl.STATUS_OK;
    else
      iStatus = GnrcConvControl.STS_CRS_ERR;

    final String sOutStr = setWidth(GnrcConvControl.AWR_FLIFO_RESP,8)   +
                               StatusFormat.format(iStatus) +
                               setWidth(aFlight.getCarrier(),3) +
                               setWidth( Integer.toString(aFlight.getFlightNum()),5) +
                               GnrcFormat.FormatAirwareDate( aFlight.getDepSchedDate(aDepCity) ) +
                               setWidth(sDepCity,3) +
                               GnrcFormat.FormatAirwareDate( aFlight.getArrSchedDate(aArrCity) ) +
                               setWidth(sArrCity,3) +
                               setWidth(aFlight.getMeal(aDepCity,aArrCity),1) +
                               setWidth(aFlight.getEquipment(aDepCity,aArrCity),3) +
                               HoursFormat.format(iElapsedHours) +
                               MinutesFormat.format(iElapsedMinutes) +
                               "00" +
                               MilesFormat.format(aFlight.getAirMiles(aDepCity,aArrCity)) +
                               GnrcFormat.FormatAirwareTime( iDepTime ) +
                               GnrcFormat.FormatAirwareTime( aFlight.getDepOffFieldDate(aDepCity) ) +
                               GnrcFormat.FormatAirwareTime( aFlight.getArrOnFieldDate(aArrCity) ) +
                               GnrcFormat.FormatAirwareTime( iArrTime ) +
                               setWidth(aFlight.getDelayCode(aDepCity,aArrCity),5) +
                               NumStopsFormat.format(aFlight.getNumStops(aDepCity,aArrCity)) +
                               GnrcFormat.FormatAirwareDate( aFlight.getDepOutGateDate(aDepCity) ) +
                               GnrcFormat.FormatAirwareDate( aFlight.getDepOffFieldDate(aDepCity) ) +
                               GnrcFormat.FormatAirwareDate( aFlight.getArrOnFieldDate(aArrCity) ) +
                               GnrcFormat.FormatAirwareDate( aFlight.getArrInGateDate(aArrCity) ) +
                               setWidth(aFlight.getDepGate(aDepCity),3) +
                               setWidth(aFlight.getArrGate(aArrCity),3) +
                               setWidth(sValid,1) +
                               setWidth( aFlight.getCodeShareCarrierCode(aDepCity,aArrCity),2) +
                               setWidth( aFlight.getCodeShareCarrierName(aDepCity,aArrCity),25) +
                               setWidth( aFlight.getCodeShareFlight(aDepCity,aArrCity),5) +
                               setWidth( aFlight.getChangeOfGaugeCity(aDepCity,aArrCity),3) +
                               setWidth( aFlight.getChangeOfGaugeEquipment(aDepCity,aArrCity),3);
                           //  setWidth(aCrsResponse,600);

    return(sOutStr);
    }


  /**
   ***********************************************************************
   * Formats the Remarks returned in a {@link PNR} object - calls other methods
   * to format specific types of remarks; currently only General Remarks are
   * implemented
   ***********************************************************************
   */
  static String formatPNRAirwareRemarks(PNR pnr)
    {
    final StringBuffer out = new StringBuffer("");

    try
      {
      // get all remarks
      final PNRRemark[] remarkArray = pnr.getRemarks();

      //String sRemarkData;
      if ( remarkArray instanceof PNRRemark[] )
        {
        for ( int i = 0; i < remarkArray.length; i++ )
          {
          try
            {
            /* // not implemented yet
            if ( remarkArray[i] instanceof PNRHeaderRemark )
              out.append(
                  formatPNRAirwareRemarkHeader( (PNRHeaderRemark)remarkArray[i] ));
            else if ( remarkArray[i] instanceof PNRSsrRemark )
              out.append(
                  formatPNRAirwareRemarkGFact( remarkArray[i],aPNR) );

            else if ( remarkArray[i] instanceof PNROsiRemark )
              out.append( 
                  formatPNRAirwareRemarkGFact( remarkArray[i],aPNR) );

            else if ( remarkArray[i] instanceof PNRSeatRemark )
              out.append( 
                  formatPNRAirwareRemarkSeat( (PNRSeatRemark)remarkArray[i] );
            */

            //else
              out.append( 
                  formatPNRAirwareRemarkGeneral(remarkArray[i], pnr.getLocator()) ); }
          catch (Exception e)
            {
            out.append(formatPNRAirwareError(
                  pnr.getLocator(), GnrcConvControl.STS_FORMAT_ERR,
                  "Unable to format remark '" + remarkArray[i].RemarkText + 
                  "': " + e.toString() ));
            }
          }
        }
      }
    catch (Exception e)
      {
      out.append( formatPNRAirwareError(
            pnr.getLocator(), GnrcConvControl.STS_NO_REMARKS,
            "Unable to read PNR remarks - " + e.toString()) );
      }

    return( out.toString() );

    } // end formatPNRAirwareRemarks

  /** 
   ***********************************************************************
   * This method formats a General Remark from of a Passenger Name Record (PNR),
   * in Airware format
   * 
   * <pre><b>
   *   position  length           description</b>
   *    0 -   8      8     - operation: 'GETPNR '
   *    8 -  13      5     - Status
   *   13 -  17      4     - Section: 'RMKS'
   *   17 -  25      8     - pnr locator
   *   25 -  29      4     - remark type
   *   29 -  33      4     - ssr/osi code
   *   33 - 161    128     - remark's text
   *  161 - 181     20     - passenger ID
   * </pre><br>
   *
   * @see getPNRRemarkType
   * @see xmax.crs.GetPNR.PNRRemark
   ***********************************************************************
   */
  private static String formatPNRAirwareRemarkGeneral(final PNRRemark aRemark, String sLocator)
    {
    StringBuffer out = new StringBuffer();

    if ( aRemark instanceof PNRRemark )
      {
      //final DecimalFormat SegmentNumFormat = new DecimalFormat("00");
      //final String sRemarkType = getPNRRemarkType(aRemark);

      out.append(formatPNRAirwareHeader(GnrcConvControl.STATUS_OK,"RMKS",sLocator));
      out.append(GnrcFormat.SetWidth(getAirwarePNRRemarkType(aRemark),4));
      
      if (aRemark instanceof PNRSsrRemark)
        out.append(GnrcFormat.SetWidth( ((PNRSsrRemark)aRemark).Code,4)); // SSR code
      else
        out.append(GnrcFormat.SetWidth("",4));

      out.append(GnrcFormat.SetWidth(aRemark.getRemarkText(),128));
      out.append(GnrcFormat.SetWidth(aRemark.getPsgrID(),20));

      /*
      final String sMessage;
      if ( aRemark instanceof PNRSsrRemark )
        sMessage = ((PNRSsrRemark )aRemark).Code + " - " + aRemark.RemarkText;
      else
        sMessage = aRemark.RemarkText;
      */

      return(out.toString());
      }
    else
      return("");
    } // formatPNRGeneralRemark

  /** 
   ***********************************************************************
   * This function returns the appropriate response for a Flifo request
   ***********************************************************************
   */
  private static String reqListBranches(final ReqListBranches aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();

    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    sOutStr.append( setWidth(GnrcConvControl.LIST_BRANCH_RESP,8) +
                               StatusFormat.format(GnrcConvControl.STATUS_OK) +
                               setWidth(aRequest.HomePseudoCity,10) );


    if ( aRequest.Branches instanceof String[] )
      {
      for ( int i = 0; i < aRequest.Branches.length; i++ )
        sOutStr.append( setWidth(aRequest.Branches[i],10) );
      }

    return( sOutStr.toString() );
    }

  /** 
   ***********************************************************************
   * This function returns the appropriate response for listing group profiles
   ***********************************************************************
   */
  private static String reqListGroupProfiles(final ReqListGroupProfiles aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();

    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    sOutStr.append( setWidth(GnrcConvControl.LIST_GROUP_PROF_RESP,8) +
                               StatusFormat.format(GnrcConvControl.STATUS_OK) +
                               setWidth(aRequest.PseudoCity,10) );


    if ( aRequest.Groups instanceof String[] )
      {
      for ( int i = 0; i < aRequest.Groups.length; i++ )
        sOutStr.append( setWidth(aRequest.Groups[i],30) );
      }

    return( sOutStr.toString() );
    }

  /** 
   ***********************************************************************
   * This function returns the appropriate response for listing personal profiles
   ***********************************************************************
   */
  private static String reqListPersonalProfiles(final ReqListPersonalProfiles aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();

    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    sOutStr.append( setWidth(GnrcConvControl.LIST_PER_PROF_RESP,8) +
                               StatusFormat.format(GnrcConvControl.STATUS_OK) +
                               setWidth(aRequest.PseudoCity,10) +
                               setWidth(aRequest.Group,30) );


    if ( aRequest.Names instanceof String[] )
      {
      for ( int i = 0; i < aRequest.Names.length; i++ )
        sOutStr.append( setWidth(aRequest.Names[i],30) );
      }

    return( sOutStr.toString() );
    }

  /** 
   ***********************************************************************
   * This function returns the appropriate response for a profile
   ***********************************************************************
   */
  private static String reqGetProfile(final ReqGetProfile aRequest) throws Exception
    {
    final StringBuffer sOutStr = new StringBuffer();

    if ( (aRequest.Profile instanceof Profile) == false )
      throw new TranServerException("No profile data");

    final String sProfileHeader = getProfileHeader( aRequest.Profile );
    sOutStr.append(sProfileHeader);

    final ProfileElement[] elements = aRequest.Profile.getElements();
    String sSection;
    if ( elements instanceof ProfileElement[] )
      {
      for ( int i = 0; i < elements.length; i++ )
        {
        sSection = getProfileSection(elements[i]);
        sOutStr.append( sSection );
        }
      }

    final String sRawData = getProfileRawData( aRequest.Profile.getRawData() );
    sOutStr.append(sRawData);

    return( sOutStr.toString() );
    }

  /** 
   ***********************************************************************
   * This function returns the header for a profile
   ***********************************************************************
   */
  private static String getProfileHeader(final Profile aProfile)
    {
    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    final String sActive;
    if ( aProfile.isActive() )
      sActive = "T";
    else
      sActive = "F";

    final String sResult = setWidth(GnrcConvControl.GET_PER_PROF_RESP,8) +
                             StatusFormat.format(GnrcConvControl.STATUS_OK) +
                             setWidth(aProfile.getCrsCode(),2) +
                             setWidth(aProfile.getPsuedoCity(),10) +
                             setWidth(aProfile.getGroupName(),30) +
                             setWidth(aProfile.getTravelerName(),30) +
                             setWidth(aProfile.getCaption(),50) + 
                             setWidth(aProfile.getDescription(),50) +
                             GnrcFormat.FormatLongDate(aProfile.getLastModDate()) +
                             GnrcFormat.FormatLongDate(aProfile.getLastAccessDate()) +
                             setWidth(aProfile.getAgentSign(),10) +
                             setWidth(sActive,1);

    return(sResult);
    }

  /** 
   ***********************************************************************
   * This function returns the header for a profile
   ***********************************************************************
   */
  private static String getProfileSection(final ProfileElement aElement)
    {
    if ( aElement instanceof ProfileElement )
      {
      final DecimalFormat LineNumFormat = new DecimalFormat("000");

      // set the move flag
      final String sMoveFlag;

      if ( aElement.getUsage() == aElement.ALWAYS_MOVE )
        sMoveFlag = "A";
      else if ( aElement.getUsage() == aElement.NEVER_MOVE )
        sMoveFlag = "N";
      else
        sMoveFlag = "O";

      final Object element    = aElement.getElement();
      final String sSection   = getSectionType(element);
      final String sQualifier = aElement.getQualifier();

      if ( element instanceof PNRNameElement )
        {
        final PNRNameElement name = (PNRNameElement )element;
        final String sResult = setWidth(sSection,10) +
                               LineNumFormat.format( aElement.getLineNum() ) +
                               setWidth(sMoveFlag,1) +
                               setWidth(sQualifier,1) +
                               setWidth(name.LastName,30) +
                               setWidth(name.FirstName,20) +
                               setWidth(name.MiddleName,1) +
                               setWidth(name.Title,8) +
                               setWidth(name.PTC,3) +
                               setWidth(name.getPassengerID(),25) +
                               setWidth(name.InfantName,25);
        return(sResult);
        }
      else if ( element instanceof PNRSsrRemark )
        {
        final PNRSsrRemark ssr = (PNRSsrRemark )element;
        final String sResult = setWidth(sSection,10) +
                               LineNumFormat.format( aElement.getLineNum() ) +
                               setWidth(sMoveFlag,1) +
                               setWidth(sQualifier,1) +
                               setWidth(ssr.RemarkText,128) +
                               setWidth(ssr.Code,4) +
                               setWidth(ssr.Carrier,2);
        return(sResult);
        }
      else if ( element instanceof PNROsiRemark )
        {
        final PNROsiRemark osi = (PNROsiRemark )element;
        final String sResult = setWidth(sSection,10) +
                               LineNumFormat.format( aElement.getLineNum() ) +
                               setWidth(sMoveFlag,1) +
                               setWidth(sQualifier,1) +
                               setWidth(osi.RemarkText,128) +
                               setWidth(osi.Carrier,2);
        return(sResult);
        }
      else if ( element instanceof PNRRemark )
        {
        final PNRRemark remark = (PNRRemark )element;
        final String sResult = setWidth(sSection,10) +
                               LineNumFormat.format( aElement.getLineNum() ) +
                               setWidth(sMoveFlag,1) +
                               setWidth(sQualifier,1) +
                               setWidth(remark.RemarkText,128);
        return(sResult);
        }
      else if ( element instanceof String )
        {
        final String sText = (String )element;
        final String sResult = setWidth(sSection,10) +
                               LineNumFormat.format( aElement.getLineNum() ) +
                               setWidth(sMoveFlag,1) +
                               setWidth(sQualifier,1) +
                               setWidth(sText,128);
        return(sResult);
        }

      }

    return("");
    }

  /** 
   ***********************************************************************
   * This function returns the rawdata for a profile
   ***********************************************************************
   */
  private static String getProfileRawData(final String aRawData)
    {
    final StringBuffer sResult = new StringBuffer();

    if ( aRawData instanceof String )
      {
      final StringTokenizer lines = new StringTokenizer(aRawData,"\r\n");
      final String sHeader = setWidth("RAWDATA",10);
      String sLine;
      while ( lines.hasMoreTokens() )
        {
        sLine = lines.nextToken();
        sResult.append( sHeader + setWidth(sLine,70) );
        }
      }

    return( sResult.toString() );
    }

  /**
   ***********************************************************************
   * This function returns the appropriate response for splitting a PNR
   ***********************************************************************
   */
  private static String reqSplitPNR(final ReqSplitPNR aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();
    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    sOutStr.append( setWidth(GnrcConvControl.SPLIT_PNR_RESP,8) +
                               StatusFormat.format(GnrcConvControl.STATUS_OK) +
                               setWidth(aRequest.OriginalLocator,8) +
                               setWidth(aRequest.SplitLocator,8) );

    return( sOutStr.toString() );
    }


  /**
   ***********************************************************************
   * Returns the original locator, the split locator if any, and the status
   * codes returned for any new air segments sold.
   ***********************************************************************
   */
  private static String reqChangePnrItin(final ReqChangePnrItin aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();
    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    sOutStr.append( setWidth(GnrcConvControl.CHG_PNR_ITIN_RESP,8) +
                    StatusFormat.format(GnrcConvControl.STATUS_OK) +
                    setWidth(aRequest.locator,8) +
                    setWidth(aRequest.splitLocator,8));

    // append each segment status
    final PNRItinSegment[] segs = aRequest.getNewSegments();
    if ( segs instanceof PNRItinSegment[] )
      {
      PNRItinAirSegment airseg = null;
      for ( int i = 0; i < segs.length; i++ )
        {
        if ( segs[i] instanceof PNRItinAirSegment )
          {
          airseg = (PNRItinAirSegment )segs[i];
          sOutStr.append( setWidth(airseg.Status,4) );
          }
        else if ( segs[i] instanceof PNRItinArunkSegment )
          sOutStr.append("ARNK");
        else
          sOutStr.append("????");
        }
      }

    return( sOutStr.toString() );
    } // end reqChangePnrItin


  /**
   ***********************************************************************
   * This function returns the header for a profile
   ***********************************************************************
   */
  private static String getSectionType(final Object aElement)
    {
    if ( aElement instanceof PNRNameElement )
      return("NAME");

    if ( aElement instanceof PNRSsrRemark )
      return("SSR");

    if ( aElement instanceof PNROsiRemark )
      return("OSI");

    if ( aElement instanceof PNRItinRemark )
      return("ITINERARY");

    if ( aElement instanceof PNRInvoiceRemark )
      return("INVOICE");

    if ( aElement instanceof PNRPocketItinRemark )
      return("PKT_ITIN");

    if ( aElement instanceof PNRFopRemark )
      return("FOP");

    if ( aElement instanceof PNRTicketRemark )
      return("TICKET");

    if ( aElement instanceof PNRReceiveByRemark )
      return("RECEIVE_BY");

    if ( aElement instanceof PNRAddressRemark )
      {
      final PNRAddressRemark address = (PNRAddressRemark )aElement;
      if ( address.type == address.HOME_ADDRESS )
        return("HM_ADDRESS");
      else if ( address.type == address.WORK_ADDRESS )
        return("WK_ADDRESS");
      else if ( address.type == address.BILLING_ADDRESS )
        return("BL_ADDRESS");
      else if ( address.type == address.DELIVERY_ADDRESS )
        return("DL_ADDRESS");
      else if ( address.type == address.AGENCY_ADDRESS )
        return("AG_ADDRESS");
      else
        return("ADDRESS");
      }

    if ( aElement instanceof PNRPhoneRemark )
      {
      final PNRPhoneRemark phone = (PNRPhoneRemark )aElement;
      if ( phone.type == phone.HOME_TYPE )
        return("HM_PHONE");
      else if ( phone.type == phone.WORK_TYPE )
        return("WK_PHONE");
      else if ( phone.type == phone.AGENCY_TYPE )
        return("AG_PHONE");
      else if ( phone.type == phone.FAX_TYPE )
        return("FAX_PHONE");
      else if ( phone.type == phone.CELL_TYPE )
        return("CELL_PHONE");
      else if ( phone.type == phone.PAGER_TYPE )
        return("PGR_PHONE");
      else
        return("PHONE");
      }

    if ( aElement instanceof PNRRemark )
      return("GENERAL");

    if ( aElement instanceof String )
      return("TEXT");

    return("???");
    }

  /** 
   ***********************************************************************
   * This function returns the appropriate response for a build block request
   ***********************************************************************
   */
  private static String reqBlockBuild(final ReqBlockBuild aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();

    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    sOutStr.append( setWidth(GnrcConvControl.ADD_BLK_RESP,8) +
                               StatusFormat.format(GnrcConvControl.STATUS_OK) );

    String sLocator;
    for ( int i = 0; i < 10; i++ )
      {
      if ( i < aRequest.getBlock().getNumFlights() )
        sLocator = aRequest.getBlock().getFlight(i).getBlockLocator();
      else
        sLocator = "";

      sOutStr.append( setWidth(sLocator,8) );
      }

    return( sOutStr.toString() );
    }


  /** 
   ***********************************************************************
   * This function returns the appropriate response for a retrieve block request
   ***********************************************************************
   */
  private static String reqBlockRetrieve(final ReqBlockRetrieve aRequest)
    {
    final StringBuffer sOutStr = new StringBuffer();

    final DecimalFormat StatusFormat = new DecimalFormat("00000");

    final String sActive;
    if ( aRequest.getBlock().isActive())
      sActive = "A";
    else
      sActive = "P";

    // basic block info
    sOutStr.append( setWidth(GnrcConvControl.GET_BLK_RESP,8));
    sOutStr.append( StatusFormat.format(GnrcConvControl.STATUS_OK));
    sOutStr.append( setWidth(aRequest.getCrsCode(),2));
    sOutStr.append( setWidth(aRequest.getBlock().getPseudoCityCode(),10));
    sOutStr.append( GnrcFormat.FormatLongDate( aRequest.getBlock().getStartSellDate() ));
    sOutStr.append( GnrcFormat.FormatLongDate( aRequest.getBlock().getStopSellDate() ));
    //sOutStr.append( setWidth(aRequest.getBlock().getMemo(),40));
    sOutStr.append( setWidth(aRequest.getBlock().getTourReference(),40));
    sOutStr.append( setWidth(sActive,1) );


    // block segments
    BlockFlight seg;
    final DecimalFormat FlightFormat    = new DecimalFormat("00000");
    final DecimalFormat SeatCountFormat = new DecimalFormat("000");

    for ( int i = 0; i < 10; i++ )
      {
      if ( i < aRequest.getBlock().getNumFlights() )
        {
        seg = aRequest.getBlock().getFlight(i);

        sOutStr.append( setWidth(seg.getFlightInfo().getCarrier(),3));
        sOutStr.append( FlightFormat.format( seg.getFlightInfo().getFlightNum() ));
        sOutStr.append( setWidth(seg.getFlightInfo().getDepCity(),3));
        sOutStr.append( setWidth(seg.getFlightInfo().getArrCity(),3));
        sOutStr.append( GnrcFormat.FormatLongDateTime( seg.getFlightInfo().getDepSchedDate() ));
        sOutStr.append( GnrcFormat.FormatLongDateTime( seg.getFlightInfo().getArrSchedDate() ));
        sOutStr.append( setWidth(seg.getClassOfService(),2));
        sOutStr.append( setWidth(seg.getActionCode(),4));
        sOutStr.append( SeatCountFormat.format( seg.getNumAllocated() ));
        sOutStr.append( SeatCountFormat.format( seg.getNumSold() ));
        sOutStr.append( setWidth(seg.getAllotmentStatus(),1));
        sOutStr.append( setWidth(seg.getBlockLocator(),8));
        sOutStr.append( setWidth(seg.getCarrierLocator(),8) );
        }
      else
        sOutStr.append( setWidth("",66) );
      }

    return( sOutStr.toString() );
    }


  /** 
   ***********************************************************************
   * This function formats a Block Message response
   ***********************************************************************
   */
  private static String reqBlockReadMessage(final ReqBlockReadMessage aRequest)
    {
    final StringBuffer  sOutStr      = new StringBuffer();
    final DecimalFormat statusFormat = new DecimalFormat("00000");
    final DecimalFormat numFormat    = new DecimalFormat("000");

    int iMsgNum = aRequest.blockMessageList.size();

    // basic block message info
    sOutStr.append(setWidth(GnrcConvControl.READ_BLK_MSG_RESP,8));
    sOutStr.append(statusFormat.format(GnrcConvControl.STATUS_OK));
    sOutStr.append(setWidth(aRequest.getCrsCode(),2));
    sOutStr.append(numFormat.format(iMsgNum));         

    formatBlockMessageNego(aRequest.blockMessageList, sOutStr);

    return sOutStr.toString();
    }

  
  /**
   ***********************************************************************
   * Given a list of {@link BlockMessage} objects this method formats a
   * response and stores it in a the passed StringBuffer
   ***********************************************************************
   */
  public static void formatBlockMessageNego(
      List blockMessageList, StringBuffer sOutStr)
    {
    final DecimalFormat flightFormat = new DecimalFormat("0000");
    final DecimalFormat numFormat    = new DecimalFormat("000");

    for (int i=0; i < blockMessageList.size(); i++)
      {
      BlockMessage blockMsg    = (BlockMessage)blockMessageList.get(i);
      Block        block       = blockMsg.getBlock();
      BlockFlight  blockFlight = block.getFlight(0);
      FlightInfo   flightInfo  = blockFlight.getFlightInfo();

      sOutStr.append(setWidth(blockMsg.getAction(),20));
      sOutStr.append(setWidth(flightInfo.getCarrier(),2));
      sOutStr.append(flightFormat.format(flightInfo.getFlightNum()));
      sOutStr.append(setWidth("",1)); // flight suffix ???
      sOutStr.append(DateTime.fmtLongDateTime(block.getStartSellDate(), "yyyyMMdd" ));
      sOutStr.append(setWidth(block.getPseudoCityCode(),10));
      sOutStr.append(numFormat.format(blockFlight.getNumAllocated()));
      sOutStr.append(numFormat.format(blockFlight.getNumPending()));
      sOutStr.append(DateTime.fmtLongDateTime(block.getReductionDate(), "yyyyMMdd" ));
      sOutStr.append(numFormat.format(block.getReductionPercent()));
      sOutStr.append(numFormat.format(blockFlight.getNumSold()));
      sOutStr.append(setWidth(flightInfo.getDepCity(),3));
      sOutStr.append(setWidth(flightInfo.getArrCity(),3));
      sOutStr.append(DateTime.fmtLongDateTime(block.getStopSellDate(), "yyyyMMdd" ));
      sOutStr.append(numFormat.format(blockFlight.getNumUnsold()));
      sOutStr.append(numFormat.format(blockFlight.getNumPendingUnsold()));
      sOutStr.append(setWidth(blockFlight.getClassOfService(),1));
      sOutStr.append(setWidth(blockFlight.getCarrierLocator(),8));
      sOutStr.append(setWidth(blockFlight.getCrsFlightId(),4));
      sOutStr.append(setWidth(blockFlight.getBlockLocator(),6));
      // sOutStr.append(setWidth(block.getMemo(),15));
      sOutStr.append(setWidth(block.getTourName(),15));
      // sOutStr.append(setWidth(block.crsProperty.getProperty("AuthCode"),6));
      sOutStr.append(setWidth(block.getAuthCode(),6));
      sOutStr.append(setWidth(block.crsProperty.getProperty("SellType"),1));
      sOutStr.append(setWidth(block.crsProperty.getProperty("SellMethod"),7));
      // sOutStr.append(setWidth(block.crsProperty.getProperty("TourRef"),40));
      sOutStr.append(setWidth(block.getTourReference(),40));

      sOutStr.append(setWidth(block.crsProperty.getProperty("Event"),20));
      sOutStr.append(setWidth(block.crsProperty.getProperty("Status"),1));
      sOutStr.append(setWidth(block.crsProperty.getProperty("StatusText"),24));

      
      sOutStr.append(DateTime.fmtLongDateTime( flightInfo.getDepSchedDate(), "yyyyMMddHHmm" ));
      sOutStr.append(DateTime.fmtLongDateTime( flightInfo.getArrSchedDate(), "yyyyMMddHHmm" ));

      final int MAX_LINKED_BLOCKS = 5;
      for (int j=0; j < MAX_LINKED_BLOCKS; j++)
        {
        sOutStr.append(setWidth("",2)); // carrier code
        sOutStr.append(setWidth("",4)); // flight number
        sOutStr.append(setWidth("",1)); // flight suffix ???
        sOutStr.append(setWidth("",1)); // class Of Service
        sOutStr.append(setWidth("",6)); // city pair
        sOutStr.append(setWidth("",6)); // block locator

        // the first linked flight contains the following info
        if (j == 1) 
          {
          sOutStr.append(setWidth("",1)); // link type
          sOutStr.append(setWidth("",4)); // link Id
          }
        }

      } // end for
    } // end formatBlockMessageNego

  /**
   ***********************************************************************
   * shortcut for GnrcFormat.SetWidth static method
   ***********************************************************************
   */
  private static String setWidth(final String aInString, final int aWidth)
    {
    return( GnrcFormat.SetWidth(aInString,aWidth) );
    }

}
