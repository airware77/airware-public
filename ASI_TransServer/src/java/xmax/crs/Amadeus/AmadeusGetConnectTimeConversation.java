package xmax.crs.Amadeus;

import xmax.TranServer.GnrcFormat;
import xmax.TranServer.TranServerException;
import xmax.crs.GnrcCrs;
import xmax.crs.ConnectTimes;
import xmax.crs.ConnectTimesAirport;
import xmax.util.RegExpMatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 ***********************************************************************
 * This class manages a cryptic conversation aimed at determining the legal
 * connect times between connecting flights; it is used by both the
 * screen-scraping and the APIv2 interfaces to Amadeus
 * 
 * @author   David Fairchild
 * @version  $Revision: 3$ - $Date: 04/24/2002 7:08:48 PM$
 *
 * @see ReqGetConnectTimes
 * @see AmadeusCrs.GetConnectTime
 * @see AmadeusAPICrs.GetConnectTime
 ***********************************************************************
 */
public class AmadeusGetConnectTimeConversation
{
  /**
   ***********************************************************************
   * Get the minimum connect time for the given flight segments
   ***********************************************************************
   */
  public void GetConnectInfo(final GnrcCrs aCRS, final ConnectTimes aConnectTimes) throws Exception
    {
    if ( (aConnectTimes instanceof ConnectTimes) == false )
      throw new TranServerException("Must specify a valid connectTimes request");

    final SimpleDateFormat dt_format = new SimpleDateFormat("ddMMMyy");

    // get the connection information
    final String sRequest = "DMS/" + aConnectTimes.InBound.ArriveCity + aConnectTimes.OutBound.DepartCity + "/" +
                            aConnectTimes.InBound.Carrier + aConnectTimes.InBound.FlightNum + "//" +
                            aConnectTimes.InBound.DepartCity + "/-" +
                            aConnectTimes.OutBound.Carrier + aConnectTimes.OutBound.FlightNum + "//" +
                            aConnectTimes.OutBound.ArriveCity + "/--/" +
                            dt_format.format( new Date(aConnectTimes.OutBound.DepSchedDateTime) ).toString().toUpperCase();

    final String sResponse = aCRS.HostTransaction(sRequest);
    readData(sResponse,aConnectTimes);
    }


  /**
   ***********************************************************************
   * Scan the Amadeus response
   ***********************************************************************
   */
  private void readData(final String aInputString, final ConnectTimes aConnectTimes) throws Exception
    {
    final StringTokenizer lines = new StringTokenizer(aInputString,"\r\n");

    String sLine;
    String[] fields;
    while( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken().trim();

      fields = RegExpMatch.getMatchPatterns(
          sLine,"[ID]\\/[ID]\\:([0-9])([0-9]{2})$");
        {
        if ( fields instanceof String[] )
          {
          final String sHour   = fields[1];
          final String sMinute = fields[2];

          final int iHour;
          if ( GnrcFormat.NotNull(sHour) )
            iHour = Integer.parseInt(sHour);
          else
            iHour = 0;

          final int iMinute = Integer.parseInt(sMinute);
          aConnectTimes.MinimumConnectMinutes = (iHour * 60) + iMinute;
          return;
          }
        }
      }
    } // end readData


  /**
   ***********************************************************************
   * Retrieves the Minimum Connect Times for an airport; see 
   * {@link ConnectTimesAirport} for a description of the different connect
   * times that are retrieved; Amadeus does not have a command to retrieve
   * online connect times, and hence the values of these are <code>null</code>
   *
   * @see ConnectTimesAirport
   * @see AmadeusCrs#GetAirportConnectTimes
   * @see NativeAsciiReader#reqGetAirportConnectTimes
   * @see NativeAsciiWriter#reqGetAirportConnectTimes
   ***********************************************************************
   */
  public void GetAirportConnectInfo(
      final GnrcCrs aCRS, final ConnectTimesAirport airportConnect) throws Exception
    {
    if ( (airportConnect instanceof ConnectTimesAirport) == false )
      throw new TranServerException("Must specify a valid Airport Connect Times request");

    // get the connection information
    final String sRequest = "DM " + airportConnect.getAirportCode();

    final String sResponse = aCRS.HostTransaction(sRequest);
    readAirportConnectTimes(sResponse,airportConnect);
    }

  /**
   ***********************************************************************
   * Parses the response to a cryptic code command such as: <code>DM SFO</code>
   * that retrieves 
   ***********************************************************************
   */
  private static void readAirportConnectTimes(
      final String resp, final ConnectTimesAirport airportTimes)
    {
    final StringTokenizer lines = new StringTokenizer(resp,"\r\n");

    String sLine;
    String[] fields;
    while( lines.hasMoreTokens() )
      {
      sLine = lines.nextToken().trim();

      fields = RegExpMatch.getMatchPatterns(
          sLine,"([0-9])([0-9]{2}) ([0-9])([0-9]{2}) ([0-9])([0-9]{2}) ([0-9])([0-9]{2})$");
        {
        if ( fields instanceof String[] )
          {
          final String sDDhour = fields[1];
          final String sDDmin  = fields[2];
          final String sDIhour = fields[3];
          final String sDImin  = fields[4];
          final String sIDhour = fields[5];
          final String sIDmin  = fields[6];
          final String sIIhour = fields[7];
          final String sIImin  = fields[8];

          airportTimes.offlineTimes[airportTimes.DOMESTIC_DOMESTIC] =
            calcMinutes(fields[1],fields[2]);

          airportTimes.offlineTimes[airportTimes.DOMESTIC_INTL] =
            calcMinutes(fields[3],fields[4]);

          airportTimes.offlineTimes[airportTimes.INTL_DOMESTIC] =
            calcMinutes(fields[5],fields[6]);

          airportTimes.offlineTimes[airportTimes.INTL_INTL] =
            calcMinutes(fields[7],fields[8]);

          return;
          }
        }
      }
    } // end readAirportConnectTimes

  /**
   ***********************************************************************
   * Given a number of hours and a number of minutes as Strings, this method
   * returns the total number of minutes as an integer
   ***********************************************************************
   */
  private static int calcMinutes(String hours, String minutes)
    {
    final int iHour;
    if ( GnrcFormat.NotNull(hours) )
      iHour = Integer.parseInt(hours);
    else
      iHour = 0;

    return (iHour * 60) + Integer.parseInt(minutes);
    } // end calcMinutes


} // end class AmadeusGetConnectTimeConversation
