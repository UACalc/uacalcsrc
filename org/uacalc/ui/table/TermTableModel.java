package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.terms.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.util.*;

public class TermTableModel extends AbstractTableModel {
  
  public static enum ResultTableType { TERM_LIST, CENTRALITY }
  
  //private List<Term> terms = new ArrayList<Term>();
  private Term[] terms;
  private Variable[] variables;
  private List<IntArray> universeList;
  private List<Partition> congrList;
  private List<CentralityData> centralityList;
  private String description;
  private ResultTableType type;

  public TermTableModel() {
    this(new Term[0]);
  }
  
  public TermTableModel(ResultTableType t){
    type = t;
    if (t.equals(ResultTableType.TERM_LIST)) terms = new Term[0];
  }
  
  public TermTableModel(Term[] terms) {
    this.terms = terms;
    type = ResultTableType.TERM_LIST;
  }
  
  public TermTableModel(Term[] terms, Variable[] variables) {
    this(terms);
    this.variables = variables;
  }
  
  public ResultTableType getType() { return type; }
  
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
  
  public void setCentralityList(List<CentralityData> lst) {
    centralityList = lst;
    fireTableDataChanged();
  }
  
  public void setCongruenceList(List<Partition> lst) {
    congrList = lst;
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
    if (centralityList != null) return 7;
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
    if (centralityList != null) return centralityList.size();
    if (terms == null) return 0;
    return terms.length;
  }
  
  public String getColumnName(int col) {
    if (col == 0) return "Index";
    if (centralityList != null) {
      if (col == 1) return "delta";
      if (col == 2) return "WC";
      if (col == 3) return "C";
      if (col == 4) return "SR";
      if (col == 5) return "SC";
      if (col == 6) return "Failure";
    }
    if (col == 1) return "Term";
    return String.valueOf(col - 2);
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) return rowIndex;
    if (centralityList != null) {
      CentralityData cd = centralityList.get(rowIndex);
      Partition par = cd.getDelta();
      //org.uacalc.element.SubProductElement failure = null;
      if (columnIndex == 1) return par;
      if (columnIndex == 2) return cd.getWeakCentralityFailure() == null ? "yes" : "no";
      if (columnIndex == 3) return cd.getCentralityFailure() == null ? "yes" : "no"; 
      if (columnIndex == 4) return cd.getStrongRectangularityFailure() == null ? "yes" : "no";
      if (columnIndex == 5) 
        return cd.getCentralityFailure() == null && cd.getStrongRectangularityFailure() == null ? "yes" : "no";
      if (columnIndex == 6) {
        if (cd.getWeakCentralityFailure() != null) return cd.getWeakCentralityFailure();
        if (cd.getCentralityFailure() != null) return cd.getCentralityFailure();
        if (cd.getStrongRectangularityFailure() != null) return cd.getStrongRectangularityFailure();
        return "";
      }
    }
    if (columnIndex == 1) return terms[rowIndex];
    return universeList.get(rowIndex).get(columnIndex - 2);
  }

  public boolean isCellEditable(int row, int col) { return false; }
  
}
