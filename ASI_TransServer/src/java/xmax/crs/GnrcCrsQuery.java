package xmax.crs;

import java.io.Serializable;

public class GnrcCrsQuery implements Serializable
{
 public String Request;
 public String Response;
 public long RequestTimeStamp;
 public long ResponseTimeStamp;

  /** 
   ***********************************************************************
   * Constructors
   ***********************************************************************
   */
  public GnrcCrsQuery()
    {
    }

    
  public GnrcCrsQuery(final String aRequest)
    {
    Request = aRequest;
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  public long getResponseTime()
    {
    if ( ResponseTimeStamp > RequestTimeStamp )
      return( ResponseTimeStamp - RequestTimeStamp );
    else
      return(-1);
    }

}