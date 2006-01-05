/* TermGraph.java (c) 2005/08/16  Ralph Freese */

package org.uacalc.terms;

import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.util.SimpleList;

/**
 * The term graph; that is, term tree with possibly shared nodes. This
 * graph is acyclic.
 */
public interface Variable extends Term {

  public static final Variable x = new VariableImp("x");
  public static final Variable y = new VariableImp("y");
  public static final Variable z = new VariableImp("z");

  /**
   * The list of the variables for the language of this term.
   */
  public String getName();

}




