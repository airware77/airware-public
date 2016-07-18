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
import xmax.crs.*;

public class dialogConnectTranServer extends JDialog {
  JPanel pnlBase = new JPanel();
  JPanel pnlInput = new JPanel();
  JButton btnOK = new JButton();
  JButton btnCancel = new JButton();
  Border border1;
  JPanel pnlButtons = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridLayout gridLayout1 = new GridLayout();
  public boolean isValidData = false;
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JLabel lblServerIP = new JLabel();
  JLabel lblServerPort = new JLabel();
  JTextField edtServerIP = new JTextField();
  JTextField edtServerPort = new JTextField();
  JLabel lblHostType = new JLabel();
  JComboBox cboHostType = new JComboBox();

  /** 
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public dialogConnectTranServer(final Frame aOwner)
    {
    super(aOwner, "Connect to Transaction Server", true);
    this.setModal(true);

    try
      {
      jbInit();
      }
    catch (Exception e)
      {
      e.printStackTrace();
      }

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
    final String[] GDS_HOSTS = {"Amadeus","Apollo","Sabre","Worldspan"};
    final DefaultComboBoxModel cboData = new DefaultComboBoxModel(GDS_HOSTS);
    cboHostType.setModel(cboData);
    cboHostType.setSelectedIndex(0);
    cboHostType.setSelectedItem(this);
    cboHostType.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        cboHostType_actionPerformed(e);
      }
    });

  //  border1 = BorderFactory.createRaisedBevelBorder();
    pnlButtons.setLayout(gridLayout1);
    pnlInput.setLayout(gridBagLayout2);
    btnOK.setText("OK");
    btnOK.addActionListener(new dialogConnectTranServer_btnOK_actionAdapter(this));
   // btnCancel.setBorder(null);
    btnCancel.setText("Cancel");
    gridLayout1.setHgap(4);
    btnCancel.addActionListener(new dialogConnectTranServer_btnCancel_actionAdapter(this));
    pnlBase.setLayout(gridBagLayout1);
    this.setModal(true);
    pnlBase.setMinimumSize(new Dimension(166, 100));
    pnlBase.setPreferredSize(new Dimension(310, 206));
    lblServerIP.setHorizontalAlignment(SwingConstants.RIGHT);
    lblServerIP.setText("IP Address");
    lblServerPort.setHorizontalAlignment(SwingConstants.RIGHT);
    lblServerPort.setText("Port");
    edtServerIP.setText("127.0.0.1");
    edtServerIP.setColumns(10);
    
    edtServerPort.setDocument( new PatternDocument("^[0-9][0-9]?[0-9]?[0-9]?[0-9]?$") );
    edtServerPort.setText("8026");
    edtServerPort.setColumns(10);

    lblHostType.setText("GDS Host");
    pnlInput.setBorder(BorderFactory.createEtchedBorder());
    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlInput.add(lblServerIP, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(lblServerPort, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtServerIP, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(edtServerPort, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblHostType, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(cboHostType, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
    pnlButtons.add(btnOK, null);
    pnlButtons.add(btnCancel, null);
    getContentPane().add(pnlBase);

    this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);


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
  public String getServerIP()
    {
    return( edtServerIP.getText() );
    }


  public int getServerPort()
    {
    final String sPort = edtServerPort.getText().trim();
    if ( sPort.length() > 0 )
      return( Integer.parseInt(sPort) );
    else
      return(-1);
    }

/*
  public int getHostType()
    {
    final int iIndex = cboHostType.getSelectedIndex();

    switch ( iIndex )
      {
      case 0:  return( baseClient.HOST_AMADEUS );   // amadeus
      case 1:  return( baseClient.HOST_APOLLO  );   // apollo
      case 2:  return( baseClient.HOST_SABRE   );   // sabre
      case 3:  return( baseClient.HOST_PARS    );   // worldspan
      default: return(-1);
      }
    }
    */

  public String getHostCode()
    {
    final int iIndex = cboHostType.getSelectedIndex();

    switch ( iIndex )
      {
      case 0:  return( BaseCrs.AMADEUS_CODE   );
      case 1:  return( BaseCrs.APOLLO_CODE    );
      case 2:  return( BaseCrs.SABRE_CODE     );
      case 3:  return( BaseCrs.WORLDSPAN_CODE );
      default: return(null);
      }
    }

  public String getHostName()
    {
    final int iIndex = cboHostType.getSelectedIndex();

    switch ( iIndex )
      {
      case 0:  return( BaseCrs.AMADEUS_NAME   );
      case 1:  return( BaseCrs.APOLLO_NAME    );
      case 2:  return( BaseCrs.SABRE_NAME     );
      case 3:  return( BaseCrs.WORLDSPAN_NAME );
      default: return(null);
      }
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
      final dialogConnectTranServer dlg = new dialogConnectTranServer(null);

      dlg.show();
      if ( dlg.isValidData )
        {
        final String sHostIP   = dlg.getServerIP();
        final int    iPort     = dlg.getServerPort();
        final String sHostName = dlg.getHostName();
        JOptionPane.showMessageDialog(null,"You selected GDS " + sHostName + " thru server " + sHostIP + " port " + iPort);
        }

      }
    catch (Exception e)
      {
      System.out.println(e.toString());
      }

    }

  void cboHostType_actionPerformed(ActionEvent e)
  {

  }

}

/** 
 ***********************************************************************
 * OK button handler
 ***********************************************************************
 */
class dialogConnectTranServer_btnOK_actionAdapter implements ActionListener
{
 private dialogConnectTranServer owner;

 // constructor
 dialogConnectTranServer_btnOK_actionAdapter(final dialogConnectTranServer aOwner)
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
class dialogConnectTranServer_btnCancel_actionAdapter implements ActionListener
{
 private dialogConnectTranServer owner;

 // constructor
 dialogConnectTranServer_btnCancel_actionAdapter(final dialogConnectTranServer aOwner)
   {
   owner = aOwner;
   }

  public void actionPerformed(final ActionEvent e)
    {
    owner.closeCancel(e);
    }
}


