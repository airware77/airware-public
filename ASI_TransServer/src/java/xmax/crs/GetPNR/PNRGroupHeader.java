package xmax.crs.GetPNR;

/**
 ***********************************************************************
 * This class holds the information found in a Group Header within a Passenger
 * Name Record (PNR); this information typically appears within or 'near' the
 * passenger name section on a terminal emulation screen, and was originally
 * stored in the PNRFamilyElement object; nevertheless, while related to
 * passenger data, this information is used quite differently and warrants it's
 * own storage class.
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 10/15/2001 12:48:59 PM$
 *
 * @see PNR
 ***********************************************************************
 */
public class PNRGroupHeader
{

   /** The group name, or whatever text appears in the group header element */
  public String headerText;


  /** The line number within a terminal display */
  public int elementNumber;

  /** 
   * A Computer Reservation System (CRS) specific internal identifier; this
   * field is necessary to store the identifier for this element within the
   * AmadeusAPI interface, which stores both the elementNumber displayed on a
   * terminal, as well as an API specific ID.
   */
  public String crsElementID;

  /**
   * The total number of seats that are expected to be booked, eventually, in
   * the Passenger Name Record containing the group header.
   */
  public int numSeatsAllocated;

  /** the raw string from which this Group Header was parsed */
  public String rawData;

  /** The type of group, as defined by the static constants defined below. */
  public int groupType;

  public static final int NOT_SPECIFIED = -1;
  public static final int GROUP      = 0;
  public static final int CORPORATE  = 1;
  public static final int BLOCK_SALE = 2;


  /**
   ***********************************************************************
   * Creates an unitialized PNRGroupHeader
   ***********************************************************************
   */
  public PNRGroupHeader()
    {
    headerText        = "";
    numSeatsAllocated = -1;
    groupType         = NOT_SPECIFIED;
    elementNumber     = NOT_SPECIFIED;
    crsElementID      = "";
    } // end PNRGroupHeader

  /**
   ***********************************************************************
   * The constructor: takes a Group Header Name and a number of
   * seats allocated in order to create a group header;  we then initialize the
   * elementNumber to <code>-1</code>, the crsElementID to the empty string, and we default
   * the group type to <code>GROUP</code>.
   ***********************************************************************
   */
  public PNRGroupHeader(String text, String sQuantity)
    {
    headerText = text;
    numSeatsAllocated = Integer.parseInt(sQuantity);
    groupType = GROUP;
    elementNumber = -1;
    crsElementID = "";
    } // end PNRGroupHeader

  /**
   ***********************************************************************
   * The total number of seats that are expected to be booked, eventually, in
   * the Passenger Name Record containing the group header.
   ***********************************************************************
   */
  public int getNumSeatsAllocated()
    {
    return(numSeatsAllocated);
    } // end getNumSeatsAllocated()

  /**
   ***********************************************************************
   * sets the number of seats allocated to this Group.
   ***********************************************************************
   */
  public void setNumSeatsAllocated(int num)
    {
    numSeatsAllocated = num;
    } // end setNumSeatsAllocated

//  /**
//   ***********************************************************************
//   * The number of seats that have actually been booked for this group; 
//   * this should really be a field calculated at the level of the 
//   * Passenger Name Record (PNR), by calling {@link getNumSeats}
//   * for each family in the {@link PNR#FamList} vector, adding the 
//   * total number of seats for each family, and subtracting this number
//   * from {@link #numSeatsAllocated}; nevertheless, Sabre provides this 
//   * number on their PNR, and we are using this field to store that number.
//   ***********************************************************************
//   */
//  public int getNumSeatsBooked()
//    {
//    return(numSeatsBooked);
//    } // end getNumSeatsBooked
//
//  /**
//   ***********************************************************************
//   * Sets the number of seats booked in this Passenger Name Record
//   ***********************************************************************
//   */
//  public void setNumSeatsBooked(int num)  
//    {
//    numSeatsBooked = num;
//    } // end setNumSeatsBooked()

//  /**
//   ***********************************************************************
//   * returns the difference between the number of seats allocated to this
//   * group, and the number of seats that have actually been
//   * booked, provided that both of these values is other than -1; otherwise, it
//   * returns -1 to indicate that the calculation cannot be performed.
//   ***********************************************************************
//   */
//  public int numSeatsAvailable()
//    {
//    if (numSeatsAllocated == -1 || numSeatsBooked == -1)
//      return(-1);
//    else
//      return(numSeatsAllocated - numSeatsBooked);
//    } // end numSeatsAvailable

} // end class PNRGroupHeader

