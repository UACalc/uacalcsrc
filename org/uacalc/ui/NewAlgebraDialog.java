package org.uacalc.ui;

import javax.swing.JDialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NewAlgebraDialog extends JDialog {
  
  UACalculator uacalc;
  String name;
  int card = -1;
  String desc;
  JTextField name_tf;
  JTextField card_tf;
  JTextField desc_tf;
  
  public NewAlgebraDialog (UACalculator uacalc) {
    super(uacalc, true); // owner, parent
    
    setTitle("New Algebra");
    getContentPane().setLayout(new BorderLayout());
    JPanel topSect = new JPanel();
    JPanel midSect = new JPanel();
    JPanel buttonSect = new JPanel();
    JPanel nameSect = new JPanel();
    JPanel cardSect = new JPanel();
    topSect.setLayout(new FlowLayout());
    topSect.add(nameSect);
    topSect.add(cardSect);
    nameSect.add(new JLabel("Name:"));
    name_tf = new JTextField(6);
    nameSect.add(name_tf);
    cardSect.add(new JLabel("cardinality:"));
    card_tf = new JTextField(3);
    cardSect.add(card_tf);

    // description stuff
    midSect.add(new JLabel("Description:"));
    desc_tf = new JTextField(24);
    midSect.add(desc_tf);
    JButton ok = new JButton("Ok");
    
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         if (getTextFields()) dispose();
      }
    });
    
    JButton cancel = new JButton("Cancel");
    buttonSect.add(ok);
    buttonSect.add(cancel);
    add(topSect, BorderLayout.NORTH);
    add(midSect, BorderLayout.CENTER);
    add(buttonSect, BorderLayout.SOUTH);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (screenSize.width * 1) / 4;
    int height = (screenSize.height * 2) / 5;

    //editDialog.dispose();
    setLocation(width, height);
    pack();
    validate();
  }
  
  public String getAlgebraName() {
    return name;
  }
  
  public boolean getTextFields() {
    name = name_tf.getText().trim();
    if (name == null || name.length() == 0 || name.indexOf(" ") > 0) {
      JOptionPane.showMessageDialog(this,
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    desc = desc_tf.getText();
    boolean cardOk = true;
    try {
      card = Integer.parseInt(card_tf.getText());
    }
    catch (NumberFormatException ex) {
      cardOk = false;
    }
    if (!cardOk || card <= 0) {
      JOptionPane.showMessageDialog(this,
          "cardinality must be a positive integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      card_tf.setText("");
      return false;
    }
    return true;
  }
  
  public void clear() {
    name_tf.setText("");
    desc_tf.setText("");
    card_tf.setText("");
    card = -1;
    name = null;
    desc = null;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDesc() {
    return desc;
  }
  
  public int getCard() {
    return card;
  }
  
    
  
}

