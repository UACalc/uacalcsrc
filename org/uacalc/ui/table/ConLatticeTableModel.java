package org.uacalc.ui.table;

import org.uacalc.alg.*;

public class ConLatticeTableModel extends LatticeTableModel {

  public static enum dataType { 
    ALL, JOIN_IRREDUCIBLES, PRINCIPALS
  }
  
  public ConLatticeTableModel(SmallAlgebra alg) {
    super(alg);
  }
  
  @Override
  public int getRowCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    // TODO Auto-generated method stub
    return null;
  }

}
