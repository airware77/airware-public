package xmax.TranServer;

import xmax.util.TypedProperties;
import xmax.util.xml.DOMutil;
import xmax.util.DateTime;

import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 ***********************************************************************
 * This class is read when the Transaction Server is first started; it reads
 * the configuration file passed in the command line argument, and sets up
 * several {@link Properties} objects that can be accessed
 * throughout the application to retrieve application-wide variables.
 *
 * @author   David Fairchild
 * @author   Philippe Paravicini
 * @version  $Revision: 36$ - $Date: 1/27/2003 5:01:25 PM$
 *
 * @see xmax.TranServerMain
 * @see java.util.Properties
 ***********************************************************************
 */
public class ConfigTranServer
{
  // the 1.5.6.2 and 20090819.1759  tokens are replaced when building with Ant
  public static String CURRENT_VERSION    = "";
	public static String API_VERSION        = "API version: 2,1,40,3";

  /**
   * The name of the Configuration File; it gets set in {@link #readConfFile} with
   * the path of the Configuration File set on the command line;
   */
  public static String configFile = "";

  // configuration parameter groups
  private static final String APPLICATION_GROUP  = "Application";
  private static final String GATEWAY_GROUP      = "Gateway";
  private static final String SIGNON_GROUP       = "Sign On";
  private static final String LOGGING_GROUP      = "Logging";

  // configuration parameters
  public static final String LISTENING_PORT     = "Listening Port";
  public static final String FORM_CAPTION       = "Form Caption";
  public static final String VERSION            = "Version";
  public static final String CONFIG_FILENAME    = "Configuration Filename";

  public static final String GATEWAY_SERVER     = "Gateway Server";
  public static final String GATEWAY_PORT       = "Gateway Port";
  public static final String MESSAGE_INTERVAL   = "Message Wait Interval";
  public static final String DEFAULT_HOST       = "Default Host";

  public static final String LOGGING_DIRECTORY  = "Logging Directory";
  public static final String DAYS_TO_RETAIN     = "Days to Retain Logs";
  public static final String MAX_LOG_SIZE       = "Max Log Size";
  public static final String ENABLE_SYSTEM_LOG  = "Enable System Log";
  public static final String ENABLE_TERM_LOG    = "Enable Terminal Log";
  public static final String ENABLE_LOCATOR_LOG = "Enable Locator Log";

  // new ConfigTranServer Variables
  private static final String APPLICATION_TAG  = "Application";
  private static final String LOGGING_TAG      = "Logging";
  private static final String GATEWAY_TAG      = "Gateway";
  private static final String SIGN_ON_TAG      = "SignOn";

  /**
   * Stores application properties; default values are provided
   */
  public static TypedProperties application = new TypedProperties();

  // setup the default application properties;
  // note that these are fall-back values that are overridden
  // by those supplied in the configuration file
  static
    {
    // the default port on which the Transerver listens for requests
    application.put("listeningPort","8026");

    // the default caption that is to appear in the title of the GUI
    application.put("formCaption", "Airware Transaction Server");

    // the default GDS code to use, if none is provided
    application.put("defaultHostCode", "1A");

    // the default number of seconds that must have elapsed before actively
    // testing the connection during a GnrcCrs.TestHostConnection
    application.put("testConnInterval","30");

    // the default string to put in a 'Received From' remark
    application.put("receiveBy","TRANSERVER");

    // the default number of seconds to wait for a response from a host after
    // issuing a request, before sending back a time out error
    // defaults to 120 seconds
    application.put("responseTimeout","120");

    // the number of times to attempt to complete a GetFlifo or GetAvail before
    // returning an exception
    application.put("maxRetry","2");

    // the default number of minutes of inactivity after which a
    // TranClientConnection disconnects from the CRS back-end
    application.put("inactivityTimeout","30");

    // the maximum number of minutes that may separate the arrival time
    // of a flight from the departure date of the next flight,
    // if the two flights are to be considered contiguous
    application.put("stopOverThreshold","360");

    // indicates whether we have available a ticket printer; when this value
    // is set to false in the configuration file, a call to ReqIssueTicket
    // always returns a success status
    application.put("enableTicketing", "true");

    // Specifies the default ticketing method, either 'Paper' or 'eTicket'
    // This method will be used by default when ticketing if no specific
    // method is provided - defaults to 'Paper'
    // It is conceivable that this parameter could be defined on a
    // per-GdsConnection basis, but for the time being we leave at the
    // application level until such requirement arises in production
    application.put("ticketingMethod", "Paper");
    
    application.put("useTestHarness", "false");
    application.put("requestDir", ".");
    application.put("responseDir", ".");
    }

  /**
   * Stores logging properties; default values are provided
   */
  public static TypedProperties logging = new TypedProperties();

  // setup the default logging properties
  static
    {
    logging.put("directory"   ,"logs");
    logging.put("maxLogSize"  ,"20000");       // in Bytes
    logging.put("daysToRetain","90");
    logging.put("enableLocatorLog" ,"true");
    logging.put("enableSystemLog"  ,"true");
    logging.put("enableTerminalLog","true");
    }

  /**
   * If an Innosys gateway is present in the installation, this Properties
   * object stores the information that is used to access such gateway; see the
   * configuration file for details on this information; no default properties
   * are provided as these properties are specific to each installation.
   */
  public static TypedProperties gateway = new TypedProperties();

  /**
   * These properties are readOnly properties needed by the application which
   * should not be configurable by the customer; they are read when the class
   * is loaded.
   */
  public static TypedProperties readOnly = new TypedProperties();

  // setup the read-only properties
  static
    {
    //readOnly.put("aReadOnlyField", "someValue");
    }

  /**
   * Stores an array of properties each corresponding to a specific signOn; no
   * default values are provided for this list, as this is something that
   * is specific to each installation.
   */
  public static TypedProperties[] signOnList;

  /**
   ***********************************************************************
   * Reads an XML file and sets configuration parameters
   ***********************************************************************
   */
  public static void readConfFile(final String sConfigFile)
    throws TranServerException
    {
    if(!(sConfigFile instanceof String))
      throw new TranServerException(
          "The name of the Configuration File was not provided");
    Document domConf;
    try
      {
      domConf = DOMutil.fileToDom(sConfigFile);
      }
    catch(Exception e)
      {
      throw new TranServerException(
          "Unable to read Configuration File: " +
          System.getProperty("line.separator") +
          e.toString());
      }

    // save the path of the configuration file provided
    ConfigTranServer.configFile = sConfigFile;

    Element el = domConf.getDocumentElement();
    if (!(el.getTagName().equals("Configuration")))
      {
      throw new TranServerException(
        "The Configuration File is invalid, it does not contain a \"Configuration\" root element");
      }

    el = (Element)domConf.getElementsByTagName(APPLICATION_TAG).item(0);
    setProperties(ConfigTranServer.application, el);
    //application.list(System.out);

    el = (Element)domConf.getElementsByTagName(LOGGING_TAG).item(0);
    setProperties(ConfigTranServer.logging, el);
    //logging.list(System.out);
    String sLogDir = logging.getProperty("directory");

    String slash = System.getProperty("file.separator");

    try {
    // creates a directory name for the current date, such as: logs/20020410
    ConfigTranServer.logging.put(
        "currentLogDir", sLogDir + slash + DateTime.dateStamp());
      } 
    catch (Exception e) {
      xmax.util.Log.AppLog.LogError(e.toString());
    }



    // el = (Element)domConf.getElementsByTagName(GATEWAY_TAG).item(0);
    // setProperties(ConfigTranServer.gateway, el);
    //gateway.list(System.out);

    //setSignOnListProperties(domConf);

    readSignOns(domConf);
    
    readVersionInfo();
    } // end setProperties

  
  private static void readVersionInfo()
  {
	  try
	  {
		  final InputStream inStream = ConfigTranServer.class.getClassLoader().getResourceAsStream("version.properties");
		  final Properties props = new Properties();
		  props.load(inStream);
		  
		  final String sVersionNumber = (String )props.get("version.number");
		  final String sBuildDate = (String )props.get("version.date");
		  
		  //System.out.println("Version = " + sVersionNumber);
		  //System.out.println("Build = " + sBuildDate);
		  
		  ConfigTranServer.CURRENT_VERSION = "Version " + sVersionNumber + " - build: " + sBuildDate;	  
	  }
	  catch (Exception e)
	  {
		  throw new RuntimeException("Unable to set version variables " + e.toString() );
	  }
	  
  }
  
  
  /**
   ***********************************************************************
   * Reads an XML file and sets configuration parameters
   ***********************************************************************
   */
  private static void readSignOns(final Document aDoc) throws TranServerException
    {
    final NodeList ConnectionNodes = aDoc.getElementsByTagName("GdsConnection");
    int iNumConnections = ConnectionNodes.getLength();

    if (iNumConnections == 0)
      throw new TranServerException( 
        "The configuration file: '" + configFile + "' must contain at least one <GdsConnection> node"); 

    final Vector Connections = new Vector();
    Element nodeConnection;
    TypedProperties props;
    String sEnabled;

    for ( int i = 0; i < iNumConnections; i++ )
      {
      nodeConnection = (Element )ConnectionNodes.item(i);

      sEnabled = nodeConnection.getAttribute("enabled").toUpperCase();
      if ( sEnabled.startsWith("T") || sEnabled.startsWith("Y") )
        {
        props = new TypedProperties();
        setProperties(props,nodeConnection);
        Connections.add(props);
        }
      }

    // copy the vector data to the array
    if ( Connections.size() > 0 )
      {
      signOnList = new TypedProperties[ Connections.size() ];
      Connections.toArray(signOnList);
      }
    else
      throw new TranServerException(
        "All <GdsConnection> nodes in the configuration file '" + configFile + "' have been disabled");
    }

  /**
   ***********************************************************************
   * This procedure returns a list of sign ons with a parameter that
   * matches the given value
   ***********************************************************************
   */
  public static TypedProperties[] getSignOns(final String aParamName, final String aParamValue)
    {
    if ( (signOnList instanceof TypedProperties[]) &&
         (aParamName instanceof String) &&
         (aParamValue instanceof String) )
      {
      final Vector vSignOns = new Vector();

      String sValue;
      for ( int i = 0; i < signOnList.length; i++ )
        {
        sValue = signOnList[i].getProperty(aParamName,null);
        if ( sValue instanceof String )
          {
          if ( sValue.equals(aParamValue) )
            vSignOns.add(signOnList[i]);
          }
        }

      if ( vSignOns.size() > 0 )
        {
        final TypedProperties[] propArray = new TypedProperties[ vSignOns.size() ];
        vSignOns.toArray(propArray);
        return(propArray);
        }
      else
        return(null);

      }
    else
      return(signOnList);
    }

  /**
   ***********************************************************************
   * Given a Properties object, and an Element which contains 'leaf' sub-elements
   * with parameter values, this method populates the Properties object passed
   * with key-value pairs corresponding to the name-value pairs of the leaf
   * Elements.
   *
   * <p>For example, calling setProperties(props,el) where the Element node
   * has the following structure:
   * <pre>
   * &lt;Logging&gt;
   *   &lt;directory&gt;"logs'&lt;/directory&gt;
   *   &lt;maxLogSize&gt;20000&lt;/maxLogSize&gt;
   *   &lt;daysToRetain&gt;90&lt;/daysToRetain&gt;
   * &lt;/Logging&gt;
   * </pre>
   * will return a properties object with 3 key value pairs: (directory,logs) -
   * (maxLogSize,20000), and (daysToRetain,90).</p>
   *
   * <p>Note that the specified Node ('Logging' in the example above) should
   * only contain leaf Elements containing a text value, rather than a list of
   * sub-Elements; the sub-Elements will be safely ignored.</p>
   ***********************************************************************
   */
  private static void setProperties(TypedProperties props, Element el)   
    {
    NodeList nodeList = el.getChildNodes();

    String k,v;
    Node n = null;

    // iterate over the children of the specified node
    if (nodeList instanceof NodeList)
      {
      for (int i=0; i < nodeList.getLength(); i++) 
        {
        n = nodeList.item(i);

        // if the node is an Element that does not contain children,
        // retrieve its value
        if (n.getNodeType() == Node.ELEMENT_NODE &&
            n.getChildNodes().getLength() == 1)
          {
          k = ((Element)n).getTagName();
          v = ((Element)n).getFirstChild().getNodeValue();
          props.put(k,v);
          }
        }
      }

    } // end set Properties

  /**
   ***********************************************************************
   * This method populates an array of {@link Properties} objects by calling
   * the {@link setProperties} method on each of the nodes contained within the
   * node list passed; this method is meant to be used by calling the 
   * {@link Element.getElementsByTagName} and passing it the resulting 
   * {@link NodeList} along with the <code>Properties</code> array to be initialized.
   ***********************************************************************
   */
  private static TypedProperties[] buildPropertiesArray(NodeList nodeList)
    {
    Vector vctrPropList = new Vector();

    for(int i=0; i < nodeList.getLength(); i++)
      {
      Node n = nodeList.item(i);
      // make sure that we only read Element objects
      // that contain children Elements
      if (n.getNodeType() == Node.ELEMENT_NODE)
        {
        TypedProperties props = new TypedProperties();
        ConfigTranServer.setProperties(props,(Element)n);
        // add the properties to the list only if the properties are not empty
        if (props.size() > 0)
          vctrPropList.add(props);
        }
      } // end for
    TypedProperties[] aryPropList = new TypedProperties[vctrPropList.size()];
    for (int i=0; i < vctrPropList.size(); i++)
      {
      aryPropList[i] = (TypedProperties)vctrPropList.elementAt(i);
      }
    return(aryPropList);

    } // end setPropertiesArray


//  /**
//   ***********************************************************************
//   * Reads info from file
//   ***********************************************************************
//   */
//  public static void SetParams(final String aConfigFileName)
//    {
//    // set up the parameters
//    ConfigInformation.clearParamList();
//
//    ConfigInformation.addReadOnlyParam(APPLICATION_GROUP, VERSION, CURRENT_VERSION, "The application version number");
//    ConfigInformation.addReadOnlyParam(APPLICATION_GROUP, CONFIG_FILENAME, new File(aConfigFileName), "The name of the file containing configuration information");
//    ConfigInformation.addParam(APPLICATION_GROUP, LISTENING_PORT,  8026,             "The port this service is to listen on");
//    ConfigInformation.addParam(APPLICATION_GROUP, FORM_CAPTION,    "TranServer",     "This string is displayed on the main form title bar");
//
//    ConfigInformation.addParam(GATEWAY_GROUP, GATEWAY_SERVER,     "ntws12",        "The IP address of the server where the Innosys gateway is located");
//    ConfigInformation.addParam(GATEWAY_GROUP, GATEWAY_PORT,       1413,            "The IP port that the Innosys gateway listens on");
//    ConfigInformation.addParam(GATEWAY_GROUP, MESSAGE_INTERVAL,   300,             "The amount of time (in milliseconds) to wait before assuming entire message has been received");
//    ConfigInformation.addParam(GATEWAY_GROUP, DEFAULT_HOST,       "1P",            "Default two character host code to use for auto sign on");
//
//    ConfigInformation.addParam(LOGGING_GROUP, LOGGING_DIRECTORY,  new File("LOG"), "Directory where log files will be written");
//    ConfigInformation.addParam(LOGGING_GROUP, DAYS_TO_RETAIN,     90,              "Number of days to retain log file");
//    ConfigInformation.addParam(LOGGING_GROUP, MAX_LOG_SIZE,       200000,          "Maximum log file size in bytes");
//    ConfigInformation.addParam(LOGGING_GROUP, ENABLE_SYSTEM_LOG,  true,            "Enable system level logging");
//    ConfigInformation.addParam(LOGGING_GROUP, ENABLE_TERM_LOG,    true,            "Enable terminal level logging");
//    ConfigInformation.addParam(LOGGING_GROUP, ENABLE_LOCATOR_LOG, true,            "Enable locator level logging");
//
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 01", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 02", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 03", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 04", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 05", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 06", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 07", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 08", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 09", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 10", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 11", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 12", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 13", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 14", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 15", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 16", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 17", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 18", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 19", "",  "Sign on environment");
//    ConfigInformation.addParam(SIGNON_GROUP, "Sign On 20", "",  "Sign on environment");
//
//    // read parameter values from saved file
//    try
//      {
//      ConfigInformation.ReadFromFile( aConfigFileName );
//      // temporary implementation of the xml configuration file:
//      // use the same name as the old file, but replace the extension with '.xml'
//      String config = aConfigFileName.substring(0,aConfigFileName.length()-4);
//      config += ".xml";
//      ConfigTranServer.readConfFile(config);
//      }
//    catch (Exception e)
//      {}
//
//    }

  /**
   ***********************************************************************
   * Test the workings of the ConfigTranServer
   ***********************************************************************
   */
  public static void main(String[] args)
    {
    final String CONFIG_FILE = "C:\\Program Files\\Xmax\\TranServer\\TranServerConfig.xml";

    try
      {
      readConfFile(CONFIG_FILE);
      application.list(System.out);
      logging.list(System.out);
      gateway.list(System.out);
      for(int i=0; i < signOnList.length; i++)
        signOnList[i].list(System.out);


      TypedProperties[] signs;

      System.out.println("----  Amadeus Sign Ons  ----");
      signs = getSignOns("hostCode","1A");
      for(int i=0; i < signs.length; i++)
        signs[i].list(System.out);

      System.out.println("----  Apollo Sign Ons  ----");
      signs = getSignOns("hostCode","1V");
      for(int i=0; i < signs.length; i++)
        signs[i].list(System.out);

      System.out.println("----  Sabre Sign Ons  ----");
      signs = getSignOns("hostCode","AA");
      for(int i=0; i < signs.length; i++)
        signs[i].list(System.out);

      System.out.println("----  Worldspan Sign Ons  ----");
      signs = getSignOns("hostCode","1P");
      for(int i=0; i < signs.length; i++)
        signs[i].list(System.out);

      String s = "";
      }
    catch (Exception e) {System.out.println(e.toString());}

    } // end main

}
