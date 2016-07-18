package xmax.crs;

/**
 ***********************************************************************
 * This is a convenience class merely used for passing tax totals by tax type
 * to {@link NativeAsciiWriter} from a {@link PNRFare} object.
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 01/25/2002 2:02:42 PM$
 *
 * @see PNRFare#getTaxTotals
 ***********************************************************************
 */
public class Tax
{
  /** type of tax */
  public String type;

  /** amount in cents */
  public long amount;

  /** 
   * This contains either the sum of all taxes, or the sum of 'other' taxes
   * that are not otherwise itemized; the literal value of this field is
   * currently <code>XT</code>;
   */
  public static final String OTHER = "XT";

  public Tax(String sType, long lAmount)
    {
    type   = sType;
    amount = lAmount;
    }

} // end class Tax
