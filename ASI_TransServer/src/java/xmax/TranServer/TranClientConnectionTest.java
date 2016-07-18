package xmax.TranServer;

import xmax.TranServer.TranClientConnection;
import xmax.TranServer.ReqTranServer;
import xmax.TranServer.GnrcConvControl;
import xmax.TranServer.GnrcFormat;
import xmax.util.RegExpMatch;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 ***********************************************************************
 * This is a convenience class used for unit testing of the Transaction
 * Server.
 ***********************************************************************
 */
public class TranClientConnectionTest
{

  /**
   ***********************************************************************
   * Main function for unit test
   ***********************************************************************
   */
  public static void main(String[] args)
    {
    //TranListener         tl  = null;
    TranClientConnection tcc = null;
    try
      {
      // the location of your configuration directory
      String CONFIGPATH   = "e:/projects/transerver/conf/";

      // change to the root of your testing folder
      String TEST_LOG_DIR = "e:/projects/transerver/logs/";
      
      // change to root of Test doc folder
      String TESTDOCS     = "e:/projects/transerver/testdocs/";
      
      String CONFIGFILE;

      // to access different environments, uncomment the appropriate line below

      // the practice training environment, via the API
      CONFIGFILE = CONFIGPATH + "TranServerConfig.xml";
      //CONFIGFILE = CONFIGPATH + "TranServerConfig_XmaxProd.xml";
      //CONFIGFILE = CONFIGPATH + "TranServerConfig_APIv2_Xmax-PDT.xml";
     
      // NCL Amadeus API environments
      // CONFIGFILE = CONFIGPATH + "TranServerConfig_NCL-PT.xml";
      // CONFIGFILE = CONFIGPATH + "TranServerConfig_NCL-PROD.xml";
      
      // the XMAX production environment, via the API
      //CONFIGFILE = CONFIGPATH + "TranServerConfig_AmadeusProduction.xml";

      // the XMAX production environment, via the Innosys gateway
      //CONFIGFILE = CONFIGPATH + "TranServerConfig_Innosys.xml";

      // the XMAX production environment, via the Innosys gateway
      //CONFIGFILE = CONFIGPATH + "TranServerConfig_Sabre_Innosys.xml";

      // the disney practice environment, via the API
      //CONFIGFILE = CONFIGPATH + "TranServerConfig_Disney.xml";

      // the disney production environment, via the API
      //CONFIGFILE = CONFIGPATH + "TranServerConfig_DisneyProduction.xml";

      ConfigTranServer.readConfFile(CONFIGFILE);

      String sRequestFile = "";

      // uncomment a line to playback a specific conversation file
      // this overrides any of the requests specified in getRequestList()

      //sRequestFile = TEST_LOG_DIR + "debug.conv";
      //sRequestFile = TEST_LOG_DIR + "/buildPNR_errors/disney_aa_err.conv";
      //sRequestFile = TESTDOCS + "/buildPNR/standard.conv";
      //sRequestFile = TESTDOCS + "/buildPNR/standard_arunk.conv";
      //sRequestFile = TESTDOCS + "buildPNR/setupGetFares.conv";
      //sRequestFile = TESTDOCS + "buildPNR/setupCxlRemarks.conv";
      //sRequestFile = TESTDOCS + "buildPNR_errors/ADDRMK_mult.conv";
      //sRequestFile = TESTDOCS + "buildPNR_errors/add_seg.conv";
      //sRequestFile = TESTDOCS + "buildPNR_errors/addRmk_errorr.conv";
      //sRequestFile = TESTDOCS + "block/add_block_err.conv";
      //sRequestFile = TESTDOCS + "buildPNR_errors/change_pnr_itin.conv";
      //sRequestFile = TEST_LOG_DIR + "error_conv/disney/lowestFare_err3.conv";

      // keeps track of the requests sent in this conversation
      // to 'record' conversations that can be replayed later, 
      // copy and rename the file 'request.log'
      File requestLog = new File(TEST_LOG_DIR + "request.log");
      PrintWriter out_req = new PrintWriter(new FileWriter(requestLog),true);

      // logs the NativeAscii responses to this conversation
      File responseLog = new File(TEST_LOG_DIR + "response.log");
      PrintWriter out_resp = new PrintWriter(new FileWriter(responseLog),true);

      String sRequest  = ""; // an individual request
      String sResponse = ""; // an individual response

      //tl  = new TranListener();
      tcc = new TranClientConnection(null);
                                             
      Vector req;
      ReqTranServer request;

      // play back a request file
      if (sRequestFile.length() > 0)
        req = readRequestListFile(sRequestFile);
      else
        // or run the commands specified at the bottom of this file
        req  = getRequestList();
  
      for (int i = 0; i < req.size(); i++)
        {
        sRequest = (String)req.elementAt(i);
        System.out.println(sRequest);
        out_req.println(sRequest); // record requests

        //request   = t.sendReceiveNativeAscii(sRequest);
        //sResponse = request.AsciiResponse;
        sResponse = tcc.sendReceiveNativeAscii(sRequest);
        System.out.println(sResponse);
        out_resp.println(sResponse); // record responses
        }

      }
    catch (Exception e) {
      String err = e.toString();
      System.out.println(err);
      }
    finally
      {
      if ( tcc instanceof TranClientConnection )
        {
        tcc.finalize();
        tcc.gdsList.clear();
        tcc = null;
        }
      /*
      if (tl instanceof TranListener)
        {
        tl.finalize();
        tl = null;
        }
      */
      }
    } // end main()

  /**
   ***********************************************************************
   * Reads a file containing a list of requests in Native Ascii format;
   * specific requests can be commented out by including a "//" at the
   * beginning of the line
   ***********************************************************************
   */
  public static Vector readRequestListFile(String sFile)
    throws FileNotFoundException, IOException
    {
    Vector req = new Vector();
    File f = new File(sFile);
    // complain if we cannot find the Run File
    if (!f.exists())
      throw new FileNotFoundException("Unable to find:" + sFile);
    
    BufferedReader in = new BufferedReader(new FileReader(f));
    while(true)
      {
      String sReq = in.readLine();
      try
        {
        // ignore commented lines and lines that only contain whitespace
        if (sReq instanceof String && !RegExpMatch.matches(sReq, "^//") 
            && !RegExpMatch.matches(sReq,"^\\s*$") )
          {
          // make sure that the request is at least 8 characters long
          if (sReq.length() < 8)
            sReq = GnrcFormat.SetWidth(sReq,8);
          req.addElement(sReq);
          }
        else if (!(sReq instanceof String) )
          break;
        }
      catch (Exception e) {System.out.println(e.toString());}
      }
    return(req);

    } // end readRequestListFile

  /**
   ***********************************************************************
   * This function creates a vector containing all the string requests
   * that are to be passed to the Transaction Server
   ***********************************************************************
   */
  private static Vector getRequestList()
    {
    Vector req = new Vector();

    /**
     ***********************************************************************
     * Availability Request
     ***********************************************************************
     */
    /*
    req.addElement(
      GnrcConvControl.AWR_GET_AVAIL_CMD +
      GnrcFormat.SetWidth("1030415", 7) +   // dep. date in Airware yyyMMdd format
      GnrcFormat.SetWidth("LHRHNL" , 6) +   // dep. & arr. city
      GnrcFormat.SetWidth("0700"   , 6) +   // dep. time in HHmmss format
      GnrcFormat.SetWidth(""       , 6) +   // arr. time in HHmmss format
      GnrcFormat.SetWidth(""       , 7) +   // arr. date in Airware yyyMMdd format
      GnrcFormat.SetWidth(""       , 2) +   // inventory code -- not used
      GnrcFormat.SetWidth("BA"     , 3) +   // preferred carrier
      GnrcFormat.SetWidth(""       , 5) +   // flight number
      GnrcFormat.SetWidth("9"      , 3) +   // number of itins
      GnrcFormat.SetWidth("X"      , 1) +   // ByClass flag
      GnrcFormat.SetWidth("F"      , 1) +   // AvailType -- (D)irect, (F)ilter, (N)eutral - default is N
      GnrcFormat.SetWidth("X"      , 1) +   // Quality Flag
      GnrcFormat.SetWidth(""       , 2)      // Private Class
    );
    */
    
    /*
    // private class of service
    req.addElement(
      GnrcConvControl.AWR_GET_AVAIL_CMD +
      GnrcFormat.SetWidth("1021014", 7) +   // dep. date in Airware yyyMMdd format
      GnrcFormat.SetWidth("ANCMIA" , 6) +   // dep. & arr. city
      GnrcFormat.SetWidth(""       , 6) +   // dep. time in HHmmss format
      GnrcFormat.SetWidth(""       , 6) +   // arr. time in HHmmss format
      GnrcFormat.SetWidth(""       , 7) +   // arr. date in Airware yyyMMdd format
      GnrcFormat.SetWidth(""       , 2) +   // inventory code -- not used
      GnrcFormat.SetWidth("NW"     , 3) +   // preferred carrier
      GnrcFormat.SetWidth(""       , 5) +   // flight number
      GnrcFormat.SetWidth("10"     , 3) +   // number of itins
      GnrcFormat.SetWidth(""       , 1) +   // ByClass flag
      GnrcFormat.SetWidth("D"      , 1) +   // AvailType -- (D)irect, (F)ilter, (N)eutral
      GnrcFormat.SetWidth("D"      , 1) +   // Quality Flag
      GnrcFormat.SetWidth("T"      , 2)     // Private Class
    );
    */
   // the following requests return different kind of errors:

    // returns 'NO MORE HA SVC THIS TIME PERIOD'
    // req.addElement("AIRAVAIL1011114DFWSEA100000               DL      010F F"); 
    // req.addElement("AIRAVAIL1011020DFWLIT150000               DL      010F F"); 

    // returns 'LOST RESPONSE' error
    // req.addElement("AIRAVAIL1010822LAXLGA010000               DL      010F F"); 

    // returns local error code
    // req.addElement("AIRAVAIL1010917SEALIT070000               UA      010F F");
    // req.addElement("AIRAVAIL1021004MIASFO005000               UA      10 XDX");

    /**
     ***********************************************************************
     * Flight Information Request
     ***********************************************************************
     */

    // req.addElement(GnrcConvControl.GET_FLIGHT_INFO_CMD  + "AA 100        20011019");
    // req.addElement(GnrcConvControl.AWR_FLIFO_CMD + "UA 854  1020320MIASFO");
    // req.addElement(GnrcConvControl.AWR_FLIFO_CMD +  "AA 1510 1020505SFODFWN");
    
    /*
    req.addElement(
      GnrcConvControl.AWR_FLIFO_CMD    +
      GnrcFormat.SetWidth("NW"     ,3) + // carrier
      GnrcFormat.SetWidth("434"    ,5) + // flight number
      GnrcFormat.SetWidth("1030330",7) + // dep date
      GnrcFormat.SetWidth("DTW"    ,3) + // dep city
      GnrcFormat.SetWidth("MCO"    ,3) + // arr city
      GnrcFormat.SetWidth("N"      ,1) + // dayOf flag
    "");
    */
    
    /**
     ***********************************************************************
     * Check Legal Connect Times
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.GET_CONN_TM_CMD +
      GnrcFormat.SetWidth("1A",2)         +
      // add one or more *pairs* of segments to be checked
      GnrcFormat.SetWidth("DL",2)         +
      GnrcFormat.SetWidth("272",5)       +
      GnrcFormat.SetWidth("20031204",8)    +
      GnrcFormat.SetWidth("SFOATL",6)     +
      GnrcFormat.SetWidth("USAUSA",6)     +

      GnrcFormat.SetWidth("DL",2)         +
      GnrcFormat.SetWidth("1487",5)       +
      GnrcFormat.SetWidth("20031204",8)    +
      GnrcFormat.SetWidth("ATLMIA",6)     +
      GnrcFormat.SetWidth("USAUSA",6)     +
    "");
    */

    /**
     ***********************************************************************
     * Get PNR requests (It is necessary to retrieve a PNR before adding
     * other elements to an already-built PNR)
     ***********************************************************************
     */
    // Airware always sends a StartSess and Ignore Commands when beginning a
    // session, so always include this to make sure that it does not break
    // anything

    // req.addElement(GnrcConvControl.AWR_STARTSES_CMD);
    // req.addElement(GnrcConvControl.AWR_IGNORE_CMD);
    // req.addElement(GnrcConvControl.AWR_GET_PNR_CMD + "ZEQWQL");
    // req.addElement(GnrcConvControl.AWR_GET_PNR2_CMD + "ZE3P45");
    // req.addElement(GnrcConvControl.AWR_GET_PNR2_CMD + "ZEQWQL");

    /**
     ***********************************************************************
     * Add Phone
     ***********************************************************************
     */
    /*
    req.addElement(
      GnrcConvControl.AWR_ADD_PHONE_CMD  +
      GnrcFormat.SetWidth("",8)  +                  // a locator
      GnrcFormat.SetWidth("OAK415-924-6374-H",90)   // a phone string
    );
    */
    /**
     ***********************************************************************
     * Add Received From
     ***********************************************************************
     */
    /*
    req.addElement(
      GnrcConvControl.AWR_RCV_PNR_CMD    +
      GnrcFormat.SetWidth("DAVE",69)        // a name
    );
    */
    /**
     ***********************************************************************
     * Add Ticketing Instructions
     ***********************************************************************
     */
    /*
    req.addElement(
        GnrcConvControl.AWR_ADD_TICKET_CMD + 
        GnrcFormat.SetWidth("",8) +          // a locator
        GnrcFormat.SetWidth("OK",40)         // ticketing instructions 
      );
    */
    /**
     ***********************************************************************
     * Add Corporate Header (must be added prior to names) ?
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.AWR_GRP_HDR_CMD    +
    GnrcFormat.SetWidth("003",3)         +        // number of seats
      GnrcFormat.SetWidth("DISNEY",50)          // group name
   );
    */
    /**
     ***********************************************************************
     * Add Name
     ***********************************************************************
     */
    /*
    req.addElement(
      GnrcConvControl.AWR_ADD_NAME_CMD   + 
      GnrcFormat.SetWidth("",8) +                  // record locator (if one exists)

      // the following can repeat in order to add multiple passengers
      GnrcFormat.SetWidth("MARTIN JR/ERIC",60) +        // Last/First Psgr Name
      GnrcFormat.SetWidth("ADTADT", 6)      +      // Native and Generic Psgr Type Code
      GnrcFormat.SetWidth("1001",20) +       // Airware's Psgr ID
      GnrcFormat.SetWidth("",30) +                   // name of infant

      GnrcFormat.SetWidth("LUKSZA/CHRISTINE",60) + 
      GnrcFormat.SetWidth("ADTADT", 6)      +      
      GnrcFormat.SetWidth("678901",20) +       
      GnrcFormat.SetWidth("NOAH",30) +                  

      GnrcFormat.SetWidth("PARAVICINI/PHILIPPE",60) + 
      GnrcFormat.SetWidth("ADTADT", 6)      +      
      GnrcFormat.SetWidth("678902",20) +       
      GnrcFormat.SetWidth("",30) +                   
   "");
    */
    /**
     ***********************************************************************
     * Change Name
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.AWR_CHG_NAME_CMD     + 
        GnrcFormat.SetWidth("Z4G6IK",8)          + 
        // this section can be repeated multiple times
        GnrcFormat.SetWidth("123456701 G0054",20) +       // the passengerID
        GnrcFormat.SetWidth("JONES/SUEANN",60) +       // the new passenger name (must be in proper format)
        GnrcFormat.SetWidth("CHD",3)             +       // the new Passenger Type Code
        GnrcFormat.SetWidth("",30)               +       // the new name of the accompanying infant
    "");
    */
    
    /*   
    req.addElement(GnrcConvControl.CHG_NAME_CMD +
        GnrcFormat.SetWidth("ZREXKE",8) +
        GnrcFormat.SetWidth("54789",20) +
        GnrcFormat.SetWidth("LAWRENCE/BOB",60) +
        GnrcFormat.SetWidth("ADT",3));
    */

    //    req.addElement(GnrcConvControl.AWR_CHG_NAME_CMD     + "  " + GnrcFormat.SetWidth("1234",20) + GnrcFormat.SetWidth("GUAVA/MARLINA",60) + "ADT" + GnrcFormat.SetWidth("",30) );

    /**
     ***********************************************************************
     * Add Air Segment
     ***********************************************************************
     */
    /*
    // sell short itinerary: one active segment - one passive segment
    req.addElement(
        GnrcConvControl.AWR_ADD_AIRSEG_CMD + 
        GnrcFormat.SetWidth("",8)          +  // locator
        // repeat this section to add multiple segments
        GnrcFormat.SetWidth("AA",3)        +  // carrier
        GnrcFormat.SetWidth("2008",5)      +  // flight number
        GnrcFormat.SetWidth("SFOMIA",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20021215",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("NN",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("",8)          +  // Remote Locator
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("",1)          +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("",1)          +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("UA",3)        +  // carrier
        GnrcFormat.SetWidth("866",5)       +  // flight number
        GnrcFormat.SetWidth("MIASFO",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20021221",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Q",2)         +  // Inv. Class
        GnrcFormat.SetWidth("GK",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("ABLOCK",8)    +  // Remote Locator
        GnrcFormat.SetWidth("REG",3)       +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("P",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("",1)          +  // isScheduledFlight (T - default)
      "");
    */

    // sell short itinerary with ARNK segment
    /*
    req.addElement(
        GnrcConvControl.AWR_ADD_AIRSEG_CMD + 
        GnrcFormat.SetWidth("",8)          +  // locator
        // repeat this section to add multiple segments
        GnrcFormat.SetWidth("AA",3)        +  // carrier
        GnrcFormat.SetWidth("2008",5)      +  // flight number
        GnrcFormat.SetWidth("SFOMIA",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20030615",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("NN",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("",8)          +  // Remote Locator
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("A",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("",3)          +  
        GnrcFormat.SetWidth("",5)          + 
        GnrcFormat.SetWidth("",6)          +
        GnrcFormat.SetWidth("",8)          +
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("",2)          +  
        GnrcFormat.SetWidth("ARNK",4)      + 
        GnrcFormat.SetWidth("",3)          +
        GnrcFormat.SetWidth("",8)          +  
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("",1)          +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("",1)          +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("AA",3)        +  // carrier
        GnrcFormat.SetWidth("2825",5)      +  // flight number
        GnrcFormat.SetWidth("FLLSFO",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20030621",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("NN",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("",8)          +  // Remote Locator
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("A",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)
      "");
      */
    
    /*
    // sell from block
    req.addElement(
        GnrcConvControl.AWR_ADD_AIRSEG_CMD + 
        GnrcFormat.SetWidth("",8)          +  // locator
        // repeat this section to add multiple segments
        GnrcFormat.SetWidth("AA",3)        +  // carrier
        GnrcFormat.SetWidth("2008",5)      +  // flight number
        GnrcFormat.SetWidth("SFOMIA",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20030401",8)  +  // Departure Date
        GnrcFormat.SetWidth("0708",4)      +  // Departure Time
        GnrcFormat.SetWidth("20020401",8)  +  // Arrival Date
        GnrcFormat.SetWidth("1516",4)      +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("SG",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("BBWJB2",8)    +  // Remote Locator
        GnrcFormat.SetWidth("MNG",3)       +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("P",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("UA",3)        +  // carrier
        GnrcFormat.SetWidth("0872",5)      +  // flight number
        GnrcFormat.SetWidth("MIASFO",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20030401",8)  +  // Departure Date
        GnrcFormat.SetWidth("0310",4)      +  // Departure Time
        GnrcFormat.SetWidth("20020401",8)  +  // Arrival Date
        GnrcFormat.SetWidth("1024",4)      +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("SG",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("BBWJB3",8)    +  // Remote Locator
        GnrcFormat.SetWidth("MNG",3)       +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("P",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)
    "");
    */
    
    // sell long itinerary: 
    // 2 single segments, one married segment, one passive segment
    /*
    req.addElement(
        GnrcConvControl.AWR_ADD_AIRSEG_CMD + 
        GnrcFormat.SetWidth("",8)          +  // locator
        // repeat this section to add multiple segments
        GnrcFormat.SetWidth("AA",3)        +  // carrier
        GnrcFormat.SetWidth("2008",5)      +  // flight number
        GnrcFormat.SetWidth("SFOMIA",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20021215",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("SG",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("",8)          +  // Remote Locator
        GnrcFormat.SetWidth("  ",3)        +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("A",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("UA",3)        +  // carrier
        GnrcFormat.SetWidth("391",5)       +  // flight number
        GnrcFormat.SetWidth("MIADEN",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20021218",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("SG",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("",8)          +  // Remote Locator
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("A",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("",3)          +  
        GnrcFormat.SetWidth("",5)          + 
        GnrcFormat.SetWidth("",6)          +
        GnrcFormat.SetWidth("",8)          +
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("",2)          +  
        GnrcFormat.SetWidth("ARNK",4)      + 
        GnrcFormat.SetWidth("",3)          +
        GnrcFormat.SetWidth("",8)          +  
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("",1)          +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("",1)          +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("NW",3)        +  
        GnrcFormat.SetWidth("1928",5)      + 
        GnrcFormat.SetWidth("DTWJFK",6)    +
        GnrcFormat.SetWidth("20021225",8)  +
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  
        GnrcFormat.SetWidth("SG",4)        + 
        GnrcFormat.SetWidth("004",3)       +
        GnrcFormat.SetWidth("",8)          +  
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("A",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("ZZ",3)        +  
        GnrcFormat.SetWidth("1234",5)      + 
        GnrcFormat.SetWidth("JFKSFO",6)    +
        GnrcFormat.SetWidth("20021228",8)  +
        GnrcFormat.SetWidth("0800",4)      +  // Departure Time
        GnrcFormat.SetWidth("20021228",8)  +  // Arrival Date
        GnrcFormat.SetWidth("1112",4)      +  // Arrival Time
        GnrcFormat.SetWidth("Q",2)         +  
        GnrcFormat.SetWidth("GK",4)        + 
        GnrcFormat.SetWidth("004",3)       +
        GnrcFormat.SetWidth("BLOCK",8)     +  
        GnrcFormat.SetWidth("REG",3)       +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("P",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("F",1)         +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("",3)          +  
        GnrcFormat.SetWidth("",5)          + 
        GnrcFormat.SetWidth("",6)          +
        GnrcFormat.SetWidth("",8)          +
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("",2)          +  
        GnrcFormat.SetWidth("ARNK",4)      + 
        GnrcFormat.SetWidth("",3)          +
        GnrcFormat.SetWidth("",8)          +  
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("",1)          +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("",1)          +  // isScheduledFlight (T - default)
   "");
   */
    /**
     ***********************************************************************
     * Save and Retrieve Locator
     ***********************************************************************
     */

    //req.addElement(GnrcConvControl.AWR_END_XACT_CMD );

    /**
     ***********************************************************************
     * get PNR By Locator
     ***********************************************************************
     */

    /*
    req.addElement(
      GnrcConvControl.AWR_GET_PNR2_CMD + 
      GnrcFormat.SetWidth("YCD6U8",8) +   // locator
      GnrcFormat.SetWidth("",10) +        // Queue
      GnrcFormat.SetWidth("",1) +         // no-remarks flag (not used)
      GnrcFormat.SetWidth("",1)           // queue action flag (not used)
    );
    */


    /**
     ***********************************************************************
     * get PNR from Named Queue
     ***********************************************************************
     */
    /*
    req.addElement(
      GnrcConvControl.AWR_GET_PNR_CMD + 
      GnrcFormat.SetWidth("",8)   + // locator
      GnrcFormat.SetWidth("8",10) + // Queue Name
      GnrcFormat.SetWidth("N",1)  + // no-remarks flag (not used)
      GnrcFormat.SetWidth("R",1)    // queue action: 'L' leave - 'R' remove
    );
    */
    
    /**
     ***********************************************************************
     * add Remarks - Airware Format
     ***********************************************************************
     */

    /*
    req.addElement(
      GnrcConvControl.AWR_ADD_REMARK_CMD   +
      GnrcFormat.SetWidth("YF2EBA",8)  +
    */

      /*
      // add SSR Remark
      GnrcFormat.SetWidth("SSR", 4)        +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("VGML",4)        +   // ReqAddRemark.ServiceCode
      GnrcFormat.SetWidth("",128)          +   // ReqAddRemark.Text
      GnrcFormat.SetWidth("",3)            +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("1002",20)       +   // ReqAddRemark.PassengerID
      //GnrcFormat.SetWidth("2345678901",20) +   // ReqAddRemark.PassengerID
      */
      /*
      // add another type of SSR Remark
      GnrcFormat.SetWidth("SSR", 4)        +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("MOML",4)        +   // ReqAddRemark.ServiceCode
      GnrcFormat.SetWidth("",128)          +   // ReqAddRemark.Text
      GnrcFormat.SetWidth("",3)            +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("1002",20)       +   // ReqAddRemark.PassengerID
      //GnrcFormat.SetWidth("2345678901",20) +    // ReqAddRemark.PassengerID
      */
      /*
      // add OSI Remark
      GnrcFormat.SetWidth("OSI", 4)        +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("",4)            +   // empty for OSI
      GnrcFormat.SetWidth("A BOISTEROUS GROUP",128)  +   // ReqAddRemark.Text
      GnrcFormat.SetWidth("",3)            +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("",20)           +   // ReqAddRemark.PassengerID


      // add OSI Remark
      GnrcFormat.SetWidth("OSI", 4)        +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("",4)            +   // empty for OSI
      GnrcFormat.SetWidth("NEEDS DARKROOM",128) +  // ReqAddRemark.Text
      GnrcFormat.SetWidth("UA",3)          +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("1004",20)       +   // ReqAddRemark.PassengerID


      // add Itin Remark
      GnrcFormat.SetWidth("ITIN", 4)       +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("",4)            +   // empty for Itinerary Remarks
      GnrcFormat.SetWidth("AN ITIN REMARK",128) +  // ReqAddRemark.Text
      GnrcFormat.SetWidth("AA",3)          +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("",20)           +   // ReqAddRemark.PassengerID
      //GnrcFormat.SetWidth("2345678901",20) +   // ReqAddRemark.PassengerID


      // add Invoice Remark
      GnrcFormat.SetWidth("INV", 4)        +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("",4)            +   // empty for Itinerary Remarks
      GnrcFormat.SetWidth("AN INVOICE REMARK",128)  +   // ReqAddRemark.Text
      GnrcFormat.SetWidth("YY",3)          +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("    ",20)       +   // ReqAddRemark.PassengerID
      //GnrcFormat.SetWidth("2345678901",20) +   // ReqAddRemark.PassengerID

      */
      /*
      // addGeneralRemark
      GnrcFormat.SetWidth("", 4)           +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("",4)            +   // ReqAddRemark.Service
      GnrcFormat.SetWidth("*FOBULK FARE",128) + // remark text
      GnrcFormat.SetWidth("",3)            +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("",20)           +   // ReqAddRemark.PassengerID
      */
      /*
      // addGeneralRemark
      GnrcFormat.SetWidth("", 4)           +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("",4)            +   // ReqAddRemark.Service
      GnrcFormat.SetWidth("AN1020323",128) + // remark text
      GnrcFormat.SetWidth("",3)            +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("",20)           +   // ReqAddRemark.PassengerID

      // addGeneralRemark
      GnrcFormat.SetWidth("", 4)           +   // ReqAddRemark.Section
      GnrcFormat.SetWidth("",4)            +   // ReqAddRemark.Service
      GnrcFormat.SetWidth("DVXMAXOUTQ",128) + // remark text
      GnrcFormat.SetWidth("",3)            +   // ReqAddRemark.Carrier
      GnrcFormat.SetWidth("",20)           +   // ReqAddRemark.PassengerID
      */
    //""); // end addRemarks

    /**
     ***********************************************************************
     * Fare command
     ***********************************************************************
     */

    // req.addElement(GnrcConvControl.GET_FARE_CMD + "1A" + GnrcFormat.SetWidth("ZK5AJ2",8) + "RF" + GnrcFormat.SetWidth("",40) + GnrcFormat.SetWidth("0034566",20) + GnrcFormat.SetWidth("0034567",20) + GnrcFormat.SetWidth("0034568",20) + GnrcFormat.SetWidth("",340) + GnrcFormat.SetWidth("",60) );
    // req.addElement(GnrcConvControl.GET_FARE_CMD + "1A" + GnrcFormat.SetWidth("Y7QV4J",8) + "RF" + GnrcFormat.SetWidth("",40) + GnrcFormat.SetWidth("00067890",20) + GnrcFormat.SetWidth("",20) + GnrcFormat.SetWidth("",20) + GnrcFormat.SetWidth("",340) + GnrcFormat.SetWidth("",60) );

   
   /*
   req.addElement(GnrcConvControl.GET_FARE_CMD +
      GnrcFormat.SetWidth("1P",2)       +  // crs code
      GnrcFormat.SetWidth("",8)         +  // PNR locator
      GnrcFormat.SetWidth("LF",2)       +  // fare and storage flag (2 chars) 
      GnrcFormat.SetWidth("",40)        +  // Segment List (2 numbers, up to 20 segments)
      GnrcFormat.SetWidth("023464302",20) +  // passenger ID's as generated by Airware (each can take 20 char)
      GnrcFormat.SetWidth("",20)        +  // more passenger ID
      GnrcFormat.SetWidth("",20)        +  // more passenger ID 
      GnrcFormat.SetWidth("",340)       +  // the rest of the passenger ID's
      GnrcFormat.SetWidth("",60)     +  // the PTC codes, each can take 3 chars
    "");    
    */
    
   /*
   req.addElement(GnrcConvControl.GET_FARE_CMD +
      GnrcFormat.SetWidth("1A",2)       +  // crs code
      GnrcFormat.SetWidth("YOEC6T",8)   +  // PNR locator
      GnrcFormat.SetWidth("LF",2)       +  // fare and storage flag (2 chars) 
      GnrcFormat.SetWidth("",40)        +  // Segment List (2 numbers, up to 20 segments)
      GnrcFormat.SetWidth("",20) +  // passenger ID's as generated by Airware (each can take 20 char)
      GnrcFormat.SetWidth("",20)        +  // more passenger ID
      GnrcFormat.SetWidth("",20)        +  // more passenger ID 
      GnrcFormat.SetWidth("",20)        +  // more passenger ID 
      GnrcFormat.SetWidth("",20)        +  // more passenger ID 
      GnrcFormat.SetWidth("",300)       +  // the rest of the passenger ID's
      GnrcFormat.SetWidth("",60)     +  // the PTC codes, each can take 3 chars
    "");    
    */
     
    /*
    req.addElement(GnrcConvControl.AWR_FARE_CMD +
      GnrcFormat.SetWidth("YTRYIG",8)     +  // PNR locator
      GnrcFormat.SetWidth("123456702",20) +  // passenger ID's as generated by Airware (each can take 20 char)
      GnrcFormat.SetWidth("",20)          +  // more passenger ID
      GnrcFormat.SetWidth("",20)          +  // more passenger ID 
      GnrcFormat.SetWidth("",340)         +  // the rest of the passenger ID's
      GnrcFormat.SetWidth("F",1)          +  // Lowest fare
      GnrcFormat.SetWidth("",40)          +  // Segment List (2 numbers, up to 20 segments)
    "");    
    */

    /**
     ***********************************************************************
     * Add Form of Payment (FOP)
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.AWR_ADD_FOP_CMD      +
        GnrcFormat.SetWidth("",8) +                  // a locator
        GnrcFormat.SetWidth("CCVI4111111111111111/0802",126)      // a Form of Payment
    );
    */
    /*
    req.addElement(GnrcConvControl.AWR_ADD_FOP_CMD      +
        GnrcFormat.SetWidth("",8) +                  // a locator
        GnrcFormat.SetWidth("CH",126)      // a Form of Payment
    );
    */
    /*
    req.addElement(GnrcConvControl.AWR_ADD_FOP_CMD      +
        GnrcFormat.SetWidth("",8) +                  // a locator
        GnrcFormat.SetWidth("MSNOMONEY",126)      // a Form of Payment
    );
    */
    /**
     ***********************************************************************
     * Add Commission
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.AWR_ADD_COMM_CMD     + 
      GnrcFormat.SetWidth("RVURPP  ",8) +      // the locator (not used by Airware)
      GnrcFormat.SetWidth("25.99",7)     +     // a 0.00 decimal amount
      GnrcFormat.SetWidth("A",1)               // if "A", the amount is a fixed literal amount
    );           
    */
    /*
    req.addElement(GnrcConvControl.AWR_ADD_COMM_CMD     + 
      GnrcFormat.SetWidth("RVURPP  ",8) +      // the locator (not used by Airware)
      GnrcFormat.SetWidth("5.25",7)     +     // a 0.00 decimal amount
      GnrcFormat.SetWidth("",1)               // if "!A", the amount represents a percentage
    );           
    */
    /**
     ***********************************************************************
     * Add Tour Code
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.AWR_ADD_TOURCODE_CMD + 
        GnrcFormat.SetWidth("ZYHQP3",8)          +        // a locator
        GnrcFormat.SetWidth("IT1DL34567",126)             // a tour code
    );
    */
    /**
     ***********************************************************************
     * Add Endorsement Remark
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.AWR_ADD_ENDORSE_CMD + 
        GnrcFormat.SetWidth("ZYHQP3",8)                +  // a locator
        GnrcFormat.SetWidth("IT1DL34567",126)             // a tour code
    );
    req.addElement(GnrcConvControl.AWR_ADD_ENDORSE_CMD  + "        " + GnrcFormat.SetWidth("BY XMAX",126) );
    */
    /**
     ***********************************************************************
     * Queue PNR
     ***********************************************************************
     */
    
    /*
    req.addElement(GnrcConvControl.AWR_QUEUE_PNR_CMD    +
      GnrcFormat.SetWidth("YUJQVO",8) + // a locator
      GnrcFormat.SetWidth("8",20)       // a Queue Name
    );
    */

    /*
    req.addElement(
      GnrcConvControl.AWR_GET_PNR2_CMD + 
      GnrcFormat.SetWidth("",8)   + // locator
      GnrcFormat.SetWidth("8",10) + // Queue Name
      GnrcFormat.SetWidth("N",1)  + // no-remarks flag (not used)
      GnrcFormat.SetWidth("L",1)    // queue action: 'L' leave - 'R' remove
    );
    
    req.addElement(
      GnrcConvControl.AWR_GET_PNR2_CMD + 
      GnrcFormat.SetWidth("",8)   + // locator
      GnrcFormat.SetWidth("8",10) + // Queue Name
      GnrcFormat.SetWidth("N",1)  + // no-remarks flag (not used)
      GnrcFormat.SetWidth("R",1)    // queue action: 'L' leave - 'R' remove
    );
    */

  /**
   ***********************************************************************
   * Ticket PNR
   ***********************************************************************
   */
    
    /*
    req.addElement(GnrcConvControl.AWR_TKT_PNR_CMD + 
      GnrcFormat.SetWidth("1A",2)     +     // crs code
      GnrcFormat.SetWidth("ZCXFW2",8) +     // locator
      GnrcFormat.SetWidth("C",1)      +     // fare type 
      GnrcFormat.SetWidth("0",5)  +     // commission percentage
      GnrcFormat.SetWidth("0",5)  +     // commission amount
      GnrcFormat.SetWidth("  ",2)     +     // validating carrier
      GnrcFormat.SetWidth("",40)  +  // segment list (2 digits per segment)
      GnrcFormat.SetWidth("",20)  +// passenger ids (20 chars per passenger)
      GnrcFormat.SetWidth("",380)     +     // passenger ids (20 chars per passenger)
      GnrcFormat.SetWidth("",60)+     // passenger PTCs (3 char per passenger)
      GnrcFormat.SetWidth("",126)     +     // tour code
      GnrcFormat.SetWidth("",126)     +     // endorsement
      GnrcFormat.SetWidth("",126)     +     // form of payment
      GnrcFormat.SetWidth("F",1)      +     // mini-itin flag
      GnrcFormat.SetWidth("T",1)      +     // e-ticket flag
      GnrcFormat.SetWidth("F",1)      +     // invoice flag
      GnrcFormat.SetWidth("",20)      +     // psgrDocPrinter
      GnrcFormat.SetWidth("",20)            // agencyDocPrinter
    );
    */
    

    /*
    req.addElement(GnrcConvControl.AWR_TKT_PNR_CMD + 
      GnrcFormat.SetWidth("1A",2)     +     // crs code
      GnrcFormat.SetWidth("YUOWIV",8) +     // locator
      GnrcFormat.SetWidth("C",1)      +     // fare type 
      GnrcFormat.SetWidth("00000",5)  +     // commission percentage
      GnrcFormat.SetWidth("00000",5)  +     // commission amount
      GnrcFormat.SetWidth("UA",2)     +     // validating carrier
      GnrcFormat.SetWidth("",40) +  // segment list (2 digits per segment)
      GnrcFormat.SetWidth("1001",400) +// passenger ids (20 chars per passenger)
      GnrcFormat.SetWidth("ADT",60)   +     // passenger PTCs (3 char per passenger)
      GnrcFormat.SetWidth("",126)     +     // tour code
      GnrcFormat.SetWidth("",126)     +     // endorsement
      GnrcFormat.SetWidth("CCTP100615027000011/0902",126)+     // form of payment
      GnrcFormat.SetWidth("T",1)       +     // mini-itin flag
      GnrcFormat.SetWidth("F",1)       +     // e-ticket flag
      GnrcFormat.SetWidth("F",1)       +     // invoice flag
      GnrcFormat.SetWidth("",20)      +     // psgrDocPrinter
      GnrcFormat.SetWidth("",20)            // agencyDocPrinter
    );
    */
    /**
     ***********************************************************************
     * Ignore, Start Session, End Session and Freeform
     ***********************************************************************
     */
     //req.addElement(GnrcConvControl.AWR_STARTSES_CMD);
     //req.addElement(GnrcConvControl.AWR_IGNORE_CMD);
     //req.addElement(GnrcConvControl.AWR_ENDSES_CMD);
     
     /*
     req.addElement(GnrcConvControl.FREEFORM_CMD + 
         GnrcFormat.SetWidth("QT",80) );
     req.addElement(GnrcConvControl.FREEFORM_CMD + 
         GnrcFormat.SetWidth("MD",80) );
     req.addElement(GnrcConvControl.FREEFORM_CMD + 
         GnrcFormat.SetWidth("MD",80) );
     req.addElement(GnrcConvControl.FREEFORM_CMD + 
         GnrcFormat.SetWidth("MD",80) );
     req.addElement(GnrcConvControl.FREEFORM_CMD + 
         GnrcFormat.SetWidth("MD",80) );
     req.addElement(GnrcConvControl.FREEFORM_CMD + 
         GnrcFormat.SetWidth("MD",80) );
     req.addElement(GnrcConvControl.FREEFORM_CMD + 
         GnrcFormat.SetWidth("MD",80) );
     */
     

    /**
     ***********************************************************************
     * cancel Air Segments and Itinerary
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.CXL_SEG_CMD + 
        GnrcFormat.SetWidth("1A",2) +           // crs code
        GnrcFormat.SetWidth("ZNSB6O",8) +       // locator
        GnrcFormat.SetWidth("GEORGE BUSH",20) +   // Received By

        // the following structure may repeat for multiple segments
        //GnrcFormat.SetWidth("AA",2)        +    // carrier
        //GnrcFormat.SetWidth("2008",5)      +    // flight number
        //GnrcFormat.SetWidth("SFOMIA",6)    +    // Dep&Arr Cities
        //GnrcFormat.SetWidth("20020315",8)  +    // Departure Date

        GnrcFormat.SetWidth("UA",2)        +
        GnrcFormat.SetWidth("0854",5)      +
        GnrcFormat.SetWidth("MIASFO",6)    +
        GnrcFormat.SetWidth("20020320",8)  +
    "");
    */
    /*
    req.addElement(GnrcConvControl.CXL_ITIN_CMD +
        GnrcFormat.SetWidth("1A",2) +
        GnrcFormat.SetWidth("YUZWHZ",8) +
        GnrcFormat.SetWidth("GEORGE BUSH",20)
    );
    */
  /**
   ***********************************************************************
   * Cancel Remarks
   ***********************************************************************
   */
    /*
    req.addElement(GnrcConvControl.AWR_CXL_REMARK_CMD   + 
        GnrcFormat.SetWidth("1A",2)          + // CRS Code
        GnrcFormat.SetWidth("Y2AU5K",8)      + // a locator
        GnrcFormat.SetWidth("AIRWARE",20) + // a locator
        GnrcFormat.SetWidth("GENE",4)        + // the type of remark
        GnrcFormat.SetWidth("*FOBULK FARE",100) + // the text of the remark
        GnrcFormat.SetWidth("",20)           + // the passenger ID
        GnrcFormat.SetWidth("CO",2)            + // the carrier code
        GnrcFormat.SetWidth("",4)            + // the service code for SSR remarks
    "");
    */
    
    /*
    req.addElement(GnrcConvControl.DEL_RMK_CMD +
      GnrcFormat.SetWidth("1A",2)        + // CRS Code
      GnrcFormat.SetWidth("X5OZIU",8)    + // locator
      GnrcFormat.SetWidth("AIRWARE",20)  + // Received from
      GnrcFormat.SetWidth("GENE",10)     + // ReqDeleteRemark.Type
      GnrcFormat.SetWidth("*U1-",100)    + // ReqDeleteRemark.Text
      GnrcFormat.SetWidth("",20)         + // ReqDeleteRemark.PassengerID
      GnrcFormat.SetWidth("DL",2)        + // ReqDeleteRemark.Carrier
      GnrcFormat.SetWidth("",4)          + // ReqDeleteRemark.ServiceCode
    "");
    */

    /*
    req.addElement(GnrcConvControl.AWR_CXL_REMARK_CMD   + 
        GnrcFormat.SetWidth("",8)       +   // a locator
        GnrcFormat.SetWidth("GEN",4)    +   // the type of remark
        GnrcFormat.SetWidth("",4)       +   // the service code for SSR remarks
        GnrcFormat.SetWidth("A GENERAL REMARK OF ANY SORT",128) // the text of the remark
    );
    */
    /*
    req.addElement(GnrcConvControl.AWR_CXL_REMARK_CMD   + "TBGOQP  " + "PHNE" + "    " + GnrcFormat.SetWidth("769-9146",128) );
    req.addElement(GnrcConvControl.AWR_CXL_REMARK_CMD   + "TBGOQP  " + "TKT " + "    " + GnrcFormat.SetWidth("29AUG",128) );
    req.addElement(GnrcConvControl.AWR_CXL_REMARK_CMD   + "XWWJQT  " + "SSR " + "VGML" + GnrcFormat.SetWidth("",128) );
    req.addElement(GnrcConvControl.AWR_CXL_REMARK_CMD   + "YGVLU3  " + "GEN " + "    " + GnrcFormat.SetWidth("IDIOT",128) );
    */

    /*
     ***********************************************************************
     * Split PNR
     ***********************************************************************
     */
    
    /*
    req.addElement(GnrcConvControl.SPLIT_PNR_CMD +
        GnrcFormat.SetWidth("1A",2)         +  // crs code
        GnrcFormat.SetWidth("ZDYXD8",8)     +  // locator
        GnrcFormat.SetWidth("",3)           +  // Number of Unassigned Seats to be split
        // the following may repeat multiple times
        GnrcFormat.SetWidth("000055703",20)       +  // Psgr ID to be split
    "");
    */

    /*
     ***********************************************************************
     * Change PNR Itin
     ***********************************************************************
     */
    
    /*
    req.addElement(GnrcConvControl.CHG_PNR_ITIN_CMD +
      GnrcFormat.SetWidth("1A",2)        +  // crs code
      GnrcFormat.SetWidth("ZRXC86",8)    +  // locator
      GnrcFormat.SetWidth("TRANSERVER",20)+ // ReceiveBy
      
      // the passengers affected by the changes (none specified indicates all psgr)
      GnrcFormat.SetWidth("",20)     +  // Psgr ID to be split
      GnrcFormat.SetWidth("",20)     +
      GnrcFormat.SetWidth(""   ,360)     +  // Psgr must repeat twenty times

      // the segments to be cancelled
      GnrcFormat.SetWidth("UA",2)        +    // carrier
      GnrcFormat.SetWidth("290",5)       +    // flight number
      GnrcFormat.SetWidth("SFOATL",6)    +    // Dep&Arr Cities
      GnrcFormat.SetWidth("20030317",8)  +    // Departure Date

      GnrcFormat.SetWidth("",2)        +    // carrier
      GnrcFormat.SetWidth("",5)      +    // flight number
      GnrcFormat.SetWidth("",6)    +    // Dep&Arr Cities
      GnrcFormat.SetWidth("",8)  +    // Departure Date

      GnrcFormat.SetWidth("",2)        +
      GnrcFormat.SetWidth("",5)      +
      GnrcFormat.SetWidth("",6)    +
      GnrcFormat.SetWidth("",8)  +

      GnrcFormat.SetWidth("",147)        +   // cxl seg must repeat ten times

      // the segments to be added
      GnrcFormat.SetWidth("UA",3)        +  // carrier
      GnrcFormat.SetWidth("290",5)       +  // flight number
      GnrcFormat.SetWidth("SFOATL",6)    +  // Dep&Arr Cities
      GnrcFormat.SetWidth("20030317",8)  +  // Departure Date
      GnrcFormat.SetWidth("",4)          +  // Departure Time
      GnrcFormat.SetWidth("",8)          +  // Arrival Date
      GnrcFormat.SetWidth("",4)          +  // Arrival Time
      GnrcFormat.SetWidth("Q",2)         +  // Inv. Class
      GnrcFormat.SetWidth("SG",4)        +  // Action Code
      GnrcFormat.SetWidth("004",3)       +  // NumSeats
      GnrcFormat.SetWidth("",8)          +  // Remote Locator
      GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
      GnrcFormat.SetWidth("A",1)         +  // Passive (P) or Active (A - default)
      GnrcFormat.SetWidth("T",1)         +  // isScheduledFlight (T - default)

      GnrcFormat.SetWidth("",3)        +  // carrier
      GnrcFormat.SetWidth("",5)      +  // flight number
      GnrcFormat.SetWidth("",6)    +  // Dep&Arr Cities
      GnrcFormat.SetWidth("",8)  +  // Departure Date
      GnrcFormat.SetWidth("",4)          +  // Departure Time
      GnrcFormat.SetWidth("",8)          +  // Arrival Date
      GnrcFormat.SetWidth("",4)          +  // Arrival Time
      GnrcFormat.SetWidth("",2)         +  // Inv. Class
      GnrcFormat.SetWidth("",4)        +  // Action Code
      GnrcFormat.SetWidth("",3)       +  // NumSeats
      GnrcFormat.SetWidth("",8)          +  // Remote Locator
      GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
      GnrcFormat.SetWidth("",1)         +  // Passive (P) or Active (A - default)
      GnrcFormat.SetWidth("",1)         +  // isScheduledFlight (T - default)

      GnrcFormat.SetWidth("",3)        +  
      GnrcFormat.SetWidth("",5)       +  
      GnrcFormat.SetWidth("",6)    +
      GnrcFormat.SetWidth("",8)  + 
      GnrcFormat.SetWidth("",4)          +  
      GnrcFormat.SetWidth("",8)          +  
      GnrcFormat.SetWidth("",4)          +  
      GnrcFormat.SetWidth("",2)         +
      GnrcFormat.SetWidth("",4)        +
      GnrcFormat.SetWidth("",3)       +
      GnrcFormat.SetWidth("",8)          +
      GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
      GnrcFormat.SetWidth("",1)         +  // Passive (P) or Active (A - default)
      GnrcFormat.SetWidth("",1)         +  // isScheduledFlight (T - default)

      GnrcFormat.SetWidth("",420)        +  // added segments repeat ten times
      "");
   */
        
    /**
     ***********************************************************************
     * Accept Scheduled Changes
     ***********************************************************************
     */
    //    req.addElement(GnrcConvControl.AWR_ACCEPT_CHG_CMD + "YQWQL4  ");

    /**
     ***********************************************************************
     * Build a block
     ***********************************************************************
     */

    /*
    req.addElement(GnrcConvControl.ADD_BLK_CMD +
        GnrcFormat.SetWidth("1A",2)            + // crs code
        GnrcFormat.SetWidth("TRANSERVER",20)   + // received from
        GnrcFormat.SetWidth("ATOURNAME",40)    + // memo
        GnrcFormat.SetWidth("F",1)             + // isActive flag
        GnrcFormat.SetWidth("",8)              + // sell start date
        GnrcFormat.SetWidth("",8)              + // sell stop date

        // this may repeat multiple times
        GnrcFormat.SetWidth("AA",3)       + // carrier
        GnrcFormat.SetWidth("2008",5)      + // flight number
        GnrcFormat.SetWidth("SFOMIA",6)   + // dep/arr city
        GnrcFormat.SetWidth("20030401",8) + // dep date
        GnrcFormat.SetWidth("0709",4)     + // dep time
        GnrcFormat.SetWidth("20030401",8) + // arr date
        GnrcFormat.SetWidth("1516",4)     + // arr time
        GnrcFormat.SetWidth("Y",2)        + // class of service
        GnrcFormat.SetWidth("NN",4)       + // action code
        GnrcFormat.SetWidth("010",3)      + // number of seats
        GnrcFormat.SetWidth("1V",2)       + // crs Remote Record Locator
        GnrcFormat.SetWidth("AIRLOC3",8)  + // airline locator
        GnrcFormat.SetWidth("",3)         + // Block Type field - not used
        GnrcFormat.SetWidth("",1)         + // passive/active - not used
        GnrcFormat.SetWidth("T",1)        + // isScheduled

        GnrcFormat.SetWidth("UA",3)       + // carrier
        GnrcFormat.SetWidth("0872",5)      + // flight number
        GnrcFormat.SetWidth("MIASFO",6)   + // dep/arr city
        GnrcFormat.SetWidth("20030405",8) + // dep date
        GnrcFormat.SetWidth("0710",4)     + // dep time
        GnrcFormat.SetWidth("20030405",8) + // arr date
        GnrcFormat.SetWidth("1024",4)     + // arr time
        GnrcFormat.SetWidth("Y",2)        + // class of service
        GnrcFormat.SetWidth("NN",4)       + // action code
        GnrcFormat.SetWidth("010",3)      + // number of seats
        GnrcFormat.SetWidth("1V",2)       + // crs Remote Record Locator
        GnrcFormat.SetWidth("AIRLOC4",8)  + // airline locator
        GnrcFormat.SetWidth("",3)         + // Block Type field - not used
        GnrcFormat.SetWidth("",1)         + // passive/active - not used
        GnrcFormat.SetWidth("T",1)        + // isScheduled
     "");
     */

    /*
    //William's bug
    req.addElement(GnrcConvControl.ADD_BLK_CMD +
        GnrcFormat.SetWidth("1A",2)            + // crs code
        GnrcFormat.SetWidth("AIRWARE",20)      + // received from
        GnrcFormat.SetWidth("0820020513",40)   + // block modifier
        GnrcFormat.SetWidth("F",1)             + // isActive flag
        GnrcFormat.SetWidth("20020321",8)      + // sell start date
        GnrcFormat.SetWidth("20020515",8)      + // sell stop date

        // this may repeat multiple times
        GnrcFormat.SetWidth("UA",3)       + // carrier
        GnrcFormat.SetWidth("1541",5)     + // flight number
        GnrcFormat.SetWidth("SEALAX",6)   + // dep/arr city
        GnrcFormat.SetWidth("20025015",8) + // dep date
        GnrcFormat.SetWidth("1205",4)     + // dep time
        GnrcFormat.SetWidth("20020515",8) + // arr date
        GnrcFormat.SetWidth("0205",4)     + // arr time
        GnrcFormat.SetWidth("Q",2)        + // class of service
        GnrcFormat.SetWidth("P",4)        + // action code
        GnrcFormat.SetWidth("005",3)      + // number of seats
        GnrcFormat.SetWidth("1V",2)       + // crs Remote Record Locator
        GnrcFormat.SetWidth("AIR_RLOC",8) + // airline locator

     "");
     */

    /**
     ***********************************************************************
     * Retrieve a block by Locator
     ***********************************************************************
     */
    
    /*
    req.addElement(GnrcConvControl.GET_BLK_CMD +
        GnrcFormat.SetWidth("1A",2)          + // crs code
        GnrcFormat.SetWidth("BBAAW3",8)      + // crs block locator
    "");
    */
    
    /**
     ***********************************************************************
     * Modify a block by Locator
     ***********************************************************************
     */
    /*    
    req.addElement(GnrcConvControl.MOD_BLK_CMD +
        GnrcFormat.SetWidth("1A",2)          + // crs code
        GnrcFormat.SetWidth("BBAAI3",8)      + // crs block locator
        GnrcFormat.SetWidth("12",3)          + // num seats allocated
        GnrcFormat.SetWidth("",8)            + // received-by
    "");
    */
    /**
     ***********************************************************************
     * Delete a block by Locator
     ***********************************************************************
     */

    
    /*
    req.addElement(GnrcConvControl.DEL_BLK_CMD +
        GnrcFormat.SetWidth("1A",2)          + // crs code
        GnrcFormat.SetWidth("BBAAYO",8)      + // crs block locator
        GnrcFormat.SetWidth("",8)            + // received-by
    "");
    */
    
    /*
    req.addElement("CDELBLK 1ABBAAI3          ");
    req.addElement("CDELBLK 1ABBAAI6          ");
    req.addElement("CDELBLK 1ABBAAJJ          ");
    req.addElement("CDELBLK 1ABBAAJL          ");
    req.addElement("CDELBLK 1ABBAAJM          ");
    req.addElement("CDELBLK 1ABBAAJN          ");
    req.addElement("CDELBLK 1ABBAAJO          ");
    req.addElement("CDELBLK 1ABBAAJ2          ");
    req.addElement("CDELBLK 1ABBAAJ4          ");
    req.addElement("CDELBLK 1ABBAAMB          ");
    req.addElement("CDELBLK 1ABBAAMC          ");
    req.addElement("CDELBLK 1ABBAAMN          ");
    req.addElement("CDELBLK 1ABBAAMP          ");
    req.addElement("CDELBLK 1ABBAAXE          ");
    req.addElement("CDELBLK 1ABBAAXG          ");
    req.addElement("CDELBLK 1ABBAAXJ          ");
    req.addElement("CDELBLK 1ABBAAXL          ");
    req.addElement("CDELBLK 1ABBAAXN          ");
    req.addElement("CDELBLK 1ABBAAXP          ");
    req.addElement("CDELBLK 1ABBAAYJ          ");
    req.addElement("CDELBLK 1ABBAAYK          ");
    */

    /**
     ***********************************************************************
     * Retrieve a message from a Block Queue
     ***********************************************************************
     */
    /*
    req.addElement(GnrcConvControl.READ_BLK_MSG_CMD + 
        GnrcFormat.SetWidth("1A",2)   + // crs code
        GnrcFormat.SetWidth("97",10)  + // queue name
        GnrcFormat.SetWidth("43",10)  + // queue name
        GnrcFormat.SetWidth("L",1)    + // 'R' remove msg - 'L' leave msg
    "");
    */
     
    /**
     ***********************************************************************
     * Misc other verbs
     ***********************************************************************
     */
    // req.addElement(GnrcConvControl.GET_STATUS_CMD + "1A");

    /**
     ***********************************************************************
     * Verbs not implemented
     ***********************************************************************
     */

    //    req.addElement(GnrcConvControl.GET_PNR_CMD + "1AY7OIR3  F");
		//    req.addElement(GnrcConvControl.GET_PNR_CMD          + "AAVRNPJM  F");
    //    req.addElement(GnrcConvControl.GET_PNR_CMD          + "UASPXPVI  T");
    //    req.addElement(GnrcConvControl.GET_PNR_CMD          + "1PM6MYVH  F");

    //    req.addElement(GnrcConvControl.GET_QUEUE_PNR_CMD    + "1P51                            T");


    //    req.addElement(GnrcConvControl.GET_HOTEL_INFO_CMD + "1P" + "VA" + GnrcFormat.SetWidth("329",10) + GnrcFormat.SetWidth("ALL",10));

    //    req.addElement(GnrcConvControl.AWR_CON_TM_CMD       + "DFW");

    //    req.addElement(GnrcConvControl.FREEFORM_CMD + GnrcFormat.SetWidth("*XWWJQT",80) );
    //    req.addElement(GnrcConvControl.AWR_FREEFORM_CMD + GnrcFormat.SetWidth("*XWWJQT",80) );
    //    req.addElement(GnrcConvControl.AWR_FREEFORM_CMD + GnrcFormat.SetWidth("MD",80) );

    //    req.addElement(GnrcConvControl.SET_PRN_CMD + "AA" + GnrcFormat.SetWidth("AB414314",20) );

    //    req.addElement(GnrcConvControl.LIST_BRANCH_CMD + "UA" + GnrcFormat.SetWidth("12OX",10) );
    //    req.addElement(GnrcConvControl.LIST_GROUP_PROF_CMD + "UA" + GnrcFormat.SetWidth("BUBBA",10) );
    //    req.addElement(GnrcConvControl.LIST_PER_PROF_CMD + "UA" + GnrcFormat.SetWidth("12OX",10) + GnrcFormat.SetWidth("BUBBA",30));

    //     req.addElement(GnrcConvControl.GET_PER_PROF_CMD + "AA" + GnrcFormat.SetWidth("HJ37",10) + GnrcFormat.SetWidth("FRANKLIN AUTO",30) + GnrcFormat.SetWidth("JOHNNY",30) );

    //    req.addElement("THIS IS AN INVALID COMMAND");

    /**
     ***********************************************************************
     * Everything one needs to add a PNR in one fell swoop
     ***********************************************************************
     */
    /*
    // add phone
    req.addElement(
      GnrcConvControl.AWR_ADD_PHONE_CMD  +
      GnrcFormat.SetWidth("",8)  +                  // a locator
      GnrcFormat.SetWidth("OAK415-924-6374-H",90)   // a phone string
    );

    // add ticket instructions
    req.addElement(
      GnrcConvControl.AWR_ADD_TICKET_CMD + 
      GnrcFormat.SetWidth("",8) +          // a locator
      GnrcFormat.SetWidth("OK",40)         // ticketing instructions 
    );

    // add Corporate Header
    req.addElement(GnrcConvControl.AWR_GRP_HDR_CMD    +
      GnrcFormat.SetWidth("004",3)         +        // number of seats
      GnrcFormat.SetWidth("DISNEY",50)          // group name
    );
    */
    /*
    // add passengers
    req.addElement(GnrcConvControl.AWR_ADD_NAME_CMD   + 
      GnrcFormat.SetWidth("",8) +                  // record locator (if one exists)

      // the following can repeat in order to add multiple passengers
      GnrcFormat.SetWidth("JONES/ZOE",60) +
      GnrcFormat.SetWidth("CHDCHD", 6)      +
      GnrcFormat.SetWidth("1001",20) +       
      GnrcFormat.SetWidth("",30) +         

      GnrcFormat.SetWidth("JONES/CHRISTINE",60) + 
      GnrcFormat.SetWidth("ADTADT", 6)      +      
      GnrcFormat.SetWidth("1002",20) +       
      GnrcFormat.SetWidth("NOAH",30) +                  

      GnrcFormat.SetWidth("JONES/CHLOE",60) +        // Last/First Psgr Name
      GnrcFormat.SetWidth("CHDCHD", 6)      +      // Airware and Crs' Psgr Type Code
      GnrcFormat.SetWidth("1003",20) +       // Airware's Psgr ID
      GnrcFormat.SetWidth("",30) +                   // name of infant

      GnrcFormat.SetWidth("JONES/PHILIPPE",60) + 
      GnrcFormat.SetWidth("ADTADT", 6)      +      
      GnrcFormat.SetWidth("1004",20) +       
      GnrcFormat.SetWidth("",30) +                   
    ""); 
    */
    /* 
   // add simple round-trip itin with one active and one passive segment
    req.addElement(
        GnrcConvControl.AWR_ADD_AIRSEG_CMD + 
        GnrcFormat.SetWidth("",8)          +  // locator
        // repeat this section to add multiple segments
        GnrcFormat.SetWidth("AA",3)        +  // carrier
        GnrcFormat.SetWidth("2008",5)      +  // flight number
        GnrcFormat.SetWidth("SFOMIA",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20021215",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Y",2)         +  // Inv. Class
        GnrcFormat.SetWidth("SG",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("",8)          +  // Remote Locator
        GnrcFormat.SetWidth("",3)          +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("",1)          +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("",1)          +  // isScheduledFlight (T - default)

        GnrcFormat.SetWidth("UA",3)        +  // carrier
        GnrcFormat.SetWidth("866",5)       +  // flight number
        GnrcFormat.SetWidth("MIASFO",6)    +  // Dep&Arr Cities
        GnrcFormat.SetWidth("20021221",8)  +  // Departure Date
        GnrcFormat.SetWidth("",4)          +  // Departure Time
        GnrcFormat.SetWidth("",8)          +  // Arrival Date
        GnrcFormat.SetWidth("",4)          +  // Arrival Time
        GnrcFormat.SetWidth("Q",2)         +  // Inv. Class
        GnrcFormat.SetWidth("GK",4)        +  // Action Code
        GnrcFormat.SetWidth("004",3)       +  // NumSeats
        GnrcFormat.SetWidth("ABLOCK",8)    +  // Remote Locator
        GnrcFormat.SetWidth("REG",3)       +  // Block Type ('MNG', 'REG' or '')
        GnrcFormat.SetWidth("P",1)         +  // Passive (P) or Active (A - default)
        GnrcFormat.SetWidth("",1)          +  // isScheduledFlight (T - default)
      "");
    */
    
    //req.addElement(GnrcConvControl.AWR_RCV_PNR_CMD + "GEORGEBUSH"); // ReceivedBy
    //req.addElement(GnrcConvControl.AWR_END_XACT_CMD ); // Save and Retrieve
    

    return(req);

    } // end getRequestList

} // end class TranClientConnectionTest

