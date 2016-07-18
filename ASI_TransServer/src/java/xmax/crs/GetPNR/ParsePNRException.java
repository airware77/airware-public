package xmax.crs.GetPNR;

public class ParsePNRException extends Exception
{
  /** 
   ***********************************************************************
   *
   ***********************************************************************
   */
  public ParsePNRException(final String aErrorMessage)
    {
    super(aErrorMessage);
    }

  public ParsePNRException(final String aErrorMessage, final String aSourceString)
    {
    super(aErrorMessage + " - Input Text: '" + aSourceString + "'");
    }

}