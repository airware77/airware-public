package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRReceiveByRemark extends PNRRemark implements Serializable
{
  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRReceiveByRemark(final String aText)
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
    return("Receive By");
    }


} 