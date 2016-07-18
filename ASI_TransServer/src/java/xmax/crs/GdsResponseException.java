package xmax.crs;

import java.io.Serializable;
import xmax.TranServer.GnrcConvControl;

public class GdsResponseException extends Exception implements Serializable
{
 private String Request;
 private String Response;
 private int ErrorNumber;     // set with values from GnrcConvControl class

  /**
   ***********************************************************************
   * Constructors
   ***********************************************************************
   */
  public GdsResponseException(final String aErrorText)
    {
    super(aErrorText);
    ErrorNumber = GnrcConvControl.STS_CRS_ERR;
    }

  public GdsResponseException(final String aErrorText, final int aErrorNumber)
    {
    super(aErrorText);
    ErrorNumber = aErrorNumber;
    }

  public GdsResponseException(final String aErrorText, final String aRequest)
    {
    super(aErrorText);
    Request     = aRequest;
    ErrorNumber = GnrcConvControl.STS_CRS_ERR;
    }

  public GdsResponseException(final String aErrorText, final String aRequest, final String aResponse)
    {
    super(aErrorText);
    Request     = aRequest;
    Response    = aResponse;
    ErrorNumber = GnrcConvControl.STS_CRS_ERR;
    }

  public GdsResponseException(final String aErrorText, final String aRequest, final String aResponse, final int aErrorNumber)
    {
    super(aErrorText);
    Request     = aRequest;
    Response    = aResponse;
    ErrorNumber = aErrorNumber;
    }

  /**
   ***********************************************************************
   * get functions
   ***********************************************************************
   */
  public String getRequest()
    {
    return(Request);
    }

  public String getResponse()
    {
    return(Response);
    }

  public int getErrorNumber()
    {
    return(ErrorNumber);
    }

}
