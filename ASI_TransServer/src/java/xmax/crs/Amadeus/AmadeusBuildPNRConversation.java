package xmax.crs.Amadeus;

import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;
import xmax.crs.*;
import xmax.crs.GetPNR.*;
import xmax.TranServer.GnrcFormat;
import xmax.TranServer.TranServerException;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;


public class AmadeusBuildPNRConversation
{


 /**
  ***********************************************************************
  * This method builds and executes the cryptic code command to change the name
  * of a passenger in a Passenger Name Record.
  * The cryptic code command has a format such as:
  * <code>2/1 SMITH/JOHN MR (ADT)(ID234234)</code>, where:
  * <ul>
  *   <li><code>2/</code>: change passenger 2
  *   <li><code>1</code>: number of seats
  *   <li><code>SMITH/JOHN MR</code>: new passenger's last name, first name and title
  *   <li><code>(ADT)</code>: Passenger Type Code (PTC)
  *   <li><code>(ID234234)</code>: Airware assigned passenger ID
  * </ul>
  *
  * @throws GdsResponseException  if the Global Distribution System
  *   was unable to change the name on the PNR
  ***********************************************************************
  */


 public void changePnrElements(final GnrcCrs aCRS, final String aLocator,
                               final PNRNameElement[] aOldNames, final PNRNameElement[] aNewNames, final String aReceiveBy) throws Exception
  {
  // check the input parms
  if ( (aLocator instanceof String) == false )
    throw new TranServerException("Must specify a locator to change names");

  if ( (aOldNames instanceof PNRNameElement[]) == false )
    throw new TranServerException("Invalid old name list");

  if ( (aNewNames instanceof PNRNameElement[]) == false )
    throw new TranServerException("Invalid new name list");

  if ( aOldNames.length != aNewNames.length )
    throw new TranServerException("Old and new name lists must have the same number of entries");

  // build the command string
  PNRNameElement currentName;
  String sCommand;
  String sResponse;
  for ( int i = 0; i < aOldNames.length; i++ )
    {
     // every time that we change a passenger's name, that passenger drops to
     // the bottom of the passenger list on the PNR, which may cause the
     // passenger numbers to change; hence, it is necessary to retrieve the PNR
     // prior to each changeName operation to ensure that the correct passenger
     // element is changed.
     PNR pnr = new PNR();
     if (i == 0) // retrieve the pnr by locator for the first change
       aCRS.GetPNRAllSegments(aLocator,pnr,true);
     else // redisplay the pnr on subsequent changes
       aCRS.GetPNRFromAAA(pnr);

    // determine the name number of the old name
    currentName = pnr.getName(aOldNames[i].getPassengerID());
    if ( (currentName instanceof PNRNameElement) == false )
      throw new TranServerException("Unable to find passenger ID " +
          aOldNames[i].getPassengerID() + " on PNR " + aLocator);

    sCommand = pnr.getPsgrNum(currentName) + "/" + 
               getChangeNameString(aNewNames[i],currentName);

    sResponse = aCRS.HostTransaction(sCommand).trim();
    if ( sResponse.indexOf("RP/") < 0 )
      throw new GdsResponseException("Unable to change names on PNR",sCommand,sResponse);
    }

  // this is now done in ReqBuildPnr.runRequest
  // aCRS.EndTransaction(aReceiveBy);
  } // end changePnrElements (names)



 /**
  ***********************************************************************
  * This method builds the cryptic code command to add the name of a
  * passenger in a Passenger Name Record.
  * The cryptic code command has a format such as:
  * <code>1SMITH/JOHN MR (ADT)(ID234234)</code>, where:
  * <ul>
  *   <li><code>1</code>: number of seats
  *   <li><code>SMITH/JOHN MR</code>: new passenger's last name, first name and title
  *   <li><code>(ADT)</code>: Passenger Type Code (PTC)
  *   <li><code>(ID234234)</code>: Airware assigned passenger ID
  * </ul>
  *
  * @throws GdsResponseException  if the Global Distribution System
  *   was unable to change the name on the PNR
  ***********************************************************************
  */
 protected static String getNameString(final PNRNameElement aName)
   {
   // create the add name command
   final StringBuffer sCommand = new StringBuffer();

  final int iNumSeats;
  if ( aName.NumSeats > 0 )
    iNumSeats = aName.NumSeats;
  else
    iNumSeats = 1;

   sCommand.append(iNumSeats + aName.LastName + "/" + aName.FirstName);

   if ( GnrcFormat.NotNull(aName.Title) )
     sCommand.append(" " + aName.Title);

   // In the mac version of the transerver, the PTC comes after the ID
   // In the API version, the PTC precedes the ID
   //sCommand.append("(" + aName.PTC + ")");

   if ( GnrcFormat.NotNull(aName.getPassengerID()) )
     {
     if ( aName.getPassengerID().startsWith("ID") )
       sCommand.append("(" + aName.getPassengerID() + ")");
     else
       sCommand.append("(ID" + aName.getPassengerID() + ")");
     }

   sCommand.append("(" + aName.PTC + ")");

   if ( GnrcFormat.NotNull(aName.InfantName) )
     sCommand.append("(INF/" + aName.InfantName + ")");

   return( sCommand.toString() );
   }

 /**
  ***********************************************************************
  * This method returns getNameString(newName); for some reason, it was doing 
  * the following before, which is now deprecated:<br/>
  * This method builds the cryptic code command to change the name of a
  * passenger in a Passenger Name Record; if either the first or last name
  * between the new and old name differ, this method merely returns 
  * the same string as {@link getNameString} above; 
  * if the first and last name of the new name are the same, only the modifiers
  * are returned by this function, such as:
  * <code>MR (ADT)(ID234234)</code>, where:
  * <ul>
  *   <li><code>MR</code>: title
  *   <li><code>(ADT)</code>: Passenger Type Code (PTC)
  *   <li><code>(ID234234)</code>: Airware assigned passenger ID
  * </ul>
  *
  * @throws GdsResponseException  if the Global Distribution System
  *   was unable to change the name on the PNR
  ***********************************************************************
  */
 protected static String getChangeNameString(
     final PNRNameElement newName, final PNRNameElement oldName)
   {
   /*
   if (!(newName.FirstName.equals(oldName.FirstName) &&
         newName.LastName.equals(oldName.LastName)) )
     return getNameString(newName);

   // if the first and last name match, create the appropriate string
   final StringBuffer sCommand = new StringBuffer();

   if ( GnrcFormat.NotNull(newName.Title) )
     sCommand.append(" " + newName.Title);

   if ( GnrcFormat.NotNull(newName.getPassengerID()) )
     {
     if ( newName.getPassengerID().startsWith("ID") )
       sCommand.append("(" + newName.getPassengerID() + ")");
     else
       sCommand.append("(ID" + newName.getPassengerID() + ")");
     }

   sCommand.append("(" + newName.PTC + ")");

   if ( GnrcFormat.NotNull(newName.InfantName) )
     sCommand.append("(INF/" + newName.InfantName + ")");

   return( sCommand.toString() );
   */
   return getNameString(newName);
   }



 /** 
  ***********************************************************************
  * Adds an arunk segment to the itinerary
  ***********************************************************************
  */
 protected static void addArunk(final GnrcCrs aCRS) throws Exception
  {
  final String sCommand = "SIARNK";
  final String sResponse = aCRS.HostTransaction(sCommand).trim();

  if ( RegExpMatch.matches(sResponse,"[1-9][0-9]? +ARNK") == false )
    throw new GdsResponseException("Unable to add arunk segment",sCommand,sResponse);
  }


  /**
  ***********************************************************************
  * This method returns the string required to add the given remark to a PNR
  *
  * @see PNRRemark
  ***********************************************************************
  */
 private String getRemarkString(final PNRRemark aRemark) throws Exception
   {
   final StringBuffer sCommand = new StringBuffer();

   if ( aRemark instanceof PNRSsrRemark )
     {
     final PNRSsrRemark ssr = (PNRSsrRemark )aRemark;
       sCommand.append("SR" + ssr.Code);
     if ( GnrcFormat.NotNull(ssr.Carrier) )
       sCommand.append(ssr.Carrier);
     if ( GnrcFormat.NotNull(ssr.RemarkText) )
       sCommand.append("-" + ssr.RemarkText);
     if ( ssr.ItinSegment > 0 )
       sCommand.append(" /S" + ssr.ItinSegment);
     if ( ssr.NameNumber > 0 )
       sCommand.append(" /P" + ssr.NameNumber);
     }
   else if ( aRemark instanceof PNRFreqFlyRemark )
     {
     final PNRFreqFlyRemark ff = (PNRFreqFlyRemark )aRemark;
       sCommand.append("FFN" + ff.Carrier + "-" + ff.RemarkText);
     if ( ff.NameNumber > 0 )
       sCommand.append(" /P" + ff.NameNumber);
     }
   else if ( aRemark instanceof PNROsiRemark )
     {
     final PNROsiRemark osi = (PNROsiRemark )aRemark;
       sCommand.append("OS ");
     if ( GnrcFormat.NotNull(osi.Carrier) )
       sCommand.append(osi.Carrier + " ");
     if ( GnrcFormat.NotNull(osi.RemarkText) )
       sCommand.append(osi.RemarkText);
     if ( osi.ItinSegment > 0 )
       sCommand.append(" /S" + osi.ItinSegment);
     if ( osi.NameNumber > 0 )
       sCommand.append(" /P" + osi.NameNumber);
     }
  else if ( aRemark instanceof PNRGeneralRemark )
    {     // general remark
    sCommand.append( "RM " + GnrcFormat.ShowString(aRemark.RemarkText) );
    if ( aRemark.FamilyNumber > 0 )
      sCommand.append("/P" + aRemark.FamilyNumber);
    if ( aRemark.ItinSegment > 0 )
      sCommand.append("/S" + aRemark.ItinSegment);
    }
  else if ( (aRemark instanceof PNRItinRemark) || (aRemark instanceof PNRPocketItinRemark) )
    {
    sCommand.append( "RIR " + GnrcFormat.ShowString(aRemark.RemarkText) );
    if ( aRemark.ItinSegment > 0 )
      sCommand.append("/S" + aRemark.ItinSegment);
    if ( aRemark.FamilyNumber > 0 )
      sCommand.append("/P" + aRemark.FamilyNumber);
    }
  else if ( aRemark instanceof PNRInvoiceRemark )
    {
    sCommand.append( "RIF " + GnrcFormat.ShowString(aRemark.RemarkText) );
    if ( aRemark.FamilyNumber > 0 )
      sCommand.append("/P" + aRemark.FamilyNumber);
    if ( aRemark.ItinSegment > 0 )
      sCommand.append("/S" + aRemark.ItinSegment);
    }
  else if ( aRemark instanceof PNRAddressRemark )
    sCommand.append("AM " + GnrcFormat.ShowString(aRemark.RemarkText) );
  else if ( aRemark instanceof PNRFreeFormRemark )
    sCommand.append( GnrcFormat.ShowString(aRemark.RemarkText) );
  else if ( aRemark instanceof PNRPhoneRemark )
    sCommand.append("AP " + GnrcFormat.ShowString(aRemark.RemarkText) );
  else
     throw new TranServerException("Unable to create command string for " + aRemark.getClass().getName());

   return( sCommand.toString() );
   }

 /**
  ***********************************************************************
  * This method determines whether the air segment is a Block Space Group
  * (BSG) segment.
  *
  * <p>BSG is a Sabre concept. Its Amadeus equivalent is
  * Negospace.  These concepts denote blocks of inventory that are sold 
  * in bulk to a Cruise Line or other large Travel Agents. These flights have
  * already been sold and reserved for the holder of the inventory.</p>
  *
  * <p>When building a Passenger Name Record (PNR) with these flights, 
  * this reserved inventory is sold 'passively'. This means that
  * the flight is added to the PNR, but is not reserved with the
  * airline, since, presumably, the seat has already been reserved and would 
  * otherwise have been sold twice.
  *
  * <p>It is then up to the holder of the inventory to reconcile in their 
  * internal systems the segments sold passively against those seats held 
  * in inventory.</p>
  ***********************************************************************
  */
 private boolean isBSGSegment(final PNRItinAirSegment aAirSeg)
   {
   final String[] PASSIVE_STATUS_CODES = {"GK","GL","PK","PL"};

   // figure out which sell command to run
   if ( GnrcParser.itemIndex(aAirSeg.ActionCode,PASSIVE_STATUS_CODES) >= 0 )
     return(false);
   else if ( GnrcFormat.NotNull(aAirSeg.RemoteLocator) )
     return(true);
   else
     return(false);

   } // end isBSGSegment

 /** 
  ***********************************************************************
  * This method creates the cryptic code string for a Long Sell command, 
  * as specified below; a 'Long Sell' is one where all the parameters 
  * for the segment are specified, versus a 'Short Sell' on a Terminal 
  * where a Travel Agent, after performing an availability request, sells the
  * segment by specifying a Line Number from the Availability Search Results.
  *
  * The equivalent cryptic code command has a format such as
  * <code>SS AA 614Y 15MAR DFWMIA 1</code> (spaces added for clarity),
  * where:
  * <ul>
  * <li><code>SS</code>    : Long Sell prefix
  * <li><code>AA</code>    : Carrier Code
  * <li><code>614Y</code>  : Flight Number and Inventory Class
  * <li><code>15MAR</code> : Departure Date
  * <li><code>DFWMIA</code>: Origin and Destination
  * <li><code>1</code>     : Number of Seats
  * <ul>
  * In addition, a passive segment code and locator such as
  * <code>PK1/C2EF6G</code> can be attached to the cryptic code command to
  * denote passive segments. See is {@link isBSGSegment} for more on 
  * passive segments.
  ***********************************************************************
  */
 private String createLongSellCommand(final PNRItinAirSegment aAirSeg)
   {
   final StringBuffer sCommand = new StringBuffer();

   // create the long sell command string
   sCommand.append("SS" + GnrcFormat.ShowString(aAirSeg.Carrier) +
                          aAirSeg.FlightNumber +
                          GnrcFormat.ShowString(aAirSeg.InventoryClass) +
                          GnrcFormat.FormatCRSDate(aAirSeg.DepartureDateTime) +
                          GnrcFormat.ShowString(aAirSeg.DepartureCityCode) +
                          GnrcFormat.ShowString(aAirSeg.ArrivalCityCode));

   final String[] PASSIVE_STATUS_CODES = {"GK","GL","PK","PL"};
   if ( GnrcParser.itemIndex(aAirSeg.ActionCode,PASSIVE_STATUS_CODES) >= 0 )
     {
     sCommand.append( GnrcFormat.ShowString(aAirSeg.ActionCode) + aAirSeg.NumberOfSeats);
     if ( GnrcFormat.NotNull(aAirSeg.RemoteLocator) )
       sCommand.append( "/" + aAirSeg.RemoteLocator );
     }
   else
     {
     sCommand.append( aAirSeg.NumberOfSeats );
     }

   return( sCommand.toString() );
   } // end createLongSellCommand

 /** 
  ***********************************************************************
  * Reads the response to a Long Sell, and populates the <code>Status</code> 
  * field of the {@link PNRItinAirSegment} with the status code returned by 
  * the Amadeus Terminal Assistant; the <code>Status</code> field is set
  * to <code>UN</code> (unknown) and is only changed if the response from
  * the TA contains a valid status code.
  *
  * @see createLongSellCommand
  * @see isBSGSegment
  ***********************************************************************
  */
 private void readLongSellResponse(final String aResponse, final PNRItinAirSegment aAirSeg)
   {
   aAirSeg.Status = "UN";

   try
     {
     final String CITY_PAIR = " " + aAirSeg.DepartureCityCode + aAirSeg.ArrivalCityCode + " ";
     String sLine;
     final StringTokenizer lines = new StringTokenizer(aResponse,"\r\n");
     while ( lines.hasMoreTokens() )
       {
       sLine = lines.nextToken();

       if ( sLine.indexOf(CITY_PAIR) >= 0 )
         {
         final MatchInfo status_match = RegExpMatch.getFirstMatch(sLine," [A-Z][A-Z][0-9] ");
         if ( status_match instanceof MatchInfo )
           {
           aAirSeg.Status = status_match.MatchString.trim().substring(0,2);
           return;
           }
         }
       }

     }
   catch (Exception e)
     {
     }
   } // end readLongSellResponse

 /** 
  ***********************************************************************
  * This method creates the cryptic code string for a Block Space Group Sell 
  * (BSG) command, as specified below; a BSG Sell is a passive Sell where
  * seats are sold from inventory that has already been allocated by the 
  * airline to the inventory holder (a Cruise Line, Travel Agent, etc...).
  * See {@link isBSGSegment} for more on this.
  *
  * <p>The cryptic code command has a format such as
  * <code>0* AEU234E NN 1</code> (spaces added for clarity), where:
  * <ul>
  * <li><code>0*</code>: Passive Sell prefix</li>
  * <li><code>AEU234E</code>: a Remote Locator</li>
  * <li><code>NN</code>: Action Code</li>
  * <li><code>1</code>: Number of Seats</li>
  * </ul></p>
  *
  * <p>In addition, a passive segment code and locator such as
  * <code>PK1/C2EF6G</code> can be attached to the cryptic code command to
  * denote passive segments. See is {@link isBSGSegment} for more on 
  * passive segments.</p>
  ***********************************************************************
  */
 private String createBSGSellCommand(final PNRItinAirSegment aAirSeg)
   {
   // create the BSG sell command string
   final String sBSGSellCommand = "0*" + 
                                  GnrcFormat.ShowString(aAirSeg.RemoteLocator) +
                             //   GnrcFormat.ShowString(aAirSeg.ActionCode) +
                                  "NN" +
                                  aAirSeg.NumberOfSeats;
   return(sBSGSellCommand);
   } // end createBSGSellCommand

 /** 
  ***********************************************************************
  * Reads the response to a Block Space Group (BSG) Sell, and populates 
  * the <code>Status</code> field of the {@link PNRItinAirSegment} with 
  * the status code returned by the Amadeus Terminal Assistant; 
  * the <code>Status</code> field is set to <code>UN</code> (unknown) 
  * and is only changed if the response from the TA contains 
  * a valid status code.
  *
  * @see createBSGSellCommand
  * @see isBSGSegment
  ***********************************************************************
  */
 private void readBSGSellResponse(final String aResponse,
                                  final PNRItinSegment[] aSegList,
                                  final String aLocator)
   {
   final StringTokenizer lines = new StringTokenizer(aResponse,"\r\n");
   String sLine;

   while ( lines.hasMoreTokens() )
     {
     sLine = lines.nextToken();

     try
       {
       if ( RegExpMatch.matches(sLine,"^ *[1-9]") )
         {
         final MatchInfo status_match = RegExpMatch.getFirstMatch(sLine," [A-Z][A-Z][1-9][0-9]? ");
         if ( status_match instanceof MatchInfo )
           {
           final String sStatus = status_match.MatchString.trim().substring(0,2);
           updateSegmentStatus(aSegList,aLocator,sStatus);
           return;
           }
         }
       }
     catch (Exception e)
       {
       }
     } // end while

   // default status
   updateSegmentStatus(aSegList,aLocator,"UN");

   } // end readBSGSellResponse

 /** 
  ***********************************************************************
  * Updates any segments that match the given flight criteria with
  * the status code
  ***********************************************************************
  */
 private void updateSegmentStatus(final PNRItinSegment[] aSegList,
                                  final String aLocator, 
                                  final String aStatusCode)
   {

   // set the status codes for any air segments that match the given criteria
   if ( (aSegList instanceof PNRItinSegment[]) && (aLocator instanceof String) )
     {
     PNRItinAirSegment airseg = null;
     for ( int i = 0; i < aSegList.length; i++ )
      {
      if ( aSegList[i] instanceof PNRItinAirSegment )
        {
        airseg = (PNRItinAirSegment )aSegList[i];
        if ( aLocator.equals(airseg.RemoteLocator) )
          airseg.Status = aStatusCode;
        }
      } // end for
     }

   } // updateSegmentStatus




  /**
   ***********************************************************************
   * Given an array of remarks to match (the needles), and an array of remarks
   * in which to search (the haystack), this method returns an array of those
   * remarks in the haystack which match one of the remarks in the array of
   * needles; this is used, for example, to match an array of remarks to be
   * deleted passed by Airware (needles) to the remarks found in a Passenger
   * Name Record (PNR) returned by Amadeus (the haystack); in order to do the
   * match, this method uses the {@link #isSelectedRemark} method; see that
   * method for a description of how the matches are made.
   *
   * @param aryNeedle   the remarks to be matched
   * @param aryNaystack the remarks from which to match
   *
   * @see #isSelectedRemark
   ***********************************************************************
   */
  protected static PNRRemark[] getMatchingRemarks(PNRRemark[] aryNeedle, 
                                                  PNRRemark[] aryHaystack)
    {
    if ( aryNeedle == null || aryHaystack == null)
      return null;

    // turn the aryHaystack into a list so that we can remove the items matched
    ArrayList listHaystack= new ArrayList();
    for (int i=0; i < aryHaystack.length ; i++)
      listHaystack.add(aryHaystack[i]);

    // store the remarks to be deleted in this vector
    ArrayList listReturn = new ArrayList();

    for (int i=0; i < aryNeedle.length ; i++ )
      {
      Iterator enumHaystack = listHaystack.iterator(); 
      while(enumHaystack.hasNext())
        {
        PNRRemark needle   = aryNeedle[i];
        PNRRemark haystack = (PNRRemark)enumHaystack.next();
        if ( isSelectedRemark(needle,haystack) )
          {
          listReturn.add(haystack);
          // remove the remark matched in the event that we have duplicate
          // remarks on the haystack - this ensures that if two duplicates are
          // to be matched, the first remark on the haystack is not selected
          // twice - this also speeds processing
          enumHaystack.remove();
          }
        }
      }
    PNRRemark[] aryRemark = new PNRRemark[listReturn.size()];
    return (PNRRemark[])listReturn.toArray(aryRemark);
    } // end getMatchingRemarks


  /** 
   ***********************************************************************
   * Matches a remark passed by Airware to a Remark found in a Passenger Name
   * Record (PNR) returned by Amadeus; the needle represents a remark that we
   * wish to delete from the PNR, for example, and the haystack represents the
   * remark on the PNR; the matching goes as follows:
   * <ul> 
   *  <li>Generally speaking, if the needle and the haystack are the same type
   *      of remark, and the text of the needle matches the beginning of the
   *      text of the haystack, we have a match, provided that the passenger IDs
   *      match as stated below
   *  </li>
   *  <li>If the needle specifies a Passenger Id, then only return true if the
   *      Passenger Id of the haystack matches the needles' Passenger Id, and
   *      the text matches as indicated above
   *  </li>
   *  <li>Conversely, if the needle's passenger Id is not specified, match
   *      according to text only, and ignore the haystack's passenger Id 
   *      (for example, this makes it possible to delete all remarks matching a
   *      certain text, regardless of their passenger Id)
   *  </li>
   *  <li>In the case of SSR remarks, the remark code must also match</li>
   * </ul>
   * 
   * @param needle the remark to be matched
   * @param haystack a remark returned by Amadeus 
   *
   * @see #remarkStringMatch
   ***********************************************************************
   */
  protected static boolean isSelectedRemark(final PNRRemark needle, 
                                            final PNRRemark haystack)
    {
    if (GnrcFormat.NotNull(needle.getPsgrID()) && 
        haystack.hasSamePassengerID(needle) == false )
      return(false);
 
    // for SSR remarks
    if ( (needle instanceof PNRSsrRemark) && 
         (haystack instanceof PNRSsrRemark) )
      {
      final PNRSsrRemark SSR_needle     = (PNRSsrRemark )needle;
      final PNRSsrRemark SSR_haystack   = (PNRSsrRemark )haystack;
 
      if ( remarkStringMatch(SSR_needle.Code,SSR_haystack.Code) &&
           remarkStringMatch(SSR_needle.RemarkText,SSR_haystack.RemarkText) )
        return(true);
      }
    // for OSI remarks
    else if ( (needle instanceof PNROsiRemark) && 
              (haystack instanceof PNROsiRemark) )
      {
      if ( remarkStringMatch(needle.RemarkText,haystack.RemarkText) )
        return(true);
      }
    // for General Remarks
    else if ( (needle instanceof PNRGeneralRemark) && 
              (haystack instanceof PNRGeneralRemark) )
      {
      if ( remarkStringMatch(needle.RemarkText,haystack.RemarkText) )
        return(true);
      }
    // for Phone Remarks
    else if ( (needle instanceof PNRPhoneRemark) &&
              (haystack instanceof PNRPhoneRemark) )
      {
      if ( remarkStringMatch(needle.RemarkText,haystack.RemarkText) )
        return(true);
      }
    // for Ticket Remarks
    else if ( (needle instanceof PNRTicketRemark) &&
              (haystack instanceof PNRTicketRemark) )
      {
      if ( remarkStringMatch(needle.RemarkText,haystack.RemarkText) )
        return(true);
      }
 
    return(false);
    }


 /** 
  ***********************************************************************
  * Returns true if the string 'needle' is found at the beginning of the string
  * 'haystack'; if either 'needle' or 'haystack' are null, they are turned into
  * the empty string for purposes of the match.
  ***********************************************************************
  */
 private static boolean remarkStringMatch(String needle, String haystack)
   {
   if ( (needle instanceof String) == false )
     needle = "";

   if ( (haystack instanceof String) == false )
     haystack = "";

   /*
   if ( (needle.length() == 0) || (haystack.indexOf(needle) >= 0) )
     return(true);
   else
     return(false);
   */
   return haystack.startsWith(needle);
   }


 /** 
  ***********************************************************************
  * sends the <code>FE</code> command a string containing the endorsement
  * information.
  ***********************************************************************
  */
 public void addEndorsement(final GnrcCrs aCRS, final String aEndorsement) throws Exception
  {
  final String sCommand = "FE " + GnrcFormat.ShowString(aEndorsement);
  final String sResponse = aCRS.HostTransaction(sCommand).trim();
  if ( sResponse.indexOf("RP/") < 0 )
    throw new GdsResponseException("Unable to add endorsement",sCommand,sResponse);
  }

 /** 
  ***********************************************************************
  * Sends the <code>FT</code> command and a string containing the tour code
  ***********************************************************************
  */
 public void addTourCode(final GnrcCrs aCRS, final String aTourCode) throws Exception
  {
  final String sCommand = "FT " + GnrcFormat.ShowString(aTourCode);
  final String sResponse = aCRS.HostTransaction(sCommand).trim();
  if ( sResponse.indexOf("RP/") < 0 )
    throw new GdsResponseException("Unable to add tour code",sCommand,sResponse);
  }



 /** 
  ***********************************************************************
  * This method issues the cryptic code command <code>ETK</code> to Accept
  * Scheduled Changes and EndTransact a the Passenger Name Record (PNR); In
  * Amadeus, this command is wrapped up within an EndTransaction type command,
  * and hence the changes are accepted and the PNR is saved in one transaction;
  * since this is the case, a ReceivedFrom remark must be added beforehand
  ***********************************************************************
  */
 public static void acceptSchedChange(final GnrcCrs aCRS, 
     final String aLocator, final String aReceiveBy) throws Exception
  {
  // display the PNR
  aCRS.LoadPNRIntoAAA(aLocator);

  // we must first do a ReceivedFrom prior to accepting scheduled changes
  // because these are wrapped within an EndTransaction type command
  aCRS.AddReceiveBy(aReceiveBy);

  // run the accept sched change command
  // 'ETK' saves all scheduled changes and ends Transact
  // 'ERK' does the same but redisplays the PNR
  String sResponse = aCRS.HostTransaction("ETK").trim();

  // if we get a SIMULTANEOUS CHANGE ERROR, try again
  final String SIMUL_ERROR = "SIMULTANEOUS CHANGES";
  if (RegExpMatch.matches(sResponse, SIMUL_ERROR) == true)
    {
    aCRS.Ignore();
    aCRS.LoadPNRIntoAAA(aLocator);
    aCRS.AddReceiveBy(aReceiveBy);
    sResponse = aCRS.HostTransaction("ETK").trim();
    }
  
  // If we get a 'CHECK SEGMENT CONTINUITY' warning, or
  // 'CHECK CONNECT TIMES' or 'IGNORE AND RE-ENTER'
  // repeat the end transaction
  final String CHECK_SEGMENT_CONT = "CHECK SEGMENT CONTINUITY";
  final String CHECK_CONNECT_TIME = "CHECK MINIMUM CONNECTION TIME";
  final String IGNORE_REENTER     = "IGNORE AND RE-ENTER";
  if (RegExpMatch.matches(sResponse,CHECK_SEGMENT_CONT) ||
      RegExpMatch.matches(sResponse,CHECK_CONNECT_TIME) ||
      RegExpMatch.matches(sResponse,IGNORE_REENTER))
    sResponse = aCRS.HostTransaction("ETK").trim();

  // make sure that we successfully saved the scheduled changes
  final String ET_RESPONSE_PATTERN = "END OF TRANSACTION COMPLETE \\- ([A-Z0-9]{6})";
  if (RegExpMatch.matches(sResponse,ET_RESPONSE_PATTERN) == false)
    throw new GdsResponseException(
        "Unable to accept scheduled changes: " + sResponse);
  } // end acceptSchedChange

}

