/* QuotientElement.java (c) 2005/05/20  Ralph Freese */

package org.uacalc.alg;

import org.uacalc.util.*;

import org.uacalc.alg.conlat.*;

/**
 * This class represents an element in a quotient algebra. It holds
 * the element from the super algebra (the pre-image) and the congruence.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class QuotientElement {

  protected final QuotientAlgebra alg;

  protected final int index;

  public QuotientElement(QuotientAlgebra alg, int index) {
    this.alg = alg;
    this.index = index;
  }

  public QuotientAlgebra getAlgebra() {
    return alg;
  }

  public SmallAlgebra superAlgebra() {
    return alg.superAlgebra();
  }

  /**
   * Get the congruence on the super algebra giving this algebra.
   */
  public Partition getCongruence() {
    return alg.getCongruence();
  }

  /**
   * The index in the quotient algebras, not in the super algebra.
   */
  public int getIndex() { return index; }

  public int getIndexInSuperAlgebra() {
    return getCongruence().representatives()[index];
  }

  public String toString() {
    Object elem = superAlgebra().getElement(getIndexInSuperAlgebra());
    return elem.toString() + "/" + getCongruence().toString();
  }

}


