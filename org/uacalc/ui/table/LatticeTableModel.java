package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;

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
    "idx", "JI?", "MI?", "Typ\u2193", "Typ\u2191",
    "cov\u2193", "cov\u2191", "elem"
  };
  
  private static final String[] allNoTypColNames = {
    "idx", "JI?", "MI?", "cov\u2193", "cov\u2191", "elem"
  };
  
  private static final String[] jiColNames = {
    "idx", "Typ\u2193", "Typ\u2191", "elem"
  };
  
  private static final String[] jiNoTypColNames = {
    "idx", "elem"
  };
  
  private String[] colNames;
  
  public String[] getColNames() { return colNames; }
  
  public void setToAllNoTypColNames() { colNames = allNoTypColNames; }
  
  public void setToJIColNames() { colNames = jiColNames; }
  
  public void setToJINoTypColNames() { colNames = jiNoTypColNames; }
  
  public void setToAllColNames() { colNames = allColNames; }
  
  
  
  
  @Override
  public int getColumnCount() {
    return colNames.length;
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

}
