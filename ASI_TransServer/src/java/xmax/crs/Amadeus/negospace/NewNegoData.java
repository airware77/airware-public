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
 * @version $Revision: 1$ $Date: 02/12/2002 2:47:21 PM$
**/
public class NewNegoData implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private ProductDateInfoType _productDateInfo;

    private SeatQuantityType _seatQuantity;

    private AdditionalInfoType _additionalInfo;


      //----------------/
     //- Constructors -/
    //----------------/

    public NewNegoData() {
        super();
    } //-- xmax.crs.Amadeus.negospace.NewNegoData()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public AdditionalInfoType getAdditionalInfo()
    {
        return this._additionalInfo;
    } //-- AdditionalInfoType getAdditionalInfo() 

    /**
    **/
    public ProductDateInfoType getProductDateInfo()
    {
        return this._productDateInfo;
    } //-- ProductDateInfoType getProductDateInfo() 

    /**
    **/
    public SeatQuantityType getSeatQuantity()
    {
        return this._seatQuantity;
    } //-- SeatQuantityType getSeatQuantity() 

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
     * @param additionalInfo
    **/
    public void setAdditionalInfo(AdditionalInfoType additionalInfo)
    {
        this._additionalInfo = additionalInfo;
    } //-- void setAdditionalInfo(AdditionalInfoType) 

    /**
     * 
     * @param productDateInfo
    **/
    public void setProductDateInfo(ProductDateInfoType productDateInfo)
    {
        this._productDateInfo = productDateInfo;
    } //-- void setProductDateInfo(ProductDateInfoType) 

    /**
     * 
     * @param seatQuantity
    **/
    public void setSeatQuantity(SeatQuantityType seatQuantity)
    {
        this._seatQuantity = seatQuantity;
    } //-- void setSeatQuantity(SeatQuantityType) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.NewNegoData unmarshalNewNegoData(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.NewNegoData) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.NewNegoData.class, reader);
    } //-- xmax.crs.Amadeus.negospace.NewNegoData unmarshalNewNegoData(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
