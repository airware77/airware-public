package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.*;
import java.io.Serializable;
import xmax.crs.GetPNR.PNRNameElement;
import java.util.Vector;

/**
 ***********************************************************************
 * This class is used to hold a request to split a PNR
 * In order to instatiate it, it is necessary to
 * pass it a Computer Reservation System (CRS) Code, the locator identifying
 * the PNR and the string identifying the Queue.
 *
 * @version  $Revision: 3$ - $Date: 09/12/2002 4:45:29 PM$
 *
 * @see GnrcCrs.splitPNR
 ***********************************************************************
 */
public class ReqSplitPNR extends ReqTranServer implements Serializable
{
 public String OriginalLocator;   // locator for original PNR to be split
 public String SplitLocator;      // newly created PNR
 public int NumUnassigned;        // number of unassigned seats to split
 private Vector vNames;           // list of names to be split

  public ReqSplitPNR(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    OriginalLocator = aLocator;
    vNames = new Vector();
    }

  /**
   ***********************************************************************
   * This method calls the {@link GnrcCrs.QueuePNR} method to queue the
   * Passenger Name Record (PNR)
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Splitting PNR locator " + OriginalLocator,null,aCrs.getConnectionName());
    final PNRNameElement[] names = getNames();
    final StringBuffer sNewLocator = new StringBuffer();

    aCrs.splitPNR(OriginalLocator,NumUnassigned,names,RequestedBy,sNewLocator);
    SplitLocator = sNewLocator.toString();

    AppLog.LogInfo("Successfully split locator " + OriginalLocator + " new PNR locator = " + SplitLocator,null,aCrs.getConnectionName());
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
      throw new TranServerException("Cannot open log file for Queue PNR.  Crs Code is null");

    if ( GnrcFormat.IsNull(OriginalLocator) )
      throw new TranServerException("Cannot open log file for split PNR.  Locator is null");

    sLogName.append(getCrsCode() + OriginalLocator + ".log");
    return( sLogName.toString() );
    }

  /**
   ***********************************************************************
   * This method adds a name to the list of names to be split
   ***********************************************************************
   */
  public void addName(final String aLastName, final String aFirstName)
    {
    final PNRNameElement name = new PNRNameElement();
    name.LastName  = aLastName;
    name.FirstName = aFirstName;
    vNames.add(name);
    }


  public void addName(final String aPsgrID)
    {
    final PNRNameElement name = new PNRNameElement();
    name.setPassengerID(aPsgrID);
    vNames.add(name);
    }

  public void addName(final PNRNameElement aName)
    {
    vNames.add(aName);
    }

  /**
   ***********************************************************************
   * This method returns an array of Name elements to be moved
   ***********************************************************************
   */
  public PNRNameElement[] getNames()
    {
    if ( vNames instanceof Vector )
      {
      if ( vNames.size() > 0 )
        {
        final PNRNameElement[] names = new PNRNameElement[ vNames.size() ];
        vNames.toArray(names);
        return(names);
        }
      }

    return(null);
    }

}
