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
 * @version $Revision: 2$ $Date: 02/08/2002 8:15:08 PM$
**/
public class FreeTextQualification implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _codedIndicator;


      //----------------/
     //- Constructors -/
    //----------------/

    public FreeTextQualification() {
        super();
    } //-- xmax.crs.Amadeus.negospace.FreeTextQualification()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getCodedIndicator()
    {
        return this._codedIndicator;
    } //-- java.lang.String getCodedIndicator() 

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
     * @param codedIndicator
    **/
    public void setCodedIndicator(java.lang.String codedIndicator)
    {
        this._codedIndicator = codedIndicator;
    } //-- void setCodedIndicator(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.FreeTextQualification unmarshalFreeTextQualification(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.FreeTextQualification) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.FreeTextQualification.class, reader);
    } //-- xmax.crs.Amadeus.negospace.FreeTextQualification unmarshalFreeTextQualification(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
