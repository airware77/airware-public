
//Title:        Your Product Name
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       John Crowley
//Company:      XMAX Corp
//Description:  Your description
package xmax.crs;

import java.util.Vector;

/** 
 ***********************************************************************
 * FamilyElement - class
 ***********************************************************************
 */
public class FamilyElement
{
  public String LastName;
  public String RawData;
  public Vector name_section = new Vector();


  public void dummy()
  {
    LastName = "CROWLEY";
    RawData  = "CROWLEY/JOHN MR/ANNE MRS";
    name_section.addElement(new NameElement());
  }
  /** 
   ***********************************************************************
   * FamilyElement - constructor
   ***********************************************************************
   */
  public FamilyElement()
  {
    dummy();
  } // FamilyElement

  public void assignTo(FamilyElement fe)
  {
    LastName        = fe.LastName;
    RawData         = fe.RawData;
    for (int i = 0; i < fe.name_section.size(); i++)
    {
      NameElement ne = new NameElement();
      ne.assignTo((NameElement)(fe.name_section.elementAt(i)));
      name_section.addElement(ne);
    }

  } // assignTo()

}
