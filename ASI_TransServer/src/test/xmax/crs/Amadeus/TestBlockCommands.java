package xmax.crs.Amadeus;

import java.util.Properties;

import xmax.TranServer.NativeAsciiReader;
import xmax.TranServer.ReqTranServer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestBlockCommands extends TestCase
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
    public TestBlockCommands(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
    	final TestSuite tSuite = new TestSuite();
    	
  //   tSuite.addTest(new TestBlockCommands("testBlockGet"));
  //   tSuite.addTest(new TestBlockCommands("testBlockModify"));
  //   tSuite.addTest(new TestBlockCommands("testBlockDelete"));
       tSuite.addTest(new TestBlockCommands("testNothing"));
    	
  //     tSuite.addTestSuite(TestBlockCommands.class);
    	
        return tSuite;
    }
    
    public void testNothing()
    {
    }
    
    public void testBlockGet() throws Exception
    {
    	final String sCommand = "CGETBLK 1ADLABCDEF  ";
    	final AmadeusAPICrs crs = getTestCrs();
    	
    	final ReqTranServer req = NativeAsciiReader.getRequestObject(sCommand, crs);
    	
    	req.runRequest(crs);
    }
    
    
    public void testBlockModify() throws Exception
    {
    	final String sCommand = "CMODBLK 1ADLABCDEF  017David Fairchild           ";
    	final AmadeusAPICrs crs = getTestCrs();
    	
    	final ReqTranServer req = NativeAsciiReader.getRequestObject(sCommand, crs);
    	
    	req.runRequest(crs);
    }    
    
    
    public void testBlockDelete() throws Exception
    {
    	final String sCommand = "CDELBLK 1ADLABCDEF  017David Fairchild           ";
    	final AmadeusAPICrs crs = getTestCrs();
    	
    	final ReqTranServer req = NativeAsciiReader.getRequestObject(sCommand, crs);
    	
    	req.runRequest(crs);
    }        
    private AmadeusAPICrs getTestCrs()
    {
    	final Properties props = new Properties();
    	final AmadeusAPICrs crs = new AmadeusAPICrs(props);
 
    	return crs;
    }

}
