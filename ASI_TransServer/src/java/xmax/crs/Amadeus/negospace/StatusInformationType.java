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
 *       This type is included in the statusInformation node that
 * is returned by
 *       the response structures for Create, Display, Change and
 * Cancel Negospace;
 *       This set of nodes contains error/warning information in
 * the form of codes
 *       and multiline messages.
 *       In the case of Create, Change and Cancel, it is the only
 * node returned;
 *       In the case of Display, it occurs if there was a problem
 * displaying the
 *       NegotiatedSpaceInformation, or if a warning/informational
 *       messages is passed along with the
 * NegotiatedSpaceInformation.
 *       The Display reply returns the node 'erroOrWarningInfo',
 * rather than
 *       'errorOrWarningInfo'
 *       
 * @version $Revision: 3$ $Date: 02/25/2002 6:48:58 PM$
**/
public class StatusInformationType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private ErrorOrWarningInfoType _errorOrWarningInfo;

    private ErrorOrWarningInfoType _erroOrWarningInfo;

    private TextInformationType _textInformation;


      //----------------/
     //- Constructors -/
    //----------------/

    public StatusInformationType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.StatusInformationType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public ErrorOrWarningInfoType getErroOrWarningInfo()
    {
        return this._erroOrWarningInfo;
    } //-- ErrorOrWarningInfoType getErroOrWarningInfo() 

    /**
    **/
    public ErrorOrWarningInfoType getErrorOrWarningInfo()
    {
        return this._errorOrWarningInfo;
    } //-- ErrorOrWarningInfoType getErrorOrWarningInfo() 

    /**
    **/
    public TextInformationType getTextInformation()
    {
        return this._textInformation;
    } //-- TextInformationType getTextInformation() 

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
     * @param erroOrWarningInfo
    **/
    public void setErroOrWarningInfo(ErrorOrWarningInfoType erroOrWarningInfo)
    {
        this._erroOrWarningInfo = erroOrWarningInfo;
    } //-- void setErroOrWarningInfo(ErrorOrWarningInfoType) 

    /**
     * 
     * @param errorOrWarningInfo
    **/
    public void setErrorOrWarningInfo(ErrorOrWarningInfoType errorOrWarningInfo)
    {
        this._errorOrWarningInfo = errorOrWarningInfo;
    } //-- void setErrorOrWarningInfo(ErrorOrWarningInfoType) 

    /**
     * 
     * @param textInformation
    **/
    public void setTextInformation(TextInformationType textInformation)
    {
        this._textInformation = textInformation;
    } //-- void setTextInformation(TextInformationType) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.StatusInformationType unmarshalStatusInformationType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.StatusInformationType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.StatusInformationType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.StatusInformationType unmarshalStatusInformationType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
