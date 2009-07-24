package org.uacalc.element;

import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;

public class SubProductElement implements Element {
  
  private final SubProductAlgebra algebra;
  private final IntArray element;
  
  
  public SubProductElement(IntArray elt, SubProductAlgebra alg) {
    this.algebra = alg;
    this.element = elt;
  }
  
  public Term getTerm() {
    return algebra.getTerm(element);
  }
  
  public List<Variable> getVariableList() {
    return getTerm().getVariableList();
  }
  
  public Map<Variable,IntArray> getVariableMap() {
    List<Variable> vars = getVariableList();
    Map<Variable,IntArray> ans = new HashMap<Variable,IntArray>(vars.size());
    Map<Variable,IntArray> varsMap = algebra.getVariableToGeneratorMap();
    for (Variable var : vars) {
      ans.put(var, varsMap.get(var));
    }
    return ans;
  }

  @Override
  public Algebra getAlgebra() {
    return algebra;
  }

  @Override
  public Element getParent() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Element[] getParentArray() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int index() {
    return algebra.elementIndex(element);
  }

  @Override
  public int[] parentIndexArray() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public String toString() {
    final String arr = " -> ";
    StringBuffer sb = new StringBuffer(ArrayString.toString(element));
    Term term = getTerm();
    if (term != null) {
      sb.append(", term: ");
      sb.append(term);
      sb.append(" under ");
      for (Variable var : getTerm().getVariableList()) {
        sb.append(var);
        sb.append(arr);
        sb.append(ArrayString.toString(getVariableMap().get(var)));
        sb.append(", ");
      }
    }
    // delete the last ,
    return sb.toString();
  }

}
