
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application
package xmax.TranServer;

import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import xmax.util.Log.*;

public class WatchTAListener implements Runnable
{
 private int ListenPort;

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public WatchTAListener(final int aPort)
    {
    ListenPort = aPort;
    }

  /** 
   ***********************************************************************
   * Run method
   ***********************************************************************
   */
  public void run()
    {

    // loop while waiting for client connections
    try
      {

      final ServerSocket ss = new ServerSocket(ListenPort);
      while ( ss instanceof ServerSocket )
        {
        // block until a client socket connetion is made
        final Socket ClientSocket = ss.accept();
        if ( ClientSocket instanceof Socket )
          {
          String sTaName = m_GetTAName(ClientSocket);
          if ( sTaName instanceof String )
            {
            if ( sTaName.equals("ALL") )
              sTaName = null;

            final SocketLogger socketlogger = new SocketLogger(ClientSocket,LoggingEvent.ALL_MESSAGES,sTaName);
            AppLog.addLogger(socketlogger);

            if ( sTaName instanceof String )
              AppLog.LogInfo("Started remote message logging on TA " + sTaName,null,null);
            else
              AppLog.LogInfo("Started remote message logging on all TAs ",null,null);
            }
          }
        }

      }
    catch (Exception e)
      {
      AppLog.LogInfo("WatchTAListener: listenport = " + Integer.toString(ListenPort) + ", " + e.toString(),null,null);
    //  System.out.println( "WatchTAListener: listenport = " + Integer.toString(ListenPort) + ", " + e.toString() );
      }

    }

  /** 
   ***********************************************************************
   * Run method
   ***********************************************************************
   */
  private String m_GetTAName(final Socket aSocket)
    {

    try
      {
      if ( aSocket instanceof Socket )
        {
        final InputStream instream = aSocket.getInputStream();
        if ( instream instanceof InputStream )
          {
          final ObjectInputStream in = new ObjectInputStream( instream );
          if ( in instanceof ObjectInputStream )
            {
            final Object read_object = in.readObject();
            if ( read_object instanceof String )
              {
              final String sTAName = (String )read_object;
              return(sTAName);
              }
            }
          }
        }
      }
    catch (Exception e)
      {
      System.out.println( e.toString() );
      }

    return(null);
    }


}
