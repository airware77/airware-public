
//Title:        TranServer
//Version:
//Copyright:    Copyright (c) 1999
//Author:       David
//Company:      XMAX Corp
//Description:  CRS Transaction Server Application

package xmax.panels;

import java.awt.*;
import xmax.crs.PNR;
import xmax.crs.PNRFare;
import xmax.crs.GetPNR.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class PanelPNR extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane splitBase = new JSplitPane();
  JSplitPane splitTop = new JSplitPane();
  JPanel pnlDetail = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JScrollPane scrollTree = new JScrollPane();
  JTree treePNR = new JTree();
  private PNR pnr;
  JScrollPane scrollRawData = new JScrollPane();
  JTextArea memRawData = new JTextArea();

  /** 
   ***********************************************************************
   * Constructor
   ***********************************************************************
   */
  public PanelPNR()
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


  public PanelPNR(final PNR aPNR)
    {
    this();
    displayPNR(aPNR);
    }

  /** 
   ***********************************************************************
   * Used by constructor
   ***********************************************************************
   */
  private void jbInit() throws Exception
    {
    splitBase.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitBase.setPreferredSize(new Dimension(475, 425));
    splitBase.setLastDividerLocation(300);
    this.setLayout(borderLayout1);
    pnlDetail.setLayout(borderLayout2);
    treePNR.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
    {

      public void valueChanged(TreeSelectionEvent e)
      {
        treePNR_valueChanged(e);
      }
    });
    memRawData.setLineWrap(true);
    memRawData.setPreferredSize(new Dimension(475, 125));
    memRawData.setWrapStyleWord(true);
    memRawData.setText("PNR Data");
    memRawData.setEditable(false);
    this.setPreferredSize(new Dimension(475, 425));
    scrollRawData.setPreferredSize(new Dimension(475, 125));
    treePNR.setPreferredSize(new Dimension(200, 300));
    scrollTree.setPreferredSize(new Dimension(200, 300));
    pnlDetail.setPreferredSize(new Dimension(275, 300));
    splitTop.setPreferredSize(new Dimension(475, 300));
    this.add(splitBase, BorderLayout.CENTER);
    splitBase.add(splitTop, JSplitPane.TOP);
    splitTop.add(pnlDetail, JSplitPane.RIGHT);
    splitTop.add(scrollTree, JSplitPane.LEFT);
    splitBase.add(scrollRawData, JSplitPane.BOTTOM);
    scrollRawData.getViewport().add(memRawData, null);
    scrollTree.getViewport().add(treePNR, null);
    splitBase.setDividerLocation(300);
    splitTop.setDividerLocation(200);
    }

  /** 
   ***********************************************************************
   * Updates the display with the given PNR
   ***********************************************************************
   */
  public void displayPNR(final PNR aPNR)
    {
    pnr = aPNR;
    // set the raw data
    memRawData.setText( aPNR.getPNRData() );

    m_buildTree(aPNR);

    // put detail data for the first name in the detail panel
    pnlDetail.removeAll();
    final PNRNameElement[] names = aPNR.getNames();
    if ( names instanceof PNRNameElement[] )
      {
      if ( names.length > 0 )
        {
        final PanelName pnlName = new PanelName(names[0],aPNR);
        pnlDetail.add(pnlName);
        }
      }
    }

  /** 
   ***********************************************************************
   * Updates the display with the given PNR
   ***********************************************************************
   */
  private void m_buildTree(final PNR aPNR)
    {
    // build the root node
    final DefaultMutableTreeNode nodeRoot = new DefaultMutableTreeNode(aPNR);

    // add the names
    final DefaultMutableTreeNode nodeFamilies = m_buildTreeNames(aPNR);
    nodeRoot.add(nodeFamilies);

    // add the itinerary segments
    final DefaultMutableTreeNode nodeSegments = m_buildTreeItin(aPNR);
    nodeRoot.add(nodeSegments);

    // add the remarks
    final DefaultMutableTreeNode nodeRemarks = m_buildTreeRemarks(aPNR);
    nodeRoot.add(nodeRemarks);

    // show fares
    final DefaultMutableTreeNode nodeFares = m_buildTreeFares(aPNR);
    nodeRoot.add(nodeFares);

    // add any errors
    final DefaultMutableTreeNode nodeErrors = m_buildTreeErrors(aPNR);
    nodeRoot.add(nodeErrors);


    // build the tree
    final TreeModel tm = new DefaultTreeModel(nodeRoot);
    treePNR.setModel(tm);
    }

  /** 
   ***********************************************************************
   * Creates a node containing all the names on a PNR
   ***********************************************************************
   */
  private DefaultMutableTreeNode m_buildTreeNames(final PNR aPNR)
    {
    // add the names
    final DefaultMutableTreeNode nodeFamilies = new DefaultMutableTreeNode("Families");

    final PNRFamilyElement[] families = aPNR.getFamilies();
    if ( families instanceof PNRFamilyElement[] )
      {
      PNRNameElement[] members;
      DefaultMutableTreeNode nodeFamily;
      DefaultMutableTreeNode nodeMember;
      for ( int iFamNum = 0; iFamNum < families.length; iFamNum++ )
        {
        nodeFamily = new DefaultMutableTreeNode(families[iFamNum]);
        members = families[iFamNum].FamilyMembers;
        if ( members instanceof PNRNameElement[] )
          {
          for ( int iMemberNum = 0; iMemberNum < members.length; iMemberNum++ )
            {
            nodeMember = new DefaultMutableTreeNode(members[iMemberNum]);
            nodeFamily.add(nodeMember);
            }
          }

        nodeFamilies.add(nodeFamily);
        }
      }

    return(nodeFamilies);
    }

  /** 
   ***********************************************************************
   * Creates a node containing all the itin segments on a PNR
   ***********************************************************************
   */
  private DefaultMutableTreeNode m_buildTreeItin(final PNR aPNR)
    {
    // add the itinerary segments
    final DefaultMutableTreeNode nodeItinerary = new DefaultMutableTreeNode("Itinerary");

    final PNRItinSegment[] segments = aPNR.getSegments();
    if ( segments instanceof PNRItinSegment[] )
      {
      DefaultMutableTreeNode nodeSegment;
      for ( int iSegNum = 0; iSegNum < segments.length; iSegNum++ )
        {
        nodeSegment = new DefaultMutableTreeNode(segments[iSegNum]);
        nodeItinerary.add(nodeSegment);
        }
      }

    return(nodeItinerary);
    }

  /** 
   ***********************************************************************
   * Creates a node containing all the remarks on a PNR
   ***********************************************************************
   */
  private DefaultMutableTreeNode m_buildTreeRemarks(final PNR aPNR)
    {
    // add the remarks
    final DefaultMutableTreeNode nodeRemarks = new DefaultMutableTreeNode("Remarks");

    final PNRRemark[] remarks = aPNR.getRemarks();
    if ( remarks instanceof PNRRemark[] )
      {
      DefaultMutableTreeNode nodeRmk;
      for ( int iRmkNum = 0; iRmkNum < remarks.length; iRmkNum++ )
        {
        nodeRmk = new DefaultMutableTreeNode(remarks[iRmkNum]);
        nodeRemarks.add(nodeRmk);
        }
      }

    return(nodeRemarks);
    }

  /** 
   ***********************************************************************
   * Creates a node containing all the fares on a PNR
   ***********************************************************************
   */
  private DefaultMutableTreeNode m_buildTreeFares(final PNR aPNR)
    {
    // add the fares
    final DefaultMutableTreeNode nodeFares = new DefaultMutableTreeNode("Fares");

    final PNRFare[] fares = aPNR.getFares();
    if ( fares instanceof PNRFare[] )
      {
      DefaultMutableTreeNode nodeFare;
      for ( int i = 0; i < fares.length; i++ )
        {
        nodeFare = new DefaultMutableTreeNode(fares[i]);
        nodeFares.add(nodeFare);
        }
      }

    return(nodeFares);
    }

  /** 
   ***********************************************************************
   * Creates a node containing all the fares on a PNR
   ***********************************************************************
   */
  private DefaultMutableTreeNode m_buildTreeErrors(final PNR aPNR)
    {
    // add the errors
    final DefaultMutableTreeNode nodeErrors = new DefaultMutableTreeNode("Errors");

    final String[] errors = aPNR.getErrors();
    if ( errors instanceof String[] )
      {
      DefaultMutableTreeNode nodeError;
      for ( int i = 0; i < errors.length; i++ )
        {
        nodeError = new DefaultMutableTreeNode(errors[i]);
        nodeErrors.add(nodeError);
        }
      }

    return(nodeErrors);
    }

  /** 
   ***********************************************************************
   * When the user selects a tree node
   ***********************************************************************
   */
  void treePNR_valueChanged(TreeSelectionEvent e)
    {

    final DefaultMutableTreeNode CurrentNode = (DefaultMutableTreeNode )treePNR.getLastSelectedPathComponent();
    if ( CurrentNode instanceof DefaultMutableTreeNode )
      {
      final Object NodeObject = CurrentNode.getUserObject();

      final JPanel panel;
      if ( NodeObject instanceof PNRNameElement )
        panel = new PanelName( (PNRNameElement )NodeObject, pnr );
      else if ( NodeObject instanceof PNRRemark )
        panel = new PanelRemark( (PNRRemark )NodeObject, pnr );
      else if ( NodeObject instanceof PNRItinAirSegment )
        panel = new PanelFlight( (PNRItinAirSegment )NodeObject, pnr );
      else if ( NodeObject instanceof PNRItinCarSegment )
        panel = new PanelCarRental( (PNRItinCarSegment )NodeObject, pnr );
      else if ( NodeObject instanceof PNRItinHotelSegment )
        panel = new PanelHotel( (PNRItinHotelSegment )NodeObject, pnr );
      else if ( NodeObject instanceof PNRFare )
        panel = new PanelFare( (PNRFare )NodeObject );
      else if ( NodeObject instanceof PNR )
        panel = new PanelTitle(pnr);
      else if ( NodeObject instanceof String )
        panel = new PanelError( (String )NodeObject );
      else
        panel = new JPanel();


      // add the new detail panel
      pnlDetail.removeAll();
      if ( panel instanceof JPanel )
        pnlDetail.add(panel);
      pnlDetail.revalidate();
      }
    }

}


/** 
 ***********************************************************************
 * Panel for displaying basic PNR info
 ***********************************************************************
 */

class PanelTitle extends JPanel
{
  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PanelTitle(final PNR aPNR)
    {
    try
      {
      // set the layout manager
      final GridLayout gridLayoutBase = new GridLayout(0,1);
      setLayout(gridLayoutBase);

      // create a property grid with PNR data
      final PropertyGrid propPNR = new PropertyGrid("PNR Properties");

      propPNR.addBlankRow();
      propPNR.addProperty("CRS", aPNR.getCrs() );
      propPNR.addProperty("Locator", aPNR.getLocator() );
      propPNR.addProperty("Pseudo City", aPNR.getPseudoCity() );
      propPNR.addProperty("Agent Sign", aPNR.getAgentSign() );
      propPNR.addBlankRow();

      add(propPNR);
      }
    catch(Exception ex)
      {
      ex.printStackTrace();
      }
    }
}

/** 
 ***********************************************************************
 * Panel for displaying basic PNR error info
 ***********************************************************************
 */

class PanelError extends JPanel
{
  /** 
   ***********************************************************************
   * constructor
   ***********************************************************************
   */
  public PanelError(final String aErrorMessage)
    {
    try
      {
      // set the layout manager
      final BorderLayout borderLayoutBase = new BorderLayout();
      setLayout(borderLayoutBase);

      // create text control
      final JTextArea memError = new JTextArea(aErrorMessage);
      memError.setEditable(false);

      final JScrollPane scrollError = new JScrollPane();

      // add to scroll control
      scrollError.getViewport().add(memError, null);
      add(scrollError,BorderLayout.CENTER);
      }
    catch(Exception ex)
      {
      ex.printStackTrace();
      }
    }

}
