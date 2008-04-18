package org.uacalc.ui.tm;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.*;
import org.uacalc.ui.MonitorPanel;
//import org.uacalc.ui.tm.TaskRunner;
//import org.uacalc.ui.tm.DataChunk;
//import org.uacalc.ui.tm.DataChunk.DataType;

public class ProgressReport {

  //private boolean cancelled = false;
  private MonitorPanel monitorPanel;
  private JTextArea logArea;
  private JTextField passField;
  private JTextField passSizeField;
  private JTextField sizeField;
  private JTextField descField;
  
  private volatile List<String> logLines = new ArrayList<String>();
  private volatile int pass;
  private volatile int passSize;
  private volatile int size;
  private volatile String desc = "";
  
  private int indent = 0;
  //private List<Long> times = new ArrayList<Long>();
  private Deque<Long> times = new ArrayDeque<Long>();
  
  public ProgressReport(MonitorPanel panel) {
    monitorPanel = panel;
    logArea = panel.getLogArea();
    sizeField = panel.getSizeField();
    passField = panel.getPassField();
    passSizeField = panel.getPassSizeField();
    descField = panel.getDescriptionField();
  }
  
  public int getPass() { return pass; }
  public void setPass(int v) {
    pass = v;
    
    if (monitorPanel.getProgressModel() == this) {
      setPassFieldText(String.valueOf(v));
    }
  }
  
  public int getPassSize() { return passSize; }
  public void setPassSize(int v) {
    passSize = v;
    if (monitorPanel.getProgressModel() == this) {
      setPassSizeFieldText(String.valueOf(v));
    }
  }
  
  public int getSize() { return size; }
  
  public void setSize(int v) {
    size = v;
    if (monitorPanel.getProgressModel() == this) {
      setSizeFieldText(String.valueOf(v));
    }
  }
  
  public String getDescription() { return desc; }

  public void setDescription(String v) {
    desc = v;
    if (monitorPanel.getProgressModel() == this) {
      setDescFieldText(String.valueOf(v));
    }
  }
  
  public List<String> getLogLines() { return logLines; }
  
  public void setLogLines(List<String> v) {
    logLines = v;
  }
  
  //public void addLine(String line) {
  //  logLines.add(line);
  //}
  
  public void addStartLine(final String line) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        final String str = getIndentString() + line;
        logLines.add(str);
        indent++;
        times.addFirst(System.currentTimeMillis());
        conditionalAppend(str);
      }
    });
  }
  
  public void addEndingLine(final String line) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {  
        long time = System.currentTimeMillis() - times.removeFirst();
        indent--;
        final String str = getIndentString() + line + "  (" + time + " ms)";
        logLines.add(str);
        conditionalAppend(str);
      }
    });
  }
  
  private void conditionalAppend(String str) {
    System.out.println("str = " + str);
    System.out.println("monitorPanel.getProgressModel() == ProgressModel.this is " 
        + (monitorPanel.getProgressModel() == ProgressReport.this));
    if (monitorPanel.getProgressModel() == ProgressReport.this) {
      logArea.append(str + "\n");
    }
  }
  
  
  
  //public void clearLines() { 
  //  logLines = new ArrayList<String>(); 
  //}
  
  //public void clearAll() {
  //  clearLines();
  //  desc = "";
  //  setPass(0);
  //  setPassSize(0);
  //  setSize(0);
  //}

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
  
  // delete most of these
  private void printToLog(final String s) {
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
