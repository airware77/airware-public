package xmax.crs.GetPNR;

/**
 ***********************************************************************
 * The AmadeusAPI may return the air segments out of order - for this reason,
 * it is necessary to sort the segments according to the lineNumber node in the
 * PNR reply, which arranges the segments into the proper chronological order
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 06/19/2002 5:01:48 PM$
 *
 * @see 
 ***********************************************************************
 */
public class PNRItinSegmentLineNumComparator implements java.util.Comparator
{
  public int compare(Object segment1, Object segment2)
    {
    PNRItinSegment seg1 = (PNRItinSegment)segment1;
    PNRItinSegment seg2 = (PNRItinSegment)segment2;

    if (seg1.SegmentNumber > seg2.SegmentNumber)
      return  1; 
    else if (seg1.SegmentNumber < seg2.SegmentNumber)
      return -1;
    else
      return 0;
    } 
} 
