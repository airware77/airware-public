package xmax.crs;

import java.util.StringTokenizer;
import xmax.util.RegExpMatch;
import java.io.Serializable;
import xmax.crs.Flifo.FlightSegment;

public class ConnectTimes implements Serializable
{
 public FlightSegment InBound;
 public FlightSegment OutBound;
 public int MinimumConnectMinutes;

 /**
  ***********************************************************************
  * Function for converting hours to minutes
  ***********************************************************************
  */
 public static int TimeStrToMinutes(final String aTimeStr)
   {

   try
     {

     // make sure user passes in a valid time string
     if ( RegExpMatch.matches(aTimeStr,"^[0-9]{0,3}[\\.\\:][0-5][0-9]$") == false )
       throw new Exception("Invalid time string: " + aTimeStr);

     final StringTokenizer fields = new StringTokenizer(aTimeStr," .:");
     if ( fields.countTokens() == 2 )
       {
       final String sHour        = fields.nextToken();
       final String sMinute      = fields.nextToken();
       final int iHour           = Integer.parseInt(sHour);
       final int iMinute         = Integer.parseInt(sMinute);
       final int iElapsedMinutes = HoursToMinutes(iHour,iMinute);
       return( iElapsedMinutes );
       }
     else if ( fields.countTokens() == 1 )
       {
       final String sMinutes     = fields.nextToken();
       final int iElapsedMinutes = Integer.parseInt(sMinutes);
       return( iElapsedMinutes );
       }
     else
       throw new Exception("Invalid time string: " + aTimeStr);

     }
   catch (Exception e)
     {
     return(0);
     }

   }

 /**
  ***********************************************************************
  * Function for converting hours to minutes
  ***********************************************************************
  */
 public static int HoursToMinutes(final int aHours)
   {
   return( aHours * 60 );
   }

 public static int HoursToMinutes(final int aHours, final int aMinutes)
   {
   return( (aHours * 60)  + aMinutes );
   }

}
