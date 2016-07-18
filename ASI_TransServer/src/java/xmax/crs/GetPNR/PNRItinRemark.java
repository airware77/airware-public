package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRItinRemark extends PNRRemark implements Serializable
{
  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRItinRemark()
    {
    }

  public PNRItinRemark(final String aText)
    {
    RemarkText = aText;
    }

  /** 
   ***********************************************************************
   * Get the remark type
   ***********************************************************************
   */
  public String getRemarkType()
    {
    return("Itinerary");
    }


}
