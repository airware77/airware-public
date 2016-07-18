package xmax.crs.Amadeus;

/**
 ***********************************************************************
 * This is a convenience class that is used by both the AmadeusParseFlifo and
 * AmadeusAPIParseFlifo classes to parse Flight Information replies; it is
 * merely used during the parsing process and is not part of the overall PNR
 * object model.
 * 
 * @author   David Fairchild
 * @version  $Revision: 1$ - $Date: 10/02/2001 12:41:36 PM$
 *
 * @see AmadeusParseFlifo
 * @see AmadeusAPIParseFlifo
 ***********************************************************************
 */
class FlightStop
{
  String CityCode;
  long ArriveDateTime;
  long DepartDateTime;
  String MealService;
  String Equipment;

  /**  flight duration for the next flight segment */
  int FlightMinutes;    
}
