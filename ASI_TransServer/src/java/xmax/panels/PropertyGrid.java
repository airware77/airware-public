
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import javax.swing.*;
import java.util.Date;
import java.text.SimpleDateFormat;


public class PropertyGrid extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel lblTitle = new JLabel();
  JPanel pnlProperties = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel lblPropName1 = new JLabel();
  JLabel lblPropValue1 = new JLabel();
  JLabel lblPropName2 = new JLabel();
  JLabel lblPropValue2 = new JLabel();
  final public static String DATE_NUM_FORMAT      = "M/d/yyyy";
  final public static String DATE_STR_FORMAT      = "MMM d";
  final public static String TIME_FORMAT          = "h:mm a";
  final public static String DATE_TIME_NUM_FORMAT = "M/d/yyyy  h:mm a";
  final public static String DATE_TIME_STR_FORMAT = "MMM d,  h:mm a";

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PropertyGrid()
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

  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PropertyGrid(final String aCaption)
    {
   // this.setBorder(BorderFactory.createLineBorder(Color.black));
    setCaption(aCaption);

    this.setLayout(borderLayout1);
    pnlProperties.setLayout(gridLayout1);

    gridLayout1.setHgap(20);
    gridLayout1.setColumns(2);
    gridLayout1.setRows(0);

    this.add(lblTitle,      BorderLayout.NORTH);
    this.add(pnlProperties, BorderLayout.CENTER);
    clearProperties();
    }

  /** 
   ***********************************************************************
   * Used by constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    lblTitle.setFont(new java.awt.Font("Dialog", 1, 12));
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
    lblTitle.setText("Flight Properties");
    this.setLayout(borderLayout1);
    pnlProperties.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    gridLayout1.setHgap(20);
    gridLayout1.setRows(0);
    lblPropName1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblPropName1.setText("Departure City:");
    lblPropValue1.setText("ATL");
    lblPropName2.setHorizontalAlignment(SwingConstants.RIGHT);
    lblPropName2.setText("Arrival City:");
    lblPropValue2.setText("MCO");
    this.add(lblTitle, BorderLayout.NORTH);
    this.add(pnlProperties, BorderLayout.CENTER);
    pnlProperties.add(lblPropName1, null);
    pnlProperties.add(lblPropValue1, null);
    pnlProperties.add(lblPropName2, null);
    pnlProperties.add(lblPropValue2, null);
    }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void addProperty(final String aName, final String aValue)
   {
   // create label components for the name and value
   final JLabel lblName  = new JLabel(aName,SwingConstants.RIGHT);
   final JLabel lblValue = new JLabel(aValue,SwingConstants.LEFT);

   // use bold font for Name field
   lblName.setFont(  new Font("Dialog", Font.BOLD,  12) );
   lblValue.setFont( new Font("Dialog", Font.PLAIN, 12) );

   // add the labels
   pnlProperties.add(lblName);
   pnlProperties.add(lblValue);
   }

 public void addBlankRow()
   {
   addProperty(null,null);
   }

 public void addBlankRow(final int aNumRows)
   {
   for ( int i = 0; i < aNumRows; i++ )
     addBlankRow();
   }

 public void addProperty(final String aName, final int aValue)
   {
   addProperty(aName,Integer.toString(aValue));
   }

 public void addProperty(final String aName, final boolean aValue)
   {
   addProperty(aName,aValue,"Yes","No");
   }

 public void addProperty(final String aName, final boolean aValue, final String aTrueStr, final String aFalseStr)
   {
   if ( aValue )
     addProperty(aName,aTrueStr);
   else
     addProperty(aName,aFalseStr);
   }


 public void addProperty(final String aName, final long aDateTimeValue, final String aDateFormat)
   {
   final SimpleDateFormat dt_tm_format = new SimpleDateFormat(aDateFormat);
   final String sDate = dt_tm_format.format( new Date(aDateTimeValue) ).toString();

   addProperty(aName,sDate);
   }

 public void addProperty(final String aName, final long aDateTimeValue)
   {
   addProperty(aName,aDateTimeValue,DATE_TIME_NUM_FORMAT);
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void clearProperties()
   {
   pnlProperties.removeAll();
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void setCaption(final String aCaption)
   {
   lblTitle.setFont( new Font("Dialog", Font.BOLD, 12) );
   lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
   lblTitle.setText(aCaption);
   }

 /** 
  ***********************************************************************
  *
  ***********************************************************************
  */
 public void clearCaption()
   {
   setCaption(" ");
   }

}
