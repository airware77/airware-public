package xmax.crs.Amadeus;

import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Method;
/**
 ***********************************************************************
 * 
 * 
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 02/12/2002 5:44:23 PM$
 *
 * @see 
 ***********************************************************************
 */
public class AmadeusAPISerializer extends XMLSerializer
{
  /** used to create an output format that will please Amadeus */
  private OutputFormat outputFormat;

  /**
   ***********************************************************************
   *
   ***********************************************************************
   */
  public AmadeusAPISerializer()
    {
    super();

    boolean doIndent = false;
    outputFormat = new OutputFormat(Method.XML,null,doIndent);

    outputFormat.setOmitXMLDeclaration(true);
    outputFormat.setOmitDocumentType(true);
    outputFormat.setStandalone(true);

    super.setOutputFormat(outputFormat);
    
    } // end constructor

  /** void this function to fool castor */
  public void setOutputFormat(OutputFormat f) {}

} // end class AmadeusAPISerializer
