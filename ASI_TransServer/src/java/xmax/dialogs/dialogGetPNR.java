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

public class dialogGetPNR extends JDialog {
  JPanel pnlBase = new JPanel();
  JPanel pnlInput = new JPanel();
  JButton btnOK = new JButton();
  JButton btnCancel = new JButton();
  Border border1;
  JPanel pnlButtons = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridLayout gridLayout1 = new GridLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel lblLocator = new JLabel();
  JTextField edtLocator = new JTextField();
  JLabel lblQueue = new JLabel();
  JTextField edtQueue = new JTextField();
  JCheckBox chkExtended = new JCheckBox();
  public boolean isValidData = false;
  public String CrsCode;

  /** 
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public dialogGetPNR(final Frame aOwner, final String aCrsCode)
    {
    super(aOwner, "" , true);


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
    this.setTitle("Get " + sHostName + " PNR");

    if ( isValid() == false )
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
    btnOK.addActionListener(new dialogGetPNR_btnOK_actionAdapter(this));
    btnCancel.setText("Cancel");
    gridLayout1.setHgap(4);
    btnCancel.addActionListener(new dialogGetPNR_btnCancel_actionAdapter(this));
    pnlBase.setLayout(gridBagLayout1);
  //  this.setTitle("Get PNR");
    this.setModal(true);
    lblLocator.setHorizontalAlignment(SwingConstants.RIGHT);
    lblLocator.setText("Locator");
    lblQueue.setText("Queue Name");
    edtQueue.setColumns(8);
    edtLocator.setColumns(8);
    chkExtended.setText("Extended Information");
    pnlBase.setMinimumSize(new Dimension(166, 100));
    pnlBase.setPreferredSize(new Dimension(290, 200));
    pnlInput.setBorder(BorderFactory.createEtchedBorder());
    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlInput.add(lblLocator, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtLocator, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(lblQueue, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 50, 10, 10), 0, 0));
    pnlInput.add(edtQueue, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 50), 0, 0));
    pnlInput.add(chkExtended, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(10, 10, 10, 10), 0, 0));
    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
    pnlButtons.add(btnOK, null);
    pnlButtons.add(btnCancel, null);
    getContentPane().add(pnlBase);

    this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);

    edtLocator.setDocument( new PatternDocument(6,PatternDocument.UPPER_CASE) );
    edtQueue.setDocument( new PatternDocument(10,PatternDocument.UPPER_CASE) );
    }

  /** 
   ***********************************************************************
   * get methods
   ***********************************************************************
   */
  public String getLocator()
    {
    return( edtLocator.getText().toUpperCase() );
    }

  public String getQueue()
    {
    return( edtQueue.getText().toUpperCase() );
    }

  public boolean getExtendedInfo()
    {
    return( chkExtended.isSelected() );
    }

  public ReqGetPNR getRequest()
    {
    final ReqGetPNR request = new ReqGetPNR(CrsCode);

    request.Locator      = getLocator();
    request.QueueName    = getQueue();
    request.ExtendedInfo = getExtendedInfo();

    return( request );
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
   * Main function for unit tests
   ***********************************************************************
   */
  public static void main(String[] args)
    {

    try
      {
      final dialogGetPNR dlg = new dialogGetPNR(null,"AA");

      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetPNR request  = dlg.getRequest();

        JOptionPane.showMessageDialog(null,"You selected locator " + request.Locator + " queue " + request.QueueName + " extended " + request.ExtendedInfo );
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
class dialogGetPNR_btnOK_actionAdapter implements ActionListener
{
 private dialogGetPNR owner;

 // constructor
 dialogGetPNR_btnOK_actionAdapter(final dialogGetPNR aOwner)
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
class dialogGetPNR_btnCancel_actionAdapter implements ActionListener
{
 private dialogGetPNR owner;

 // constructor
 dialogGetPNR_btnCancel_actionAdapter(final dialogGetPNR aOwner)
   {
   owner = aOwner;
   }

  public void actionPerformed(final ActionEvent e)
    {
    owner.closeCancel(e);
    }
}


