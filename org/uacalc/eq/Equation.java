package org.uacalc.eq;

import java.util.*;
import org.uacalc.terms.*;
import org.uacalc.alg.*;
import org.uacalc.alg.op.*;

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
  
  /**
   * Check if this equation holds in <code>alg</code>, 
   * returning a place where it fails
   * or <code>null</code> if it is true.
   * 
   * @param alg
   * @return
   */
  public int[] findFailure(SmallAlgebra alg) {
    Operation leftOp = leftSide.interpretation(alg, getVariableList(), true);
    Operation rightOp = rightSide.interpretation(alg, getVariableList(), true);
    int[] diff = Operations.findDifference(leftOp, rightOp);
    return diff; // may want to return a map from variable to ints
  }
  
  /**
   * Check if this equation holds in <code>alg</code>, 
   * returning the map from variables to ints
   * where it fails or <code>null</code> if it is true.
   * 
   * @param alg
   * @return
   */
  public Map<Variable,Integer> findFailureMap(SmallAlgebra alg) {
    int[] diff = findFailure(alg);
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
