package xmax.crs.GetPNR;

import java.io.Serializable;
import xmax.TranServer.GnrcFormat;

public class PNRFreqFlyRemark extends PNRRemark implements Serializable
{
 public String Carrier;
 public String Code;         // SSR code used to represent a frequent flyer number

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRFreqFlyRemark(final String aFreqFlyNumber, final String aCarrier)
    {
    RemarkText = aFreqFlyNumber;
    Carrier    = aCarrier;
    }

  /** 
   ***********************************************************************
   * Get the remark type
   ***********************************************************************
   */
  public String getRemarkType()
    {
    return("Frequent flier");
    }

 /**
  ***********************************************************************
  * Remarks are equal if the remark text is the same and the associations
  * are the same
  ***********************************************************************
  */
 public boolean equals(final PNRRemark aRemark)
   {
   if ( (aRemark instanceof PNRFreqFlyRemark) == false )
     return(false);

   if ( GnrcFormat.strEqual(Carrier,((PNRFreqFlyRemark )aRemark).Carrier) == false )
     return(false);

 //  if ( GnrcFormat.strEqual(RemarkText,aRemark.RemarkText) == false )
 //    return(false);

   if ( isSameSegmentAssociation(aRemark) == false )
     return(false);

   if ( isSameNameAssociation(aRemark) == false )
     return(false);

   return(true);
   }

}
