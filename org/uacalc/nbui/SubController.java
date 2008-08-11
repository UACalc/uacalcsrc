package org.uacalc.nbui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.uacalc.alg.SmallAlgebra;
import org.uacalc.alg.Subalgebra;
import org.uacalc.alg.sublat.*;
import org.uacalc.ui.util.*;

public class SubController {

  private final UACalculatorUI uacalcUI;
  private LatDrawer subLatDrawer;
  
  public SubController(UACalculatorUI uacalcUI, PropertyChangeSupport cs) {
    this.uacalcUI = uacalcUI;
    subLatDrawer = new LatDrawer(uacalcUI);
    uacalcUI.getSubMainPanel().setLayout(new BorderLayout());
    uacalcUI.getSubMainPanel().add(subLatDrawer, BorderLayout.CENTER);
    addPropertyChangeListener(cs);
    createPopupMenu();
  }
  
  private void addPropertyChangeListener(PropertyChangeSupport cs) {
    cs.addPropertyChangeListener(
        MainController.ALGEBRA_CHANGED,
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            getSubLatDrawer().setBasicLattice(null);  
            final SmallAlgebra alg = uacalcUI.getMainController().getCurrentAlgebra().getAlgebra();
            alg.resetConAndSub();
          }
        }
    );
  }
  
  public LatDrawer getSubLatDrawer() { return subLatDrawer; }
  
  public void drawSub() {
    GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg == null) return;
    SmallAlgebra alg = gAlg.getAlgebra();
    if (alg == null) return;
    final int subTabIndex = 4;
    if (uacalcUI.getTabbedPane().getSelectedIndex() != subTabIndex) {
      uacalcUI.getTabbedPane().setSelectedIndex(subTabIndex);
      uacalcUI.repaint();
    }
    drawSub(alg, true);
  }
  
  public void drawSub(SmallAlgebra alg, boolean makeIfNull) {
    if (makeIfNull) {
      if (!alg.isTotal()) {
        uacalcUI.getMainController().beep();
        uacalcUI.getMainController().setUserWarning(
            "Not all the operations of this algebra are total.", false);
        getSubLatDrawer().setBasicLattice(null);
        return;
      }
      final int maxSize = SubalgebraLattice.MAX_DRAWABLE_SIZE;
      if (!alg.sub().isDrawable()) {
        uacalcUI.getMainController().beep();
        uacalcUI.getMainController().setUserWarning(
            "Too many elements in the subalgebra lattice. More than " + maxSize + ".", false);
        getSubLatDrawer().setBasicLattice(null);
        return;
      }
    }
    getSubLatDrawer().setBasicLattice(alg.sub().getBasicLattice(makeIfNull));
    //getConLatDrawer().setDiagram(alg.con().getDiagram());
    getSubLatDrawer().repaint();
  }
  
  public void createPopupMenu() {
    JMenuItem menuItem;

    //Create the popup menu.
    JPopupMenu popup = new JPopupMenu();
    menuItem = new JMenuItem("Make the subalgebra with this subuniverse");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (getSubLatDrawer().getSelectedElem() == null) return;
        BasicSet subUniv = (BasicSet)getSubLatDrawer().getSelectedElem().getUnderlyingObject();
        if (subUniv.size() == 0) return;
        GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
        SmallAlgebra alg = gAlg.getAlgebra();
        Subalgebra sub = new Subalgebra(alg, subUniv);
        if (alg.getName() != null && alg.getName().length() > 0) {
          sub.setName("SubalgebraOf" + alg.getName());
          sub.setDescription("The subalgebra of " + alg.getName() + ", univ " + subUniv); 
        }
        else sub.setDescription(" subalgebra " + subUniv);
        GUIAlgebra gSub = new GUIAlgebra(sub, null, gAlg);
        uacalcUI.getMainController().addAlgebra(gSub, false);
      }
    });
    popup.add(menuItem);
    //menuItem = new JMenuItem("Another popup menu item");
    //menuItem.addActionListener(this);
    //popup.add(menuItem);

    //Add listener to the text area so the popup menu can come up.
    MouseListener popupListener = new PopupListener(popup);
    getSubLatDrawer().getDrawPanel().addMouseListener(popupListener);
  }
  
  
}
