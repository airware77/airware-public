package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRGeneralRemark extends PNRRemark implements Serializable
{
  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRGeneralRemark()
    {
    }

  public PNRGeneralRemark(final String aText)
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
    return("General");
    }


}
