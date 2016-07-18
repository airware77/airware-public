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
import java.net.InetAddress;
import java.io.IOException;

import xmax.util.Log.AppLog;

/**
 * This class runs as a service/daemon, listens for a request from
 * a client/applet, and instantiates a TranClientConnection object
 * once a connection with a client has been made.
 *
 * @author	 David Fairchild
 * @version  1.x Copyright (c) 1999
 * @see      TranClientConnection
 */

public class TranListener implements Runnable
{

/**
 * the default_port on which the client listens, currently 8026
 */
 // final private static int DEFAULT_PORT = 8026;

 private final int ListenPort;
 private final ServerSocket ss;
 private final ThreadGroup tGroup;
 private TranClientConnection tranClientConnection;

 
  /**
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public TranListener() throws IOException
    {
    ListenPort = ConfigTranServer.application.getIntProperty("listeningPort",8026);

    ss = new ServerSocket(ListenPort);
    tGroup = new ThreadGroup("CRS_HOST");
    }

  /**
   ***********************************************************************
   * Run method
   ***********************************************************************
   */
  public void run()
    {
    // loop while waiting for client connections
    long counter = 1000;

    while ( true )
      {
      try
        {
        // block until a client socket connection is made
        final Socket ClientSocket = ss.accept();

        String locAddr = ClientSocket.getLocalAddress().getHostAddress();
        int    locPort = ClientSocket.getLocalPort();

        String rmtAddr = ClientSocket.getInetAddress().getHostAddress();
        int    rmtPort = ClientSocket.getPort();

        tranClientConnection = new TranClientConnection(ClientSocket);

        counter++;
        String sThreadName = "T" + counter;

        AppLog.LogWarning(
            "TranClientConnection created on thread: " + sThreadName + " - "
            + " locAddr=" + locAddr + ":" + locPort + " - "
            + " rmtAddr=" + rmtAddr + ":" + rmtPort);

        // reset counter when it gets to 9999
        if (counter == 9999) counter = 1000;

        Thread RequestThread = new Thread(tGroup,tranClientConnection,sThreadName);
        RequestThread.setPriority(Thread.NORM_PRIORITY - 1);
        RequestThread.setDaemon(true);
        RequestThread.start();

        AppLog.LogWarning(
            "TranClientConnection started on thread: " + sThreadName);

        Thread.yield();
        }
      catch ( IOException e )
        {
        AppLog.LogError(
            "TranListener Error: counter=" + counter + " - " + e.toString());
        }
      }

    } // end run


  /**
   ***********************************************************************
   * returns the {@link InetAddress} representing the internet address of
   * the {@link ServerSocket} which this <code>TranListener</code> wraps
   ***********************************************************************
   */
  public InetAddress getLocalHost() throws Exception
    {
    return ss.getInetAddress().getLocalHost();
    } 


  public TranClientConnection getTranClientConnection()
  {
	return tranClientConnection;
  }

/**
   ***********************************************************************
   * Close the ServerSocket
   ***********************************************************************
   */
  protected void finalize()
    {
    try {
      ss.close();
      }
    catch (IOException e) {
      AppLog.LogError("TranListener.finalize(): " + e.toString());
      }
    } // end finalize


} // end TranListener




