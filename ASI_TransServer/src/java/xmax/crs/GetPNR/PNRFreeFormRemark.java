package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRFreeFormRemark extends PNRRemark implements Serializable
{
  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRFreeFormRemark()
    {
    }

  public PNRFreeFormRemark(final String aText)
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
    return("Free Form");
    }
}
