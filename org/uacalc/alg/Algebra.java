/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.List;
import java.util.Iterator;
import java.util.Set;

import org.uacalc.alg.conlat.CongruenceLattice;

public interface Algebra {

// keep these for awhile. Not sure if they'll be useful beyond -1 to
// indicate unknown.

  /**
   * This is used to indicate the carinality of the algebra is unknown.
   */
  public static final int CARDINALITY_UNKNOWN = -1;

  /**
   * This is used to indicate the carinality of the algebra is finite 
   * but otherwise unknown.
   */
  public static final int CARDINALITY_FINITE = -2;

  /**
   * This is used to indicate the carinality of the algebra is infinite 
   * but otherwise unknown.
   */
  public static final int CARDINALITY_INFINITE = -3;

  /**
   * This is used to indicate the carinality of the algebra is either 
   * finite or countably infinite.
   */
  public static final int CARDINALITY_COUNTABLE = -4;

  /**
   * This is used to indicate the carinality of the algebra is 
   * countably infinite.
   */
  public static final int CARDINALITY_COUNTABLY_INFINITE = -5;

  /**
   * We use java.util.Set to hold the universe of the algebra.
   * In order to accomodate infinite algebras and algebras with
   * unknown cardinality, we allow the following changes: 
   * <ul>
   *  <li>size can return a negative value if the cardinality is 
   *      infinite or unknown or is larger that the largest int.
   *      In this case we may want to specify some information
   *      about the cardinality in another method but we have not
   *      decided on this yet.
   *  </li>
   *  <li>it is allowed that the iterator method of Set throw
   *      an UnsupportedOperationException.
   *  </li>
   *  <li>when extending AbstractSet, the <tt>contains</tt> method should be
   *      overwritten.
   *  </li>
   * </ul>
   */
  public Set universe();

  public int cardinality();

  public boolean isUnary();

  /**
   * returns the iterator of the universe. Since we allow that
   * to be optional, this may throw an UnsupportedOperationException.
   */
  public Iterator iterator();

  public List<Operation> operations();

  /**
   * Get the operation correspond to a symbol or null if 
   * the symbol is not part of the similarityType.
   */
  public Operation getOperation(OperationSymbol sym);

  //public CongruenceLattice con();  // only for SmallAlgebra's

  public String name();

  public String description();

  public SimilarityType similarityType();

  public boolean isSimilarTo(Algebra alg);

  /**
   * Make operation tables to speed up the evaluation of operations at
   * the cost using more space.
   *
   * @see Operation.makeTable
   */
  public void makeOperationTables();

  /**
   * This gives a list of the operations of arity 0, which is
   * a little different from the constants.
   */
  public List constantOperations();

  /**
   * Test if all of the operations are idempotent.
   */
  public boolean isIdempotent();
}




