package xmax.crs.GetPNR;

import xmax.crs.PNR;
import xmax.TranServer.GnrcFormat;
import java.io.Serializable;

public abstract class PNRRemark implements Serializable
{
 // public String RemarkType;

 /** The free form text contained in the Remark */
 public String RemarkText;

 /**
  * corresponds to the line number of the Remark when viewing the
  * Passenger Name Record (PNR) through a Terminal Address display
  */
 public int MessageNumber;

 /**
  * This field is used to in the event that a
  * Computer Reservation System uses an internal remark identifier
  * (such as is the case for the Amadeus API); in effect, this field
  * replaces/duplicates the function of the field {@link #MessageNumber}
  */
 public String CrsMessageID;

 /**
  * used to associate the Remark to a Segment on the Passenger Name Record
  * (PNR); corresponds to the line number of the associated Segment when
  * viewing the Passenger Namer Record (PNR) through a Terminal Address display
  */
 public int ItinSegment;

 /**
  * This field is used to identify an associated Segment in the event that a
  * Computer Reservation System uses an internal segment identifier
  * (such as is the case for the Amadeus API); in effect, it replaces/duplicates
  * the function of the field {@link #ItinSegment}; it refers to the field
  * {@link PNRItinSegment.#CrsSegmentID}
  */
 public String CrsSegmentRef;

 /** the Last Name of the Passenger that may be associated to this Remark */
 public String LastName;

 /** the First Name of the Passenger that may be associated to this Remark */
 public String FirstName;

 /**
  * The passenger number with whom this Remark is associated, when counting all
  * passengers; this number uniquely identifies a Passenger within
  * a Passenger Name Record (PNR).
  */
 public int NameNumber;

 /**
  * The FamilyNumber with whom this Remark is associated; this number
  * identifies groupings of passengers that are entered
  * together in one entry of a Passenger Name Record (PNR); the FamilyNumber,
  * along with the MemberNumber, uniquely identify a passenger within a PNR.
  */
 public int FamilyNumber;

 /**
  * The number of a passenger with whom this Remark is associated,
  * when counting within a Family grouping; the FamilyNumber,
  * along with the MemberNumber, uniquely identify a passenger within a PNR.
  */
 public int MemberNumber;

 /**
  * This field is used in the event that a Computer Reservation System uses an
  * internal passenger identifier to refer to a passenger association
  * (such as is the case for the Amadeus API); in effect, this field
  * replaces/duplicates the function of the fields {@link #NameNumber},
  * {@link #FamilyNumber}, and {@link #MemberNumber}; it refers to the field
  * {@link PNRNameElement.#CrsPsgrID}.
  */
 public String CrsPsgrRef;

 /** 
  * This field stores any code that may be assigned by the CRS to distinguish
  * one remark type from another
  */
 public String TypeCode;
  
 /**
  * This field is used for the Airware passengerID for associating
  * remarks
  */
 private String PsgrID;

 // common remark types
 public static final String ADDRESS_REMARK      = "ADRS";
 public static final String FOP_REMARK          = "FOP";
 public static final String PHONE_REMARK        = "PHNE";
 public static final String OSI_REMARK          = "OSI";
 public static final String SSR_REMARK          = "SSR";
 public static final String TICKET_REMARK       = "TKT";

 // Airware only remark type constants
 public static final String AWR_GENERAL_REMARK  = "GENE";
 public static final String HEADER_REMARK       = "HEAD";
 public static final String AWR_INVOICE_REMARK  = "INVO";
 public static final String AWR_ITIN_REMARK     = "ITIN";

 // eItin only remark type constants
 public static final String GENERAL_REMARK      = "GENERAL";
 public static final String INVOICE_REMARK      = "INVOICE";
 public static final String ITINERARY_REMARK    = "ITINERARY";
 public static final String POCKET_ITIN_REMARK  = "PKT_ITIN";
 public static final String HOME_ADDR_REMARK    = "HM_ADDR";
 public static final String WORK_ADDR_REMARK    = "WK_ADDR";
 public static final String BILL_ADDR_REMARK    = "BL_ADDR";
 public static final String DEL_ADDR_REMARK     = "DL_ADDR";
 public static final String AGENCY_ADDR_REMARK  = "AG_ADDR";
 public static final String HOME_PHONE_REMARK   = "HM_PHONE";
 public static final String WORK_PHONE_REMARK   = "WK_PHONE";
 public static final String AGENCY_PHONE_REMARK = "AG_PHONE";
 public static final String FREEFORM_REMARK     = "FREEFORM";
 public static final String FREQFLY_REMARK      = "FREQFLY";

 // 
 
 /** used to indicate that remark applies to all carriers */
 public static final String ALL_CARRIERS        = "YY";

 public abstract String getRemarkType();

  /**
   ***********************************************************************
   * Returns the passenger ID of the passenger associated with this remark,
   * including any Group ID
   ***********************************************************************
   */
  public String getPsgrID()
    {
    if (GnrcFormat.IsNull(PsgrID))
      return "";
    else
      return PsgrID;
    } // end getPsgrID

  /** Sets the PassengerID of the passenger associated with this remark */
  public void setPsgrID(String id)
    {
    PsgrID = id;
    } // end getPsgrID

  /**
   ***********************************************************************
   * Compares the first 9 characters of the PassengerID of the remark passed to
   * the PassengerID of this remark to determine whether they are associated
   * with the same passenger - note that the PassengerID may be longer than 9
   * characters but any subsequent characters (such as the GroupID) are ignored
   * for purposes of this comparison
   ***********************************************************************
   */
  public boolean hasSamePassengerID(PNRRemark aRemark)
    {
    if (this.getPsgrID().length() < 9 || aRemark.getPsgrID().length() < 9)
      return this.getPsgrID().equals(aRemark.getPsgrID());
    else
      return this.getPsgrID().substring(0,9).equals(
                aRemark.getPsgrID().substring(0,9));
    } // end hasSamePassengerID


  /**
   ***********************************************************************
   * This method returns the RemarkText field, or the empty string, if
   * such field is null
   ***********************************************************************
   */
  public String getRemarkText()
    {
    if (this.RemarkText instanceof String)
      return this.RemarkText;
    else
      return("");
    }

 /**
  ***********************************************************************
  * returns the associated itinerary segment if assigned
  ***********************************************************************
  */
 public String getAssocSegmentDesc(final PNR aPNR)
   {
   final PNRItinSegment its = getAssocSegment(aPNR);

   final String sResult;
   if ( its instanceof PNRItinAirSegment )
     sResult = ((PNRItinAirSegment )its).getItinDesc();
   else if ( its instanceof PNRItinHotelSegment )
     sResult = ((PNRItinHotelSegment )its).getItinDesc();
   else if ( its instanceof PNRItinCarSegment )
     sResult = ((PNRItinCarSegment )its).getItinDesc();
   else if ( its instanceof PNRItinArunkSegment )
     sResult = ((PNRItinArunkSegment )its).getItinDesc();
   else if ( its instanceof PNRItinSegment )
     sResult = its.getItinDesc();
   else
     sResult = "";

   return(sResult);
   }


  /**
   ***********************************************************************
   * returns the associated segment, if assigned
   ***********************************************************************
   */
  public PNRItinSegment getAssocSegment(final PNR aPNR)
    {
    if ( aPNR instanceof PNR )
      {
      try
        {
        final PNRItinSegment its = aPNR.getItinSegment(ItinSegment);
        return(its);
        }
      catch (Exception e)
        {}
      }
  
    return(null);
    }

 /** 
  ***********************************************************************
  * returns the associated name, if assigned
  ***********************************************************************
  */
 public String getAssocNameDesc(final PNR aPNR)
   {
   final PNRNameElement name = getAssocName(aPNR);
   if ( name instanceof PNRNameElement )
     {
     final String sResult = name.getFullName();
     return(sResult);
     }
   else
     return("");
   }



 public PNRNameElement getAssocName(final PNR aPNR)
   {
   PNRNameElement name = null;

   if ( aPNR instanceof PNR )
     {
     try
       {
       if ( GnrcFormat.NotNull(FirstName) && GnrcFormat.NotNull(LastName) )
         name = aPNR.getName(LastName,FirstName);
       else if ( (FamilyNumber > 0) && (MemberNumber > 0) )
         name = aPNR.getName(FamilyNumber,MemberNumber);
       else if ( NameNumber > 0 )
         name = aPNR.getName(NameNumber);
       }
     catch (Exception e)
       {}
     }

   return(name);
   }

 /**
  ***********************************************************************
  * returns a string of the form "[remarkType] - [remarkText]" (less the
  * brackets)
  ***********************************************************************
  */
 public String toString()
   {
   return( getRemarkType() + " - " + RemarkText );
   }

 /**
  ***********************************************************************
  * Remarks are equal if the remark text is the same and the associations
  * are the same
  ***********************************************************************
  */
 public boolean equals(final PNRRemark aRemark)
   {
   if ( getClass().getName().equals(aRemark.getClass().getName()) == false )
     return(false);

   if ( GnrcFormat.strEqual(RemarkText,aRemark.RemarkText) == false )
     return(false);

   if ( isSameSegmentAssociation(aRemark) == false )
     return(false);

   if ( isSameNameAssociation(aRemark) == false )
     return(false);

   return(true);
   }

 /**
  ***********************************************************************
  * Returns true if the remark is associated
  ***********************************************************************
  */
 public boolean isSameNameAssociation(final PNRRemark aRemark)
   {
   if ( isNameAssociated() == aRemark.isNameAssociated() )
     {
     if ( isNameAssociated() == false )
       return(true);

     if ( isSamePsgrNum(aRemark) || isSameFamilyMember(aRemark) || 
          isSameName(aRemark) || isSamePsgrID(aRemark) )
       return(true);
     }

   return(false);
   }

 private boolean isSamePsgrNum(final PNRRemark aRemark)
   {
   if ( (NameNumber == aRemark.NameNumber) && (NameNumber > 0) )
     return(true);
   else
     return(false);
   }

 private boolean isSameFamilyMember(final PNRRemark aRemark)
   {
   if ( (FamilyNumber == aRemark.FamilyNumber) && (FamilyNumber > 0) &&
        (MemberNumber == aRemark.MemberNumber) && (MemberNumber > 0) )
     return(true);
   else
     return(false);
   }


 private boolean isSameName(final PNRRemark aRemark)
   {
   if ( GnrcFormat.strEqual(LastName,aRemark.LastName) && GnrcFormat.NotNull(LastName) &&
        GnrcFormat.strEqual(FirstName,aRemark.FirstName) && GnrcFormat.NotNull(FirstName) )
     return(true);
   else
     return(false);
   }

 private boolean isSamePsgrID(final PNRRemark aRemark)
   {
   if ( GnrcFormat.strEqual(PsgrID,aRemark.PsgrID) && GnrcFormat.NotNull(PsgrID) )
     return(true);
   else
     return(false);
   }


 /**
  ***********************************************************************
  * Returns true if the remark is associated
  ***********************************************************************
  */
 protected boolean isSameSegmentAssociation(final PNRRemark aRemark)
   {
   if ( isSegmentAssociated() == aRemark.isSegmentAssociated() )
     {
     if ( isSegmentAssociated() == false )
       return(true);
     if ( ItinSegment == aRemark.ItinSegment )
       return(true);
     }

   return(false);
   }

 /**
  ***********************************************************************
  * Returns true if the remark is associated
  ***********************************************************************
  */
 public boolean isSegmentAssociated()
   {
   if ( ItinSegment > 0 )
     return(true);
   else
     return(false);
   }

 public boolean isNameAssociated()
   {
   if ( FamilyNumber > 0 )
     return(true);
   if ( MemberNumber > 0 )
     return(true);
   if ( NameNumber > 0 )
     return(true);
   if ( GnrcFormat.NotNull(LastName) )
     return(true);
   if ( GnrcFormat.NotNull(FirstName) )
     return(true);
   if ( GnrcFormat.NotNull(PsgrID) )
     return(true);

   return(false);
   }

 
} // end PNRRemark
