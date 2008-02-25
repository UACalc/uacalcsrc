/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.uacalc.alg.conlat.CongruenceLattice;
import org.uacalc.alg.sublat.SubalgebraLattice;

/**
 * A small algebra is one whose universe can be effectively
 * indexed by {0,..., n-1} for some positive int n.
 * <tt>cardinality()</tt> should return n.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public interface SmallAlgebra extends Algebra {

  public static enum AlgebraType {
    BASIC, BASIC_LATTICE, QUOTIENT, SUBALGEBRA, PRODUCT, POWER,
    REDUCT, SUBPRODUCT, FREE, POLIN_LIKE, UNARY_TERMS_MONOID
  }
  
  /**
   * The type of algebra this is, like a quotient algebra.
   * 
   * @return
   */
  public AlgebraType algebraType();
  
  /**
   * returns the <tt>k</tt>th element. 
   */
  public Object getElement(int k);

  /**
   * This method will return <tt>k</tt> if <tt>elem</tt> 
   * is the <tt>k</tt>th element; otherwise -1. (Should we have it
   * throw an error instead?)
   */
  public int elementIndex(Object elem);

  /**
   * The universe as a List such getElement(k) will equal the kth
   * element of the List. It is allowed to be null.
   */
  public List getUniverseList();

  /**
   * A map that can be used for elementIndex(a).
   * It is allowed to be null.
   */
  public Map getUniverseOrder();

  /**
   * The congurence lattice.
   */
  public CongruenceLattice con();

  /**
   * The subalgebra lattice.
   */
  public SubalgebraLattice sub();

  /**
   * For a BasicAlgebra this is null; for a QuotientAlgebra this is
   * the preimage; for a Subalgebra it is the super algebra.
   */
  public SmallAlgebra parent();

}




