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

public class dialogIssueTicket extends JDialog {
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
  JLabel lblCommission = new JLabel();
  JTextField edtCommission = new JTextField();
  JLabel lblFop = new JLabel();
  JTextField edtFop = new JTextField();
  JLabel lblTourCode = new JLabel();
  JTextField edtTourCode = new JTextField();
  JLabel lblEndorsement = new JLabel();
  JTextField edtEndorsement = new JTextField();
  JLabel lblCarrier = new JLabel();
  JTextField edtCarrier = new JTextField();

  /** 
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public dialogIssueTicket(final Frame aOwner, final String aCrsCode)
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
    this.setTitle("Issue Ticket for " + sHostName);

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
    btnOK.addActionListener(new dialogIssueTicket_btnOK_actionAdapter(this));
    btnCancel.setText("Cancel");
    gridLayout1.setHgap(4);
    btnCancel.addActionListener(new dialogIssueTicket_btnCancel_actionAdapter(this));
    pnlBase.setLayout(gridBagLayout1);
    this.setTitle("Issue Ticket");
    this.setModal(true);
    this.setResizable(false);
    pnlBase.setMinimumSize(new Dimension(166, 100));
    pnlBase.setPreferredSize(new Dimension(530, 185));
    lblLocator.setText("Locator");
    edtLocator.setColumns(8);
    lblFareType.setText("Fare Type");
    lblCommission.setText("Commission");
    lblFop.setText("Form of Payment");
    lblTourCode.setText("Tour Code");
    lblEndorsement.setText("Endorsement");
    lblCarrier.setText("Carrier");
    edtCommission.setColumns(8);
    edtTourCode.setColumns(8);
    edtCarrier.setColumns(8);
    edtFop.setColumns(10);
    edtEndorsement.setColumns(10);
    pnlInput.setBorder(BorderFactory.createEtchedBorder());
    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    pnlInput.add(lblLocator, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtLocator, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblFareType, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(cboFareType, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblCommission, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtCommission, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblFop, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtFop, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblTourCode, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtTourCode, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblEndorsement, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtEndorsement, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlInput.add(lblCarrier, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
    pnlInput.add(edtCarrier, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
    pnlButtons.add(btnOK, null);
    pnlButtons.add(btnCancel, null);
    getContentPane().add(pnlBase);

    this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);

    edtLocator.setDocument( new PatternDocument(6,PatternDocument.UPPER_CASE) );
    edtCarrier.setDocument( new PatternDocument(2,PatternDocument.UPPER_CASE) );
    edtFop.setDocument( new PatternDocument(null,PatternDocument.UPPER_CASE) );
    edtEndorsement.setDocument( new PatternDocument(null,PatternDocument.UPPER_CASE) );
    edtTourCode.setDocument( new PatternDocument(null,PatternDocument.UPPER_CASE) );
    edtCommission.setDocument( new PatternDocument("^[1-9][0-9]?[0-9]?\\.?[0-9]?[0-9]?\\%?$",PatternDocument.UPPER_CASE) );
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

  public String getCarrier()
    {
    return( edtCarrier.getText() );
    }

  public String getFop()
    {
    return( edtFop.getText() );
    }

  public String getTourCode()
    {
    return( edtTourCode.getText() );
    }

  public String getEndorsement()
    {
    return( edtEndorsement.getText() );
    }

  public float getCommissionAmount()
    {
    final String sCommission = edtCommission.getText().trim();

    if ( sCommission.endsWith("%") )
      return( 0 );
    else
      return( Float.parseFloat(sCommission) );
    }

  public float getCommissionPercent()
    {
    String sCommission = edtCommission.getText().trim();

    if ( sCommission.endsWith("%") )
      {
      sCommission = sCommission.substring(0, sCommission.length() - 1);
      return( Float.parseFloat(sCommission) );
      }
    else
      return( 0 );
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

  public ReqIssueTicket getRequest()
    {
    final ReqIssueTicket request = new ReqIssueTicket(CrsCode,getLocator(),getFareType());

    request.TourCode          = getTourCode();
    request.EndorsementInfo   = getEndorsement();
    request.FOP               = getFop();
    request.ValidatingCarrier = getCarrier();
    request.CommissionAmount  = getCommissionAmount();
    request.CommissionPercent = getCommissionPercent();

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
      final dialogIssueTicket dlg = new dialogIssueTicket(null,"AA");

      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqIssueTicket request = dlg.getRequest();
        JOptionPane.showMessageDialog(null,"Ticket issued for locator " + request.getLocator() + " Fare Type " + request.getFareType() + " Commission Amount " + request.CommissionAmount + " Commission % " + request.CommissionPercent);
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
class dialogIssueTicket_btnOK_actionAdapter implements ActionListener
{
 private dialogIssueTicket owner;

 // constructor
 dialogIssueTicket_btnOK_actionAdapter(final dialogIssueTicket aOwner)
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
class dialogIssueTicket_btnCancel_actionAdapter implements ActionListener
{
 private dialogIssueTicket owner;

 // constructor
 dialogIssueTicket_btnCancel_actionAdapter(final dialogIssueTicket aOwner)
   {
   owner = aOwner;
   }

  public void actionPerformed(final ActionEvent e)
    {
    owner.closeCancel(e);
    }
}


                                                                                            
