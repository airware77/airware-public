package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.*;
import java.io.Serializable;
import xmax.crs.profiles.Profile;

public class ReqGetProfile extends ReqTranServer
{
 public String  PseudoCity;
 public String  Group;
 public String  Passenger;
 public Profile Profile;
 public boolean getHeaderInfoOnly;

  /**
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public ReqGetProfile(final String aCrsCode, final String aPseudoCity,
                       final String aGroup, final String aPassenger)
    {
    super(aCrsCode);
    PseudoCity = aPseudoCity;
    Group      = aGroup;
    Passenger  = aPassenger;

    Profile = new Profile(getCrsCode(),PseudoCity,Group,Passenger);
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    if ( GnrcFormat.IsNull(Passenger) )
      AppLog.LogInfo("Getting group profile for pseudo city " + PseudoCity + " group " + Group,null,aCrs.getConnectionName());
    else
      AppLog.LogInfo("Getting passenger profile for pseudo city " + PseudoCity + " group " + Group + " passenger " + Passenger,null,aCrs.getConnectionName());

    aCrs.GetProfile(this);
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

    sLogName.append("\\Profiles\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for get Profile.  Crs Code is null");

    if ( GnrcFormat.IsNull(PseudoCity) )
      throw new TranServerException("Cannot open log file for get profile.  Pseudo city is null");

    if ( GnrcFormat.IsNull(Group) && GnrcFormat.IsNull(Passenger) )
      throw new TranServerException("Cannot open log file for get profile.  must specify a group or passenger");

    sLogName.append(getCrsCode() + PseudoCity  + "-" + GnrcFormat.ShowString(Group) + GnrcFormat.ShowString(Passenger) + ".log");

    return( sLogName.toString() );
    }

  /** 
   ***********************************************************************
   * Identify which type of profile request we are handling
   ***********************************************************************
   */
  public boolean isCorporateProfileReq()
    {
    return ( GnrcFormat.IsNull(Passenger) );
    }

  public boolean isTravelerProfileReq()
    {
    return ( GnrcFormat.NotNull(Passenger) );
    }

}
