package xmax.crs;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;
import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;
import xmax.TranServer.GnrcFormat;
import java.io.Serializable;

/**
 ***********************************************************************
 * This class is used to store the fare for a given Passenger Name Record
 * (PNR) and a given Passenger Type Code (PTC).  This PNRFare object 
 * is in turn stored in the FareList vector in a PNR object.
 *
 * <p>The object stores the PTC, the number of passengers for that PTC, the
 * fare and taxes for each segment, and whether the fare returned is 
 * lowest, contract, or tax-exempt. If the fare returned is not lowest, 
 * contract, or tax-exempt, it is assumed to be a regular coach fare.
 *
 * <p>The segment fare information is stored in a Vector which contains
 * objects of the inner class LineItem. The LineItem object stores 
 * the number of passengers to whom the line item applies, a Description, 
 * an Amount for the number of passengers listed (stored in cents as a long),
 * and an int[] containing a list of the segments for which a fare was requested.
 * If the faring request does not include a list of segments, this array is
 * defaulted to the default value ALL_SEGMENTS ( {0} as of this writing).
 *
 * <p>The Tax information is also stored in a Vector which contains 
 * objects of type LineItem.
 *
 * @author   David Fairchild
 * @author   Philippe Paravicini
 * @version  1.x Copyright (c) 1999
 *
 * @see  LineItem
 * @see  PNR
 ***********************************************************************
 */

public class PNRFare implements Serializable
{

  /** Passenger Type Code (TPC) used by Airware (ADT, CHD, INF at this point) */
  private String GenericPTC;       

  /** Passenger Type Code (TPC) used by the Computer Reservation System host */
  private String NativePTC;        

  /** fare was returned by host as the lowest fare */
  public boolean isLowest;         

  /** fare is a contract fare */
  public boolean isContract;       

  /** fare is a tax exempt fare */
  public boolean isExempt;         

  /** 
   * contains a list of the fares (LineItem objects) for this PTC 
   * @see LineItem
 * @see #ALL_SEGMENTS
   */
  private final Vector FareList;   

  /** 
   * contains a list of the taxes (LineItem objects) for this PTC 
   * @see LineItem
 * @see #ALL_SEGMENTS
   */
  private final Vector TaxList;    

  /** 
 * An int array meant which indicates that no segment numbers
 * were passed in the Faring Request; it is presumed that this means that 
 * the faring information provided covers all the segments of the PNR.
 *
 * @see LineItem
 * @see #FareList
 * @see #TaxList
 */
  public final static int [] ALL_SEGMENTS = {0};

  
  /** the mostly unprocessed string returned by the host */
  private String RawData;


  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */

  public PNRFare(final String aNativePTC)
    {
    NativePTC  = aNativePTC;
    FareList = new Vector();
    TaxList  = new Vector();
    }

  public PNRFare(final String aGenericPTC, 
                 final String aNativePTC) 
    {
    GenericPTC = aGenericPTC;
    NativePTC  = aNativePTC;
    FareList = new Vector();
    TaxList  = new Vector();
    }

  /**
   ***********************************************************************
   * This method looks into the FareList Vector and sums the number of
   * passengers for the first segment that it encounters; as all passengers
   * on a Passenger Name Record (PNR) share the same itinerary, this will
   * give us the total number of passengers.
   *
   * <pr>Sabre returns a single line for all passengers per segment, while
   * Amadeus returns a single line for each individual passenger, hence
   * the need to count all manifestations of one segment list.
   ***********************************************************************
   */
  public int getNumPsgrs()
    {
    LineItem f = (LineItem )FareList.elementAt(0);
    int [] firstSegmentList = f.segmentList();
    int intNumPsgrs = 0;
    for ( int i = 0; i < FareList.size(); i++ )
      {
      f = (LineItem )FareList.elementAt(i);
      if ( (f instanceof LineItem) && f.segmentList() == firstSegmentList )
          intNumPsgrs += f.numPsgrs();
      } // end for

    return(intNumPsgrs);
    }

  /** 
   ***********************************************************************
   * Generic PTC
   ***********************************************************************
   */
  public void setGenericPTC(final String aGenericPTC)
    {
    GenericPTC = aGenericPTC;
    }

  public String getGenericPTC()
    {
    return(GenericPTC);
    }

  /** 
   ***********************************************************************
   * Native PTC
   ***********************************************************************
   */
  public void setNativePTC(final String aNativePTC)
    {
    NativePTC = aNativePTC;
    }

  public String getNativePTC()
    {
    return(NativePTC);
    }

  /** 
   ***********************************************************************
   * raw data
   ***********************************************************************
   */
  public void setRawData(final String aRawData)
    {
    RawData = aRawData;
    }

  public String getRawData()
    {
    return(RawData);
    }

  /** 
   ***********************************************************************
   * Determines whether the fare requested is Coach, that is, the default 
 * regular fare, one that is neither the lowest, contract or tax-exempt
   ***********************************************************************
   */
  public boolean isCoach()
    {
    if ( isLowest || isContract || isExempt )
      return(false);
    else
      return(true);
    }

  /** 
   ***********************************************************************
   * Clears all LineItems from the FareList 
   ***********************************************************************
   */
  public void clearFares()
    {
    FareList.clear();
    }

  /** 
   ***********************************************************************
   * Clears all LineItems from the TaxList 
   ***********************************************************************
   */
  public void clearTaxes()
    {
    TaxList.clear();
    }

  /** 
   ***********************************************************************
   * Clears all LineItems from both the FareList and the TaxList 
   ***********************************************************************
   */
  public void clearAll()
    {
    FareList.clear();
    TaxList.clear();
    }

  /** 
   ***********************************************************************
   * This method adds a LineItem object to the FareList; this is the long
   * form of the method in which all the elements of the Fare are specified.
   *
   * @param aNumPsgrs
   *   the number of Passengers covered by this fare
   *   
   * @param anAmount
   *   the dollar amount of this fare
   *
   * @param aDescription
   *   an optional description of the fare
   *
   * @param aSegmentList
   *   an optional array containing the list of segments to which this 
   *   fare applies - a null array is interpreted to mean all segments
   *
   * @throws exception 
   *   if either the number of passengers or the amount are not specified
   *
   * @see LineItem
   * @see #FareList
   ***********************************************************************
   */
  public void addFare(final int aNumPsgrs, 
                      final long anAmount,
                      final int[] aSegmentList,
                      final String aDescription) throws Exception 
    {
    final LineItem newFare = new LineItem(aNumPsgrs,anAmount,
                                          aSegmentList,aDescription);
    FareList.add(newFare);
    }

  /** 
   ***********************************************************************
   * This is the short form of the method which only requires 
   * a Number of Passengers and an Amount; the description is set
   * to an empty string and the Fare is interpreted to cover all
   * segments.
   *
   * @param aNumPsgrs
   *   the number of Passengers covered by this fare
   *   
   * @param anAmount
   *   the dollar amount of this fare
   *
   * @param aSegmentList
   *   an optional array containing the list of segments to which this 
   *   fare applies - a null array is interpreted to mean all segments
   *
   * @throws Exception 
   *   if either the number of passengers or the amount are not specified
   *
   * @see LineItem
   * @see #FareList
   ***********************************************************************
   */
  public void addFare(final int aNumPsgrs,
                      final long anAmount,
                      final int[] aSegmentList) throws Exception
    {
    final LineItem newFare = new LineItem(aNumPsgrs,anAmount,aSegmentList);
    FareList.add(newFare);
    }

  /**
   ***********************************************************************
   * This version of the method adds a Fare LineItem based on the 
   * FareDefinition string (of the form 172.XT or USD1971.15) with which 
   * it is called; this method is used as a shortcut for Sabre -
   * it does not apply to Amadeus and may not apply to other CRS systems.
   * 
   * @param aNumPsgrs
   *   The number of passengers to whom the FareDefinition applies
   *
   * @param aFareDefinition
   *   a String such as 172.XT or USD1971.15, as returned by a Sabre TA
   *
   * @param aSegmentList
   *   an int array containing a list of segment numbers
   *
   * @throws Exception - if the fare definition is not valid, 
   *                     or if the number of passengers is not specified
   *
   * @see StrToFare
   * @see LineItem
   * @see #FareList
   * @see #TaxList
   ***********************************************************************
   */

  public void addFare(final int aNumPsgrs,
                      final String aFareDefinition,
                      final int [] aSegmentList) throws Exception
    {
    final LineItem newFareLine = StrToFare(aNumPsgrs,aFareDefinition,aSegmentList);
    if ( newFareLine instanceof LineItem )
      FareList.add(newFareLine);
    else
      throw new Exception("Invalid fare definition - " + aFareDefinition);
    }


  /**
   ***********************************************************************
   * This version of the method adds a Fare LineItem based on the 
   * FareDefinition string (of the form 172.XT or USD1971.15) with which 
   * it is called; this method is used as a shortcut for Sabre -
   * it does not apply to Amadeus and may not apply to other CRS systems.
   *
   * <p>This is the short-form of the method, which assumes that the
   * FareDefinition provided covers all segments
   * 
   * @param aNumPsgrs
   *   The number of passengers to whom the FareDefinition applies
   *
   * @param aFareDefinition
   *   a String such as 172.XT or USD1971.15, as returned by a Sabre TA
   *
   * @throws Exception - if the fare definition is not valid, 
   *                     or if the number of passengers is not specified
   *
   * @see StrToFare
   * @see LineItem
   * @see #FareList
   * @see #TaxList
   ***********************************************************************
   */

  /*
  public void addFare(final int aNumPsgrs,
                      final String aFareDefinition) throws Exception
    {
    final LineItem newFareLine = StrToFare(aNumPsgrs,aFareDefinition,ALL_SEGMENTS);
    if ( newFareLine instanceof LineItem )
      FareList.add(newFareLine);
    else
      throw new Exception("Invalid fare definition - " + aFareDefinition);
    }
  */

  /** 
   ***********************************************************************
   * This method adds a LineItem object to the TaxList; this is the long
   * form of the method in which all the elements of the Tax are specified.
   *
   * @param aNumPsgrs
   *   the number of Passengers covered by this tax item
   *   
   * @param anAmount
   *   the dollar amount of this tax item
   *
   * @param aSegmentList
   *   an optional array containing the list of segments to which this 
   *   tax item applies - a null value is interpreted to mean all segments
   *
   * @param aDescription
   *   an optional description of the tax item
   *
   * @throws exception 
   *   when either the number of passengers or the amount are not valid
   *
   * @see LineItem
   * @see #TaxList
   ***********************************************************************
   */
  public void addTax(final int aNumPsgrs, 
                     final long anAmount,
 final int[] aSegmentList,
                     final String aDescription) throws Exception
    {
    final LineItem newTax = new LineItem(aNumPsgrs,anAmount,
                                          aSegmentList,aDescription);
    TaxList.add(newTax);
    }

  /** 
   ***********************************************************************
   * This is the short form of the method which only requires 
   * a Number of Passengers and an Amount; the description is set
   * to an empty string and the segment is interpreted to cover all
   * segments.
   *
   * @param aNumPsgrs
   *   the number of Passengers covered by this tax item
   *   
   * @param anAmount
   *   the dollar amount of this tax item
   *
   * @param aSegmentList
   *   an optional array containing the list of segments to which this 
   *   tax item applies - a null value is interpreted to mean all segments
   *
   * @throws exception
 *   when either the number of passengers or the amount are not valid
 *
   * @see LineItem
   * @see #TaxList
   ***********************************************************************
   */
  public void addTax(final int aNumPsgrs,
                      final long anAmount,
final int[] aSegmentList) throws Exception
    {
    final LineItem newTax = new LineItem(aNumPsgrs,anAmount,aSegmentList);
    TaxList.add(newTax);
    }


  /**
   ***********************************************************************
   * This version of the method adds a Tax LineItem based on the 
   * FareDefinition string (of the form 172.04XT or USD1971.15) with which
   * it is called;
   * 
   * @param aNumPsgrs
   *   The number of passengers to whom the FareDefinition applies
   *
   * @param aFareDefinition
   *   a String such as 172.02XT or USD1971.15
   *
   * @param aSegmentList
   *   an int array containing a list of segment numbers
   *
   * @throws Exception - if the fare definition is not valid,
   *                     or if the number of passengers is not specified
   *
   * @see StrToFare
   * @see LineItem
   * @see #FareList
   * @see #TaxList
   ***********************************************************************
   */

  public void addTax(final int aNumPsgrs,
                      final String aFareDefinition,
                      int [] aSegmentList) throws Exception
    {
    final LineItem newTaxLine = StrToFare(aNumPsgrs,aFareDefinition,aSegmentList);
    if ( newTaxLine instanceof LineItem )
      TaxList.add(newTaxLine);
    else
      throw new Exception("Invalid tax definition - " + aFareDefinition);
    }


  /**
   ***********************************************************************
   * This version of the method adds a Tax LineItem based on the 
   * FareDefinition string (of the form 172.XT or USD1971.15) with which 
   * it is called; this method is used as a shortcut for Sabre -
   * it does not apply to Amadeus and may not apply to other CRS systems.
   *
   * <p>This is the short-form of the method, which assumes that the
   * FareDefinition provided covers all segments
   * 
   * @param aNumPsgrs
   *   The number of passengers to whom the FareDefinition applies
   *
   * @param aFareDefinition
   *   a String such as 172.XT or USD1971.15, as returned by a Sabre TA
   *
   * @throws Exception - if the fare definition is not valid, 
   *                     or if the number of passengers is not specified
   *
   * @see StrToFare
   * @see LineItem
   * @see #FareList
   * @see #TaxList
   ***********************************************************************
   */
  /*
  public void addTax(final int aNumPsgrs,
                      final String aFareDefinition) throws Exception
    {
    final LineItem newTaxLine = StrToFare(aNumPsgrs,aFareDefinition,ALL_SEGMENTS);
    if ( newTaxLine instanceof LineItem )
      TaxList.add(newTaxLine);
    else
      throw new Exception("Invalid tax definition - " + aFareDefinition);
    }
  */

  /** 
   ***********************************************************************
   * This method performs in much the same way as the addFare method
   * when called with a FareDefinition parameter; it only differs
   * in that it blanks out the 'description' field of the LineItem and 
   * only enters the Amount NumPsgrs field; as for any of the methods
   * which pass a FareDefinition string, this method applies to Sabre,
   * cannot be used on Amadeus, and may not apply to other CRS as well.
   *
   * @param aNumPsgrs
   *   the number of passengers to whom this FareDefinition applies
   *
   * @param aFareDefinition
   *   a String such as 172.XT or USD1971.15, as returned by a Sabre TA
   *
   * @see addFare
   * @see StrToFare
   * @see LineItem
   * @see FareList
   * @see TaxList
   ***********************************************************************
   */
  /*
  public void addFareSum(final int aNumPsgrs,
                         final String aFareDefinition) throws Exception
    {
    final LineItem newfare = StrToFare(aNumPsgrs,aFareDefinition,ALL_SEGMENTS);
    if ( newfare instanceof LineItem )
      {
      newfare.setDescription("");
      FareList.add(newfare);
      }
    else
      throw new Exception("Invalid fare definition - " + aFareDefinition);
    }
  */


  /** 
   ***********************************************************************
   * This method performs in much the same way as the addFare method
   * when called with a FareDefinition parameter; it only differs
   * in that it blanks out the 'description' field of the LineItem and 
   * only enters the Amount NumPsgrs field; as for any of the methods
   * which pass a FareDefinition string, this method applies to Sabre,
   * cannot be used on Amadeus, and may not apply to other CRS as well.
   *
   * @param aNumPsgrs
   *   the number of passengers to whom this FareDefinition applies
   *
   * @param aFareDefinition
   *   a String such as 172.XT or USD1971.15, as returned by a Sabre TA
   *
   * @see addFare
   * @see StrToFare
   * @see LineItem
   * @see FareList
   * @see TaxList
   ***********************************************************************
   */
  /*
  public void addTaxSum(final int aNumPsgrs,
                        final String aTaxDefinition) throws Exception
    {
    final LineItem newtax = StrToFare(aNumPsgrs,aTaxDefinition,ALL_SEGMENTS);
    if ( newtax instanceof LineItem )
      {
      newtax.setDescription("");
      TaxList.add(newtax);
      }
    else
      throw new Exception("Invalid tax definition - " + aTaxDefinition);
    }
  */

  /** 
   ***********************************************************************
   * This method parses the FareDefinition string with which it is called,
   * and returns a LineItem object which can be added to the FareList
   * or TaxList vectors; this method when receiving a string of the form 172.03XT
   * or USD1971.15
   *
   * @param aNumPsgrs
   *   The number of passengers to whom the FareDefinition applies
   *
   * @param aFareDefinition
   *   a String such as 172.XT or USD1971.15, as returned by a Sabre TA
   *
   * @param aSegmentList
   *   an int array containing a list of segment numbers
   *
   * @see LineItem
   * @see #FareList
   * @see #TaxList
   ***********************************************************************
   */
  private LineItem StrToFare(final int aNumPsgrs,
                             final String aFareDefinition, 
                             final int[] aSegmentList) throws Exception
    {

    // see if this looks like a fare with the amount listed first
    {
    final String AMOUNT_FIRST = "^[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?\\.[0-9][0-9]";
    final MatchInfo amount_first_match = RegExpMatch.getFirstMatch(aFareDefinition,AMOUNT_FIRST);
    if ( amount_first_match instanceof MatchInfo )
      {
      final String sFare = amount_first_match.MatchString;
      final String sDesc = aFareDefinition.substring(amount_first_match.getEndPosition());
      final long iAmount = strToAmount(sFare);
      final LineItem aLineItem = new LineItem(aNumPsgrs,iAmount,aSegmentList,sDesc);
      return(aLineItem);
      }
    }

    // see if this looks like a fare with the amount listed last
    {
    final String AMOUNT_LAST   = "[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?\\.[0-9][0-9]$";
    final MatchInfo amount_last_match  = RegExpMatch.getFirstMatch(aFareDefinition,AMOUNT_LAST);
    if ( amount_last_match instanceof MatchInfo )
      {
      final String sFare = amount_last_match.MatchString;
      final String sDesc = aFareDefinition.substring(0,amount_last_match.MatchPosition);
      final long iAmount = strToAmount(sFare);
      final LineItem aLineItem = new LineItem(aNumPsgrs,iAmount,aSegmentList,sDesc);
      return(aLineItem);
      }
    }

    return(null);
    }

  /** 
   ***********************************************************************
   * converts a string in the form 187.32 to a long integer representing
   * the amount in cents
   ***********************************************************************
   */
  public static long strToAmount(final String aFareStr) throws Exception
    {
    final String VALID_FARE_STRING = "^[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?\\.[0-9][0-9]$";

    if ( RegExpMatch.matches(aFareStr,VALID_FARE_STRING) == false )
      throw new Exception("Invalid fare amount " + aFareStr);

    final StringTokenizer fields = new StringTokenizer(aFareStr,".");
    if ( fields.countTokens() != 2 )
      throw new Exception("Invalid fare amount " + aFareStr);

    final String sDollars  = fields.nextToken();
    final String sCents    = fields.nextToken();
    final long iDollars    = Integer.parseInt(sDollars);
    final long iCents      = Integer.parseInt(sCents);
    final long iTotalCents = (iDollars * 100) + iCents;
    return(iTotalCents);
    }

  /** 
   ***********************************************************************
   * returns the sum of all segments and all descriptions for all passengers
   ***********************************************************************
   */
  public long getBaseFare()
    {         // return sum of all segments and all descriptions
    // totalizer for fare with nothing set (indicates entire base fare)
      long iHighValue    = 999999;  
      
    long iEntireFareSum  = 0;   
    long iEntireFareLow  = iHighValue;

    // totalizer for fares with just segment set
    long iSegFareSum     = 0;   
    long iSegFareLow     = iHighValue; 

    // totalizer for fares with just description set
    long iDescFareSum    = 0;   
    long iDescFareLow    = iHighValue;

    // totalizer for fares with both description and segments set
    long iSegDescFareSum = 0;  
    long iSegDescFareLow = iHighValue;

    LineItem f;
    for ( int i = 0; i < FareList.size(); i++ )
      {
      if ( FareList.elementAt(i) instanceof LineItem )
        {
        f = (LineItem )FareList.elementAt(i);
        System.out.println("PNRFare: getBaseFare: numPsgrs: "  + f.numPsgrs());

        // assign the fare amount to one of the four totalizers (whichever is appropriate)
        if ( f.segmentList() == ALL_SEGMENTS )
          {
          if ( GnrcFormat.IsNull(f.description()) )
            {  
            iEntireFareSum  += f.amount() * f.numPsgrs();
            if ((iEntireFareSum > 0) && (iEntireFareSum < iEntireFareLow)) 
              iEntireFareLow = iEntireFareSum;
            }          
          else
            {  
            iDescFareSum    += f.amount() * f.numPsgrs();
            if ((iDescFareSum > 0) && (iDescFareSum < iDescFareLow)) 
              iDescFareLow = iDescFareSum;
            }
          }
        else
          {
          if ( GnrcFormat.IsNull(f.description()) )
            {  
            iSegFareSum     += f.amount() * f.numPsgrs();
            if ((iSegFareSum > 0) && (iSegFareSum < iSegFareLow)) 
              iSegFareLow = iSegFareSum;
            }
          else
            {
            iSegDescFareSum += f.amount() * f.numPsgrs();
            if ((iSegDescFareSum > 0) && (iSegDescFareSum < iSegDescFareLow)) 
              iSegDescFareLow = iSegDescFareSum;
            }
          }

        }
      }

    System.out.println("PNRFare: getBaseFare: " + iEntireFareSum 
        + " / " +  iSegFareSum + " / " +  iDescFareSum + " / " +  iSegDescFareSum);
    System.out.println("PNRFare: getLowFare: " + iEntireFareLow 
            + " / " +  iSegFareLow + " / " +  iDescFareLow + " / " +  iSegDescFareLow);
    
    // return one of the totalizers in this order of preference
    if (( iEntireFareSum > 0 ) && ( iEntireFareLow != iHighValue )) 
      return(iEntireFareLow);
    else if (( iSegFareSum > 0 ) && ( iSegFareLow != iHighValue ))
      return(iSegFareLow);
    else if (( iDescFareSum > 0 ) && ( iDescFareLow != iHighValue )) 
      return(iDescFareLow);
    else if ( iSegDescFareLow != iHighValue )
      return(iSegDescFareLow);
    else 
      return(0);  
    }


  /**
   ***********************************************************************
   * Returns the base fare for a given segment list
   ***********************************************************************
   */
  public long getBaseFare(final int [] aSegmentList)
    {         // return sum of amounts for a given segment list
    long iFareSum = 0;
    LineItem f;

    for ( int i = 0; i < FareList.size(); i++ )
      {
      if ( FareList.elementAt(i) instanceof LineItem )
        {
        f = (LineItem )FareList.elementAt(i);
        if ( f.segmentList() == aSegmentList )
          {
          if ( GnrcFormat.IsNull(f.description()) )
            // if you find a fare for this segment with a null description, 
            // that's the only fare for this segment
            return( f.amount() * f.numPsgrs() );     
          else
            iFareSum += f.amount() * f.numPsgrs() ;
          }
        }
      }

    return(iFareSum);
    }


  /**
   ***********************************************************************
   * Return the sum of fares for a given type of fare, for all segments
 * and for all passengers
   ***********************************************************************
   */
  public long getBaseFare(final String aDescription)
    {         
    long iFareSum = 0;
    LineItem f;

    for ( int i = 0; i < FareList.size(); i++ )
      {
      if ( FareList.elementAt(i) instanceof LineItem )
        {
        f = (LineItem )FareList.elementAt(i);
        if ( f.description().equals(aDescription) )
          {
          if ( f.segmentList() == ALL_SEGMENTS )
            return( f.amount() * f.numPsgrs() );     
          else
            iFareSum += f.amount();
          }
        }
      }

    return(iFareSum);
    }

    
  /**
   ***********************************************************************
   * Return the sum of fares for a given type of fare and segment list,
 * and for all passengers
   ***********************************************************************
   */
  public long getBaseFare(final String aDescription, final int [] aSegmentList)
    {        
    long iFareSum = 0;
    LineItem f;
    for ( int i = 0; i < FareList.size(); i++ )
      {
      if ( FareList.elementAt(i) instanceof LineItem )
        {
        f = (LineItem )FareList.elementAt(i);
        if ( f.description().equals(aDescription) 
             && (f.segmentList() == aSegmentList) )
          iFareSum += f.amount() * f.numPsgrs();
        }
      }

    return(iFareSum);
    }

  /** 
   ***********************************************************************
   * This function totals and returns all tax types for all segments 
   * and all passengers on a given PNRFare.
   *
   * @return  the sum of all segments and all types of taxes - the taxes 
   *          are returned in cents
   ***********************************************************************
   */
  public long getTax()
    {         
    // totalizer for tax with nothing set (indicates entire base fare)
    long iEntireTaxSum  = 0;   
    
    // totalizer for taxes with just segment set
    long iSegTaxSum     = 0;   
    
    // totalizer for taxes with just description set
    long iDescTaxSum    = 0;   
    
    // totalizer for taxes with both description and segments set
    long iSegDescTaxSum = 0;   
    
    LineItem t;
    for ( int i = 0; i < TaxList.size(); i++ )
      {
      if ( TaxList.elementAt(i) instanceof LineItem )
        {
        t = (LineItem )TaxList.elementAt(i);
    
        // assign the tax amount to one of the four totalizers (whichever is appropriate)
        if ( t.segmentList() == ALL_SEGMENTS )
          {
          if ( GnrcFormat.IsNull(t.description()) )
            iEntireTaxSum  += t.amount() * t.numPsgrs();
          else
            iDescTaxSum    += t.amount() * t.numPsgrs();
          }
        else
          {
          if ( GnrcFormat.IsNull(t.description()) )
            iSegTaxSum     += t.amount() * t.numPsgrs();
          else
            iSegDescTaxSum += t.amount() * t.numPsgrs();
          }

        }
      }

    // return one of the totalizers in this order of preference
    if ( iEntireTaxSum > 0 )
      return(iEntireTaxSum);
    else if ( iSegTaxSum > 0 )
      return(iSegTaxSum);
    else if ( iDescTaxSum > 0 )
      return(iDescTaxSum);
    else
      return(iSegDescTaxSum);
    }

  /** 
   ***********************************************************************
   * This function totals and returns all taxes for a given Segment List
   *
   * @param  an int[] containing a list of segments, as reflected in the PNR
   *
   * @return  the sum of all taxes for a given segment list - the taxes
   *          are returned in cents
   ***********************************************************************
   */
  public long getTax(final int [] aSegmentList)
    {         // return sum of amounts for a given segment
    long iTaxSum = 0;
    LineItem t;

    for ( int i = 0; i < TaxList.size(); i++ )
      {
      if ( TaxList.elementAt(i) instanceof LineItem )
        {
        t = (LineItem)TaxList.elementAt(i);
        if ( t.segmentList() == aSegmentList )
          {
          if ( GnrcFormat.IsNull(t.description()) )
            // if you find a tax for this segment with a null description, 
            // that's the sum    
            return( t.amount() * t.numPsgrs() );     
          else
            iTaxSum += t.amount() * t.numPsgrs();
          }
        }
      }

    return(iTaxSum);
    }

  /**
   ***********************************************************************
   * Return the total for a given type of tax, for all segments
   * 
   * @param aDescription  
   *   the description of the tax, as returned by the CRS
   ***********************************************************************
   */
  public long getTax(final String aDescription)
    {         
    long iTaxSum = 0;
    LineItem t;

    for ( int i = 0; i < TaxList.size(); i++ )
      {
      if ( TaxList.elementAt(i) instanceof LineItem )
        {
        t = (LineItem)TaxList.elementAt(i);
        if ( t.description().equals(aDescription) )
          {
          if ( t.segmentList() == ALL_SEGMENTS )
            return( t.amount() * t.numPsgrs() );     
          else
            iTaxSum += t.amount() * t.numPsgrs();
          }
        }
      }

    return(iTaxSum);
    }

  /**
   ***********************************************************************
   * Returns the total of a given tax for a given segment list
   *
   * @param aDescription  
   *   the description of the tax, as returned by the CRS
   *
   * @param aSegmentList
   *   an int[] that specifies the segment numbers, as reflected in the
   *   Passenger Name Record (PNR)
   *
   * @return
   *   the amount of a given tax on a given segment list - the tax is 
   *   expressed in cents
   ***********************************************************************
   */
  public long getTax(final String aDescription, final int [] aSegmentList)
    {        
    long iTaxSum = 0;
    LineItem t;
    for ( int i = 0; i < TaxList.size(); i++ )
      {
      if ( TaxList.elementAt(i) instanceof LineItem )
        {
        t = (LineItem )TaxList.elementAt(i);
        if ( t.description().equals(aDescription) 
            && (t.segmentList() == aSegmentList) )
          iTaxSum += t.amount() * t.numPsgrs();
        }
      }

    return(iTaxSum);
    }

  public long getTaxAmountByIndex(final int aIndex)
    {         // return tax amount for the given tax at index
    if ( aIndex < TaxList.size() )
      {
      if ( TaxList.elementAt(aIndex) instanceof LineItem )
        {
        final LineItem t = (LineItem )TaxList.elementAt(aIndex);
        return( t.amount() * t.numPsgrs() );
        }
      }

    return(0);
    }

  public String getTaxNameByIndex(final int aIndex)
    {         // return tax amount for the given tax at index
    if ( aIndex < TaxList.size() )
      {
      if ( TaxList.elementAt(aIndex) instanceof LineItem )
        {
        final LineItem t = (LineItem )TaxList.elementAt(aIndex);
        return( t.description() );
        }
      }

    return("");
    }


  public int getNumTaxes()
    {
    if ( TaxList instanceof Vector )
      return( TaxList.size() );
    else
      return(0);
    }


  /**
   ***********************************************************************
   * <p>This method returns an array of at most 4 {@link Tax} objects, each
   * corresponding to the total amount of a type of tax in the
   * <code>PNRFare</code>; this method is used to build the string
   * response returned by the Transaction Server via the {@link
   * NativeAsciiWriter#reqGetFare NativeAsciiWriter} object; the current
   * Airware response interface can accept at most 4 different types of taxes,
   * hence the magic 4.</p>
   *
   * <p>Different Computer Reservation Systems (CRS) itemize taxes in different
   * manners. For example, Sabre itemizes the taxes by tax type and 
   * Passenger Type Code (PTC), but returns a total for all passengers 
   * and segments fared.  On the other hand, Amadeus totals all the types 
   * of taxes, but itemizes these totals per passenger.</p>
   *
   * <p>The tax information, however it is returned, is stored in  
   * {@link LineItem} objects within the {@link #TaxList} vector. The method
   * herewith is used to total the information contained in the TaxList vector.</p>
   *
   * @return   
   * <ul>
   *   <li>If the TaxList vector does not itemize the taxes by type, 
   *       this method returns an array containing a single <code>Tax</code>
   *       object of type <code>Tax.OTHER</code></li>
   *   <li>If TaxList itemizes taxes by type, and there are less than 4
   *       different types of taxes, this method returns an array of at most 4
   *       <code>Tax</code> objects, each corresponding to the total for a
   *       given type of Tax. 4 is the maximum number of tax types that Airware
   *       can handle currently.</li>
   *   <li>If the TaxList vector itemizes more than four types of tax, the
   *       first three types taxes are listed in the first three positions of
   *       the array, and the other tax type totals are added and stored in the
   *       fourth position of the array under the type <code>Tax.OTHER</code>.</li> 
   *</ul>
   *
   * @see  NativeAsciiWriter#reqGetFare
   * @see  #TaxList
   * @see  LineItem
   ***********************************************************************
   */

  public Tax[] getTaxTotals()
    {
    // used to store tax amounts by type
    HashMap taxTypeTotals = new HashMap();

    // used to preserve order of tax types as they appear in the fare response
    Vector taxTypeList = new Vector();
    LineItem t;
    long lAmount;
    String sType = "";

    // First go through the TaxList Vector and: 
    // - store totals per type of tax in taxTypeTotals
    // - store the order in which the tax types appear in taxTypeList
    for ( int i = 0; i < TaxList.size(); i++ )
      {
      if ( TaxList.elementAt(i) instanceof LineItem )
        {
        t = (LineItem )TaxList.elementAt(i);
        sType = t.description();

        if (sType.equals("")) // we are being given a non-itemized tax total
          {
          lAmount = t.amount() * t.numPsgrs();
          if (taxTypeTotals.containsKey(Tax.OTHER))
            lAmount += ((Long)taxTypeTotals.get(Tax.OTHER)).longValue();
          else
            taxTypeList.add(Tax.OTHER);
         
          taxTypeTotals.put(Tax.OTHER, new Long(lAmount) );
          }
        else if ( taxTypeTotals.containsKey(sType) )
          {
          lAmount = t.amount() * t.numPsgrs();
          lAmount += ((Long)taxTypeTotals.get(sType)).longValue();
          taxTypeTotals.put(sType, new Long(lAmount) );
          }
        else
          {
          taxTypeTotals.put(sType, new Long(t.amount() * t.numPsgrs()) );
          taxTypeList.add(sType);
          }
        }
      } // end for


    Tax[] aryTaxTotals = null;

    int MAX_AWR_BUCKETS = 4;
    int size = taxTypeList.size();
     
    if (size <= MAX_AWR_BUCKETS)
      {
      aryTaxTotals = new Tax[taxTypeList.size()];

      for (int i=0; i < taxTypeList.size(); i++)
        {
        sType   = (String)taxTypeList.get(i);
        lAmount = ((Long)taxTypeTotals.get(sType)).longValue();
        aryTaxTotals[i] = new Tax(sType,lAmount);
        }
      }
    else
      {
      aryTaxTotals = new Tax[MAX_AWR_BUCKETS];
      long lOtherTax = 0;

      for (int i=0; i < taxTypeList.size(); i++)
       {
        sType   = (String)taxTypeList.get(i);
        lAmount = ((Long)taxTypeTotals.get(sType)).longValue();
        if (i < 4)
          aryTaxTotals[i] = new Tax(sType,lAmount);
        else
          lOtherTax += lAmount;
        }
      aryTaxTotals[3] = new Tax(Tax.OTHER, lOtherTax);
      }

    return aryTaxTotals;

    } // end getTaxTotals()


  /** 
   ***********************************************************************
   * return the sum of BaseFare and Tax for all passengers, segments 
   * and all descriptions
   ***********************************************************************
   */
  public long getTotal()
    {         // return sum of all segments and all descriptions
    final long iBaseFare = getBaseFare();
    final long iTax      = getTax();

    return(iBaseFare + iTax);
    }

  /** 
   ***********************************************************************
   * return the sum of BaseFare and Tax for all passengers and all descriptions
   * for a given segment list
   ***********************************************************************
   */
  public long getTotal(final int [] aSegmentList)
    {         
    final long iBaseFare = getBaseFare(aSegmentList);
    final long iTax      = getTax(aSegmentList);

    return(iBaseFare + iTax);
    }

  /** 
   ***********************************************************************
   * This method returns the Generic Airware Passenger Type Code 
   * and the fare rate requested, for example: 'ADT Lowest' or 'CHD Contract'
   *
   * @see GenericPTC
   ***********************************************************************
   */
  public String toString()
    {
    if ( isLowest )
      return( GenericPTC + " Lowest" );
    else if ( isContract )
      return( GenericPTC + " Contract" );
    else
      return( GenericPTC + " Coach");
    }


  /**
   ***********************************************************************
   * This is an inner class contained within PNRFare that is used to store 
   * line items, such as Fare and Taxes.
   *
   * @author   David Fairchild
   * @author   Philippe Paravicini
   * @version  1.x Copyright (c) 1999
   *
   * @see  PNRFare
   * @see  PNR
   ***********************************************************************
   */
  
  private class LineItem
    {
    /** number of passengers to which this line item applies */
    private int numPsgrs;
  
    /** dollar amount of fare or tax, expressed in cents */
    private long amount;           
   
    /** segments to which this fare applies */
    private int[] segmentList;    
  
    /** 
     * Description of the line item contained; as of this writing,
     * this could be either fare or taxes. 
     */
    private String description;      
  
  
    /** 
     ***********************************************************************
     * Long Constructor: specify all fields in the line item.
     *
     * @throws Exception if aNumPsgrs or anAmount are not positive a 
     * positive int or positive long, respectively
     *
     * @param aNumPsgrs
     *   the number of Passengers covered by this tax item
     *   
     * @param anAmount
     *   the dollar amount of this tax item
     *
     * @param aSegmentList
     *   an optional array containing the list of segments to which this 
     *   tax item applies - a null value is interpreted to mean all segments
     *
     * @param aDescription
     *   an optional description of the tax item
     *
     * @throws exception 
     *   if either the number of passengers or the amount are not specified
     ***********************************************************************
     */
    public LineItem(int aNumPsgrs, 
                    long anAmount, 
                    int[] aSegmentList,
                    String aDescription) throws Exception
      {
      if ( aNumPsgrs <= 0)
        throw new Exception("Invalid Number of Passengers for this line item");
  
      if ( anAmount < 0)
        throw new Exception("Invalid Amount for this line item");

      numPsgrs = aNumPsgrs;  
      amount   = anAmount;

      if (aSegmentList instanceof int[])
         segmentList = aSegmentList;
      else
         segmentList = ALL_SEGMENTS;

      if (aDescription instanceof String)
        description = aDescription;
      else
        description = "";
      }

    /**
     ***********************************************************************
     * Short Constructor: specify only a Number of Passengers and an Amount;
     * the segmentList is set to the default value for all segments ,
     * and the description is set to the empty string.
     *
     * @param aNumPsgrs
     *   the number of Passengers covered by this tax item
     *   
     * @param anAmount
     *   the dollar amount of this tax item
     ***********************************************************************
     */
    public LineItem(int aNumPsgrs,
                long anAmount, 
                int[] aSegmentList) throws Exception
      {
      new LineItem(aNumPsgrs,anAmount,aSegmentList,"");
      }


    /** get number of passengers */
    public int numPsgrs() {
      return(numPsgrs);
    }
    
    /** get amount */
    public long amount() {return(amount);}
    
    /** get segment list */
    public int [] segmentList() {return(segmentList);}

    /** get description */
    public String description() {return(description);}
    
    /** set number of passengers */
    public void setNumPsgrs(int aNumPsgrs) throws Exception
      {
      if (aNumPsgrs <= 0)
        throw new Exception("Number of passengers must be a positive non-zero integer");
      numPsgrs = aNumPsgrs;
      }
    
    /** set amount */
    public void setAmount(long anAmount) throws Exception
      {
      if (anAmount < 0)
        throw new Exception("Amount must be a positive integer");
      amount = anAmount;
      }
    
    /** set segment list */
    public void setSegmentList(int [] aSegmentList)
      {
      if (aSegmentList instanceof int [])
        segmentList = aSegmentList;
      else 
        segmentList = ALL_SEGMENTS;
      }
    
    /** set description */
    public void setDescription(String aDescription)
      {
      if (aDescription instanceof String)
        description = aDescription;
      else 
        description = "";
      }

  } // end LineItem inner class


} // end PNRFare class

