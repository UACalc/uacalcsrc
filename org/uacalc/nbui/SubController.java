package org.uacalc.nbui;

import java.awt.BorderLayout;

import org.uacalc.alg.SmallAlgebra;
import org.uacalc.alg.sublat.SubalgebraLattice;

public class SubController {

  private final UACalculatorUI uacalcUI;
  private LatDrawer subLatDrawer;
  
  public SubController(UACalculatorUI uacalcUI) {
    this.uacalcUI = uacalcUI;
    subLatDrawer = new LatDrawer(uacalcUI);
    uacalcUI.getSubMainPanel().setLayout(new BorderLayout());
    uacalcUI.getSubMainPanel().add(subLatDrawer, BorderLayout.CENTER);
  }
  
  public LatDrawer getSubLatDrawer() { return subLatDrawer; }
  
  
  
  public void drawSub(SmallAlgebra alg) {
    final int maxSize = SubalgebraLattice.MAX_DRAWABLE_SIZE;
    if (!alg.sub().isDrawable()) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning(
          "Too many elements in the subalgebra lattice. More than " + maxSize + ".", false);
      return;
    }
    getSubLatDrawer().setBasicLattice(alg.sub().getBasicLattice());
    //getConLatDrawer().setDiagram(alg.con().getDiagram());
    getSubLatDrawer().repaint();
  }
  
  
}
