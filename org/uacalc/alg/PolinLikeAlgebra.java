/* Malcev.java 2001/09/02  */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;
//import org.apache.log4j.*;
import java.util.logging.*;

/**
 * Given a homomorphism f: A to B, this constructs a Polin type algebra
 * on the disjoint union of A and B. Actually for this first edition A
 * and B must be equal and f is the identity.
 *
 * @version $Id$
 */
public class PolinLikeAlgebra extends GeneralAlgebra implements SmallAlgebra {

  static Logger logger = Logger.getLogger("org.uacalc.alg.PolinConstructions");
  static {
    logger.setLevel(Level.FINER);
  }

  protected SmallAlgebra alg0;
  protected SmallAlgebra alg1;
  protected Operation map;
  //protected Object const0;
  //protected Object const1;
  protected int c0index;
  protected int c1index;
  

  public PolinLikeAlgebra(String name, final SmallAlgebra alg0, 
                          final SmallAlgebra alg1, 
                          final Operation map, int c0, int c1) {
    super(name, 
          new AbstractSet() {
                final int s = alg0.cardinality() + alg1.cardinality();
                public boolean contains(Object obj) {
                  try {
                    int k = ((Integer)obj).intValue();
                    if (0 <= k && k < s) return true;
                  }
                  catch (ClassCastException ex) { }
                  return false;
                }
                public int size() { return s; }
                public Iterator iterator() {
                  throw new UnsupportedOperationException();
                }
              },
          null);
    this.alg0 = alg0;
    this.alg1 = alg1;
    this.map = map;  // a map from alg1 to alg0
    c0index = c0;
    c1index = c1;
    setOperations(makeOperations(alg0, alg1, map));
  }

  private List makeOperations(SmallAlgebra alg0, 
                              SmallAlgebra alg1, Operation map) {
    return null;
    // do polinz and also add the unary external complement.
  }


  public int elementIndex(Object elt) {
    return -1;
  }

  public Object getElement(int index) {
    return null;
  }

  public static SmallAlgebra constructPolinAlgebra(SmallAlgebra alg, 
                                                   Object elem) {
    return constructPolinAlgebra(alg, alg.elementIndex(elem));
  }

  public static SmallAlgebra constructPolinAlgebra(SmallAlgebra alg, 
                                                   final int elt) {
    return null;
  }

  public Operation polinizeOperation(OperationSymbol sym) {
    final Operation op0 = alg0.getOperation(sym);
    final Operation op1 = alg1.getOperation(sym);
    final int n0 = alg0.cardinality();
    return new AbstractOperation(sym, alg0.cardinality() + alg1.cardinality()) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int intValueAt(int[] args) {
        final int type = argType(args, n0);
        if (type == 0) return op0.intValueAt(args);
        if (type == 1) return op1.intValueAt(args);
        int[] argsx = new int[args.length];
        for (int i = 0; i < args.length; i++) {
          if (args[i] < n0) argsx[i] = args[i];
          else argsx[i] = map.intValueAt(new int[] {args[i] - n0});
        }
        return op1.intValueAt(argsx);
      }
    };


  }


  /**
   * Gives 0 is all are in alg0; 1 is all are in alg1; else 2.
   */
  private static int argType(final int[] args, final int n) {
    if (args.length == 0) return 0;
    if (args[0] < n) {
      for (int i = 1; i < args.length; i++) {
        if (args[i] >= n) return 2;
      }
      return 0;
    }
    for (int i = 1; i < args.length; i++) {
      if (args[i] < n) return 2;
    }
    return 1;
  }


}







