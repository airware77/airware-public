package xmax.TranServer;

import xmax.crs.GetPNR.PNRNameElement;
import java.util.Vector;
import java.util.StringTokenizer;
import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;

public class ReqAddNames extends ReqTranServer implements Serializable
{
 public String Locator;
 public PNRNameElement[] NameList;

 /**
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqAddNames(final String aCrsCode, final String aLocator)
    {
    super(aCrsCode);
    Locator = aLocator;
    }

  /**
   ***********************************************************************
   * This method uses the parameters provided and:
   * <ul>
   *  <li> calls {@link createName} to create a new {@link PNRNameElement}</li>
   *  <li> calls the overloaded addName method below to add the PNRNameElement
   *       to the NameList array.</li>
   * </ul>
   ***********************************************************************
   */
  public void addName(final String aFullName, final String aPTC,
                      final String aIDNumber, final String aInfant,
											final String aInfantDOB)
    {
    final PNRNameElement newName = createName(aFullName,aPTC,aIDNumber,aInfant,aInfantDOB);
    if ( newName instanceof PNRNameElement )
      addName(newName);
    }

  /**
   ***********************************************************************
   * This method takes the {@link PNRNameElement} provided and either:
   * <ul>
   *  <li>Creates a new NameList array</li>
   *  <li>Increases the size of the current NameList array by one
   *      adds the PNRNameElement to it.</li>
   * </ul>
   ***********************************************************************
   */
  public void addName(final PNRNameElement aName)
    {
    // create a new array to hold the new elements
    final PNRNameElement[] newArray;
    if ( NameList instanceof PNRNameElement[] )
      {
      newArray = new PNRNameElement[ NameList.length + 1 ];

      // copy the existing array into the new array
      for ( int i = 0; i < NameList.length; i++ )
        newArray[i] = NameList[i];
      }
    else
      newArray = new PNRNameElement[1];

    // add the new element
    newArray[newArray.length - 1] = aName;

    NameList = newArray;
    }

  /**
   ***********************************************************************
   * Used to create a {@link PNRNameElement} object from the given parms
   ***********************************************************************
   */
  private static PNRNameElement createName(final String aFullName, final String aPTC,
                                           final String aIDNumber, final String aInfant,
																					 final String aInfantDOB)
    {
    final String sLastName;
    final String sFirstName;

    // try to parse read the name field
    final StringTokenizer fields = new StringTokenizer(aFullName,"/");
    if ( fields.countTokens() >= 2 )
      {
      sLastName  = fields.nextToken();
      sFirstName = fields.nextToken();
      }
    else
      {
      sLastName  = aFullName;
      sFirstName = "";
      }

    return( createName(sLastName,sFirstName,aPTC,aIDNumber,aInfant,aInfantDOB) );
    }


  /**
   ***********************************************************************
   * Used to create a {@link PNRNameElement} object from the given parms
   ***********************************************************************
   */
  private static PNRNameElement createName(final String aLastName,
                                           final String aFirstName,
                                           final String aPTC,
                                           final String aIDNumber,
                                           final String aInfant,
																					 final String aInfantDOB)
    {
    final PNRNameElement newName = new PNRNameElement();

    // try to read the name field
    newName.LastName    = aLastName;
    newName.FirstName   = aFirstName;
    newName.PTC         = aPTC;
    newName.setPassengerID(aIDNumber);
    newName.InfantName  = aInfant;
		newName.InfantDOB  = aInfantDOB;
    newName.NumSeats    = 1;

    return(newName);
    }


  /**
   ***********************************************************************
   * This method iterates over the NameList of PNRNameElement objects
   * and calls the {@link GnrcCrs.AddName} method of the appropriate
   * Computer Reservation System sub-class.
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    if ( NameList instanceof PNRNameElement[] )
      {
      for ( int i = 0; i < NameList.length; i++ )
        {
        AppLog.LogInfo("Adding name: " + NameList[i].FirstName + " " + NameList[i].LastName,null,aCrs.getConnectionName());
        aCrs.AddName(NameList[i]);
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
