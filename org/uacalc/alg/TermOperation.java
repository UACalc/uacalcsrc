/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.List;
import org.uacalc.terms.*;

/**
 * This interface specifies a term operation, that is, the interpretation
 * of a term in an algebra.
 */
public interface TermOperation extends Operation {

  /**
   * This gives the term.
   */
  public Term getTerm();

  /**
   * This gives a list of the variables in order without repeats.
   */
  public List getOrderedVariables();


}




