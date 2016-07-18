package xmax.TranServer;

import xmax.crs.GnrcCrs;
import xmax.util.Log.*;
import java.io.Serializable;

/**
 ***********************************************************************
 * This class is used to hold a request to add an existing Passenger Name 
 * Record (PNR) to a PNR Queue; in order to instatiate it, it is necessary to
 * pass it a Computer Reservation System (CRS) Code, the locator identifying 
 * the PNR and the string identifying the Queue.
 *
 * @author   David Fairchild
 * @version  $Revision: 7$ - $Date: 10/01/2001 1:14:30 PM$
 ***********************************************************************
 */
public class ReqQueuePNR extends ReqTranServer implements Serializable
{
 /** the Remote Locator identifying the Passenger Name Record (PNR) */
 public String Locator;

 /** 
  * the identifier identifying the Passenger Name Record Queue where the PNR
  * to which the PNR will be queued 
  * */
 public String QueueName;

 /** 
  ***********************************************************************
  * constructor
  ***********************************************************************
  */
  public ReqQueuePNR(final String aCrsCode, final String aLocator, 
                       final String aQueueName) 
    {
    super(aCrsCode);
    Locator   = aLocator;
    QueueName = aQueueName;
    }

  /** 
   ***********************************************************************
   * This method calls the {@link GnrcCrs.QueuePNR} method to queue the
   * Passenger Name Record (PNR)
   * it also runs all the GnrcCrs commands required to fulfill the request
   ***********************************************************************
   */
  public void runRequest(final GnrcCrs aCrs) throws Exception
    {
    aCrs.QueuePNR(Locator,QueueName);
    AppLog.LogInfo("Locator " + Locator + " added to queue " + QueueName,null,aCrs.getConnectionName());
    }

//  /** 
//   ***********************************************************************
//   *
//   ***********************************************************************
//   */
//  private FileLogger startLogging(final GnrcCrs aCrs) throws Exception
//    {
//    // check input parms
//    if ( GnrcFormat.IsNull(CrsCode) )
//      throw new TranServerException(
//          "Cannot open log file for Get PNR.  Crs Code is null");
//
//    if ( GnrcFormat.IsNull(Locator) )
//      throw new TranServerException(
//          "Cannot open log file for Get PNR.  Locator is null");
//
//    // log file name is based on Crs code and locator
//    final String sLogDir  = ConfigInformation.getFileParamValue(
//        ConfigTranServer.LOGGING_DIRECTORY,"LOG");
//
//    final String sLogName = 
//      sLogDir + "\\Locators\\" + CrsCode + Locator + ".log";
//
//    return( startLogging(sLogName,aCrs) );
//    }

  /** 
   ***********************************************************************
   * Get a log file name to use for this request
   ***********************************************************************
   */
  public String getLogFileName(final String aLogDirectory) throws Exception
    {
    final StringBuffer sLogName = new StringBuffer();
    if ( GnrcFormat.NotNull(aLogDirectory) )
      sLogName.append(aLogDirectory);

    sLogName.append("\\Locators\\");

    // check input parms
    if ( GnrcFormat.IsNull(getCrsCode()) )
      throw new TranServerException("Cannot open log file for Queue PNR.  Crs Code is null");

    if ( GnrcFormat.IsNull(Locator) )
      throw new TranServerException(
          "Cannot open log file for Queue PNR.  Locator is null");

    sLogName.append(getCrsCode() + Locator + ".log");
    return( sLogName.toString() );
    }

}
