package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.io.*;
import org.uacalc.alg.SmallAlgebra;

import org.uacalc.ui.util.GUIAlgebra;

public class AlgebraTableModel extends AbstractTableModel {
  
  private final List<GUIAlgebra> algs = new ArrayList<GUIAlgebra>();
  
  private static final String[] columnNames = new String[] {
    "Internal", "Name", "Type", "Description", "File"};
  
  public void addAlgebra(SmallAlgebra alg) {
    addAlgebra(alg, null);
  }

  public void addAlgebra(SmallAlgebra alg, File file) {
    addAlgebra(new GUIAlgebra(alg, file));
  }
  
  public void addAlgebra(GUIAlgebra alg) {
    algs.add(alg);
    fireTableDataChanged();
  }
  
  public void removeAlgebra(GUIAlgebra alg) {
    int index = findIndex(alg);
    if (index != -1) algs.remove(index);
  }
  
  private int findIndex(GUIAlgebra alg) {
    int index = 0;
    for (GUIAlgebra alg2 : algs) {
      if (alg2.equals(alg)) return index;
      index++;
    }
    return -1;
  }
  
  public int getColumnCount() {
    return 5;
  }

  public int getRowCount() {
    return algs.size();
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    // TODO Auto-generated method stub
    GUIAlgebra gAlg = algs.get(rowIndex);
    if (columnIndex == 0) return "A" + gAlg.getSerial();
    if (columnIndex == 1) return gAlg.getAlgebra().name();
    if (columnIndex == 2) return gAlg.getAlgebra().algebraType();
    if (columnIndex == 3) return gAlg.getAlgebra().description();
    //System.out.println("file: " + gAlg.getFile());
    if (gAlg.getFile() != null) return  gAlg.getFile().getName();
    return null;
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }
  
  public boolean isCellEditable(int row, int col) { return false; }
  
}
