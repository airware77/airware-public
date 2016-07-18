package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.ConnectTimesAirport;
import xmax.util.Log.AppLog;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 ***********************************************************************
 * This request is used to procure the Minimum Connect Times for a particular
 * airport; there are four Minimum Connect Times to account for both Domestic
 * and International Arrival and Departures:
 * <ul>
 *   <li>Domestic Arrival to Domestic Departure</li>
 *   <li>Domestic Arrivalto International Departure</li>
 *   <li>International Arrival to Domestic Departure</li>
 *   <li>International Arrival to International Departure</li>
 * </ul>
 *
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 04/24/2002 7:05:49 PM$
 ***********************************************************************
 */
public class ReqGetConnectTimesAirport extends ReqTranServer implements Serializable
{

  private List airportList;

  /** Populates the airport code for this request */
  public ReqGetConnectTimesAirport(final String aCrsCode)
    {
    super(aCrsCode);
    airportList = new ArrayList();
    }

  /**
   ***********************************************************************
   * This method calls the GetAirportConnectTimes method of the appropriate
   * Computer Reservation System (CRS) specified
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    if (airportList.size() > 0)
      {
      ConnectTimesAirport airport;
      for ( int i = 0; i < airportList.size(); i++ )
        {
        airport = (ConnectTimesAirport)airportList.get(i);
        AppLog.LogInfo(
            "Getting connect times for airport " + airport.getAirportCode());

        aCrs.GetConnectTimesAirport(airport);
        }
      }

    } // end runRequest

  /**
   ***********************************************************************
   * Given an airport code for which the connection times are desired, this
   * method adds a {@link ConnectTimesAirport} object to be populated with the
   * appropriate connection times
   ***********************************************************************
   */
  public void addAirportConnectTimeQuery(final String airportCode)
    {
    airportList.add(new ConnectTimesAirport(airportCode));
    }

  /**
   ***********************************************************************
   * Returns the list of <code>ConnectTimesAirport</code> in this request
   ***********************************************************************
   */
  public ConnectTimesAirport[] getAirportConnectTimesList()
    {
    ConnectTimesAirport[] array = new ConnectTimesAirport[airportList.size()];
    return (ConnectTimesAirport[])airportList.toArray(array);
    }

  /**
   ***********************************************************************
   * returns null - this request is logged in the Terminal file only
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    return(null);
    }

} // end ReqGetAirportConnectTimes
