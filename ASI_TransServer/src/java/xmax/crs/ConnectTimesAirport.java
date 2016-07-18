package xmax.crs;

import xmax.crs.GnrcCrs;
import java.io.Serializable;

/**
 ***********************************************************************
 * This object is used to store the Minimum Connect Times for a particular
 * airport; there are four Minimum Connect Times to account for both Domestic
 * and International Arrival and Departures:
 * <ul>
 *   <li>Domestic Arrival to Domestic Departure</li>
 *   <li>Domestic Arrivalto International Departure</li>
 *   <li>International Arrival to Domestic Departure</li>
 *   <li>International Arrival to International Departure</li>
 * </ul>
 * Furthermore, the four times above vary depending on whether the connection
 * is 'online' or 'offline', for a total of 8 possible connection times;
 * a connection is 'online' when the connecting flight is operated by the same
 * airline, and 'offline' when it is not.
 *
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 04/24/2002 7:09:33 PM$
 ***********************************************************************
 */
public class ConnectTimesAirport
{
  private String airport;
  /** The airport code for which the minimum connect times are requested */
  public String getAirportCode() {return airport;}

  public final static int DOMESTIC_DOMESTIC = 0;
  public final static int DOMESTIC_INTL     = 1;
  public final static int INTL_DOMESTIC     = 2;
  public final static int INTL_INTL         = 3;

  /** 
   * Contains the Minimum Connect Times in minutes for connecting flights
   * operated by the same airline, categorized per the constants listed above;
   * for example, <code>onlineTimes[DOMESTIC_INTL]</code> stores the minimum
   * connection time for a Domestic to International flight operated by the
   * same airline; a value of -1 indicates that the time for a specific
   * category is not available.
   */
  public int[] onlineTimes  = new int[4];

  /** 
   * Contains the Minimum Connect Times in minutes for connecting flights
   * operated by different airlines, categorized per the constants listed above;
   * for example, <code>offlineTimes[INTL_INTL]</code> stores the minimum
   * connection time for an International to International flight operated by
   * different airlines; a value of -1 indicates that the time for a specific
   * category is not available.
   */
  public int[] offlineTimes = new int[4];

  /** 
   ***********************************************************************
   * Constructor: populates the airport code for this request and initializes
   * all the onlineTimes and offlineTimes arrays buckets to -1.
   ***********************************************************************
   */
  public ConnectTimesAirport(final String anAirportCode)
    {
    airport = anAirportCode;
    
    for (int i=0; i < 4; i++)
      {
      onlineTimes[i]  = -1;
      offlineTimes[i] = -1;
      }
    }

} // end ConnectTimesAirport
