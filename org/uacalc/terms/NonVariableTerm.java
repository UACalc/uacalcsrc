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
  List children;
  OperationSymbol leadingOperationSymbol;

  public NonVariableTerm(OperationSymbol opSym, List children) {
    this.leadingOperationSymbol = opSym;
    this.children = children;
    //this.similarityType = st;
  }

  public boolean equals(Object obj) {
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
  public List getChildren() { return children; }

  /**
   * The evaluation of this term in an algebra using <tt>map</tt> as
   * the variable assignment.
   */
  public Object eval(Algebra alg, Map map) {
    return null;
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

        /*
        public Term getTerm() { return NonVariableTerm.this; }
        public List getOrderedvariables() { return varlist; }
        */
        public int intValueAt(int[] args) {
          if (tableOp != null) return tableOp.intValueAt(args);
          Map map = new  HashMap();
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
    List lst = new ArrayList();
    addVariables(this, lst);
    return lst;
  }

  private void addVariables(Term t, List vars) {
    if (t.isaVariable()) {
      if (!vars.contains(t)) vars.add(t);
    }
    else {
      for (Iterator it = t.getChildren().iterator(); it.hasNext(); ) {
        addVariables((Term)it.next(), vars);
      }
    }
  }



  public int length() {
    int ans = 1;
    for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
      ans =  ans + ((Term)it.next()).length();
    }
    return ans;
  }

  public int depth() {
    int max = 0;
    for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
      max =  Math.max(max, ((Term)it.next()).depth());
    }
    return 1 + max;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    return writeStringBuffer(sb).toString();
  }

  public StringBuffer writeStringBuffer(StringBuffer sb) {
    sb.append(leadingOperationSymbol().name());
    sb.append(LEFT_PAR);
    List children = getChildren();
    final int n = children.size() - 1;
    for (int i = 0; i < n ; i++) {
      Term child = (Term)children.get(i);
      child.writeStringBuffer(sb);
      sb.append(COMMA);
    }
    ((Term)children.get(n)).writeStringBuffer(sb);
    sb.append(RIGHT_PAR);
    return sb;
  }
    

}




