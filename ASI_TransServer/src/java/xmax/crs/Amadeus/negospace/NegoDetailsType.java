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
 *     The NegoDetailsType is the type of the
 * negotiatedSpaceDetails node that
 *     appears in all the negospace query verbs, and in the display
 * reply.
 *     The type below is generic and is meant to encompass all the
 * query/reply
 *     documents which contain it.  As such, this type does not
 * properly reflect
 *     the different validation rules that apply to this node in
 * the different
 *     documents. 
 *     Since the AmadeusAPI is not namespace-aware, it is not
 * possible to describe
 *     these rules without generating a confusing amount of similar
 * classes when
 *     using Castor.
 *     
 * @version $Revision: 3$ $Date: 02/26/2002 4:31:40 PM$
**/
public class NegoDetailsType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * 
     *         This node is actually compulsory, but the fact that
     * it is mispelled in
     *         the PoweredAir_ChangeNegoSpace query forces us to
     * specify this node as
     *         optional, when it is required in all documents
     *         
    **/
    private FlightDetailsType _flightDetails;

    /**
     * 
     *         Another Amadeus typo! this one occurs exclusively in
     * the
     *         PoweredAir_ChangeNegoSpace query
     *         
    **/
    private FlightDetailsType _fligthDetails;

    private java.util.ArrayList _productInfoList;

    private ProductDateInfoType _productDateInfo;

    private SeatQuantityType _seatQuantity;

    private java.lang.String _ownerId;

    private AdditionalInfoType _additionalInfo;

    /**
     * 
     *         a numeric key that identifies multiple instances of
     * the same
     *         flight/departure date/class block (alternate key for
     * Tour Reference)
     *         
    **/
    private int _nego1Aid;

    /**
     * keeps track of state for field: _nego1Aid
    **/
    private boolean _has_nego1Aid;

    private java.lang.String _negoRloc;

    private java.lang.String _eventName;

    private java.lang.String _handlingTable;

    private java.lang.String _signLevelHandlingTable;

    /**
     * Only displayed in PoweredAir_DisplayNegoSpaceReply
    **/
    private java.lang.String _allotmentStatus;

    /**
     * Only used in PoweredAir_DisplayNegoSpace
    **/
    private java.lang.String _searchOption;


      //----------------/
     //- Constructors -/
    //----------------/

    public NegoDetailsType() {
        super();
        _productInfoList = new ArrayList();
    } //-- xmax.crs.Amadeus.negospace.NegoDetailsType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * @param vProductInfo
    **/
    public void addProductInfo(ProductInfoType vProductInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_productInfoList.size() < 2)) {
            throw new IndexOutOfBoundsException();
        }
        _productInfoList.add(vProductInfo);
    } //-- void addProductInfo(ProductInfoType) 

    /**
     * 
     * @param index
     * @param vProductInfo
    **/
    public void addProductInfo(int index, ProductInfoType vProductInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        if (!(_productInfoList.size() < 2)) {
            throw new IndexOutOfBoundsException();
        }
        _productInfoList.add(index, vProductInfo);
    } //-- void addProductInfo(int, ProductInfoType) 

    /**
    **/
    public void clearProductInfo()
    {
        _productInfoList.clear();
    } //-- void clearProductInfo() 

    /**
    **/
    public void deleteNego1Aid()
    {
        this._has_nego1Aid= false;
    } //-- void deleteNego1Aid() 

    /**
    **/
    public java.util.Enumeration enumerateProductInfo()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_productInfoList.iterator());
    } //-- java.util.Enumeration enumerateProductInfo() 

    /**
    **/
    public AdditionalInfoType getAdditionalInfo()
    {
        return this._additionalInfo;
    } //-- AdditionalInfoType getAdditionalInfo() 

    /**
    **/
    public java.lang.String getAllotmentStatus()
    {
        return this._allotmentStatus;
    } //-- java.lang.String getAllotmentStatus() 

    /**
    **/
    public java.lang.String getEventName()
    {
        return this._eventName;
    } //-- java.lang.String getEventName() 

    /**
    **/
    public FlightDetailsType getFlightDetails()
    {
        return this._flightDetails;
    } //-- FlightDetailsType getFlightDetails() 

    /**
    **/
    public FlightDetailsType getFligthDetails()
    {
        return this._fligthDetails;
    } //-- FlightDetailsType getFligthDetails() 

    /**
    **/
    public java.lang.String getHandlingTable()
    {
        return this._handlingTable;
    } //-- java.lang.String getHandlingTable() 

    /**
    **/
    public int getNego1Aid()
    {
        return this._nego1Aid;
    } //-- int getNego1Aid() 

    /**
    **/
    public java.lang.String getNegoRloc()
    {
        return this._negoRloc;
    } //-- java.lang.String getNegoRloc() 

    /**
    **/
    public java.lang.String getOwnerId()
    {
        return this._ownerId;
    } //-- java.lang.String getOwnerId() 

    /**
    **/
    public ProductDateInfoType getProductDateInfo()
    {
        return this._productDateInfo;
    } //-- ProductDateInfoType getProductDateInfo() 

    /**
     * 
     * @param index
    **/
    public ProductInfoType getProductInfo(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _productInfoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ProductInfoType) _productInfoList.get(index);
    } //-- ProductInfoType getProductInfo(int) 

    /**
    **/
    public ProductInfoType[] getProductInfo()
    {
        int size = _productInfoList.size();
        ProductInfoType[] mArray = new ProductInfoType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ProductInfoType) _productInfoList.get(index);
        }
        return mArray;
    } //-- ProductInfoType[] getProductInfo() 

    /**
    **/
    public int getProductInfoCount()
    {
        return _productInfoList.size();
    } //-- int getProductInfoCount() 

    /**
    **/
    public java.lang.String getSearchOption()
    {
        return this._searchOption;
    } //-- java.lang.String getSearchOption() 

    /**
    **/
    public SeatQuantityType getSeatQuantity()
    {
        return this._seatQuantity;
    } //-- SeatQuantityType getSeatQuantity() 

    /**
    **/
    public java.lang.String getSignLevelHandlingTable()
    {
        return this._signLevelHandlingTable;
    } //-- java.lang.String getSignLevelHandlingTable() 

    /**
    **/
    public boolean hasNego1Aid()
    {
        return this._has_nego1Aid;
    } //-- boolean hasNego1Aid() 

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
     * @param vProductInfo
    **/
    public boolean removeProductInfo(ProductInfoType vProductInfo)
    {
        boolean removed = _productInfoList.remove(vProductInfo);
        return removed;
    } //-- boolean removeProductInfo(ProductInfoType) 

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
     * @param allotmentStatus
    **/
    public void setAllotmentStatus(java.lang.String allotmentStatus)
    {
        this._allotmentStatus = allotmentStatus;
    } //-- void setAllotmentStatus(java.lang.String) 

    /**
     * 
     * @param eventName
    **/
    public void setEventName(java.lang.String eventName)
    {
        this._eventName = eventName;
    } //-- void setEventName(java.lang.String) 

    /**
     * 
     * @param flightDetails
    **/
    public void setFlightDetails(FlightDetailsType flightDetails)
    {
        this._flightDetails = flightDetails;
    } //-- void setFlightDetails(FlightDetailsType) 

    /**
     * 
     * @param fligthDetails
    **/
    public void setFligthDetails(FlightDetailsType fligthDetails)
    {
        this._fligthDetails = fligthDetails;
    } //-- void setFligthDetails(FlightDetailsType) 

    /**
     * 
     * @param handlingTable
    **/
    public void setHandlingTable(java.lang.String handlingTable)
    {
        this._handlingTable = handlingTable;
    } //-- void setHandlingTable(java.lang.String) 

    /**
     * 
     * @param nego1Aid
    **/
    public void setNego1Aid(int nego1Aid)
    {
        this._nego1Aid = nego1Aid;
        this._has_nego1Aid = true;
    } //-- void setNego1Aid(int) 

    /**
     * 
     * @param negoRloc
    **/
    public void setNegoRloc(java.lang.String negoRloc)
    {
        this._negoRloc = negoRloc;
    } //-- void setNegoRloc(java.lang.String) 

    /**
     * 
     * @param ownerId
    **/
    public void setOwnerId(java.lang.String ownerId)
    {
        this._ownerId = ownerId;
    } //-- void setOwnerId(java.lang.String) 

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
     * @param index
     * @param vProductInfo
    **/
    public void setProductInfo(int index, ProductInfoType vProductInfo)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _productInfoList.size())) {
            throw new IndexOutOfBoundsException();
        }
        if (!(index < 2)) {
            throw new IndexOutOfBoundsException();
        }
        _productInfoList.set(index, vProductInfo);
    } //-- void setProductInfo(int, ProductInfoType) 

    /**
     * 
     * @param productInfoArray
    **/
    public void setProductInfo(ProductInfoType[] productInfoArray)
    {
        //-- copy array
        _productInfoList.clear();
        for (int i = 0; i < productInfoArray.length; i++) {
            _productInfoList.add(productInfoArray[i]);
        }
    } //-- void setProductInfo(ProductInfoType) 

    /**
     * 
     * @param searchOption
    **/
    public void setSearchOption(java.lang.String searchOption)
    {
        this._searchOption = searchOption;
    } //-- void setSearchOption(java.lang.String) 

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
     * @param signLevelHandlingTable
    **/
    public void setSignLevelHandlingTable(java.lang.String signLevelHandlingTable)
    {
        this._signLevelHandlingTable = signLevelHandlingTable;
    } //-- void setSignLevelHandlingTable(java.lang.String) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.NegoDetailsType unmarshalNegoDetailsType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.NegoDetailsType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.NegoDetailsType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.NegoDetailsType unmarshalNegoDetailsType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
