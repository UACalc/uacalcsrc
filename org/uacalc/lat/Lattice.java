/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.lat;

import java.util.List;
import java.util.Iterator;
import java.util.Set;

import org.uacalc.alg.*;

public interface Lattice extends Algebra, OrderedSet {

  /**
   * An optional operation returning the list of join irreducible elements.
   */
  public List<? extends Object> joinIrreducibles();

  /**
   * An optional operation returning the list of meet irreducible elements.
   */
  public List<? extends Object> meetIrreducibles();
  
  public List<? extends Object> atoms();
  public List<? extends Object> coatoms();

  public Object join(Object a, Object b);
  public Object join(List args);
  public Object meet(Object a, Object b);
  public Object meet(List args);


}




