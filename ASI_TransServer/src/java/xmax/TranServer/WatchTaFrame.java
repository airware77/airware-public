
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.TranServer;

import java.awt.*;
import javax.swing.*;
//import com.borland.jbcl.layout.*;
//import com.borland.jbcl.control.*;
import java.awt.event.*;
import java.io.File;
import xmax.util.Log.*;
import xmax.TranServer.LogListener;

public class WatchTaFrame extends JFrame
{
 String sTAName;
 private ScreenLogger LeftScreenLogger;
 private ScreenLogger RightScreenLogger;
 private ScreenLogger BottomScreenLogger;
 private LogListener listenThread;
 FileLogger filelogger;
  JSplitPane jSplitPane1 = new JSplitPane();
  JPanel pnlTop = new JPanel();
  JPanel pnlBottom = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane scrollBottom = new JScrollPane();
  JTextArea memBottom = new JTextArea();
  JLabel lblBottomCaption = new JLabel();
  JSplitPane jSplitPane2 = new JSplitPane();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel pnlLeft = new JPanel();
  JPanel pnlRight = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  JScrollPane scrollLeft = new JScrollPane();
  JScrollPane ScrollRight = new JScrollPane();
  JTextArea memLeft = new JTextArea();
  JTextArea memRight = new JTextArea();
  JLabel lblLeft = new JLabel();
  JLabel lblRight = new JLabel();

  JPopupMenu jPopupMenuLeft   = new JPopupMenu();
  JPopupMenu jPopupMenuRight  = new JPopupMenu();
  JPopupMenu jPopupMenuBottom = new JPopupMenu();

  JCheckBoxMenuItem mnuShowInfo     = new JCheckBoxMenuItem();
  JCheckBoxMenuItem mnuShowWarnings = new JCheckBoxMenuItem();
  JCheckBoxMenuItem mnuShowErrors   = new JCheckBoxMenuItem();
  JCheckBoxMenuItem mnuShowDebug    = new JCheckBoxMenuItem();

  JMenuItem mnuClearBottom            = new JMenuItem();
  JCheckBoxMenuItem mnuWordWrapBottom = new JCheckBoxMenuItem();
  JMenuItem mnuClearLeft              = new JMenuItem();
  JCheckBoxMenuItem mnuWordWrapLeft   = new JCheckBoxMenuItem();
  JMenuItem mnuClearRight             = new JMenuItem();
  JCheckBoxMenuItem mnuWordWrapRight  = new JCheckBoxMenuItem();

  JMenuItem mnuStartFileLogLeft   = new JMenuItem();
  JMenuItem mnuStopFileLogLeft    = new JMenuItem();
  JMenuItem mnuStartFileLogRight  = new JMenuItem();
  JMenuItem mnuStopFileLogRight   = new JMenuItem();
  JMenuItem mnuStartFileLogBottom = new JMenuItem();
  JMenuItem mnuStopFileLogBottom  = new JMenuItem();

  BorderLayout borderLayout5 = new BorderLayout();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public WatchTaFrame(final String aFormCaption, final String aClientWindowCaption, final String aTAName)
    {
    try
      {
      sTAName = aTAName;
      jbInit();

      pack();

      lblLeft.setText(aClientWindowCaption);
      lblRight.setText(aTAName);
      this.setTitle(aFormCaption);

      m_EnableLogging();
      }
    catch(Exception e)
      {
      e.printStackTrace();
      }
    }

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public WatchTaFrame(final String aFormCaption, final String aClientWindowCaption, final String aTAName, final LogListener aListener)
    {
    try
      {
      sTAName = aTAName;
      listenThread = aListener;
      jbInit();

      pack();

      lblLeft.setText(aClientWindowCaption);
      lblRight.setText(aTAName);
      this.setTitle(aFormCaption);

      m_EnableLogging();
      }
    catch(Exception e)
      {
      e.printStackTrace();
      }
    }

  /** 
   ***********************************************************************
   * Initialize visual components
   ***********************************************************************
   */
  private void jbInit() throws Exception
  {
    // form dimensions
    final int WIDTH  = 600;
    final int HEIGHT = 400;

    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    jSplitPane1.setDividerLocation(300);
    jSplitPane1.setLastDividerLocation(300);
    jSplitPane1.setLeftComponent(null);
    jSplitPane1.setRightComponent(null);
    pnlBottom.setLayout(borderLayout1);
    lblBottomCaption.setFont(new java.awt.Font("Dialog", 1, 12));
    lblBottomCaption.setHorizontalAlignment(SwingConstants.CENTER);
    lblBottomCaption.setHorizontalTextPosition(SwingConstants.LEFT);
    lblBottomCaption.setText("Messages");
    pnlTop.setLayout(borderLayout2);
    pnlLeft.setLayout(borderLayout3);
    pnlRight.setLayout(borderLayout4);
    lblLeft.setFont(new java.awt.Font("Dialog", 1, 12));
    lblLeft.setAlignmentY((float) 0.0);
    lblLeft.setHorizontalAlignment(SwingConstants.CENTER);
    lblLeft.setText("Client");
    lblRight.setFont(new java.awt.Font("Dialog", 1, 12));
    lblRight.setHorizontalAlignment(SwingConstants.CENTER);
    lblRight.setText("Host");

    // menu item for opening file log
    mnuStartFileLogLeft.setText("Start file logging");
    mnuStartFileLogLeft.addActionListener( new mnuStartFileLogListener(this) );
    mnuStartFileLogRight.setText("Start file logging");
    mnuStartFileLogRight.addActionListener( new mnuStartFileLogListener(this) );
    mnuStartFileLogBottom.setText("Start file logging");
    mnuStartFileLogBottom.addActionListener( new mnuStartFileLogListener(this) );


    // menu item for closing file log
    mnuStopFileLogLeft.setText("Stop file logging");
    mnuStopFileLogLeft.addActionListener( new mnuStopFileLogListener(this) );
    mnuStopFileLogRight.setText("Stop file logging");
    mnuStopFileLogRight.addActionListener( new mnuStopFileLogListener(this) );
    mnuStopFileLogBottom.setText("Stop file logging");
    mnuStopFileLogBottom.addActionListener( new mnuStopFileLogListener(this) );


    // menu item for show status messages
    mnuShowInfo.setSelected(true);
    mnuShowInfo.setText("Show Status Messages");
    mnuShowInfo.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
      m_SetScreenLogTypes();
      }
    });
    // menu item for show warnings
    mnuShowWarnings.setSelected(true);
    mnuShowWarnings.setText("Display Warnings");
    mnuShowWarnings.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      m_SetScreenLogTypes();
      }
    });
    // menu item for show errors
    mnuShowErrors.setSelected(true);
    mnuShowErrors.setText("Display Errors");
    mnuShowErrors.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      m_SetScreenLogTypes();
      }
    });
    // menu item for show debug
    mnuShowDebug.setText("Display Debug");
    mnuShowDebug.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      m_SetScreenLogTypes();
      }
    });
    // menu item to clear bottom memo field
    mnuClearBottom.setText("Clear errors");
    mnuClearBottom.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      memBottom.setText("");
      }
    });
    // menu item for setting word wrap on bottom memo field
    mnuWordWrapBottom.setText("Word Wrap");
    mnuWordWrapBottom.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
      memBottom.setLineWrap( !memBottom.getLineWrap() );
      }
    });
    // menu item to clear left memo field
    mnuClearLeft.setText("Clear Client Messages");
    mnuClearLeft.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      memLeft.setText("");
      }
    });
    // menu item for setting word wrap on left memo field
    mnuWordWrapLeft.setText("Word Wrap");
    mnuWordWrapLeft.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      memLeft.setLineWrap( !memLeft.getLineWrap() );
      }
    });
    // menu item to clear right memo field
    mnuClearRight.setText("Clear Host Messages");
    mnuClearRight.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      memRight.setText("");
      }
    });
    // menu item for setting word wrap on right memo field
    mnuWordWrapRight.setText("Word Wrap");
    mnuWordWrapRight.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
      memRight.setLineWrap( !memRight.getLineWrap() );
      }
    });


    memLeft.setEditable(false);
    memLeft.addMouseListener(new java.awt.event.MouseAdapter()
    {

      public void mousePressed(MouseEvent e)
      {
        memLeft_mousePressed(e);
      }

      public void mouseReleased(MouseEvent e)
      {
        memLeft_mouseReleased(e);
      }
    });
    memRight.setEditable(false);
    memRight.addMouseListener(new java.awt.event.MouseAdapter()
    {

      public void mousePressed(MouseEvent e)
      {
        memRight_mousePressed(e);
      }

      public void mouseReleased(MouseEvent e)
      {
        memRight_mouseReleased(e);
      }
    });
    memBottom.setPreferredSize(new Dimension(24, 24));
    memBottom.setMinimumSize(new Dimension(24, 24));
    memBottom.setEditable(false);
    memBottom.addMouseListener(new java.awt.event.MouseAdapter()
    {

      public void mousePressed(MouseEvent e)
      {
        memBottom_mousePressed(e);
      }

      public void mouseReleased(MouseEvent e)
      {
        memBottom_mouseReleased(e);
      }
    });

    jSplitPane2.setPreferredSize(new Dimension(266, 51));
    jSplitPane2.setLastDividerLocation(250);


    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setTitle("Host Watch");
    this.addWindowListener(new java.awt.event.WindowAdapter()
    {

      public void windowClosed(WindowEvent e)
      {
        this_windowClosed(e);
      }
    });
    this.getContentPane().setLayout(borderLayout5);
    pnlTop.setPreferredSize(new Dimension(266, 51));
    pnlBottom.setMinimumSize(new Dimension(10, 46));
    pnlBottom.setPreferredSize(new Dimension(WIDTH, HEIGHT/4));
    pnlLeft.setPreferredSize(new Dimension(WIDTH/2, (HEIGHT * 3)/4));
    pnlRight.setPreferredSize(new Dimension(WIDTH/2, (HEIGHT * 3)/4));
    scrollBottom.setPreferredSize(new Dimension(24, 24));
    scrollLeft.setPreferredSize(new Dimension(24, 24));
    ScrollRight.setPreferredSize(new Dimension(0, 0));

    jPopupMenuBottom.setInvoker(memBottom);
    jPopupMenuLeft.setInvoker(memLeft);
    jPopupMenuRight.setInvoker(memRight);

    jPopupMenuBottom.add(mnuWordWrapBottom);
    jPopupMenuBottom.add(mnuClearBottom);
    jPopupMenuBottom.addSeparator();
    jPopupMenuBottom.add(mnuShowInfo);
    jPopupMenuBottom.add(mnuShowWarnings);
    jPopupMenuBottom.add(mnuShowErrors);
    jPopupMenuBottom.add(mnuShowDebug);
    jPopupMenuBottom.addSeparator();
    jPopupMenuBottom.add(mnuStartFileLogBottom);
    jPopupMenuBottom.add(mnuStopFileLogBottom);

    jPopupMenuLeft.add(mnuWordWrapLeft);
    jPopupMenuLeft.add(mnuClearLeft);
    jPopupMenuLeft.addSeparator();
    jPopupMenuLeft.add(mnuStartFileLogLeft);
    jPopupMenuLeft.add(mnuStopFileLogLeft);

    jPopupMenuRight.add(mnuWordWrapRight);
    jPopupMenuRight.add(mnuClearRight);
    jPopupMenuRight.addSeparator();
    jPopupMenuRight.add(mnuStartFileLogRight);
    jPopupMenuRight.add(mnuStopFileLogRight);
    this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(pnlTop, JSplitPane.TOP);
    pnlTop.add(jSplitPane2, BorderLayout.CENTER);
    jSplitPane2.add(pnlLeft, JSplitPane.LEFT);
    pnlLeft.add(scrollLeft, BorderLayout.CENTER);
    pnlLeft.add(lblLeft, BorderLayout.NORTH);
    jSplitPane2.add(pnlRight, JSplitPane.RIGHT);
    pnlRight.add(ScrollRight, BorderLayout.CENTER);
    pnlRight.add(lblRight, BorderLayout.NORTH);
    jSplitPane1.add(pnlBottom, JSplitPane.BOTTOM);
    pnlBottom.add(scrollBottom, BorderLayout.CENTER);
    pnlBottom.add(lblBottomCaption, BorderLayout.NORTH);
    scrollBottom.getViewport().add(memBottom, null);
    ScrollRight.getViewport().add(memRight, null);
    scrollLeft.getViewport().add(memLeft, null);

    jSplitPane1.setDividerLocation(200);
    jSplitPane2.setDividerLocation(250);
  }

  /** 
   ***********************************************************************
   * Mouse press events for the three memo areas
   * this fires the pop-up menu
   ***********************************************************************
   */
  void memBottom_mousePressed(MouseEvent e)
    {
    if ( memBottom.getLineWrap() )
      mnuWordWrapBottom.setSelected(true);
    else
      mnuWordWrapBottom.setSelected(false);

    // set the message options
    if ( BottomScreenLogger instanceof ScreenLogger )
      {
      if ( LoggingEvent.isVisibleMessageType(LoggingEvent.DEBUG,BottomScreenLogger.MessagesToWatch) )
        mnuShowDebug.setState(true);
      else
        mnuShowDebug.setState(false);

      if ( LoggingEvent.isVisibleMessageType(LoggingEvent.INFORMATION,BottomScreenLogger.MessagesToWatch) )
        mnuShowInfo.setState(true);
      else
        mnuShowInfo.setState(false);

      if ( LoggingEvent.isVisibleMessageType(LoggingEvent.WARNING,BottomScreenLogger.MessagesToWatch) )
        mnuShowWarnings.setState(true);
      else
        mnuShowWarnings.setState(false);

      if ( LoggingEvent.isVisibleMessageType(LoggingEvent.ERROR,BottomScreenLogger.MessagesToWatch) )
        mnuShowErrors.setState(true);
      else
        mnuShowErrors.setState(false);
      }


    jPopupMenuBottom.show((Component)e.getSource(),e.getX(),e.getY());
    }

  void memBottom_mouseReleased(MouseEvent e)
    {
    memBottom_mousePressed(e);
    }

  void memRight_mousePressed(MouseEvent e)
    {
    if ( memRight.getLineWrap() )
      mnuWordWrapRight.setSelected(true);
    else
      mnuWordWrapRight.setSelected(false);

    jPopupMenuRight.show((Component)e.getSource(),e.getX(),e.getY());
    }

  void memRight_mouseReleased(MouseEvent e)
    {
    memRight_mousePressed(e);
    }

  void memLeft_mousePressed(MouseEvent e)
    {
    if ( memLeft.getLineWrap() )
      mnuWordWrapLeft.setSelected(true);
    else
      mnuWordWrapLeft.setSelected(false);

    jPopupMenuLeft.show((Component)e.getSource(),e.getX(),e.getY());
    }

  void memLeft_mouseReleased(MouseEvent e)
    {
    memLeft_mousePressed(e);
    }

  /** 
   ***********************************************************************
   * Enable logging to the screen
   ***********************************************************************
   */
  private void m_EnableLogging()
    {
    final int[] client_messages  = {LoggingEvent.CLIENT_TO_APP,LoggingEvent.APP_TO_CLIENT};
    LeftScreenLogger = new ScreenLogger(memLeft,false,client_messages,sTAName);
    AppLog.addLogger(LeftScreenLogger);

    final int[] host_messages    = {LoggingEvent.APP_TO_HOST,LoggingEvent.HOST_TO_APP};
    RightScreenLogger = new ScreenLogger(memRight,false,host_messages,sTAName);
    AppLog.addLogger(RightScreenLogger);

    final int[] warning_messages = {LoggingEvent.WARNING,LoggingEvent.ERROR,LoggingEvent.DEBUG,LoggingEvent.INFORMATION};
    BottomScreenLogger = new ScreenLogger(memBottom,false,warning_messages,sTAName);
    AppLog.addLogger(BottomScreenLogger);
    }

 /** 
  ***********************************************************************
  * Enable/Disable the various warning events
  ***********************************************************************
  */
  void m_SetScreenLogTypes()
  {
  if ( BottomScreenLogger instanceof ScreenLogger )
    {
    int[] AllowedMessageTypes = {};

    // toggle show information
    if ( mnuShowInfo.getState() )
      AllowedMessageTypes = LoggingEvent.addMessageType(LoggingEvent.INFORMATION,AllowedMessageTypes);

    // toggle show warnings
    if ( mnuShowWarnings.getState() )
      AllowedMessageTypes = LoggingEvent.addMessageType(LoggingEvent.WARNING,AllowedMessageTypes);

    // toggle show errors
    if ( mnuShowErrors.getState() )
      AllowedMessageTypes = LoggingEvent.addMessageType(LoggingEvent.ERROR,AllowedMessageTypes);

    // toggle show debug
    if ( mnuShowDebug.getState() )
      AllowedMessageTypes = LoggingEvent.addMessageType(LoggingEvent.DEBUG,AllowedMessageTypes);

    BottomScreenLogger.MessagesToWatch = AllowedMessageTypes;
    }
  }


  /** 
   ***********************************************************************
   * Closing procedures
   ***********************************************************************
   */
  void this_windowClosed(WindowEvent e)
    {
    m_DisableLogging();
    if ( listenThread instanceof LogListener )
      {
      listenThread.pleaseStop();
      listenThread = null;
      }
    }

  /** 
   ***********************************************************************
   * Disable logging to the screen
   ***********************************************************************
   */
  private void m_DisableLogging()
    {
    AppLog.removeLogger(LeftScreenLogger);
    AppLog.removeLogger(RightScreenLogger);
    AppLog.removeLogger(BottomScreenLogger);

    if ( filelogger instanceof FileLogger )
      AppLog.removeLogger(filelogger);
    }


 /** 
  ***********************************************************************
  * called when form is destroyed
  ***********************************************************************
  */
 public void finalize()
   {
   m_DisableLogging();
   if ( listenThread instanceof LogListener )
     {
     listenThread.pleaseStop();
     listenThread = null;
     }

   }

}

/** 
 ***********************************************************************
 *
 ***********************************************************************
 */

class mnuStartFileLogListener implements ActionListener
{
 private WatchTaFrame owner;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
 public mnuStartFileLogListener(final WatchTaFrame aOwner)
   {
   owner = aOwner;
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void actionPerformed(ActionEvent e)
   {

   try
     {

     if ( owner instanceof WatchTaFrame )
       {
       if ( owner.filelogger instanceof FileLogger )
         AppLog.removeLogger(owner.filelogger);

       final File LogFile = m_PromptForFileName("Select the file to save messages to");
       if ( LogFile instanceof File )
         {
         owner.filelogger = new FileLogger(LogFile,LoggingEvent.ALL_MESSAGES,owner.sTAName);
         AppLog.addLogger(owner.filelogger);
         owner.lblBottomCaption.setText("Messages      (saved to " + LogFile.getAbsolutePath() + ")");
         }
       }

     }
   catch (Exception excp)
     {
     }

   }

  /** 
   ***********************************************************************
   *  This procedure prompts the user for a file
   ***********************************************************************
   */
  private File m_PromptForFileName(final String aPrompt)
    {
    // change logging directory
    final JFileChooser filechooser = new JFileChooser();
    filechooser.setFileSelectionMode( filechooser.FILES_ONLY );
    filechooser.setDialogTitle( aPrompt );
    final int retval = filechooser.showSaveDialog(owner);
    if ( retval == filechooser.APPROVE_OPTION )
      return( filechooser.getSelectedFile() );
    else
      return(null);
    }

}

/** 
 ***********************************************************************
 *
 ***********************************************************************
 */

class mnuStopFileLogListener implements ActionListener
{
 private WatchTaFrame owner;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
 public mnuStopFileLogListener(final WatchTaFrame aOwner)
   {
   owner = aOwner;
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void actionPerformed(ActionEvent e)
   {

   try
     {

     if ( owner instanceof WatchTaFrame )
       {
       owner.lblBottomCaption.setText("Messages");

       if ( owner.filelogger instanceof FileLogger )
         AppLog.removeLogger(owner.filelogger);
       }

     }
   catch (Exception excp)
     {
     }

   }

}
