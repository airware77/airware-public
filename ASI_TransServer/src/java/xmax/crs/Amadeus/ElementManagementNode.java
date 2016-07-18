package xmax.crs.Amadeus;


/**
 ***********************************************************************
 * This class represents a recurring structure within a 
 * <code>PoweredPNR_PNRReply</code> document that is used to identify
 * and reference passenger, segments, and remark elements; see below
 * for details on this structure.
 * <p>
 * As mentioned in the api comments of the containing class
 * {@link AmadeusAPIParsePNR.java}, all the specific elements of a
 * <code>PoweredPNR_PNRReply</code> are included into one of three
 * main sections: 
 * <ul>
 *  <li><code>travellerInfo</code></li>  
 *  <li><code>originDestinationDetails</code></li>
 *  <li><code>dataElementsMaster</code></li>
 * </ul></p>
 * <p>Each of which is uniquely identified with a Node titled,
 * respectively:
 * <ul>
 *  <li><code>elementManagementPassenger</code></li>
 *  <li><code>elementManagementItinerary</code></li>
 *  <li><code>elementManagementData</code></li>
 * </ul></p>
 * <p>
 * These nodes all share the following common structure:
 * <pre>
 * &lt;status&gt; &lt;/status&gt;
 * &lt;referencea&gt;
 *   &lt;qualifier&gt; &lt;/qualifier&gt;
 *   &lt;number&gt; &lt;/number&gt;
 * &lt;/referencea&gt;
 * &lt;segmentName&gt; &lt;/segmentName&gt;
 * &lt;lineNumber&gt; &lt;/lineNumber&gt;
 * </pre>
 * <p>
 * Where:
 * <ul>
 *  <li><code>status</code>: indicates an error or warning for the segment
 *    and, presumably, the presence of an error element within the containing
 *    node that this element identifies. This element is not mandatory.</li>
 *  <li><code>reference</code>: this node and it's included elements only
 *    seem to be present when the element to which they are attached can
 *    be potentially referenced from another element.</li>    
 *  <li><code>qualifier</code>: the code contained in this tag qualifies the
 *    reference made. This reference can be a relative reference provided
 *    while the PNR is being created (and while the absolute identity of each
 *    element is not yet known, or, conversely, an absolute reference to 
 *    a known element in a PNR that has already been saved and retrieved.
 *    For more on this see the Amadeus API Documentation.</li>
 *  <li><code>number</code>: an internal identifier that can be either
 *    relative or absolute, depending on the value of the 
 *    <code>qualifier</code> node.
 *  <li><code>segmentName</code>: a two-letter code denoting the type
 *    of segment, such as <code>AP</code> for Contact Element.  These codes
 *    correspond to Terminal Address and cryptic codes. This element appears
 *    to be mandatory.</li>
 *  <li><code>lineNumber</code>: the traditional 'line number' identifier 
 *    that would be visible on a Terminal Address or other Cryptic Code
 *    interface. This element appears to be mandatory.</li>
 * </ul>
 *
 * @author   Philippe Paravicini
 * @version  $Revision: 1$ - $Date: 07/10/2001 3:48:06 PM$
 *
 * @see AmadeusAPIParsePNR
 * @see scanElementManagementNode
 ***********************************************************************
 */
class ElementManagementNode
{
/**
 * Indicates an error or warning in the segment and, presumably, the 
 * presence of an error element within the containing node that this 
 * element identifies.
 */
public String status;

/** 
 * this code qualifies the reference made by {@link #refNumber}; see
 * comments above and the Amadeus API documentation.
 */
public String refQualifier;

/**
 * an internal identifier that can be either relative or absolute,
 * depending on the value of {@link #refQualifier}; see comments above
 * and the Amadeus API documentation.
 */
public String refNumber;

/**
 * a two-letter code denoting the type of segment, such as <code>AP</code> 
 * for Contact Element.  These codes correspond to Terminal Address and 
 * cryptic codes, and must be used for reference to the cryptic interface
 * as they do not used per-se by the Amadeus API. 
 */
public String segmentName;

/**
 * the traditional 'line number' identifier that would be visible on a 
 * Terminal Address or other Cryptic Code interface. This element appears 
 * to be mandatory.
 */
public String lineNumber;


  /**
   ***********************************************************************
   * The constructor with the minimum number of elements required to 
   * instantiate an object from this class: the {@link #segmentName}
   * and {@lineNumber} fields.
   ***********************************************************************
   */
  public ElementManagementNode(String sSegName, String sLineNum)
    {
    segmentName = sSegName;
    lineNumber  = sLineNum;
    } // end constructor

} // end class ElementManagementNode
