package xmax.crs;

import xmax.crs.Flifo.FlightInfo;
import xmax.crs.GetPNR.PNRNameElement;

import java.io.Serializable;
import java.util.List;

/**
 ***********************************************************************
 * This class is used to store the information for flight segments that are to
 * be stored in a Computer Reservation System (CRS) {@link Block} inventory
 * record; this class as it stands was designed around an Amadeus NegoSpace
 * block; while many aspects of this class could be generic to blocks from
 * other CRSs, this class may have to be stripped from Amadeus specific fields,
 * and then subclassed to accomodate these fields.
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 9$ - $Date: 01/27/2003 8:07:23 PM$
 *
 * @see Block
 * @see FlightInfo
 * @see PNRNameElement
 ***********************************************************************
 */
public class BlockFlight implements Serializable
{
  private FlightInfo flightInfo;
  private String     classOfService;
  private int        numAllocated;
  private int        numSold;
  private int        numUnsold;
  private int        numPending;
  private int        numPendingUnsold;
  private String     allotmentStatus;
  private String     actionCode;
  private String     blockLocator;
  private String     crsFlightId;
  private String     remoteCarrierCode;
  private String     carrierLocator;
  private List       names; 
  private boolean    isScheduled;


  /** Constructor: creates an empty instance of a BlockFlight */
  public BlockFlight()
    {
    }

  /** Constructor: initializes a BlockFlight with the parameters passed */
  public BlockFlight(final FlightInfo aFlight, final String aClassOfService, final int aNumAllocated)
    {
    setFlightInfo(aFlight);
    setClassOfService(aClassOfService);
    setNumAllocated(aNumAllocated);
    }

  /*
   ***********************************************************************
   * get functions
   ***********************************************************************
   */

  /** returns the flight number of this BlockFlight */
  public FlightInfo getFlightInfo()    {   return(flightInfo);         }

  /** returns the Inventory Class of Service of this BlockFlight */
  public String     getClassOfService()       {   return(classOfService);        }

  /** returns the action code for this BlockFlight */
  public String     getActionCode()    {   return(actionCode);     }

  /** returns the number of seats (inventory) that have been allocated to this block */
  public int        getNumAllocated()  {   return(numAllocated);   }

  /** returns the number of seats that have been sold from this block */
  public int        getNumSold()       {   return(numSold);        }

  /** 
   * returns the number of seats that have been not been sold from this block;
   * this is mostly used to store an entry parsed in a Negospace queue message;
   * note that at this time it's not clear whether the number Unsold is equal
   * to the numAllocated minus numSold 
   */ 
  public int        getNumUnsold()       {   return(numUnsold);        }

  /** returns the number of seats pending confirmation from the airline */
  public int        getNumPending()    {   return(numPending);        }

  /** 
   * returns the number of seats that have not yet been sold and are pending
   * confirmation from the airline; this is mostly used to store an entry
   * parsed in a Negospace queue message;
   */
  public int        getNumPendingUnsold()    {   return(numPendingUnsold);        }

  /** 
   * When doing a retrieve block in Amadeus through the API, we are not
   * provided the number of seats pending for allocation (this is provided in
   * the cryptic or in an Ack message); we are provided an 'allotmentStatus'
   * indicator which indicates whether the block is active (A), pending
   * creation (C), pending for decrease (D), pending for increase (I),
   * not active (N), or deleted (X).
   */
  public String     getAllotmentStatus()    {   return(allotmentStatus);        }

  /** 
   * returns the CRS-assigned locator identifying the block to which this
   * BlockFlight belongs 
   */
  public String     getBlockLocator()    {   return(blockLocator);     }

  /** 
   * returns an identifier for this flight that is internal to the Computer
   * Reservation System; mostly needed to parse an entry in a Negospace queue
   * message
   */
  public String getCrsFlightId() { return(crsFlightId);}

  /** 
   * returns the code that identifies the reservation system in which the
   * carrierLocator referenced below originates
   */
  public String     getRemoteCarrierCode(){   return(remoteCarrierCode); }

  /** 
   * returns the locator that identifies this BlockFlight, presumably one
   * assigned by the Airline Carrier 
   */
  public String     getCarrierLocator(){   return(carrierLocator); }

  /** specifies whether or not this flight is scheduled */
  public boolean    isScheduled()      { return isScheduled; }


  /*
   ***********************************************************************
   * set procedures
   ***********************************************************************
   */

  /** sets the flight number of this BlockFlight */
  public void setFlightInfo(final FlightInfo aFlight)       {   flightInfo = aFlight;            }

  /** sets the Inventory Class of Service of this BlockFlight */
  public void setClassOfService(final String aClassOfService)         {   classOfService = aClassOfService;          }

  /** sets the action code for this BlockFlight */
  public void setActionCode(final String aActionCode)   {   actionCode = aActionCode;    }

  /** sets the number of seats (inventory) that have been allocated to this block */
  public void setNumAllocated(final int aNumAllocated)  {   numAllocated = aNumAllocated;}

  /** sets the number of seats that have been sold from this block */
  public void setNumSold(final int aNumSold)            {   numSold = aNumSold;          }

  /** 
   * sets the number of seats that have not been sold from this block;
   * this is mostly used to store an entry parsed in a Negospace queue message;
   * note that at this time it's not clear whether the number Unsold is equal
   * to the numAllocated minus numSold 
   */
  public void setNumUnsold(final int aNumUnsold)        {   numUnsold = aNumUnsold;          }

  /** sets the number of seats pending confirmation by the airline */
  public void setNumPending(final int aNumPending)      {   numPending = aNumPending;          }

  /** 
   * sets the number of seats that have not yet been sold and are pending
   * confirmation from the airline; this is mostly used to store an entry
   * parsed in a Negospace queue message;
   */
  public void setNumPendingUnsold(final int aNumPendingUnsold) { numPendingUnsold = aNumPendingUnsold;          }

  /** 
   * When doing a retrieve block in Amadeus through the API, we are not
   * provided the number of seats pending for allocation (this is provided in
   * the cryptic or in an Ack message); we are provided an 'allotmentStatus'
   * indicator which indicates whether the block is active (A), pending
   * creation (C), pending for decrease (D), pending for increase (I),
   * not active (N), or deleted (X); 
   */
  public void setAllotmentStatus(String status) {allotmentStatus = status;}


  /** 
   * sets the CRS-assigned locator identifying the block to which this
   * BlockFlight belongs
   */
  public void setBlockLocator(final String aLocator)      {   blockLocator = aLocator;       }

  /** 
   * sets the identifier for this flight that is internal to the Computer
   * Reservation System; mostly needed to parse an entry in a Negospace queue
   * message
   */
  public void setCrsFlightId(final String anId) {crsFlightId = anId;}


  /** 
   * sets the code that identifies the reservation system in which the
   * carrierLocator referenced below originates
   */
  public void setRemoteCarrierCode(final String aRmtCrsCode){ remoteCarrierCode = aRmtCrsCode;}

  /** 
   * sets the locator that identifies this BlockFlight, presumably one
   * assigned by the Airline Carrier 
   */
  public void setCarrierLocator(final String aLocator)   {   carrierLocator = aLocator;  }

  /** sets the isScheduled flight */
  public void setIsScheduled(final boolean bIsScheduled) {  isScheduled = bIsScheduled;  }

  /*
   ***********************************************************************
   * name procedures
   ***********************************************************************
   */

  /**
   * retrieves the list of passengers to whom flights on this BlockFlight have
   * been sold 
   */
  public PNRNameElement[] getNameList()
    {
    if ( names instanceof List )
      return( (PNRNameElement[] )names.toArray() );
    else
      return(null);
    }

  /** 
   * gets the number of passengers to whom inventory from this block has been
   * sold 
   */
  public int getNumNames()
    {
    if ( names instanceof List )
      return( names.size() );
    else
      return(0);
    }

  /** retrieves a passenger from the passenger list by index */
  public PNRNameElement getName(final int aIndex)
    {
    if ( names instanceof List )
      return( (PNRNameElement )names.get(aIndex) );
    else
      throw new IndexOutOfBoundsException("Unable to get name index " + aIndex + " name list not created");
    }
  
  /** 
   * adds a passengers to the list of passengers to whom a seat from
   * this block has been sold 
   */
   public void addName(final PNRNameElement aName)
     {
     if ( (names instanceof List) == false )
       names = new java.util.ArrayList();

     if ( names.contains(aName) == false )
       names.add(aName);
     }

  /** 
   * adds an array of passengers to the list of passengers to whom a seat from
   * this block has been sold 
   */
  public void setNames(final PNRNameElement[] aNames)
    {
    if ( names instanceof List )
      names.clear();

    if ( aNames instanceof PNRNameElement[] )
      {
      for ( int i = 0; i < aNames.length; i++ )
        addName(aNames[i]);
      }
    }

} // end class BlockFlight
