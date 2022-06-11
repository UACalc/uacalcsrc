package org.uacalc.eq;

import java.util.*;
import org.uacalc.terms.*;
import org.uacalc.alg.*;
import org.uacalc.alg.op.*;
import org.uacalc.ui.tm.ProgressReport;

/**
 * A class to represent equations, that is, pairs of terms.
 * 
 * @author ralph
 *
 */
public class Equation {
  
  private final Term leftSide;
  private final Term rightSide;
  private List<Variable> varList;
  
  public Equation(Term left, Term right) {
    this.leftSide = left;
    this.rightSide = right;
  }
  
  public Equation(Term left, Term right, List<Variable> vars) {
    this(left, right);
    this.varList = vars;
  }

  public Term leftSide() { return leftSide; }
  public Term rightSide() { return rightSide; }
  
  public List<Variable> getVariableList() {
    if (varList == null) makeVarList();
    return varList;
  }
  
  private void makeVarList() {
    varList = leftSide.getVariableList();
    for (Variable v : rightSide.getVariableList()) {
      if (!varList.contains(v)) varList.add(v);
    }
  }
  
  public Set<OperationSymbol> getOperationSymbols() {
    Set<OperationSymbol> set = leftSide().getOperationSymbols();
    for (OperationSymbol sym : rightSide().getOperationSymbols()) {
      set.add(sym);
    }
    return set;
  }
  
  public int[] findFailure(SmallAlgebra alg) {
    return findFailure(alg, null);
  }
  
  /**
   * Check if this equation holds in <code>alg</code>, 
   * returning a place where it fails
   * or <code>null</code> if it is true.
   * 
   * @param alg
   * @return where it fails, or <code>null</code> if it is true.
   */
  public int[] findFailure(SmallAlgebra alg, ProgressReport report) {
    Operation leftOp = leftSide.interpretation(alg, getVariableList(), true);
    Operation rightOp = rightSide.interpretation(alg, getVariableList(), true);
    int[] diff = Operations.findDifference(leftOp, rightOp, report);
    return diff; // may want to return a map from variable to ints
  }
  
  public Map<Variable,Integer> findFailureMap(SmallAlgebra alg) {
    return findFailureMap(alg, null);
  }
  
  /**
   * Check if this equation holds in <code>alg</code>, 
   * returning the map from variables to ints
   * where it fails or <code>null</code> if it is true.
   * 
   * @param alg
   * @return
   */
  public Map<Variable,Integer> findFailureMap(SmallAlgebra alg, ProgressReport report) {
    int[] diff = findFailure(alg, report);
    if (diff == null) return null;
    Map<Variable,Integer> map = new HashMap<Variable,Integer>();
    int k = 0;
    for (Variable v : getVariableList()) {
      map.put(v, diff[k++]);
    }
    return map;
  }
  
  public String toString() {
    return leftSide.toString() + " = " + rightSide.toString();
  }
  
  
}
