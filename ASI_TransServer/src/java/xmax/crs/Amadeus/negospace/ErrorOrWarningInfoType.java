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
 * @version $Revision: 1$ $Date: 02/08/2002 8:17:19 PM$
**/
public class ErrorOrWarningInfoType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private Error _error;


      //----------------/
     //- Constructors -/
    //----------------/

    public ErrorOrWarningInfoType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.ErrorOrWarningInfoType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public Error getError()
    {
        return this._error;
    } //-- Error getError() 

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
     * @param error
    **/
    public void setError(Error error)
    {
        this._error = error;
    } //-- void setError(Error) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.ErrorOrWarningInfoType unmarshalErrorOrWarningInfoType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.ErrorOrWarningInfoType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.ErrorOrWarningInfoType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.ErrorOrWarningInfoType unmarshalErrorOrWarningInfoType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
