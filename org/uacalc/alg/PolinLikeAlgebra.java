/* Malcev.java 2001/09/02  */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.sublat.*;
//import org.apache.log4j.*;
import java.util.logging.*;

import org.uacalc.lat.*;
import org.uacalc.ui.*;

/**
 * Given a homomorphism f: A to B, this constructs a Polin type algebra
 * on the disjoint union of A and B. 
 * The elements are ordered by the elements of B first followed by those
 * of A.
 *
 * @version $Id$
 */
public class PolinLikeAlgebra extends GeneralAlgebra implements SmallAlgebra {

  static Logger logger = Logger.getLogger("org.uacalc.alg.PolinConstructions");
  static {
    logger.setLevel(Level.FINER);
  }

  protected SmallAlgebra botAlg;
  protected SmallAlgebra topAlg;
  protected Operation map;
  //protected Object const0;
  //protected Object const1;
  protected int botConstIndex;
  protected int topConstIndex;
  

  public PolinLikeAlgebra(String name, final SmallAlgebra topAlg, 
                          final SmallAlgebra botAlg, Operation map, 
                          final int topConstIndex, final int botConstIndex) {
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
    this.topConstIndex = topConstIndex;
    this.botConstIndex = botConstIndex;
    this.map = map;  // a map from topAlg to botAlg
    setup();
  }

  private void setup() {
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
          if (args[0] < botSize) return botSize + topConstIndex;
          return botConstIndex;
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

  /**
   * This is not implemented; don't use it.
   */
  public static SmallAlgebra constructPolinAlgebra(SmallAlgebra alg, 
                                                   Object elem) {
    return constructPolinAlgebra(alg, alg.elementIndex(elem));
  }

  /**
   * This is not implemented; don't use it.
   */
  public static SmallAlgebra constructPolinAlgebra(SmallAlgebra alg, 
                                                   final int elt) {
    return null;
  }

  public Operation polinizeOperation(final OperationSymbol sym) {
    final Operation op0 = botAlg.getOperation(sym);
    final Operation op1 = topAlg.getOperation(sym);
    final int botSize = botAlg.cardinality();
    return new AbstractOperation(sym, topAlg.cardinality() 
                                                + botAlg.cardinality()) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int intValueAt(int[] args) {
        final int type = argType(args, botSize);
        if (type == 0) return op0.intValueAt(args);
        int[] argsx = new int[args.length];
        if (type == 1) {
          for (int i = 0; i < args.length; i++) {
            argsx[i] = args[i] - botSize;
          }
          return botSize + op1.intValueAt(argsx);
        }
        for (int i = 0; i < args.length; i++) {
          if (args[i] < botSize) argsx[i] = args[i];
          else {
            argsx[i] = map.intValueAt(new int[] {args[i] - botSize});
          }
        }
        return op0.intValueAt(argsx);
      }
    };


  }


  /**
   * Gives 0 if all are in botAlg; 1 is all are in topAlg; else 2.
   */
  private static int argType(final int[] args, final int botSize) {
    if (args.length == 0) return 0;
    if (args[0] < botSize) {
      for (int i = 1; i < args.length; i++) {
        if (args[i] >= botSize) return 2;
      }
      return 0;
    }
    for (int i = 1; i < args.length; i++) {
      if (args[i] < botSize) return 2;
    }
    return 1;
  }

  public CongruenceLattice con() {
    if (con == null) con = new CongruenceLattice(this);
    return con;
  }

  public SubalgebraLattice sub() {
    if (sub == null) sub = new SubalgebraLattice(this);
    return sub;
  }


  public static void main(String[] args) throws java.io.IOException {
    SmallAlgebra alg0 = null;
    //try {
    //  lat2 = org.uacalc.io.AlgebraIO.readAlgebraFile(
    //           "/home/ralph/Java/Algebra/algebras/lat2.xml");
    //}
    try {
      alg0 = org.uacalc.io.AlgebraIO.readAlgebraFile(
               "/home/ralph/Java/Algebra/algebras/3polidpent2.xml"
               //"/home/ralph/Java/Algebra/algebras/polin.xml"
               );
    }
    catch(Exception e) { e.printStackTrace(); }
    //SmallAlgebra alg = new PolinLikeAlgebra("pol", lat2, lat2, null, 1, 1);
    SmallAlgebra alg = new PolinLikeAlgebra("pol", alg0, alg0, null, 3, 3);
    SmallAlgebra alg2 = new PolinLikeAlgebra("pol", alg, alg, null, 7, 7);
    //org.uacalc.io.AlgebraIO.writeAlgebraFile(alg, "/tmp/newidempolin.xml");
    System.out.println("con size = " + alg2.con().cardinality());
    //LatDrawer.drawLattice(new BasicLattice("", alg.con(), true));
    //LatDrawer.drawLattice(new BasicLattice("", alg2.con(), true));
    //Partition p = alg2.con().Cg(0,1).join(alg2.con().Cg(2,3));
    //SmallAlgebra q = new QuotientAlgebra(alg2,p);
    //LatDrawer.drawLattice(new BasicLattice("", alg2.con(), true));
    List terms = Malcev.hagemannMitschkeTerms(alg);
    for (Iterator it = terms.iterator(); it.hasNext(); ) {
      System.out.println(it.next());
    }
  }

}

