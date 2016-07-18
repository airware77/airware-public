package xmax.TranServer;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.util.GregorianCalendar;
import java.util.Calendar;

import xmax.util.Log.*;

/**
 ***********************************************************************
 * This is the main class for the Transaction Server application; assuming that
 * the class has been properly jarred, the application should be called with a
 * with a command of the form:
 * <pre>
 * java -jar transerver.jar -c TranServerConfig.xml
 * </pre>
 * where <code>TranServerConfig.xml</code> is a duly formed xml configuration
 * file.
 * <p>
 * This class starts the Transaction Server Listener and the Transaction Server
 * Console (GUI), from where the communication with the GDSs can be monitored.
 * </p>
 * 
 * @author   David Fairchild
 * @version  $Revision: 15$ - $Date: 09/12/2002 4:40:23 PM$
 *
 * @see TranListener
 * @see TranServerFrame
 ***********************************************************************
 */
public class TranServerMain
{
  private final static String CR    = System.getProperty("line.separator");
  private final static String slash = System.getProperty("file.separator");
  private TranListener tListener;

  /** 
   ***********************************************************************
   *Main method
   ***********************************************************************
   */
  public static void main(String[] args)
    {
    // look for name of configuration file
    for ( int i = 1; i < args.length; i++ )
      {
      if ( args[i - 1].toUpperCase().startsWith("-C") )
        {
        final String sConfigFileName = args[i];
        try
          {
          ConfigTranServer.readConfFile(sConfigFileName);
          }
        catch (Exception e)
          {
          JOptionPane.showMessageDialog(null,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
          System.exit(0);
          }
        }
      }

    // start the application
    new TranServerMain();
    }

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public TranServerMain()
    {
    try
      {
       createSystemLogs();
       setupLogRotation();

       // create the GUI
       UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
       // final TranServerFrame frame = new TranServerFrame(tGroup);

       // start the socket listener thread
       tListener = new TranListener();

       final Thread ListenerThread = new Thread(tListener,"TranListener");
       ListenerThread.setPriority(Thread.NORM_PRIORITY);
       ListenerThread.setDaemon(true);
       ListenerThread.start();

       AppLog.LogWarning("TranServer started" + CR + getConfig());

       // start the watch TA listener thread
       // final int iObjectPort = ConfigInformation.getParamValue(ConfigTranServer.OBJECT_PORT,7026);
       final int iObjectPort = 7026;
       final WatchTAListener taListener = new WatchTAListener(iObjectPort);

       final Thread watchThread = new Thread(taListener);
       watchThread.setPriority(Thread.NORM_PRIORITY);
       watchThread.setDaemon(true);
       watchThread.start();

       final TranServerFrame frame = new TranServerFrame(tListener);
       }
     catch (Exception e)
       {
       e.printStackTrace();
       JOptionPane.showMessageDialog(null,e.toString(),"Error",JOptionPane.ERROR_MESSAGE);
       }
    }

  /**
   ***********************************************************************
   * This method sets up a timer that will roll the logging directory at
   * midnight every night
   ***********************************************************************
   */
  public void setupLogRotation() throws java.io.IOException
    {
    // inner class that implements an ActionListener for the Timer below
    ActionListener rotator = new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
        {
        rollLogDirectory();
        rollSystemLogs();
        deleteOldLogs();
        } 
      }; // end rotator

    // get a long representing the next midnight
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.set(calendar.HOUR_OF_DAY,24);
    calendar.set(calendar.MINUTE,1);
    calendar.set(calendar.SECOND,0);
    final long MIDNIGHT = calendar.getTime().getTime();

    /*
    try {
      System.out.println(xmax.util.DateTime.dateStamp(MIDNIGHT));
      System.out.println(xmax.util.DateTime.timeStamp(MIDNIGHT));
    } catch (Exception e) {
      System.out.println(e);
    }
    */

    final int MILLIS_TO_MIDNIGHT = 
      new Long(MIDNIGHT - System.currentTimeMillis()).intValue();

    //System.out.println("Hours to midnight: " + MILLIS_TO_MIDNIGHT/(60*60*1000));

    final int DAILY = 24 * 60 * 60 * 1000;

    final Timer logRotationTimer = new Timer( DAILY, rotator ); 
    logRotationTimer.setInitialDelay(MILLIS_TO_MIDNIGHT);
    logRotationTimer.start();

    // this one is for testing purposes - changes log directory every minute
    //final Timer testTimer = new Timer(60 * 1000, rotator);
    //testTimer.start();
    
    } // end setupLogRotation


  /**
   ***********************************************************************
   * This method creates a new system.log and system.err files in the current
   * logging directory
   ***********************************************************************
   */
  public static void createSystemLogs() throws java.io.IOException
    {
    final String sLogDir =
      ConfigTranServer.logging.getProperty("currentLogDir","logs");

    final String sSystemLogName = sLogDir + slash + "system.log";

    final File logfile = new File(sSystemLogName);
    final int[] allowed_messages = 
      {LoggingEvent.WARNING,LoggingEvent.ERROR,LoggingEvent.DEBUG};

    final FileLogger filelogger = new FileLogger(logfile,allowed_messages,null);
    AppLog.addLogger(filelogger);

    // redirect System.err
    final String sSystemErr = sLogDir + slash + "system.err";
    final boolean autoflush = true;
    final boolean append    = true;

    System.setErr(new java.io.PrintStream(
          new java.io.FileOutputStream(sSystemErr,append) ,autoflush));

    System.err.println("This file records standard error output from the JVM");
    
    } // end createSystemLog

  
  /**
   ***********************************************************************
   * This method echoes the current properties of the Transaction Server
   ***********************************************************************
   */
  public String getConfig()
    {
    StringWriter sw = new StringWriter();
    PrintWriter  pw = new PrintWriter(sw);

    //String tHostName = tListener.getAddress();

    pw.println("Airware Transaction Server");
    pw.println(ConfigTranServer.CURRENT_VERSION);

    try {
      String port = ConfigTranServer.application.getProperty("listeningPort");
      String tHostName = tListener.getLocalHost().getHostName()    + ":" + port;
      String tIP       = tListener.getLocalHost().getHostAddress() + ":" + port;
      pw.println("Listening at: " + tHostName + " - " + tIP);
      }
    catch (Exception e) {
      pw.println("Unable to retrieve local transerver IP address");
    }
    pw.println();
    pw.println("-- Configuration --");
    pw.println();

    pw.println("-- Application --");
    ConfigTranServer.application.list(pw);
    pw.println();

    pw.println("-- Logging --");
    ConfigTranServer.logging.list(pw);
    pw.println();

    pw.println("-- SignOn --");
    for (int i=0; i < ConfigTranServer.signOnList.length; i++)
      ConfigTranServer.signOnList[i].list(pw);
    pw.println();
    
    pw.flush();
    return sw.toString();

    } // end getConfig


  /**
   ***********************************************************************
   * Changes the current logging directory to a new datestamped directory
   * for the current date, such as: logs/20020410
   ***********************************************************************
   */
  private static void rollLogDirectory()  
    {
    String sLogDir = ConfigTranServer.logging.getProperty("directory","logs");

    try {
    ConfigTranServer.logging.setProperty("currentLogDir", 
        sLogDir + slash + xmax.util.DateTime.dateStamp());
      }
    catch (Exception pe) {
      AppLog.LogError(pe.toString());
      }

    } // end rollLogDirectory

  /**
   ***********************************************************************
   * Create a new system.log FileLogger in the new current logging directory
   * and remove the system.log FileLogger in the previous logging dir; redirect
   * standard err (System.err) to the new logging directory
   ***********************************************************************
   */
  private void rollSystemLogs()
    {
    // find the existing system.log FileLogger
    Logger[] loggerList = AppLog.getLoggerList();

    FileLogger oldSystemLog = null;

    for (int i=0; i < loggerList.length ; i++)
      {
      if (loggerList[i] instanceof FileLogger)
        {
        FileLogger fl = (FileLogger)loggerList[i];
        if (fl.getLogFile().getName().equals("system.log"))
          oldSystemLog = fl;
        }
      } // end for

    try {
      createSystemLogs();

      AppLog.LogWarning(
          "Rotated System Log" + CR + getConfig());
      AppLog.removeLogger(oldSystemLog);
      }
    catch (java.io.IOException ioe) {
      AppLog.LogError("Unable to create system logs: " + ioe);
      }
    
    } // end rollSystemLog


  /**
   ***********************************************************************
   * Delete log directories that older than the <code>daysToRetain</code>
   * parameter in the configuration file (90 days by default)
   ***********************************************************************
   */
  private static void deleteOldLogs()
    {
    // Delete the old logging directories
    File logDir = new File(ConfigTranServer.logging.getProperty("directory"));

    if (logDir.exists() == false) 
      {
      AppLog.LogError("Unable to find logging directory: " +
          logDir.getAbsolutePath());
      return;
      }
        
    File[] logs = logDir.listFiles();

    final long DAYS_TO_RETAIN =
      ConfigTranServer.logging.getLongProperty("daysToRetain",90) 
      * 24 * 60 * 60 * 1000;

    // uncomment to debug
    //final long DAYS_TO_RETAIN = 5 * 60 * 1000;

    final long NOW = System.currentTimeMillis();

    for (int i=0; i < logs.length ; i++)
      {
      if ( logs[i].isDirectory() && 
           NOW - logs[i].lastModified() > DAYS_TO_RETAIN )
        {
        String logDirName = logs[i].getAbsolutePath();
        if (deleteDir(logs[i]))
          AppLog.LogWarning(
              "deleted logging directory: " + logDirName);
        else
          AppLog.LogError(
              "unable to delete logging directory: " + logDirName);
        }
      } // end for

    } // end deleteOldLogs


  /**
   ***********************************************************************
   * This method deletes all subdirectories and files from a directory
   * recursively; it returns <code>true</code> if all the files were
   * succesively deleted
   ***********************************************************************
   */
  private static boolean deleteDir(File dir)
    {
    if (dir.exists() == false)
      return true;

    File[] dirList = dir.listFiles();

    for (int i=0; i < dirList.length ; i++)
      {
      if (dirList[i].isDirectory())
        {
        if (deleteDir(dirList[i]) == false)
          return false;
        }
      else
        if (dirList[i].delete() == false)
          return false;
      } // end for

    return dir.delete();

    } // end deleteDir

  /*
  public void finalize()
    {
    int iNumThreads = TranClientConnection.activeCount();
    TranClientConnection[] StopThreads = new TranClientConnection[iNumThreads];
    TranClientConnection.enumerate(StopThreads);

    for ( int i = 0; i < StopThreads.length; i++ )
      StopThreads[i].pleaseStop();
    }
  */
}


