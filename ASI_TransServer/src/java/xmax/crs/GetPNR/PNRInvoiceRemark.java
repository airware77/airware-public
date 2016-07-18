package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRInvoiceRemark extends PNRRemark implements Serializable
{
  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRInvoiceRemark()
    {
    }

  public PNRInvoiceRemark(final String aText)
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
    return("Invoice");
    }

}
