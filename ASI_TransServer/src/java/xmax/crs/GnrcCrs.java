package xmax.crs;

import xmax.crs.Flifo.FlightInfo;
import xmax.crs.cars.LocationInfo;
import xmax.crs.hotel.HotelInfo;
import xmax.crs.Availability.DestAvailability;
import xmax.crs.GetPNR.*;
import xmax.crs.BlockMessage;
import xmax.TranServer.*;

import java.util.Properties;
import java.util.List;

/**
 ***********************************************************************
 * The Generic Computer Reservation System (GnrcCrs) interface provides a high
 * level abstraction for the business services that a CRS (also called Global
 * Distribution System or GDS) is expected to provide to clients in the air
 * travel industry, independently from the specific implementation of each
 * service, function or command by an individual CRS. 
 * <p>
 * A stub implementation of this implementation is provided in the form of the
 * class {@link BaseCrs} which should be extended to derive more specialized
 * classes, such as the {@link InnosysCrs} class that represents CRS systems as
 * experienced through an Innosys Terminal Emulation Gateway, or the {@link
 * AmadeusAPICrs} class that represents a connection to Amadeus through its XML
 * API. The Airware Transaction Server currently models the 4 largest CRS
 * systems in the air travel industry: Amadeus, Apollo, Sabre and
 * Worldspan.</p>
 * <p>
 * The GnrcCrs interface is not concerned with the manner by which a specific
 * GnrcCrs implementation connects to an actual CRS host to retrieve the
 * information.  This information is encapsulated within the implementation of
 * the generic connection methods {@link Connect}, {@link Disconnect}, 
 * {@link TestHostConnection} and {@link ChangePassword} of each of the classes
 * that implement the GnrcCrs interface.</p>
 * 
 * @author   David Fairchild
 * @version  $Revision: 31$ - $Date: 01/27/2003 8:04:45 PM$
 *
 * @see BaseCrs
 * @see InnosysCrs
 * @see AmadeusCrs
 * @see AmadeusAPICrs
 * @see ApolloCrs
 * @see SabreCrs
 * @see WorldspanCrs
 ***********************************************************************
 */
public interface GnrcCrs
{
  /* 
   * constant for paper ticketing method, corresponds to the 
   * 'ticketingMethod' of the transerver configuration file
   */
  static public final String TICKET_PAPER      = "PAPER";

  /* 
   * constant for electronic ticketing method, corresponds to the 
   * 'ticketingMethod' of the transerver configuration file
   */
  static public final String TICKET_ELECTRONIC = "ETICKET";

  // property methods
  public abstract void    setProperty(final String aName, final String aValue);
  public abstract void    setProperties(final Properties aProperties);
  public abstract void    addProperties(final Properties aProperties);
  public abstract void    clearProperties();
  public abstract Properties getProperties();
  public abstract String  getProperty(final String aName);

  // connection methods
  public abstract int     getSessionID();
  public abstract long    getLastSentTime();
  public abstract long    getLastRecvTime();
  public abstract String  getHostName();
  public abstract String  getHostCode();
  public abstract String  getConnectionName();
  public abstract void    Connect() throws Exception;
  public abstract void    Disconnect() throws Exception;                                                      // returns true if logged off correctly
  public abstract boolean TestHostConnection();
  public abstract void    ChangePassword(final String aUserName, final String aOldPassword, final String aNewPassword, final String aPseudoCity) throws Exception;

  // session methods
  public abstract void    SessionStart() throws Exception;
  public abstract void    SessionEnd() throws Exception;
  public abstract String  EndTransaction(final String aReceiveBy) throws Exception;   // saves the contents of the AAA, returns the locator
  public abstract String  EndTransaction() throws Exception;   // saves the contents of the AAA, returns the locator
  public abstract void    Ignore() throws Exception;                                                       // ignores any open transaction and clears the AAA
  public abstract void    AssignPrinter(final String aPrinterName) throws Exception;
  public abstract void    FreeForm(final String aRequest, final StringBuffer aResponse) throws Exception;
  public abstract void    runUserRequest(final ReqTranServer aRequest) throws Exception;
  public abstract String  HostTransaction(String sCommand) throws Exception;

  // PNR retrieval methods
  public abstract void    GetPNRAllSegments(final String aLocator, final PNR aPNR, final boolean aLeaveOpen) throws Exception;  // returns true if PNR is returned
  public abstract void    GetPNRFromQueue(final String aQueueName, final PNR aPNR, final boolean aRemove, final boolean aLeaveOpen) throws Exception; // retrieve a PNR from the indicated queue
  public abstract void    GetPNRFromAAA(final PNR aPNR) throws Exception; // retrieve a PNR from the AAA
  public abstract void    LoadPNRIntoAAA(final String aLocator) throws Exception;
  public abstract void    GetSeatAssignments(final String aLocator, final PNR aPNR) throws Exception;
  public abstract void    getManagedBlockLocators(final String aLocator, final PNR aPNR) throws Exception;

  // PNR creation and modification
  public abstract void    AddCorpHeader(final String aGroupName, final int aNumSeats) throws Exception;
  public abstract void    AddName(final PNRNameElement aName) throws Exception;
  public abstract void    ChangeName(final String aLocator, final String aPsgrID, final PNRNameElement aNewName) throws Exception;
  public abstract void    AddAirSeg(final PNRItinAirSegment aAirSeg) throws Exception;
  public abstract void    AcceptSchedChange(final String aLocator, final String aReceiveBy) throws Exception;
  public abstract void    AddRemark(final PNRRemark aRemark) throws Exception;
  public abstract void    AddPhone(final String aPhone) throws Exception;
  public abstract void    AddEndorsement(final String aEndorsement) throws Exception;
  public abstract void    AddTourCode(final String aTourCode) throws Exception;
  public abstract void    AddCommission(final float aCommission, final boolean aPercentage) throws Exception;
  public abstract void    AddFOP(final String aFOP) throws Exception;
  public abstract void    AddTicket(final String aTicket) throws Exception;
  public abstract void    AddReceiveBy(final String aName) throws Exception;
  public abstract void    CancelRemark(final String aLocator, final PNRRemark aRemark) throws Exception;
  public abstract void    CancelPhone(final String aLocator, final String aPhone) throws Exception;
  public abstract void    CancelTicket(final String aLocator, final String aTicket) throws Exception;
  public abstract void    splitPNR(final String aLocator, final int aNumUnassigned, final PNRNameElement[] aNames, final String aReceiveBy, final StringBuffer aNewLocator) throws Exception;
  public abstract void    cancelItinerary(final String aLocator, final String aReceiveBy) throws Exception;
  public abstract void    addPnrElements(final String aLocator, final PNRNameElement[] aNames, final String aReceiveBy) throws Exception;
  public abstract void    addPnrElements(final String aLocator, final PNRItinSegment[] aSegs, final String aReceiveBy) throws Exception;
  public abstract void    addPnrElements(final String aLocator, final PNRRemark[] aRemarks, final String aReceiveBy) throws Exception;
  public abstract void    changePnrElements(final String aLocator, final PNRNameElement[] aOldNames, final PNRNameElement[] aNewNames, final String aReceiveBy) throws Exception;
  public abstract void    changePnrElements(final String aLocator, final PNRRemark[] aOldRemarks, final PNRRemark[] aNewRemarks, final String aReceiveBy) throws Exception;
  public abstract void    changePnrItinerary(final String aLocator, final PNRNameElement[] aPsgrList, final PNRItinSegment[] aOldSegments, final PNRItinSegment[] aNewSegments, final String aReceiveBy, final StringBuffer aNewLocator) throws Exception;

  public abstract void    deletePnrElements(final String aLocator, final PNRNameElement[] aNames, final String aReceiveBy) throws Exception;
  public abstract void    deletePnrElements(final String aLocator, final PNRItinSegment[] aSegments, final String aReceiveBy) throws Exception;
  public abstract void    deletePnrElements(final String aLocator, final PNRRemark[] aRemarks, final String aReceiveBy) throws Exception;

  // PNR queueing, faring, and ticketing
  public abstract void    QueuePNR(final String aLocator, final String aQueueName) throws Exception;
  public abstract void    FarePNR(final ReqGetFare aRequest) throws Exception;
  public abstract void    getStoredFare(final String aLocator, final PNR aPnr) throws Exception;
  public abstract void    IssueTicket(final ReqIssueTicket aRequest) throws Exception;
  public abstract void    getTicketInfo(final ReqGetTicketInfo aRequest) throws Exception;

  // hotel, car, flight, and airline information
  public abstract void    GetFlightInfo(final String aCarrier, final int aFlightNum, final String aDepDate, final FlightInfo aFlightData) throws Exception;  // returns true if flight data is found
  public abstract void    GetFlightInfo(final String aCarrier, final int aFlightNum, final String aDepDate, final String sFromCity, final String sToCity, final FlightInfo aFlightData) throws Exception;  // returns true if flight data is found
  public abstract void    GetFlightInfo(final String aLocator, final PNRItinAirSegment aAirSegment, final FlightInfo aFlightData) throws Exception;
  public abstract void    GetLocationInfo(final String aCompanyCode, final String aCityCode, final String aLocationCode, final LocationInfo aLocationData) throws Exception;
  public abstract void    GetLocationInfo(final String aLocator, final PNRItinCarSegment aCarSegment, final LocationInfo aLocationData) throws Exception;
  public abstract void    GetHotelInfo(final String aCompanyCode, final String aLocationCode, final HotelInfo aHotelData) throws Exception;
  public abstract void    GetHotelInfo(final String aLocator, final PNRItinHotelSegment aHotelSegment, final HotelInfo aHotel) throws Exception;
  public abstract void    GetHotelInfo(final String aCompanyCode, final String aLocationCode, final StringBuffer aHotelData) throws Exception;
  public abstract void    GetAirlineInfoFromName(final String aAirlineName, final AirlineInfo aAirline) throws Exception;
  public abstract void    GetAirlineInfoFromCode(final String aAirlineCode, final AirlineInfo aAirline) throws Exception;

  // availability methods
  public abstract void    GetAvailability(final DestAvailability aAvail) throws Exception;
  public abstract void    GetConnectTime(final ConnectTimes aConnectTimes) throws Exception;
  public abstract void    GetConnectTimesAirport(final ConnectTimesAirport airport) throws Exception;
  public abstract void    GetCityInformation(final CityInfo aCityInfo) throws Exception;

  // profile methods
  public abstract void    GetBranchList(final ReqListBranches aRequest) throws Exception;
  public abstract void    GetGroupProfileList(final ReqListGroupProfiles aRequest) throws Exception;
  public abstract void    GetPersonalProfileList(final ReqListPersonalProfiles aRequest) throws Exception;
  public abstract void    GetProfile(final ReqGetProfile aRequest) throws Exception;
  public abstract void    BuildProfile(final ReqBuildProfile aRequest) throws Exception;

  // block methods
  public abstract void    blockBuild(final Block aBlock) throws Exception;
  public abstract void    blockModify(final String aLocator, final String aCarrierCode, final int aNumAllocated) throws Exception;
  public abstract void    blockDelete(final String aLocator, final String aCarrierCode) throws Exception;
  public abstract void    blockGet(final String aLocator, final String aCarrierCode, final Block aBlock) throws Exception;
  public abstract void    blockReadMessage(final String queueName, final String queueCategory, final boolean leaveOnQueue, List blockMessageList) throws Exception;
   
}

