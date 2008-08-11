package org.uacalc.nbui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.uacalc.alg.*;
import org.uacalc.lat.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.ui.util.*;
import org.latdraw.beans.*;
import org.latdraw.diagram.*;

public class ConController {

  private final UACalculatorUI uacalcUI;
  private LatDrawer conLatDrawer;
  
  public ConController(UACalculatorUI uacalcUI, PropertyChangeSupport cs) {
    this.uacalcUI = uacalcUI;
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
  
  public void drawCon() {
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
    if (makeIfNull) {
      if (!alg.isTotal()) {
        uacalcUI.getMainController().beep();
        uacalcUI.getMainController().setUserWarning(
            "Not all the operations of this algebra are total.", false);
        getConLatDrawer().setBasicLattice(null);
        return;
      }
      final int maxSize = CongruenceLattice.MAX_DRAWABLE_SIZE;
      if (!alg.con().isDrawable()) {
        uacalcUI.getMainController().beep();
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
