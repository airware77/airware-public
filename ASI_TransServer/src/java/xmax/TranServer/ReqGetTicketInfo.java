package xmax.TranServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xmax.crs.GnrcCrs;
import xmax.crs.PNR;
import xmax.crs.TicketInformation;

public class ReqGetTicketInfo extends ReqTranServer implements Serializable
{
	private final String locator;
	private final Set<String> passengerIDs;
	private PNR pnr;
	private final List<TicketInformation> ticketInformation;
	
	 /**
	  ***********************************************************************
	  * Constructor
	  ***********************************************************************
	  */
	 public ReqGetTicketInfo(final String aCrsCode, final String aLocator)
	 {
		 super(aCrsCode);
		 
		 this.locator = aLocator;
		 this.passengerIDs = new HashSet<String>();
		 this.ticketInformation = new ArrayList<TicketInformation>();
	 }


	public String getLocator()
	{
		return locator;
	}

	public Set<String> getPassengerIDs()
	{
		return passengerIDs;
	}

	public PNR getPnr()
	{
		return pnr;
	}

	public void setPnr(PNR pnr)
	{
		this.pnr = pnr;
	}
	
	public List<TicketInformation> getTicketInformation()
	{
		return ticketInformation;
	}


	/**
	 * Get the name of a logfile to log to
	 */
	public String getLogFileName(String aLogDirectory) throws Exception
	{
	    final StringBuffer sLogName = new StringBuffer();
	    if ( GnrcFormat.NotNull(aLogDirectory) )
	      sLogName.append(aLogDirectory);

	    sLogName.append("\\Locators\\");

	    // check input parms
	    if ( GnrcFormat.IsNull(getCrsCode()) )
	      throw new TranServerException("Cannot open log file for get ticket info.  Crs Code is null");

	    if ( GnrcFormat.IsNull(locator) )
	      throw new TranServerException("Cannot open log file for get ticket info.  locator is null");

	    sLogName.append(getCrsCode() + locator + ".log");

	    return( sLogName.toString() );
	}

	
	@Override
	public void runRequest(GnrcCrs crs) throws Exception
	{
		crs.getTicketInfo(this);
	}
	
}
