package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;

import org.uacalc.ui.util.GUIAlgebra;

public class AlgebraTableModel extends AbstractTableModel {
  
  private final List<GUIAlgebra> algs = new ArrayList<GUIAlgebra>();

  public int getColumnCount() {
    return 4;
  }

  public int getRowCount() {
    return algs.size();
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    // TODO Auto-generated method stub
    return null;
  }

}
