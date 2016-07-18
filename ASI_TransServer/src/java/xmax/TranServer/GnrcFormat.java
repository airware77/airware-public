package xmax.TranServer;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class GnrcFormat
{

  /** 
   ***********************************************************************
   * Takes a long (type) representation of a date and returns it in yyyyMMdd
   * format; if the long passed is 0 it returns 8 spaces.
   ***********************************************************************
   */
  static public String FormatLongDate(final long aDate)
    {
    if ( aDate > 0 )
      {
      final SimpleDateFormat dt_format = new SimpleDateFormat("yyyyMMdd");
      final String sResult = dt_format.format( new Date(aDate) ).toString();
      return(sResult);
      }
    else
      return("        ");
    }

  /** 
   ***********************************************************************
   * Takes a long (type) representation of a DateTime and returns it in
   * yyyyMMddHHmm format
   ***********************************************************************
   */
  static public String FormatLongDateTime(final long aDateTime)
    {
    if ( aDateTime > 0 )
      {
      final SimpleDateFormat dt_tm_format = new SimpleDateFormat("yyyyMMddHHmm");
      final String sResult = dt_tm_format.format( new Date(aDateTime) ).toString();
      return(sResult);
      }
    else
      return("            ");
    }

  /** 
   ***********************************************************************
   * Takes a long (type) representation of a DateTime and returns it in
   * human readable format MM/dd/yyyy hh:mm
   ***********************************************************************
   */
  static public String FormatReadableDate(final long aDateTime)
    {
    if ( aDateTime > 0 )
      {
      final SimpleDateFormat dt_tm_format = new SimpleDateFormat("MM/dd/yyyy");
      final String sResult = dt_tm_format.format( new Date(aDateTime) ).toString();
      return(sResult);
      }
    else
      return("null date");
    }

  static public String FormatReadableDateTime(final long aDateTime)
    {
    if ( aDateTime != 0 )
      {
      final SimpleDateFormat dt_tm_format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
      final String sResult = dt_tm_format.format( new Date(aDateTime) ).toString();
      return(sResult);
      }
    else
      return("null date");
    }

  /**
   ***********************************************************************
   * Takes a long (type) representation of a Date and returns it in Airware
   * format such as: 0yyMMdd or 1yyMMdd , where 0 stands for the 20th century
   * and 1 stands for the 21st century (including the year 2000)
   ***********************************************************************
   */
  static public String FormatAirwareDate(final long aDateTime)
    {
    if ( aDateTime > 0 )
      {
      // figure out which century
      final String sCentury;
      final int iYear = m_GetYear(aDateTime);
      if ( (iYear >= 2000) && (iYear <= 2099) )
        sCentury = "1";
      else
        sCentury = "0";

      final SimpleDateFormat dt_tm_format = new SimpleDateFormat("yyMMdd");
      final String sDate = dt_tm_format.format( new Date(aDateTime) ).toString();

      return(sCentury + sDate);
      }
    else
      return("       ");
    }

  /** 
   ***********************************************************************
   * Takes a long (type) representation of a time and returns it in Airware
   * format: HHmmss (military time);
   ***********************************************************************
   */
  static public String FormatAirwareTime(final long aDateTime)
    {
    if ( aDateTime > 0 )
      {
      final SimpleDateFormat dt_tm_format = new SimpleDateFormat("HHmmss");
      final String sResult = dt_tm_format.format( new Date(aDateTime) ).toString();
      return(sResult);
      }
    else
      return("      ");
    }

  /** 
   ***********************************************************************
   * Takes a long (type) representation of a Date and Time and returns the
   * concatenation of the FormatAirwareDate and FormatAirwareTime methods
   ***********************************************************************
   */
  static public String FormatAirwareDateTime(final long aDateTime)
    {
    final String sResult = FormatAirwareDate(aDateTime) + FormatAirwareTime(aDateTime);
    return( sResult );
    }

  /** 
   ***********************************************************************
   * Retrieves the year from a long (type) representation of a date and time
   ***********************************************************************
   */
  static private int m_GetYear(final long aDateTime)
    {
    // determine the given year
    final GregorianCalendar given_date = new GregorianCalendar();
    given_date.setTime( new Date(aDateTime) );
    final int iYear = given_date.get(GregorianCalendar.YEAR);

    return(iYear);
    }

  /** 
   ***********************************************************************
   * This function prints a date in the CRS ddMMM format
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
   * This function scans in a date string in the YYYYMMDD format and returns a
   * long corresponding to that date
   ***********************************************************************
   */
  static public long ScanLongDate(final String aDateString) throws Exception
    {
    try
      {
      final SimpleDateFormat formatDate  = new SimpleDateFormat("yyyyMMdd");
      final Date fDate = formatDate.parse(aDateString);
      final long ftime = fDate.getTime();
      return(ftime);
      }
    catch (Exception e)
      {
      throw new Exception("unable to scan date string " + aDateString);
      }
    }

  /** 
   ***********************************************************************
   * This function scans a date string in the yyyyMMddHHmm format and returns a
   * long corresponding to that date and time
   ***********************************************************************
   */
  static public long ScanLongDateTime(final String aDateString) throws Exception
    {
    try
      {
      final SimpleDateFormat formatDate  = new SimpleDateFormat("yyyyMMddHHmm");
      final Date fDate = formatDate.parse(aDateString);
      final long ftime = fDate.getTime();
      return(ftime);
      }
    catch (Exception e)
      {
      throw new Exception("Unable to scan date/time string " + aDateString);
      }
    }
  /** 
   ***********************************************************************
   * This function converts a date string in the form YYYYMMDD into
   * the CRS style ddMMM
   ***********************************************************************
   */
  static public String ConvertLongToCrsDate(final String aDateString) throws Exception
    {
    final long ftime      = ScanLongDate(aDateString);
    final String sDepDate = FormatCRSDate(ftime);
    return(sDepDate);
    }

  /** 
   ***********************************************************************
   * This function returns the string passed padded with spaces up to the
   * width supplied; if the string is longer than the width, it is truncated
   ***********************************************************************
   */
  static public String SetWidth(final String aInString, final int aWidth)
    {
    final StringBuffer sOutString;
    if ( aInString instanceof String )
      sOutString = new StringBuffer(aInString);
    else
      sOutString = new StringBuffer("");

    // append spaces until the width
    while ( sOutString.length() < aWidth )
      sOutString.append(' ');

    // truncate the resulting string just in case its too long
    if ( sOutString.length() > aWidth )
      sOutString.setLength(aWidth);

    return( sOutString.toString() );
    }

  /** 
   ***********************************************************************
   * Returns the input string if there's any data in it, otherwise a blank string
   ***********************************************************************
   */
  static public String ShowString(final String aInString)
    {
    if ( NotNull(aInString) )
      return(aInString);
    else
      return("");
    }

  /** 
   ***********************************************************************
   * Returns true if the given string has data in it
   ***********************************************************************
   */
  static public boolean IsNull(final String aInString)
    {
    return( !NotNull(aInString) );
    }

  static public boolean NotNull(final String aInString)
    {
    if ( aInString instanceof String )
      {
      if ( aInString.length() > 0 )
        return(true);
      }

    return(false);
    }

  /**
   ***********************************************************************
   * Returns true if the given strings are equal, null and "" strings are
   * considered equivalent
   ***********************************************************************
   */
  static public boolean strEqual(final String aString1, final String aString2)
    {
    if ( NotNull(aString1) && NotNull(aString2) )
      return( aString1.equals(aString2) );
    else if ( IsNull(aString1) && IsNull(aString2) )
      return(true);
    else
      return(false);
    }

}
