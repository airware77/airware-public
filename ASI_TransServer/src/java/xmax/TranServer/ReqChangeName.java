package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import xmax.crs.GetPNR.PNRNameElement;
import java.util.StringTokenizer;
import java.io.Serializable;

public class ReqChangeName extends ReqTranServer implements Serializable
{
 public String Locator;
 public String PassengerID;
 public String LastName;
 public String FirstName;
 public String PTC;
 public String InfantName;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqChangeName(final String aCrsCode, final String aLocator, final String aPassengerID)
    {
    super(aCrsCode);
    Locator     = aLocator;
    PassengerID = aPassengerID;
    }

  /** 
   ***********************************************************************
   * Used to parse up a name 
   ***********************************************************************
   */
  public void setName(final String aFullName)
    {
    // try to parse read the name field
    final StringTokenizer fields = new StringTokenizer(aFullName," /");
    if ( fields.countTokens() >= 2 )
      {
      LastName  = fields.nextToken();
      FirstName = fields.nextToken();
      }
    else
      {
      LastName  = aFullName;
      FirstName = "";
      }
    }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Changing Passenger ID " + PassengerID + " to name " + FirstName + " " + LastName,null,aCrs.getConnectionName());

    final PNRNameElement NewName = new PNRNameElement();
    NewName.LastName         = LastName;
    NewName.FirstName        = FirstName;
    NewName.PTC              = PTC;
    NewName.setPassengerID(PassengerID);

    aCrs.ChangeName(Locator,PassengerID,NewName);
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

    if ( GnrcFormat.IsNull(Locator) )
      throw new TranServerException("Cannot open log file for Queue PNR.  Locator is null");

    sLogName.append(getCrsCode() + Locator + ".log");
    return( sLogName.toString() );
    }

}
