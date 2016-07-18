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
 * @version $Revision: 2$ $Date: 02/25/2002 6:49:12 PM$
**/
public class NegotiatedSpaceInfo implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private NegoDetailsType _negotiatedSpaceDetails;

    private LocationDetailsType _locationDetails;

    private java.util.ArrayList _linkedNegotiatedSpaceListList;


      //----------------/
     //- Constructors -/
    //----------------/

    public NegotiatedSpaceInfo() {
        super();
        _linkedNegotiatedSpaceListList = new ArrayList();
    } //-- xmax.crs.Amadeus.negospace.NegotiatedSpaceInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vLinkedNegotiatedSpaceList
    **/
    public void addLinkedNegotiatedSpaceList(LinkedNegoListType vLinkedNegotiatedSpaceList)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_linkedNegotiatedSpaceListList.size() < 6)) {
            throw new IndexOutOfBoundsException();
        }
        _linkedNegotiatedSpaceListList.add(vLinkedNegotiatedSpaceList);
    } //-- void addLinkedNegotiatedSpaceList(LinkedNegoListType) 

    /**
     * 
     * @param index
     * @param vLinkedNegotiatedSpaceList
    **/
    public void addLinkedNegotiatedSpaceList(int index, LinkedNegoListType vLinkedNegotiatedSpaceList)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_linkedNegotiatedSpaceListList.size() < 6)) {
            throw new IndexOutOfBoundsException();
        }
        _linkedNegotiatedSpaceListList.add(index, vLinkedNegotiatedSpaceList);
    } //-- void addLinkedNegotiatedSpaceList(int, LinkedNegoListType) 

    /**
    **/
    public void clearLinkedNegotiatedSpaceList()
    {
        _linkedNegotiatedSpaceListList.clear();
    } //-- void clearLinkedNegotiatedSpaceList() 

    /**
    **/
    public java.util.Enumeration enumerateLinkedNegotiatedSpaceList()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_linkedNegotiatedSpaceListList.iterator());
    } //-- java.util.Enumeration enumerateLinkedNegotiatedSpaceList() 

    /**
     * 
     * @param index
    **/
    public LinkedNegoListType getLinkedNegotiatedSpaceList(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _linkedNegotiatedSpaceListList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (LinkedNegoListType) _linkedNegotiatedSpaceListList.get(index);
    } //-- LinkedNegoListType getLinkedNegotiatedSpaceList(int) 

    /**
    **/
    public LinkedNegoListType[] getLinkedNegotiatedSpaceList()
    {
        int size = _linkedNegotiatedSpaceListList.size();
        LinkedNegoListType[] mArray = new LinkedNegoListType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (LinkedNegoListType) _linkedNegotiatedSpaceListList.get(index);
        }
        return mArray;
    } //-- LinkedNegoListType[] getLinkedNegotiatedSpaceList() 

    /**
    **/
    public int getLinkedNegotiatedSpaceListCount()
    {
        return _linkedNegotiatedSpaceListList.size();
    } //-- int getLinkedNegotiatedSpaceListCount() 

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
     * @param vLinkedNegotiatedSpaceList
    **/
    public boolean removeLinkedNegotiatedSpaceList(LinkedNegoListType vLinkedNegotiatedSpaceList)
    {
        boolean removed = _linkedNegotiatedSpaceListList.remove(vLinkedNegotiatedSpaceList);
        return removed;
    } //-- boolean removeLinkedNegotiatedSpaceList(LinkedNegoListType) 

    /**
     * 
     * @param index
     * @param vLinkedNegotiatedSpaceList
    **/
    public void setLinkedNegotiatedSpaceList(int index, LinkedNegoListType vLinkedNegotiatedSpaceList)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _linkedNegotiatedSpaceListList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 6)) {
            throw new IndexOutOfBoundsException();
        }
        _linkedNegotiatedSpaceListList.set(index, vLinkedNegotiatedSpaceList);
    } //-- void setLinkedNegotiatedSpaceList(int, LinkedNegoListType) 

    /**
     * 
     * @param linkedNegotiatedSpaceListArray
    **/
    public void setLinkedNegotiatedSpaceList(LinkedNegoListType[] linkedNegotiatedSpaceListArray)
    {
        //-- copy array
        _linkedNegotiatedSpaceListList.clear();
        for (int i = 0; i < linkedNegotiatedSpaceListArray.length; i++) {
            _linkedNegotiatedSpaceListList.add(linkedNegotiatedSpaceListArray[i]);
        }
    } //-- void setLinkedNegotiatedSpaceList(LinkedNegoListType) 

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
    public static xmax.crs.Amadeus.negospace.NegotiatedSpaceInfo unmarshalNegotiatedSpaceInfo(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.NegotiatedSpaceInfo) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.NegotiatedSpaceInfo.class, reader);
    } //-- xmax.crs.Amadeus.negospace.NegotiatedSpaceInfo unmarshalNegotiatedSpaceInfo(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
