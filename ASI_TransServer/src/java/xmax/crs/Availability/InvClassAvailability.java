package xmax.crs.Availability;

import java.io.Serializable;

public class InvClassAvailability implements Serializable
{
 private String Carrier;
 private String InvClass;
 private int NumSeats;
 public static String FIRST_CLASS    = "F";
 public static String BUSINESS_CLASS = "B";
 public static String COACH_CLASS    = "c";

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public InvClassAvailability(final String aCarrier, final String aInvClass)
    {
    Carrier  = aCarrier;
    InvClass = aInvClass;
    NumSeats = 0;
    }

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public InvClassAvailability(final String aCarrier, final String aInvClass, final int aNumSeats)
    {
    Carrier  = aCarrier;
    InvClass = aInvClass;
    NumSeats = aNumSeats;
    }

  /** 
   ***********************************************************************
   * Functions for the number of seats
   ***********************************************************************
   */
  public void incNumSeats(final int aNumSeats)
    {
    NumSeats += aNumSeats;
    }

  public void setNumSeats(final int aNumSeats)
    {
    NumSeats = aNumSeats;
    }

  public int getNumSeats()
    {
    return(NumSeats);
    }

  /** 
   ***********************************************************************
   * Functions for the inventory class of service
   ***********************************************************************
   */
  public void setInvClass(final String aInvClass)
    {
    InvClass = aInvClass;
    }

  public String getInvClass()
    {
    return(InvClass);
    }

  /** 
   ***********************************************************************
   * Functions for the carrier
   ***********************************************************************
   */
  public void setCarrier(final String aCarrier)
    {
    Carrier = aCarrier;
    }

  public String getCarrier()
    {
    return(Carrier);
    }

}
