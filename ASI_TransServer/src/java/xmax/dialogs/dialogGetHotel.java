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
import xmax.TranServer.*;
import xmax.crs.GnrcCrs;
import xmax.crs.BaseCrs;

public class dialogGetHotel extends JDialog {
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
  JLabel lblCompanyCode = new JLabel();
  JTextField edtCompanyCode = new JTextField();
  JLabel lblLocationCode = new JLabel();
  JTextField edtLocationCode = new JTextField();
  JLabel lblCityCode = new JLabel();
  JTextField edtCityCode = new JTextField();

  /** 
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public dialogGetHotel(final Frame aOwner, final String aCrsCode)
    {
    super(aOwner, "Get PNR", true);

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
    this.setTitle("Get " + sHostName + " Hotel Information");

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
    pnlButtons.setLayout(gridLayout1);
    pnlInput.setLayout(gridBagLayout2);
    btnOK.setText("OK");
    btnOK.addActionListener(new dialogGetHotel_btnOK_actionAdapter(this));
    btnCancel.setText("Cancel");
    gridLayout1.setHgap(4);
    btnCancel.addActionListener(new dialogGetHotel_btnCancel_actionAdapter(this));
    pnlBase.setLayout(gridBagLayout1);
    this.setModal(true);
    this.setResizable(false);
    pnlBase.setMinimumSize(new Dimension(166, 100));
    pnlBase.setPreferredSize(new Dimension(285, 210));
    lblCompanyCode.setText("Company Code");
    edtCompanyCode.setColumns(5);
    lblLocationCode.setText("Location Code");
    edtLocationCode.setColumns(5);
    lblCityCode.setText("City Code");
    edtCityCode.setColumns(5);
    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
    pnlInput.add(lblCompanyCode, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtCompanyCode, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(lblLocationCode, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtLocationCode, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(lblCityCode, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtCityCode, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 50), 0, 0));
    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
    pnlButtons.add(btnOK, null);
    pnlButtons.add(btnCancel, null);
    getContentPane().add(pnlBase);

    this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);

    edtCompanyCode.setDocument( new PatternDocument(2,PatternDocument.UPPER_CASE) );
    edtLocationCode.setDocument( new PatternDocument(10,PatternDocument.UPPER_CASE) );
    edtCityCode.setDocument( new PatternDocument(3,PatternDocument.UPPER_CASE) );
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
  public String getCompanyCode()
    {
    return( edtCompanyCode.getText() );
    }

  public String getLocationCode()
    {
    return( edtLocationCode.getText() );
    }

  public String getCityCode()
    {
    return( edtCityCode.getText() );
    }


  public ReqGetHotelInfo getRequest()
    {
    final ReqGetHotelInfo request = new ReqGetHotelInfo(CrsCode,getCompanyCode(),getLocationCode(),getCityCode());
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
      final dialogGetHotel dlg = new dialogGetHotel(null,"AA");

      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetHotelInfo request  = dlg.getRequest();

        JOptionPane.showMessageDialog(null,"You selected hotel info for company " + request.HotelChain + " location " + request.PropertyCode + " city " + request.CityCode);
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
class dialogGetHotel_btnOK_actionAdapter implements ActionListener
{
 private dialogGetHotel owner;

 // constructor
 dialogGetHotel_btnOK_actionAdapter(final dialogGetHotel aOwner)
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
class dialogGetHotel_btnCancel_actionAdapter implements ActionListener
{
 private dialogGetHotel owner;

 // constructor
 dialogGetHotel_btnCancel_actionAdapter(final dialogGetHotel aOwner)
   {
   owner = aOwner;
   }

  public void actionPerformed(final ActionEvent e)
    {
    owner.closeCancel(e);
    }
}


 