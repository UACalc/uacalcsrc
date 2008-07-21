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
  
  public void drawSub() {
    SmallAlgebra alg = uacalcUI.getMainController().getCurrentAlgebra().getAlgebra();
    if (alg == null) return;
    final int subTabIndex = 4;
    if (uacalcUI.getTabbedPane().getSelectedIndex() != subTabIndex) {
      uacalcUI.getTabbedPane().setSelectedIndex(subTabIndex);
      uacalcUI.repaint();
    }
    drawSub(alg, true);
  }
  
  public void drawSub(SmallAlgebra alg, boolean makeIfNull) {
    final int maxSize = SubalgebraLattice.MAX_DRAWABLE_SIZE;
    if (!alg.sub().isDrawable()) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning(
          "Too many elements in the subalgebra lattice. More than " + maxSize + ".", false);
      getSubLatDrawer().setBasicLattice(null);
      return;
    }
    getSubLatDrawer().setBasicLattice(alg.sub().getBasicLattice(makeIfNull));
    //getConLatDrawer().setDiagram(alg.con().getDiagram());
    getSubLatDrawer().repaint();
  }
  
  
}
