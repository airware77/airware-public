
// This snippet creates a new dialog box
// with buttons on the bottom.

//Title:
//Version:
//Copyright:
//Author:
//Company:
//Description:

package xmax.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import xmax.crs.GnrcCrs;
import xmax.TranServer.*;
import xmax.TranServer.GnrcFormat;
import xmax.crs.GnrcParser;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.SimpleDateFormat;
import xmax.crs.BaseCrs;

public class dialogGetAvail extends JDialog {
  JPanel pnlBase = new JPanel();
  JPanel pnlInput = new JPanel();
  JButton btnOK = new JButton();
  JButton btnCancel = new JButton();
  Border border1;
  JPanel pnlButtons = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridLayout gridLayout1 = new GridLayout();
  public boolean isValidData = false;
  private String CrsCode;
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel lblDepCity = new JLabel();
  JTextField edtDepCity = new JTextField();
  JLabel lblArrCity = new JLabel();
  JTextField edtArrCity = new JTextField();
  JLabel lblDepDate = new JLabel();
  JTextField edtDepDate = new JTextField();
  JLabel lblQualtity = new JLabel();
  JComboBox cboQuality = new JComboBox();
  JLabel lblNumItins = new JLabel();
  JTextField edtNumItins = new JTextField();
  JLabel lblCarrier = new JLabel();
  JTextField edtCarrier = new JTextField();
  JLabel lblFlightNum = new JLabel();
  JTextField edtFlight = new JTextField();
  JCheckBox chkExtendedInfo = new JCheckBox();

  /** 
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public dialogGetAvail(final Frame aOwner, final String aCrsCode)
    {
    super(aOwner, "", true);

    try
      {
      jbInit();
      }
    catch (Exception e)
      {
      e.printStackTrace();
      }

    CrsCode = aCrsCode;
    final String sHostName = BaseCrs.HostCodeToName(CrsCode);
    this.setTitle("Get " + sHostName + " Availability");

    pack();
    dialogUtils.centerWindow(this);
    }

  /** 
   ***********************************************************************
   * used by constructors
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    final String[] ITIN_TYPES = {"CONNECT","DIRECT","NONSTOP"};
    final DefaultComboBoxModel cboData = new DefaultComboBoxModel(ITIN_TYPES);
    cboQuality.setModel(cboData);
    cboQuality.setSelectedIndex(0);


    border1 = BorderFactory.createRaisedBevelBorder();
    pnlButtons.setLayout(gridLayout1);
    pnlInput.setLayout(gridBagLayout2);
    btnOK.setText("OK");
    btnOK.addActionListener(new dialogGetAvail_btnOK_actionAdapter(this));
    btnCancel.setText("Cancel");
    gridLayout1.setHgap(4);
    btnCancel.addActionListener(new dialogGetAvail_btnCancel_actionAdapter(this));
    pnlBase.setLayout(gridBagLayout1);
    this.setModal(true);
    this.setResizable(false);
    pnlBase.setMinimumSize(new Dimension(166, 100));
    pnlBase.setPreferredSize(new Dimension(450, 286));
    lblDepCity.setText("From");
    edtDepCity.setColumns(5);
    lblArrCity.setText("To");
    edtArrCity.setColumns(5);
    lblDepDate.setText("Leaving");
    edtDepDate.setText("5MAR  800A");
    edtDepDate.setColumns(12);
    lblQualtity.setText("Quality");
    lblNumItins.setText("# Itins");
    edtNumItins.setColumns(5);
    edtNumItins.setText("10");
    cboQuality.setPreferredSize(new Dimension(100, 24));
    lblCarrier.setText("Carrier");
    edtCarrier.setColumns(5);
    lblFlightNum.setText("Flight");
    edtFlight.setColumns(5);
    pnlInput.setBorder(BorderFactory.createEtchedBorder());
    chkExtendedInfo.setText("Extended Information");
    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlInput.add(lblDepCity, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtDepCity, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    pnlInput.add(lblArrCity, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    pnlInput.add(edtArrCity, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(lblDepDate, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtDepDate, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(lblQualtity, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(cboQuality, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
    pnlInput.add(lblNumItins, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 30, 10, 10), 0, 0));
    pnlInput.add(edtNumItins, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(lblCarrier, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtCarrier, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    pnlInput.add(lblFlightNum, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    pnlInput.add(edtFlight, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(chkExtendedInfo, new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
    pnlButtons.add(btnOK, null);
    pnlButtons.add(btnCancel, null);
    getContentPane().add(pnlBase);

    this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);

    edtDepCity.setDocument( new PatternDocument(3,PatternDocument.UPPER_CASE) );
    edtArrCity.setDocument( new PatternDocument(3,PatternDocument.UPPER_CASE) );
    edtCarrier.setDocument( new PatternDocument(2,PatternDocument.UPPER_CASE) );
    edtFlight.setDocument( new PatternDocument("^[0-9][0-9]?[0-9]?[0-9]?[0-9]?$") );
    edtNumItins.setDocument( new PatternDocument("^[0-9][0-9]?[0-9]?$") );

    edtNumItins.setText("10");

    final long iFutureDate = System.currentTimeMillis() + 15552000000L;
    final String sDepDate = GnrcFormat.FormatReadableDateTime(iFutureDate);
    edtDepDate.setText(sDepDate);
    }

  /** 
   ***********************************************************************
   * OK button
   ***********************************************************************
   */
  void closeOK(ActionEvent e)
    {
    // dispose();
    isValidData = true;
    setVisible(false);
    }

  /** 
   ***********************************************************************
   * cancel button
   ***********************************************************************
   */
  void closeCancel(ActionEvent e)
    {
    // dispose();
    isValidData = false;
    setVisible(false);
    }

  /** 
   ***********************************************************************
   * get functions
   ***********************************************************************
   */
  public String getDepCity()
    {
    return( edtDepCity.getText() );
    }

  public String getArrCity()
    {
    return( edtArrCity.getText() );
    }

  public String getCarrier()
    {
    return( edtCarrier.getText() );
    }

  public int getFlight()
    {
    try
      {
      final int iFlight = Integer.parseInt( edtFlight.getText() );
      return(iFlight);
      }
    catch (Exception e)
      {
      return(0);
      }

    }

  public boolean getExtendedInfo()
    {
    return( chkExtendedInfo.isSelected() );
    }


  public int getNumItins()
    {
    final String sNumItins = edtNumItins.getText().trim();
    if ( sNumItins.length() > 0 )
      return( Integer.parseInt(sNumItins) );
    else
      return(0);
    }

  public int getQuality()
    {
    final int iIndex = cboQuality.getSelectedIndex();

    if ( iIndex == 2 )
      return(ReqGetAvail.ITIN_NONSTOP);
    else if ( iIndex == 1 )
      return(ReqGetAvail.ITIN_DIRECT);
    else
      return(ReqGetAvail.ITIN_CONNECT);
    }

  public String getDepDate()
    {
    try
      {
      final long depDate    = dialogUtils.ScanDateTime( edtDepDate.getText() );
      final String sDepDate = GnrcFormat.FormatCRSDate(depDate);
      return(sDepDate);
      }
    catch (Exception e)
      {
      return("");
      }
    }

  public String getDepTime()
    {
    try
      {
      final long depDate    = dialogUtils.ScanDateTime( edtDepDate.getText() );
      final SimpleDateFormat CrsDate = new SimpleDateFormat("HHmm");
      final String sTime = CrsDate.format( new Date(depDate) ).toUpperCase().trim();
      return( sTime );
      }
    catch (Exception e)
      {
      return("");
      }
    }

  public ReqGetAvail getRequest()
    {
    final ReqGetAvail request = new ReqGetAvail(CrsCode,getDepCity(),getArrCity(),getDepDate());

    request.Carrier           = getCarrier();
    request.FlightNum         = getFlight();
    request.DepTime           = getDepTime();
    request.NumItins          = getNumItins();
    request.ItinQuality       = getQuality();
    request.getSegmentDetails = getExtendedInfo();

    return(request);
    }


  /** 
   ***********************************************************************
   * Main function for unit tests
   ***********************************************************************
   */
  public static void main(String[] args)
    {

    try
      {
      final dialogGetAvail dlg = new dialogGetAvail(null,"AA");

      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetAvail request  = dlg.getRequest();

        JOptionPane.showMessageDialog(null,"You selected availabity from " + request.DepCity + " to " + request.ArrCity + " on " + request.DepDate + " " + request.DepTime );
        }

      }
    catch (Exception e)
      {
      System.out.println(e.toString());
      }

    }

}

/** 
 ***********************************************************************
 * OK button handler
 ***********************************************************************
 */
class dialogGetAvail_btnOK_actionAdapter implements ActionListener
{
 private dialogGetAvail owner;

 // constructor
 dialogGetAvail_btnOK_actionAdapter(final dialogGetAvail aOwner)
   {
   owner = aOwner;
   }

 public void actionPerformed(final ActionEvent e)
   {
   owner.closeOK(e);
   }
}


/** 
 ***********************************************************************
 * cancel button handler
 ***********************************************************************
 */
class dialogGetAvail_btnCancel_actionAdapter implements ActionListener
{
 private dialogGetAvail owner;

 // constructor
 dialogGetAvail_btnCancel_actionAdapter(final dialogGetAvail aOwner)
   {
   owner = aOwner;
   }

  public void actionPerformed(final ActionEvent e)
    {
    owner.closeCancel(e);
    }
}


