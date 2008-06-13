package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.io.*;
import org.uacalc.alg.SmallAlgebra;

import org.uacalc.ui.util.*;;

public class AlgebraTableModel extends AbstractTableModel {
  
  //private final List<GUIAlgebra> algs = new ArrayList<GUIAlgebra>();
  private final GUIAlgebraList algebraList = new GUIAlgebraList();
  
  private static final String[] columnNames = new String[] {
    "Internal", "Name", "Type", "Description", "File"};
  
  public void addAlgebra(SmallAlgebra alg) {
    getAlgebraList().add(alg);
  }

  public void addAlgebra(SmallAlgebra alg, File file) {
    getAlgebraList().add(alg, file);
  }
  
  public void addAlgebra(GUIAlgebra alg) {
    getAlgebraList().add(alg);
  }
  
  public void removeAlgebra(GUIAlgebra alg) {
    getAlgebraList().removeAlgebra(alg);
  }
  
  public int getColumnCount() {
    return 5;
  }

  public int getRowCount() {
    return getAlgebraList().size();
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    System.out.println("getValueAt called, row " + rowIndex);
    // TODO Auto-generated method stub
    GUIAlgebra gAlg = getAlgebraList().get(rowIndex);
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

  public GUIAlgebraList getAlgebraList() {
    return algebraList;
  }
  
}
