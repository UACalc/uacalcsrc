package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.ui.tm.*;
import org.uacalc.ui.*;

public class TaskTableModel extends AbstractTableModel {

  //MonitorPanel mp;
  private List<BackgroundTask<?>> tasks = new ArrayList<BackgroundTask<?>> ();
  private BackgroundTask<?> currentTask;
  
  private String[] colNames = new String[] {" ", "Description", "Pass", 
      "Pass Size", "Size", "Time Left For Pass", "Status"};
  
  public TaskTableModel() {}
  
  /**
   * This constructor is for the old UI.
   * 
   * @param mp
   */
  public TaskTableModel(MonitorPanel mp) {
    tasks = mp.getTaskList();
    //this.mp = mp;
  }
  
  public void setCurrentTask(int index) {
    currentTask = tasks.get(index);
  }
  
  public int getColumnCount() {
    return colNames.length;
  }

  public int getRowCount() {
    return getTasks().size();
  }

  public boolean isCellEditable(int row, int col) { return false; }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    BackgroundTask<?> task = getTasks().get(rowIndex);
    ProgressReport prog = task.getProgressReport();
    if (columnIndex == 0) return currentTask == task ? "  ->" : "";
    if (columnIndex == 1) return prog.getDescription();
    if (columnIndex == 2) return prog.getPass();
    if (columnIndex == 3) return prog.getPassSize();
    if (columnIndex == 4) return prog.getSize();
    if (columnIndex == 5) return null; //TODO put a number or progress bar
    if (columnIndex == 6) return task.getStatus();
    return null;
  }

  public String getColumnName(int col) {
    return colNames[col];
  }
  
  public BackgroundTask<?> getCurrentTask() { return currentTask; }
  
  public void setCurrentTask(BackgroundTask<?> v) { 
    currentTask = v; //highlight its row
  }
  
  public void addTask(BackgroundTask<?> task) {
    addTask(task, true);
    fireTableDataChanged();
    
  }
  
  public void addTask(BackgroundTask<?> task, boolean makecurrent) {
    tasks.add(task);
    if (makecurrent) setCurrentTask(task);
    fireTableDataChanged();
  }
  
  public void removeTask(BackgroundTask<?> task) {
    tasks.remove(task);
    if (tasks.size() > 0) setCurrentTask(tasks.get(tasks.size() - 1));
    fireTableDataChanged();
  }
  
  public List<BackgroundTask<?>> getTasks() { return tasks; }
  
  public int index(BackgroundTask<?> task) {
    for (int i = 0; i < tasks.size(); i++ ) {
      if (task == tasks.get(i)) return i;
    }
    return -1;
  }
  
}
