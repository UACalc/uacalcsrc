package org.uacalc.util;

import javax.swing.JTextArea;

public class Monitor {

  private boolean cancelled = false;
  private JTextArea textArea;
  
  public Monitor(JTextArea ta) {
    this.textArea = ta;
  }
  
  public boolean isCancelled() { return cancelled; }
  
  public void setCancelled(boolean v) { cancelled = v; }
  
  public void cancel() { cancelled = true; }
  
  public void print(String s) {
    textArea.append(s);
  }
  
  public void println(String s) {
    textArea.append(s + "\n");
  }
  
}
