package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import xmax.crs.GetPNR.PNRItinAirSegment;
import java.util.StringTokenizer;
import java.io.Serializable;
import java.util.Vector;

public class ReqCxlAirSegment extends ReqTranServer implements Serializable
{
 public String Locator;
 private Vector airsegs;

 /**
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqCxlAirSegment(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    Locator = aLocator;
    airsegs = new Vector();
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    final PNRItinAirSegment[] segs = getAirSegments();
    if ( segs instanceof PNRItinAirSegment[] )
      {
      AppLog.LogInfo("Canceling " +  segs.length + " air segments for locator " + Locator,null,aCrs.getConnectionName());

      aCrs.deletePnrElements(Locator,segs,RequestedBy);
     }
    }

  /**
   ***********************************************************************
   * Add an air segment to be cancelled
   ***********************************************************************
   */
  public void addAirSegment(final String aCarrier, final int aFlightNum,
                            final String aDepCity, final String aArrCity, final long aDepDate)
    {
    final PNRItinAirSegment airseg = new PNRItinAirSegment();

    airseg.Carrier           = aCarrier;
    airseg.FlightNumber      = aFlightNum;
    airseg.DepartureCityCode = aDepCity;
    airseg.ArrivalCityCode   = aArrCity;
    airseg.DepartureDateTime = aDepDate;

    airsegs.add(airseg);
    }


  public void addAirSegment(final PNRItinAirSegment aSegment)
    {
    airsegs.add(aSegment);
    }

  /**
   ***********************************************************************
   * get the current list of air segments to be cancelled
   ***********************************************************************
   */
  public PNRItinAirSegment[] getAirSegments()
    {
    if ( airsegs instanceof Vector )
      {
      if ( airsegs.size() > 0 )
        {
        final PNRItinAirSegment[] segs = new PNRItinAirSegment[ airsegs.size() ];
        airsegs.toArray(segs);
        return(segs);
        }
      }

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
      throw new TranServerException("Cannot open log file for cancel air segment.  Crs Code is null");

    if ( GnrcFormat.IsNull(Locator) )
      throw new TranServerException("Cannot open log file for cancel air segment.  Locator is null");

    sLogName.append(getCrsCode() + Locator + ".log");
    return( sLogName.toString() );
    }


}
