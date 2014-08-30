/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.terms;

import org.uacalc.alg.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.TermOperation;

import java.util.*;

/**
 * 
 */
public interface Term {

  /*
   * The list of the variables for the language of this term.
   * It is allowed to have variables not actually used in this term.
   */
  //public List variables();

  //public SimilarityType similarityType();

  public boolean isaVariable();

  /**
   * The leading operation symbol or null if this term is a variable.
   */
  public OperationSymbol leadingOperationSymbol();
  
  /**
   * The set of operation symbols or null for a variable.
   * 
   * @return set of operation symbols
   */
  public Set<OperationSymbol> getOperationSymbols();

  /**
   * A list of terms which are the immediate children.
   * If the term is a  variable this return null; it is a constant
   * this returns the empty list.
   */
  public List<Term> getChildren();

  /**
   * The evaluation of this term in an algebra using <code>map</code> as
   * the variable assignment.
   */
  public Object eval(Algebra alg, Map map);

  /**
   * The int evaluation of this term in an algebra using <code>map</code> as
   * the variable assignment.
   */
  public int intEval(Algebra alg, Map<Variable,Integer> map);

  /**
   * The interpretation of this term in an algebra; that is, the
   * operation on <code>alg</code> corresponding to this term.
   * The varlist, a list of <code>Variable</code>'s, specifies the
   * order. If <code>useAll</code> is true, variables that are
   * not explicit in this term are still used.
   * <br>
   * For example, suppose the term is <code>(z*y)*z</code>, and
   * <code>varlist</code> is <code>(x, y, z)</code>.
   * If <code>useAll</code> is true, the resulting operation will
   * have arity 3 and be independent of its first variable. Otherwise
   * it will be a 2 place operation with <code>y</code> as the first
   * variable and <code>z</code> as the second.
   *
   * @param alg        the Algebra
   * @param varlist    the list of variables
   * @param useAll     if true, use all the variables in varlist
   * @return           the term operation
   */
// should return TermOperation but having some trouble with casting
  public Operation interpretation(SmallAlgebra alg, 
                                  List<Variable> varlist, boolean useAll);

  /**
   * The operation obtained from this term using the variables in the
   * order they occur. 
   * <br>
   * For example, suppose the term is <code>(z*y)*z</code>.
   * The result will be a 2 place operation with <code>z</code> as the first
   * variable and <code>y</code> as the second.
   *
   * @param alg        the Algebra
   * @return           the term operation
   * @see #interpretation(Algebra,List,boolean)
   */
  public TermOperation interpretation(SmallAlgebra alg);

  /**
   * The depth of the term tree. A variable has depth 0.
   */
  public int depth();

  /**
   * The length of the term. A variable has length 1.
   */
  public int length();

  /**
   * The list of variables in the order they appear in the term.
   */
  public List<Variable> getVariableList();
  
  /**
   * Replace some of the variables with terms.
   * 
   * @param map   a map for the substitution
   * @return
   */
  public Term substitute(Map<Variable,Term> map);

  /**
   * A reasonably good printout of the term.
   */
  public String toString();

  /**
   * This is really an efficiency helper for toString. It returns 
   * the StringBuffer as a convenience.
   */
  public StringBuffer writeStringBuffer(StringBuffer sb);


}




