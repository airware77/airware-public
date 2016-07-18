package xmax.crs.GetPNR;

import java.io.Serializable;
import java.util.Vector;

/**
 *******************************************************************************
 * The PNRItinSegment represents a segment that is itinerary based; this class
 * is extended by the {@link PNRItinAirSegment} and the PNRItinArunkSegment}
 * class
 *******************************************************************************
 */
public class PNRItinSegment implements Serializable
{
 /** 
  * corresponds to the line number of the Segment when viewing the 
  * Passenger Name Record (PNR) through a Terminal Address display
  */
 public int SegmentNumber;

 /**
  * This field is used to in the event that a Computer Reservation System 
  * (CRS) uses an internal segment identifier (such as is the case for the 
  * Amadeus API); in effect, this field replaces/duplicates the function 
  * of the field {@link #SegmentNumber}
  */
 public String CrsSegmentID;

 /** identifies the departure city of the segment */
 public String  DepartureCityCode;

 /** identifies the arrival city of the segment */
 public String  ArrivalCityCode;

 /** 
  * contains the raw text returned by a Computer Reservation System (CRS)
  * describing this Segment 
  */
 public String RawData;

 /** 
  ***********************************************************************
  * Returns a description of the segment
  ***********************************************************************
  */
 public String getItinDesc()
   {
   return( "Segment " + SegmentNumber );
   }

 /** 
  ***********************************************************************
  * Returns a description of the segment
  ***********************************************************************
  */
 public String toString()
   {
   return( getItinDesc() );
   }


  /**
   *****************************************************************************
   * This method takes an array of segments and returns a vector containing
   * multiple arrays of {@link PNRItinAirSegment} and {@link
   * PNRItinArunkSegment} objects where all Live Contiguous Segments are
   * grouped together, and where all Block (both managed and passive) or
   * Non-Contiguous Live Segments appear as one-element arrays; see below for
   * known issues.
   * <p>
   * When selling an Air Segment, Airware does not provide the Departure time
   * nor the Arrival Date/Time.  In this case, we deem 2 segments to be
   * contiguous if their arrival/departure cities match:
   * <code>s1.ArrivalCityCode ==  s2.DepartureCityCode)</code>
   * and if they both depart within 24 hours ( to account for contiguous
   * segments around midnight).</p>
   * <p>
   * This raises the possibility that a round-trip itinerary occuring within 48
   * hours, for example outbound on one day and inbound on the following day,
   * may be considered a contiguous itinerary, which, of course, would be very
   * silly. Hence, when grouping itineraries, we must also check that the
   * destination of each of the segments of the group is different from the
   * origin of the first segment of the group.</p>
   * <p>
   * In Amadeus, Negospace blocks are created per individual segment, hence,
   * when grouping Contiguous segments we group each Nego block segment into a
   * block of one; Sabre enables the creation of blocks containing multiple
   * segments (married segments); under such circumstances, it may be necessary
   * to modify this method, or to move it to the Amadeus package and only use
   * it it within that context.</p>
   * 
   * @see PNRItinAirSegment#isContiguousTo for the definition of a contiguous segment
   *****************************************************************************
   */
  public static Vector groupContiguousLiveSegments(PNRItinSegment[] segments)
    {
    if (segments instanceof PNRItinSegment[] == false)
      return null;

    Vector vHoldSegs = new Vector();
    Vector vSegGroups = new Vector();
    String sOrigin = "";

    for (int i=0; i < segments.length; i++)
      {
      if (segments[i] instanceof PNRItinAirSegment)
        {
        PNRItinAirSegment airSeg = (PNRItinAirSegment)segments[i];

        if (airSeg.isBlock())  // if we have a block segment
          {
          // check whether we have segments on hold
          if (vHoldSegs.size() > 0) 
            {
            // if so, create a new group with segments on hold
            // and clear the holding vector
            PNRItinAirSegment[] arySegGroup = new PNRItinAirSegment[vHoldSegs.size()];
            vHoldSegs.toArray(arySegGroup);
            vHoldSegs.removeAllElements();
            vSegGroups.add(arySegGroup);
            sOrigin = "";
            }
          // create a group of one with the passive segment
          PNRItinAirSegment[] arySegGroup = {airSeg};
          vSegGroups.add(arySegGroup);
          }

        else // if the segment is live
          {
          // check whether we have segments on hold
          if (vHoldSegs.size() > 0)
            {
            // retrieve the last segment on hold
            PNRItinAirSegment prevSeg = (PNRItinAirSegment)vHoldSegs.lastElement();

            // if the segment is not contiguous to the prior one,
            // or if the arrival city is the same as the origin,
            // create a new group with the segments on hold
            // and clear the holding vector
            if (!airSeg.isContiguousTo(prevSeg) || 
                airSeg.ArrivalCityCode.equals(sOrigin) )
              {
              PNRItinAirSegment[] arySegGroup = new PNRItinAirSegment[vHoldSegs.size()];
              vHoldSegs.toArray(arySegGroup);
              vHoldSegs.removeAllElements();
              vSegGroups.add(arySegGroup);
              sOrigin = "";
              }
            }
          vHoldSegs.add(airSeg);
          // if starting a new group, store the origin of that group
          if (sOrigin.length() == 0)
            sOrigin = airSeg.DepartureCityCode;
          } // end else 
        } // end if 
      else if (segments[i] instanceof PNRItinArunkSegment) 
        {
        // check whether we have segments on hold
        if (vHoldSegs.size() > 0) 
          {
          // if so, create a new group with segments on hold
          // and clear the holding vector
          PNRItinAirSegment[] arySegGroup = new PNRItinAirSegment[vHoldSegs.size()];
          vHoldSegs.toArray(arySegGroup);
          vHoldSegs.removeAllElements();
          vSegGroups.add(arySegGroup);
          sOrigin = "";
          }
        // create a group of one with the arunk segment
        PNRItinArunkSegment[] arySegGroup = {(PNRItinArunkSegment)segments[i]};
        vSegGroups.add(arySegGroup);
        }
      } // end for

    // if there are any segments left in the holding vector,
    // create a new group
    if (vHoldSegs.size() > 0)
      {
      PNRItinAirSegment[] arySegGroup = new PNRItinAirSegment[vHoldSegs.size()];
      vHoldSegs.toArray(arySegGroup);
      vSegGroups.add(arySegGroup);
      }

    // populate the DepartureCityCode and ArrivalCityCode of any 
    // arunk segments; arunk segments cannot appear as the first
    // or last segment groups
    for (int i=1; i < vSegGroups.size()-1; i++) {
      if (vSegGroups.get(i) instanceof PNRItinArunkSegment[])
        {
        PNRItinArunkSegment arunkSeg =
          ((PNRItinArunkSegment[])vSegGroups.get(i))[0];

        PNRItinAirSegment[] prevSegGroup = 
          (PNRItinAirSegment[])vSegGroups.get(i-1);

        PNRItinAirSegment[] nextSegGroup = 
          (PNRItinAirSegment[])vSegGroups.get(i+1);

        PNRItinAirSegment prevSeg = prevSegGroup[prevSegGroup.length-1];
        PNRItinAirSegment nextSeg = nextSegGroup[0];

        arunkSeg.DepartureCityCode = prevSeg.ArrivalCityCode;
        arunkSeg.ArrivalCityCode   = nextSeg.DepartureCityCode;
        }
    }

    return(vSegGroups);
    } // end groupContiguousLiveSegments
}
