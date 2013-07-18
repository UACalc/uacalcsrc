/* BasicAlgebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationWithDefaultValue;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.Operations;
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
  public BasicAlgebra(String name, final int s, List<Operation> operations) {
    
    super(name, new AbstractSet() {
      public boolean contains(Object obj) {
        try {
          int k = ((Integer) obj).intValue();
          if (0 <= k && k < s) return true;
        }
        catch (ClassCastException ex) {
        }
        return false;
      }

      public int size() {
        return s;
      }

      public Iterator iterator() {
        return new Iterator() {
          int current = 0;

          public boolean hasNext() {
            return current < s;
          }

          public Object next() {
            return new Integer(current++);
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    }, operations);
  }

  /**
   * This constructs a SmallAlgebra from a ordered list of elements. The
   * operations need to have intValueAt(int[]) implemented.
   */
  public BasicAlgebra(String name, List univ, List<Operation> operations) {
// not tested yet
    super(name, new HashSet(univ));
    this.universeList = univ;
    universeOrder = new HashMap(univ.size());
    int k = 0;
    for (Iterator it = univ.iterator(); it.hasNext() ; k++) {
      universeOrder.put(it.next(), new Integer(k));
    }
    List<Operation> ops = new ArrayList<Operation>(operations.size());
    for (final Operation op : operations) {
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

  public List getUniverseList() { return universeList; }
  public void setUniverseList(List lst) { universeList = lst; }

  public Map getUniverseOrder() { return universeOrder; }
  public void setUniverseOrder(Map ord) { universeOrder = ord; }

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
  
  public void resetConAndSub() {
    con = null;
    sub = null;
  }
  
  public void convertToDefaultValueOps() {
    List<Operation> ops = operations();
    List<Operation> opsDV = new ArrayList<Operation>(ops.size());
    for (Operation op : ops) {
      if (!(op instanceof OperationWithDefaultValue)) {
        opsDV.add(new OperationWithDefaultValue(op));
      }
      else opsDV.add(op);
    }
    setOperations(opsDV);
    //operations = opsDV;
  }
  
  
  
  public AlgebraType algebraType() {
    return AlgebraType.BASIC;
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


