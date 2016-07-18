package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.*;
import java.io.Serializable;

public class ReqGetHotelInfo extends ReqTranServer implements Serializable
{
 public String HotelChain;
 public String PropertyCode;
 public String CityCode;
 public String HotelInformation;     // returned string of info

 /** 
  ***********************************************************************
  * Constructors
  ***********************************************************************
  */
 public ReqGetHotelInfo(final String aCrsCode, final String aHotelChain,
                        final String aPropertyCode)
   {
   this(aCrsCode,aHotelChain,aPropertyCode,"");
   }


 public ReqGetHotelInfo(final String aCrsCode, final String aHotelChain,
                        final String aPropertyCode, final String aCityCode)
   {
   super(aCrsCode);
   HotelChain   = aHotelChain;
   PropertyCode = aPropertyCode;
   CityCode     = aCityCode;
   }

  /** 
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Getting hotel info for Chain " + HotelChain + " property code " + PropertyCode,null,aCrs.getConnectionName());

    // get info from the crs
    final StringBuffer sHotelData = new StringBuffer();
    aCrs.GetHotelInfo(HotelChain,PropertyCode,sHotelData);
    HotelInformation = sHotelData.toString();

    AppLog.LogInfo("Info from CRS = " + HotelInformation,null,aCrs.getConnectionName());
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

    sLogName.append("\\Hotel\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for hotel info.  Crs Code is null");

    if ( GnrcFormat.IsNull(HotelChain) )
      throw new TranServerException("Cannot open log file for hotel info.  Chain code is null");

    if ( GnrcFormat.IsNull(PropertyCode) )
      throw new TranServerException("Cannot open log file for hotel info.  Property code is null");

    sLogName.append(getCrsCode() + "-" + HotelChain + PropertyCode + ".log");

    return( sLogName.toString() );
    }

}
