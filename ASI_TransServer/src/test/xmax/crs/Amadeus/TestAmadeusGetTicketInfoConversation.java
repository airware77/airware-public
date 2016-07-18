package xmax.crs.Amadeus;

import java.io.File;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import xmax.TranServer.ConfigTranServer;
import xmax.TranServer.NativeAsciiWriter;
import xmax.TranServer.ReqGetTicketInfo;
import xmax.TranServer.TranClientConnection;
import xmax.crs.PNR;
import xmax.crs.TicketInformation;
import xmax.util.FileStore;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestAmadeusGetTicketInfoConversation extends TestCase
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
    public TestAmadeusGetTicketInfoConversation(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	final TestSuite tSuite = new TestSuite();
    	
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("testGetPnrDate"));
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("testGetTaxAmounts"));
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("testGetFieldValue"));
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("testGetLastFieldValue"));
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("testParseRttnResponse3"));
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("testParseTktResponse"));
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("notestTranClientConnection"));
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("notestAmountFormat"));
     tSuite.addTest(new TestAmadeusGetTicketInfoConversation("notestNegativeTicketAmountResponse"));
     
   //  tSuite.addTest(new TestAmadeusGetTicketInfoConversation("notestParseRttnResponseWithMoveDowns"));
    
    	// normally, this is run
  //      tSuite.addTestSuite(TestAmadeusGetTicketInfoConversation.class);
    	
        return tSuite;
    }
	
    
    public void testGetPnrDate()
    {
    	final Calendar cal = new GregorianCalendar();
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
       	cal.set(Calendar.SECOND, 0);
       	cal.set(Calendar.MILLISECOND, 0);
            	
 
    	// try a couple of dates
       	cal.set(2011, 8, 28);
     	Date dtTest = AmadeusGetTicketInfoConversation.getPnrDate("28SEP11");
    	assertEquals(cal.getTime(), dtTest);
    	
    	
       	cal.set(2014, 9, 22);
     	dtTest = AmadeusGetTicketInfoConversation.getPnrDate(" 22OCT14 ");
    	assertEquals(cal.getTime(), dtTest);
    	
    	
    	// test garbled date string
    	try
    	{
         	dtTest = AmadeusGetTicketInfoConversation.getPnrDate("GARBLEDSTRING");
        	fail("Should have failed on garbled date string");
    	}
    	catch (IllegalArgumentException e)
    	{
    		;  // this is the expected behavior
    	}

    	
    	// null check
     	dtTest = AmadeusGetTicketInfoConversation.getPnrDate(null);
    	assertNull(dtTest);
    }

    
    public void testGetTaxAmounts()
    {
    	  final String sTaxLine = "   17.70US     5.00AY   16.40XT   ";
    	  
    	  final Map<String, BigDecimal> taxMap = AmadeusGetTicketInfoConversation.getTaxAmounts(sTaxLine);
    	  assertNotNull(taxMap);
    	  assertEquals(3, taxMap.size());
    	  assertTrue(taxMap.containsKey("US"));
    	  assertEquals(new BigDecimal("17.70"), taxMap.get("US") );
    	  assertTrue(taxMap.containsKey("AY"));
    	  assertEquals(new BigDecimal("5.00"), taxMap.get("AY") );
    	  assertTrue(taxMap.containsKey("XT"));
    	  assertEquals(new BigDecimal("16.40"), taxMap.get("XT") );
    	  
    	  // test null string
    	  final Map<String, BigDecimal> taxMapEmpty = AmadeusGetTicketInfoConversation.getTaxAmounts(null);
    	  assertNotNull(taxMapEmpty);
    	  assertEquals(0, taxMapEmpty.size());
    	  
    	  // test null string
    	  final Map<String, BigDecimal> taxMapEmpty2 = AmadeusGetTicketInfoConversation.getTaxAmounts("   ");
    	  assertNotNull(taxMapEmpty2);
    	  assertEquals(0, taxMapEmpty2.size());
    }

    
    public void testGetFieldValue()
    {
  	      final String END_OF_LINE = "\r";
	      final String END_OF_RESPONSE = null;
    	
    	  final String sTktResponse = getSampleTktResponse();

 		  final String sAgency      = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "AGENCY  - ", " ");
		  final String sOfficeAgent = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "OFFID/AS- ", "ITEM -");   // gets both the office ID and the agent sign
		  final String sDocument    = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "DOCUMENT- ", "-");        // already have this
		  final String sItem        = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, " ITEM - ", " ");  
		  final String sCurrency    = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, " CURR - ", " ");  
		  final String sPassenger   = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "PASSENGER :", END_OF_LINE);  
		  final String sFop1        = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "FOP1 :", END_OF_LINE);  
		  final String sFop2        = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "FOP2 :", END_OF_LINE);  
		  final String sFop3        = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "FOP3 :", END_OF_LINE);  
		  final String sTax         = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "TAX :", END_OF_LINE);     // gets all taxes
		  final String sExchValue   = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "EXCH VALUE :", END_OF_LINE);  
		  final String sNewTicket   = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "NEW TICKET :", END_OF_LINE);  
		  final String sOrigin      = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "ORIGIN :", END_OF_LINE);  
		  final String sPurchaser   = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "PURCHASER :", END_OF_LINE);  
		  final String sFareCalc    = AmadeusGetTicketInfoConversation.getFieldValue(sTktResponse, "FARE CALC :", END_OF_RESPONSE);  
		  
		  
		  assertEquals("10529050", sAgency);
		  assertEquals("MCODL2100 AA AA", sOfficeAgent);
		  assertEquals("8709668509", sDocument);
		  assertEquals("121256", sItem);
		  assertEquals("USD", sCurrency);
		  assertEquals("TREGLOWN/TROY(ID033483348)", sPassenger);
		  assertEquals("CC      275.10  TPXXXXXXXXXX0017     0714 S9772", sFop1);
		  assertEquals("", sFop2);
		  assertEquals("", sFop3);
		  assertEquals("17.70US     5.00AY   16.40XT", sTax);
		  assertEquals("BASE            TAX        TOTAL", sExchValue);
		  assertEquals("BASE            TAX        TOTAL", sNewTicket);
		  assertEquals("", sOrigin);
		  assertEquals("", sPurchaser);
		  
		  
		  final String sExpectedFareLadder = 
			  "ORL AS X/SEA AS PDX236.00VSF200 USD236.00END ZP MC \r\n" +
				"  MANUAL     03.70SEA3.70XT7.40ZP9.00XF MCO4.5SEA4.5            \r\n" +
				"  PRICED";
		  
		  assertEquals(sExpectedFareLadder, sFareCalc);
    }

    public void testGetLastFieldValue()
    {
     	  final String sTktResponse = getSampleTktResponse();
    	
    	
		  // get amounts
		  final String sCredit = AmadeusGetTicketInfoConversation.getLastFieldValue(sTktResponse, "CREDIT");
		  final BigDecimal dCredit = new BigDecimal(sCredit);
		  assertEquals(new BigDecimal("275.10"), dCredit);
		  
		  final String sCash = AmadeusGetTicketInfoConversation.getLastFieldValue(sTktResponse, "CASH");
		  final BigDecimal dCash = new BigDecimal(sCash);
		  assertEquals(new BigDecimal("0.00"), dCash);
		  
		  final String sTaxAmount = AmadeusGetTicketInfoConversation.getLastFieldValue(sTktResponse, "TAX");
		  final BigDecimal dTaxAmount = new BigDecimal(sTaxAmount);
		  assertEquals(new BigDecimal("39.10"), dTaxAmount);
		  
		  final String sFeeAmount = AmadeusGetTicketInfoConversation.getLastFieldValue(sTktResponse, "FEES");
		  final BigDecimal dFeeAmount = new BigDecimal(sFeeAmount);
		  assertEquals(new BigDecimal("0.00"), dFeeAmount);
		  
		  final String sCommAmount = AmadeusGetTicketInfoConversation.getLastFieldValue(sTktResponse, "COMM");
		  final BigDecimal dCommAmount = new BigDecimal(sCommAmount);
		  assertEquals(new BigDecimal("0.00"), dCommAmount);
    }
    
    

    public void testParseRttnResponse()
    {
    	final String sRttnResponse = getSampleRttnResponse();

    	final PNR pnr = null;
    	final List<TicketInformation> tktInfoList = new ArrayList<TicketInformation>();
      	AmadeusGetTicketInfoConversation.parseRttnResponse(sRttnResponse, pnr, tktInfoList);
    	  
      	/*
      	// set the expected ticket date
    	final Calendar cal = new GregorianCalendar();
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
       	cal.set(Calendar.SECOND, 0);
       	cal.set(Calendar.MILLISECOND, 0);
       	cal.set(2011, 8, 28);
       	final Date dtTicket = cal.getTime();
      	*/
       	
       	// test the list
      	assertNotNull(tktInfoList);
      	assertEquals(6, tktInfoList.size());
    	
      	// test each element of the list
      	assertEquals("8709668509", tktInfoList.get(0).getTicketNumber());
      	assertEquals(1, tktInfoList.get(0).getPsgrSequence());
      	assertNotNull(tktInfoList.get(0).getSegments());
      	assertEquals(2, tktInfoList.get(0).getSegments().size());
     	assertEquals(8, tktInfoList.get(0).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(0).getSegments().get(1).intValue());
      //	assertEquals(new BigDecimal("275.10"), tktInfoList.get(0).getTicketValue());
      //	assertEquals(dtTicket, tktInfoList.get(0).getTicketDate());
      	
      	assertEquals("8709668510", tktInfoList.get(1).getTicketNumber());
      	assertEquals(2, tktInfoList.get(1).getPsgrSequence());
      	assertNotNull(tktInfoList.get(1).getSegments());
      	assertEquals(2, tktInfoList.get(1).getSegments().size());
     	assertEquals(8, tktInfoList.get(1).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(1).getSegments().get(1).intValue());
     // 	assertEquals(new BigDecimal("275.10"), tktInfoList.get(1).getTicketValue());
     // 	assertEquals(dtTicket, tktInfoList.get(1).getTicketDate());
      	
      	assertEquals("8709668511", tktInfoList.get(2).getTicketNumber());
      	assertEquals(3, tktInfoList.get(2).getPsgrSequence());
      	assertNotNull(tktInfoList.get(2).getSegments());
      	assertEquals(2, tktInfoList.get(2).getSegments().size());
     	assertEquals(8, tktInfoList.get(2).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(2).getSegments().get(1).intValue());
     // 	assertEquals(new BigDecimal("275.10"), tktInfoList.get(2).getTicketValue());
     // 	assertEquals(dtTicket, tktInfoList.get(2).getTicketDate());
      	
      	assertEquals("8709668512", tktInfoList.get(3).getTicketNumber());
      	assertEquals(4, tktInfoList.get(3).getPsgrSequence());
      	assertNotNull(tktInfoList.get(3).getSegments());
      	assertEquals(2, tktInfoList.get(3).getSegments().size());
     	assertEquals(8, tktInfoList.get(3).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(3).getSegments().get(1).intValue());
     // 	assertEquals(new BigDecimal("275.10"), tktInfoList.get(3).getTicketValue());
     // 	assertEquals(dtTicket, tktInfoList.get(3).getTicketDate());
      	
      	assertEquals("8709668550", tktInfoList.get(4).getTicketNumber());
      	assertEquals(4, tktInfoList.get(4).getPsgrSequence());
      	assertNotNull(tktInfoList.get(4).getSegments());
      	assertEquals(2, tktInfoList.get(4).getSegments().size());
     	assertEquals(8, tktInfoList.get(4).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(4).getSegments().get(1).intValue());
     // 	assertEquals(new BigDecimal("227.80"), tktInfoList.get(4).getTicketValue());
     // 	assertEquals(dtTicket, tktInfoList.get(4).getTicketDate());
      	
      	assertEquals("8709668551", tktInfoList.get(5).getTicketNumber());
      	assertEquals(5, tktInfoList.get(5).getPsgrSequence());
      	assertNotNull(tktInfoList.get(5).getSegments());
      	assertEquals(2, tktInfoList.get(5).getSegments().size());
     	assertEquals(8, tktInfoList.get(5).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(5).getSegments().get(1).intValue());
     // 	assertEquals(new BigDecimal("275.10"), tktInfoList.get(5).getTicketValue());
     // 	assertEquals(dtTicket, tktInfoList.get(5).getTicketDate());
    }

    
    public void notestParseRttnResponse2()
    {
    	final String sRttnResponse = getSampleRttnResponse2();

    	final PNR pnr = null;
    	final List<TicketInformation> tktInfoList = new ArrayList<TicketInformation>();
      	AmadeusGetTicketInfoConversation.parseRttnResponse(sRttnResponse, pnr, tktInfoList);
    	  
      	// set the expected ticket date
    	final Calendar cal = new GregorianCalendar();
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
       	cal.set(Calendar.SECOND, 0);
       	cal.set(Calendar.MILLISECOND, 0);
       	cal.set(2011, 8, 28);
       	final Date dtTicket = cal.getTime();
      	
       	
       	// test the list
      	assertNotNull(tktInfoList);
      	assertEquals(6, tktInfoList.size());
    	
      	// test each element of the list
      	assertEquals("8709668509", tktInfoList.get(0).getTicketNumber());
      	assertEquals(1, tktInfoList.get(0).getPsgrSequence());
      	assertNotNull(tktInfoList.get(0).getSegments());
      	assertEquals(2, tktInfoList.get(0).getSegments().size());
     	assertEquals(8, tktInfoList.get(0).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(0).getSegments().get(1).intValue());
      	assertEquals(new BigDecimal("275.10"), tktInfoList.get(0).getTicketValue());
      	assertEquals(dtTicket, tktInfoList.get(0).getTicketDate());
      	
      	assertEquals("8709668510", tktInfoList.get(1).getTicketNumber());
      	assertEquals(2, tktInfoList.get(1).getPsgrSequence());
      	assertNotNull(tktInfoList.get(1).getSegments());
      	assertEquals(2, tktInfoList.get(1).getSegments().size());
     	assertEquals(8, tktInfoList.get(1).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(1).getSegments().get(1).intValue());
      	assertEquals(new BigDecimal("275.10"), tktInfoList.get(1).getTicketValue());
      	assertEquals(dtTicket, tktInfoList.get(1).getTicketDate());
      	
      	assertEquals("8709668511", tktInfoList.get(2).getTicketNumber());
      	assertEquals(3, tktInfoList.get(2).getPsgrSequence());
      	assertNotNull(tktInfoList.get(2).getSegments());
      	assertEquals(2, tktInfoList.get(2).getSegments().size());
     	assertEquals(8, tktInfoList.get(2).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(2).getSegments().get(1).intValue());
      	assertEquals(new BigDecimal("275.10"), tktInfoList.get(2).getTicketValue());
      	assertEquals(dtTicket, tktInfoList.get(2).getTicketDate());
      	
      	assertEquals("8709668512", tktInfoList.get(3).getTicketNumber());
      	assertEquals(4, tktInfoList.get(3).getPsgrSequence());
      	assertNotNull(tktInfoList.get(3).getSegments());
      	assertEquals(2, tktInfoList.get(3).getSegments().size());
     	assertEquals(8, tktInfoList.get(3).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(3).getSegments().get(1).intValue());
      	assertEquals(new BigDecimal("275.10"), tktInfoList.get(3).getTicketValue());
      	assertEquals(dtTicket, tktInfoList.get(3).getTicketDate());
      	
      	assertEquals("8709668550", tktInfoList.get(4).getTicketNumber());
      	assertEquals(4, tktInfoList.get(4).getPsgrSequence());
      	assertNotNull(tktInfoList.get(4).getSegments());
      	assertEquals(2, tktInfoList.get(4).getSegments().size());
     	assertEquals(8, tktInfoList.get(4).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(4).getSegments().get(1).intValue());
      	assertEquals(new BigDecimal("227.80"), tktInfoList.get(4).getTicketValue());
      	assertEquals(dtTicket, tktInfoList.get(4).getTicketDate());
      	
      	assertEquals("8709668551", tktInfoList.get(5).getTicketNumber());
      	assertEquals(5, tktInfoList.get(5).getPsgrSequence());
      	assertNotNull(tktInfoList.get(5).getSegments());
      	assertEquals(2, tktInfoList.get(5).getSegments().size());
     	assertEquals(8, tktInfoList.get(5).getSegments().get(0).intValue());
     	assertEquals(9, tktInfoList.get(5).getSegments().get(1).intValue());
      	assertEquals(new BigDecimal("275.10"), tktInfoList.get(5).getTicketValue());
      	assertEquals(dtTicket, tktInfoList.get(5).getTicketDate());
    }
    
    public void testParseRttnResponse3()
    {
    	final String sRttnResponse = getSampleRttnResponse3();

    	final PNR pnr = null;
    	final List<TicketInformation> tktInfoList = new ArrayList<TicketInformation>();
    	AmadeusGetTicketInfoConversation.parseRttnResponse(sRttnResponse, pnr, tktInfoList);
    	  
      	
       	// test the list
      	assertNotNull(tktInfoList);
      	assertEquals(4, tktInfoList.size());
    	
      	// test each element of the list
      	assertEquals("7104986898", tktInfoList.get(0).getTicketNumber());
      	assertEquals(1, tktInfoList.get(0).getPsgrSequence());
      	assertNotNull(tktInfoList.get(0).getSegments());
      	assertEquals(1, tktInfoList.get(0).getSegments().size());
     	assertEquals(6, tktInfoList.get(0).getSegments().get(0).intValue());
     // 	assertEquals(new BigDecimal("275.10"), tktInfoList.get(0).getTicketValue());
     // 	assertEquals(dtTicket, tktInfoList.get(0).getTicketDate());
      	
      	assertEquals("7104986899", tktInfoList.get(1).getTicketNumber());
      	assertEquals(2, tktInfoList.get(1).getPsgrSequence());
      	assertNotNull(tktInfoList.get(1).getSegments());
      	assertEquals(1, tktInfoList.get(1).getSegments().size());
     	assertEquals(6, tktInfoList.get(1).getSegments().get(0).intValue());
   //   	assertEquals(new BigDecimal("275.10"), tktInfoList.get(1).getTicketValue());
    //  	assertEquals(dtTicket, tktInfoList.get(1).getTicketDate());
      	
      	assertEquals("7104986900", tktInfoList.get(2).getTicketNumber());
      	assertEquals(3, tktInfoList.get(2).getPsgrSequence());
      	assertNotNull(tktInfoList.get(2).getSegments());
      	assertEquals(1, tktInfoList.get(2).getSegments().size());
     	assertEquals(6, tktInfoList.get(2).getSegments().get(0).intValue());
    //  	assertEquals(new BigDecimal("275.10"), tktInfoList.get(2).getTicketValue());
    //  	assertEquals(dtTicket, tktInfoList.get(2).getTicketDate());
      	
      	assertEquals("7104986901", tktInfoList.get(3).getTicketNumber());
      	assertEquals(4, tktInfoList.get(3).getPsgrSequence());
      	assertNotNull(tktInfoList.get(3).getSegments());
      	assertEquals(1, tktInfoList.get(3).getSegments().size());
     	assertEquals(6, tktInfoList.get(3).getSegments().get(0).intValue());
    //  	assertEquals(new BigDecimal("275.10"), tktInfoList.get(3).getTicketValue());
    //  	assertEquals(dtTicket, tktInfoList.get(3).getTicketDate());
    }
    
    
    public void notestParseRttnResponseWithMoveDowns()
    {
    	final String sRttnResponse = getSampleRttnResponseFirstPage();

    	final PNR pnr = null;
    	final List<TicketInformation> tktInfoList = new ArrayList<TicketInformation>();
      	AmadeusGetTicketInfoConversation.parseRttnResponse(sRttnResponse, pnr, tktInfoList);
    	  
      	
      	final String sMdResponse1 = getSampleRttnMDResponse1();
     	AmadeusGetTicketInfoConversation.parseRttnResponse(sMdResponse1, pnr, tktInfoList);
     	
      	final String sMdResponse2 = getSampleRttnMDResponse2();
     	AmadeusGetTicketInfoConversation.parseRttnResponse(sMdResponse2, pnr, tktInfoList);
     	

       	
       	// test the list
      	assertNotNull(tktInfoList);
      	assertEquals(12, tktInfoList.size());
    	
      	// test each element of the list
      	assertEquals("7104986758", tktInfoList.get(0).getTicketNumber());
      	assertEquals(2, tktInfoList.get(0).getPsgrSequence());
      	assertNotNull(tktInfoList.get(0).getSegments());
      	assertEquals(2, tktInfoList.get(0).getSegments().size());
     	assertEquals(7, tktInfoList.get(0).getSegments().get(0).intValue());
     	assertEquals(8, tktInfoList.get(0).getSegments().get(1).intValue());
       	
      	assertEquals("7104986759", tktInfoList.get(1).getTicketNumber());
      	assertEquals(3, tktInfoList.get(1).getPsgrSequence());
      	assertNotNull(tktInfoList.get(1).getSegments());
      	assertEquals(2, tktInfoList.get(1).getSegments().size());
     	assertEquals(7, tktInfoList.get(1).getSegments().get(0).intValue());
     	assertEquals(8, tktInfoList.get(1).getSegments().get(1).intValue());
     	
      	assertEquals("7104986760", tktInfoList.get(2).getTicketNumber());
      	assertEquals(4, tktInfoList.get(2).getPsgrSequence());
      	assertNotNull(tktInfoList.get(2).getSegments());
      	assertEquals(2, tktInfoList.get(2).getSegments().size());
     	assertEquals(7, tktInfoList.get(2).getSegments().get(0).intValue());
     	assertEquals(8, tktInfoList.get(2).getSegments().get(1).intValue());
       	
      	assertEquals("7104986761", tktInfoList.get(3).getTicketNumber());
      	assertEquals(5, tktInfoList.get(3).getPsgrSequence());
      	assertNotNull(tktInfoList.get(3).getSegments());
      	assertEquals(2, tktInfoList.get(3).getSegments().size());
     	assertEquals(7, tktInfoList.get(3).getSegments().get(0).intValue());
     	assertEquals(8, tktInfoList.get(3).getSegments().get(1).intValue());
     	
      	assertEquals("7104986762", tktInfoList.get(4).getTicketNumber());
      	assertEquals(1, tktInfoList.get(4).getPsgrSequence());
      	assertNotNull(tktInfoList.get(4).getSegments());
      	assertEquals(2, tktInfoList.get(4).getSegments().size());
     	assertEquals(7, tktInfoList.get(4).getSegments().get(0).intValue());
     	assertEquals(8, tktInfoList.get(4).getSegments().get(1).intValue());
       	
      	assertEquals("7104986763", tktInfoList.get(5).getTicketNumber());
      	assertEquals(6, tktInfoList.get(5).getPsgrSequence());
      	assertNotNull(tktInfoList.get(5).getSegments());
      	assertEquals(2, tktInfoList.get(5).getSegments().size());
     	assertEquals(7, tktInfoList.get(5).getSegments().get(0).intValue());
     	assertEquals(8, tktInfoList.get(5).getSegments().get(1).intValue());
     	
     	
      	assertEquals("7104986794", tktInfoList.get(6).getTicketNumber());
      	assertEquals(2, tktInfoList.get(6).getPsgrSequence());
      	assertNotNull(tktInfoList.get(6).getSegments());
      	assertEquals(2, tktInfoList.get(6).getSegments().size());
     	assertEquals(9, tktInfoList.get(6).getSegments().get(0).intValue());
     	assertEquals(10, tktInfoList.get(6).getSegments().get(1).intValue());
       	
      	assertEquals("7104986795", tktInfoList.get(7).getTicketNumber());
      	assertEquals(4, tktInfoList.get(7).getPsgrSequence());
      	assertNotNull(tktInfoList.get(7).getSegments());
      	assertEquals(2, tktInfoList.get(7).getSegments().size());
     	assertEquals(9, tktInfoList.get(7).getSegments().get(0).intValue());
     	assertEquals(10, tktInfoList.get(7).getSegments().get(1).intValue());
     	
      	assertEquals("7104986796", tktInfoList.get(8).getTicketNumber());
      	assertEquals(5, tktInfoList.get(8).getPsgrSequence());
      	assertNotNull(tktInfoList.get(8).getSegments());
      	assertEquals(2, tktInfoList.get(8).getSegments().size());
     	assertEquals(9, tktInfoList.get(8).getSegments().get(0).intValue());
     	assertEquals(10, tktInfoList.get(8).getSegments().get(1).intValue());
       	
      	assertEquals("7104986797", tktInfoList.get(9).getTicketNumber());
      	assertEquals(3, tktInfoList.get(9).getPsgrSequence());
      	assertNotNull(tktInfoList.get(9).getSegments());
      	assertEquals(2, tktInfoList.get(9).getSegments().size());
     	assertEquals(9, tktInfoList.get(9).getSegments().get(0).intValue());
     	assertEquals(10, tktInfoList.get(9).getSegments().get(1).intValue());
     	
      	assertEquals("7104986798", tktInfoList.get(10).getTicketNumber());
      	assertEquals(1, tktInfoList.get(10).getPsgrSequence());
      	assertNotNull(tktInfoList.get(10).getSegments());
      	assertEquals(2, tktInfoList.get(10).getSegments().size());
     	assertEquals(9, tktInfoList.get(10).getSegments().get(0).intValue());
     	assertEquals(10, tktInfoList.get(10).getSegments().get(1).intValue());
       	
      	assertEquals("7104986799", tktInfoList.get(11).getTicketNumber());
      	assertEquals(6, tktInfoList.get(11).getPsgrSequence());
      	assertNotNull(tktInfoList.get(11).getSegments());
      	assertEquals(2, tktInfoList.get(11).getSegments().size());
     	assertEquals(9, tktInfoList.get(11).getSegments().get(0).intValue());
     	assertEquals(10, tktInfoList.get(11).getSegments().get(1).intValue());
    }
    
   
    public void testParseTktResponse()
    {
  	  final String sTktResponse = getSampleTktResponse();

  	  final TicketInformation ticketInformation = new TicketInformation();
	  AmadeusGetTicketInfoConversation.parseTktResponse(sTktResponse, ticketInformation);
	  
	  
	  assertEquals("10529050", ticketInformation.getAgency());
	  assertEquals("MCODL2100", ticketInformation.getOfficeID());
	  assertEquals("AA AA", ticketInformation.getAgent());
	  assertEquals("8709668509", ticketInformation.getDocumentNumber());
	  assertEquals("121256", ticketInformation.getItem());
	  assertEquals("USD", ticketInformation.getCurrencyCode());
	  assertEquals("TREGLOWN/TROY(ID033483348)", ticketInformation.getPsgrName());
	//  assertEquals("033483348", ticketInformation.getPsgrID());    // this comes from the RTTN response
	  assertEquals("CC      275.10  TPXXXXXXXXXX0017     0714 S9772", ticketInformation.getFop1());
	  assertEquals("", ticketInformation.getFop2());
	  assertEquals("", ticketInformation.getFop3());
	  assertEquals("BASE            TAX        TOTAL", ticketInformation.getExchangeValue());
	  assertEquals("BASE            TAX        TOTAL", ticketInformation.getNewTicket());
	  assertEquals("", ticketInformation.getOrigin());
	  assertEquals("", ticketInformation.getPurchaser());
	  assertTrue(ticketInformation.isEticket());
	  
      // set the expected ticket date
  	  final Calendar cal = new GregorianCalendar();
  	  cal.set(Calendar.HOUR_OF_DAY, 0);
  	  cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      cal.set(2011, 8, 28);
      final Date dtTicket = cal.getTime();	  
      assertEquals(dtTicket, ticketInformation.getTicketDate());
	  
	  
	  assertEquals(new BigDecimal("275.10"), ticketInformation.getTicketValue());
	  assertEquals(new BigDecimal("275.10"), ticketInformation.getCcAmount());
	  assertEquals(new BigDecimal("0.00"), ticketInformation.getCashAmount());
	  assertEquals(new BigDecimal("39.10"), ticketInformation.getTaxAmount());
	  assertEquals(new BigDecimal("0.00"), ticketInformation.getFeeAmount());
	  assertEquals(new BigDecimal("0.00"), ticketInformation.getCommissionAmount());
	  
	  assertEquals(3, ticketInformation.getTaxes().size());
	  assertTrue(ticketInformation.getTaxes().containsKey("US"));
	  assertEquals(new BigDecimal("17.70"), ticketInformation.getTaxes().get("US"));
	  assertTrue(ticketInformation.getTaxes().containsKey("AY"));
	  assertEquals(new BigDecimal("5.00"), ticketInformation.getTaxes().get("AY"));
	  assertTrue(ticketInformation.getTaxes().containsKey("XT"));
	  assertEquals(new BigDecimal("16.40"), ticketInformation.getTaxes().get("XT"));
	  
	  final String sExpectedFareLadder = 
		  "ORL AS X/SEA AS PDX236.00VSF200 USD236.00END ZP MC \r\n" +
			"       03.70SEA3.70XT7.40ZP9.00XF MCO4.5SEA4.5";
	  
	  assertEquals(sExpectedFareLadder, ticketInformation.getFareLadder());
    }
    
    
    private String getSampleTktResponse()
    {
    	final String sTktResponse = 
		"                                               275.10 CREDIT    \r\n" +
		"AGENCY  - 10529050        28SEP11                0.00 CASH      \r\n" +
		"OFFID/AS- MCODL2100 AA AA   ITEM - 121256       39.10 TAX       \r\n" +
		"DOC TYPE- ELEC TKT SALE     CURR - USD           0.00 FEES      \r\n" +
		"AL/PROV - 027 - US        STATUS - PENDING       0.00 COMM      \r\n" +
		"DOCUMENT- 8709668509-        ELEC TKT SALE       PNR 7A95JY     \r\n" +
        "                                                                \r\n" +
		" PASSENGER : TREGLOWN/TROY(ID033483348)                         \r\n" +
		"      TOUR :                     INVOICE :                      \r\n" +
		"      FOP1 : CC      275.10  TPXXXXXXXXXX0017     0714 S9772    \r\n" +
		"      FOP2 :                                                    \r\n" +
		"      FOP3 :                                                    \r\n" +
		"       TAX :      17.70US     5.00AY   16.40XT                  \r\n" +
		"EXCH VALUE : BASE            TAX        TOTAL                   \r\n" +
		"NEW TICKET : BASE            TAX        TOTAL                   \r\n" +
		"    ORIGIN :                                                    \r\n" +
		" PURCHASER :                                                    \r\n" +
		" FARE CALC : ORL AS X/SEA AS PDX236.00VSF200 USD236.00END ZP MC \r\n" +
		"  MANUAL     03.70SEA3.70XT7.40ZP9.00XF MCO4.5SEA4.5            \r\n" +
		"  PRICED                                                        \r\n";
    	
    	
    	return sTktResponse;
    }
    
    
    private String getSampleRttnResponse()
    {
    	final String sRttnResponse = 
    		"RP/MCODL2100/MCODL2100            AL/GS  28SEP11/1325Z   7A95JY \r\n" +
    		" 81 FA PAX 027-8709668509/ETAS/USD275.10/28SEP11/MCODL2100/10529\r\n" +
    		"       050/S8-9/P1                                              \r\n" +
    		" 82 FA PAX 027-8709668510/ETAS/USD275.10/28SEP11/MCODL2100/10529\r\n" +
    		"       050/S8-9/P2                                              \r\n" +
    		" 83 FA PAX 027-8709668511/ETAS/USD275.10/28SEP11/MCODL2100/10529\r\n" +
    		"       050/S8-9/P3                                              \r\n" +
    		" 84 FA PAX 027-8709668512/ETAS/USD275.10/28SEP11/MCODL2100/10529\r\n" +
    		"       050/S8-9/P4                                              \r\n" +
    		" 85 FA PAX 016-8709668550/ETAS/USD227.80/28SEP11/MCODL2100/10529\r\n" +
    		"       050/S8-9/P4                                              \r\n" +
    		" 86 FA PAX 016-8709668551/ETAS/USD275.10/28SEP11/MCODL2100/10529\r\n" +
    		"       050/S8-9/P5";
    	
    	return sRttnResponse;
    }
    
    
    private String getSampleRttnResponse2()
    {
    	final String sRttnResponse = 
    		"RP/MCODL2100/MCODL2100            MW/GS  21SEP12/1413Z   54X8ZH \r\n" +
    		" 65 FA PAX 037-7104986758/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P2                                              \r\n" +
    		" 66 FA PAX 037-7104986759/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P3                                              \r\n" +
    		" 67 FA PAX 037-7104986760/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P4                                              \r\n" +
    		" 68 FA PAX 037-7104986761/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P5                                              \r\n" +
    		" 69 FA PAX 037-7104986762/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P1                                              \r\n" +
    		" 70 FA PAX 037-7104986763/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P6                                              \r\n" +
    		" 71 FA PAX 006-7104986794/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P2                                                \r\n" +
    		" 72 FA PAX 006-7104986795/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P4                                                \r\n" +
    		" 73 FA PAX 006-7104986796/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P5                                                \r\n" +
    		" 74 FA PAX 006-7104986797/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P3                                                \r\n" +
    		" 75 FA PAX 006-7104986798/ETDL/21SEP12/MCODL2100/10529050       \r\n";
    		    		
    	return sRttnResponse;
    }
    
    
    private String getSampleRttnResponse3()
    {
    	final String sRttnResponse = 
    		"RP/MCODL2100/MCODL2100            AL/GS  25SEP12/1420Z   3UQ5FY \r\n" +
    		" 55 FA PAX 006-7104986898/ETDL/25SEP12/MCODL2100/10529050/S6/P1 \r\n" +
    		" 56 FA PAX 006-7104986899/ETDL/25SEP12/MCODL2100/10529050/S6/P2 \r\n" +
    		" 57 FA PAX 006-7104986900/ETDL/25SEP12/MCODL2100/10529050/S6/P3 \r\n" +
    		" 58 FA PAX 006-7104986901/ETDL/25SEP12/MCODL2100/10529050/S6/P4 \r\n" +
    		" 59 FHE PAX 001-7102591503/P1 \r\n" +
    		" 60 FHE PAX 001-7102591504/P2 \r\n" +
    		" 61 FHE PAX 001-7102591505/P3 \r\n" +
    		" 62 FHE PAX 001-7102591506/P4 \r\n";
   		
    	return sRttnResponse;
    }
    
    
    private String getSampleRttnResponseFirstPage()
    {
    	final String sRttnResponse = 
    		"RP/MCODL2100/MCODL2100            MW/GS  21SEP12/1413Z   54X8ZH \r\n" +
    		" 65 FA PAX 037-7104986758/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P2                                              \r\n" +
    		" 66 FA PAX 037-7104986759/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P3                                              \r\n" +
    		" 67 FA PAX 037-7104986760/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P4                                              \r\n" +
    		" 68 FA PAX 037-7104986761/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P5                                              \r\n" +
    		" 69 FA PAX 037-7104986762/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		"       050/S7-8/P1                                              \r\n" +
    		" 70 FA PAX 037-7104986763/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
    		")>\r\n";
 
    	return sRttnResponse;
    }
    
    private String getSampleRttnMDResponse1()
    {
    	final String sRttnResponse = 
			"RP/MCODL2100/MCODL2100            MW/GS  21SEP12/1413Z   54X8ZH \r\n" +
			"       050/S7-8/P5                                              \r\n" +
			" 69 FA PAX 037-7104986762/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
			"       050/S7-8/P1                                              \r\n" +
			" 70 FA PAX 037-7104986763/ETUS/USD310.77/21SEP12/MCODL2100/10529\r\n" +
			"       050/S7-8/P6                                              \r\n" +
			" 71 FA PAX 006-7104986794/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
			"       /S9-10/P2                                                \r\n" +
			" 72 FA PAX 006-7104986795/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
			"       /S9-10/P4                                                \r\n" +
			" 73 FA PAX 006-7104986796/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
			"       /S9-10/P5                                                \r\n" +
			" 74 FA PAX 006-7104986797/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
			"       /S9-10/P3                                                \r\n" +
			" 75 FA PAX 006-7104986798/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
	   		")>\r\n";
    		    		
    	return sRttnResponse;
    }
    
    
    private String getSampleRttnMDResponse2()
    {
    	final String sRttnResponse = 
    		"RP/MCODL2100/MCODL2100            MW/GS  21SEP12/1413Z   54X8ZH \r\n" +
    		"       /S9-10/P2                                                \r\n" +
    		" 72 FA PAX 006-7104986795/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P4                                                \r\n" +
    		" 73 FA PAX 006-7104986796/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P5                                                \r\n" +
    		" 74 FA PAX 006-7104986797/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P3                                                \r\n" +
    		" 75 FA PAX 006-7104986798/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
    		"       /S9-10/P1                                                \r\n" +
			" 76 FA PAX 006-7104986799/ETDL/21SEP12/MCODL2100/10529050       \r\n" +
			"       /S9-10/P6                                                \r\n";

    		    		
    	return sRttnResponse;
    }
    
    
    public void notestTranClientConnection() throws Exception
    {
    	final String sConfigFile = "run/TranserverConfig_ASI.xml";
    	ConfigTranServer.readConfFile(sConfigFile);
    	
    	final Socket socket = null;
    	final TranClientConnection tc = new TranClientConnection(socket);
    	
    	//final String sRequest = "TKTPNR  " + "ABCDEF  " + 
    	//						"033483348           ";
    	
    //	final String sRequest = "TKTPNR  " + "7TKLYP  " +
    //	"000711111           000711112           ";
    	
    //	final String sRequest = "TKTPNR  7GJCGN  000911111           000911112           000911113";      	
    	
    	
    	final String sRequest = "TKTPNR  " + "35TF3D  " +
    	"                                        ";
    	
    	final String sResponse = tc.sendReceiveNativeAscii(sRequest);
    	assertNotNull(sResponse);
    	
    	// write the response to file
		  final String sTicketInfoFile = "logs/AirwareResponse-54X8ZH.txt";
		  final File ticketFile = new File(sTicketInfoFile);
		  if (ticketFile.exists())
		  {
			  ticketFile.delete();
		  }
		  FileStore.Write(ticketFile, sResponse);
    	
    }
    
    public void notestAmountFormat()
    {
       // final DecimalFormat amountFormat = new DecimalFormat("00000.00");    
            	
        final DecimalFormat amountFormat = new DecimalFormat("'0'0000.00;'-'0000.00");    
        
        String sResult;
        sResult = amountFormat.format(new BigDecimal("152.75") );
        assertEquals("00152.75",sResult);
        assertEquals(8, sResult.length());
        
        sResult = amountFormat.format(new BigDecimal("-152.75") );
        assertEquals("-0152.75",sResult);
        assertEquals(8, sResult.length());
                 
        sResult = amountFormat.format(new BigDecimal("0.94") );
        assertEquals("00000.94",sResult);
        assertEquals(8, sResult.length());
                 
        sResult = amountFormat.format(new BigDecimal("-152.75") );
        assertEquals("-0152.75",sResult);
        assertEquals(8, sResult.length());
          
        amountFormat.setPositivePrefix("");
        sResult = amountFormat.format(new BigDecimal("12345.00") );
        assertEquals("12345.00",sResult);
        assertEquals(8, sResult.length());
        
        
    }
    
    
    public void notestNegativeTicketAmountResponse()
    {
    	final Date dtCurrent = new Date();
    	
    	
        final ReqGetTicketInfo req = new ReqGetTicketInfo("1A", "ABCDEF");
        
        final TicketInformation tktInformation = new TicketInformation();
        tktInformation.setTicketValue( new BigDecimal("123.45") );
        tktInformation.setCashAmount( new BigDecimal("-12.75") );
        tktInformation.setTicketDate(dtCurrent);
        
        req.getTicketInformation().add(tktInformation);
        
    	final String sResponse = NativeAsciiWriter.getResponseString(req);
        

        assertNotNull(sResponse);
     }
    
    
    public void notestTranClientConnection2() throws Exception
    {
    	final String sConfigFile = "run/TranserverConfig_ASI.xml";
    	ConfigTranServer.readConfFile(sConfigFile);
    	
    	final Socket socket = null;
    	final TranClientConnection tc = new TranClientConnection(socket);
    	
    	
    	// write the response to file
		  final String sTicketInfoFile = "logs/RttnDebug.txt";
		  final File ticketFile = new File(sTicketInfoFile);
		  if (ticketFile.exists())
		  {
			  ticketFile.delete();
		  }
    	
    	//final String sRequest = "TKTPNR  " + "ABCDEF  " + 
    	//						"033483348           ";
    	
    //	final String sRequest = "TKTPNR  " + "7TKLYP  " +
    //	"000711111           000711112           ";
    	
    //	final String sRequest = "TKTPNR  7GJCGN  000911111           000911112           000911113";      	
    	
    	// display the PNR
    	final String sRequest = "CPSSTHRU" + "RT54X8ZH";
    	final String sResponse = tc.sendReceiveNativeAscii(sRequest);
    	assertNotNull(sResponse);
    	
		FileStore.Write(ticketFile, sResponse);
    	
		
    	// run the RTTN command
    	final String sRequest2 = "CPSSTHRU" + "RTTN";
    	final String sResponse2 = tc.sendReceiveNativeAscii(sRequest2);
    	assertNotNull(sResponse2);
    	
		FileStore.Write(ticketFile, sResponse2);
		
		  
	   	// run the MD command
    	final String sRequest3 = "CPSSTHRU" + "MD";
    	final String sResponse3 = tc.sendReceiveNativeAscii(sRequest3);
    	assertNotNull(sResponse3);
    	
		FileStore.Write(ticketFile, sResponse3);
    }
    
}
