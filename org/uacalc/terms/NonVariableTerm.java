/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.terms;

import org.uacalc.alg.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.Operations;
import org.uacalc.alg.op.TermOperation;
import org.uacalc.alg.op.TermOperationImp;
import org.uacalc.util.*;
import java.util.*;

/**
 * 
 */
public class NonVariableTerm implements Term {

  public final static String LEFT_PAR = "(";
  public final static String RIGHT_PAR = ")";
  public final static String COMMA = ",";

  //SimilarityType similarityType;
  List<Term> children;
  OperationSymbol leadingOperationSymbol;

  public NonVariableTerm(OperationSymbol opSym, List<Term> children) {
    this.leadingOperationSymbol = opSym;
    this.children = children;
    //this.similarityType = st;
  }

  /**
   * A static method to make a constant term from an operation symbol.
   * 
   * @param sym
   * @return
   */
  public static Term makeConstantTerm(OperationSymbol sym) {
    return new NonVariableTerm(sym, new ArrayList<Term>());
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof NonVariableTerm)) return false;
    final NonVariableTerm t = (NonVariableTerm)obj;
    return t.leadingOperationSymbol().equals(leadingOperationSymbol) 
                               &&  t.getChildren().equals(children);
  }

  public int hashCode() {
    return leadingOperationSymbol.hashCode() + children.hashCode();
  }

  //public SimilarityType similarityType() { return similarityType; }

  public boolean isaVariable() { return false; }

  /**
   * The leading operation symbol or null if this term is a variable.
   */
  public OperationSymbol leadingOperationSymbol() {
    return leadingOperationSymbol;
  }

  /**
   * A list of terms which are the immediate children.
   */
  public List<Term> getChildren() { return children; }

  /**
   * The evaluation of this term in an algebra using <tt>map</tt> as
   * the variable assignment.
   */
  public Object eval(Algebra alg, Map map) {
    //@mike implemented this
    final Operation op = alg.getOperation(leadingOperationSymbol());
    final List args = new ArrayList(op.arity());
    List children = getChildren();
    for (int i = 0; i < op.arity(); i++ ) {
      Term t = (Term)children.get(i);
      args.add(t.eval(alg, map));
    }
    return op.valueAt(args);
  }

  public int intEval(Algebra alg, Map map) {
    final Operation op = alg.getOperation(leadingOperationSymbol());
    int[] arg = new int[op.arity()];
    List children = getChildren();
    for (int i = 0; i < op.arity(); i++ ) { 
      Term t = (Term)children.get(i);
      arg[i] = t.intEval(alg, map);
    }
    return op.intValueAt(arg);
  }

  // should be TermOperation but having trouble with casting
  public Operation interpretation(final SmallAlgebra alg, 
                                      final List<Variable> varlist, 
                                      final boolean all) {
    final int arity = varlist.size(); // if no varlist ??
    final int size = alg.cardinality();
    Operation op = new AbstractOperation("Op_" + this,
                                         arity, size) {
        public Object valueAt(List args) {
          return null;  // IMPLEMENT THIS !!!!!!!!!!!
        }
        Operation tableOp = null;
        @Override
        public void makeTable() {
          int h = 1;
          for (int i = 0; i < arity; i++) {
            h = h * size;
          }
          int[] values = new int[h];
          for (int i = 0; i < h; i++) {
            values[i] = intValueAt(Horner.hornerInv(i, size, arity));
          }
          tableOp = Operations.makeIntOperation(symbol(), size, values);
        }
        @Override
        public int[] getTable() {
          //if (tableOp == null) makeTable();
          return tableOp.getTable();
        }
        @Override
        public int[] getTable(boolean makeTable) {
          if (makeTable) makeTable();
          return tableOp.getTable();
        }

        /*
        public Term getTerm() { return NonVariableTerm.this; }
        public List getOrderedvariables() { return varlist; }
        */
        @Override
        public int intValueAt(int[] args) {
          if (tableOp != null) return tableOp.intValueAt(args);
          Map<Variable,Integer> map = new  HashMap<Variable,Integer>();
          for (int i = 0; i < args.length; i++) {
            map.put(varlist.get(i), new Integer(args[i]));
          }
          return intEval(alg, map);
        }
      };
    return op;
  }

  public TermOperation interpretation(final SmallAlgebra alg) {
    return new TermOperationImp(this, getVariableList(), alg);
  }

  public List<Variable> getVariableList() {
    List<Variable> lst = new ArrayList<Variable>();
    addVariables(this, lst);
    return lst;
  }

  private void addVariables(Term t, List<Variable> vars) {
    if (t.isaVariable()) {
      if (!vars.contains(t)) vars.add((Variable)t);
    }
    else {
      for (Term term : t.getChildren() ) {
        addVariables(term, vars);
      }
    }
  }



  public int length() {
    int ans = 1;
    for (Term t : getChildren()) {
      ans =  ans + t.length();
    }
    return ans;
  }

  public int depth() {
    int max = 0;
    for (Term t : getChildren()) {
      max =  Math.max(max, t.depth());
    }
    return 1 + max;
  }
  
  public Term substitute(Map<Variable,Term> map) {
    List<Term> newChildren = new ArrayList<Term>(getChildren().size());
    for (Term t : getChildren()) {
      newChildren.add(t.substitute(map));
    }
    return new NonVariableTerm(leadingOperationSymbol(), newChildren);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    return writeStringBuffer(sb).toString();
  }

  public StringBuffer writeStringBuffer(StringBuffer sb) {
    sb.append(leadingOperationSymbol().name());
    sb.append(LEFT_PAR);
    List<Term> children = getChildren();
    final int n = children.size() - 1;
    for (int i = 0; i < n ; i++) {
      Term child = children.get(i);
      child.writeStringBuffer(sb);
      sb.append(COMMA);
    }
    if (n >= 0) getChildren().get(n).writeStringBuffer(sb);
    sb.append(RIGHT_PAR);
    return sb;
  }
    

}




