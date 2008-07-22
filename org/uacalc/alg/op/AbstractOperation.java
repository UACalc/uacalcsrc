/* AbstractOperation.java (c) 2001/07/28  Ralph Freese and Emil Kiss */

package org.uacalc.alg.op;

import java.util.List;
import java.util.logging.*;



/**
 * This class implements the basic methods of <code>Operation</code>.
 * <code>valueAt</code> is abstract so must be overwritten.
 *
 * @author Ralph Freese
 * @author Emil Kiss
 *
 * @version $Id$
 */
public abstract class AbstractOperation implements Operation {

  static Logger logger = Logger.getLogger("org.uacalc.alg.AbstractOperation");
  static {
    logger.setLevel(Level.FINER);
  }

  //protected int arity;
  protected OperationSymbol symbol;
  protected int algSize;
  protected int[] valueTable;

  public AbstractOperation(String name, int arity, int algSize) {
    this(new OperationSymbol(name, arity), algSize);
  }

  public AbstractOperation(OperationSymbol symbol, int algSize) {
    this.symbol = symbol;
    this.algSize = algSize;
  }


  /**
   * This gives the arity of this operation.
   */
  public int arity() { return symbol.arity(); }

  /**
   * This gives the size of the set upon which the operation acts.
   */
  public int getSetSize() { return algSize; }

  public OperationSymbol symbol() { return symbol; }

  public abstract Object valueAt(List args);

  public int[] valueAt(int[][] args) {
    throw new UnsupportedOperationException();
  }


  public int intValueAt(int[] args) {
    logger.warning("intValueAt is not define.");
    logger.info("op symbol is " + symbol());
    logger.info("args = " + org.uacalc.util.ArrayString.toString(args));

    throw new UnsupportedOperationException();
  }
  
  public boolean isTableBased() {
    return false;
  }

  /**
   * Is this operation idempotent in the sense f(x,x,..,x) = x.
   */
  public boolean isIdempotent() {
    final int n = getSetSize();
    final int arity = arity();
    int[] arg = new int[arity];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < arity; j++) {
        arg[j] = i;
      }
      if (intValueAt(arg) != i) return false;
    }
    return true;
  }

  public boolean isTotal() {
    return Operations.isTotal(this);
  }
  
  /**
   * Test if this operation is totally symmetric; that is, invariant
   * under all permutation of the variables.
   */
  public boolean isTotallySymmetric() {
    return Operations.isTotallySymmetric(this);
  }

  /**
   * Test if this operation is binary and associative.
   */
  public boolean isAssociative() {
    return Operations.isAssociative(this);
  }

  /**
   * Test if this operation is binary and commutative.
   */
  public boolean isCommutative() {
    return Operations.isCommutative(this);
  }

  public void makeTable() {}

  public int[] getTable() { return valueTable; }


}




