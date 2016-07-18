
//Title:        CRS Test Project
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David Fairchild
//Company:      XMAX Corp
//Description:  This class has procedures and functions for converting raw PNR
// data into formatted PNR data

package xmax.crs.Amadeus;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Date;
import java.text.SimpleDateFormat;
import xmax.crs.*;
import xmax.crs.GetPNR.*;
import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;
import xmax.crs.GnrcParser;
import xmax.TranServer.GnrcFormat;

public class AmadeusParsePNR
{
  /** 
   ***********************************************************************
   * returns true if the given string is the beginning of a PNR
   ***********************************************************************
   */
  public static boolean isValidPNRData(final StringBuffer aPNRData)
    {
    if ( aPNRData instanceof StringBuffer )
      return( isValidPNRData(aPNRData.toString()) );
    else
      return(false);
    }


  public static boolean isValidPNRData(final String[] aPNRData)
    {
    if ( aPNRData instanceof String[] )
      {
      final String sPNRData = GnrcParser.ArrayToString(aPNRData);
      return( isValidPNRData(sPNRData) );
      }
    else
      return(false);
    }


  public static boolean isValidPNRData(final String aPNRData)
    {
    final String[] BAD_RESPONSES = {"INVALID RECORD LOCATOR","NO MATCH FOR RECORD LOCATOR","SECURED PNR"};
    if ( GnrcParser.containedWithin(aPNRData,BAD_RESPONSES) )
      return(false);


    final StringTokenizer PNRToken = new StringTokenizer(aPNRData,"\r\n");
    if ( PNRToken.countTokens() < 3 )
      return(false);

    return(true);
    }

 /** 
  ***********************************************************************
  * This procedure completely parses the given PNR
  ***********************************************************************
  */
 public static void parsePNR(final String[] aRawData, final PNR aPNR)
   {
   // save raw data strings
   final String sAMADEUS_LINE_STARTS = "(^[0-9 ][0-9 ][0-9][ \\.][A-Z ][A-Z0-9]|^--- RLP ---|^RP/)";
   final String sCombinedPNRData = GnrcParser.getCombinedHostResponse(aRawData);
   final String sPNRData         = GnrcParser.findNewLines(sCombinedPNRData,sAMADEUS_LINE_STARTS);

   aPNR.setRawPNRData(aRawData);
   aPNR.setPNRData(sPNRData);
   aPNR.setReadDate();

 //  aPNR.AllSectionResponse = aPNR.getRawPNRData();  // for now
 //  aPNR.AllSections        = aPNR.getPNRData();

   // set PNR locator, psuedo city, agent sign, and CRS code
   if ( isValidPNRData(sPNRData) )
     {
     aPNR.setCrs(BaseCrs.AMADEUS_CODE);
     scanPnrID(aPNR);

     // scan the various sections
     scanNameSection(aPNR);
     scanItinSection(aPNR);
     scanHeaderRemarks(aPNR);
     scanGeneralFacts(aPNR);     // read SSRs, OSIs
     scanGeneralRemarks(aPNR);   // read general remarks
     scanPhoneRemarks(aPNR);
     scanTicketRemarks(aPNR);
     scanAddressRemarks(aPNR);
     scanFopRemarks(aPNR);
     }
   }

 /** 
  ***********************************************************************
  * Returns the entire PNR as one string
  ***********************************************************************
  */
 /*
 public String GetEntirePNR(final PNR aPNR) throws Exception
   {
   if ( aPNR instanceof PNR )
      {   // make sure you have raw data in AllSection
      if ( GnrcFormat.NotNull(aPNR.AllSections) )
        return(aPNR.AllSections);
      else if ( aPNR.AllSectionResponse instanceof String[] )
        {
        final String sAMADEUS_LINE_STARTS = "(^[0-9 ][0-9 ][0-9][ \\.][A-Z ][A-Z0-9]|^--- RLP ---|^RP/)";

        String sPNRData  = GnrcParser.getCombinedHostResponse( aPNR.AllSectionResponse );
        sPNRData         = GnrcParser.findNewLines(sPNRData,sAMADEUS_LINE_STARTS);
        aPNR.AllSections = sPNRData;
        return( aPNR.AllSections );
        }
      }

   return("");
   }
 */
 /** 
  ***********************************************************************
  * Extract agent signon from PNR data
  ***********************************************************************
  */
 private static void scanPnrID(final PNR aPNR)
   {
   try
     {
     final String sPNRData = aPNR.getPNRData();

     // scan in the header lines
     final StringTokenizer lines = new StringTokenizer(sPNRData,"\r\n");
     String sLine;
     while ( lines.hasMoreTokens() )
       {
       sLine = lines.nextToken().trim();

       if ( sLine.startsWith("RP/") )
         {
         // go to fourth field
         final StringTokenizer fields = new StringTokenizer(sLine," /");
         String sField;
         if ( fields.countTokens() >= 4 )
           {
                        sField      = fields.nextToken();
           final String sPseudoCity = fields.nextToken();
                        sField      = fields.nextToken();
           final String sAgentSign  = fields.nextToken();

           // go to last field
           while ( fields.hasMoreTokens() )
             sField = fields.nextToken();

           final String sLocator = sField;

           aPNR.setLocator(sLocator);
           aPNR.setPseudoCity(sPseudoCity);
           aPNR.setAgentSign(sAgentSign);
           }
         else
           aPNR.addError("Unable to scan ID line for locator, agent sign, and pseudo city. not enough fields: " + sLine);

         return;
         }
       }

     aPNR.addError("Unable to find ID line for locator, agent sign, and pseudo city.");
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to set locator, agent sign, and pseudo city: " + e.toString() );
     }

   }

 /** 
  ***********************************************************************
  * Extract locator from PNR data
  ***********************************************************************
  */
 /*
 public String GetLocator(final PNR aPNR)
   {
   // make sure PNR object is valid
   if ( (aPNR instanceof PNR) == false )
     return("");

   // make sure you have raw data in AllSection
   if ( (aPNR.AllSectionResponse instanceof String[]) == false )
     return("");

   // scan in the header lines
   final StringTokenizer tk = new StringTokenizer(aPNR.AllSectionResponse[0],"\r\n");
   String sLine;
   while ( tk.hasMoreTokens() )
     {
     sLine = tk.nextToken().trim();
     if ( sLine.startsWith("RP/") )
       {
       // go to last field
       final StringTokenizer fields = new StringTokenizer(sLine," /");
       String sField = "";
       while ( fields.hasMoreTokens() )
         sField = fields.nextToken();

       final String sLocator = sField;
       return(sLocator);
       }
     }

   return("");
   }
 */
 /** 
  ***********************************************************************
  * Extract agent signon from PNR data
  ***********************************************************************
  */
 /*
 public String GetAgentSign(final PNR aPNR)
   {
   // make sure PNR object is valid
   if ( (aPNR instanceof PNR) == false )
     return("");

   // make sure you have raw data in AllSection
   if ( (aPNR.AllSectionResponse instanceof String[]) == false )
     return("");

   // scan in the header lines
   final StringTokenizer tk = new StringTokenizer(aPNR.AllSectionResponse[0],"\r\n");
   String sLine;
   while ( tk.hasMoreTokens() )
     {
     sLine = tk.nextToken().trim();

     if ( sLine.startsWith("RP/") )
       {
       // go to fourth field
       final StringTokenizer fields = new StringTokenizer(sLine," /");
       final String sAgentSign;
       if ( fields.countTokens() >= 4 )
         {
         final String sField1 = fields.nextToken();
         final String sField2 = fields.nextToken();
         final String sField3 = fields.nextToken();

         sAgentSign          = fields.nextToken();
         }
       else
         sAgentSign = "";

       return(sAgentSign);
       }
     }

    return("");
   }
 */
  /** 
   ***********************************************************************
   * This function returns the Pseudo City code used when creating the PNR
   ***********************************************************************
   */
  /*
  public String GetPseudoCity(final PNR aPNR) throws Exception
    {
   // make sure PNR object is valid
   if ( (aPNR instanceof PNR) == false )
     return("");

   // make sure you have raw data in AllSection
   if ( (aPNR.AllSectionResponse instanceof String[]) == false )
     return("");

   // scan in the header lines
   final StringTokenizer tk = new StringTokenizer(aPNR.AllSectionResponse[0],"\r\n");
   String sLine;
   while ( tk.hasMoreTokens() )
     {
     sLine = tk.nextToken().trim();

     if ( sLine.startsWith("RP/") )
       {
       // go to second field
       final StringTokenizer fields = new StringTokenizer(sLine," /");
       final String sPseudoCity;
       if ( fields.countTokens() >= 2 )
         {
         final String sField1 = fields.nextToken();
         sPseudoCity          = fields.nextToken();
         }
       else
         sPseudoCity = "";

       return(sPseudoCity);
       }
     }

    return("");
    }
  */
 /** 
  ***********************************************************************
  * Extract Header data
  ***********************************************************************
  */
 /*
 public String [] GetHeader(final PNR aPNR) throws Exception
   {
   // get the header lines
   final String sPNRData = GetEntirePNR(aPNR);
   final String HEADER_SECTION_START = "^[\\-0A-Z]";
   aPNR.HeaderSection                = readSection(sPNRData, HEADER_SECTION_START );

   // scan in the header lines
   final StringTokenizer tk = new StringTokenizer(aPNR.HeaderSection,"\r\n");
   if ( tk.hasMoreTokens() )
     {
     final String[] HeaderLines = new String[ tk.countTokens() ];
     for ( int i = 0; tk.hasMoreTokens(); i++ )
       HeaderLines[i] = tk.nextToken();
     return( HeaderLines );
     }
   else
     return(null);
   }
 */

 private static void scanHeaderRemarks(final PNR aPNR)
   {

   try
     {
     // extract the header section
     final String HEADER_SECTION_START = "^[\\-0A-Z]";
     final String sPNRData    = aPNR.getPNRData();
     final String sHeaderData = readSection(sPNRData, HEADER_SECTION_START );

     // scan in the header lines
     final StringTokenizer lines = new StringTokenizer(sHeaderData,"\r\n");
     String sHeaderLine;
     while ( lines.hasMoreTokens() )
       {
       sHeaderLine = lines.nextToken();
       aPNR.addHeaderRemark(sHeaderLine);
       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan header lines: " + e.toString() );
     }

   }

 /** 
  ***********************************************************************
  * Extract Phone data
  ***********************************************************************
  */
 /*
 public String [] GetPhones(final PNR aPNR) throws Exception
   {
   // get the phone lines
   final String PHONE_SECTION_START      = "^[ 1-9][ 0-9][0-9] AP ";
   final String sPNRData = GetEntirePNR(aPNR);
   aPNR.PhoneSection     = readSection(sPNRData, PHONE_SECTION_START );

   // scan in the phone lines
   final StringTokenizer tk = new StringTokenizer(aPNR.PhoneSection,"\r\n");
   if ( tk.hasMoreTokens() )
     {
     // allocate an array
     final String[] PhoneLines = new String[ tk.countTokens() ];

     String sLine;
     for ( int i = 0; tk.hasMoreTokens(); i++ )
       {
       sLine = tk.nextToken();
       if ( sLine.length() > 6 )
         sLine = sLine.substring(6).trim();
       PhoneLines[i] = sLine;
       }

     return( PhoneLines );
     }
   else
     return(null);
   }
  */


 private static void scanPhoneRemarks(final PNR aPNR)
   {
   try
     {
     // extract the phone lines
     final String PHONE_SECTION_START      = "^[ 1-9][ 0-9][0-9] AP ";
     final String sPNRData   = aPNR.getPNRData();
     final String sPhoneData = readSection(sPNRData, PHONE_SECTION_START );

     // scan in the phone lines
     PNRPhoneRemark remark;
     String sPhoneLine;
     String sLineNum;
     final StringTokenizer lines = new StringTokenizer(sPhoneData,"\r\n");
     while ( lines.hasMoreTokens() )
       {
       sPhoneLine = lines.nextToken();

       try
         {
         sLineNum = sPhoneLine.substring(0,3).trim();

         if ( sPhoneLine.length() > 6 )
           sPhoneLine = sPhoneLine.substring(6).trim();

         remark = new PNRPhoneRemark(sPhoneLine);
         remark.MessageNumber = Integer.parseInt(sLineNum);
         aPNR.addRemark(remark);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan phone number '" + sPhoneLine + "': " + e.toString() );
         }

       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan phone section: " + e.toString() );
     }

   }


 /** 
  ***********************************************************************
  * Extract Address data
  ***********************************************************************
  */
 /*
 public String [] GetAddress(final PNR aPNR) throws Exception
   {
   // get the address lines
   final String ADDRESS_SECTION_START = "^[ 1-9][ 0-9][0-9] (AB|AM) ";
   final String sPNRData = GetEntirePNR(aPNR);
   aPNR.AddressSection   = readSection(sPNRData, ADDRESS_SECTION_START );

   // scan in the address lines
   final StringTokenizer tk = new StringTokenizer(aPNR.AddressSection,"\r\n");
   if ( tk.hasMoreTokens() )
     {
     // allocate an array
     final String[] AddressLines = new String[ tk.countTokens() ];

     String sLine;
     for ( int i = 0; tk.hasMoreTokens(); i++ )
       {
       sLine = tk.nextToken();
       if ( sLine.length() > 6 )
         sLine = sLine.substring(6).trim();
       AddressLines[i] = sLine;
       }

     return( AddressLines );
     }
   else
     return(null);
   }
 */

 private static void scanAddressRemarks(final PNR aPNR)
   {
   try
     {
     // extract the ticket lines
     final String ADDRESS_SECTION_START = "^[ 1-9][ 0-9][0-9] (AB|AM) ";
     final String sPNRData    = aPNR.getPNRData();
     final String sAddressData = readSection(sPNRData, ADDRESS_SECTION_START );


     // scan in the address lines
     PNRAddressRemark remark;
     String sAddressLine;
     String sLineNum;
     final StringTokenizer lines = new StringTokenizer(sAddressData,"\r\n");
     while ( lines.hasMoreTokens() )
       {
       sAddressLine = lines.nextToken();

       try
         {
         sLineNum = sAddressLine.substring(0,3).trim();
         if ( sAddressLine.length() > 6 )
           sAddressLine = sAddressLine.substring(6).trim();

         remark = new PNRAddressRemark(sAddressLine);
         remark.MessageNumber = Integer.parseInt(sLineNum);
         aPNR.addRemark(remark);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan address remark '" + sAddressLine + "': " + e.toString() );
         }

       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan address section: " + e.toString() );
     }

   }

 /** 
  ***********************************************************************
  * Extract Ticket data
  ***********************************************************************
  */
 /*
 public String [] GetTicketing(final PNR aPNR) throws Exception
   {
   // get the ticket lines
   final String TICKET_SECTION_START     = "^[ 1-9][ 0-9][0-9] TK ";
   final String sPNRData = GetEntirePNR(aPNR);
   aPNR.TicketSection    = readSection(sPNRData, TICKET_SECTION_START );


   // scan the ticket lines
   final StringTokenizer tk = new StringTokenizer(aPNR.TicketSection,"\r\n");
   if ( tk.hasMoreTokens() )
     {
     // allocate an array
     final String[] TicketLines = new String[ tk.countTokens() ];

     String sLine;
     for ( int i = 0; tk.hasMoreTokens(); i++ )
       {
       sLine = tk.nextToken();
       if ( sLine.length() > 6 )
         sLine = sLine.substring(6).trim();
       TicketLines[i] = sLine;
       }

     return( TicketLines );
     }
   else
     return(null);
   }
 */

 private static void scanTicketRemarks(final PNR aPNR)
   {
   try
     {
     // extract the ticket lines
     final String TICKET_SECTION_START = "^[ 1-9][ 0-9][0-9] TK ";
     final String sPNRData    = aPNR.getPNRData();
     final String sTicketData = readSection(sPNRData, TICKET_SECTION_START );


     // scan in the ticket lines
     PNRTicketRemark remark;
     String sTicketLine;
     String sLineNum;
     final StringTokenizer lines = new StringTokenizer(sTicketData,"\r\n");
     while ( lines.hasMoreTokens() )
       {
       sTicketLine = lines.nextToken();

       try
         {
         sLineNum = sTicketLine.substring(0,3).trim();
         if ( sTicketLine.length() > 6 )
           sTicketLine = sTicketLine.substring(6).trim();

         remark = new PNRTicketRemark(sTicketLine);
         remark.MessageNumber = Integer.parseInt(sLineNum);
         aPNR.addRemark(remark);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan ticket remark '" + sTicketLine + "': " + e.toString() );
         }

       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan ticket section: " + e.toString() );
     }

   }

 /** 
  ***********************************************************************
  * Extract Form of Payment data
  ***********************************************************************
  */
 private static void scanFopRemarks(final PNR aPNR)
   {
   try
     {
     // extract form of payment remark section
     final String FOP_SECTION_START        = "^[ 1-9][ 0-9][0-9] FP";
     final String sPNRData = aPNR.getPNRData();
     final String sFOPData = readSection(sPNRData,FOP_SECTION_START);

     // read each line of the FOP section
     final StringTokenizer lines = new StringTokenizer(sFOPData,"\r\n");
     String sLine;
     String sLineNum;
     PNRFopRemark remark;
     while ( lines.hasMoreTokens() )
       {
       sLine = lines.nextToken().trim();
       try
         {
         sLineNum = sLine.substring(0,3).trim();
         final int iPos = sLine.indexOf("FP");
         if ( iPos >= 0 )
           sLine = sLine.substring(iPos + 2).trim();

         remark = new PNRFopRemark(sLine);
         remark.MessageNumber = Integer.parseInt(sLineNum);
         aPNR.addRemark(remark);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan form of payment line '" + sLine + "': " + e.toString());
         }
       }

     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan form of payment remarks: " + e.toString() );
     }

   }

 /** 
  ***********************************************************************
  * Extract Remark data
  ***********************************************************************
  */
 /*
 public PNRRemark [] GetRemarks(final PNR aPNR) throws Exception
   {
   final String REMARK_SECTION_START     = "^[ 1-9][ 0-9][0-9*] (RM[A-Z]? |RI[RIFA] |AK )";
   final String sPNRData = GetEntirePNR(aPNR);
   aPNR.RemarkSection    = readSection(sPNRData, REMARK_SECTION_START );
  // aPNR.RemarkSection    = GnrcParser.findNewLines(aPNR.RemarkSection,REMARK_SECTION_START);


   final Vector RemarkList = new Vector();
   String sLine;
   PNRRemark RemarkData;
   PNRNameElement Name = null;
   PNRItinSegment seg  = null;
   final StringTokenizer tk = new StringTokenizer(aPNR.RemarkSection,"\r\n");
   while ( tk.hasMoreTokens() )
     {
     sLine = tk.nextToken();
     RemarkData = new PNRRemark();

     // get remark number
     try
       {
       final String sRemarkNum = sLine.substring(0,3).trim();
       RemarkData.MessageNumber = Integer.parseInt(sRemarkNum);
       }
     catch (Exception e)
       {
       RemarkData.MessageNumber = 0;
       }

     RemarkData.RemarkText = sLine.substring(6).trim();

     if ( sLine.indexOf("RII") >= 0 )
       {
       RemarkData.RemarkType = RemarkData.ITINERARY_REMARK;
       RemarkData.RemarkText = sLine.substring(7).trim();
       }
     else if ( sLine.indexOf("RIR") >= 0 )
       {
       RemarkData.RemarkType = RemarkData.ITINERARY_REMARK;
       RemarkData.RemarkText = sLine.substring(7).trim();
       }
     else if ( sLine.indexOf("RIF") >= 0 )
       {
       RemarkData.RemarkType = RemarkData.INVOICE_REMARK;
       RemarkData.RemarkText = sLine.substring(7).trim();
       }
     else
       {
       RemarkData.RemarkType = RemarkData.GENERAL_REMARK;
       RemarkData.RemarkText = sLine.substring(6).trim();
       }

     // see if name or segment specifier is listed
     Name = null;
     seg  = null;
     StringTokenizer words = new StringTokenizer(sLine," /");
     while ( words.hasMoreTokens() )
       {
       String sWord = words.nextToken();

       try
         {
         if ( RegExpMatch.matches(sWord,"^P([1-9]|[1-9][0-9])") )
           {
           final String sNameNumber = sWord.substring(1);
           final int iNameNumber    = Integer.parseInt(sNameNumber);
           Name = aPNR.getName(iNameNumber);
           }
         else if ( RegExpMatch.matches(sWord,"^S([1-9]|[1-9][0-9])") )
           {
           final String sSegmentNumber = sWord.substring(1);
           final int iSegmentNumber    = Integer.parseInt(sSegmentNumber);
           seg = aPNR.getItinSegment(iSegmentNumber);
           }
         else if ( sWord.equals("2V") )
           {
           PNRItinSegment[] segarray = aPNR.getSegments();
           for ( int i = 0; i < segarray.length; i++ )
             {
             if ( segarray[i].RawData.indexOf("TRN 2V") >= 0 )
               seg = segarray[i];
             }
           }

         }
       catch (Exception e)
         {}
       }

     if ( Name instanceof PNRNameElement )
       {
       RemarkData.LastName  = Name.LastName;
       RemarkData.FirstName = Name.FirstName;
       }
     if ( seg instanceof PNRItinSegment )
       RemarkData.ItinSegment = seg.SegmentNumber;

     RemarkList.add(RemarkData);
     }

   if ( RemarkList.size() > 0 )
     {
     final PNRRemark[] Remarks = new PNRRemark[ RemarkList.size() ];
     RemarkList.toArray(Remarks);
     return(Remarks);
     }
   else
     return(null);
   }
 */

 private static void scanGeneralRemarks(final PNR aPNR)
   {
   try
     {
     // extract the remark section
     final String REMARK_SECTION_START     = "^[ 1-9][ 0-9][0-9*] (RM[A-Z]? |RI[RIFA] |AK )";
     final String sPNRData    = aPNR.getPNRData();
     final String sRemarkData = readSection(sPNRData, REMARK_SECTION_START );


    // sRemarkData    = GnrcParser.findNewLines(sRemarkData,REMARK_SECTION_START);


     String sLine;
     PNRRemark newRemark;
     final StringTokenizer lines = new StringTokenizer(sRemarkData,"\r\n");
     while ( lines.hasMoreTokens() )
       {
       sLine = lines.nextToken();

       try
         {
         // get remark number
         final String sRemarkNum = sLine.substring(0,3).trim();

         if ( sLine.indexOf("RII") >= 0 )
           {
           final PNRItinRemark remark = new PNRItinRemark();
           remark.RemarkText = sLine.substring(7).trim();
           newRemark = remark;
           }
         else if ( sLine.indexOf("RIR") >= 0 )
           {
           final PNRItinRemark remark = new PNRItinRemark();
           remark.RemarkText = sLine.substring(7).trim();
           newRemark = remark;
           }
         else if ( sLine.indexOf("RIF") >= 0 )
           {
           final PNRInvoiceRemark remark = new PNRInvoiceRemark();
           remark.RemarkText = sLine.substring(7).trim();
           newRemark = remark;
           }
         else
           {
           final PNRGeneralRemark remark = new PNRGeneralRemark();
           remark.RemarkText = sLine.substring(6).trim();
           newRemark = remark;
           }

         newRemark.MessageNumber = Integer.parseInt(sRemarkNum);

         setNameAssignment(newRemark);
         setSegmentAssignment(newRemark);

         aPNR.addRemark(newRemark);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan remark line '" + sLine + "': " + e.toString());
         }
       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan remark section: " + e.toString());
     }

   }


 /** 
  ***********************************************************************
  * Extract General Facts data
  ***********************************************************************
  */
 /*
 public PNRGFactsRemark[] GetGeneralFacts(final PNR aPNR) throws Exception
   {
   // extract the general facts section
   final String GFAX_SECTION_START       = "^[ 1-9][ 0-9][0-9] (SSR|OSI) ";
   final String sPNRData = GetEntirePNR(aPNR);
   aPNR.GeneralFactsSection = readSection(sPNRData, GFAX_SECTION_START );
   // remove line wrapping
  // String aPNR.GeneralFactsSection = GnrcParser.findNewLines(aPNR.GeneralFactsSection,GFAX_SECTION_START);


   // get itinerary
   PNRNameElement Name = null;
   PNRItinSegment seg  = null;


   // scan in the general facts lines
   final Vector GFactList = new Vector();
   final StringTokenizer tk = new StringTokenizer(aPNR.GeneralFactsSection,"\r\n");

   String sGFAXData;
   PNRGFactsRemark RemarkData;
   String sRemarkType;
   String sServiceCode;
   String sCarrier;
   String sRemarkNum;
   int iRemarkNumber;
   String sMessage;
   while ( tk.hasMoreTokens() )
     {
     sGFAXData = tk.nextToken();

     // get fact number
     sRemarkNum = sGFAXData.substring(0,3).trim();
     if ( Character.isDigit( sRemarkNum.charAt(0) ) )
       iRemarkNumber = Integer.parseInt(sRemarkNum);
     else
       iRemarkNumber = 0;

     // set the remark type
     sRemarkType    = sGFAXData.substring(4,7);
     if ( sRemarkType.equals("SSR") )
       {
       sRemarkType  = PNRGFactsRemark.SSR_REMARK;
       sServiceCode = sGFAXData.substring(8,12);
       sCarrier     = sGFAXData.substring(13,15);
       sMessage     = sGFAXData.substring(15).trim();
       }
     else
       {
       sRemarkType  = PNRGFactsRemark.OSI_REMARK;
       sServiceCode = "";
       sCarrier     = sGFAXData.substring(8,10).trim();
       sMessage     = sGFAXData.substring(11).trim();
       }


     // see if name or segment specifier is listed
     Name = null;
     seg  = null;
     StringTokenizer words = new StringTokenizer(sMessage," /");
     while ( words.hasMoreTokens() )
       {
       String sWord = words.nextToken();

       try
         {
         if ( RegExpMatch.matches(sWord,"^P([1-9]|[1-9][0-9])") )
           {
           final String sNameNumber = sWord.substring(1);
           final int iNameNumber    = Integer.parseInt(sNameNumber);
           Name = aPNR.getName(iNameNumber);
           }
         else if ( RegExpMatch.matches(sWord,"^S([1-9]|[1-9][0-9])") )
           {
           final String sSegmentNumber = sWord.substring(1);
           final int iSegmentNumber    = Integer.parseInt(sSegmentNumber);
           seg = aPNR.getItinSegment(iSegmentNumber);
           }

         }
       catch (Exception e)
         {}
       }


     RemarkData = new PNRGFactsRemark();

     RemarkData.MessageNumber = iRemarkNumber;
     RemarkData.RemarkType    = sRemarkType;
     RemarkData.Code          = sServiceCode;
     RemarkData.RemarkText    = sMessage;
     RemarkData.Carrier       = sCarrier;
     if ( Name instanceof PNRNameElement )
       {
       RemarkData.LastName  = Name.LastName;
       RemarkData.FirstName = Name.FirstName;
       }
     if ( seg instanceof PNRItinSegment )
       RemarkData.ItinSegment = seg.SegmentNumber;

     GFactList.add(RemarkData);
     }

   // convert vector into an array
   if ( GFactList.size() > 0 )
     {
     final PNRGFactsRemark[] Remarks = new PNRGFactsRemark[ GFactList.size() ];
     GFactList.toArray(Remarks);
     return(Remarks);
     }
   else
     return(null);
   }
 */

 private static void scanGeneralFacts(final PNR aPNR)
   {
   try
     {
     // extract the general facts section
     final String GFAX_SECTION_START       = "^[ 1-9][ 0-9][0-9] (SSR|OSI) ";
     final String sPNRData       = aPNR.getPNRData();
     final String sGFactsSection = readSection(sPNRData, GFAX_SECTION_START );

     // remove line wrapping
     // sGFactsSection = GnrcParser.findNewLines(sGFactsSection,GFAX_SECTION_START);


     // scan in the general facts lines
     final StringTokenizer lines = new StringTokenizer(sGFactsSection,"\r\n");

     String sGFAXData;
     String sRemarkType;
     String sRemarkNum;
     PNRRemark newRemark;

     while ( lines.hasMoreTokens() )
       {
       sGFAXData = lines.nextToken();

       try
         {
         // get the remark type
         sRemarkType = sGFAXData.substring(4,7);
         if ( sRemarkType.equals("SSR") )
           {
           final PNRSsrRemark remark = new PNRSsrRemark();

           sRemarkNum        = GnrcParser.getSubstring(sGFAXData,0,3).trim();
           remark.Code       = GnrcParser.getSubstring(sGFAXData,8,12);
           remark.Carrier    = GnrcParser.getSubstring(sGFAXData,13,15);
           remark.RemarkText = GnrcParser.getSubstring(sGFAXData,15,200).trim();

           final PNRSeatRemark seatRemark = ssrToSeatRemark(remark);
           if ( seatRemark instanceof PNRSeatRemark )
             {
             aPNR.addRemark(remark);    // add the regular SSR remark in addition to the seat remark
             newRemark = seatRemark;
             }
           else
             newRemark = remark;
           }
         else
           {
           final PNROsiRemark remark = new PNROsiRemark();

           sRemarkNum        = sGFAXData.substring(0,3).trim();
           remark.Carrier    = sGFAXData.substring(8,10).trim();
           remark.RemarkText = sGFAXData.substring(11).trim();

           newRemark = remark;
           }

         if ( Character.isDigit( sRemarkNum.charAt(0) ) )
           newRemark.MessageNumber = Integer.parseInt(sRemarkNum);
         else
           newRemark.MessageNumber = 0;

         setNameAssignment(newRemark);
         setSegmentAssignment(newRemark);

         aPNR.addRemark(newRemark);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan general fact line '" + sGFAXData + "': " + e.toString() );
         }
       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan general facts section: " + e.toString() );
     }

   }

  /** 
   ***********************************************************************
   * This function converts an SSR remark into a corresponding seat remark
   ***********************************************************************
   */
  private static PNRSeatRemark ssrToSeatRemark(final PNRSsrRemark aSsrRemark)
    {
    // if this SSR is for anything but a seat assignment, return null
    final String[] SEAT_CODES = {"SEAT","NSST","SMST"};
    if ( GnrcParser.itemIndex(aSsrRemark.Code,SEAT_CODES) < 0 )
      return(null);

    final String sInputString = aSsrRemark.RemarkText;

    final PNRSeatRemark SeatInfo = new PNRSeatRemark();

    // get the segment number at the end of the string
    try
      {
      final MatchInfo match_info = RegExpMatch.getLastMatch(sInputString,"/S[1-9][0-9]?");
      if ( match_info instanceof MatchInfo )
        {
        final String sSegment = match_info.MatchString.substring(2).trim();
        SeatInfo.ItinSegment  = Integer.parseInt(sSegment);
        }
      }
    catch (Exception e)
      {}


    // get the passenger number at the end of the string
    try
      {
      final MatchInfo match_info = RegExpMatch.getLastMatch(sInputString,",P[1-9][0-9]?");
      if ( match_info instanceof MatchInfo )
        {
        final String sPsgrNum = match_info.MatchString.substring(2).trim();
        SeatInfo.NameNumber   = Integer.parseInt(sPsgrNum);
        }
      }
    catch (Exception e)
      {}



    // go through each field looking for a seat assignment
    StringTokenizer PsgrFields;
    String sField;
    final StringTokenizer fields = new StringTokenizer(sInputString,"/");
    while ( fields.hasMoreTokens() )
      {
      sField = fields.nextToken();
      PsgrFields = new StringTokenizer(sField,",");
      if ( PsgrFields.countTokens() == 2 )
        {
        final String sSeatNumber = PsgrFields.nextToken();

        // remove smoking indicator
        if ( sSeatNumber.endsWith("S") )
          {
          SeatInfo.Seat = sSeatNumber.substring(0,sSeatNumber.length() - 1);
          SeatInfo.Smoking = true;
          }
        else if ( sSeatNumber.endsWith("N") )
          SeatInfo.Seat = sSeatNumber.substring(0,sSeatNumber.length() - 1);
        else
          SeatInfo.Seat = sSeatNumber;

        break;
        }
      }

    if ( aSsrRemark.Code.equals("SMST") )
      SeatInfo.Smoking = true;

    return(SeatInfo);
    }

 /** 
  ***********************************************************************
  * Used to make name associations for all remarks
  ***********************************************************************
  */
 private static void setNameAssignment(final PNRRemark aRemark)
   {

   try
     {
     final String NAME_ASSIGN_PATTERN = "/P[1-9][0-9]?";

     final MatchInfo name_match = RegExpMatch.getFirstMatch(aRemark.RemarkText,NAME_ASSIGN_PATTERN);
     if ( name_match instanceof MatchInfo )
       {
       final String sNameNumber = name_match.MatchString.substring(2).trim();
       aRemark.NameNumber       = Integer.parseInt(sNameNumber);

       // remove the name association
       final StringBuffer remarkBuf = new StringBuffer(aRemark.RemarkText);
       remarkBuf.delete(name_match.getStartPosition(),name_match.getEndPosition());
       aRemark.RemarkText = remarkBuf.toString().trim();
       }
     }
   catch (Exception e)
     {
     }

   }

 /** 
  ***********************************************************************
  * Used to make segment associations for all remarks
  ***********************************************************************
  */
 private static void setSegmentAssignment(final PNRRemark aRemark)
   {

   try
     {
     final String SEGMENT_ASSIGN_PATTERN = "/S[1-9][0-9]?";

     final MatchInfo segment_match = RegExpMatch.getFirstMatch(aRemark.RemarkText,SEGMENT_ASSIGN_PATTERN);
     if ( segment_match instanceof MatchInfo )
       {
       final String sSegmentNumber = segment_match.MatchString.substring(2).trim();
       aRemark.ItinSegment         = Integer.parseInt(sSegmentNumber);

       // remove the segment association
       final StringBuffer remarkBuf = new StringBuffer(aRemark.RemarkText);
       remarkBuf.delete(segment_match.getStartPosition(),segment_match.getEndPosition());
       aRemark.RemarkText = remarkBuf.toString().trim();
       }

       /*   for Amtrak associated remarks
       if ( sWord.equals("2V") )
         {
         final PNRItinSegment[] segarray = aPNR.GetItin();
         for ( int i = 0; i < segarray.length; i++ )
           {
           if ( segarray[i].RawData.indexOf("TRN 2V") >= 0 )
             aRemark.ItinSegment = segarray[i].SegmentNum;
           }
         }
       */
     }
   catch (Exception e)
     {
     }

   }

 /** 
  ***********************************************************************
  * Extract Family data
  ***********************************************************************
  */
 /*
 public PNRFamilyElement [] GetFamilies(final PNR aPNR) throws Exception
   {
   final String NAME_SECTION_START       = "^[ 1-9][ 0-9][0-9]\\.[A-Z]";       // starts with an optional space, digit, period, and a digit
   final String sPNRData = GetEntirePNR(aPNR);
   aPNR.NameSection      = readSection(sPNRData, NAME_SECTION_START );

   // scan in the families
   return( scanAllFamilies(aPNR.NameSection) );
   }
 */
 /** 
  ***********************************************************************
  * Extract Individual name data
  ***********************************************************************
  */
 /*
 public PNRNameElement [] GetNames(final PNR aPNR) throws Exception
   {
   final PNRFamilyElement[] Families = GetFamilies(aPNR);
   final Vector NameList = new Vector();

   // for every family
   for ( int iFamNum = 0; iFamNum < Families.length; iFamNum++ )
     {
     // for every member
     for ( int iMemberNum = 0; iMemberNum < Families[iFamNum].FamilyMembers.length; iMemberNum++ )
       NameList.add( Families[iFamNum].FamilyMembers[iMemberNum] );
     }

   // convert vector of family member objects to an array
   if ( NameList.size() > 0 )
     {
     final PNRNameElement[] Names = new PNRNameElement[ NameList.size() ];
     NameList.toArray(Names);
     return(Names);
     }
   else
     return(null);
   }
 */
 /** 
  ***********************************************************************
  * Extract Itinerary data
  ***********************************************************************
  */
 /*
 public PNRItinSegment [] GetItin(final PNR aPNR) throws Exception
   {
   final String ITIN_SECTION_START       = "^[ 1-9][ 0-9][0-9] ( [A-Z0-9][A-Z0-9]|CCR|HHL|TRN)";       // starts with a one or two digit field, a space, then a letter
   final String sPNRData = GetEntirePNR(aPNR);
   aPNR.ItinSection      = readSection(sPNRData, ITIN_SECTION_START );
 //  aPNR.ItinSection = GnrcParser.findNewLines(aPNR.ItinSection,ITIN_SECTION_START);

   // scan in the itinerary segments
   return( scanAllSegments(aPNR.ItinSection) );
   }
  */
 /** 
  ***********************************************************************
  * This procedure uses the given string tokenizer to read in PNR strings
  * it returns a string representing the name section.
  ***********************************************************************
  */
  private static String readSection(final String aPNRData,final String aStartPattern) throws Exception
    {
    final StringBuffer sSection = new StringBuffer("");
    final StringTokenizer tk    = new StringTokenizer(aPNRData,"\r\n");
    String sPNRLine;
    boolean section_started = false;

    // read name section
    while ( tk.hasMoreTokens() )
      {
      sPNRLine = tk.nextToken();

      // see if this is the beginning of the section
      if ( RegExpMatch.matches(sPNRLine,aStartPattern) )
        {
        sSection.append(sPNRLine + "\r\n");
        section_started = true;
        }
      else if ( section_started )
        break;
      }

    return( sSection.toString() );
    }

 /** 
  ***********************************************************************
  * Extract Family data
  ***********************************************************************
  */
 private PNRFamilyElement [] scanAllFamilies(final String aNameData) throws Exception
   {
   final Vector FamilyVector = new Vector();
   PNRFamilyElement Family;


   // parse up the individual families and scan them in one at a time
   final StringTokenizer NameToken = new StringTokenizer(aNameData,".");
   String sFamilyInfo = "";
   while ( NameToken.hasMoreTokens() )
     {
     sFamilyInfo = NameToken.nextToken();

     // get rid of digits at the end
     while ( RegExpMatch.matches(sFamilyInfo,"[0-9]$") )
       sFamilyInfo = sFamilyInfo.substring(0,sFamilyInfo.length() - 1);
     sFamilyInfo = sFamilyInfo.trim();

     Family = scanSingleFamily(sFamilyInfo);
     if ( Family instanceof PNRFamilyElement )
       FamilyVector.add(Family);
     }

   // convert vector of family objects to an array
   if ( FamilyVector.size() > 0 )
     {
     final PNRFamilyElement[] Families = new PNRFamilyElement[ FamilyVector.size() ];
     FamilyVector.toArray(Families);
     return(Families);
     }
   else
     return(null);
   }



 private static void scanNameSection(final PNR aPNR)
   {
   try
     {
     // extract the name data from the PNR
     final String NAME_SECTION_START       = "^[ 1-9][ 0-9][0-9]\\.[A-Z]";       // starts with an optional space, digit, period, and a digit
     final String sPNRData  = aPNR.getPNRData();
     final String sNameData = readSection(sPNRData, NAME_SECTION_START );

     // parse up the individual families and scan them in one at a time
     PNRFamilyElement Family;
     final StringTokenizer NameToken = new StringTokenizer(sNameData,".");
     String sFamilyInfo;
     while ( NameToken.hasMoreTokens() )
       {
       sFamilyInfo = NameToken.nextToken();

       try
         {
         // get rid of digits at the end
         while ( RegExpMatch.matches(sFamilyInfo,"[0-9]$") )
           sFamilyInfo = sFamilyInfo.substring(0,sFamilyInfo.length() - 1);
         sFamilyInfo = sFamilyInfo.trim();

         Family = scanSingleFamily(sFamilyInfo);
         if ( Family instanceof PNRFamilyElement )
           aPNR.addFamily(Family);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan family '" + sFamilyInfo + "': " + e.toString() );
         }
       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan name section: " + e.toString() );
     }

   }

 /** 
  ***********************************************************************
  * This procedure loops until it reads all the names on the PNR
  ***********************************************************************
  */
 private static PNRFamilyElement scanSingleFamily(final String aInString) throws Exception
   {
   // make sure this is a valid string
   if ( aInString.length() < 4 )
     return(null);

   // separate the name and name options
   final String sNameData;
   final String sOptionData;
   final int iPos = aInString.indexOf('(');
   if ( iPos >= 0 )
     {
     sNameData   = aInString.substring(0,iPos).trim();
     sOptionData = aInString.substring(iPos).trim();
     }
   else
     {
     sNameData   = aInString.trim();
     sOptionData = "";
     }

   final PNRNameElement member = new PNRNameElement();

   // get last name, first name, and title
   final StringTokenizer nameTokens = new StringTokenizer(sNameData,"/ ");
   if ( nameTokens.countTokens() >= 2 )
     {
     member.LastName  = nameTokens.nextToken();
     member.FirstName = nameTokens.nextToken();
     if ( nameTokens.hasMoreTokens() )
       member.Title = nameTokens.nextToken();
     }
   else
     return(null);


   // get PTC and any remarks
   final StringTokenizer optionTokens = new StringTokenizer(sOptionData,"()");
   String sRemark;
   while ( optionTokens.hasMoreTokens() )
     {
     sRemark = optionTokens.nextToken().trim();

     if ( sRemark.equals("PFA") || sRemark.equals("ADT") )
       member.PTC = PNRNameElement.PTC_ADULT;
     else if ( sRemark.equals("MIL") )
       member.PTC = PNRNameElement.PTC_MILITARY;
     else if ( sRemark.equals("SNR") )
       member.PTC = PNRNameElement.PTC_SENIOR;
     else if ( sRemark.equals("CHD") )
       member.PTC = PNRNameElement.PTC_CHILD;
     else if ( RegExpMatch.matches(sRemark,"^P[0-9][0-9]") )
       {
       final String sAge = sRemark.substring(1,3);
       member.Age = Integer.parseInt(sAge);
       member.PTC = PNRNameElement.PTC_CHILD;
       }
     else if ( RegExpMatch.matches(sRemark,"^ID *[0-9]") )
       member.setPassengerID(sRemark.substring(2).trim());
     else if ( RegExpMatch.matches(sRemark,"^ *[0-9]") )
       member.setPassengerID(sRemark);
     else if ( RegExpMatch.matches(sRemark,"^INF/ *[A-Z]") )
       member.InfantName = sRemark.substring(4).trim();
     }


   final PNRFamilyElement family = new PNRFamilyElement(member);
   family.RawData  = aInString.trim();
   return(family);
   }

 /** 
  ***********************************************************************
  * Extract Itinerary data
  ***********************************************************************
  */
 private PNRItinSegment [] scanAllSegments(final String aItinData) throws Exception
   {
   // parse up the individual segments and scan them in one at a time
   final Vector ItinVector = new Vector();
   PNRItinSegment Segment;
   final StringTokenizer ItinToken = new StringTokenizer(aItinData,"\r\n");
   while ( ItinToken.hasMoreTokens() )
     {
     Segment = scanSingleItinSegment( ItinToken.nextToken() );
     if ( Segment instanceof PNRItinSegment )
       ItinVector.add(Segment);
     }

   // convert vector of itinerary segment objects to an array
   if ( ItinVector.size() > 0 )
     {
     final PNRItinSegment[] Segments = new PNRItinSegment[ ItinVector.size() ];
     ItinVector.toArray(Segments);
     return(Segments);
     }
   else
     return(null);
   }


 private static void scanItinSection(final PNR aPNR)
   {
   try
     {
     // extract the itinerary section
     final String ITIN_SECTION_START       = "^[ 1-9][ 0-9][0-9] ( [A-Z0-9][A-Z0-9]|CCR|HHL|TRN)";       // starts with a one or two digit field, a space, then a letter
     final String sPNRData  = aPNR.getPNRData();
     final String sItinData = readSection(sPNRData, ITIN_SECTION_START );


     // parse up the individual segments and scan them in one at a time
     PNRItinSegment Segment;
     final StringTokenizer ItinToken = new StringTokenizer(sItinData,"\r\n");
     String sItinLine;
     while ( ItinToken.hasMoreTokens() )
       {
       sItinLine = ItinToken.nextToken();

       try
         {
         Segment = scanSingleItinSegment( sItinLine );
         if ( Segment instanceof PNRItinSegment )
           aPNR.addSegment(Segment);
         }
       catch (Exception e)
         {
         aPNR.addError("Unable to scan itin segment '" + sItinLine + "': " + e.toString() );
         }

       }
     }
   catch (Exception e)
     {
     aPNR.addError("Unable to scan itinerary section: " + e.toString() );
     }

   }


 /** 
  ***********************************************************************
  * This function scans in a single line of itinerary data
  ***********************************************************************
  */
 private static PNRItinSegment scanSingleItinSegment(final String aItinString) throws Exception
   {
   // patterns to match beginning of string with
//   final String INS_SEGMENT_START     = "^[0-9 ][0-9]  INS";
   final String HTL_SEGMENT_START     = "^[0-9 ][0-9 ][0-9] HHL ";
//   final String MHTL_SEGMENT_START    = "^[0-9 ][0-9] [A-Z0-9][A-Z0-9] HTL ";
   final String CAR_SEGMENT_START     = "^[0-9 ][0-9 ][0-9] CCR ";
//   final String MCAR_SEGMENT_START    = "^[0-9 ][0-9] [A-Z0-9][A-Z0-9] CAR ";
//   final String TVL_CAR_SEGMENT_START = "^[0-9 ][0-9] TVL [A-Z0-9][A-Z0-9] [A-Z][A-Z][0-9] CAR ";
//   final String TVL_HTL_SEGMENT_START = "^[0-9 ][0-9] TVL [A-Z0-9][A-Z0-9] [A-Z][A-Z][0-9] HTL ";
//   final String TVL_AIR_SEGMENT_START = "^[0-9 ][0-9] TVL [A-Z0-9][A-Z0-9] [A-Z][A-Z][0-9] AIR ";
   final String AIR_SEGMENT_START     = "^[0-9 ][0-9 ][0-9]  [A-Z0-9][A-Z0-9][0-9 ][0-9 ][0-9 ][0-9] [A-Z]";
   final String ARUNK_SEGMENT_START   = "(ARUNK|ARNK)";


   // call the appropriate scanning function
   if ( RegExpMatch.matches(aItinString,HTL_SEGMENT_START) )
     return( scanItinHotelSegment(aItinString) );
   else if ( RegExpMatch.matches(aItinString,CAR_SEGMENT_START) )
     return( scanItinCarSegment(aItinString) );
   else if ( RegExpMatch.matches(aItinString,AIR_SEGMENT_START) )
     return( scanItinAirSegment(aItinString) );
   else if ( RegExpMatch.matches(aItinString,ARUNK_SEGMENT_START) )
     return( scanItinArunkSegment(aItinString) );
   else
     return( scanItinOtherSegment(aItinString) );
   }

 /** 
  ***********************************************************************
  * This function scans in a single itinerary segment
  ***********************************************************************
  */
 private static PNRItinSegment scanItinOtherSegment(final String aItinString) throws Exception
   {
   final String SEGMENT_START  = "^[0-9 ][0-9 ][0-9]";
   if ( RegExpMatch.matches(aItinString,SEGMENT_START) )
     {
     final PNRItinSegment is = new PNRItinSegment();

     // get the segment number
     final String sSegmentNum = aItinString.substring(0,3).trim();
     is.SegmentNumber   = Integer.parseInt(sSegmentNum);
     is.RawData         = aItinString.substring(3).trim();
     return(is);
     }
   else
     return(null);
   }

 /** 
  ***********************************************************************
  * This function scans in a single itinerary ARUNK segment
  ***********************************************************************
  */
 private static PNRItinSegment scanItinArunkSegment(final String aItinString) throws Exception
   {
   final String SEGMENT_START  = "^[0-9 ][0-9 ][0-9]";
   if ( RegExpMatch.matches(aItinString,SEGMENT_START) )
     {
     final PNRItinArunkSegment is = new PNRItinArunkSegment();

     // get the segment number
     final String sSegmentNum = aItinString.substring(0,3).trim();
     is.SegmentNumber   = Integer.parseInt(sSegmentNum);
     is.RawData         = aItinString.substring(3).trim();
     return(is);
     }
   else
     return(null);
   }

 /** 
  ***********************************************************************
  * This function scans in a single air segment
  ***********************************************************************
  */
 private static PNRItinAirSegment scanItinAirSegment(final String aItinString) throws Exception
   {
   final PNRItinAirSegment ais = new PNRItinAirSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"(FLWN|FLOWN|PAST)") )
     return(null);

   // save the raw data
   ais.RawData = aItinString;

   // get the segment number
   final String sSegmentNum = GnrcParser.getSubstring(aItinString,0,3).trim();
   ais.Carrier              = GnrcParser.getSubstring(aItinString,5,7).trim();
   final String sFlightNum  = GnrcParser.getSubstring(aItinString,7,11).trim();
   ais.InventoryClass       = GnrcParser.getSubstring(aItinString,12,13).trim();
   final String sDepDate    = GnrcParser.getSubstring(aItinString,14,19).trim();
   final String sDayOfWeek  = GnrcParser.getSubstring(aItinString,20,21).trim();
   ais.DepartureCityCode    = GnrcParser.getSubstring(aItinString,22,25).trim();
   ais.ArrivalCityCode      = GnrcParser.getSubstring(aItinString,25,28).trim();
   ais.Status               = GnrcParser.getSubstring(aItinString,29,31).trim();
   final String sNumSeats   = GnrcParser.getSubstring(aItinString,31,33).trim();
   final String sDepTime    = GnrcParser.getSubstring(aItinString,43,48).trim();
   final String sArrTime    = GnrcParser.getSubstring(aItinString,48,55).trim();
   ais.RemoteLocator        = GnrcParser.getSubstring(aItinString,55,65).trim();
   ais.isCodeShare          = false;

   // scan the flight number
   try
     {
     ais.FlightNumber = Integer.parseInt(sFlightNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan air segment.  Error scanning flight number.  aItinString =  <" + aItinString + ">");
     }

   // scan the segement number
   try
     {
     ais.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan air segment.  Error scanning segment number.  aItinString =  <" + aItinString + ">");
     }


   // set the number of seats
   try
     {
     if ( sNumSeats.length() > 0 )
       ais.NumberOfSeats = Integer.parseInt(sNumSeats);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan air segment.  Error scanning number of seats.  aItinString =  <" + aItinString + ">");
     }

   // set the departure date and time
   try
     {
     if ( sDepTime.length() > 0 )
       ais.DepartureDateTime = GnrcParser.ScanCRSDateTimeString(sDepDate,sDepTime);
     else
       ais.DepartureDateTime = GnrcParser.ScanCRSDateString(sDepDate);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan air segment.  Error scanning departure date/time.  aItinString =  <" + aItinString + ">");
     }


   // set the arrival date and time
   try
     {
     if ( sArrTime.length() > 0 )
       ais.ArrivalDateTime = GnrcParser.ScanCRSDateTimeString(sDepDate,sArrTime);
     else
       ais.ArrivalDateTime = GnrcParser.ScanCRSDateString(sDepDate);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan air segment.  Error scanning arrival date/time.  aItinString =  <" + aItinString + ">");
     }

  return(ais);
  }

 /** 
  ***********************************************************************
  * This function scans in a single TVL air segment
  ***********************************************************************
  */
 private static PNRItinAirSegment scanItinTVLAirSegment(final String aItinString) throws Exception
   {
   final PNRItinAirSegment AirItinSeg = new PNRItinAirSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"(FLWN|FLOWN|PAST)") )
     return(null);

   AirItinSeg.RawData           = aItinString;

   StringTokenizer fields = new StringTokenizer(aItinString," /");
   if ( fields.countTokens() < 6 )
     throw new Exception("Unable to scan TVL air segment.  Must have at least 6 fields. Instring = <" + aItinString + ">");

   String sSegmentNum         = fields.nextToken();
   String sLabel1             = fields.nextToken();
   AirItinSeg.Carrier         = fields.nextToken();
   String sStatus             = fields.nextToken();
   AirItinSeg.Status          = sStatus.substring(0,2);
   String sLabel2             = fields.nextToken();
   String sDepDate            = fields.nextToken();
   if ( sDepDate.length() > 5 )
     sDepDate = sDepDate.substring(0,5);

  AirItinSeg.DepartureDateTime = GnrcParser.ScanCRSDateTimeString(sDepDate,"1200A");
  AirItinSeg.ArrivalDateTime   = GnrcParser.ScanCRSDateTimeString(sDepDate,"1200A");

   try
     {
     AirItinSeg.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL air segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   try
     {
     AirItinSeg.NumberOfSeats = Integer.parseInt(sStatus.substring(2));
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL air segment.  Unable to scan number of seats. Instring = <" + aItinString + ">");
     }

   // read in the options
   String sOption = "";
   try
     {
     while ( fields.hasMoreTokens() )
       {
       sOption = fields.nextToken("/");
       setAirItinOption(AirItinSeg,sOption);
       }
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL air segment.  Unable to scan option " + sOption + ". Instring = <" + aItinString + ">");
     }

  /*
   // handle overnight flights
  final long MSECS_PER_DAY = 24 * 60 * 60 * 1000;
   while ( AirItinSeg.ArrivalDateTime < AirItinSeg.DepartureDateTime )
     AirItinSeg.ArrivalDateTime  += MSECS_PER_DAY;
  */

   return( AirItinSeg );
   }

 /** 
  ***********************************************************************
  * This function scans in a single insurance segment
  ***********************************************************************
  */
 private static PNRItinInsSegment scanItinInsSegment(final String aItinString) throws Exception
   {
   // check if line is long enough
   if ( aItinString.length() < 28 )
     throw new Exception("Unable to scan insurance segment.  line < 28 chars long. Instring = <" + aItinString + ">");


   final PNRItinInsSegment InsItinSeg = new PNRItinInsSegment();

   InsItinSeg.RawData         = aItinString;
   try
     {InsItinSeg.SegmentNumber   = Integer.parseInt(aItinString.substring(0,2).trim());}
   catch (Exception e)
     {
     throw new Exception("Unable to scan insurance segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   InsItinSeg.ServiceType     = aItinString.substring(4,7);
   InsItinSeg.ServiceProvider = aItinString.substring(8,10);
   InsItinSeg.OrigDate        = GnrcParser.ScanCRSDateString( aItinString.substring(11,16) );
   InsItinSeg.BookingData     = aItinString.substring(28);
   InsItinSeg.City            = aItinString.substring(24,27);
   InsItinSeg.Status          = aItinString.substring(19,21);
   try
     {InsItinSeg.NumberOfSeats = Integer.parseInt(aItinString.substring(21,22));}
   catch (Exception e)
     {
     throw new Exception("Unable to scan insurance segment.  Unable to scan seat count. Instring = <" + aItinString + ">");
     }

   return( InsItinSeg );
   }

 /** 
  ***********************************************************************
  * This function scans in a single car rental segment
  ***********************************************************************
  */
 private static PNRItinCarSegment scanItinCarSegment(final String aItinString) throws Exception
   {
   final PNRItinCarSegment CarItinSeg = new PNRItinCarSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"PAST") )
     return(null);

   CarItinSeg.RawData         = aItinString;

   final StringTokenizer fields = new StringTokenizer(aItinString," /");
   if ( fields.countTokens() < 8 )
     throw new Exception("Unable to scan car segments.  Must have at least 8 fields. Instring = <" + aItinString + ">");

   final String sSegmentNum   = fields.nextToken();
   final String sLabel        = fields.nextToken();
   CarItinSeg.CompanyCode     = fields.nextToken();
   final String sStatus       = fields.nextToken();
   CarItinSeg.SegmentStatus   = sStatus.substring(0,2);
   CarItinSeg.PickupCityCode  = fields.nextToken();
   final String sPickupDate   = fields.nextToken();
   final String sDropoffDate  = fields.nextToken();
   CarItinSeg.CarTypeCode     = fields.nextToken();

   CarItinSeg.PickUpDateTime  = GnrcParser.ScanCRSDateString(sPickupDate);
   CarItinSeg.DropoffDateTime = GnrcParser.ScanCRSDateString(sDropoffDate);

   try
     {
     CarItinSeg.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan car segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   try
     {
     CarItinSeg.NumCars = Integer.parseInt(sStatus.substring(2));
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan car segment.  Unable to scan car count. Instring = <" + aItinString + ">");
     }

   // read in the options
   String sOption = "";
   try
     {
     while ( fields.hasMoreTokens() )
       {
       sOption = fields.nextToken("/");
       setCarItinOption(CarItinSeg,sOption);
       }
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan car segment.  Unable to scan option " + sOption + ". Instring = <" + aItinString + ">");
     }

   return( CarItinSeg );
   }

 /** 
  ***********************************************************************
  * This function scans in a single manual car rental segment
  ***********************************************************************
  */
 private static PNRItinCarSegment scanItinManualCarSegment(final String aItinString) throws Exception
   {
   final PNRItinCarSegment CarItinSeg = new PNRItinCarSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"PAST") )
     return(null);

   CarItinSeg.RawData         = aItinString;

   final StringTokenizer fields = new StringTokenizer(aItinString," /");
   if ( fields.countTokens() < 7 )
     throw new Exception("Unable to scan manual car segments.  Must have at least 6 fields. Instring = <" + aItinString + ">");

   String sSegmentNum         = fields.nextToken();
   CarItinSeg.CompanyCode     = fields.nextToken();
   String sLabel              = fields.nextToken();
   String sPickupDate         = fields.nextToken();
   String sStatus             = fields.nextToken();
   CarItinSeg.SegmentStatus   = sStatus.substring(0,2);
   CarItinSeg.PickupCityCode  = fields.nextToken();
   String sDropoffDate        = fields.nextToken();

   CarItinSeg.PickUpDateTime  = GnrcParser.ScanCRSDateString(sPickupDate);
   CarItinSeg.DropoffDateTime = GnrcParser.ScanCRSDateString(sDropoffDate);

   try
     {
     CarItinSeg.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan manual car segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   try
     {
     CarItinSeg.NumCars = Integer.parseInt(sStatus.substring(2));
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan manual car segment.  Unable to scan car count. Instring = <" + aItinString + ">");
     }

   // read in the options
   String sOption = "";
   try
     {
     while ( fields.hasMoreTokens() )
       {
       sOption = fields.nextToken("/");
       setCarItinOption(CarItinSeg,sOption);
       }
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan manual car segment.  Unable to scan option " + sOption + ". Instring = <" + aItinString + ">");
     }

   return( CarItinSeg );
   }

 /** 
  ***********************************************************************
  * This function scans in a single manual car rental segment
  ***********************************************************************
  */
 private static PNRItinCarSegment scanItinTVLCarSegment(final String aItinString) throws Exception
   {
   final PNRItinCarSegment CarItinSeg = new PNRItinCarSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"PAST") )
     return(null);

   CarItinSeg.RawData         = aItinString;

   final StringTokenizer fields = new StringTokenizer(aItinString," /");
   if ( fields.countTokens() < 7 )
     throw new Exception("Unable to scan TVL car segments.  Must have at least 6 fields. Instring = <" + aItinString + ">");

   String sSegmentNum         = fields.nextToken();
   String sLabel              = fields.nextToken();
   CarItinSeg.CompanyCode     = fields.nextToken();
   String sStatus             = fields.nextToken();
   CarItinSeg.SegmentStatus   = sStatus.substring(0,2);
   String sLabel2             = fields.nextToken();
   String sPickupDate         = fields.nextToken();
   String sDropoffDate        = fields.nextToken();

   if ( sPickupDate.length() > 5 )
    sPickupDate = sPickupDate.substring(0,5);

   if ( sDropoffDate.length() > 5 )
    sDropoffDate = sDropoffDate.substring(0,5);

   CarItinSeg.PickUpDateTime  = GnrcParser.ScanCRSDateString(sPickupDate);
   CarItinSeg.DropoffDateTime = GnrcParser.ScanCRSDateString(sDropoffDate);

   try
     {
     CarItinSeg.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL car segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   try
     {
     CarItinSeg.NumCars = Integer.parseInt(sStatus.substring(2));
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL car segment.  Unable to scan car count. Instring = <" + aItinString + ">");
     }

   // read in the options
   String sOption = "";
   try
     {
     while ( fields.hasMoreTokens() )
       {
       sOption = fields.nextToken("/");
       setCarItinOption(CarItinSeg,sOption);
       }
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL car segment.  Unable to scan option " + sOption + ". Instring = <" + aItinString + ">");
     }

   return( CarItinSeg );
   }

 /** 
  ***********************************************************************
  * This function sets options for a car itinerary segment
  ***********************************************************************
  */
 private static void setCarItinOption(final PNRItinCarSegment aItinSegment, String aOptionString) throws Exception
   {
   // parse the option string
   final int iPos = aOptionString.indexOf('-');
   if ( iPos < 0 )
     {
     if ( aOptionString.startsWith("L") )    // L option is a special case, it doesn't have a dash
       aItinSegment.LocationCode = aOptionString.substring(1);
     return;
     }

   final String sOptionCode  = aOptionString.substring(0,iPos).toUpperCase();
   String sOptionValue = "";
   if ( aOptionString.length() > (iPos + 1) )
     sOptionValue = aOptionString.substring(iPos + 1).trim();

   // set option values
   if ( sOptionCode.equals("AA1") )               // address line 1
     {
     aItinSegment.Address = new String[1];
     aItinSegment.Address[0] = sOptionValue;
     }
   else if ( sOptionCode.equals("AA2") )          // address line 2
     {
     if ( aItinSegment.Address instanceof String[] )
       {
       if ( aItinSegment.Address.length > 0 )
         {
         String sAddress0 = aItinSegment.Address[0];

         aItinSegment.Address = new String[2];
         aItinSegment.Address[0] = sAddress0;
         aItinSegment.Address[1] = sOptionValue;
         }
       else
         {
         aItinSegment.Address = new String[1];
         aItinSegment.Address[0] = sOptionValue;
         }
       }
     else
       {
       aItinSegment.Address = new String[1];
       aItinSegment.Address[0] = sOptionValue;
       }
     }
   else if ( sOptionCode.equals("AN") )           // car company name
     aItinSegment.CompanyName = sOptionValue;
   else if ( sOptionCode.equals("AP") )           // phone
     {
     aItinSegment.Phone = new String[1];
     aItinSegment.Phone[0] = sOptionValue;
     }
   else if ( sOptionCode.equals("ARR") )          // arrival information
     {
     final StringTokenizer tk = new StringTokenizer(sOptionValue,"-");
     String sPickupTime;
     while ( tk.hasMoreTokens() )
       {
       sPickupTime = tk.nextToken();
       if ( Character.isDigit(sPickupTime.charAt(0)) )
         {
         final String sPickupDate    = GnrcFormat.FormatCRSDate(aItinSegment.PickUpDateTime);
         aItinSegment.PickUpDateTime = GnrcParser.ScanCRSDateTimeString(sPickupDate,sPickupTime);
         return;
         }
       }
     }
   else if ( sOptionCode.equals("CF") )          // confirmation
     aItinSegment.Confirmation = sOptionValue;
   else if ( sOptionCode.equals("CTY") )         // city name
     {
     if ( (aItinSegment.DropoffCityName instanceof String) == false )
       aItinSegment.DropoffCityName = sOptionValue;
     if ( (aItinSegment.PickupCityName instanceof String) == false )
       aItinSegment.PickupCityName = sOptionValue;
     }
   else if ( sOptionCode.equals("DC") )          // drop charge
     aItinSegment.DropoffCharge = sOptionValue;
   else if ( sOptionCode.equals("DO") )          // drop off location
     {
     if ( sOptionValue.length() <= 3 )
       aItinSegment.DropoffCityCode = sOptionValue;
     else
       aItinSegment.DropoffCityName = sOptionValue;
     }
   else if ( sOptionCode.equals("DT") )          // drop off information
     {
     final String sDropoffDate = GnrcFormat.FormatCRSDate(aItinSegment.DropoffDateTime);
     final String sDropoffTime = sOptionValue;
     aItinSegment.DropoffDateTime = GnrcParser.ScanCRSDateTimeString(sDropoffDate,sDropoffTime);
     }
   else if ( sOptionCode.equals("G") )           // guarantee
     aItinSegment.GuaranteeInfo = sOptionValue;
   else if ( sOptionCode.equals("PU") )          // pickup city name
     aItinSegment.PickupCityName = sOptionValue;
   else if ( sOptionCode.equals("PUP") )          // pickup city name
     aItinSegment.PickupCityName = sOptionValue;
   else if ( sOptionCode.equals("PUT") )          // pick up time
     {
     final String sPickUpDate = GnrcFormat.FormatCRSDate(aItinSegment.PickUpDateTime);
     final String sPickUpTime = sOptionValue;
     aItinSegment.PickUpDateTime = GnrcParser.ScanCRSDateTimeString(sPickUpDate,sPickUpTime);
     }
   else if ( sOptionCode.equals("R") )            // rate quote
     aItinSegment.Rate = sOptionValue;
   else if ( sOptionCode.equals("RQ") )           // rate quote
     aItinSegment.Rate = sOptionValue;
   else if ( sOptionCode.equals("RC") )           // rate code
     aItinSegment.RateCode = sOptionValue;
   else if ( sOptionCode.equals("RTD") )           // rate description
     aItinSegment.RateCode = sOptionValue;
   else if ( sOptionCode.equals("RG") )                // rate guaranteed
     {
     aItinSegment.RateGuaranteed = true;
     aItinSegment.Rate = sOptionValue;
     }
   else if ( sOptionCode.equals("RT") )          // drop off information
     {
     final String sDropoffDate = GnrcFormat.FormatCRSDate(aItinSegment.DropoffDateTime);
     final String sDropoffTime = sOptionValue;
     aItinSegment.DropoffDateTime = GnrcParser.ScanCRSDateTimeString(sDropoffDate,sDropoffTime);
     }
   else if ( sOptionCode.equals("VT") )           // car type code
     aItinSegment.CarTypeCode = sOptionValue;
   }

 /** 
  ***********************************************************************
  * This function scans in a single hotel segment
  ***********************************************************************
  */
 private static PNRItinHotelSegment scanItinHotelSegment(final String aItinString) throws Exception
   {
   final PNRItinHotelSegment HotelItinSeg = new PNRItinHotelSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"PAST") )
     return(null);

   HotelItinSeg.RawData = aItinString;

   // separate the main hotel data from the options
   final int iPos = aItinString.indexOf("/",60);
   String sHotelData;
   String sOptionData;
   if ( iPos > 60 )
     {
     sHotelData  = aItinString.substring(0,iPos);
     sOptionData = aItinString.substring(iPos);
     }
   else
     {
     sHotelData  = aItinString;
     sOptionData = "";
     }

   // parse up the main hotel data
   final StringTokenizer fields = new StringTokenizer(sHotelData," ");
   if ( fields.countTokens() < 8 )
     throw new Exception("Unable to scan hotel segment.  Must have at least 8 fields. Instring = <" + aItinString + ">");

   final String sSegmentNum   = fields.nextToken();
   final String sLabel        = fields.nextToken();
   HotelItinSeg.ChainCode     = fields.nextToken();
   final String sStatus       = fields.nextToken();
   HotelItinSeg.SegmentStatus = sStatus.substring(0,2);
   HotelItinSeg.CityCode      = fields.nextToken();
   final String sCheckInDate  = fields.nextToken();
   final String sCheckOutDate = fields.nextToken();
   HotelItinSeg.RoomType      = fields.nextToken();

   // get the rate, location code, and name
   String sField;
   boolean propertyCodeSet = false;
   while ( fields.hasMoreTokens() )
     {
     sField = fields.nextToken();

     if ( propertyCodeSet )
       {
       if ( (HotelItinSeg.Name instanceof String) == false )
         HotelItinSeg.Name = sField;
       else if ( HotelItinSeg.Name.length() == 0 )
         HotelItinSeg.Name = sField;
       else
         HotelItinSeg.Name = HotelItinSeg.Name + " " + sField;
       }
     else if ( RegExpMatch.matches(sField,"^[A-Z0-9][A-Z0-9][A-Z0-9]$") && (sField.equals("DLY") == false) )
       {          // property code is a three character code (except for DLY)
       HotelItinSeg.PropertyCode = HotelItinSeg.CityCode + sField;
       propertyCodeSet = true;
       }
     else
       {       // must be rate info
       if ( (HotelItinSeg.Rate instanceof String) == false )
         HotelItinSeg.Rate = sField;
       else if ( HotelItinSeg.Rate.length() == 0 )
         HotelItinSeg.Rate = sField;
       else
         HotelItinSeg.Rate = HotelItinSeg.Rate + " " + sField;
       }
     }

   HotelItinSeg.CheckInDate  = GnrcParser.ScanCRSDateString(sCheckInDate.substring(2));
   HotelItinSeg.CheckOutDate = GnrcParser.ScanCRSDateString(sCheckOutDate.substring(3));

   try
     {
     HotelItinSeg.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan hotel segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   try
     {
     HotelItinSeg.NumRooms = Integer.parseInt(sStatus.substring(2));
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan hotel segment.  Unable to scan room count. Instring = <" + aItinString + ">");
     }

   // read in the options
   String sOption = "";
   final StringTokenizer options = new StringTokenizer(sOptionData,"/");
   try
     {
     while ( options.hasMoreTokens() )
       {
       sOption = options.nextToken();
       setHotelItinOption(HotelItinSeg,sOption);
       }
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan hotel segment.  Unable to scan option " + sOption + ". Instring = <" + aItinString + ">");
     }

   return( HotelItinSeg );
   }

 /** 
  ***********************************************************************
  * This function scans in a single manual hotel segment
  ***********************************************************************
  */
 private static PNRItinHotelSegment scanItinManualHotelSegment(final String aItinString) throws Exception
   {
   final PNRItinHotelSegment HotelItinSeg = new PNRItinHotelSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"PAST") )
     return(null);

   HotelItinSeg.RawData         = aItinString;

   final StringTokenizer fields = new StringTokenizer(aItinString," /");
   if ( fields.countTokens() < 7 )
     throw new Exception("Unable to scan manual hotel segment.  Must have at least 7 fields. Instring = <" + aItinString + ">");

   String sSegmentNum         = fields.nextToken();
   HotelItinSeg.ChainCode     = fields.nextToken();
   String sLabel              = fields.nextToken();
   String sCheckInDate        = fields.nextToken();
   String sStatus             = fields.nextToken();
   HotelItinSeg.SegmentStatus = sStatus.substring(0,2);
   HotelItinSeg.CityCode      = fields.nextToken();
   String sCheckOutDate       = fields.nextToken();

   if ( sCheckInDate.length() > 5 )
     sCheckInDate = sCheckInDate.substring(0,5);
   HotelItinSeg.CheckInDate  = GnrcParser.ScanCRSDateString(sCheckInDate);

   int iPos = sCheckOutDate.indexOf("OUT");
   if ( iPos == 0 )
     sCheckOutDate = sCheckOutDate.substring(3);
   else if ( iPos >= 5)
     sCheckOutDate = sCheckOutDate.substring(0,5);

   if ( Character.isDigit(sCheckOutDate.charAt(sCheckOutDate.length() - 1)) )
     sCheckOutDate = sCheckOutDate.substring(0,sCheckOutDate.length() - 1);

   if ( sCheckOutDate.length() > 5 )
     sCheckOutDate = sCheckOutDate.substring(0,5);

   HotelItinSeg.CheckOutDate = GnrcParser.ScanCRSDateString(sCheckOutDate);

   try
     {
     HotelItinSeg.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan manual hotel segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   try
     {
     HotelItinSeg.NumRooms = Integer.parseInt(sStatus.substring(2));
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan manual hotel segment.  Unable to scan room count. Instring = <" + aItinString + ">");
     }

   // read in the options
   String sOption = "";
   try
     {
     while ( fields.hasMoreTokens() )
       {
       sOption = fields.nextToken("/");
       setHotelItinOption(HotelItinSeg,sOption);
       }
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan manual hotel segment.  Unable to scan option " + sOption + ". Instring = <" + aItinString + ">");
     }

   return( HotelItinSeg );
   }

 /** 
  ***********************************************************************
  * This function scans in a single TVL hotel segment
  ***********************************************************************
  */
 private static PNRItinHotelSegment scanItinTVLHotelSegment(final String aItinString) throws Exception
   {
   final PNRItinHotelSegment HotelItinSeg = new PNRItinHotelSegment();

   // see if the air segment has already flown
   if ( RegExpMatch.matches(aItinString,"PAST") )
     return(null);

   HotelItinSeg.RawData         = aItinString;

   final StringTokenizer fields = new StringTokenizer(aItinString," /");
   if ( fields.countTokens() < 7 )
     throw new Exception("Unable to scan TVL hotel segment.  Must have at least 7 fields. Instring = <" + aItinString + ">");

   String sSegmentNum         = fields.nextToken();
   String sLabel1             = fields.nextToken();
   HotelItinSeg.ChainCode     = fields.nextToken();
   String sStatus             = fields.nextToken();
   HotelItinSeg.SegmentStatus = sStatus.substring(0,2);
   String sLabel2             = fields.nextToken();
   String sCheckInDate        = fields.nextToken();
   String sCheckOutDate       = fields.nextToken();

   if ( sCheckInDate.length() > 5 )
     sCheckInDate = sCheckInDate.substring(0,5);
   HotelItinSeg.CheckInDate  = GnrcParser.ScanCRSDateString(sCheckInDate);

   if ( sCheckOutDate.length() > 5 )
     sCheckOutDate = sCheckOutDate.substring(0,5);
   HotelItinSeg.CheckOutDate = GnrcParser.ScanCRSDateString(sCheckOutDate);

   try
     {
     HotelItinSeg.SegmentNumber = Integer.parseInt(sSegmentNum);
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL hotel segment.  Unable to scan segment number. Instring = <" + aItinString + ">");
     }

   try
     {
     HotelItinSeg.NumRooms = Integer.parseInt(sStatus.substring(2));
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL hotel segment.  Unable to scan room count. Instring = <" + aItinString + ">");
     }

   // read in the options
   String sOption = "";
   try
     {
     while ( fields.hasMoreTokens() )
       {
       sOption = fields.nextToken("/");
       setHotelItinOption(HotelItinSeg,sOption);
       }
     }
   catch (Exception e)
     {
     throw new Exception("Unable to scan TVL hotel segment.  Unable to scan option " + sOption + ". Instring = <" + aItinString + ">");
     }

   return( HotelItinSeg );
   }

 /** 
  ***********************************************************************
  * This function sets options for a hotel itinerary segment
  ***********************************************************************
  */
 private static void setHotelItinOption(final PNRItinHotelSegment aItinSegment, String aOptionString) throws Exception
   {
   // parse the option string
   final int iPos = aOptionString.indexOf('-');
   if ( iPos < 0 )
     return;

   final String sOptionCode  = aOptionString.substring(0,iPos).toUpperCase();
   String sOptionValue = "";
   if ( aOptionString.length() > (iPos + 1) )
     sOptionValue = aOptionString.substring(iPos + 1).trim();



   if ( sOptionCode.equals("AA1") )               // address line 1
     {
     aItinSegment.Address = new String[1];
     aItinSegment.Address[0] = sOptionValue;
     }
   else if ( sOptionCode.equals("AA2") )          // address line 2
     {
     if ( aItinSegment.Address instanceof String[] )
       {
       if ( aItinSegment.Address.length > 0 )
         {
         String sAddress0 = aItinSegment.Address[0];

         aItinSegment.Address = new String[2];
         aItinSegment.Address[0] = sAddress0;
         aItinSegment.Address[1] = sOptionValue;
         }
       else
         {
         aItinSegment.Address = new String[1];
         aItinSegment.Address[0] = sOptionValue;
         }
       }
     else
       {
       aItinSegment.Address = new String[1];
       aItinSegment.Address[0] = sOptionValue;
       }
     }
   else if ( sOptionCode.equals("AN") )           // hotel company name
     {
     if ( (aItinSegment.Name instanceof String) == false )
       aItinSegment.Name = sOptionValue;
     }
   else if ( sOptionCode.equals("AP") )           // phone
     aItinSegment.Phone = sOptionValue;
   else if ( sOptionCode.equals("BED") )           // bed type
     aItinSegment.RoomType = sOptionValue;
   else if ( sOptionCode.equals("CF") )           // confirmation
     aItinSegment.ConfirmationNumber = sOptionValue;
   else if ( sOptionCode.equals("CTY") )          // city name
     aItinSegment.CityName = sOptionValue;
   else if ( sOptionCode.equals("CXP") )         // cancellation policy
     aItinSegment.CancelPolicy = sOptionValue;
   else if ( sOptionCode.equals("G") )           // guarantee
     aItinSegment.Guarantee = sOptionValue;
   else if ( sOptionCode.equals("NM") )          // name on reservation
     aItinSegment.ResName = sOptionValue;
   else if ( sOptionCode.equals("PRP") )         // property name
     aItinSegment.Name = sOptionValue;
   else if ( sOptionCode.equals("R") )           // rate code/room type
     {
   //  aItinSegment.RateCode = sOptionValue;
     aItinSegment.RoomType = sOptionValue;
     }
   else if ( sOptionCode.equals("RD") )          // room description
     {
   //  aItinSegment.RoomType = sOptionValue;
     String sDescription = sOptionValue;
     }
   else if ( sOptionCode.equals("RG") )          // rate guaranteed
     {
     aItinSegment.RateGuaranteed = true;
     aItinSegment.Rate = sOptionValue;
     }
   else if ( sOptionCode.equals("RQ") )          // rate quote
     {
     aItinSegment.RateGuaranteed = false;
     aItinSegment.Rate = sOptionValue;
     }
   else if ( sOptionCode.equals("RTD") )          // rate description
     aItinSegment.RateCode = sOptionValue;
   }

 /** 
  ***********************************************************************
  * This function sets options for an air itinerary segment
  ***********************************************************************
  */
 private static void setAirItinOption(final PNRItinAirSegment aItinSegment, String aOptionString) throws Exception
   {
   // parse the option string
   final int iPos = aOptionString.indexOf('-');
   if ( iPos < 0 )
     return;

   final String sOptionCode  = aOptionString.substring(0,iPos).toUpperCase();
   String sOptionValue = "";
   if ( aOptionString.length() > (iPos + 1) )
     sOptionValue = aOptionString.substring(iPos + 1).trim();



   if ( sOptionCode.equals("ARR") )                // date of arrival
     {
     if ( sOptionValue.length() > 5 )
       sOptionValue = sOptionValue.substring(0,5);

     final SimpleDateFormat CrsTime = new SimpleDateFormat("hmma");
     final String sArrTime = CrsTime.format( new Date(aItinSegment.ArrivalDateTime) );

     aItinSegment.ArrivalDateTime = GnrcParser.ScanCRSDateTimeString(sOptionValue,sArrTime);
     }
   else if ( sOptionCode.equals("CC1") )           // departure city
     aItinSegment.DepartureCityCode = sOptionValue;
   else if ( sOptionCode.equals("CC2") )           // arrival city
     aItinSegment.ArrivalCityCode = sOptionValue;
   else if ( sOptionCode.equals("CL") )            // class of service
     aItinSegment.InventoryClass = sOptionValue;
   else if ( sOptionCode.equals("FLT") )           // flight number
     {
     // get rid of leading zeroes
     while ( sOptionValue.startsWith("0") )
       sOptionValue = sOptionValue.substring(1);

     aItinSegment.FlightNumber = Integer.parseInt(sOptionValue);
     }
   else if ( sOptionCode.equals("TD") )            // time of departure
     {
     final String sDepDate = GnrcFormat.FormatCRSDate(aItinSegment.DepartureDateTime);
     aItinSegment.DepartureDateTime = GnrcParser.ScanCRSDateTimeString(sDepDate,sOptionValue);
     }
   else if ( sOptionCode.equals("TA") )            // time of arrival
     {
     final String sArrDate = GnrcFormat.FormatCRSDate(aItinSegment.ArrivalDateTime);
     aItinSegment.ArrivalDateTime = GnrcParser.ScanCRSDateTimeString(sArrDate,sOptionValue);
     }
   }
}
