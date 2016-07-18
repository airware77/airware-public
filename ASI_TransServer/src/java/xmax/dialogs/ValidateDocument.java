package xmax.dialogs;

import javax.swing.text.PlainDocument;
import javax.swing.text.*;
import java.awt.Toolkit;
import xmax.util.RegExpMatch;

public class ValidateDocument extends PlainDocument
{
 private String Pattern;

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public ValidateDocument(final int aMaxChars)
    {     // match 0 to maxchars occurences of any character
    Pattern = "^.\\{0," + aMaxChars + "\\}$";
    }


  public ValidateDocument(final String aPattern)
    {
    Pattern = aPattern;
    }

  /** 
   ***********************************************************************
   * override the insertString procedure
   ***********************************************************************
   */
  public void insertString(final int offset, final String str, final AttributeSet a) throws BadLocationException
    {
    // make sure the input string matches the pattern
    if ( Pattern instanceof String )
      {
      try
        {
        final String sTest = super.getText(0, super.getLength() ) + str;
        if ( RegExpMatch.matches(sTest,Pattern) == false )
          {
          Toolkit.getDefaultToolkit().beep();
          return;
          }
        }
      catch (Exception e)
        {
        }
      }

    super.insertString(offset,str,a);
    }

}