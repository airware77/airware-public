
//Title:        CRS Test Project
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David Fairchild
//Company:      XMAX Corp
//Description:  This class defines an itinerary hotel segment

package xmax.crs.GetPNR;

import xmax.crs.hotel.HotelInfo;
import xmax.TranServer.GnrcFormat;
import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.util.RegExpMatch;
import java.util.StringTokenizer;
import java.io.Serializable;

public class PNRItinHotelSegment extends PNRItinSegment implements Serializable
{
 public String   ChainCode;
 public String   SegmentStatus;
 public int      NumRooms;
 public String   CityCode;
 public String   CityName;
 public long     CheckInDate;
 public long     CheckOutDate;
 public String   PropertyCode;
 public String   Name;
 public String   Rate;
 public String   RateCode;
 public boolean  RateGuaranteed;
 public String   RoomType;
 public String   CancelPolicy;
 public String   ConfirmationNumber;
 public String   Guarantee;
 public String   ResName;
 public String[] Address;
 public String   PostalCode;
 public String   Phone;
 public String   Fax;
 public String   Comment;

 /** 
  ***********************************************************************
  * Sets any unfilled fields with info from location
  ***********************************************************************
  */
 public void setLocationInfo(final HotelInfo aHotelInfo)
   {
   if ( aHotelInfo instanceof HotelInfo )
     {
     if ( GnrcFormat.IsNull(ChainCode) )
       ChainCode = aHotelInfo.CompanyCode;

     if ( GnrcFormat.IsNull(CityCode) )
       CityCode = aHotelInfo.CityCode;

     if ( GnrcFormat.IsNull(PropertyCode) )
       PropertyCode = aHotelInfo.PropertyCode;

     if ( GnrcFormat.IsNull(Name) )
       Name = aHotelInfo.LocationDescription;

     if ( GnrcFormat.IsNull(Phone) )
       Phone = aHotelInfo.Phone;

     if ( GnrcFormat.IsNull(Fax) )
       Fax = aHotelInfo.Fax;

     if ( (Address instanceof String[]) == false )
       Address = aHotelInfo.Address;

     if ( GnrcFormat.IsNull(PostalCode) )
       PostalCode = aHotelInfo.PostalCode;

      // make sure the zip code is set
      if ( GnrcFormat.IsNull(PostalCode) )
        PostalCode = m_GetZipCode(Address);


     if ( GnrcFormat.NotNull(aHotelInfo.CheckInTime) )
       {
       try
         {
         final String sDate = GnrcParser.FormatCRSDate(CheckInDate);
         CheckInDate        = GnrcParser.ScanCRSDateTimeString(sDate,aHotelInfo.CheckInTime);
         }
       catch (Exception e)
         {
         }
       }

     if ( GnrcFormat.NotNull(aHotelInfo.CheckOutTime) )
       {
       try
         {
         final String sDate = GnrcParser.FormatCRSDate(CheckOutDate);
         CheckOutDate       = GnrcParser.ScanCRSDateTimeString(sDate,aHotelInfo.CheckOutTime);
         }
       catch (Exception e)
         {
         }
       }


     }
   }

 /** 
  ***********************************************************************
  * Returns a string from the array
  ***********************************************************************
  */
 public String getAddressLine(final int aIndex)
   {
   return( m_GetStringElement(Address,aIndex) );
   }

 /** 
  ***********************************************************************
  * Returns a string from the array
  ***********************************************************************
  */
 private String m_GetStringElement(final String[] aStringArray, final int aIndex)
   {
   if ( aStringArray instanceof String[] )
     {
     if ( aIndex < aStringArray.length )
       return(aStringArray[aIndex]);
     }

   return(null);
   }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  private String m_GetZipCode(final String[] aAddress)
    {
    // recognized formats for US postal codes
    final String POSTAL_CODE_5DIGIT = "[0-9][0-9][0-9][0-9][0-9]";
    final String POSTAL_CODE_9DIGIT = "[0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]";
    final String POSTAL_CODE_FORMAT = "^(" + POSTAL_CODE_5DIGIT + "|" + POSTAL_CODE_9DIGIT + ")$";

    // look at each word in the given address, keep the last one that matches the postal format
    String sPostalCode = "";
    if ( aAddress instanceof String[] )
      {
      StringTokenizer fields;
      String sField;
      for ( int i = 0; i < aAddress.length; i++ )
        {
        fields = new StringTokenizer(aAddress[i]," ");
        while ( fields.hasMoreTokens() )
          {
          sField = fields.nextToken();
          try
            {
            if ( RegExpMatch.matches(sField,POSTAL_CODE_FORMAT) )
              sPostalCode = sField;
            }
          catch (Exception e)
            {}
          }
        }
      }

    return(sPostalCode);
    }

 /** 
  ***********************************************************************
  * Returns a description of the segment
  ***********************************************************************
  */
 public String getItinDesc()
   {
   if ( GnrcFormat.NotNull(Name) && GnrcFormat.NotNull(CityCode) )
     return( Name + " " + CityCode );
   else if ( GnrcFormat.NotNull(ChainCode) && GnrcFormat.NotNull(CityCode) )
     return( "Hotel " + ChainCode + " " + CityCode );
   else
     return( "Hotel Segment " + SegmentNumber );
   }

 /** 
  ***********************************************************************
  * Returns a description of the segment
  ***********************************************************************
  */
 public String toString()
   {
   return( getItinDesc() );
   }


}
