package xmax.crs;

import java.util.Date;
import java.util.StringTokenizer;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;
import xmax.TranServer.GnrcFormat;

public class GnrcParser
{
  /** 
   ***********************************************************************
   * This function scans in a date in the Airware CYYMMDD format
   ***********************************************************************
   */
  static public long ScanAirwareDate(final String aDateString) throws Exception
    {
    return( ScanAirwareDateTime(aDateString + "000000") );
    }

  /** 
   ***********************************************************************
   * This function scans in a date in the Airware CYYMMDDHHMMSS format
   ***********************************************************************
   */
  static public long ScanAirwareDateTime(final String aDateTimeString) throws Exception
    {
    final String sCentury = aDateTimeString.substring(0,1);
    final String sYear    = aDateTimeString.substring(1,3);
    final String sMonth   = aDateTimeString.substring(3,5);
    final String sDay     = aDateTimeString.substring(5,7);
    final String sHour    = aDateTimeString.substring(7,9);
    final String sMinute  = aDateTimeString.substring(9,11);
    final String sSecond  = aDateTimeString.substring(11,13);

    final int iCentury    = Integer.parseInt(sCentury);
          int iYear       = Integer.parseInt(sYear);
          int iMonth      = Integer.parseInt(sMonth);
    final int iDay        = Integer.parseInt(sDay);
    final int iHour       = Integer.parseInt(sHour);
    final int iMinute     = Integer.parseInt(sMinute);
    final int iSecond     = Integer.parseInt(sSecond);

    // if iCentury = 0, year is assumed to be in 1900 century, otherwise, its 2000
    if ( iCentury == 0 )
      iYear += 1900;
    else
      iYear += 2000;

    if ( iMonth > 0 )
      iMonth--;

    // set the scanned input date to the current year
    final GregorianCalendar SetCal = new GregorianCalendar( iYear, iMonth, iDay,
                                                            iHour, iMinute, iSecond );
    return( SetCal.getTime().getTime() );
    }

  /** 
   ***********************************************************************
   * This function scans in a date in the CRS DDMON format
   ***********************************************************************
   */
  static public long ScanCRSDateString(final String aDateString) throws Exception
    {
    return( ScanCRSDateTimeString(aDateString,"0000") );
    }

  static public long ScanCRSDateTimeString(final String aDateString, final String aTimeString) throws Exception
    {
    /*
    // scan in hours and minutes
    int iHour    = 0;
    int iMinutes = 0;
    {
    String sHour    = "00";
    String sMinutes = "00";

    final StringTokenizer tk = new StringTokenizer(aTimeString,"APNM /");
    if ( tk.countTokens() < 1 )
      throw new Exception("Unable to scan time string " + aTimeString);

    String sHourMin = tk.nextToken();

    // remove the preceeding "1|" if present
    final MatchInfo OffsetMatch = RegExpMatch.getFirstMatch(sHourMin,"^[1-9]?[|+#-]");
    if  ( OffsetMatch instanceof MatchInfo )
      sHourMin = sHourMin.substring( OffsetMatch.getLength() );

    // remove colon if present
    final int iColonPos = sHourMin.indexOf(':');
    if ( iColonPos >= 0 )
      sHourMin = sHourMin.substring(0,iColonPos) + sHourMin.substring(iColonPos + 1);

    switch ( sHourMin.length() )
      {
      case 1:
               sHour    = sHourMin;
               sMinutes = "00";
      case 2:
               sHour    = sHourMin;
               sMinutes = "00";
               break;
      case 3:
               sHour    = sHourMin.substring(0,1);
               sMinutes = sHourMin.substring(1);
               break;
      case 4:
               sHour    = sHourMin.substring(0,2);
               sMinutes = sHourMin.substring(2);
               break;
      default:
               throw new Exception("Unable to scan time string " + aTimeString);
      }

    // set the hour of day
    try
      {
      iHour = Integer.parseInt(sHour.trim());
      }
    catch (Exception e)
      {
      throw new Exception("Unable to scan hour in time string " + aTimeString);
      }

    // set the minutes
    try
      {
      iMinutes = Integer.parseInt(sMinutes.trim());
      }
    catch (Exception e)
      {
      throw new Exception("Unable to scan minutes in time string " + aTimeString);
      }
    }


    // set am/pm flags
    {
    boolean pmFlag = false;
    boolean amFlag = false;

    if ( RegExpMatch.matches(aTimeString,"[0-9] ?P") )
      pmFlag = true;
    else if ( RegExpMatch.matches(aTimeString,"[0-9] ?[AM]") )
      amFlag = true;

    // do time corrections based on am/pm flags
    if ( pmFlag && (iHour < 12) )
      iHour += 12;                      // ie: 400P -> 1600, 900P -> 2100, 1200N, 1200P -> 1200
    else if ( (amFlag == true) && (iHour == 12) )
      iHour = 0;                        // ie: 1200A -> 0000, 1200 -> 1200, 1200P -> 1200
    }


    // scan in the day and month
    int iDay   = 0;
    int iMonth = 0;
    {
    String sDay   = "";
    String sMonth = "";
    if ( aDateString.length() == 5 )
      {
      sDay   = aDateString.substring(0,2).trim();
      sMonth = aDateString.substring(2,5).toUpperCase();
      }
    else if ( aDateString.length() == 4 )
      {
      sDay   = aDateString.substring(0,1).trim();
      sMonth = aDateString.substring(1,4).toUpperCase();
      }
    else
      throw new Exception("Unable to scan date string " + aDateString);


    // get the day of month
    try
      {
      iDay = Integer.parseInt(sDay);
      }
    catch (Exception e)
      {
      throw new Exception("Unable to scan day of month in date string " + aDateString);
      }

    // get the month of the year
    if ( sMonth.equals("JAN") )
      iMonth = 0;
    else if ( sMonth.equals("FEB") )
      iMonth = 1;
    else if ( sMonth.equals("MAR") )
      iMonth = 2;
    else if ( sMonth.equals("APR") )
      iMonth = 3;
    else if ( sMonth.equals("MAY") )
      iMonth = 4;
    else if ( sMonth.equals("JUN") )
      iMonth = 5;
    else if ( sMonth.equals("JUL") )
      iMonth = 6;
    else if ( sMonth.equals("AUG") )
      iMonth = 7;
    else if ( sMonth.equals("SEP") )
      iMonth = 8;
    else if ( sMonth.equals("OCT") )
      iMonth = 9;
    else if ( sMonth.equals("NOV") )
      iMonth = 10;
    else if ( sMonth.equals("DEC") )
      iMonth = 11;
    else
      throw new Exception("Unable to scan Date string, Invalid month - " + aDateString);
    }

    // calculate date offset of time string  (this is how midnight rollovers are handled
    int iDayOffset = 0;
    {
    // check if time if for the next day
    if ( (aTimeString.indexOf('+') >= 0) || (aTimeString.indexOf('#') >= 0) || (aTimeString.indexOf('|') >= 0) )
      {
      final StringTokenizer tk = new StringTokenizer(aTimeString,"+#|");
      if ( tk.countTokens() >= 2 )
        {
        final String sField1 = tk.nextToken();
        final String sField2 = tk.nextToken();
        final String sHourMin;
        final String sDayOffset;

        if ( sField1.length() == 1 )
          {
          // sDayOffset = sField1;
          sDayOffset = "1";
          sHourMin   = sField2;
          }
        else
          {
          sHourMin   = sField1;
          sDayOffset = sField2;
          }

        try
          {
          iDayOffset = Integer.parseInt(sDayOffset.trim());
          }
        catch (Exception e)
          {
          throw new Exception("Unable to scan date offset in time string, time = " + aTimeString);
          }
        }
      else
        iDayOffset = 1;
      }
    else if ( aTimeString.indexOf('-') >= 0 )
      {   // check if time is for previous day
      final StringTokenizer tk = new StringTokenizer(aTimeString,"-");
      if ( tk.countTokens() >= 2 )
        {
        final String sHourMin   = tk.nextToken();
        final String sDayOffset = tk.nextToken();
        try
          {
          iDayOffset = 0 - Integer.parseInt(sDayOffset.trim());
          }
        catch (Exception e)
          {
          throw new Exception("Unable to scan date offset in time string, time = " + aTimeString);
          }
        }
      else
        iDayOffset = -1;
      }
    }

    // get the current date minus three days
    final GregorianCalendar CurrentCal = new GregorianCalendar();
    CurrentCal.add(GregorianCalendar.DATE,-3);

    // set the scanned input date to the current year
    final GregorianCalendar SetCal = new GregorianCalendar( CurrentCal.get(GregorianCalendar.YEAR),
                                                             iMonth, iDay, iHour, iMinutes );
    // factor in the date offset
    if ( iDayOffset != 0 )
      SetCal.add(GregorianCalendar.DATE,iDayOffset);

    // increment set year until given date is after current date (Sabre dates are always in the future)
    while ( CurrentCal.after(SetCal) )
      SetCal.add(GregorianCalendar.YEAR,1);

    return( SetCal.getTime().getTime() );
    */

    // parse up the times
    final int iMonth        = getMonth(aDateString);
    final int iDay          = getDay(aDateString);
    final int iTotalMinutes = getTotalMinutes(aTimeString);
    final int iDayOffset    = getDayOffset(aTimeString);
    final int iHour         = iTotalMinutes / 60;
    final int iMinutes      = iTotalMinutes % 60;

    // get the current date minus three days
    final GregorianCalendar CurrentCal = new GregorianCalendar();
    CurrentCal.add(GregorianCalendar.DATE,-3);

    // set the scanned input date to the current year
    final GregorianCalendar SetCal = new GregorianCalendar( CurrentCal.get(GregorianCalendar.YEAR),
                                                             iMonth, iDay, iHour, iMinutes );
    // factor in the date offset
    if ( iDayOffset != 0 )
      SetCal.add(GregorianCalendar.DATE,iDayOffset);

    // increment set year until given date is after current date (Sabre dates are always in the future)
    while ( CurrentCal.after(SetCal) )
      SetCal.add(GregorianCalendar.YEAR,1);

    return( SetCal.getTime().getTime() );
    }

  /** 
   ***********************************************************************
   * This functions returns the day of month
   ***********************************************************************
   */
  static private int getDay(final String aDateString) throws Exception
    {
    final MatchInfo match = RegExpMatch.getFirstMatch(aDateString,"^[0-9][0-9]?[A-Z][A-Z][A-Z]$");
    if ( match instanceof MatchInfo )
      {
      final int iLength = match.getLength() - 3;
      final String sDay = aDateString.substring(0,iLength);
      final int iDay = Integer.parseInt(sDay);
      return(iDay);
      }
    else
      throw new Exception("Invalid date string " + aDateString);
    }

  /** 
   ***********************************************************************
   * This functions returns the month in year 0 - 11
   ***********************************************************************
   */
  static private int getMonth(final String aDateString) throws Exception
    {
    final MatchInfo match = RegExpMatch.getFirstMatch(aDateString,"^[0-9][0-9]?[A-Z][A-Z][A-Z]$");
    if ( match instanceof MatchInfo )
      {
      final int iLength = match.getLength() - 3;
      final String sMonth = aDateString.substring(iLength);

      // get the month of the year
      if ( sMonth.equals("JAN") )
        return(0);
      else if ( sMonth.equals("FEB") )
        return(1);
      else if ( sMonth.equals("MAR") )
        return(2);
      else if ( sMonth.equals("APR") )
        return(3);
      else if ( sMonth.equals("MAY") )
        return(4);
      else if ( sMonth.equals("JUN") )
        return(5);
      else if ( sMonth.equals("JUL") )
        return(6);
      else if ( sMonth.equals("AUG") )
        return(7);
      else if ( sMonth.equals("SEP") )
        return(8);
      else if ( sMonth.equals("OCT") )
        return(9);
      else if ( sMonth.equals("NOV") )
        return(10);
      else if ( sMonth.equals("DEC") )
        return(11);
      }

    throw new Exception("Invalid date string " + aDateString);
    }

  /** 
   ***********************************************************************
   * This functions returns the year based on the given month and
   * day
   ***********************************************************************
   */
  static private int getYear(final int iMonth, final int iDay) throws Exception
    {
    return(0);
    }

  /** 
   ***********************************************************************
   * This functions returns the year based on the given month and
   * day
   ***********************************************************************
   */
  static private int getDayOffset(final String aTimeString) throws Exception
    {
    if ( !(aTimeString instanceof String) )
      return(0);
    
    final StringBuffer sTimeBuf = new StringBuffer(aTimeString);

    {
    // extract prefix offset string and determine number of days
    final String DAY_OFFSET_PREFIX = "^ *[1-4]? *[|+#-]";
    String sDayOffset = removeLastMatch(sTimeBuf,DAY_OFFSET_PREFIX);
    if ( GnrcFormat.NotNull(sDayOffset) )
      {
      sDayOffset = sDayOffset.trim();
      final int iNumDays;
      if ( RegExpMatch.matches(sDayOffset,"^[1-4]") )
        iNumDays = Integer.parseInt(sDayOffset.substring(0,1));
      else
        iNumDays = 1;

      if ( sDayOffset.indexOf('-') >= 0 )
        return(0 - iNumDays);
      else
        return(iNumDays);
      }
    }


    {
    // extract suffix offset string and determine number of days
    final String DAY_OFFSET_SUFFIX = "[|+#-] *[1-4]? *$";
    String sDayOffset = removeLastMatch(sTimeBuf,DAY_OFFSET_SUFFIX);
    if ( GnrcFormat.NotNull(sDayOffset) )
      {
      sDayOffset = sDayOffset.trim();
      final int iNumDays;
      if ( RegExpMatch.matches(sDayOffset,"[1-4]$") )
        iNumDays = Integer.parseInt(sDayOffset.substring(1).trim());
      else
        iNumDays = 1;

      if ( sDayOffset.indexOf('-') >= 0 )
        return(0 - iNumDays);
      else
        return(iNumDays);
      }
    }

    return(0);
    }

  /** 
   ***********************************************************************
   * This functions returns the total number of minutes represented
   * by the input time string
   ***********************************************************************
   */
  static private int getTotalMinutes(final String aTimeString) throws Exception
    {
    if ( !(aTimeString instanceof String) )
      return(0);

    String sDebug;
    final StringBuffer sTimeBuf = new StringBuffer(aTimeString);

    // remove any day offset modifiers
    final String DAY_OFFSET_PREFIX = "^ *[1-4]? *[|+#-]";
    final String DAY_OFFSET_SUFFIX = "[|+#-] *[1-4]? *$";
    removeLastMatch(sTimeBuf,DAY_OFFSET_PREFIX);
    sDebug = sTimeBuf.toString();
    removeLastMatch(sTimeBuf,DAY_OFFSET_SUFFIX);
    sDebug = sTimeBuf.toString();


    // read hour modifier (ie: AM, PM, N, or M)
    final String HOUR_MOD_SUFFIX = "(AM?|PM?|M|N) *$";
    final String sHourMod = removeLastMatch(sTimeBuf,HOUR_MOD_SUFFIX);
    sDebug = sTimeBuf.toString();

    // read hours and minutes
    final String sTime = sTimeBuf.toString().trim();
    int iHour;
    final int iMinute;
    if ( RegExpMatch.matches(sTime,"^[0-9][0-9][0-9][0-9]?$") )
      {        // 3 or 4 digits only must be hour and minutes
      final int iLength = sTime.length() - 2;

      iHour   = Integer.parseInt(sTime.substring(0,iLength));
      iMinute = Integer.parseInt(sTime.substring(iLength));
      }
    else if ( RegExpMatch.matches(sTime,"^[0-9][0-9]?$") )
      {        // 1 or 2 digits only must be hour and no minutes
      iHour   = Integer.parseInt(sTime);
      iMinute = 0;
      }
    else
      throw new Exception("Invalid time string " + aTimeString);


    // apply hour modifier
    if ( sHourMod instanceof String )
      {
      if ( iHour == 12 )
        {
        if ( sHourMod.startsWith("A") || sHourMod.startsWith("M") )
          iHour = 0;
        }
      else if ( sHourMod.startsWith("P") && (iHour < 12) )
        iHour += 12;
      }

    // return total minutes
    final int iTotalMinutes = (iHour * 60) + iMinute;
    return( iTotalMinutes );
    }

  /** 
   ***********************************************************************
   * This function prints a date in the CRS DDMON format
   ***********************************************************************
   */
  static public String FormatCRSDate(final long aDate)
    {
    final SimpleDateFormat CrsDate = new SimpleDateFormat("ddMMM");
    final String sDate = CrsDate.format( new Date(aDate) ).toUpperCase();
    return( sDate );
    }

  /** 
   ***********************************************************************
   * This function converts a string in HH:MM format to minutes
   ***********************************************************************
   */
  static public int getElapsedMinutes(final String aElapsedTime)
    {
    final StringTokenizer tfields = new StringTokenizer(aElapsedTime,".: ");
    if ( tfields.countTokens() >= 2 )
      {
      final String sHours   = tfields.nextToken();
      final String sMinutes = tfields.nextToken();

      final int iHours   = Integer.parseInt(sHours);
      final int iMinutes = Integer.parseInt(sMinutes);

      final int iTotalMinutes = (iHours * 60) + iMinutes;
      return (iTotalMinutes);
      }
    else if ( tfields.countTokens() == 1 )
      {
      final String sMinutes = tfields.nextToken();
      final int iMinutes = Integer.parseInt(sMinutes);
      return(iMinutes);
      }
    else
      return(0);
    }

 /** 
  ***********************************************************************
  * This function is used to clean up the responses received from a host
  * and to combine them into a single non-repeating string.
  *
  * It defines the following constants:
  * <PRE>
  *   MAX_LINE_WIDTH       = 64;
  *   PROMPT_PATTERN       = "&gt;[\r\n]?[\r\n]?$";
  *   CONTINUATION_PATTERN = "[)#]&gt;[\r\n]?[\r\n]?$";
  * </PRE>
  * which it passes to it's overloaded brethen.
  *
  * @param  aInputStrings
  *   an array of strings, typically the result of compiling all the
  *   lines returned by successive page downs in a TA
  * @see GnrcCrs.getAllHostResponses
  ***********************************************************************
  */
  public static String getCombinedHostResponse(final String[] aInputStrings)
    {
    final int MAX_LINE_WIDTH          = 64;
  //final String PROMPT_PATTERN       = ">[\r\n]?[\r\n]?$";
  //final String CONTINUATION_PATTERN = "[)#]>[\r\n]?[\r\n]?$";
    final String PROMPT_PATTERN       = "(?s)>[\r\n]*$";
    final String CONTINUATION_PATTERN = "(?s)[\\)\\#\\|]\\>?[\r\n]*$";

    return( getCombinedHostResponse(aInputStrings,MAX_LINE_WIDTH,PROMPT_PATTERN,CONTINUATION_PATTERN) );
    }


  /**
  ***********************************************************************
  * This function combines the responses received from a host
  * into a single non-repeating string.
  ***********************************************************************
  */
  public static String getCombinedHostResponse(final String[] aInputStrings,
                                               final int aLineWidth,
                                               final String aPrompt,
                                               final String aContinuation)
    {
    String sDebug;

    // check the input array
    if ( (aInputStrings instanceof String[]) == false )
      return("");

    // allocate an array of strings the same size as the input array
    final String[] NewResponses = new String[aInputStrings.length];

    for ( int i = 0; i < aInputStrings.length; i++ )
      {
      sDebug = aInputStrings[i];

      // make sure all lines terminate in a CR/LF pair
			// and are no longer than 64 chars
      NewResponses[i] = addLineFeeds(aInputStrings[i],aLineWidth);
      sDebug = NewResponses[i];

      // remove continuation info at end of each response (can also appear on first line indicating there is prior data)
      NewResponses[i] = replaceAllMatches(NewResponses[i],aContinuation,"\r\n");
      sDebug = NewResponses[i];

      // remove prompt from the end of each response
      NewResponses[i] = replaceAllMatches(NewResponses[i],aPrompt,"");
      sDebug = NewResponses[i];

      NewResponses[i] = trimLinesRight(NewResponses[i]);
      sDebug = NewResponses[i];
      }

    for ( int i = 1; i < NewResponses.length; i++ )
      {
      sDebug = NewResponses[i];
      // remove repeating headers from second and subsequent responses
      NewResponses[i] = removeHeader(NewResponses[0],NewResponses[i],4);
      sDebug = NewResponses[i];

      // the above uses the StringToArray method which, after a string is run
      // through the method trimLinesRight() as is the case above, has the
      // unintended consequence of removing all empty lines from the second and
      // subsequent response - hence, do the same for the first
      // response to keep things consistent - this may have to be fixed later
      NewResponses[0] = ArrayToString(StringToArray(NewResponses[0]));
      sDebug = NewResponses[0];

			// remove repeating data
      NewResponses[i] = removeOverlap(NewResponses[i - 1],NewResponses[i]);
      sDebug = NewResponses[i];
      }

    // convert array into a single string
    final String sOutStr = ArrayToString(NewResponses);
    return( sOutStr );
    }

 /** 
  ***********************************************************************
  * This function strips trailing characters that might be on the end of
  * the given string
  ***********************************************************************
  */
  private static String removeLastMatch(final String aInputString, final String aPattern)
    {
    final StringBuffer sOutStr = new StringBuffer(aInputString);
    removeLastMatch(sOutStr,aPattern);
    final String sResult = sOutStr.toString();
    return( sResult );
    }


  private static String removeLastMatch(final StringBuffer aInputString, final String aPattern)

    {
    try
      {
      if ( (aInputString instanceof StringBuffer) && (aPattern instanceof String) )
        {
        final MatchInfo m_info = RegExpMatch.getLastMatch( aInputString.toString() ,aPattern);
        if ( m_info instanceof MatchInfo )
          {           // return the string that you removed
          aInputString.delete(m_info.MatchPosition,m_info.MatchPosition + m_info.getLength() );
          return(m_info.MatchString);
          }
        }

      return(null);
      }
    catch (Exception e)
      {
      return(null);
      }
    }

  /*
  private static String removeAllMatches(final String aInputString, final String aPattern)
    {
    final StringBuffer sOutStr = new StringBuffer(aInputString);
    removeAllMatches(sOutStr,aPattern);
    final String sResult = sOutStr.toString().trim() + "\r\n";
    return( sResult );
    }
  */
  
  private static void removeAllMatches(final StringBuffer aInputString, final String aPattern)
    {
    try
      {
      if ( (aInputString instanceof StringBuffer) && (aPattern instanceof String) )
        {
        MatchInfo m_info = RegExpMatch.getLastMatch( aInputString.toString() ,aPattern);
        while ( m_info instanceof MatchInfo )
          {           // return the string that you removed
          aInputString.delete(m_info.MatchPosition,m_info.MatchPosition + m_info.getLength() );
          m_info = RegExpMatch.getLastMatch( aInputString.toString() ,aPattern);
          }
        }
      }
    catch (Exception e)
      {
      }
    }

 /**
  ***********************************************************************
  * This function replaces all occurrances of the given pattern with the
  * given string
  ***********************************************************************
  */

  protected static String replaceAllMatches(final String aInputString, final String aPattern, final String aReplace)
    {
    final StringBuffer sOutStr = new StringBuffer(aInputString);
    replaceAllMatches(sOutStr,aPattern,aReplace);
    final String sResult = sOutStr.toString();
    return( sResult );
    }

  private static void replaceAllMatches(final StringBuffer aInputString, final String aPattern, final String aReplace)
    {
    try
      {
      if ( (aInputString instanceof StringBuffer) && (aPattern instanceof String) )
        {
        MatchInfo m_info = RegExpMatch.getLastMatch( aInputString.toString() ,aPattern);
        while ( m_info instanceof MatchInfo )
          {           // return the string that you removed
          aInputString.delete(m_info.MatchPosition,m_info.MatchPosition + m_info.getLength() );
          aInputString.insert(m_info.MatchPosition,aReplace);
          m_info = RegExpMatch.getLastMatch( aInputString.toString() ,aPattern);
          }
        }
      }
    catch (Exception e)
      {
      }
    }


 /**
  ***********************************************************************
  * This function is used to remove headers from 'Move Down' responses, which
  * repeat from the original response
  *
  * @param String1 the original response
  * @param String2 a subsequent move-down
  * @param aMaxHeaderLines 
  *   the maximum number of lines in which to search for repeating headers
  ***********************************************************************
  */
  protected static String removeHeader(
      final String String1, final String String2, final int aMaxHeaderLines)
    {
    final String[] sArray1 = StringToArray(String1);
    final String[] sArray2 = StringToArray(String2);

    if ( (sArray1 instanceof String[]) && (sArray2 instanceof String[]) )
      {
      // null out all matching lines and break on the first line that is
      // different - this replaces the block below
      for (int i=0; i<sArray1.length && i<sArray1.length && i<aMaxHeaderLines; i++)
        {
          if ( sArray2[i] instanceof String && sArray2[i].equals(sArray1[i]) )
            {
            sArray2[i] = null;
            }
          else
            break;
        }

      /*
      // this was here before - it caused the removal of the second line of
      // sArray2, for example, when the it matched the fourth line of sArray1
      // which is incorrect behavior
      // skip lines that match the first array
      for ( int i2 = 0; (i2 < sArray2.length) && (i2 < aMaxHeaderLines); i2++ )
        {
        for ( int i1 = 0; (i1 < sArray1.length) && (i1 < aMaxHeaderLines); i1++ )
          {
          if ( sArray2[i2] instanceof String )
            {
            if ( sArray2[i2].equals(sArray1[i1]) )
              sArray2[i2] = null;
            }
          }
        }
      */
      }

    return( ArrayToString(sArray2) );
    }

 /** 
  ***********************************************************************
  * This function strips repeated information from the input string list
  ***********************************************************************
  */
  public static String getNonRepeatedResponse(final String[] aInputStrings)
    {
    if ( (aInputStrings instanceof String[]) == false )
      return("");

    final String[] newlines = removeRepeatedResponse(aInputStrings);

    return( ArrayToString(newlines) );
    }


  public static String[] removeRepeatedResponse(final String[] aInputStrings)
    {
    if ( (aInputStrings instanceof String[]) == false )
      return(null);

    if ( aInputStrings.length < 1 )
      return(null);

    // allocate new string list
    final String[] newlines = new String[ aInputStrings.length ];

    // copy the non-overlapping info from the source list
    newlines[0] = aInputStrings[0];
    for ( int i = 1; i < aInputStrings.length; i++ )
      newlines[i] = removeOverlap(newlines[i - 1],aInputStrings[i]);

    return(newlines);
    }

 /**
  ***********************************************************************
  * This function returns a string that represents the non-overlapping
  * characters from the second string
  ***********************************************************************
  */
  private static String removeOverlap(final String aFirst, final String aSecond)
    {
    // convert to an array of strings for each line
    final String[] sFirstPage  = StringToArray(aFirst);
    final String[] sSecondPage = StringToArray(aSecond);

    // get the number of overlapping lines
    final int iNumOverlap = numOverlappingLines(sFirstPage,sSecondPage);
    if ( iNumOverlap > 0 )
      {
      // if theres an overlap, copy the new info to a new array
      final String[] sNewInfo = new String[ sSecondPage.length - iNumOverlap ];
      for ( int i = 0; i < sNewInfo.length; i++ )
        sNewInfo[i] = sSecondPage[ iNumOverlap + i ];

      final String sResult = ArrayToString(sNewInfo);
      return(sResult);
      }
    else
      return(aSecond);


    /*
    // remove trailing CR/LF from first string since this is not part of the PNR data
    // (we needed this done for 1V-Q8DJ5I)
    final String sFirst = trimTrailingLineFeeds(aFirst);

    // remove leading CR/LF from second string since this is not part of the PNR data
    final String sSecond = trimLeadingLineFeeds(aSecond);


    String sSecondBegin;        // leading portion of second string
    int iDebug;
    for ( int i = sSecond.length(); i > 2; i-- )
      {
      if ( (i % 10) == 0 )
        iDebug = i;

      sSecondBegin = sSecond.substring(0,i);
      if ( sSecondBegin.trim().length() > 0 )
        {
        if ( sFirst.endsWith(sSecondBegin) )
          {
          String sSecondEnd = sSecond.substring(i);
          sSecondEnd = trimLeadingLineFeeds(sSecondEnd);
          return( sSecondEnd );
          }
        }

      }

    return(aSecond);
    */
    }

 /**
  ***********************************************************************
  * This function returns the number of lines in the given string
  ***********************************************************************
  */
  protected static int numOverlappingLines(
      final String[] aFirstPage, final String[] aSecondPage)
    {
    // do we have anything to match ?
    if( aFirstPage  == null || aFirstPage.length  == 0 ||
        aSecondPage == null || aSecondPage.length == 0)
      return 0;
    
    // get the first line of the second page
    String sL1P2 = aSecondPage[0];

    // return an array of the line numbers in the first page 
    // that match the first line in the second page
    // if none found, we have no overlap
    int[] aryMatchingLineNums = getMatchingLineNumbers(sL1P2, aFirstPage);
    if (aryMatchingLineNums == null)
      return 0;

    // starting at each line in the first page that matches the first line of
    // the second page, try to determine whether the remainder of the lines in
    // the first page match the lines at the top of the second page
    for (int i=0; i <  aryMatchingLineNums.length; i++) 
      {
      int beginLine = aryMatchingLineNums[i];
      
      // If there are not enough lines in the second page to match to the
      // remainder of the first page, go on to the next matching line
      if ( (aFirstPage.length - beginLine) > aSecondPage.length )
        continue;

      // see if the remainder of the lines in page 1 match 
      // the top lines in pages 2
      boolean foundMatch = true;
      for (int iP1 = beginLine, iP2 = 0; iP1 < aFirstPage.length; iP1++, iP2++)
        {
        if (aFirstPage[iP1].equals(aSecondPage[iP2]) == false)
          {
          foundMatch = false;
          break;
          }
        }

      // return the number of matching lines upon finding the first match
      if (foundMatch)
        return (aFirstPage.length - beginLine);
      }

    // we did not find anything
    return 0;

    /* // broken implementation:
    if ( (aFirstPage instanceof String[]) && (aSecondPage instanceof String[]) )
      {
      int iFirstLineNum  = aFirstPage.length - 1;
      int iSecondLineNum = 0;
      String sFirst;
      String sSecond;

      while ( (iFirstLineNum >= 0) && (iSecondLineNum < aSecondPage.length) )
        {
        sFirst  = aFirstPage[iFirstLineNum].trim();
        sSecond = aSecondPage[iSecondLineNum].trim();

        if ( sFirst.equals(sSecond) )
          {
          iFirstLineNum--;
          iSecondLineNum++;
          }
        else
          break;
        }

      return(iSecondLineNum);
      }
    else
      return(0);
    */
    }

  /**
   ***********************************************************************
   * Returns an array of index numbers corresponding to the entries in an array
   * of Strings (the haystack) which match the String provided (the needle) ;
   * returns null if either the needle or the haystack are null.
   *
   * @param needle
   *   the string that we are trying to match
   * @param haystack
   *   the strings in which to look for a match
   ***********************************************************************
   */
  protected static int[] getMatchingLineNumbers(String needle, String[] haystack)
    {
    if (haystack == null || needle == null)
      return null;

    ArrayList lstMatchingIndexes = new ArrayList();
    
    for (int i=0; i < haystack.length; i++)
      {
      if (needle.equals(haystack[i]))
        lstMatchingIndexes.add(new Integer(i));
      }

    int[] aryMatchingIndexes = new int[lstMatchingIndexes.size()];

    for (int i=0; i < lstMatchingIndexes.size(); i++)
      {
      aryMatchingIndexes[i] = ((Integer)lstMatchingIndexes.get(i)).intValue();
      }
    
    return aryMatchingIndexes;
    
    } // end numMatchingStrings


 /**
  ***********************************************************************
  * This procedure trims CR and LF characters from the leading end
  * of a string
  ***********************************************************************
  */
 private static String trimLeadingLineFeeds(final String aInString)
   {
   final StringBuffer sBuf = new StringBuffer(aInString);

   char firstChar;
   while ( sBuf.length() > 0 )
     {
     firstChar = sBuf.charAt(0);
     if ( (firstChar == '\r') || (firstChar == '\n') )
       sBuf.deleteCharAt(0);
     else
       break;
     }

   return( sBuf.toString() );
   }

 /** 
  ***********************************************************************
  * This procedure trims CR and LF characters from the trailing end
  * of a string
  ***********************************************************************
  */
 private static String trimTrailingLineFeeds(final String aInString)
   {
   final StringBuffer sBuf = new StringBuffer(aInString);

   char lastChar;
   int iLastPos;
   while ( sBuf.length() > 0 )
     {
     iLastPos = sBuf.length() - 1;
     lastChar = sBuf.charAt(iLastPos);
     if ( (lastChar == '\r') || (lastChar == '\n') )
       sBuf.deleteCharAt(iLastPos);
     else
       break;
     }

   return( sBuf.toString() );
   }

 /** 
  ***********************************************************************
  * This procedure adds CR/LF pairs to any line that exceeds a
  * certain length
  ***********************************************************************
  */
 public static String addLineFeeds(final String aInString)
   {
   return( addLineFeeds(aInString,64) );
   }

 public static String addLineFeeds(final String aInString, final int aMaxWidth)
   {
   String sSegment;
   final StringBuffer sOutString = new StringBuffer("");
   final StringTokenizer lines   = new StringTokenizer(aInString,"\r\n");


   // loop through all the lines
   while ( lines.hasMoreTokens() )
     {
     final String sLine    = lines.nextToken();
     final int iLineLength = sLine.length();

     for ( int iStartPos = 0, iEndPos = aMaxWidth; iStartPos < iLineLength; iStartPos += aMaxWidth, iEndPos += aMaxWidth )
       {
       if ( iEndPos < iLineLength )
         sSegment = sLine.substring(iStartPos,iEndPos);
       else
         sSegment = sLine.substring(iStartPos);

       sOutString.append(sSegment + "\r\n");
       }
     }

   return( sOutString.toString() );
   }

 /** 
  ***********************************************************************
  * This function strips blank spaces from the end of every input line
  ***********************************************************************
  */
 public static String trimLinesRight(final String aInString)
   {
   String sLine;
   final StringBuffer sOutString = new StringBuffer("");
   final StringTokenizer lines   = new StringTokenizer(aInString,"\r\n");

   // loop through all the lines a
   while ( lines.hasMoreTokens() )
     {
     sLine = "." + lines.nextToken();
     sLine = sLine.trim().substring(1);

     sOutString.append(sLine + "\r\n");
     }

   return( sOutString.toString() );
   }

 /** 
  ***********************************************************************
  * This function converts a string delimited with carriage returns
  * and line feeds into an array of strings; note that running trimLinesRight
  * prior to this method has the unintended consequence that all blank
  * lines in the original string will not be included in the array; 
  * hence running ArrayToString(StringToArray(trimLinesRight(aString)) will
  * remove all blank lines from aString, a somewhat unintended consequence that
  * does not seem to hurt
  * anything for now.
  ***********************************************************************
  */
 public static String[] StringToArray(final String aInString)
   {
   // make sure a string with text has been passed in
   if ( (aInString instanceof String) == false )
     return(null);

   if ( aInString.length() == 0 )
     return(null);

   final StringTokenizer lines = new StringTokenizer(aInString,"\r\n");

   final String[] sResult = new String[ lines.countTokens() ];

   // loop through all the lines
   int iLineNum = 0;
   while ( lines.hasMoreTokens() )
     sResult[iLineNum++] = lines.nextToken();

   return( sResult );
   }

 /** 
  ***********************************************************************
  * This function converts an array of strings into a single string by turning 
  * the each individual string in the array into a line in the final string
  ***********************************************************************
  */
 public static String ArrayToString(final String[] aInString)
   {
   // make sure a string with text has been passed in
   if ( (aInString instanceof String[]) == false )
     return("");

   if ( aInString.length == 0 )
     return("");

   final StringBuffer sOutString = new StringBuffer("");
   for ( int i = 0; i < aInString.length; i++ )
     {
     if ( aInString[i] instanceof String )
       {
       if ( aInString[i].endsWith("\r") || aInString[i].endsWith("\n") )
         sOutString.append( aInString[i] );
       else
         sOutString.append( aInString[i] + "\r\n" );
       }
     }

   return( sOutString.toString() );
   }

 /** 
  ***********************************************************************
  * This function looks at each individual line and determines if
  * it's a new line or a continuation of the previous line based on
  * the given start pattern
  ***********************************************************************
  */
 public static String findNewLines(final String aInString, final String aStartPattern)
   {
   final String[] linelist = StringToArray(aInString);

   final StringBuffer sOutString = new StringBuffer();

   if ( linelist instanceof String[] )
     {
     for ( int i = 0; i < linelist.length; i++ )
       {
       try
         {
         if ( sOutString.toString().length() > 0 )
           {
           if ( RegExpMatch.matches(linelist[i],aStartPattern) )
             sOutString.append("\r\n");
           else if ( linelist[i].startsWith("OPERATED BY ") )
             sOutString.append(" ");
           }
         }
       catch (Exception e)
         {}

       sOutString.append( linelist[i] );
       }
     }

   return( sOutString.toString() );
   }

 /** 
  ***********************************************************************
  * This function extracts a portion of the given source string
  * between the starting pattern and ending pattern - the string returned does
  * not include the beginning and ending matching characters
  ***********************************************************************
  */
 public static String extractString(final String aSourceString, 
                                    final String aStartPattern, 
                                    final String aEndPattern) 
   {      
   return( extractString(aSourceString,aStartPattern,aEndPattern,false,false) );
   }


 /** 
  ***********************************************************************
  * This function extracts a portion of the given source string
  * between the starting pattern and ending pattern, and provides the option 
  * to include either one of the beginning or ending matching characters
  ***********************************************************************
  */
 public static String extractString(final String aSourceString,
                                    final String aStartPattern, final String aEndPattern,
                                    final boolean aIncludeStartChars,
                                    final boolean aIncludeEndChars)
   {
   try
     {

     // set starting position
     final int iStartPos;                  // position where substring starts
     final int iEndSearchPos;              // position to begin searching for end pattern
     if ( aStartPattern instanceof String )
       {
       final MatchInfo startmatch = RegExpMatch.getFirstMatch(aSourceString,aStartPattern);
       if ( startmatch instanceof MatchInfo )
         {
         iEndSearchPos = startmatch.getEndPosition();
         if ( aIncludeStartChars )                  // found the start pattern
           iStartPos = startmatch.MatchPosition;
         else
           iStartPos = startmatch.getEndPosition();
         }
       else
         return("");       // start pattern never found, return null string
       }
     else
       {
       iStartPos     = 0;  // no start pattern defined, so start at beginning of string
       iEndSearchPos = 0;
       }


     // set ending position
     final int iEndPos;                  // position where substring ends
     if ( aEndPattern instanceof String )
       {
       final MatchInfo endmatch = RegExpMatch.getMatchFromPosition(aSourceString,aEndPattern,iEndSearchPos,-1);
       if ( endmatch instanceof MatchInfo )
         {
         if ( aIncludeEndChars )                  // found the start pattern
           iEndPos = endmatch.getEndPosition();
         else
           iEndPos = endmatch.MatchPosition;
         }
       else
         iEndPos = aSourceString.length();     // end pattern never found, so stop at end of string
       }
     else
       iEndPos = aSourceString.length();     // no end pattern defined, so stop at end of string


     // extract the substring
     final String sResult;
     if ( iEndPos >= iStartPos )
       sResult = aSourceString.substring(iStartPos,iEndPos);
     else
       sResult = aSourceString.substring(iStartPos);

     return(sResult);
     }
   catch (Exception e)
     {
     return("");
     }

   }

 /** 
  ***********************************************************************
  * This function extracts a portion of the given source string
  * between the starting pattern and ending positions
  ***********************************************************************
  */
 public static String getSubstring(final String aSourceString,
                                       final int aStartPos, final int aEndPos)
  {
  return( getSubstring(aSourceString,aStartPos,aEndPos,"") );
  }

 public static String getSubstring(final String aSourceString,
                                   final int aStartPos, final int aEndPos,
                                   final String aDefault)
   {
   if ( aSourceString instanceof String )
     {
     if ( aEndPos >= aStartPos )
       {
       if ( aSourceString.length() >= aEndPos )
         return( aSourceString.substring(aStartPos,aEndPos) );
       else if ( aSourceString.length() >= aStartPos )
         return( aSourceString.substring(aStartPos) );
       }
     }

   return(aDefault);
   }

 /** 
  ***********************************************************************
  * This function extracts a portion of the given source string
  * after the given starting pattern, defined by the given delimiters
  ***********************************************************************
  */
 public static String getField(final String aInputString,    // string to search through
                               final String aFieldPattern)   // field pattern to search for

   {
   return( getField(aInputString,aFieldPattern," ",1) );
   }


 public static String getField(final String aInputString,    // string to search through
                               final String aFieldPattern,   // field pattern to search for
                               final String aDelimiters,     // list of delimiter characters for field value
                               final int aFieldNum)          // which field value to return
  {
  try
    {
    // find the line that contains the given field pattern
    final StringTokenizer lines = new StringTokenizer(aInputString,"\r\n");
    String sLine;
    MatchInfo matchinfo;

    while ( lines.hasMoreTokens())
      {
      sLine = lines.nextToken();

      matchinfo = RegExpMatch.getFirstMatch(sLine,aFieldPattern);
      if ( matchinfo instanceof MatchInfo )
        {
        sLine = sLine.substring( matchinfo.getEndPosition() );

        final StringTokenizer fields = new StringTokenizer(sLine,aDelimiters);
        String sField;
        for ( int iFieldNum = 1; fields.hasMoreTokens(); iFieldNum++ )
          {
          sField = fields.nextToken();
          if ( iFieldNum >= aFieldNum )
            return(sField);
          }

        return(null);
        }

      }
    }
  catch (Exception e)
    {}

  return(null);
  }

  /**
   ***********************************************************************
   * this function returns true if any of the possible response
   * strings appear anywhere within the actual response
   ***********************************************************************
   */
  static public boolean containedWithin(final String aActualResponse, final String[] aPossibleResponseList)
    {
    if ( (aActualResponse instanceof String) && (aPossibleResponseList instanceof String[]) )
      {
      for ( int i = 0; i < aPossibleResponseList.length; i++ )
        {
        if ( aActualResponse.indexOf(aPossibleResponseList[i]) >= 0 )
          return(true);
        }
      }

    return(false);
    }

  /**
   ***********************************************************************
   * this function returns the index of the matching element or -1 if no match
   ***********************************************************************
   */
  static public int itemIndex(final String aActualResponse, final String[] aPossibleResponseList)
    {
    if ( (aActualResponse instanceof String) && (aPossibleResponseList instanceof String[]) )
      {
      for ( int i = 0; i < aPossibleResponseList.length; i++ )
        {
        if ( aActualResponse.equals(aPossibleResponseList[i]) )
          return(i);
        }
      }

    return(-1);
    }

  /**
   ***********************************************************************
   * Main function for unit test
   ***********************************************************************
   */
  public static void main(String[] args)
    {
    String sResult;
    long date = 0;

    try
      {
      date    = ScanCRSDateTimeString("22OCT","1600");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      date    = ScanCRSDateTimeString("22OCT","2A");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      date    = ScanCRSDateTimeString("22OCT","210A");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      date    = ScanCRSDateTimeString("22OCT","1# 900A");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      date    = ScanCRSDateTimeString("22OCT","300A-1");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      date    = ScanCRSDateTimeString("22OCT","245P +3");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      date    = ScanCRSDateTimeString("22OCT","| 415P");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      date    = ScanCRSDateTimeString("22OCT","1623 |3");
      sResult = GnrcFormat.FormatReadableDateTime(date);

      sResult = "";
      date    = 0;
      }
    catch (Exception e)
      {
      }

    }

}
