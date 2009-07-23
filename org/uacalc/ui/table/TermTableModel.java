package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.terms.*;
import org.uacalc.util.*;

public class TermTableModel extends AbstractTableModel {
  
  //private List<Term> terms = new ArrayList<Term>();
  private Term[] terms;
  private Variable[] variables;
  private List<IntArray> universeList;
  private String description;

  public TermTableModel() {
    this(new Term[0]);
  }
  
  public TermTableModel(Term[] terms) {
    this.terms = terms;
  }
  
  public TermTableModel(Term[] terms, Variable[] variables) {
    this.terms = terms;
    this.variables = variables;
  }
  
  public void setTerms(List<Term> termList) {
    final int n = termList.size();
    terms = new Term[n];
    for (int i = 0; i < n; i++) {
      terms[i] = termList.get(i);
    }
    fireTableDataChanged();
  }
  
  public void setVariables(List<Variable> varList) {
    final int n = varList.size();
    variables = new Variable[n];
    for (int i = 0; i < n; i++) {
      variables[i] = varList.get(i);
    }
    fireTableDataChanged();
  }
  
  public void setUniverse(List<IntArray> univ) {
    //System.out.println("setUniverse called");
    universeList = univ;
    //System.out.println("col count = " + getColumnCount());
    //System.out.println("univ size = " + univ.size());
    //System.out.println("univ: " + univ);
    fireTableDataChanged();
  }
  
  /**
   * For the description text field (not really part of the table model).
   */
  public void setDescription(String desc) { description = desc; }
  
  /**
   * For the description text field (not really part of the table model).
   */
  public String getDescription() { return description; }
  
  public void setTerms(Term[] lst) { terms = lst; }
  
  public Term[] getTerms() { return terms; }
  public Variable[] getVariables() { return variables; }
  
  public int getColumnCount() {
    if (universeList == null  || universeList.size() == 0) return 2;
    return 2 + universeList.get(0).universeSize();
  }
  
/*    This causes an error 
  public Class getColumnClass(int col) {
    if (col == 0) return Integer.class;
    return Term.class;
  }
*/
  
  public int getRowCount() {
    return terms.length;
  }
  
  public String getColumnName(int col) {
    if (col == 0) return "Index";
    if (col == 1) return "Term";
    return String.valueOf(col - 2);
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) return rowIndex;
    if (columnIndex == 1) return terms[rowIndex];
    return universeList.get(rowIndex).get(columnIndex - 2);
  }

  public boolean isCellEditable(int row, int col) { return false; }
  
}
