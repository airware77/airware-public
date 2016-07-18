package xmax.crs.Amadeus;

import javax.swing.JOptionPane;

import APIv2.APIproxy;
import APIv2.AmadeusAPI;

import xmax.TranServer.ConfigTranServer;
import xmax.TranServer.TranServerMain;
import xmax.crs.GdsResponseException;
import xmax.util.RegExpMatch;
import xmax.util.TypedProperties;
import xmax.util.Log.AppLog;

public class TestAmadeusConnection
{
	  /** a response code of '0', indicating a successful exchange */
	  private static final int OK = 0;
	  /** a response code of '5', indicating a Response Time-Out */
	  // private static final int RESP_TIME_OUT    =  5;
	  /** a response code of '7', indicating a Conversation Rejected Error */
	  private static final int CONV_REJECTED    =  7;
	  /** a response code of '16', indicating a Functional Error */
	  private static final int FUNCTIONAL_ERROR = 16;

	

	  /** 
	   ***********************************************************************
	   *Main method
	   ***********************************************************************
	   */
	  public static void main(String[] args)
	  {
		  try
		  {
		 
		  // look for name of configuration file
	      for ( int i = 1; i < args.length; i++ )
	      {
	    	  if ( args[i - 1].toUpperCase().startsWith("-C") )
	          {
	    		  final String sConfigFileName = args[i];
	    		  ConfigTranServer.readConfFile(sConfigFileName);
	          }
	      }

	      final TypedProperties defaultSignon = getDefaultSignon();
	      
	      // get an instance of the AmadeusAPI
	      final  AmadeusAPI amadeus = AmadeusAPI.getAmadeusAPIInstance();
	      
	      final String sDefaultServer; 
	      final String sDefaultPort;
	      final String sDefaultCorpID;
	      final String sDefaultUserID;
	      final String sDefaultPassword;
	      if (defaultSignon instanceof TypedProperties)
	      {
		      sDefaultServer = defaultSignon.getProperty("serverIP","apiv2.amadeus.net"); 
		      sDefaultPort   = defaultSignon.getProperty("port","20002");
		      sDefaultCorpID = defaultSignon.getProperty("corporateID", "XMAX-PT");
		      sDefaultUserID = defaultSignon.getProperty("userID", "MCODL2104");
		      sDefaultPassword = defaultSignon.getProperty("password", "4VXKPV2Y");
	      }
	      else
	      {
		      sDefaultServer = "apiv2.amadeus.net"; 
		      sDefaultPort   = "20002";
		      sDefaultCorpID = "XMAX-PT";
		      sDefaultUserID = "MCODL2104";
		      sDefaultPassword = "4VXKPV2Y";
	      }
	      
	      // prompt for connection parameters, provide defaults from configuration
	      final String serverIP    = promptUser("ServerIP", sDefaultServer);
	      final String sPortNumber = promptUser("Port", sDefaultPort);
	      final String corporateID = promptUser("Corporate ID", sDefaultCorpID);
	      final String userID      = promptUser("User ID", sDefaultUserID);
	      final String password    = promptUser("Password", sDefaultPassword);

	      final int portNumber = Integer.parseInt(sPortNumber);

	      System.out.println("Test Connection with Amadeus using the following parameters:");
	      System.out.println("Server = " + serverIP);
	      System.out.println("Port = " + portNumber);
	      System.out.println("Corporate ID = " + corporateID);
	      System.out.println("User ID = " + userID);
	      System.out.println("Password = " + password);
	      
	      // obtain a conversation handle:
	      final APIproxy.Reply reply = amadeus.openConversationByCorporateID(
	          serverIP, portNumber, corporateID, userID, password);

	      // if we were able to connect, obtained a reply,
	      // and the reply does not contain an error
	      if ( (reply instanceof APIproxy.Reply) && (reply.returnCode == OK) )
	      {
	    	  final String sMsg = "Successfully opened Conversation - API version: " + getVersion(reply);
	          System.out.println(sMsg);
	          System.out.println();
	      }
	      else
	      {
	        // a returnCode of '5' corresponds to a Time Out error.
	        // we may want to handle this error in a special kind of way
	        System.out.println("Failed to open Conversation with AmadeusAPI Server");
	        System.out.println("Error Code  : " + reply.returnCode);
	        System.out.println("Error String: " + amadeus.getErrorStr(reply.returnCode) );
	        System.out.println("Dump Buffer : " + reply.dumpBuffer);
	        System.out.println();
	      }

	      System.exit(0);
      }
      catch (Exception e)
      {
    	  e.printStackTrace();
    	  System.out.println("Unable to open connection to Amadeus " + e.toString());
    	//  JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
    	  System.exit(1);
 	  }
	}

	  
	  private static String promptUser(final String aPrompt, final String aDefaultValue)
	  {
		  final String sResult = JOptionPane.showInputDialog(aPrompt, aDefaultValue);
		  return sResult;
	  }
	
	  
	  private static TypedProperties getDefaultSignon()
	  {
		  final TypedProperties[] propsArray = ConfigTranServer.getSignOns("hostCode", "1A");
		  if (propsArray instanceof TypedProperties[])
		  {
			  for (TypedProperties props : propsArray)
			  {
				  return props;
			  }
		  }
		  
		  return null;
	  }
	  
	  /**
	   ***********************************************************************
	   * This method rummages through the <code>dumpBuffer</code> field of the
	   * {@link reply} object, and retrieves the Amadeus API version number
	   * of the dll through which we are connecting
	   ***********************************************************************
	   */
	  private static String getVersion(final APIproxy.Reply aReply)
	    {
	    if (aReply instanceof APIproxy.Reply &&
	         aReply.dumpBuffer instanceof String)
	      return(extractVersion(aReply.dumpBuffer));
	    else
	      return("undefined");

	    } // end getVersion

	  
	  /**
	   ***********************************************************************
	   * This method matches a pattern such as: <code>version = 2.1.10.1</code>,
	   * and returns the version number on the right of the equal sign
	   ***********************************************************************
	   */
	  private static String extractVersion(final String sDumpBuffer)
	    {
	    // (?s) treat string to be matched as single line
	    // [0-9,]* match sequence of numbers and commas
	    final String VERSION_PATTERN = "(?s)vers. = ([0-9,]*)";
	    final String[] sFields = RegExpMatch.getMatchPatterns(sDumpBuffer,VERSION_PATTERN);
	    if ( sFields instanceof String[] )
	      {
	      final String sVersion = sFields[1];
	      return(sVersion);
	      }

	    return("undefined");
	    } // end extractVersion
}
