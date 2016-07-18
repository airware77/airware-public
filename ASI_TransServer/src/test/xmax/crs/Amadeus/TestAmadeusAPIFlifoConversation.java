package xmax.crs.Amadeus;

import java.io.File;
import java.net.Socket;
import java.util.Date;

import xmax.TranServer.ConfigTranServer;
import xmax.TranServer.TranClientConnection;
import xmax.crs.GnrcParseFlifo;
import xmax.crs.Flifo.FlightInfo;
import xmax.crs.Flifo.FlightSegment;
import xmax.util.FileStore;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestAmadeusAPIFlifoConversation extends TestCase
{
    /**
     * Main function for unit tests
     * @param args command line args
     */
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

    
    /**
     * Create the test case
     * @param testName name of the test case
     */
    public TestAmadeusAPIFlifoConversation(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	final TestSuite tSuite = new TestSuite();
    	
  //   tSuite.addTest(new TestAmadeusAPIFlifoConversation("testNothing"));
     tSuite.addTest(new TestAmadeusAPIFlifoConversation("notestGetFlifo"));
  //   tSuite.addTest(new TestAmadeusAPIFlifoConversation("notestParseAAFlifo"));
  //     tSuite.addTest(new TestAmadeusAPIFlifoConversation("notestParseUAFlifo"));
       
    	// normally, this is run
   //    tSuite.addTestSuite(TestAmadeusAPIFlifoConversation.class);
    	
        return tSuite;
    }
	
    public void testNothing()
    {
    	
    }


    public void notestGetFlifo() throws Exception
    {
    	final String sConfigFile = "run/TranserverConfig_ASI.xml";
    	ConfigTranServer.readConfFile(sConfigFile);
    	
    	final Socket socket = null;
    	final TranClientConnection tc = new TranClientConnection(socket);

    	
    	
    	final String sRequest = "FLIFO   UA 5172 1130727MIAIAHN F054713A3B";
    	
    	final String sResponse = tc.sendReceiveNativeAscii(sRequest);
    	assertNotNull(sResponse);
    	
    	// write the response to file
		final String sTicketInfoFile = "logs/AirwareResponse-UA5172.txt";
		  final File ticketFile = new File(sTicketInfoFile);
		  if (ticketFile.exists())
		  {
			  ticketFile.delete();
		  }
		  FileStore.Write(ticketFile, sResponse);
    	
    }
    
    public void notestParseAAFlifo() throws Exception
    {
    	final String sFlifoString = "1AADO2461/17JUL\r\n" +
    		"** AA - AMERICAN AIRLINES **\r\n" +
    		"AA2461/17JUL\r\n" +
    		"DFW                    1610  A  A24\r\n" +   
    		"LAX     3 T4 48B 1725 \r\n" +
    		"7DFW/AUTO REACCOM DLY FLT COMPLETED SEE N*P1AA2461DFW17JUL *1315\r\n" +
    		"*CRCYMG\r\n" +
    		"3DFW/ETD1749 A/E F1524     *1316\r\n" +
    		"2LAX/PRE1904  *1316\r\n" +
    		"*TRN*\r\n" +
    		">";
    	
    	final Date dtFlight = new Date(113, 6, 17, 16, 10, 0);
    	
    	final FlightInfo flightInfo = new FlightInfo("AA",2461);
    	final FlightSegment flightSegment = new FlightSegment();
    	flightSegment.Carrier = "AA";
    	flightSegment.FlightNum = 2461;
    	flightSegment.DepartCity = "DFW";
    	flightSegment.ArriveCity = "LAX";
    	flightSegment.DepSchedDateTime = dtFlight.getTime();
    	flightSegment.ArrSchedDateTime = dtFlight.getTime();
    	flightInfo.addFlightSegment(flightSegment);
    	GnrcParseFlifo.setDayOfFlightSegments_AA(flightInfo, sFlifoString);
    	
    	assertTrue(flightInfo.getDepEstDate("DFW") > 0);
    }
    
    
    public void notestParseUAFlifo() throws Exception
    {
    	final String sFlifoString = "1UADO4907/23JUL\r\n" +
    	"** UA - UNITED AIRLINES **\r\n" + 
    	"4907/23JUL\r\n" +
    	"F BWI/ETD    226P  L00.30  AIR TRAFFIC CONTROL\r\n" +
    	"D CRC//EWR POSSIBLE DELAYS DUE TO THUNDERSTORMS\r\n" +
    	"F EWR/ETA    346P  L00.30\r\n" +
    	"D EWU/BWI/PROPOSED FAA ATC WHEELS UP 337P(EWR GDP)\r\n" +
    	"D EWU/BWI/RECOMMEND NO REQUOTE AT THIS TIME\r\n" +
    	"SKED  EWR  ORIG   1213P              GTD C103 SHIP 1346\r\n" +
    	"      BWI   121P   156P     GTA  D13 GTD  D13 SHIP 1346\r\n" +
    	"      EWR   316P  TERM      GTA C109\r\n" +
    	"*TRN*\r\n" +
    	">";
    	
    	final Date dtFlightDep = new Date(113, 6, 23, 13, 56, 0);
    	final Date dtFlightArr = new Date(113, 6, 23, 15, 16, 0);
    	     	
    	final FlightInfo flightInfo = new FlightInfo("UA",4907);
    	final FlightSegment flightSegment = new FlightSegment();
    	flightSegment.Carrier = "UA";
    	flightSegment.FlightNum = 4907;
    	flightSegment.DepartCity = "BWI";
    	flightSegment.ArriveCity = "EWR";
    	flightSegment.DepSchedDateTime = dtFlightDep.getTime();
    	flightSegment.ArrSchedDateTime = dtFlightArr.getTime();
    	flightInfo.addFlightSegment(flightSegment);
    	GnrcParseFlifo.setDayOfFlightSegments_CO(flightInfo, sFlifoString);
    	
    	assertTrue(flightInfo.getDepEstDate("BWI") > 0);
    	final Date dtDepSched = new Date(flightInfo.getDepSchedDate());
       	final Date dtDepEst = new Date(flightInfo.getDepEstDate());
       
       	final Date dtArrSched = new Date(flightInfo.getArrSchedDate());
       	final Date dtArrEst = new Date(flightInfo.getArrEstDate());
       	
    	assertNotNull(dtDepSched);
       	assertNotNull(dtDepEst);
       	assertNotNull(dtArrSched);
       	assertNotNull(dtArrEst);
    	
    }
    
    
    
    
    public void testDateFormat()
    {
    	// 2 digit year
    	String sResult = AmadeusAPICrs.fmt_ddMMM_To_ddMMyy("06JUN");
    	assertEquals("060614", sResult);
    	
       	sResult = AmadeusAPICrs.fmt_ddMMM_To_ddMMyy("28JUN");
    	assertEquals("280613", sResult);
    	
       	sResult = AmadeusAPICrs.fmt_ddMMM_To_ddMMyy("20JUN");
    	assertEquals("200613", sResult);
    	
    	// 4 digit year
    
       	sResult = AmadeusAPICrs.fmt_ddMMM_To_ddMMMyyyy("06JUN");
    	assertEquals("06JUN2014", sResult);
    	
       	sResult = AmadeusAPICrs.fmt_ddMMM_To_ddMMMyyyy("28JUN");
    	assertEquals("28JUN2013", sResult);
    	
       	sResult = AmadeusAPICrs.fmt_ddMMM_To_ddMMMyyyy("20JUN");
    	assertEquals("20JUN2013", sResult);
    	
    	
    }
    
    
    
    
}
