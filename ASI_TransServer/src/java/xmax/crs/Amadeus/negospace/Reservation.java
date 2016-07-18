/*
 * Copied from Error.java.  Modified for reservation
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
 * @version $Revision: 2$ $Date: 02/08/2002 8:15:09 PM$
**/
public class Reservation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/


    private java.lang.String _controlNumber;


      //----------------/
     //- Constructors -/
    //----------------/

    public Reservation() {
        super();
    } //-- xmax.crs.Amadeus.negospace.Reservation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getControlNumber()
    {
        return this._controlNumber;
    } //-- java.lang.String getControlNumber()


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
     * @param code
    **/
    public void setControlNumber(java.lang.String controlNumber)
    {
        this._controlNumber = controlNumber;
    } //-- void setCode(java.lang.String)


    /**
     *
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.Reservation unmarshalReservation(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.Reservation) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.Reservation.class, reader);
    } //-- xmax.crs.Amadeus.negospace.ControlNumber unmarshalReservation(java.io.Reader)

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate()

}
