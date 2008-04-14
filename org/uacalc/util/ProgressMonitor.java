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

  //private boolean cancelled = false;
  private MonitorPanel monitorPanel;
  private JTextArea logArea;
  private JTextField passField;
  private JTextField passSizeField;
  private JTextField sizeField;
  private JTextField descField;
  
  private int indent = 0;
  //private List<Long> times = new ArrayList<Long>();
  private Deque<Long> times = new ArrayDeque<Long>();
  

  public ProgressMonitor(MonitorPanel panel) {
    monitorPanel = panel;
    logArea = panel.getLogArea();
    sizeField = panel.getSizeField();
    passField = panel.getPassField();
    passSizeField = panel.getPassSizeField();
    descField = panel.getDescriptionField();
  }
  
  /*
  public ProgressMonitor(JTextArea ta, JTextField sizeField, JTextField passField) {
    this.logArea = ta;
    this.sizeField = sizeField;
    this.passField = passField;
  }
  */
  
  //public boolean isCancelled() { return cancelled; }
  
  //public void setCancelled(boolean v) { cancelled = v; }
  
  //public void cancel() { cancelled = true; }
  
  public void reset() {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        resetAux();
      }
    });
  }
  
  private void resetAux() {
    indent = 0;
    times = new ArrayDeque<Long>();
  }
  
  private void printToLogAux(final String s) {
    System.out.println("s = " + s + ", indent = " + indent);
    logArea.append(getIndentString() + s);
    int pos = logArea.getDocument().getLength();
    System.out.println("pos = " + pos);
    logArea.setCaretPosition(logArea.getDocument().getLength());
  }
  
  public void printToLog(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        printToLogAux(s);
      }
    });
  }
  
  private void printlnToLogAux(String s) {
    printToLog(s + "\n");
  }
  
  public void printlnToLog(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        printlnToLogAux(s);
      }
    });
  }
  
  public void printStart(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        printlnToLogAux(s);
        indent++;
        times.addFirst(System.currentTimeMillis());
        System.out.println("start: s = " + s + ", times.size(0 = " + times.size() + ", indent = " + indent);
      }
    });
  }
  
  public void printEnd(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        System.out.println("end: s = " + s + ", times.size() = " + times.size());
        long time = System.currentTimeMillis() - times.removeFirst();
        indent--;
        printlnToLogAux(s + "  (" + time + " ms)");
        System.out.println("printEnd: indent = " + indent);
      }
    });
  }
  
  public void setDescFieldText(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        descField.setText(s);
      }
    });
  }
  
  public void setPassFieldText(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        passField.setText(s);
      }
    });
  }
  
  public void setPassSizeFieldText(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        System.out.println("pass size, s = " + s);
        passSizeField.setText(s);
      }
    });
  }
  
  public void setSizeFieldText(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        sizeField.setText(s);
      }
    });
  }
  
  private String getIndentString() {
    final String two = "  ";
    StringBuffer sb = new StringBuffer();
    System.out.println("indent = " + indent);
    for (int i = 0; i < indent; i++) {
      sb.append(two);
    }
    return sb.toString();
  }
  
}
