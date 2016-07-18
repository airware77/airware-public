package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRPocketItinRemark extends PNRRemark implements Serializable
{
  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRPocketItinRemark()
    {
    }

  public PNRPocketItinRemark(final String aText)
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
    return("Pocket Itinerary");
    }


}
