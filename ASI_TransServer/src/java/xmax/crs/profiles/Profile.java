package xmax.crs.profiles;

import java.util.Vector;
import java.io.Serializable;
import xmax.TranServer.GnrcFormat;

public class Profile implements Serializable
{
 private String CrsCode;
 private String PseudoCity;
 private String GroupName;
 private String TravelerName;
 private String Caption;
 private String Description;
 private Vector Elements;
 private long   dateModified;      // date of last profile modification
 private long   dateAccessed;      // date of last profile usage
 private String Agent;             // agent who last modified the profile
 private boolean Active;
 private String RawData;           // rawdata saved from the GDS

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public Profile(final String aCrsCode, final String aPsuedoCity,
                 final String aGroupName)
    {
    this(aCrsCode,aPsuedoCity,aGroupName,null);
    }


  public Profile(final String aCrsCode, final String aPsuedoCity,
                 final String aGroupName, final String aTravelerName)
    {
    CrsCode      = aCrsCode;
    PseudoCity   = aPsuedoCity;
    GroupName    = aGroupName;
    TravelerName = aTravelerName;
    Active       = true;
    }

  /** 
   ***********************************************************************
   * set functions
   ***********************************************************************
   */
  public void setDescription(final String aDescription)
    {
    Description = aDescription;
    }

  public void setCaption(final String aCaption)
    {
    Caption = aCaption;
    }

  public void setAgentSign(final String aAgentSign)
    {
    Agent = aAgentSign;
    }

  public void setLastModDate(final long aLastModDate)
    {
    dateModified = aLastModDate;
    }

  public void setLastAccessDate(final long aLastAccessDate)
    {
    dateAccessed = aLastAccessDate;
    }

  public void setActive(final boolean aIsActive)
    {
    Active = aIsActive;
    }

  public void setRawData(final String aRawData)
    {
    RawData = aRawData;
    }

  /** 
   ***********************************************************************
   * get functions
   ***********************************************************************
   */
  public String getCrsCode()
    {
    return(CrsCode);
    }

  public String getPsuedoCity()
    {
    return(PseudoCity);
    }

  public String getGroupName()
    {
    return(GroupName);
    }

  public String getTravelerName()
    {
    return(TravelerName);
    }

  public String getDescription()
    {
    return(Description);
    }

  public String getCaption()
    {
    return(Caption);
    }

  public String getAgentSign()
    {
    return(Agent);
    }

  public long getLastModDate()
    {
    return(dateModified);
    }

  public long getLastAccessDate()
    {
    return(dateAccessed);
    }

  public boolean isActive()
    {
    return(Active);
    }

  public boolean isPurged()
    {
    return(!Active);
    }

  public String getRawData()
    {
    return(RawData);
    }

  /** 
   ***********************************************************************
   * element functions
   ***********************************************************************
   */
  public void addElement(final Object aElement)
    {
    final ProfileElement element = new ProfileElement(aElement);
    addElement(element);
    }

  public void addElement(final Object aElement, final int aUsage)
    {
    final ProfileElement element = new ProfileElement(aElement,aUsage);
    addElement(element);
    }

  public void addElement(final Object aElement, final int aUsage, final int aLineNum)
    {
    final ProfileElement element = new ProfileElement(aElement,aUsage,aLineNum);
    addElement(element);
    }

  public void addElement(final Object aElement, final int aUsage, final int aLineNum, final String aQualifier)
    {
    final ProfileElement element = new ProfileElement(aElement,aUsage,aLineNum,aQualifier);
    addElement(element);
    }


  public void addElement(final ProfileElement aElement)
    {
    if ( (Elements instanceof Vector) == false )
      Elements = new Vector();

    if ( isListed(aElement) == false )
      Elements.add(aElement);
    }


  public void clearElements()
    {
    if ( Elements instanceof Vector )
      Elements.clear();
    }


  public ProfileElement[] getElements()
    {
    if ( Elements instanceof Vector )
      {
      if ( Elements.size() > 0 )
        {
        final ProfileElement[] elementArray = new ProfileElement[ Elements.size() ];
        Elements.toArray(elementArray);
        return(elementArray);
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * returns the first element that matches the given pattern
   ***********************************************************************
   */
  public ProfileElement getMatchingElement(final String aPattern)
    {
    final ProfileElement[] elements = getElements();

    if ( elements instanceof ProfileElement[] )
      {
      for ( int i = 0; i < elements.length; i++ )
        {
        if ( elements[i].matches(aPattern) )
          return(elements[i]);
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * returns the first element that matches the given line number
   ***********************************************************************
   */
  public ProfileElement getMatchingElement(final int aLineNumber)
    {
    final ProfileElement[] elements = getElements();

    if ( elements instanceof ProfileElement[] )
      {
      for ( int i = 0; i < elements.length; i++ )
        {
        if ( elements[i].getLineNum() == aLineNumber )
          return(elements[i]);
        }
      }

    return(null);
    }

  /**
   ***********************************************************************
   * returns true if the given line number is defined in the profile
   ***********************************************************************
   */
  public boolean isListed(final ProfileElement aElement)
    {
    final ProfileElement[] elements = getElements();
    if ( elements instanceof ProfileElement[] )
      {
      for ( int i = 0; i < elements.length; i++ )
        {
        if ( aElement.isEqual(elements[i]) )
          return(true);
        }
      }

    return(false);
    }

  /**
   ***********************************************************************
   * returns true if this profile is for a group
   ***********************************************************************
   */
  public boolean isGroupProfile()
    {
    if ( GnrcFormat.NotNull(GroupName) && GnrcFormat.IsNull(TravelerName) )
      return(true);
    else
      return(false);
    }

  /** 
   ***********************************************************************
   * returns true if this profile is for a traveler
   ***********************************************************************
   */
  public boolean isTravelerProfile()
    {
    if ( GnrcFormat.NotNull(TravelerName) )
      return(true);
    else
      return(false);
    }
}
