package xmax.crs.Availability;

import java.util.Vector;
import xmax.TranServer.GnrcFormat;
import java.io.Serializable;

public class DestAvailability implements Serializable
{
 // requested availability parameters
 public String ReqDepCity;
 public String ReqDepDate;
 public String ReqDepTime;
 public String ReqArrCity;
 public String ReqArrDate;
 public String ReqArrTime;
 public String ReqDirectAccessCarrier;

 /** 
  * can be: ReqGetAvail.AVAIL_DIRECT, ReqGetAvail.AVAIL_FILTER or
  * ReqGetAvail.AVAIL_NEUTRAL
  */
 public String ReqAvailType;
 public String ReqFilterCarrier;
 public int    ReqFilterFlight;
 public String ReqClassOfService;
 public boolean NonStopsOnly;
 public boolean DirectOnly;
 public int ReqMaxItins;
 public String RawData;
 private StringBuffer Errors;
 private Vector ItinList;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public DestAvailability()
    {
    ItinList = new Vector();
    Errors = new StringBuffer();
    }

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public DestAvailability(final String aDepCity, final String aArrCity, final String aDepDate, final int aMaxItins)
    {
    ReqDepCity  = aDepCity;
    ReqArrCity  = aArrCity;
    ReqDepDate  = aDepDate;
    ReqMaxItins = aMaxItins;

    ItinList = new Vector();
    }

  /** 
   ***********************************************************************
   * Get number of itins collected
   ***********************************************************************
   */
  public int getNumItins()
    {
    if ( ItinList instanceof Vector )
      return( ItinList.size() );
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Get number of itins that fit the given criteria
   *
   * @see itinFitsCriteria
   ***********************************************************************
   */
  public int getNumGoodItins()
    {
    int iNumItins = 0;

    if ( ItinList instanceof Vector )
      {
      ItinAvailability itin;
      for ( int i = 0; i < ItinList.size(); i++ )
        {
        itin = (ItinAvailability )ItinList.elementAt(i);
        if ( itin instanceof ItinAvailability )
          {
          if ( itinFitsCriteria(itin) )
            iNumItins++;
          }
        }
      }

    return(iNumItins);
    }

  /** 
   ***********************************************************************
   * Get number of direct itins collected
   ***********************************************************************
   */
  public int getNumDirectItins()
    {
    if ( ItinList instanceof Vector )
      {
      ItinAvailability itin;
      int iNumItins = 0;
      for ( int i = 0; i < ItinList.size(); i++ )
        {
        itin = (ItinAvailability )ItinList.elementAt(i);
        if ( itin instanceof ItinAvailability )
          {
          if ( itin.isDirect() )
            iNumItins++;
          }
        }

      return( iNumItins );
      }
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Get number of non-stop itins collected
   ***********************************************************************
   */
  public int getNumNonStopItins()
    {
    if ( ItinList instanceof Vector )
      {
      ItinAvailability itin;
      int iNumItins = 0;
      for ( int i = 0; i < ItinList.size(); i++ )
        {
        itin = (ItinAvailability )ItinList.elementAt(i);
        if ( itin instanceof ItinAvailability )
          {
          if ( itin.isNonStop() )
            iNumItins++;
          }
        }

      return( iNumItins );
      }
    else
      return(0);
    }

  /** 
   ***********************************************************************
   * Returns true if we have collected enough itineraries
   ***********************************************************************
   */
  public boolean isComplete()
    {
    final int DEFAULT_MAX_ITINS = 10;

    final int iMaxItins;
    if ( ReqMaxItins > 0 )
      iMaxItins = ReqMaxItins;
    else
      iMaxItins = DEFAULT_MAX_ITINS;

    final int iNumItins = getNumGoodItins();

    if ( iNumItins >= iMaxItins )
      return(true);
    else
      return(false);
    }

  /** 
   ***********************************************************************
   * Returns true if the given itinerary fits the criteria provided in the
   * Availability request, as described below.
   *
   * <p>The criteria may include:
   * <ul>
   *   <li>Return non-stop flights only</li>
   *   <li>Return direct flights only</li>
   *   <li>Return flights with the carrier or flight requested</li>
   * </ul>
   ***********************************************************************
   */
  public boolean itinFitsCriteria(final ItinAvailability aItin)
    {
    // make sure it is a non-stop itin if desired
    if ( NonStopsOnly && (aItin.isNonStop() == false) )
      return(false);

    // make sure it is a DirectOnly itin if desired
    if ( DirectOnly && (aItin.isDirect() == false) )
      return(false);

    // if a certain carrier/flight is specified, make sure the itin uses it
    if ( GnrcFormat.NotNull(ReqFilterCarrier) )
      {
      if ( ReqFilterFlight > 0 )
        {
        if ( aItin.usesFlight(ReqFilterCarrier,ReqFilterFlight) == false )
          return(false);
        }

      if ( aItin.usesCarrier(ReqFilterCarrier) == false )
        return(false);
      }

    return(true);
    }

  /** 
   ***********************************************************************
   * Adds a new itinerary to our list of itineraries {@link #ItinList}.
   ***********************************************************************
   */
  public void addItin(final ItinAvailability aItin)
    {
    if ( (ItinList instanceof Vector) == false )
      ItinList = new Vector();

    ItinList.add(aItin);
    }

  /** 
   ***********************************************************************
   * Returns {@link #ItinList} as an array of itineraries
   ***********************************************************************
   */
  public ItinAvailability[] getItinArray()
    {
    if ( ItinList instanceof Vector )
      {
      if ( ItinList.size() > 0 )
        {
        final ItinAvailability[] ItinArray = new ItinAvailability[ ItinList.size() ];
        ItinList.toArray(ItinArray);
        return(ItinArray);
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * concatenates the error provided to the list of errors encountered in the
   * availability reply
   ***********************************************************************
   */
  public void addError(String sError)
    {
    Errors.append(" - " + sError);
    } // end addError

  /**
   ***********************************************************************
   * Returns the errors encountered while parsing the Avail response
   ***********************************************************************
   */
  public String getErrors()
    {
    return Errors.toString();
    } // end getErrors

}
