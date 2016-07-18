package xmax.crs.profiles;

import xmax.util.RegExpMatch;
import xmax.util.MatchInfo;
import xmax.TranServer.GnrcFormat;
import xmax.crs.GetPNR.*;
import java.io.Serializable;

public class ProfileElement implements Serializable
{
 private Object Element;
 private int LineNum;
 private int Usage;             // always move, never move, optional move
 private String Qualifier;
 private int InsertAfterLine;   // insert this profile element in the PNR after this line
 public static final int OPTIONAL_MOVE = 0;
 public static final int ALWAYS_MOVE   = 1;
 public static final int NEVER_MOVE    = 2;

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public ProfileElement(final Object aElement)
    {
    this(aElement,OPTIONAL_MOVE,0,null);
    }

  public ProfileElement(final Object aElement, final int aUsage)
    {
    this(aElement,aUsage,0,null);
    }

  public ProfileElement(final Object aElement, final int aUsage, final int aLineNum)
    {
    this(aElement,aUsage,aLineNum,null);
    }

  public ProfileElement(final Object aElement, final int aUsage, final int aLineNum, final String aQualifier)
    {
    Element   = aElement;
    Usage     = aUsage;
    LineNum   = aLineNum;
    Qualifier = aQualifier;
    }

  /** 
   ***********************************************************************
   * get functions
   ***********************************************************************
   */
  public int getLineNum()
    {
    return(LineNum);
    }

  public int getUsage()
    {
    return(Usage);
    }

  public String getQualifier()
    {
    return(Qualifier);
    }

  public int getInsertAfterLine()
    {
    return(InsertAfterLine);
    }

  public Object getElement()
    {
    return(Element);
    }

  /** 
   ***********************************************************************
   * set functions
   ***********************************************************************
   */
  public void setInsertAfterLine(final int aInsertAfterLine)
    {
    InsertAfterLine = aInsertAfterLine;
    }

  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  public boolean matches(final String aPattern)
    {
    // determine the appropriate string to compare to
    final String sSource = getText();

    // make sure you have a pattern and something to compare to
    if ( GnrcFormat.IsNull(aPattern) || GnrcFormat.IsNull(sSource) )
      return(false);

    try
      {
      if ( RegExpMatch.matches(sSource,aPattern) )
        return(true);
      }
    catch (Exception e)
      {
      System.out.println(e.toString());
      }

    return(false);
    }

  /**
   ***********************************************************************
   * Returns true if the given profile element is the same is this one
   ***********************************************************************
   */
  public boolean isEqual(final ProfileElement aElement)
    {
    // compare line numbers, usage, qualifier, and text
    if ( this.LineNum != aElement.LineNum )
      return(false);

    if ( this.Usage != aElement.Usage )
      return(false);

    if ( GnrcFormat.strEqual(this.Qualifier,aElement.Qualifier) == false )
      return(false);

    if ( GnrcFormat.strEqual(this.getText(),aElement.getText()) == false )
      return(false);

    return(true);
    }

  /**
   ***********************************************************************
   *
   ***********************************************************************
   */
  public String getText()
    {
    // determine the appropriate string to compare to
    final String sSource;
    if ( Element instanceof String )
      sSource = (String )Element;
    else if ( Element instanceof PNRRemark )
      sSource = ((PNRRemark )Element).RemarkText;
    else if ( Element instanceof PNRNameElement )
      sSource = ((PNRNameElement )Element).LastName;
    else
      sSource = null;

    return(sSource);
    }

}