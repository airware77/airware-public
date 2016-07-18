package xmax.crs.GetPNR;

import java.io.Serializable;
import xmax.TranServer.GnrcFormat;

public class PNRSsrRemark extends PNRRemark implements Serializable
{
 public String Carrier;
 public String Code;
 public boolean isAAFacts;     // used to distinguish AA facts from General facts

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRSsrRemark()
    {
    Carrier = ALL_CARRIERS;
    }

  public PNRSsrRemark(final String aCode)
    {
    Code    = aCode;
    Carrier = ALL_CARRIERS;
    }

  public PNRSsrRemark(final String aCode, final String aCarrier)
    {
    Code    = aCode;
    Carrier = aCarrier;
    }

  /** 
   ***********************************************************************
   * Get the remark type
   ***********************************************************************
   */
  public String getRemarkType()
    {
    return("SSR");
    }

 /**
  ***********************************************************************
  * Remarks are equal if the remark text is the same and the associations
  * are the same
  ***********************************************************************
  */
 public boolean equals(final PNRRemark aRemark)
   {
   if ( (aRemark instanceof PNRSsrRemark) == false )
     return(false);

   if ( GnrcFormat.strEqual(Code,((PNRSsrRemark )aRemark).Code) == false )
     return(false);

   if ( GnrcFormat.strEqual(Carrier,((PNRSsrRemark )aRemark).Carrier) == false )
     return(false);

   if ( isSameSegmentAssociation(aRemark) == false )
     return(false);

   if ( isSameNameAssociation(aRemark) == false )
     return(false);

   return(true);
   }

}
