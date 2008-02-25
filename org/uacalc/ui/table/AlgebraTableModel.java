package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.alg.SmallAlgebra;

import org.uacalc.ui.util.GUIAlgebra;

public class AlgebraTableModel extends AbstractTableModel {
  
  private final List<GUIAlgebra> algs = new ArrayList<GUIAlgebra>();
  
  private static final String[] columnNames = new String[] {
    "Internal", "Name", "Type", "Description"};

  public void addAlgebra(SmallAlgebra alg) {
    addAlgebra(new GUIAlgebra(alg));
  }
  
  public void addAlgebra(GUIAlgebra alg) {
    algs.add(alg); 
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
    return 4;
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
    return gAlg.getAlgebra().description();
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }
  
  public boolean isCellEditable(int row, int col) { return false; }
  
}
