package org.uacalc.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class AlgebraEditor extends JPanel {

  private UACalculator uacalc;
  
  public AlgebraEditor(UACalculator uacalc) {
    this.uacalc = uacalc;
    setLayout(new BorderLayout());
    JPanel top = new JPanel();
    top.setLayout(new BorderLayout());
    JPanel titlePanel = new JPanel();
    titlePanel.add(new JLabel("Algebra Input"));
    top.add(titlePanel, BorderLayout.NORTH);
  }
  
}
