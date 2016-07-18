
// This snippet creates a new dialog box
// with buttons on the bottom.

//Title:
//Version:
//Copyright:
//Author:
//Company:
//Description:

package  xmax.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import xmax.TranServer.*;
import xmax.crs.GnrcCrs;
import xmax.crs.BaseCrs;

public class dialogGetFlifo extends JDialog {
  JPanel pnlBase = new JPanel();
  JPanel pnlInput = new JPanel();
  JButton btnOK = new JButton();
  JButton btnCancel = new JButton();
  Border border1;
  JPanel pnlButtons = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridLayout gridLayout1 = new GridLayout();
  public boolean isValidData = false;
  final String CrsCode;
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel lblCarrier = new JLabel();
  JTextField edtCarrier = new JTextField();
  JLabel lblFlightNum = new JLabel();
  JTextField edtFlightNum = new JTextField();
  JLabel lblDepDate = new JLabel();
  JTextField edtDepDate = new JTextField();
  JLabel lblDepCity = new JLabel();
  JTextField edtDepCity = new JTextField();
  JLabel lblArrCity = new JLabel();
  JTextField edtArrCity = new JTextField();

  /** 
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public dialogGetFlifo(final Frame aOwner, final String aCrsCode)
    {
    super(aOwner, "Get Flifo", true);

    try
      {
      jbInit();
      }
    catch (Exception e)
      {
      e.printStackTrace();
      }

    CrsCode = aCrsCode;
    final String sHostName = BaseCrs.HostCodeToName( CrsCode );
    this.setTitle("Get " + sHostName + " Flight Information");

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
    border1 = BorderFactory.createRaisedBevelBorder();
    pnlButtons.setLayout(gridLayout1);
    pnlInput.setLayout(gridBagLayout2);
    btnOK.setText("OK");
    btnOK.addActionListener(new dialogGetFlifo_btnOK_actionAdapter(this));
    btnCancel.setText("Cancel");
    gridLayout1.setHgap(4);
    btnCancel.addActionListener(new dialogGetFlifo_btnCancel_actionAdapter(this));
    pnlBase.setLayout(gridBagLayout1);
    this.setModal(true);
    this.setResizable(true);
    pnlBase.setMinimumSize(new Dimension(420, 100));
    pnlBase.setPreferredSize(new Dimension(430, 200));
    pnlInput.setBorder(BorderFactory.createEtchedBorder());
    lblCarrier.setText("Carrier");
    edtCarrier.setColumns(7);
    lblFlightNum.setText("Flight");
    edtFlightNum.setColumns(7);
    lblDepDate.setText("Leaving");
    edtDepDate.setColumns(7);
    lblDepCity.setText("From");
    edtDepCity.setColumns(7);
    lblArrCity.setText("To");
    edtArrCity.setColumns(7);
    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlInput.add(lblCarrier, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtCarrier, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 20), 0, 0));
    pnlInput.add(lblFlightNum, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
    pnlInput.add(edtFlightNum, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(lblDepDate, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtDepDate, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 20), 0, 0));
    pnlInput.add(lblDepCity, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtDepCity, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 20), 0, 0));
    pnlInput.add(lblArrCity, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 20, 10, 10), 0, 0));
    pnlInput.add(edtArrCity, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 50), 0, 0));
    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
    pnlButtons.add(btnOK, null);
    pnlButtons.add(btnCancel, null);
    getContentPane().add(pnlBase);

    this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);

    edtCarrier.setDocument(   new PatternDocument(2,PatternDocument.UPPER_CASE) );
    edtFlightNum.setDocument( new PatternDocument("^[1-9][0-9]?[0-9]?[0-9]?[0-9]?$") );
    edtDepCity.setDocument(   new PatternDocument(3,PatternDocument.UPPER_CASE) );
    edtArrCity.setDocument(   new PatternDocument(3,PatternDocument.UPPER_CASE) );

    // default to yesterdays date
    final long iDate = System.currentTimeMillis() - 86400000L;
    final String sDepDate = dialogUtils.formatDate(iDate);
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
  public String getCarrier()
    {
    return( edtCarrier.getText() );
    }

  public int getFlightNum()
    {
    try
      {
      final int iFlight = Integer.parseInt( edtFlightNum.getText() );
      return(iFlight);
      }
    catch (Exception e)
      {
      return(0);
      }
    }

  public String getDepCity()
    {
    return( edtDepCity.getText() );
    }

  public String getArrCity()
    {
    return( edtArrCity.getText() );
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


  public ReqGetFlifo getRequest()
    {
    final ReqGetFlifo request = new ReqGetFlifo(CrsCode,getCarrier(), getFlightNum(), getDepDate() );

    request.DepCity = getDepCity();
    request.ArrCity = getArrCity();

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
      final dialogGetFlifo dlg = new dialogGetFlifo(null,"AA");

      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetFlifo request  = dlg.getRequest();

        JOptionPane.showMessageDialog(null,"You selected flifo for " + request.Carrier + " " + request.FlightNum + " from " + request.DepCity + " to " + request.ArrCity + " on " + request.DepDate);
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
class dialogGetFlifo_btnOK_actionAdapter implements ActionListener
{
 private dialogGetFlifo owner;

 // constructor
 dialogGetFlifo_btnOK_actionAdapter(final dialogGetFlifo aOwner)
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
class dialogGetFlifo_btnCancel_actionAdapter implements ActionListener
{
 private dialogGetFlifo owner;

 // constructor
 dialogGetFlifo_btnCancel_actionAdapter(final dialogGetFlifo aOwner)
   {
   owner = aOwner;
   }

  public void actionPerformed(final ActionEvent e)
    {
    owner.closeCancel(e);
    }
}


