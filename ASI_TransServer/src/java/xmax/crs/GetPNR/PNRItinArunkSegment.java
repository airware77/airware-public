package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRItinArunkSegment extends PNRItinSegment implements Serializable
{
 /** 
  ***********************************************************************
  * Returns a description of the segment
  ***********************************************************************
  */
 public String getItinDesc()
   {
   return( "ARUNK Segment " + SegmentNumber );
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
} 
