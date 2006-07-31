/* Malcev.java 2001/09/02  */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;
//import org.apache.log4j.*;
import java.util.logging.*;

import org.uacalc.lat.*;
import org.uacalc.ui.*;

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

  protected SmallAlgebra topAlg;
  protected SmallAlgebra botAlg;
  protected Operation map;
  //protected Object const0;
  //protected Object const1;
  protected int c0index;
  protected int c1index;
  

  public PolinLikeAlgebra(String name, final SmallAlgebra topAlg, 
                          final SmallAlgebra botAlg, 
                          Operation map, final int c0, final int c1) {
    super(name, 
          new AbstractSet() {
                final int s = topAlg.cardinality() + botAlg.cardinality();
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
              });
    this.topAlg = topAlg;
    this.botAlg = botAlg;
    c0index = c0;
    c1index = c1;
    this.map = map;  // a map from topAlg to botAlg
    setup();
  }

  public void setup() {
    final int topSize = topAlg.cardinality();
    final int botSize = botAlg.cardinality();
    if (map == null) map = id(topSize + botSize);
    List ops = new ArrayList();
    for (Iterator it = topAlg.similarityType().getOperationSymbols().iterator();
                                                              it.hasNext(); ) {
      ops.add(polinizeOperation((OperationSymbol)it.next()));
    }
    ops.add(new AbstractOperation("^+", 1, topSize + botSize) {
        public Object valueAt(List args) {
          throw new UnsupportedOperationException();
        }
        public int intValueAt(int[] args) {
          if (args[0] < topSize) return topSize + c1index;
          return c0index;
        }
      });
//System.out.println("ops : " + ops.size());
    //setOperations(ops);
//System.out.println("get ops : " + operations().size());
    operations = ops;


    //setOperations(makeOperations(topAlg, botAlg, map));
  }

  private Operation id(final int k) {
    return new AbstractOperation("id", 1, k) {
      public Object valueAt(List args) {
        //throw new UnsupportedOperationException();
        return args.get(0);
      }
      public int intValueAt(int[] args) {
        return args[0];
      }
    };
  }



  private List makeOperations(SmallAlgebra topAlg, 
                              SmallAlgebra botAlg, Operation map) {
    return null;
    // do polinz and also add the unary external complement.
  }


  public int elementIndex(Object elt) {
    return -1;
  }

  public Object getElement(int index) {
    return null;
  }

  public Map getUniverseOrder() { return null; }
  public List getUniverseList() { return null; }

  public static SmallAlgebra constructPolinAlgebra(SmallAlgebra alg, 
                                                   Object elem) {
    return constructPolinAlgebra(alg, alg.elementIndex(elem));
  }

  public static SmallAlgebra constructPolinAlgebra(SmallAlgebra alg, 
                                                   final int elt) {
    return null;
  }

  public Operation polinizeOperation(final OperationSymbol sym) {
    final Operation op0 = topAlg.getOperation(sym);
    final Operation op1 = botAlg.getOperation(sym);
    final int topSize = topAlg.cardinality();
    return new AbstractOperation(sym, topAlg.cardinality() + botAlg.cardinality()) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int intValueAt(int[] args) {
        final int type = argType(args, topSize);
        if (type == 0) return op0.intValueAt(args);
        //if (type == 1) return op1.intValueAt(args);
        int[] argsx = new int[args.length];
        for (int i = 0; i < args.length; i++) {
          if (args[i] < topSize) argsx[i] = args[i];
          else {
            argsx[i] = map.intValueAt(new int[] {args[i] - topSize});
          }
        }
        return topSize + op1.intValueAt(argsx);
      }
    };


  }


  /**
   * Gives 0 if all are in topAlg; 1 is all are in botAlg; else 2.
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

  public CongruenceLattice con() {
System.out.println("this is " +  this);
System.out.println("this operations " +  operations());

    if (con == null) con = new CongruenceLattice(this);
    return con;
  }

  public SubalgebraLattice sub() {
    if (sub == null) sub = new SubalgebraLattice(this);
    return sub;
  }


  public static void main(String[] args) throws java.io.IOException {
    SmallAlgebra lat2 = null;
    try {
      lat2 = org.uacalc.io.AlgebraIO.readAlgebraFile(
               "/home/ralph/Java/Algebra/algebras/lat2.xml");
    }
    catch(Exception e) { e.printStackTrace(); }
    SmallAlgebra alg = new PolinLikeAlgebra("pol", lat2, lat2, null, 1, 1);
    SmallAlgebra alg2 = new PolinLikeAlgebra("pol", alg, alg, null, 1, 1);
    org.uacalc.io.AlgebraIO.writeAlgebraFile(alg2, "/tmp/newpolin2.xml");
    System.out.println("con size = " + alg2.con().cardinality());
    LatDrawer.drawLattice(new BasicLattice("", alg2.con(), true));
  }

}







 //public PolinLikeAlgebra(String name, final SmallAlgebra topAlg,
                          //final SmallAlgebra botAlg,
                          //Operation map, final int c0, final int c1) {

