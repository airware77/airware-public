
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.TranServer;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
//import com.borland.jbcl.layout.*;
import xmax.dialogs.*;
import javax.swing.JOptionPane;
import xmax.crs.GnrcCrs;
import xmax.panels.*;
import java.net.Socket;
import java.io.*;
import javax.swing.JInternalFrame;
import java.applet.Applet;
import javax.swing.JPopupMenu;
import javax.swing.JApplet;

public class PanelGdsClient extends JApplet
{                                         // JFrame or JApplet
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu mnuRetrieve = new JMenu();
  JMenuItem mnuGetPNR = new JMenuItem();
  JMenu mnuPNR = new JMenu();
  JMenuItem mnuGetPNR2 = new JMenuItem();
  JMenuItem mnuGetAvail = new JMenuItem();
  JMenuItem mnuGetFlifo = new JMenuItem();
  JMenuItem mnuGetHotel = new JMenuItem();
  JMenuItem mnuGetCar = new JMenuItem();
  JMenuItem mnuGetFares = new JMenuItem();
  JMenuItem mnuIssueTicket = new JMenuItem();
  JMenuItem mnuBuildPNR = new JMenuItem();
  JMenu mnuLink = new JMenu();
  JMenuItem mnuConnect = new JMenuItem();
  JMenuItem mnuFreeForm = new JMenuItem();
  JMenuItem mnuIgnore = new JMenuItem();
  JMenuItem mnuStatus = new JMenuItem();
  JMenuItem mnuTerminal = new JMenuItem();
  JMenuItem mnuClose = new JMenuItem();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel pnlNull = new JPanel();
  private Socket socketClient;
  private BufferedInputStream inBuffer;
  private OutputStream outstream;
  private ObjectInputStream  ObjIn;
  private ObjectOutputStream ObjOut;
  private String CrsCode;
  boolean TerminateOnClose;
  private Frame owner;
  BorderLayout borderLayout2 = new BorderLayout();

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PanelGdsClient()
    {
    this(null,false);
    }


  public PanelGdsClient(final Frame aOwner)
    {
    this(aOwner,false);
    }


  public PanelGdsClient(final Frame aOwner, final boolean aTerminateOnClose)
    {
    try
      {
      jbInit();
      owner = aOwner;
      TerminateOnClose = aTerminateOnClose;
      }
    catch(Exception e)
      {
      e.printStackTrace();
      }
    }

  /** 
   ***********************************************************************
   * used by constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    this.getContentPane().setLayout(borderLayout1);

    mnuRetrieve.setText("Retrieve");
    mnuGetPNR.setText("PNR");
    mnuGetPNR.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        mnuGetPNR_actionPerformed(e);
      }
    });
    mnuPNR.setText("PNR");
    mnuGetPNR2.setText("Retrieve");
    mnuGetPNR2.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuGetPNR_actionPerformed(e);
      }
    });
    mnuGetAvail.setText("Availability");
    mnuGetAvail.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuGetAvail_actionPerformed(e);
      }
    });
    mnuGetFlifo.setText("Flifo");
    mnuGetFlifo.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuGetFlifo_actionPerformed(e);
      }
    });
    mnuGetHotel.setText("Hotel ");
    mnuGetHotel.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuGetHotel_actionPerformed(e);
      }
    });
    mnuGetCar.setText("Car Rental");
    mnuGetCar.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuGetCar_actionPerformed(e);
      }
    });
    mnuGetFares.setText("Get Fares");
    mnuGetFares.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuGetFares_actionPerformed(e);
      }
    });
    mnuIssueTicket.setText("Issue Ticket");
    mnuIssueTicket.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuIssueTicket_actionPerformed(e);
      }
    });
    mnuBuildPNR.setText("Build");
    mnuLink.setText("Link");
    mnuConnect.setText("Connect");
    mnuConnect.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuConnect_actionPerformed(e);
      }
    });
    mnuFreeForm.setText("Free Form");
    mnuFreeForm.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuFreeForm_actionPerformed(e);
      }
    });
    mnuIgnore.setText("Ignore");
    mnuStatus.setText("Status");
    mnuTerminal.setText("Terminal");
    mnuClose.setText("Close");
    mnuClose.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        mnuClose_actionPerformed(e);
      }
    });
    pnlNull.setPreferredSize(new Dimension(475, 425));
    pnlNull.setLayout(borderLayout2);
    this.setEnabled(true);
    this.setName("pnlApplet");
    jMenuBar1.add(mnuLink);
    jMenuBar1.add(mnuRetrieve);
    jMenuBar1.add(mnuPNR);
    mnuRetrieve.add(mnuGetPNR);
    mnuRetrieve.add(mnuGetAvail);
    mnuRetrieve.add(mnuGetFlifo);
    mnuRetrieve.add(mnuGetHotel);
    mnuRetrieve.add(mnuGetCar);
    mnuPNR.add(mnuGetPNR2);
    mnuPNR.add(mnuGetFares);
    mnuPNR.add(mnuIssueTicket);
    mnuPNR.add(mnuBuildPNR);
    mnuLink.add(mnuStatus);
    mnuLink.add(mnuConnect);
    mnuLink.addSeparator();
    mnuLink.add(mnuFreeForm);
    mnuLink.add(mnuIgnore);
    mnuLink.add(mnuTerminal);
    mnuLink.addSeparator();
    mnuLink.add(mnuClose);

    this.setJMenuBar(jMenuBar1);
    this.getContentPane().add(pnlNull, BorderLayout.CENTER);
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  public void setOwner(final Frame aOwner)
    {
    owner = aOwner;
    }

  /** 
   ***********************************************************************
   * Connect to host
   ***********************************************************************
   */
  void mnuConnect_actionPerformed(ActionEvent aAction)
    {
    //System.out.println("Action performed event source = " + aAction.getSource().getClass().getName() );

    final dialogConnectTranServer dlg = new dialogConnectTranServer( new Frame("Silent frame") );

    try
      {
      dlg.show();
      if ( dlg.isValidData )
        {
        final String sIPAddress = dlg.getServerIP();
        final int iPort         = dlg.getServerPort();

        CrsCode = dlg.getHostCode();
        final String sCrsName = dlg.getHostName();
      //  final int iHostType   = dlg.getHostType();

        this.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
        try
          {
          if ( socketClient instanceof Socket )
            socketClient.close();
          socketClient = new Socket(sIPAddress,iPort);

          inBuffer  = new BufferedInputStream( socketClient.getInputStream() );  // set up a buffered stream
          // inObjectReader = new ObjectInputStream(inBuffer);    // set up an object reader
          outstream  = socketClient.getOutputStream();

          JOptionPane.showMessageDialog(this,"Connect to " + sCrsName + " via server " + sIPAddress + " on port " + iPort );
          }
        catch (Exception e)
          {
          JOptionPane.showMessageDialog(this,e.toString());
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

  /** 
   ***********************************************************************
   * Get PNR
   ***********************************************************************
   */
  void mnuGetPNR_actionPerformed(ActionEvent aEvent)
    {
    // prompt for PNR info
    final dialogGetPNR dlg = new dialogGetPNR(owner,CrsCode);
    try
      {
      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetPNR request     = dlg.getRequest();
        final ReqGetPNR reqResponse = (ReqGetPNR )m_SendRequest(request);

        final PanelPNR pnl = new PanelPNR(reqResponse.pnr);
        m_DisplayPanel(pnl);
        }
      }
    finally
      {
      dlg.dispose();
      }
    }

  /** 
   ***********************************************************************
   * free form
   ***********************************************************************
   */
  void mnuFreeForm_actionPerformed(ActionEvent aAction)
    {
    // prompt for free form info
    final String sRequest = JOptionPane.showInputDialog("Request").toUpperCase();
    if ( sRequest.length() == 0 )
      return;


    // run the request
    try
      {
      this.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );

      final ReqFreeForm request = new ReqFreeForm("AA",sRequest);
      final ReqFreeForm reqResponse = (ReqFreeForm )m_SendRequest(request);
      JOptionPane.showMessageDialog(this,reqResponse.Response);
      }
    catch (Exception e)
      {
      JOptionPane.showMessageDialog(this,e.toString());
      }
    finally
      {
      this.setCursor( Cursor.getDefaultCursor() );
      }

    }

  /** 
   ***********************************************************************
   * Main function for unit tests
   ***********************************************************************
   */
  private ReqTranServer m_SendRequest(final ReqTranServer aRequest)
    {
    try
      {
      this.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );

      // check if you need to create the object output stream
      if ( (ObjOut instanceof ObjectOutputStream) == false )
        ObjOut = new ObjectOutputStream(outstream);

      // write the object
      ObjOut.writeObject(aRequest);

      // check if you need to create an object input stream
      if ( (ObjIn instanceof ObjectInputStream) == false )
        ObjIn = new ObjectInputStream(inBuffer);

      // read the object returned
      final ReqTranServer reqResult = (ReqTranServer )ObjIn.readObject();
      return(reqResult);
      }
    catch (Exception e)
      {
      System.out.println(e.toString());
      return(null);
      }
    finally
      {
      this.setCursor( Cursor.getDefaultCursor() );
      }
    }

  /** 
   ***********************************************************************
   * Display the given panel
   ***********************************************************************
   */
  private void m_DisplayPanel(final JPanel aPanel)
    {
    this.getContentPane().removeAll();

    if ( aPanel instanceof JPanel )
      this.getContentPane().add(aPanel);
    else
      this.getContentPane().add(pnlNull);

    if ( owner instanceof Frame )
      {
      owner.pack();
      dialogUtils.centerWindow(owner);
      owner.show();
      }
    else
      {
      this.getParent().invalidate();
      }
    }

  /** 
   ***********************************************************************
   * Get Availability
   ***********************************************************************
   */
  void mnuGetAvail_actionPerformed(ActionEvent e)
    {
    // prompt for availability info
    final dialogGetAvail dlg = new dialogGetAvail(owner,CrsCode);
    try
      {
      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetAvail request     = dlg.getRequest();
        final ReqGetAvail reqResponse = (ReqGetAvail )m_SendRequest(request);

        final PanelAvailability pnl = new PanelAvailability(reqResponse.avail);
        m_DisplayPanel(pnl);
        }
      }
    finally
      {
      dlg.dispose();
      }
    }

  /** 
   ***********************************************************************
   *  Flifo
   ***********************************************************************
   */
  void mnuGetFlifo_actionPerformed(ActionEvent e)
    {
    // prompt for flight info
    final dialogGetFlifo dlg = new dialogGetFlifo(owner,CrsCode);
    try
      {
      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetFlifo request     = dlg.getRequest();
        final ReqGetFlifo reqResponse = (ReqGetFlifo )m_SendRequest(request);

        final PanelFlight pnl = new PanelFlight(reqResponse.Flight);
        m_DisplayPanel(pnl);
        }
      }
    finally
      {
      dlg.dispose();
      }
    }

  /** 
   ***********************************************************************
   * Get hotel info
   ***********************************************************************
   */
  void mnuGetHotel_actionPerformed(ActionEvent e)
    {
    // prompt for hotel info
    final dialogGetHotel dlg = new dialogGetHotel(owner,CrsCode);
    try
      {
      dlg.show();
      if ( dlg.isValidData )
        {
        final ReqGetHotelInfo request     = dlg.getRequest();
        final ReqGetHotelInfo reqResponse = (ReqGetHotelInfo )m_SendRequest(request);

        final PanelHotel pnl = new PanelHotel(reqResponse.HotelChain,reqResponse.CityCode,reqResponse.PropertyCode,reqResponse.HotelInformation);
        m_DisplayPanel(pnl);
        }
      }
    finally
      {
      dlg.dispose();
      }
    }

  /** 
   ***********************************************************************
   * Get car rental info
   ***********************************************************************
   */
  void mnuGetCar_actionPerformed(ActionEvent e)
    {
    //
    }

  /** 
   ***********************************************************************
   * Get fares for a PNR
   ***********************************************************************
   */
  void mnuGetFares_actionPerformed(ActionEvent e)
    {
    //
    }

  /** 
   ***********************************************************************
   * Issue a ticket for a PNR
   ***********************************************************************
   */
  void mnuIssueTicket_actionPerformed(ActionEvent e)
    {
    //
    }

  /** 
   ***********************************************************************
   * Close the form
   ***********************************************************************
   */
  void mnuClose_actionPerformed(ActionEvent aAction)
    {
    if ( socketClient instanceof Socket )
      {
      try
        {
        socketClient.close();
        }
      catch (Exception e)
        {
        }
      }

    if ( owner instanceof Frame )
      {
      owner.setVisible(false);
      owner.dispose();
      }

    if ( TerminateOnClose )
      System.exit(0);
    }


  void this_windowClosing(WindowEvent aEvent)
    {
    if ( socketClient instanceof Socket )
      {
      try
        {
        socketClient.close();
        }
      catch (Exception e)
        {
        }
      }

    if ( owner instanceof Frame )
      {
      owner.setVisible(false);
      owner.dispose();
      }

    if ( TerminateOnClose )
      System.exit(0);
    }

  void this_windowClosed(WindowEvent aEvent)
  {
    if ( socketClient instanceof Socket )
      {
      try
        {
        socketClient.close();
        }
      catch (Exception e)
        {
        }
      }

    if ( owner instanceof Frame )
      {
      owner.setVisible(false);
      owner.dispose();
      }

    if ( TerminateOnClose )
      System.exit(0);
  }

  /** 
   ***********************************************************************
   * Main function for unit tests
   ***********************************************************************
   */
  public static JFrame getGdsClientFrame(final String aCaption)
    {
    return( getGdsClientFrame(aCaption,false) );
    }


  public static JFrame getGdsClientFrame(final String aCaption, final boolean aTerminateOnClose)
    {

    try
      {
      // create the container frame
      final JFrame thisFrame = new JFrame(aCaption);
      thisFrame.setDefaultCloseOperation(thisFrame.DISPOSE_ON_CLOSE);

      // create the applet panel within
      final PanelGdsClient thisPanel = new PanelGdsClient(thisFrame,aTerminateOnClose);

      // display the frame
      thisFrame.getContentPane().add(thisPanel);

      thisFrame.pack();
      dialogUtils.centerWindow(thisFrame);
      thisFrame.setVisible(true);
      return(thisFrame);
      }
    catch (Exception e)
      {
      System.out.println(e.toString());
      return(null);
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
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      final JFrame thisFrame = getGdsClientFrame("GDS Client",true);
      }
    catch (Exception e)
      {
      System.out.println(e.toString());
      }

    }

  /** 
   ***********************************************************************
   * applet functions
   ***********************************************************************
   */

  public void init()
    {
    final JRootPane rp = getRootPane();
    rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
    }


  public void start()
    {
    try
      {
      // create the container frame
      final JFrame thisFrame = new JFrame("INVISIBLE FRAME");
      thisFrame.setDefaultCloseOperation(thisFrame.HIDE_ON_CLOSE);
      thisFrame.setVisible(false);
      this.setOwner(thisFrame);

      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      jbInit();
      }
    catch(Exception e)
      {
      e.printStackTrace();
      }
    }

  /*
  public void stop()
    {
    //
    }

  public void destroy()
    {
    //
    }
  */
}
