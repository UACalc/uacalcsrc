package org.uacalc.ui.table;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.nbui.LatDrawer;
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
  
  public ConLatticeTableModel(SmallAlgebra alg, DataType t, org.latdraw.diagram.Diagram diagram) {
    this(alg, t);
    sortElemsByDiagram(diagram);
  }
  
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
  
  public void sortElemsByDiagram(final org.latdraw.diagram.Diagram diagram) {
    if (diagram == null) return;
    final org.latdraw.diagram.Vertex[] verts = diagram.getVertices();
    if (verts == null || !(verts.length == elems.size())) return;
    //System.out.println("before: " + elems);
    Collections.sort(elems, new Comparator<Partition>() {
        public int compare(Partition p0, Partition p1) {
          final int i0 = findIndex(p0, verts);
          final int i1 = findIndex(p1, verts);
          return i0 - i1;
        }
    });
    //System.out.println("after : " + elems);
    List vertsPar = new ArrayList();
    for (int i = 0; i < verts.length; i++) {
      vertsPar.add(verts[i].getUnderlyingObject());
    }
    //System.out.println("verts : " + vertsPar);
  }
  
  public int findIndex(Partition par, final org.latdraw.diagram.Vertex[] verts) {
    for (int i = 0; i < verts.length; i++) {
      if (verts[i].getUnderlyingObject().equals(par)) return i;
    }
    return -1;
  }
  
  public List<Partition> getElementList() { return elems; }
  
  public String getColumnName(int col) {
    return getColNames()[col];
  }
  
  public Class getColumnClass(int c) {
    if (c == 1 || c == 2) return Boolean.class;
    if (c == 3) return Integer.class;
    return String.class;
  }

  public int rowOfPartition(Partition part) {
    if (part == null || elems == null) return -1;
    int k = 0;
    for (Partition elem : elems) {
      if (part.equals(elem)) return k;
      k++;
    }
    return -1;
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
