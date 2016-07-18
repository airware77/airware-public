package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.BaseCrs;
import xmax.crs.Amadeus.AmadeusAPICrs;
import xmax.util.Log.AppLog;

import java.util.Properties;
import java.io.Serializable;

public class ReqGetStatus extends ReqTranServer implements Serializable
{
 public String GatewayServer;
 public int    GatewayPort;
 public String Version;
 public String APIVersion;
 public int    TaNumber;
 public String TaName;
 public int    HostType;
 public String HostCode;
 public String SignOn;
 public String Password;
 public String PseudoCity;
 public Properties properties;

  /**
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public ReqGetStatus(final String aCrsCode)
    {
    super(aCrsCode);
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    // GatewayServer = ConfigInformation.getParamValue( ConfigTranServer.GATEWAY_SERVER, "");
    // GatewayPort   = ConfigInformation.getParamValue( ConfigTranServer.GATEWAY_PORT,0);
    // Version       = ConfigInformation.getParamValue( ConfigTranServer.VERSION, "");

    GatewayServer = ConfigTranServer.gateway.getProperty("serverIP");
    GatewayPort   = ConfigTranServer.gateway.getIntProperty("port",1413);
    Version       = ConfigTranServer.CURRENT_VERSION;
		APIVersion    = ConfigTranServer.API_VERSION;
    HostCode      = aCrs.getHostCode();

    properties = aCrs.getProperties();

    /*
    if ( aCrs instanceof InnosysCrs )
      {
      final InnosysCrs iCrs = (InnosysCrs )aCrs;
      HostType = iCrs.GetIateTA().getHostType();
      TaName   = iCrs.GetTaName();
   //   TaNumber = iCrs.GetTaNumber();
      TaNumber = 0;
      }
    else
    */
      {
      HostType = 0;
      TaName   = "";
      TaNumber = 0;
      }


    if ( aCrs instanceof BaseCrs )
      {
      final BaseCrs bCrs = (BaseCrs )aCrs;
      SignOn     = bCrs.getProperty("userID","");
      Password   = bCrs.getProperty("password","");
      PseudoCity = bCrs.getProperty("pseudoCity","");
      }

    if ( aCrs instanceof AmadeusAPICrs)
      {
      final AmadeusAPICrs amCrs = (AmadeusAPICrs)aCrs;
      GatewayServer = amCrs.getProperty("serverIP");
      GatewayPort   = Integer.parseInt(amCrs.getProperty("port"));
      SignOn        = amCrs.getProperty("corporateID","");
      Password      = amCrs.getProperty("password","");
      PseudoCity    = amCrs.getProperty("userID","");
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
