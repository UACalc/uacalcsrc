package org.uacalc.util;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.*;
import org.uacalc.ui.MonitorPanel;
//import org.uacalc.ui.tm.TaskRunner;
//import org.uacalc.ui.tm.DataChunk;
//import org.uacalc.ui.tm.DataChunk.DataType;
import org.uacalc.ui.tm.GuiExecutor;

public class ProgressMonitor {

  private boolean cancelled = false;
  private MonitorPanel monitorPanel;
  private JTextArea logArea;
  private JTextField passField;
  private JTextField sizeField;
  private int indent = 0;
  //private List<Long> times = new ArrayList<Long>();
  private Deque<Long> times = new ArrayDeque<Long>();
  
  public ProgressMonitor(MonitorPanel panel) {
    monitorPanel = panel;
    logArea = panel.getLogArea();
    sizeField = panel.getSizeField();
    passField = panel.getPassField();
  }
  
  public ProgressMonitor(JTextArea ta, JTextField sizeField, JTextField passField) {
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
  
  public void printToLog(final String s) {
    //TaskRunner runner = monitorPanel.getRunner();
    //System.out.println("runner = " + runner + ", s = " + s);
    //if (runner != null) {
    //  runner.publishx(new DataChunk(DataType.LOG, getIndentString() + s));
    //}
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        logArea.append(getIndentString() + s);
        logArea.setCaretPosition(logArea.getDocument().getLength());
      }
    });
    //logArea.append(s);
    //logArea.setCaretPosition(logArea.getDocument().getLength());
  }
  
  public void printlnToLog(String s) {
    printToLog(s + "\n");
  }
  
  public void printStart(String s) {
    //printIndent();
    printlnToLog(s);
    indent++;
    times.addFirst(System.currentTimeMillis());
    System.out.println("start: s = " + s + ", times.size(0 = " + times.size());
  }
  
  public void printEnd(String s) {
    System.out.println("end: s = " + s + ", times.size() = " + times.size());
    long time = System.currentTimeMillis() - times.removeFirst();
    indent--;
    //printIndent();
    printlnToLog(s + "  (" + time + " ms)");
  }
  
  public void setPassFieldText(final String s) {
    //TaskRunner runner = monitorPanel.getRunner();
    //if (runner != null && !runner.isCancelled()) {
    //  runner.publishx(new DataChunk(DataType.PASS, s));
    //}
    //passField.setText(s);
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        passField.setText(s);
      }
    });
  }
  
  public void setSizeFieldText(final String s) {
    //TaskRunner runner = monitorPanel.getRunner();
    //if (runner != null && !runner.isCancelled()) {
    //  runner.publishx(new DataChunk(DataType.SIZE, s));
    //}
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        sizeField.setText(s);
      }
    });
    //sizeField.setText(s);    
  }
  
  private String getIndentString() {
    final String two = "  ";
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < indent; i++) {
      sb.append(two);
    }
    return sb.toString();
  }
  
  private void printIndent() {
    final String two = "  ";
    for (int i = 0; i < indent; i++) {
      printToLog(two);
    }
  }
  
}
