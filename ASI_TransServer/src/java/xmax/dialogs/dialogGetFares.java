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

public class dialogGetFares extends JDialog {
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
  JLabel lblLocator = new JLabel();
  JTextField edtLocator = new JTextField();
  JLabel lblFareType = new JLabel();
  JComboBox cboFareType = new JComboBox();

  /** 
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public dialogGetFares(final Frame aOwner, final String aCrsCode)
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
    final String sHostName = BaseCrs.HostCodeToName( CrsCode );
    this.setTitle("Get Fares from " + sHostName);

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
    final String[] FARE_TYPES = {"COACH","LOWEST","CONTRACT"};
    final DefaultComboBoxModel cboData = new DefaultComboBoxModel(FARE_TYPES);
    cboFareType.setModel(cboData);
    cboFareType.setSelectedIndex(1);

    pnlButtons.setLayout(gridLayout1);
    pnlInput.setLayout(gridBagLayout2);
    btnOK.setText("OK");
    btnOK.addActionListener(new dialogGetFares_btnOK_actionAdapter(this));
    btnCancel.setText("Cancel");
    gridLayout1.setHgap(4);
    btnCancel.addActionListener(new dialogGetFares_btnCancel_actionAdapter(this));
    pnlBase.setLayout(gridBagLayout1);
    this.setTitle("Get Fares");
    this.setModal(true);
    this.setResizable(false);
    pnlBase.setMinimumSize(new Dimension(166, 100));
    pnlBase.setPreferredSize(new Dimension(275, 170));
    pnlInput.setBorder(BorderFactory.createEtchedBorder());
    lblLocator.setText("Locator");
    lblFareType.setText("Fare Type");
    edtLocator.setColumns(10);
    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlInput.add(lblLocator, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtLocator, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblFareType, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(cboFareType, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
    pnlButtons.add(btnOK, null);
    pnlButtons.add(btnCancel, null);
    getContentPane().add(pnlBase);

    this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);

    edtLocator.setDocument( new PatternDocument(6,PatternDocument.UPPER_CASE) );
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
  public String getLocator()
    {
    return( edtLocator.getText() );
    }

  public String getFareType()
    {
    final int iIndex = cboFareType.getSelectedIndex();
    
    switch ( iIndex )
      {
      case 0:    return("R");     // regular coach fare
      case 1:    return("L");     // lowest fare
      case 2:    return("C");     // contract fare
      default:   return("R");     // coach fare
      }
    }


  public ReqGetFare getRequest()
    {
    final ReqGetFare request = new ReqGetFare(CrsCode,getLocator(),getFareType());
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
      final dialogGetFares dlg = new dialogGetFares(null,"AA");

      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetFare request = dlg.getRequest();
        JOptionPane.showMessageDialog(null,"Get fares for locator " + request.getLocator() + " fare type " + request.getFareType());
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
class dialogGetFares_btnOK_actionAdapter implements ActionListener
{
 private dialogGetFares owner;

 // constructor
 dialogGetFares_btnOK_actionAdapter(final dialogGetFares aOwner)
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
class dialogGetFares_btnCancel_actionAdapter implements ActionListener
{
 private dialogGetFares owner;

 // constructor
 dialogGetFares_btnCancel_actionAdapter(final dialogGetFares aOwner)
   {
   owner = aOwner;
   }

  public void actionPerformed(final ActionEvent e)
    {
    owner.closeCancel(e);
    }
}


 