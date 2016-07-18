package xmax.crs;

import xmax.crs.GetPNR.*;
import xmax.crs.Flifo.FlightInfo;
import xmax.crs.cars.LocationInfo;
import xmax.crs.hotel.HotelInfo;
import xmax.crs.Amadeus.AmadeusGetTicketInfoConversation;
import xmax.crs.Availability.DestAvailability;
import xmax.TranServer.*;
import xmax.util.TypedProperties;

import java.util.Properties;
import java.util.List;

/**
 ***********************************************************************
 * This class provides a base implementation of the {@link GnrcCrs} interface,
 * so that empty 'stub' methods do not have to be created in the classes that
 * implement the <code>GnrcCrs</code> interface; this class also provides some
 * useful constants and appropriate default property values and method
 * behaviors for those methods that have not been implemented.
 * 
 * @author   David Fairchild
 * @version  $Revision: 17$ - $Date: 01/27/2003 8:07:43 PM$
 *
 * @see InnosysCrs
 * @see AmadeusCrs
 * @see AmadeusAPICrs
 * @see ApolloCrs
 * @see SabreCrs
 * @see WorldspanCrs
 ***********************************************************************
 */
public class BaseCrs implements GnrcCrs
{
  // constants used by other classes
  static public final String AMADEUS_CODE   = "1A";
  static public final String APOLLO_CODE    = "1V";
  static public final String SABRE_CODE     = "AA";
  static public final String WORLDSPAN_CODE = "1P";
  static public final String AMADEUS_NAME   = "Amadeus";
  static public final String APOLLO_NAME    = "Apollo";
  static public final String SABRE_NAME     = "Sabre";
  static public final String WORLDSPAN_NAME = "Worldspan";

  // constants to be overridden by extended classes
  static public final boolean isAmadeus     = false;
  static public final boolean isApollo      = false;
  static public final boolean isSabre       = false;
  static public final boolean isWorldspan   = false;
  public String getHostName()        { return(null); }
  public String getHostCode()        { return(null); }
  public String getConnectionName()  { return(null); }

  /* stores GDS specific key-value pair variables */
  protected TypedProperties properties;

  // session fields
  static private int iSessionIDGenerator = 1;
  private final int iSessionID;
  public int getSessionID() { return(iSessionID); }
  private long iLastSentTime;
  private long iLastRecvTime;

  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public BaseCrs()
    {
    iSessionID = iSessionIDGenerator++;
    properties = new TypedProperties();
    }

  /**
   ***********************************************************************
   * Constructor: adds the <code>Properties</code> passed to the properties
   * field
   ***********************************************************************
   */
  public BaseCrs(final Properties props)  
    {
    this();
    addProperties(props);
    } // end

  /**
   ***********************************************************************
   * convert the host code to the full name
   ***********************************************************************
   */
  public static String HostCodeToName(final String aHostCode)
    {
    final String sHostCode = aHostCode.toUpperCase();

    if ( sHostCode.equals( APOLLO_CODE ) || sHostCode.equals("UA") )
      return( APOLLO_NAME );
    else if ( sHostCode.equals( AMADEUS_CODE ) )
      return( AMADEUS_NAME );
    else if ( sHostCode.equals( SABRE_CODE ) )
      return( SABRE_NAME );
    else if ( sHostCode.equals( WORLDSPAN_CODE ) )
      return( WORLDSPAN_NAME );
    else
      return(null);
    }

  /**
   ***********************************************************************
   * This method is shorthand for properties.getProperty
   ***********************************************************************
   */
  public String getProperty(final String aName)
    {
    return( properties.getProperty(aName) );
    }
 
  /**
   ***********************************************************************
   * This method is shorthand for properties.getProperty
   ***********************************************************************
   */
  public String getProperty(final String aName, final String aDefaultValue)
    {
    return( properties.getProperty(aName,aDefaultValue) );
    }
 
  /**
   ***********************************************************************
   * This method is shorthand for properties.getProperties
   ***********************************************************************
   */
  public Properties getProperties()
    {
    return(properties);
    }

  /**
   ***********************************************************************
   * This method is shorthand for properties.setProperties
   ***********************************************************************
   */
  public void setProperty(final String aName, final String aValue)
    {
    properties.setProperty(aName,aValue);
    }
 
  /**
   ***********************************************************************
   * This method will replace the properties field with the
   * <code>Properties</code> object passed; care should be taken when using
   * this method, as it could delete the default properties for the object set
   * in constructor; {@link addProperties} is safer because it only overwrites
   * the properties passed in the <code>Properties</code> passed, and does not
   * disturb any other pre-existing ones.
   ***********************************************************************
   */
  public void setProperties(final Properties aProperties)
    {
    properties = new TypedProperties(aProperties);
    }

  /**
   ***********************************************************************
   * This method is a shorthand for properties.addProperties
   ***********************************************************************
   */
  public void addProperties(final Properties aProperties)  
    {
    properties.addProperties(aProperties);
    } // end addProperties

  /**
   ***********************************************************************
   * This method is shorthand for properties.clear
   ***********************************************************************
   */
  public void clearProperties()
    {
    properties.clear();
    }

  /**
   ***********************************************************************
   * TimeStamp functions
   ***********************************************************************
   */
  public void setLastSentTime()
    {
    iLastSentTime = System.currentTimeMillis();
    }

  public void setLastRecvTime()
    {
    iLastRecvTime = System.currentTimeMillis();
    }

  public long getLastSentTime() { return(iLastSentTime); }
  public long getLastRecvTime() { return(iLastRecvTime); }

  /**
   ***********************************************************************
   * User request
   ***********************************************************************
   */
  public void runUserRequest(final ReqTranServer aRequest) throws Exception
    {
    try
      {
      aRequest.setCrs( (GnrcCrs )this);
      aRequest.runRequest( (GnrcCrs)this );
      }
    finally
      {
      aRequest.setCrs(null);
      }
    }

  /**
   ***********************************************************************
   * This method is implemented for backward compatibility so that the
   * AmadeusAPICrs can make use of screen-scraping routines through its cryptic
   * interface
   ***********************************************************************
   */
    public String HostTransaction(final String sCommand) throws Exception
    {
    throw new TranServerException("Connect not implemented");
    }

  /**
   ***********************************************************************
   * Convienience routines for faring
   ***********************************************************************
   */

  public void FarePNRForLowest(final String aLocator) throws Exception
    {
    final ReqGetFare request = new ReqGetFare(aLocator,ReqGetFare.FARE_LOWEST);
    FarePNR(request);
    }
 
  public void FarePNRForContract(final String aLocator) throws Exception
    {
    final ReqGetFare request = new ReqGetFare(aLocator,ReqGetFare.FARE_CONTRACT);
    FarePNR(request);
    }
 
  public void FarePNRForCoach(final String aLocator) throws Exception
    {
    final ReqGetFare request = new ReqGetFare(aLocator,ReqGetFare.FARE_REGULAR);
    FarePNR(request);
    }

  /**
   ***********************************************************************
   * This function connects to the CRS
   ***********************************************************************
   */
  public void Connect() throws Exception
    {
    throw new TranServerException("Connect not implemented");
    }

  /**
   ***********************************************************************
   * This function changes the password on the host
   ***********************************************************************
   */
  public void ChangePassword(final String aUserName, final String aOldPassword, final String aNewPassword, final String aPseudoCity) throws Exception
    {
    throw new TranServerException("change password not implemented");
    }

  /**
   ***********************************************************************
   * ignore
   ***********************************************************************
   */
  public void Ignore() throws Exception
    {
    throw new TranServerException("ignore not implemented");
    }

  /**
   ***********************************************************************
   * Start a new session
   ***********************************************************************
   */
  public void SessionStart() throws Exception
    {
    throw new TranServerException("Session start not implemented");
    } // end SessionStart()

  /**
   ***********************************************************************
   * Close an existing session
   ***********************************************************************
   */
  public void SessionEnd() throws Exception
    {
    throw new TranServerException("Session end not implemented");
    } // end SessionStart()

  /**
   ***********************************************************************
   * FreeForm command
   ***********************************************************************
   */
 
  public void FreeForm(final String aRequest, final StringBuffer aResponse) throws Exception
    {
    throw new TranServerException("Free form commmand not implemented");
    }
 
  /**
   ***********************************************************************
   * Adds a Receive By line to a PNR
   ***********************************************************************
   */
  public void AddReceiveBy(final String aName) throws Exception
    {
    throw new TranServerException("Unable to add receive by line - not implemented");
    }

  /**
   ***********************************************************************
   * End Transaction
   ***********************************************************************
   */
  public String EndTransaction() throws Exception
    {
    throw new TranServerException("Unable to end transaction - not implemented");
    }

  public String EndTransaction(final String aReceiveBy) throws Exception
    {
    throw new TranServerException("Unable to end transaction - not implemented");
    }

  /**
   ***********************************************************************
   * Disconnect from the CRS
   ***********************************************************************
   */
  public void Disconnect() throws Exception
    {
    throw new TranServerException("Disconnect not implemented");
    }

 /**
  ***********************************************************************
  *
  ***********************************************************************
  */
 public boolean TestHostConnection()
   {
   return(false);
   }

 /**
  ***********************************************************************
  * Retrieves all PNR segments from the AAA
  ***********************************************************************
  */
 public void GetPNRFromAAA(final PNR aPNR) throws Exception
   {
   throw new TranServerException("Get PNR from AAA not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all PNR segments from the given queue
  ***********************************************************************
  */
 public void GetPNRFromQueue(final String aQueueName, final PNR aPNR, final boolean aRemove, final boolean aLeaveOpen) throws Exception
   {
   throw new TranServerException("Get PNR from queue not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all PNR segments from the CRS
  ***********************************************************************
  */
 public void GetPNRAllSegments(final String aLocator, final PNR aPNR, final boolean aLeaveOpen) throws Exception
   {
   throw new TranServerException("Get PNR not implemented");
   }

 /**
  ***********************************************************************
  * Loads the given PNR into the AAA
  ***********************************************************************
  */
 public void LoadPNRIntoAAA(final String aLocator) throws Exception
   {
   throw new TranServerException("Load PNR into AAA not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all flight service information from the CRS
  ***********************************************************************
  */
 public void GetFlightInfo(final String aCarrier, final int aFlightNum, final String aDepDate, final FlightInfo aFlightData) throws Exception
   {
   GetFlightInfo(aCarrier,aFlightNum,aDepDate,null,null,aFlightData);
//   throw new TranServerException("Flight Info not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all flight service information from the CRS
  ***********************************************************************
  */
 public void GetFlightInfo(final String aCarrier, final int aFlightNum, final String aDepDate, final String FromCity, final String ToCity, final FlightInfo aFlightData) throws Exception
   {
   throw new TranServerException("Flight Info not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all flight service information from the CRS
  ***********************************************************************
  */
 public void GetFlightInfo(final String aLocator, final PNRItinAirSegment aAirSegment, final FlightInfo aFlightData) throws Exception
   {
   throw new TranServerException("Flight Info not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all car rental location information from the crs
  ***********************************************************************
  */
 public void GetLocationInfo(final String aCompanyCode, final String aCityCode, final String aLocationCode, final LocationInfo aLocationData) throws Exception
   {
   throw new TranServerException("Car rental info not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all car rental location information from the crs
  ***********************************************************************
  */
 public void GetLocationInfo(final String aLocator, final PNRItinCarSegment aCarSegment, final LocationInfo aLocationData) throws Exception
   {
   throw new TranServerException("Car rental info not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all hotel information from the crs
  ***********************************************************************
  */
 public void GetHotelInfo(final String aCompanyCode, final String aLocationCode, final HotelInfo aHotelData) throws Exception
   {
   throw new TranServerException("Hotel info not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all hotel information from the crs
  ***********************************************************************
  */
 public void GetHotelInfo(final String aLocator, final PNRItinHotelSegment aHotelSegment, final HotelInfo aHotelData) throws Exception
   {
   throw new TranServerException("Hotel info not implemented");
   }

 /** 
  ***********************************************************************
  * Retrieves all hotel information from the crs
  ***********************************************************************
  */
 public void GetHotelInfo(final String aCompanyCode, final String aLocationCode, final StringBuffer aHotelData) throws Exception
   {
   throw new TranServerException("Hotel info not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all airline information from the crs
  ***********************************************************************
  */
 public void GetAirlineInfoFromName(final String aAirlineName, final AirlineInfo aAirline) throws Exception
   {
   throw new TranServerException("Unable to get airline information - not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all airline information from the crs
  ***********************************************************************
  */
 public void GetAirlineInfoFromCode(final String aAirlineCode, final AirlineInfo aAirline) throws Exception
   {
   throw new TranServerException("Unable to get airline information - not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all seat information from the CRS
  ***********************************************************************
  */
 public void GetSeatAssignments(final String aLocator, final PNR aPNR) throws Exception
   {
   throw new TranServerException("Unable to get seat assignments - not implemented");
   }

 /**
  ***********************************************************************
  * for each of the segments passed, this method attempts to identify the
  * managed block locator from which they were sold
  ***********************************************************************
  */
 public void getManagedBlockLocators(final String aLocator, final PNR aPNR) throws Exception
   {
   throw new TranServerException("Unable to get managed block locators - not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves all seat information from the CRS
  ***********************************************************************
  */
 public void GetAvailability(final DestAvailability aAvail) throws Exception
   {
   throw new TranServerException("Unable to get availability - not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves legal connect times from the crs for specific segments
  ***********************************************************************
  */
 public void GetConnectTime(final ConnectTimes aConnectTimes) throws Exception
   {
   throw new TranServerException("Unable to get connection times - not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves legal connect times from the crs for an airport
  ***********************************************************************
  */
 public void GetConnectTimesAirport(final ConnectTimesAirport airport) throws Exception
   {
   throw new TranServerException("Unable to get airport connection times - not implemented");
   }

 /**
  ***********************************************************************
  * Retrieves legal connect times from the crs
  ***********************************************************************
  */
 public void GetCityInformation(final CityInfo aCityInfo) throws Exception
   {
   throw new TranServerException("Unable to get city information - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a corporate header to a PNR
  ***********************************************************************
  */
 public void AddCorpHeader(final String aGroupName, final int aNumSeats) throws Exception
   {
   throw new TranServerException("Unable to add corporate header - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a name to a PNR
  ***********************************************************************
  */
 public void AddName(final PNRNameElement aName) throws Exception
   {
   throw new TranServerException("Unable to add name - not implemented");
   }

 /**
  ***********************************************************************
  * Changes a name on a PNR
  ***********************************************************************
  */
 public void ChangeName(final String aLocator, final String aPsgrID, final PNRNameElement aNewName) throws Exception
   {
   throw new TranServerException("Unable to change name - not implemented");
   }

 /**
  ***********************************************************************
  * Changes multiple elements on a PNR
  ***********************************************************************
  */
 public void changePnrElements(final String aLocator, final PNRNameElement[] aOldNames, final PNRNameElement[] aNewNames, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to change names - not implemented");
   }


 public void changePnrElements(final String aLocator, final PNRRemark[] aOldRemarks, final PNRRemark[] aNewRemarks, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to change remarks - not implemented");
   }

  /**
   ***********************************************************************
   * Given a locator, a list of passengers, a list of segments to be deleted,
   * and a list of new segments to replace those deleted, this method deletes
   * the old segments from the PNR and replaces them with the new
   * segments;<br/> 
   * if a passenger list is provided, and this list is a subset of the
   * passenger list on the existing PNR, the PNR is split and the segment
   * modification is only performed on the split PNR.
   ***********************************************************************
   */
  public void changePnrItinerary(final String aLocator, final PNRNameElement[] aPsgrList, 
      final PNRItinSegment[] aOldSegments, final PNRItinSegment[] aNewSegments, 
      final String aReceiveBy, final StringBuffer aNewLocator) throws Exception
   {
   throw new TranServerException("Unable to modify Itinerary - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a name to a PNR
  ***********************************************************************
  */
 public void AddAirSeg(final PNRItinAirSegment aAirSeg) throws Exception
   {
   throw new TranServerException("Unable to add air segment - not implemented");
   }


 public void addPnrElements(final String aLocator, final PNRNameElement[] aNames, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to add names - not implemented");
   }


 public void addPnrElements(final String aLocator, final PNRItinSegment[] aSegs, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to add itinerary segments - not implemented");
   }


 public void addPnrElements(final String aLocator, final PNRRemark[] aSegs, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to add remarks - not implemented");
   }

 /**
  ***********************************************************************
  * Accept any schedule changes made on the PNR
  ***********************************************************************
  */
 public void AcceptSchedChange(final String aLocator, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to accept schedule changes - not implemented");
   }

 /**
  ***********************************************************************
  * Removes a remark from a PNR
  ***********************************************************************
  */
 public void CancelRemark(final String aLocator, final PNRRemark aRemark) throws Exception
   {
   throw new TranServerException("Unable to cancel remark - not implemented");
   }

 /**
  ***********************************************************************
  * Removes a phone line from a PNR
  ***********************************************************************
  */
 public void CancelPhone(final String aLocator, final String aPhone) throws Exception
   {
   throw new TranServerException("Unable to cancel phone - not implemented");
   }

 /**
  ***********************************************************************
  * Removes a ticket line from a PNR
  ***********************************************************************
  */
 public void CancelTicket(final String aLocator, final String aTicket) throws Exception
   {
   throw new TranServerException("Unable to cancel ticket - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a name to a PNR
  ***********************************************************************
  */
 public void AddPhone(final String aPhone) throws Exception
   {
   throw new TranServerException("Unable to add phone - not implemented");
   }

 /**
  ***********************************************************************
  * Adds an endorsement to a PNR
  ***********************************************************************
  */
 public void AddEndorsement(final String aEndorsement) throws Exception
   {
   throw new TranServerException("Unable to add endorsement - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a tour code to a PNR
  ***********************************************************************
  */
 public void AddTourCode(final String aTourCode) throws Exception
   {
   throw new TranServerException("Unable to add tour code - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a commission to a PNR
  ***********************************************************************
  */
 public void AddCommission(final float aCommission, final boolean aPercentage) throws Exception
   {
   throw new TranServerException("Unable to add commission - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a remark to a PNR
  ***********************************************************************
  */
 public void AddRemark(final PNRRemark aRemark) throws Exception
   {
   throw new TranServerException("Unable to add remark - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a form of payment to a PNR
  ***********************************************************************
  */
 public void AddFOP(final String aFOP) throws Exception
   {
   throw new TranServerException("Unable to add form of payment - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a name to a PNR
  ***********************************************************************
  */
 public void AddTicket(final String aTicket) throws Exception
   {
   throw new TranServerException("Unable to add ticket - not implemented");
   }

 /**
  ***********************************************************************
  * Adds a PNR to a Queue
  ***********************************************************************
  */
 public void QueuePNR(final String aLocator, final String aQueueName) throws Exception
   {
   throw new TranServerException("Unable to queue PNR - not implemented");
   }

 /**
  ***********************************************************************
  * Fare a PNR
  ***********************************************************************
  */
 public void FarePNR(final ReqGetFare aRequest) throws Exception
   {
   throw new TranServerException("Unable to fare PNR - not implemented");
   }

 public void getStoredFare(final String aLocator, final PNR aPnr) throws Exception
   {
   throw new TranServerException("Unable to get stored fare for PNR - not implemented");
   }

 /**
  ***********************************************************************
  * Issue a ticket
  ***********************************************************************
  */
 public void IssueTicket(final ReqIssueTicket aRequest) throws Exception
   {
   throw new TranServerException("Unable to issue ticket - not implemented");
   }

 /**
  ***********************************************************************
  * Get ticket info for this PNR
  ***********************************************************************
  */
 public void getTicketInfo(final ReqGetTicketInfo aRequest) throws Exception
   {
	   throw new TranServerException("Unable to get ticket information - not implemented");
   }
 
  /**
   ***********************************************************************
   * This function assigns the user to a specific printer
   ***********************************************************************
   */
 public void AssignPrinter(final String aPrinterName) throws Exception
   {
   throw new TranServerException("Unable to assign ticket printer - not implemented");
   }

 /**
  ***********************************************************************
  * Get a list of branches for the given pseudocity
  ***********************************************************************
  */
 public void GetBranchList(final ReqListBranches aRequest) throws Exception
   {
   throw new TranServerException("Unable to get list of branches - not implemented");
   }

 /**
  ***********************************************************************
  * Get a list of group profiles for the given pseudocity
  ***********************************************************************
  */
 public void GetGroupProfileList(final ReqListGroupProfiles aRequest) throws Exception
   {
   throw new TranServerException("Unable to get list of group profiles - not implemented");
   }

 /**
  ***********************************************************************
  * Get a list of personal profiles for the given pseudocity
  ***********************************************************************
  */
 public void GetPersonalProfileList(final ReqListPersonalProfiles aRequest) throws Exception
   {
   throw new TranServerException("Unable to get list of personal profiles - not implemented");
   }

 /**
  ***********************************************************************
  * Get a profile
  ***********************************************************************
  */
 public void GetProfile(final ReqGetProfile aRequest) throws Exception
   {
   throw new TranServerException("Unable to get profile - not implemented");
   }

 /**
  ***********************************************************************
  * Build a profile
  ***********************************************************************
  */
 public void BuildProfile(final ReqBuildProfile aRequest) throws Exception
   {
   throw new TranServerException("Unable to build profile - not yet implemented");
   }

 /**
  ***********************************************************************
  * Split a PNR
  ***********************************************************************
  */
 public void splitPNR(final String aLocator, final int aNumUnassigned, final PNRNameElement[] aNames, final String aReceiveBy, final StringBuffer aNewLocator) throws Exception
   {
   throw new TranServerException("Unable to split PNR - not yet implemented");
   }

 /**
  ***********************************************************************
  * Cancel a list of airsegments
  ***********************************************************************
  */
 public void deletePnrElements(final String aLocator, final PNRNameElement[] aNames, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to cancel names - not yet implemented");
   }


 public void deletePnrElements(final String aLocator, final PNRItinSegment[] aSegments, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to cancel segments - not yet implemented");
   }


 public void deletePnrElements(final String aLocator, final PNRRemark[] aRemarks, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to cancel remarks - not yet implemented");
   }

 /**
  ***********************************************************************
  * Cancel an entire itinerary
  ***********************************************************************
  */
 public void cancelItinerary(final String aLocator, final String aReceiveBy) throws Exception
   {
   throw new TranServerException("Unable to cancel itinerary - not yet implemented");
   }

 /**
  ***********************************************************************
  * Create a block
  ***********************************************************************
  */
 public void blockBuild(final Block aBlock) throws Exception
   {
   throw new TranServerException("Unable to build block - not yet implemented");
   }

 /**
  ***********************************************************************
  * Change a block
  ***********************************************************************
  */
 public void blockModify(final String aLocator, final String aCarrierCode, final int aNumAllocated) throws Exception
   {
   throw new TranServerException("Unable to modify block - not yet implemented");
   }

 /**
  ***********************************************************************
  * Delete a block
  ***********************************************************************
  */
 public void blockDelete(final String aLocator, final String aCarrierCode) throws Exception
   {
   throw new TranServerException("Unable to delete block - not yet implemented");
   }

 /**
  ***********************************************************************
  * Get block details
  ***********************************************************************
  */
 public void blockGet(final String aLocator, final String aCarrierCode, final Block aBlock) throws Exception
   {
   throw new TranServerException("Unable to get block details - not yet implemented");
   }

 /** read next message from CRS block queue */
 public void blockReadMessage(final String queueName, final String queueCategory,
     final boolean leaveOnQueue, List blockMessageList) throws Exception
   {
   throw new TranServerException("Unable to read block message from queue - not yet implemented");
   }

  /**
   ***********************************************************************
   * This procedure is called when the object is garbage collected
   ***********************************************************************
   */
  protected void finalize()
    {
    try
      {
      Disconnect();
      }
    catch (Exception e)
      {
      e.printStackTrace();
      }
    }

}
