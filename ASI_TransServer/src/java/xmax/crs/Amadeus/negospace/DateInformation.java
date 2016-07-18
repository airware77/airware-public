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
 * @version $Revision: 1$ $Date: 02/08/2002 8:17:18 PM$
**/
public class DateInformation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _effectiveDate;

    private java.lang.String _discontinueDate;

    private int _operationDays;

    /**
     * keeps track of state for field: _operationDays
    **/
    private boolean _has_operationDays;


      //----------------/
     //- Constructors -/
    //----------------/

    public DateInformation() {
        super();
    } //-- xmax.crs.Amadeus.negospace.DateInformation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteOperationDays()
    {
        this._has_operationDays= false;
    } //-- void deleteOperationDays() 

    /**
    **/
    public java.lang.String getDiscontinueDate()
    {
        return this._discontinueDate;
    } //-- java.lang.String getDiscontinueDate() 

    /**
    **/
    public java.lang.String getEffectiveDate()
    {
        return this._effectiveDate;
    } //-- java.lang.String getEffectiveDate() 

    /**
    **/
    public int getOperationDays()
    {
        return this._operationDays;
    } //-- int getOperationDays() 

    /**
    **/
    public boolean hasOperationDays()
    {
        return this._has_operationDays;
    } //-- boolean hasOperationDays() 

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
     * @param discontinueDate
    **/
    public void setDiscontinueDate(java.lang.String discontinueDate)
    {
        this._discontinueDate = discontinueDate;
    } //-- void setDiscontinueDate(java.lang.String) 

    /**
     * 
     * @param effectiveDate
    **/
    public void setEffectiveDate(java.lang.String effectiveDate)
    {
        this._effectiveDate = effectiveDate;
    } //-- void setEffectiveDate(java.lang.String) 

    /**
     * 
     * @param operationDays
    **/
    public void setOperationDays(int operationDays)
    {
        this._operationDays = operationDays;
        this._has_operationDays = true;
    } //-- void setOperationDays(int) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.DateInformation unmarshalDateInformation(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.DateInformation) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.DateInformation.class, reader);
    } //-- xmax.crs.Amadeus.negospace.DateInformation unmarshalDateInformation(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
