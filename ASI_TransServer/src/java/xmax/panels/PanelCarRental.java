
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import xmax.crs.Flifo.*;
import xmax.crs.GetPNR.PNRItinCarSegment;
import xmax.TranServer.GnrcFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import xmax.crs.PNR;
import xmax.crs.GetPNR.*;
import xmax.crs.cars.LocationInfo;
import javax.swing.*;

public class PanelCarRental extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane splitBase = new JSplitPane();
  JScrollPane scrollRawData = new JScrollPane();
  JTextArea memRawData = new JTextArea();
  JPanel pnlDetail = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel lblCaption = new JLabel();
  JPanel pnlDetail2 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JPanel pnlResInfo = new JPanel();
  JPanel pnlLocationInfo = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PanelCarRental()
    {
    try
      {
      jbInit();
      }
    catch(Exception ex)
      {
      ex.printStackTrace();
      }
    }


  public PanelCarRental(final PNRItinCarSegment aSeg)
    {
    this();
    displayCarRental(aSeg);
    }


  public PanelCarRental(final PNRItinCarSegment aSeg, final PNR aPNR)
    {
    this();
    displayCarRental(aSeg,aPNR);
    }


  public PanelCarRental(final LocationInfo aLocation)
    {
    this();
    displayCarRental(aLocation);
    }

  /** 
   ***********************************************************************
   * Used by constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    this.setLayout(borderLayout1);
    splitBase.setOrientation(JSplitPane.VERTICAL_SPLIT);
    memRawData.setText("Car Data");
    memRawData.setEditable(false);
    pnlDetail.setLayout(borderLayout2);
    lblCaption.setFont(new java.awt.Font("Dialog", 1, 16));
    lblCaption.setBorder(BorderFactory.createEtchedBorder());
    lblCaption.setPreferredSize(new Dimension(102, 40));
    lblCaption.setHorizontalAlignment(SwingConstants.CENTER);
    lblCaption.setText("Alamo Car Rentals");
    pnlDetail2.setLayout(gridLayout1);
    pnlResInfo.setBorder(BorderFactory.createEtchedBorder());
    pnlResInfo.setLayout(borderLayout3);
    pnlLocationInfo.setBorder(BorderFactory.createEtchedBorder());
    pnlLocationInfo.setLayout(borderLayout4);
    this.add(splitBase, BorderLayout.CENTER);
    splitBase.add(scrollRawData, JSplitPane.BOTTOM);
    splitBase.add(pnlDetail, JSplitPane.TOP);
    pnlDetail.add(lblCaption, BorderLayout.NORTH);
    pnlDetail.add(pnlDetail2, BorderLayout.CENTER);
    pnlDetail2.add(pnlResInfo, null);
    pnlDetail2.add(pnlLocationInfo, null);
    scrollRawData.getViewport().add(memRawData, null);
    splitBase.setDividerLocation(350);
    }

  /** 
   ***********************************************************************
   * Display car rental info from an itinerary car segment
   ***********************************************************************
   */
  public void displayCarRental(final PNRItinCarSegment aSeg)
    {
    displayCarRental(aSeg,null);
    }


  public void displayCarRental(final PNRItinCarSegment aSeg, final PNR aPNR)
    {
    final SimpleDateFormat long_date_format  = new SimpleDateFormat("MMM d, yyyy");

    // set header
    if ( GnrcFormat.NotNull(aSeg.CompanyName) )
      lblCaption.setText("Car Rental - " + aSeg.CompanyName);
    else if ( GnrcFormat.NotNull(aSeg.CompanyCode) )
      lblCaption.setText( "Car Rental - Company Code: " + aSeg.CompanyCode);
    else
      lblCaption.setText( "Car Rental Information");


    // set reservation info
    final PropertyGrid propRes = new PropertyGrid("Reservation Info");

    propRes.addBlankRow();

    // rate info
    if ( GnrcFormat.NotNull(aSeg.CarTypeCode) )
      propRes.addProperty("Car Type",aSeg.CarTypeCode);
    propRes.addProperty("Segment",aSeg.SegmentNumber);
    propRes.addProperty("Status",aSeg.SegmentStatus);
    propRes.addProperty("Number of Cars",aSeg.NumCars);

    if ( GnrcFormat.NotNull(aSeg.Confirmation) )
      propRes.addProperty("Confirmation",aSeg.Confirmation);
    if ( GnrcFormat.NotNull(aSeg.Rate) )
      propRes.addProperty("Rate",aSeg.Rate);
    if ( GnrcFormat.NotNull(aSeg.RateCode) )
      propRes.addProperty("Rate Code",aSeg.RateCode);
    propRes.addProperty("Rate Status",aSeg.RateGuaranteed,"Guaranteed","Quoted");

    propRes.addBlankRow();

    // pickup info
    if ( GnrcFormat.NotNull(aSeg.PickupCityName) )
      propRes.addProperty("Pickup City",aSeg.PickupCityName);
    if ( GnrcFormat.NotNull(aSeg.PickupCityCode) )
      propRes.addProperty("Pickup City Code",aSeg.PickupCityCode);

    if ( aSeg.PickUpDateTime > 0 )
      {
      propRes.addProperty("Pickup Date",aSeg.PickUpDateTime,"MMM d");
      propRes.addProperty("Pickup Time",aSeg.PickUpDateTime,"h:mm a");
      }

    propRes.addBlankRow();

    // dropoff info
    if ( GnrcFormat.NotNull(aSeg.DropoffCityName) )
      propRes.addProperty("Dropoff City",aSeg.DropoffCityName);
    if ( GnrcFormat.NotNull(aSeg.DropoffCityCode) )
      propRes.addProperty("Dropoff City Code",aSeg.DropoffCityCode);

    if ( aSeg.DropoffDateTime > 0 )
      {
      propRes.addProperty("Dropoff Date",aSeg.DropoffDateTime,"MMM d");
      propRes.addProperty("Dropoff Time",aSeg.DropoffDateTime,"h:mm a");
      }

    if ( GnrcFormat.NotNull(aSeg.DropoffCharge) )
      propRes.addProperty("Dropoff Charge",aSeg.DropoffCharge);

    propRes.addBlankRow();


    pnlResInfo.removeAll();
    pnlResInfo.add(propRes);


    // set location info
    final PropertyGrid propLoc = new PropertyGrid("Location Information");
    propLoc.addBlankRow();

    if ( GnrcFormat.NotNull(aSeg.CompanyCode) )
      propLoc.addProperty("Company Code",aSeg.CompanyCode);

    if ( GnrcFormat.NotNull(aSeg.CompanyName) )
      propLoc.addProperty("Company Name",aSeg.CompanyName);

    if ( GnrcFormat.NotNull(aSeg.LocationDescription) )
      propLoc.addProperty("Location",aSeg.LocationDescription);

    if ( GnrcFormat.NotNull(aSeg.LocationCode) )
      propLoc.addProperty("Location Code",aSeg.LocationCode);

    if ( aSeg.Address instanceof String[] )
      {
      propLoc.addBlankRow();
      for ( int i = 0; i < aSeg.Address.length; i++ )
        propLoc.addProperty("Address " + Integer.toString(i + 1),aSeg.Address[i]);
      propLoc.addBlankRow();
      }

    if ( aSeg.Phone instanceof String[] )
      {
      for ( int i = 0; i < aSeg.Phone.length; i++ )
        propLoc.addProperty("Phone " + Integer.toString(i + 1),aSeg.Phone[i]);
      }

    if ( GnrcFormat.NotNull(aSeg.HoursOfOperation) )
      propLoc.addProperty("Hours",aSeg.HoursOfOperation);

    propLoc.addBlankRow();

    pnlLocationInfo.removeAll();
    pnlLocationInfo.add(propLoc);

    pnlDetail2.removeAll();
    pnlDetail2.add(pnlResInfo);

    // decide whether to use second panel for location info
    if ( m_HasLocationInfo(aSeg) )
      pnlDetail2.add(pnlLocationInfo);
    else
      {
      propRes.addBlankRow();

      if ( GnrcFormat.NotNull(aSeg.CompanyCode) )
        propRes.addProperty("Company Code",aSeg.CompanyCode);

      if ( GnrcFormat.NotNull(aSeg.LocationCode) )
        propRes.addProperty("Location Code",aSeg.LocationCode);

      propRes.addBlankRow();
      }

    // get any segment associated remarks
    final PNRRemark[] remarks;
    if ( aPNR instanceof PNR )
      remarks = aPNR.getRemarks(aSeg.SegmentNumber);
    else
      remarks = null;

    if ( remarks instanceof PNRRemark[] )
      {
      splitBase.setBottomComponent(scrollRawData);
      splitBase.setDividerSize(3);

      memRawData.setText("");
      for ( int i = 0; i < remarks.length; i++ )
        memRawData.append(remarks[i].RemarkText);
      }
    else
      {
      // clear the raw data
      memRawData.setText("");
      splitBase.remove(scrollRawData);
      splitBase.setBottomComponent(null);
      splitBase.setRightComponent(null);
      splitBase.setDividerSize(0);
      }
    }

  /** 
   ***********************************************************************
   * Returns true if the itinerary segment has info about the location
   ***********************************************************************
   */
  private boolean m_HasLocationInfo(final PNRItinCarSegment aSeg)
    {
    if ( GnrcFormat.NotNull(aSeg.CompanyName) )
      return(true);

    if ( GnrcFormat.NotNull(aSeg.LocationDescription) )
      return(true);

    if ( aSeg.Address instanceof String[] )
      {
      if ( aSeg.Address.length > 0 )
        return(true);
      }

    if ( aSeg.Phone instanceof String[] )
      {
      if ( aSeg.Phone.length > 0 )
        return(true);
      }

    if ( GnrcFormat.NotNull(aSeg.HoursOfOperation) )
      return(true);

    return(false);
    }

  /** 
   ***********************************************************************
   * Display car location information
   ***********************************************************************
   */
  public void displayCarRental(final LocationInfo aLocation)
    {
    // set header
    if ( GnrcFormat.NotNull(aLocation.CompanyName) )
      lblCaption.setText("Car Rental - " + aLocation.CompanyName);
    else if ( GnrcFormat.NotNull(aLocation.CompanyCode) )
      lblCaption.setText( "Car Rental - Company Code: " + aLocation.CompanyCode);
    else
      lblCaption.setText( "Car Rental Information");

    pnlResInfo.removeAll();
    pnlDetail2.remove(pnlResInfo);

    // set location info
    final PropertyGrid propLoc = new PropertyGrid("Location Information");
    propLoc.addBlankRow();

    if ( GnrcFormat.NotNull(aLocation.CompanyCode) )
      propLoc.addProperty("Company Code",aLocation.CompanyCode);

    if ( GnrcFormat.NotNull(aLocation.CompanyName) )
      propLoc.addProperty("Company Name",aLocation.CompanyName);

    if ( GnrcFormat.NotNull(aLocation.CityCode) )
      propLoc.addProperty("City Code",aLocation.CityCode);

    if ( GnrcFormat.NotNull(aLocation.LocationDescription) )
      propLoc.addProperty("Location",aLocation.LocationDescription);

    if ( GnrcFormat.NotNull(aLocation.LocationCode) )
      propLoc.addProperty("Location Code",aLocation.LocationCode);

    if ( aLocation.Address instanceof String[] )
      {
      propLoc.addBlankRow();
      for ( int i = 0; i < aLocation.Address.length; i++ )
        propLoc.addProperty("Address " + Integer.toString(i + 1),aLocation.Address[i]);
      propLoc.addBlankRow();
      }

    if ( aLocation.Phones instanceof String[] )
      {
      for ( int i = 0; i < aLocation.Phones.length; i++ )
        propLoc.addProperty("Phone " + Integer.toString(i + 1),aLocation.Phones[i]);
      }

    if ( GnrcFormat.NotNull(aLocation.Fax) )
      propLoc.addProperty("Fax",aLocation.Fax);

    if ( GnrcFormat.NotNull(aLocation.Hours) )
      propLoc.addProperty("Hours",aLocation.Hours);

    if ( aLocation.Services instanceof String[] )
      {
      for ( int i = 0; i < aLocation.Services.length; i++ )
        propLoc.addProperty("Service " + Integer.toString(i + 1),aLocation.Services[i]);
      }

    propLoc.addBlankRow();

    pnlLocationInfo.removeAll();
    pnlLocationInfo.add(propLoc);

    pnlDetail2.removeAll();
    pnlDetail2.add(pnlLocationInfo);

    // set raw data
    memRawData.setText( GnrcFormat.ShowString(aLocation.RawData) );
    splitBase.setBottomComponent(scrollRawData);
    splitBase.setDividerSize(3);
    }

}