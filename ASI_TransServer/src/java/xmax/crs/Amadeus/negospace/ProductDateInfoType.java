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
 * @version $Revision: 1$ $Date: 01/29/2002 2:48:01 PM$
**/
public class ProductDateInfoType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _commencementDateForSale;

    private java.lang.String _expiryNegoDate;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProductDateInfoType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.ProductDateInfoType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public java.lang.String getCommencementDateForSale()
    {
        return this._commencementDateForSale;
    } //-- java.lang.String getCommencementDateForSale() 

    /**
    **/
    public java.lang.String getExpiryNegoDate()
    {
        return this._expiryNegoDate;
    } //-- java.lang.String getExpiryNegoDate() 

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
     * @param commencementDateForSale
    **/
    public void setCommencementDateForSale(java.lang.String commencementDateForSale)
    {
        this._commencementDateForSale = commencementDateForSale;
    } //-- void setCommencementDateForSale(java.lang.String) 

    /**
     * 
     * @param expiryNegoDate
    **/
    public void setExpiryNegoDate(java.lang.String expiryNegoDate)
    {
        this._expiryNegoDate = expiryNegoDate;
    } //-- void setExpiryNegoDate(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.ProductDateInfoType unmarshalProductDateInfoType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.ProductDateInfoType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.ProductDateInfoType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.ProductDateInfoType unmarshalProductDateInfoType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
