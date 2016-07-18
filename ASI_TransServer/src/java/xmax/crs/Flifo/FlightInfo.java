package xmax.crs.Flifo;

import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.util.RegExpMatch;
import xmax.util.Log.AppLog;
import xmax.TranServer.GnrcFormat;

import java.util.Vector;
import java.util.Date;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import java.io.Serializable;

/**
 ***********************************************************************
 * This class provides a convenient structure in which to store information 
 * for the different segments of a flight; this class is used when when
 * performing Flight Information requests from a Computer Reservation System
 * (CRS); it is not used for storing flight information from a Passenger Name
 * Record (PNR).
 * @see FlightSegment
 ***********************************************************************
 */
public class FlightInfo implements Serializable
{
 /** Computer Reservation System (CRS) host code where flight data originated */
 public  String sCrsCode;

 /** marketing airline */
 private String sCarrier;

 private int FlightNum;

 /** raw data return from CRS host for basic flight schedule info */
 public  String FlightSchedResponse;       

 /** raw data returned from CRS host for day of flifo */
 public  String DayOfFlifoResponse;        

 /** 
  * A single flight may contain multiple segments, where each segment is made
  * up by a Departure City - Arrival City Pair; this array stores each of the
  * individual segment of this flight 
  */
 private FlightSegment[] Segments;        

 /** plain vanilla empty constructor */
 public FlightInfo() {}

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public FlightInfo(final String aCarrier, final int aFlightNum)
    {
    sCarrier  = aCarrier;
    FlightNum = aFlightNum;
    }

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public FlightInfo(final FlightSegment[] aFlightList) throws Exception
    {
    setFlightSegments(aFlightList);
    }

  public void setCarrier(String aCarrier) {sCarrier = aCarrier;}
  public void setFlightNum(int flightNum) {FlightNum = flightNum;}
  public void setFlightNum(String flightNum) {FlightNum =
    Integer.parseInt(flightNum);}

  public void setFlightSegments(final FlightSegment[] aFlightList) throws Exception
    {
    if ( aFlightList instanceof FlightSegment[] )
      {
      Segments   = aFlightList;

      if ( Segments.length > 0 )
        {
        sCarrier   = Segments[0].Carrier;
        FlightNum = Segments[0].FlightNum;
        }

      for ( int i = 0; i < Segments.length; i++ )
        {
        if ( Segments[i].Carrier.equals(sCarrier) == false )
          throw new Exception("All Carriers for a given flight must be the same");

        if ( Segments[i].FlightNum != FlightNum )
          throw new Exception("All flight numbers for a given flight must be the same");
        }
      }
    }


  public void addFlightSegment(final FlightSegment aFlight)
    {
    // create a vector of existing flight segment objects
    final Vector FlightVector = new Vector();
    if ( Segments instanceof FlightSegment[] )
      {
      for ( int i = 0; i < Segments.length; i++ )
        FlightVector.add( Segments[i] );
      }

    // add the new flight segment
    FlightVector.add( aFlight );

    // allocate an array large enough to hold all the segments
    Segments = new FlightSegment[ FlightVector.size() ];
    FlightVector.toArray(Segments);
    }

 /** set Estimated Arrival time */
 public void setArrEstDateTime(final String aCity, final String aArrTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning(
         "Unable to set estimated arrival time. " +
         "Could not find flight segment arriving into " + aCity);
     return;
     }

   if ( fseg.ArrSchedDateTime == 0 )
     {
     AppLog.LogWarning(
         "Unable to set estimated arrival time. " +
         "Scheduled arrival time to " + aCity + " not set");
     return;
     }

   fseg.ArrEstDateTime = getClosestDateTime(fseg.ArrSchedDateTime,aArrTime);
   }

 /** set Estimated Arrival time */
 public void setArrEstDateTime(final String aCity, final String aArrDate, final String aArrTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set estimated arrival time.  Could not find flight segment arriving into " + aCity);
     return;
     }

   fseg.ArrEstDateTime = GnrcParser.ScanCRSDateTimeString(aArrDate,aArrTime);
   }

 /** 
  ***********************************************************************
  * set Field On Arrival time
  ***********************************************************************
  */
 public void setArrFieldOnDateTime(final String aCity, final String aArrTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set field on arrival time.  Could not find flight segment arriving into " + aCity);
     return;
     }

   if ( fseg.ArrSchedDateTime == 0 )
     {
     AppLog.LogWarning("Unable to set field on arrival time.  Scheduled arrival time not set");
     return;
     }

   fseg.ArrFieldOnDateTime = getClosestDateTime(fseg.ArrSchedDateTime,aArrTime);
   }

 /** 
  ***********************************************************************
  * set Field On Arrival time
  ***********************************************************************
  */
 public void setArrFieldOnDateTime(final String aCity, final String aArrDate, final String aArrTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set field on arrival time.  Could not find flight segment arriving into " + aCity);
     return;
     }

   fseg.ArrFieldOnDateTime = GnrcParser.ScanCRSDateTimeString(aArrDate,aArrTime);
   }

 /** 
  ***********************************************************************
  * set Gate In Arrival time
  ***********************************************************************
  */
 public void setArrGateInDateTime(final String aCity, final String aArrTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set gate in arrival time.  Could not find flight segment arriving into " + aCity);
     return;
     }

   if ( fseg.ArrSchedDateTime == 0 )
     {
     AppLog.LogWarning("Unable to set gate in arrival time.  Scheduled arrival time not set");
     return;
     }

   fseg.ArrGateInDateTime = getClosestDateTime(fseg.ArrSchedDateTime,aArrTime);
   }

 public void setArrGateInDateTime(final String aCity, final String aArrDate, final String aArrTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set gate in arrival time.  Could not find flight segment arriving into " + aCity);
     return;
     }

   fseg.ArrGateInDateTime = GnrcParser.ScanCRSDateTimeString(aArrDate,aArrTime);
   }

 /** 
  ***********************************************************************
  * set Estimated Departure time
  ***********************************************************************
  */
 public void setDepEstDateTime(final String aCity, final String aDepTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set estimated departure time.  Could not find flight segment departing from " + aCity);
     return;
     }

   if ( fseg.DepSchedDateTime == 0 )
     {
     AppLog.LogWarning("Unable to set estimated departure time.  Scheduled departure time not set");
     return;
     }

   fseg.DepEstDateTime = getClosestDateTime(fseg.DepSchedDateTime,aDepTime);
   }

 public void setDepEstDateTime(final String aCity, final String aDepDate, final String aDepTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set estimated departure time.  Could not find flight segment departing from " + aCity);
     return;
     }

   fseg.DepEstDateTime = GnrcParser.ScanCRSDateTimeString(aDepDate,aDepTime);
   }

 /** 
  ***********************************************************************
  * set Gate Out Departure time
  ***********************************************************************
  */
 public void setDepGateOutDateTime(final String aCity, final String aDepTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set gate out departure time.  Could not find flight segment departing from " + aCity);
     return;
     }

   if ( fseg.DepSchedDateTime == 0 )
     {
     AppLog.LogWarning("Unable to set gate out departure time.  Scheduled departure time not set");
     return;
     }

   fseg.DepGateOutDateTime = getClosestDateTime(fseg.DepSchedDateTime,aDepTime);
   }

 public void setDepGateOutDateTime(final String aCity, final String aDepDate, final String aDepTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set gate out departure time.  Could not find flight segment departing from " + aCity);
     return;
     }

   fseg.DepGateOutDateTime = GnrcParser.ScanCRSDateTimeString(aDepDate,aDepTime);
   }

 /** 
  ***********************************************************************
  * set Field Off Departure time - this represents the time at which the plane
  * left the airfield
  ***********************************************************************
  */
 public void setDepFieldOffDateTime(final String aCity, final String aDepTime)  
   throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning(
         "Unable to set field off departure time. " +
         "Could not find flight segment departing from " + aCity);
     return;
     }

   if ( fseg.DepSchedDateTime == 0 )
     {
     AppLog.LogWarning(
         "Unable to set field off departure time. " +
         "Scheduled departure time from " + aCity + " not set.");
     return;
     }

   fseg.DepFieldOffDateTime = getClosestDateTime(fseg.DepSchedDateTime,aDepTime);
   }

 /** 
  ***********************************************************************
  * set Field Off Departure time - this represents the time at which the plane
  * left the airfield
  ***********************************************************************
  */
 public void setDepFieldOffDateTime(final String aCity, final String aDepDate, final String aDepTime) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning(
         "Unable to set field off departure time. " +
         "Could not find flight segment departing from " + aCity);
     return;
     }

   fseg.DepFieldOffDateTime = GnrcParser.ScanCRSDateTimeString(aDepDate,aDepTime);
   }

 /** 
  ***********************************************************************
  * set arrival gate
  ***********************************************************************
  */
 public void setArrGate(final String aCity, final String aArrGate) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set arrival gate.  Could not find flight segment arriving into " + aCity);
     return;
     }

   if ( aArrGate instanceof String )
     fseg.ArrGate = aArrGate.trim();
   }

 /** 
  ***********************************************************************
  * set departure gate
  ***********************************************************************
  */
 public void setDepGate(final String aCity, final String aDepGate) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set departure gate.  Could not find flight segment departing from " + aCity);
     return;
     }

   if ( aDepGate instanceof String )
     fseg.DepGate = aDepGate.trim();
   }

 /** 
  ***********************************************************************
  * set arrival terminal
  ***********************************************************************
  */
 public void setArrTerminal(final String aCity, final String aArrTerminal) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentToCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set arrival terminal.  Could not find flight segment arriving into " + aCity);
     return;
     }

   if ( aArrTerminal instanceof String )
     fseg.ArrTerminal = aArrTerminal.trim();
   }

 /** 
  ***********************************************************************
  * set departure terminal
  ***********************************************************************
  */
 public void setDepTerminal(final String aCity, final String aDepTerminal) throws Exception
   {
   final FlightSegment fseg = getFlightSegmentFromCity(aCity);
   if ( (fseg instanceof FlightSegment) == false )
     {
     AppLog.LogWarning("Unable to set departure terminal.  Could not find flight segment departing from " + aCity);
     return;
     }

   if ( aDepTerminal instanceof String )
     fseg.DepTerminal = aDepTerminal.trim();
   }

  /** 
   ***********************************************************************
   * Set Code share flight number
   ***********************************************************************
   */
 public void setCodeShareFlight(final String aCodeShareFlightNum)
   {
   setCodeShareFlight(aCodeShareFlightNum,null,null);
   }

 public void setCodeShareFlight(final String aCodeShareFlightNum, final String aDepCity, final String aArrCity)
   {
   if ( Segments instanceof FlightSegment[] )
     {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        Segments[i].CodeShareFlightNum = aCodeShareFlightNum;
        Segments[i].CodeShare = true;
        }
     }
   }

  /** 
   ***********************************************************************
   * Set Code share carrier code
   ***********************************************************************
   */
 public void setCodeShareCarrierCode(final String aCodeShareCarrierCode)
   {
   setCodeShareCarrierCode(aCodeShareCarrierCode,null,null);
   }

 public void setCodeShareCarrierCode(final String aCodeShareCarrierCode, final String aDepCity, final String aArrCity)
   {
   if ( Segments instanceof FlightSegment[] )
     {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        Segments[i].CodeShareCarrierCode = aCodeShareCarrierCode;
        Segments[i].CodeShare = true;
        }
     }
   }

  /** 
   ***********************************************************************
   * Set Code share carrier name
   ***********************************************************************
   */
 public void setCodeShareCarrierName(final String aCodeShareCarrierName)
   {
   setCodeShareCarrierName(aCodeShareCarrierName,null,null);
   }

 public void setCodeShareCarrierName(final String aCodeShareCarrierName, final String aDepCity, final String aArrCity)
   {
   if ( Segments instanceof FlightSegment[] )
     {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        Segments[i].CodeShareCarrierName = aCodeShareCarrierName;
        Segments[i].CodeShare = true;
        }
     }
   }

  /** 
   ***********************************************************************
   * Access Functions
   ***********************************************************************
   */
  public String getCarrier()    { return(sCarrier);   }
  public int getFlightNum()     { return(FlightNum); }

  /** 
   ***********************************************************************
   * Return first departure city
   ***********************************************************************
   */
  public String getDepCity()
    {
    if ( Segments instanceof FlightSegment[] )
      {
      if ( Segments.length > 0 )
        return( Segments[0].DepartCity );
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return last arrival city
   ***********************************************************************
   */
  public String getArrCity()
    {
    if ( Segments instanceof FlightSegment[] )
      {
      if ( Segments.length > 0 )
        return( Segments[Segments.length - 1].ArriveCity );
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the departure date
   ***********************************************************************
   */
  public long getDepSchedDate()
    {
    return( getDepSchedDate(null) );
    }


  public long getDepSchedDate(final String aDepCity)
    {
    final int iSegNum = getDepSegmentNum(aDepCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].DepSchedDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the estimated departure date
   ***********************************************************************
   */
  public long getDepEstDate()
    {
    return( getDepEstDate(null) );
    }


  public long getDepEstDate(final String aDepCity)
    {
    final int iSegNum = getDepSegmentNum(aDepCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].DepEstDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the off field departure date
   ***********************************************************************
   */
  public long getDepOffFieldDate()
    {
    return( getDepOffFieldDate(null) );
    }


  public long getDepOffFieldDate(final String aDepCity)
    {
    final int iSegNum = getDepSegmentNum(aDepCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].DepFieldOffDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the gate out departure date
   ***********************************************************************
   */
  public long getDepOutGateDate()
    {
    return( getDepOutGateDate(null) );
    }


  public long getDepOutGateDate(final String aDepCity)
    {
    final int iSegNum = getDepSegmentNum(aDepCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].DepGateOutDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the arrival date
   ***********************************************************************
   */
  public long getArrSchedDate()
    {
    return( getArrSchedDate(null) );
    }


  public long getArrSchedDate(final String aArrCity)
    {
    final int iSegNum = getArrSegmentNum(aArrCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].ArrSchedDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the estimated arrival date
   ***********************************************************************
   */
  public long getArrEstDate()
    {
    return( getArrEstDate(null) );
    }


  public long getArrEstDate(final String aArrCity)
    {
    final int iSegNum = getArrSegmentNum(aArrCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].ArrEstDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the on field arrival date
   ***********************************************************************
   */
  public long getArrOnFieldDate()
    {
    return( getArrOnFieldDate(null) );
    }


  public long getArrOnFieldDate(final String aArrCity)
    {
    final int iSegNum = getArrSegmentNum(aArrCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].ArrFieldOnDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the in gate arrival date
   ***********************************************************************
   */
  public long getArrInGateDate()
    {
    return( getArrInGateDate(null) );
    }


  public long getArrInGateDate(final String aArrCity)
    {
    final int iSegNum = getArrSegmentNum(aArrCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].ArrGateInDateTime );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the number of minutes elapsed between the two city pairs
   ***********************************************************************
   */
  public int getElapsedMinutes()
    {
    return( getElapsedMinutes(null,null) );
    }


  public int getElapsedMinutes(final String aDepCity, final String aArrCity)
    {
    int iTotalMinutes = 0;

    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        iTotalMinutes += Segments[i].AirMinutes;
        if ( i < iStop )
          iTotalMinutes += getSchedGroundMinutes(Segments[i].ArriveCity);
        }
      }

    return(iTotalMinutes);
    }

  /** 
   ***********************************************************************
   * Return the number of stops
   ***********************************************************************
   */
  public int getNumStops()
    {
    if ( Segments instanceof FlightSegment[] )
      return( Segments.length - 1 );
    else
      return(0);
    }


  public int getNumStops(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      if ( (iStart < 0) || (iStop < 0) )
        return(0);

      final int iNumStops = iStop - iStart;
      if ( iNumStops >= 0 )
        return(iNumStops);
      else
        return(0);
      }

    return(0);
    }

  /** 
   ***********************************************************************
   * Return the number of segments
   ***********************************************************************
   */
  public int getNumSegments()
    {
    if ( Segments instanceof FlightSegment[] )
      return( Segments.length );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Return the meal service
   ***********************************************************************
   */
  public String getMeal()
    {
    return( getMeal(null,null) );
    }


  public String getMeal(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].MealCode instanceof String )
          return(Segments[i].MealCode);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the delay code
   ***********************************************************************
   */
  public String getDelayCode()
    {
    return( getDelayCode(null,null) );
    }


  public String getDelayCode(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].DelayCode instanceof String )
          return(Segments[i].DelayCode);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the equipment code
   ***********************************************************************
   */
  public String getEquipment()
    {
    return( getEquipment(null,null) );
    }

  public String getEquipment(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].EquipmentCode instanceof String )
          return(Segments[i].EquipmentCode);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the on time performance
   ***********************************************************************
   */
  public String getOnTimePerformance()
    {
    return( getOnTimePerformance(null,null) );
    }


  public String getOnTimePerformance(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].OnTimePerformance instanceof String )
          return(Segments[i].OnTimePerformance);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the operating carrier full name
   ***********************************************************************
   */
  public String getCodeShareCarrierName()
    {
    return( getCodeShareCarrierName(null,null) );
    }

    
  public String getCodeShareCarrierName(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].CodeShareCarrierName instanceof String )
          return(Segments[i].CodeShareCarrierName);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the operating carrier code
   ***********************************************************************
   */
  public String getCodeShareCarrierCode()
    {
    return( getCodeShareCarrierCode(null,null) );
    }


  public String getCodeShareCarrierCode(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].CodeShareCarrierCode instanceof String )
          return(Segments[i].CodeShareCarrierCode);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the operating flight
   ***********************************************************************
   */
  public String getCodeShareFlight()
    {
    return( getCodeShareFlight(null,null) );
    }


  public String getCodeShareFlight(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].CodeShareFlightNum instanceof String )
          return(Segments[i].CodeShareFlightNum);
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Returns true if any segment is code shared
   ***********************************************************************
   */
  public boolean isCodeShare()
    {
    return( isCodeShare(null,null) );
    }


  public boolean isCodeShare(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        // check if any of the code share fields have data in them
        if ( Segments[i].CodeShare )
          return(true);

        if ( GnrcFormat.NotNull(Segments[i].CodeShareFlightNum) )
          return(true);

        if ( GnrcFormat.NotNull(Segments[i].CodeShareCarrierName)  )
          return(true);

        if ( GnrcFormat.NotNull(Segments[i].CodeShareCarrierCode) )
          return(true);
        }

      }

    return(false);
    }

  /** 
   ***********************************************************************
   * Return the air miles
   ***********************************************************************
   */
  public int getAirMiles()
    {
    return( getAirMiles(null,null) );
    }


  public int getAirMiles(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      int iTotalMiles = 0;
      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        iTotalMiles += Segments[i].AirMiles;

      return(iTotalMiles);
      }

    return(0);
    }

  /**
   ***********************************************************************
   * Return the air miles
   ***********************************************************************
   */
  public boolean isInternational()
    {
    return( isInternational(null,null) );
    }


  public boolean isInternational(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        try
          {
          if ( Segments[i].isInternational() )
            return(true);
          }
        catch (Exception e)
          {
          }
        }
      }

    return(false);
    }

  /**
   ***********************************************************************
   * Return the time in the air
   ***********************************************************************
   */
  public int getAirMinutes()
    {
    return( getAirMinutes(null,null) );
    }

    
  public int getAirMinutes(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      int iTotalMinutes = 0;
      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        iTotalMinutes += Segments[i].AirMinutes;

      return(iTotalMinutes);
      }

    return(0);
    }

  /** 
   ***********************************************************************
   * Return the Change of Gauge city
   ***********************************************************************
   */
  public String getChangeOfGaugeCity()
    {
    return( getChangeOfGaugeCity(null,null) );
    }


  public String getChangeOfGaugeCity(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      if ( iStart < 0 )
        return("");

      if ( Segments[iStart].EquipmentCode instanceof String )
        {
        final String sEquip = Segments[iStart].EquipmentCode;
        for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
          {
          if ( Segments[i].EquipmentCode instanceof String )
            {
            if ( Segments[i].EquipmentCode.equals(sEquip) == false )
              return(Segments[i].DepartCity);
            }
          }
        }
      }

    return("");
    }

  /** 
   ***********************************************************************
   * Return the Change of Gauge equipment
   ***********************************************************************
   */
  public String getChangeOfGaugeEquipment()
    {
    return( getChangeOfGaugeEquipment(null,null) );
    }


  public String getChangeOfGaugeEquipment(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      if ( iStart < 0 )
        return("");

      if ( Segments[iStart].EquipmentCode instanceof String )
        {
        final String sEquip = Segments[iStart].EquipmentCode;
        for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
          {
          if ( Segments[i].EquipmentCode instanceof String )
            {
            if ( Segments[i].EquipmentCode.equals(sEquip) == false )
              return( Segments[i].EquipmentCode.substring(0,3) );
            }
          }
        }
      }

    return("");
    }



  public boolean isChangeOfGauge()
    {
    return( isChangeOfGauge(null,null) );
    }

  public boolean isChangeOfGauge(final String aDepCity, final String aArrCity)
    {
    final String sChangeEquip = getChangeOfGaugeEquipment(aDepCity,aArrCity);
    if ( GnrcFormat.NotNull(sChangeEquip) )
      return(true);
    else
      return(false);
    }

  /** 
   ***********************************************************************
   * Return the departure terminal
   ***********************************************************************
   */
  public String getDepTerm()
    {
    return( getDepTerm(null) );
    }

  public String getDepTerm(final String aDepCity)
    {
    final int iSegNum = getDepSegmentNum(aDepCity);
    if ( iSegNum >= 0 )
      return( Segments[iSegNum].DepTerminal );
    else
      return("");
    }

  /** 
   ***********************************************************************
   * Return the departure gate
   ***********************************************************************
   */
  public String getDepGate()
    {
    return( getDepGate(null) );
    }

  public String getDepGate(final String aDepCity)
    {
    final int iSegNum = getDepSegmentNum(aDepCity);
    if ( iSegNum >= 0 )
      return(Segments[iSegNum].DepGate);
    else
      return("");
    }

  /** 
   ***********************************************************************
   * Return the arrival terminal
   ***********************************************************************
   */
  public String getArrTerm()
    {
    return( getArrTerm(null) );
    }

    
  public String getArrTerm(final String aArrCity)
    {
    final int iSegNum = getArrSegmentNum(aArrCity);
    if ( iSegNum >= 0 )
      return(Segments[iSegNum].ArrTerminal);
    else
      return("");
    }

  /** 
   ***********************************************************************
   * Return the arrival gate
   ***********************************************************************
   */
  public String getArrGate()
    {
    return( getArrGate(null) );
    }

  public String getArrGate(final String aArrCity)
    {
    final int iSegNum = getArrSegmentNum(aArrCity);
    if ( iSegNum >= 0 )
      return(Segments[iSegNum].ArrGate);
    else
      return("");
    }

  /** 
   ***********************************************************************
   * Mark the flight as canceled
   ***********************************************************************
   */
  public void setCanceled(final boolean aCanceled)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      for ( int i = 0; i < Segments.length; i ++ )
        Segments[i].Canceled = aCanceled;
      }
    }

  public void setSingleSegCanceled(final String aDepCity, final boolean aCanceled)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      if ( iStart >= 0 )
        Segments[iStart].Canceled = aCanceled;
      }
    }

  public void setRemainingSegsCanceled(final String aDepCity, final boolean aCanceled)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);

      for ( int i = iStart; (i >= 0) && (i < Segments.length); i++ )
        Segments[i].Canceled = aCanceled;
      }
    }

  public void setCanceled(final String aDepCity, final String aArrCity, final boolean aCanceled)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        Segments[i].Canceled = aCanceled;
      }
    }

  /** 
   ***********************************************************************
   * Returns true if any segment between the two cities is canceled
   ***********************************************************************
   */
  public boolean isCanceled(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      final int iStart = getDepSegmentNum(aDepCity);
      final int iStop  = getArrSegmentNum(aArrCity);

      for ( int i = iStart; (i >= 0) && (i <= iStop); i++ )
        {
        if ( Segments[i].Canceled )
          return(true);
        }
      }
    else
      {
      try
        {
        final String CANCEL_PHRASES = "(NOOP|NO-OP|CANCEL|FLT CNCLD|FLIGHT NOT OPERATIONAL)";

        if ( FlightSchedResponse instanceof String )
          {
          if ( RegExpMatch.matches(FlightSchedResponse,CANCEL_PHRASES) )
            return(true);
          }

        if ( DayOfFlifoResponse instanceof String )
          {
          if ( RegExpMatch.matches(DayOfFlifoResponse,CANCEL_PHRASES) )
            return(true);
          }
        }
      catch (Exception e)
        {}
      }

    return(false);
    }

  public boolean isCanceled()
    {
    if ( Segments instanceof FlightSegment[] )
      {
      for ( int i = 0; i < Segments.length; i++ )
        {
        if ( Segments[i].Canceled )
          return(true);
        }
      }
    else
      {
      try
        {
        final String CANCEL_PHRASES = "(NOOP|NO-OP|CANCEL|FLT CNCLD|FLIGHT NOT OPERATIONAL)";

        if ( FlightSchedResponse instanceof String )
          {
          if ( RegExpMatch.matches(FlightSchedResponse,CANCEL_PHRASES) )
            return(true);
          }

        if ( DayOfFlifoResponse instanceof String )
          {
          if ( RegExpMatch.matches(DayOfFlifoResponse,CANCEL_PHRASES) )
            return(true);
          }
        }
      catch (Exception e)
        {}
      }

    return(false);
    }

  /** 
   ***********************************************************************
   * Return the Flight Segment object
   ***********************************************************************
   */
  public boolean hasDepCity(final String aDepCity)
    {
    final FlightSegment fseg = getFlightSegmentFromCity(aDepCity);
    return( fseg instanceof FlightSegment );
    }

  public boolean hasArrCity(final String aArrCity)
    {
    final FlightSegment fseg = getFlightSegmentToCity(aArrCity);
    return( fseg instanceof FlightSegment );
    }


  public FlightSegment getFlightSegmentFromCity(final String aDepCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      for ( int i = 0; i < Segments.length; i++ )
        {
        if ( Segments[i].DepartCity.equals(aDepCity) )
          return( Segments[i] );
        }
      }

    return(null);
    }

  public FlightSegment getFlightSegmentToCity(final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      for ( int i = 0; i < Segments.length; i++ )
        {
        if ( Segments[i].ArriveCity.equals(aArrCity) )
          return( Segments[i] );
        }
      }

    return(null);
    }

  public FlightSegment getFlightSegment(final String aDepCity, final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      for ( int i = 0; i < Segments.length; i++ )
        {
        if ( Segments[i].ArriveCity.equals(aArrCity) && Segments[i].DepartCity.equals(aDepCity) )
          return( Segments[i] );
        }
      }

    return(null);
    }

  public FlightSegment getFlightSegment(final int aSegmentNum)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      if ( aSegmentNum < Segments.length )
        return( Segments[aSegmentNum] );
      }

    return(null);
    }


  public FlightSegment getFirstFlightSegment()
    {
    if ( Segments instanceof FlightSegment[] )
      {
      if ( Segments.length > 0 )
        return( Segments[0] );
      }

    return(null);
    }



  public FlightSegment getLastFlightSegment()
    {
    if ( Segments instanceof FlightSegment[] )
      {
      if ( Segments.length > 0 )
        return( Segments[ Segments.length - 1] );
      }

    return(null);
    }



  public FlightSegment[] getFlightSegments()
    {
    return( Segments );
    }

  /** 
   ***********************************************************************
   * Return the Flight Segment number that departs from this city
   ***********************************************************************
   */
  private int getDepSegmentNum(final String aDepCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      if ( GnrcFormat.NotNull(aDepCity) )
        {
        for ( int i = 0; i < Segments.length; i++ )
          {
          if ( Segments[i].DepartCity.equals(aDepCity) )
            return(i);
          }
        }
      else
        return(0);
      }

    return(-1);
    }

  /** 
   ***********************************************************************
   * Return the Flight Segment number that arrives into this city
   ***********************************************************************
   */
  private int getArrSegmentNum(final String aArrCity)
    {
    if ( Segments instanceof FlightSegment[] )
      {
      if ( GnrcFormat.NotNull(aArrCity) )
        {
        for ( int i = 0; i < Segments.length; i++ )
          {
          if ( Segments[i].ArriveCity.equals(aArrCity) )
            return(i);
          }
        }
      else
        return( Segments.length - 1 );
      }

    return(-1);
    }

  /** 
   ***********************************************************************
   * Return whether or not day of flifo data is available
   ***********************************************************************
   */
  public boolean hasDayOfFlifo()
    {
    if ( DayOfFlifoResponse instanceof String )
      {
      if ( DayOfFlifoResponse.length() > 0 )
        return(true);
      }

    return(false);
    }

  /** 
   ***********************************************************************
   * Return whether or not it's OK to try and do a day of flifo
   ***********************************************************************
   */
  public boolean isOkToFlifo()
    {
    final long MAX_FLIFO_MSECS = 72 * 60 * 60 * 1000;     // can flifo up to 72 hours before or after departure

    final long iTimeTilFlight = Math.abs( timeBeforeFlight() );
    if ( iTimeTilFlight < MAX_FLIFO_MSECS )
      return(true);
    else
      return(false);
    }

  /** 
   ***********************************************************************
   * Return amount of time before flight departs
   ***********************************************************************
   */
  public long timeBeforeFlight()
    {
    final long CurrentTime = System.currentTimeMillis();
    final long DepTime     = getDepSchedDate();

    {
    final SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    String sCurrentDate = date_format.format( new Date(CurrentTime) );
    String sDepDate     = date_format.format( new Date(DepTime) );
    sCurrentDate = "";
    sDepDate     = "";
    }

    return(DepTime - CurrentTime);
    }

  public long timeBeforeFlight(final String aDepCity)
    {
    final long CurrentTime = System.currentTimeMillis();
    final long DepTime     = getDepSchedDate(aDepCity);

    return(DepTime - CurrentTime);
    }

 /** 
  ***********************************************************************
  * Return amount of time on ground in minutes
  ***********************************************************************
  */
 public int getSchedGroundMinutes(final String aCityCode)
   {
   final FlightSegment arrSeg = getFlightSegmentToCity(aCityCode);
   final FlightSegment depSeg = getFlightSegmentFromCity(aCityCode);

   if ( (arrSeg instanceof FlightSegment) && (depSeg instanceof FlightSegment) )
     {
     final int iMin = getElapsedMinutes(arrSeg.ArrSchedDateTime,depSeg.DepSchedDateTime);
     if ( iMin > 0 )
       return(iMin);
     }

   return(0);
   }

 public int getEstGroundMinutes(final String aCityCode)
   {
   final FlightSegment arrSeg = getFlightSegmentToCity(aCityCode);
   final FlightSegment depSeg = getFlightSegmentFromCity(aCityCode);

   if ( (arrSeg instanceof FlightSegment) && (depSeg instanceof FlightSegment) )
     {
     final int iMin = getElapsedMinutes(arrSeg.ArrEstDateTime,depSeg.DepEstDateTime);
     if ( iMin > 0 )
       return(iMin);
     }

   return(0);
   }


 public int getGateGroundMinutes(final String aCityCode)
   {
   final FlightSegment arrSeg = getFlightSegmentToCity(aCityCode);
   final FlightSegment depSeg = getFlightSegmentFromCity(aCityCode);

   if ( (arrSeg instanceof FlightSegment) && (depSeg instanceof FlightSegment) )
     {
     final int iMin = getElapsedMinutes(arrSeg.ArrGateInDateTime,depSeg.DepGateOutDateTime);
     if ( iMin > 0 )
       return(iMin);
     }

   return(0);
   }

 public int getFieldGroundMinutes(final String aCityCode)
   {
   final FlightSegment arrSeg = getFlightSegmentToCity(aCityCode);
   final FlightSegment depSeg = getFlightSegmentFromCity(aCityCode);

   if ( (arrSeg instanceof FlightSegment) && (depSeg instanceof FlightSegment) )
     {
     final int iMin = getElapsedMinutes(arrSeg.ArrFieldOnDateTime,depSeg.DepFieldOffDateTime);
     if ( iMin > 0 )
       return(iMin);
     }

   return(0);
   }

 /** 
  ***********************************************************************
  * Return the number of minutes between starttime and stoptime
  ***********************************************************************
  */
 private int getElapsedMinutes(final long aStartTime, final long aStopTime)
   {
   final long lMinutes = millisecondsToMinutes(aStopTime - aStartTime);
   final int iMinutes = (int )lMinutes;
   return( iMinutes );
   }

 /** 
  ***********************************************************************
  * Converts the given number of milliseconds into minutes
  ***********************************************************************
  */
 private long millisecondsToMinutes(final long aMilliSeconds)
   {
   return( aMilliSeconds / 60000L );
   }

  /** 
   ***********************************************************************
   * This procedure returns the nearest time compared to the given
   * date time
   ***********************************************************************
   */
  private static long getClosestDateTime(
      final long aScheduledDateTime, final String aActualTime) throws Exception
   {
   if ( aScheduledDateTime == 0 )
     return(0);

   if ( (aActualTime instanceof String) == false )
     return(0);

   final String sScheduledDate = GnrcFormat.FormatCRSDate(aScheduledDateTime);
   
   // strip the given time of any date modifiers
   String sActualTime = "";
   StringTokenizer fields = new StringTokenizer(aActualTime,"|+#- ");   // make sure that only the time component is used
   if ( fields.hasMoreTokens() )
     sActualTime = fields.nextToken();
   else
     return(0);

   final long MSECS_PER_DAY = 24 * 60 * 60 * 1000;    // number of milliseconds in one day

   // get the actual time for today, yesterday, and tomorrow
   long iCurrentDate = GnrcParser.ScanCRSDateTimeString(sScheduledDate,sActualTime);
   
   // this fixes a very bizarre bug that would cause this function to fail
   // after repeated flifos; this bizarre bug is automagically fixed when we
   // attempt to log the value of iCurrentDate to the console or to a file; it
   // is also fixed by the statement below.
   String s = iCurrentDate + "";

   final long iPrevDate    = iCurrentDate - MSECS_PER_DAY;
   final long iNextDate    = iCurrentDate + MSECS_PER_DAY;


   final long iCurrentDiff = Math.abs(iCurrentDate - aScheduledDateTime);
   final long iPrevDiff    = Math.abs(iPrevDate - aScheduledDateTime);
   final long iNextDiff    = Math.abs(iNextDate - aScheduledDateTime);

   // assume the closest time is for today
   long iMinDiff        = iCurrentDiff;
   long iReturnDateTime = iCurrentDate;

   // check if closest time is previous day
   if ( iPrevDiff < iMinDiff )
     {
     iMinDiff        = iPrevDiff;
     iReturnDateTime = iPrevDate;
     }

   // check if closest time is next day
   if ( iNextDiff < iMinDiff )
     {
     iMinDiff        = iNextDiff;
     iReturnDateTime = iNextDate;
     }

   if ( iReturnDateTime == aScheduledDateTime )
     {}

   return(iReturnDateTime);
   }

  /** 
   ***********************************************************************
   * Main function for unit test
   ***********************************************************************
   */
  public static void main(String[] args)
    {
     // create the first flight segment
    FlightSegment[] Flight = new FlightSegment[4];
    FlightInfo fInfo = null;

    try
      {
      Flight[0] = new FlightSegment();

    Flight[0].Carrier             = "DL";
    Flight[0].FlightNum           = 16;

    Flight[0].DepartCity          = "HNL";
    Flight[0].ArriveCity          = "DFW";
    Flight[0].DepSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","1030A");
    Flight[0].ArrSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","1215P");
    Flight[0].DepTerminal      = "1W";
    Flight[0].ArrTerminal      = "1E";
    Flight[0].EquipmentCode       = "L10";
    Flight[0].MealCode            = "BB ";
    Flight[0].AirMiles            = 1001;
    Flight[0].OnTimePerformance   = "8";
    Flight[0].CodeShareCarrierCode = "UA";
    Flight[0].CodeShareFlightNum  = "101";

     // create the second flight segment
      Flight[1] = new FlightSegment();

    Flight[1].Carrier             = "DL";
    Flight[1].FlightNum           = 16;

    Flight[1].DepartCity          = "DFW";
    Flight[1].ArriveCity          = "ATL";
    Flight[1].DepSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","1245P");
    Flight[1].ArrSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","120P");
    Flight[1].DepTerminal      = "1E";
    Flight[1].ArrTerminal      = "6N";
    Flight[1].EquipmentCode       = "L10";
    Flight[1].MealCode            = "LLL";
    Flight[1].AirMiles            = 1002;
    Flight[1].OnTimePerformance   = "9";
    Flight[1].CodeShareCarrierCode = "DL";
    Flight[1].CodeShareFlightNum  = "16";


     // create the third flight segment
      Flight[2] = new FlightSegment();

    Flight[2].Carrier             = "DL";
    Flight[2].FlightNum           = 16;

    Flight[2].DepartCity          = "ATL";
    Flight[2].ArriveCity          = "NYC";
    Flight[2].DepSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","240P");
    Flight[2].ArrSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","430P");
    Flight[2].DepTerminal      = "6N";
    Flight[2].ArrTerminal      = "EAST";
    Flight[2].EquipmentCode       = "727";
    Flight[2].MealCode            = "SS";
    Flight[2].AirMiles            = 1003;
    Flight[2].OnTimePerformance   = "6";
    Flight[2].CodeShareCarrierCode = "DL";
    Flight[2].CodeShareFlightNum  = "16";


     // create the fourth flight segment
      Flight[3] = new FlightSegment();

    Flight[3].Carrier             = "DL";
    Flight[3].FlightNum           = 16;

    Flight[3].DepartCity          = "NYC";
    Flight[3].ArriveCity          = "BOS";
    Flight[3].DepSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","545P");
    Flight[3].ArrSchedDateTime      = GnrcParser.ScanCRSDateTimeString("10JAN","617P");
    Flight[3].DepTerminal      = "EAST";
    Flight[3].ArrTerminal      = "TERM1";
    Flight[3].EquipmentCode       = "727";
    Flight[3].MealCode            = "DDD";
    Flight[3].AirMiles            = 1004;
    Flight[3].OnTimePerformance   = "9";
    Flight[3].CodeShareCarrierCode = "DL";
    Flight[3].CodeShareFlightNum  = "16";

      fInfo = new FlightInfo(Flight);
      }
    catch (Exception e)
      {}

    SimpleDateFormat CrsDate = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    int iNumStops = fInfo.getNumStops();
    String sOpCarr   = fInfo.getCodeShareCarrierName("HNL","DFW");
    String sOpFlight = fInfo.getCodeShareFlight("HNL","DFW");


    String sChangeCity = fInfo.getChangeOfGaugeCity();
    String sChangeEquip = fInfo.getChangeOfGaugeEquipment();

    sOpCarr = "";
    sOpFlight = "";
    iNumStops = 0;
    sChangeCity = "";
    sChangeEquip = "";
    }

}
