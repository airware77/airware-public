package xmax.crs.Amadeus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import xmax.TranServer.TranServerException;
import xmax.crs.GdsResponseException;
import xmax.crs.Amadeus.negospace.PoweredAir_DisplayNegoSpaceReply;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestAmadeusAPIBlockConversation extends TestCase
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
    public TestAmadeusAPIBlockConversation(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	final TestSuite tSuite = new TestSuite();
    	
   //  tSuite.addTest(new TestAmadeusAPIBlockConversation("testChangeMessage"));
   //  tSuite.addTest(new TestAmadeusAPIBlockConversation("testCancelMessage"));
    	
        tSuite.addTestSuite(TestAmadeusAPIBlockConversation.class);
    	
        return tSuite;
    }


    public void testCancelMessage() throws Exception
    {
    	// create cancel block request message from airware
    	final PoweredAir_DisplayNegoSpaceReply dispReply = this.getPnrDisplay();
    	
    	// create cancel block request message to send to Amadeus
        final String sQuery = AmadeusAPIBlockConversation.buildQuery_deleteBlock(dispReply);
        
    	// write the message to file
        final String sFilename = "c:\\CustomerSupport\\Airware\\BlockReduceBug\\logs\\DeleteBlock.xml";
        final FileWriter writer = new FileWriter(sFilename, false);
        writer.write(sQuery);
        writer.flush();
        writer.close();
    }
    
    
    
    public void testChangeMessage() throws Exception
    {
    	// create cancel block request message from airware
    	final PoweredAir_DisplayNegoSpaceReply dispReply = this.getPnrDisplay();
    	
    	// create change block request message to send to Amadeus
        final String sQuery = AmadeusAPIBlockConversation.buildQuery_changeBlock(dispReply, 15);
    	
    	// write the message to file
        final String sFilename = "c:\\CustomerSupport\\Airware\\BlockReduceBug\\logs\\ChangeBlock.xml";
        final FileWriter writer = new FileWriter(sFilename, false);
        writer.write(sQuery);
        writer.flush();
        writer.close();
    }
    
    
    
    
    private PoweredAir_DisplayNegoSpaceReply getPnrDisplay() throws Exception
    {
    	// read the contents of the PNR display response from file
    	final String infileName = "c:\\CustomerSupport\\Airware\\BlockReduceBug\\XmlRequests\\BlockDisplayResponse.xml";
    	final File infile = new File(infileName);
    	final int size = (int )infile.length();
    	final FileReader inReader = new FileReader(infile);
    	final char[] cbuf = new char[size];
    	inReader.read(cbuf);
        final String sReply = new String(cbuf);


        try 
        {
            // unmarshall the display reply
            PoweredAir_DisplayNegoSpaceReply dispReply = new PoweredAir_DisplayNegoSpaceReply();
            dispReply = dispReply.unmarshalPoweredAir_DisplayNegoSpaceReply( new StringReader(sReply) );
            return dispReply;
        }
        catch (Exception e) 
        {
        	throw new RuntimeException("Unable to unmarshal DisplayNegoSpaceReply " + e);
        }
    }
}
