package xmax.TranServer;

/**
 ***********************************************************************
 * Throw this exception when the requested flight does not serve
 * the requested city pair
 * 
 * @author   David Fairchild
 * @version  $Revision: 1$ - $Date: 10/04/2001 6:10:07 PM$
 ***********************************************************************
 */
class FlifoCityPairException extends FlifoException

{
  public FlifoCityPairException(final String aFlifoResponse)
    {
    super("Flight does not serve the selected city pair",CITY_NOT_RECOGNIZED,aFlifoResponse);
    }

  public FlifoCityPairException(final String aMessage, final String aFlifoResponse)
    {
    super(aMessage,CITY_NOT_RECOGNIZED,aFlifoResponse);
    }
}
