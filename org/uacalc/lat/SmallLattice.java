/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.lat;

import java.util.List;
import java.util.Iterator;
import java.util.Set;

import org.uacalc.alg.*;

public interface SmallLattice extends Lattice {

  /**
   * The indices of the upper covers of the element whose index is
   * <tt>index</tt>.
   */
  public int[] upperCoversIndices(int index);

// maybe one for lower covers. Not sure.

}




