package org.uacalc.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.util.*;
import org.uacalc.alg.*;

public class AlgebraEditor extends JPanel {

  private final UACalculator uacalc;
  private int card = -1;
  private String algName;
  private String desc;
  private SmallAlgebra alg;
  
  private JToolBar toolBar;
  private final JTextField name_tf = new JTextField(8);
  private final JTextField card_tf = new JTextField(4);
  private final JTextField desc_tf = new JTextField(40);
  private final JComboBox ops_cb = new JComboBox();
  
  public AlgebraEditor(final UACalculator uacalc) {
    this.uacalc = uacalc;
    setLayout(new BorderLayout());
    JPanel top = new JPanel();
    top.setLayout(new BorderLayout());
    //JPanel titlePanel = new JPanel();
    //titlePanel.add(new JLabel("Algebra Input"));
    //top.add(titlePanel, BorderLayout.NORTH);
    JPanel fieldsPanel = new JPanel();
    top.add(fieldsPanel, BorderLayout.NORTH);
    
    /*
    JButton newAlgBut = new JButton("New Algebra");
    fieldsPanel.add(newAlgBut);
    newAlgBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        alg = null;
        setupNewAlgebra();
      }
    });
    */
    
    fieldsPanel.add(new JLabel("Name:"));
    name_tf.setEditable(false);
    fieldsPanel.add(name_tf);
    
    fieldsPanel.add(new JLabel("Cardinality:"));
    card_tf.setEditable(false);
    fieldsPanel.add(card_tf);
    
    fieldsPanel.add(new JLabel("Description:"));
    desc_tf.setEditable(true);
    fieldsPanel.add(desc_tf);
    
    fieldsPanel.add(new JLabel("Operations"));
    resetOpsCB();
    fieldsPanel.add(ops_cb);
    
    add(top, BorderLayout.NORTH);
    validate();
  }
  
  private void resetOpsCB() {
    ops_cb.removeAllItems();
    ops_cb.addItem("New Op");
  }
  
  private void makeToolBar() {
    toolBar = new JToolBar();
    ClassLoader cl = uacalc.getClass().getClassLoader();
    ImageIcon icon = new ImageIcon(cl.getResource(
                          "org/uacalc/ui/images/New16.gif"));
    JButton newAlgBut = new JButton("New", icon);
    toolBar.add(newAlgBut);
    newAlgBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        alg = null;
        setupNewAlgebra();
      }
    });
  }
  
  public JToolBar getToolBar() {
    if (toolBar == null) makeToolBar();
    return toolBar; 
  }
  
  private void populateFields() {
    if (alg == null) return;
    name_tf.setText(alg.name());
    card_tf.setText("" + alg.cardinality());
    desc_tf.setText("");
  }
  
  public SmallAlgebra getAlgebra() {
    return alg;
  }
  
  public void setAlgebra(SmallAlgebra alg) {
    this.alg = alg;
  }
  
  public void setAlgebra() {
    SmallAlgebra alg = getAlgebra();
    if (alg == null) return;
    name_tf.setText(alg.name());
    card_tf.setText("" + alg.cardinality());
    desc_tf.setText(alg.description());
    setOpsCB();
  }
  
  private void setOpsCB() {
    java.util.List<OperationSymbol> opSyms 
               = getAlgebra().similarityType().getOperationSymbols();
    
  }
  
  private void setupNewAlgebra() {
    String name = getAlgName();
    if (name == null) return;
    int card = getCard();
    if (card > 0) {
      alg = new BasicAlgebra(name, card, new ArrayList<Operation>());
      populateFields();
    }
  }
  
  private String getAlgName() {
    String name = JOptionPane.showInputDialog(uacalc, "Short name (with no spaces) for the algebra?");
    if (name == null || name.length() == 0 || name.indexOf(" ") > 0) {
      JOptionPane.showMessageDialog(this,
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      uacalc.beep();
      name = null; 
    }
    algName = name;
    return name;
  }
  
  public int getCard() {
    String cardStr = JOptionPane.showInputDialog(uacalc, "What is the cardinality?");
    if (cardStr == null) return -1;
    int card = -1;
    boolean cardOk = true;
    try {
      card = Integer.parseInt(cardStr);
    }
    catch (NumberFormatException e) {
      cardOk = false;
    }
    if (!cardOk || card <= 0) {
      JOptionPane.showMessageDialog(this,
          "cardinality must be a positive integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    this.card = card;
    return card;
    // set the card field and clear all else
  }
  
}
