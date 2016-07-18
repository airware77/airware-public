package xmax.crs.Amadeus;

import java.util.Vector;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import java.util.Date;

import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.crs.GnrcParseFlifo;
import xmax.crs.GetPNR.GnrcParsePNR;
import xmax.crs.GetPNR.PNRItinAirSegment;
import xmax.crs.Flifo.FlightSegment;
import xmax.crs.Flifo.FlightInfo;
import xmax.util.RegExpMatch;
import xmax.TranServer.GnrcFormat;

/**
 ***********************************************************************
 * This class contains the parsing routines that are needed to parse Flight
 * Information replies from an Amadeus Terminal.
 * 
 * @author   David Fairchild
 * @version  $Revision: 9$ - $Date: 10/02/2001 3:34:56 PM$
 ***********************************************************************
 */
public class AmadeusParseFlifo
{
  /** 
   ***********************************************************************
   * Parsing routine
   ***********************************************************************
   */
  public FlightSegment [] getFlightSegments(final String aCarrier,
                                            final int aFlightNum,
                                            final String aDepDate,
                                            final String aFlightData) throws Exception
    {
    final String sFlightData = GnrcParsePNR.m_AddLineWrap(aFlightData);
    final String sFlightScheduleInfo = GnrcParser.extractString(
        sFlightData,"PLANNED FLIGHT INFO","^(COMMENTS-|CONFIGURATION-)");

    // read in the flight stops
    final StringTokenizer Schedlines = new StringTokenizer(sFlightScheduleInfo,"\r\n");
    String sLine;
    FlightStop StopInfo = null;
    final Vector StopList = new Vector();
    while ( Schedlines.hasMoreTokens() )
      {
      sLine = Schedlines.nextToken();

      if ( RegExpMatch.matches(sLine,"^[A-Z][A-Z][A-Z] ") && (sLine.startsWith("APT ") == false) )
        {
        StopInfo = scanStopData(aDepDate,sLine);
        StopList.add(StopInfo);
        }
      else if ( sLine.startsWith("   ") )
        {
        if ( StopList.size() > 0 )
          {
          StopInfo = (FlightStop )StopList.elementAt(StopList.size() - 1);
          if ( StopInfo instanceof FlightStop )
            {
            final String sMealService = GnrcParser.getSubstring(sLine,22,40).trim();
            scanMealService(StopInfo,sLine);
            }
          }
        }
      }

   // make sure there are at least two stops listed
   if ( StopList.size() < 2 )
     return(null);

   // convert vector into an array of stops
   final FlightStop[] StopArray = new FlightStop[ StopList.size() ];
   StopList.toArray(StopArray);

   // convert array of city objects to array of flight objects
   final FlightInfo fdata = stopsToSegments(aCarrier, aFlightNum, StopArray);

   // read comments
   final String sFlightCommentInfo = GnrcParser.extractString(sFlightData,"^COMMENTS-","^CONFIGURATION-");
   readComments(fdata, sFlightCommentInfo);

   final String sFlightOperationInfo = GnrcParser.extractString(sFlightData,"OPERATIONAL FLIGHT INFO","PLANNED FLIGHT INFO");
   readOperations(fdata, sFlightOperationInfo);

   return( fdata.getFlightSegments() );
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  private FlightStop scanStopData(final String aDepDate,
                                     final String aDataLine)
                                       throws Exception
    {
    final FlightStop StopData = new FlightStop();

    // scan in the data
    StopData.CityCode            = GnrcParser.getSubstring(aDataLine,0,3).trim();
    final String sArrTime        = GnrcParser.getSubstring(aDataLine,4,9).trim();
    final String sArrDayofWeek   = GnrcParser.getSubstring(aDataLine,10,12).trim();
    final String sDepTime        = GnrcParser.getSubstring(aDataLine,13,18).trim();
    final String sDepDayofWeek   = GnrcParser.getSubstring(aDataLine,19,21).trim();
    final String sMealCode       = GnrcParser.getSubstring(aDataLine,22,41).trim();
    StopData.Equipment           = GnrcParser.getSubstring(aDataLine,42,45).trim();
    final String sElapsedTime    = GnrcParser.getSubstring(aDataLine,53,58).trim();

    // calculate the departure and arrival times
    if ( GnrcFormat.NotNull(sArrTime) )
      StopData.ArriveDateTime = getDateTime(aDepDate,sArrDayofWeek,sArrTime);
    if ( GnrcFormat.NotNull(sDepTime) )
      StopData.DepartDateTime = getDateTime(aDepDate,sDepDayofWeek,sDepTime);

    // calculate the elapsed time
    if ( GnrcFormat.NotNull(sElapsedTime) )
      StopData.FlightMinutes = GnrcParser.getElapsedMinutes(sElapsedTime);

    // scan in meal service codes
    scanMealService(StopData,sMealCode);

    return(StopData);
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  private long getDateTime(final String aDate, final String aDayOfWeek, final String aTime) throws Exception
    {
    // convert given date and time
    long lDateTime = GnrcParser.ScanCRSDateTimeString(aDate,aTime);

    // get day of week for given date and time
    final SimpleDateFormat CrsDate = new SimpleDateFormat("EEEE");
    final String sFirstDay = CrsDate.format( new Date(lDateTime) );

    // determine number of days between given date and given day of week
    final int iNumDays = dayDiff(sFirstDay,aDayOfWeek);

    // increment that many days
    final long MSECS_PER_DAY = 24 * 60 * 60 * 1000;
    lDateTime += iNumDays * MSECS_PER_DAY;

    return(lDateTime);
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  private int dayDiff(final String aFirstDayOfWeek, final String aSecondDayOfWeek)
    {
    try
      {
      final int iFirstDay  = dayStrToDayNum(aFirstDayOfWeek);
      final int iSecondDay = dayStrToDayNum(aSecondDayOfWeek);

      int iDiff = iSecondDay - iFirstDay;
      while ( iDiff < 0 )
        iDiff += 7;

      return(iDiff);
      }
    catch (Exception e)
      {
      return(0);
      }
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  private int dayStrToDayNum(String aDayOfWeek) throws Exception
    {
    aDayOfWeek = aDayOfWeek.toUpperCase();

    if ( aDayOfWeek.startsWith("M") )
      return(1);
    else if ( aDayOfWeek.startsWith("TU") )
      return(2);
    else if ( aDayOfWeek.startsWith("W") )
      return(3);
    else if ( aDayOfWeek.startsWith("TH") )
      return(4);
    else if ( aDayOfWeek.startsWith("F") )
      return(5);
    else if ( aDayOfWeek.startsWith("SA") )
      return(6);
    else if ( aDayOfWeek.startsWith("SU") )
      return(7);
    else
      throw new Exception("Invalid day of week specified - given day of week = " + aDayOfWeek);
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  private void scanMealService(final FlightStop aSegment, final String aMealService)
    {
    if ( (aSegment.MealService instanceof String) == false )
      aSegment.MealService = "";

    final StringTokenizer fields = new StringTokenizer(aMealService," /");
    String sClassList;
    String sService;
    while ( fields.countTokens() >= 2 )
      {
      sClassList = fields.nextToken();
      sService   = fields.nextToken();

      if ( sService.equals("-") == false )
        aSegment.MealService = aSegment.MealService + "/" + sService;
      }
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  static FlightInfo stopsToSegments(final String aCarrier,
                                    final int aFlightNum,
                                    final FlightStop[] aStops)
    {
    if ( (aStops instanceof FlightStop[]) == false )
      return(null);

    if ( aStops.length < 2 )
      return(null);

    final Vector FlightList = new Vector();
    FlightSegment flight = null;
    FlightStop depcity  = null;
    FlightStop arrcity  = null;
    String sLastEquipment = "";
    for ( int i = 0; i < aStops.length - 1; i++  )
      {
      depcity = aStops[i];
      arrcity = aStops[i + 1];

      flight = new FlightSegment();

      flight.Carrier          = aCarrier;
      flight.FlightNum        = aFlightNum;
      flight.DepartCity       = depcity.CityCode;
      flight.DepSchedDateTime = depcity.DepartDateTime;
      flight.ArriveCity       = arrcity.CityCode;
      flight.ArrSchedDateTime = arrcity.ArriveDateTime;
      flight.MealCode         = depcity.MealService;
      flight.EquipmentCode    = depcity.Equipment;
      flight.AirMinutes       = depcity.FlightMinutes;

      if ( GnrcFormat.IsNull(flight.EquipmentCode) )
        flight.EquipmentCode = sLastEquipment;

      sLastEquipment = flight.EquipmentCode;

      FlightList.add(flight);
      }


    try
      {
      // convert vector into an array of flight segments
      final FlightSegment[] FlightArray = new FlightSegment[ FlightList.size() ];
      FlightList.toArray(FlightArray);

      final FlightInfo fdata = new FlightInfo(FlightArray);
      return(fdata);
      }
    catch (Exception e)
      {
      return(null);
      }
    }

  /** 
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  void readComments(final FlightInfo aFlightData, final String aComments)
    {
    // String sCommentData = GnrcParser.extractString(aFlightStr,"^COMMENTS-","^CONFIGURATION-");
    final String sCommentData = GnrcParser.findNewLines(aComments,"^[ 1-9][0-9]\\.");

    final StringTokenizer lines = new StringTokenizer(sCommentData,"\r\n");
    String sLine;

    while ( lines.hasMoreTokens() )
      {

      sLine = lines.nextToken();
      try
        {
        if ( RegExpMatch.matches(sLine,"^[ 1-9][0-9]\\.") )
          readSingleComment(aFlightData,sLine);
        }
      catch (Exception e)
        {}

      }
    }

  /** 
   ***********************************************************************
   * read the operation flight information
   ***********************************************************************
   */
  private void readOperations(final FlightInfo aFlightData, final String aOperations)
    {
    final StringTokenizer lines = new StringTokenizer(aOperations,"\r\n");
    String sLine;
    String sArrCity = "";
    String sDepCity = "";
    String sComment = "";
    String sTime    = "";

    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      sDepCity = GnrcParser.getSubstring(sLine,0,3).trim();
      sComment = GnrcParser.getSubstring(sLine,5,44).trim();
      sTime    = GnrcParser.getSubstring(sLine,48,53).trim();
      sArrCity = GnrcParser.getSubstring(sLine,54,57).trim();

      if ( GnrcFormat.NotNull(sTime) )
        {
        try
          {
          if ( sComment.equals("ESTIMATED TIME OF DEPARTURE") )
            aFlightData.setDepEstDateTime(sDepCity,sTime);
          else if ( sComment.equals("LEFT THE GATE") )
            aFlightData.setDepGateOutDateTime(sDepCity,sTime);
          else if ( sComment.equals("TOOK OFF") )
            aFlightData.setDepFieldOffDateTime(sDepCity,sTime);
          else if ( sComment.equals("ESTIMATED TIME OF ARRIVAL") )
            aFlightData.setArrEstDateTime(sArrCity,sTime);
          else if ( sComment.equals("AIRCRAFT LANDED") )
            aFlightData.setArrFieldOnDateTime(sDepCity,sTime);   // this is intentional, the arrival city is actually shown in this field
          else if ( sComment.equals("AT THE GATE") )
            aFlightData.setArrGateInDateTime(sDepCity,sTime);    // this is intentional, the arrival city is actually shown in this field
          }
        catch (Exception e)
          {}

        }

      }
    }


  /** 
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  static void readSingleComment(final FlightInfo aFlightData, final String aComment)
    {
    // determine departure or arrival city for comment
    String sArrCity = "";
    String sDepCity = "";
    try
      {
      if ( RegExpMatch.matches(aComment,"^[ 1-9][0-9]\\.TO [A-Z][A-Z][A-Z] ") )
        sArrCity = aComment.substring(6,9);
      else if ( RegExpMatch.matches(aComment,"^[ 1-9][0-9]\\.FROM [A-Z][A-Z][A-Z] ") )
        sDepCity = aComment.substring(8,11);
      else if ( RegExpMatch.matches(aComment,"^[ 1-9][0-9]\\.[A-Z][A-Z][A-Z] [A-Z][A-Z][A-Z] ") )
        {
        sDepCity = aComment.substring(3,6);
        sArrCity = aComment.substring(7,10);
        }
      }
    catch (Exception e)
      {}

    // get the relevant flight segment based on arrival or departure city
    FlightSegment fseg = null;
    if ( aFlightData.getNumStops() == 0 )
      fseg = aFlightData.getFlightSegment(0);
    else if ( sDepCity.length() > 0 )
      fseg = aFlightData.getFlightSegmentFromCity(sDepCity);
    else if ( sArrCity.length() > 0 )
      fseg = aFlightData.getFlightSegmentToCity(sArrCity);

    if ( (fseg instanceof FlightSegment) == false )
      return;

    // extract the comment
    String sComment = "";
    {
    final int iPos = aComment.indexOf('-');
    if ( iPos >= 0 )
      sComment = aComment.substring(iPos + 1);
    else
      sComment = aComment.substring(3);
    }

    if ( aComment.indexOf("TERM") >= 0 )
      {
      // this is a terminal comment
      // strip the comment of words like "arrival", "departure", and "terminal"
      final StringTokenizer words = new StringTokenizer(sComment," ");
      String sWord = "";
      String sTerminal = "";
      while ( words.hasMoreTokens() )
        {
        sWord = words.nextToken();

        if ( (sWord.indexOf("ARR") < 0) && (sWord.indexOf("DEP") < 0) && (sWord.indexOf("TERM") < 0) )
          sTerminal = sTerminal + " " + sWord;
        }

      if ( aComment.indexOf("ARR") >= 0 )
        fseg.ArrTerminal = sTerminal.trim();
      else
        fseg.DepTerminal = sTerminal.trim();
      }

    else if ( aComment.indexOf("OPERATED BY") >= 0 )
      {     // this is a shared carrier comment
      final int iPos = aComment.indexOf("OPERATED BY");
      fseg.CodeShareCarrierName = aComment.substring(iPos + 11).trim();
      }

    else if ( aComment.indexOf("OPERATIONAL LEG") >= 0 )
      {     // this is a shared carrier comment
      final int iPos         = aComment.indexOf("OPERATIONAL LEG");
      final String sCarrflgt = aComment.substring(iPos + 15).trim();

      if ( sCarrflgt.length() > 2 )
        {
        fseg.CodeShareCarrierCode = sCarrflgt.substring(0,2).trim();
        fseg.CodeShareFlightNum   = sCarrflgt.substring(2).trim();
        }
      }

    }

  /** 
   ***********************************************************************
   * Parsing routine for carrier specific flight times
   * This read the "F:UA826/02JUN" response
   ***********************************************************************
   */
  public void setDayOfFlightSegments(final FlightInfo aFlightData,
                            final String aFlifoResponse) throws Exception
    {
    final String sCarrier = aFlightData.getCarrier();

    // scan in the response depending on the carrier
    if ( sCarrier.equals("AA") )
      GnrcParseFlifo.setDayOfFlightSegments_AA(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("AC") )
      GnrcParseFlifo.setDayOfFlightSegments_AC(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("AS") )
      GnrcParseFlifo.setDayOfFlightSegments_AS(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("CO") )
      GnrcParseFlifo.setDayOfFlightSegments_CO(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("DL") )
      GnrcParseFlifo.setDayOfFlightSegments_DL(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("HP") )
      GnrcParseFlifo.setDayOfFlightSegments_HP(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("NW") )
      GnrcParseFlifo.setDayOfFlightSegments_NW(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("TW") )
      GnrcParseFlifo.setDayOfFlightSegments_TW(aFlightData,aFlifoResponse);
    else if ( sCarrier.equals("UA") )
    {
      //  United now uses the Continental flifo format
      //  GnrcParseFlifo.setDayOfFlightSegments_UA(aFlightData,aFlifoResponse);
      GnrcParseFlifo.setDayOfFlightSegments_CO(aFlightData,aFlifoResponse);
    }
    else if ( sCarrier.equals("US") )
      GnrcParseFlifo.setDayOfFlightSegments_US(aFlightData,aFlifoResponse);
   }
}
