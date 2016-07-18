package xmax.TranServer;

import xmax.crs.Flifo.FlightInfo;
import java.io.File;
import xmax.crs.GnrcCrs;
import xmax.util.Log.FileLogger;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqGetFlifo extends ReqTranServer implements Serializable
{
 public String Carrier;
 public int FlightNum;
 public String DepDate;               // in DDMMM format (like CRS uses)
 public String DepCity;
 public String ArrCity;
 public FlightInfo Flight;

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public ReqGetFlifo(final String aCrsCode,   final String aCarrier,
                     final int aFlightNum, final String aDepDate)
    {
    super(aCrsCode);
    Carrier   = aCarrier;
    FlightNum = aFlightNum;
    DepDate   = aDepDate;
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Getting Flifo data for " + Carrier + " " + FlightNum + " departing " + DepDate,null,aCrs.getConnectionName());

    Flight = new FlightInfo(Carrier,FlightNum);

    System.out.println("ReqGetFlifo.runRequest: Carriers/FlightNum/DepDate/DepCity/ArrCity/Flight: "
      + Carrier + "/" + FlightNum + "/" + DepDate + "/" + DepCity + "/" + ArrCity + "/" + Flight);

    if ( (DepCity != null) && (ArrCity != null) )
      aCrs.GetFlightInfo(Carrier,FlightNum,DepDate,DepCity,ArrCity,Flight);
    else if ( (DepCity == null) && (ArrCity != null) )
      aCrs.GetFlightInfo(Carrier,FlightNum,DepDate,DepCity,ArrCity,Flight);
    else if ( (DepCity != null) && (ArrCity == null) )
      aCrs.GetFlightInfo(Carrier,FlightNum,DepDate,DepCity,ArrCity,Flight);
    else
      aCrs.GetFlightInfo(Carrier,FlightNum,DepDate,Flight);
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

    sLogName.append("\\Flifo\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for Flifo.  Crs Code is null");

    if ( GnrcFormat.IsNull(Carrier) )
      throw new TranServerException("Cannot open log file for Flifo.  Carrier is null");

    if ( FlightNum == 0 )
      throw new TranServerException("Cannot open log file for Flifo.  Flight number is null");

    if ( GnrcFormat.IsNull(DepDate) )
      throw new TranServerException("Cannot open log file for Flifo.  Departure date is null");

    sLogName.append(getCrsCode() + Carrier + FlightNum + "-" + DepDate + ".log");

    return( sLogName.toString() );
    }

}