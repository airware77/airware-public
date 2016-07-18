package xmax.TranServer;

import xmax.crs.PNR;
import xmax.crs.GnrcCrs;
import xmax.crs.BaseCrs;
import xmax.crs.GetPNR.*;
import xmax.crs.hotel.HotelInfo;
import xmax.crs.Flifo.FlightInfo;
import xmax.crs.cars.LocationInfo;

import xmax.util.Log.FileLogger;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;

import java.io.File;
import java.io.Serializable;

/**
 ***********************************************************************
 * This request is used to retrieve a Passenger Name Record (PNR) from a
 * Computer Reservation System (CRS).
 * 
 * @author   David Fairchild
 * @version  $Revision: 17$ - $Date: 09/18/2002 4:42:42 PM$
 ***********************************************************************
 */
public class ReqGetPNR extends ReqTranServer implements Serializable
{
 public String Locator;
 public String QueueName;
 public boolean ExtendedInfo;
 public boolean GetStoredFares;

 /** 
  * temporary flag to indicate that we want to use the new PNR2 format;
  * this should be removed after we migrate everyone to the new format
  */
 public boolean isPNR2 = false;

 /** 
  * causes {@link NativeAsciiWriter} to return remarks on the PNR, set to
  * <code>true</code> by default.
  */
 public boolean ReturnRemarks = true;

 /** 
  * if this is a request to retrieve a PNR from a queue, specifies whether to
  * leave or remove the PNR from the queue; set to <code>true</code> by default, 
  */
 public boolean RemoveFromQueue = true;

 /** this is set by runRequest if the queue is empty */
 public boolean QueueEmpty;        
 public PNR pnr;

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public ReqGetPNR(final String aCrsCode)
    {
    super(aCrsCode);
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    pnr = new PNR();

    // log before the attempt as well
    final String sPreLogging;
    if ( GnrcFormat.NotNull(QueueName) )
      sPreLogging = "Attempting to pull next PNR from queue " + QueueName + " on host " + aCrs.getHostCode() + " using TA " + aCrs.getConnectionName();
    else if ( GnrcFormat.NotNull(Locator) )
      sPreLogging = "Attempting to retrieve PNR for locator " + Locator + " on host " + aCrs.getHostCode() + " using TA " + aCrs.getConnectionName();
    else
      sPreLogging = "Attempting to get PNR from AAA on host " + aCrs.getHostCode() + " using TA " + aCrs.getConnectionName();
    AppLog.LogInfo(sPreLogging,null,aCrs.getConnectionName());


    // get basic PNR info from queue or by locator
    if ( GnrcFormat.NotNull(QueueName) )
      {
      boolean isRedisplayPNR = false;

      //aCrs.GetPNRFromQueue(QueueName,pnr,true,true);
      aCrs.GetPNRFromQueue(QueueName,pnr,RemoveFromQueue,isRedisplayPNR);
      if ( pnr.hasNoData() )
        {
        QueueEmpty = true;
        return;
        }
      }
    else if ( GnrcFormat.NotNull(Locator) )
      {
      String receivedBy = ConfigTranServer.application.getProperty("receiveBy"); 
      aCrs.AcceptSchedChange(Locator,receivedBy);
      aCrs.GetPNRAllSegments(Locator,pnr,true);
      }
    else
      aCrs.GetPNRFromAAA(pnr);

    final String sPNRData = pnr.getPNRData();
    AppLog.LogInfo("PNR Data = \r\n" + sPNRData + "\r\n",null,aCrs.getConnectionName());

    final String sLogging;
    if ( GnrcFormat.NotNull(QueueName) )
      sLogging = "Pulled PNR " + pnr.getLocator() + " from queue " + QueueName + " on host " + aCrs.getHostCode() + " using TA " + aCrs.getConnectionName();
    else if ( GnrcFormat.NotNull(Locator) )
      sLogging = "Retrieve PNR for locator " + Locator + " on host " + aCrs.getHostCode() + " using TA " + aCrs.getConnectionName();
    else
      sLogging = "Retrieve PNR from AAA on host " + aCrs.getHostCode() + " using TA " + aCrs.getConnectionName();
    AppLog.LogInfo(sLogging,null,aCrs.getConnectionName());

    // get detailed info on itinerary segments, if needed
    getExtendedSegmentInfo(aCrs,pnr,ExtendedInfo);

    // get optional info
    if ( ExtendedInfo )
      aCrs.GetSeatAssignments(pnr.getLocator(),pnr);

    if ( GetStoredFares )
      aCrs.getStoredFare(pnr.getLocator(),pnr);

    // if the pnr contains segments sold from a Managed block
    // retrieve the locators for those blocks
    if (pnr.hasManagedBlockAirSegment())
       aCrs.getManagedBlockLocators(pnr.getLocator(),pnr);

    // if this is Apollo, check if you need to get the itinerary remarks
    /*
    if ( aCrs instanceof ApolloCrs )
      {
      final ApolloCrs apolloCrs = (ApolloCrs )aCrs;
      if ( sPNRData.indexOf("ITINERARY REMARKS EXIST") >= 0 )
        apolloCrs.GetItinRemarks(pnr);
      }
      */

    // added 2002-08-07 to prevent conversation cross-overs from resulting 
    // in freak PNR modifications
    // aCrs.Ignore();
    } // end runRequest

  /**
   ***********************************************************************
   *
   ***********************************************************************
   */
  private void getExtendedSegmentInfo(final GnrcCrs aCrs, final PNR aPNR, final boolean aExtendedInfo)
    {
    try
      {
      // get PNR itinerary segments
      final PNRItinSegment[] Segments = aPNR.getSegments();
      final String sLocator           = aPNR.getLocator();


      // look at each segment and get extended info if required
      if ( Segments instanceof PNRItinSegment[] )
        {
        PNRItinCarSegment   CarSeg;
        PNRItinHotelSegment HotelSeg;
        PNRItinAirSegment   AirSeg;

        for ( int i = 0; i < Segments.length; i++ )
          {
          if ( Segments[i] instanceof PNRItinCarSegment )
            {
            // get extended car rental info
            CarSeg = (PNRItinCarSegment )Segments[i];
            if ( aExtendedInfo )
              {
              try
                {
                final LocationInfo LocInfo = new LocationInfo();
                aCrs.GetLocationInfo(sLocator,CarSeg,LocInfo);
                CarSeg.setLocationInfo(LocInfo);
                }
              catch (Exception e)
                {
                aPNR.addError("Unable to get extended info for car location: " + e.toString());
                }
              }

            }
          else if ( Segments[i] instanceof PNRItinHotelSegment )
            {
            // get extended hotel info
            HotelSeg = (PNRItinHotelSegment )Segments[i];
            if ( aExtendedInfo )
              {
              try
                {
                final HotelInfo HtlInfo = new HotelInfo();
                aCrs.GetHotelInfo(sLocator,HotelSeg,HtlInfo);
                HotelSeg.setLocationInfo(HtlInfo);
                }
              catch (Exception e)
                {
                aPNR.addError("Unable to get extended info for hotel: " + e.toString());
                }
              }

            }
          else if ( Segments[i] instanceof PNRItinAirSegment )
            {
            // get extended flight info
            AirSeg = (PNRItinAirSegment )Segments[i];
            if ( aExtendedInfo || AirSeg.isCodeShare || AirSeg.isChangeOfGauge || aCrs.getHostCode().equals(BaseCrs.AMADEUS_CODE) )
              {
              try
                {
                final FlightInfo FlightData = new FlightInfo(AirSeg.Carrier,AirSeg.FlightNumber);
                aCrs.GetFlightInfo(sLocator,AirSeg,FlightData);
                AirSeg.setFlightInfo(FlightData);
                }
              catch (Exception e)
                {
                aPNR.addError("Unable to get extended info for flight segment " + AirSeg.SegmentNumber + ": " + e.toString());
                }
              }

            }
          }
        }

      }
    catch (Exception e)
      {
      aPNR.addError("Unable to get extended info for itinerary: " + e.toString());
      }
    }

  /** 
   ***********************************************************************
   * Get a log file name to use for this request
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    final StringBuffer sLogName = new StringBuffer();
    if ( GnrcFormat.NotNull(aLogDirectory) )
      sLogName.append(aLogDirectory);

    sLogName.append("\\Locators\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for Get PNR.  Crs Code is null");

    if ( GnrcFormat.NotNull(Locator) )
      {
      sLogName.append(getCrsCode() + Locator + ".log");
      return( sLogName.toString() );
      }
    else
      return(null);
    }

}
