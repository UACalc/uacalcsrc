package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.terms.Term;

public class TermTableModel extends AbstractTableModel {
  
  //private List<Term> terms = new ArrayList<Term>();
  private Term[] terms;

  public TermTableModel(Term[] terms) {
    this.terms = terms;
  }
  
  public void setTerms(Term[] lst) { terms = lst; }
  
  public int getColumnCount() {
    // TODO Auto-generated method stub
    return 2;
  }
  
/*    This causes an error
  public Class getColumnClass(int col) {
    if (col == 0) return Integer.class;
    return Term.class;
  }
*/
  
  public int getRowCount() {
    // TODO Auto-generated method stub
    return terms.length;
  }
  
  public String getColumnName(int col) {
    if (col == 0) return "Index";
    return "Term";
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) return rowIndex;
    return terms[rowIndex];
  }

  public boolean isCellEditable(int row, int col) { return false; }
  
}
