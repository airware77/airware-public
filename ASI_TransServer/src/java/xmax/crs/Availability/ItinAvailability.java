package xmax.crs.Availability;

import xmax.crs.Flifo.FlightSegment;
import java.util.Vector;
import java.io.Serializable;

public class ItinAvailability implements Serializable
{
 private Vector FlightList;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ItinAvailability()
    {
    FlightList = new Vector();
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public FlightAvailability[] getFlightAvailabilities()
   {
   if ( FlightList instanceof Vector )
     {
     if ( FlightList.size() > 0 )
       {
       final FlightAvailability[] FlightArray = new FlightAvailability[ FlightList.size() ];
       FlightList.toArray(FlightArray);
       return(FlightArray);
       }
     }

   return(null);
   }

  /**
   ***********************************************************************
   *
   ***********************************************************************
   */
 public FlightAvailability getFlightSegment(final String aCarrier, final int aFlightNum)
   {
   // check each segment and find the one that uses the given carrier and flight
   FlightAvailability fseg;
   for ( int i = 0; i < getNumSegments(); i++ )
     {
     fseg = getFlightSegment(i);
     if ( fseg instanceof FlightAvailability )
       {
       if ( fseg.Carrier.equals(aCarrier) && (fseg.FlightNum == aFlightNum) )
         return(fseg);
       }
     }

   return(null);
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 private FlightAvailability getFlightSegment(final int aSegmentNum)
   {
   if ( FlightList instanceof Vector )
     {
     if ( (0 <= aSegmentNum) && (aSegmentNum < FlightList.size()) )
       {
       final FlightAvailability flightavail = (FlightAvailability )FlightList.elementAt(aSegmentNum);
       return( flightavail );
       }
     }

   return(null);
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public long getDepDate()
   {
   final FlightAvailability first_seg = getFlightSegment(0);

   if ( first_seg instanceof FlightAvailability )
     return(first_seg.DepDate);
   else
     return(0);
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public String getDepCity()
   {
   final FlightAvailability first_seg = getFlightSegment(0);

   if ( first_seg instanceof FlightAvailability )
     return(first_seg.DepCity);
   else
     return("");
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public long getArrDate()
   {
   final FlightAvailability last_seg = getFlightSegment( getNumSegments() - 1 );

   if ( last_seg instanceof FlightAvailability )
     return(last_seg.ArrDate);
   else
     return(0);
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public String getArrCity()
   {
   final FlightAvailability last_seg = getFlightSegment( getNumSegments() - 1 );

   if ( last_seg instanceof FlightAvailability )
     return(last_seg.ArrCity);
   else
     return("");
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public int getNumSegments()
   {
   if ( FlightList instanceof Vector )
     return( FlightList.size() );
   else
     return(0);
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public int getNumStops()
   {
   FlightAvailability fseg;
   int iNumStops = 0;

   // check each segment to see if they all use the same carrier and flight
   for ( int i = 0; i < getNumSegments(); i++ )
     {
     fseg = getFlightSegment(i);

     iNumStops += (fseg.NumStops + 1);
     }

   return(iNumStops);
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
 public boolean isNonStop()
   {
   if ( getNumStops() <= 1 )
     return(true);
   else
     return(false);
   }

  /** 
   ***********************************************************************
   * Checks each segment of an itinerary to see if each uses the same
   * carrier and flight number
   ***********************************************************************
   */
 public boolean isDirect()
   {
   FlightAvailability next_seg;
   final FlightAvailability first_seg = getFlightSegment(0);

   // check each segment to see if they all use the same carrier and flight
   for ( int i = 1; i < getNumSegments(); i++ )
     {
     next_seg = getFlightSegment(i);

     if ( (first_seg instanceof FlightAvailability) && (next_seg instanceof FlightAvailability) )
       {
       if ( next_seg.Carrier.equals(first_seg.Carrier) == false )
         return(false);
       else if ( next_seg.FlightNum != first_seg.FlightNum )
         return(false);
       }
     }

   return(true);
   }

  /** 
   ***********************************************************************
   * Checks each segment of an itinerary to see if any segment uses
   * the given carrier
   ***********************************************************************
   */
 public boolean usesCarrier(final String aCarrier)
   {
   if ( (aCarrier instanceof String) == false )
     return(true);
   else if ( aCarrier.length() <= 0 )
     return(true);

   // check each segment to see if they use the given carrier
   FlightAvailability fseg;
   for ( int i = 0; i < getNumSegments(); i++ )
     {
     fseg = getFlightSegment(i);
     if ( fseg instanceof FlightAvailability )
       {
       if ( fseg.Carrier.equals(aCarrier) )
         return(true);
       }
     }

   return(false);
   }

  /** 
   ***********************************************************************
   * Checks each segment of an itinerary to see if any segment uses
   * the given flight
   ***********************************************************************
   */
 public boolean usesFlight(final String aCarrier, final int aFlightNum)
   {
   // make sure a flight is specified
   if ( (aCarrier instanceof String) == false )
     return(true);
   else if ( aCarrier.length() <= 0 )
     return(true);

   // make sure a flight is specified
   if ( aFlightNum <= 0 )
     return(true);


   // check each segment to see if they use the given carrier
   FlightAvailability fseg;
   for ( int i = 0; i < getNumSegments(); i++ )
     {
     fseg = getFlightSegment(i);
     if ( fseg instanceof FlightAvailability )
       {
       if ( fseg.Carrier.equals(aCarrier) && (fseg.FlightNum == aFlightNum) )
         return(true);
       }
     }

   return(false);
   }

  /** 
   ***********************************************************************
   * This procedure adds a flight availability to the itin
   ***********************************************************************
   */
 public void addFlightAvailability(final FlightAvailability aFlightAvail)
   {
   if ( (FlightList instanceof Vector) == false )
     FlightList = new Vector();

   FlightList.add(aFlightAvail);
   }
}
