package xmax.crs.Amadeus;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import xmax.util.xml.DOMutil;
import xmax.util.RegExpMatch;
import xmax.util.Log.AppLog;

import xmax.crs.GnrcCrs;
import xmax.crs.BaseCrs;
import xmax.crs.ConnectTimes;
import xmax.crs.ConnectTimesAirport;
import xmax.crs.PNR;
import xmax.crs.GdsResponseException;

import xmax.crs.GetPNR.PNRNameElement;
import xmax.crs.GetPNR.PNRItinSegment;
import xmax.crs.GetPNR.PNRRemark;
import xmax.crs.GetPNR.PNRPhoneRemark;
import xmax.crs.GetPNR.PNRTicketRemark;

import xmax.crs.Availability.DestAvailability;
import xmax.crs.Flifo.FlightInfo;

import xmax.TranServer.ConfigTranServer;
import xmax.TranServer.ReqGetTicketInfo;
import xmax.TranServer.TranClientConnection;
import xmax.TranServer.TranServerException;
import xmax.TranServer.ReqGetFare;
import xmax.TranServer.ReqIssueTicket;
import xmax.TranServer.GnrcFormat;

import xmax.crs.Block;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.xerces.dom.DocumentImpl;

/**
 ***********************************************************************
 * This class represents an Amadeus Computer Reservation System (CRS)
 * accessed through Amadeus' C XML APIv2.
 *
 * @author   Philippe Paravicini
 *
 * @version  $Revision: 38$ - $Date: 02/13/2003 10:05:49 PM$
 ***********************************************************************
 */
public class AmadeusAPICrs extends BaseCrs
{
  /** The subclass that abstracts the connection to the CRS */
  public AmadeusAPICrsConnection connection;

  /** the name of the Conversation Name property */
  // static private final String CONV_NAME = "convName";

  static private final String hostCode = "1A";
  static public final boolean isAmadeus = true;

  /** stores the maximum number to attempt to recover from errors */
  static private final int maxRetry =
    ConfigTranServer.application.getIntProperty("maxRetry",2);


  /**
   ***********************************************************************
   * This constructor sets the default properties for this instance, and is
   * called by the overloaded constructor that passes the actual properties,
   * which may overwrite these default properties.
   ***********************************************************************
   */
  public AmadeusAPICrs()
    {
    super();
    // add all default properties in this constructor

    // Used to specify whether we want to record an Amadeus Trace
    properties.setProperty("doTrace","false");
    } // end constructor

  /**
   ***********************************************************************
   * This constructor accepts a set of sign-on properties, and
   * instantiates the {@link #connection} member.
   ***********************************************************************
   */
  public AmadeusAPICrs(final Properties props)
    {
    // set the default properties
    this();

    // modify them with the properties passed
    addProperties(props);


    // open the connection to the CRS
    final boolean doFileConnection = Boolean.valueOf(properties.getProperty("useTestHarness")).booleanValue();
    if (doFileConnection)
    {
    	connection = new AmadeusAPIFileConnectionImpl(this);
    }
    else
    {
    	connection = new AmadeusAPICrsConnectionImpl(this);
    }
    
    } // end constructor


  /** 1A */
  public String getHostCode()  { return(hostCode); }

  /** 
   ***********************************************************************
   * returns the Conversation Name ("convName") property - this property is set
   * after doing a {@link Connect}, as a timestamp is used to distinguish one
   * conversation from another
   ***********************************************************************
   */
  public String getConnectionName()
    {
    return(connection.getConvHandle());
    }


  /**
   ***********************************************************************
   * returns the name of this host by retrieving the host code and calling
   * {@link BaseCrs.HostCodeToName} - currently "Amadeus" - and appending the
   * string "API" to distinguish it from an Innosys-type connection
   ***********************************************************************
   */
  public String getHostName()       
    {
    return(BaseCrs.HostCodeToName(hostCode)+"API");
    } // end getHostName


  /**
   ***********************************************************************
   * Checks to see whether we are still connected to Amadeus 
   ***********************************************************************
   */
  public boolean TestHostConnection() 
    {
    return(connection.isConnected());
    } // end TestHostConnection


  /**
   ***********************************************************************
   * This method instantiates an {@link AmadeusAPICrsConnection} object with
   * the stored signOn parameters and calls its openConversationByID method.
   ***********************************************************************
   */
  public void Connect() throws Exception
    {
    if (!TestHostConnection() )
      connection.openConnection();

    } // end Connect


  /**
   ***********************************************************************
   * This method disconnects from the Crs System if indeed the connection is
   * still open
   ***********************************************************************
   */
  public void Disconnect()
    {
    if (connection instanceof AmadeusAPICrsConnection && connection.isConnected())
      connection.closeConnection();
    } // end Disconnect


  /**
   ***********************************************************************
   * This method sends an Ignore command with no Retrieve to the Amadeus 
   * Server. 
   * <p>
   * The XML interface retains the Agent Assembly Area (AAA) metaphore that 
   * exists when using a Terminal Address. Opening a new conversation with
   * the Amadeus API clears the AAA (unlike when using a Terminal Emulator 
   * where the AAA may contain remnants of a previous session). Hence, it is 
   * not technically necessary to perform an Ignore when first starting a 
   * session, although the code still does this for backward compatibility
   * with the Terminal Address emulation functionality.</p>
   ***********************************************************************
   */
  public void Ignore() throws Exception                                                       // ignores any open transaction and clears the AAA
    {
    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();

    /*
    Element root = domQuery.createElement("PNR_Ignore_Query");
    domQuery.appendChild(root);

    DOMutil.addTextElement(domQuery,root,"RedisplayFlag","F");
    */

    Element root = domQuery.createElement("Cryptic_GetScreen_Query");
    domQuery.appendChild(root);
    DOMutil.addTextElement(domQuery,root,"Command","IG");

    Document domReply = connection.sendAndReceive(domQuery);
    
    //recycle the root variable to point to the root of the reply
    root = domReply.getDocumentElement();

    // if we did not get a PNR_Reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      // 'ErrorCode: 111' means that the IG command actually occurred
      if (sErr.indexOf("ErrorCode: 111") < 0)
        throw new GdsResponseException("Error while Ignoring:" + sErr);
      }
      
    /*
    // The PNR_Ignore_Query returns an Functional Error 16 when performing the
    // AmadeusAPICrsConnection.sendAndReceive, and it would be a bitch to fix
    
    // if we did not get a PNR_Reply, issue an error
    if ( !(root.getTagName().equals("MessagesOnly_Reply")) )
      throw new GdsResponseException("Error while Ignoring: unrecognized response");

    Element elm1 = (Element)root.getElementsByTagName("CAPI_Messages").item(0);
    String sErrCode = DOMutil.getTagValue(elm1,"ErrorCode");

    if ( !(sErrCode instanceof String) )
      throw new GdsResponseException("Error while Ignoring: invalid response");
    else if(!sErrCode.equals("111") )
      {
      String sErrText = DOMutil.getTagValue(elm1,"Text");
      throw new GdsResponseException(
          "Error while Ignoring: unrecognized response code - " + sErrCode
          + " - " + sErrText);
      }

    The defective PoweredPNR Ignore    
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    Element elm1 = domQuery.createElement("pnrActions");
    // "20" means Ignore (no retrieve)
    DOMutil.addTextElement(domQuery,elm1,"optionCode","20");
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);
    // execute the command

    Document domReply = connection.sendAndReceive(domQuery);
    //Document domReply = HostTransaction(domQuery);

    // recycle the root variable to point to the root of the reply
    root = domReply.getDocumentElement();

    // if we did not get a PNR_Reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      throw new GdsResponseException("Error while Ignoring");
    */
    }

  /**
   ***********************************************************************
   * This method does nothing in the Amadeus API; doing a PoweredPNR Ignore 
   * prior to a getPNR (as in the other Crs objects) will generate an error.
   ***********************************************************************
   */
  public void SessionStart() throws Exception  
    {
    Ignore();
    } // end SessionStart()

  /**
   ***********************************************************************
   * This method does nothing in the Amadeus API; opening a conversation
   * implicitly 'cleans' the AAA, so there is no need to clear it upon
   * ending a session.
   ***********************************************************************
   */
  public void SessionEnd() throws Exception  
    {
    Ignore();
    } // end SessionStart()


  /**
   ***********************************************************************
   * After the 5 minimum required elements (PRINT: Phone, Received-From,
   * Itinerary, Name(s), Ticketing Instructions) have been added to a 
   * Passenger Name Record (PNR), this method can be called to save the PNR
   * and retrieve a Locator for it; optionally the 'Received From' can be
   * rolled into the EndTransaction by passing the Received From string, and
   * the PNR can be cleared from the AAA by setting <code>leaveOpen</code> to
   * false.
   *
   * @param sReceivedFrom
   *   the person requesting
   * @param leaveOpen
   *   if true, specifies that we should to an End Retrieve
   ***********************************************************************
   */
  public String EndTransaction( 
      final String sReceivedFrom, final boolean leaveOpen) throws Exception
    {
    boolean endedOnce = false;
    return EndTransaction(sReceivedFrom, leaveOpen, endedOnce);
    } // end EndTransaction


  /** 
   ***********************************************************************
   * After the 5 minimum required elements (PRINT: Phone, Received-From,
   * Itinerary, Name(s), Ticketing Instructions) have been added to a 
   * Passenger Name Record (PNR), this method can be called to save the PNR
   * and retrieve a Locator for it; optionally the 'Received From' can be
   * rolled into the EndTransaction by passing the Received From string, and
   * the PNR can be cleared from the AAA by setting <code>leaveOpen</code> to
   * false.
   *
   * @param sReceivedFrom
   *   the person requesting
   * @param leaveOpen
   *   if true, specifies that we should to an End Retrieve
   * @param savedOnce
   *   if we get a 'CHECK SEGMENT CONTINUITY' we need to call this method
   *   once again - this boolean prevents the method from going into a
   *   recursive loop
   ***********************************************************************
   */
  public String EndTransaction( final String sReceivedFrom, 
                                final boolean leaveOpen, 
                                boolean endedOnce) throws Exception
    {
    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();
    Element root = domQuery.createElement("PoweredPNR_AddMultiElements");

    Element elm1 = domQuery.createElement("pnrActions");
    if (leaveOpen == true)
      // End and retrieve
      DOMutil.addTextElement(domQuery,elm1,"optionCode","11");
    else
      // End Transaction without Retrieving
      DOMutil.addTextElement(domQuery,elm1,"optionCode","10");
    root.appendChild(elm1);

    // add the root to the top level document
    domQuery.appendChild(root);

    if (sReceivedFrom != null)
      {
      Element elm11 = AmadeusAPIBuildPNRConversation.buildNode_dataElementsIndiv(domQuery,"OT","1","RF");
      AmadeusAPIBuildPNRConversation.appendNode_freetextData(elm11,"3","P22",null,sReceivedFrom);
      }

    // execute the command
    Document domReply = connection.sendAndReceive(domQuery);
    //Document domReply = HostTransaction(domQuery);

    // recycle the root variable to point to the root of the reply
    root = domReply.getDocumentElement();

    // if we did not get a PNR_Reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(" Error when saving PNR: " + sErr);
      }

    // Make sure that a PNR Locator was properly generated
    PNR pnr = AmadeusAPIParsePNR.parsePNR(domReply);
    String sLoctr  = pnr.getLocator();
    String sErrors = pnr.getErrorsConcatenated();

    if (sLoctr == null)
      {
      throw new GdsResponseException(
          " Unable to retrieve Locator after saving PNR: " +
          pnr.getErrorsConcatenated());
      }
    else if (RegExpMatch.matches(sErrors,"CHECK SEGMENT CONTINUITY") &&
             endedOnce == false)
      {
      endedOnce = true;
      return EndTransaction(sReceivedFrom, leaveOpen, endedOnce);
      }
    else if (RegExpMatch.matches(sErrors, "ERROR AT END OF TRANSACTION TIME") ||
             RegExpMatch.matches(sErrors, "ERROR AT EOT TIME") ||
             RegExpMatch.matches(sErrors, "NON-HOMOGENEOUS CONDITION") )
      {
      throw new GdsResponseException(
          " Unable to save PNR: " + pnr.getErrorsConcatenated());
      }
    else
      return(sLoctr);

    } // end EndTransaction

  /** 
   ***********************************************************************
   * After the 5 minimum required elements (PRINT: Phone, Received-From,
   * Itinerary, Name(s), Ticketing Instructions) have been added to a 
   * Passenger Name Record (PNR), this method can be called to save the PNR
   * and retrieve a Locator for it; this version hardcodes 'TRANSERVER' as the
   * Received From and re-retrieves the PNR
   ***********************************************************************
   */
  public String EndTransaction() throws Exception
    {
    boolean retrieve = true;
    return(EndTransaction("TRANSERVER",retrieve));
    }

  /** 
   ***********************************************************************
   * After the 5 minimum required elements (PRINT: Phone, Received-From,
   * Itinerary, Name(s), Ticketing Instructions) have been added to a 
   * Passenger Name Record (PNR), this method can be called to save the PNR
   * and retrieve a Locator for it; this version re-retrieves the PNR
   ***********************************************************************
   */
  public String EndTransaction(String sReceivedFrom) throws Exception
    {
    boolean retrieve = true;
    return(EndTransaction(sReceivedFrom,retrieve));
    }

  /**
   ***********************************************************************
   * This method is called to save schedule changes in a Passenger Name Record;
   * in turn, it calls {@link AmadeusBuildPNRConversation.acceptSchedChange}
   ***********************************************************************
   */
  public void AcceptSchedChange(String sLocator, String sReceivedFrom) 
    throws Exception
    {
    AmadeusBuildPNRConversation.acceptSchedChange(this, sLocator, sReceivedFrom);
    } 

  /**
   ***********************************************************************
   * Used to send 'free form' cryptic commands through the API's cryptic
   * interface.
   ***********************************************************************
   */
  public void FreeForm (final String aRequest, final StringBuffer aResponse) 
    throws GdsResponseException
    {
    final String sResponse = sendRecvCryptic(aRequest);
    aResponse.setLength(0);
    aResponse.append(sResponse);
    } // end FreeForm


  /** 
   ***********************************************************************
   * Calls {@link AmadeusAPIGetPNRConversation.retrievePNR} 
   * Retrieves and parses all the segments from a Passenger Name Record (PNR)
   * from this Computer Reservation System (CRS) by calling
   * {@link AmadeusAPIGetPNRConversation.retrievePNR}.
   *
   * @param aLocator
   *   the <code>String</code> locator identifying this PNR
   * @param aPNR
   *   the <code>PNR</code> object that will store the PNR
   * @param aLeaveOpen
   *   leaves the PNR in the AAA so that it can be edited further; setting
   *   this flag to false would perform an ignore after retrieving the PNR
   *
   * @see  AmadeusAPIGetPNRConversation.GetAllSegments
   ***********************************************************************
   */
  public void GetPNRAllSegments(final String aLocator, 
                                final PNR aPNR, 
                                final boolean aLeaveOpen) throws Exception
    {
    AmadeusAPIGetPNRConversation.retrievePNR(this,aLocator,aPNR,aLeaveOpen);
    }  // end GetPNRAllSegments


  /**
   ***********************************************************************
   * Calls {@link AmadeusAPIGetPNRConversation.retrieveFromQueue} 
   * to retrieve the next Passenger Name Record (PNR) from the given Queue and
   * optionally remove the PNR from the Queue, and/or leave the PNR open for
   * editing; if the Queue is empty, this method returns silently.
   * 
   * @param aQueueName
   *   the name of the Queue from which the PNR is to be retrieved
   * @param aPNR
   *   an empty <code>PNR</code> object in which to store the PNR retrieved
   *   from the queue
   * @param aRemove
   *   indicates that the PNR should be removed from the Queue after retrieval
   * @param aLeaveOpen
   *   indicates that the PNR should be left in the Agent Assembly Area (AAA)
   *   after retrieval, presumably so that it can be modified
   ***********************************************************************
   */
  public void GetPNRFromQueue(final String aQueueName, final PNR aPNR, 
                              final boolean aRemove, final boolean aLeaveOpen) 
    throws Exception 
    {
    AmadeusAPIGetPNRConversation.retrieveFromQueue(this,aQueueName,aPNR,aRemove,aLeaveOpen);
    }

  /** 
   ***********************************************************************
   * Retrieves and parses a Passenger Name Record (PNR) from the 
   * Agent Assembly Area (AAA) of this Computer Reservation System (CRS)
   * by calling {@link AmadeusAPIGetPNRConversation.redisplayPNR}.
   *
   * @param aPNR
   *   the <code>PNR</code> object that will store the PNR
   *
   * @see  AmadeusAPIGetPNRConversation.redisplayPNR
   ***********************************************************************
   */
  public void GetPNRFromAAA(final PNR aPNR) throws Exception
    {
    AmadeusAPIGetPNRConversation.redisplayPNR(this,aPNR);
    } // end GetPNRFromAAA

  /**
   ***********************************************************************
   * This method is used to load a Passenger Name Record (PNR) in the 
   * Agent Assembly Area (AAA).
   ***********************************************************************
   */
  public void LoadPNRIntoAAA(final String aLocator) throws Exception
    {
    boolean leaveOpen = true;
    AmadeusAPIGetPNRConversation.retrievePNR(this,aLocator,new PNR(),leaveOpen);
    }

  /**
   ***********************************************************************
   * This function retrieves the managed block locators for any segments in the
   * PNR that were sold from a Managed Block (such as NegoSpace in Amadeus, BSG
   * in Sabre, WGR in Galileo...); if the PNR has data in it, it is not
   * redisplayed; if it is empty, the specified locator is used to retrieve the
   * PNR; if the locator is null, an attempt is made to retrieve the PNR from the AAA
   ***********************************************************************
   */
  public void getManagedBlockLocators(final String aLocator, final PNR aPNR)
    throws Exception
    {
    AmadeusAPIGetPNRConversation.getNegoSpaceLocators(this, aLocator, aPNR);
    } 

  /**
   ***********************************************************************
   * This method is the main method used to retrieve flight information
   * through the Amadeus API; this method will try up to 'maxRetry' attempts to
   * complete the transaction, and will reconnect if the connection is lost.
   * 
   * @param aCarrier  
   *   the 2-letter code of the Carrier operating the fligh 
   * @param aFlightNum
   *   the number of the flight
   * @param aDepDate
   *   the departure date in String format?
   * @param aFlightData
   *   the FlightInfo object that will store the flight information
   ***********************************************************************
   */
  public void GetFlightInfo(final String aCarrier, 
                            final int aFlightNum, 
                            final String aDepDate,
                            final FlightInfo aFlightData) throws Exception
    {
    final AmadeusAPIFlightServiceConversation FlightServiceConv =
        new AmadeusAPIFlightServiceConversation();

    for (int i=1; i <= maxRetry; i++)
      {
      try {
        FlightServiceConv.getFlightInfo(this,aCarrier,aFlightNum,aDepDate,null,null,aFlightData);
        break;
        } 
      catch (Exception e) {
        if (i == maxRetry)
          throw e;
        else
          recover(e);
        }
      } // end for

    } // end GetFlightInfo


  /**
   ***********************************************************************
   * This method is the same as above, but allows the user to specify a
   * Departure and Arrival Cities, in order to extract the correct date/time
   * information for a segment that is part of a more extensive itinerary that
   * may span two days; this method will try up to 'maxRetry' attempts to
   * complete the transaction, and will reconnect if the connection is lost.
   * 
   * @param aCarrier  
   *   the 2-letter code of the Carrier operating the fligh 
   * @param aFlightNum
   *   the number of the flight
   * @param aDepDate
   *   the departure date in String format?
   * @param sDepCity
   *   the departure city
   * @param sArrCity
   *   the arriving city
   * @param aFlightData
   *   the FlightInfo object that will store the flight information
   ***********************************************************************
   */
  public void GetFlightInfo(final String aCarrier, 
                            final int aFlightNum, 
                            final String aDepDate,
                            final String sDepCity,
                            final String sArrCity,
                            final FlightInfo aFlightData) throws Exception
    {
    final AmadeusAPIFlightServiceConversation FlightServiceConv =
        new AmadeusAPIFlightServiceConversation();

    for (int i=1; i <= maxRetry; i++)
      {
      try {
        FlightServiceConv.getFlightInfo(
            this,aCarrier,aFlightNum,aDepDate, sDepCity, sArrCity, aFlightData);
        break;
        } 
      catch (Exception e) {
        if (i == maxRetry)
          throw e;
        else
          recover(e);
        }
      } // end for

    } // end GetFlightInfo


  /** 
   ***********************************************************************
   * Retrieves an Availability response from the Amadeus API by instantiating
   * an AmadeusAPIGetAvailConversation, and passing the DestAvailability
   * that will store the availability requested; this method will try up to
   * 'maxRetry' attempts to complete the transaction, and will reconnect if
   * the connection is lost.
   *
   * @see AmadeusAPIGetAvailConversation
   * @see AmadeusAPIGetAvailConversation.getAvailability
   ***********************************************************************
   */
  public void GetAvailability(final DestAvailability aAvail) throws Exception
    {
    final AmadeusAPIGetAvailConversation GetAvailConv = new AmadeusAPIGetAvailConversation();

    final boolean doNewAvail = 
      ConfigTranServer.application.getBooleanProperty("newAvail",false);

    for (int i=1; i <= maxRetry; i++)
      {
      try {
          GetAvailConv.getAvailability(this,aAvail);
          break;
        } 
      catch (Exception e) {
        if (i == maxRetry)
          throw e;
        else
          recover(e);
        }
      }

    } // end GetAvailability


  /**
   ***********************************************************************
   * Retrieves connect times from the crs for two or more segments; note
   * that because there is no particular structured interface for this function
   * in the Amadeus API, we merely call the screen-scraping routines and use
   * the {@link sendRecvCryptic} method wrappered inside the {@link
   * HostTransaction} method
   ***********************************************************************
   */
  public void GetConnectTime(final ConnectTimes aConnectTimes) throws Exception
    {
    final AmadeusGetConnectTimeConversation ConnectTimeConv = new AmadeusGetConnectTimeConversation();
    ConnectTimeConv.GetConnectInfo(this,aConnectTimes);
    }

  /**
   ***********************************************************************
   * Retrieves standard connect times from the crs for a specific airport ;
   * note that because there is no particular structured interface for this
   * function in the Amadeus API, we merely call the screen-scraping routines
   * and use the {@link sendRecvCryptic} method wrappered inside the {@link
   * HostTransaction} method; also, Amadeus does not provide standard connect
   * times for online flights (same airline) and hence these are returned as
   * nulls in this method.
   ***********************************************************************
   */
  public void GetConnectTimesAirport(final ConnectTimesAirport airport) throws Exception
    {
    final AmadeusGetConnectTimeConversation ConnectTimeConv = new AmadeusGetConnectTimeConversation();
    ConnectTimeConv.GetAirportConnectInfo(this,airport);
    }

  /**
   ***********************************************************************
   * This method is used to add a Corporate Header to a Passenger Name Record 
   * (PNR); it does so by creating a new {@link AmadeusAPIBuildPNRConversation}
   * and calling its 
   * {@link AmadeusAPIBuildPNRConversation.addCorpHeader addCorpHeader}
   * method.
   *
   * @see  AmadeusAPIBuildPNRConversation.addCorpHeader
   ***********************************************************************
   */
  public void AddCorpHeader(final String sGroupName, final int iNumSeats) throws Exception 
    {
    final AmadeusAPIBuildPNRConversation BuildPNRConv = new AmadeusAPIBuildPNRConversation();
    BuildPNRConv.addCorpHeader(this,sGroupName,iNumSeats);
    } // AddCorpHeader

  /**
   ***********************************************************************
   * This method is used to add a Name to a Passenger Name Record (PNR);
   * it does so by creating a new {@link AmadeusAPIBuildPNRConversation}
   * and calling its {@link AmadeusAPIBuildPNRConversation.addName addName}
   * method.
   * 
   * @param aName
   *   the PNRNameElement containing the information of the passenger
   *   that we wish to add to the PNR
   *
   * @see  AmadeusAPIBuildPNRConversation.addName
   ***********************************************************************
   */
  public void AddName(final PNRNameElement aName) throws Exception
    {
    final AmadeusAPIBuildPNRConversation BuildPNRConv = new AmadeusAPIBuildPNRConversation();
    BuildPNRConv.addName(this,aName);
    } // end Add Name


  /**
   ***********************************************************************
   * This method instantiates a new {@link AmadeusAPIBuildPNRConversation}
   * and calls its 
   * {@link AmadeusAPIBuildPNRConversation.addPnrElemenst addPnrElements}
   * method to add an itinerary to a Passenger Name Record (PNR).
   *
   * @see  PNRItinSegment
   ***********************************************************************
   */
 public void addPnrElements(final String aLocator, final PNRItinSegment[] aSegs,
     final String aReceiveBy) throws Exception
   {
   final AmadeusAPIBuildPNRConversation BuildPNRConv = new AmadeusAPIBuildPNRConversation();
   //BuildPNRConv.addPnrElements(this,aLocator,aSegs,aReceiveBy);
   BuildPNRConv.addPnrElements(this, aSegs);
   }


  /**
   ***********************************************************************
   * This method is used to change the Name on a Passenger Name Record; it calls
   * {@link AmadeusAPIBuildPNRConversation.changePnrElements}
   ***********************************************************************
   */
  public void changePnrElements(final String aLocator, final PNRNameElement[] aOldNames, 
      final PNRNameElement[] aNewNames, final String aReceiveBy) 
    throws Exception
    {
    //final AmadeusBuildPNRConversation BuildPNRConv = new AmadeusBuildPNRConversation();
    //BuildPNRConv.changePnrElements(this,aLocator,aOldNames,aNewNames,aReceiveBy);
    AmadeusAPIBuildPNRConversation.changePnrElements(this,aLocator,aOldNames,aNewNames,aReceiveBy);
    }

  /**
   ***********************************************************************
   * This method instantiates a new {@link AmadeusAPIBuildPNRConversation}
   * and calls its 
   * {@link AmadeusAPIBuildPNRConversation.addPnrElemenst addPnrElements}
   * method to add an itinerary to a Passenger Name Record (PNR).
   *
   * @see  PNRItinSegment
   ***********************************************************************
   */
 public void addPnrElements( final String sLocator, final PNRRemark[] remarks,
     final String sReceivedFrom) throws Exception
   {
   AmadeusAPIBuildPNRConversation.addPnrElements(this,sLocator,remarks,sReceivedFrom);
   }


  /**
   ***********************************************************************
   * This method is used to change the itinerary of one or more passengers in a
   * Passenger Name Record (PNR), as described below.  
   * <p>
   * If the passenger list is not specified, or if the passenger list includes
   * all passengers on the PNR, the PNR is modified without creating a new
   * PNR.</p> 
   * <p>
   * On the other hand, if the passenger list provided only includes a subset
   * of passengers on the PNR, the designated passengers are first split from
   * the PNR, and the modifications are performed on the split PNR.</p>
   * <p>
   * The split PNR is only saved after the modifications have been successfully
   * performed.  If an error occurs while cancelling the existing segments or
   * adding the new segments, the whole operation fails, and the original PNR
   * is left as it was originally.</p>
   * 
   * @param aLocator
   *   the locator identifying the PNR to be modified
   *
   * @param aPsgrList
   *   the list of passengers which itinerary will be modified; if
   *   <code>null</code>, modify all passengers
   *
   * @param aOldSegments
   *   the segments to be cancelled from the existing PNR
   *
   * @param aNewSegments
   *   the new segments to be added to the PNR
   *
   * @param aReceiveBy
   *   the name of the person requesting the change 
   *   
   * @param aNewLocator
   *   String Buffer that will hold the split locator, if need be to split the
   *   PNR
   ***********************************************************************
   */
  public void changePnrItinerary(final String aLocator, final PNRNameElement[]
      aPsgrList, final PNRItinSegment[] aOldSegments, final PNRItinSegment[]
      aNewSegments, final String aReceiveBy, StringBuffer aNewLocator) 
    throws Exception
    {
    AmadeusAPIBuildPNRConversation.changeItinerary(
        this, aLocator, aPsgrList, aOldSegments, aNewSegments,
        aReceiveBy, aNewLocator);
    }

  /**
   ***********************************************************************
   * This method is used to add different kinds of Remarks to a Passenger
   * Name Record (PNR); it turn it calls 
   * {@link AmadeusAPIBuildPNRConversation.addRemark}
   *
   * @see AmadeusAPIBuildPNRConversation.addRemark
   ***********************************************************************
   */
  public void AddRemark(final PNRRemark aRemark) throws Exception
    {
    final AmadeusAPIBuildPNRConversation BuildPNRConv = new AmadeusAPIBuildPNRConversation();
    BuildPNRConv.addRemark(this,aRemark);
    }

  /**
   ***********************************************************************
   * This method is used to add a Phone field to a Passenger Name Record (PNR)
   * 
   * @param aPhone
   *   the Phone String that we wish to add to the PNR
   *
   * @see  AmadeusAPIBuildPNRConversation.addPhone
   ***********************************************************************
   */
  public void AddPhone(final String aPhone) throws Exception
    {
    AmadeusAPIBuildPNRConversation.addPhone(this,aPhone);
    } // end AddPhone


  /**
   ***********************************************************************
   * This method is used to add a Form of Payment (FOP) to a 
   * Passenger Name Record (PNR);
   * it does so by creating a new {@link AmadeusAPIBuildPNRConversation}
   * and calling its 
   * {@link AmadeusAPIBuildPNRConversation.addFormOfPayment addFormOfPayment}
   * method.
   * 
   * @param aTicketRemark
   *   a structured string containing ticketing instructions
   *
   * @see  AmadeusAPIBuildPNRConversation.addTicket
   ***********************************************************************
   */
  public void AddFOP(final String sFormOfPayment) throws Exception
    {
    final AmadeusAPIBuildPNRConversation BuildPNRConv = new AmadeusAPIBuildPNRConversation();
    BuildPNRConv.addFormOfPayment(this,sFormOfPayment);
    }

  /**
   ***********************************************************************
   * This method is used to add a Commission Amount to a 
   * Passenger Name Record (PNR);
   * it does so by calling 
   * {@link AmadeusAPIBuildPNRConversation.addCommission}
   * 
   * @param aTicketRemark
   *   a structured string containing ticketing instructions
   *
   * @see  AmadeusAPIBuildPNRConversation.addTicket
   ***********************************************************************
   */
  public void AddCommission(final float fAmount, final boolean isPercent) throws Exception
    {
    AmadeusAPIBuildPNRConversation.addCommission(this, fAmount, isPercent);
    }

  /** 
   ***********************************************************************
   * Uses the cryptic interface to send a <code>FT</code> command and a 
   * string containing the endorsement information; the cryptic interface was
   * used because Airware does not provide a structured interface to the
   * AddTour command, and the structured API call requires that it provided in
   * different pieces (Text, passenger/segment associations...); until such
   * time as the Airware interface is made more structured, it's simpler to
   * pass an unstructured string through the cryptic and, if an error occurs,
   * return the cryptic error.
   ***********************************************************************
   */
  public void AddEndorsement(final String aEndorsement) throws Exception
   {
   final String sCommand = "FE " + GnrcFormat.ShowString(aEndorsement);
   final String sResponse = HostTransaction(sCommand).trim();
   if ( sResponse.indexOf("RP/") < 0 )
     throw new GdsResponseException("Unable to add endorsement",sCommand,sResponse);
   }

 
  /** 
   ***********************************************************************
   * Uses the cryptic interface to send a <code>FT</code> command and a 
   * string containing the tour code; the cryptic interface was used because
   * Airware does not provide a structured interface to the AddTour command,
   * and the structured API call requires that it provided in different pieces
   * (Tour Code Type, Text...); until such time as the Airware interface is
   * made more structured, it's simpler to pass an unstructured string through
   * the cryptic and, if an error occurs, return the cryptic error.
   ***********************************************************************
   */
  public void AddTourCode(final String aTourCode) throws Exception
   {
   final String sCommand = "FT " + GnrcFormat.ShowString(aTourCode);
   final String sResponse = HostTransaction(sCommand).trim();
   if ( sResponse.indexOf("RP/") < 0 )
     throw new GdsResponseException("Unable to add tour code: " + sResponse,sCommand,sResponse);
   }


  /**
   ***********************************************************************
   * This method is used to add Ticketing Instructions to a 
   * Passenger Name Record (PNR);
   * it does so by creating a new {@link AmadeusAPIBuildPNRConversation}
   * and calling its {@link AmadeusAPIBuildPNRConversation.addTicket addTicket}
   * method.
   * 
   * @param aTicketRemark
   *   a structured string containing ticketing instructions
   *
   * @see  AmadeusAPIBuildPNRConversation.addTicket
   ***********************************************************************
   */
  public void AddTicket(final String aTicketRemark) throws Exception
    {
    final AmadeusAPIBuildPNRConversation BuildPNRConv = new AmadeusAPIBuildPNRConversation();
    BuildPNRConv.addTicket(this,aTicketRemark);
    }


  /**
   ***********************************************************************
   * Adds a 'Received By' line to a Passenger Name Record (PNR)
   * 
   * @param aName  
   *   the name of the agent
   ***********************************************************************
   */
  public void AddReceiveBy(final String aName) throws Exception
    {
    final AmadeusAPIBuildPNRConversation BuildPNRConv = new AmadeusAPIBuildPNRConversation();
    BuildPNRConv.addReceivedBy(this,aName);
    } // end AddReceiveBy


  /**
   ***********************************************************************
   * Cancels one or more Segments from a Passenger Name Record (PNR)
   ***********************************************************************
   */
  public void deletePnrElements(final String aLocator,
                                final PNRItinSegment[] aAirSegments, 
                                final String aReceiveBy) throws Exception
    {
    AmadeusAPIBuildPNRConversation.cancelAirSegments(this,aLocator,aAirSegments,aReceiveBy);
    } // end cancelAirSegments


  /**
   ***********************************************************************
   * Cancels one or more remarks from a Passenger Name Record (PNR)
   ***********************************************************************
   */
 public void deletePnrElements(final String aLocator, final PNRRemark[] aRemarks, final String aReceiveBy) throws Exception
   {
   AmadeusAPIBuildPNRConversation.cancelRemarks(this,aLocator,aRemarks);
   }

  /**
   ***********************************************************************
   * Cancels the whole Itinerary on a Passenger Name Record (essentially
   * cancels the PNR)
   ***********************************************************************
   */
  public void cancelItinerary(final String aLocator, final String sReceivedBy) throws Exception
                      
    {
    AmadeusAPIBuildPNRConversation.cancelItinerary(this,aLocator,sReceivedBy);
    } // end cancelItinerary

 /**
  ***********************************************************************
  * Removes a remark from a PNR
  ***********************************************************************
  */
 public void CancelRemark(final String aLocator, final PNRRemark aRemark) throws Exception
   {
   PNRRemark[] aryRemark = {aRemark};
   AmadeusAPIBuildPNRConversation.cancelRemarks(this,aLocator,aryRemark);
   }

 /** 
  ***********************************************************************
  * Removes a phone line from a PNR
  ***********************************************************************
  */
 public void CancelPhone(final String aLocator, final String aPhone) throws Exception
   {
   AmadeusAPIBuildPNRConversation.cancelRemark(this,aLocator,new PNRPhoneRemark(aPhone));
   }

 /** 
  ***********************************************************************
  * Removes a ticket line from a PNR
  ***********************************************************************
  */
 public void CancelTicket(final String aLocator, final String aTicket) throws Exception
   {
   AmadeusAPIBuildPNRConversation.cancelRemark(this,aLocator,new PNRTicketRemark(aTicket));
   }

  /**
   ***********************************************************************
   * This method is used to add a Passenger Name Record (PNR) to a Queue; valid
   * strings identifying the PNR to be queued and the Queue Name must be
   * available in order to use this method successfully.
   * must be passed; this method in turn instantiates a 
   * {@link AmadeusAPIBuildPNRConversation} and calls its 
   * {@link AmadeusAPIBuildPNRConversation.queuePNR queuePNR} method.
   ***********************************************************************
   */
  public void QueuePNR(final String aLocator, final String aQueueName) 
    throws Exception
    {
    this.LoadPNRIntoAAA(aLocator);

    // build the XML string
    String sXmlQry  = "<Queue_PlaceOnSingle_Query><Queue>";
    sXmlQry += aQueueName;
    sXmlQry += "</Queue></Queue_PlaceOnSingle_Query>";

    String sResponse = connection.sendAndReceive(sXmlQry);

    // parse the response
    Document domReply = DOMutil.stringToDom(sResponse);
    Element root = domReply.getDocumentElement();

    // a successful exchange is reported within an error structure (go figure)
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      if ( !(RegExpMatch.matches(sResponse, "ON QUEUE") && 
             RegExpMatch.matches(sResponse, aLocator)))
        throw new GdsResponseException(
            "QueuePNR: unable to queue PNR: " + sErr);
      }
    else
      throw new GdsResponseException(
          "QueuePNR: unrecognized response from Amadeus API: " + sResponse);

    } // end QueuePNR

 /**
  ***********************************************************************
  * Calls a {@link AmadeusAPIBuildPNRConversation.splitPNR} to split a
  * Passenger Name Record (PNR)
  ***********************************************************************
  */
 public void splitPNR(final String aLocator, final int aNumUnassigned, 
                      final PNRNameElement[] aNames, final String aReceiveBy, 
                      final StringBuffer aNewLocator) throws Exception
   {
   AmadeusAPIBuildPNRConversation.splitPNR(
       this,aLocator,aNumUnassigned,aNames,aReceiveBy,aNewLocator);

   } // end splitPNR


  /**
   ***********************************************************************
   * Calls {@link AmadeusAPIFareConversation.getFareForPNR} to build and
   * execute a faring request and to parse the response.
   *
   * @see AmadeusAPIFareConversation.getFareForPNR
   ***********************************************************************
   */
  public void FarePNR (ReqGetFare aRequest) throws Exception
    {
    AmadeusAPIFareConversation.getFareForPNR(this,aRequest);
    } // end FarePNR

  /**
   ***********************************************************************
   * Issue a Ticket for this PNR
   ***********************************************************************
   */
  public void IssueTicket(final ReqIssueTicket aRequest) throws Exception
    {
    AmadeusAPIFareConversation.issueTicket(this,aRequest);
    }

  
  /**
   ***********************************************************************
   * Get ticket info for this PNR
   ***********************************************************************
   */
  public void getTicketInfo(final ReqGetTicketInfo aRequest) throws Exception
    {
    AmadeusGetTicketInfoConversation.getTicketInformation(this,aRequest);
    }
  
  /**
   ***********************************************************************
   * Create a block
   ***********************************************************************
   */
  public void blockBuild(final Block aBlock) throws Exception
    {
    AmadeusAPIBlockConversation.createBlock(this,aBlock);
    }
 
  /**
   ***********************************************************************
   * Change a block
   ***********************************************************************
   */
  public void blockModify(final String aLocator, final String aCarrierCode, final int aNumAllocated) throws Exception
    {
    AmadeusAPIBlockConversation.changeBlock(this,aLocator,aCarrierCode,aNumAllocated);
    }
 
  /**
   ***********************************************************************
   * Delete a block
   ***********************************************************************
   */
  public void blockDelete(final String aLocator, final String aCarrierCode) throws Exception
    {
    AmadeusAPIBlockConversation.deleteBlock(this,aLocator,aCarrierCode);
    }
 
  /**
   ***********************************************************************
   * Get block details
   ***********************************************************************
   */
  public void blockGet(final String aLocator, final String aCarrierCode, final Block aBlock) throws Exception
    {
    AmadeusAPIBlockConversation.displayBlock(this,aLocator,aCarrierCode,aBlock);
    }

  /** 
   ***********************************************************************
   * read next NegoSpace message from the specified Amadeus block queue 
   ***********************************************************************
   */
  public void blockReadMessage(final String queueName, final String queueCategory,
      final boolean leaveOnQueue, List blockMessageList) throws Exception
    {
    AmadeusAPIBlockConversation.readQueueMessage(
        this,queueName,queueCategory,leaveOnQueue,blockMessageList);
    }

  /**
   ***********************************************************************
   * This method sends and receives a Cryptic Command through the Amadeus API
   * Cryptic Interface.
   ***********************************************************************
   */
  public String sendRecvCryptic(String sCommand) throws GdsResponseException
    {
    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();

    Element root = domQuery.createElement("Cryptic_GetScreen_Query");
    domQuery.appendChild(root);
    DOMutil.addTextElement(domQuery,root,"Command",sCommand);

    Document domReply = connection.sendAndReceive(domQuery);

    return(readAPICrypticResponse(sCommand, domReply));
    
    /*
    // recycle the root variable to point to the root of the reply
    root = domReply.getDocumentElement();

    // if we received a MessagesOnly_Reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "The command: '" + sCommand + "' generated an error: " + sErr);
      }
    else if ( root.getTagName().equals("Cryptic_GetScreen_Reply") )
      {
      Element el = (Element)root.getElementsByTagName("CAPI_Screen").item(0);
      String response = DOMutil.getTagValue(el,"Response");
      return(response);
      }
    else 
      throw new GdsResponseException("sendRecvCryptic: unrecognized response");
    */

    } // end sendRecvCryptic


  /**
   ***********************************************************************
   * Reads the response to a Cryptic_GetScreen_Query
   * @throws GdsResponseException if a MessagesOnly_Reply or an unrecognized
   * response is encountered
   ***********************************************************************
   */
  public static String readAPICrypticResponse(String sCommand, Document domReply)
    throws GdsResponseException
    {
    Element root = domReply.getDocumentElement();

    // if we received a MessagesOnly_Reply, issue an error
    if ( root.getTagName().equals("MessagesOnly_Reply") )
      {
      String sErr = AmadeusAPIParsePNR.readError_MessagesOnly_Reply(domReply);
      throw new GdsResponseException(
          "The command: '" + sCommand + "' generated an error: " + sErr);
      }
    else if ( root.getTagName().equals("Cryptic_GetScreen_Reply") )
      {
      Element el = (Element)root.getElementsByTagName("CAPI_Screen").item(0);
      String response = DOMutil.getTagValue(el,"Response");
      return(response);
      }
    else 
      throw new GdsResponseException("sendRecvCryptic: unrecognized response");

    } // end readAPICrypticResponse

  /**
   ***********************************************************************
   * This method uses Amadeus' <code>PoweredPNR_Hybrid</code> interface to send
   * a Passenger Name Record related cryptic command and receive in return a
   * <code>PoweredPNR_PNRReply</code> response
   * THIS APPEARS IN AMADEUS DOCS, BUT HAS NOT BEEN IMPLEMENTED YET
   ***********************************************************************
   */
  /*
  public Document sendRecvHybrid(String sCommand) throws GdsResponseException
    {
    // create a document object and our top level Element
    DocumentImpl domQuery = new DocumentImpl();

    Element root = domQuery.createElement("PoweredPNR_Hybrid");
    domQuery.appendChild(root);
    DOMutil.addTextElement(domQuery,root,"Command",sCommand);

    Document domReply = connection.sendAndReceive(domQuery);

    return(domReply);
    } // end sendRecvHybrid
  */


  /**
   ***********************************************************************
   * This is the same as {@link sendRecvCryptic} and is used for compatability
   * with other subclasses of {@link GnrcCrs}
   ***********************************************************************
   */
  public String HostTransaction(String command) throws GdsResponseException
    {
    return(sendRecvCryptic(command));
    } // end HostTransaction


  /**
   ***********************************************************************
   * This method calls and returns the overloaded getAllHostResponses method.
	 *
   * It defines three perl5 regular expression patterns as constants:
   * <PRE>
   *   MORE_REQUEST    = "MD";
   *   MORE_RESPONSE   = "(?s)[-#\\)\\|][\r\n]?>?$";
	 *     (?s)       : treat pattern as single line
	 *     [-#\\)\\|] : match any of - # ) |
	 *     [\r\n]?    : followed by 0 or 1 linefeed or new line
	 *     >?$        : match 0 or 1 '>' at end of string
   *   NOMORE_RESPONSE = "(NOTHING TO SCROLL|NO MORE|END OF SCROLL)"
   * </PRE>
   * which are used to define return generic return patterns and which are passed
   * to the overloaded version of the function.
	 *
   ***********************************************************************
   */
  public String[] getAllHostResponses(final String aRequest) throws Exception
    {
    final String MORE_REQUEST    = "MD";
    final String MORE_RESPONSE   = "(?s)[-#\\)\\|][\r\n]?>?$";
    final String NOMORE_RESPONSE = "(NOTHING TO SCROLL|NO MORE|END OF SCROLL|END OF DISPLAY)";

    return( getAllHostResponses(aRequest,MORE_REQUEST,MORE_RESPONSE,NOMORE_RESPONSE) );
    }


  /**
   ***********************************************************************
   * This method returns all the text returned to the terminal by the TA.
   *
   * @param  aRequest
   *   an Airware request in a string format
   * @param  aMoreRequest
   *   the command for paging down for more info on the terminal
   * @param  aMoreRespPattern
   *   a set of characters indicating that the terminal has returned information
   * @param  aNoMoreRespPattern
   *   a set of characters that indicate that the terminal has reached the 'bottom'
   *   of the request and cannot page down any further
   ***********************************************************************
   */
  public String[] getAllHostResponses(
      final String aRequest, final String aMoreRequest,
      final String aMoreRespPattern, final String aNoMoreRespPattern) throws Exception
    {
    final String MORE_REQUEST    = "MD";
    final String MORE_RESPONSE   = "(?s)[-#\\)\\|][\r\n]?>?$";
    final String NOMORE_RESPONSE = "(NOTHING TO SCROLL|NO MORE|END OF SCROLL|END OF DISPLAY)";

    // make sure a command is specified
    if ( (aRequest instanceof String) == false )
      throw new TranServerException("Must specify a command to send to the host");


    String sInString;
    final Vector ResponseList = new Vector();

    // collect host responses
    String sRequest = aRequest;
    while ( sRequest instanceof String )
      {
      sInString = HostTransaction(sRequest);
      ResponseList.add(sInString);
      // check if returned string indicates there's no more data
      if ( aNoMoreRespPattern instanceof String )
        {
        if ( RegExpMatch.matches(sInString,aNoMoreRespPattern) )
          break;
        }

      // check if returned string indicates there's more data
      if ( aMoreRespPattern instanceof String )
        {
        if ( RegExpMatch.matches(sInString,aMoreRespPattern) == false )
          break;
        }
      else
        break;

      sRequest = aMoreRequest;
      }

    // make sure you got some kind of response
    if ( ResponseList.size() <= 0 )
      throw new TranServerException("No host responses were collected");

    // convert response list into an array of strings
    final String[] ResponseLines = new String[ ResponseList.size() ];
    ResponseList.toArray(ResponseLines);
    return(ResponseLines);

    } // end getAllHostResponses

  /**
   ***********************************************************************
   * This function converts a date in CRS format ddMMM, into the format
   * ddMMMyyyy suitable for inputting to the Amadeus XML server; if the
   * ddMMM date has already passed for the current year, it appends the
   * following year to the string.
   ***********************************************************************
   */
  public static String fmt_ddMMM_To_ddMMMyyyy(String ddMMM)
    {
    // get the current date
   // Date now = new Date();

    // get the current date (with no time component)
    final Calendar cal = GregorianCalendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    final Date now = cal.getTime(); 
    
    // get the current year
    int yyyy = cal.get(GregorianCalendar.YEAR);

    final SimpleDateFormat fmt = new SimpleDateFormat("ddMMMyyyy");

    // assume that the date/month passed refers to the current year
    Date d = fmt.parse( ddMMM + yyyy, new ParsePosition(0) );

    // if that date has already passed, increment the year
    if (d.before(now))
      yyyy++;

    // concatenate and return the string
    return(ddMMM += yyyy);

    } // end fmt_ddMMM_To_ddMMMyyyy


  /**
   ***********************************************************************
   * This function converts a date in CRS format ddMMM, into the format
   * ddMMyy suitable for inputting to the 'Powered' interface of the
   * Amadeus XML server; if the ddMMM date has already passed for the
   * current year, it returns the date as of the following year.
   ***********************************************************************
   */
  public static String fmt_ddMMM_To_ddMMyy(String ddMMM)
    {
    // get the current date  // TODO remove the time component of this
    // Date now = new Date();

    // get the current date (with no time component)
    final Calendar cal = GregorianCalendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    final Date now = cal.getTime(); 

    final SimpleDateFormat fmt_in  = new SimpleDateFormat("ddMMMyyyy");
    final SimpleDateFormat fmt_out = new SimpleDateFormat("ddMMyy");

    // assume that the date/month passed refers to the current year
    int yyyy = cal.get(GregorianCalendar.YEAR);
    Date d = fmt_in.parse( ddMMM + yyyy, new ParsePosition(0) );

    // if that date has already passed, increment the year
    if (d.before(now))
      {
      yyyy++;
      d = fmt_in.parse( ddMMM + yyyy, new ParsePosition(0) );
      }

    // concatenate and return the string
    return(fmt_out.format(d));

    } // end fmt_ddMMM_To_ddMMyy


  /**
   ***********************************************************************
   * This function converts a date and time in String format into a
   * <code>long</code>, according to the specified format resulting from
   * the concatenation of the date and time string; see below.
   * For example:
   * <code>fmtDateTimeToLong("17APR2002", "1606", "ddMMMyyyyHHmm")</code>
   * will format the date/time: April 17, 2002, 4:06pm into its
   * <code>long</code> representation.<br>
   * Note that the format specified must be a parameter that can be accepted
   * by the underlying {@link SimpleDateFormat} used to format the Date object.
   *
   * @see SimpleDateFormat
   * @see Date
   ***********************************************************************
   */
  public static long fmtDateTimeToLong( String sDate, String sTime, String sFmt)
    {
    SimpleDateFormat fmt = new SimpleDateFormat(sFmt);
    Date dt = fmt.parse( sDate + sTime, new ParsePosition(0) );

    // return a long representation of this date
    return( dt.getTime() );

    } // end fmtDateTimeToLong

  /**
   ***********************************************************************
   * This function converts a <code>long</code> DateTime to a string
   * representation, according to the format passed; the format passed
   * must be parseable by the {@link SimpleDateFormat} class.
   ***********************************************************************
   */
  /*
  public static String fmtLongDateTime(long aDate, String sDateTimeFmt)
    {
    Date d = new Date(aDate);
    SimpleDateFormat fmt = new SimpleDateFormat(sDateTimeFmt);
    return( fmt.format(d) );

    } // end fmtLongDateTime
  */

  /**
   ***********************************************************************
   * This method is a convenience function that detects whether the
   * root Document Element of the response received from the
   * Amadeus XML server is of type <code>MessagesOnly_Reply</code>; in all
   * likelihood this means that some kind of error occurred.
   *
   * @param domReply
   *   a reply from the Amadeus XML server
   ***********************************************************************
   */
  public static boolean isMessagesOnlyReply(Document domReply)
    {
    Element root = domReply.getDocumentElement();

    if ( root.getTagName().equals("MessagesOnly_Reply") )
      return true;
    else
      return false;

    } // end isMessagesOnlyReply

  /**
   ***********************************************************************
   * This method tries to recover from errors that occur in 'one shot'
   * transactions, such as GetAvailability, and getFlifo, where the transaction
   * is not dependent on prior or future transactions.
   ***********************************************************************
   */
  private void recover(Exception e) throws Exception
    {
    // log the error
    AppLog.LogError( e.toString(),null,getConnectionName());

    // if we lost the connection, reconnect
    if (!TestHostConnection()) 
      {
      // delete the logger for the dead connection/conversation
      if (AppLog.hasFileLogger(getConnectionName()))
        AppLog.removeLogger(AppLog.getFileLoggerList(getConnectionName())[0]);

      // establish a new conversation with Amadeus
      Connect();

      // create and add a new logger for the new conversation
      AppLog.addLogger(
          TranClientConnection.createConnectionLogger(getConnectionName()));
      }

    } // end recover

} // end AmadeusAPICrs class
