/* TermGraph.java (c) 2005/08/16  Ralph Freese */

package org.uacalc.terms;

import java.util.*;

import org.uacalc.alg.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.TermOperation;
import org.uacalc.alg.op.TermOperationImp;
import org.uacalc.util.SimpleList;

/**
 * The term graph; that is, term tree with possibly shared nodes. This
 * graph is acyclic.
 */
public class VariableImp implements Variable {

  String name;
  //SimilarityType similarityType;

  public VariableImp(String name) {
    this.name = name;
    //this.similarityType = similarityType;
  }

  /**
   * The list of the variables for the language of this term.
   */
  public String getName() {
    return name;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof VariableImp)) return false;
    final VariableImp v = (VariableImp)obj;
    return name.equals(v.getName());
  }

  public int hashCode() {
    return name.hashCode();
  }

  public boolean isaVariable() { return true; }

  //public SimilarityType similarityType() { return similarityType; }

  public List<Term> getChildren() { return null; }

  public OperationSymbol leadingOperationSymbol() { return null; }
  
  public Set<OperationSymbol> getOperationSymbols() { 
    return new HashSet<OperationSymbol>();
  }

  public Object eval(Algebra alg, Map map) {
    return map.get(this);
  }

  public int intEval(Algebra alg, Map map) {
    return ((Integer)map.get(this)).intValue();
  }

  public Operation interpretation(final SmallAlgebra alg, 
                                      final List<Variable> varlist, 
                                      final boolean all) {
    final int index = varlist == null ? 0 : varlist.indexOf(this);
    final int arity = varlist == null ? 1 : varlist.size();
    Operation op = new AbstractOperation("Op_" + this, 
                                         arity, alg.cardinality()) {
        public Object valueAt(List args) {
          return null;  // IMPLEMENT THIS !!!!!!!!!!!
        }
        Operation tableOp = null;
        public void makeTable() {} // no need to implement this
        public Term getTerm() { return VariableImp.this; }
        public List getOrderedvariables() { return varlist; }
        public int intValueAt(int[] args) {
          return args[index];
        }
      };
    return op;
  }

  //public Operation interpretation(SmallAlgebra alg) {
  //  return interpretation(alg, null, false);
  //}
  
  public TermOperation interpretation(final SmallAlgebra alg) {
    return new TermOperationImp(this, getVariableList(), alg);
  }

  public List getVariableList() {
    List lst = new ArrayList(1);
    lst.add(this);
    return lst;
  }

  public int depth() { return 0; }

  public int length() { return 1; }
  
  public Term substitute(Map<Variable,Term> map) {
    if (map.get(this) != null) return map.get(this);
    return this;
  }

  public String toString() { return getName(); }

  public StringBuffer writeStringBuffer(StringBuffer sb) {
    sb.append(getName());
    return sb;
  }

}




