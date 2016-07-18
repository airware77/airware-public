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
 * @version $Revision: 2$ $Date: 02/25/2002 6:49:22 PM$
**/
public class PoweredAir_CreatePassiveNegoSpaceReply implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StatusInformationType _statusInformation;
		private RecordLocatorType _recordLocator;


      //----------------/
     //- Constructors -/
    //----------------/

    public PoweredAir_CreatePassiveNegoSpaceReply() {
        super();
    } //-- xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpaceReply()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public StatusInformationType getStatusInformation()
    {
        return this._statusInformation;
    } //-- StatusInformationType getStatusInformation()

		/**
    **/
    public RecordLocatorType getRecordLocator()
    {
        return this._recordLocator;
    } //-- RecordLocatorTypeType getRecordLocatorType()


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
     * @param statusInformation
    **/
    public void setStatusInformation(StatusInformationType statusInformation)
    {
        this._statusInformation = statusInformation;
    } //-- void setStatusInformation(StatusInformationType)

		/**
     *
     * @param recordLocator
    **/
    public void setRecordLocator(RecordLocatorType recordLocator)
    {
        this._recordLocator = recordLocator;
    } //-- void recordLocator(RecordLocatorType)


		/**
     *
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpaceReply unmarshalPoweredAir_CreatePassiveNegoSpaceReply(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpaceReply) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpaceReply.class, reader);
    } //-- xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpaceReply unmarshalPoweredAir_CreatePassiveNegoSpaceReply(java.io.Reader)

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate()

}
