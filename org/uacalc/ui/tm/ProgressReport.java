package org.uacalc.ui.tm;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.util.*;
import org.uacalc.ui.MonitorPanel;
import org.uacalc.ui.table.TaskTableModel;


public class ProgressReport {

  private MonitorPanel monitorPanel;
  private JTextArea logArea;
  private JTextField passField;
  private JTextField passSizeField;
  private JTextField sizeField;
  private JTextField descField;
  
  private TaskTableModel taskTableModel;
  
  private volatile List<String> logLines = new ArrayList<String>();
  private volatile int pass;
  private volatile int passSize;
  private volatile int size;
  private volatile String desc = "";
  private volatile String timeLeft = "";
  private volatile String timeNext = "";
  
  private int indent = 0;
  //private List<Long> times = new ArrayList<Long>();
  private Deque<Long> times = new ArrayDeque<Long>();
  
  // the new version
  public ProgressReport(TaskTableModel taskTableModel, JTextArea logArea) {
    this.taskTableModel = taskTableModel;
    this.logArea = logArea;
  }
  
  // the old version depending on a MonitorPanel
  public ProgressReport(MonitorPanel panel) {
    monitorPanel = panel;
    logArea = panel.getLogArea();
    sizeField = panel.getSizeField();
    passField = panel.getPassField();
    passSizeField = panel.getPassSizeField();
    descField = panel.getDescriptionField();
    taskTableModel = monitorPanel.getTaskTableModel();
  }
  
  
  
  public TaskTableModel getTaskTableModel() {
    return taskTableModel;
  }
  
  public String getTimeLeft() { return timeLeft; }
  public void setTimeLeft(String time) {
    timeLeft = time;
    getTaskTableModel().fireTableDataChanged();
  }
  
  public String getTimeNext() { return timeNext; }
  public void setTimeNext(String time) {
    timeNext = time;
    getTaskTableModel().fireTableDataChanged();
  }
  
  public int getPass() { return pass; }
  
  public void setPass(int v) {
    pass = v;
    //if (monitorPanel.getProgressReport() == this) {
    //  setPassFieldText(String.valueOf(v));
    //  getTaskTableModel().fireTableDataChanged();
    //}
    getTaskTableModel().fireTableDataChanged();
  }
  
  public int getPassSize() { return passSize; }
  public void setPassSize(int v) {
    passSize = v;
    //if (monitorPanel.getProgressReport() == this) {
    //  setPassSizeFieldText(String.valueOf(v));
    //  getTaskTableModel().fireTableDataChanged();
    //}
    getTaskTableModel().fireTableDataChanged();
  }
  
  public int getSize() { return size; }
  
  public void setSize(int v) {
    size = v;
    //if (monitorPanel.getProgressReport() == this) {
    //  setSizeFieldText(String.valueOf(v));
    //  getTaskTableModel().fireTableDataChanged();
    //}
    getTaskTableModel().fireTableDataChanged(); 
  }
  
  public String getDescription() { return desc; }

  public void setDescription(String v) {
    desc = v;
    //if (monitorPanel.getProgressReport() == this) {
    //  setDescFieldText(String.valueOf(v));
    //}
    getTaskTableModel().fireTableDataChanged();
  }
  
  public List<String> getLogLines() { return logLines; }
  
  public void setLogLines(List<String> v) {
    logLines = v;
  }
  
  public void addLine(final String line) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        final String str = getIndentString() + line;
        logLines.add(str);
        conditionalAppend(str);
      }
    });
  }
  
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
    if (taskTableModel.getCurrentTask().getProgressReport() == ProgressReport.this) {
      appendToLogArea(str + "\n");
    }
  }
  
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
    //System.out.println("s = " + s + ", indent = " + indent);
    logArea.append(getIndentString() + s);
    int pos = logArea.getDocument().getLength();
    //System.out.println("pos = " + pos);
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
  
  public void appendToLogArea(final String s) {
    GuiExecutor.instance().execute(new Runnable() {
      public void run() {
        logArea.append(s);
      }
    });
  }
  
  private String getIndentString() {
    final String two = "  ";
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < indent; i++) {
      sb.append(two);
    }
    return sb.toString();
  }
  
}
