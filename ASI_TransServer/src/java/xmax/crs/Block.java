package xmax.crs;

import xmax.crs.Flifo.FlightInfo;
import xmax.crs.GetPNR.PNRNameElement;

import java.util.List;
import java.util.Properties;
import java.io.Serializable;

/**
 ***********************************************************************
 * <p>This class is used to represent an inventory 'Block' of airline flights
 * existing in one of the Computer Reservation Systems (CRS); although each CRS
 * treats block differently, this class is meant to be used generically
 * to store flight segment information for the different blocks; some of the
 * idiosyncrasies of the different CRS blocks are described below.</p>
 * <p><ul>
 *  <li>Sabre can store multi-flight segments (married segments) in one block,
 *      which are to be sold together as a unit</li>
 *  <li>Amadeus can only store one flight segment per block; it handles married
 *      segments by providing the ability to 'link' blocks together</li>
 *  <li>Worldspan provides the ability to create a 'Wigger'.  A wigger contains
 *      a list of unrelated segments, each of which is sold separately, and
 *      each of which has its own inventory properties (such as expiration
 *      date). A wigger can be used, for example, all inventory for a given
 *      month.</li>
 * </ul></p>
 *
 * @author   Philippe Paravicini
 * @version  $Revision: 11$ - $Date: 12/02/2002 7:02:52 PM$
 *
 * @see xmax.TranServer.ReqBlockBuild
 * @see xmax.TranServer.ReqBlockDelete
 * @see xmax.TranServer.ReqBlockModify
 * @see xmax.TranServer.ReqBlockRetrieve
 * @see BlockFlight
 ***********************************************************************
 */
public class Block implements Serializable
{
  private String  crsCode;
  private String  pseudoCityCode;
  private String  tourName;
  private String  tourReference;
  private String  authCode;
	private String  ownerId;
	private String  handlingTbl;
  private List    flightList;
  private long    startSellDate;
  private long    stopSellDate;
  private long    reductionDate;
  private byte    reductionPercent;
  private boolean isActive;

  /** stores crs specific properties */
  public Properties crsProperty = new Properties();

  /**
   * Identifies segments that are part of managed blocks; for example,
   * Negospace in Amadeus, BSG records in Sabre, or Wiggers in Worldspan
   */
  public static final String MANAGED = "MNG";

  /** Identifies segments that are regular passive airline blocks */
  public static final String REGULAR = "REG";

  /**
   * string used as to indicate that we were not able to parse the locator
   * of a segment sold from a managed block space
   */
  public static final String LOCTR_NOT_FOUND = "NOTFOUND";

  /** Constructor: provides empty instance of a Block */
  public Block() {}

  /** constructor: characterizes the block with the CRS code */
  public Block(final String aCrsCode) { crsCode = aCrsCode; }


  /*
   ***********************************************************************
   * get functions
   ***********************************************************************
   */

  /** returns the code of the CRS in which this block resides */
  public String getCrsCode() { return(crsCode); }

  /** returns the PseudoCityCode (travel office) to which this block belongs */
  public String getPseudoCityCode() { return(pseudoCityCode); }

  /** returns the name of the tour attached to this block */
  public String getTourName()  { return(tourName); }

  /** returns a reference that can be attached to this block */
  public String getTourReference()  { return(tourReference); }

  /** returns an authorization code for this block */
  public String getAuthCode()  { return(authCode); }

	/** returns an owner id for this block */
  public String getOwnerID()  { return(ownerId); }

	/** returns a handling table for this block */
  public String getHandlingTable()  { return(handlingTbl); }

  /** returns the date on which this block may start being sold */
  public long   getStartSellDate() { return(startSellDate); }

  /** returns the date on which this block may no longer be sold */
  public long   getStopSellDate() { return(stopSellDate); }

  /** returns the date on which the allocation of this block will be reduced */
  public long   getReductionDate() { return(reductionDate); }

  /** returns the percent by which the allocation of this block will be reduced */
  public byte   getReductionPercent() { return(reductionPercent); }

  /** specifies whether this is an active or passive block */
  public boolean isActive() { return(isActive); }

  /** returns the number of {@link BlockFlight}s in this Block */
  public int getNumFlights()
    {
    if ( flightList instanceof List )
      return( flightList.size() );
    else
      return(0);
    }

  /** given an index, returns the corresponding {@link BlockFlight} */

  public BlockFlight getFlight(final int aIndex)
    {
    if ( flightList instanceof List )
      return( (BlockFlight )flightList.get(aIndex) );
    else
      throw new IndexOutOfBoundsException( "Unable to get block flight index " + aIndex + " flight list not created");
    }

  /** returns an array of the {@link BlockFlight} objects in this Block */
  public BlockFlight[] getFlightList()
    {
    if ( flightList instanceof List )
      return((BlockFlight[] )flightList.toArray(new BlockFlight[flightList.size()]) );
    else
      return(null);
    }

  /*
   ***********************************************************************
   * set functions
   ***********************************************************************
   */

  /** sets the Computer Reservation System (CRS) in which this block resides */
  public void setCrsCode(final String aCrsCode) { crsCode = aCrsCode;}

  /** sets the PseudoCityCode (office/branch ID) where this block resides */
  public void setPseudoCityCode(final String aPseudoCityCode) { pseudoCityCode = aPseudoCityCode; }

  /** sets the tour name attached to this block */
  public void setTourName(final String aMemo) { tourName  = aMemo; }

  /** references this block to a tour or other travel event */
  public void setTourReference(final String aMemo) { tourReference  = aMemo; }

  /** sets the authorization code for this block */
  public void setAuthCode(final String sAuth) { authCode  = sAuth; }

	/** sets the owner id for this block */
  public void setOwnerID(final String sOwner_Id) { ownerId  = sOwner_Id; }

	/** sets the handling table for this block */
  public void setHandlingTable(final String sHandling_Tbl) { handlingTbl  = sHandling_Tbl; }

  /** sets the date on which this block may start being sold */
  public void setStartSellDate(final long aStartSellDate) { startSellDate  = aStartSellDate; }

  /** sets the date past which this block may no longer be sold */
  public void setStopSellDate(final long aStopSellDate) { stopSellDate   = aStopSellDate; }

  /** sets the date on which the allocation of this block will be reduced */
  public void setReductionDate(final long aReductionDate) { reductionDate = aReductionDate; }

  /** sets the percentage by which the allocation of this block will be reduced */
  public void setReductionPercent(final byte aPrcnt) { reductionPercent = aPrcnt; }

  /** specify whether this is an Active or Passive block */
  public void setActive(final boolean trueOrFalse) { isActive = trueOrFalse; }

  /** adds a flight to the list of flights in this Block */
  public void addFlight(final BlockFlight aFlight)
    {
    if ( (flightList instanceof List) == false )
      flightList = new java.util.ArrayList();

    if ( flightList.contains(aFlight) == false )
      flightList.add(aFlight);
    }


} // end class Block
