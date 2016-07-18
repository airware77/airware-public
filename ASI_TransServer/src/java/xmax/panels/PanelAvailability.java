
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import xmax.crs.Availability.*;
import xmax.TranServer.GnrcFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.*;

public class PanelAvailability extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane splitBase = new JSplitPane();
  JScrollPane scrollRawData = new JScrollPane();
  JTextArea memRawData = new JTextArea();
  JPanel pnlDetail = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JLabel lblCaption = new JLabel();
  JScrollPane scrollGrid = new JScrollPane();
  JTable grdAvail = new JTable();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PanelAvailability()
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

    
  public PanelAvailability(final DestAvailability aDestAvail)
    {
    this();
    displayAvail(aDestAvail);
    }

  /** 
   ***********************************************************************
   * Used by constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    memRawData.setPreferredSize(new Dimension(475, 125));
    memRawData.setBorder(BorderFactory.createEtchedBorder());
    memRawData.setText("Availability Data");
    memRawData.setEditable(false);
    splitBase.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitBase.setPreferredSize(new Dimension(475, 425));
    this.setLayout(borderLayout1);
    pnlDetail.setLayout(borderLayout2);
    lblCaption.setFont(new java.awt.Font("Dialog", 1, 16));
    lblCaption.setBorder(BorderFactory.createEtchedBorder());
    lblCaption.setPreferredSize(new Dimension(475, 26));
    lblCaption.setHorizontalAlignment(SwingConstants.CENTER);
    lblCaption.setText("Availability from DFW to ATL  Sep 21, 2000");
    pnlDetail.setBorder(BorderFactory.createEtchedBorder());
    pnlDetail.setPreferredSize(new Dimension(475, 300));
    scrollGrid.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollGrid.setPreferredSize(new Dimension(475, 274));
    grdAvail.setEnabled(false);
    grdAvail.setPreferredSize(new Dimension(475, 274));
    grdAvail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    grdAvail.setRowMargin(0);
    grdAvail.setShowHorizontalLines(false);
    this.setPreferredSize(new Dimension(475, 425));
    scrollRawData.setPreferredSize(new Dimension(475, 125));
    this.add(splitBase, BorderLayout.CENTER);
    splitBase.add(scrollRawData, JSplitPane.BOTTOM);
    scrollRawData.getViewport().add(memRawData, null);
    splitBase.add(pnlDetail, JSplitPane.TOP);
    pnlDetail.add(lblCaption, BorderLayout.NORTH);
    pnlDetail.add(scrollGrid, BorderLayout.CENTER);
    scrollGrid.getViewport().add(grdAvail, null);
    splitBase.setDividerLocation(300);
    }

  /** 
   ***********************************************************************
   * Display an availability
   ***********************************************************************
   */
  public void displayAvail(final DestAvailability aDestAvail)
    {
    // fill in raw data
    memRawData.setText(aDestAvail.RawData);

    // fill out caption
    lblCaption.setText("Availability from " + aDestAvail.ReqDepCity + " to " + aDestAvail.ReqArrCity + " on " + aDestAvail.ReqDepDate);

    // fill in grid
    final String[] COLUMN_NAMES = {"Itin","Seg","Flight","From","To",
                                   "Leaves","Arrives","Stops","Meals","Equip","Availability"};

    final int[] COLUMN_WIDTHS = {30,30,80,50,50,
                                 120,120,50,50,80,200};

    final DefaultTableModel tableData = new DefaultTableModel(COLUMN_NAMES,0);

    final SimpleDateFormat dt_format = new SimpleDateFormat("MMM dd, hh:mm a");

    // add a line to the grid for every itinerary flight segment
    final ItinAvailability[] itins = aDestAvail.getItinArray();
    if ( itins instanceof ItinAvailability[] )
      {
      String[] sRowData;
      FlightAvailability[] flights;
      for ( int iItinNum = 0; iItinNum < itins.length; iItinNum++ )
        {
        flights = itins[iItinNum].getFlightAvailabilities();
        if ( flights instanceof FlightAvailability[] )
          {
          for ( int iSegNum = 0; iSegNum < flights.length; iSegNum++ )
            {
            // get row values
            sRowData = new String[11];

            sRowData[0]  = Integer.toString(iItinNum + 1);
            sRowData[1]  = Integer.toString(iSegNum + 1);
            sRowData[2]  = getFlightNum( flights[iSegNum] );
            sRowData[3]  = flights[iSegNum].DepCity;
            sRowData[4]  = flights[iSegNum].ArrCity;
            sRowData[5]  = dt_format.format( new Date(flights[iSegNum].DepDate) );
            sRowData[6]  = dt_format.format( new Date(flights[iSegNum].ArrDate) );
            sRowData[7]  = Integer.toString(flights[iSegNum].NumStops);
            sRowData[8]  = flights[iSegNum].Meal;
            sRowData[9]  = getEquipment( flights[iSegNum] );
            sRowData[10] = getClassList( flights[iSegNum] );

            tableData.addRow(sRowData);
            }
          }
        }
      }


    grdAvail.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    grdAvail.setModel(tableData);

    // set column widths
    TableColumn tColumn;
    for ( int iColNum = 0; iColNum < COLUMN_WIDTHS.length; iColNum++ )
      {
      tColumn = grdAvail.getColumnModel().getColumn(iColNum);
      if ( tColumn instanceof TableColumn )
        tColumn.setPreferredWidth( COLUMN_WIDTHS[iColNum] );
      }

    // select all the rows with even numbered itins
    grdAvail.clearSelection();
    final int iRowCount = grdAvail.getRowCount();
    String sItinNum;
    int iItinNum;
    for ( int iNumRow = 0; iNumRow < iRowCount; iNumRow++ )
      {
      sItinNum = (String )tableData.getValueAt(iNumRow,0);
      if ( sItinNum instanceof String )
        {
        iItinNum = Integer.parseInt(sItinNum);
        if ( (iItinNum % 2) > 0 )
          grdAvail.addRowSelectionInterval(iNumRow,iNumRow);
        }
      }
    }

  /** 
   ***********************************************************************
   * Display an availability for a given flight segment
   ***********************************************************************
   */
  private String getClassList(final FlightAvailability aFlightAvail)
    {
    final StringBuffer sClassList = new StringBuffer();

    if ( aFlightAvail instanceof FlightAvailability )
      {
      final InvClassAvailability[] classes = aFlightAvail.getInvClassAvailability();
      if ( classes instanceof InvClassAvailability[] )
        {
        String sInvClass;
        for ( int iClassNum = 0; iClassNum  < classes.length; iClassNum++ )
          {
          sInvClass = classes[iClassNum].getInvClass() + classes[iClassNum].getNumSeats() + " ";
          sClassList.append(sInvClass);
          }
        }
      }

    return( sClassList.toString().trim() );
    }

  /** 
   ***********************************************************************
   * get equipment used for a given flight segment
   ***********************************************************************
   */
  private String getEquipment(final FlightAvailability aFlightAvail)
    {
    final StringBuffer sEquipment = new StringBuffer();

    if ( aFlightAvail instanceof FlightAvailability )
      {
      sEquipment.append( aFlightAvail.Equipment );
      if ( GnrcFormat.NotNull(aFlightAvail.EquipChangeCity) || GnrcFormat.NotNull(aFlightAvail.EquipChangeCode) )
        sEquipment.append(" change to " + aFlightAvail.EquipChangeCode + " in " + aFlightAvail.EquipChangeCity);
      }

    return( sEquipment.toString().trim() );
    }

  /** 
   ***********************************************************************
   * get flight number used for a given flight segment
   ***********************************************************************
   */
  private String getFlightNum(final FlightAvailability aFlightAvail)
    {
    final StringBuffer sFlightNum = new StringBuffer();

    if ( aFlightAvail instanceof FlightAvailability )
      {
      sFlightNum.append( aFlightAvail.Carrier + "  " + aFlightAvail.FlightNum );
      if ( GnrcFormat.NotNull(aFlightAvail.SharedCarrCode) || GnrcFormat.NotNull(aFlightAvail.SharedCarrDesc) || GnrcFormat.NotNull(aFlightAvail.SharedCarrFlight) )
        {
        sFlightNum.append(" Code Share ");
        if ( GnrcFormat.NotNull(aFlightAvail.SharedCarrCode) )
          sFlightNum.append(aFlightAvail.SharedCarrCode + " ");
        if ( GnrcFormat.NotNull(aFlightAvail.SharedCarrDesc) )
          sFlightNum.append(aFlightAvail.SharedCarrDesc + " ");
        if ( GnrcFormat.NotNull(aFlightAvail.SharedCarrFlight) )
          sFlightNum.append(aFlightAvail.SharedCarrFlight + " ");
        }
      }

    return( sFlightNum.toString().trim() );
    }

}
