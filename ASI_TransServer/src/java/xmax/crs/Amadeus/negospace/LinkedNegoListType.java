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
 * @version $Revision: 1$ $Date: 01/29/2002 2:47:59 PM$
**/
public class LinkedNegoListType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private LinkedNego _linkedNego;


      //----------------/
     //- Constructors -/
    //----------------/

    public LinkedNegoListType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.LinkedNegoListType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public LinkedNego getLinkedNego()
    {
        return this._linkedNego;
    } //-- LinkedNego getLinkedNego() 

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
     * @param linkedNego
    **/
    public void setLinkedNego(LinkedNego linkedNego)
    {
        this._linkedNego = linkedNego;
    } //-- void setLinkedNego(LinkedNego) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.LinkedNegoListType unmarshalLinkedNegoListType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.LinkedNegoListType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.LinkedNegoListType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.LinkedNegoListType unmarshalLinkedNegoListType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
