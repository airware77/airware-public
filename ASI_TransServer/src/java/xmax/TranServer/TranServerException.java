package xmax.TranServer;

import java.io.Serializable;
import xmax.TranServer.GnrcConvControl;

public class TranServerException extends Exception implements Serializable
{
 private ReqTranServer request;
 private int ErrorNumber;

  /**
   ***********************************************************************
   * constructors
   ***********************************************************************
   */
  public TranServerException(final String aDescription)
    {
    super(aDescription);
    ErrorNumber = GnrcConvControl.STS_CRS_ERR;
    }

  public TranServerException(final String aDescription, final ReqTranServer aRequest)
    {
    super(aDescription);
    request     = aRequest;
    ErrorNumber = GnrcConvControl.STS_CRS_ERR;
    }

  public TranServerException(final String aDescription, final ReqTranServer aRequest, final int aErrorNumber)
    {
    super(aDescription);
    request     = aRequest;
    ErrorNumber = aErrorNumber;
    }

  /**
   ***********************************************************************
   * get functions
   ***********************************************************************
   */
  public ReqTranServer getRequest()
    {
    return(request);
    }

  public int getErrorNumber()
    {
    return(ErrorNumber);
    }

}
