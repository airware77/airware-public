
//Title:      CRS Test Project
//Version:
//Copyright:  Copyright (c) 1999
//Author:     David Fairchild
//Company:    XMAX Corp
//Description:This is a first shot at some CRS classes

package xmax.crs;

import xmax.crs.GetPNR.PNRFopRemark;

public class VendorPref
{
 public static final int INCLUDE   = 0;
 public static final int PREFERRED = 1;
 public static final int EXCLUDE   = 2;

 private String Company;        // company name or code
 public String CustomerID;      // ID number given to customer by company
 public String ProductType;     // seat type, room type, or car type codes
 public String RateCode;
 public String DiscountCode;
 public String ExtraInfo;
 public int status;             // indicates preferred or excluded companies
 public PNRFopRemark Guarantee;
 public String Carrier;         // mileage membership carrier code

   /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public VendorPref(final String aCompany)
    {
    Company = aCompany;
    }

  /**
   ***********************************************************************
   * returns true if the given company is marked as Excluded
   ***********************************************************************
   */
  public boolean isExcluded()
    {
    if ( status == EXCLUDE )
      return(true);
    else
      return(false);
    }

  /**
   ***********************************************************************
   * returns true if the given company is not marked as Excluded
   ***********************************************************************
   */
  public boolean isIncluded()
    {
    if ( status != EXCLUDE )
      return(true);
    else
      return(false);
    }

  /**
   ***********************************************************************
   * returns true if the given company is marked as preferred
   ***********************************************************************
   */
  public boolean isPreferred()
    {
    if ( status == PREFERRED )
      return(true);
    else
      return(false);
    }

  /**
   ***********************************************************************
   * returns the company name
   ***********************************************************************
   */
  public String getCompany()
    {
    return(Company);
    }
}