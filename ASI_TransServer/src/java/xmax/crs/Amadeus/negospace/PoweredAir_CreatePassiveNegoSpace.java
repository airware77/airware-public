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
 * @version $Revision: 1$ $Date: 02/12/2002 6:40:32 PM$
**/
public class PoweredAir_CreatePassiveNegoSpace implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private NegotiatedSpaceInformation _negotiatedSpaceInformation;


      //----------------/
     //- Constructors -/
    //----------------/

    public PoweredAir_CreatePassiveNegoSpace() {
        super();
    } //-- xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpace()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public NegotiatedSpaceInformation getNegotiatedSpaceInformation()
    {
        return this._negotiatedSpaceInformation;
    } //-- NegotiatedSpaceInformation getNegotiatedSpaceInformation() 

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
     * @param negotiatedSpaceInformation
    **/
    public void setNegotiatedSpaceInformation(NegotiatedSpaceInformation negotiatedSpaceInformation)
    {
        this._negotiatedSpaceInformation = negotiatedSpaceInformation;
    } //-- void setNegotiatedSpaceInformation(NegotiatedSpaceInformation) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpace unmarshalPoweredAir_CreatePassiveNegoSpace(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpace) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpace.class, reader);
    } //-- xmax.crs.Amadeus.negospace.PoweredAir_CreatePassiveNegoSpace unmarshalPoweredAir_CreatePassiveNegoSpace(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
