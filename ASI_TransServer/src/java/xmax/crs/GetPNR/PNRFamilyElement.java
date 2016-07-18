package xmax.crs.GetPNR;

import java.io.Serializable;

/**
 ***********************************************************************
 * This class holds information that applies to multiple family members
 * who may be booking a ticket together; in some Computer Reservation Systems
 * (CRS) terminals such as Sabre's, a family with multiple passengers may 
 * appear in one line on the Passenger Name Record (PNR).
 * 
 * @author   David Fairchild
 * @author   Philippe Paravicini
 * @version  1.x Copyright (c) 1999
 *
 * @see PNRNameElement
 * @see PNR
 ***********************************************************************
 */
public class PNRFamilyElement implements Serializable
{
 /** 
  * This field contains the last name of a group of passengers travelling 
  * under the same surname (presumably a family), or it can also contain
  * the name of a Corporate Header
  */
 private String LastName;

 /** 
  * the raw screen-scraping or xml data from where the information on 
  * this family element was parsed
  */
 public String RawData;
 public int FamilyNumber;
 /** 
  * this array contains all the individual passengers who are travelling
  * under the same family name or group header
  */
 public PNRNameElement[] FamilyMembers;

  // /** 
  //  * This flag indicates that this element is actually a corporate or group 
  //  * header; group headers are represented by PNRFamilyElement objects because 
  //  * they appear in the name section of a Passenger Name Record (PNR) but 
  //  * ideally they should be represented by other distinct fields in the 
  //  * {@link PNR} object.
  //  */
  // public boolean isCorpHeader;
  //
  // /** indicates that this element is a Block Sale Group (BSG) header */
  // public boolean isBSGHeader;
  //
  // // that said, the following fields only apply to corporate headers
  //

  /** the number of seats used by this family - this field used to also store
   * the number of seats allocated to a group, it has been preserved for now
   * for ApolloParseCrs compatibility */
  private int GroupNumSeats;
 
  // /** 
  //  * The number of seats that have actually been booked for this group; 
  //  * this should really be a field calculated at the level of the 
  //  * Passenger Name Record (PNR), by calling {@link getNumSeats}
  //  * for each family in the {@link PNR#FamList} vector, adding the 
  //  * total number of seats for each family, and subtracting this number
  //  * from {@link #GroupNumSeats}; nevertheless, Sabre provides this 
  //  * number on their PNR, and are using this field to store that number.
  //  */
  // public int NumBooked;              

 /** 
  ***********************************************************************
  * Constructors
  ***********************************************************************
  */
 public PNRFamilyElement()
   {
   }

 public PNRFamilyElement(final PNRNameElement[] aNames)
   {
   FamilyMembers = aNames;
   LastName      = getLastName();
   //GroupNumSeats = getNumSeats();
   }

 public PNRFamilyElement(final PNRNameElement aName)
   {
   FamilyMembers    = new PNRNameElement[1];
   FamilyMembers[0] = aName;
   LastName         = getLastName();
   //GroupNumSeats    = getNumSeats();
   }

 
// /**
//  ***********************************************************************
//  * Instantiates this <code>PNRFamilyElement</code> as a Corporate Header
//  ***********************************************************************
//  */
// public PNRFamilyElement(final int aNumBooked)
//   {
//   NumBooked    = aNumBooked;
//   isCorpHeader = true;
//   }

 /** 
  ***********************************************************************
  * adds a member by creating a {@link PNRNameElement} element and adding
  * it to the {@link #FamilyMembers} array
  ***********************************************************************
  */
 public void addMember(final String aFirstName, final String aPTC,
                       final String aPsgrID, final String aInfantName)
   {
   final PNRNameElement newName = new PNRNameElement();

   newName.LastName    = getLastName();
   newName.FirstName   = aFirstName;
   newName.PTC         = aPTC;
   newName.setPassengerID(aPsgrID);
   newName.InfantName  = aInfantName;
   newName.NumSeats    = 1;

   addMember(newName);
   }

 /**
  ***********************************************************************
  * adds a <code>PNRNameElement</code> to the {@link #FamilyMembers} array
  ***********************************************************************
  */
 public void addMember(final PNRNameElement aName)
   {
   // determine the size of the new array
   final int iOldSize;
   if ( FamilyMembers instanceof PNRNameElement[] )
     iOldSize = FamilyMembers.length;
   else
     iOldSize = 0;

   // allocate the new array
   final PNRNameElement[] newNameArray = new PNRNameElement[ iOldSize + 1 ];

   // copy the existing array
   for ( int i = 0; i < iOldSize; i++ )
     newNameArray[i] = FamilyMembers[i];

   // add the new member
   newNameArray[ newNameArray.length - 1] = aName;

   // point to the new array
   FamilyMembers = newNameArray;
   }

 /** 
  ***********************************************************************
  * returns the number of seats used by the entire family by adding the 
  * {@link PNRNameElement.NumSeats} fields of each element in the 
  * {@link FamilyMembers} array; if this number is not greater than zero
  * then return the field {@link GroupNumSeats}
  ***********************************************************************
  */
 public int getNumSeats()
   {
   int iNumSeats = 0;

   if ( FamilyMembers instanceof PNRNameElement[] )
     {
     for ( int i = 0; i < FamilyMembers.length; i++ )
       iNumSeats += FamilyMembers[i].NumSeats;
     }
   
   return(iNumSeats);
//
//   if ( iNumSeats > 0 )
//     return(iNumSeats);
//   else
//     return(GroupNumSeats);
   }

 /** 
  ***********************************************************************
  * return the number of members in the family
  ***********************************************************************
  */
 public int getNumMembers()
   {
   if ( FamilyMembers instanceof PNRNameElement[] )
     return(FamilyMembers.length);
   else
     return(0);
   }

 /** 
  ***********************************************************************
  * returns one of the following, if found:
  * <ul>
  *  <li>the last name of the first {@link PNRNameElement} in the 
  *      {@link #FamilyMembers} array if such array contains elements</li> 
  *  <li>the string stored in the {@link LastName} field</li>
  *  <li>the empty string, if none of the two above are found</li>
  * </ul>
  ***********************************************************************
  */
 public String getLastName()
   {
   if ( FamilyMembers instanceof PNRNameElement[] )
     {
     if ( FamilyMembers.length > 0 )
       return(FamilyMembers[0].LastName);
     }

   if ( LastName instanceof String )
     return(LastName);
   else
     return("");
   }

 /** 
  ***********************************************************************
  * The setter method for the field GroupNumSeats; this used to
  * denote the number of fields that have been allocated to a Group (all
  * of which may not have been booked in the Passenger Name Record);
  * this method is preserved for backward compatibility because Apollo makes
  * use of it to store the number of family elements
  ***********************************************************************
  */
 public void setGroupNumSeats(final int aNumSeats)
   {
   GroupNumSeats = aNumSeats;
   }

// /**
//  ***********************************************************************
//  * The getter method for the field GroupNumSeats
//  ***********************************************************************
//  */
// public int getGroupNumSeats()  
//   {
//   return(GroupNumSeats);
//   } 

/**
 ***********************************************************************
 * setter method for LastName field 
 ***********************************************************************
 */
 public void setLastName(final String aLastName)
   {
   LastName = aLastName;
   }

  /** 
   ***********************************************************************
   * Returns the LastName field
   ***********************************************************************
   */
  public String toString()
    {
    return( LastName );
    }

}
