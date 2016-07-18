package xmax.crs.Availability;

import java.util.Vector;
import xmax.crs.Flifo.FlightInfo;
import xmax.TranServer.GnrcFormat;
import java.io.Serializable;

public class FlightAvailability implements Serializable
{
 public int     LineNum;
 public String  Carrier;
 public int     FlightNum;
 public String  DepCity;
 public String  ArrCity;
 public long    DepDate;
 public long    ArrDate;
 public String  Equipment;
 public String  Meal;
 public int     NumStops;
 public boolean isCharter;
 public String  SharedCarrCode;
 public String  SharedCarrDesc;
 public String  SharedCarrFlight;
 public boolean hasSharedCarr;
 public String  EquipChangeCity;
 public String  EquipChangeCode;
 public boolean hasEquipChange;
 /** 
  * This flag being set to <code>true</code> means that the departure city 
  * is not explicitly listed, but is implied from the previous line, and hence
  * this segment is a continuation.
  */
 public boolean hasImpliedDepCity;      
 /** 
  * This flag is used to indicate that this segment should be flifoed 
  * when doing availability.
  */
 public boolean needsFlifo;             
 private Vector ClassList;

 /** 
  ***********************************************************************
  * Sets any unfilled fields with info from flight
  ***********************************************************************
  */
 public void setFlightInfo(final FlightInfo aFlightInfo)
   {
   if ( aFlightInfo instanceof FlightInfo )
     {
     // code share info
     hasSharedCarr = aFlightInfo.isCodeShare(DepCity,ArrCity);

     if ( GnrcFormat.IsNull(SharedCarrCode) )
       SharedCarrCode = aFlightInfo.getCodeShareCarrierCode(DepCity,ArrCity);

     if ( GnrcFormat.IsNull(SharedCarrDesc) )
       SharedCarrDesc = aFlightInfo.getCodeShareCarrierName(DepCity,ArrCity);

     if ( GnrcFormat.IsNull(SharedCarrFlight) )
       SharedCarrFlight = aFlightInfo.getCodeShareFlight(DepCity,ArrCity);

     // equipment, meals
     if ( GnrcFormat.IsNull(Equipment) )
       Equipment = aFlightInfo.getEquipment(DepCity,ArrCity);

     if ( GnrcFormat.IsNull(Meal) )
       Meal = aFlightInfo.getMeal(DepCity,ArrCity);

     // change of gauge city and equipment
     hasEquipChange = aFlightInfo.isChangeOfGauge(DepCity,ArrCity);

     if ( GnrcFormat.IsNull(EquipChangeCity) )
       EquipChangeCity = aFlightInfo.getChangeOfGaugeCity(DepCity,ArrCity);

     if ( GnrcFormat.IsNull(EquipChangeCode) )
       EquipChangeCode = aFlightInfo.getChangeOfGaugeEquipment(DepCity,ArrCity);

     // number of stops, elapsed minutes, and air miles
     NumStops = aFlightInfo.getNumStops(DepCity,ArrCity);

     // departure times
     if ( DepDate == 0 )
       DepDate = aFlightInfo.getDepSchedDate(DepCity);

     // arrival times
     if ( ArrDate == 0 )
       ArrDate = aFlightInfo.getArrSchedDate(ArrCity);
     }
   }

  /** 
   ***********************************************************************
   * Availability for a single flight segment in the form Cnn
   * ie: F7  M4  Q12  Y8  FN2, if the last character is not a digit,
   * then a zero is assumed
   ***********************************************************************
   */
  public void addAvailability(final String aInputString)
    {
    try
      {
      final String sDef = aInputString.trim();
      final int iLength = sDef.length();

      final String sClassName;
      final int iNumSeats;
      if ( iLength > 1 )
        {
        sClassName = sDef.substring(0,iLength - 1);
        final String sNumSeats  = sDef.substring(iLength - 1);
        if ( Character.isDigit( sNumSeats.charAt(0) ) )
          iNumSeats = Integer.parseInt(sNumSeats);
        else
          iNumSeats = 0;
        }
      else
        {
        sClassName = sDef;
        iNumSeats  = 0;
        }

      addAvailability(sClassName,iNumSeats);
      }
    catch (Exception e)
      {}
    }

  /** 
   ***********************************************************************
   * Availability for a single flight segment
   ***********************************************************************
   */
  public void addAvailability(final String aInvClass, final int aNumSeats)
    {
    InvClassAvailability finv = getInvClassAvailability(aInvClass);

    if ( finv instanceof InvClassAvailability )
      finv.incNumSeats(aNumSeats);
    else
      {
      if ( (ClassList instanceof Vector) == false )
        ClassList = new Vector();

      finv = new InvClassAvailability(Carrier,aInvClass,aNumSeats);
      ClassList.add(finv);
      }
    }

  /** 
   ***********************************************************************
   * Availability for a single flight segment
   ***********************************************************************
   */
  public InvClassAvailability getInvClassAvailability(final String aInvClass)
    {
    if ( ClassList instanceof Vector )
      {
      InvClassAvailability finv;
      for ( int i = 0; i < ClassList.size(); i++ )
        {
        finv = (InvClassAvailability )ClassList.elementAt(i);
        if ( finv instanceof InvClassAvailability )
          {
          if ( finv.getInvClass().equals(aInvClass) )
            return(finv);
          }
        }
      }

    return(null);
    }

  /** 
   ***********************************************************************
   * Availability for a single flight segment
   ***********************************************************************
   */
  public int getInvClassNumSeats(final String aInvClass)
    {
    final InvClassAvailability finv = getInvClassAvailability(aInvClass);

    if ( finv instanceof InvClassAvailability )
      return( finv.getNumSeats() );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Availability for a single flight segment
   ***********************************************************************
   */
  public InvClassAvailability[] getInvClassAvailability()
    {
    if ( ClassList instanceof Vector )
      {
      if ( ClassList.size() > 0 )
        {
        final InvClassAvailability[] ClassArray = new InvClassAvailability[ ClassList.size() ];
        ClassList.toArray(ClassArray);
        return(ClassArray);
        }
      }

    return(null);
    }

}
