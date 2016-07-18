package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.crs.GnrcParser;
import xmax.util.xml.DOMutil;
import org.w3c.dom.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlReader
{
 private static final SimpleDateFormat fmtXmlDate     = new SimpleDateFormat("yyyy-MM-dd");
 private static final SimpleDateFormat fmtXmlTime     = new SimpleDateFormat("HH:mm:ss");
 private static final SimpleDateFormat fmtXmlDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  /**
   ***********************************************************************
   * This method reads a string request and calls the appropriate method,
   * the method thus called
   * returns a request object that corresponds to the string request.
   *
   * @param aStringInput   The client request passed in string form
   * @param aCrs           A computer reservation system
	 *
   * @return               A request object via a call to one of the
   *                       methods in this class
	 *
	 * @see xmax.crs.Generic.GnrcConvControl
   ***********************************************************************
   */

  public static ReqTranServer getRequestObject(final String aInputString, final GnrcCrs aCrs) throws Exception
    {
    final Document doc = DOMutil.stringToDom(aInputString);
    return( getRequestObject(doc,aCrs) );
    }


  public static ReqTranServer getRequestObject(final Document aDoc, final GnrcCrs aCrs) throws Exception
    {
    NodeList nodes;

    nodes = aDoc.getElementsByTagName("GetPnr");
    if ( nodes instanceof NodeList )
      {
      if ( nodes.getLength() > 0 )
        return( reqGetPNR( (Element )nodes.item(0), aCrs ) );
      }

    nodes = aDoc.getElementsByTagName("ConnectDetailsRQ");
    if ( nodes instanceof NodeList )
      {
      if ( nodes.getLength() > 0 )
        return( reqGetStatus( (Element )nodes.item(0), aCrs ) );
      }

    nodes = aDoc.getElementsByTagName("AirDetailsRQ");
    if ( nodes instanceof NodeList )
      {
      if ( nodes.getLength() > 0 )
        return( reqGetFlifo( (Element )nodes.item(0), aCrs ) );
      }

    nodes = aDoc.getElementsByTagName("EndSession");
    if ( nodes instanceof NodeList )
      {
      if ( nodes.getLength() > 0 )
        return( new ReqEndSession("") );
      }

    throw new TranServerException("Command unrecognized");
    }

  /**
   ***********************************************************************
   * generated by {@link GnrcConvControl#GET_FLIFO_CMD}, this method returns
   * an object request to get connection information
   *
   * @param aInputString
   *  a fixed-width string request, formatted as follows:
   *
   ***********************************************************************
   */
  private static ReqGetStatus reqGetStatus(final Element aRootNode, final GnrcCrs aCrs) throws Exception
    {
    String sCrsCode = aRootNode.getAttribute("CrsCode");
    if ( GnrcFormat.IsNull(sCrsCode) )
      sCrsCode = getDefaultCrsCode(aCrs);

    // check fields
    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to get connection status information");

    // create request object
    final ReqGetStatus request = new ReqGetStatus(sCrsCode);
    request.commType  = request.COMM_XML;

    return(request);
    }


  /**
   ***********************************************************************
   * generated by {@link GnrcConvControl#GET_PNR_CMD}, this method returns
   * an object request to get a PNR by locator; note that a valid PNR must
   * exist in order to obtain a result.
   *
   * @param aInputString
   *  a fixed-width string request, formatted as follows:
   *
   * <pre><b>
   *   position  length           description</b>
   *    0 -   7      8     - the string 'CGETPNR'
   *    8 -   9      2     - a code denoting the CRS codes:
	 *                         1A=Amadeus  AA=Sabre  UA=Apollo/Galileo  1P=Worldspan
   *   10 -  17      8     - a Passenger Name Record (PNR) locator
   *   18 -  18      1     - a 'T' or 'F' flag (true or false) to denote whether
   *                         extended information should be retrieved for the PNR
   * </pre>
   ***********************************************************************
   */
  private static ReqGetPNR reqGetPNR(final Element aRootNode, final GnrcCrs aCrs) throws Exception
    {
    // read fields
    final String sCrsCode     = DOMutil.getTagValue(aRootNode,"CrsCode");
    final String sPseudoCity  = DOMutil.getTagValue(aRootNode,"PseudoCity");
    final String sLocator     = DOMutil.getTagValue(aRootNode,"Locator");
    final String sQueueName   = DOMutil.getTagValue(aRootNode,"Queue");
    final String sExtended    = DOMutil.getTagValue(aRootNode,"ExtendedInfo");
    final String sStoredFares = DOMutil.getTagValue(aRootNode,"StoredFares");

    // check fields
    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to pull PNR from");

    if ( GnrcFormat.IsNull(sLocator) && GnrcFormat.IsNull(sQueueName) )
      throw new TranServerException("Must specify a locator or queue name for get PNR");

    // create request object
    final ReqGetPNR request = new ReqGetPNR(sCrsCode);

    request.commType  = request.COMM_XML;
    request.Locator   = sLocator;
    request.QueueName = sQueueName;
    if ( GnrcFormat.NotNull(sExtended) )
      {
      if ( sExtended.toUpperCase().startsWith("T") || sExtended.toUpperCase().startsWith("Y") )
        request.ExtendedInfo = true;
      }
    if ( GnrcFormat.NotNull(sStoredFares) )
      {
      if ( sStoredFares.toUpperCase().startsWith("T") || sStoredFares.toUpperCase().startsWith("Y") )
        request.GetStoredFares = true;
      }


    return(request);
    }


  /**
   ***********************************************************************
   * generated by {@link GnrcConvControl#GET_FLIFO_CMD}, this method returns
   * an object request to get flight information;
   *
   * @param aInputString
   *  a fixed-width string request, formatted as follows:
   *
   ***********************************************************************
   */
  private static ReqGetFlifo reqGetFlifo(final Element aRootNode, final GnrcCrs aCrs) throws Exception
    {
    // read fields
    final String sCrsCode = getDefaultCrsCode(aCrs);
    final String sCarrier = aRootNode.getElementsByTagName("SpecificVendorID").item(0).getFirstChild().getNodeValue();
    final String sFlight  = aRootNode.getElementsByTagName("FlightNumber").item(0).getFirstChild().getNodeValue();
    final String sDepCity = aRootNode.getAttribute("OriginAirportID");
    final String sArrCity = aRootNode.getAttribute("DestinationAirportID");
    final String sDepDate = aRootNode.getElementsByTagName("DepartureDate").item(0).getFirstChild().getNodeValue();

    final int iFlight;
    try
      {
      iFlight = Integer.parseInt(sFlight);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid flight number " + sFlight);
      }

    final String sCrsDepDate;
    try
      {
      final long iDepDate = scanDateStr(sDepDate);
      sCrsDepDate         = GnrcFormat.FormatCRSDate(iDepDate);
      }
    catch (Exception e)
      {
      throw new TranServerException("Invalid departure date " + sDepDate);
      }

    // check fields
    if ( GnrcFormat.IsNull(sCrsCode) )
      throw new TranServerException("Must specify a CRS code to get flight information");

    if ( GnrcFormat.IsNull(sCarrier) )
      throw new TranServerException("Must specify a carrier code to get flight information");

    if ( GnrcFormat.IsNull(sFlight) )
      throw new TranServerException("Must specify a flight number to get flight information");

    if ( GnrcFormat.IsNull(sDepDate) )
      throw new TranServerException("Must specify a departure date to get flight information");

    // create request object
    final ReqGetFlifo request = new ReqGetFlifo(sCrsCode,sCarrier,iFlight,sCrsDepDate);
    request.commType  = request.COMM_XML;

    if ( GnrcFormat.NotNull(sDepCity) )
      request.DepCity = sDepCity;

    if ( GnrcFormat.NotNull(sArrCity) )
      request.ArrCity = sArrCity;

    return(request);
    }

  /**
   ***********************************************************************
   * This function scans a date string in the xml date/time format and returns a
   * long corresponding to that date and time
   ***********************************************************************
   */
  private static long scanDateTimeStr(final String aDateTimeString) throws Exception
    {
    try
      {
      final Date fDate = fmtXmlDateTime.parse(aDateTimeString);
      final long ftime = fDate.getTime();
      return(ftime);
      }
    catch (Exception e)
      {
      throw new Exception("Unable to scan date/time string " + aDateTimeString);
      }
    }

  /**
   ***********************************************************************
   * This function scans a date string in the xml date/time format and returns a
   * long corresponding to that date and time
   ***********************************************************************
   */
  private static long scanDateStr(final String aDateString) throws Exception
    {
    try
      {
      final Date fDate = fmtXmlDate.parse(aDateString);
      final long ftime = fDate.getTime();
      return(ftime);
      }
    catch (Exception e)
      {
      throw new Exception("Unable to scan date string " + aDateString);
      }
    }

  /**
   ***********************************************************************
   * returns the default CRS code
   ***********************************************************************
   */
  private static String getDefaultCrsCode(final GnrcCrs aCrs)
    {
    // determine if you are currently connected, if so, use that as the default
    if ( aCrs instanceof GnrcCrs )
      return( aCrs.getHostCode() );

    // look up the default CRS code from the configuration
    final String sDefaultHost = ConfigTranServer.application.getProperty("defaultHostCode","AA");
    return( sDefaultHost );
    }

}