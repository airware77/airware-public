package xmax.crs;

/**
 ***********************************************************************
 * NameElement - class
 ***********************************************************************
 */
public class NameElement
{
  public String FirstName;
  public String MiddleName;
  public String Title;
  public String AdditionalRemark;
  public String PTC;
  public String InfantName;
	public String InfantDOB;

  /**
   ***********************************************************************
   * NameElement - constructor
   ***********************************************************************
   */
  public NameElement()
  {
    FirstName = "JOHN";
    PTC = "ADT";
  } // NameElement


  public void assignTo(NameElement ne)
  {
    FirstName        = ne.FirstName;
    Title            = ne.Title;
    AdditionalRemark = ne.AdditionalRemark;
    PTC              = ne.PTC;
    InfantName       = ne.InfantName;
		InfantDOB        = ne.InfantDOB;

  } // assignTo()

}