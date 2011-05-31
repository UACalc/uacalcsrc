package org.uacalc.nbui;

import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import org.uacalc.alg.*;
import org.uacalc.io.*;

import javax.swing.JPanel;

public class AlgebraPreviewer extends JPanel implements PropertyChangeListener {
  
  UACalc uacalc;
  Algebra algebra;

  public AlgebraPreviewer(UACalc uacalc, JFileChooser fc) {
    this.uacalc = uacalc;
    setPreferredSize(new Dimension(300, 300));
    fc.addPropertyChangeListener(this);

  
  }


  public void propertyChange(PropertyChangeEvent e) {
    // TODO Auto-generated method stub
    String prop = e.getPropertyName();
    // If the directory changed, clear the preview
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      algebra = null;
      repaint();
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
    }
    repaint();
  }
  
  public void paint(Graphics g) {
    if (algebra == null) return;
    Graphics2D g2 = (Graphics2D)g;
    g2.clearRect(0, 0, getWidth(), getHeight());
    String desc = algebra.getDescription();
    if (desc != null) {
      g2.drawString(algebra.getDescription(), 20, 20);
    }
    
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
