
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import javax.swing.*;
import xmax.crs.PNR;
import xmax.crs.GetPNR.*;
import xmax.TranServer.GnrcFormat;

public class PanelRemark extends JPanel
{
  GridLayout gridLayoutBase = new GridLayout();
  JTextArea memRemark = new JTextArea();
  JPanel pnlRemarkDetails = new JPanel();
  GridLayout gridLayoutDetails = new GridLayout();

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PanelRemark()
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


  public PanelRemark(final PNRRemark aRemark)
    {
    this();
    displayRemark(aRemark);
    }


  public PanelRemark(final PNRRemark aRemark, final PNR aPNR)
    {
    this();
    displayRemark(aRemark,aPNR);
    }

  /** 
   ***********************************************************************
   * Used with constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
  {
    memRemark.setLineWrap(true);
    memRemark.setWrapStyleWord(true);
    memRemark.setBackground(Color.lightGray);
    memRemark.setBorder(null);
    memRemark.setText("This is a remark");
    gridLayoutBase.setColumns(1);
    gridLayoutBase.setRows(2);
    this.setLayout(gridLayoutBase);
    pnlRemarkDetails.setLayout(gridLayoutDetails);
    gridLayoutDetails.setColumns(1);
    gridLayoutDetails.setRows(0);
    this.add(pnlRemarkDetails, null);
    this.add(memRemark, null);
  }

  /** 
   ***********************************************************************
   * Used to update panel components to display the given remark
   ***********************************************************************
   */
  public void displayRemark(final PNRRemark aRemark)
    {
    displayRemark(aRemark,null);
    }


  public void displayRemark(final PNRRemark aRemark, final PNR aPNR)
    {
    // set the remark text
    memRemark.setText(aRemark.RemarkText);

    // clear out the existing remark data
    pnlRemarkDetails.removeAll();

    // get new remark property panel
    final PropertyGrid pnlDetail = getRemarkPropertyPanel(aRemark,aPNR);
    pnlRemarkDetails.add(pnlDetail);
    }

  /** 
   ***********************************************************************
   * Return a panel with remark properties on it
   ***********************************************************************
   */
  private PropertyGrid getRemarkPropertyPanel(final PNRRemark aRemark, final PNR aPNR)
    {
    final PropertyGrid pnlDetail = new PropertyGrid( aRemark.getRemarkType() + " Remark" );

    // add a blank line at the top to center everything
    pnlDetail.addBlankRow(1);

    // show message number
    pnlDetail.addProperty("Message #",aRemark.MessageNumber);

    // set the name association
    final String sNameAssoc = aRemark.getAssocNameDesc(aPNR);
    if ( GnrcFormat.NotNull(sNameAssoc) )
      pnlDetail.addProperty("Name",sNameAssoc);

    // set the segment association
    final String sSegAssoc = aRemark.getAssocSegmentDesc(aPNR);
    if ( GnrcFormat.NotNull(sSegAssoc) )
      pnlDetail.addProperty("Segment",sSegAssoc);

    // set specific fields for the various types of remarks
    if ( aRemark instanceof PNRSsrRemark )
      {
      final PNRSsrRemark ssr = (PNRSsrRemark )aRemark;
      pnlDetail.addProperty("Carrier",ssr.Carrier);
      pnlDetail.addProperty("SSR Code",ssr.Code);
      }
    else if ( aRemark instanceof PNROsiRemark )
      {
      final PNROsiRemark osi = (PNROsiRemark )aRemark;
      pnlDetail.addProperty("Carrier",osi.Carrier);
      }
    else if ( aRemark instanceof PNRSeatRemark )
      {
      final PNRSeatRemark seat = (PNRSeatRemark )aRemark;
      pnlDetail.addProperty("Seat",seat.Seat);
      }

    // add a final blank line to center everything
    pnlDetail.addBlankRow(1);

    return(pnlDetail);
    }

}
