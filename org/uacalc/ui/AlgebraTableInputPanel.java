package org.uacalc.ui;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;

public class AlgebraTableInputPanel extends JPanel {

  String algName;
  int card;
  String desc;
  
  public AlgebraTableInputPanel(String algName, int card, String desc) {
    this.algName = algName;
    this.card = card;
    this.desc = desc;
    setLayout(new BorderLayout());
    JPanel top = new JPanel();
    top.setLayout(new BorderLayout());
    JPanel titlePanel = new JPanel();
    titlePanel.add(new JLabel("Algebra Input"));
    top.add(titlePanel, BorderLayout.NORTH);
    
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new FlowLayout());
    JPanel namePanel = new JPanel();
    JPanel cardPanel = new JPanel();
    JPanel descPanel = new JPanel();
    
    namePanel.add(new JLabel("Name:"));
    JTextField name_tf = new JTextField(algName);
    name_tf.setEditable(false);
    namePanel.add(name_tf);
    
    cardPanel.add(new JLabel("Cardinality:"));
    JTextField card_tf = new JTextField("" + card);
    card_tf.setEditable(false);
    cardPanel.add(card_tf);
    
    descPanel.add(new JLabel("Description:"));
    JTextField desc_tf = new JTextField(desc);
    desc_tf.setEditable(true);
    descPanel.add(desc_tf);
    
    infoPanel.add(namePanel);
    infoPanel.add(cardPanel);
    infoPanel.add(descPanel);
    
    JPanel buttonPanel = new JPanel();
    JButton addOp = new JButton("Add operation");
    buttonPanel.add(addOp);
    JButton done = new JButton("Done");
    buttonPanel.add(done);

    top.add(buttonPanel, BorderLayout.SOUTH);
    
    top.add(infoPanel, BorderLayout.CENTER);
    add(top, BorderLayout.NORTH);
    validate();
  }
  
}



