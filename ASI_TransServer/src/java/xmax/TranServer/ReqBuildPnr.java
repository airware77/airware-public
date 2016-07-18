package xmax.TranServer;

import xmax.crs.PNR;
import xmax.crs.GnrcCrs;
import xmax.crs.GetPNR.*;
import xmax.util.Log.AppLog;

import java.io.Serializable;
import java.util.Vector;

public class ReqBuildPnr extends ReqTranServer implements Serializable
{
 private String Locator;
 private Vector vAddNames;          // names to be added
 private Vector vAddSegments;       // segments to be added
 private Vector vAddRemarks;        // remarks to be added
 private Vector vModOldNames;       // names before modification
 private Vector vModOldRemarks;     // remarks before modification
 private Vector vModNewNames;       // names after modification
 private Vector vModNewRemarks;     // remarks after modification
 private Vector vDelNames;          // names to be deleted
 private Vector vDelSegments;       // segments to be deleted
 private Vector vDelRemarks;        // remarks to be deleted
 public boolean doEndTransact = true; // run the end transaction command after all commands

 /**
  ***********************************************************************
  * constructor
  ***********************************************************************
  */

  public ReqBuildPnr(final String aCrsCode)
    {
    super(aCrsCode);
    vAddNames      = new Vector();
    vAddSegments   = new Vector();
    vAddRemarks    = new Vector();
    vModOldNames   = new Vector();
    vModOldRemarks = new Vector();
    vModNewNames   = new Vector();
    vModNewRemarks = new Vector();
    vDelNames      = new Vector();
    vDelSegments   = new Vector();
    vDelRemarks    = new Vector();
    }


  public ReqBuildPnr(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    Locator = aLocator;
    vAddNames      = new Vector();
    vAddSegments   = new Vector();
    vAddRemarks    = new Vector();
    vModOldNames   = new Vector();
    vModOldRemarks = new Vector();
    vModNewNames   = new Vector();
    vModNewRemarks = new Vector();
    vDelNames      = new Vector();
    vDelSegments   = new Vector();
    vDelRemarks    = new Vector();
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // change names
    if ( (vModOldNames.size() > 0) && (vModNewNames.size() > 0) )
      {
      final PNRNameElement[] oldnames = new PNRNameElement[ vModOldNames.size() ];
      final PNRNameElement[] newnames = new PNRNameElement[ vModNewNames.size() ];
      vModOldNames.toArray(oldnames);
      vModNewNames.toArray(newnames);
      aCrs.changePnrElements(Locator,oldnames,newnames,RequestedBy);
      }

    // change remarks
    if ( (vModOldRemarks.size() > 0) && (vModNewRemarks.size() > 0) )
      {
      final PNRRemark[] oldremarks = new PNRRemark[ vModOldRemarks.size() ];
      final PNRRemark[] newremarks = new PNRRemark[ vModNewRemarks.size() ];
      vModOldRemarks.toArray(oldremarks);
      vModNewRemarks.toArray(newremarks);
      // aCrs.ChangePnrElements(pnr,oldremarks,newremarks);
      }

    // delete names
    if ( vDelNames.size() > 0)
      {
      final PNRNameElement[] oldnames = new PNRNameElement[ vDelNames.size() ];
      vDelNames.toArray(oldnames);
      // aCrs.DeletePnrElements(pnr,oldnames);
      }

    // delete segments
    if ( vDelSegments.size() > 0)
      {
      final PNRItinAirSegment[] oldsegments = new PNRItinAirSegment[ vDelSegments.size() ];
      vDelSegments.toArray(oldsegments);
      // aCrs.DeletePnrElements(pnr,oldsegments);
      }

    // delete remarks
    if ( vDelRemarks.size() > 0)
      {
      final PNRRemark[] oldremarks = new PNRRemark[ vDelRemarks.size() ];
      vDelRemarks.toArray(oldremarks);
      aCrs.deletePnrElements(Locator,oldremarks,RequestedBy);
      }

    // insert names
    if ( vAddNames.size() > 0)
      {
      final PNRNameElement[] newnames = new PNRNameElement[ vAddNames.size() ];
      vAddNames.toArray(newnames);
     // aCrs.AddPnrElements(pnr,newnames);
      }

    // insert segments
    if ( vAddSegments.size() > 0)
      {
      final PNRItinAirSegment[] newsegments = new PNRItinAirSegment[ vAddSegments.size() ];
      vAddSegments.toArray(newsegments);
      // aCrs.AddPnrElements(pnr,newsegments);
      }

    // insert remarks
    if ( vAddRemarks.size() > 0)
      {
      final PNRRemark[] newRemarks = new PNRRemark[ vAddRemarks.size() ];
      vAddRemarks.toArray(newRemarks);
      aCrs.addPnrElements(Locator,newRemarks,RequestedBy);
      }

    if ( doEndTransact )
      {
      if (RequestedBy instanceof String)
        aCrs.EndTransaction(RequestedBy);
      else 
        aCrs.EndTransaction();
      }
    }

  /**
   ***********************************************************************
   * This procedure adds a request to the existing list of requests
   ***********************************************************************
   */

  public void addInsertRequest(final Object aPnrObject)
    {
    if ( aPnrObject instanceof PNRNameElement )
      addToVector(vAddNames,aPnrObject);
    else if ( aPnrObject instanceof PNRItinAirSegment )
      addToVector(vAddSegments,aPnrObject);
    else if ( aPnrObject instanceof PNRRemark )
      addToVector(vAddRemarks,aPnrObject);
    }

    
  public void addModifyRequest(final Object aOldPnrObject, final Object aNewPnrObject)
    {
    if ( (aOldPnrObject instanceof PNRNameElement) && (aNewPnrObject instanceof PNRNameElement) )
      {
      addToVector(vModOldNames,aOldPnrObject);
      addToVector(vModNewNames,aNewPnrObject);
      }
    else if ( (aOldPnrObject instanceof PNRRemark) && (aNewPnrObject instanceof PNRRemark) )
      {
      addToVector(vModOldRemarks,aOldPnrObject);
      addToVector(vModNewRemarks,aNewPnrObject);
      }
    }



  public void addDeleteRequest(final Object aPnrObject)
    {
    if ( aPnrObject instanceof PNRNameElement )
      addToVector(vDelNames,aPnrObject);
    else if ( aPnrObject instanceof PNRItinAirSegment )
      addToVector(vDelSegments,aPnrObject);
    else if ( aPnrObject instanceof PNRRemark )
      addToVector(vDelRemarks,aPnrObject);
    }


  private void addToVector(final Vector aVector, final Object aPnrObject)
    {
    aVector.add(aPnrObject);
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

    sLogName.append("\\Locators\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for modifying PNR.  Crs Code is null");

    if ( GnrcFormat.IsNull(Locator) )
      return(null);

    sLogName.append(getCrsCode() + Locator + ".log");
    return( sLogName.toString() );
    }

}
