package xmax.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import xmax.TranServer.ConfigTranServer;
import xmax.crs.GnrcCrs;
import xmax.crs.Amadeus.AmadeusAPICrs;
import xmax.crs.Amadeus.AmadeusAPICrsConnectionImpl;
import xmax.util.TypedProperties;


public class dialogStaticFiles extends JDialog
{
	private GnrcCrs gnrcCrs;
	  JPanel pnlBase = new JPanel();
	  JPanel pnlInput = new JPanel();
	  JButton btnOK = new JButton();
	  JButton btnCancel = new JButton();
	  JButton btnReset = new JButton();
	  JButton btnReqDir = new JButton();
	  JButton btnRespDir = new JButton();
	  Border border1;
	  JPanel pnlButtons = new JPanel();
	  GridBagLayout gridBagLayout1 = new GridBagLayout();
	  GridLayout gridLayout1 = new GridLayout();
	  public boolean isValidData = false;
	  GridBagLayout gridBagLayout2 = new GridBagLayout();
	  JLabel lblEnableStaticFiles = new JLabel();
	  JLabel lblReqDirectory = new JLabel();
	  JLabel lblRespDirectory = new JLabel();
	  JLabel lblFileIndex = new JLabel();
	  JCheckBox chkEnableStaticFiles = new JCheckBox();
	  JTextField edtReqDirectory = new JTextField();
	  JTextField edtRespDirectory = new JTextField();
	  JTextField edtFileIndex = new JTextField();

	  /** 
	   ***********************************************************************
	   * constructors
	   ***********************************************************************
	   */
	  public dialogStaticFiles(final Frame aOwner, final GnrcCrs aCrs)
	    {
	    super(aOwner, "Setup Static Files", true);
	    this.setModal(true);
	    this.gnrcCrs = aCrs;

	    try
	      {
	      jbInit();
	      setConfigValues();
	      }
	    catch (Exception e)
	      {
	      e.printStackTrace();
	      }

	    if ( isValid() == false )
	      pack();

	    dialogUtils.centerWindow(this);
	    }

	  private void setConfigValues()
	  {
		  final TypedProperties[] props = ConfigTranServer.getSignOns("hostCode", "1A");
		  if ((props != null) && (props.length > 0))
		  {
			  String sRequestDir = ConfigTranServer.application.getStringProperty("requestDir", "");
			  String sResponseDir = ConfigTranServer.application.getStringProperty("responseDir", "");
			  boolean enableStaticFiles = ConfigTranServer.application.getBooleanProperty("useTestHarness", false);
			  int fileIndex = 0;
			  
			  if (this.gnrcCrs instanceof AmadeusAPICrs)
			  {
				  final AmadeusAPICrs crs = (AmadeusAPICrs )this.gnrcCrs;
				  final AmadeusAPICrsConnectionImpl conn = (AmadeusAPICrsConnectionImpl )crs.connection;
				  
				  sRequestDir = conn.getStaticRequestDirectory();
				  sResponseDir = conn.getStaticResponseDirectory();
				  enableStaticFiles = conn.isUseStaticFile();
				  fileIndex = conn.getStaticResponseIndex();
			  }
			  
			  this.edtReqDirectory.setText(sRequestDir);
			  this.edtRespDirectory.setText(sResponseDir);
			  this.chkEnableStaticFiles.setSelected(enableStaticFiles);
			  this.edtFileIndex.setText(Integer.toString(fileIndex));
		  }
	  }
	  
	  /** 
	   ***********************************************************************
	   * used by constructors
	   ***********************************************************************
	   */
	  private void jbInit() throws Exception
	    {
	  //  border1 = BorderFactory.createRaisedBevelBorder();
	    pnlButtons.setLayout(gridLayout1);
	    pnlInput.setLayout(gridBagLayout2);
	    
	    btnOK.setText("OK");
	    btnOK.addActionListener(new java.awt.event.ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) 
	      {
	    	  closeOK(e); 
	      }
	    });
	    
	    btnReset.setText("Reset");
	    btnReset.addActionListener(new java.awt.event.ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) 
	      {
	    	  doReset(e); 
	      }
	    });
	    
	   // btnCancel.setBorder(null);
	    btnCancel.setText("Cancel");
	    btnCancel.addActionListener(new java.awt.event.ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) 
	      {
	    	  closeCancel(e); 
	      }
	    });
	    
	    
	    btnReqDir.setText("...");
	    btnReqDir.setMaximumSize(new Dimension(18, 18));
	    btnReqDir.setPreferredSize(new Dimension(18, 18));
	    btnReqDir.addActionListener(new java.awt.event.ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) 
	      {
	    	  doRequestDirectory(e); 
	      }
	    });
	    
	    
	    
	    btnRespDir.setText("...");
	    btnRespDir.addActionListener(null);
	    btnRespDir.setMaximumSize(new Dimension(18, 18));
	    btnRespDir.setPreferredSize(new Dimension(18, 18));
	    btnRespDir.addActionListener(new java.awt.event.ActionListener()
	    {
	      public void actionPerformed(ActionEvent e) 
	      {
	    	  doResponseDirectory(e); 
	      }
	    });
	    
	    gridLayout1.setHgap(4);
	    
	    pnlBase.setLayout(gridBagLayout1);
	    this.setModal(true);
	    pnlBase.setMinimumSize(new Dimension(400, 200));
	    pnlBase.setPreferredSize(new Dimension(600, 200));
	    
	    // enable Static files
	    lblEnableStaticFiles.setHorizontalAlignment(SwingConstants.RIGHT);
	    lblEnableStaticFiles.setText("Enable Static Files");
	    chkEnableStaticFiles.setLabel("");
	    chkEnableStaticFiles.setSelected(true);
	    
	    // request directory
	    lblReqDirectory.setHorizontalAlignment(SwingConstants.RIGHT);
	    lblReqDirectory.setText("Request Directory");
	    edtReqDirectory.setText("/initial/request/dir");
	    edtReqDirectory.setColumns(30);
	    
	    // response directory
	    lblRespDirectory.setHorizontalAlignment(SwingConstants.RIGHT);
	    lblRespDirectory.setText("Response Directory");
	    edtRespDirectory.setText("/initial/response/dir");
	    edtRespDirectory.setColumns(30);

	    // index
	    lblFileIndex.setHorizontalAlignment(SwingConstants.RIGHT);
	    lblFileIndex.setText("File Index");
	    edtFileIndex.setText("0");
	    edtFileIndex.setColumns(2);
	    
	    
	    pnlInput.setBorder(BorderFactory.createEtchedBorder());
	    pnlBase.add(pnlInput, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
	            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	    
	    pnlInput.add(lblEnableStaticFiles, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
	    pnlInput.add(chkEnableStaticFiles, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
	    
	    pnlInput.add(lblReqDirectory, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
	    pnlInput.add(edtReqDirectory, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
	    pnlInput.add(btnReqDir, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
	    
	    pnlInput.add(lblRespDirectory, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
	    pnlInput.add(edtRespDirectory, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
	    pnlInput.add(btnRespDir, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
	    
	    pnlInput.add(lblFileIndex, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 30, 5, 5), 0, 0));
	    pnlInput.add(edtFileIndex, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 30), 0, 0));
	    
	    
	    pnlBase.add(pnlButtons, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
	            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 8, 4, 8), 0, 0));
	    pnlButtons.add(btnReset, null);
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
		  final AmadeusAPICrsConnectionImpl connection = getAmadeusAPIConnection();
		  if (connection != null)
		  {
			  final int iIndex = Integer.parseInt(this.edtFileIndex.getText().trim());
			  
			  connection.setStaticResponseIndex(iIndex);
			  connection.setStaticRequestDirectory(this.edtReqDirectory.getText());
			  connection.setStaticResponseDirectory(this.edtRespDirectory.getText());
			  connection.setUseStaticFile(this.chkEnableStaticFiles.isSelected());
			  
			//  this.edtFileIndex.setText("0");
			//  this.edtFileIndex.invalidate();
			  
			    isValidData = true;
			    setVisible(false);
			  
		  }
		  else
		  {
		      JOptionPane.showMessageDialog(null,"Unable to set static files parameters");
		  }
	    }

	  /** 
	   ***********************************************************************
	   * Reset button
	   ***********************************************************************
	   */
	  void doReset(ActionEvent e)
	    {
		  final AmadeusAPICrsConnectionImpl connection = getAmadeusAPIConnection();
		  if (connection != null)
		  {
			  connection.setStaticResponseIndex(0);
			  this.edtFileIndex.setText("0");
			  this.edtFileIndex.invalidate();
		  }
		  else
		  {
		      JOptionPane.showMessageDialog(null,"Unable to reset response file index");
		  }
	    }

	  
	  private AmadeusAPICrsConnectionImpl getAmadeusAPIConnection()
	  {
		  if (this.gnrcCrs instanceof AmadeusAPICrs )
		  {
			  final AmadeusAPICrs amadeusCrs = (AmadeusAPICrs )this.gnrcCrs;
			  if (amadeusCrs.connection instanceof AmadeusAPICrsConnectionImpl)
			  {
				  final AmadeusAPICrsConnectionImpl connection = (AmadeusAPICrsConnectionImpl )amadeusCrs.connection;
				  return connection;
			  }
		  }
		  
		  return null;
	  }
	  
	  
	  /** 
	   ***********************************************************************
	   * Choose the request file directory 
	   ***********************************************************************
	   */
	  void doRequestDirectory(ActionEvent e)
	    {
		    final File currentDir = new File(this.edtReqDirectory.getText());
		    
		    final JFileChooser chooser = new JFileChooser();

		    chooser.setDialogTitle("Select the directory to store request files into");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    
		    if (currentDir.exists())
		    {
		    	chooser.setCurrentDirectory(currentDir);
		    }
		    
		    final int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) 
		    {
		    	this.edtReqDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
		    	this.edtReqDirectory.invalidate();
		    }
	    }
	  
	  /** 
	   ***********************************************************************
	   * Choose the response file directory 
	   ***********************************************************************
	   */
	  void doResponseDirectory(ActionEvent e)
	    {
		    final File currentDir = new File(this.edtRespDirectory.getText());
		    
		    final JFileChooser chooser = new JFileChooser();

		    chooser.setDialogTitle("Select the directory to read response files from");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    if (currentDir.exists())
		    {
		    	chooser.setCurrentDirectory(currentDir);
		    }
		    
		    
		    final int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) 
		    {
		    	this.edtRespDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
		    	this.edtRespDirectory.invalidate();
		    }
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
	    return( edtReqDirectory.getText() );
	    }


	  public int getServerPort()
	    {
	    final String sPort = edtRespDirectory.getText().trim();
	    
	    if ( sPort.length() > 0 )
	      return( Integer.parseInt(sPort) );
	    else
	      return(-1);
	    }

	  public boolean isStaticFilesEnabled()
	  {
		  return this.chkEnableStaticFiles.isSelected();
	  }
	  
	  public String getStaticFilesIndex()
	  {
		  return this.edtFileIndex.getText();
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
	    	final Frame frame = null;
	    	final GnrcCrs crs = null;
	      final dialogStaticFiles dlg = new dialogStaticFiles(frame, crs);

	      dlg.show();
	      if ( dlg.isValidData )
	        {
	    //    final String sHostIP   = dlg.getServerIP();
	    //    final int    iPort     = dlg.getServerPort();
	    //    final String sHostName = dlg.getHostName();
	    	final String sMessage = dlg.chkEnableStaticFiles.isSelected() ? "Static files enabled, index set to " + dlg.edtFileIndex.getText() : "Static files disabled";
	        JOptionPane.showMessageDialog(null,sMessage);
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





