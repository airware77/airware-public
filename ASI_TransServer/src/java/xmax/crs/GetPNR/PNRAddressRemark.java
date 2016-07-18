package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRAddressRemark extends PNRRemark implements Serializable
{
 public int type;
 public static final int HOME_ADDRESS     = 0;
 public static final int BILLING_ADDRESS  = 1;
 public static final int DELIVERY_ADDRESS = 2;
 public static final int WORK_ADDRESS     = 3;
 public static final int AGENCY_ADDRESS   = 4;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRAddressRemark(final String aAddress)
    {
    RemarkText = aAddress;
    }

  public PNRAddressRemark(final String aAddress, final int aAddressType)
    {
    RemarkText = aAddress;
    type       = aAddressType;
    }

 /** 
  ***********************************************************************
  * Convert an array of Remarks
  ***********************************************************************
  */
 public static String[] convertAddressArray(final PNRAddressRemark[] aRemarkArray)
   {
   if ( aRemarkArray instanceof PNRAddressRemark[] )
     {
     final String[] StringArray = new String[ aRemarkArray.length ];

     for ( int i = 0; i < aRemarkArray.length; i++ )
       StringArray[i] = aRemarkArray[i].RemarkText;

     return(StringArray);
     }
   else
     return(null);
   }

  /** 
   ***********************************************************************
   * Get the remark type
   ***********************************************************************
   */
  public String getRemarkType()
    {
    return("Address");
    }


}
