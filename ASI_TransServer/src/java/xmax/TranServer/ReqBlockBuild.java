package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.Block;
import xmax.crs.BlockFlight;
import xmax.crs.Flifo.FlightInfo;
import xmax.crs.Flifo.FlightSegment;

import xmax.util.Log.AppLog;
import xmax.util.DateTime;

import java.io.Serializable;

/**
 ***********************************************************************
 * This class is used convey the command and the generic information necessary
 * to build a 'Block' of inventory in a Computer Reservation System (CRS); each
 * CRS has its own requirements and treats blocks differently.
 * 
 * @author   David Fairchild
 * @author   Philippe Paravicini
 * @version  $Revision: 8$ - $Date: 10/25/2002 4:34:58 PM$
 *
 * @see xmax.crs.Block
 * @see xmax.crs.BlockFlight
 ***********************************************************************
 */
public class ReqBlockBuild extends ReqTranServer implements Serializable
{

 /** contains all information needed to build the block */
 private Block block;

  /**
   ***********************************************************************
   * initializes the private block field with a crs code and a PseudoCity code
   * (office ID)
   ***********************************************************************
   */
  public ReqBlockBuild(final String aCrsCode)
    {
    super(aCrsCode);
    block = new Block(aCrsCode);
    }


  /**
   ***********************************************************************
   * Add flight segments to the block
   ***********************************************************************
   */
  public void addFlight(final String  aCarrier,
                        final int     aFlightNum,
                        final String  aDepCity,
                        final String  aArrCity,
                        final long    aDepDate,
                        final long    aArrDate,
                        final String  aClassOfService,
                        final int     aNumAllocated,
                        final String  aActionCode,
                        final String  aRmtCarrierCode,
                        final String  aCarrierLocator,
                        final boolean isScheduled)
    {
    // create the flight segment
    final FlightSegment seg = new FlightSegment();
    seg.Carrier          = aCarrier;
    seg.FlightNum        = aFlightNum;
    seg.DepartCity       = aDepCity;
    seg.ArriveCity       = aArrCity;
    seg.DepSchedDateTime = aDepDate;
    seg.ArrSchedDateTime = aArrDate;

    final FlightInfo flight = new FlightInfo(aCarrier,aFlightNum);
    flight.addFlightSegment(seg);

    final BlockFlight bflight = new BlockFlight(flight,aClassOfService,aNumAllocated);
    bflight.setActionCode(aActionCode);
    bflight.setRemoteCarrierCode(aRmtCarrierCode);
    bflight.setCarrierLocator(aCarrierLocator);
    bflight.setIsScheduled(isScheduled);

    block.addFlight(bflight);
    }

  /**
   ***********************************************************************
   * This procedure opens and closes a log file for tracking the request
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    AppLog.LogInfo("Building block",null,aCrs.getConnectionName());
    aCrs.blockBuild(block);
    AppLog.LogInfo("Created block with " + block.getNumFlights() + " flights",null,aCrs.getConnectionName());
    }

  /**
   ***********************************************************************
   * The log file of a ReqBlockBuild request is derived by concatenating the
   * crs code, the carrier code, the flight number, and the departure date of
   * the first flight in the flight list.
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    final StringBuffer sLogName = new StringBuffer();
    if ( GnrcFormat.NotNull(aLogDirectory) )
      sLogName.append(aLogDirectory);

    sLogName.append("\\Block\\");

    // check input parms
    if ( GnrcFormat.IsNull(block.getCrsCode()) )
      throw new TranServerException("Cannot open log file for creating block.  Crs Code is null");


    String sCarrier = "";
    String sDepDate = "";
    long   lDepDate   = 0;
    int    iFlightNum = 0;

    if (block.getNumFlights() > 0)
      {
      BlockFlight blockFlight = block.getFlight(0);
      sCarrier   = blockFlight.getFlightInfo().getCarrier();
      iFlightNum = blockFlight.getFlightInfo().getFlightNum();
      lDepDate   = blockFlight.getFlightInfo().getDepSchedDate();
      }

    String sFlightNum = (iFlightNum == 0) ? "": String.valueOf(iFlightNum);
    sDepDate = DateTime.fmtLongDateTime(lDepDate, "ddMMM");

    sLogName.append(
        block.getCrsCode() + sCarrier + sFlightNum + "-" + sDepDate + ".log");
       
    return( sLogName.toString() );
    }

  /** Get functions */
  public Block getBlock()  { return(block); }

} // end ReqBlockBuild
