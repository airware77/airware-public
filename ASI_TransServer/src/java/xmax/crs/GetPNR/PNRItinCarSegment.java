
//Title:        CRS Test Project
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David Fairchild
//Company:      XMAX Corp
//Description:  This class defines a car segment

package xmax.crs.GetPNR;

import xmax.crs.cars.LocationInfo;
import xmax.TranServer.GnrcFormat;
import java.io.Serializable;

public class PNRItinCarSegment extends PNRItinSegment implements Serializable
{
 public String   CompanyCode;
 public int      NumCars;
 public String   CarTypeCode;
 public String   SegmentStatus;
 public String   Confirmation;
 public String   Rate;
 public String   RateCode;
 public boolean  RateGuaranteed;
 public String   GuaranteeInfo;
 public String   LocationCode;
 public String   PickupCityCode;
 public String   PickupCityName;
 public long     PickUpDateTime;
 public String   DropoffCityCode;
 public String   DropoffCityName;
 public long     DropoffDateTime;
 public String   DropoffCharge;
 public String[] Address;
 public String[] Phone;
 public String   CompanyName;
 public String   LocationDescription;
 public String   HoursOfOperation;
 public String   Comment;

 /** 
  ***********************************************************************
  * Sets any unfilled fields with info from location
  ***********************************************************************
  */
 public void setLocationInfo(final LocationInfo aLocationInfo)
   {
   if ( aLocationInfo instanceof LocationInfo )
     {
     if ( GnrcFormat.IsNull(CompanyCode) )
       CompanyCode = aLocationInfo.CompanyCode;

     if ( GnrcFormat.IsNull(LocationCode) )
       LocationCode = aLocationInfo.LocationCode;

     if ( (Address instanceof String[]) == false )
       Address = aLocationInfo.Address;

     if ( (Phone instanceof String[]) == false )
       Phone = aLocationInfo.Phones;

     if ( GnrcFormat.IsNull(CompanyName) )
       CompanyName = aLocationInfo.CompanyName;

     if ( GnrcFormat.IsNull(LocationDescription) )
       LocationDescription = aLocationInfo.LocationDescription;

     if ( GnrcFormat.IsNull(HoursOfOperation) )
       HoursOfOperation = aLocationInfo.Hours;
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

 public String getPhoneLine(final int aIndex)
   {
   return( m_GetStringElement(Phone,aIndex) );
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
  * Returns a description of the segment
  ***********************************************************************
  */
 public String getItinDesc()
   {
   if ( GnrcFormat.NotNull(CompanyName) && GnrcFormat.NotNull(PickupCityCode) )
     return( CompanyName + " " + PickupCityCode );
   else if ( GnrcFormat.NotNull(CompanyCode) && GnrcFormat.NotNull(PickupCityCode) )
     return( "Car " + CompanyCode + " " + PickupCityCode );
   else
     return( "Car Segment " + SegmentNumber );
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
