package xmax.crs;

import xmax.crs.Flifo.FlightInfo;
import xmax.crs.Flifo.FlightSegment;

/**
 ***********************************************************************
 * This class represents a Block Message returned from Block message queue
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 10/25/2002 4:36:31 PM$
 *
 * @see Block
 ***********************************************************************
 */
public class BlockMessage
{
  /** indicates the type of action that was performed on this block */
  private String action;

  /** the block referenced in the message */
  private Block block;

  /** plain vanilla empty constructor */
  public BlockMessage() {}

  /** initializes message with a block containing a single block flight */
  public BlockMessage(String crsCode) 
    {
    setBlock(new Block(crsCode));
    getBlock().addFlight(new BlockFlight());
    getBlock().getFlight(0).setFlightInfo(new FlightInfo());
    getBlock().getFlight(0).getFlightInfo().addFlightSegment(new FlightSegment());
    }

  /** sets the type of action that was performed on this block */
  public String getAction() {return action;}

  /** returns the type of action that was performed on this block */
  public void setAction(String sAction) {action = sAction;}

  /** set the block to which this message refers*/
  public Block getBlock() {return block;}

  /** retrieve the block to which this message refers*/
  public void setBlock(Block aBlock) {block = aBlock;}

} // end class BlockMessage
