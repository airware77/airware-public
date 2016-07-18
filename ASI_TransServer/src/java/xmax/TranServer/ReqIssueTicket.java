package xmax.TranServer;

import xmax.crs.GetPNR.PNRItinAirSegment;
import xmax.crs.GetPNR.PNRItinSegment;
import xmax.crs.GetPNR.PNRNameElement;
import xmax.crs.PNR;
import xmax.crs.GnrcCrs;
import xmax.crs.BaseCrs;
import xmax.util.Log.*;

import java.io.Serializable;

/**
 ***********************************************************************
 * This command is used to issue a command to ticket a Passenger Name Record
 * (PNR) to a Computer Reservation System (CRS).
 *
 * @author   David Fairchild
 * @version  $Revision: 11$ - $Date: 01/27/2003 8:06:04 PM$
 ***********************************************************************
 */
public class ReqIssueTicket extends ReqTranServer implements Serializable
{
 private String Locator;
 private String FareType;
 public float CommissionAmount;
 public float CommissionPercent;
 public String ValidatingCarrier;
 public String EndorsementInfo;
 public String TourCode;
 public String FOP;
 public boolean printMiniItin;
 //public boolean printETicket;
 public String TicketType;
 public boolean printInvoice;
 public String PassengerDocPrinter;
 public String AgencyDocPrinter;
 private PNRItinAirSegment[] segments;
 private PNRNameElement[] names;
 public String [] Req_RawPsgrId;
 public PNR pnr;
 public String [] WT_Parms;
 public static final String FARE_CONTRACT = "C";
 public static final String FARE_LOWEST   = "L";
 public static final String FARE_EXEMPT   = "T";
 public static final String FARE_STORED   = "S";
 public static final String FARE_REGULAR  = "R";
 public static final String FARE_ALT_CONTRACT = "K";
 public static final String TICKET_TYPE_ETKT  = "E";
 public static final String TICKET_TYPE_PAPER = "P";
 public static final String TICKET_TYPE_NULL  = " ";
 public static final String ETKT_TRUE = "T";
 public static final String ETKT_FALSE = "F";

 /**
  ***********************************************************************
  * Constructors
  ***********************************************************************
  */
 public ReqIssueTicket(final String aCrsCode, final String aLocator)
   {
   this(aCrsCode,aLocator,FARE_REGULAR);
   }

 public ReqIssueTicket(final String aCrsCode, final String aLocator, final String aFareType)
   {
   super(aCrsCode);
   Locator   = aLocator;
   FareType  = aFareType;
   }

 /**
  ***********************************************************************
  * Locator
  ***********************************************************************
  */
 public void setLocator(final String aLocator)
   {
    Locator = aLocator;
   }

 public String getLocator()
   {
   return(Locator);
   }

 /**
  ***********************************************************************
  * Fare Type
  ***********************************************************************
  */
 public void setFareType(final String aFareType)
   {
   FareType = aFareType;
   }

 public String getFareType()
   {
   return(FareType);
   }

 public boolean isContract()
   {
   if ( FareType.equals(FARE_CONTRACT) )
     return(true);
   else
     return(false);
   }

 public boolean isLowest()
   {
   if ( FareType.equals(FARE_LOWEST) )
     return(true);
   else
     return(false);
   }

 public boolean isExempt()
   {
   if ( FareType.equals(FARE_EXEMPT) )
     return(true);
   else
     return(false);
   }

 public boolean isStored()
   {
   if ( FareType.equals(FARE_STORED) )
     return(true);
   else
     return(false);
   }

 public boolean isRegular()
   {
   if ( FareType.equals(FARE_REGULAR) )
     return(true);
   else
     return(false);
   }

 /**
  ***********************************************************************
  * Names
  ***********************************************************************
  */
 public PNRNameElement[] getNames()
   {
   return(names);
   }

 public void setNames(final PNRNameElement[] aNameList)
   {
   names = aNameList;
   }

 public void addName(final PNRNameElement aName)
   {
   if ( (aName instanceof PNRNameElement) == false )
     return;

   // get the current number of names
   final int iNumNames;
   if ( names instanceof PNRNameElement[] )
     iNumNames = names.length;
   else
     iNumNames = 0;

   // create a new array of names to hold the existing list
   final PNRNameElement[] newNameList = new PNRNameElement[ iNumNames + 1 ];

   // copy any existing names into the new array
   for ( int i = 0; i < iNumNames; i++ )
     newNameList[i] = names[i];

   // add the name to the end of the list
   newNameList[ newNameList.length - 1] = aName;

   names = newNameList;
   }

 public void addName(final PNR aPNR, final String aPsgrID, final String aPTC)
   {
   try
     {
     final PNRNameElement name = aPNR.getName(aPsgrID);
     if ( name instanceof PNRNameElement )
       {
       name.PTC = aPTC;
       addName(name);
       }
     }
   catch (Exception e)
     {}
   }

 public void addName(final String aPsgrID, final String aPTC)
   {
   final PNRNameElement name = new PNRNameElement();
   name.setPassengerID(aPsgrID);
   name.PTC = aPTC;
   addName(name);
   }

 /**
  ***********************************************************************
  * Set Raw Psgr id / inf
  ***********************************************************************
  */
 public void addRawPsgrId(final String aRawPsgrId)
   {
   if ( (aRawPsgrId instanceof String) == false )
     return;

   // get the current number of additional parameters
   final int iNumPsgrIds;
   if ( Req_RawPsgrId instanceof String[] )
     iNumPsgrIds = Req_RawPsgrId.length;
   else
     iNumPsgrIds = 0;

   // create a new array of name`s to hold the existing list
   final String[] newReq_RawPsgrId = new String[ iNumPsgrIds + 1 ];

   // copy any existing names into the new array
   for ( int i = 0; i < iNumPsgrIds; i++ )
     newReq_RawPsgrId[i] = Req_RawPsgrId[i];

   // add the parm to the end of the list
   newReq_RawPsgrId[ newReq_RawPsgrId.length - 1] = aRawPsgrId;

   Req_RawPsgrId = newReq_RawPsgrId;
   }

 /**
  ***********************************************************************
  * Segments
  ***********************************************************************
  */
 public PNRItinAirSegment[] getSegments()
   {
   return(segments);
   }

 public void setSegments(final PNRItinAirSegment[] aSegList)
   {
   segments = aSegList;
   }

 public void addSegment(final PNRItinAirSegment aSegment)
   {
   if ( (aSegment instanceof PNRItinAirSegment) == false )
     return;

   // get the current number of segments
   final int iNumSeg;
   if ( segments instanceof PNRItinAirSegment[] )
     iNumSeg = segments.length;
   else
     iNumSeg = 0;

   // create a new array of segments to hold the existing list
   final PNRItinAirSegment[] newSegList = new PNRItinAirSegment[ iNumSeg + 1 ];

   // copy any existing segments into the new array
   for ( int i = 0; i < iNumSeg; i++ )
     newSegList[i] = segments[i];

   // add the segment to the end of the list
   newSegList[ newSegList.length - 1] = aSegment;

   segments = newSegList;
   }

 public void addSegment(final PNR aPNR, final int aSegmentNum)
   {
   try
     {
     final PNRItinSegment segment = aPNR.getItinSegment(aSegmentNum);
     if ( segment instanceof PNRItinAirSegment )
       addSegment( (PNRItinAirSegment )segment );
     }
   catch (Exception e)
     {}
   }

 public void addSegment(final int aSegmentNum)
   {
   final PNRItinAirSegment segment = new PNRItinAirSegment();
   segment.SegmentNumber = aSegmentNum;
   addSegment( segment );
   }

	/**
  ***********************************************************************
  * Withhold Tax Parameters for Ticketing
  ***********************************************************************
  */
 public String[] getWT_Parms()
   {
   return(WT_Parms);
   }

 public void setWT_Parms(final String[] aWT_Parms)
   {
   WT_Parms = aWT_Parms;
   }

 public void add_WT_Parm(final String aWT_parm)
   {
   if ( (aWT_parm instanceof String) == false )
     return;

   // get the current number of additional parameters
   final int iNumWT_Parms;
   if ( WT_Parms instanceof String[] )
     iNumWT_Parms = WT_Parms.length;
   else
     iNumWT_Parms = 0;

   // create a new array of name`s to hold the existing list
   final String[] newWT_Parms = new String[ iNumWT_Parms + 1 ];

   // copy any existing names into the new array
   for ( int i = 0; i < iNumWT_Parms; i++ )
     newWT_Parms[i] = WT_Parms[i];

   // add the parm to the end of the list
   newWT_Parms[ newWT_Parms.length - 1] = aWT_parm;

   WT_Parms = newWT_Parms;
   }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Issuing tickets for locator " + Locator,null,aCrs.getConnectionName());
    aCrs.IssueTicket(this);
    }

  /**
   ***********************************************************************
   * This method reads the configuration values and determines whether
   * ticketing is enabled or not; ticketing is enabled by default, but when
   * testing it can be disabled so that a Ticketing Request always returns a
   * successful status code.
   ***********************************************************************
   */
  public static boolean isTicketingEnabled()
    {
    // retrieve enableTicketing value from configuration file
    boolean ticketDefault = true;
    boolean enableTicketing = ConfigTranServer.application.getBooleanProperty(
        "enableTicketing",ticketDefault);

    return(enableTicketing);

    /*
      final GnrcCrs crs = this.getCrs();
      if ( crs instanceof SabreCrs )
        {
        if ( GnrcFormat.NotNull( crs.getProperty(SabreCrs.AGNT_DOC_PTR_PROPERTY) ) )
          return(true);
        else if ( GnrcFormat.NotNull( crs.getProperty(SabreCrs.PSGR_DOC_PTR_PROPERTY) ) )
          return(true);
        }

      return(false);
    */
    }

  /**
   ***********************************************************************
   * Get a log file name to use for this request
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    final StringBuffer sLogName = new StringBuffer();
    if ( GnrcFormat.NotNull(aLogDirectory) )
      sLogName.append(aLogDirectory);

    sLogName.append("\\Locators\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for Issue ticket.  Crs Code is null");

    if ( GnrcFormat.IsNull(Locator) )
      throw new TranServerException("Cannot open log file for Issue ticket.  locator is null");

    sLogName.append(getCrsCode() + Locator + ".log");

    return( sLogName.toString() );
    }

}
