package org.uacalc.nbui;

import java.awt.*;
import javax.swing.*;
import org.uacalc.alg.*;
import org.uacalc.lat.*;
import org.uacalc.alg.conlat.*;

public class ConController {

  private final UACalculatorUI uacalcUI;
  private ConLatDrawer conLatDrawer;
  
  public ConController(UACalculatorUI uacalcUI) {
    this.uacalcUI = uacalcUI;
    conLatDrawer = new ConLatDrawer(uacalcUI);
    uacalcUI.getConMainPanel().setLayout(new BorderLayout());
    uacalcUI.getConMainPanel().add(conLatDrawer, BorderLayout.CENTER);
  }
  
  public ConLatDrawer getConLatDrawer() { return conLatDrawer; }
  
  
  
  public void drawCon(SmallAlgebra alg) {
    final int maxSize = CongruenceLattice.MAX_DRAWABLE_SIZE - 1;
    if (!alg.con().isDrawable()) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning("Too many elements. More than " + maxSize + ".", false);
      return;
    }
    getConLatDrawer().setDiagram(alg.con().getDiagram());
    getConLatDrawer().repaint();
  }
  
}
