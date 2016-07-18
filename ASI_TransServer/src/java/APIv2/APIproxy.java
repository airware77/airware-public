package APIv2;

/*
 *  Copyright 2000 - Amadeus Development Company S.A. Copyright of this program
 *  is the property of AMADEUS, without whose written permission reproduction in
 *  whole or in part is prohibited. All rights reserved. Amadeus development
 *  company S.A. B.P. 69 06902 SOPHIA ANTIPOLIS CEDEX http://www.amadeus.net
 *
 * $Header: APIproxy.java, 1, 03/20/2003 2:54:11 PM, Philippe Paravicini$
 */
// package amadeusAPI;

//package xmax.crs.Amadeus.APIv2;

/**
 *  AmadeusAPI proxy interface 
 *
 *  Defines the interface for a general amadeus (XML flavor) proxy <p>
 *
 *
 *
 * @created    October 20, 2000
 * @version    1.0
 * @author     API team
 */

public interface APIproxy
{
  /**
   *  Reply of a send and receive
   *
   * @created    October 20, 2000
   * @version    $Revision: 1$
   */
  public static class Reply
  {
    /**
     *  return code
     */
    public int returnCode;

   /**
     *  Dump Buffer, contains debug information you can forward it to your Amadeus support if necessary
     */
    public String dumpBuffer;
    
    /**
     *  Conversation context (might have been altered during the transaction)
     */
    public Object context;

    /**
     *  Xml string replied (null if the transaction failed)
     */
    public String xmlString;

    /**
     *  Constructor for the Reply object
     *
     * @param  context  Conversation context
     * @param  string   xml reply string
     */
    public Reply( int retcode, String dump, Object context, String xmlReply )
    {
      this.returnCode = retcode;
      this.dumpBuffer = dump;
      this.context    = context;
      this.xmlString  = xmlReply;
    }

     public Reply( String retcode, String dump, Object context, String xmlReply )
    {
      this.returnCode = Integer.parseInt(retcode);
      this.dumpBuffer = dump;
      this.context    = context;
      this.xmlString  = xmlReply;
    }
 
    public Reply()
    {
    }
    
    
    // S E T T E R   M E T H O D S 
 
    public void setreturnCode(String value)
    {
      returnCode = Integer.parseInt(value);
    }

    public void setdumpBuffer(String value)
    {
      dumpBuffer = value;
    }

    public void setcontext(Object value)
    {
      context = value;
    }

    public void setxmlString(String value)
    {
      xmlString = value;
    }
    
  }

  /**
   * This method opens the conversation with the server, and returns the 
   * serializedContext (conversation handle) as an object; this handle 
   * is necessary for subsequent calls to sendAndReceive; if it fails, 
   * the method returns a null conversation handle.
   *
   * @param  tcp_server    ip address of target
   * @param  port_number   application portnumber of target
   * @param  corporate_id
   * @param  user_id
   * @param  password
   *
   * @return ReturnedValueDescription
   */
  public Reply openConversationByCorporateID( String tcp_server,
                                              int port_number,
                                              String corporate_id,
                                              String user_id,
                                              String password );

  /**
   * Sends an xml string as a query, returns the new conversation handle 
   * and the reply from the proxy.
   *
   * @param  serializedContext  
   *   a unique conversation handle required to send the transaction.
   * @param  sQuery
   *   the XML query string
   *
   * @return the reply
   *   Returns a Reply containing the <code>context</code> Object that can 
   *   be modified within this function, and the xml reply string.
   */
  public Reply sendAndReceiveXml(Object serializedContext, 
                                 String sQuery, 
                                 String tcp_server,
                                 int    port_number );

  /**
   *  Close the conversation with the server
   *
   * @param  serializedContext  - the unique conversation handle
   */
  public void closeConversation( Object serializedContext,
                                 String tcp_server,
                                 int    port_number);

  /**
   *  Returns a list of transactions supported by the proxy
   *
   * @return    ReturnedValueDescription
   */
  public String[] listTransactions();

  /**
   *  Returns the XML model for a given Transaction name
   *
   * @param  transaction name
   * @return The XML model value
   */
  public String getModel( String name );

  /**
   *  Returns the string format of the API error code
   *
   * @param  error code
   * @return The string value
   */
  public String getErrorStr(int errorCode);

  /**
   *  Returns the proxy and server versions (xml string format)
   *
   * @param  serializedContext  
   *  a unique conversation handle required to send the transaction.
   *
   * @return  The reply
   */
  public Reply getVersion(Object serializedContext);
}

