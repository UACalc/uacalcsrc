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
  JTextField nameField = new JTextField(18);
  JTextField cardField = new JTextField(6);
  JTextField simTypeField = new JTextField();
  JTextField descField = new JTextField();
  JTextArea descArea = new JTextArea();

  public AlgebraPreviewer() {
    this.uacalc = uacalc;
    setLayout(new MigLayout());
    //setLayout(new BorderLayout());
    setPreferredSize(new Dimension(400, 300));
    //add(new JLabel("North:"), BorderLayout.NORTH);
    //add(new JLabel("Center:"), BorderLayout.CENTER);
    //add(nameField, BorderLayout.SOUTH);
    
    add(new JLabel("name:"));
    add(nameField);
    add(new JLabel("card:"));
    add(cardField, "wrap");
    add(new JLabel("Sim Type:"));
    add(simTypeField, "grow, span, wrap");
    nameField.setText("testing");
    //add(descArea, "grow, span, wrap");
    add(descField, "grow, span, wrap");
    
    //revalidate();
    //setVisible(true);
    //fc.addPropertyChangeListener(this);

  
  }


  public void propertyChange(PropertyChangeEvent e) {
    // TODO Auto-generated method stub
    String prop = e.getPropertyName();
    // If the directory changed, clear the preview
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      algebra = null;
      //repaint();
      return;
    }
    if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      File file = (File) e.getNewValue();
      if (file != null) {
        algebra = null;
        try {
          algebra = AlgebraIO.readAlgebraFile(file);
          // TODO: add to list of algs
        }
        catch (BadAlgebraFileException ex) {uacalc.beep(); }

        catch (IOException ex) {uacalc.beep(); }
      }
      nameField.setText(algebra.getName());
      cardField.setText("" + algebra.cardinality());
      simTypeField.setText(algebra.similarityType().aritiesString());
      descField.setText(algebra.getDescription());

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
