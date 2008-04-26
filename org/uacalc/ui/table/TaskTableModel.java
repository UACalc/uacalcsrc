package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.ui.tm.*;
import org.uacalc.ui.*;

public class TaskTableModel extends AbstractTableModel {

  MonitorPanel mp;
  private List<BackgroundTask<?>> tasks = new ArrayList<BackgroundTask<?>> ();
  private BackgroundTask<?> currentTask;
  
  private String[] colNames = new String[] {"Description", "Pass", 
      "Pass Size", "Size", "Status"};
  
  public TaskTableModel(MonitorPanel mp) {
    this.mp = mp;
  }
  
  public int getColumnCount() {
    return colNames.length;
  }

  public int getRowCount() {
    return mp.getTaskList().size();
  }

  public boolean isCellEditable(int row, int col) { return false; }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    BackgroundTask<?> task = mp.getTaskList().get(rowIndex);
    ProgressReport prog = task.getProgressReport();
    if (columnIndex == 0) return prog.getDescription();
    if (columnIndex == 1) return prog.getPass();
    if (columnIndex == 2) return prog.getPassSize();
    if (columnIndex == 3) return prog.getSize();
    if (columnIndex == 4) return task.getStatus();
    return null;
  }

  public String getColumnName(int col) {
    return colNames[col];
  }
  
  public BackgroundTask<?> getCurrentTask() { return currentTask; }
  
  public void setCurrrentTask(BackgroundTask<?> v) { 
    currentTask = v; //highlight its row
  }
  
  public void addTask(BackgroundTask<?> task) {
    addTask(task, true);
    fireTableDataChanged();
  }
  
  public void addTask(BackgroundTask<?> task, boolean makecurrent) {
    tasks.add(task);
    if (makecurrent) setCurrrentTask(task);
  }
  
  public List<BackgroundTask<?>> getTasks() { return tasks; }
  
  
}
