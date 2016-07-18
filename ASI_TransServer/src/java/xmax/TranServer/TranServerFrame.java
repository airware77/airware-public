package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.dialogs.dialogConnectTranServer;
import xmax.dialogs.dialogStaticFiles;
import xmax.util.TypedProperties;
import xmax.util.Log.Logger;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Date;

//import com.borland.jbcl.model.MatrixModelEvent;
//import com.borland.jbcl.control.GridControl;
//import com.borland.jbcl.view.SizeVector;

import javax.swing.*;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.AWTEvent;
import java.awt.GridLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

/**
 ***********************************************************************
 * This is frame for the Transaction Server Console that appears when the
 * Transaction Server is first launched.
 * 
 * @author   David Fairchild
 * @version  $Revision: 15$ - $Date: 05/20/2002 4:26:41 PM$
 ***********************************************************************
 */
public class TranServerFrame extends JFrame implements Logger
{
 private final static String NAME_HEADER   = "TA Name";
 private final static String TIME_HEADER   = "TimeStamp";
 private final static String ACTION_HEADER = "Action";

  // menu stuff
  JMenuBar jMenuBar1;
  JMenu mnuFile;
  JMenuItem mnuConfig;
  JMenuItem mnuDeleteLogs;
  JMenuItem mnuClose;
  JMenuItem mnuRemoteLink;
  JMenuItem mnuRemoteUnlink;

  // layout managers
  GridLayout gridLayout1 = new GridLayout();
  GridLayout gridLayout2 = new GridLayout();

  // labels
  JLabel lblNumConnections;
  JLabel lblTotalMemory;
  JLabel lblFreeMemory;
  JLabel lblUsedMemory;
  JLabel lblRemoteLink;

  // thread stuff
  private TranListener listener;
  private Timer threadTimer;
  private Timer memoryTimer;
  LogListener RemoteLogListener;
  JPanel pnlForm = new JPanel();
  JPanel pnlLabels = new JPanel();

  JTable grdTAList = new JTable();
  private long lastClickTime = 0;
  JMenuItem mnuClient = new JMenuItem();
  JMenuItem mnuStaticFiles = new JMenuItem();
// time at last mouse click

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public TranServerFrame(final TranListener aListener)
    {
    listener = aListener;

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    // set up components
    try
      {
      jbInit();
      AppLog.addLogger(this);
      }
    catch(Exception e)
      {
      e.printStackTrace();
      }

    // set the title bar
    //final String sFormCaption = ConfigInformation.getParamValue( ConfigTranServer.FORM_CAPTION, "TranServer");
    //final String sVersion     = ConfigInformation.getParamValue( ConfigTranServer.VERSION, "Version ???");
    final String sFormCaption = ConfigTranServer.application.getProperty(
        "formCaption","Airware Transaction Server");

    final String sVersion     = ConfigTranServer.CURRENT_VERSION;
    this.setTitle( sFormCaption + " - " + sVersion );

    // draw the window
    this.pack();
    this.setSize(500,400);
    centerWindow(this);
    }

  /** 
   ***********************************************************************
   * Component initialization
   ***********************************************************************
   */
  private void jbInit() throws Exception
  {
    this.setEnabled(true);

    // add this listener so that the main window gets redrawn after showing config form
    this.addWindowListener(new java.awt.event.WindowAdapter()
      {
      public void windowActivated(WindowEvent e)
        {
        this_windowActivated(e);
        }
      });


    //  ********* MENU STUFF **************

    // File menu item
    mnuFile = new JMenu("File");

    // configuration menu item
    /*
    mnuConfig = new JMenuItem("Configuration");
    mnuConfig.addActionListener( new mnuConfigListener() );

    // link to remote service
    mnuRemoteLink = new JMenuItem("Remote Link");
    mnuRemoteLinkListener RemoteLinkListener = new mnuRemoteLinkListener(this);
    mnuRemoteLink.addActionListener( RemoteLinkListener );

    // unlink from remote service
    mnuRemoteUnlink = new JMenuItem("Remote Link");
    mnuRemoteUnlink.setText("Remote Unlink");
    mnuRemoteUnLinkListener RemoteUnLinkListener = new mnuRemoteUnLinkListener(this);
    mnuRemoteUnlink.addActionListener( RemoteUnLinkListener );

    // delete old logs menu item
    mnuDeleteLogs = new JMenuItem("Delete Old Logs");
    mnuDeleteLogs.addActionListener( new mnuDeleteLogsListener() );
    */

    // close program menu item
    mnuClose = new JMenuItem("Stop Transaction Server");
    mnuClose.addActionListener( new mnuCloseListener() );



    // create menu
    jMenuBar1 = new JMenuBar();
    jMenuBar1.setOpaque(false);

    mnuClient.setText("Launch Test Client");
    mnuClient.addActionListener(new TranServerFrame_mnuClient_actionAdapter(this));

    mnuStaticFiles.setText("Static Files");
    mnuStaticFiles.addActionListener(new TranServerFrame_mnuStaticFiles_actionAdapter(this));
    
    jMenuBar1.add(mnuFile);
    // mnuFile.add(mnuConfig);
    mnuFile.add(mnuClient);
    mnuFile.add(mnuStaticFiles);
    // mnuFile.addSeparator();
    // mnuFile.add(mnuRemoteLink);
    // mnuFile.add(mnuRemoteUnlink);
    // mnuFile.add(mnuDeleteLogs);
    // mnuFile.addSeparator();
    mnuFile.add(mnuClose);

    this.setJMenuBar(jMenuBar1);


    // ***************** OLD GRID STUFF *******************
    /*
    grdTAList.setAutoEdit(false);
    grdTAList.setAutoInsert(false);
    grdTAList.setEditInPlace(false);
    grdTAList.setRowHeaderVisible(false);
    grdTAList.setSortOnHeaderClick(true);
    grdTAList.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        grdTAList_mouseReleased(e);
      }
    });


    grdTAList.setColumnCaptions(new String[] {NAME_HEADER, TIME_HEADER, ACTION_HEADER});

    grdTAList.removeAllRows();
    {
    final String[] sTermList = getLocalTAList();
    if ( sTermList instanceof String[] )
      {
      for ( int i = 0; i < sTermList.length; i++ )
        addTaRow(sTermList[i]);
      }
    }

    grdTAList.setSelectRow(true);
    // set column sizes to 100, 130, and 250
    final SizeVector colsizes = grdTAList.getColumnSizes();
    colsizes.setSize(0,100);
    colsizes.setSize(1,130);
    colsizes.setSize(2,250);
    grdTAList.setColumnSizes( colsizes );


    grdTAList.addActionListener(new
    java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        grdTAList_actionPerformed(e);
      }
    });
    */

    // new grid stuff
    final TableModel tm = grdTAList.getModel();
    if ( tm instanceof DefaultTableModel )
      {
      final DefaultTableModel dm = (DefaultTableModel )tm;

      dm.addColumn(NAME_HEADER);
      dm.addColumn(TIME_HEADER);
      dm.addColumn(ACTION_HEADER);

      dm.setNumRows(0);
      }

    grdTAList.getColumn(NAME_HEADER).setPreferredWidth(100);
    grdTAList.getColumn(TIME_HEADER).setPreferredWidth(130);
    grdTAList.getColumn(ACTION_HEADER).setPreferredWidth(250);

    grdTAList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    grdTAList.setRowSelectionAllowed(true);
    grdTAList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    grdTAList.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        grdTAList_mouseReleased(e);
      }
    });

   // grdTAList.removeAllRows();
    {
    final String[] sTermList = getLocalTAList();
    if ( sTermList instanceof String[] )
      {
      for ( int i = 0; i < sTermList.length; i++ )
        addTaRow(sTermList[i]);
      }
    }

    // set the layout to accept three panels
    gridLayout1.setColumns(1);
    gridLayout1.setRows(2);
    gridLayout2.setColumns(1);
    gridLayout2.setRows(5);

    pnlForm.setLayout(gridLayout1);
    pnlLabels.setLayout(gridLayout2);

    this.getContentPane().add(pnlForm, null);

    pnlForm.add(grdTAList, null);
    pnlForm.add(pnlLabels, null);

    // Labels
    lblNumConnections = new JLabel("Number of Connections:");
    lblTotalMemory    = new JLabel("Total Memory: ");
    lblUsedMemory     = new JLabel("Used Memory: ");
    lblFreeMemory     = new JLabel("Free Memory: ");
    lblRemoteLink     = new JLabel("Remote Link: ");

    lblRemoteLink.setHorizontalAlignment(SwingConstants.LEFT);
    lblNumConnections.setHorizontalAlignment(SwingConstants.LEFT);
    lblUsedMemory.setHorizontalAlignment(SwingConstants.LEFT);
    lblFreeMemory.setHorizontalAlignment(SwingConstants.LEFT);
    lblTotalMemory.setHorizontalAlignment(SwingConstants.LEFT);

    pnlLabels.add(lblNumConnections, null);
    pnlLabels.add(lblUsedMemory, null);
    pnlLabels.add(lblFreeMemory, null);
    pnlLabels.add(lblTotalMemory, null);
    pnlLabels.add(lblRemoteLink, null);

    {
    final int THREAD_CHECK_TIMEOUT = 2 * 1000;    // check thread counts every 2 seconds
    final CountThreads threadCounter = new CountThreads(this,listener);
    threadTimer = new Timer(THREAD_CHECK_TIMEOUT,threadCounter);
    threadTimer.start();
    }

    {
    final int MEM_CHECK_TIMEOUT     = 10 * 1000;   // check mem usage every 10 seconds (also runs garbage collection)
    final int MEM_CHECK_MAX_PERCENT = 5;           // log any memory jumps larger than this percentage
    final CountMemUsage listener = new CountMemUsage(this,MEM_CHECK_MAX_PERCENT);
    memoryTimer = new Timer(MEM_CHECK_TIMEOUT,listener);
    memoryTimer.start();
    }

  }

  /** 
   ***********************************************************************
   * Center the frame
   ***********************************************************************
   */
  public static void centerWindow(final Window wndw)
    {
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension frameSize  = wndw.getSize();

    if ( frameSize.height > screenSize.height )
      frameSize.height = screenSize.height;
    if ( frameSize.width > screenSize.width )
      frameSize.width = screenSize.width;

    wndw.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    wndw.setVisible(true);
    }

  /** 
   ***********************************************************************
   * This procedure is used to redraw the main window after the
   * configuration window closes.  Otherwise, the main windows' menu
   * gets screwed up
   ***********************************************************************
   */
  void this_windowActivated(WindowEvent e)
    {
     final Dimension frameSize = getSize();
     setSize(frameSize.width + 1,frameSize.height + 1);
     centerWindow(this);
    }

  /** 
   ***********************************************************************
   * Overridden so we can exit on system close
   ***********************************************************************
   */
  protected void processWindowEvent(WindowEvent e)
    {
    if ( e.getID() == WindowEvent.WINDOW_CLOSING )
      {
      AppLog.LogWarning(
          "Transaction Server console closed by user - " +
          "Transaction Server is stopping");
      System.exit(0);
      }
    }

  void grdTAList_actionPerformed(ActionEvent e)
  {

  }



  /**
   ***********************************************************************
   * This procedure is called whenever a logging event happens
   ***********************************************************************
   */
  public void handleLogEvent(final LoggingEvent aLogEvent)
    {
    // make sure the TA is listed on the grid
    if ( aLogEvent.getMessageType() != aLogEvent.INFORMATION )
      return;

    // find the TA name and action columns
    final String sConvName = aLogEvent.getConvName();
    final int iNameRow = getTaNameRowIndex( sConvName );
    if ( iNameRow >= 0 )
      {
      final int iActionColumn = getColumnIndex(ACTION_HEADER);
      if ( iActionColumn >= 0 )
        grdTAList.setValueAt(aLogEvent.getMessageText(),iNameRow,iActionColumn);

      final int iTimeColumn = getColumnIndex(TIME_HEADER);
      if ( iTimeColumn >= 0 )
        {
        final SimpleDateFormat dt_tm_format = new SimpleDateFormat("EEE  MMM dd   h:mm a");
        final String sTimeData = dt_tm_format.format( new Date(aLogEvent.getTimeStamp()) ).toString();

        grdTAList.setValueAt(sTimeData,iNameRow,iTimeColumn);
        }

      }

    }

  /** 
   ***********************************************************************
   * This procedure returns the index of the row for the given TA
   ***********************************************************************
   */
  private int getTaNameRowIndex(final String aTaName)
    {
    // find the name column
    final int iNameColumn = getColumnIndex(NAME_HEADER);

    // find the row that matches the TA name
    if ( iNameColumn >= 0 )
      {
      Object CellObject;
      String sCellValue;
      for ( int iRowNum = 0; iRowNum < grdTAList.getRowCount(); iRowNum++ )
        {
        CellObject = grdTAList.getValueAt(iRowNum,iNameColumn);
        if ( CellObject instanceof String )
          {
          sCellValue = ((String )CellObject).trim();
          if ( sCellValue.equals(aTaName) )
            return(iRowNum);
          }
        }
      }

    return(-1);
    }

  /** 
   ***********************************************************************
   * This procedure returns the name of the TA in the given row
   ***********************************************************************
   */
  private String getTaNameFromRow(final int aRowNum)
    {
    // find the name column
    final int iNameColumn = getColumnIndex(NAME_HEADER);

    // find the row that matches the TA name
    if ( iNameColumn >= 0 )
      {
      final Object CellObject = grdTAList.getValueAt(aRowNum,iNameColumn);
      if ( CellObject instanceof String )
        {
        final String sCellValue = (String )CellObject;
        return(sCellValue);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * This procedure returns the index of the given column header
   * -1 means not found
   ***********************************************************************
   */
  private int getColumnIndex(final String aCaption)
    {
    for ( int iCol = 0; iCol < grdTAList.getColumnCount(); iCol++ )
      {
      if ( grdTAList.getColumnName(iCol).equals(aCaption) )
        return(iCol);
      }

    return(-1);
    }

  /**
   ***********************************************************************
   * This procedure adds a new row for a TA at the bottom of the grid
   ***********************************************************************
   */
  private void addTaRow(final String aTaName)
    {
    final TableModel dm = grdTAList.getModel();
    if ( dm instanceof DefaultTableModel )
      ((DefaultTableModel )dm).addRow(new Vector());


    // find the name column
    final int iNameColumn = getColumnIndex(NAME_HEADER);
    final int iNameRow    = grdTAList.getRowCount() - 1;

    grdTAList.setValueAt(aTaName,iNameRow,iNameColumn);
    }

  /** 
   ***********************************************************************
   * This procedure returns a list of TA names defined in the config file
   ***********************************************************************
   */
  private String[] getLocalTAList()
    {
    final Vector TaList = new Vector();
    final DecimalFormat numFormat = new DecimalFormat("00");

    // add all the sign on parameter TA Names from the configuration file
//    String sParamName;
//    String sParamValue;
    String sHostCode;
    String sTaName;
//    StringTokenizer fields;
    TypedProperties[] signOns = ConfigTranServer.signOnList;

    for (int i=0; i < signOns.length; i++)
      {
      sHostCode = signOns[i].getProperty("hostCode");
      sTaName   = signOns[i].getProperty("taName");
      if (GnrcFormat.NotNull(sTaName))
        TaList.add(sTaName);
      }
//    for ( int i = 1; true; i++ )
//      {
//      sParamName  = "Sign On " + numFormat.format(i);
//      sParamValue = ConfigInformation.getParamValue( sParamName, "");
//      if ( GnrcFormat.NotNull(sParamValue) )
//        {
//        fields = new StringTokenizer(sParamValue,",");
//        if ( fields.countTokens() >= 2 )
//          {
//          sHostCode = fields.nextToken().trim();
//          sTaName   = fields.nextToken().trim();
//          if ( GnrcFormat.NotNull(sTaName) )
//            TaList.add(sTaName);
//          }
//        else
//          break;
//        }
//      else
//        break;
//      }

    // convert vector to an array of strings
    if ( TaList.size() > 0 )
      {
      final String[] sList = new String[ TaList.size() ];
      TaList.toArray(sList);
      return(sList);
      }
    else
      return(null);
    }

  /** 
   ***********************************************************************
   * This section handles double clicking on the grid
   ***********************************************************************
   */
  void grdTAList_mouseReleased(MouseEvent e)
    {
    final long MAX_CLICK_TIME = 300;     // allow up to 300 milliseconds between clicks

    // figure the time interval from the previous click
    final long thisClickTime = e.getWhen();
    final long timeDiff      = Math.abs(thisClickTime - lastClickTime);

    // check if this is a double click
    if ( timeDiff < MAX_CLICK_TIME )
      {
       final int iSelectedRow = grdTAList.getSelectedRow();
       final String sTAName   = getTaNameFromRow(iSelectedRow);

       final WatchTaFrame ta_watch = new WatchTaFrame("Local TA Watch","TranServer Client",sTAName);
       centerWindow(ta_watch);
      }

    // save the click time
    lastClickTime = thisClickTime;
    }

  void mnuRemoteLink_actionPerformed(ActionEvent e)
  {
  //
  }

  void mnuRemoteUnlink_actionPerformed(ActionEvent e)
  {
  //
  }

  void mnuClient_actionPerformed(ActionEvent e)
    {
    final JFrame frame = PanelGdsClient.getGdsClientFrame("GDS Client");
    frame.show();
    }

  /** 
   ***********************************************************************
   * Show the static files configuration dialog
   ***********************************************************************
   */
  void mnuStaticFiles_actionPerformed(ActionEvent e)
  {
	  // get the latest tranclientConnection GnrcCrsHost
	  if (this.listener == null)
      {
          JOptionPane.showMessageDialog(this, "listener is null");
          return;
	  }
	  if (this.listener.getTranClientConnection() == null)
      {
          JOptionPane.showMessageDialog(this, "No Client connections have been made");
          return;
	  }
	  if (this.listener.getTranClientConnection().getCrsHost() == null)
      {
          JOptionPane.showMessageDialog(this, "Client connection does not have crs connection parameters set");
          return;
	  }
	  
	  final GnrcCrs gnrcHost = this.listener.getTranClientConnection().getCrsHost();
	  
    final dialogStaticFiles dlg = new dialogStaticFiles( new Frame("Static Files"), gnrcHost );
     
    try
      {
      dlg.show();
      if ( dlg.isValidData )
        {
       // final String sIPAddress = dlg.getServerIP();
      //  final int iPort         = dlg.getServerPort();

        // CrsCode = dlg.getHostCode();
     //   final String sCrsName = dlg.getHostName();
      //  final int iHostType   = dlg.getHostType();

        this.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
        try
          {
	      final String sMessage = dlg.isStaticFilesEnabled() ? "Static files enabled, index set to " + dlg.getStaticFilesIndex() : "Static files disabled";
          JOptionPane.showMessageDialog(this,sMessage);
          }
        catch (Exception ex)
          {
          JOptionPane.showMessageDialog(this,ex.toString());
          }
        finally
          {
          this.setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR) );
          }

        }
      }
    finally
      {
      dlg.dispose();
      }
    
  }
  
}

/** 
 ***********************************************************************
 * Timer event listener
 ***********************************************************************
 */
class CountMemUsage implements ActionListener
  {
  private TranServerFrame owner;
  private static long iLastFreePercent;
  private static long iMaxPercent;

  public CountMemUsage(final TranServerFrame aOwner, final long aMaxPercent)
    {
    owner = aOwner;
    iMaxPercent = aMaxPercent;
    }

  public void actionPerformed(final ActionEvent e)
    {

    try
      {
      final Runtime rt = Runtime.getRuntime();
      if ( rt instanceof Runtime )
        {
        rt.gc();

        final long FreeMem  = rt.freeMemory();
        final long TotalMem = rt.totalMemory();
        final long UsedMem  = TotalMem - FreeMem;
        final long iFreePercent = (FreeMem * 100)/TotalMem;
        final long iUsedPercent = (UsedMem * 100)/TotalMem;

        final DecimalFormat MemFormat = new DecimalFormat("###,###,###,###");

        if ( owner.lblTotalMemory instanceof JLabel )
          owner.lblTotalMemory.setText("Total Memory:   " + MemFormat.format(TotalMem) );
        if ( owner.lblUsedMemory instanceof JLabel )
          owner.lblUsedMemory.setText( " Used Memory:   " + MemFormat.format(UsedMem) + "   "  + iUsedPercent + "%");
        if ( owner.lblFreeMemory instanceof JLabel )
          owner.lblFreeMemory.setText( " Free Memory:   " + MemFormat.format(FreeMem) + "   "  + iFreePercent + "%");

        if ( Math.abs(iFreePercent - iLastFreePercent) > iMaxPercent )
          {
          AppLog.LogInfo("Amount of free memory changed from " + iLastFreePercent + "% to " + iFreePercent + "%");
          iLastFreePercent = iFreePercent;
          }
        }

      }
    catch (Exception ex)
      {}

    }

  }

/**
 ***********************************************************************
 * Listener that counts the number of threads running
 * and logs it if there is a difference
 ***********************************************************************
 */
  class CountThreads implements ActionListener
  {
  private TranServerFrame owner;
  private TranListener listener;
  private int iNumThreads;

  public CountThreads(final TranServerFrame aOwner, final TranListener aListener)
    {
    owner    = aOwner;
    listener = aListener;
    iNumThreads = -1;
    }

  public void actionPerformed(final ActionEvent e)
    {

    try
      {
      if ( listener instanceof TranListener )
        {
        final int iThisCount = TranClientConnection.getNumClients();
        if ( iNumThreads != iThisCount )
          {
          iNumThreads = iThisCount;
          AppLog.LogInfo("There are currently " + iNumThreads + " TranClientListener threads running");
          if ( owner instanceof TranServerFrame )
            owner.lblNumConnections.setText("Thread Count: " + iNumThreads );
          }
        }
      }
    catch (Exception ex)
      {}
    }

  }

/**
 ***********************************************************************
 * This class is used when the config menu item is used
 ***********************************************************************
 */
 /*
  class mnuConfigListener implements ActionListener
{
 public void actionPerformed(final ActionEvent e)
   {
//   final File ConfigFile = ConfigInformation.getFileParamValue(
//       ConfigTranServer.CONFIG_FILENAME, new File("config.txt") );

   final File ConfigFile = new File(ConfigTranServer.configFile);

   ConfigTranServerDlg cfgPage = new ConfigTranServerDlg(
       null,"Configuration Setup - " + ConfigFile.getAbsolutePath(),true);

   TranServerFrame.centerWindow(cfgPage);
   }
}
*/

/**
 ***********************************************************************
 * This class is used when the delete logs menu item is used
 ***********************************************************************
 */
class mnuDeleteLogsListener implements ActionListener
{
 public void actionPerformed(final ActionEvent e)
   {
   final int iResult = JOptionPane.showConfirmDialog(null, "This procedure has been temporarily disabled","Delete old log files", JOptionPane.YES_NO_OPTION);

   //final int iResult = JOptionPane.showConfirmDialog(null, "This procedure may take a few minutes.  Continue?", "Delete old log files", JOptionPane.YES_NO_OPTION);
   //if ( iResult == JOptionPane.YES_OPTION )
     //logging.MaintainLogFiles();
  }
}

/** 
 ***********************************************************************
 * This class is used when the remote link menu item is used
 ***********************************************************************
 */
class mnuRemoteLinkListener implements ActionListener
{
 private TranServerFrame owner;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public mnuRemoteLinkListener(final TranServerFrame aOwner)
   {
   owner = aOwner;
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void actionPerformed(final ActionEvent e)
   {
   // prompt user for values
   final String sHostName = JOptionPane.showInputDialog(null,"Host name","Remote TA Watch",JOptionPane.QUESTION_MESSAGE);
   if ( GnrcFormat.IsNull(sHostName) )
     return;

   final String sPort = JOptionPane.showInputDialog(null,"Port Number","Remote TA Watch",JOptionPane.QUESTION_MESSAGE);
   if ( GnrcFormat.IsNull(sPort) )
     return;

   // start thread that listens for log events
   try
     {
     final int iPort = Integer.parseInt(sPort);
     owner.RemoteLogListener = new LogListener(sHostName,iPort,null);
     final Thread lThread = new Thread(owner.RemoteLogListener);

     lThread.start();

     owner.lblRemoteLink.setText("Remote Link:    " + sHostName + " port " + sPort);
     }
   catch (Exception excp)
     {
     System.out.println( excp.toString() );
     }

   }
}

/** 
 ***********************************************************************
 * This class is used when the remote unlink menu item is used
 ***********************************************************************
 */
class mnuRemoteUnLinkListener implements ActionListener
{
 private TranServerFrame owner;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public mnuRemoteUnLinkListener(final TranServerFrame aOwner)
   {
   owner = aOwner;
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void actionPerformed(final ActionEvent e)
   {
   if ( owner instanceof TranServerFrame )
     {
     if ( owner.RemoteLogListener instanceof LogListener )
       owner.RemoteLogListener.pleaseStop();

     owner.lblRemoteLink.setText("Remote Link:    Disabled");
     }
   }
}

/** 
 ***********************************************************************
 * This class is used when the close menu item is used
 ***********************************************************************
 */
class mnuCloseListener implements ActionListener
{
 public void actionPerformed(final ActionEvent e)
   {
   AppLog.LogWarning("Transaction Server stopped by user");
   System.exit(0);
   }
}

class TranServerFrame_mnuClient_actionAdapter implements ActionListener
{
  TranServerFrame adaptee;

  TranServerFrame_mnuClient_actionAdapter(TranServerFrame adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.mnuClient_actionPerformed(e);
  }
}

class TranServerFrame_mnuStaticFiles_actionAdapter implements ActionListener
{
  TranServerFrame adaptee;

  TranServerFrame_mnuStaticFiles_actionAdapter(TranServerFrame adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.mnuStaticFiles_actionPerformed(e);
  }
}




