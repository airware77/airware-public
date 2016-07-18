package xmax.crs;

import xmax.crs.Flifo.FlightInfo;
import xmax.TranServer.GnrcFormat;
import xmax.util.RegExpMatch;
import java.util.StringTokenizer;


public class GnrcParseFlifo
{
  /**
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_AA(final FlightInfo aFlightData,
                                               final String aFlightStr) throws Exception
    {
    final String CITY_STOP = "^[A-Z][A-Z][A-Z] ";
    final String TIME_LINE = "^[0-9][A-Z][A-Z][A-Z]/";

    final StringTokenizer lines = new StringTokenizer(aFlightStr,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( RegExpMatch.matches(sLine,CITY_STOP) )
        {                                      // this line has flight stopover info
        final StopOver stop = new StopOver();

        stop.City             = GnrcParser.getSubstring(sLine,0,3);
        stop.ArrTerminal      = GnrcParser.getSubstring(sLine,5,10);
        stop.ArrGate          = GnrcParser.getSubstring(sLine,10,16);
        stop.ArrSchedDateTime = GnrcParser.getSubstring(sLine,16,22);
        stop.DepSchedDateTime = GnrcParser.getSubstring(sLine,22,28);
        stop.DepTerminal      = GnrcParser.getSubstring(sLine,28,31);
        stop.DepGate          = GnrcParser.getSubstring(sLine,31,35);

        setStopOverData(aFlightData,stop);
        }
      else if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        final StringTokenizer fields = new StringTokenizer(sLine," /*");

        if ( fields.countTokens() > 2 )
          {
          final String sCity = fields.nextToken().substring(1);
          String sTime;

          while ( fields.hasMoreTokens() )
            {
            sTime = fields.nextToken();

            if ( sTime.startsWith("ETD") )
              {
              sTime = sTime.substring(3);
              aFlightData.setDepEstDateTime(sCity,sTime);
              }
            else if ( sTime.startsWith("ETA") )
              {
              sTime = sTime.substring(3);
              aFlightData.setArrEstDateTime(sCity,sTime);
              }
            else if ( sTime.startsWith("IN") )
              {
              sTime = sTime.substring(2);
              aFlightData.setArrGateInDateTime(sCity,sTime);
              }
            else if ( sTime.startsWith("OUT") )
              {
              sTime = sTime.substring(3);
              aFlightData.setDepGateOutDateTime(sCity,sTime);
              }
            else if ( sTime.startsWith("OFF") )
              {
              sTime = sTime.substring(3);
              aFlightData.setDepFieldOffDateTime(sCity,sTime);
              }
            else if ( sTime.startsWith("ON") )
              {
              sTime = sTime.substring(2);
              aFlightData.setArrFieldOnDateTime(sCity,sTime);
              }

            }

          }

        }


      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CANCEL_STRING) )
            {
            final String sDepCity = fields.nextToken();
            aFlightData.setSingleSegCanceled(sDepCity,true);
            break;
            }
          }
        }


      }

    }

  /**
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_AC(final FlightInfo aFlightData,
                                               final String aFlightStr) throws Exception
    {
    final String TIME_LINE = "^[A-Z][A-Z][A-Z] [0-9 ][0-9][0-9][0-9 ] ";

    final StringTokenizer lines = new StringTokenizer(aFlightStr,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sCity;
        String sTime;

        // get actual gate departure time
        while ( fields.hasMoreTokens() )
          {
          sCity = fields.nextToken();
          if ( RegExpMatch.matches(sCity,"^[A-Z][A-Z][A-Z]$") )
            {
            sTime = fields.nextToken();
            if ( RegExpMatch.matches(sTime,"^[0-9][0-9][0-9][0-9]?$") )
              aFlightData.setDepGateOutDateTime(sCity,sTime);

            break;
            }
          }

        // get actual gate arrival time
        while ( fields.hasMoreTokens() )
          {
          sCity = fields.nextToken();
          if ( RegExpMatch.matches(sCity,"^[A-Z][A-Z][A-Z]$") )
            {
            sTime = fields.nextToken();
            if ( RegExpMatch.matches(sTime,"^[0-9][0-9][0-9][0-9]?$") )
              aFlightData.setArrGateInDateTime(sCity,sTime);

            break;
            }
          }

        }


      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CANCEL_STRING) )
            {
            final String sDepCity = fields.nextToken();
            aFlightData.setSingleSegCanceled(sDepCity,true);
            break;
            }
          }
        }


      }

    }

  /**
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_AS(final FlightInfo aFlightData,
                                               final String aFlightStr) throws Exception
    {
    final String CITY_STOP = "^[A-Z][A-Z][A-Z] ";
    final String TIME_LINE = "^[0-9][A-Z][A-Z][A-Z]/";

    final StringTokenizer lines = new StringTokenizer(aFlightStr,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( RegExpMatch.matches(sLine,CITY_STOP) )
        {                                      // this line has flight stopover info
        final StopOver stop = new StopOver();

        stop.City               = GnrcParser.getSubstring(sLine,0,3);
        stop.ArrGateInDateTime  = GnrcParser.getSubstring(sLine,18,23);
        stop.DepGateOutDateTime = GnrcParser.getSubstring(sLine,24,29);
        stop.ArrGate            = GnrcParser.getSubstring(sLine,46,51);
        stop.DepGate            = GnrcParser.getSubstring(sLine,46,51);

        setStopOverData(aFlightData,stop);
        }
      else if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        final StringTokenizer fields = new StringTokenizer(sLine," /*");

        if ( fields.countTokens() > 2 )
          {
          final String sCity = fields.nextToken().substring(1);
          String sTime;
          String sType;

          while ( fields.countTokens() >= 2 )
            {
            sType = fields.nextToken();

            if ( sType.startsWith("ETD") )
              {
              sTime = fields.nextToken();
              aFlightData.setDepEstDateTime(sCity,sTime);
              }
            else if ( sType.startsWith("ETA") )
              {
              sTime = fields.nextToken();
              aFlightData.setArrEstDateTime(sCity,sTime);
              }
            else if ( sType.startsWith("IN") )
              {
              sTime = fields.nextToken();
              aFlightData.setArrGateInDateTime(sCity,sTime);
              }
            else if ( sType.startsWith("OUT") )
              {
              sTime = fields.nextToken();
              aFlightData.setDepGateOutDateTime(sCity,sTime);
              }
            else if ( sType.startsWith("OFF") )
              {
              sTime = fields.nextToken();
              aFlightData.setDepFieldOffDateTime(sCity,sTime);
              }
            else if ( sType.startsWith("ON") )
              {
              sTime = fields.nextToken();
              aFlightData.setArrFieldOnDateTime(sCity,sTime);
              }
            }

          }

        }


      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CANCEL_STRING) )
            {
            final String sDepCity = fields.nextToken();
            aFlightData.setSingleSegCanceled(sDepCity,true);
            break;
            }
          }
        }

      }

    }

  /**
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_CO(final FlightInfo aFlightData,
                                               final String aFlightStr) throws Exception
    {
    final String CITY_STOP = "(GTA|GTD)";
    final String TIME_LINE = "^[A-Z] [A-Z][A-Z][A-Z]/";

    final StringTokenizer lines = new StringTokenizer(aFlightStr,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( RegExpMatch.matches(sLine,CITY_STOP) )
        {                                      // this line has flight stopover info
        final String sCity    = GnrcParser.getSubstring(sLine,6,9);
        final String sArrGate = GnrcParser.getSubstring(sLine,31,36);
        final String sDepGate = GnrcParser.getSubstring(sLine,40,45);

        if ( sDepGate.trim().length() > 0 &&
             aFlightData.getFlightSegmentFromCity(sCity) != null )
          aFlightData.setDepGate(sCity,sDepGate.trim());

        if ( sArrGate.trim().length() > 0 &&
             aFlightData.getFlightSegmentToCity(sCity) != null )
          aFlightData.setArrGate(sCity,sArrGate.trim());
        }
      else if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        final StringTokenizer fields = new StringTokenizer(sLine," /*");

        if ( fields.countTokens() >= 4 )
          {
          final String sLabel = fields.nextToken();
          final String sCity  = fields.nextToken();
          final String sType  = fields.nextToken();
          final String sTime  = fields.nextToken();

          if ( sType.startsWith("ETD") )
            aFlightData.setDepEstDateTime(sCity,sTime);
          else if ( sType.startsWith("ETA") )
            aFlightData.setArrEstDateTime(sCity,sTime);
          else if ( sType.startsWith("IN") )
            aFlightData.setArrGateInDateTime(sCity,sTime);
          else if ( sType.startsWith("OUT") )
            aFlightData.setDepGateOutDateTime(sCity,sTime);
          else if ( sType.startsWith("OFF") )
            aFlightData.setDepFieldOffDateTime(sCity,sTime);
          else if ( sType.startsWith("ON") )
            aFlightData.setArrFieldOnDateTime(sCity,sTime);
          }

        }

      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CANCEL_STRING) )
            {
            final String sDepCity = fields.nextToken();
            aFlightData.setSingleSegCanceled(sDepCity,true);
            break;
            }
          }
        }


      }

    }

  /**
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_DL(final FlightInfo aFlightData,
                                               final String aFlightStr) throws Exception
    {
    final String HDR_LINE1 = "FLT INFO";
    final String HDR_LINE2 = "LOF  ";
    final String CITY_STOP = "^[A-Z][A-Z][A-Z]  ";
    boolean has_miles_info = false;
    StringTokenizer lines = new StringTokenizer(aFlightStr,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( sLine.startsWith(HDR_LINE1) )       // ignore the first header line
        {}
      else if ( sLine.startsWith(HDR_LINE2) )  // check the second header line to see if miles are shown
        {
        if ( sLine.endsWith("MILES") )
          has_miles_info = true;
        }
      else if ( RegExpMatch.matches(sLine,CITY_STOP) )
        {                                      // this line has flight stopover info
        StopOver stop = new StopOver();

        if ( has_miles_info )
          {
          stop.City                 = GnrcParser.getSubstring(sLine,0,3);
          stop.ArrSchedDateTime     = GnrcParser.getSubstring(sLine,7,14);
          stop.DepSchedDateTime     = GnrcParser.getSubstring(sLine,15,20);
          stop.ArrEstDateTime       = GnrcParser.getSubstring(sLine,20,24);
          stop.ArrFieldOnDateTime   = GnrcParser.getSubstring(sLine,24,27);
          stop.ArrGateInDateTime    = GnrcParser.getSubstring(sLine,27,30);
          stop.DepEstDateTime       = GnrcParser.getSubstring(sLine,30,34);
          stop.DepGateOutDateTime   = GnrcParser.getSubstring(sLine,34,38);
          stop.DepFieldOffDateTime  = GnrcParser.getSubstring(sLine,38,42);
          }
        else
          {
          stop.City                 = GnrcParser.getSubstring(sLine,0,3);
          stop.ArrSchedDateTime     = GnrcParser.getSubstring(sLine,7,14);
          stop.DepSchedDateTime     = GnrcParser.getSubstring(sLine,15,20);
          stop.ArrEstDateTime       = GnrcParser.getSubstring(sLine,21,26);
          stop.ArrFieldOnDateTime   = GnrcParser.getSubstring(sLine,26,33);
          stop.ArrGateInDateTime    = GnrcParser.getSubstring(sLine,33,39);
          stop.DepEstDateTime       = GnrcParser.getSubstring(sLine,39,45);
          stop.DepGateOutDateTime   = GnrcParser.getSubstring(sLine,45,51);
          stop.DepFieldOffDateTime  = GnrcParser.getSubstring(sLine,51,57);
          }

        setStopOverData(aFlightData,stop);
        }


      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final String CITY_PAIR_PATTERN = "^[A-Z][A-Z][A-Z]\\-[A-Z][A-Z][A-Z]$";
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CITY_PAIR_PATTERN) )
            {
            final String sCancelField = fields.nextToken();
            if ( RegExpMatch.matches(sCancelField,CANCEL_STRING) )
              {
              final String sDepCity = sField.substring(0,3);
              final String sArrCity = sField.substring(4,7);
              aFlightData.setCanceled(sDepCity,sArrCity,true);
              break;
              }
            }
          }
        }

      }

    }

  /**
   ***********************************************************************
   * Day of Flifo routines
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_HP(final FlightInfo aFlightData,
                                               final String aFlightStr) throws Exception
    {
    final String CITY_STOP = "(GTA|GTD)";
    final String TIME_LINE = "^[A-Z] [A-Z][A-Z][A-Z]/";

    final StringTokenizer lines = new StringTokenizer(aFlightStr,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( RegExpMatch.matches(sLine,CITY_STOP) )
        {                                      // this line has flight stopover info
        final String sCity    = GnrcParser.getSubstring(sLine,6,9);
        final String sArrGate = GnrcParser.getSubstring(sLine,31,36);
        final String sDepGate = GnrcParser.getSubstring(sLine,40,45);

        if ( sDepGate.trim().length() > 0 )
          aFlightData.setDepGate(sCity,sDepGate.trim());

        if ( sArrGate.trim().length() > 0 )
          aFlightData.setArrGate(sCity,sArrGate.trim());
        }
      else if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        final StringTokenizer fields = new StringTokenizer(sLine," /*");

        if ( fields.countTokens() >= 4 )
          {
          final String sLabel = fields.nextToken();
          final String sCity  = fields.nextToken();
          final String sType  = fields.nextToken();
          final String sTime  = fields.nextToken();

          if ( sType.startsWith("ETD") )
            aFlightData.setDepEstDateTime(sCity,sTime);
          else if ( sType.startsWith("ETA") )
            aFlightData.setArrEstDateTime(sCity,sTime);
          else if ( sType.startsWith("IN") )
            aFlightData.setArrGateInDateTime(sCity,sTime);
          else if ( sType.startsWith("OUT") )
            aFlightData.setDepGateOutDateTime(sCity,sTime);
          else if ( sType.startsWith("OFF") )
            aFlightData.setDepFieldOffDateTime(sCity,sTime);
          else if ( sType.startsWith("ON") )
            aFlightData.setArrFieldOnDateTime(sCity,sTime);
          }

        }

      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CANCEL_STRING) )
            {
            final String sDepCity = fields.nextToken();
            aFlightData.setSingleSegCanceled(sDepCity,true);
            break;
            }
          }
        }


      }

    }

  /**
   ***********************************************************************
   * Parsing flight data routine for Northwest
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_NW(final FlightInfo aFlightData,
                                               final String aFlifoResponse) throws Exception
    {
    // get the time info
    final String sTimeInfo = GnrcParser.extractString(aFlifoResponse,"","GATE EQUIP");
    final String TIME_LINE = "^  [A-Z][A-Z][A-Z] ";
    final StringTokenizer lines = new StringTokenizer(sTimeInfo,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( sLine.startsWith("  DEP  SKD ") )
        {}
      else if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        // actual gate departure time
        {
        // set the departure city
        final String sDepCity = GnrcParser.getSubstring(sLine,2,5).trim();
        final String sTime1   = GnrcParser.getSubstring(sLine,12,17).trim();

        // set the actual gate departure time
        if ( (sDepCity.length() > 0) && (sTime1.length() > 0) )
          {
          if ( sLine.substring(17,19).equals("-E") )
            aFlightData.setDepEstDateTime(sDepCity,sTime1);
          else
            aFlightData.setDepGateOutDateTime(sDepCity,sTime1);
          }
        }

        // actual gate arrival time
        {
        final String sArrCity = GnrcParser.getSubstring(sLine,20,23).trim();
        final String sTime1   = GnrcParser.getSubstring(sLine,30,35).trim();


        // set the actual gate departure time
        if ( (sArrCity.length() > 0) && (sTime1.length() > 0) )
          aFlightData.setArrGateInDateTime(sArrCity,sTime1);

        // set the estimated arrival time, if there is one
        if ( sLine.length() >= 41 )
          {
          if ( sLine.substring(35,36).equals("E") )
            {
            final String sEstTime = sLine.substring(36,41);
            aFlightData.setArrEstDateTime(sArrCity,sEstTime);
            }
          }

        }

        }

      }


    // get the gate information
    final String sGateInfo = GnrcParser.extractString(aFlifoResponse,"GATE EQUIP",null);
    final String GATE_LINE = "^  [A-Z][A-Z][A-Z] ";
    final StringTokenizer gatelines = new StringTokenizer(sGateInfo,"\r\n");

    // scan through each line returned by the host
    while ( gatelines.hasMoreTokens() )
      {
      sLine = gatelines.nextToken();

      if ( RegExpMatch.matches(sLine,GATE_LINE) )
        {

        {     // set the departure gate
        final String sDepCity   = GnrcParser.getSubstring(sLine,2,5);
        final String sDepGate   = GnrcParser.getSubstring(sLine,6,10).trim();

        if ( sDepGate.length() > 0 )
          aFlightData.setDepGate(sDepCity,sDepGate);
        }

        {     // set the arrival gate
        final String sArrCity   = GnrcParser.getSubstring(sLine,29,32);
        final String sArrGate   = GnrcParser.getSubstring(sLine,33,37).trim();

        if ( sArrGate.length() > 0 )
          aFlightData.setArrGate(sArrCity,sArrGate);
        }

        }
      }

    }

  /**
   ***********************************************************************
   * Parsing flight data routine for TWA
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_TW(final FlightInfo aFlightData,
                                               final String aFlifoResponse) throws Exception
    {
    // get the time info
    final String sTimeInfo = GnrcParser.extractString(aFlifoResponse,"","GATE EQUIP");
    final String TIME_LINE = "^  [A-Z][A-Z][A-Z] ";
    final StringTokenizer lines = new StringTokenizer(sTimeInfo,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( sLine.startsWith("  DEP  SKD ") )
        {}
      else if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        // actual gate departure time
        {
        // set the departure city
        final String sDepCity = GnrcParser.getSubstring(sLine,2,5);
        final String sTime1   = GnrcParser.getSubstring(sLine,12,17).trim();

        // set the actual gate departure time
        if ( (sDepCity.length() > 0) && (sTime1.length() > 0) )
          {
          if ( sLine.substring(17,19).equals("-E") )
            aFlightData.setDepEstDateTime(sDepCity,sTime1);
          else
            aFlightData.setDepGateOutDateTime(sDepCity,sTime1);
          }
        }

        // actual gate arrival time
        {
        final String sArrCity = GnrcParser.getSubstring(sLine,20,23);
        final String sTime1   = GnrcParser.getSubstring(sLine,30,35).trim();

        // set the actual gate departure time
        if ( (sArrCity.length() > 0) && (sTime1.length() > 0) )
          aFlightData.setArrGateInDateTime(sArrCity,sTime1);

        // set the estimated arrival time, if there
        if ( sLine.length() >= 41 )
          {
          if ( sLine.substring(35,36).equals("E") )
            {
            final String sEstTime = sLine.substring(36,41);
            aFlightData.setArrEstDateTime(sArrCity,sEstTime);
            }
          }

        }

        }

      }


    // get the gate information
    final String sGateInfo = GnrcParser.extractString(aFlifoResponse,"GATE EQUIP",null);
    final String GATE_LINE = "^  [A-Z][A-Z][A-Z] ";
    final StringTokenizer gatelines = new StringTokenizer(sGateInfo,"\r\n");

    // scan through each line returned by the host
    while ( gatelines.hasMoreTokens() )
      {
      sLine = gatelines.nextToken();

      if ( RegExpMatch.matches(sLine,GATE_LINE) )
        {
        final String sDepCity = GnrcParser.getSubstring(sLine,2,5);
        final String sDepGate = GnrcParser.getSubstring(sLine,6,10).trim();
        final String sArrCity = GnrcParser.getSubstring(sLine,20,23);
        final String sArrGate = GnrcParser.getSubstring(sLine,24,28).trim();

        if ( sDepGate.length() > 0 )
          aFlightData.setDepGate(sDepCity,sDepGate);

        if ( sArrGate.length() > 0 )
          aFlightData.setArrGate(sArrCity,sArrGate);
        }
      }

    }

  /**
   ***********************************************************************
   * Parsing flight data routine for United
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_UA(final FlightInfo aFlightData,
                                               final String aFlifoResponse) throws Exception
    {
    // get the time info
    final String sTimeInfo = GnrcParser.extractString(
        aFlifoResponse,"","(GATE INFORMATION|TERMINAL GATE)");

    final String TIME_LINE = "^[A-Z][A-Z][A-Z]/[0-9][0-9][0-9]";
    final StringTokenizer lines = new StringTokenizer(sTimeInfo,"\r\n");
    String sLine;

    // check if entire flight has been canceled
    if ( aFlifoResponse.indexOf("FLT CNCLD") >= 0 )
      aFlightData.setCanceled(true);

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( RegExpMatch.matches(sLine,TIME_LINE) )
        { // this line has extra time information
        final String sCity = GnrcParser.getSubstring(sLine,0,3);

        // first time field
        {
        final String sLabel1 = GnrcParser.getSubstring(sLine,11,14);
        final String sTime1  = GnrcParser.getSubstring(sLine,16,21).trim();

        // set the first time field
        if ( sTime1.length() > 0 )
          {
          if ( sLabel1.startsWith("ETD") )
            aFlightData.setDepEstDateTime(sCity,sTime1);
          else if ( sLabel1.startsWith("ETA") )
            aFlightData.setArrEstDateTime(sCity,sTime1);
          else if ( sLabel1.startsWith("IN") )
            aFlightData.setArrGateInDateTime(sCity,sTime1);
          else if ( sLabel1.startsWith("OUT") )
            aFlightData.setDepGateOutDateTime(sCity,sTime1);
          else if ( sLabel1.startsWith("OFF") )
            aFlightData.setDepFieldOffDateTime(sCity,sTime1);
          else if ( sLabel1.startsWith("ON") )
            aFlightData.setArrFieldOnDateTime(sCity,sTime1);
          }
        }

        // second time field
        {
        final String sLabel2 = GnrcParser.getSubstring(sLine,37,40);
        final String sTime2  = GnrcParser.getSubstring(sLine,42,47).trim();

        // set the second time field
        if ( sTime2.length() > 0 )
          {
          if ( sLabel2.startsWith("ETD") )
            aFlightData.setDepEstDateTime(sCity,sTime2);
          else if ( sLabel2.startsWith("ETA") )
            aFlightData.setArrEstDateTime(sCity,sTime2);
          else if ( sLabel2.startsWith("IN") )
            aFlightData.setArrGateInDateTime(sCity,sTime2);
          else if ( sLabel2.startsWith("OUT") )
            aFlightData.setDepGateOutDateTime(sCity,sTime2);
          else if ( sLabel2.startsWith("OFF") )
            aFlightData.setDepFieldOffDateTime(sCity,sTime2);
          else if ( sLabel2.startsWith("ON") )
            aFlightData.setArrFieldOnDateTime(sCity,sTime2);
          }
        }

        if ( sLine.indexOf("DPTS CNCLD") >= 0 )
          aFlightData.setSingleSegCanceled(sCity,true);
        }
      } // end while


    // get the gate information
    final String sGateInfo = GnrcParser.extractString(
        aFlifoResponse,"(GATE INFORMATION|TERMINAL GATE)",null);
    final String GATE_LINE = "^[A-Z][A-Z][A-Z]  ";
    final StringTokenizer gatelines = new StringTokenizer(sGateInfo,"\r\n");

    // scan through each line returned by the host
    while ( gatelines.hasMoreTokens() )
      {
      sLine = gatelines.nextToken();

      if ( RegExpMatch.matches(sLine,GATE_LINE) )
        {                                      // this line has extra gate information
        final String sCity    = GnrcParser.getSubstring(sLine,0,3).trim();
        final String sArrTerm = GnrcParser.getSubstring(sLine,6,12).trim();
        final String sArrGate = GnrcParser.getSubstring(sLine,12,17).trim();
        final String sDepTerm = GnrcParser.getSubstring(sLine,37,43).trim();
        final String sDepGate = GnrcParser.getSubstring(sLine,43,48).trim();

        if ( sArrTerm.length() > 0 )
          aFlightData.setArrTerminal(sCity,sArrTerm);

        if ( sArrGate.length() > 0 )
          aFlightData.setArrGate(sCity,sArrGate);

        if ( sDepTerm.length() > 0 )
          aFlightData.setDepTerminal(sCity,sDepTerm);

        if ( sDepGate.length() > 0 )
          aFlightData.setDepGate(sCity,sDepGate);
        }

      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CANCEL_STRING) )
            {
            final String sDepCity = fields.nextToken();
            aFlightData.setSingleSegCanceled(sDepCity,true);
            break;
            }
          }
        }


      } // end while

    } // end setDayOfFlightSegments_UA

  /**
   ***********************************************************************
   * This method parses the following kind of output:
   * (this is a direct access request)
   * <pre>
   * ** US - US AIRWAYS **
   * 1886/23APR
   * P PHL/OUT    159P
   * P PHL/OFF    227P
   * P MCO/ETA    426P
   *
   * SKED  PHL  ORIG    155P              GTD  C28 SHIP 932
   *       MCO   419P  TERM      GTA   56
   * *TRN*
   * &gt;
   * </pre>
   * Day of Flifo routines
   ***********************************************************************
   */
  public static void setDayOfFlightSegments_US(final FlightInfo aFlightData,
                                               final String aFlightStr) throws Exception
    {
    final String CITY_STOP = "(GTA|GTD)";
    final String TIME_LINE = "^[A-Z] [A-Z][A-Z][A-Z]/";

    final StringTokenizer lines = new StringTokenizer(aFlightStr,"\r\n");
    String sLine;

    // scan through each line returned by the host
    while ( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken();

      if ( RegExpMatch.matches(sLine,CITY_STOP) )
        {                                      // this line has flight stopover info
        final String sCity    = GnrcParser.getSubstring(sLine,6,9);
        final String sArrGate = GnrcParser.getSubstring(sLine,31,36);
        final String sDepGate = GnrcParser.getSubstring(sLine,40,45);

        if ( sDepGate.trim().length() > 0 &&
             aFlightData.getFlightSegmentFromCity(sCity) != null )
          aFlightData.setDepGate(sCity,sDepGate.trim());

        if ( sArrGate.trim().length() > 0 &&
             aFlightData.getFlightSegmentToCity(sCity) != null )
          aFlightData.setArrGate(sCity,sArrGate.trim());
        }
      else if ( RegExpMatch.matches(sLine,TIME_LINE) )
        {                                      // this line has extra time information
        final StringTokenizer fields = new StringTokenizer(sLine," /*");

        if ( fields.countTokens() >= 4 )
          {
          final String sLabel = fields.nextToken();
          final String sCity  = fields.nextToken();
          final String sType  = fields.nextToken();
          final String sTime  = fields.nextToken();

          if ( sType.startsWith("ETD") )
            aFlightData.setDepEstDateTime(sCity,sTime);
          else if ( sType.startsWith("ETA") )
            aFlightData.setArrEstDateTime(sCity,sTime);
          else if ( sType.startsWith("IN") )
            aFlightData.setArrGateInDateTime(sCity,sTime);
          else if ( sType.startsWith("OUT") )
            aFlightData.setDepGateOutDateTime(sCity,sTime);
          else if ( sType.startsWith("OFF") )
            aFlightData.setDepFieldOffDateTime(sCity,sTime);
          else if ( sType.startsWith("ON") )
            aFlightData.setArrFieldOnDateTime(sCity,sTime);
          }

        }

      // check if flight segment has been cancelled
      final String CANCEL_STRING = "(CANCEL|CXLD)";
      if ( RegExpMatch.matches(sLine,CANCEL_STRING) )
        {
        final StringTokenizer fields = new StringTokenizer(sLine," ");
        String sField;
        while ( fields.countTokens() >= 2 )
          {
          sField = fields.nextToken();
          if ( RegExpMatch.matches(sField,CANCEL_STRING) )
            {
            final String sDepCity = fields.nextToken();
            aFlightData.setSingleSegCanceled(sDepCity,true);
            break;
            }
          }
        }

      }

    } // end setDayOfFlightSegments_US

 /**
  ***********************************************************************
  * Copy stopover info to the appropriate flight segment
  ***********************************************************************
  */
 private static void setStopOverData(final FlightInfo aFlightData, final StopOver aStopOverData)
   {
   if ( (aStopOverData.City instanceof String) == false )
     return;

   // set the estimated arrival date time
   try
     {
     if ( aStopOverData.ArrEstDateTime.trim().length() > 0 )
       aFlightData.setArrEstDateTime(aStopOverData.City,aStopOverData.ArrEstDateTime);
     }
   catch (Exception e)
     {}

   // set the arrival on field
   try
     {
     if ( aStopOverData.ArrFieldOnDateTime.trim().length() > 0 )
       aFlightData.setArrFieldOnDateTime(aStopOverData.City,aStopOverData.ArrFieldOnDateTime);
     }
   catch (Exception e)
     {}

   // set the arrival in gate
   try
     {
     if ( aStopOverData.ArrGateInDateTime.trim().length() > 0 )
       aFlightData.setArrGateInDateTime(aStopOverData.City,aStopOverData.ArrGateInDateTime);
     }
   catch (Exception e)
     {}

   // set the estimated departure time
   try
     {
     if ( aStopOverData.DepEstDateTime.trim().length() > 0 )
       aFlightData.setDepEstDateTime(aStopOverData.City,aStopOverData.DepEstDateTime);
     }
   catch (Exception e)
     {}

   // set the gate out departure time
   try
     {
     if ( aStopOverData.DepGateOutDateTime.trim().length() > 0 )
       aFlightData.setDepGateOutDateTime(aStopOverData.City,aStopOverData.DepGateOutDateTime);
     }
   catch (Exception e)
     {}

   // set the field off departure time
   try
     {
     if ( aStopOverData.DepFieldOffDateTime.trim().length() > 0 )
       aFlightData.setDepFieldOffDateTime(aStopOverData.City,aStopOverData.DepFieldOffDateTime);
     }
   catch (Exception e)
     {}

   // set the departure gate
   try
     {
     if ( aStopOverData.DepGate instanceof String )
       {
       if ( aStopOverData.DepGate.trim().length() > 0 )
         aFlightData.setDepGate(aStopOverData.City,aStopOverData.DepGate);
       }
     }
   catch (Exception e)
     {}

   // set the arrival gate
   try
     {
     if ( aStopOverData.ArrGate instanceof String )
       {
       if ( aStopOverData.ArrGate.trim().length() > 0 )
         aFlightData.setArrGate(aStopOverData.City,aStopOverData.ArrGate);
       }
     }
   catch (Exception e)
     {}

   // set the departure terminal
   try
     {
     if ( aStopOverData.DepTerminal instanceof String )
       {
       if ( aStopOverData.DepTerminal.trim().length() > 0 )
         aFlightData.setDepTerminal(aStopOverData.City,aStopOverData.DepTerminal);
       }
     }
   catch (Exception e)
     {}

   // set the arrival terminal
   try
     {
     if ( aStopOverData.ArrTerminal instanceof String )
       {
       if ( aStopOverData.ArrTerminal.trim().length() > 0 )
         aFlightData.setArrTerminal(aStopOverData.City,aStopOverData.ArrTerminal);
       }
     }
   catch (Exception e)
     {}

   }

}

/**
 ***********************************************************************
 * Class for storing city stop data
 ***********************************************************************
 */

class StopOver
{
 String City;

 String DepSchedDateTime;
 String DepEstDateTime;
 String DepGateOutDateTime;
 String DepFieldOffDateTime;
 String DepTerminal;
 String DepGate;

 String ArrSchedDateTime;
 String ArrEstDateTime;
 String ArrGateInDateTime;
 String ArrFieldOnDateTime;
 String ArrTerminal;
 String ArrGate;
}