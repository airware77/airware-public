
//Title:        CRS Test Project
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David Fairchild
//Company:      XMAX Corp
//Description:  This class defines the routines used to parse out PNR data

package xmax.crs.GetPNR;

import xmax.crs.PNR;
import xmax.crs.GnrcParser;
import java.util.StringTokenizer;

public abstract class GnrcParsePNR
{
 public abstract String GetLocator(final PNR aPNR);
 public abstract String GetAgentSign(final PNR aPNR);
 public abstract String GetPseudoCity(final PNR aPNR) throws Exception;
 public abstract String [] GetHeader(final PNR aPNR) throws Exception;
 public abstract PNRFamilyElement [] GetFamilies(final PNR aPNR) throws Exception;
 public abstract PNRNameElement [] GetNames(final PNR aPNR) throws Exception;
 public abstract PNRItinSegment [] GetItin(final PNR aPNR) throws Exception;
 public abstract String [] GetPhones(final PNR aPNR) throws Exception;
 public abstract String [] GetAddress(final PNR aPNR) throws Exception;
 public abstract String [] GetTicketing(final PNR aPNR) throws Exception;
 public abstract String [] GetFOP(final PNR aPNR) throws Exception;
 public abstract PNRRemark [] GetRemarks(final PNR aPNR) throws Exception;
 //public abstract PNRGFactsRemark [] GetGeneralFacts(final PNR aPNR) throws Exception;

 /** 
  ***********************************************************************
  * Returns the entire PNR as one string
  ***********************************************************************
  */
 /*
 private String GetEntirePNR(final PNR aPNR) throws Exception
   {
   // make sure PNR object is valid
   if ( aPNR instanceof PNR )
     {
     // make sure you have raw data in AllSection
     if ( aPNR.AllSections instanceof String )
       return( aPNR.AllSections );
     else if ( aPNR.AllSectionResponse instanceof String[] )
       {
       aPNR.AllSections = GnrcParser.getCombinedHostResponse(aPNR.AllSectionResponse);
       return( aPNR.AllSections );
       }
     }

   return(null);
   }
 */
 /** 
  ***********************************************************************
  * This function converts an array of strings into a single string.
  * The beginning of each string might be a repeat of the trailing
  * characters from the previous string
  ***********************************************************************
  */
  public static String m_ArrayToString(final String [] aInStrings)
    {
    final StringBuffer sOutString = new StringBuffer("");
    String sAppendStr;

    // append each string from the array to the output string
    for ( int i = 0; i < aInStrings.length; i++ )
      {
      // remove trailing continuation characters
      if ( aInStrings[i].endsWith(")>") )
        sAppendStr = aInStrings[i].substring(0,aInStrings[i].length() - 2);
      else if ( aInStrings[i].endsWith(">") )
        sAppendStr = aInStrings[i].substring(0,aInStrings[i].length() - 1);
      else
        sAppendStr = aInStrings[i];


      sAppendStr = m_RemoveOverlap( sOutString.toString() ,sAppendStr);
      sOutString.append( sAppendStr );
      }

    final String sResult = m_AddLineWrap( sOutString.toString() );
    return( sResult );
    }
 /** 
  ***********************************************************************
  * This function returns a string that represents the non-overlapping
  * characters from Dest and Source.
  ***********************************************************************
  */
  private static String m_RemoveOverlap(final String aFirst, final String aSecond)
    {
    String sSecondBegin;        // leading portion of second string

    // remove trailing CR/LF
    String sFirst = aFirst;
    if ( aFirst.endsWith("\n\r") || aFirst.endsWith("\r\n") )
      sFirst = aFirst.substring(0,aFirst.length() - 2);
    else if ( aFirst.endsWith("\r") || aFirst.endsWith("\n") )
      sFirst = aFirst.substring(0,aFirst.length() - 1);


    for ( int i = aSecond.length(); i > 2; i-- )
      {
      sSecondBegin = aSecond.substring(0,i);
      if ( sFirst.endsWith(sSecondBegin) )
        {
        if ( sSecondBegin.trim().length() > 0 )
          return( aSecond.substring(i) );
        }
      }

    return(aSecond);
    }

 /** 
  ***********************************************************************
  * This procedure adds CR/LF pairs to any line that exceeds a
  * certain length
  ***********************************************************************
  */
 public static String m_AddLineWrap(final String aInString)
   {
   return( m_AddLineWrap(aInString,64) );
   }

 public static String m_AddLineWrap(final String aInString, final int aMaxWidth)
   {
   String sLine;
   String sSegment;
   StringBuffer sOutString = new StringBuffer("");
   StringTokenizer lines   = new StringTokenizer(aInString,"\r\n");


   // loop through all the lines a
   while ( lines.hasMoreTokens() )
     {
     sLine = lines.nextToken();
     int iLineLength = sLine.length();

     for ( int iStartPos = 0, iEndPos = aMaxWidth; iStartPos < iLineLength; iStartPos += aMaxWidth, iEndPos += aMaxWidth )
       {
       if ( iEndPos < iLineLength )
         sSegment = sLine.substring(iStartPos,iEndPos);
       else
         sSegment = sLine.substring(iStartPos);

       sOutString.append(sSegment + "\r\n");
       }
     }

   return( sOutString.toString() );
   }

}
