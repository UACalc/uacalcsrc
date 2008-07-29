/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg.op;

import java.util.List;

/**
 * This interface specifies an operation, that is, a map from 
 * the direct product of some number (called the arity) of a set
 * to the set. Since the set will often be just 
 * <code>{0, 1, ..., n-1}</code> we have <code>intValueAt(int[] args)</code>
 * form of the operation. We also have an <code>Object</code> form for
 * general sets. Since only one or the other may be required, both are
 * optional. 
 * 
 */
public interface Operation {

  /**
   * This gives the arity of this operation.
   */
  public int arity();

  /**
   * This gives the size of the set upon which the operation is defined.
   */
  public int getSetSize();

  /**
   * The operation symbol for this operation.
   */
  public OperationSymbol symbol();

  /**
   * This operation is the element version.
   */
  public Object valueAt(List args);

  /**
   * This operation is for fast product operation. 
   *
   *
   * @param  args an array of arity int arrays from the product algebra.
   */
  public int[] valueAt(int[][] args);

  /**
   * This (optional) operation is the int version.
   */
  public int intValueAt(int[] args);
  
  /**
   * This (optional) operation is for fast access to the table,
   * if it exits.
   * 
   * @param arg    the Horner encoding of the actual args.
   * @return
   */
  public int intValueAt(int arg);

  /**
   * This will make a table and so make the operation faster but
   * require more space. So if A is in HSP(B) then for ints x and y,
   * x * y would be evaluated by finding the representative
   * of x and y of the congruence; then these representatives would be
   * expanded into array representing the corresponding elements in the 
   * direct product. These would be multiplied and then the whole process
   * would be reversed. If A is reasonable small it may make sense to
   * make a table for the multiplication.
   */
  public void makeTable();

  /**
   * Get the table for this operation or null if it does not exist.
   */
  public int[] getTable();
  
  public boolean isTableBased();

  /**
   * Is this operation idempotent in the sense f(x,x,..,x) = x.
   */
  public boolean isIdempotent();

  /**
   * Is this operation binary and associative.
   */
  public boolean isAssociative();

  /**
   * Is this operation binary and commutative.
   */
  public boolean isCommutative();

  /**
   * Is this operation totally symmetric; that is, invariant
   * under all permutation of the variables.
   */
  public boolean isTotallySymmetric();
  
  /**
   * Only OperationWithDefaultValue's can fail this.
   * 
   * @return
   */
  public boolean isTotal();

}




