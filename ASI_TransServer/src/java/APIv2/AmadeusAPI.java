package APIv2;

//import APIv2.APIproxy$Reply;

/**
 ***********************************************************************
 * Title:        Amadeus API v2
 * Version:
 * Copyright:    Copyright (c) 1999
 * Company:      Amadeus Development
 * 
 * @author   API Team
 * @author   Philippe Paravicini
 *
 * @see APIproxy
 * @see APIproxy$Reply
 ***********************************************************************
 */
public class AmadeusAPI implements APIproxy{
  
   private String    server;
   private int       port;
   private Object    last_serialized_context;
   private boolean   trace;
   private String    trace_path;
   //private String[]  sReply;
   private static AmadeusAPI defaultAmadeusAPI;
   //APIproxy.Reply tempReply = new APIproxy.Reply();

   /* the native functions */

   private static native String[] CAIlistTransactions();
   private static native String   CAIgetModel(String name);
   private static native String   CAIgetErrorStr(int errorCode);
   private static native String[] CAIgetVersion(String  sc, 
                                                boolean fulltrace, 
                                                String  path, 
                                                String  tcp_server, 
                                                int     port_number);

   private static native String[] CAIopenConversationByCorporateID(
       String tcp_server, 
       int    port_number, 
       String corporate_id, 
       String user_id, 
       String password);

   private static native void CAIcloseConversation(
       String sc, String tcp_server, int port_number);

   private static native String[] CAIsendAndReceive(
       String  sc, 
       boolean fulltrace, 
       String  path, 
       String  xml_query, 
       String  tcp_server, 
       int     port_number);
    
  /* the public interfaces to the native functions */ 

  public static AmadeusAPI getAmadeusAPIInstance()
  {
    if (defaultAmadeusAPI == null)  
    {
      defaultAmadeusAPI = new AmadeusAPI();  
    }
    return defaultAmadeusAPI;
  }
    
  private AmadeusAPI()
  {
  server =  "";
  port   = 0;
  last_serialized_context = "";
  trace_path = "";
  trace = false;
  //sReply = new  String[4] ;

  System.loadLibrary("apiv2_jni");

  }

  protected void finalize() throws Throwable
  {
    closeConversation(last_serialized_context, server, port);
    super.finalize();
  }

  public APIproxy.Reply sendAndReceiveXml(
      Object serializedContext, 
      String sQuery, 
      String tcp_server,
      int    port_number)
  {
    //trace = false;
//    sReply = CAIsendAndReceive((String)serializedContext, 
//         trace, trace_path, sQuery, tcp_server, port_number);

//    tempReply.setreturnCode(sReply[0]);
//    tempReply.setdumpBuffer(sReply[1]);
//    tempReply.setcontext((Object)sReply[2]);
//    tempReply.setxmlString(sReply[3]);
//
//    return tempReply;      

    String [] aryReply = new String[4]; 
    aryReply = CAIsendAndReceive((String)serializedContext, 
         trace, trace_path, sQuery, tcp_server, port_number);

    APIproxy.Reply reply = new APIproxy.Reply(aryReply[0],aryReply[1],aryReply[2],aryReply[3]);
    return reply;
  }

  public synchronized APIproxy.Reply openConversationByCorporateID(
                                               String tcp_server,
                                               int    port_number,
                                               String corporate_id,
                                               String user_id,
                                               String password)
  {
//    sReply = CAIopenConversationByCorporateID(
//        tcp_server, port_number, corporate_id, user_id, password);

//    tempReply.setreturnCode(sReply[0]);
//    tempReply.setdumpBuffer(sReply[1]);
//    tempReply.setcontext((Object)sReply[2]);
//    tempReply.setxmlString(sReply[3]);
// 
//    return tempReply;
     
    String [] aryReply = new String[4]; 
    aryReply = CAIopenConversationByCorporateID(
        tcp_server, port_number, corporate_id, user_id, password);

    APIproxy.Reply reply = new APIproxy.Reply(aryReply[0],aryReply[1],aryReply[2],aryReply[3]);
    return reply;
  }

  public synchronized void closeConversation(Object serializedContext,
                                             String tcp_server,
                                             int    port_number)
  {
    if ((String)serializedContext != "") {
      CAIcloseConversation(
        (String)serializedContext, tcp_server, port_number);
    }
  }
 
  public synchronized String[] listTransactions()
  {
    String[] list;
    list = CAIlistTransactions();
    return list;
  }

  public synchronized String getModel(String name)
  {
    String model;
    model = CAIgetModel(name);
    return model;
  }

  public synchronized void setTrace(boolean fulltrace, String path)
  {
    trace = fulltrace;
    trace_path = path;
  }

  public synchronized String getErrorStr(int errorCode)
  {
    String errstr = "";
    errstr = CAIgetErrorStr(errorCode);
    return errstr;
  }

  public synchronized APIproxy.Reply getVersion(Object serializedContext)
  {
    String[] sReply;
    sReply = CAIgetVersion((String) serializedContext, trace, trace_path, server, port);
    last_serialized_context = sReply[2];

    return new APIproxy.Reply(sReply[0], sReply[1], last_serialized_context, sReply[3]);
  }

  
}// END of AmadeusAPI
