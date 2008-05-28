package org.uacalc.nbui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.prefs.*;

import org.uacalc.alg.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.Operations;
import org.uacalc.lat.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;
import org.uacalc.io.*;
import org.uacalc.ui.MonitorPanel;
import org.uacalc.ui.table.AlgebraTablePanel;
import org.uacalc.ui.tm.ProgressReport;


public class Actions {

  private boolean dirty = false;
  private UACalculatorUI uacalcUI;
  private SmallAlgebra currentAalgebra;  // Small ??
  private java.util.List<SmallAlgebra> algebraList = new ArrayList<SmallAlgebra>();
  private File currentFile;
  private String title = "";  // if currentFile is null this might be "New"
  private String progName = "UACalculator   ";
  private String currentFolder;
  
  public Actions(UACalculatorUI uacalcUI) {
    this.uacalcUI = uacalcUI;
  }
  
  public boolean isDirty() { return dirty; }
  public void setDirty(boolean v) { dirty = v; }
  
  public File getCurrentFile() { return currentFile; }
  public void setCurrentFile(File f) { currentFile = f; }
  
  /*
  public void setCurrentAlgebra(SmallAlgebra alg) {
    if (isDirty()) checkSave();
    updateCurrentAlgebra(alg);
    getAlgebraEditor().setAlgebra(alg);
  }
  
  public Random getRandom() {
    return random;
  }
  
  public void setRandomSeed(long seed) {
    random.setSeed(seed);
  }
  */
  
  public void quit() {
    
  }
  
  public SmallAlgebra getCurrentAlgebra() { return currentAalgebra; }
  
  /*
  public boolean save() throws IOException {
    if (getCurrentAlgebra() == null) return true;
    //if (!getAlgebraEditor().sync()) return false; TODO: get this functionality back
    File f = getCurrentFile();
    if (f == null) return saveAs(org.uacalc.io.ExtFileFilter.UA_EXT);
    String ext = ExtFileFilter.getExtension(f);
    boolean newFormat = true;
    if (ext.equals(ExtFileFilter.ALG_EXT)) newFormat = false;
    AlgebraIO.writeAlgebraFile(getCurrentAlgebra(), f, !newFormat);
    setDirty(false);
    return true;
  }
  */
  
  public boolean save() throws IOException {
    return true;
  }
  
  public boolean checkSave() {
    Object[] options = {"Save", "Discard", "Cancel"};
    int n = JOptionPane.showOptionDialog(uacalcUI,
                                         "Do you want to save your algebra?",
                                         "Save Your Algebra?",
                                         JOptionPane.YES_NO_CANCEL_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         options,
                                         options[0]);
    if (n == JOptionPane.YES_OPTION) {
         try {
           return save();
         }
         catch (IOException ex) {
           System.err.println("IO error in saving: " + ex.getMessage());
           //setUserWarning("Error 187. File Not Saved. There is "
           //               + "something wrong with your file.");
         }
         return false;
    }
    else if (n == JOptionPane.NO_OPTION) {
      return true;
    }
    else {
      return false;
    }
 }
  
}
