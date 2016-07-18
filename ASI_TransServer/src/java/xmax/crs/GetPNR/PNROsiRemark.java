package xmax.crs.GetPNR;

import java.io.Serializable;
import xmax.TranServer.GnrcFormat;

public class PNROsiRemark extends PNRRemark implements Serializable
{
 public String Carrier;
 public boolean isAAFacts;     // used to distinguish AA facts from General facts

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNROsiRemark()
    {
    Carrier    = ALL_CARRIERS;
    }

  public PNROsiRemark(final String aText)
    {
    RemarkText = aText;
    Carrier    = ALL_CARRIERS;
    }

  public PNROsiRemark(final String aText, final String aCarrier)
    {
    RemarkText = aText;
    Carrier    = aCarrier;
    }

  /** 
   ***********************************************************************
   * Get the remark type
   ***********************************************************************
   */
  public String getRemarkType()
    {
    return("OSI");
    }

 /**
  ***********************************************************************
  * Remarks are equal if the remark text is the same and the associations
  * are the same
  ***********************************************************************
  */
 public boolean equals(final PNRRemark aRemark)
   {
   if ( (aRemark instanceof PNROsiRemark) == false )
     return(false);

   if ( GnrcFormat.strEqual(RemarkText,aRemark.RemarkText) == false )
     return(false);

   if ( GnrcFormat.strEqual(Carrier,((PNROsiRemark )aRemark).Carrier) == false )
     return(false);

   if ( isSameSegmentAssociation(aRemark) == false )
     return(false);

   if ( isSameNameAssociation(aRemark) == false )
     return(false);

   return(true);
   }

}
