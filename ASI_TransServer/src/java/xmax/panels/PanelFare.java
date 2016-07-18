
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;

import xmax.TranServer.GnrcFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import xmax.crs.PNR;
import xmax.crs.GetPNR.*;
import xmax.crs.PNRFare;
import javax.swing.*;

public class PanelFare extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane splitBase = new JSplitPane();
  JPanel pnlDetail = new JPanel();
  JScrollPane scrollRawData = new JScrollPane();
  GridLayout gridLayout1 = new GridLayout();
  JTextArea memRawData = new JTextArea();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PanelFare()
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


  public PanelFare(final PNRFare aFare)
    {
    this();
    displayFare(aFare);
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
    pnlDetail.setLayout(gridLayout1);
    memRawData.setText("Fare Info");
    memRawData.setEditable(false);
    this.add(splitBase, BorderLayout.CENTER);
    splitBase.add(pnlDetail, JSplitPane.TOP);
    splitBase.add(scrollRawData, JSplitPane.BOTTOM);
    scrollRawData.getViewport().add(memRawData, null);
    splitBase.setDividerLocation(200);
    }

  /** 
   ***********************************************************************
   * Used by constructor
   ***********************************************************************
   */
  public void displayFare(final PNRFare aFare)
    {
    final DecimalFormat moneyFormat = new DecimalFormat("0.00");

    // set reservation info
    final PropertyGrid propFare = new PropertyGrid( aFare.getGenericPTC() + " Fare Info");

    propFare.addBlankRow();

    propFare.addProperty("Generic PTC",aFare.getGenericPTC());
    propFare.addProperty("Native PTC", aFare.getNativePTC());
    propFare.addProperty("Number of Passengers",aFare.getNumPsgrs());

    final String sFareType;
    if ( aFare.isExempt )
      sFareType = "Tax Exempt";
    else if ( aFare.isContract )
      sFareType = "Contract";
    else if ( aFare.isLowest )
      sFareType = "Lowest";
    else
      sFareType = "Coach";

    propFare.addProperty("Fare Type",sFareType);

    final long iBaseFare   = aFare.getBaseFare();
    final float fBaseFare  = iBaseFare;
    final String sBaseFare = "$" + moneyFormat.format( fBaseFare/100 );
    propFare.addProperty("Base Fare",sBaseFare);

    final long iTax        = aFare.getTax();
    final float fTax       = iTax;
    final String sTax      = "$" + moneyFormat.format( fTax/100 );
    propFare.addProperty("Tax",sTax);

    propFare.addBlankRow();

    pnlDetail.removeAll();
    pnlDetail.add(propFare);

    memRawData.setText( aFare.getRawData() );
    }

}