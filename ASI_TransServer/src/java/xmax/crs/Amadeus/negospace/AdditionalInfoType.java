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
 * @version $Revision: 2$ $Date: 02/08/2002 8:14:59 PM$
**/
public class AdditionalInfoType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _negoReductionDate;

    private byte _percentageOfReduction;

    /**
     * keeps track of state for field: _percentageOfReduction
    **/
    private boolean _has_percentageOfReduction;

    private java.lang.String _tourName;

    private java.lang.String _tourReference;

    private java.lang.String _authorizationCode;

    private java.lang.String _typeOfNegoLink;

    /**
     * The Airline Record Locator
    **/
    private java.lang.String _alidValue;


      //----------------/
     //- Constructors -/
    //----------------/

    public AdditionalInfoType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.AdditionalInfoType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deletePercentageOfReduction()
    {
        this._has_percentageOfReduction= false;
    } //-- void deletePercentageOfReduction() 

    /**
    **/
    public java.lang.String getAlidValue()
    {
        return this._alidValue;
    } //-- java.lang.String getAlidValue() 

    /**
    **/
    public java.lang.String getAuthorizationCode()
    {
        return this._authorizationCode;
    } //-- java.lang.String getAuthorizationCode() 

    /**
    **/
    public java.lang.String getNegoReductionDate()
    {
        return this._negoReductionDate;
    } //-- java.lang.String getNegoReductionDate() 

    /**
    **/
    public byte getPercentageOfReduction()
    {
        return this._percentageOfReduction;
    } //-- byte getPercentageOfReduction() 

    /**
    **/
    public java.lang.String getTourName()
    {
        return this._tourName;
    } //-- java.lang.String getTourName() 

    /**
    **/
    public java.lang.String getTourReference()
    {
        return this._tourReference;
    } //-- java.lang.String getTourReference() 

    /**
    **/
    public java.lang.String getTypeOfNegoLink()
    {
        return this._typeOfNegoLink;
    } //-- java.lang.String getTypeOfNegoLink() 

    /**
    **/
    public boolean hasPercentageOfReduction()
    {
        return this._has_percentageOfReduction;
    } //-- boolean hasPercentageOfReduction() 

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
     * @param alidValue
    **/
    public void setAlidValue(java.lang.String alidValue)
    {
        this._alidValue = alidValue;
    } //-- void setAlidValue(java.lang.String) 

    /**
     * 
     * @param authorizationCode
    **/
    public void setAuthorizationCode(java.lang.String authorizationCode)
    {
        this._authorizationCode = authorizationCode;
    } //-- void setAuthorizationCode(java.lang.String) 

    /**
     * 
     * @param negoReductionDate
    **/
    public void setNegoReductionDate(java.lang.String negoReductionDate)
    {
        this._negoReductionDate = negoReductionDate;
    } //-- void setNegoReductionDate(java.lang.String) 

    /**
     * 
     * @param percentageOfReduction
    **/
    public void setPercentageOfReduction(byte percentageOfReduction)
    {
        this._percentageOfReduction = percentageOfReduction;
        this._has_percentageOfReduction = true;
    } //-- void setPercentageOfReduction(byte) 

    /**
     * 
     * @param tourName
    **/
    public void setTourName(java.lang.String tourName)
    {
        this._tourName = tourName;
    } //-- void setTourName(java.lang.String) 

    /**
     * 
     * @param tourReference
    **/
    public void setTourReference(java.lang.String tourReference)
    {
        this._tourReference = tourReference;
    } //-- void setTourReference(java.lang.String) 

    /**
     * 
     * @param typeOfNegoLink
    **/
    public void setTypeOfNegoLink(java.lang.String typeOfNegoLink)
    {
        this._typeOfNegoLink = typeOfNegoLink;
    } //-- void setTypeOfNegoLink(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.AdditionalInfoType unmarshalAdditionalInfoType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.AdditionalInfoType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.AdditionalInfoType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.AdditionalInfoType unmarshalAdditionalInfoType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
