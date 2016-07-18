
//Title:        TranServer
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import javax.swing.JPanel;

public class PanelRemark2 extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();

  public PanelRemark2()
  {
    try 
    {
      jbInit();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    this.setLayout(borderLayout1);
  }
} 