package xmax.dialogs;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import xmax.TranServer.GnrcFormat;

public class dialogUtils
{
  /** 
   ***********************************************************************
   * Center the frame
   ***********************************************************************
   */
  public static void centerWindow(final Window wndw)
    {
    if ( wndw.isValid() == false )
      wndw.pack();

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension frameSize  = wndw.getSize();

    if ( frameSize.height > screenSize.height )
      frameSize.height = screenSize.height;
    if ( frameSize.width > screenSize.width )
      frameSize.width = screenSize.width;

    wndw.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }


  /** 
   ***********************************************************************
   * This function scans in a date string in the m/d/yyyy format
   ***********************************************************************
   */
  static public long ScanDateTime(final String aDateString) throws Exception
    {
    return( ScanDateTime(aDateString,null) );
    }


  static public long ScanDateTime(final String aDateString, final String aTimeString) throws Exception
    {
    // convert date string from YYYYMMDD format to ddMMM format
    try
      {
      final SimpleDateFormat formatDate       = new SimpleDateFormat("M/d/yy");
      final SimpleDateFormat formatDateTime24 = new SimpleDateFormat("M/d/yy H:m");
      final SimpleDateFormat formatDateTime12 = new SimpleDateFormat("M/d/yy h:m a");

      // create a single date time string
      final String sDateTime;
      if ( GnrcFormat.NotNull(aTimeString) )
        sDateTime = aDateString + "  " + aTimeString.toLowerCase();
      else
        sDateTime = aDateString.toLowerCase();

      final Date fDate;
      if ( sDateTime.indexOf(':') >= 0 )
        {
        if ( (sDateTime.indexOf('a') >= 0) || (sDateTime.indexOf('p') >= 0) )
          fDate = formatDateTime12.parse( sDateTime );
        else
          fDate = formatDateTime24.parse( sDateTime );
        }
      else
        fDate = formatDate.parse( sDateTime );


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
   * This function prints a date in the m/d/yyyy format
   ***********************************************************************
   */
  static public String formatDate(final long aDate)
    {
    final SimpleDateFormat DateFormat = new SimpleDateFormat("M/d/yyyy");
    final String sDate = DateFormat.format( new Date(aDate) );
    return( sDate );
    }

  /** 
   ***********************************************************************
   * This function prints a date in the m/d/yyyy h:mm AM/PM format
   ***********************************************************************
   */
  static public String formatDateTime(final long aDate)
    {
    final SimpleDateFormat DateFormat = new SimpleDateFormat("M/d/yyyy  h:mm a");
    final String sDate = DateFormat.format( new Date(aDate) );
    return( sDate );
    }


  /** 
   ***********************************************************************
   * main procedure for unit tests
   ***********************************************************************
   */
  public static void main(String[] args)
    {

    try
      {
      long time = 0;
      String sTime = "";

      time = dialogUtils.ScanDateTime("10/22/96  8:01 pm");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = dialogUtils.ScanDateTime("3/5/3");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = dialogUtils.ScanDateTime("12/17/2004");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = dialogUtils.ScanDateTime("12/3/5");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = dialogUtils.ScanDateTime("1/13/2001");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = dialogUtils.ScanDateTime("10/22/97  8:12 am");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = dialogUtils.ScanDateTime("10/22/99  4:00 pm");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = dialogUtils.ScanDateTime("10/22/99  16:00 am");
      sTime = GnrcFormat.FormatReadableDateTime(time);

      time = 0;
      }
    catch (Exception e)
      {
      System.out.println(e.toString());
      }

    }
}
