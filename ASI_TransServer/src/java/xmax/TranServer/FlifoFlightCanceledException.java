package xmax.TranServer;

/**
 ***********************************************************************
 * Throw this exception when CRS reports that the flight is canceled
 * 
 * @author   David Fairchild
 * @version  $Revision: 1$ - $Date: 10/04/2001 6:10:07 PM$
 ***********************************************************************
 */
class FlifoFlightCanceledException extends FlifoException

{
  public FlifoFlightCanceledException(final String aFlifoResponse)
    {
    super("CRS reports that the flight is canceled",FLIGHT_CANCELED,aFlifoResponse);
    }

  public FlifoFlightCanceledException(final String aMessage, final String aFlifoResponse)
    {
    super(aMessage,FLIGHT_CANCELED,aFlifoResponse);
    }
}
