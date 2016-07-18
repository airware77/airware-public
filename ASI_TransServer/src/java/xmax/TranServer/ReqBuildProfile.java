package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.AppLog;
import java.io.Serializable;
import xmax.crs.profiles.Profile;

public class ReqBuildProfile extends ReqTranServer implements Serializable
{
 public Profile profile;
 public long thresholdDate;     // timestamp for this profile data (can be used to compare to CRS timestamp)
 public boolean forceChange;    // update the CRS profile, even if it's timestamped later

  /**
  *******************************************************************
  * Constructor
  *******************************************************************
  */
  public ReqBuildProfile(final String aCrsCode, final Profile aProfile)
  {
  super(aCrsCode);
  profile = aProfile;
  }

  /**
   ***********************************************************************
   * This method is the main method for running a build profile request;
   *
   * @see xmax.crs.profile.Profile
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    if ( profile.isGroupProfile() )
      AppLog.LogInfo("Building group profile '" + profile.getGroupName() + "' for pseudo city " + profile.getPsuedoCity(),null,aCrs.getConnectionName());
    else
      AppLog.LogInfo("Building traveler profile '" + profile.getGroupName() + "/" + profile.getTravelerName() + "' for pseudo city " + profile.getPsuedoCity(),null,aCrs.getConnectionName());

    // build the profile
    aCrs.BuildProfile(this);
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

    if ( GnrcFormat.IsNull(profile.getPsuedoCity()) )
      throw new TranServerException("Cannot open log file for get profile.  Pseudo city is null");

    if ( GnrcFormat.IsNull(profile.getGroupName()) && GnrcFormat.IsNull(profile.getTravelerName()) )
      throw new TranServerException("Cannot open log file for get profile.  must specify a group or passenger");

    sLogName.append(getCrsCode() + profile.getPsuedoCity()  + "-" + GnrcFormat.ShowString(profile.getGroupName()) + GnrcFormat.ShowString(profile.getTravelerName()) + ".log");

    return( sLogName.toString() );
    }


}