package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRTicketRemark extends PNRRemark implements Serializable
{
  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRTicketRemark(final String aText)
    {
    RemarkText = aText;
    }
 /** 
  ***********************************************************************
  * Convert an array of Remarks
  ***********************************************************************
  */
 public static String[] convertTicketArray(final PNRTicketRemark[] aRemarkArray)
   {
   if ( aRemarkArray instanceof PNRTicketRemark[] )
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
    return("Ticket");
    }


}
