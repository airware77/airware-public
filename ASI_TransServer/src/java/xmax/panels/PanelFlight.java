
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import javax.swing.*;
import xmax.crs.Flifo.*;
import xmax.crs.GetPNR.PNRItinAirSegment;
import xmax.TranServer.GnrcFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import xmax.crs.PNR;
import xmax.crs.GetPNR.*;

public class PanelFlight extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane splitBase = new JSplitPane();
  JScrollPane scrollRawData = new JScrollPane();
  JTextArea memRawData = new JTextArea();
  JPanel pnlDetail = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel pnlHeader = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel lblCarrier = new JLabel();
  JLabel lblCityPair = new JLabel();
  JLabel lblDepDate = new JLabel();
  JPanel pnlDetail2 = new JPanel();
  GridLayout gridLayout2 = new GridLayout();
  JPanel pnlTimes = new JPanel();
  JPanel pnlFlightInfo = new JPanel();
  GridLayout gridLayout3 = new GridLayout();
  JPanel pnlDepTimes = new JPanel();
  JPanel pnlArrTimes = new JPanel();
  GridLayout gridLayout4 = new GridLayout();
  GridLayout gridLayout5 = new GridLayout();
  GridLayout gridLayout6 = new GridLayout();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PanelFlight()
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


  public PanelFlight(final PNRItinAirSegment aSeg)
    {
    this();
    displayFlight(aSeg);
    }


  public PanelFlight(final PNRItinAirSegment aSeg, final PNR aPNR)
    {
    this();
    displayFlight(aSeg,aPNR);
    }


  public PanelFlight(final FlightInfo aFlight)
    {
    this();
    displayFlight(aFlight);
    }


  public PanelFlight(final FlightInfo aFlight, final String aDepCity, final String aArrCity)
    {
    this();
    displayFlight(aFlight,aDepCity,aArrCity);
    }

  /** 
   ***********************************************************************
   * Used by Constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    this.setLayout(borderLayout1);
    splitBase.setOrientation(JSplitPane.VERTICAL_SPLIT);
    memRawData.setPreferredSize(new Dimension(475, 125));
    memRawData.setText("Raw Flight Data");
    memRawData.setEditable(false);
    pnlDetail.setLayout(borderLayout2);
    pnlHeader.setLayout(gridLayout1);
    pnlHeader.setBorder(BorderFactory.createEtchedBorder());
    pnlHeader.setPreferredSize(new Dimension(475, 26));
    gridLayout1.setColumns(3);
    gridLayout1.setRows(0);
    lblCarrier.setFont(new java.awt.Font("Dialog", 1, 12));
    lblCarrier.setHorizontalAlignment(SwingConstants.CENTER);
    lblCarrier.setText("AA  146");
    lblCityPair.setFont(new java.awt.Font("Dialog", 1, 12));
    lblCityPair.setHorizontalAlignment(SwingConstants.CENTER);
    lblCityPair.setText("DFW  to  LHR");
    lblDepDate.setFont(new java.awt.Font("Dialog", 1, 12));
    lblDepDate.setHorizontalAlignment(SwingConstants.CENTER);
    lblDepDate.setText("May 23,  2001");
    pnlDetail2.setLayout(gridLayout2);
    gridLayout2.setColumns(2);
    gridLayout2.setRows(0);
    pnlTimes.setLayout(gridLayout3);
    gridLayout3.setRows(2);
    pnlFlightInfo.setBorder(BorderFactory.createEtchedBorder());
    pnlFlightInfo.setPreferredSize(new Dimension(238, 274));
    pnlFlightInfo.setLayout(gridLayout4);
    pnlDepTimes.setBorder(BorderFactory.createEtchedBorder());
    pnlDepTimes.setPreferredSize(new Dimension(237, 137));
    pnlDepTimes.setLayout(gridLayout5);
    pnlArrTimes.setBorder(BorderFactory.createEtchedBorder());
    pnlArrTimes.setPreferredSize(new Dimension(237, 137));
    pnlArrTimes.setLayout(gridLayout6);
    scrollRawData.setPreferredSize(new Dimension(475, 125));
    this.add(splitBase, BorderLayout.CENTER);
    splitBase.add(scrollRawData, JSplitPane.BOTTOM);
    splitBase.add(pnlDetail, JSplitPane.TOP);
    pnlDetail.add(pnlHeader, BorderLayout.NORTH);
    pnlHeader.add(lblCarrier, null);
    pnlHeader.add(lblDepDate, null);
    pnlHeader.add(lblCityPair, null);
    pnlDetail.add(pnlDetail2, BorderLayout.CENTER);
    pnlDetail2.add(pnlTimes, null);
    pnlTimes.add(pnlDepTimes, null);
    pnlTimes.add(pnlArrTimes, null);
    pnlDetail2.add(pnlFlightInfo, null);
    scrollRawData.getViewport().add(memRawData, null);
    splitBase.setDividerLocation(300);
    }

  /** 
   ***********************************************************************
   * Display flight info from an itinerary flight segment
   ***********************************************************************
   */
  public void displayFlight(final PNRItinAirSegment aSeg)
    {
    displayFlight(aSeg,null);
    }


  public void displayFlight(final PNRItinAirSegment aSeg, final PNR aPNR)
    {
    final SimpleDateFormat long_date_format  = new SimpleDateFormat("MMM d, yyyy");

    // set header
    lblCarrier.setText(aSeg.Carrier + "  " + aSeg.FlightNumber);
    lblCityPair.setText(aSeg.DepartureCityCode + "  to  " + aSeg.ArrivalCityCode);
    lblDepDate.setText( long_date_format.format( new Date(aSeg.DepartureDateTime) ) );

    // set flight info
    final PropertyGrid propFlight = new PropertyGrid("Flight Info");

    propFlight.addBlankRow();
    propFlight.addProperty("Segment",aSeg.SegmentNumber);
    if ( aSeg.Miles > 0 )
      propFlight.addProperty("Miles",aSeg.Miles);
    if ( aSeg.ElapsedMinutes > 0 )
      {
      final int iHours = aSeg.ElapsedMinutes / 60;
      final int iMin   = aSeg.ElapsedMinutes % 60;
      final String sTime = iHours + " hrs  " + iMin + " min";
      propFlight.addProperty("Time",sTime);
      }
    propFlight.addProperty("Stops",aSeg.NumStops);
    if ( GnrcFormat.NotNull(aSeg.Equipment) )
      propFlight.addProperty("Equip",aSeg.Equipment);
    if ( GnrcFormat.NotNull(aSeg.ChangeOfGaugeEquipment) || GnrcFormat.NotNull(aSeg.ChangeOfGaugeCity) )
      {
      final String sChange = GnrcFormat.ShowString(aSeg.ChangeOfGaugeEquipment) + " at " + GnrcFormat.ShowString(aSeg.ChangeOfGaugeCity);
      propFlight.addProperty("Change",sChange);
      }
    if ( aSeg.isCanceled )
      propFlight.addProperty("Flight Status","Cancelled");
    if ( GnrcFormat.NotNull(aSeg.DelayCode) )
      propFlight.addProperty("Delay Code",aSeg.DelayCode);
    if ( GnrcFormat.NotNull(aSeg.OnTimePerformance) )
      propFlight.addProperty("On Time",aSeg.OnTimePerformance);
    if ( GnrcFormat.NotNull(aSeg.CodeShareCarrCd) || GnrcFormat.NotNull(aSeg.CodeShareCarrDesc) || GnrcFormat.NotNull(aSeg.CodeShareCarrFlgt) )
      {
      final String sShare = GnrcFormat.ShowString(aSeg.CodeShareCarrDesc) + " " + GnrcFormat.ShowString(aSeg.CodeShareCarrCd) + "  " + GnrcFormat.ShowString(aSeg.CodeShareCarrFlgt);
      propFlight.addProperty("Code Share",sShare);
      }

    propFlight.addBlankRow();

    if ( GnrcFormat.NotNull(aSeg.Meals) )
      propFlight.addProperty("Meal",aSeg.Meals);
    if ( GnrcFormat.NotNull(aSeg.InventoryClass) )
      propFlight.addProperty("Class",aSeg.InventoryClass);
    propFlight.addProperty("Seats",aSeg.NumberOfSeats);
    propFlight.addProperty("Status",aSeg.Status);
    if ( GnrcFormat.NotNull(aSeg.RemoteLocator) )
      propFlight.addProperty("Locator",aSeg.RemoteLocator);

    propFlight.addBlankRow();

    pnlFlightInfo.removeAll();
    pnlFlightInfo.add(propFlight);


    // set departure info
    final PropertyGrid propDep = new PropertyGrid("Depart " + aSeg.DepartureCityCode);
    propDep.addBlankRow();

    propDep.addProperty("Sched",aSeg.DepartureDateTime,propDep.TIME_FORMAT);
    if ( aSeg.DepEstDateTime > 0 )
      propDep.addProperty("Est",  aSeg.DepEstDateTime,propDep.TIME_FORMAT);
    if ( aSeg.DepGateOutDateTime > 0 )
      propDep.addProperty("Gate", aSeg.DepGateOutDateTime,propDep.TIME_FORMAT);
    if ( aSeg.DepFieldOffDateTime > 0 )
      propDep.addProperty("Field",aSeg.DepFieldOffDateTime,propDep.TIME_FORMAT);

    propDep.addBlankRow();

    if ( GnrcFormat.NotNull(aSeg.DepTerminal) )
      propDep.addProperty("Terminal",aSeg.DepTerminal);
    if ( GnrcFormat.NotNull(aSeg.DepGate) )
      propDep.addProperty("Gate",aSeg.DepGate);

    propDep.addBlankRow();

    pnlDepTimes.removeAll();
    pnlDepTimes.add(propDep);


    // set arrival info
    final PropertyGrid propArr = new PropertyGrid("Arrive " + aSeg.ArrivalCityCode);

    propArr.addBlankRow();

    if ( isDifferentDay(aSeg.DepartureDateTime,aSeg.ArrivalDateTime) )
      propArr.addProperty("Date",aSeg.ArrivalDateTime,"MMM d");

    propArr.addProperty("Sched",aSeg.ArrivalDateTime,propDep.TIME_FORMAT);
    if ( aSeg.ArrEstDateTime > 0 )
      propArr.addProperty("Est",  aSeg.ArrEstDateTime,propDep.TIME_FORMAT);
    if ( aSeg.ArrFieldOnDateTime > 0 )
      propArr.addProperty("Field",aSeg.ArrFieldOnDateTime,propDep.TIME_FORMAT);
    if ( aSeg.ArrGateInDateTime > 0 )
      propArr.addProperty("Gate", aSeg.ArrGateInDateTime,propDep.TIME_FORMAT);

    propArr.addBlankRow();

    if ( GnrcFormat.NotNull(aSeg.ArrTerminal) )
      propArr.addProperty("Terminal",aSeg.ArrTerminal);
    if ( GnrcFormat.NotNull(aSeg.ArrGate) )
      propArr.addProperty("Gate",aSeg.ArrGate);

    propArr.addBlankRow();

    pnlArrTimes.removeAll();
    pnlArrTimes.add(propArr);

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
   * Display flight info from FLIFO
   ***********************************************************************
   */
  public void displayFlight(final FlightInfo aFlight)
    {
    final String sDepCity = aFlight.getDepCity();
    final String sArrCity = aFlight.getArrCity();

    displayFlight(aFlight,sDepCity,sArrCity);
    }


  public void displayFlight(final FlightInfo aFlight, final String aDepCity, final String aArrCity)
    {
    final SimpleDateFormat long_date_format  = new SimpleDateFormat("MMM d, yyyy");

    // set header
    lblCarrier.setText(aFlight.getCarrier() + "  " + aFlight.getFlightNum());
    lblCityPair.setText(aDepCity + "  to  " + aArrCity);
    lblDepDate.setText( long_date_format.format( new Date(aFlight.getDepSchedDate(aDepCity)) ) );


    // set flight info
    final PropertyGrid propFlight = new PropertyGrid("Flight Info");

    propFlight.addBlankRow();

    final int iAirMiles = aFlight.getAirMiles(aDepCity,aArrCity);
    if ( iAirMiles > 0 )
      propFlight.addProperty("Miles",iAirMiles);

    final int iElapsedMinutes = aFlight.getElapsedMinutes(aDepCity,aArrCity);
    if ( iElapsedMinutes > 0 )
      {
      final int iHours = iElapsedMinutes / 60;
      final int iMin   = iElapsedMinutes % 60;
      final String sTime = iHours + " hrs  " + iMin + " min";
      propFlight.addProperty("Time",sTime);
      }

    propFlight.addProperty("Stops",aFlight.getNumStops(aDepCity,aArrCity));

    final String sEquip = aFlight.getEquipment(aDepCity,aArrCity);
    if ( GnrcFormat.NotNull(sEquip) )
      propFlight.addProperty("Equip",sEquip);

    final String sChangeEquip = aFlight.getChangeOfGaugeEquipment(aDepCity,aArrCity);
    final String sChangeCity  = aFlight.getChangeOfGaugeCity(aDepCity,aArrCity);
    if ( GnrcFormat.NotNull(sChangeEquip) || GnrcFormat.NotNull(sChangeCity) )
      {
      final String sChange = GnrcFormat.ShowString(sChangeEquip) + " at " + GnrcFormat.ShowString(sChangeCity);
      propFlight.addProperty("Change",sChange);
      }

    if ( aFlight.isCanceled(aDepCity,aArrCity) )
      propFlight.addProperty("Flight Status","Cancelled");

    final String sDelayCode = aFlight.getDelayCode(aDepCity,aArrCity);
    if ( GnrcFormat.NotNull(sDelayCode) )
      propFlight.addProperty("Delay Code",sDelayCode);

    final String sOnTime = aFlight.getOnTimePerformance(aDepCity,aArrCity);
    if ( GnrcFormat.NotNull(sOnTime) )
      propFlight.addProperty("On Time",sOnTime);

    final String sCodeShareCd     = aFlight.getCodeShareCarrierCode(aDepCity,aArrCity);
    final String sCodeShareName   = aFlight.getCodeShareCarrierName(aDepCity,aArrCity);
    final String sCodeShareFlight = aFlight.getCodeShareFlight(aDepCity,aArrCity);
    if ( GnrcFormat.NotNull(sCodeShareCd) || GnrcFormat.NotNull(sCodeShareName) || GnrcFormat.NotNull(sCodeShareFlight) )
      {
      final String sShare = GnrcFormat.ShowString(sCodeShareName) + " " + GnrcFormat.ShowString(sCodeShareCd) + "  " + GnrcFormat.ShowString(sCodeShareFlight);
      propFlight.addProperty("Code Share",sShare.trim());
      }

    final String sMeal = aFlight.getMeal(aDepCity,aArrCity);
    if ( GnrcFormat.NotNull(sMeal) )
      propFlight.addProperty("Meal",sMeal);

    propFlight.addBlankRow();

    pnlFlightInfo.removeAll();
    pnlFlightInfo.add(propFlight);


    // set departure info
    final PropertyGrid propDep = new PropertyGrid("Depart " + aDepCity);

    propDep.addBlankRow();

    propDep.addProperty("Sched",aFlight.getDepSchedDate(aDepCity),propDep.TIME_FORMAT);

    final long iDepEstTime = aFlight.getDepEstDate(aDepCity);
    if ( iDepEstTime > 0 )
      propDep.addProperty("Est",iDepEstTime,propDep.TIME_FORMAT);

    final long iDepGateTime = aFlight.getDepOutGateDate(aDepCity);
    if ( iDepGateTime > 0 )
      propDep.addProperty("Gate", iDepGateTime,propDep.TIME_FORMAT);

    final long iDepFieldTime = aFlight.getDepOffFieldDate(aDepCity);
    if ( iDepFieldTime > 0 )
      propDep.addProperty("Field",iDepFieldTime,propDep.TIME_FORMAT);

    propDep.addBlankRow();

    final String sDepTerm = aFlight.getDepTerm(aDepCity);
    if ( GnrcFormat.NotNull(sDepTerm) )
      propDep.addProperty("Terminal",sDepTerm);

    final String sDepGate = aFlight.getDepGate(aDepCity);
    if ( GnrcFormat.NotNull(sDepGate) )
      propDep.addProperty("Gate",sDepGate);

    propDep.addBlankRow();

    pnlDepTimes.removeAll();
    pnlDepTimes.add(propDep);


    // set arrival info
    final PropertyGrid propArr = new PropertyGrid("Arrive " + aArrCity);

    propArr.addBlankRow();

    if ( isDifferentDay(aFlight.getDepSchedDate(aDepCity),aFlight.getArrSchedDate(aArrCity)) )
      propArr.addProperty("Date",aFlight.getArrSchedDate(aArrCity),"MMM d");

    propArr.addProperty("Sched",aFlight.getArrSchedDate(aArrCity),propArr.TIME_FORMAT);

    final long iArrEstTime = aFlight.getArrEstDate(aArrCity);
    if ( iArrEstTime > 0 )
      propArr.addProperty("Est",iArrEstTime,propArr.TIME_FORMAT);

    final long iArrFieldTime = aFlight.getArrOnFieldDate(aArrCity);
    if ( iArrFieldTime > 0 )
      propArr.addProperty("Field",iArrFieldTime,propArr.TIME_FORMAT);

    final long iArrGateTime = aFlight.getArrInGateDate(aArrCity);
    if ( iArrGateTime > 0 )
      propArr.addProperty("Gate", iArrGateTime,propArr.TIME_FORMAT);

    propArr.addBlankRow();

    final String sArrTerm = aFlight.getArrTerm(aArrCity);
    if ( GnrcFormat.NotNull(sArrTerm) )
      propArr.addProperty("Terminal",sArrTerm);

    final String sArrGate = aFlight.getArrGate(aArrCity);
    if ( GnrcFormat.NotNull(sArrGate) )
      propArr.addProperty("Gate",sArrGate);

    propArr.addBlankRow();

    pnlArrTimes.removeAll();
    pnlArrTimes.add(propArr);


    // set raw data
    memRawData.setText( GnrcFormat.ShowString(aFlight.FlightSchedResponse) + "\r\n\r\n" + GnrcFormat.ShowString(aFlight.DayOfFlifoResponse) );
    splitBase.setBottomComponent(scrollRawData);
    splitBase.setDividerSize(3);
    }

  /** 
   ***********************************************************************
   * Display flight info from FLIFO
   ***********************************************************************
   */
  private boolean isDifferentDay(final long aDate1, final long aDate2)
    {
    final SimpleDateFormat day_format  = new SimpleDateFormat("d");

    final String sDay1 = day_format.format( new Date(aDate1) );
    final String sDay2 = day_format.format( new Date(aDate2) );

    if ( sDay1.equals(sDay2) )
      return(false);
    else
      return(true);
    }

}
