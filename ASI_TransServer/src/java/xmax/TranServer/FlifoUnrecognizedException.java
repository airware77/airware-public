package xmax.TranServer;

/**
 ***********************************************************************
 * Throw this exception when CRS doesn't recognize the requested
 * carrier or flight number
 * 
 * @author   David Fairchild
 * @version  $Revision: 1$ - $Date: 10/04/2001 6:10:08 PM$
 ***********************************************************************
 */
class FlifoUnrecognizedException extends FlifoException
{
  public FlifoUnrecognizedException()
    {
    super("CRS does not recognize the requested flight",FLIGHT_NOT_RECOGNIZED,"");
    }

  public FlifoUnrecognizedException(final String aFlifoResponse)
    {
    super("CRS does not recognize the requested flight",FLIGHT_NOT_RECOGNIZED,aFlifoResponse);
    }

  public FlifoUnrecognizedException(final String aMessage, final String aFlifoResponse)
    {
    super(aMessage,FLIGHT_NOT_RECOGNIZED,aFlifoResponse);
    }
}
