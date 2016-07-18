package xmax.crs.Amadeus;

import xmax.crs.PNRFare;
import xmax.crs.GetPNR.PNRNameElement;
import java.util.Vector;

/**
 ***********************************************************************
 * This class is used to group the fares returned in a
 * <code>Ticket_ImagePlus_Reply</code>; most often, these fare groups will
 * correspond to a specific PTC group.
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 5/9/2002 10:48:44 AM$
 ***********************************************************************
 */
class FareGroup
{
  /** the Passenger Type Code (PTC) associated with this fare group */
  private String PTC;

  /** Passenger Type Code getter */
  public String PTC() { return PTC; }

  /** Passenger Type Code setter */
  public void setPTC(String sPTC) { PTC = sPTC; }

  /** the passengers belonging to this group */
  private Vector nameList;

  /** the fare attached to this group */
  private PNRFare pnrFare;

  /** fare getter */
  public PNRFare getFare(){return pnrFare;}


  /** constructor: initializes the FareGroup with a {@link PNRNameElement} */
  public FareGroup(PNRNameElement aPsgr) throws IllegalArgumentException
    {
    if ( (aPsgr instanceof PNRNameElement) == false)
      throw new IllegalArgumentException("You provided a null passenger name");

    if ( (aPsgr.PTC instanceof String) == false )
      throw new IllegalArgumentException(
          "The passenger name that you provided does not have a valid PTC");

    PTC      = aPsgr.PTC;
    pnrFare  = new PNRFare(PTC);
    nameList = new Vector();
    nameList.add(aPsgr);
    }

  /** constructor: initializes the FareGroup with a Passenger Type Code PTC */
  public FareGroup(String sPTC)
    {
    PTC = sPTC;
    nameList = new Vector();
    pnrFare  = new PNRFare(sPTC);
    }

  /** adds a PNRNameElement to this Passenger Type Code (PTC) group */
  //public void addName(PNRNameElement aPsgr) throws IllegalArgumentException
  public void addName(PNRNameElement aPsgr)
    {
    /*
    if ( (aPsgr instanceof PNRNameElement) == false )
      throw new IllegalArgumentException("You provided a null passenger name");

    if (! ( aPsgr.PTC instanceof String && aPsgr.PTC.equals(this.PTC) ) )
      throw new IllegalArgumentException(
          "The passenger name that you provided does not have a valid PTC: "
          + aPsgr.PTC + " - it should be: " + this.PTC);
    */
    nameList.add(aPsgr);
    }

  /** adds an array of PNRNameElements to this PassengerTypeCode group */
  public void addNames(PNRNameElement[] psgrList)
    {
    if (psgrList != null)
      for (int i=0; i < psgrList.length ; i++)
        addName(psgrList[i]);
    }

  /** 
   * get an array of the {@link PNRNameElement} objects that belong in this
   * PTC group 
   */
  public PNRNameElement[] getNames()
    {
    return (PNRNameElement[])nameList.toArray(new PNRNameElement[countNames()]);
    }

  /** returns the number of passengers in this PTC group */
  public int countNames()
    {
    return(nameList.size());
    }

  /** 
   * given an array of PNRNameElements, this method returns a vector
   * of FareGroup objects that group those PNRNameElements by PTC
   */
  public static FareGroup[] groupNames(PNRNameElement[] psgrList)
    {
    if ( (psgrList instanceof PNRNameElement[]) == false )
      return(null);

    Vector ptcGroupsList = new Vector();

    for (int i=0; i < psgrList.length; i++)
      {
      String sPTC = psgrList[i].PTC;
      if (sPTC instanceof String)
        {
        boolean found = false;
        for (int j=0; j < ptcGroupsList.size(); j++)
          {
          FareGroup ptcGroup = (FareGroup)ptcGroupsList.get(j);
          // see if we can find such a PTC group
          if ( sPTC.equals(ptcGroup.PTC) )
            {
            ptcGroup.addName(psgrList[i]);
            found = true;
            }
          } // end inner for
        // if we do not have a group with that PTC, create it now
        if (!found)
          ptcGroupsList.add(new FareGroup(psgrList[i]));
        }
      } // end outer for

    return((FareGroup[])ptcGroupsList.toArray(new FareGroup[ptcGroupsList.size()]));
    } // end groupNames


} // end class FareGroup


