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
 * @version $Revision: 3$ $Date: 02/25/2002 6:48:40 PM$
**/
public class LinkedNego implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _airlineCode;

    private int _flightNumber;

    /**
     * keeps track of state for field: _flightNumber
    **/
    private boolean _has_flightNumber;

    private java.lang.String _suffixOfFlight;

    private byte _changeDateIndic;

    /**
     * keeps track of state for field: _changeDateIndic
    **/
    private boolean _has_changeDateIndic;

    private java.lang.String _identifierOfClass;

    private java.lang.String _originCity;

    private java.lang.String _destinationCity;


      //----------------/
     //- Constructors -/
    //----------------/

    public LinkedNego() {
        super();
    } //-- xmax.crs.Amadeus.negospace.LinkedNego()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getAirlineCode()
    {
        return this._airlineCode;
    } //-- java.lang.String getAirlineCode() 

    /**
    **/
    public byte getChangeDateIndic()
    {
        return this._changeDateIndic;
    } //-- byte getChangeDateIndic() 

    /**
    **/
    public java.lang.String getDestinationCity()
    {
        return this._destinationCity;
    } //-- java.lang.String getDestinationCity() 

    /**
    **/
    public int getFlightNumber()
    {
        return this._flightNumber;
    } //-- int getFlightNumber() 

    /**
    **/
    public java.lang.String getIdentifierOfClass()
    {
        return this._identifierOfClass;
    } //-- java.lang.String getIdentifierOfClass() 

    /**
    **/
    public java.lang.String getOriginCity()
    {
        return this._originCity;
    } //-- java.lang.String getOriginCity() 

    /**
    **/
    public java.lang.String getSuffixOfFlight()
    {
        return this._suffixOfFlight;
    } //-- java.lang.String getSuffixOfFlight() 

    /**
    **/
    public boolean hasChangeDateIndic()
    {
        return this._has_changeDateIndic;
    } //-- boolean hasChangeDateIndic() 

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
     * @param changeDateIndic
    **/
    public void setChangeDateIndic(byte changeDateIndic)
    {
        this._changeDateIndic = changeDateIndic;
        this._has_changeDateIndic = true;
    } //-- void setChangeDateIndic(byte) 

    /**
     * 
     * @param destinationCity
    **/
    public void setDestinationCity(java.lang.String destinationCity)
    {
        this._destinationCity = destinationCity;
    } //-- void setDestinationCity(java.lang.String) 

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
     * @param identifierOfClass
    **/
    public void setIdentifierOfClass(java.lang.String identifierOfClass)
    {
        this._identifierOfClass = identifierOfClass;
    } //-- void setIdentifierOfClass(java.lang.String) 

    /**
     * 
     * @param originCity
    **/
    public void setOriginCity(java.lang.String originCity)
    {
        this._originCity = originCity;
    } //-- void setOriginCity(java.lang.String) 

    /**
     * 
     * @param suffixOfFlight
    **/
    public void setSuffixOfFlight(java.lang.String suffixOfFlight)
    {
        this._suffixOfFlight = suffixOfFlight;
    } //-- void setSuffixOfFlight(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.LinkedNego unmarshalLinkedNego(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.LinkedNego) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.LinkedNego.class, reader);
    } //-- xmax.crs.Amadeus.negospace.LinkedNego unmarshalLinkedNego(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
