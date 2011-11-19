package org.uacalc.nbui;

import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import org.uacalc.alg.*;
import org.uacalc.io.*;
import net.miginfocom.swing.MigLayout;

public class AlgebraPreviewer extends JPanel implements PropertyChangeListener {
  
  UACalc uacalc;
  Algebra algebra;
  int numAlgs = 1;
  JTextField nameField = new JTextField(18);
  JTextField cardField = new JTextField(6);
  JTextField simTypeField = new JTextField();
  JTextField descField = new JTextField();
  JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
  JLabel numAlgsLabel = new JLabel();
  //JTextArea descArea = new JTextArea();

  public AlgebraPreviewer(UACalc uacalc) {
    this.uacalc = uacalc;
    setLayout(new MigLayout());
    //setLayout(new BorderLayout());
    setPreferredSize(new Dimension(400, 300));
    //add(new JLabel("North:"), BorderLayout.NORTH);
    //add(new JLabel("Center:"), BorderLayout.CENTER);
    //add(nameField, BorderLayout.SOUTH);
    
    BoundedRangeModel brm = descField.getHorizontalVisibility();
    scrollBar.setModel(brm);
    
    add(new JLabel("name:"));
    add(nameField);
    add(new JLabel("card:"));
    add(cardField, "wrap");
    add(new JLabel("Sim Type:"));
    add(simTypeField, "grow, span, wrap");
    nameField.setText("testing");
    //add(descArea, "grow, span, wrap");
    add(descField, "grow, span, wrap");
    add(scrollBar, "grow, span, wrap");
    add(new JLabel("Num of Algs in File: "));
    add(numAlgsLabel);
  }
  
  private void clearTextFields() {
    final String emptyStr = "";
    nameField.setText(emptyStr);
    cardField.setText(emptyStr);
    simTypeField.setText(emptyStr);
    descField.setText(emptyStr);
    numAlgsLabel.setText(emptyStr);
  }


  public void propertyChange(PropertyChangeEvent e) {
    // TODO Auto-generated method stub
    String prop = e.getPropertyName();
    // If the directory changed, clear the preview
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      algebra = null;
      clearTextFields();
      //repaint();
      return;
    }
    if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      File file = (File) e.getNewValue();
      if (file != null) {
        algebra = null;
        clearTextFields();
        try {
          java.util.List<SmallAlgebra> algs = AlgebraIO.readAlgebraListFile(file);
          numAlgs = algs == null ? 0 : algs.size();
          if (numAlgs > 0) {
            algebra = algs.get(0);
            numAlgsLabel.setText("" + numAlgs);
          }
          else uacalc.beep();
        }
        catch (BadAlgebraFileException ex) {uacalc.beep(); }

        catch (IOException ex) {uacalc.beep(); }
      }
      if (algebra != null) {
        if (algebra.getName() != null) nameField.setText(algebra.getName());

        cardField.setText("" + algebra.cardinality());
        simTypeField.setText(algebra.similarityType().aritiesString());
        if (algebra.getDescription() != null) descField.setText(algebra.getDescription());
      }
      /*
      javax.swing.text.Document doc = new javax.swing.text.PlainDocument();
      try {
        doc.insertString(0, algebra.getDescription(), null);
      }
      catch (javax.swing.text.BadLocationException ex) {}
      descArea.setDocument(doc);
      descField.setText(algebra.getDescription());
      */
    }
  }
  
  /*
  public void paint(Graphics g) {
    if (false) {
      if (algebra == null) return;
      Graphics2D g2 = (Graphics2D)g;
      g2.clearRect(0, 0, getWidth(), getHeight());
      String desc = algebra.getDescription();
      if (desc != null) {
        g2.drawString(algebra.getDescription(), 20, 20);
      }
    }
    
  }
  */


}
