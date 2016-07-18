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
 * @version $Revision: 1$ $Date: 02/08/2002 8:17:21 PM$
**/
public class NegoDisplayRequest implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private NegoDetailsType _negotiatedSpaceDetails;

    private LocationDetailsType _locationDetails;

    private DateRangeType _dateRange;


      //----------------/
     //- Constructors -/
    //----------------/

    public NegoDisplayRequest() {
        super();
    } //-- xmax.crs.Amadeus.negospace.NegoDisplayRequest()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public DateRangeType getDateRange()
    {
        return this._dateRange;
    } //-- DateRangeType getDateRange() 

    /**
    **/
    public LocationDetailsType getLocationDetails()
    {
        return this._locationDetails;
    } //-- LocationDetailsType getLocationDetails() 

    /**
    **/
    public NegoDetailsType getNegotiatedSpaceDetails()
    {
        return this._negotiatedSpaceDetails;
    } //-- NegoDetailsType getNegotiatedSpaceDetails() 

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
     * @param dateRange
    **/
    public void setDateRange(DateRangeType dateRange)
    {
        this._dateRange = dateRange;
    } //-- void setDateRange(DateRangeType) 

    /**
     * 
     * @param locationDetails
    **/
    public void setLocationDetails(LocationDetailsType locationDetails)
    {
        this._locationDetails = locationDetails;
    } //-- void setLocationDetails(LocationDetailsType) 

    /**
     * 
     * @param negotiatedSpaceDetails
    **/
    public void setNegotiatedSpaceDetails(NegoDetailsType negotiatedSpaceDetails)
    {
        this._negotiatedSpaceDetails = negotiatedSpaceDetails;
    } //-- void setNegotiatedSpaceDetails(NegoDetailsType) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.NegoDisplayRequest unmarshalNegoDisplayRequest(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.NegoDisplayRequest) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.NegoDisplayRequest.class, reader);
    } //-- xmax.crs.Amadeus.negospace.NegoDisplayRequest unmarshalNegoDisplayRequest(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
