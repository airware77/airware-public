package xmax.crs.Amadeus;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xmax.TranServer.ReqGetTicketInfo;
import xmax.crs.GnrcCrs;
import xmax.crs.PNR;
import xmax.crs.TicketInformation;
import xmax.crs.GetPNR.PNRNameElement;
import xmax.crs.GetPNR.PNRRemark;
import xmax.util.FileStore;
import xmax.util.Log.AppLog;
import xmax.util.Log.LoggingEvent;

public class AmadeusGetTicketInfoConversation
{

	/**
	   ***********************************************************************
	   * Retrieves ticket information for the given PNR 
	   ***********************************************************************
	   */
	  public static void getTicketInformation(final GnrcCrs aCRS, final ReqGetTicketInfo aRequest) throws Exception
	  {
		  /* david */
	  //    final String sJDCommand = "JD";
		//  final String sJDResponse = aCRS.HostTransaction(sJDCommand);
		  
		  
		  // get the PNR
		  aRequest.setPnr(new PNR());		  
	      aCRS.GetPNRAllSegments(aRequest.getLocator(),aRequest.getPnr(),true);
	     
		  
		  // run the RTTN command
	      final String sRttnCommand = "RTTN";
		  final String sRttnResponse = aCRS.HostTransaction(sRttnCommand);
		  AppLog.LogEvent("Got RTTN response " + sRttnResponse, LoggingEvent.INFORMATION);
		  
		  // parse the sRttnResponse to get a list of ticket numbers
		  final List<TicketInformation> tktInfoList = new ArrayList<TicketInformation>();
		  parseRttnResponse(sRttnResponse, aRequest.getPnr(), tktInfoList);
		  
		  
		  // run move down commands as needed
	      final String sMDCommand = "MD";
		  boolean hasMore = sRttnResponse.endsWith(")>");
		  int iNumMoveDowns = 1;
		  while ( hasMore )
		  {
			  final String sMdResponse = aCRS.HostTransaction(sMDCommand);
			  
			  parseRttnResponse(sMdResponse, aRequest.getPnr(), tktInfoList);
			  
			  iNumMoveDowns++;
			  if (iNumMoveDowns > 5)
			  {
				  AppLog.LogError("RTTN command required more than 5 move downs for PNR " + aRequest.getLocator());
				  throw new IllegalStateException("RTTN command required more than 5 move downs for PNR " + aRequest.getLocator());				  
			  }
			  
			  hasMore = sMdResponse.endsWith(")>");
		  }
		  
		  
		  // run the TJT ticket info command for each ticket id
		  for (TicketInformation ticketInformation : tktInfoList)
		  {
			  if (isRequestedPassenger(aRequest, ticketInformation))
			  {
				  final String sTjtCommand = "TJT/TK-" + ticketInformation.getTicketNumber();
				  final String sTjtResponse = aCRS.HostTransaction(sTjtCommand);
				  
				//  AppLog.LogInfo("Got TJT response " + sTjtResponse);
				  AppLog.LogEvent("Got TJT response " + sTjtResponse, LoggingEvent.INFORMATION);
				  
				  // parse the sTjtResponse to get the ticket info
				  parseTktResponse(sTjtResponse, ticketInformation );
				  
				  // add the ticket authorization info from the remarks
		//		  getTicketAuthInfo(ticketInformation, aRequest.getPnr());     // skipping this step since CU remark data is not needed
				  
				  aRequest.getTicketInformation().add(ticketInformation);
				  
				  final String sDebug = ticketInformation.writeInfo();
				//  AppLog.LogInfo("TicketInformation = " + sDebug);
				  AppLog.LogEvent(sDebug, LoggingEvent.INFORMATION);

				  final String sTicketInfoFile = "logs/TicketInfo-" + ticketInformation.getTicketNumber() + ".txt";
				  final File ticketFile = new File(sTicketInfoFile);
				  if (ticketFile.exists())
				  {
					  ticketFile.delete();
				  }
				  FileStore.Write(ticketFile, sDebug);
				  
									  
				  System.out.println(sDebug);
			  }
		  }
	  }

	  private static boolean isRequestedPassenger(final ReqGetTicketInfo aRequest, final TicketInformation aTicketInformation)
	  {
		  // no passenger list was created, assume all are to be returned
		  if (aRequest.getPassengerIDs() ==  null)
		  {
			  return true;
		  }
		  
		  // passenger list is empty, assume all are to be returned
		  if (aRequest.getPassengerIDs().size() == 0)
		  {
			  return true;
		  }
		
		  // don't have passenger ID info for this passenger, go ahead and return it
		  if (aTicketInformation.getPsgrID() == null)
		  {
			  return true;
		  }
		  
		  // return true if the given ticket information is for one of the passengers listed
		  return aRequest.getPassengerIDs().contains(aTicketInformation.getPsgrID());
	  }
	
	  static void parseRttnResponse(final String aRttnResponse, final PNR aPnr, final List<TicketInformation> aTktInfoList)
	  {
		//  final List<TicketInformation> tktInfoList = new ArrayList<TicketInformation>();
		  
		  // parse each line of the RTTN response and extract the ticket number if its there
		  final StringTokenizer tk = new StringTokenizer(aRttnResponse, "\r\n");
		  while ( tk.hasMoreTokens() )
		  {
			  final String sLine = tk.nextToken();
			  if (sLine.contains(" FA PAX ") || sLine.contains(" FA INF "))
			  {
				  final String sFullLine;
				  if ( sLine.contains("/S") )
				  {
					  // everything is on a single line
					  sFullLine = sLine;
				  }
				  else
				  {
					  // get the next line and combine the two lines
					  if (tk.hasMoreTokens())
					  {
						  final String sLine2 = tk.nextToken();
					  	  sFullLine = sLine.trim() + sLine2.trim();
					  }
					  else
					  {
						  sFullLine = sLine;
					  }
				  }
				  
				  // extract the ticket number
				  final List<String> ticketNumbers;
				  {
				  // skip past the "FA PAX" part
				  int iPos = sFullLine.indexOf("FA ");
				  iPos = sFullLine.indexOf('-', iPos);
				  final int iStartPos = iPos + 1;
				  final int iEndPos = sFullLine.indexOf('/', iStartPos);
				  final String sTicketNum = sFullLine.substring(iStartPos, iEndPos);
				  
				  ticketNumbers = getTicketNumberList(sTicketNum);
				  }
				  
				  // get the PNR line number
				  final int iPnrLineNum;
				  {
					  final int iPos = sFullLine.indexOf("FA ");
					  final String sLineNum = sFullLine.substring(0, iPos).trim();
					  iPnrLineNum = Integer.parseInt(sLineNum);
				  }
				  
					  
				  // get the passenger index (this field is optional if there is only a single passenger)
				  final String sPsgrInfo    = getFieldValue(sFullLine, "/P", "/");
				  final int iPassengerIndex; 
				  if ((sPsgrInfo instanceof String) && (sPsgrInfo.length() > 0))
				  {
					  iPassengerIndex = Integer.parseInt(sPsgrInfo);
				  }
				  else
				  {
					  iPassengerIndex = 1;
				  }
				  
				  // attempt to lookup the airware passenger id
				  String sPassengerID = null;
				  if (aPnr instanceof PNR)
				  {
					  final PNRNameElement name = aPnr.getName(iPassengerIndex);
					  if (name instanceof PNRNameElement)
					  {
						  sPassengerID = name.getPassengerID();
					  }
				  }
				  
				  
				  // get the ticket date
			//	  final Date dtTicket = getPnrDate(sTicketDate);
				  
				  // get the segment index(es)
				  String sSegmentInfo = getFieldValue(sFullLine, "/S", "/");
				  if (sSegmentInfo instanceof String)
				  {
					  if ( sSegmentInfo.contains("*TRN*") )
					  {
						  // special check to remove *TRN* text that comes back in training environment
						  sSegmentInfo = sSegmentInfo.replace("*TRN*", "");
					  }
					  
					  if ( lineAlreadyRead(aTktInfoList, iPnrLineNum) == false )
					  {
						  final List<Integer> segList = getSegmentList(sSegmentInfo);
						  
						  for (String sTicketNum : ticketNumbers)
						  {
							  final TicketInformation ticketInformation = new TicketInformation();
							  
							  ticketInformation.setTicketNumber(sTicketNum);
							  ticketInformation.setPsgrSequence(iPassengerIndex);
							  ticketInformation.setPsgrID(sPassengerID);
							  ticketInformation.setSegments(segList);
							  ticketInformation.setPnrLineNumber(iPnrLineNum);
							  
							  aTktInfoList.add(ticketInformation);
						  }
					  }
				  }
				  
				  
			  }
		  }
		  
		//  return tktInfoList;
	  }
	  
	  
	  private static boolean lineAlreadyRead(final List<TicketInformation> aTktInfoList, final int aPnrLineNum)
	  {
		  for (TicketInformation ticketInformation : aTktInfoList)
		  {
			  if (ticketInformation.getPnrLineNumber() == aPnrLineNum)
			  {
				  // we've already read in this line
				  return true;
			  }
		  }
		  
		  return false;
	  }
	  
	  
	  private static List<String> getTicketNumberList(final String aTicketNumberField)
	  {
		  final List<String> ticketNumbers = new ArrayList<String>();
		  
		  final int iPos = aTicketNumberField.indexOf('-');
		  
		  if (iPos < 0)
		  {
			  // normal ticket number field - no range indicator
			  ticketNumbers.add(aTicketNumberField);
		  }
		  else
		  {
			  // determine the range of ticket numbers
			  final String sFirstTicket = aTicketNumberField.substring(0, iPos);
			  final String sRange = aTicketNumberField.substring(iPos + 1);
			  
			  ticketNumbers.add(sFirstTicket);
			  /*
			  final int iFirstTicketLength = sFirstTicket.length();
			  final int iRangeLength = sRange.length();
			  final String sLastTicket = sFirstTicket.substring(0, iFirstTicketLength - iRangeLength) + sRange;
			  
			  final long iStartTicket = Long.parseLong(sFirstTicket);
			  final long iStopTicket = Long.parseLong(sLastTicket);
			  
			  
			  for (long i = iStartTicket; i <= iStopTicket; i++)
			  {
				  ticketNumbers.add(Long.toString(i));
				  
				  if (ticketNumbers.size() >= 20)
				  {
					  throw new IllegalArgumentException("Unable to parse ticket numbers from ticket field " + aTicketNumberField);
				  }
			  }
			  */
			  
		  }
		  
		  
		  return ticketNumbers;
	  }
	  
	  private static List<Integer> getSegmentList(final String aSegmentField)
	  {
		  try
		  {
			  final List<Integer> segList = new ArrayList<Integer>();
			
			  if (aSegmentField == null)
			  {
				  throw new IllegalArgumentException("Unable to determine ticket segments.  No '/S' field found");
			  }
			  
			  // separate by commas
			  final StringTokenizer tk = new StringTokenizer(aSegmentField, ",");
			  while (tk.hasMoreTokens())
			  {
				  final String sSegField = tk.nextToken();
				  
				  if (sSegField.contains("-"))
				  {
					  // handle the S8-9 format
					  final StringTokenizer tkDash = new StringTokenizer(sSegField, "-");
					  final String sFirst = tkDash.nextToken().trim();
					  final String sLast = tkDash.nextToken().trim();
					  final int iFirst = Integer.parseInt(sFirst);
					  final int iLast = Integer.parseInt(sLast);
					  
					  for (int i = iFirst; i <= iLast; i++)
					  {
						  segList.add(i);
					  }
				  }
				  else
				  {
					  final Integer iSegNum = Integer.parseInt(sSegField.trim());
					  segList.add(iSegNum);
				  }
			  }
			  
			  if (segList.size() == 0)
			  {
				  throw new IllegalArgumentException("Unable to determine ticket segment info from string " + aSegmentField);
			  }
			  
			  return segList;
		  }
		  catch (Exception e)
		  {
			  throw new IllegalArgumentException("Unable to parse segment information " + aSegmentField, e);
		  }
	  }
	  
	  
	  private static final String END_OF_LINE = "\r";
	  private static final String END_OF_RESPONSE = null;
	  
	  static void parseTktResponse(final String aTktResponse, final TicketInformation aTicketInformation)
	  {
		  final String sAgency      = getFieldValue(aTktResponse, "AGENCY  - ", " ");
		  final String sOfficeAgent = getFieldValue(aTktResponse, "OFFID/AS- ", "ITEM -");   // gets both the office ID and the agent sign
	//	  final String sDocType     = getFieldValue(aTktResponse, "DOC TYPE- ", "CURR -");  
	//	  final String sAlProv      = getFieldValue(aTktResponse, "AL/PROV - ", "STATUS -");  
		  String sDocument          = getFieldValue(aTktResponse, "DOCUMENT- ", " ");     
		  
		  final String sItem        = getFieldValue(aTktResponse, " ITEM - ", " ");  
		  final String sCurrency    = getFieldValue(aTktResponse, " CURR - ", " ");  
	//	  final String sStatus      = getFieldValue(aTktResponse, " STATUS - ", " ");  
		  final String sTour        = getFieldValue(aTktResponse, " TOUR : ", " ");  
		  final String sInvoice     = getFieldValue(aTktResponse, " INVOICE : ", " ");  
		  
		  
		  final String sPassenger   = getFieldValue(aTktResponse, "PASSENGER :", END_OF_LINE);  
		  final String sFop1        = getFieldValue(aTktResponse, "FOP1 :", END_OF_LINE);  
		  final String sFop2        = getFieldValue(aTktResponse, "FOP2 :", END_OF_LINE);  
		  final String sFop3        = getFieldValue(aTktResponse, "FOP3 :", END_OF_LINE);  
		  final String sTax         = getFieldValue(aTktResponse, "TAX :", END_OF_LINE);     // gets all taxes
		 
		  final String sExchValue   = getFieldValue(aTktResponse, "EXCH VALUE :", END_OF_LINE);  
		  final String sNewTicket   = getFieldValue(aTktResponse, "NEW TICKET :", END_OF_LINE);  
		  final String sOrigin      = getFieldValue(aTktResponse, "ORIGIN :", END_OF_LINE);  
		  final String sPurchaser   = getFieldValue(aTktResponse, "PURCHASER :", END_OF_LINE);  
		  final String sFareCalc    = getFieldValue(aTktResponse, "FARE CALC :", END_OF_RESPONSE);  
		  
		  final boolean isETicket   = aTktResponse.contains("ELEC TKT");
		  final boolean isAutoPriced = sFareCalc.contains("AUTOMATED");
		  
		  // extract the words AUTOMATED, MANUAL, and PRICED
		  final String sFareCalc2 = sFareCalc.replace("AUTOMATED", "").replace("MANUAL", "").replace("PRICED", "").trim();
		  String sFareCalc3 = (sFareCalc2.length() > 500) ? sFareCalc2.substring(0, 500) : sFareCalc2;
		  if (sFareCalc3.endsWith(">"))
		  {
			  sFareCalc3 = sFareCalc3.substring(0, sFareCalc3.length() - 1).trim();
		  }
		  
		  // separate the office id and agent sign
		  final String sOfficeID;
		  final String sAgent;
		  {
	      final int iPos = sOfficeAgent.indexOf(' ');
	      if (iPos >= 0)
	      {
	    	  sOfficeID = sOfficeAgent.substring(0, iPos).trim();
	    	  sAgent = sOfficeAgent.substring(iPos).trim();
	      }
	      else
	      {
	    	  sOfficeID = sOfficeAgent;
	    	  sAgent = null;
	      }
		  }
		  
		  // get the passenger ID
		  /*
		  final String sPsgrID;
		  {
			  final int iStartPos = sPassenger.indexOf("(ID");
			  final int iEndPos = sPassenger.indexOf(')');
			  if (iEndPos > iStartPos)
			  {
				  sPsgrID = sPassenger.substring(iStartPos + 3, iEndPos);
			  }
			  else
			  {
				  sPsgrID = null;
			  }
		  }
		  */
		  
		  
		  // get amounts
		  final String sCredit = getLastFieldValue(aTktResponse, "CREDIT");
		  final BigDecimal dCredit = new BigDecimal(sCredit);
		  
		  final String sCash = getLastFieldValue(aTktResponse, "CASH");
		  final BigDecimal dCash = new BigDecimal(sCash);
		  
		  final String sTaxAmount = getLastFieldValue(aTktResponse, "TAX");
		  final BigDecimal dTaxAmount = new BigDecimal(sTaxAmount);
		  
		  final String sFeeAmount = getLastFieldValue(aTktResponse, "FEES");
		  final BigDecimal dFeeAmount = new BigDecimal(sFeeAmount);
		  
		  final String sCommAmount = getLastFieldValue(aTktResponse, "COMM");
		  final BigDecimal dCommAmount = new BigDecimal(sCommAmount);
		  
		  // get the ticket date
		  final String sDateLine = getFieldValue(aTktResponse, "AGENCY  -", "CASH");
		  final Date dtTicket;
		  if (sDateLine instanceof String)
		  {
			  final StringTokenizer tk = new StringTokenizer(sDateLine, " ");
			  final String sAgencyNum = tk.nextToken();
			  final String sTicketDate = tk.nextToken();
			  dtTicket = getPnrDate(sTicketDate);
		  }
		  else
		  {
			  dtTicket = null;
		  }
		  
		  
		  // set ticket value by summing the credit and cash amounts
		  final BigDecimal ticketValue = dCredit.add(dCash);
		  
		  if (sDocument.endsWith("-"))
		  {
			  sDocument = sDocument.substring(0, sDocument.length() -1);
		  }
		  
		  // set fields in the ticket info response
		  aTicketInformation.setAuthID(null);     // don't have this yet - this is the CU number from the getPNR remark
		  aTicketInformation.setAuthSequence(0);  // don't have this yet - this is the sequence number after the CU number from the PNR remark
		//  aTicketInformation.setPsgrID(sPsgrID);     // set from RTTN response and PNR data 
		//  aTicketInformation.setPsgrSequence(0);     // set from RTTN response
		//  aTicketInformation.setTicketNumber(null);  // set from RTTN response
		  aTicketInformation.setTicketValue(ticketValue);
		  aTicketInformation.setTicketDate(dtTicket); 
		  aTicketInformation.setEticket(isETicket);
		  aTicketInformation.setCurrencyCode(sCurrency);
		  aTicketInformation.setAgency(sAgency);
		  aTicketInformation.setOfficeID(sOfficeID); 
		  aTicketInformation.setAgent(sAgent);      
		  aTicketInformation.setItem(sItem);
		  aTicketInformation.setCcAmount(dCredit);  
		  aTicketInformation.setCashAmount(dCash);
		  aTicketInformation.setTaxAmount(dTaxAmount);   
		  aTicketInformation.setFeeAmount(dFeeAmount); 
		  aTicketInformation.setCommissionAmount(dCommAmount);   
		  aTicketInformation.setDocumentNumber(sDocument);
		  aTicketInformation.setPsgrName(sPassenger);
		  aTicketInformation.setTourCode(sTour);     
		  aTicketInformation.setInvoice(sInvoice);    
		  aTicketInformation.setFop1(sFop1);
		  aTicketInformation.setFop2(sFop2);
		  aTicketInformation.setFop3(sFop3);
		  aTicketInformation.setExchangeValue(sExchValue);
		  aTicketInformation.setNewTicket(sNewTicket);
		  aTicketInformation.setOrigin(sOrigin);
		  aTicketInformation.setPurchaser(sPurchaser);
		  aTicketInformation.setFareLadder(sFareCalc3);
		  aTicketInformation.setAutomated(isAutoPriced);
		  
		  // get the taxes
		  final Map<String, BigDecimal> taxMap = getTaxAmounts(sTax);
		  for (String sCode : taxMap.keySet())
		  {
			  final BigDecimal value = taxMap.get(sCode);
			  aTicketInformation.getTaxes().put(sCode, value);
		  }
	  }
	  
	  
	  static void getTicketAuthInfo(final TicketInformation aTicketInformation, final PNR aPNR)
	  {
		  // look for a ticket remark for the given passenger
		  final PNRRemark[] remarks = aPNR.getRemarks();
		  if (remarks instanceof PNRRemark[])
		  {
			  final int iPsgrSeq = aTicketInformation.getPsgrSequence();
			  final String sEndsWith = "/P" + iPsgrSeq;
			  
			  for (PNRRemark remark : remarks)
			  {
				  final String sText = remark.getRemarkText().trim();
				  
				  if ( sText.startsWith("*CU") )
				  {
					  if (sText.endsWith(sEndsWith) || (remark.NameNumber == iPsgrSeq) )
					  {
						  final int iCUPos   = sText.indexOf("*CU");
						  final int iDashPos = sText.indexOf('-', iCUPos);
						  
						  if ( (iCUPos >= 0) && (iDashPos >= 0) )
						  {
							  final int iStartPos = iCUPos + 3;
							  final String sAuthID = sText.substring(iStartPos, iDashPos);
							  
							  final int iSlashPos = sText.indexOf('/', iDashPos);
							  final String sAuthSeq;
							  if (iSlashPos >= 0)
							  {
								  sAuthSeq = sText.substring(iDashPos + 1, iSlashPos);
							  }
							  else
							  {
								  sAuthSeq = sText.substring(iDashPos + 1);
							  }
							  
							  final int iAuthSeq = Integer.parseInt(sAuthSeq);
							  
							  aTicketInformation.setAuthID(sAuthID);
							  aTicketInformation.setAuthSequence(iAuthSeq);
						  }
					  }
				  }
			  }
		  }
		  
	  }	  
	  
	  
	  /**
	   * Look for the field with the given label
	   * @param aInputString
	   * @param aStartPattern
	   * @param aEndPattern
	   * @return
	   */
	  static String getFieldValue(final String aInputString, final String aStartPattern, final String aEndPattern)
	  {
		  // null checks
		  if ( (aInputString == null) || (aStartPattern == null) )
		  {
			  return null;
		  }
		  
		  // see if the start pattern is contained within the string
		  int iStartPos = aInputString.indexOf(aStartPattern);
		  if (iStartPos < 0)
		  {
			  return null;
		  }
		  
		  
		  // advance to the end of the start pattern
		  iStartPos += aStartPattern.length();
		  
		  // now determine the position of the end pattern, if specified
		  final int iEndPos;
		  if (aEndPattern == null)
		  {
			  iEndPos = -1;         // no end pattern
		  }
		  else if (aEndPattern.equals(END_OF_LINE))
		  {
			  // get the position of the first CR or LF character 
			  final int iCRPos = aInputString.indexOf('\r', iStartPos);
			  final int iLFPos = aInputString.indexOf('\n', iStartPos);
			  if ((iCRPos >= 0) && (iLFPos >=0) )
			  {
				  iEndPos = (iCRPos < iLFPos) ? iCRPos : iLFPos;
			  }
			  else if (iCRPos >= 0)
			  {
				  iEndPos = iCRPos;
			  }
			  else if (iLFPos >= 0)
			  {
				  iEndPos = iLFPos;
			  }
			  else
			  {
				  iEndPos = -1;
			  }
		  }
		  else
		  {
			 iEndPos = aInputString.indexOf(aEndPattern, iStartPos + 1);   // end pattern is some specific string
		  }
		  
		  
		  // extract the data between the start and end patterns
		  final String sFieldValue;
		  if (iEndPos > iStartPos)
		  {
			  sFieldValue = aInputString.substring(iStartPos, iEndPos);
		  }
		  else
		  {
			  sFieldValue = aInputString.substring(iStartPos);
		  }
		  
		  
		  return sFieldValue.trim();
	  }
	  
	  
	  /**
	   * Returns a field value from the end of the line
	   * @param aInputString
	   * @param aFieldName
	   */
	  static String getLastFieldValue(final String aInputString, final String aFieldName)
	  {
		  final StringTokenizer tk = new StringTokenizer(aInputString, "\r\n");
		  
		  while (tk.hasMoreTokens())
		  {
			  final String sLine = tk.nextToken().trim();
			  
			  if (sLine.endsWith(aFieldName))
			  {
				  final int iEndPos = sLine.lastIndexOf(" " + aFieldName);
				  int iStartPos = sLine.lastIndexOf(' ', iEndPos - 1);
				  if (iStartPos < 0)
				  {
					iStartPos = 0;  
				  }
				  
				  final String sValue = sLine.substring(iStartPos, iEndPos).trim();
				  return sValue;
			  }
		  }
		  
		  return null;
	  }
	  
	  
	  /**
	   * Converts a date string into a date object
	   * @param aDateString
	   * @return
	   */
	  static Date getPnrDate(final String aDateString)
	  {
		if (aDateString == null)
		{
			return null;
		}
		
		try
		{
			final SimpleDateFormat fmtDate = new SimpleDateFormat("ddMMMyy");
			final Date returnDate = fmtDate.parse(aDateString.trim());
			return returnDate;
		} 
		catch (ParseException e)
		{
			throw new IllegalArgumentException("Unable to parse date " + aDateString);
		}
	  }
	  
	  
	  /**
	   * Parse up the tax amount line
	   * @param aTaxLine
	   * @return
	   */
	  static Map<String, BigDecimal> getTaxAmounts(final String aTaxLine)
	  {
		  final Map<String, BigDecimal> taxMap = new HashMap<String, BigDecimal>(); 
		  
		  if (aTaxLine == null)
		  {
			  return taxMap;
		  }
		  
		  // read in each tax amount
		  final StringTokenizer tk = new StringTokenizer(aTaxLine, " ");
		  while (tk.hasMoreTokens())
		  {
			  final String sTaxInfo = tk.nextToken();
			  if (sTaxInfo.length() > 2)
			  {
				  final int iPos = sTaxInfo.length() - 2;
				  
				  final String sTaxAmount = sTaxInfo.substring(0, iPos);
				  final String sTaxCode   = sTaxInfo.substring(iPos);
				  final BigDecimal dTaxAmount = new BigDecimal(sTaxAmount);
				  
				  taxMap.put(sTaxCode, dTaxAmount);
			  }
		  }
		  
		  return taxMap;
	  }
}
