/*
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
 * @version $Revision: 2$ $Date: 02/08/2002 8:14:50 PM$
**/
public class FlightDetailsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _airlineCode;

    private int _flightNumber;

    /**
     * keeps track of state for field: _flightNumber
    **/
    private boolean _has_flightNumber;

    private java.lang.String _flightSuffix;

    private java.lang.String _identifierOfClass;


      //----------------/
     //- Constructors -/
    //----------------/

    public FlightDetailsType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.FlightDetailsType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteFlightNumber()
    {
        this._has_flightNumber= false;
    } //-- void deleteFlightNumber() 

    /**
    **/
    public java.lang.String getAirlineCode()
    {
        return this._airlineCode;
    } //-- java.lang.String getAirlineCode() 

    /**
    **/
    public int getFlightNumber()
    {
        return this._flightNumber;
    } //-- int getFlightNumber() 

    /**
    **/
    public java.lang.String getFlightSuffix()
    {
        return this._flightSuffix;
    } //-- java.lang.String getFlightSuffix() 

    /**
    **/
    public java.lang.String getIdentifierOfClass()
    {
        return this._identifierOfClass;
    } //-- java.lang.String getIdentifierOfClass() 

    /**
    **/
    public boolean hasFlightNumber()
    {
        return this._has_flightNumber;
    } //-- boolean hasFlightNumber() 

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
     * @param airlineCode
    **/
    public void setAirlineCode(java.lang.String airlineCode)
    {
        this._airlineCode = airlineCode;
    } //-- void setAirlineCode(java.lang.String) 

    /**
     * 
     * @param flightNumber
    **/
    public void setFlightNumber(int flightNumber)
    {
        this._flightNumber = flightNumber;
        this._has_flightNumber = true;
    } //-- void setFlightNumber(int) 

    /**
     * 
     * @param flightSuffix
    **/
    public void setFlightSuffix(java.lang.String flightSuffix)
    {
        this._flightSuffix = flightSuffix;
    } //-- void setFlightSuffix(java.lang.String) 

    /**
     * 
     * @param identifierOfClass
    **/
    public void setIdentifierOfClass(java.lang.String identifierOfClass)
    {
        this._identifierOfClass = identifierOfClass;
    } //-- void setIdentifierOfClass(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.FlightDetailsType unmarshalFlightDetailsType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.FlightDetailsType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.FlightDetailsType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.FlightDetailsType unmarshalFlightDetailsType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
