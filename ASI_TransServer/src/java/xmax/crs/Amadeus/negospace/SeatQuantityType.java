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
 * @version $Revision: 2$ $Date: 02/08/2002 8:15:04 PM$
**/
public class SeatQuantityType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private int _numberOfAllocatedSeat;

    /**
     * keeps track of state for field: _numberOfAllocatedSeat
    **/
    private boolean _has_numberOfAllocatedSeat;

    /**
     * Only displayed in PoweredAir_DisplayNegoSpaceReply
    **/
    private int _numberOfSoldSeat;

    /**
     * keeps track of state for field: _numberOfSoldSeat
    **/
    private boolean _has_numberOfSoldSeat;


      //----------------/
     //- Constructors -/
    //----------------/

    public SeatQuantityType() {
        super();
    } //-- xmax.crs.Amadeus.negospace.SeatQuantityType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteNumberOfSoldSeat()
    {
        this._has_numberOfSoldSeat= false;
    } //-- void deleteNumberOfSoldSeat() 

    /**
    **/
    public int getNumberOfAllocatedSeat()
    {
        return this._numberOfAllocatedSeat;
    } //-- int getNumberOfAllocatedSeat() 

    /**
    **/
    public int getNumberOfSoldSeat()
    {
        return this._numberOfSoldSeat;
    } //-- int getNumberOfSoldSeat() 

    /**
    **/
    public boolean hasNumberOfAllocatedSeat()
    {
        return this._has_numberOfAllocatedSeat;
    } //-- boolean hasNumberOfAllocatedSeat() 

    /**
    **/
    public boolean hasNumberOfSoldSeat()
    {
        return this._has_numberOfSoldSeat;
    } //-- boolean hasNumberOfSoldSeat() 

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
     * @param numberOfAllocatedSeat
    **/
    public void setNumberOfAllocatedSeat(int numberOfAllocatedSeat)
    {
        this._numberOfAllocatedSeat = numberOfAllocatedSeat;
        this._has_numberOfAllocatedSeat = true;
    } //-- void setNumberOfAllocatedSeat(int) 

    /**
     * 
     * @param numberOfSoldSeat
    **/
    public void setNumberOfSoldSeat(int numberOfSoldSeat)
    {
        this._numberOfSoldSeat = numberOfSoldSeat;
        this._has_numberOfSoldSeat = true;
    } //-- void setNumberOfSoldSeat(int) 

    /**
     * 
     * @param reader
    **/
    public static xmax.crs.Amadeus.negospace.SeatQuantityType unmarshalSeatQuantityType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (xmax.crs.Amadeus.negospace.SeatQuantityType) Unmarshaller.unmarshal(xmax.crs.Amadeus.negospace.SeatQuantityType.class, reader);
    } //-- xmax.crs.Amadeus.negospace.SeatQuantityType unmarshalSeatQuantityType(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
