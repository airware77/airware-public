package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import xmax.crs.ConnectTimes;
import java.io.Serializable;
import xmax.crs.Flifo.FlightSegment;
import java.util.Vector;

public class ReqGetConnectTimes extends ReqTranServer implements Serializable
{
 private Vector Connections;

 /**
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public ReqGetConnectTimes(final String aCrsCode)
   {
   super(aCrsCode);
   Connections = new Vector();
   }

 /**
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public void addConnectionQuery(final FlightSegment aInBound, final FlightSegment aOutBound)
   {
   final ConnectTimes conn = new ConnectTimes();

   // set the flight segments for the new query
   conn.InBound  = aInBound;
   conn.OutBound = aOutBound;
   Connections.add(conn);
   }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public int numConnections()
    {
    if ( Connections instanceof Vector )
      return( Connections.size() );
    else
      return(0);
    }

  /**
   ***********************************************************************
   *
   ***********************************************************************
   */
  public ConnectTimes getConnection(final int aIndex)
    {
    if ( Connections instanceof Vector )
      return( (ConnectTimes )Connections.elementAt(aIndex) );
    else
      return(null);
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    if ( Connections instanceof Vector )
      {
      ConnectTimes conn;
      for ( int i = 0; i < Connections.size(); i++ )
        {
        conn = getConnection(i);
        AppLog.LogInfo("Getting connect times for " + conn.InBound.ArriveCity,null,aCrs.getConnectionName());

        // get legal connect time info from the crs
        aCrs.GetConnectTime(conn);
        }
      }
    }

  /**
   ***********************************************************************
   * Get a log file name to use for this request
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    return(null);
    }

}
