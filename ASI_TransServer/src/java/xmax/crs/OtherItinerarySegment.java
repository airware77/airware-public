package xmax.crs;

/** 
 ***********************************************************************
 * contains an Other segment
 ***********************************************************************
 */
public class OtherItinerarySegment
{
  public int    SegmentNumber;
  public String ServiceType;
  public String ServiceProvider;
  public String Status;
  public int    NumberOfSeats;
  public String City;
  public long   OrigDate;
  public String BookingData;
  public String RawData;

  boolean ParseData(String other_segment)
  {
    return true;
  }

  void DummyData()
  {
    SegmentNumber = 2;
    ServiceType = "INS";
    ServiceProvider = "BX";
    Status = "GK";
    NumberOfSeats = 1;
    City = "SFO";
    OrigDate = 23135411;
    BookingData = "INSURANCE FOR ROCK CLIMBING";
    RawData = "INS BX 24NOV W GK1  SFO/CANCELLATION INSURANCE";
  }

  public OtherItinerarySegment()
  {
    DummyData();
  }


  public void assignTo(OtherItinerarySegment ois)
  {
    SegmentNumber   = ois.SegmentNumber;
    ServiceType     = ois.ServiceType;
    ServiceProvider = ois.ServiceProvider;
    Status          = ois.Status;
    NumberOfSeats   = ois.NumberOfSeats;
    City            = ois.City;
    OrigDate        = ois.OrigDate;
    BookingData     = ois.BookingData;
    RawData         = ois.RawData;
  }

} // OtherItinerarySegment class

