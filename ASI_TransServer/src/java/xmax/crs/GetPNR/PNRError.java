package xmax.crs.GetPNR;

import java.io.Serializable;

/**
 ***********************************************************************
 * This class is used to store references to errors that may have 
 * occurred in a Passenger Name Record (PNR); when encountering an 
 * individual error while building or parsing a PNR, an object of this
 * class is instantiated and added to the {@link xmax.crs.PNR#ErrList} vector.
 * 
 * Note that prior to version 1.4.0, the <code>PNR.ErrList</code> vector 
 * only contained strings. It was necessary to introduce this structure 
 * in order to handle the errors being returned by a structured API, 
 * such as the Amadeus API, which returns errors attached to elements 
 * of the PNR.
 *
 * As of July 2001, only the Amadeus API classes are using this class
 * to store errors.  The rest of the API is still storing String objects
 * in the <code>PNR.ErrList</code> vector.
 *
 * @author   Philippe Paravicini
 * @version  $Revision: 4$ - $Date: 08/20/2001 2:01:27 PM$
 ***********************************************************************
 */
public class PNRError implements Serializable
{
  /**
   * references on of the objects making up a PNR object
   * 
   * @see PNRNameElement
   * @see PNRItinSegment
   * @see PNRRemark
   */
  public Object pnrElement;

  /**
   * an error string provided by the Transaction Server to clarify
   * the error returned by the Computer Reservation System, or to
   * suggest a course of action to rectify the error.
   */
  public String nativeError = "";

  /**
   * the error code returned by the Computer Reservation System (CRS)
   */
  public String crsErrorCode = "";

  /**
   * the error string returned by the Computer Reservation System (CRS)
   */
  public String crsError = "";


  /**
   ***********************************************************************
   * this constructor initializes all the fields of the class
   ***********************************************************************
   */
  public PNRError(Object aPNRelement, 
                  String sErr, 
                  String sCrsErrorCode, 
                  String sCrsError)  
    {
    pnrElement = aPNRelement;
    nativeError = sErr;
    crsErrorCode = sCrsErrorCode;
    crsError = sCrsError;
    } // end PNRError constructor

  /**
   ***********************************************************************
   * This constructor is used to record error messages at times when 
   * there is no pnr element that can be explicitly referenced in the error.
   ***********************************************************************
   */
  public PNRError(String sErr, String sCrsErrorCode, String sCrsError)  
    {
    nativeError = sErr;
    crsErrorCode = sCrsErrorCode;
    crsError = sCrsError;
    } // end PNRError constructor

  /**
   ***********************************************************************
   * This constructor is used to record error messages at times when 
   * there is no pnr element that can be explicitly referenced in the error,
   * and there is no message from the CRS to be recorded.
   ***********************************************************************
   */
  public PNRError(String sErr) 
    {
    nativeError = sErr;
    } // end PNRError constructor

  /**
   ***********************************************************************
   * This constructor returns an unitialized <code>PNRError</code> object
   ***********************************************************************
   */
  public PNRError() 
    {
    } // end PNRError constructor


} // end class PNRError

