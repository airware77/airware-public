package xmax.crs.GetPNR;

import xmax.crs.Flifo.FlightInfo;
import xmax.crs.Flifo.FlightSegment;
import xmax.crs.GnrcParser;
import xmax.TranServer.GnrcFormat;
import xmax.TranServer.ConfigTranServer;
import java.io.Serializable;
import java.util.Vector;

/**
 ***********************************************************************
 * This class defines the fields for an air segment within a Passenger Name
 * Record itinerary
 * 
 * @author   David Fairchild
 * @version  $Revision: 15$ - $Date: 01/27/2003 7:59:30 PM$
 ***********************************************************************
 */
public class PNRItinAirSegment extends PNRItinSegment implements Serializable
{
 public String  Carrier;
 public int     FlightNumber;
 public String  InventoryClass;
 public long    DepartureDateTime;
 public long    ArrivalDateTime;
 //public String  DepartureCityCode; moved to PNRItinSegment
 //public String  ArrivalCityCode;   moved to PNRItinSegment
 public String  ActionCode;
 public String  Status;
 public boolean isPassive   = false;
 public boolean isScheduled = true;
 public boolean is_eTicketeable = false;
 public int     NumberOfSeats;
 public String  RemoteLocator;
 public String  BlockType;
 public String  BlockLocator;
 public String  BlockCrsCode;
 public boolean isCodeShare;
 public boolean isCanceled;
 public String  CodeShareCarrCd;
 public String  CodeShareCarrDesc;
 public String  CodeShareCarrFlgt;
 public int     NumStops;
 public int     Miles;
 public int     ElapsedMinutes;
 public String  Equipment;
 public String  Meals;
 public String  DepTerminal;
 public String  DepGate;
 public String  ArrTerminal;
 public String  ArrGate;
 public boolean isChangeOfGauge;
 public String  ChangeOfGaugeEquipment;
 public String  ChangeOfGaugeCity;
 public String  OnTimePerformance;
 public long    DepEstDateTime;
 public long    DepGateOutDateTime;
 public long    DepFieldOffDateTime;
 public long    ArrEstDateTime;
 public long    ArrGateInDateTime;
 public long    ArrFieldOnDateTime;
 public String  DelayCode;
 public static final String[] PASSIVE_STATUS_CODES = {"GK","GL","PK","PL"};

 /** 
  ***********************************************************************
  * Sets any unfilled fields with info from flight
  ***********************************************************************
  */
 public void setFlightInfo(final FlightInfo aFlightInfo)
   {
   if ( aFlightInfo instanceof FlightInfo )
     {
     // set canceled flag
     isCanceled = aFlightInfo.isCanceled(DepartureCityCode,ArrivalCityCode);

     // code share info
     isCodeShare = aFlightInfo.isCodeShare(DepartureCityCode,ArrivalCityCode);

     if ( GnrcFormat.IsNull(CodeShareCarrCd) )
       CodeShareCarrCd = aFlightInfo.getCodeShareCarrierCode(DepartureCityCode,ArrivalCityCode);

     if ( GnrcFormat.IsNull(CodeShareCarrDesc) )
       CodeShareCarrDesc = aFlightInfo.getCodeShareCarrierName(DepartureCityCode,ArrivalCityCode);

     if ( GnrcFormat.IsNull(CodeShareCarrFlgt) )
       CodeShareCarrFlgt = aFlightInfo.getCodeShareFlight(DepartureCityCode,ArrivalCityCode);

     // equipment, meals
     if ( GnrcFormat.IsNull(Equipment) )
       Equipment = aFlightInfo.getEquipment(DepartureCityCode,ArrivalCityCode);

     if ( GnrcFormat.IsNull(Meals) )
       Meals = aFlightInfo.getMeal(DepartureCityCode,ArrivalCityCode);

     // gates and terminals
     if ( GnrcFormat.IsNull(DepTerminal) )
       DepTerminal = aFlightInfo.getDepTerm(DepartureCityCode);

     if ( GnrcFormat.IsNull(DepGate) )
       DepGate = aFlightInfo.getDepGate(DepartureCityCode);

     if ( GnrcFormat.IsNull(ArrTerminal) )
       ArrTerminal = aFlightInfo.getArrTerm(ArrivalCityCode);

     if ( GnrcFormat.IsNull(ArrGate) )
       ArrGate = aFlightInfo.getArrGate(ArrivalCityCode);

     // change of gauge city and equipment
     isChangeOfGauge = aFlightInfo.isChangeOfGauge(DepartureCityCode,ArrivalCityCode);

     if ( GnrcFormat.IsNull(ChangeOfGaugeCity) )
       ChangeOfGaugeCity = aFlightInfo.getChangeOfGaugeCity(DepartureCityCode,ArrivalCityCode);

     if ( GnrcFormat.IsNull(ChangeOfGaugeEquipment) )
       ChangeOfGaugeEquipment = aFlightInfo.getChangeOfGaugeEquipment(DepartureCityCode,ArrivalCityCode);

     // on time performance and delay code
     if ( GnrcFormat.IsNull(OnTimePerformance) )
       OnTimePerformance = aFlightInfo.getOnTimePerformance(DepartureCityCode,ArrivalCityCode);

     if ( GnrcFormat.IsNull(DelayCode) )
       DelayCode = aFlightInfo.getDelayCode(DepartureCityCode,ArrivalCityCode);

     // number of stops, elapsed minutes, and air miles
     NumStops = aFlightInfo.getNumStops(DepartureCityCode,ArrivalCityCode);

     if ( Miles == 0 )
       Miles = aFlightInfo.getAirMiles(DepartureCityCode,ArrivalCityCode);

     if ( ElapsedMinutes == 0 )
       ElapsedMinutes = aFlightInfo.getElapsedMinutes(DepartureCityCode,ArrivalCityCode);

     // departure times
     if ( DepartureDateTime == 0 )
       DepartureDateTime = aFlightInfo.getDepSchedDate(DepartureCityCode);

     if ( DepEstDateTime == 0 )
       DepEstDateTime = aFlightInfo.getDepEstDate(DepartureCityCode);

     if ( DepGateOutDateTime == 0 )
       DepGateOutDateTime = aFlightInfo.getDepOutGateDate(DepartureCityCode);

     if ( DepFieldOffDateTime == 0 )
       DepFieldOffDateTime = aFlightInfo.getDepOffFieldDate(DepartureCityCode);


     // arrival times
     if ( ArrivalDateTime == 0 )
       ArrivalDateTime = aFlightInfo.getArrSchedDate(ArrivalCityCode);

     if ( ArrEstDateTime == 0 )
       ArrEstDateTime = aFlightInfo.getArrEstDate(ArrivalCityCode);

     if ( ArrGateInDateTime == 0 )
       ArrGateInDateTime = aFlightInfo.getArrInGateDate(ArrivalCityCode);

     if ( ArrFieldOnDateTime == 0 )
       ArrFieldOnDateTime = aFlightInfo.getArrOnFieldDate(ArrivalCityCode);
     }
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public int getElapsedHours()
   {
   return( ElapsedMinutes / 60 );
   }

 public int getRemainingElapsedMinutes()
   {
   return( ElapsedMinutes % 60 );
   }

 /** 
  ***********************************************************************
  * Returns a description of the segment
  ***********************************************************************
  */
 public String getItinDesc()
   {
   if ( GnrcFormat.NotNull(Carrier) )
     return( Carrier + " " + FlightNumber );
   else
     return( "Air Segment " + SegmentNumber );
   }

  /**
   ***********************************************************************
   * Returns true if the action code is contained within the
   * PASSIVE_ACTION_CODES list
   ***********************************************************************
   */
  public boolean isPassive()
    {
    return(isPassive);
    } // end isPassive

  /**
   ***********************************************************************
   * Returns true if the air segment is sold from a block
   ***********************************************************************
   */
  public boolean isBlock()
    {
    if ( GnrcFormat.NotNull(RemoteLocator) && GnrcFormat.NotNull(BlockType) )
      return true;
    else
      return false;
    } // end isBlock

  /**
   ***********************************************************************
   * This method determines whether the segment provided, if it were to precede this
   * this segment, would be considered to be contiguous to it, according to the
   * rules specified below.
   * <p>
   * Two segments are considered to be contiguous if:
   * <ul>
   * <li>the arrival destination of the first segment is the same as the
   * departure destination of the second segment, and </li>
   * <li>the departure date/time of the second segment takes place within N
   * minutes of the arrival date/time of the first segment, where N is the
   * configurable parameter
   * <code>ConfigTranServer.application.getProperty("stopOverThreshold")</code>
   * and has a default value of 360 minutes (6 hours);</li>
   * </ul></p>
   * <p>
   * In the event that only a departure date is provided (and not a departure
   * time), and that an arrival date and time is not provided for the segments,
   * such as is the case when Airware provides a list of segments to sell, the
   * arrival date/time is set to the departure date/time. Under such
   * circumstances, two segments are contiguous if they depart on the same date
   * and their departure/arrival cities match.</p>
   * <p>
   * Note that the order in which the segments are compared is important; for
   * example, the segments MIA/ATL and ATL/SFO are contiguous when compared in
   * that order, but the segments ATL/SFO and MIA/ATL are not; the argument 
   * passed in this method should correspond to the first segment of the
   * sequence.</p>
   ***********************************************************************
   */
  public boolean isContiguousTo(PNRItinAirSegment precedingSegment)
    {
    if (precedingSegment instanceof PNRItinAirSegment)
      {
      long layover;
      long stopOverThreshold;
      // calculate the layover in minutes
      if (precedingSegment.ArrivalDateTime > 0)
        {
        // calculate layover and convert from milliseconds to minutes
        layover = (this.DepartureDateTime - 
                   precedingSegment.ArrivalDateTime) / (60*1000); 

        stopOverThreshold =
          ConfigTranServer.application.getLongProperty("stopOverThreshold",360); // minutes
        }
      else
        {
        layover = (this.DepartureDateTime - 
                   precedingSegment.DepartureDateTime) / (60*1000);

        stopOverThreshold = 24*60; // 24 hours in minutes
        }


      if ( (this.DepartureCityCode.equals(precedingSegment.ArrivalCityCode)) &&
           (0 <= layover) && (layover <= stopOverThreshold) )
        return(true);
      }
    return(false);
    } // end isContiguousTo


 /** 
  ***********************************************************************
  * Returns a description of the segment
  ***********************************************************************
  */
 public String toString()
   {
   return( getItinDesc() );
   }

}
