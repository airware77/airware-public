package xmax.crs.GetPNR;

import java.util.Vector;
import java.io.Serializable;
import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;

public class PNRPhoneRemark extends PNRRemark implements Serializable
{
 public int type;
 public static final int HOME_TYPE   = 0;
 public static final int WORK_TYPE   = 1;
 public static final int AGENCY_TYPE = 2;
 public static final int CELL_TYPE   = 3;
 public static final int FAX_TYPE    = 4;
 public static final int PAGER_TYPE  = 5;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRPhoneRemark(final String aPhoneNumber)
    {
    RemarkText = aPhoneNumber;
    }

  public PNRPhoneRemark(final String aPhoneNumber, final int aPhoneType)
    {
    RemarkText = aPhoneNumber;
    type       = aPhoneType;
    }

 /** 
  ***********************************************************************
  * Convert an array of Remarks
  ***********************************************************************
  */
 public static String[] convertPhoneArray(final PNRPhoneRemark[] aRemarkArray)
   {
   if ( aRemarkArray instanceof PNRPhoneRemark[] )
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
    return("Phone");
    }

//  /*
//  /** 
//   ***********************************************************************
//   * Get the base phone number (minus the extension)
//   ***********************************************************************
//   */
//  private String getBasePhoneNumber()
//    {
//    final int iPos = getExtensionPos();
//    if ( iPos >= 0 )
//      return(RemarkText.substring(0,iPos).trim());
//    else
//      return(RemarkText);
//    }
//
//  /** 
//   ***********************************************************************
//   * Get the extension for the phone number
//   ***********************************************************************
//   */
//  private String getExtension()
//    {
//    final int iPos = getExtensionPos();
//    if ( iPos >= 0 )
//      return(RemarkText.substring(iPos).trim());
//    else
//      return("");
//    }
//
//  /** 
//   ***********************************************************************
//   * Determine where the extension information starts
//   ***********************************************************************
//   */
//  private int getExtensionPos()
//    {
//    try
//      {
//      // pattern skips an optional 3 characters and the digits
//      final String PHONE_PATTERN = "^([A-Z][A-Z][A-Z])?[ 0-9\\-]+";
//      final MatchInfo m_info = RegExpMatch.getFirstMatch(RemarkText,PHONE_PATTERN);
//      if ( m_info instanceof MatchInfo )
//        return(m_info.getEndPosition());
//      else
//        return(-1);
//      }
//    catch (Exception e)
//      {
//      return(-1);
//      }
//    }
//  */
  /** 
   ***********************************************************************
   * Main function for unit test
   ***********************************************************************
   */
  /*
  public static void main(String[] args)
    {
    final PNRPhoneRemark phone = new PNRPhoneRemark("");

    final String TEST_CASE_1 = "SFO415-924-6374";
    final String TEST_CASE_2 = "OAK707-769-9146  ext 126";
    final String TEST_CASE_3 = "555 1212";
    final String TEST_CASE_4 = "SFO415 924 6374";
    final String TEST_CASE_5 = "SFO415 924-6374  x124";
    final String TEST_CASE_6 = "SFO415 924 6374  ext 134";

    String sBase = "";
    String sExt  = "";

    phone.RemarkText = TEST_CASE_1;
    sBase = phone.getBasePhoneNumber();
    sExt  = phone.getExtension();

    phone.RemarkText = TEST_CASE_2;
    sBase = phone.getBasePhoneNumber();
    sExt  = phone.getExtension();

    phone.RemarkText = TEST_CASE_3;
    sBase = phone.getBasePhoneNumber();
    sExt  = phone.getExtension();

    phone.RemarkText = TEST_CASE_4;
    sBase = phone.getBasePhoneNumber();
    sExt  = phone.getExtension();

    phone.RemarkText = TEST_CASE_5;
    sBase = phone.getBasePhoneNumber();
    sExt  = phone.getExtension();

    phone.RemarkText = TEST_CASE_6;
    sBase = phone.getBasePhoneNumber();
    sExt  = phone.getExtension();

    sBase = "";
    sExt  = "";
    }
    */
}
