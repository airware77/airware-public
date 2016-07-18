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
 * @version $Revision: 1$ $Date: 02/08/2002 8:17:25 PM$
**/
public class TextInformationType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private FreeTextQualification _freeTextQualification;

    private java.util.ArrayList _freeTextList;


      //----------------/
     //- Constructors -/
    //----------------/

    public TextInformationType() {
        super();
        _freeTextList = new ArrayList();
    } //-- xmax.crs.Amadeus.negospace.TextInformationType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vFreeText
    **/
    public void addFreeText(java.lang.String vFreeText)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_freeTextList.size() < 5)) {
            throw new IndexOutOfBoundsException();
        }
        _freeTextList.add(vFreeText);
    } //-- void addFreeText(java.lang.String) 

    /**
     * 
     * @param index
     * @param vFreeText
    **/
    public void addFreeText(int index, java.lang.String vFreeText)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_freeTextList.size() < 5)) {
            throw new IndexOutOfBoundsException();
        }
        _freeTextList.add(index, vFreeText);
    } //-- void addFreeText(int, java.lang.String) 

    /**
    **/
    public void clearFreeText()
    {
        _freeTextList.clear();
    } //-- void clearFreeText() 

    /**
    **/
    public java.util.Enumeration enumerateFreeText()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_freeTextList.iterator());
    } //-- java.util.Enumeration enumerateFreeText() 

    /**
     * 
     * @param index
    **/
    public java.lang.String getFreeText(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _freeTextList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_freeTextList.get(index);
    } //-- java.lang.String getFreeText(int) 

    /**
    **/
    public java.lang.String[] getFreeText()
    {
        int size = _freeTextList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_freeTextList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getFreeText() 

    /**
    **/
    public int getFreeTextCount()
    {
        return _freeTextList.size();
    } //-- int getFreeTextCount() 

    /**
    **/
    public FreeTextQualification getFreeTextQualification()
    {
        return this._freeTextQualification;
    } //-- FreeTextQualification getFreeTextQualification() 

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
     * @param vFreeText
    **/
    public boolean removeFreeText(java.lang.String vFreeText)
    {
        boolean removed = _freeTextList.remove(vFreeText);
        return removed;
    } //-- boolean removeFreeText(java.lang.String) 

    /**
     * 
     * @param index
     * @param vFreeText
    **/
    public void setFreeText(int index, java.lang.String vFreeText)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _freeTextList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 5)) {
            throw new IndexOutOfBoundsException();
        }
        _freeTextList.set(index, vFreeText);
    } //-- void setFreeText(int, java.lang.String) 

    /**
     * 
     * @param freeTextArray
    **/
    public void setFreeText(java.lang.String[] freeTextArray)
    {
        //-- copy array
        _freeTextList.clear();
        for (int i = 0; i < freeTextArray.length; i++) {
            _freeTextList.add(freeTextArray[i]);
        }
    } //-- void setFreeText(java.lang.String) 

    /**
     * 
     * @param freeTextQualification
    **/
    public void setFreeTextQualification(FreeTextQualification freeTextQualification)
    {
        this._freeTextQualification = freeTextQualification;
    } //-- void setFreeTextQualification(FreeTextQualification) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.TextInformationType unmarshalTextInformationType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.TextInformationType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.TextInformationType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.TextInformationType unmarshalTextInformationType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
