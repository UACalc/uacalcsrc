package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.terms.*;

public class TermTableModel extends AbstractTableModel {
  
  //private List<Term> terms = new ArrayList<Term>();
  private Term[] terms;
  private Variable[] variables;
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
    return 2;
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
    return "Term";
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) return rowIndex;
    return terms[rowIndex];
  }

  public boolean isCellEditable(int row, int col) { return false; }
  
}
