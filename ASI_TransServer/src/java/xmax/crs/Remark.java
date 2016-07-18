package xmax.crs;


/** 
 ***********************************************************************
 * contains a PNR remark
 ***********************************************************************
 */
public class Remark
{
  String Type;
  String Format;
  String Remark;

  private void dummy()
  {
    Type = "OSI";
    Format = "";
    Remark = "ENJOY YOUR VACATION";
  }


  public Remark()
  {
    dummy();
  }

  public void assignTo(Remark rmk)
  {
    Type = rmk.Type;
    Format = rmk.Format;
    Remark = rmk.Remark;
  }

} // Remark class