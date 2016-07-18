package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRFopRemark extends PNRRemark implements Serializable
{
  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRFopRemark()
    {
    }

  public PNRFopRemark(final String aText)
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
    return("Form of Payment");
    }
}
