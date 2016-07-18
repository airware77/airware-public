package xmax.crs.GetPNR;

import java.io.Serializable;

public class PNRSeatRemark extends PNRRemark implements Serializable
{
 public String Seat;
 public String SeatStatus;
 public String BoardingStatus;
 public boolean Smoking;
 public String Carrier;
 public int FlightNum;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PNRSeatRemark()
    {
    }

  public PNRSeatRemark(final String aSeatData)
    {
    Seat = aSeatData;
    }

  /** 
   ***********************************************************************
   * Get the remark type
   ***********************************************************************
   */
  public String getRemarkType()
    {
    return("Seat");
    }

}