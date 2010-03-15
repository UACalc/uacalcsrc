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
    if (alg != null) {
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
    else {
      rowCount = 0;
      elems = null;
      joinIrreds = null;
      joinIrredsSet = null;
    }
  }
  
  
  public String getColumnName(int col) {
    return getColNames()[col];
  }
  
  public Class getColumnClass(int c) {
    if (c == 1 || c == 2) return Boolean.class;
    if (c == 3) return Integer.class;
    return String.class;
  }

  
  @Override
  public int getRowCount() {
    return rowCount;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (getColNames()[columnIndex].equals("idx")) return rowIndex;
    final Partition elem = elems.get(rowIndex);
    final CongruenceLattice con = getAlgebra().con();
    if (getColNames()[columnIndex].equals("elem")) return elem;
    if (getColNames()[columnIndex].equals("JI")) {
      if (dType == DataType.JOIN_IRREDUCIBLES)return true;
      return con.joinIrreducible(elem);
    }
    if (getColNames()[columnIndex].equals("MI")) return con.meetIrreducible(elem);
    if (getColNames()[columnIndex].equals("Typ\u2193")) {
      if (!con.joinIrreducible(elem)) return null;
      return con.type(elem); 
    }
    return null;
  }

}
