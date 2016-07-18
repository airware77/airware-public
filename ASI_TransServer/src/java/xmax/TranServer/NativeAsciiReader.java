package xmax.TranServer;

import xmax.TranServer.GnrcConvControl;
import xmax.crs.GnrcParser;
import xmax.crs.GnrcCrs;
import xmax.crs.GetPNR.*;
import xmax.crs.profiles.*;
import xmax.crs.Flifo.FlightSegment;
import xmax.crs.Block;
import xmax.crs.BlockFlight;
import xmax.util.RegExpMatch;

import java.util.Vector;

/**
 ***********************************************************************
 * The majority of the methods in this class accept a string request
 * in native Airware command and return the appropriate request object;
 * In other words, this class is used to 'transform' a string request
 * into a request object.
 *
 * <p> This class is mostly used by calling its getRequestObject method.
 * This method accepts a request in string format, parses the request
 * and, based on the type of request, calls the appropriate method within
 * the class.  The method called, in turn, returns a request object
 * corresponding to the string request.
 *
 * @author	David Fairchild
 * @version     1.x Copyright (c) 1999
 * @see	        #getRequestObject
 ***********************************************************************
 */

public class NativeAsciiReader
{

  /** the regular expression that matches exactly a three letter string */
  private final static String VALID_AIRPORT_CODE = "[A-Z]{3}";

  /**
   ***********************************************************************
   * returns the default CRS code
   ***********************************************************************
   */
  private static String getDefaultCrsCode(final GnrcCrs aCrs)
    {
    // determine if you are currently connected, if so, use that as the default
    if ( aCrs instanceof GnrcCrs )
      return( aCrs.getHostCode() );

    // look up the default CRS code from the configuration
    //final String sDefaultHost = ConfigInformation.getParamValue(ConfigTranServer.DEFAULT_HOST,"AA");
    final String sDefaultHost =
      ConfigTranServer.application.getProperty("defaultHostCode","AA");

    return( sDefaultHost );
    }

  /**
   ***********************************************************************
   * This method reads a string request and calls the appropriate method,
   * depending on the type of request specified in the first 8 characters
	 * of the string;  in turn, the method thus called
   * returns a request object that corresponds to the string request.
   *
	 * The string is 520 characters long and has a fixed-width format.
	 * The meaning of the characters in the string varies according to
	 * the type of request.  The type of request (the 'command' portion)
	 * is always contained within the first 8 characters of the string.
	 * A list of the valid commands and their explanation can be found at
	 * {@link xmax.crs.Generic.GnrcConvControl}
	 *
   * @param aStringInput   The client request passed in string form
   * @param aCrs           A computer reservation system
	 *
   * @return               A request object via a call to one of the
   *                       methods in this class
	 *
	 * @see xmax.crs.Generic.GnrcConvControl
   ***********************************************************************
   */

  public static ReqTranServer getRequestObject(
      final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);

    if ( sOperation.equals(GnrcConvControl.GET_QUEUE_PNR_CMD) )
      return( reqGetQueuePNR(aInputString) );

    if ( sOperation.equals(GnrcConvControl.GET_PNR_CMD))
      return( reqGetPNR(aInputString) );

    if ( sOperation.equals(GnrcConvControl.AWR_GET_PNR_CMD) ||
         sOperation.equals(GnrcConvControl.AWR_GET_PNR2_CMD))
      return( reqGetAirwarePNR(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_STATUS_CMD) )
      return( reqGetStatus(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_HOTEL_INFO_CMD) )
      return( reqGetHotelInfo(aInputString) );

    if ( sOperation.equals(GnrcConvControl.GET_FLIGHT_INFO_CMD) )
      return( reqGetFlightInfo(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_FLIFO_CMD) )
      return( reqGetAirwareFlightInfo(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_AVAIL_CMD) )
      return( reqGetAvailInfo(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_GET_AVAIL_CMD) )
      return( reqGetAirwareAvailInfo(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_CONN_TM_CMD) )
      return( reqGetConnectTimes(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_AIRPORT_TM_CMD) )
      return( reqGetConnectTimesAirport(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_IGNORE_CMD) )
      return( reqIgnore(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_STARTSES_CMD) )
      return( reqSessionStart(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ENDSES_CMD) )
      return( reqSessionEnd(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_GRP_HDR_CMD) )
      return( reqAddCorpHeader(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_NAME_CMD) )
      return( reqAddNames(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_CHG_NAME_CMD) )
      return( reqAirwareChangeName(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.REPL_NAME_CMD) )
      return( reqReplaceName(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.CHG_PNR_ITIN_CMD) )
      return( reqChangePnrItin(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_AIRSEG_CMD) )
      return( reqAddAirSeg(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_REMARK_CMD) )
      return( reqAddRemark(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_CXL_REMARK_CMD) )
      return( reqCxlRemarks(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_PHONE_CMD) )
      return( reqAddPhone(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_ENDORSE_CMD) )
      return( reqAddEndorsement(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_TOURCODE_CMD) )
      return( reqAddTourCode(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_COMM_CMD) )
      return( reqAddCommission(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_FOP_CMD) )
      return( reqAddFOP(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_TICKET_CMD) )
      return( reqAddTicket(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_RCV_PNR_CMD) )
      return( reqAddReceiveBy(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_END_XACT_CMD) )
      return( reqEndTransaction(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_ACCEPT_CHG_CMD) )
      return( reqAcceptSchedChange(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_FARE_CMD) )
      return( reqAirwareFarePNR(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_FARE_CMD) )
      return( reqFarePNR(aInputString) );

    if ( sOperation.equals(GnrcConvControl.AWR_TKT_PNR_CMD) )
      return( reqIssueTicket(aInputString) );

    if ( sOperation.equals(GnrcConvControl.AWR_TKT_INFO_CMD) )
        return( reqGetTicketInfo(aInputString) );
    
    if ( sOperation.equals(GnrcConvControl.AWR_QUEUE_PNR_CMD) )
      return( reqQueuePNR(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.AWR_FREEFORM_CMD) )
      return( reqFreeForm(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.FREEFORM_CMD) )
      return( reqFreeForm(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.SET_PRN_CMD) )
      return( reqAssignPrinter(aInputString) );

    if ( sOperation.equals(GnrcConvControl.SPLIT_PNR_CMD) )
      return( reqSplitPnr(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.CXL_SEG_CMD) )
      return( reqCxlAirSegment(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.CXL_ITIN_CMD) )
      return( reqCxlItinerary(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.ADD_RMK_CMD) )
      return( reqAddRemarks(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.MOD_RMK_CMD) )
      return( reqModRemarks(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.DEL_RMK_CMD) )
      return( reqDelRemarks(aInputString, aCrs) );

    if ( sOperation.equals(GnrcConvControl.START_LOG_CMD) )
      return( reqEnableLogForwarding(aInputString) );

    if ( sOperation.equals(GnrcConvControl.END_LOG_CMD) )
      return( reqDisableLogForwarding(aInputString) );

    if ( sOperation.equals(GnrcConvControl.LIST_BRANCH_CMD) )
      return( reqListBranches(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.LIST_GROUP_PROF_CMD) )
      return( reqListGroupProfiles(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.LIST_PER_PROF_CMD) )
      return( reqListPersonalProfiles(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_PER_PROF_CMD) )
      return( reqGetProfile(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.SET_PER_PROF_CMD) )
      return( reqBuildProfile(aInputString,aCrs) );

    if ( sOperation.equals(GnrcConvControl.GET_BLK_CMD) )
      return( reqBlockRetrieve(aInputString) );

    if ( sOperation.equals(GnrcConvControl.ADD_BLK_CMD) )
      return( reqBlockBuild(aInputString) );

		if ( sOperation.equals(GnrcConvControl.ADD_BLK3_CMD) )
      return( reqBlockBuild3(aInputString) );

    if ( sOperation.equals(GnrcConvControl.MOD_BLK_CMD) )
      return( reqBlockModify(aInputString) );

    if ( sOperation.equals(GnrcConvControl.DEL_BLK_CMD) )
      return( reqBlockDelete(aInputString) );

    if ( sOperation.equals(GnrcConvControl.READ_BLK_MSG_CMD) )
      return( reqBlockReadMessage(aInputString) );


    throw new TranServerException("Command unrecognized");
    }


  /**
   ***********************************************************************
   * Get queued PNR
   ***********************************************************************
   */
  private static ReqGetPNR reqGetQueuePNR(final String aInputString) throws Exception
    {
    // read fields
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim();
    final String sQueueName = GnrcParser.getSubstring(aInputString,10,20).trim();
    final String sCatagory  = GnrcParser.getSubstring(aInputString,20,30).trim();
    final String sDateRange = GnrcParser.getSubstring(aInputString,30,40).trim();
    final String sExtended  = GnrcParser.getSubstring(aInputString,40,41).trim();

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_QUEUE_PNR_CMD) == false )
      throw new TranServerException("Invalid operation code for get queued PNR, received: " + sOperation + "  expected: " + GnrcConvControl.GET_QUEUE_PNR_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to pull PNR from");

    if ( GnrcFormat.IsNull(sQueueName) )
      throw new TranServerException("Must specify a queue to pull PNR from");

    // create request object
    final ReqGetPNR request = new ReqGetPNR(sCrsCode);

    request.commType  = request.COMM_NATIVE_ASCII;
    request.QueueName = sQueueName;
    if ( sExtended.equals("T") )
      request.ExtendedInfo = true;

    return(request);
    }

  /**
   ***********************************************************************
   * generated by {@link GnrcConvControl#GET_PNR_CMD}, this method returns
   * an object request to get a PNR by locator; note that a valid PNR must
   * exist in order to obtain a result.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as follows:
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   7      8     - the string 'CGETPNR'
   *    8 -   9      2     - a code denoting the CRS codes:
	 *                         1A=Amadeus  AA=Sabre  UA=Apollo/Galileo  1P=Worldspan
   *   10 -  17      8     - a Passenger Name Record (PNR) locator
   *   18 -  18      1     - a 'T' or 'F' flag (true or false) to denote whether
   *                         extended information should be retrieved for the PNR
   * </pre>
   ***********************************************************************
   */
  private static ReqGetPNR reqGetPNR(final String aInputString) throws Exception
    {
    // read fields
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim();
    final String sExtended  = GnrcParser.getSubstring(aInputString,18,19).trim();

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_PNR_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for get PNR, received: " + sOperation +
          "  expected: " + GnrcConvControl.GET_PNR_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to pull PNR from");

    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator for get PNR");
    */

    // create request object
    final ReqGetPNR request = new ReqGetPNR(sCrsCode);

    request.commType = request.COMM_NATIVE_ASCII;
    request.Locator  = sLocator;
    if ( sExtended.equals("T") )
      request.ExtendedInfo = true;

    return(request);
    }

  /**
   ***********************************************************************
   * Get PNR by locator for Airware
   ***********************************************************************
   */
  private static ReqGetPNR reqGetAirwarePNR(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // read fields
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim();
    final String sQueue       = GnrcParser.getSubstring(aInputString,16,26).trim();
    final String sNoRemarks   = GnrcParser.getSubstring(aInputString,26,27).trim();
    final String sQueueAction = GnrcParser.getSubstring(aInputString,27,28).trim();

    // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_GET_PNR_CMD) == false &&
         sOperation.equals(GnrcConvControl.AWR_GET_PNR2_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for get PNR, received: " + sOperation +
          "  expected: " + GnrcConvControl.AWR_GET_PNR_CMD );

    final String sCrsCode = getDefaultCrsCode(aCrs);

    // create request object
    final ReqGetPNR request = new ReqGetPNR(sCrsCode);

    if ( sOperation.equals(GnrcConvControl.AWR_GET_PNR2_CMD))
      request.isPNR2 = true;

    request.commType = request.COMM_NATIVE_AIRWARE;
    request.Locator  = sLocator;
    request.QueueName = sQueue;

    if (sNoRemarks.equals("T"))
      request.ReturnRemarks = false;

    if (sQueueAction.equals("L"))
      request.RemoveFromQueue = false;
    else
      request.RemoveFromQueue = true;

    return(request);
    }

  /**
   ***********************************************************************
   * Get Status
   ***********************************************************************
   */
  private static ReqGetStatus reqGetStatus(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // read fields
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
          String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10);

    if ( GnrcFormat.IsNull(sCrsCode) )
      sCrsCode = getDefaultCrsCode(aCrs);

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_STATUS_CMD) == false )
      throw new TranServerException("Invalid operation code for get status, received: " + sOperation + "  expected: " + GnrcConvControl.GET_STATUS_CMD );

    // create request object
    final ReqGetStatus request = new ReqGetStatus(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;

    return(request);
    }

  /**
   ***********************************************************************
   * Get Hotel Information
   ***********************************************************************
   */
  private static ReqGetHotelInfo reqGetHotelInfo(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode      = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sChainCode    = GnrcParser.getSubstring(aInputString,10,12).trim().toUpperCase();
    final String sPropertyCode = GnrcParser.getSubstring(aInputString,12,22).trim().toUpperCase();
    final String sInfoType     = GnrcParser.getSubstring(aInputString,22,32).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_HOTEL_INFO_CMD) == false )
      throw new TranServerException("Invalid operation code for get hotel info, received: " + sOperation + "  expected: " + GnrcConvControl.GET_HOTEL_INFO_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a Crs code to get hotel info");

    if ( GnrcFormat.IsNull(sChainCode) )
      throw new TranServerException("Must specify a chain code to get hotel info");

    if ( GnrcFormat.IsNull(sPropertyCode) )
      throw new TranServerException("Must specify a property code to get hotel info");

    // create request object
    final ReqGetHotelInfo request = new ReqGetHotelInfo(sCrsCode,sChainCode,sPropertyCode);
    request.commType = request.COMM_NATIVE_ASCII;
    return(request);
    }

  /**
   ***********************************************************************
   * Get Flight Information
   ***********************************************************************
   */
  private static ReqGetFlifo reqGetFlightInfo(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCarrier   = GnrcParser.getSubstring(aInputString,8,11).trim().toUpperCase();
    final String sFlight    = GnrcParser.getSubstring(aInputString,11,16).trim().toUpperCase();
    final String sDepCity   = GnrcParser.getSubstring(aInputString,16,19).trim().toUpperCase();
    final String sArrCity   = GnrcParser.getSubstring(aInputString,19,22).trim().toUpperCase();
    final String sDepDate   = GnrcParser.getSubstring(aInputString,22,30).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_FLIGHT_INFO_CMD) == false )
      throw new TranServerException("Invalid operation code for get flight info, received: " + sOperation + "  expected: " + GnrcConvControl.GET_FLIGHT_INFO_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a Crs code to get flight info");

    if ( GnrcFormat.IsNull(sCarrier) )
      throw new TranServerException("Must specify a carrier code to get flight info");

    if ( GnrcFormat.IsNull(sFlight) )
      throw new TranServerException("Must specify a flight number to get flight info");

    // convert date string from YYYY/MM/DD format to ddMMM format
    final String sCrsDepDate;
    try
      {
      sCrsDepDate = GnrcFormat.ConvertLongToCrsDate(sDepDate);
      }
    catch (Exception e)
      {
      throw new FlifoException("Flifo error: unable to scan date string " + sDepDate);
      }

    // convert flight number
    final int iFlightNum;
    try
      {
      iFlightNum = Integer.parseInt(sFlight);
      }
    catch (Exception e)
      {
      throw new FlifoException("Flifo error: unable to scan flight number " + sFlight);
      }

    if ( GnrcFormat.IsNull(sCrsDepDate) )
      throw new TranServerException("Must specify a departure date to get flight info");

    System.out.println("NativeAsciiReader.ReqGetFlifo: sCarriers/iFlightNum/sCrsDepDate/sDepCity/sArrCity: "
      + sCarrier + "/" + iFlightNum + "/" + sCrsDepDate + "/" + sDepCity + "/" + sArrCity);

    // create request object
    final ReqGetFlifo request = new ReqGetFlifo(sCrsCode,sCarrier,iFlightNum,sCrsDepDate);
    request.commType = request.COMM_NATIVE_ASCII;

    if ( GnrcFormat.NotNull(sDepCity) )
      request.DepCity = sDepCity;

    if ( GnrcFormat.NotNull(sArrCity) )
      request.ArrCity = sArrCity;

    return(request);
    }

  /**
   ***********************************************************************
   * generated by {@link GnrcConvControl#AWR_FLIGHT_INFO}, this method returns
   * an object request to retrieve flight information.
   *
   * @param aInputString
   *  a fixed-width 30 char string request, formatted as follows:
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   7      8     - the string 'FLIFO   '
   *    8 -  10      3     - a code denoting the Airline Carrier
   *   11 -  15      5     - a Flight Number
   *   16 -  22      7     - the Departure Date in cyyddmm format
   *   23 -  25      3     - the Departure City's 3 char code
   *   26 -  28      3     - the Arrival City's 3 char code
   *   29 -  29      1     - DayOf: currently not being used
   * </pre>
   ***********************************************************************
   */
  private static ReqGetFlifo reqGetAirwareFlightInfo(final String aInputString,
                                                     final GnrcCrs aCrs)
                                                        throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCarrier   = GnrcParser.getSubstring(aInputString,8,11).trim().toUpperCase();
    final String sFlight    = GnrcParser.getSubstring(aInputString,11,16).trim().toUpperCase();
    final String sDepDate   = GnrcParser.getSubstring(aInputString,16,23).trim().toUpperCase();
    final String sDepCity   = GnrcParser.getSubstring(aInputString,23,26).trim().toUpperCase();
    final String sArrCity   = GnrcParser.getSubstring(aInputString,26,29).trim().toUpperCase();
    final String sDayOf     = GnrcParser.getSubstring(aInputString,29,30).trim().toUpperCase();


    final String sCrsCode = getDefaultCrsCode(aCrs);


    // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_FLIFO_CMD) == false )
      throw new TranServerException("Invalid operation code for get flight info, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_FLIFO_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a Crs code to get flight info");

    if ( GnrcFormat.IsNull(sCarrier) )
      throw new TranServerException("Must specify a carrier code to get flight info");

    if ( GnrcFormat.IsNull(sFlight) )
      throw new TranServerException("Must specify a flight number to get flight info");

    // convert date string from CYY/MM/DD format to ddMMM format
    final String sCrsDepDate;
    try
      {
      final long iDepDate = GnrcParser.ScanAirwareDate(sDepDate);
      sCrsDepDate = GnrcFormat.FormatCRSDate(iDepDate);
      }
    catch (Exception e)
      {
      throw new FlifoException("Flifo error: unable to scan date string " + sDepDate);
      }

    // convert flight number
    final int iFlightNum;
    try
      {
      iFlightNum = Integer.parseInt(sFlight);
      }
    catch (Exception e)
      {
      throw new FlifoException("Flifo error: unable to scan flight number " + sFlight);
      }

    if ( GnrcFormat.IsNull(sCrsDepDate) )
      throw new TranServerException("Must specify a departure date to get flight info");


    // create request object
    final ReqGetFlifo request =
      new ReqGetFlifo(sCrsCode,sCarrier,iFlightNum,sCrsDepDate);

    request.commType = request.COMM_NATIVE_AIRWARE;

    if ( GnrcFormat.NotNull(sDepCity) )
      request.DepCity = sDepCity;

    if ( GnrcFormat.NotNull(sArrCity) )
      request.ArrCity = sArrCity;

    return(request);
    }

  /**
   ***********************************************************************
   * Get availability
   ***********************************************************************
   */
  private static ReqGetAvail reqGetAvailInfo(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the input parameters
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sDepCity   = GnrcParser.getSubstring(aInputString,8,11).trim().toUpperCase();
    final String sArrCity   = GnrcParser.getSubstring(aInputString,11,14).trim().toUpperCase();
    final String sDepDate   = GnrcParser.getSubstring(aInputString,14,22).trim().toUpperCase();
    final String sDepTime   = GnrcParser.getSubstring(aInputString,22,26).trim().toUpperCase();
    final String sArrDate   = GnrcParser.getSubstring(aInputString,26,34).trim().toUpperCase();
    final String sArrTime   = GnrcParser.getSubstring(aInputString,34,38).trim().toUpperCase();
    final String sCarrier   = GnrcParser.getSubstring(aInputString,38,41).trim().toUpperCase();
    final String sFlight    = GnrcParser.getSubstring(aInputString,41,46).trim().toUpperCase();
    final String sQuality   = GnrcParser.getSubstring(aInputString,46,47).trim().toUpperCase();
    final String sNumItins  = GnrcParser.getSubstring(aInputString,47,50).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);


    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_AVAIL_CMD) == false )
      throw new TranServerException("Invalid operation code for get availability info, received: " + sOperation + "  expected: " + GnrcConvControl.GET_AVAIL_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a Crs code to get availability info");

    if ( GnrcFormat.IsNull(sDepCity) )
      throw new TranServerException("Must specify a departure city to get availability info");

    if ( GnrcFormat.IsNull(sArrCity) )
      throw new TranServerException("Must specify an arrival city to get availability info");

    if ( GnrcFormat.IsNull(sDepDate) )
      throw new TranServerException("Must specify a departure date to get availability info");


    // convert the date strings
    final int iFlightNum;
    try
      {
      if ( GnrcFormat.NotNull(sFlight) )
        iFlightNum = Integer.parseInt(sFlight);
      else
        iFlightNum = 0;
      }
    catch (Exception e)
      {
      throw new TranServerException("Unable to scan in flight number " + sFlight);
      }

    // convert the date strings
    final String sCrsDepDate;
    try
      {
      sCrsDepDate = GnrcFormat.ConvertLongToCrsDate(sDepDate);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid departure date format: " + sDepDate);
      }


    String sCrsArrDate = "";
    try
      {
      sCrsArrDate = GnrcFormat.ConvertLongToCrsDate(sArrDate);
      }
    catch (Exception e)
      {
      sCrsArrDate = "";
      }


    // create availability request object
    final ReqGetAvail request = new ReqGetAvail(sCrsCode,sDepCity,sArrCity,sCrsDepDate);
    request.commType = request.COMM_NATIVE_ASCII;

    if ( GnrcFormat.NotNull(sDepTime) )
      request.DepTime = sDepTime;

    if ( GnrcFormat.NotNull(sCrsArrDate) )
      request.ArrDate = sCrsArrDate;

    if ( GnrcFormat.NotNull(sArrTime) )
      request.ArrTime = sArrTime;

    if ( GnrcFormat.NotNull(sCarrier) )
      request.Carrier = sCarrier;

    request.FlightNum = iFlightNum;

    if ( GnrcFormat.NotNull(sNumItins) )
      request.NumItins = Integer.parseInt(sNumItins);
    else
      request.NumItins = 10;

    if ( sQuality.equals("N") )
      request.ItinQuality = request.ITIN_NONSTOP;
    else if ( sQuality.equals("D") )
      request.ItinQuality = request.ITIN_DIRECT;

    return(request);
    }

  /**
   ***********************************************************************
   * generated by {@link xmax.crs.Generic.GnrcConvControl#AWR_GET_AVAIL_CMD},
   * corresponds to Airware's verb <code>RequestAirAvail</code>;
   * this method returns a request to retrieve availability for a flight.
   *
   * @see crs_defn_s.pls
   ***********************************************************************
   */
  private static ReqGetAvail reqGetAirwareAvailInfo(final String aInputString,
                                                    final GnrcCrs aCrs)
                                                    throws Exception
    {
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sDepDate   = GnrcParser.getSubstring(aInputString,8,15).trim().toUpperCase();
    final String sDepCity   = GnrcParser.getSubstring(aInputString,15,18).trim().toUpperCase();
    final String sArrCity   = GnrcParser.getSubstring(aInputString,18,21).trim().toUpperCase();
    final String sDepTime   = GnrcParser.getSubstring(aInputString,21,25).trim().toUpperCase();
    final String sArrTime   = GnrcParser.getSubstring(aInputString,27,31).trim().toUpperCase();
    final String sArrDate   = GnrcParser.getSubstring(aInputString,33,40).trim().toUpperCase();
    final String sInvCode   = GnrcParser.getSubstring(aInputString,40,42).trim().toUpperCase();
    final String sCarrier   = GnrcParser.getSubstring(aInputString,42,45).trim().toUpperCase();
    final String sFlight    = GnrcParser.getSubstring(aInputString,45,50).trim().toUpperCase();
    final String sNumItins  = GnrcParser.getSubstring(aInputString,50,53).trim().toUpperCase();
    final String sByClass   = GnrcParser.getSubstring(aInputString,53,54).trim().toUpperCase();
    final String sAvailType = GnrcParser.getSubstring(aInputString,54,55).trim().toUpperCase();
    final String sQuality   = GnrcParser.getSubstring(aInputString,55,56).trim().toUpperCase();
    final String sPrivClass = GnrcParser.getSubstring(aInputString,56,58).trim().toUpperCase();


    final String sCrsDepDate;
    try
      {
      final long iDepDate = GnrcParser.ScanAirwareDate(sDepDate);
      sCrsDepDate         = GnrcFormat.FormatCRSDate(iDepDate);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid departure date format: " + sDepDate);
      }

    String sCrsArrDate = "";
    try
      {
      final long iArrDate = GnrcParser.ScanAirwareDate(sArrDate);
      sCrsArrDate         = GnrcFormat.FormatCRSDate(iArrDate);
      }
    catch (Exception e)
      {
      // throw new TranServerException("Invalid arrival date format: " + sArrDate);
      sCrsArrDate = "";
      }


    final String sCrsCode = getDefaultCrsCode(aCrs);


    // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_GET_AVAIL_CMD) == false )
      throw new TranServerException("Invalid operation code for get availability info, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_GET_AVAIL_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a Crs code to get availability info");

    if ( GnrcFormat.IsNull(sDepCity) )
      throw new TranServerException("Must specify a departure city to get availability info");

    if ( GnrcFormat.IsNull(sArrCity) )
      throw new TranServerException("Must specify an arrival city to get availability info");

    if ( GnrcFormat.IsNull(sDepDate) )
      throw new TranServerException("Must specify a departure date to get availability info");


    // create availability request object
    final ReqGetAvail request =
      new ReqGetAvail(sCrsCode,sDepCity,sArrCity,sCrsDepDate);
    request.commType = request.COMM_NATIVE_AIRWARE;

    if ( GnrcFormat.NotNull(sDepTime) )
      request.DepTime = sDepTime;

    if ( GnrcFormat.NotNull(sCrsArrDate) )
      request.ArrDate = sCrsArrDate;

    if ( GnrcFormat.NotNull(sArrTime) )
      request.ArrTime = sArrTime;

    if ( GnrcFormat.NotNull(sCarrier) )
      request.Carrier = sCarrier;

    if ( GnrcFormat.NotNull(sAvailType) )
      request.AvailType = sAvailType;

    if ( GnrcFormat.NotNull(sFlight) )
      request.FlightNum = Integer.parseInt(sFlight);

    if ( GnrcFormat.NotNull(sNumItins) )
      request.NumItins = Integer.parseInt(sNumItins);
    else
      request.NumItins = 10;

    if ( sQuality.equals("Y") || sQuality.equals("T") )
      request.ItinQuality = request.ITIN_DIRECT;

    if ( GnrcFormat.NotNull(sPrivClass) )
      request.ClassOfService = sPrivClass;

    return(request);
    } // end reqGetAvail

  /**
   ***********************************************************************
   * Get legal connect times between two or more segments
   ***********************************************************************
   */
  private static ReqGetConnectTimes reqGetConnectTimes(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8).toUpperCase();
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_CONN_TM_CMD) == false )
      throw new TranServerException("Invalid operation code for get connect time info, received: " + sOperation + "  expected: " + GnrcConvControl.GET_CONN_TM_CMD );

    final ReqGetConnectTimes request = new ReqGetConnectTimes(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;


    // scan in the connections to be checked
    String sConnection;
    final int CONN_WIDTH = 27;
    FlightSegment seg1 = null;
    FlightSegment seg2 = null;
    String sDate;
    for ( int i = 10; i < aInputString.length(); i += CONN_WIDTH )
      {
      sConnection = GnrcParser.getSubstring(aInputString,i,i + CONN_WIDTH);

      seg1 = seg2;

      // scan inbound info
      seg2                    = new FlightSegment();
      seg2.Carrier            = GnrcParser.getSubstring(sConnection,0,2).trim().toUpperCase();
      final String sFlightNum = GnrcParser.getSubstring(sConnection,2,7).trim();
      sDate                   = GnrcParser.getSubstring(sConnection,7,15).trim();
      seg2.DepartCity         = GnrcParser.getSubstring(sConnection,15,18).trim().toUpperCase();
      seg2.ArriveCity         = GnrcParser.getSubstring(sConnection,18,21).trim().toUpperCase();
      seg2.DepartCountry      = GnrcParser.getSubstring(sConnection,21,24).trim().toUpperCase();
      seg2.ArriveCountry      = GnrcParser.getSubstring(sConnection,24,27).trim().toUpperCase();

      // scan the departure date
      try
        {
        seg2.DepSchedDateTime = GnrcFormat.ScanLongDate(sDate);
        }
      catch (Exception e)
        {
        throw new TranServerException("Invalid segment departure date format: " + sDate);
        }

      // scan the flight number
      try
        {
        seg2.FlightNum = Integer.parseInt(sFlightNum);
        }
      catch (Exception e)
        {
        throw new TranServerException("Invalid flight number " + sFlightNum);
        }

      if ( (seg1 instanceof FlightSegment) && (seg2 instanceof FlightSegment) )
        request.addConnectionQuery(seg1,seg2);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * Get legal connect times for one or more airports; for each airport, 4
   * different connect times will be returned: DomesticDomestic, DomesticIntl,
   * IntlDomestic, IntlIntl
   ***********************************************************************
   */
  private static ReqGetConnectTimesAirport reqGetConnectTimesAirport(
      final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    final int MIN_WIDTH = 13;
    if (aInputString.length() < MIN_WIDTH)
      throw new TranServerException(
          "The Request for Airport Connect Times that you provided " +
          "must contain at least " + MIN_WIDTH + " characters");

    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8).toUpperCase();
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_AIRPORT_TM_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for get airport connect times info, received: "
          + sOperation + "  expected: " + GnrcConvControl.GET_AIRPORT_TM_CMD );

    final ReqGetConnectTimesAirport request = new ReqGetConnectTimesAirport(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;

    // scan in the airports for which connection times have been requested
    String sAirportCode;
    int CODE_WIDTH = 3;
    for ( int i = 10; i < aInputString.length(); i += CODE_WIDTH )
      {
      sAirportCode = GnrcParser.getSubstring(aInputString,i,i + CODE_WIDTH).trim();
      if (RegExpMatch.matches(sAirportCode,VALID_AIRPORT_CODE) == false)
        throw new TranServerException(
            "The airport code: '" + sAirportCode + "' provided in your " +
            "Airport Connect Times request is invalid");
      request.addAirportConnectTimeQuery(sAirportCode);
      }

    return(request);
    } // end reqGetConnectTimesAirport


  /**
   ***********************************************************************
   * Ignore
   ***********************************************************************
   */
  private static ReqIgnore reqIgnore(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);

    final String sCrsCode = getDefaultCrsCode(aCrs);

    // check fields
    if ( (sOperation.equals(GnrcConvControl.AWR_IGNORE_CMD)   == false) )
      throw new TranServerException("Invalid operation code for Ignore, received: " + sOperation);

    final ReqIgnore request = new ReqIgnore(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;
    request.NativeAsciiRequest = sOperation;

    return(request);
    }

  /**
   ***********************************************************************
   * This method instantiates a ReqSessionStart object, used to perform
   * any initializations needed prior to starting a session with a Computer
   * Reservation System (CRS).
   ***********************************************************************
   */
  private static ReqSessionStart reqSessionStart(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);

    final String sCrsCode = getDefaultCrsCode(aCrs);

    // check fields
    if ( (sOperation.equals(GnrcConvControl.AWR_STARTSES_CMD) == false) )
      throw new TranServerException("Invalid operation code received for Start Session command: " + sOperation);

    final ReqSessionStart request = new ReqSessionStart(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;
    request.NativeAsciiRequest = sOperation;

    return(request);
    }

  /**
   ***********************************************************************
   * This method instantiates a ReqSessionEnd object, used to perform
   * any final cleanup operations after ending a session with a Computer
   * Reservation System (CRS).
   ***********************************************************************
   */
  private static ReqSessionEnd reqSessionEnd(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);

    final String sCrsCode = getDefaultCrsCode(aCrs);

    // check fields
    if ( (sOperation.equals(GnrcConvControl.AWR_ENDSES_CMD) == false) )
      throw new TranServerException("Invalid operation code received for End Session command: " + sOperation);

    final ReqSessionEnd request = new ReqSessionEnd(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;
    request.NativeAsciiRequest = sOperation;

    return(request);
    }

  /**
   ***********************************************************************
   * Add Corporate header
   ***********************************************************************
   */
  private static ReqAddCorpHeader reqAddCorpHeader(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sNumSeats  = GnrcParser.getSubstring(aInputString,8,11).trim().toUpperCase();
    final String sGroupName = GnrcParser.getSubstring(aInputString,11,61).trim().toUpperCase();

    final int iNumSeats;
    try
      {
      iNumSeats = Integer.parseInt(sNumSeats);
      }
    catch (Exception e)
      {
      throw new TranServerException("Unable to add corp header " + sGroupName + " Invalid number of seats - " + sNumSeats);
      }

    final String sCrsCode = getDefaultCrsCode(aCrs);

    // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_GRP_HDR_CMD) == false )
      throw new TranServerException("Invalid operation code for adding corporate header, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_GRP_HDR_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a Crs code to add a corporate header");

    if ( GnrcFormat.IsNull(sGroupName) )
      throw new TranServerException("Must specify a group name to add a corporate header");

    final ReqAddCorpHeader request = new ReqAddCorpHeader(sCrsCode,sGroupName,iNumSeats);
    request.commType = request.COMM_NATIVE_ASCII;

    return(request);
    }

  /**
   ***********************************************************************
   * Add endorsement
   ***********************************************************************
   */
  private static ReqAddEndorsement reqAddEndorsement(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sEndorsement = GnrcParser.getSubstring(aInputString,16,142).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ADD_ENDORSE_CMD) == false )
      throw new TranServerException("Invalid operation code for adding endorsement, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_ENDORSE_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add an endorsement");

    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to add an endorsement");
    */

    if ( GnrcFormat.IsNull(sEndorsement) )
      throw new TranServerException("Must specify endorsement text");


    // create request object
    final ReqAddEndorsement request = new ReqAddEndorsement(sCrsCode,sLocator,sEndorsement);

    return(request);
    }

/**
   ***********************************************************************
   * This method parses the input string, and adds one or multiple passengers
   * to a ReqAddnames object which it returns.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as showw below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   8      8     - the type of request,
                             see {@link xmax.crs.Generic.GnrcConvControl#AWR_ADDNAME_CMD}
   *    8 -  16      8     - a record locator, if one exists
   *
   * The following section may repeat several times to accomodate multiple passengers
   *   16 -  76     60     - the name of the passenger (e.g. FAIRCHILD/DAVID)
   *   76 -  79      3     - the Native Passenger Type Code (PTC)
   *   79 -  82      3     - the Generic Passenger Type Code (PTC)
   *   82 - 102     20     - the passenger ID, as specified by Airware
   *  102 - 132     30     - the name of an accompanying infant, if any
	 *  132 - 141      8     - infant date of birth ddmmyyyy, if any
   * </pre>
   ***********************************************************************
   */
  private static ReqAddNames reqAddNames(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);

    final ReqAddNames request = new ReqAddNames(sCrsCode,sLocator);


    // get all the names to be added
    String sName;
    String sGenericPTC;
    String sNativePTC;
    String sPassengerID;
    String sInfant;
		String sInfantDOB;

    /*
    for ( int i = 16; i < aInputString.length(); i += 116 )
      {
      sName        = GnrcParser.getSubstring(aInputString,i +  0,i + 60).trim().toUpperCase();
      sNativePTC   = GnrcParser.getSubstring(aInputString,i + 60,i + 63).trim().toUpperCase();
      sGenericPTC  = GnrcParser.getSubstring(aInputString,i + 63,i + 66).trim().toUpperCase();
      sPassengerID = GnrcParser.getSubstring(aInputString,i + 66,i + 86).trim().toUpperCase();
      sInfant      = GnrcParser.getSubstring(aInputString,i + 86,i + 116).trim().toUpperCase();

      request.addName(sName,sGenericPTC,sPassengerID,sInfant);
      }
    */

    for ( int i = 16; i < aInputString.length(); )
      {
      sName        = GnrcParser.getSubstring(aInputString,i, i+= 60).trim().toUpperCase();
      sNativePTC   = GnrcParser.getSubstring(aInputString,i, i+=  3).trim().toUpperCase();
      sGenericPTC  = GnrcParser.getSubstring(aInputString,i, i+=  3).trim().toUpperCase();
      sPassengerID = GnrcParser.getSubstring(aInputString,i, i+= 20).trim().toUpperCase();
      sInfant      = GnrcParser.getSubstring(aInputString,i, i+= 22).trim().toUpperCase();
			sInfantDOB   = GnrcParser.getSubstring(aInputString,i, i+= 8).trim().toUpperCase();
			//sInfantDOB = "151012009";
			System.out.println("sInfant/InfantDOB: " + sInfant + "/" + sInfantDOB );

      request.addName(sName,sGenericPTC,sPassengerID,sInfant,sInfantDOB);
      }

		// check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ADD_NAME_CMD) == false )
      throw new TranServerException("Invalid operation code for adding names, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_NAME_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a Crs code to add names");

    request.commType = request.COMM_NATIVE_ASCII;

    return(request);
    }

  /**
   ***********************************************************************
   * Change name: this verb changes makes it possible to change the name and ID
   * of a passenger, but not its passenger ID
   ***********************************************************************
   */
  private static ReqBuildPnr reqAirwareChangeName(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator   = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sCrsCode   = getDefaultCrsCode(aCrs);

    // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_CHG_NAME_CMD) == false )
      throw new TranServerException("Invalid operation code for change name, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_CHG_NAME_CMD );

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to change name");

    final ReqBuildPnr request = new ReqBuildPnr(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;

    final String NAME_PATTERN = " *([A-Z ]+)\\/([A-Z]+) *([A-Z]*)";
    String sPsgrID;
    String sFullName;
    String sPTC;
    String sInfant;
		String sInfantDOB;

    PNRNameElement oldname;
    PNRNameElement newname;
    String[] nameFields;

    for ( int i = 16; i < aInputString.length();)
      {
      /*
      sPsgrID   = GnrcParser.getSubstring(aInputString,i + 0,  i + 20 ).trim().toUpperCase();
      sFullName = GnrcParser.getSubstring(aInputString,i + 20, i + 80 ).trim().toUpperCase();
      sPTC      = GnrcParser.getSubstring(aInputString,i + 80, i + 83 ).trim().toUpperCase();
      sInfant   = GnrcParser.getSubstring(aInputString,i + 83, i + 113).trim().toUpperCase();
      */

      sPsgrID   = GnrcParser.getSubstring(aInputString,i, i+= 20 ).trim().toUpperCase();
      sFullName = GnrcParser.getSubstring(aInputString,i, i+= 60 ).trim().toUpperCase();
      sPTC      = GnrcParser.getSubstring(aInputString,i, i+=  3 ).trim().toUpperCase();
      sInfant   = GnrcParser.getSubstring(aInputString,i, i+= 30).trim().toUpperCase();
			//sInfantDOB = GnrcParser.getSubstring(aInputString,i, i+= 8).trim().toUpperCase();

      // check fields
      if ( GnrcFormat.IsNull(sPsgrID) )
        throw new TranServerException("Must specify a passenger ID to change name");

      if ( GnrcFormat.IsNull(sPTC) )
        throw new TranServerException("Must specify a passenger type to change name");

      if ( GnrcFormat.IsNull(sFullName) )
        throw new TranServerException("Must specify a new name to change name");

      nameFields = RegExpMatch.getMatchPatterns(sFullName,NAME_PATTERN);
      if ( (nameFields instanceof String[]) == false )
        throw new TranServerException(
            "New name field is not in expected format: LastName/FirstName Title");

      oldname = new PNRNameElement();
      oldname.setPassengerID(sPsgrID);

      newname = new PNRNameElement();
      newname.setPassengerID(sPsgrID);
      newname.LastName    = nameFields[1];
      newname.FirstName   = nameFields[2];
      newname.Title       = nameFields[3];
      newname.PTC         = sPTC;
      newname.InfantName  = sInfant;
			//newname.InfantDOB   = sInfantDOB;

      request.addModifyRequest(oldname,newname);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * Change name
   ***********************************************************************
   */
   /*
  private static ReqChangeName reqChangeName(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sPsgrID      = GnrcParser.getSubstring(aInputString,16,36).trim().toUpperCase();
    final String sFullName    = GnrcParser.getSubstring(aInputString,36,96).trim().toUpperCase();
    final String sPTC         = GnrcParser.getSubstring(aInputString,96,99).trim().toUpperCase();
    final String sInfant      = GnrcParser.getSubstring(aInputString,99,129).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);

     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_CHG_NAME_CMD) == false )
      throw new TranServerException("Invalid operation code for change name, received: " +
      sOperation + "  expected: " + GnrcConvControl.AWR_CHG_NAME_CMD );

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to change name");

    if ( GnrcFormat.IsNull(sPsgrID) )
      throw new TranServerException("Must specify a passenger ID to change name");

    if ( GnrcFormat.IsNull(sFullName) )
      throw new TranServerException("Must specify a new name to change name");

    if ( GnrcFormat.IsNull(sPTC) )
      throw new TranServerException("Must specify a passenger type to change name");


    // create the request object
    final ReqChangeName request = new ReqChangeName(sCrsCode,sLocator,sPsgrID);

    request.commType = request.COMM_NATIVE_ASCII;
    request.setName(sFullName);
    request.PTC        = sPTC;
    request.InfantName = sInfant;

    return(request);
    }
   */

  /**
   ***********************************************************************
   * Replace name: this verb changes makes it possible to completely
   * replace a passenger with another (replaces passenger ID)
   ***********************************************************************
   */
  private static ReqBuildPnr reqReplaceName(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator   = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sCrsCode   = getDefaultCrsCode(aCrs);

    // check fields
    if ( sOperation.equals(GnrcConvControl.REPL_NAME_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for replacing name, received: " + sOperation +
          "  expected: " + GnrcConvControl.REPL_NAME_CMD );

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException(
          "Must specify a locator to replace a passenger");

    final ReqBuildPnr request = new ReqBuildPnr(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;

    final String NAME_PATTERN = " *([A-Z ]+)\\/([A-Z]+) *([A-Z]*)";
    String sOldPsgrID;
    String sNewPsgrID;
    String sFullName;
    String sPTC;
    String sInfant;
		String sInfantDOB;

    PNRNameElement oldname;
    PNRNameElement newname;
    String[] nameFields;

    for ( int i = 16; i < aInputString.length();)
      {
      sOldPsgrID = GnrcParser.getSubstring(aInputString,i, i+= 20 ).trim().toUpperCase();
      sNewPsgrID = GnrcParser.getSubstring(aInputString,i, i+= 20 ).trim().toUpperCase();
      sFullName  = GnrcParser.getSubstring(aInputString,i, i+= 60 ).trim().toUpperCase();
      sPTC       = GnrcParser.getSubstring(aInputString,i, i+=  3 ).trim().toUpperCase();
      sInfant    = GnrcParser.getSubstring(aInputString,i, i+= 30).trim().toUpperCase();
			//sInfantDOB = GnrcParser.getSubstring(aInputString,i, i+=8).trim().toUpperCase();

      // check fields
      if ( GnrcFormat.IsNull(sOldPsgrID) )
        throw new TranServerException(
            "Must specify the passenger ID that you want to replace");

      if ( GnrcFormat.IsNull(sNewPsgrID) )
        throw new TranServerException(
            "Must specify the new passenger's ID");

      if ( GnrcFormat.IsNull(sPTC) )
        throw new TranServerException(
            "Must specify a Passenger Type Code for the new passenger");

      if ( GnrcFormat.IsNull(sFullName) )
        throw new TranServerException(
            "Must specify a the name of the new passenger");

      nameFields = RegExpMatch.getMatchPatterns(sFullName,NAME_PATTERN);
      if ( (nameFields instanceof String[]) == false )
        throw new TranServerException(
            "The name field is not in the expected format: " +
            "LastName/FirstName Title");

      oldname = new PNRNameElement();
      oldname.setPassengerID(sOldPsgrID);

      newname = new PNRNameElement();
      newname.setPassengerID(sNewPsgrID);
      newname.LastName    = nameFields[1];
      newname.FirstName   = nameFields[2];
      newname.Title       = nameFields[3];
      newname.PTC         = sPTC;
      newname.InfantName  = sInfant;
			//newname.InfantDOB   = sInfantDOB;

      request.addModifyRequest(oldname,newname);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * This method returns a request to change the itinerary on a Passenger Name
   * Record (PNR), either for all passengers or a subset thereoff; see below
   * for the format of the incoming ascii request.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as shown below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   ***********************************************************************
   */
  private static ReqChangePnrItin reqChangePnrItin(
      final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sReceiveBy = GnrcParser.getSubstring(aInputString,18,38).trim().toUpperCase();

    int HEADER_END = 38;

    // check fields
    if ( sOperation.equals(GnrcConvControl.CHG_PNR_ITIN_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for modifying PNR air segments, received: " + sOperation +
          "  expected: " + GnrcConvControl.CHG_PNR_ITIN_CMD );

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to modify air segments");

    final ReqChangePnrItin request = new ReqChangePnrItin(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;

    // add the passenger list who will be affected by the changes
    String sPsgrID;
    int PSGR_WIDTH    = 20;
    int PSGR_MAX      = 20;
    int PSGR_LIST_END = HEADER_END + (PSGR_WIDTH * PSGR_MAX);
    for ( int i = HEADER_END; i < PSGR_LIST_END; i += PSGR_WIDTH )
      {
      sPsgrID = GnrcParser.getSubstring(aInputString,i,i+PSGR_WIDTH).trim().toUpperCase();
      if ( GnrcFormat.NotNull(sPsgrID) )
        request.addName(sPsgrID);
      else
        break;
      }

    // add the list of segments to be removed from the PNR
    String sCarrier;
    int iFlightNum;
    String sFlightNum;
    String sDepCity;
    String sArrCity;
    String sDepDate;
    long iDepDate;
    int OLD_SEG_WIDTH = 21;
    int OLD_SEG_MAX   = 10;
    int OLD_SEG_LIST_END = PSGR_LIST_END + (OLD_SEG_WIDTH * OLD_SEG_MAX);

    for ( int i = PSGR_LIST_END; i < OLD_SEG_LIST_END ; i += OLD_SEG_WIDTH )
      {
      String seg =
        GnrcParser.getSubstring(aInputString,i,i+OLD_SEG_WIDTH).trim().toUpperCase();

      if (seg.equals(""))
        break;

      PNRItinAirSegment airSeg = new PNRItinAirSegment();

      sCarrier   = GnrcParser.getSubstring(seg,  0,  2).trim().toUpperCase();
      sFlightNum = GnrcParser.getSubstring(seg,  2,  7).trim().toUpperCase();
      sDepCity   = GnrcParser.getSubstring(seg,  7, 10).trim().toUpperCase();
      sArrCity   = GnrcParser.getSubstring(seg, 10, 13).trim().toUpperCase();
      sDepDate   = GnrcParser.getSubstring(seg, 13, 21).trim().toUpperCase();

      try
        { iFlightNum = Integer.parseInt(sFlightNum); }
      catch (Exception e)
        { throw new TranServerException("Invalid flight number" + sFlightNum); }

      // convert the date strings
      final String sCrsDepDate;
      try
        { iDepDate = GnrcFormat.ScanLongDate(sDepDate); }
      catch (Exception e)
        { throw new TranServerException("Invalid departure date format: " + sDepDate); }

      request.addSegmentToBeCancelled(sCarrier,iFlightNum,sDepCity,sArrCity,iDepDate);
      } // end for that adds segments to be cancelled


    // add the new segments to be added to the PNR
    String sAirSeg;
    PNRItinSegment airseg;
    int NEW_SEG_WIDTH    = 60;
    int NEW_SEG_MAX      = 10;
    int NEW_SEG_LIST_END = OLD_SEG_LIST_END + (NEW_SEG_WIDTH * NEW_SEG_MAX);
    for ( int i = OLD_SEG_LIST_END; i < NEW_SEG_LIST_END; i += NEW_SEG_WIDTH )
      {
      sAirSeg = GnrcParser.getSubstring(aInputString,i,i + NEW_SEG_WIDTH);
      airseg  = scanAirSegment(sAirSeg);
      if ( airseg instanceof PNRItinAirSegment )
        request.addNewSegment((PNRItinAirSegment)airseg);
      }

    return(request);

    } // end reqChangePnrItin


  /**
   ***********************************************************************
   * This method returns a request to add an Itinerary to a PNR, in the format
   * specified below; this request in turn calls {@link scanAirSegment} to
   * parse the 55 char AirSegment portions of the Input String.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as showw below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   8      8     - the type of request,
                             see {@link xmax.crs.Generic.GnrcConvControl#AWR_AIRSEG_CMD}
   *    8 -  16      8     - a record locator, if one exists
   *   16 -  71     55     - Air Segment information. Subsequent segments
                             are attached in the same 55 char pattern,
                             see {@link scanAirSegment} below.
                           - this pattern may repeat multiple times
   * </pre>
   ***********************************************************************
   */
  private static ReqAddAirSeg reqAddAirSeg(final String aInputString,
                                           final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator   = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sCrsCode   = getDefaultCrsCode(aCrs);

    // create the request object
    final ReqAddAirSeg request = new ReqAddAirSeg(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;

    String sAirSeg;
    PNRItinSegment airseg;
    final int AIR_SEG_WIDTH = 60;
    for ( int i = 16; i < aInputString.length(); i += AIR_SEG_WIDTH )
      {
      sAirSeg = GnrcParser.getSubstring(aInputString,i,i + AIR_SEG_WIDTH);
      airseg  = scanAirSegment(sAirSeg);
      if ( airseg instanceof PNRItinSegment )
        request.addSegment(airseg);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * This method returns a request to add remarks to a PNR.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as showw below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   ***********************************************************************
   */
  private static ReqBuildPnr reqAddRemarks(final String aInputString,
                                            final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sReceiveBy = GnrcParser.getSubstring(aInputString,18,38).trim().toUpperCase();

    // create the request object
    final ReqBuildPnr request = new ReqBuildPnr(sCrsCode,sLocator);
    request.commType    = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;

    // sccan in each of the new remarks
    final int RMK_SEG_WIDTH = 136;
    String sType;
    String sText;
    String sCode;
    String sCarrier;
    String sPsgrID;
    for ( int i = 38; i < aInputString.length(); i += RMK_SEG_WIDTH )
      {
      sType    = GnrcParser.getSubstring(aInputString,i + 0,  i + 10).trim().toUpperCase();
      sText    = GnrcParser.getSubstring(aInputString,i + 10, i + 110).trim().toUpperCase();
      sPsgrID  = GnrcParser.getSubstring(aInputString,i + 110,i + 130).trim().toUpperCase();
      sCarrier = GnrcParser.getSubstring(aInputString,i + 130,i + 132).trim().toUpperCase();
      sCode    = GnrcParser.getSubstring(aInputString,i + 132,i + 136).trim().toUpperCase();

      if ( sType.equals("SSR") )
        {
        final PNRSsrRemark ssr = new PNRSsrRemark();
        ssr.Code       = sCode;
        ssr.Carrier    = sCarrier;
        ssr.setPsgrID(sPsgrID);
        ssr.RemarkText = sText;
        request.addInsertRequest(ssr);
        }
      else if ( sType.equals("FREQFLY") )
        {
        final PNRFreqFlyRemark ff = new PNRFreqFlyRemark(sText,sCarrier);
        ff.setPsgrID(sPsgrID);
        request.addInsertRequest(ff);
        }
      else if ( sType.equals("GENERAL") )
        {
        final PNRGeneralRemark rm = new PNRGeneralRemark(sText);
        rm.setPsgrID(sPsgrID);
        request.addInsertRequest(rm);
        }
      else if ( GnrcFormat.NotNull(sType) )
        throw new TranServerException("Could not add remark.  Unrecognized remark type " + sType);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * This method returns a request to modify remarks on a PNR.
   *
   * @param aInputString
   *  a fixed-width string request
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   ***********************************************************************
   */
  private static ReqBuildPnr reqModRemarks(final String aInputString,
                                           final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sReceiveBy = GnrcParser.getSubstring(aInputString,18,38).trim().toUpperCase();

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to change remarks");

    // create the request object
    final ReqBuildPnr request = new ReqBuildPnr(sCrsCode,sLocator);
    request.commType    = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;

    // sccan in each of the new remarks
    final int RMK_SEG_WIDTH = 240;
    String sType;
    String sOldText;
    String sOldCode;
    String sNewText;
    String sNewCode;
    String sCarrier;
    String sPsgrID;
    for ( int i = 38; i < aInputString.length(); i += RMK_SEG_WIDTH )
      {
      sType    = GnrcParser.getSubstring(aInputString,i + 0,  i + 10).trim().toUpperCase();
      sOldText = GnrcParser.getSubstring(aInputString,i + 10, i + 110).trim().toUpperCase();
      sPsgrID  = GnrcParser.getSubstring(aInputString,i + 110,i + 130).trim().toUpperCase();
      sCarrier = GnrcParser.getSubstring(aInputString,i + 130,i + 132).trim().toUpperCase();
      sOldCode = GnrcParser.getSubstring(aInputString,i + 132,i + 136).trim().toUpperCase();
      sNewText = GnrcParser.getSubstring(aInputString,i + 136,i + 236).trim().toUpperCase();
      sNewCode = GnrcParser.getSubstring(aInputString,i + 236,i + 240).trim().toUpperCase();

      if ( sType.equals("SSR") )
        {
        final PNRSsrRemark old_ssr = new PNRSsrRemark();
        old_ssr.Code       = sOldCode;
        old_ssr.Carrier    = sCarrier;
        old_ssr.setPsgrID(sPsgrID);
        old_ssr.RemarkText = sOldText;

        final PNRSsrRemark new_ssr = new PNRSsrRemark();
        new_ssr.Code       = sNewCode;
        new_ssr.Carrier    = sCarrier;
        new_ssr.setPsgrID(sPsgrID);
        new_ssr.RemarkText = sNewText;

        request.addModifyRequest(old_ssr,new_ssr);
        }
      else if ( sType.equals("FREQFLY") )
        {
        final PNRFreqFlyRemark old_ff = new PNRFreqFlyRemark(sOldText,sCarrier);
        old_ff.setPsgrID(sPsgrID);

        final PNRFreqFlyRemark new_ff = new PNRFreqFlyRemark(sNewText,sCarrier);
        new_ff.setPsgrID(sPsgrID);

        request.addModifyRequest(old_ff,new_ff);
        }
      else
        throw new TranServerException("Could not modify remark.  Unrecognized remark type " + sType);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * This method returns a request to delete remarks from a PNR, in the format
   * specified below;
   *
   * @param aInputString
   *  a fixed-width string request, formatted as showw below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   8      8     - the type of request,
                             see {@link xmax.crs.Generic.GnrcConvControl#AWR_AIRSEG_CMD}
   *    8 -  10      2     - crs code
   *   10 -  18      8     - a record locator, if one exists
   *   18 -  38     20     - 'ReceivedFrom' field
   * plus the following structure which can repeat multiple times
   *    0 -  10     10     - type of remark
   *   10 - 110    100     - text of remark
   *  110 - 130     20     - passenger ID for passenger associated remark
   *  130 - 132      2     - carrier code
   *  132 - 136      4     - 4 letter service code for SSR remarks
   * </pre>
   ***********************************************************************
   */
  private static ReqBuildPnr reqDelRemarks(final String aInputString,
                                           final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sReceiveBy = GnrcParser.getSubstring(aInputString,18,38).trim().toUpperCase();

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to delete remarks");

    // create the request object
    final ReqBuildPnr request = new ReqBuildPnr(sCrsCode,sLocator);
    request.commType    = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;

    // scan in each of the new remarks
    final int RMK_SEG_WIDTH = 136;
    String sType;
    String sText;
    String sCode;
    String sCarrier;
    String sPsgrID;
    for ( int i = 38; i < aInputString.length(); i += RMK_SEG_WIDTH )
      {
      sType    = GnrcParser.getSubstring(aInputString,i + 0,  i + 10).trim().toUpperCase();
      sText    = GnrcParser.getSubstring(aInputString,i + 10, i + 110).trim().toUpperCase();
      sPsgrID  = GnrcParser.getSubstring(aInputString,i + 110,i + 130).trim().toUpperCase();
      sCarrier = GnrcParser.getSubstring(aInputString,i + 130,i + 132).trim().toUpperCase();
      sCode    = GnrcParser.getSubstring(aInputString,i + 132,i + 136).trim().toUpperCase();

      if ( sType.equals("SSR") )
        {
        final PNRSsrRemark ssr = new PNRSsrRemark();
        ssr.Code       = sCode;
        ssr.Carrier    = sCarrier;
        ssr.setPsgrID(sPsgrID);
        ssr.RemarkText = sText;
        request.addDeleteRequest(ssr);
        }
      else if ( sType.equals("FREQFLY") )
        {
        final PNRFreqFlyRemark ff = new PNRFreqFlyRemark(sText,sCarrier);
        ff.setPsgrID(sPsgrID);
        request.addDeleteRequest(ff);
        }
      else if ( sType.equals("GENERAL") || sType.equals("GENE") )
        {
        final PNRGeneralRemark rm = new PNRGeneralRemark(sText);
        if (sPsgrID.equals(GnrcFormat.SetWidth("",20)))
          rm.setPsgrID("");
        else
          rm.setPsgrID(sPsgrID);

        request.addDeleteRequest(rm);
        }
      else if ( GnrcFormat.NotNull(sType) )
        throw new TranServerException(
            "Deletion of " + sType + " remarks has not been implemented");
      }

    return(request);
    }

  /**
   ***********************************************************************
   * This method returns a request to delete remarks from a PNR, in the format
   * specified below; this is the format used by Airware - it is functionally
   * equivalent to {@link reqDelRemarks}, but the Remark Type field passed is 4
   * char rather than 10 char.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as showw below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   8      8     - the type of request,
                             see {@link xmax.crs.Generic.GnrcConvControl#AWR_AIRSEG_CMD}
   *    8 -  10      2     - crs code
   *   10 -  18      8     - a record locator, if one exists
   *   18 -  38     20     - 'ReceivedFrom' field
   * plus the following structure which can repeat multiple times
   *    0 -   4      4     - type of remark
   *    4 - 104    100     - text of remark
   *  104 - 124     20     - passenger ID for passenger associated remark
   *  124 - 126      2     - carrier code
   *  126 - 130      4     - 4 letter service code for SSR remarks
   * </pre>
   ***********************************************************************
   */
  private static ReqBuildPnr reqCxlRemarks(final String aInputString,
                                           final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sReceiveBy = GnrcParser.getSubstring(aInputString,18,38).trim().toUpperCase();

    // if ( GnrcFormat.IsNull(sLocator) )
    //   throw new TranServerException(
    //       "Must specify a locator to delete remarks");

    // create the request object
    final ReqBuildPnr request = new ReqBuildPnr(sCrsCode,sLocator);
    request.commType    = request.COMM_NATIVE_ASCII;

    if (GnrcFormat.NotNull(sReceiveBy))
      request.RequestedBy = sReceiveBy;
    else // use Transerver Default
      request.RequestedBy = ConfigTranServer.application.getProperty("receiveBy");

    // scan in each of the new remarks
    String sType;
    String sText;
    String sCode;
    String sCarrier;
    String sPsgrID;

    for ( int i = 38; i < aInputString.length(); )
      {
      sType    = GnrcParser.getSubstring(aInputString,i, i+=  4).trim().toUpperCase();
      sText    = GnrcParser.getSubstring(aInputString,i, i+=100).trim().toUpperCase();
      sPsgrID  = GnrcParser.getSubstring(aInputString,i, i+= 20).trim().toUpperCase();
      sCarrier = GnrcParser.getSubstring(aInputString,i, i+=  2).trim().toUpperCase();
      sCode    = GnrcParser.getSubstring(aInputString,i, i+=  4).trim().toUpperCase();

      if ( sType.equals(PNRRemark.SSR_REMARK) )
        {
        final PNRSsrRemark ssr = new PNRSsrRemark();
        ssr.Code       = sCode;
        ssr.Carrier    = sCarrier;
        ssr.setPsgrID(sPsgrID);
        ssr.RemarkText = sText;
        request.addDeleteRequest(ssr);
        }
      else if ( sType.equals(PNRRemark.AWR_GENERAL_REMARK) )
        {
        final PNRGeneralRemark rm = new PNRGeneralRemark(sText);
        if (sPsgrID.equals(GnrcFormat.SetWidth("",20)))
          rm.setPsgrID("");
        else
          rm.setPsgrID(sPsgrID);

        request.addDeleteRequest(rm);
        }
      else if ( GnrcFormat.NotNull(sType) )
        throw new TranServerException(
            "Deletion of " + sType + " remarks has not been implemented");
      }

    return(request);
    } // end reqCxlRemarks

  /**
   ***********************************************************************
   * This method parses the 55 char portion of requests containing
   * flight segment information
   *
   * @param aInputString
   *  a fixed-width string request, formatted as shown below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   3      3     - the Carrier Code
   *    3 -   8      5     - the Flight Number
   *    8 -  11      3     - the Departure City Code
   *   11 -  14      3     - the Arrival City Code
   *   14 -  22      8     - the Departure Date YYYYMMDD
   *   22 -  26      4     - the Departure Time HHMM (24 hours)
   *   26 -  34      8     - the Arrival Date YYYYMMDD
   *   34 -  38      4     - the Arrival Time HHMM (24 hours)
   *   38 -  40      2     - the Inventory Class Code
   *   40 -  44      4     - the Action Code
   *   44 -  47      3     - the Number of Seats
   *   47 -  55      8     - the Remote Locator for this Segment
   * </pre>
   ***********************************************************************
   */
  private static PNRItinSegment scanAirSegment(final String aInputString) throws Exception
    {
    // if string is entirely blank, return nothing
    if ( aInputString.trim().length() == 0 )
      return(null);

    final String sActionCode = GnrcParser.getSubstring(aInputString,40,44).trim().toUpperCase();

    // check if this is an arunk
    if ( sActionCode.equals("ARNK") )
      return( new PNRItinArunkSegment() );


    final PNRItinAirSegment airseg = new PNRItinAirSegment();

    int i = 0;
    airseg.Carrier           = GnrcParser.getSubstring(aInputString,i,i+=3).trim().toUpperCase();
    final String sFlightNum  = GnrcParser.getSubstring(aInputString,i,i+=5).trim().toUpperCase();
    airseg.DepartureCityCode = GnrcParser.getSubstring(aInputString,i,i+=3).trim().toUpperCase();
    airseg.ArrivalCityCode   = GnrcParser.getSubstring(aInputString,i,i+=3).trim().toUpperCase();
    final String sDepDate    = GnrcParser.getSubstring(aInputString,i,i+=8).trim().toUpperCase();
    final String sDepTime    = GnrcParser.getSubstring(aInputString,i,i+=4).trim().toUpperCase();
    final String sArrDate    = GnrcParser.getSubstring(aInputString,i,i+=8).trim().toUpperCase();
    final String sArrTime    = GnrcParser.getSubstring(aInputString,i,i+=4).trim().toUpperCase();
    airseg.InventoryClass    = GnrcParser.getSubstring(aInputString,i,i+=2).trim().toUpperCase();
    airseg.ActionCode        = GnrcParser.getSubstring(aInputString,i,i+=4).trim().toUpperCase();
    final String sNumSeats   = GnrcParser.getSubstring(aInputString,i,i+=3).trim().toUpperCase();
    airseg.RemoteLocator     = GnrcParser.getSubstring(aInputString,i,i+=8).trim().toUpperCase();
    airseg.BlockType         = GnrcParser.getSubstring(aInputString,i,i+=3).trim().toUpperCase();
    final String sPassiveFlg = GnrcParser.getSubstring(aInputString,i,i+=1).trim().toUpperCase();
    final String sSchedFlg   = GnrcParser.getSubstring(aInputString,i,i+=1).trim().toUpperCase();

    if ( sPassiveFlg.equals("P") )
      airseg.isPassive = true;
    else
      airseg.isPassive = false;

    if ( sSchedFlg.equals("F") )
      airseg.isScheduled = false;
    else
      airseg.isScheduled = true;

    // make sure that dep/arr times are given for non-scheduled flights
    if ( airseg.isScheduled == false &&
         (sDepTime.equals("") || sArrTime.equals("")) ) {
      throw new TranServerException(
           "Departure and Arrival times must be provided when " +
           "selling non-scheduled segment " + airseg.Carrier + sFlightNum);
      }

    // scan the departure date and time
    if (sDepTime.equals("")) {
      try {
        airseg.DepartureDateTime = GnrcFormat.ScanLongDate(sDepDate);
      }
      catch (Exception e) {
        throw new TranServerException(
            "Invalid departure date format: " + sDepDate);
      }
    }
    else { // if a time was provided
      try {
        airseg.DepartureDateTime
          = GnrcFormat.ScanLongDateTime(sDepDate + sDepTime);
      }
      catch (Exception e) {
        throw new TranServerException(
            "Invalid departure date/time format: " + sDepDate + sDepTime);
      }
    }

    // if an arrival date was provided
    if (!sArrDate.equals("") && sArrTime.equals("")) {
      try {
        airseg.ArrivalDateTime = GnrcFormat.ScanLongDate(sArrDate);
      }
      catch (Exception e) {
        throw new TranServerException(
            "Invalid arrival date format: " + sArrDate);
      }
    }
    // if an arrival date and time were both provided
    else if (! (sArrDate.equals("") || sArrTime.equals("")) ) {
      try {
        airseg.ArrivalDateTime
          = GnrcFormat.ScanLongDateTime(sArrDate + sArrTime);
      }
      catch (Exception e) {
        throw new TranServerException(
            "Invalid arrival date/time format: " + sArrDate + sArrTime);
      }
    }

    // scan the number of seats
    try
      {
      airseg.NumberOfSeats = Integer.parseInt(sNumSeats);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid number of seats " + sNumSeats);
      }

    // scan the flight number
    try
      {
      airseg.FlightNumber = Integer.parseInt(sFlightNum);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid flight number " + sFlightNum);
      }


    // check fields
    if ( GnrcFormat.IsNull(airseg.Carrier) )
      throw new TranServerException("Must specify a carrier to add an air segment");

    if ( airseg.FlightNumber == 0 )
      throw new TranServerException("Must specify a flight number to add an air segment");

    if ( GnrcFormat.IsNull(airseg.InventoryClass) )
      throw new TranServerException("Must specify an inventory class to add an air segment");

    if ( airseg.DepartureDateTime == 0 )
      throw new TranServerException("Must specify a departure date to add an air segment");

    if ( airseg.NumberOfSeats == 0 )
      throw new TranServerException("Must specify number of seats > 0 to add an air segment");

    if ( GnrcFormat.IsNull(airseg.DepartureCityCode) )
      throw new TranServerException("Must specify a departure city to add an air segment");

    if ( GnrcFormat.IsNull(airseg.ArrivalCityCode) )
      throw new TranServerException("Must specify an arrival city to add an air segment");

    if ( GnrcFormat.IsNull(airseg.ActionCode) )
      throw new TranServerException("Must specify an action code to add an air segment");

    return(airseg);
    }

  /**
   ***********************************************************************
   * Accept schedule changes
   ***********************************************************************
   */
  private static ReqAcceptSchedChange reqAcceptSchedChange(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();

    final String sReceiveBy = "AIRWARE";
    final String sCrsCode   = getDefaultCrsCode(aCrs);


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ACCEPT_CHG_CMD) == false )
      throw new TranServerException("Invalid operation code for accepting schedule change, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ACCEPT_CHG_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to accept a schedule change");

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to accept a schedule change");

    // create request object
    final ReqAcceptSchedChange request = new ReqAcceptSchedChange(sCrsCode,sLocator,sReceiveBy);
    return(request);
    }

  /**
   ***********************************************************************
   * Used by Airware to add one remark at a time
   ***********************************************************************
   */
  private static ReqAddRemark reqAddRemark(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator   = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sCrsCode   = getDefaultCrsCode(aCrs);

     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ADD_REMARK_CMD) == false )
      throw new TranServerException("Invalid operation code for adding remark, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_REMARK_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add a remark");


    // create request object
    final ReqAddRemark request = new ReqAddRemark(sCrsCode,sLocator);

    String sSection;
    String sService;
    String sText;
    String sCarrier;
    String sPassengerID;
    for ( int i = 16; i < aInputString.length(); i += 159 )
      {
      sSection     = GnrcParser.getSubstring(aInputString,i + 0,  i + 4  ).trim().toUpperCase();
      sService     = GnrcParser.getSubstring(aInputString,i + 4,  i + 8  ).trim().toUpperCase();
      sText        = GnrcParser.getSubstring(aInputString,i + 8,  i + 136).trim().toUpperCase();
      sCarrier     = GnrcParser.getSubstring(aInputString,i + 136,i + 139).trim().toUpperCase();
      sPassengerID = GnrcParser.getSubstring(aInputString,i + 139,i + 159).trim().toUpperCase();

      request.addRemark(sSection,sText,sService,sCarrier,sPassengerID);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * This method returns a request to cancel a remark from a PNR, in the format
   * specified below; </br>
   * <i>Deprecation warning! This is an old Airware implementation, as of 05-14-2002 I am not sure
   * whether it is being used - the current Airware implementation as of this
   * date is {@link reqCxlRemarks}</i>
   *
   * @param aInputString
   *  a fixed-width string request, formatted as showw below
   * @param aCRS
   *  the Computer Reservation System in which to execute the request
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   8      8     - the type of request, see {@link xmax.crs.Generic.GnrcConvControl}
   *    8 -  16      8     - the PNR locator
   *   16 -  20      4     - the type of remark
   *   20 -  24      4     - the service code for SSR remarks
   *   24 - 152    128     - the text of the remark
   * </pre>
   ***********************************************************************
   */
  private static ReqCxlRemark reqCxlRemark(final String aInputString, final GnrcCrs aCrs) throws Exception
  {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator   = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sSection   = GnrcParser.getSubstring(aInputString,16,20).trim().toUpperCase();
    final String sService   = GnrcParser.getSubstring(aInputString,20,24).trim().toUpperCase();
    final String sText      = GnrcParser.getSubstring(aInputString,24,152).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);

     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_CXL_REMARK_CMD) == false )
      throw new TranServerException("Invalid operation code for cancelling remark, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_CXL_REMARK_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to cancel a remark");

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to cancel a remark");

    if ( GnrcFormat.IsNull(sSection) )
      throw new TranServerException("Must specify a section to cancel a remark");


    // create request object
    final ReqCxlRemark request = new ReqCxlRemark(sCrsCode,sLocator,sSection);
    request.Service = sService;
    request.Text    = sText;

    return(request);
    }

  /**
   ***********************************************************************
   * Add phone number
   ***********************************************************************
   */
  private static ReqAddPhone reqAddPhone(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator   = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sPhone     = GnrcParser.getSubstring(aInputString,16,106).trim().toUpperCase();


    final String sCrsCode = getDefaultCrsCode(aCrs);


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ADD_PHONE_CMD) == false )
      throw new TranServerException("Invalid operation code for adding phone number, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_PHONE_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add a phone number");
   /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to add a phone number");
   */
    if ( GnrcFormat.IsNull(sPhone) )
      throw new TranServerException("Must specify a phone number to add");


    // create request object
    final ReqAddPhone request = new ReqAddPhone(sCrsCode,sLocator,sPhone);

    return(request);
    }

  /**
   ***********************************************************************
   * Add Tour code
   ***********************************************************************
   */
  private static ReqAddTourCode reqAddTourCode(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sTourCode    = GnrcParser.getSubstring(aInputString,16,142).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ADD_TOURCODE_CMD) == false )
      throw new TranServerException("Invalid operation code for adding tour code, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_TOURCODE_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add a tour code");
    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to add a tour code");
    */
    if ( GnrcFormat.IsNull(sTourCode) )
      throw new TranServerException("Must specify a tour code");


    // create request object
    final ReqAddTourCode request = new ReqAddTourCode(sCrsCode,sLocator,sTourCode);

    return(request);
    }

  /**
   ***********************************************************************
   * Add commission; note that in the Airware request, the percent flag is
   * either an 'A' for fixed Amount, or anything else, meaning a percentage
   * commission
   ***********************************************************************
   */
  private static ReqAddCommission reqAddCommission(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sCommission  = GnrcParser.getSubstring(aInputString,16,23).trim().toUpperCase();
    final String sPercentFlag = GnrcParser.getSubstring(aInputString,23,24).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);


     // check fields
    final float fCommission;
    try
      {
      fCommission = Float.parseFloat(sCommission);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid commission amount " + sCommission);
      }

    if ( sOperation.equals(GnrcConvControl.AWR_ADD_COMM_CMD) == false )
      throw new TranServerException("Invalid operation code for adding commission, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_COMM_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add a commission");
    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to add a commission");
    */

    // create request object
    final ReqAddCommission request = new ReqAddCommission(sCrsCode,sLocator);

    request.fAmount = fCommission;

    if ( sPercentFlag.equals("A") )
      request.isPercent  = false;
    else
      request.isPercent  = true;

    return(request);
    }

  /**
   ***********************************************************************
   * Add FOP
   ***********************************************************************
   */
  private static ReqAddFOP reqAddFOP(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sFOP         = GnrcParser.getSubstring(aInputString,16,142).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ADD_FOP_CMD) == false )
      throw new TranServerException("Invalid operation code for adding form of payment, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_FOP_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add a form of payment");

    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to add a form of payment");
    */
    if ( GnrcFormat.IsNull(sFOP) )
      throw new TranServerException("Must specify a form of payment");


    // create request object
    final ReqAddFOP request = new ReqAddFOP(sCrsCode,sLocator,sFOP);

    return(request);
    }

  /**
   ***********************************************************************
   * Add ticket
   ***********************************************************************
   */
  private static ReqAddTicket reqAddTicket(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator      = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sTicketRemark = GnrcParser.getSubstring(aInputString,16,56).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_ADD_TICKET_CMD) == false )
      throw new TranServerException("Invalid operation code for adding ticket remark, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_ADD_TICKET_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add a ticket remark");
    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to add a ticket remark");
    */
    if ( GnrcFormat.IsNull(sTicketRemark) )
      throw new TranServerException("Must specify a ticket remark");


    // create request object
    final ReqAddTicket request = new ReqAddTicket(sCrsCode,sLocator,sTicketRemark);

    return(request);
    }

  /**
   ***********************************************************************
   * Add receive By line
   ***********************************************************************
   */
  private static ReqAddReceiveBy reqAddReceiveBy(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);
    final String sName         = GnrcParser.getSubstring(aInputString,8,77).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);
    final String sLocator = "";


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_RCV_PNR_CMD) == false )
      throw new TranServerException("Invalid operation code for receive by remark, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_RCV_PNR_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to add a receive by remark");

    if ( GnrcFormat.IsNull(sName) )
      throw new TranServerException("Must specify a name");


    // create request object
    final ReqAddReceiveBy request = new ReqAddReceiveBy(sCrsCode,sLocator,sName);

    return(request);
    }

  /**
   ***********************************************************************
   * End Transaction
   ***********************************************************************
   */
  private static ReqEndTransaction reqEndTransaction(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);

    final String sCrsCode = getDefaultCrsCode(aCrs);

     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_END_XACT_CMD) == false )
      throw new TranServerException("Invalid operation code for end transactions, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_END_XACT_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to end a transaction");

    // create request object
    final ReqEndTransaction request = new ReqEndTransaction(sCrsCode);

    return(request);
    }

  /**
   ***********************************************************************
   * Queue PNR
   ***********************************************************************
   */
  private static ReqQueuePNR reqQueuePNR(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator      = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sQueueName    = GnrcParser.getSubstring(aInputString,16,36).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);


     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_QUEUE_PNR_CMD) == false )
      throw new TranServerException("Invalid operation code for queueing PNR, received: " + sOperation + "  expected: " + GnrcConvControl.AWR_QUEUE_PNR_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to queue a PNR");

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to be queued");

    if ( GnrcFormat.IsNull(sQueueName) )
      throw new TranServerException("Must specify a queue name");


    // create request object
    final ReqQueuePNR request = new ReqQueuePNR(sCrsCode,sLocator,sQueueName);

    return(request);
    }

  /**
   ***********************************************************************
   * Free form
   ***********************************************************************
   */
  private static ReqFreeForm reqFreeForm(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);
    final String sRequest      = GnrcParser.getSubstring(aInputString,8,88).trim().toUpperCase();

    final String sCrsCode = getDefaultCrsCode(aCrs);

    // create request object
    final ReqFreeForm request = new ReqFreeForm(sCrsCode,sRequest);


     // check fields
    if ( sOperation.equals(GnrcConvControl.FREEFORM_CMD) )
      request.commType = request.COMM_NATIVE_ASCII;
    else if ( sOperation.equals(GnrcConvControl.AWR_FREEFORM_CMD) )
      request.commType = request.COMM_NATIVE_AIRWARE;
    else
      throw new TranServerException("Invalid operation code for freeform request, received: " + sOperation);

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code for freeform request");

    if ( GnrcFormat.IsNull(sRequest) )
      throw new TranServerException("Must specify a request");

    return(request);
    }

  /**
   ***********************************************************************
   * Assign Printer
   ***********************************************************************
   */
  private static ReqAssignPrinter reqAssignPrinter(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode     = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sPrinterName = GnrcParser.getSubstring(aInputString,10,30).trim().toUpperCase();

    // create request object
    final ReqAssignPrinter request = new ReqAssignPrinter(sCrsCode,sPrinterName);

     // check fields
    if ( sOperation.equals(GnrcConvControl.SET_PRN_CMD) )
      request.commType = request.COMM_NATIVE_ASCII;
    else
      throw new TranServerException("Invalid operation code for assign printer request, received: " + sOperation);

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code for freeform request");

    if ( GnrcFormat.IsNull(sPrinterName) )
      throw new TranServerException("Must specify a printer");

    return(request);
    }

  /**
   ***********************************************************************
   * Enable log forwarding
   ***********************************************************************
   */
  private static ReqEnableLogForwarding reqEnableLogForwarding(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);
    final String sTaName       = GnrcParser.getSubstring(aInputString,8,18).trim().toUpperCase();


     // check fields
    if ( sOperation.equals(GnrcConvControl.START_LOG_CMD) == false )
      throw new TranServerException("Invalid operation code for start log command, received: " + sOperation + " expected: " + GnrcConvControl.START_LOG_CMD);

    // create request object
    final ReqEnableLogForwarding request = new ReqEnableLogForwarding(sTaName);
    request.TaName = sTaName;

    return(request);
    }

  /**
   ***********************************************************************
   * Disable log forwarding
   ***********************************************************************
   */
  private static ReqDisableLogForwarding reqDisableLogForwarding(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);


     // check fields
    if ( sOperation.equals(GnrcConvControl.END_LOG_CMD) == false )
      throw new TranServerException("Invalid operation code for stop log command, received: " + sOperation + " expected: " + GnrcConvControl.END_LOG_CMD);

    // create request object
    final ReqDisableLogForwarding request = new ReqDisableLogForwarding();
    return(request);
    }

  /**
   ***********************************************************************
   * Fare PNR for Airware
   ***********************************************************************
   */
  private static ReqGetFare reqAirwareFarePNR(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation  = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator    = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sPsgrIDs    = GnrcParser.getSubstring(aInputString,16,416);
    final String sFareType   = GnrcParser.getSubstring(aInputString,416,417);
    final String sSegments   = GnrcParser.getSubstring(aInputString,417,457);
		//final String sTaxExmpt   = GnrcParser.getSubstring(aInputString,457,458);

    final String sCrsCode    = getDefaultCrsCode(aCrs);

     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_FARE_CMD) == false )
      throw new TranServerException("Invalid operation code for Get Fare request, received: " + sOperation);

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code for Get Fare request");
    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator for Get Fare request");
    */

    // create the passenger ID array
    final int PSGR_ID_SIZE = 20;
    final String[] PsgrIDList = stringToStrArray(sPsgrIDs,PSGR_ID_SIZE);

    // create the segment number array
    final int SEG_NUM_SIZE = 2;
    final int[] SegmentList = stringToIntArray(sSegments,SEG_NUM_SIZE);


    // create request object
    final ReqGetFare request = new ReqGetFare(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_AIRWARE;

    // set the fare type
    if ( sFareType.equals("T") || sFareType.equals("Y") )
      request.setFareType(ReqGetFare.FARE_LOWEST);
    else
      request.setFareType(ReqGetFare.FARE_REGULAR);

    // set segment specifiers
    for ( int i = 0; i < SegmentList.length; i++ )
      {
      if ( SegmentList[i] > 0 )
        request.addSegment(SegmentList[i]);
      }

    // set name specifiers
    for ( int i = 0; i < PsgrIDList.length; i++ )
      {
      if ( PsgrIDList[i].length() > 0 )
        request.addName(PsgrIDList[i],"ADT");
      }

    return(request);
    }

  /**
   ***********************************************************************
   * This method returns a request to fare a PNR; note that a valid PNR
   * must exist in order to obtain a result.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as follows:
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   7      8     - the type of request, see {@link xmax.crs.Generic.GnrcConvControl}
   *    8 -   9      2     - a code denoting the CRS codes:
	 *                         1A=Amadeus  AA=Sabre  UA=Apollo/Galileo  1P=Worldspan
   *   10 -  17      8     - a Passenger Name Record (PNR) locator
   *   18 -  18      1     - the rate flag (L=Lowest - C=Contract - T=Tax-Exempt)
   *                         any other character is interpreted to mean the regular coach fare
   *   19 -  19      1     - a flag used to indicate whether the Fare should be stored with the PNR
   *                         'T' or 'Y' means true, any other character means false
   *   20 -  59     40     - a list of the segments to be fared, for example: 0103 (segments 1 and 3)
	 *                         each segment uses 2 spaces, and up to 20 segments can be passed
   *   60 - 459    400     - a list of numeric string passenger ID's generated by Airware
	 *                         each string can take up to 20 spaces, and up to 20 passengers can be passed
   *  460 - 519     60     - a list of {@link <a href="crsCodes.html#ptc_codes">Passenger Type Codes</a>}
	 *                         each code takes the form of 3 upper cap characters, and up to 20 codes can be passed
   * </pre>
   ***********************************************************************
   */
  private static ReqGetFare reqFarePNR(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation  = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode    = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator    = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sFareType   = GnrcParser.getSubstring(aInputString,18,19).trim().toUpperCase();
    final String sStoreFlag  = GnrcParser.getSubstring(aInputString,19,20).trim().toUpperCase();
    final String sSegments   = GnrcParser.getSubstring(aInputString,20,60);
    final String sPsgrIDs    = GnrcParser.getSubstring(aInputString,60,460);
    final String sPTCs       = GnrcParser.getSubstring(aInputString,460,520).toUpperCase();
		final String sWTParms;
		// withhold tax parameters in TTP command 10 2 char fields
		if ( aInputString.length() > 520)
		  sWTParms    = GnrcParser.getSubstring(aInputString, 520, 540);
		else
		  sWTParms = "";
		System.out.println("NativeAsciiReader.reqFarePNR:");

     // check fields
    if ( sOperation.equals(GnrcConvControl.GET_FARE_CMD) == false )
      throw new TranServerException("Invalid operation code for Get Fare request, received: " + sOperation);

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code for Get Fare request");

    /*
    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator for Get Fare request");
    */
    // create the passenger ID array
    final int PSGR_ID_SIZE = 20;
    final String[] PsgrIDList = stringToStrArray(sPsgrIDs,PSGR_ID_SIZE);

    // create the PTC array
    final int PTC_SIZE = 3;
    final String[] PTCList = stringToStrArray(sPTCs,PTC_SIZE);

    // the number of PTCs must match the number of passengers

    // create the segment number array
    final int SEG_NUM_SIZE = 2;
    final int[] SegmentList = stringToIntArray(sSegments,SEG_NUM_SIZE);

		// create the withhold tax parameters array
    final int PARMS_SIZE = 2;
    final String[] WTParmsList = stringToStrArray(sWTParms,PARMS_SIZE);

    // create request object
    final ReqGetFare request = new ReqGetFare(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;

    // set the fare type
    if ( sFareType.equals("L") )
      request.setFareType(ReqGetFare.FARE_LOWEST);
    else if ( sFareType.equals("C") )
      request.setFareType(ReqGetFare.FARE_CONTRACT);
		else if ( sFareType.equals("K") )
      request.setFareType(ReqGetFare.FARE_ALT_CONTRACT);
    else if ( sFareType.equals("T") )
      request.setFareType(ReqGetFare.FARE_EXEMPT);
    else if ( sFareType.equals("S") )
      request.setFareType(ReqGetFare.FARE_STORED);
    else
      request.setFareType(ReqGetFare.FARE_REGULAR);

    // set the store fares flag
    if ( sStoreFlag.equals("T") || sStoreFlag.equals("Y") )
      request.StoreFares = true;
    else
      request.StoreFares = false;

    // set segment specifiers
    for ( int i = 0; i < SegmentList.length; i++ )
      {
      if ( SegmentList[i] > 0 )
        request.addSegment(SegmentList[i]);
      }

    // set name specifiers
    for ( int i = 0; i < PsgrIDList.length; i++ )
      {
      if ( PsgrIDList[i].length() > 0 )
        {
        String sPID = PsgrIDList[i];
        request.addName(PsgrIDList[i],PTCList[i]);
        }
      }

		// set withholding tax parameters
		if ( WTParmsList instanceof String[] )
		{
		  for ( int i = 0; i < WTParmsList.length; i++ )
        {
			  System.out.println("NativeAsciiReader.reqFarePNR: loop: <" + WTParmsList[i] + ">");
        if ( WTParmsList[i].length() > 0 )
          request.add_WT_Parm(WTParmsList[i]);
        }
		}

		System.out.println("NativeAsciiReader.reqFarePNR: END");

    return(request);
    }

  /**
   ***********************************************************************
   * Issue ticket
   ***********************************************************************
   */
  private static ReqIssueTicket reqIssueTicket(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode     = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator     = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sFareType    = GnrcParser.getSubstring(aInputString,18,19).trim().toUpperCase();
    final String sCommPercent = GnrcParser.getSubstring(aInputString,19,24).trim().toUpperCase();
    final String sCommAmount  = GnrcParser.getSubstring(aInputString,24,29).trim().toUpperCase();
    final String sValCarr     = GnrcParser.getSubstring(aInputString,29,31).trim().toUpperCase();
    final String sSegments    = GnrcParser.getSubstring(aInputString,31,71);
    final String sPsgrIDs     = GnrcParser.getSubstring(aInputString,71,471);
    final String sPTCs        = GnrcParser.getSubstring(aInputString,471,531).toUpperCase();
    final String sTourCode    = GnrcParser.getSubstring(aInputString,531,657).trim().toUpperCase();
    final String sEndorsement = GnrcParser.getSubstring(aInputString,657,783).trim().toUpperCase();
    final String sFop         = GnrcParser.getSubstring(aInputString,783,909).trim().toUpperCase();

    final String sMiniItinFlag     = GnrcParser.getSubstring(aInputString,909,910).trim().toUpperCase();
    final String sETicketFlag      = GnrcParser.getSubstring(aInputString,910,911).trim().toUpperCase();
    final String sInvoiceFlag      = GnrcParser.getSubstring(aInputString,911,912).trim().toUpperCase();
    final String sPsgrDocPrinter   = GnrcParser.getSubstring(aInputString,912,932).trim().toUpperCase();
    final String sAgencyDocPrinter = GnrcParser.getSubstring(aInputString,932,952).trim().toUpperCase();

		System.out.println("NativeAsciiReader.reqIssueTicket: aInputString.length: " + aInputString.length());
		final String sWTParms;
		// withhold tax parameters in TTP command 10 2 char fields
		if ( aInputString.length() > 952)
			sWTParms       = GnrcParser.getSubstring(aInputString, 952, 972);
		else
		  sWTParms = "";

     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_TKT_PNR_CMD) == false )
      throw new TranServerException("Invalid operation code for Issue Ticket request, received: " + sOperation);

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code for Issue Ticket request");

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator for Issue Ticket request");


    // create the passenger ID array
    final int PSGR_ID_SIZE = 20;
    final String[] PsgrIDList = stringToStrArray(sPsgrIDs,PSGR_ID_SIZE);

    // create the PTC array
    final int PTC_SIZE = 3;
    final String[] PTCList = stringToStrArray(sPTCs,PTC_SIZE);

    // create the segment number array
    final int SEG_NUM_SIZE = 2;
    final int[] SegmentList = stringToIntArray(sSegments,SEG_NUM_SIZE);

		// create the withhold tax parameters array
    final int PARMS_SIZE = 2;
    final String[] WTParmsList = stringToStrArray(sWTParms,PARMS_SIZE);

    // create the request object
    final ReqIssueTicket request = new ReqIssueTicket(sCrsCode,sLocator);

    if ( sFareType.equals("L") )
      request.setFareType(ReqIssueTicket.FARE_LOWEST);
    else if ( sFareType.equals("C") )
      request.setFareType(ReqIssueTicket.FARE_CONTRACT);
		else if ( sFareType.equals("K") )
      request.setFareType(ReqIssueTicket.FARE_ALT_CONTRACT);
    else if ( sFareType.equals("T") )
      request.setFareType(ReqIssueTicket.FARE_EXEMPT);
    else if ( sFareType.equals("S") )
      request.setFareType(ReqIssueTicket.FARE_STORED);
    else
      request.setFareType(ReqIssueTicket.FARE_REGULAR);


    // set the validating carrier
    request.ValidatingCarrier = sValCarr;
    request.TourCode          = sTourCode;
    request.EndorsementInfo   = sEndorsement;
    request.FOP               = sFop;

    request.AgencyDocPrinter    = sAgencyDocPrinter;
    request.PassengerDocPrinter = sPsgrDocPrinter;

    // set printer flags
    if  ( sMiniItinFlag.equals("T") || sMiniItinFlag.equals("Y") )
      request.printMiniItin = true;
    else
      request.printMiniItin = false;

    /* ETicketFlg changed from true/false to 'E'/'P'/Null
    if  ( sETicketFlag.equals("T") || sETicketFlag.equals("Y") )
      request.printETicket = true;
    else
      request.printETicket = false;
    */
    request.TicketType = sETicketFlag;
		//System.out.println("NativeAsciiReader.reqIssueTicket: sETicketFlag: " + sETicketFlag);

    if  ( sInvoiceFlag.equals("T") || sInvoiceFlag.equals("Y") )
      request.printInvoice = true;
    else
      request.printInvoice = false;


    // set the commission
    if ( sCommAmount.length() > 0 )
      request.CommissionAmount = Float.parseFloat(sCommAmount)/100;
    if ( sCommPercent.length() > 0 )
      request.CommissionPercent = Float.parseFloat(sCommPercent)/100;

    // set segment specifiers
    for ( int i = 0; i < SegmentList.length; i++ )
      {
      if ( SegmentList[i] > 0 )
        request.addSegment(SegmentList[i]);
      }

    // set name specifiers
    for ( int i = 0; i < PsgrIDList.length; i++ )
      {
      if ( PsgrIDList[i].length() > 0 )
			  {
        request.addName(PsgrIDList[i],PTCList[i]);
				request.addRawPsgrId(PsgrIDList[i]);  // add raw psgr_id, inf
				}
			}

		if ( WTParmsList instanceof String[] )
		{
			System.out.println("NativeAsciiReader.reqIssueTicket: " + WTParmsList.length);
			// set withholding tax parameters
      for ( int i = 0; i < WTParmsList.length; i++ )
        {
        if ( WTParmsList[i].length() > 0 )
          request.add_WT_Parm(WTParmsList[i]);
        }
		}
		//System.out.println("NativeAsciiReader.reqIssueTicket:END ");
		return(request);
    }

  
  /**
   ***********************************************************************
   * Get Ticket information
   ***********************************************************************
   */
  private static ReqGetTicketInfo reqGetTicketInfo(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sLocator     = GnrcParser.getSubstring(aInputString,8,16).trim().toUpperCase();
    final String sPsgrIDs     = GnrcParser.getSubstring(aInputString,16,416);
    final String sCrsCode     = "1A";    // TODO this should come over as part of the interface

	System.out.println("NativeAsciiReader.reqIssueTicket: aInputString.length: " + aInputString.length());

     // check fields
    if ( sOperation.equals(GnrcConvControl.AWR_TKT_INFO_CMD) == false )
      throw new TranServerException("Invalid operation code for Get ticket info request, received: " + sOperation);

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator for get ticket info request");


    // create the passenger ID array
    final int PSGR_ID_SIZE = 20;
    final String[] PsgrIDList = stringToStrArray(sPsgrIDs,PSGR_ID_SIZE);


    // create the request object
    final ReqGetTicketInfo request = new ReqGetTicketInfo(sCrsCode, sLocator);
 
    // set name specifiers
    for ( int i = 0; i < PsgrIDList.length; i++ )
    {
    	final String sPsgrID = PsgrIDList[i].trim();
    	if ( sPsgrID.length() > 0 )
    	{
    		request.getPassengerIDs().add(sPsgrID);
		}
    }
  
    
	return(request);
    }
  
  
  /**
   ***********************************************************************
   * Get list of branch offices
   ***********************************************************************
   */
  private static ReqListBranches reqListBranches(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation  = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode    = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sPseudoCity = GnrcParser.getSubstring(aInputString,10,20).trim().toUpperCase();

     // check fields
    if ( sOperation.equals(GnrcConvControl.LIST_BRANCH_CMD) == false )
      throw new TranServerException("Invalid operation code for list branch offices, received: " + sOperation + "  expected: " + GnrcConvControl.LIST_BRANCH_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to get a list of branch offices");

    if ( GnrcFormat.IsNull(sPseudoCity) )
      throw new TranServerException("Must specify a pseudo city to get a list of branch offices");

    // create request object
    final ReqListBranches request = new ReqListBranches(sCrsCode,sPseudoCity);

    return(request);
    }

  /**
   ***********************************************************************
   * Get list of group profiles
   ***********************************************************************
   */
  private static ReqListGroupProfiles reqListGroupProfiles(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation  = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode    = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sPseudoCity = GnrcParser.getSubstring(aInputString,10,20).trim().toUpperCase();

     // check fields
    if ( sOperation.equals(GnrcConvControl.LIST_GROUP_PROF_CMD) == false )
      throw new TranServerException("Invalid operation code for list group profiles, received: " + sOperation + "  expected: " + GnrcConvControl.LIST_GROUP_PROF_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to get a list of group profiles");

    if ( GnrcFormat.IsNull(sPseudoCity) )
      throw new TranServerException("Must specify a pseudo city to get a list of group profiles");

    // create request object
    final ReqListGroupProfiles request = new ReqListGroupProfiles(sCrsCode,sPseudoCity);

    return(request);
    }

  /**
   ***********************************************************************
   * Get list of personal profiles
   ***********************************************************************
   */
  private static ReqListPersonalProfiles reqListPersonalProfiles(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation  = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode    = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sPseudoCity = GnrcParser.getSubstring(aInputString,10,20).trim().toUpperCase();
    final String sGroupName  = GnrcParser.getSubstring(aInputString,20,50).trim().toUpperCase();

     // check fields
    if ( sOperation.equals(GnrcConvControl.LIST_PER_PROF_CMD) == false )
      throw new TranServerException("Invalid operation code for list personal profiles, received: " + sOperation + "  expected: " + GnrcConvControl.LIST_PER_PROF_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to get a list of personal profiles");

    if ( GnrcFormat.IsNull(sPseudoCity) )
      throw new TranServerException("Must specify a pseudo city to get a list of personal profiles");

    // create request object
    final ReqListPersonalProfiles request = new ReqListPersonalProfiles(sCrsCode,sPseudoCity,sGroupName);

    return(request);
    }

  /**
   ***********************************************************************
   * Get a profile
   ***********************************************************************
   */
  private static ReqGetProfile reqGetProfile(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation    = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode      = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sPseudoCity   = GnrcParser.getSubstring(aInputString,10,20).trim().toUpperCase();
    final String sGroupName    = GnrcParser.getSubstring(aInputString,20,50).trim().toUpperCase();
    final String sTravelerName = GnrcParser.getSubstring(aInputString,50,80).trim().toUpperCase();
    final String sHeaderOnly   = GnrcParser.getSubstring(aInputString,80,81).trim().toUpperCase();

     // check fields
    if ( sOperation.equals(GnrcConvControl.GET_PER_PROF_CMD) == false )
      throw new TranServerException("Invalid operation code to get profile, received: " + sOperation + "  expected: " + GnrcConvControl.GET_PER_PROF_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to get a profile");

    if ( GnrcFormat.IsNull(sPseudoCity) )
      throw new TranServerException("Must specify a pseudo city to get a profile");

    if ( GnrcFormat.IsNull(sGroupName) && GnrcFormat.IsNull(sTravelerName) )
      throw new TranServerException("Must specify either a group or traveler name to get a profile");

    // create request object
    final ReqGetProfile request = new ReqGetProfile(sCrsCode,sPseudoCity,sGroupName,sTravelerName);

    if ( GnrcFormat.NotNull(sHeaderOnly) )
      {
      if ( sHeaderOnly.equals("T") || sHeaderOnly.equals("Y") )
        request.getHeaderInfoOnly = true;
      }

    return(request);
    }

  /**
   ***********************************************************************
   * Build a profile
   ***********************************************************************
   */
  private static ReqBuildProfile reqBuildProfile(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation     = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode       = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sPseudoCity    = GnrcParser.getSubstring(aInputString,10,20).trim().toUpperCase();
    final String sGroupName     = GnrcParser.getSubstring(aInputString,20,50).trim().toUpperCase();
    final String sTravelerName  = GnrcParser.getSubstring(aInputString,50,80).trim().toUpperCase();
    final String sCaption       = GnrcParser.getSubstring(aInputString,80,130).trim().toUpperCase();
    final String sDescription   = GnrcParser.getSubstring(aInputString,130,180).trim().toUpperCase();
    final String sThresholdDate = GnrcParser.getSubstring(aInputString,180,188).trim().toUpperCase();
    final String sForce         = GnrcParser.getSubstring(aInputString,188,189).trim().toUpperCase();

     // check fields
    if ( sOperation.equals(GnrcConvControl.SET_PER_PROF_CMD) == false )
      throw new TranServerException("Invalid operation code to build a profile, received: " + sOperation + "  expected: " + GnrcConvControl.SET_PER_PROF_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to build a profile");

    if ( GnrcFormat.IsNull(sPseudoCity) )
      throw new TranServerException("Must specify a pseudo city to build a profile");

    if ( GnrcFormat.IsNull(sGroupName) && GnrcFormat.IsNull(sTravelerName) )
      throw new TranServerException("Must specify either a group or traveler name to build a profile");


    final Profile profile = new Profile(sCrsCode,sPseudoCity,sGroupName,sTravelerName);
    profile.setCaption(sCaption);
    profile.setDescription(sDescription);

    final int ELEMENT_WIDTH = 143;
    String sProfileElement;
    String sType;
    String sLineNum;
    String sMoveFlag;
    String sQualifier;
    String sText;
    int iUsage;
    int iLineNumber;
    for ( int iPos = 189; iPos < aInputString.length(); iPos += ELEMENT_WIDTH )
      {
      sProfileElement = GnrcParser.getSubstring(aInputString,iPos,iPos + ELEMENT_WIDTH);
      if ( GnrcFormat.NotNull(sProfileElement) )
        {
        sType      = GnrcParser.getSubstring(sProfileElement,0,10).trim().toUpperCase();
        sLineNum   = GnrcParser.getSubstring(sProfileElement,10,13).trim().toUpperCase();
        sMoveFlag  = GnrcParser.getSubstring(sProfileElement,13,14).trim().toUpperCase();
        sQualifier = GnrcParser.getSubstring(sProfileElement,14,15).trim().toUpperCase();
        sText      = GnrcParser.getSubstring(sProfileElement,15,143).trim().toUpperCase();

        // set the line number
        iLineNumber = Integer.parseInt(sLineNum);

        // set the move type
        if ( sMoveFlag.equals("A") )
          iUsage = ProfileElement.ALWAYS_MOVE;
        else if ( sMoveFlag.equals("N") )
          iUsage = ProfileElement.NEVER_MOVE;
        else
          iUsage = ProfileElement.OPTIONAL_MOVE;

        // set the element type
        if ( sType.equals("TEXT") == false )
          throw new TranServerException("Build profile error: invalid profile element type " + sType);

        profile.addElement(sText,iUsage,iLineNumber,sQualifier);
        }
      }

    // create request object
    final ReqBuildProfile request = new ReqBuildProfile(sCrsCode,profile);

    // convert threshold date string from YYYY/MM/DD format to long int
    try
      {
      if ( GnrcFormat.NotNull(sThresholdDate) )
        request.thresholdDate = GnrcFormat.ScanLongDate(sThresholdDate);
      else
        request.thresholdDate = 0;
      }
    catch (Exception e)
      {
      throw new TranServerException("Build profile error: unable to scan threshold date string " + sThresholdDate);
      }

    // set the force flag
    if ( sForce instanceof String )
      {
      if ( sForce.equals("T") || sForce.equals("Y") )
        request.forceChange = true;
      }

    return(request);
    }

  /**
   ***********************************************************************
   * Split PNR
   ***********************************************************************
   */
  private static ReqSplitPNR reqSplitPnr(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation     = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode       = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator       = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sNumUnassigned = GnrcParser.getSubstring(aInputString,18,21).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.SPLIT_PNR_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for split PNR, received: " + sOperation +
          "  expected: " + GnrcConvControl.SPLIT_PNR_CMD );

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to split PNR");

    final ReqSplitPNR request = new ReqSplitPNR(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;

    try
      {
      if (sNumUnassigned.length() > 0)
        request.NumUnassigned = Integer.parseInt(sNumUnassigned);
      else
        request.NumUnassigned = 0;
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid number of unassigned seats to be split " + sNumUnassigned);
      }

    // add names to be split
    String sPsgrID;
    for ( int i = 21; i < aInputString.length(); i += 20 )
      {
      sPsgrID = GnrcParser.getSubstring(aInputString,i,i + 20).trim().toUpperCase();
      if ( GnrcFormat.NotNull(sPsgrID) )
        request.addName(sPsgrID);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * Cancel air segments
   ***********************************************************************
   */
  private static ReqCxlAirSegment reqCxlAirSegment(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sReceiveBy = GnrcParser.getSubstring(aInputString,18,38).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.CXL_SEG_CMD) == false )
      throw new TranServerException("Invalid operation code for cancelling air segments, received: " + sOperation + "  expected: " + GnrcConvControl.CXL_SEG_CMD );

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to cancel air segments");

    final ReqCxlAirSegment request = new ReqCxlAirSegment(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;


    // add the air segments to be canceled
    String sCarrier;
    int iFlightNum;
    String sFlightNum;
    String sDepCity;
    String sArrCity;
    String sDepDate;
    long iDepDate;
    for ( int i = 38; i < aInputString.length(); i += 21 )
      {
      sCarrier   = GnrcParser.getSubstring(aInputString,i + 0,i + 2).trim().toUpperCase();
      sFlightNum = GnrcParser.getSubstring(aInputString,i + 2,i + 7).trim().toUpperCase();
      sDepCity   = GnrcParser.getSubstring(aInputString,i + 7,i + 10).trim().toUpperCase();
      sArrCity   = GnrcParser.getSubstring(aInputString,i + 10,i + 13).trim().toUpperCase();
      sDepDate   = GnrcParser.getSubstring(aInputString,i + 13,i + 21).trim().toUpperCase();

      try
        {
        iFlightNum = Integer.parseInt(sFlightNum);
        }
      catch (Exception e)
        {
        throw new TranServerException("Invalid flight number" + sFlightNum);
        }

      // convert the date strings
      final String sCrsDepDate;
      try
        {
        iDepDate = GnrcFormat.ScanLongDate(sDepDate);
        }
      catch (Exception e)
        {
        throw new TranServerException("Invalid departure date format: " + sDepDate);
        }

      request.addAirSegment(sCarrier,iFlightNum,sDepCity,sArrCity,iDepDate);
      }

    return(request);
    }

  /**
   ***********************************************************************
   * Cancel Entire itinerary
   ***********************************************************************
   */
  private static ReqCxlItinerary reqCxlItinerary(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    // parse up the request
    final String sOperation = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sLocator   = GnrcParser.getSubstring(aInputString,10,18).trim().toUpperCase();
    final String sReceiveBy = GnrcParser.getSubstring(aInputString,18,38).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.CXL_ITIN_CMD) == false )
      throw new TranServerException("Invalid operation code for cancel itinerary, received: " + sOperation + "  expected: " + GnrcConvControl.CXL_ITIN_CMD );

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a locator to cancel an itinerary");

    final ReqCxlItinerary request = new ReqCxlItinerary(sCrsCode,sLocator);
    request.commType = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;

    return(request);
    }

  /**
   ***********************************************************************
   * Create a block: temporary new implementation that will soon replace
   * reqBlockBuild
   ***********************************************************************
   */
  /*
    private static ReqBlockBuild reqBlockBuild(final String aInputString) throws Exception
      {
      // parse up the request
      int i=0;
      final String sOperation  = GnrcParser.getSubstring(aInputString,i,i+= 8);
      final String sCrsCode    = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
      final String sReceiveBy  = GnrcParser.getSubstring(aInputString,i,i+=20).trim().toUpperCase();
      final String sTourRef    = GnrcParser.getSubstring(aInputString,i,i+=40).trim().toUpperCase();
      final String sActiveFlag = GnrcParser.getSubstring(aInputString,i,i+= 1).trim().toUpperCase();
      final String sStartDate  = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();
      final String sStopDate   = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();

      // check fields
      if ( sOperation.equals(GnrcConvControl.ADD_BLK_CMD) == false )
        throw new TranServerException(
            "Invalid operation code for creating block, received: " + sOperation +
            "  expected: " + GnrcConvControl.ADD_BLK_CMD );

      if ( GnrcFormat.IsNull(sCrsCode) )
        throw new TranServerException("Must specify a CRS code to create a block");

      final ReqBlockBuild request = new ReqBlockBuild(sCrsCode);
      request.commType = request.COMM_NATIVE_ASCII;
      //request.RequestedBy = sReceiveBy;

      // populate the header information for the block
      // request.getBlock().setMemo(sMemo);
      request.getBlock().setTourReference(sTourRef);

      if (!sStartDate.equals(""))
        {
        try {
          long iStartDate = GnrcFormat.ScanLongDate(sStartDate);
          request.getBlock().setStartSellDate(iStartDate);
          }
        catch (Exception e) {
          throw new TranServerException("Invalid start sell date format: " + sStartDate);
          }
        }

      if (!sStopDate.equals(""))
        {
        try {
          long iStopDate = GnrcFormat.ScanLongDate(sStopDate);
          request.getBlock().setStopSellDate(iStopDate);
          }
        catch (Exception e) {
          throw new TranServerException("Invalid stop sell date format: " + sStopDate);
          }
        }

      if ( sActiveFlag.equals("T") || sActiveFlag.equals("Y") || sActiveFlag.equals("A") )
        request.getBlock().setActive(true);
      else
        request.getBlock().setActive(false);


      // add the air segments to be added to the block
      int iFlightNum, iNumSeats;
      String sCarrier, sFlightNum, sDepCity, sArrCity, sDepDate, sArrDate;
      String sClassOfService, sActionCode, sNumSeats, sRmtCrsCode, sRmtLocator, sIsScheduled;
      long iDepDate, iArrDate;

      while ( i < aInputString.length() )
        {
        sCarrier    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
        sFlightNum  = GnrcParser.getSubstring(aInputString,i,i+= 5).trim().toUpperCase();
        sDepCity    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
        sArrCity    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
        sDepDate    = GnrcParser.getSubstring(aInputString,i,i+=12).trim().toUpperCase();
        sArrDate    = GnrcParser.getSubstring(aInputString,i,i+=12).trim().toUpperCase();
        sClassOfService    = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
        sActionCode = GnrcParser.getSubstring(aInputString,i,i+= 4).trim().toUpperCase();
        sNumSeats   = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
        sRmtCrsCode = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
        sRmtLocator = GnrcParser.getSubstring(aInputString,i,i+= 8).trim().toUpperCase();
        i+=3; // ignore Block Type field - part of segment info type
        i+=1; // ignore Active/Passive flag - part of segment info type
        sIsScheduled = GnrcParser.getSubstring(aInputString,i,i+= 1).trim().toUpperCase();

        try {
          iFlightNum = Integer.parseInt(sFlightNum);
          }
        catch (Exception e) {
          throw new TranServerException("Invalid flight number " + sFlightNum);
          }

        try {
          iNumSeats = Integer.parseInt(sNumSeats);
          }
        catch (Exception e) {
          throw new TranServerException("Invalid number of seats " + sNumSeats);
          }

        // convert the date strings
        try {
          iDepDate = GnrcFormat.ScanLongDateTime(sDepDate);
          }
        catch (Exception e) {
          throw new TranServerException("Invalid departure date format: " + sDepDate);
          }

        try {
          iArrDate = GnrcFormat.ScanLongDateTime(sArrDate);
          }
        catch (Exception e) {
          throw new TranServerException("Invalid arrival date format: " + sArrDate);
          }

        boolean isScheduled = Boolean.valueOf(sIsScheduled).booleanValue();

        request.addFlight(sCarrier,iFlightNum,sDepCity,sArrCity,iDepDate,
                          iArrDate,sClassOfService,iNumSeats,sActionCode,
                          sRmtCrsCode,sRmtLocator,isScheduled);

        } // end while

      return(request);

      } // end reqBlockBuild
  */

  /**
   ***********************************************************************
   * Create a managed block
   ***********************************************************************
   */
  private static ReqBlockBuild reqBlockBuild(final String aInputString) throws Exception
    {
    // parse up the request
    int i=0;
    final String sOperation  = GnrcParser.getSubstring(aInputString,i,i+= 8);
    final String sCrsCode    = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
    final String sReceiveBy  = GnrcParser.getSubstring(aInputString,i,i+=20).trim().toUpperCase();
    final String sTourName   = GnrcParser.getSubstring(aInputString,i,i+=15).trim().toUpperCase();
    // final String sMemo       = GnrcParser.getSubstring(aInputString,i,i+=40).trim().toUpperCase();
    final String sTourRef    = GnrcParser.getSubstring(aInputString,i,i+=40).trim().toUpperCase();
    final String sActiveFlag = GnrcParser.getSubstring(aInputString,i,i+= 1).trim().toUpperCase();
    final String sStartDate  = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();
    final String sStopDate   = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();
    final String sAuthCode   = GnrcParser.getSubstring(aInputString,i,i+= 6).trim().toUpperCase();
    final String sReduceDate = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();
    final String sReducePrcnt = GnrcParser.getSubstring(aInputString,i,i+= 3).trim();

    // check fields
    if ( sOperation.equals(GnrcConvControl.ADD_BLK_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for creating block, received: " + sOperation +
          "  expected: " + GnrcConvControl.ADD_BLK_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to create a block");

    final ReqBlockBuild request = new ReqBlockBuild(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;
    //request.RequestedBy = sReceiveBy;

    // populate the header information for the block
    // request.getBlock().setMemo(sMemo);
    request.getBlock().setTourName(sTourName);
    request.getBlock().setTourReference(sTourRef);
    request.getBlock().setAuthCode(sAuthCode);

    if (!sReducePrcnt.equals(""))
      request.getBlock().setReductionPercent(Byte.parseByte(sReducePrcnt));

    if (!sStartDate.equals(""))
      {
      try {
        long iStartDate = GnrcFormat.ScanLongDate(sStartDate);
        request.getBlock().setStartSellDate(iStartDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid start sell date format: " + sStartDate);
        }
      }

    if (!sStopDate.equals(""))
      {
      try {
        long iStopDate = GnrcFormat.ScanLongDate(sStopDate);
        request.getBlock().setStopSellDate(iStopDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid stop sell date format: " + sStopDate);
        }
      }

    if (!sReduceDate.equals(""))
      {
      try {
        long iReduceDate = GnrcFormat.ScanLongDate(sReduceDate);
        request.getBlock().setReductionDate(iReduceDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid reduce date format: " + sReduceDate);
        }
      }

    if ( sActiveFlag.equals("T") || sActiveFlag.equals("Y") || sActiveFlag.equals("A") )
      request.getBlock().setActive(true);
    else
      request.getBlock().setActive(false);


    // add the air segments to be added to the block
    int iFlightNum, iNumSeats;
    String sCarrier, sFlightNum, sDepCity, sArrCity, sDepDate, sArrDate;
    String sClassOfService, sActionCode, sNumSeats, sRmtCrsCode, sRmtLocator, sIsScheduled;
    long iDepDate, iArrDate;

    while ( i < aInputString.length() )
      {
      sCarrier    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sFlightNum  = GnrcParser.getSubstring(aInputString,i,i+= 5).trim().toUpperCase();
      sDepCity    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sArrCity    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sDepDate    = GnrcParser.getSubstring(aInputString,i,i+=12).trim().toUpperCase();
      sArrDate    = GnrcParser.getSubstring(aInputString,i,i+=12).trim().toUpperCase();
      sClassOfService    = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
      sActionCode = GnrcParser.getSubstring(aInputString,i,i+= 4).trim().toUpperCase();
      sNumSeats   = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sRmtCrsCode = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
      sRmtLocator = GnrcParser.getSubstring(aInputString,i,i+= 8).trim().toUpperCase();
      i+=3; // ignore Block Type field - part of segment info type
      i+=1; // ignore Active/Passive flag - part of segment info type
      sIsScheduled = GnrcParser.getSubstring(aInputString,i,i+= 1).trim().toUpperCase();

      try {
        iFlightNum = Integer.parseInt(sFlightNum);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid flight number " + sFlightNum);
        }

      try {
        iNumSeats = Integer.parseInt(sNumSeats);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid number of seats " + sNumSeats);
        }

      // convert the date strings
      try {
        iDepDate = GnrcFormat.ScanLongDateTime(sDepDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid departure date format: " + sDepDate);
        }

      try {
        iArrDate = GnrcFormat.ScanLongDateTime(sArrDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid arrival date format: " + sArrDate);
        }

      boolean isScheduled = Boolean.valueOf(sIsScheduled).booleanValue();

      request.addFlight(sCarrier,iFlightNum,sDepCity,sArrCity,iDepDate,
                        iArrDate,sClassOfService,iNumSeats,sActionCode,
                        sRmtCrsCode,sRmtLocator,isScheduled);

      } // end while

    return(request);

    } // end reqBlockBuild

	/**
   ***********************************************************************
   * Create a managed block 3 (third structure)
   ***********************************************************************
   */
  private static ReqBlockBuild reqBlockBuild3(final String aInputString) throws Exception
    {
    // parse up the request
    int i=0;
    final String sOperation  = GnrcParser.getSubstring(aInputString,i,i+= 8);
    final String sCrsCode    = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
    final String sReceiveBy  = GnrcParser.getSubstring(aInputString,i,i+=20).trim().toUpperCase();
    final String sTourName   = GnrcParser.getSubstring(aInputString,i,i+=15).trim().toUpperCase();
		final String sTourRef    = GnrcParser.getSubstring(aInputString,i,i+=40).trim().toUpperCase();
    final String sActiveFlag = GnrcParser.getSubstring(aInputString,i,i+= 1).trim().toUpperCase();
    final String sStartDate  = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();
    final String sStopDate   = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();
    final String sAuthCode   = GnrcParser.getSubstring(aInputString,i,i+= 6).trim().toUpperCase();
    final String sReduceDate = GnrcParser.getSubstring(aInputString,i,i+= 8).trim();
    final String sReducePrcnt = GnrcParser.getSubstring(aInputString,i,i+= 3).trim();
		// New for third structure
		final String sOwner_Id = GnrcParser.getSubstring(aInputString,i,i+= 20).trim();
		final String sHandling_Tbl = GnrcParser.getSubstring(aInputString,i,i+= 20).trim();
		final String sBlk_Flgts = GnrcParser.getSubstring(aInputString,i,i+= 620).trim();

		// check fields
    if ( sOperation.equals(GnrcConvControl.ADD_BLK3_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for creating block, received: " + sOperation +
          "  expected: " + GnrcConvControl.ADD_BLK3_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to create a block");

    final ReqBlockBuild request = new ReqBlockBuild(sCrsCode);
    request.commType = request.COMM_NATIVE_ASCII;
    //request.RequestedBy = sReceiveBy;

    // populate the header information for the block
    // request.getBlock().setMemo(sMemo);
    request.getBlock().setTourName(sTourName);
    request.getBlock().setTourReference(sTourRef);
    request.getBlock().setAuthCode(sAuthCode);

		// populate block structure 3
		request.getBlock().setOwnerID(sOwner_Id);
		request.getBlock().setHandlingTable(sHandling_Tbl);
		System.out.println("reqBlockBuild3: SOwner_Id/sHandling_Tbl: " + sOwner_Id + " / " + sHandling_Tbl);

    if (!sReducePrcnt.equals(""))
      request.getBlock().setReductionPercent(Byte.parseByte(sReducePrcnt));

    if (!sStartDate.equals(""))
      {
      try {
        long iStartDate = GnrcFormat.ScanLongDate(sStartDate);
        request.getBlock().setStartSellDate(iStartDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid start sell date format: " + sStartDate);
        }
      }

    if (!sStopDate.equals(""))
      {
      try {
        long iStopDate = GnrcFormat.ScanLongDate(sStopDate);
        request.getBlock().setStopSellDate(iStopDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid stop sell date format: " + sStopDate);
        }
      }

    if (!sReduceDate.equals(""))
      {
      try {
        long iReduceDate = GnrcFormat.ScanLongDate(sReduceDate);
        request.getBlock().setReductionDate(iReduceDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid reduce date format: " + sReduceDate);
        }
      }

    if ( sActiveFlag.equals("T") || sActiveFlag.equals("Y") || sActiveFlag.equals("A") )
      request.getBlock().setActive(true);
    else
      request.getBlock().setActive(false);


    // add the air segments to be added to the block
    int iFlightNum, iNumSeats;
    String sCarrier, sFlightNum, sDepCity, sArrCity, sDepDate, sArrDate;
    String sClassOfService, sActionCode, sNumSeats, sRmtCrsCode, sRmtLocator, sIsScheduled;
    long iDepDate, iArrDate;

		i-= 620;
		while ( i < aInputString.length() )
      {
      sCarrier    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sFlightNum  = GnrcParser.getSubstring(aInputString,i,i+= 5).trim().toUpperCase();
      sDepCity    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sArrCity    = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sDepDate    = GnrcParser.getSubstring(aInputString,i,i+=12).trim().toUpperCase();
      sArrDate    = GnrcParser.getSubstring(aInputString,i,i+=12).trim().toUpperCase();
      sClassOfService    = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
      sActionCode = GnrcParser.getSubstring(aInputString,i,i+= 4).trim().toUpperCase();
      sNumSeats   = GnrcParser.getSubstring(aInputString,i,i+= 3).trim().toUpperCase();
      sRmtCrsCode = GnrcParser.getSubstring(aInputString,i,i+= 2).trim().toUpperCase();
      sRmtLocator = GnrcParser.getSubstring(aInputString,i,i+= 8).trim().toUpperCase();
      i+=3; // ignore Block Type field - part of segment info type
      i+=1; // ignore Active/Passive flag - part of segment info type
      sIsScheduled = GnrcParser.getSubstring(aInputString,i,i+= 1).trim().toUpperCase();

      try {
        iFlightNum = Integer.parseInt(sFlightNum);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid flight number " + sFlightNum);
        }

      try {
        iNumSeats = Integer.parseInt(sNumSeats);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid number of seats " + sNumSeats);
        }

      // convert the date strings
      try {
        iDepDate = GnrcFormat.ScanLongDateTime(sDepDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid departure date format: " + sDepDate);
        }

      try {
        iArrDate = GnrcFormat.ScanLongDateTime(sArrDate);
        }
      catch (Exception e) {
        throw new TranServerException("Invalid arrival date format: " + sArrDate);
        }

      boolean isScheduled = Boolean.valueOf(sIsScheduled).booleanValue();

      request.addFlight(sCarrier,iFlightNum,sDepCity,sArrCity,iDepDate,
                        iArrDate,sClassOfService,iNumSeats,sActionCode,
                        sRmtCrsCode,sRmtLocator,isScheduled);

			System.out.println("reqBlockBuild3: sCarrier/sFlightNum: " + sCarrier + " / " + sFlightNum);
			} // end while

    return(request);

    } // end reqBlockBuild3

	/**
   ***********************************************************************
   * Modify a block
   ***********************************************************************
   */
  private static ReqBlockModify reqBlockModify(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation     = GnrcParser.getSubstring(aInputString, 0, 8);
    final String sCrsCode       = GnrcParser.getSubstring(aInputString, 8,10).trim().toUpperCase();
    final String sCarrierCode   = GnrcParser.getSubstring(aInputString,10,12).trim().toUpperCase();
    final String sLocator       = GnrcParser.getSubstring(aInputString,12,20).trim().toUpperCase();
    final String sNumAllocated  = GnrcParser.getSubstring(aInputString,20,23).trim().toUpperCase();
    final String sReceiveBy     = GnrcParser.getSubstring(aInputString,23,43).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.MOD_BLK_CMD) == false )
      throw new TranServerException("Invalid operation code for modifying a block, received: " + sOperation + "  expected: " + GnrcConvControl.MOD_BLK_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to modify a block");

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a record locator to modify a block");

    if ( GnrcFormat.IsNull(sCarrierCode) )
        throw new TranServerException("Must specify a carrier code to modify a block");
    
    if ( GnrcFormat.IsNull(sNumAllocated) )
      throw new TranServerException("Must specify a new number of seats to modify a block");

    final int iNumAllocated;
    try
      {
      iNumAllocated = Integer.parseInt(sNumAllocated);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid seat allocation " + sNumAllocated);
      }

    final ReqBlockModify request = new ReqBlockModify(sCrsCode,sLocator,sCarrierCode,iNumAllocated);
    request.commType = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;

    return(request);

    } // end reqBlockModify


  /**
   ***********************************************************************
   * Delete a block
   ***********************************************************************
   */
  private static ReqBlockDelete reqBlockDelete(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode     = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sCarrierCode = GnrcParser.getSubstring(aInputString,10,12).trim().toUpperCase();
    final String sLocator     = GnrcParser.getSubstring(aInputString,12,20).trim().toUpperCase();
    final String sReceiveBy   = GnrcParser.getSubstring(aInputString,20,40).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.DEL_BLK_CMD) == false )
      throw new TranServerException("Invalid operation code for deleting a block, received: " + sOperation + "  expected: " + GnrcConvControl.DEL_BLK_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to delete a block");

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a record locator to delete a block");

    if ( GnrcFormat.IsNull(sCarrierCode) )
        throw new TranServerException("Must specify a carrier code to delete a block");
    
    final ReqBlockDelete request = new ReqBlockDelete(sCrsCode,sLocator,sCarrierCode);
    request.commType = request.COMM_NATIVE_ASCII;
    request.RequestedBy = sReceiveBy;

    return(request);
    }


  /**
   ***********************************************************************
   * retrieve a block
   ***********************************************************************
   */
  private static ReqBlockRetrieve reqBlockRetrieve(final String aInputString) throws Exception
    {
    // parse up the request
    final String sOperation   = GnrcParser.getSubstring(aInputString,0,8);
    final String sCrsCode     = GnrcParser.getSubstring(aInputString,8,10).trim().toUpperCase();
    final String sCarrierCode = GnrcParser.getSubstring(aInputString,10,12).trim().toUpperCase();
    final String sLocator     = GnrcParser.getSubstring(aInputString,12,20).trim().toUpperCase();

    // check fields
    if ( sOperation.equals(GnrcConvControl.GET_BLK_CMD) == false )
      throw new TranServerException("Invalid operation code for retrieving a block, received: " + sOperation + "  expected: " + GnrcConvControl.GET_BLK_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to retrieve a block");

    if ( GnrcFormat.IsNull(sLocator) )
      throw new TranServerException("Must specify a record locator to retrieve a block");

    if ( GnrcFormat.IsNull(sCarrierCode) )
        throw new TranServerException("Must specify a carrier code to retrieve a block");
    
    final ReqBlockRetrieve request = new ReqBlockRetrieve(sCrsCode,sLocator,sCarrierCode);
    request.commType = request.COMM_NATIVE_ASCII;

    return(request);
    }

  /**
   ***********************************************************************
   * reads block messages from Crs Queue
   ***********************************************************************
   */
  private static ReqBlockReadMessage reqBlockReadMessage(final String aInputString) throws Exception
    {
    // read fields
    int i=0;
    final String sOperation = GnrcParser.getSubstring(aInputString,i,i+= 8);
    final String sCrsCode   = GnrcParser.getSubstring(aInputString,i,i+= 2);
    final String sQueueName = GnrcParser.getSubstring(aInputString,i,i+=10).trim();
    final String sQueueCat  = GnrcParser.getSubstring(aInputString,i,i+=10).trim();
    final String sAction    = GnrcParser.getSubstring(aInputString,i,i+= 1).trim();

    // check fields
    if ( sOperation.equals(GnrcConvControl.READ_BLK_MSG_CMD) == false )
      throw new TranServerException(
          "Invalid operation code for reading block message queue, received: "
          + sOperation + "  expected: " + GnrcConvControl.READ_BLK_MSG_CMD );

    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException(
          "Must specify a CRS code to read block messages from queue");

    if ( GnrcFormat.IsNull(sQueueName) )
      throw new TranServerException(
          "Must specify a queue name to read block messages from queue");

    if ( GnrcFormat.IsNull(sQueueCat) )
      throw new TranServerException(
          "Must specify a queue category to read block messages from queue");

    // create request object
    final ReqBlockReadMessage request = new ReqBlockReadMessage(sCrsCode);

    request.commType  = request.COMM_NATIVE_ASCII;
    request.queueName = sQueueName;
    request.queueCategory = sQueueCat;

    if ( sAction.equals("L") )
      request.leaveMsgInQueue = true;

    return(request);
    }


  /**
   ***********************************************************************
   * Returns an array of strings derived from one long string composed
	 * of ElementWidth spaced sub-strings.
	 *
	 * @param aInputString
	 *   the input string containing the list of sub-strings
	 *
	 * @param aElementWidth
	 *   the number of characters that each sub-string occupies within the string
   ***********************************************************************
   */
  private static String[] stringToStrArray(final String aInputString, final int aElementWidth)
    {
    final Vector stringlist = new Vector();
    String sField;
    for ( int iPos = 0; iPos < aInputString.length(); iPos += aElementWidth )
      {
      sField = GnrcParser.getSubstring(aInputString,iPos,iPos + aElementWidth).trim();
      stringlist.add(sField);
      }

    // convert vector to an array of strings
    if ( stringlist.size() > 0 )
      {
      final String[] stringArray = new String[ stringlist.size() ];
      stringlist.toArray(stringArray);
      return(stringArray);
      }
    else
      return(null);
    }

  /**
   ***********************************************************************
   * Returns an array of ints derived from one long string composed
	 * of ElementWidth spaced sub-strings.
	 *
	 * It does so by calling the method {@link stringToStrArray}
	 * which splits the string and loads the pieces into an array of strings.
	 * The method herewith then loops through the array, converts the strings
	 * to their int value, and populates an int array which it then returns.
	 *
	 * @param aInputString
	 *   the input string containing the list of ints
	 *
	 * @param aElementWidth
	 *   the number of characters that each int occupies within the string
   ***********************************************************************
   */
  private static int[] stringToIntArray(final String aInputString, final int aElementWidth)
    {
    final String[] stringArray = stringToStrArray(aInputString,aElementWidth);

    if ( stringArray instanceof String[] )
      {
      final int[] intArray = new int[ stringArray.length ];

      // convert each string element to an int
      for ( int i = 0; i < stringArray.length; i++ )
        {
        intArray[i] = 0;

        if ( stringArray[i].length() > 0 )
          {
          try
            {
            intArray[i] = Integer.parseInt(stringArray[i]);
            }
          catch (Exception e)
            {}
          }
        }

    return(intArray);
      }
    else
      return(null);

    }

}
