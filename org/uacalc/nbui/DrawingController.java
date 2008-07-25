package org.uacalc.nbui;

import java.awt.BorderLayout;
import java.util.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.uacalc.alg.SmallAlgebra;
import org.uacalc.ui.util.*;
import org.uacalc.alg.op.*;
import org.uacalc.lat.*;


public class DrawingController {
  
  public static final int MAX_DRAWABLE_SIZE = 100;
  private final UACalculatorUI uacalcUI;
  private LatDrawer latDrawer;
  
  
  public DrawingController(UACalculatorUI uacalcUI, PropertyChangeSupport cs) {
    this.uacalcUI = uacalcUI;
    latDrawer = new LatDrawer(uacalcUI);
    uacalcUI.getDrawingMainPanel().setLayout(new BorderLayout());
    uacalcUI.getDrawingMainPanel().add(latDrawer, BorderLayout.CENTER);
    addPropertyChangeListener(cs);
  }
  
  private void addPropertyChangeListener(PropertyChangeSupport cs) {
    cs.addPropertyChangeListener(
        MainController.ALGEBRA_CHANGED,
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            getLatDrawer().setBasicLattice(null);  
            uacalcUI.getMainController().getCurrentAlgebra().resetLattices();
          }
        }
    );
  }
  
  public LatDrawer getLatDrawer() { return latDrawer; }
  
  private List<Operation> findSemilatticeOps(SmallAlgebra alg) {
    List<Operation> ans = new ArrayList<Operation>();
    for (Operation opx : alg.operations()) {
      if (Operations.isCommutative(opx) && Operations.isIdempotent(opx)
          && Operations.isAssociative(opx)) ans.add(opx);
    }
    return ans;
  }

  public void drawAlg() {
    GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg == null) return;
    SmallAlgebra alg = gAlg.getAlgebra();
    if (alg == null) return;
    final int algDrawTabIndex = 5;
    if (uacalcUI.getTabbedPane().getSelectedIndex() != algDrawTabIndex) {
      uacalcUI.getTabbedPane().setSelectedIndex(algDrawTabIndex);
      uacalcUI.repaint();
    }
    drawAlg(gAlg, true);
  }
  
  public void drawAlg(GUIAlgebra gAlg, boolean makeIfNull) {
    if (makeIfNull) {
      if (!gAlg.getAlgebra().isTotal()) {
        uacalcUI.getMainController().beep();
        uacalcUI.getMainController().setUserWarning(
            "Not all the operations of this algebra are total.", false);
        getLatDrawer().setBasicLattice(null);
        return;
      }
      SmallAlgebra alg = gAlg.getAlgebra();
      if (alg.cardinality() > MAX_DRAWABLE_SIZE) {
        uacalcUI.getMainController().beep();
        uacalcUI.getMainController().setUserWarning(
            "Too many elements in this algebra. More than " 
            + MAX_DRAWABLE_SIZE + ".", false);
        getLatDrawer().setBasicLattice(null);
        uacalcUI.repaint();
        return;
      }
    }
    /*
    List<Operation> semilatOps = findSemilatticeOps(alg);
    if (semilatOps.isEmpty()) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning(
          "This algebra has no semlattice operations.", false);
      getLatDrawer().setBasicLattice(null);
      uacalcUI.repaint();
      getLatDrawer().setBasicLattice(null);
      return;
    }
    List univ = new ArrayList(alg.universe());
    BasicLattice lat = Lattices.latticeFromMeet("", univ, semilatOps.get(0));
    */
    // need to save these lat's, associated with the algebra and the op, 
    // probably in this class.
    BasicLattice lat = gAlg.getCurrentLattice(makeIfNull);
    if (makeIfNull && lat == null) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning(
          "This algebra has no semlattice operations.", false); 
    }
    getLatDrawer().setBasicLattice(lat);
    getLatDrawer().repaint();
  }
  

}
