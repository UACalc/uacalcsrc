package org.uacalc.util;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.*;

public class Monitor {

  private boolean cancelled = false;
  private JTextArea logArea;
  private JTextField passField;
  private JTextField sizeField;
  private int indent = 0;
  //private List<Long> times = new ArrayList<Long>();
  private Deque<Long> times = new ArrayDeque<Long>();
  
  public Monitor(JTextArea ta, JTextField sizeField, JTextField passField) {
    this.logArea = ta;
    this.sizeField = sizeField;
    this.passField = passField;
  }
  
  public boolean isCancelled() { return cancelled; }
  
  public void setCancelled(boolean v) { cancelled = v; }
  
  public void cancel() { cancelled = true; }
  
  public void reset() {
    resetIndent();
    setCancelled(false);
    times = new ArrayDeque<Long>();
  }
  
  public void resetIndent() { indent = 0; }
  
  public void printToLog(String s) {
    logArea.append(s);
    logArea.setCaretPosition(logArea.getDocument().getLength());
  }
  
  public void printlnToLog(String s) {
    printToLog(s + "\n");
  }
  
  public void printStart(String s) {
    printIndent();
    printlnToLog(s);
    indent++;
    times.addFirst(System.currentTimeMillis());
  }
  
  public void printEnd(String s) {
    long time = System.currentTimeMillis() - times.removeFirst();
    indent--;
    printIndent();
    printlnToLog(s + "  (" + time + " ms)");
  }
  
  public void setPassFieldText(String s) {
    passField.setText(s);    
  }
  
  public void setSizeFieldText(String s) {
    sizeField.setText(s);    
  }
  
  private void printIndent() {
    final String two = "  ";
    for (int i = 0; i < indent; i++) {
      printToLog(two);
    }
  }
  
}
