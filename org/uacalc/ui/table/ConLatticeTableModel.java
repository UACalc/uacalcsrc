package org.uacalc.ui.table;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import java.util.*;

public final class ConLatticeTableModel extends LatticeTableModel {

  public static enum DataType { 
    ALL, PRINCIPALS, JOIN_IRREDUCIBLES
  }
  
  private DataType dType;
  
  private final int rowCount;
  
  private final List<Partition> elems;
  
  List<Partition> univ = null;
  List<Partition> principals = null;
  private final List<Partition> joinIrreds;
  private final Set<Partition> joinIrredsSet;
  
  public ConLatticeTableModel(SmallAlgebra alg, DataType t) {
    super(alg);
    dType = t;
    joinIrreds = alg.con().joinIrreducibles();
    joinIrredsSet = new HashSet<Partition>(joinIrreds);
    switch (t) {
      case ALL: elems = new ArrayList<Partition>(alg.con().universe());
                break;
      case PRINCIPALS: elems = alg.con().principals();  
                break;
      default: elems = alg.con().joinIrreducibles();
    }
    rowCount = elems.size();
    Collections.sort(elems);
  }
  
  @Override
  public int getRowCount() {
    return rowCount;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (colNames[columnIndex].equals("idx")) return rowIndex;
    Partition elem = elems.get(rowIndex);
    if (colNames[columnIndex].equals("elem")) return elem;
    if (colNames[columnIndex].equals("JI?")) {
      if (dType == DataType.JOIN_IRREDUCIBLES)return true;
      return getAlgebra().con().joinIrreducible(elem);
    }
    
    return null;
  }

}
