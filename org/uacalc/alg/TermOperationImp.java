/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.List;
import org.uacalc.terms.*;

/**
 * This interface specifies a term operation, that is, the interpretation
 * of a term in an algebra.
 */
public class TermOperationImp 
                 extends AbstractOperation implements TermOperation {

  Term term;
  List<Variable> variables;
  SmallAlgebra alg;
  Operation interpretation;

  public TermOperationImp (Term term, List<Variable> variables, 
                                                  SmallAlgebra alg) {
    this("\"" + term.toString() + "\"", term, variables, alg);
  } 

  public TermOperationImp (String name, Term term, 
                           List<Variable> variables, SmallAlgebra alg) {
    super(name, variables.size(), alg.cardinality());
    this.alg = alg;
    this.term = term;
    this.variables = variables;
    interpretation = term.interpretation(alg, variables, true);
  } 

  public Object valueAt(List args) {
    return interpretation.valueAt(args);
  }

  public int intValueAt(int[] args) {
    return interpretation.intValueAt(args);
  }


  /**
   * This gives the term.
   */
  public Term getTerm() {
    return term;
  }

  /**
   * This gives a list of the variables in order without repeats.
   */
  public List getOrderedVariables() {
    return variables;
  }

  public String toString() {
    return term.toString();
  }


}




