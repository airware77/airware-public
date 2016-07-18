package xmax.TranServer;

import xmax.crs.GetPNR.PNRItinSegment;
import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.util.Log.AppLog;
import java.io.Serializable;
import java.util.Vector;

public class ReqAddAirSeg extends ReqTranServer implements Serializable
{
 public String Locator;
 private Vector segments;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddAirSeg(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    Locator = aLocator;
    segments = new Vector();
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Adding air segments",null,aCrs.getConnectionName());
    final PNRItinSegment[] segs = getSegments();
    aCrs.addPnrElements(Locator,segs,RequestedBy);
    }

 /** 
  ***********************************************************************
  * access routines
  ***********************************************************************
  */
  public PNRItinSegment[] getSegments()
    {
    if ( segments.size() > 0 )
      {
      final PNRItinSegment[] segs = new PNRItinSegment[segments.size()];
      segments.toArray(segs);
      return(segs);
      }
    else
      return(null);
    }


 public void setSegments(final PNRItinSegment[] aSegs)
   {
   segments.clear();

   if ( aSegs instanceof PNRItinSegment[] )
     {
     for ( int i = 0; i < aSegs.length; i++ )
       segments.add(aSegs[i]);
     }
   }


 public void clearSegments()
   {
   segments.clear();
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void addSegment(final PNRItinSegment aSeg)
   {
   segments.add(aSeg);
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
