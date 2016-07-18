package xmax.TranServer;

import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Externalizable;
import java.io.PushbackInputStream;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;

public class LogListener implements Runnable
{
 private String sTAName;
 private Socket socket;
 private boolean KeepRunning;

 /** 
  ***********************************************************************
  * Constructor
  ***********************************************************************
  */
 public LogListener(final String aHostIP, final int aPort, final String aTAName) throws IOException
   {
   sTAName = aTAName;
   socket = new Socket(aHostIP,aPort);

   if ( (socket instanceof Socket) == false )
     throw new IOException("Unable to connect to TranServer service");

   KeepRunning = true;
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void run()
   {
   try
     {
     m_StartLogging();

     if ( (socket instanceof Socket) == false )
       throw new Exception("Invalid socket connection");

     final InputStream instream = socket.getInputStream();
     if ( (instream instanceof InputStream) == false )
       throw new Exception("Unable to create input stream ");

     final ObjectInputStream in = new ObjectInputStream( instream );
     if ( (in instanceof ObjectInputStream) == false )
       throw new Exception("Unable to create object input stream ");

     LoggingEvent event;
     Object read_object;

     KeepRunning = true;
     while ( KeepRunning )
       {

       try
         {
         read_object = in.readObject();
         if ( read_object instanceof LoggingEvent )
           {
           event = (LoggingEvent )read_object;
           AppLog.LogEvent(event);
           }
           }
         catch (Exception e)
           {
           System.out.println( e.toString() );
           }

       Thread.yield();
       }

     m_StopLogging();
     in.close();
     socket.close();
     }
   catch (Exception e)
     {
     System.out.println( e.toString() );
     }
   finally
     {
     }
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 private void m_StartLogging() throws Exception
   {

   if ( (socket instanceof Socket) == false )
     throw new Exception("Unable to start LogListener = invalid socket object");

   final OutputStream outstream = socket.getOutputStream();
   if ( (outstream instanceof OutputStream) == false )
     throw new Exception("Unable to start LogListener = invalid OutputStream object");

   final ObjectOutputStream objectstream = new ObjectOutputStream( outstream );
   if ( (objectstream instanceof ObjectOutputStream) == false )
     throw new Exception("Unable to start LogListener = invalid ObjectOutputStream object");

   if ( sTAName instanceof String )
     objectstream.writeObject(sTAName);
   else
     objectstream.writeObject("ALL");

   objectstream.flush();

   final String sCommand = GnrcFormat.SetWidth("CSTRTLOG",8) + GnrcFormat.SetWidth(sTAName,10);
  // m_WriteString(sCommand);
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void pleaseStop()
   {
   KeepRunning = false;
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 private void m_StopLogging() throws IOException
   {
   final String sCommand = GnrcFormat.SetWidth("CENDLOG",8);
//   m_WriteString(sCommand);
   }

  /** 
   ***********************************************************************
   * This procedure writes the given string out the socket port
   ***********************************************************************
   */
  private void m_WriteString(final String aOutString) throws IOException
    {
    final OutputStream outstream = socket.getOutputStream();

    final int iLength = aOutString.length();
    final byte[] lengthbuf = new byte[4];

    lengthbuf[3] = (byte )(iLength & 0xff);
    lengthbuf[2] = (byte )((iLength >> 8)  & 0xff);
    lengthbuf[1] = (byte )((iLength >> 16) & 0xff);
    lengthbuf[0] = (byte )((iLength >> 24) & 0xff);

    outstream.write( lengthbuf );
    outstream.write( aOutString.getBytes() );
    outstream.flush();
    }

 /** 
  ***********************************************************************
  * Main procedure for unit tests
  ***********************************************************************
  */
 public static void main(String[] args)
   {
   try
     {
     LogListener logListener = new LogListener("ntws12",8028,"AATerm05");



     }
   catch (Exception e)
     {
     }
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void finalize()
   {
   // send out the command to stop logging
   try
     {
     m_StopLogging();
     }
   catch (Exception e)
     {}

   // make sure the socket is closed
   if ( socket instanceof Socket )
     {
     try
       {
       socket.close();
       }
     catch (Exception e)
       {}
     socket = null;
     }
   }

}
