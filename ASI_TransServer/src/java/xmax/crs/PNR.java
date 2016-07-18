package xmax.crs;

import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.ArrayList;

import xmax.crs.GetPNR.*;
import xmax.TranServer.GnrcFormat;
import xmax.TranServer.TranServerException;

/**
 ***********************************************************************
 * This class describes a Passenger Name Record (PNR) which contains
 * the multiple elements making up a flight reservation.
 *
 * @author John Crowley
 * @author David Fairchild
 * 
 * @version  $Revision: 23$ - $Date: 09/18/2002 4:38:19 PM$
 ***********************************************************************
 */

public class PNR implements Serializable
{
  // new fields for PNR

  /** the exact strings returned by the CRS for this PNR */
  private String[] RawPNRData;

  /** a copy of rawdata with overlapping data removed */
  private String PNRData;

  /**
   * The Computer Reservation System (CRS) code from which this
   * PNR originates
   */
  private String CrsCode;

  /** 
   * a string returned by the Computer Reservation System that
   * uniquely identifies this PNR
   */
  private String record_locator;

  /** a string that identifies the entity which created this PNR */
  private String PseudoCity;

  /** a string that identifies the travel agent under which name this
   * PNR was built
   */
  private String AgentSign;

  /** the customer account number */
  private String CustAcctNumber;

  /** the date on which this PNR was last read */
  private long   ReadDate;

  /** the group header associate with this PNR */
  private PNRGroupHeader GroupHeader;
  /**
   * A vector of elements of type {@link PNRFamilyElement} used
   * to store groups of passengers; in turn, each <code>PNRFamilyElement
   * refers to a vector of {@link PNRNameElement} objects which contain
   * information on each individual passenger.
   */
  private Vector FamList;

  /** 
   * a vector of elements of type {@link PNRItinSegment} used to store
   * the segments (flights, car or hotel reservation) sold on this 
   * reservation
   */
  private Vector SegList;

  /** 
   * a vector of elements of type {@link PNRRemark} which stores the
   * many types of remarks that can be added to a PNR
   */
  private Vector RmkList;

  /** 
   * a vector of elements of type {@link PNRFare} which stores the
   * line items of the fare of the PNR; different Computer Reservation Systems
   * (CRS) display fares in different formats.
   */
  private Vector FareList;

  /** 
   * a vector of elements of type {@link PNRError} or <code>String</code> 
   * which contains the errors encountered when parsing this PNR;
   */
  private Vector ErrList;

  /**
   ***********************************************************************
   * Constructors
   ***********************************************************************
   */
  public PNR()
    {
    }

  public PNR(final File aFile) throws IOException
    {
    ReadFromFile(aFile);
    }

  public PNR(final String aFileName) throws IOException
    {
    ReadFromFile(aFileName);
    }


  private void assignTo(PNR pnr)
    {
    CrsCode        = pnr.CrsCode;
    record_locator = pnr.record_locator;

    /*
    AllSectionResponse     = pnr.AllSectionResponse;
    HeaderSectionResponse  = pnr.HeaderSectionResponse;
    NameSectionResponse    = pnr.NameSectionResponse;
    ItinSectionResponse    = pnr.ItinSectionResponse;
    TicketSectionResponse  = pnr.TicketSectionResponse;
    PhoneSectionResponse   = pnr.PhoneSectionResponse;
    AddressSectionResponse = pnr.AddressSectionResponse;
    FOPSectionResponse     = pnr.FOPSectionResponse;
    RemarkSectionResponse  = pnr.RemarkSectionResponse;

    HeaderSection  = pnr.HeaderSection;
    NameSection    = pnr.NameSection;
    ItinSection    = pnr.ItinSection;
    PhoneSection   = pnr.PhoneSection;
    AddressSection = pnr.AddressSection;
    TicketSection  = pnr.TicketSection;
    RemarkSection  = pnr.RemarkSection;
    ErrorResponse  = pnr.ErrorResponse;
    GeneralFactsSection = pnr.GeneralFactsSection;
    */
    }

  /**
   ***********************************************************************
   * returns the CrsCode field concatenated to the record_locator field
   ***********************************************************************
   */
  public String toString()
    {
    return( CrsCode + " " + record_locator );
    }

  /**
   ***********************************************************************
   * This procedure saves PNR data to a file
   ***********************************************************************
   */
  public void WriteToFile(final String aFileName) throws IOException
    {
    final File OutFile = new File(aFileName);
    WriteToFile(OutFile);
    }


  public void WriteToFile(final File aOutFile) throws IOException
    {
    if ( PNRData instanceof String )
      {
      final FileOutputStream out = new FileOutputStream(aOutFile);
      out.write( PNRData.getBytes() );
      out.flush();
      out.close();
      }
    }

  /**
   ***********************************************************************
   * This procedure reads PNR data from a file
   ***********************************************************************
   */
  public void ReadFromFile(final String aFileName) throws IOException
    {
    final File InFile = new File(aFileName);
    ReadFromFile(InFile);
    }


  public void ReadFromFile(final File aInFile) throws IOException
    {
    // set the crs code and locator from the file name
    final String sBaseName = aInFile.getName().toUpperCase();
    if ( sBaseName.length() < 8 )
      throw new IOException("Invalid PNR data file name.  Name must contain at least 8 characters");

    CrsCode          = sBaseName.substring(0,2);
    record_locator   = sBaseName.substring(2,8);

    // open the file and read the entire contents into the byte buffer
    final FileInputStream in = new FileInputStream(aInFile);
    int file_size = (int )aInFile.length();
    byte[] buffer = new byte[file_size];

    if ( in.read(buffer, 0, file_size) != file_size )
      throw new IOException("Error! Could not read all of the data in " + aInFile.getAbsolutePath() );

    PNRData = new String(buffer);
    in.close();
    }

  /**
   ***********************************************************************
   * This function returns a string array that has the aInString string
   * appended to aSection
   ***********************************************************************
   */
  /*
  private String [] addToSection(final String[] aSection, final String aInString)
    {
    String [] NewSection;

    // create a new array with extra space in it for the new string
    if ( aSection instanceof String[] )
      {
      NewSection = new String[ aSection.length + 1 ];
      System.arraycopy(aSection, 0, NewSection, 0, aSection.length );
      }
    else
      NewSection = new String[1];

    NewSection[NewSection.length - 1] = aInString;
    return(NewSection);
    }
  */

  /**
   ***********************************************************************
   * This function returns an array of family objects referred to
   * in the PNR data strings
   ***********************************************************************
   */
  /*
  public PNRFamilyElement [] GetFamilies() throws Exception
    {
    GnrcParsePNR parser = GetParser();
    return( parser.GetFamilies(this) );
    }
  */

  /**
   ***********************************************************************
   * Associates a GroupHeader with this PNR
   ***********************************************************************
   */
  public void setGroupHeader(PNRGroupHeader aGroupHeader)
    {
    GroupHeader = aGroupHeader;
    } // end setGroupHeader


  /**
   ***********************************************************************
   * Returns this PNR's Group Header
   ***********************************************************************
   */
  public PNRGroupHeader getGroupHeader()
    {
    return(GroupHeader);
    } // end getGroupHeader

  /**
   ***********************************************************************
   * This method totals the number of seats that have been sold in this
   * Passenger Name Record; in the event that this PNR contains a Group Header,
   * this method is used to calculate the number of seats available 
   *
   * @see getNumSeatsAvailable
   ***********************************************************************
   */
  public int getNumSeatsBooked()
    {
    final PNRFamilyElement[] families = getFamilies();
    int iNumSeats = 0;

    /*
    // search through all the family members
    if ( families instanceof PNRFamilyElement[] )
      {
      for ( int i = 0; i < families.length; i++ )
        {
        if ( families[i].FamilyMembers instanceof PNRNameElement[] )
          {
          for ( int j = 0; j < families[i].FamilyMembers.length; j++ )
            {
            iNumSeats += families[i].FamilyMembers[j].NumSeats;
            }
          }
        }
      }
    */

    // loop through all the family members and total the number of seats
    if ( families instanceof PNRFamilyElement[] )
      for ( int i = 0; i < families.length; i++ )
        if ( families[i].FamilyMembers instanceof PNRNameElement[] )
          for ( int j = 0; j < families[i].FamilyMembers.length; j++ )
            iNumSeats += families[i].FamilyMembers[j].NumSeats;

    return(iNumSeats);
    
    } // end getNumSeatsBooked

  /**
   ***********************************************************************
   * In the event that this PNR contains a Group Header, this method returns
   * the difference between the number of seats that have been allocated to
   * this PNR in the Group Header, and the number of seats that have been
   * actually booked in the PNR; if the PNR contains no group header, the
   * method returns <code>0</code>
   ***********************************************************************
   */
  public int getNumSeatsAvailable()
    {
    if (GroupHeader instanceof PNRGroupHeader)
      return(GroupHeader.numSeatsAllocated - getNumSeatsBooked());
    else
      return(0);
    } // end getNumSeatsAllocated

  /**
   ***********************************************************************
   * This function returns a number indicating which family the given
   * name belongs to (1 based) -1 = not found
   ***********************************************************************
   */
  public int getFamilyNum(final PNRNameElement aName) throws Exception
    {
    final PNRFamilyElement[] families = getFamilies();

    // search through all the family members
    if ( families instanceof PNRFamilyElement[] )
      {
      for ( int iFamilyNum = 0; iFamilyNum < families.length; iFamilyNum++ )
        {
        if ( families[iFamilyNum].FamilyMembers instanceof PNRNameElement[] )
          {
          for ( int iMemberNum = 0; iMemberNum < families[iFamilyNum].FamilyMembers.length; iMemberNum++ )
            {
            if ( families[iFamilyNum].FamilyMembers[iMemberNum].LastName.equals(aName.LastName) &&
                 families[iFamilyNum].FamilyMembers[iMemberNum].FirstName.equals(aName.FirstName) )
              {
              if ( families[iFamilyNum].FamilyNumber > 0 )
                return(families[iFamilyNum].FamilyNumber);
              else
                return(iFamilyNum + 1);
              }
            }
          }
        }
      }

    return(-1);
    }

  /**
   ***********************************************************************
   * This function returns a number indicating which member of a family
   * the given name belongs to (1 based) -1 = not found
   ***********************************************************************
   */
  public int getMemberNum(final PNRNameElement aName) throws Exception
    {
    final PNRFamilyElement[] families = getFamilies();

    // search through all the family members
    if ( families instanceof PNRFamilyElement[] )
      {
      for ( int iFamilyNum = 0; iFamilyNum < families.length; iFamilyNum++ )
        {
        if ( families[iFamilyNum].FamilyMembers instanceof PNRNameElement[] )
          {
          for ( int iMemberNum = 0; iMemberNum < families[iFamilyNum].FamilyMembers.length; iMemberNum++ )
            {
            if ( families[iFamilyNum].FamilyMembers[iMemberNum].LastName.equals(aName.LastName) &&
                 families[iFamilyNum].FamilyMembers[iMemberNum].FirstName.equals(aName.FirstName) )
              {
              return(iMemberNum + 1);
              }
            }
          }
        }
      }

    return(-1);
    }

  /**
   ***********************************************************************
   * This function returns a number indicating which line the remark is on
   *  -1 = not found
   ***********************************************************************
   */
  public int getMessageNum(final PNRRemark aRemark) throws Exception
    {
    final PNRRemark[] remarks = getRemarks();
    if ( remarks instanceof PNRRemark[] )
      {
      for ( int i = 0; i < remarks.length; i++ )
        {
        if ( remarks[i].equals(aRemark) )
          return(remarks[i].MessageNumber);
        }
      }

    return(-1);
    }

  /**
   ***********************************************************************
   * Given a {@link PNRNameElement}, this function returns a number indicating
   * the position of the name within the PNR starting at 1 ; a -1 = not
   * found<br/> 
   * NOTE: this breaks when there are two passengers with the same
   * FirstName and LastName and no PassengerID
   ***********************************************************************
   */
  public int getPsgrNum(final PNRNameElement aName) 
    {
    final PNRNameElement[] names = getNames();
    if ( (names instanceof PNRNameElement[]) == false )
      return(-1);

    // search through all the passengers
    for ( int iPsgrNum = 0; iPsgrNum < names.length; iPsgrNum++ )
      {
      if ( GnrcFormat.NotNull(aName.LastName) && GnrcFormat.NotNull(aName.FirstName) )
        {
        if ( names[iPsgrNum].LastName.equals(aName.LastName) && 
             names[iPsgrNum].FirstName.equals(aName.FirstName) )
          return(iPsgrNum + 1);
        }
      else if ( GnrcFormat.NotNull(aName.getPassengerID()) && 
                GnrcFormat.NotNull(names[iPsgrNum].getPassengerID()) )
        {
        if ( names[iPsgrNum].hasSamePsgrID(aName) )
          return(iPsgrNum + 1);
        }
      }

    return(-1);
    }

  /**
   ***********************************************************************
   * Given a PassengerID, this function returns a number indicating the
   * position of the name within the PNR starting at 1 ; a -1 = not found<br/>
   * NOTE: this breaks when there are two passengers with the same FirstName
   * and LastName and no PassengerID
   ***********************************************************************
   */
  public int getPsgrNum(final String psgrID) 
    {
    final PNRNameElement[] names = getNames();
    if ( (names instanceof PNRNameElement[]) == false )
      return(-1);

    // search through all the passengers
    for ( int iPsgrNum = 0; iPsgrNum < names.length; iPsgrNum++ )
      {
      if ( names[iPsgrNum].hasPassengerID(psgrID) )
          return(iPsgrNum + 1);
      }

    return(-1);
    }

  /**
   ***********************************************************************
   * This function returns an array of name objects referred to
   * in the PNR data strings
   ***********************************************************************
   */
  /*
  public String GetEntirePNR() throws Exception
    {
    if ( (AllSections instanceof String) == false )
      AllSections = GnrcParser.getCombinedHostResponse(AllSectionResponse);

    return( AllSections );
    }
  */


  /**
   ***********************************************************************
   * This function returns an array of name objects referred to
   * in the PNR data strings
   ***********************************************************************
   */
  /*
  public PNRNameElement [] GetNames() throws Exception
    {
    final GnrcParsePNR parser = GetParser();
    return( parser.GetNames(this) );
    }
  */
  /**
   ***********************************************************************
   * This function looks up a given passenger name and returns the
   * name element
   ***********************************************************************
   */
  public PNRNameElement getName(final String aLastName, final String aFirstName) 
    throws Exception
    {
    // get a list of all the names on the PNR
    final PNRNameElement[] AllNames = getNames();
    if ( (AllNames instanceof PNRNameElement[]) == false )
      return(null);

    for ( int i = 0; i < AllNames.length; i++ )
      {
      if ( AllNames[i].LastName.startsWith(aLastName) && AllNames[i].FirstName.startsWith(aFirstName) )
        return(AllNames[i]);
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This function looks through the name elements stored in the PNR and
   * returns the passenger whose {@link PNRNameElement#getPassengerID()} matches 
   * the passenger ID passed, or <code>null</code> if no passenger was found.
   ***********************************************************************
   */
  public PNRNameElement getName(final String psgrID) throws Exception
    {
    if ( (psgrID instanceof String) == false ) 
      throw new TranServerException(
          "You must provide a non-null PassengerID " +
          "in order to search a PNR for a passenger by PassengerID");

    if ( psgrID.length() < 9 )
      throw new TranServerException(
          "You must provide a PassengerID that is at least 9 characters " +
          "in order to search a PNR for a passenger by PassengerID");

    // get a list of all the names on the PNR
    final PNRNameElement[] AllNames = getNames();
    if ( (AllNames instanceof PNRNameElement[]) == false )
      return(null);

    for ( int i = 0; i < AllNames.length; i++ )
      {
      if ( GnrcFormat.NotNull(AllNames[i].getPassengerID()) )
        {
        if ( AllNames[i].getStrictPassengerID().equals(psgrID.substring(0,9)) )
          return(AllNames[i]);
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This function looks up a given passenger family number and returns the
   * name element;  Input numbers are 1 based - NOT zero based
   ***********************************************************************
   */
  public PNRNameElement getName(int aFamilyNumber, int aFamilyMemberNumber) throws Exception
    {
    // set the indexes to be zero based
    if ( aFamilyNumber > 0 )
      aFamilyNumber--;
    if ( aFamilyMemberNumber > 0 )
      aFamilyMemberNumber--;

    // get a list of all the families on the PNR
    final PNRFamilyElement[] AllFamilies = getFamilies();
    if ( AllFamilies instanceof PNRFamilyElement[] )
      {
      if ( aFamilyNumber < AllFamilies.length )
        {
        if ( aFamilyMemberNumber < AllFamilies[aFamilyNumber].FamilyMembers.length )
          return( AllFamilies[aFamilyNumber].FamilyMembers[aFamilyMemberNumber] );
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This function looks up a given passenger number and returns the
   * name element.  aNameNumber is 1 based - NOT zero based
   ***********************************************************************
   */
  public PNRNameElement getName(int aNameNumber)
    {
    // get a list of all the names on the PNR
    final PNRNameElement[] AllNames = getNames();
    if ( AllNames instanceof PNRNameElement[] )
      {
      if ( aNameNumber > 0 )
        aNameNumber--;

      if ( aNameNumber < AllNames.length )
        return(AllNames[aNameNumber]);
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This function looks up a the parent for the given infant on
   * the PNR, returns null if the infant is NOT listed, or there is
   * no adult that shares the same last name
   ***********************************************************************
   */
  public PNRNameElement getParentName(final PNRNameElement aInfant) throws Exception
    {
    if ( aInfant.isInfant() == false )
      return(null);

    // get a list of all the names on the PNR
    final PNRNameElement[] AllNames = getNames();
    if ( AllNames instanceof PNRNameElement[] )
      {
      PNRNameElement parentName = null;
      for ( int i = 0; i < AllNames.length; i++ )
        {
        if ( AllNames[i].isAdult() )
          parentName = AllNames[i];
        else if ( AllNames[i].equals(aInfant)  )
          return(parentName);
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This function looks up a the infant for the given parent on
   * the PNR, returns null if the infant is NOT listed, or there is
   * no adult that shares the same last name
   ***********************************************************************
   */
  public PNRNameElement getInfantName(final PNRNameElement aParent) throws Exception
    {
    if ( aParent.isAdult() == false )
      return(null);

    // get a list of all the names on the PNR
    final PNRNameElement[] AllNames = getNames();
    if ( AllNames instanceof PNRNameElement[] )
      {
      boolean foundParent = false;
      for ( int i = 0; i < AllNames.length; i++ )
        {
        if ( foundParent )
          {
          if ( AllNames[i].isInfant() )
            return(AllNames[i]);
          else if ( AllNames[i].isAdult() )
            return(null);
          }
        else if ( AllNames[i].equals(aParent)  )
          foundParent = true;
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This function looks up a given passenger number and returns the
   * name element; note that aNameNumber is 1 based - NOT zero based.
   ***********************************************************************
   */
  public PNRItinAirSegment getItinSegmentByFlight(final String aCarrier,
                                                  final int aFlightNum)
                                                      throws Exception
    {
    // get a list of all the itinerary segments on the PNR
    final PNRItinSegment[] AllSegments = getSegments();

    // look for itinerary air segments that match the given carrier and flight
    if ( AllSegments instanceof PNRItinSegment[] )
      {
      PNRItinAirSegment airsegment;
      for ( int i = 0; i < AllSegments.length; i++ )
        {
        if ( AllSegments[i] instanceof PNRItinAirSegment )
          {
          airsegment = (PNRItinAirSegment )AllSegments[i];
          if ( airsegment.Carrier.equals(aCarrier) && (airsegment.FlightNumber == aFlightNum) )
            return(airsegment);
          }
        }
      }


    return(null);
    }


  public PNRItinAirSegment getItinSegment(final String aDepCity, final String aArrCity) throws Exception
    {
    // get a list of all the itinerary segments on the PNR
    final PNRItinSegment[] AllSegments = getSegments();

    // look for itinerary air segments that match the given city pair
    if ( AllSegments instanceof PNRItinSegment[] )
      {
      PNRItinAirSegment airsegment;
      for ( int i = 0; i < AllSegments.length; i++ )
        {
        if ( AllSegments[i] instanceof PNRItinAirSegment )
          {
          airsegment = (PNRItinAirSegment )AllSegments[i];
          if ( airsegment.DepartureCityCode.equals(aDepCity) && airsegment.ArrivalCityCode.equals(aArrCity) )
            return(airsegment);
          }
        }
      }


    return(null);
    }

  public PNRItinSegment getItinSegment(final int aSegmentNumber) throws Exception
    {
    // get a list of all the itinerary segments on the PNR
    final PNRItinSegment[] AllSegments = getSegments();

    // find the itinerary segment
    if ( AllSegments instanceof PNRItinSegment[] )
      {
      for ( int i = 0; i < AllSegments.length; i++ )
        {
        if ( AllSegments[i].SegmentNumber == aSegmentNumber )
          return(AllSegments[i]);
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This method returns all the Air Segments in a given PNR.
   *
   * @see  PNRItinSegment
   * @see  PNRItinAirSegment
   ***********************************************************************
   */
  public PNRItinAirSegment[] getItinAirSegments() 
    {
    // get a list of all the itinerary segments on the PNR
    final PNRItinSegment[] AllSegments = getSegments();

    // find the air segments
    Vector vctrAirSegments = new Vector();
    if ( AllSegments instanceof PNRItinSegment[] )
      {
      for ( int i = 0; i < AllSegments.length; i++ )
        {
        if ( AllSegments[i] instanceof PNRItinAirSegment )
          {
          vctrAirSegments.addElement(AllSegments[i]);
          }
        }  //end for
      } // end if

    PNRItinAirSegment [] aryAirSegments =
      new PNRItinAirSegment [vctrAirSegments.size()];

    vctrAirSegments.toArray(aryAirSegments);
    return( aryAirSegments );
    }

  /**
   ***********************************************************************
   * This method returns all the Air Segment numbers in a given PNR. 
   *
   * @see  PNRItinSegment
   * @see  PNRItinAirSegment
   ***********************************************************************
   */
  public int[] getItinAirSegmentNumbers() throws Exception
    {
    // get a list of all the air segments on the PNR
    final PNRItinAirSegment[] aryAirSegments = getItinAirSegments();

    // return a list of numbers
    int[] arySegmentList = new int[aryAirSegments.length];
    if ( aryAirSegments instanceof PNRItinAirSegment[] )
      {
      for ( int i = 0; i < aryAirSegments.length; i++ )
        {
				arySegmentList[i] = aryAirSegments[i].SegmentNumber;
        }  //end for
      } // end if
    return( arySegmentList );
    }

  /** 
   ***********************************************************************
   * This method returns all the SSR remarks that match the given criteria
   *  in a given PNR.
   *
   * @see  PNRSSRRemark
   ***********************************************************************
   */
  public PNRSsrRemark[] getMatchingSSR(final String aCode) throws Exception
    {
    return( getMatchingSSR(aCode,null,null) );
    }

  public PNRSsrRemark[] getMatchingSSR(final String aCode, final String aCarrier) throws Exception
    {
    return( getMatchingSSR(aCode,aCarrier,null) );
    }


  public PNRSsrRemark[] getMatchingSSR(final String aCode, final String aCarrier, final String aPassengerID) throws Exception
    {
    // get all the remarks and the PNRNameElement for the given passenger
    final PNRRemark[] remarks = getRemarks();
    final PNRNameElement name = getName(aPassengerID);

    // find the SSR segements
    final Vector vSSR = new Vector();
    PNRSsrRemark ssr;
    if ( remarks instanceof PNRRemark[] )
      {
      for ( int i = 0; i < remarks.length; i++ )
        {
        if ( remarks[i] instanceof PNRSsrRemark )
          {
          ssr = (PNRSsrRemark )remarks[i];
          if ( ssrMatch(ssr,aCode,aCarrier,name) )
            vSSR.add(ssr);
          }
        }  //end for
      } // end if

    if ( vSSR.size() > 0 )
      {
      final PNRSsrRemark[] ssr_array = new PNRSsrRemark[ vSSR.size() ];
      vSSR.toArray(ssr_array);
      return(ssr_array);
      }
    else
      return(null);
    }


  private boolean ssrMatch(final PNRSsrRemark aSSR, final String aCode, final String aCarrier, final PNRNameElement aName)
    {
    // test the SSR code
    if ( GnrcFormat.NotNull(aCode) )
      {
      if ( GnrcFormat.strEqual(aCode,aSSR.Code) == false )
        return(false);
      }

    // test the carrier code
    if ( GnrcFormat.NotNull(aCarrier) )
      {
      if ( aCarrier.equals("YY") == false )
        {
        if ( GnrcFormat.strEqual(aCarrier,aSSR.Carrier) == false )
          return(false);
        }
      }

    // test the passenger association
    if ( aName instanceof PNRNameElement )
      {
      if ( GnrcFormat.strEqual(aName.LastName,aSSR.LastName) == false )
        return(false);
      if ( GnrcFormat.strEqual(aName.FirstName,aSSR.FirstName) == false )
        return(false);
      }

    return(true);
    }

  /**
   ***********************************************************************
   * This method returns all the frequent flier remarks that match the given criteria
   *  in a given PNR.
   *
   * @see  PNRFreqFlyRemark
   ***********************************************************************
   */
  public PNRFreqFlyRemark[] getMatchingFreqFlier(final String aPassengerID) throws Exception
    {
    return( getMatchingFreqFlier(aPassengerID,null) );
    }


  public PNRFreqFlyRemark[] getMatchingFreqFlier(final String aPassengerID, final String aCarrier) throws Exception
    {
    // get all the remarks and the PNRNameElement for the given passenger
    final PNRRemark[] remarks = getRemarks();
    final PNRNameElement name = getName(aPassengerID);

    // find the frequent flier segements
    final Vector vFreqFly = new Vector();
    PNRFreqFlyRemark freqFly;
    if ( remarks instanceof PNRRemark[] )
      {
      for ( int i = 0; i < remarks.length; i++ )
        {
        if ( remarks[i] instanceof PNRFreqFlyRemark )
          {
          freqFly = (PNRFreqFlyRemark )remarks[i];
          if ( freqFlyMatch(freqFly,aCarrier,name) )
            vFreqFly.add(freqFly);
          }
        }  //end for
      } // end if

    if ( vFreqFly.size() > 0 )
      {
      final PNRFreqFlyRemark[] ff_array = new PNRFreqFlyRemark[ vFreqFly.size() ];
      vFreqFly.toArray(ff_array);
      return(ff_array);
      }
    else
      return(null);
    }


  private boolean freqFlyMatch(final PNRFreqFlyRemark aFreqFly, final String aCarrier, final PNRNameElement aName)
    {
    // test the carrier code
    if ( GnrcFormat.NotNull(aCarrier) )
      {
      if ( GnrcFormat.strEqual(aCarrier,aFreqFly.Carrier) == false )
        return(false);
      }

    // test the passenger association
    if ( aName instanceof PNRNameElement )
      {
      if ( GnrcFormat.strEqual(aName.LastName,aFreqFly.LastName) == false )
        return(false);
      if ( GnrcFormat.strEqual(aName.FirstName,aFreqFly.FirstName) == false )
        return(false);
      }

    return(true);
    }

  /**

   ***********************************************************************
   * This function returns an array of itinerary segment objects
   * referred to in the PNR data strings
   ***********************************************************************
   */
  /*
  public PNRItinSegment [] GetItin() throws Exception
    {
    final GnrcParsePNR parser = GetParser();
    return( parser.GetItin(this) );
    }
  */
  /** 
   ***********************************************************************
   * This function returns an array of header lines
   * referred to in the PNR data strings
   ***********************************************************************
   */
  /*
  public String [] GetHeader() throws Exception
    {
    final GnrcParsePNR parser = GetParser();
    return( parser.GetHeader(this) );
    }
  */
  /** 
   ***********************************************************************
   * This function returns the locator
   * referred to in the PNR data strings
   ***********************************************************************
   */
  /*
  public String GetLocator()
    {
    if ( record_locator instanceof String )
      {
      if ( record_locator.length() == 0 )
        record_locator = null;
      }

    if ( (record_locator instanceof String) == false )
      {
      try
        {
        GnrcParsePNR parser = GetParser();
        record_locator = parser.GetLocator(this);
        }
      catch (Exception e)
        {
        return("");
        }
      }

    return(record_locator);
    }
  */

  /** 
   ***********************************************************************
   * Access function for the Crs Code
   ***********************************************************************
   */
  public String getCrs()
    {
    return(CrsCode);
    }

  public void setCrs(final String aCrsCode)
    {
    CrsCode = aCrsCode;
    }

  /** 
   ***********************************************************************
   * Access function for the locator
   ***********************************************************************
   */
  public String getLocator()
    {
    return(record_locator);
    }

  public void setLocator(final String aLocator)
    {
    record_locator = aLocator;
    }

  /** 
   ***********************************************************************
   * Access function for the pseudocity
   ***********************************************************************
   */
  public String getPseudoCity()
    {
    return(PseudoCity);
    }

  public void setPseudoCity(final String aPseudoCity)
    {
    PseudoCity = aPseudoCity;
    }

  /**
   ***********************************************************************
   * Access function for the agent sign
   ***********************************************************************
   */
  public String getAgentSign()
    {
    return(AgentSign);
    }

  public void setAgentSign(final String aAgentSign)
    {
    AgentSign = aAgentSign;
    }

  /**
   ***********************************************************************
   * Access function for the customer account number
   ***********************************************************************
   */
  public String getCustAcctNumber()
    {
    return(CustAcctNumber);
    }

  public void setCustAcctNumber(final String aCustAcctNumber)
    {
    CustAcctNumber = aCustAcctNumber;
    }

  /**
   ***********************************************************************
   * Access function for the read date
   ***********************************************************************
   */
  public long getReadDate()
    {
    return(ReadDate);
    }

  public void setReadDate()
    {
    ReadDate = System.currentTimeMillis();
    }

  public void setReadDate(final long aReadDate)
    {
    ReadDate = aReadDate;
    }

  /**
   ***********************************************************************
   * Access function for the raw PNR data
   ***********************************************************************
   */
  public String[] getRawPNRData()
    {
    return(RawPNRData);
    }

  public void setRawPNRData(final String[] aRawPNRData)
    {
    RawPNRData = aRawPNRData;
    }

  /**
   ***********************************************************************
   * Access function for the filtered PNR data
   ***********************************************************************
   */
  public String getPNRData()
    {
    return(PNRData);
    }

  public void setPNRData(final String aPNRData)
    {
    PNRData = aPNRData;
    }

  /**
   ***********************************************************************
   * Fare routines
   ***********************************************************************
   */
  public void clearFares()
    {
    if ( FareList instanceof Vector )
      FareList.clear();
    }


  public void addFare(final PNRFare aNewFare)
    {
    if ( (FareList instanceof Vector) == false )
      FareList = new Vector();

    if ( FareList instanceof Vector )
      FareList.add(aNewFare);
    }


  public void setFares(final PNRFare[] aNewFares)
    {
    clearFares();
    if ( aNewFares instanceof PNRFare[] )
      {
      for ( int i = 0; i < aNewFares.length; i++ )
        addFare(aNewFares[i]);
      }
    }

  /**
   * This method returns an array of the {@link PNRFare} objects stored in
   * the FareList vector
   */
  public PNRFare[] getFares()
    {
    if ( FareList instanceof Vector )
      {
      if ( FareList.size() > 0 )
        {
        final PNRFare[] fareArray = new PNRFare[ FareList.size() ];
        FareList.toArray(fareArray);
        return(fareArray);
        }
      }

    return(null);
    }


  public PNRFare getLowestFare(final String aGenericPTC)
    {
    final PNRFare[] fareArray = getFares();
    if ( fareArray instanceof PNRFare[] )
      {
      for ( int i = 0; i < fareArray.length; i++ )
        {
        if ( fareArray[i].isLowest && fareArray[i].getGenericPTC().equals(aGenericPTC) )
          return(fareArray[i]);
        }
      }

    return(null);
    }

  public PNRFare getContractFare(final String aGenericPTC)
    {
    final PNRFare[] fareArray = getFares();
    if ( fareArray instanceof PNRFare[] )
      {
      for ( int i = 0; i < fareArray.length; i++ )
        {
        if ( fareArray[i].isContract && fareArray[i].getGenericPTC().equals(aGenericPTC) )
          return(fareArray[i]);
        }
      }

    return(null);
    }

  public PNRFare getCoachFare(final String aGenericPTC)
    {
    final PNRFare[] fareArray = getFares();
    if ( fareArray instanceof PNRFare[] )
      {
      for ( int i = 0; i < fareArray.length; i++ )
        {
        if ( fareArray[i].isCoach() && fareArray[i].getGenericPTC().equals(aGenericPTC) )
          return(fareArray[i]);
        }
      }

    return(null);
    }

  /** 
   ***********************************************************************
   * segment routines
   ***********************************************************************
   */
  public void clearSegments()
    {
    if ( SegList instanceof Vector )
      SegList.clear();
    }


  public void addSegment(final PNRItinSegment aNewSegment)
    {
    if ( (SegList instanceof Vector) == false )
      SegList = new Vector();

    if ( SegList instanceof Vector )
      SegList.add(aNewSegment);
    }


  public void setSegments(final PNRItinSegment[] aNewSegments)
    {
    clearSegments();
    if ( aNewSegments instanceof PNRItinSegment[] )
      {
      for ( int i = 0; i < aNewSegments.length; i++ )
        addSegment(aNewSegments[i]);
      }
    }


  public PNRItinSegment[] getSegments()
    {
    if ( SegList instanceof Vector )
      {
      if ( SegList.size() > 0 )
        {
        final PNRItinSegment[] segments = new PNRItinSegment[ SegList.size() ];
        SegList.toArray(segments);
        return(segments);
        }
      }

    return(null);
    }

  /** 
   ***********************************************************************
   * remark routines
   ***********************************************************************
   */
  public void clearRemarks()
    {
    if ( RmkList instanceof Vector )
      RmkList.clear();
    }

  public void addRemark(final PNRRemark aNewRemark)
    {
    if ( (RmkList instanceof Vector) == false )
      RmkList = new Vector();

    setRemarkNameFields(aNewRemark);
    setRemarkSegmentFields(aNewRemark);

    if ( RmkList instanceof Vector )
      RmkList.add(aNewRemark);
    }


  public void addRemarks(final PNRRemark[] aNewRemarks)
    {
    if ( aNewRemarks instanceof PNRRemark[] )
      {
      for ( int i = 0; i < aNewRemarks.length; i++ )
        addRemark(aNewRemarks[i]);
      }
    }


  public void setRemarks(final PNRRemark[] aNewRemarks)
    {
    clearRemarks();
    addRemarks(aNewRemarks);
    }


  /**
   ***********************************************************************
   * This method returns an array of the <code>PNRRemark</code> objects 
   * stored in the {@link #RmkList} vector.
   ***********************************************************************
   */
  public PNRRemark[] getRemarks()
    {
    if ( RmkList instanceof Vector )
      {
      if ( RmkList.size() > 0 )
        {
        final PNRRemark[] remarkArray = new PNRRemark[ RmkList.size() ];
        RmkList.toArray(remarkArray);
        return(remarkArray);
        }
      }

    return(null);
    }


  /**
   ***********************************************************************
   * This method returns an array of the <code>PNRRemark</code> objects
   * stored in the {@link #RmkList} vector that are associated with the 
   * <code>PNRNameElement</code> supplied.
   ***********************************************************************
   */
  public PNRRemark[] getRemarks(final PNRNameElement aName)
    {
    if ( (RmkList instanceof Vector) && (aName instanceof PNRNameElement) )
      {
      if ( RmkList.size() > 0 )
        {
        // look at each remark and figure out if its assigned to the given name
        PNRRemark remark;
        PNRNameElement remarkName;
        final Vector AssocRmkList = new Vector();

        for ( int i = 0; i < RmkList.size(); i++ )
          {
          remark = (PNRRemark )RmkList.elementAt(i);
          if ( remark instanceof PNRRemark )
            {
            remarkName = remark.getAssocName(this);
            if ( aName.equals(remarkName) )
              AssocRmkList.add(remark);
            }
          }

        // convert vector list into an array
        if ( AssocRmkList.size() > 0 )
          {
          final PNRRemark[] remarkArray = new PNRRemark[ AssocRmkList.size() ];
          AssocRmkList.toArray(remarkArray);
          return(remarkArray);
          }
        }
      }

    return(null);
    }


  /**
   ***********************************************************************
   * This method returns an array of the <code>PNRRemark</code> objects
   * stored in the {@link #RmkList} vector that are associated with the 
   * segment identified by the line number supplied.
   ***********************************************************************
   */
  public PNRRemark[] getRemarks(final int aSegmentNum)
    {
    if ( RmkList instanceof Vector )
      {
      if ( RmkList.size() > 0 )
        {
        // look at each remark and figure out if its assigned to the given segment
        PNRRemark remark;
        final Vector AssocRmkList = new Vector();

        for ( int i = 0; i < RmkList.size(); i++ )
          {
          remark = (PNRRemark )RmkList.elementAt(i);
          if ( remark instanceof PNRRemark )
            {
            if ( remark.ItinSegment == aSegmentNum )
              AssocRmkList.add(remark);
            }
          }

        // convert vector list into an array
        if ( AssocRmkList.size() > 0 )
          {
          final PNRRemark[] remarkArray = new PNRRemark[ AssocRmkList.size() ];
          AssocRmkList.toArray(remarkArray);
          return(remarkArray);
          }
        }
      }

    return(null);
    }


  /**
   ***********************************************************************
   * This method returns an array of the <code>PNRRemark</code> objects
   * stored in the {@link #RmkList} vector that are associated with both 
   * the <code>PNRNameElement</code> supplied and the segment identified 
   * by the line number supplied.
   ***********************************************************************
   */
  public PNRRemark[] getRemarks(final PNRNameElement aName, final int aSegmentNum)
    {
    if ( (RmkList instanceof Vector) && (aName instanceof PNRNameElement) )
      {
      if ( RmkList.size() > 0 )
        {
        // look at each remark and figure out if its assigned to the given name
        PNRRemark remark;
        PNRNameElement remarkName;
        final Vector AssocRmkList = new Vector();

        for ( int i = 0; i < RmkList.size(); i++ )
          {
          remark = (PNRRemark )RmkList.elementAt(i);
          if ( remark instanceof PNRRemark )
            {
            remarkName = remark.getAssocName(this);
            if ( aName.equals(remarkName) && (remark.ItinSegment == aSegmentNum) )
              AssocRmkList.add(remark);
            }
          }

        // convert vector list into an array
        if ( AssocRmkList.size() > 0 )
          {
          final PNRRemark[] remarkArray = new PNRRemark[ AssocRmkList.size() ];
          AssocRmkList.toArray(remarkArray);
          return(remarkArray);
          }
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * This method calls {@link getRemarks()} and determines whether the
   * <code>PNRRemark</code> passed matches any of the remarks contained
   * in the Passenger Name Record; the matching criteria varies according
   * to the type of remark passed - see note below.
   * <p>
   * Note that this method does <b>not</b> use the <code>equals()</code> method
   * of the corresponding <code>PNRRemark</code> subclass because when using
   * the the Amadeus APIv2 remark, the remark added and the remark returned may
   * not be exactly the same, and hence the method herewith has 'relaxed'
   * matching criteria. </p>
   * <p>
   * For example, when adding an SSR with a 'YY' carrier code (meaning all
   * airlines) the Amadeus APIv2 creates as many SSR remarks as there are
   * segments, each with the segment's carrier code. In such case, the method
   * herewith does not attempt to match the carrier code for a SSR remark, but
   * merely determines that there is at least one SSR remark in the PNR which
   * matches the remark text and SSR service code. </p>
   ***********************************************************************
   */
  public boolean hasRemark(PNRRemark aRemark)  
    {
    PNRRemark[] remarks = this.getRemarks();

    if (!(remarks instanceof PNRRemark[]))
      return false;

    String sRmkType = aRemark.getRemarkType();
    String sRmkText = aRemark.getRemarkText();

    for (int i=0; i < remarks.length; i++)
      {
      if ( remarks[i].getRemarkType().equals(sRmkType) &&
           remarks[i].getRemarkText().indexOf(sRmkText) >= 0)
        {
        if ( sRmkType.equals("SSR") ) 
          {
          if( ((PNRSsrRemark)remarks[i]).Code.equals(((PNRSsrRemark)aRemark).Code) )
              //&& ((PNRSsrRemark)remarks[i]).Carrier.equals(((PNRSsrRemark)aRemark).Carrier) )
          return true;
          }
        else if ( sRmkType.equals("OSI") )
          {
          if ( ((PNROsiRemark)remarks[i]).Carrier.equals(((PNROsiRemark)aRemark).Carrier) )
          return true;
          }
        else
          return true;
        }

      } // end for

    return false;
    } // end hasRemark

  /** 
   ***********************************************************************
   * Adds a Header Remark to this Passenger Name Record object
   ***********************************************************************
   */
  public void addHeaderRemark(final String aHeaderText)
    {
    // count the number of header remarks already in the list
    int iNumHeaderRemarks = 0;
    if ( RmkList instanceof Vector )
      {
      Object remark;
      for ( int i = 0; i < RmkList.size(); i ++ )
        {
        remark = RmkList.elementAt(i);
        if ( remark instanceof PNRHeaderRemark )
          iNumHeaderRemarks++;
        }
      }

    final PNRHeaderRemark newRemark = new PNRHeaderRemark(aHeaderText);
    newRemark.MessageNumber = iNumHeaderRemarks + 1;
    addRemark(newRemark);
    }

  public void addSsrRemark(final String aCode)
    {
    final PNRSsrRemark newRemark = new PNRSsrRemark(aCode);
    addRemark(newRemark);
    }

  public void addSsrRemark(final String aCode, final String aCarrier)
    {
    final PNRSsrRemark newRemark = new PNRSsrRemark(aCode,aCarrier);
    addRemark(newRemark);
    }


  /** 
   ***********************************************************************
   * fill in name assignment info
   ***********************************************************************
   */
  private void setRemarkNameFields(final PNRRemark aRemark)
    {
    // find the corresponding name element
    try
      {
      if ( (aRemark.FamilyNumber > 0) && (aRemark.MemberNumber > 0) )
        {
        final PNRNameElement name = getName(aRemark.FamilyNumber,aRemark.MemberNumber);
        if ( name instanceof PNRNameElement )
          {
          aRemark.NameNumber   = getPsgrNum(name);
          aRemark.FirstName    = name.FirstName;
          aRemark.LastName     = name.LastName;
          aRemark.setPsgrID(name.getPassengerID());
          }
        }
      else if ( GnrcFormat.NotNull(aRemark.LastName) && GnrcFormat.NotNull(aRemark.FirstName) )
        {
        final PNRNameElement name = getName(aRemark.LastName,aRemark.FirstName);
        if ( name instanceof PNRNameElement )
          {
          aRemark.FamilyNumber = getFamilyNum(name);
          aRemark.MemberNumber = getMemberNum(name);
          aRemark.NameNumber   = getPsgrNum(name);
          aRemark.FirstName    = name.FirstName;
          aRemark.LastName     = name.LastName;
          aRemark.setPsgrID(name.getPassengerID());
          }
        }
      else if ( aRemark.NameNumber > 0 )
        {
        final PNRNameElement name = getName(aRemark.NameNumber);
        if ( name instanceof PNRNameElement )
          {
          aRemark.FamilyNumber = getFamilyNum(name);
          aRemark.MemberNumber = getMemberNum(name);
          aRemark.FirstName    = name.FirstName;
          aRemark.LastName     = name.LastName;
          aRemark.setPsgrID(name.getPassengerID());
          }
        }
      else if ( GnrcFormat.NotNull(aRemark.getPsgrID()) )
        {
        final PNRNameElement name = getName(aRemark.getPsgrID());
        if ( name instanceof PNRNameElement )
          {
          aRemark.FamilyNumber = getFamilyNum(name);
          aRemark.MemberNumber = getMemberNum(name);
          aRemark.NameNumber   = getPsgrNum(name);
          aRemark.FirstName    = name.FirstName;
          aRemark.LastName     = name.LastName;
          }
        }
      }
    catch (Exception e)
      {}


    // set all the name ID fields

    }

  /** 
   ***********************************************************************
   * fill in segment assignment info
   ***********************************************************************
   */
  private void setRemarkSegmentFields(final PNRRemark aRemark)
    {
    // find the corresponding itinerary segment
    try
      {
      if ( aRemark.ItinSegment > 0 )
        {
        // set the carrier and flight number fields if needed
        final PNRItinSegment seg = getItinSegment(aRemark.ItinSegment);
        if ( seg instanceof PNRItinAirSegment )
          {
          final PNRItinAirSegment AirSeg = (PNRItinAirSegment )seg;

          if ( aRemark instanceof PNRSsrRemark )
            ((PNRSsrRemark )aRemark).Carrier = AirSeg.Carrier;
          else if ( aRemark instanceof PNROsiRemark )
            ((PNROsiRemark )aRemark).Carrier = AirSeg.Carrier;
          else if ( aRemark instanceof PNRSeatRemark )
            {
            ((PNRSeatRemark )aRemark).Carrier   = AirSeg.Carrier;
            ((PNRSeatRemark )aRemark).FlightNum = AirSeg.FlightNumber;
            }

          }
        }
      else if ( aRemark instanceof PNRSeatRemark )
        {
        final PNRSeatRemark seatRemark = (PNRSeatRemark )aRemark;
        if ( GnrcFormat.NotNull(seatRemark.Carrier) && (seatRemark.FlightNum != 0) )
          {
          // set the itin segment number if needed
          final PNRItinSegment seg = getItinSegmentByFlight(seatRemark.Carrier,seatRemark.FlightNum);
          if ( (seg instanceof PNRItinSegment) && (aRemark.ItinSegment == 0) )
            aRemark.ItinSegment = seg.SegmentNumber;
          }
        }

      }
    catch (Exception e)
      {}

    // set all the air segment fields
    }

  /** 
   ***********************************************************************
   * error routines
   ***********************************************************************
   */
  public void clearErrors()
    {
    if ( ErrList instanceof Vector )
      ErrList.clear();
    }


  /**
   ***********************************************************************
   * this method adds a <code>PNRError</code> to the {@link #ErrList} vector,
   * initialized with the error String passed; if the  <code>ErrList</code> 
   * vector does not already exist, it creates a new one.
   ***********************************************************************
   */
  public void addError(final String aNewError)
    {
    if ( (ErrList instanceof Vector) == false )
      ErrList = new Vector();

    if ( ErrList instanceof Vector )
      ErrList.add( new PNRError(aNewError));
    }

  /**
   ***********************************************************************
   * this method adds a <code>PNRError</code> to the {@link #ErrList} vector; 
   * if the  <code>ErrList</code> vector does not already exist, 
   * it creates a new one.
   ***********************************************************************
   */
  public void addError(final PNRError aNewError)
    {
    if ( (ErrList instanceof Vector) == false )
      ErrList = new Vector();

    if ( ErrList instanceof Vector )
      ErrList.add(aNewError);
    }


  /**
   ***********************************************************************
   * this method adds an array of <code>PNRError</code> objects to the 
   * {@link #ErrList} vector, by calling {@link addError} successively
   ***********************************************************************
   */
  public void addErrors(final PNRError[] aryPnrErrors)
    {
    if ( (ErrList instanceof Vector) == false )
      ErrList = new Vector();

    if ( ErrList instanceof Vector )
      {
      for (int i=0; i < aryPnrErrors.length; i++)
        {
        ErrList.add(aryPnrErrors[i]);
        }
      }
    }


  /**
   ***********************************************************************
   * clears the errors currently in the {@link #ErrList} vector, and populates 
   * the <code>ErrList</code> with the  with strings contained in the 
   * String array passed
   ***********************************************************************
   */
  public void setErrors(final String[] aNewErrors)
    {
    clearErrors();
    if ( aNewErrors instanceof String[] )
      {
      for ( int i = 0; i < aNewErrors.length; i++ )
        addError(aNewErrors[i]);
      }
    }

  /**
   ***********************************************************************
   * Returns an array of strings describing the errors encountered while
   * parsing the Passenger Name Record (PNR).
   * <p>
   * Prior to version 1.4.0 of the TranServer, this vector contained only
   * String objects. While implementing the Amadeus API it became necessary
   * to store the Errors returned in a PNR in a more structured fashioned.
   * To this end, the {@link PNRError} class was created.</p>
   * <p>
   * To preserve backward compatibility, the ErrList vector now may contain
   * either <code>String</code> objects or <code>PNRError</code> objects,
   * although it should not contain both at the same time.</p>
   * <p>
   * Nonetheless, just in case, this method assumes that either can be
   * present. On the other hand, for later implementations the function 
   * {@link #getPNRErrors()} should be used instead of this function.</p>
   *
   * @returns 
   *  An array of the <code>String</code> objects contained in the 
   *  {@link #ErrList} vector. In the event that the vector
   *  contains <code>PNRError</code> objects, an array of strings of the
   *  {@link PNRError#nativeError} strings contained in those objects.
   ***********************************************************************
   */
  public String[] getErrors()
    {
    if ( ErrList instanceof Vector )
      {
      if ( ErrList.size() > 0 )
        {
        final String[] errorArray = new String[ ErrList.size() ];
        for (int i=0; i < ErrList.size(); i++)
          {
          if (ErrList.elementAt(i) instanceof String)
            errorArray[i] = (String)ErrList.elementAt(i);
          if (ErrList.elementAt(i) instanceof PNRError)
            errorArray[i] = ( (PNRError)ErrList.elementAt(i) ).nativeError;
          } // end for
        return(errorArray);
        }
      }

    return(null);

    } // end getErrors

  /**
   ***********************************************************************
   * This method returns all the errors found in the PNR, concatenated 
   * in one single string separated by pipes ('|'); this method was written
   * for the implementation of the Amadeus API in order to be able to return
   * multiple errors to Airware in the event that multiple errors are parsed
   * in the Amadeus response; this method is a temporary fix until the Airware 
   * response structures can be modified to accept multiple errors; see
   * {@link getErrors} for more detail on PNR error handling.
   ***********************************************************************
   */
  public String getErrorsConcatenated()
    {
    if ( ErrList instanceof Vector )
      {
      if ( ErrList.size() > 0 )
        {
        String sErr = ""; 
        for (int i=0; i < ErrList.size(); i++)
          {
          if (ErrList.elementAt(i) instanceof String)
            sErr += (String)ErrList.elementAt(i) + " | ";
          if (ErrList.elementAt(i) instanceof PNRError)
            sErr += ( (PNRError)ErrList.elementAt(i) ).nativeError + " | ";
          } // end for
        //strip the last pipe
        if (sErr instanceof String && sErr.length() > 3)
          sErr = sErr.substring(0,sErr.length()-3);

        return(sErr);
        }
      }

    return("No errors stored in PNR");

    } // end getErrorsConcatenated

  /**
   ***********************************************************************
   * Returns an array of {@link PNRError} objects describing the errors 
   * encountered while parsing the Passenger Name Record (PNR).
   * <p>
   * See the comments in {@link #getErrors()} above for the genesis of this
   * function. Unlike the <code>getErrors()</code> method, this method
   * assumes that all the objects contained in the vector {@link #ErrList}
   * contain objects of type <code>PNRError</code>.</p>
   *
   * @since 1.4.0
   ***********************************************************************
   */
  public PNRError[] getPNRErrors()
    {
    if ( ErrList instanceof Vector )
      {
      if ( ErrList.size() > 0 )
        {
        final PNRError[] errorArray = new PNRError[ ErrList.size() ];
        ErrList.toArray(errorArray);
        return(errorArray);
        }
      }

    return(null);
    }


  /**
   ***********************************************************************
   * Returns a count of the errors currently stored in {@link #ErrList}
   ***********************************************************************
   */
  public int getErrorsCount()  
    {
    if (ErrList instanceof Vector)
     return ErrList.size();
    else
      return(0);

    } // end getErrorCount


  /**
   ***********************************************************************
   * This method clears all the Families (Passenger Groups) associated to
   * this Passenger Name Record, by clearing the vector {@link #FamList}
   ***********************************************************************
   */
  public void clearFamilies()
    {
    if ( FamList instanceof Vector )
      FamList.clear();
    }


  public void addFamily(final PNRFamilyElement aNewFamily)
    {
    if ( (FamList instanceof Vector) == false )
      FamList = new Vector();

    if ( FamList instanceof Vector )
      FamList.add(aNewFamily);
    }


  public void setFamilies(final PNRFamilyElement[] aNewFamilies)
    {
    clearFamilies();
    if ( aNewFamilies instanceof PNRFamilyElement[] )
      {
      for ( int i = 0; i < aNewFamilies.length; i++ )
        addFamily(aNewFamilies[i]);
      }
    }


  public PNRFamilyElement[] getFamilies()
    {
    if ( FamList instanceof Vector )
      {
      if ( FamList.size() > 0 )
        {
        final PNRFamilyElement[] familyArray = new PNRFamilyElement[ FamList.size() ];
        FamList.toArray(familyArray);
        return(familyArray);
        }
      }

    return(null);
    }

  /** 
   ***********************************************************************
   * This method is used to clear all Passenger Names from a PNR; it does
   * so by calling {@link clearFamilies()}.
   ***********************************************************************
   */
  public void clearNames()
    {
    clearFamilies();
    }


  /**
   ***********************************************************************
   * This method is used to add a Passenger Name to an existing Family of
   * Passengers; it searches for a {@link PNRFamilyElement} that has the same
   * Last Name as the {@link PNRNameElement} that is being added; if no 
   * PNRFamilyElement with the same Last Name is found, the name is not added.
   *
   * @see PNR#FamList
   * @see PNR.getFamilies()
   * @see PNRFamilyElement
   * @see PNRFamilyElement#FamilyMembers
   * @see PNRNameElement
   ***********************************************************************
   */
  public void addName(final PNRNameElement aNewName)
    {
    final PNRFamilyElement[] families = getFamilies();

    // find first family that has same last name
    if ( families instanceof PNRFamilyElement[] )
      {
      String sLastName;
      for ( int i = 0; i < families.length; i++ )
        {
        sLastName = families[i].getLastName();
        if ( aNewName.LastName.equals(sLastName) )
          families[i].addMember(aNewName);
        }
      }
    }


  public void setNames(final PNRNameElement[] aNewNames)
    {
    clearNames();
    if ( aNewNames instanceof PNRNameElement[] )
      {
      for ( int i = 0; i < aNewNames.length; i++ )
        addName(aNewNames[i]);
      }
    }


  /**
   ***********************************************************************
   * This method returns an array of {@link PNRNameElement} objects which
   * contain information on all the passengers who are on this PNR.
   * <p>
   * To do so, this method first calls the method {@link getfamilies()} 
   * to retrieve an array of {@link PNRFamilyElement} objects associated 
   * with this PNR; in turn, each of those <code>PNRFamilyElement</code> 
   * references an array of  {@link PNRNameElement} objects, which denote every
   * passenger belonging to that family group; the method iterates over
   * those two arrays to arrive at an array of all the passengers on this
   * PNR.
   *
   * @see PNR#FamList
   * @see PNR.getFamilies()
   * @see PNRFamilyElement
   * @see PNRFamilyElement#FamilyMembers
   * @see PNRNameElement
   ***********************************************************************
   */
  public PNRNameElement[] getNames()
    {
    final PNRFamilyElement[] families = getFamilies();
    final Vector NameList = new Vector();

    // for every family
    if ( families instanceof PNRFamilyElement[] )
      {
      for ( int iFamNum = 0; iFamNum < families.length; iFamNum++ )
        {
        // for every member
        if ( families[iFamNum].FamilyMembers instanceof PNRNameElement[] )
          {
          for ( int iMemberNum = 0; iMemberNum < families[iFamNum].FamilyMembers.length; iMemberNum++ )
            NameList.add( families[iFamNum].FamilyMembers[iMemberNum] );
          }
        }
      }

    // convert vector of family member objects to an array
    if ( NameList.size() > 0 )
      {
      final PNRNameElement[] nameArray = new PNRNameElement[ NameList.size() ];
      NameList.toArray(nameArray);
      return(nameArray);
      }
    else
      return(null);
    }

  /**
   ***********************************************************************
   * returns true if the pnr contains an Air Segment which was sold from a
   * Managed Block (such as NegoSpace on Amadeus, BSG in Sabre, etc...)
   ***********************************************************************
   */
  public boolean hasManagedBlockAirSegment()
    {
      PNRItinAirSegment[] segs = this.getItinAirSegments();

      for (int i=0; i < segs.length ; i++)
        {
        if (segs[i].BlockType != null &&
            segs[i].BlockType.equals(xmax.crs.Block.MANAGED))
          return true;
        } 

      return false;
    } // end hasManagedBlockAirSegment

  /**
   ***********************************************************************
   * returns an array of air segments on this PNR that have been sold from a
   * Managed Block (such as NegoSpace on Amadeus, BSG in Sabre, etc...)
   ***********************************************************************
   */
  public PNRItinAirSegment[] getManagedBlockAirSegments()
    {
      PNRItinAirSegment[] segs = this.getItinAirSegments();
      ArrayList listBlockSegs = new ArrayList();

      for (int i=0; i < segs.length ; i++)
        {
        if (segs[i].BlockType != null &&
            segs[i].BlockType.equals(xmax.crs.Block.MANAGED))
          listBlockSegs.add(segs[i]);
        } 

      PNRItinAirSegment[] aryBlockSegs = new PNRItinAirSegment[listBlockSegs.size()];
      return (PNRItinAirSegment[])listBlockSegs.toArray(aryBlockSegs);
    } // end getManagedBlockAirSegment


 /** 
  ***********************************************************************
  * This function returns true if the PNR has any data in it
  ***********************************************************************
  */

  public boolean hasData()
    {
    if ( FamList instanceof Vector )
      {
      if ( FamList.size() > 0 )
        return(true);
      }
 
    if ( SegList instanceof Vector )
      {
      if ( SegList.size() > 0 )
        return(true);
      }
 
    if ( RmkList instanceof Vector )
      {
      if ( RmkList.size() > 0 )
        return(true);
      }
 
    return(false);
    }

 public boolean hasNoData()
   {
   return( !hasData() );
   }

} // end class PNR
