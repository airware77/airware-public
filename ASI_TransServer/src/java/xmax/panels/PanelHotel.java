
//Title:        TranServer
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import xmax.crs.GetPNR.PNRItinCarSegment;
import xmax.TranServer.GnrcFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import xmax.crs.PNR;
import xmax.crs.GetPNR.*;
import xmax.crs.hotel.HotelInfo;
import javax.swing.*;

public class PanelHotel extends JPanel
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
  GridLayout gridLayout2 = new GridLayout();
  GridLayout gridLayout3 = new GridLayout();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PanelHotel()
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

  public PanelHotel(final PNRItinHotelSegment aSeg)
    {
    this();
    displayHotel(aSeg);
    }


  public PanelHotel(final PNRItinHotelSegment aSeg, final PNR aPNR)
    {
    this();
    displayHotel(aSeg,aPNR);
    }


  public PanelHotel(final HotelInfo aLocation)
    {
    this();
    displayHotel(aLocation);
    }


  public PanelHotel(final String aCompanyCode, final String aCityCode, final String aPropertyCode,
                    final String aRawData)
    {
    this();

    // set header
    lblCaption.setText( "Hotel Information - Company Code: " + aCompanyCode + " City: " + aCityCode + " Location: " + aPropertyCode);
    pnlLocationInfo.removeAll();
    pnlDetail2.removeAll();

    // set raw data
    memRawData.setText( GnrcFormat.ShowString(aRawData) );
    splitBase.setBottomComponent(scrollRawData);
    splitBase.setDividerSize(3);
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
    memRawData.setPreferredSize(new Dimension(475, 125));
    memRawData.setText("Hotel Information");
    memRawData.setEditable(false);
    pnlDetail.setLayout(borderLayout2);
    lblCaption.setFont(new java.awt.Font("Dialog", 1, 16));
    lblCaption.setBorder(BorderFactory.createEtchedBorder());
    lblCaption.setPreferredSize(new Dimension(475, 26));
    lblCaption.setHorizontalAlignment(SwingConstants.CENTER);
    lblCaption.setText("Hotel Information");
    pnlDetail2.setLayout(gridLayout1);
    pnlResInfo.setLayout(gridLayout2);
    pnlLocationInfo.setLayout(gridLayout3);
    pnlResInfo.setBorder(BorderFactory.createEtchedBorder());
    pnlResInfo.setPreferredSize(new Dimension(237, 274));
    pnlLocationInfo.setBorder(BorderFactory.createEtchedBorder());
    pnlLocationInfo.setPreferredSize(new Dimension(238, 274));
    this.add(splitBase, BorderLayout.CENTER);
    splitBase.add(scrollRawData, JSplitPane.BOTTOM);
    splitBase.add(pnlDetail, JSplitPane.TOP);
    pnlDetail.add(lblCaption, BorderLayout.NORTH);
    pnlDetail.add(pnlDetail2, BorderLayout.CENTER);
    pnlDetail2.add(pnlResInfo, null);
    pnlDetail2.add(pnlLocationInfo, null);
    scrollRawData.getViewport().add(memRawData, null);
    splitBase.setDividerLocation(300);
    }

  /** 
   ***********************************************************************
   * Display hotel info from an itinerary hotel segment
   ***********************************************************************
   */
  public void displayHotel(final PNRItinHotelSegment aSeg)
    {
    displayHotel(aSeg,null);
    }


  public void displayHotel(final PNRItinHotelSegment aSeg, final PNR aPNR)
    {
    final SimpleDateFormat long_date_format  = new SimpleDateFormat("MMM d, yyyy");
    final SimpleDateFormat time_format       = new SimpleDateFormat("HH:mm");


    // set header
    if ( GnrcFormat.NotNull(aSeg.ChainCode) )
      lblCaption.setText( "Hotel Information - Company Code: " + aSeg.ChainCode);
    else
      lblCaption.setText( "Hotel Information");


    // set reservation info
    final PropertyGrid propRes = new PropertyGrid("Reservation Info");

    propRes.addBlankRow();

    // rate info
    propRes.addProperty("Segment",aSeg.SegmentNumber);
    propRes.addProperty("Status",aSeg.SegmentStatus);
    propRes.addProperty("Number of Rooms",aSeg.NumRooms);

    if ( GnrcFormat.NotNull(aSeg.ResName) )
      propRes.addProperty("Name",aSeg.ResName);

    if ( aSeg.CheckInDate > 0 )
      {
      final String sTime = time_format.format( new Date(aSeg.CheckInDate) );
      if ( sTime.equals("00:00") )
        propRes.addProperty("Check In",aSeg.CheckInDate,"MMM d");
      else
        propRes.addProperty("Check In",aSeg.CheckInDate,"MMM d,  h:mm a");
      }

    if ( aSeg.CheckOutDate > 0 )
      {
      final String sTime = time_format.format( new Date(aSeg.CheckOutDate) );

      if ( sTime.equals("00:00") )
        propRes.addProperty("Check Out",aSeg.CheckOutDate,"MMM d");
      else
        propRes.addProperty("Check Out",aSeg.CheckOutDate,"MMM d,  h:mm a");
      }

    propRes.addBlankRow();

    if ( GnrcFormat.NotNull(aSeg.RoomType) )
      propRes.addProperty("Room Type",aSeg.RoomType);
    if ( GnrcFormat.NotNull(aSeg.ConfirmationNumber) )
      propRes.addProperty("Confirmation",aSeg.ConfirmationNumber);
    if ( GnrcFormat.NotNull(aSeg.Rate) )
      propRes.addProperty("Rate",aSeg.Rate);
    if ( GnrcFormat.NotNull(aSeg.RateCode) )
      propRes.addProperty("Rate Code",aSeg.RateCode);
    propRes.addProperty("Rate Status",aSeg.RateGuaranteed,"Guaranteed","Quoted");

    if ( GnrcFormat.NotNull(aSeg.Guarantee) )
      propRes.addProperty("Guaranteed With",aSeg.Guarantee);

    if ( GnrcFormat.NotNull(aSeg.CancelPolicy) )
      propRes.addProperty("Cancel Policy",aSeg.CancelPolicy);

    propRes.addBlankRow();

    pnlResInfo.removeAll();
    pnlResInfo.add(propRes);


    // set location info
    final PropertyGrid propLoc = new PropertyGrid("Location Information");
    propLoc.addBlankRow();

    if ( GnrcFormat.NotNull(aSeg.ChainCode) )
      propLoc.addProperty("Company Code",aSeg.ChainCode);

    if ( GnrcFormat.NotNull(aSeg.CityName) )
      propLoc.addProperty("City Name",aSeg.CityName);

    if ( GnrcFormat.NotNull(aSeg.CityCode) )
      propLoc.addProperty("City Code",aSeg.CityCode);

    if ( GnrcFormat.NotNull(aSeg.PropertyCode) )
      propLoc.addProperty("Location Code",aSeg.PropertyCode);

    if ( aSeg.Address instanceof String[] )
      {
      propLoc.addBlankRow();
      for ( int i = 0; i < aSeg.Address.length; i++ )
        propLoc.addProperty("Address " + Integer.toString(i + 1),aSeg.Address[i]);

      if ( GnrcFormat.NotNull(aSeg.PostalCode) )
        propLoc.addProperty("Postal Code",aSeg.PostalCode);
      propLoc.addBlankRow();
      }

    if ( GnrcFormat.NotNull(aSeg.Phone) )
      propLoc.addProperty("Phone",aSeg.Phone);

    if ( GnrcFormat.NotNull(aSeg.Fax) )
      propLoc.addProperty("Fax",aSeg.Fax);

    propLoc.addBlankRow();

    pnlLocationInfo.removeAll();
    pnlLocationInfo.add(propLoc);

    pnlDetail2.removeAll();
    pnlDetail2.add(pnlResInfo);

    // decide if location info panel will be shown or if just single panel
    if ( m_HasLocationInfo(aSeg) )
      pnlDetail2.add(pnlLocationInfo);
    else
      {
      propRes.addBlankRow();

      if ( GnrcFormat.NotNull(aSeg.ChainCode) )
        propRes.addProperty("Company Code",aSeg.ChainCode);

      if ( GnrcFormat.NotNull(aSeg.CityCode) )
        propRes.addProperty("City Code",aSeg.CityCode);

      if ( GnrcFormat.NotNull(aSeg.PropertyCode) )
        propRes.addProperty("Location Code",aSeg.PropertyCode);

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
  private boolean m_HasLocationInfo(final PNRItinHotelSegment aSeg)
    {
    if ( GnrcFormat.NotNull(aSeg.CityName) )
      return(true);

    if ( aSeg.Address instanceof String[] )
      {
      if ( aSeg.Address.length > 0 )
        return(true);
      }

    if ( GnrcFormat.NotNull(aSeg.PostalCode) )
      return(true);

    if ( GnrcFormat.NotNull(aSeg.Phone) )
      return(true);

    if ( GnrcFormat.NotNull(aSeg.Fax) )
      return(true);

    return(false);
    }

  /** 
   ***********************************************************************
   * Display hotel information
   ***********************************************************************
   */
  public void displayHotel(final HotelInfo aLocation)
    {
    final SimpleDateFormat long_date_format  = new SimpleDateFormat("MMM d, yyyy");

    // set header
    if ( GnrcFormat.NotNull(aLocation.CompanyName) )
      lblCaption.setText( "Hotel Information - " + aLocation.CompanyName);
    else if ( GnrcFormat.NotNull(aLocation.CompanyCode) )
      lblCaption.setText( "Hotel Information - Company Code: " + aLocation.CompanyCode);
    else
      lblCaption.setText( "Hotel Information");


    // set location info
    final PropertyGrid propLoc = new PropertyGrid("Location Information");
    propLoc.addBlankRow();

    if ( GnrcFormat.NotNull(aLocation.CompanyName) )
      propLoc.addProperty("Company Name",aLocation.CompanyName);

    if ( GnrcFormat.NotNull(aLocation.CompanyCode) )
      propLoc.addProperty("Company Code",aLocation.CompanyCode);

    if ( GnrcFormat.NotNull(aLocation.CityCode) )
      propLoc.addProperty("City Code",aLocation.CityCode);

    if ( GnrcFormat.NotNull(aLocation.PropertyCode) )
      propLoc.addProperty("Location Code",aLocation.PropertyCode);

    if ( GnrcFormat.NotNull(aLocation.LocationDescription) )
      propLoc.addProperty("Location Description",aLocation.LocationDescription);

    if ( aLocation.Address instanceof String[] )
      {
      propLoc.addBlankRow();
      for ( int i = 0; i < aLocation.Address.length; i++ )
        propLoc.addProperty("Address " + Integer.toString(i + 1),aLocation.Address[i]);

      if ( GnrcFormat.NotNull(aLocation.PostalCode) )
        propLoc.addProperty("Postal Code",aLocation.PostalCode);
      propLoc.addBlankRow();
      }

    if ( GnrcFormat.NotNull(aLocation.Phone) )
      propLoc.addProperty("Phone",aLocation.Phone);

    if ( GnrcFormat.NotNull(aLocation.Fax) )
      propLoc.addProperty("Fax",aLocation.Fax);

    if ( GnrcFormat.NotNull(aLocation.CheckInTime) )
      propLoc.addProperty("Check In",aLocation.CheckInTime);

    if ( GnrcFormat.NotNull(aLocation.CheckOutTime) )
      propLoc.addProperty("Check In",aLocation.CheckOutTime);

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