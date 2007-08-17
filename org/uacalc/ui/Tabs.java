package org.uacalc.ui;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Tabs extends JTabbedPane {

  public final int EDITOR_INDEX;
  
  public final int CONSTRUCTIONS_INDEX;
  
  public final int COMPUTATIONS_INDEX;
  
  public final int PROPERTIES_INDEX;
  
  public final int DRAWING_INDEX;
  
  /*
  static {
    int index = 0, disabled = -1;
    EDITOR_INDEX = index++;
    CONSTRUCTIONS_INDEX = index++;
    CALCULATIONS_INDEX = index++;
    PROPERTIES_INDEX = index++;
    DRAWING_INDEX = index++;
  }
  */
  
  UACalculator uacalc;
  AlgebraEditor algebraEditor;
  LatDrawPanel latticeDrawer;
  ComputationsPanel computationsPanel;
  
  public Tabs(final UACalculator uacalc) {
    this.uacalc = uacalc;
    algebraEditor = new AlgebraEditor(uacalc);
    latticeDrawer = new LatDrawPanel(uacalc);
    computationsPanel = new ComputationsPanel(uacalc);
    int index = 0;
    addTab("Editor", algebraEditor);
    EDITOR_INDEX = index++;
    addTab("Drawing", latticeDrawer);
    DRAWING_INDEX = index++;
    addTab("Computations", computationsPanel);
    COMPUTATIONS_INDEX = index++;
    // for now
    CONSTRUCTIONS_INDEX = -1;
    PROPERTIES_INDEX = -1;
    
    addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
         uacalc.resetToolBar();
      }
    });
  }
  
  public LatDrawPanel getLatticeDrawer() {
    return latticeDrawer;
  }
  
  public AlgebraEditor getAlgebraEditor() {
    return algebraEditor;
  }
  
  public ComputationsPanel getComputationsPanel() {
    return computationsPanel;
  }
  
  public JToolBar getCurrentToolBar() {
    if (getSelectedComponent() == latticeDrawer) return latticeDrawer.getToolBar();
    if (getSelectedComponent() == algebraEditor) return algebraEditor.getToolBar();
    return computationsPanel.getToolBar();
  }
  
}
