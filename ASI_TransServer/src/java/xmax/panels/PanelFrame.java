
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import javax.swing.JFrame;
import xmax.crs.PNR;
import xmax.crs.GetPNR.*;
import xmax.crs.Flifo.*;
import xmax.crs.cars.LocationInfo;
import xmax.crs.hotel.HotelInfo;
import xmax.crs.PNRFare;
import xmax.crs.Amadeus.AmadeusAPICrs;
import xmax.crs.Availability.*;

public class PanelFrame extends JFrame
{
  GridLayout gridLayout1 = new GridLayout();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PanelFrame()
    {
    try
    {
      jbInit();
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
    this.setTitle("Test Frame");
    gridLayout1.setColumns(1);
    gridLayout1.setRows(0);
    this.getContentPane().setLayout(gridLayout1);
    }

  /**
   ***********************************************************************
   * main (unit test)
   ***********************************************************************
   */
  public static void main(String[] args)
    {

     final AmadeusAPICrs CRS = new AmadeusAPICrs();
     try
       {
       // setup the frame that will contain the panel
       final PanelFrame thisFrame  = new PanelFrame();
       thisFrame.setDefaultCloseOperation(thisFrame.DISPOSE_ON_CLOSE);
       final Container contentPane = thisFrame.getContentPane();

       final PNR pnr = new PNR();
       final FlightInfo flight = new FlightInfo("AA",100);
       final LocationInfo location = new LocationInfo();
       final HotelInfo hotel = new HotelInfo();
       final DestAvailability destAvail = new DestAvailability("SFO","MCO","03MAR",10);
       if ( CRS instanceof AmadeusAPICrs )
         {
      //   CRS.setProperty(AmadeusAPICrs.GATEWAY_HOST_PROPERTY,"ntws13");
      //   CRS.setProperty(AmadeusAPICrs.GATEWAY_PORT_PROPERTY,"1413");
      //   CRS.setProperty(AmadeusAPICrs.TA_NAME_PROPERTY,"AATerm07");
      //   CRS.setProperty(AmadeusAPICrs.USER_PROPERTY,"8028");
      //   CRS.setProperty(AmadeusAPICrs.PASSWORD_PROPERTY,"XMAX77");
      //   CRS.setProperty(AmadeusAPICrs.HOME_PCC_PROPERTY,"HJ37");
         CRS.Connect();

           /*
           CRS.GetPNRAllSegments("TBYFRM",pnr,false);
           final PanelPNR pnlPNR = new PanelPNR(pnr);
           contentPane.add(pnlPNR);
           */

           CRS.GetAvailability(destAvail);
           final PanelAvailability pnlAvail = new PanelAvailability(destAvail);
           contentPane.add(pnlAvail);

           /*
           CRS.GetFlightInfo("AA","100","21SEP",flight);
           final PanelFlight pnlFlight = new PanelFlight(flight,"JFK","LHR");
           contentPane.add(pnlFlight);
           */
           /*
           CRS.GetLocationInfo("ZL","AUS","",location);
           final PanelCarRental pnlLocation = new PanelCarRental(location);
           contentPane.add(pnlLocation);
           */
           /*
           CRS.GetHotelInfo("RL","12938",hotel);
           final PanelHotel pnlHotel = new PanelHotel(hotel);
           contentPane.add(pnlHotel);
           */
           /*
           final PNRFare fare = new PNRFare("ADT","PFA",3);
           final long iFare = 43750;
           final long iTax  = 2783;
           fare.addFare("Base Fare",iFare);
           fare.addTax("Tax",iTax);
           final PanelFare pnlFare = new PanelFare(fare);
           contentPane.add(pnlFare);
           */
         thisFrame.pack();
         thisFrame.show();
         }

       }
     catch( Exception e )
       {
       System.out.println(e.toString());
       }
     finally
       {
       CRS.Disconnect();
       }
    }


}