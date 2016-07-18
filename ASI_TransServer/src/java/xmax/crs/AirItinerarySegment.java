package xmax.crs;


/** 
 ***********************************************************************
 * contains an air segment
 ***********************************************************************
 */
public class AirItinerarySegment
{
  public int    SegmentNumber;
  public String Carrier;
  public String FlightNumber;
  public String InventoryClass;
  public long   DepartureDateTime;
  public long   ArrivalDateTime;
  public String DepartureCityCode;
  public String ArrivalCityCode;
  public String Status;
  public int    NumberOfSeats;
  public String RawData;

  boolean ParseData(String air_segment)
  {
    return true;
  }

  void DummyData()
  {
    SegmentNumber = 1;
    Carrier = "DL";
    FlightNumber = "4711";
    InventoryClass = "Y";
    DepartureDateTime = 21351321;
    ArrivalDateTime = 23154232;
    DepartureCityCode = "SFO";
    ArrivalCityCode = "MCO";
    Status = "HK";
    NumberOfSeats = 1;
    RawData = "DL  4711 Y 04JUN SFO MCO HK ";
  }

  public AirItinerarySegment()
  {
    DummyData();
  }


  public void assignTo(AirItinerarySegment ais)
  {
    SegmentNumber     = ais.SegmentNumber;
    Carrier           = ais.Carrier;
    FlightNumber      = ais.FlightNumber;
    InventoryClass    = ais.InventoryClass;
    DepartureDateTime = ais.DepartureDateTime;
    ArrivalDateTime   = ais.ArrivalDateTime;
    DepartureCityCode = ais.DepartureCityCode;
    ArrivalCityCode   = ais.ArrivalCityCode;
    Status            = ais.Status;
    NumberOfSeats     = ais.NumberOfSeats;
    RawData           = ais.RawData;
  }

  public static void main(String[] args)
  {
    AirItinerarySegment ais = new AirItinerarySegment();
  }

} // AirItinerarySegment class
