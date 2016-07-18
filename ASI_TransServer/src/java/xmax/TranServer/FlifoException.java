package xmax.TranServer;

/**
 ***********************************************************************
 * A class for general Flight Information Exceptions
 * 
 * @author   David Fairchild
 * @version  $Revision: 3$ - $Date: 10/04/2001 6:10:44 PM$
 ***********************************************************************
 */
public class FlifoException extends Exception
{
 private String FlifoResponse;
 private String ErrorCode;
 public static String CARRIER_NOT_SUPPORTED = "N";
 public static String FLIGHT_CANCELED       = "X";
 public static String FLIGHT_NOT_RECOGNIZED = "U";
 public static String FLIGHT_OPERATIONAL    = "O";
 public static String PARSING_ERROR         = "E";
 public static String CITY_NOT_RECOGNIZED   = "A";

  /** 
   ***********************************************************************
   * Constructors
   ***********************************************************************
   */
  public FlifoException(final String aMessage)
    {
    super(aMessage);

    ErrorCode = "E";
    }

  public FlifoException(final String aMessage, final String aErrorCode)
    {
    super(aMessage);

    ErrorCode = aErrorCode;
    }

  public FlifoException(final String aMessage, final String aErrorCode, final String aFlifoResponse)
    {
    super(aMessage);

    ErrorCode     = aErrorCode;
    FlifoResponse = aFlifoResponse;
    }

  /** 
   ***********************************************************************
   * Access functions
   ***********************************************************************
   */
  public String getErrorCode()
    {
    if ( ErrorCode instanceof String )
      return(ErrorCode);
    else
      return("");
    }

  public String getFlifoResponse()
    {
    if ( FlifoResponse instanceof String )
      return(FlifoResponse);
    else
      return("");
    }

}

// This Class is not used anywhere
///** 
// ***********************************************************************
// * Throw this exception when the user selects a carrier that the
// * Transaction server does not know how to parse
// ***********************************************************************
// */
//
//class FlifoCarrierException extends FlifoException
//
//{
//  public FlifoCarrierException(final String aFlifoResponse)
//    {
//    super("Transaction Server cannot parse the FLIFO response for the selected carrier",CARRIER_NOT_SUPPORTED,aFlifoResponse);
//    }
//
//  public FlifoCarrierException(final String aMessage, final String aFlifoResponse)
//    {
//    super(aMessage,CARRIER_NOT_SUPPORTED,aFlifoResponse);
//    }
//}

///** 
// ***********************************************************************
// * Throw this exception when CRS reports that the flight is canceled
// ***********************************************************************
// */
//
//class FlifoFlightCanceledException extends FlifoException
//
//{
//  public FlifoFlightCanceledException(final String aFlifoResponse)
//    {
//    super("CRS reports that the flight is canceled",FLIGHT_CANCELED,aFlifoResponse);
//    }
//
//  public FlifoFlightCanceledException(final String aMessage, final String aFlifoResponse)
//    {
//    super(aMessage,FLIGHT_CANCELED,aFlifoResponse);
//    }
//}

///** 
// ***********************************************************************
// * Throw this exception when CRS doesn't recognize the requested
// * carrier or flight number
// ***********************************************************************
// */
//
//class FlifoUnrecognizedException extends FlifoException
//
//{
//  public FlifoUnrecognizedException()
//    {
//    super("CRS does not recognize the requested flight",FLIGHT_NOT_RECOGNIZED,"");
//    }
//
//  public FlifoUnrecognizedException(final String aFlifoResponse)
//    {
//    super("CRS does not recognize the requested flight",FLIGHT_NOT_RECOGNIZED,aFlifoResponse);
//    }
//
//  public FlifoUnrecognizedException(final String aMessage, final String aFlifoResponse)
//    {
//    super(aMessage,FLIGHT_NOT_RECOGNIZED,aFlifoResponse);
//    }
//}

// This class is not used anywhere
///** 
// ***********************************************************************
// * Throw this exception when the Transaction Server generates a
// * parsing error
// ***********************************************************************
// */
//
//class FlifoParseException extends FlifoException
//
//{
//  public FlifoParseException(final String aMessage, final String aFlifoResponse)
//    {
//    super(aMessage,PARSING_ERROR,aFlifoResponse);
//    }
//}

///** 
// ***********************************************************************
// * Throw this exception when the requested flight does not serve
// * the requested city pair
// ***********************************************************************
// */
//
//class FlifoCityPairException extends FlifoException
//
//{
//  public FlifoCityPairException(final String aFlifoResponse)
//    {
//    super("Flight does not serve the selected city pair",CITY_NOT_RECOGNIZED,aFlifoResponse);
//    }
//
//  public FlifoCityPairException(final String aMessage, final String aFlifoResponse)
//    {
//    super(aMessage,CITY_NOT_RECOGNIZED,aFlifoResponse);
//    }
//}
