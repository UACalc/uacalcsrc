package org.uacalc.nbui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableColumn;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.uacalc.alg.*;
import org.uacalc.lat.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.ui.util.*;
import org.uacalc.ui.table.*;
import org.latdraw.beans.*;
import org.latdraw.diagram.*;

public class ConController {

  private final UACalc uacalcUI;
  private LatDrawer conLatDrawer;
  final private JScrollPane conTableScrollPane;
  private ConLatticeTableModel conTableModel;
  private JTable conTable;
  
  private static final JTable blankConTable = new JTable(new ConLatticeTableModel(null, ConLatticeTableModel.DataType.ALL));
  static {
    blankConTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    setColWidths(blankConTable);
  }
  
  public ConController(UACalc uacalcUI, PropertyChangeSupport cs) {
    this.uacalcUI = uacalcUI;
    this.conTableScrollPane = new JScrollPane();
    conLatDrawer = new LatDrawer(uacalcUI);
    uacalcUI.getConMainPanel().setLayout(new BorderLayout());
    uacalcUI.getConMainPanel().add(conLatDrawer, BorderLayout.CENTER);
    conLatDrawer.getDrawPanel().getChangeSupport().addPropertyChangeListener(
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(ChangeSupport.VERTEX_RIGHT_PRESSED)) {
              Vertex v = (Vertex)e.getNewValue();
              System.out.println("underlyingObj = " + v.getUnderlyingObject());
            }
          }
      
    });
    addPropertyChangeListener(cs);
    createPopupMenu();
    //setupButtons();
    // new stuff
    //ConLatticeTableModel model = new ConLatticeTableModel(polin, ConLatticeTableModel.DataType.ALL);
    //JTable conTable = new JTable(model);
  }
  
  public void setDrawer() {
    uacalcUI.getConMainPanel().removeAll();
    uacalcUI.getConMainPanel().add(conLatDrawer, BorderLayout.CENTER);
    uacalcUI.getConMainPanel().revalidate();
    drawCon();
  }
  
  public void setTable() {
    uacalcUI.getConMainPanel().removeAll();
    uacalcUI.getConMainPanel().revalidate();
    uacalcUI.repaint();
  }
  
  private void setupButtons() {
    uacalcUI.getConLeftPanel().setLayout(new GridLayout(1,2));
    JButton diagBut = new JButton("Diagram");
    JButton tableBut = new JButton("Table");
    uacalcUI.getConLeftPanel().add(diagBut);
    uacalcUI.getConLeftPanel().add(tableBut);
    diagBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        uacalcUI.getConMainPanel().removeAll();
        uacalcUI.getConMainPanel().add(conLatDrawer, BorderLayout.CENTER);
        uacalcUI.getConMainPanel().revalidate();
        drawCon();
      }
    });
  }
  
  private void addPropertyChangeListener(PropertyChangeSupport cs) {
    cs.addPropertyChangeListener(
        MainController.ALGEBRA_CHANGED,
        new   PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            getConLatDrawer().setBasicLattice(null);
            final SmallAlgebra alg = uacalcUI.getMainController().getCurrentAlgebra().getAlgebra();
            alg.resetConAndSub();
          }
        }
    );
  }
  
  public LatDrawer getConLatDrawer() { return conLatDrawer; }
  
  public void notTotalWarning() {
    uacalcUI.getMainController().beep();
    uacalcUI.getMainController().setUserWarning(
        "Not all the operations of this algebra are total.", false);
  }
  

  /**
   * Assumes alg.con().universeFound() is false.
   * 
   * @param alg
   * @return    true if a background task is launched
   */
  public boolean makeConInBackground(SmallAlgebra alg) {  
    
    System.out.println("makeConInBG called");
    //final int maxSize = CongruenceLattice.MAX_DRAWABLE_SIZE;
    final int inputSize = alg.inputSize();
    if (inputSize < 0 || inputSize > CongruenceLattice.MAX_DRAWABLE_INPUT_SIZE) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserMessage(
          "The input size (" + inputSize + ") is pretty big; " +
          "putting this in a background task.", false);
      // for testing;
      final int comTabIndex = 2;
      if (uacalcUI.getTabbedPane().getSelectedIndex() != comTabIndex) {
        uacalcUI.getTabbedPane().setSelectedIndex(comTabIndex);
        uacalcUI.repaint();
      }
      uacalcUI.getComputationsController().setupCongruencesTask();

      //getConLatDrawer().setBasicLattice(null);
      return true;
    }
    return false;
  }

  
  public void drawCon() {
    System.out.println("drawCon() called");
    GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg == null) return;
    SmallAlgebra alg = gAlg.getAlgebra();
    if (alg == null) return;
    final int conTabIndex = 3;
    if (uacalcUI.getTabbedPane().getSelectedIndex() != conTabIndex) {
      uacalcUI.getTabbedPane().setSelectedIndex(conTabIndex);
      uacalcUI.repaint();
    }
    drawCon(alg, true);
  }
  
  public void drawCon(SmallAlgebra alg, boolean makeIfNull) {
    ////////////////////// temporary //////////////
    setConTable(makeIfNull);
    System.out.println("univ made: " + alg.con().universeFound());
    if (makeIfNull) {
      if (!alg.isTotal()) {
        notTotalWarning();
        getConLatDrawer().setBasicLattice(null);
        return;
      }
      if (!alg.con().universeFound()) { 
        if (makeConInBackground(alg)) return;
        alg.con().universe();
      }
        
      if (!alg.con().isDrawable()) {
        uacalcUI.getMainController().beep();
        final int maxSize = CongruenceLattice.MAX_DRAWABLE_SIZE;
        uacalcUI.getMainController().setUserWarning(
            "Too many elements in the congruence lattice. More than " + maxSize + ".", false);
        getConLatDrawer().setBasicLattice(null);
        return;
      }
      
    }
    getConLatDrawer().setBasicLattice(alg.con().getBasicLattice(makeIfNull));
    //getConLatDrawer().setDiagram(alg.con().getDiagram());
    getConLatDrawer().repaint();
  }
  
  public void setConTable(boolean makeIfNull) {
    GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg == null) return;
    SmallAlgebra alg = gAlg.getAlgebra();
    if (alg == null) return;
    conTable = alg.con().getConTable();
    if (conTable == null) {
      if (makeIfNull) makeConTable(alg, makeIfNull);
      else setBlankConTable();
      return;
      
    }
    if (makeIfNull) { // only move to this window if makeIfNull is true
      final int conTabIndex = 3;
      if (uacalcUI.getTabbedPane().getSelectedIndex() != conTabIndex) {
        uacalcUI.getTabbedPane().setSelectedIndex(conTabIndex);
      }
    }
    setConTable(alg);
  }
  
  public void makeConTable(SmallAlgebra alg, boolean makeIfNull) {
    if (makeIfNull && !alg.con().universeFound()) {
      final boolean bgLaunched = makeConInBackground(alg);
      if (bgLaunched) return;
      // the background process will call this again. It needs to check if
      // alg is still the current one and draw it if it is. TODO: 
    }
    setConTable(alg);
  }
  
  private void setBlankConTable() {
    conTable = blankConTable;
    conTableScrollPane.setViewportView(conTable);
    uacalcUI.getConLeftPanel().removeAll();
    uacalcUI.getConLeftPanel().add(conTableScrollPane, "grow");
    uacalcUI.getConLeftPanel().revalidate();
    uacalcUI.repaint();
  }
  
  public void setConTable(SmallAlgebra alg) {
    conTableModel = new ConLatticeTableModel(alg, ConLatticeTableModel.DataType.ALL);
    conTable = new JTable(conTableModel);
    setColWidths();
    alg.con().setConTable(conTable);
    conTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    conTableScrollPane.setViewportView(conTable);
    uacalcUI.getConLeftPanel().removeAll();
    uacalcUI.getConLeftPanel().add(conTableScrollPane, "grow");
    uacalcUI.getConLeftPanel().revalidate();
    uacalcUI.getConLeftPanel().validate();
    
    
    uacalcUI.repaint();
    System.out.println("foo: ");
  }
  
  private void setColWidths() {
    setColWidths(conTable);
  }
  
  private static void setColWidths(JTable table) {
    final int cols = table.getColumnCount();
    TableColumn col = table.getColumnModel().getColumn(0);
    col.setPreferredWidth(50);
    col = table.getColumnModel().getColumn(1);
    col.setPreferredWidth(25);
    col = table.getColumnModel().getColumn(2);
    col.setPreferredWidth(25);
    for (int i = 3; i < cols - 1; i++) {
      col = table.getColumnModel().getColumn(i);
      col.setPreferredWidth(50);
    }
    col = table.getColumnModel().getColumn(cols - 1);
    col.setPreferredWidth(400);
  }
  
  public void createPopupMenu() {
    JMenuItem menuItem;

    //Create the popup menu.
    JPopupMenu popup = new JPopupMenu();
    menuItem = new JMenuItem("Make quotient over selected elt");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (getConLatDrawer().getSelectedElem() == null) return;
        Partition par = (Partition)getConLatDrawer().getSelectedElem().getUnderlyingObject();
        GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
        SmallAlgebra alg = gAlg.getAlgebra();
        QuotientAlgebra quot = new QuotientAlgebra(alg, par);
        if (alg.getName() != null && alg.getName().length() > 0) {
          quot.setName("QuotOf" + alg.getName());
          quot.setDescription("The quotient of " + alg.getName() + " by " + par); 
        }
        else quot.setDescription(" quotient by " + par);
        GUIAlgebra gQuot = new GUIAlgebra(quot, null, gAlg);
        uacalcUI.getMainController().addAlgebra(gQuot, false);
      }
    });
    popup.add(menuItem);
    //menuItem = new JMenuItem("Another popup menu item");
    //menuItem.addActionListener(this);
    //popup.add(menuItem);

    //Add listener to the text area so the popup menu can come up.
    MouseListener popupListener = new PopupListener(popup);
    getConLatDrawer().getDrawPanel().addMouseListener(popupListener);
  }

}
