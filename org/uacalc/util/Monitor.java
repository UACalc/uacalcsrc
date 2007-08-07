package org.uacalc.util;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Monitor {

  private boolean cancelled = false;
  private JTextArea logArea;
  private JTextField passField;
  private JTextField sizeField;
  
  public Monitor(JTextArea ta,JTextField sizeField, JTextField passField) {
    this.logArea = ta;
    this.sizeField = sizeField;
    this.passField = passField;
  }
  
  public boolean isCancelled() { return cancelled; }
  
  public void setCancelled(boolean v) { cancelled = v; }
  
  public void cancel() { cancelled = true; }
  
  public void printToLog(String s) {
    logArea.append(s);
    logArea.setCaretPosition(logArea.getDocument().getLength());
  }
  
  public void printlnToLog(String s) {
    printToLog(s + "\n");
  }
  
  public void setPassFieldText(String s) {
    passField.setText(s);    
  }
  
  public void setSizeFieldText(String s) {
    sizeField.setText(s);    
  }
  
}
