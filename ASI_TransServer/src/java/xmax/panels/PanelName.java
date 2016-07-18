
//Title:        TranServer
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import xmax.crs.PNR;
import xmax.crs.GetPNR.*;
import xmax.TranServer.GnrcFormat;
import javax.swing.*;

public class PanelName extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JTextArea memMessages = new JTextArea();
  JPanel pnlNameDetails = new JPanel();
  GridLayout gridLayout1 = new GridLayout();

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PanelName()
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


  public PanelName(final PNRNameElement aName)
    {
    this();
    displayName(aName);
    }


  public PanelName(final PNRNameElement aName, final PNR aPNR)
    {
    this();
    displayName(aName,aPNR);
    }

  /** 
   ***********************************************************************
   * Used by constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    memMessages.setLineWrap(true);
    memMessages.setPreferredSize(new Dimension(400, 70));
    memMessages.setWrapStyleWord(true);
    memMessages.setBackground(Color.lightGray);
    memMessages.setBorder(BorderFactory.createLineBorder(Color.black));
    memMessages.setText("");
    memMessages.setEditable(false);
    this.setLayout(borderLayout1);
    pnlNameDetails.setBorder(BorderFactory.createLineBorder(Color.black));
    pnlNameDetails.setLayout(gridLayout1);
    this.add(memMessages, BorderLayout.SOUTH);
    this.add(pnlNameDetails, BorderLayout.CENTER);
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  public void displayName(final PNRNameElement aName, final PNR aPNR)
    {
    displayName(aName);


    // set the associated remarks for the given name
    boolean hasMessages = false;
    memMessages.setText("");

    if ( aPNR instanceof PNR )
      {
      final PNRRemark[] remarks = aPNR.getRemarks(aName);
      if ( remarks instanceof PNRRemark[] )
        {
        for ( int i = 0; i < remarks.length; i++ )
          {
          hasMessages = true;
          if ( remarks[i] instanceof PNRSsrRemark )
            {
            final PNRSsrRemark ssr = (PNRSsrRemark )remarks[i];
            memMessages.append(ssr.Code + " " + ssr.RemarkText);
            }
          else
            memMessages.append(remarks[i].RemarkText);
          }
        }
      }


    // be sure the message memo control is in the layout if there are messages to be shown
    final Container Parent = memMessages.getParent();
    if ( Parent instanceof Container )
      {      // messages area is already in layout
      if ( hasMessages == false )
        this.remove(memMessages);
      }
    else if ( hasMessages )
      this.add(memMessages, BorderLayout.SOUTH);
    }


  public void displayName(final PNRNameElement aName)
    {
    // set the remark text
    memMessages.setText("");

    // clear out the existing name data
    pnlNameDetails.removeAll();

    // get new name property panel
    final PropertyGrid pnlDetail = getNamePropertyPanel(aName);
    pnlNameDetails.add(pnlDetail);
    }

  /** 
   ***********************************************************************
   * Return a panel with name properties on it
   ***********************************************************************
   */
  private PropertyGrid getNamePropertyPanel(final PNRNameElement aName)
    {
    final PropertyGrid pnlDetail = new PropertyGrid( aName.getFullName() );

    // add a blank line at the top to center everything
    pnlDetail.addBlankRow(1);


    // required name and seat count info
    if ( GnrcFormat.NotNull(aName.Title) )
      pnlDetail.addProperty("Title",aName.Title);
    pnlDetail.addProperty("First Name",aName.FirstName);
    if ( GnrcFormat.NotNull(aName.MiddleName) )
      pnlDetail.addProperty("Middle Name", aName.MiddleName);
    pnlDetail.addProperty("Last Name",aName.LastName);

    pnlDetail.addProperty("Seats",aName.NumSeats);


    // optional passenger data for ID and infant name
    if ( GnrcFormat.NotNull(aName.getPassengerID()) )
      pnlDetail.addProperty("Passenger ID", aName.getPassengerID());

    if ( GnrcFormat.NotNull(aName.InfantName) )
      pnlDetail.addProperty("Infant Name", aName.InfantName);


    // PTC, birthdate, and age fields
    pnlDetail.addProperty("Type",aName.PTC);

    if ( aName.BirthDate > 0 )
      pnlDetail.addProperty("Birth Date", aName.BirthDate,pnlDetail.DATE_NUM_FORMAT);

    if ( aName.Age > 0 )
      pnlDetail.addProperty("Age", aName.Age);


    // add a final blank line to center everything
    pnlDetail.addBlankRow(1);

    return(pnlDetail);
    }

}
