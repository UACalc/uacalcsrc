/* BasicAlgebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;

/**
 * This class represents SmallAlgebra's.
 * Such algebras have a map from {0, ..., n-1} and the elements of
 * the algebra.  The operations are done on the ints and converted back
 * the elements.
 */
public class BasicAlgebra extends GeneralAlgebra implements SmallAlgebra {

  /**
   * Ordered list of the universe.
   */
  protected List universeList;

  /**
   * A map from the elements to their order in the universe list.
   */
  protected Map universeOrder;

  /**
   * If a universe is not given, use Integers.
   */
  public BasicAlgebra(String name, final int s, List operations) {
    super(name, 
          new AbstractSet() {
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
          operations);
  }

  /**
   * This constructs a SmallAlgebra from a ordered list of elements.
   * The operations need to have intValueAt(int[]) implemented.
   */
  public BasicAlgebra(String name, List univ, List operations) {
// not tested yet
    super(name, new HashSet(univ));
    this.universeList = univ;
    universeOrder = new HashMap(univ.size());
    int k = 0;
    for (Iterator it = univ.iterator(); it.hasNext() ; k++) {
      universeOrder.put(it.next(), new Integer(k));
    }
    List ops = new ArrayList(operations.size());
    for (Iterator it = operations.iterator(); it.hasNext() ; ) {
      final Operation op = (Operation)it.next();
      ops.add(new AbstractOperation(op.symbol(), univ.size()) {
         public int intValueAt(int[] args) {
           return op.intValueAt(args);
         }
         public Object valueAt(List args) {
           int[] argv = new int[args.size()];
           for (int i = 0 ; i < args.size(); i++) {
             argv[i] =  ((Integer)universeOrder.get(args.get(i))).intValue();
           }
           return universeList.get(op.intValueAt(argv));
         }
      });
    }
    setOperations(ops);
  }

  /**
   * If this is true, there is no real universe; we just use 
   * Integers.
   */
  public boolean intUniverse() {
    return universeList == null;
  }

  public int elementIndex(Object obj) {
    if (!intUniverse()) return ((Integer)universeOrder.get(obj)).intValue();
    try {
      int k = ((Integer)obj).intValue();
      if (0 <= k && k < cardinality()) return k;
      return -1;
    } 
    catch (ClassCastException ex) {
      return -1;
    }
  }

  public Object getElement(int index) {
    if (!intUniverse()) return universeList.get(index);
    return new Integer(index);
  }

  public CongruenceLattice con() {
    if (con == null) con = new CongruenceLattice(this);
    return con;
  }

  public SubalgebraLattice sub() {
    if (sub == null) sub = new SubalgebraLattice(this);
    return sub;
  }

  public static void main(String[] args) {
    Operation op = Operations.makeBinaryIntOperation(
                     new OperationSymbol("*", 2), 3,
                     new int[][] {{0, 0, 0}, {0, 1, 2}, {0,2,1}});
    Operation op2 = Operations.makeBinaryIntOperation(
                     new OperationSymbol("*", 2), 3,
                     new int[][] {{0, 1, 2}, {1, 2, 2}, {0,2,2}});
    //Operation op = new AbstractOperation("*", 2, 3
    List ops = new ArrayList();
    ops.add(op);
    SmallAlgebra alg = new BasicAlgebra("test1", 3, ops);
    System.out.println("0*2 = " + op.intValueAt(new int[] {0, 2}));
    System.out.println("2*0 = " + op.intValueAt(new int[] {2, 0}));
/*
    Operation mulModN = new AbstractOperation("*", 2, 100) {
        public Object valueAt(List args) {
          BigInteger arg0 = (BigInteger)args.get(0);
          BigInteger arg1 = (BigInteger)args.get(1);
          return arg0.multiply(arg1);
        }
      } ;
    Operation addModN = new AbstractOperation("+", 2, 100) {
        public Object valueAt(List args) {
          BigInteger arg0 = (BigInteger)args.get(0);
          BigInteger arg1 = (BigInteger)args.get(1);
          return arg0.add(arg1);
        }
      } ;
    List modOps = new ArrayList();
    modOps.add(addModN);
    modOps.add(mulModN);
    //Algebra RmodN = new Algebra() {};
*/
    System.out.println("Cg(0,1) is " + alg.con().Cg(0,1));
    System.out.println("Cg(1,2) is " + alg.con().Cg(1,2));
  }

}


