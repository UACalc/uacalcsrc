package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import org.uacalc.alg.*;

/**
 * The super class for congruence, sublattice, and algebra as a lattice tables.
 * 
 * @author ralph
 *
 */
public abstract class LatticeTableModel extends AbstractTableModel {

  // \u2191 is the up arrow and \u2193 is the down arrow;
  // see http://www.alanwood.net/unicode/arrows.html
  private static final String[] allColNames = {
    "idx", "JI", "MI", "Typ\u2193", "Typ\u2191",
    "cov\u2193", "cov\u2191", "elem"
  };
  
  private static final String[] allNoTypColNames = {
    "idx", "JI", "MI", "cov\u2193", "cov\u2191", "elem"
  };
  
  private static final String[] jiColNames = {
    "idx", "Typ\u2193", "Typ\u2191", "elem"
  };
  
  private static final String[] jiNoTypColNames = {
    "idx", "elem"
  };
  
  private String[] colNames = allColNames;  // fix this
  
  private SmallAlgebra algebra;
  
  public LatticeTableModel(SmallAlgebra alg) {
    this.algebra = alg;
  }
  
  public String[] getColNames() { return colNames; }
  
  protected void setColNames(String[] colNames) {
    this.colNames = colNames;
  }
  
  public void setToAllNoTypColNames() { setColNames(allNoTypColNames); }
  
  public void setToJIColNames() { setColNames(jiColNames); }
  
  public void setToJINoTypColNames() { setColNames(jiNoTypColNames); }
  
  public void setToAllColNames() { setColNames(allColNames); }
  
 
  
  
  @Override
  public int getColumnCount() {
    return getColNames().length;
  }


  public static String[] getAllColNames() {
    return allColNames;
  }

  public static String[] getAllNoTypColNames() {
    return allNoTypColNames;
  }

  public static String[] getJIColNames() {
    return jiColNames;
  }

  public static String[] getJINoTypColNames() {
    return jiNoTypColNames;
  }

  public void setAlgebra(SmallAlgebra algebra) {
    this.algebra = algebra;
  }

  public SmallAlgebra getAlgebra() {
    return algebra;
  }

  

}
