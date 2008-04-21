package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.ui.tm.*;

public class TaskTableModel extends AbstractTableModel {

  private List<BackgroundTask> tasks;
  
  private String[] colNames = new String[] {"Description", "Pass", 
      "Pass Size", "Status"};
  
  public int getColumnCount() {
    return 5;
  }

  public int getRowCount() {
    return tasks.size();
  }

  public boolean isCellEditable(int row, int col) { return false; }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) return tasks.get(rowIndex).getProgressReport().getDescription();
    if (columnIndex == 1) return tasks.get(rowIndex).getProgressReport().getPass();
    if (columnIndex == 0) return tasks.get(rowIndex).getProgressReport().getPassSize();
    if (columnIndex == 0) return tasks.get(rowIndex).getProgressReport().getSize();
    if (columnIndex == 0) return tasks.get(rowIndex).getStatus();
    return null;
  }

  public String getColumnName(int col) {
    return colNames[col];
  }
}
