package org.uacalc.ui.tm;

import java.util.*;

/**
 * This will hold the state needed by the ProgressMonitor.
 * Things like pass, passSize and the whole log document.
 * Since this is a model for ProgressMonitor, it should only
 * be accessed through the EventThread.
 * 
 * @author ralph
 *
 */
public class ProgressState {
  
  private List<String> logLines = new ArrayList<String>();
  private int pass;
  private int passSize;
  private int size;
  private String desc;
  
  public int getPass() { return pass; }
  public void setPass(int v) { pass = v; }
  public int getPassSize() { return passSize; }
  public void setPassSize(int v) { passSize = v; }
  public int getSize() { return size; }
  public void setSize(int v) { size = v; }
  public String getDescription() { return desc; }
  public void setDescription(String v) { desc = v; }
  public List<String> getLogLines() { return logLines; }
  public void setLogLines(List<String> v) { logLines = v; }
  
  public void addLogLine(String line) {
    logLines.add(line);
  }
  
  public void clearLogLines() { 
    logLines = new ArrayList<String>(); 
  }
  
  public void clearAll() {
    clearLogLines();
    desc = "";
    setPass(0);
    setPassSize(0);
    setSize(0);
  }
  
}
