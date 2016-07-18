package xmax.dialogs;

import javax.swing.text.PlainDocument;
import javax.swing.text.*;
import java.awt.Toolkit;
//import xmax.util.RegExpMatch;

public class PatternDocument extends PlainDocument
{
 private String Pattern;
 private int TextCase;
 public static final int MIXED_CASE = 0;
 public static final int UPPER_CASE = 1;
 public static final int LOWER_CASE = 2;

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PatternDocument(final int aMaxChars)
    {
    this(aMaxChars,MIXED_CASE);
    }


  public PatternDocument(final String aPattern)
    {
    this(aPattern,MIXED_CASE);
    }


  public PatternDocument(final int aMaxChars, final int aCase)
    {
    final StringBuffer sPattern = new StringBuffer("^");

    for ( int i = 0; i < aMaxChars; i++ )
      sPattern.append(".?");
    sPattern.append("$");

    Pattern  = sPattern.toString();
    TextCase = aCase;
    }


  public PatternDocument(final String aPattern, final int aCase)
    {
    Pattern  = aPattern;
    TextCase = aCase;
    }

  /** 
   ***********************************************************************
   * override the insertString procedure
   ***********************************************************************
   */
  public void insertString(final int offset, final String str, final AttributeSet a) throws BadLocationException
    {
    // set the case for the new string
    final String sAppend;
    if ( TextCase == UPPER_CASE )
      sAppend = str.toUpperCase();
    else if ( TextCase == UPPER_CASE )
      sAppend = str.toLowerCase();
    else
      sAppend = str;

    final String sTest;
    if ( offset > 0 )
      sTest = super.getText(0,offset) + sAppend;
    else
      sTest = sAppend;

    // make sure the input string matches the pattern
    if ( Pattern instanceof String )
      {
      try
        {
    	  if (sTest.matches(this.Pattern) == false)
     //   if ( RegExpMatch.matches(sTest,Pattern) == false )
          {
          Toolkit.getDefaultToolkit().beep();
          return;
          }
        }
      catch (Exception e)
        {
        System.out.println(e.toString());
        }
      }

    super.insertString(offset,sAppend,a);
    }

}