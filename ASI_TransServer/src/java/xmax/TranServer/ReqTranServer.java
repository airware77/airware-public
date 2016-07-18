package xmax.TranServer;

import xmax.util.Log.FileLogger;
import xmax.util.Log.LoggingEvent;
import xmax.util.Log.AppLog;
import java.io.File;
import xmax.crs.GnrcCrs;
import xmax.crs.GnrcCrsQuery;
import java.io.Serializable;
import java.util.Vector;
import xmax.crs.BaseCrs;

/**
 * The ReqTranServer class is the top level abstraction for a Client Request to
 * the Transaction Server, from which are derived the specific Client Requests
 * (for example, ReqGetFlifo).
 *
 * @author	David Fairchild
 * @version $Revision: 7$ - $Date: 04/02/2002 8:12:49 PM$
 * @see     runRequest
 *
 */
public abstract class ReqTranServer implements Serializable
{

/**  CRS that makes request */
 private String CrsCode;

/** Description of request */
 public String Description;     

/** Identity of user who made request */
 public String RequestedBy;

/** timestamp when request was received */
 private long StartTime;        

/** timestamp when request was completed */
 private long StopTime;         

/** keeps a list of commands sent to the host */
 private Vector CommandHistory;

/** list of exceptions that happen while doing request */
 private Vector ExceptionList;

/** current crs connection */
 private GnrcCrs crs;

/** a copy of the original ascii request that generated this request object */
 public String AsciiRequest;    

/** a copy of the ascii response that was sent to the client */
 public String AsciiResponse;   

/** one of the five constant types listed below */
 public int commType;           

 public static final int COMM_UNKNOWN        = 0;
 public static final int COMM_NATIVE_ASCII   = 1;
 public static final int COMM_NATIVE_AIRWARE = 2;
 public static final int COMM_JAVA_OBJECT    = 3;
 public static final int COMM_XML            = 4;

 /** 
  * passes the request to the appropriate Computer Reservation System (CRS)
  * for execution; this must be implemented by all deriving classes
  */
 public abstract void runRequest(final GnrcCrs aCrs) throws Exception;

 /** specifies the name of the file that is logging this request */
 public abstract String getLogFileName(final String aLogDirectory) throws Exception;

 /**
  * constructor: sets the crsCode and timestamps the time at which the request
  * was received
  */
 public ReqTranServer(final String aCrsCode)
   {
   CrsCode = aCrsCode;

   if ( CrsCode.equals("UA") )
     CrsCode = BaseCrs.APOLLO_CODE;

   setStartTime();

   RequestedBy = ConfigTranServer.application.getProperty("receiveBy","TRANSERVER");
   }

 /** sets the time on which the request was received to the current system time */
 public void setStartTime()
   {
   StartTime = System.currentTimeMillis();
   }

 /** sets the time on which the request was received to the time specified */
 public void setStartTime(final long aStartTime)
   {
   StartTime = aStartTime;
   }

 /** retrieves the time at which the request was received */
 public long getStartTime()
   {
   return(StartTime);
   }

 /** sets the time on which the request was completed to the current system time */
 public void setStopTime()
   {
   StopTime = System.currentTimeMillis();
   }

 /** sets the time on which the request was completed to the time specified */
 public void setStopTime(final long aStopTime)
   {
   StopTime = aStopTime;
   }

 /** retrieves the time at which the request was completed */
 public long getStopTime()
   {
   return(StopTime);
   }

 /** 
  * returns the number of milliseconds between the time that the request was
  * completed, and the time at which it was started 
  */
 public long getElapsedTime()
   {
   if ( StopTime > StartTime )
     return( StopTime - StartTime );
   else if ( StartTime > 0 )
     return( System.currentTimeMillis() - StartTime );
   else
     return(0);
   }

  /**
   * Derived request objects can call this to start logging to the
   * indicated file name
   */
  public FileLogger startLogging(final String aLogFilePathName, final GnrcCrs aCrs) throws Exception
    {
    if ( GnrcFormat.NotNull(aLogFilePathName) )
      {
      final File LogFile = new File(aLogFilePathName);
      final FileLogger filelogger = new FileLogger(LogFile,LoggingEvent.ALL_MESSAGES,aCrs.getConnectionName());
      AppLog.addLogger(filelogger);
      return(filelogger);
      }
    else
      return(null);
    }

  /** Derived request objects can call this to stop logging */
  public void stopLogging(final FileLogger aFileLogger)
    {
    if ( aFileLogger instanceof FileLogger )
      AppLog.removeLogger(aFileLogger);
    }


  /** this will clear the command history vector */
  public void clearCommandHistory()
    {
    if ( CommandHistory instanceof Vector )
      CommandHistory.clear();
    }

  /** as commands for a request are run, save them here for logging */
  public void saveCommand(final GnrcCrsQuery aCommand)
    {
    // create the vector if needed
    if ( (CommandHistory instanceof Vector) == false )
      CommandHistory = new Vector();

    CommandHistory.add(aCommand);
    }

  /**
   * this function returns all the commands that were run against the
   * host to fulfill the request
   */
  public GnrcCrsQuery[] getCommandHistory()
    {
    // create the vector if needed
    if ( CommandHistory instanceof Vector )
      {
      if ( CommandHistory.size() > 0 )
        {
        final GnrcCrsQuery[] commandArray = new GnrcCrsQuery[ CommandHistory.size() ];
        CommandHistory.toArray(commandArray);
        return(commandArray);
        }
      }

    return(null);
    }

  /**
   * this function returns all the commands that were run against the
   * host to fulfill the request
   */
  public GnrcCrsQuery getLastCommand()
    {
    // create the vector if needed
    if ( CommandHistory instanceof Vector )
      {
      if ( CommandHistory.size() > 0 )
        {
        final GnrcCrsQuery command = (GnrcCrsQuery )CommandHistory.elementAt( CommandHistory.size() - 1 );
        if ( command instanceof GnrcCrsQuery )
          return(command);
        }
      }

    return(null);
    }

  /** this will clear the exception list vector */
  public void clearExceptions()
    {
    if ( ExceptionList instanceof Vector )
      ExceptionList.clear();
    }

  /** as commands for a request are run, save any exceptions here */
  public void saveException(final Exception aException)
    {
    // create the vector if needed
    if ( (ExceptionList instanceof Vector) == false )
      ExceptionList = new Vector();

    ExceptionList.add(aException);
    }

  /** return the list of exceptions */
  public Exception[] getExceptionList()
    {
    // create the vector if needed
    if ( ExceptionList instanceof Vector )
      {
      if ( ExceptionList.size() > 0 )
        {
        final Exception[] ExceptionArray = new Exception[ ExceptionList.size() ];
        ExceptionList.toArray(ExceptionArray);
        return(ExceptionArray);
        }
      }

    return(null);
    }

  /** return the first exception */
  public Exception getFirstException()
    {
    // create the vector if needed
    if ( ExceptionList instanceof Vector )
      {
      if ( ExceptionList.size() > 0 )
        {
        final Exception e = (Exception )ExceptionList.elementAt(0);
        if ( e instanceof Exception )
          return(e);
        }
      }

    return(null);
    }

  /** return the list of exceptions */
  public boolean hasExceptions()
    {
    // create the vector if needed
    if ( ExceptionList instanceof Vector )
      {
      if ( ExceptionList.size() > 0 )
        return(true);
      }

    return(false);
    }

  public void setCrs(final GnrcCrs aCrs)
    {
    crs = aCrs;
    }

  public GnrcCrs getCrs()
    {
    return(crs);
    }


  public String getCrsCode()
    {
    return(CrsCode);
    }

} // end class ReqTranServer
