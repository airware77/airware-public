package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.GetPNR.PNRNameElement;
import xmax.crs.GetPNR.PNRItinSegment;
import xmax.crs.GetPNR.PNRItinAirSegment;
import xmax.util.Log.AppLog;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 ***********************************************************************
 * This object stores the request and response information used when changing
 * the itinerary on a Passenger Name Record (PNR);  if the itinerary is changed
 * for all passengers on the PNR, the changes are made on the original PNR; if
 * the itinerary change only applies to a subset of the passengers on the PNR,
 * the PNR is split and the changes are made on the split PNR.
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 2$ - $Date: 09/12/2002 4:47:13 PM$
 ***********************************************************************
 */
public class ReqChangePnrItin extends ReqTranServer implements Serializable
{
  /** the record locator of the PNR to be modified */
  public String locator;

  /** the record locator of the split PNR, if any */
  public String splitLocator;

  /** the list of passenger names to be modified */
  protected List passengerList;

  /** the list of segments to be modified */
  protected List oldSegmentList;

  /** the list of segments that will replace the segments to be modified */
  protected List newSegmentList;
  
  
  /**
   ***********************************************************************
   * simple constructor that calls the super constructor and instatiates empty
   * lists
   ***********************************************************************
   */
  public ReqChangePnrItin(String sCrsCode)
    {
    super(sCrsCode);
    passengerList  = new ArrayList();
    oldSegmentList = new ArrayList();
    newSegmentList = new ArrayList();
    } // end ReqChangePnrItin

  /**
   ***********************************************************************
   * calls the constructor above, and populates the locator field
   ***********************************************************************
   */
  public ReqChangePnrItin(String sCrsCode, String sLocator)
    {
    this(sCrsCode);
    locator = sLocator;
    } // end ReqChangePnrItin


  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * and runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Modifying PNR locator " + locator,null,aCrs.getConnectionName());

    PNRNameElement[]    aryPsgrList = null;;
    PNRItinAirSegment[] aryOldSegs  = null;;
    PNRItinAirSegment[] aryNewSegs  = null;;

    if (passengerList instanceof List)
      {
      aryPsgrList = new PNRNameElement[ passengerList.size() ];
      passengerList.toArray(aryPsgrList);
      }

    if ( (oldSegmentList instanceof List) && (newSegmentList instanceof List) )
      {
      aryOldSegs = new PNRItinAirSegment[ oldSegmentList.size() ];
      aryNewSegs = new PNRItinAirSegment[ newSegmentList.size() ];
      oldSegmentList.toArray(aryOldSegs);
      newSegmentList.toArray(aryNewSegs);
      }

    final StringBuffer aNewLocator = new StringBuffer();

    aCrs.changePnrItinerary(locator, aryPsgrList, aryOldSegs, aryNewSegs,
        RequestedBy,aNewLocator);

    //final PNRNameElement[] names = getNames();

    //aCrs.splitPNR(OriginalLocator,NumUnassigned,names,RequestedBy,sNewLocator);
    splitLocator = aNewLocator.toString();

    if (splitLocator instanceof String)
      AppLog.LogInfo(
          "Successfully modified itinerary in locator " + locator + 
          " - new associate PNR locator = " + splitLocator,
          null,aCrs.getConnectionName());
    else
      AppLog.LogInfo(
          "Successfully modified itinerary in locator " + locator,
          null,aCrs.getConnectionName());


//    // do a receive by
//    aCrs.AddReceiveBy(RequestedBy);
//
//    // do an end transaction
//    aCrs.EndTransaction();
//
    } // end runRequest


  /**
   ***********************************************************************
   * Adds a passenger, identified by her passenger ID, to the list of
   * passengers whose itinerary will be modified
   ***********************************************************************
   */
  public void addName(final String aPsgrID)
    {
    final PNRNameElement name = new PNRNameElement();
    name.setPassengerID(aPsgrID);
    passengerList.add(name);
    }


  /**
   ***********************************************************************
   * Add an air segment to the specified list
   ***********************************************************************
   */
  private void addAirSegment(List aList, String aCarrier, final int aFlightNum,
      final String aDepCity, final String aArrCity, final long aDepDate) 
    {
    final PNRItinAirSegment airseg = new PNRItinAirSegment();

    airseg.Carrier           = aCarrier;
    airseg.FlightNumber      = aFlightNum;
    airseg.DepartureCityCode = aDepCity;
    airseg.ArrivalCityCode   = aArrCity;
    airseg.DepartureDateTime = aDepDate;

    aList.add(airseg);
    } // end addAirSegment


  /**
   ***********************************************************************
   * Adds the specified segment to the list of segments to be cancelled
   ***********************************************************************
   */
  public void addSegmentToBeCancelled(String aCarrier, final int aFlightNum,
      final String aDepCity, final String aArrCity, final long aDepDate)
    {
    addAirSegment(
        oldSegmentList, aCarrier, aFlightNum, aDepCity, aArrCity, aDepDate);

    } // end addSegmentToBeCancelled


  /**
   ***********************************************************************
   * Adds the specified segment to the list of segments to be added to the PNR
   ***********************************************************************
   */
  public void addNewSegment(PNRItinAirSegment airSeg)
    {
    newSegmentList.add(airSeg);

    } // end addNewSegment


   /** 
    ***********************************************************************
    * Returns an array of the new segments that were added to the PNR
    ***********************************************************************
    */
    public PNRItinSegment[] getNewSegments()
      {
      if ( newSegmentList.size() > 0 )
        {
        final PNRItinSegment[] segs = new PNRItinSegment[newSegmentList.size()];
        newSegmentList.toArray(segs);
        return(segs);
        }
      else
        return(null);
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
      throw new TranServerException("Cannot open log file for modifying PNR.  Crs Code is null");

    if ( GnrcFormat.IsNull(locator) )
      throw new TranServerException("Cannot open log file for modifying PNR.  Locator is null");

    sLogName.append(getCrsCode() + locator + ".log");
    return( sLogName.toString() );
    } // end getLogFileName

} // end class ReqChangePnrItin

