package org.uacalc.nbui;

import java.awt.*;
import javax.swing.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.uacalc.alg.*;
import org.uacalc.lat.*;
import org.uacalc.alg.conlat.*;

public class ConController {

  private final UACalculatorUI uacalcUI;
  private LatDrawer conLatDrawer;
  
  public ConController(UACalculatorUI uacalcUI, PropertyChangeSupport cs) {
    this.uacalcUI = uacalcUI;
    conLatDrawer = new LatDrawer(uacalcUI);
    uacalcUI.getConMainPanel().setLayout(new BorderLayout());
    uacalcUI.getConMainPanel().add(conLatDrawer, BorderLayout.CENTER);
    addPropertyChangeListener(cs);
  }
  
  private void addPropertyChangeListener(PropertyChangeSupport cs) {
    cs.addPropertyChangeListener(
        MainController.ALGEBRA_CHANGED,
        new PropertyChangeListener() {
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
    SmallAlgebra alg = uacalcUI.getMainController().getCurrentAlgebra().getAlgebra();
    if (alg == null) return;
    final int conTabIndex = 3;
    if (uacalcUI.getTabbedPane().getSelectedIndex() != conTabIndex) {
      uacalcUI.getTabbedPane().setSelectedIndex(conTabIndex);
      uacalcUI.repaint();
    }
    drawCon(alg, true);
  }
  
  public void drawCon(SmallAlgebra alg, boolean makeIfNull) {
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
    getConLatDrawer().setBasicLattice(alg.con().getBasicLattice(makeIfNull));
    //getConLatDrawer().setDiagram(alg.con().getDiagram());
    getConLatDrawer().repaint();
  }
  
}
