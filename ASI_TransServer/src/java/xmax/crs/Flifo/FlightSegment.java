
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.crs.Flifo;

import java.io.Serializable;

public class FlightSegment implements Serializable
{
 public String  Carrier;
 public int     FlightNum;
 public String  DepartCity;
 public String  ArriveCity;
 public long    DepSchedDateTime;
 public long    DepEstDateTime;
 public long    DepGateOutDateTime;
 public long    DepFieldOffDateTime;
 public long    ArrSchedDateTime;
 public long    ArrEstDateTime;
 public long    ArrGateInDateTime;
 public long    ArrFieldOnDateTime;
 public String  DepTerminal;
 public String  DepGate;
 public String  ArrTerminal;
 public String  ArrGate;
 public String  EquipmentCode;
 public String  MealCode;
 public int     AirMiles;
 public int     AirMinutes;
 public String  OnTimePerformance;
 public String  CodeShareCarrierName;
 public String  CodeShareCarrierCode;
 public String  CodeShareFlightNum;
 public boolean CodeShare;
 public String  DelayCode;
 public String  Status;
 public boolean Canceled;
 public String DepartCountry;
 public String ArriveCountry;

 public boolean isInternational() throws Exception
   {
   if ( (DepartCountry instanceof String) && (ArriveCountry instanceof String) )
     {
     if ( ArriveCountry.equals(DepartCountry) )
       return(false);
     else
       return(true);
     }
   else
     throw new Exception("Unable to determine if flight is international.  departure/arrival country codes must be specified");
   }
}
