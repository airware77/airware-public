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
 * @version $Revision: 2$ $Date: 02/25/2002 6:48:48 PM$
**/
public class ProductInfoType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * 
     *         This should be a required node, but because what
     * should be the
     *         'productDateInfo' node is displayed as the
     * 'productInfo' node in
     *         PoweredAir_DisplayNegoSpaceReply documents, we are
     * forced to make this
     *         an optional node so as to keep the xsd simple.
     *         
    **/
    private java.lang.String _flightDepartureDate;

    /**
     * 
     *         This only appears here in
     * PoweredAir_DisplayNegoSpaceReply documents;
     *         it was obviously meant to appear in the node below,
     * where it appears
     *         for all other query/replies
     *         
    **/
    private java.lang.String _commencementDateForSale;

    /**
     * 
     *         This only appears here in
     * PoweredAir_DisplayNegoSpaceReply documents;
     *         it was obviously meant to appear in the node below,
     * where it appears
     *         for all other query/replies
     *         
    **/
    private java.lang.String _expiryNegoDate;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProductInfoType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.ProductInfoType()


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
    public java.lang.String getFlightDepartureDate()
    {
        return this._flightDepartureDate;
    } //-- java.lang.String getFlightDepartureDate() 

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
     * @param flightDepartureDate
    **/
    public void setFlightDepartureDate(java.lang.String flightDepartureDate)
    {
        this._flightDepartureDate = flightDepartureDate;
    } //-- void setFlightDepartureDate(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.ProductInfoType unmarshalProductInfoType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.ProductInfoType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.ProductInfoType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.ProductInfoType unmarshalProductInfoType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
