package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.Availability.*;
import xmax.crs.Flifo.FlightInfo;
import xmax.util.Log.*;

import java.io.Serializable;

public class ReqGetAvail extends ReqTranServer implements Serializable
{
 public String DepCity;
 public String ArrCity;
 public String DepDate;     // in CRS format  (ie: 21MAY)
 public String DepTime;
 public String ArrDate;     // in CRS format
 public String ArrTime;
 public String Carrier;

 /** 
  * The type of availability call to make (only relevant when using Amadeus
  * API); this flag can be one of three values:
  * <ul>
  *   <li> Direct (D): make a direct call to the reservation system of the
  *        carrier</li>
  *   <li> Filtered (F): use Amadeus Availability, but filter by carrier so
  *        that only flights operated by the given carrier are displayed</li>
  *   <li> Neutral (N): show all carriers</li>
  *</ul>
  */
 public String AvailType = AVAIL_NEUTRAL;

 /** 
  * used for private fares, but could also be used to see a specific class of
  * service 
  */
 public String ClassOfService;

 public int FlightNum;
 public int NumItins;

 /** 
  * The Transaction Server allows for the following functionality, if the
  * <code>ItinQuality</code> flag is set to one of the three following
  * constants:
  * <ul>
  *  <li><code>ITIN_CONNECT</code> : returns all itins</li>
  *  <li><code>ITIN_DIRECT</code>  : returns itin if all segments share the
  *                                  same carrier and flight (may have stops)</li>
  *  <li><code>ITIN_NON_STOP</code>: returns only itins that contain only 1
  *                                  segment (no stops, most restrictive)</li>
  * </ul>
  * Currently, the field is being used in the following manner:
  * If  the flag is set to 'Y' or 'T' in the incoming request, the constant is
  * set to ITIN_DIRECT;
  * If the flag is set to anything else, the flag is set to
  * <code>ITIN_CONNECT</code> (all itins); 
  * Airware is currently passing an N on all requests, which defaults to
  * <code>ITIN_CONNECT</code>, and effectively returns all itineraries.
  */
 public int ItinQuality;

 public boolean getSegmentDetails;
 public static final int ITIN_CONNECT = 0;
 public static final int ITIN_DIRECT  = 1;
 public static final int ITIN_NONSTOP = 2;
 public DestAvailability avail;
 public static final String AVAIL_DIRECT  = "D";
 public static final String AVAIL_FILTER  = "F";
 public static final String AVAIL_NEUTRAL = "N";

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqGetAvail(final String aCrsCode,
                    final String aDepCity, final String aArrCity,
                    final String aDepDate)
   {
   super(aCrsCode);
   DepCity = aDepCity;
   ArrCity = aArrCity;
   DepDate = aDepDate;
   }

  /** 
   ***********************************************************************
   * This method is the main method for running an availability request;
   * it populates the request portion of the
   * {@link xmax.crs.Availability.DestAvailability DestAvailabality} object
   * that will be used to store the Itinerary, Segment, Flight and
   * Class Information returned; it also calls getExtendedSegmentInfo to
   * retrieve extended flight information for those segments that require it.
   *
   * @see getExtendedSegmentInfo
   * @see xmax.crs.Availability
   * @see xmax.crs.Availability.DestAvailabality
   * @see xmax.crs.Availability.ItinAvailabality
   * @see xmax.crs.Availability.FlightAvailability
   * @see xmax.crs.Availability.InvClassAvailability
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Getting availability from " + DepCity + " to " + ArrCity + " on " + DepDate,null,aCrs.getConnectionName());

    // set up the availability object
    avail = new DestAvailability();

    avail.ReqDepCity             = DepCity;
    avail.ReqArrCity             = ArrCity;
    avail.ReqDepDate             = DepDate;
    avail.ReqDepTime             = DepTime;
    avail.ReqArrDate             = ArrDate;
    avail.ReqArrTime             = ArrTime;
    avail.ReqDirectAccessCarrier = Carrier;
    avail.ReqAvailType           = AvailType;
    avail.ReqFilterFlight        = FlightNum;
    avail.ReqMaxItins            = NumItins;
    avail.ReqClassOfService      = ClassOfService;

    if ( ItinQuality == ITIN_NONSTOP )
      avail.NonStopsOnly = true;
    else if ( ItinQuality == ITIN_DIRECT )
      avail.DirectOnly = true;

    // get the availability
    aCrs.GetAvailability(avail);

    AppLog.LogInfo("Received " + avail.getNumItins() + " itineraries for " + DepCity + " to " + ArrCity + " on " + DepDate,null,aCrs.getConnectionName());

    getExtendedSegmentInfo(aCrs,avail,getSegmentDetails);
    }

  /** 
   ***********************************************************************
   * This method gets flight information if any of the segments has
   * a SharedCarrier Code, an Equipment Change, or specifically requires
   * Flight Information.
   *
   * @param aCrs
   *   the Computer Reservation System (CRS) object with which
   *   we are connecting
   *
   * @param aAvail
   *   the DestAvailability object where we are storing all
   *   the flight information for this request
   *
   * @param aExtendedInfo
   *   A boolean flag used to specify whether we should fetch extended info.
   *   This field is *not* being used at this time.
   ***********************************************************************
   */
  private void getExtendedSegmentInfo(final GnrcCrs aCrs,
                                        final DestAvailability aAvail,
                                        final boolean aExtendedInfo)
                                        throws Exception
    {
    if ( (aAvail instanceof DestAvailability) == false )
      return;

    // get PNR itinerary segments
    final ItinAvailability[] itins = aAvail.getItinArray();

    // look at each segment and get extended info if required
    if ( itins instanceof ItinAvailability[] )
      {
      FlightAvailability[] flights;

      for ( int itinNum = 0; itinNum < itins.length; itinNum++ )
        {
        flights = itins[itinNum].getFlightAvailabilities();

        if ( flights instanceof FlightAvailability[] )
          {

          for ( int fNum = 0; fNum < flights.length; fNum++ )
            {
            // get extended flight info
            // a Flifo is required for Code Shares or ChangeOfGauge,
            // when screen-scraping, but not when using the Amadeus XML API
            // the needsFlifo flag is set in the GetAvail Conversations
            if ( //flights[fNum].hasSharedCarr ||
                 //flights[fNum].hasEquipChange ||
                 flights[fNum].needsFlifo )
              {
              try
                {
                final String sCarrier   = flights[fNum].Carrier;
                final int iFlightNum = flights[fNum].FlightNum;
                final String sDepDate   = GnrcFormat.FormatCRSDate(flights[fNum].DepDate);
                final FlightInfo FlightData = new FlightInfo(sCarrier,iFlightNum);
                aCrs.GetFlightInfo(sCarrier,iFlightNum,sDepDate,FlightData);
                flights[fNum].setFlightInfo(FlightData);
                }
              catch (Exception e)
                {
                AppLog.LogError(e.toString(),null,aCrs.getConnectionName());
                }
              }

            }

          }
        }
      }

    }

//  /** 
//   ***********************************************************************
//   *
//   ***********************************************************************
//   */
//  private FileLogger startLogging(final GnrcCrs aCrs) throws Exception
//    {
//    // check input parms
//    if ( GnrcFormat.IsNull(DepCity) )
//      throw new TranServerException("Cannot open log file for Get Availability.  Departure city is null");
//
//    if ( GnrcFormat.IsNull(ArrCity) )
//      throw new TranServerException("Cannot open log file for Get Availability.  Arrival city is null");
//
//    if ( GnrcFormat.IsNull(DepDate) )
//      throw new TranServerException("Cannot open log file for Get Availability.  Departure date is null");
//
//    // setup the file logger
//    final String sLogDir  = ConfigInformation.getFileParamValue(ConfigTranServer.LOGGING_DIRECTORY,"LOG");
//    final String sLogName = sLogDir + "\\Availability\\" + aCrs.getHostCode()
//                            + DepCity + ArrCity + DepDate
//                            + ".log";
//
//    return( startLogging(sLogName,aCrs) );
//    }

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

    sLogName.append("\\Availability\\");

    // check input parms
    if ( GnrcFormat.IsNull(DepCity) )
      throw new TranServerException("Cannot open log file for Get Availability.  Departure city is null");

    if ( GnrcFormat.IsNull(ArrCity) )
      throw new TranServerException("Cannot open log file for Get Availability.  Arrival city is null");

    if ( GnrcFormat.IsNull(DepDate) )
      throw new TranServerException("Cannot open log file for Get Availability.  Departure date is null");

    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for Get Availability.  Crs Code is null");

    final String sCarrier;
    if ( Carrier instanceof String )
      sCarrier = Carrier;
    else
      sCarrier = "YY";

    sLogName.append( getCrsCode()
        + DepCity + ArrCity + DepDate + sCarrier + NumItins + ".log");

    return( sLogName.toString() );
    }

}
