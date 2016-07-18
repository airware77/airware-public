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
import java.util.ArrayList;
import java.util.Enumeration;
import org.exolab.castor.xml.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.DocumentHandler;

/**
 * 
 * @version $Revision: 1$ $Date: 02/08/2002 8:17:24 PM$
**/
public class PoweredAir_DisplayNegoSpaceReply implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private StatusInformationType _statusInformation;

    private java.util.ArrayList _negotiatedSpaceInfoList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PoweredAir_DisplayNegoSpaceReply() {
        super();
        _negotiatedSpaceInfoList = new ArrayList();
    } //-- xmax.crs.Amadeus.negospace.PoweredAir_DisplayNegoSpaceReply()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vNegotiatedSpaceInfo
    **/
    public void addNegotiatedSpaceInfo(NegotiatedSpaceInfo vNegotiatedSpaceInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_negotiatedSpaceInfoList.size() < 99)) {
            throw new IndexOutOfBoundsException();
        }
        _negotiatedSpaceInfoList.add(vNegotiatedSpaceInfo);
    } //-- void addNegotiatedSpaceInfo(NegotiatedSpaceInfo) 

    /**
     * 
     * @param index
     * @param vNegotiatedSpaceInfo
    **/
    public void addNegotiatedSpaceInfo(int index, NegotiatedSpaceInfo vNegotiatedSpaceInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_negotiatedSpaceInfoList.size() < 99)) {
            throw new IndexOutOfBoundsException();
        }
        _negotiatedSpaceInfoList.add(index, vNegotiatedSpaceInfo);
    } //-- void addNegotiatedSpaceInfo(int, NegotiatedSpaceInfo) 

    /**
    **/
    public void clearNegotiatedSpaceInfo()
    {
        _negotiatedSpaceInfoList.clear();
    } //-- void clearNegotiatedSpaceInfo() 

    /**
    **/
    public java.util.Enumeration enumerateNegotiatedSpaceInfo()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_negotiatedSpaceInfoList.iterator());
    } //-- java.util.Enumeration enumerateNegotiatedSpaceInfo() 

    /**
     * 
     * @param index
    **/
    public NegotiatedSpaceInfo getNegotiatedSpaceInfo(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _negotiatedSpaceInfoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (NegotiatedSpaceInfo) _negotiatedSpaceInfoList.get(index);
    } //-- NegotiatedSpaceInfo getNegotiatedSpaceInfo(int) 

    /**
    **/
    public NegotiatedSpaceInfo[] getNegotiatedSpaceInfo()
    {
        int size = _negotiatedSpaceInfoList.size();
        NegotiatedSpaceInfo[] mArray = new NegotiatedSpaceInfo[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (NegotiatedSpaceInfo) _negotiatedSpaceInfoList.get(index);
        }
        return mArray;
    } //-- NegotiatedSpaceInfo[] getNegotiatedSpaceInfo() 

    /**
    **/
    public int getNegotiatedSpaceInfoCount()
    {
        return _negotiatedSpaceInfoList.size();
    } //-- int getNegotiatedSpaceInfoCount() 

    /**
    **/
    public StatusInformationType getStatusInformation()
    {
        return this._statusInformation;
    } //-- StatusInformationType getStatusInformation() 

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
     * @param vNegotiatedSpaceInfo
    **/
    public boolean removeNegotiatedSpaceInfo(NegotiatedSpaceInfo vNegotiatedSpaceInfo)
    {
        boolean removed = _negotiatedSpaceInfoList.remove(vNegotiatedSpaceInfo);
        return removed;
    } //-- boolean removeNegotiatedSpaceInfo(NegotiatedSpaceInfo) 

    /**
     * 
     * @param index
     * @param vNegotiatedSpaceInfo
    **/
    public void setNegotiatedSpaceInfo(int index, NegotiatedSpaceInfo vNegotiatedSpaceInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _negotiatedSpaceInfoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 99)) {
            throw new IndexOutOfBoundsException();
        }
        _negotiatedSpaceInfoList.set(index, vNegotiatedSpaceInfo);
    } //-- void setNegotiatedSpaceInfo(int, NegotiatedSpaceInfo) 

    /**
     * 
     * @param negotiatedSpaceInfoArray
    **/
    public void setNegotiatedSpaceInfo(NegotiatedSpaceInfo[] negotiatedSpaceInfoArray)
    {
        //-- copy array
        _negotiatedSpaceInfoList.clear();
        for (int i = 0; i < negotiatedSpaceInfoArray.length; i++) {
            _negotiatedSpaceInfoList.add(negotiatedSpaceInfoArray[i]);
        }
    } //-- void setNegotiatedSpaceInfo(NegotiatedSpaceInfo) 

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
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.PoweredAir_DisplayNegoSpaceReply unmarshalPoweredAir_DisplayNegoSpaceReply(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.PoweredAir_DisplayNegoSpaceReply) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.PoweredAir_DisplayNegoSpaceReply.class, reader);
    } //-- xmax.crs.Amadeus.negospace.PoweredAir_DisplayNegoSpaceReply unmarshalPoweredAir_DisplayNegoSpaceReply(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
