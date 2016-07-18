/*
 * Copied from StatusInformation.java.  Modified for recordLocator
 * This class was automatically generated with
 * <a href="http://castor.exolab.org">Castor 0.9.3</a>, using an
 * XML Schema.
 * $Id$
 */

package xmax.crs.Amadeus.negospace;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 *
 *       This type is included in the RecordLocator node that
 * is returned by
 *       the response structures for Create, Display, Change and
 * Cancel Negospace;
 *       This set of nodes contains reservation information in
 * the form of a controlNumber.
 *
 * @version $Revision: 3$ $Date: 02/25/2002 6:48:58 PM$
**/
public class RecordLocatorType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private Reservation _reservation;

      //----------------/
     //- Constructors -/
    //----------------/

    public RecordLocatorType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.RecordLocatorType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public Reservation getReservation()
    {
        return this._reservation;
    } //-- Reservation getReservation()

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid()

    /**
     *
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer)

    /**
     *
     * @param handler
    **/
    public void marshal(org.xml.sax.DocumentHandler handler)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.DocumentHandler)

    /**
     *
     * @param Reservation
    **/
    public void setReservation(Reservation reservation)
    {
        this._reservation = reservation;
    } //-- void setReservation(Reservation)

		/**
     *
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.RecordLocatorType unmarshalRecordLocatorType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.RecordLocatorType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.RecordLocatorType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.RecordLocatorType unmarshalRecordLocatorType(java.io.Reader)

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate()

}
