/* Subalgebra.java (c) 2003/07/12  Ralph Freese */

package org.uacalc.alg;

import java.util.*;
import java.math.BigInteger;
import org.uacalc.util.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;

/**
 * This class represents a subalgebra of a <tt>SmallAlgebra</tt>.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class Subalgebra extends GeneralAlgebra implements SmallAlgebra {

  protected final SmallAlgebra superAlgebra;
  protected final int[] univArray;

  public Subalgebra(SmallAlgebra alg, int[] univ) {
    this("", alg, univ);
  }

  public Subalgebra(SmallAlgebra alg, IntArray univ) {
    this("", alg, univ.getArray());
  }

  public Subalgebra(String name, SmallAlgebra alg, IntArray univ) {
    this(name, alg, univ.getArray());
  }

  /**
   * Form the subalgebra given the super algebra and the subuniverse.
   */
  public Subalgebra(String name, SmallAlgebra alg, int[] univ) {
    super(name);
    superAlgebra = alg;
    univArray = univ;
    size = univ.length;
    universe = makeUniverse();
    makeOperations();
  }

  /**
   * Find the index in this subalgebra of the element with index k
   * in the super algebra. If k is not in the subalgebra, a negative
   * number is returned.
   */
  public int index(int k) {
    return Arrays.binarySearch(univArray, k);
  }

  /**
   * Restrict a partition (or congruence) on the parent algebra to 
   * this subalgebra.
   */
  public BasicPartition restrictPartition(BasicPartition par) {
// this is totally untested 6/11/2005
    final int[] reps = par.representatives();
    final List[] blocks = new List[reps.length];
    final int[] univ = getSubuniverseArray();
    for (int i = 0; i < univ.length; i++) {
      final int r = par.representative(univ[i]);
      final int index = Arrays.binarySearch(reps, r);
      if (blocks[index] == null) blocks[index] = new ArrayList();
      blocks[index].add(new Integer(i));
    }
    final int[] arr = new int[univ.length];
    for (int i = 0; i < blocks.length; i++) {
      if (blocks[i] != null) {
        final List block = blocks[i];
        final int root = ((Integer)block.get(0)).intValue();
        arr[root] = - block.size();
        for (int j = 1; j < block.size(); j++) {
          arr[((Integer)block.get(j)).intValue()] = root;
        }
      }
    }
    return new BasicPartition(arr);
  }


  private void makeOperations() {
    final int k = superAlgebra.operations().size();
    operations = new ArrayList(k);
    for (int i = 0; i < k; i++) {
      final Operation opx = (Operation)superAlgebra.operations().get(i);
      final int arity = opx.arity();
      Operation op = new AbstractOperation(opx.symbol(), size) {
          // this is not tested yet
          public Object valueAt(List args) {
            return opx.valueAt(args);
          }
          Operation tableOp = null;
          public void makeTable() {
            int h = 1;
            for (int i = 0; i < arity; i++) {
              h = h * size;
            }
            int[] values = new int[h];
            for (int i = 0; i < h; i++) {
              values[i] = intValueAt(Horner.hornerInv(i, size, arity));
            }
            tableOp = Operations.makeIntOperation(symbol(), size, values);
          }
          final int[] argsx = new int[arity];
          public int intValueAt(int[] args) {
            if (tableOp != null) return tableOp.intValueAt(args);
            for (int i = 0; i < arity; i++) {
              argsx[i] = univArray[args[i]];
            }
            return index(opx.intValueAt(argsx));
          }
      };
      operations.add(op);
    }
  }

  public void makeOperationTables() {
    for (Iterator it = operations().iterator(); it.hasNext(); ) {
      ((Operation)it.next()).makeTable();
    }
  }

  public SmallAlgebra superAlgebra() {
    return superAlgebra;
  }

  /**
   * Get the subuniverse of the super algebra for this algebra.
   */
  public int[] getSubuniverseArray() {
    return univArray;
  }

  public CongruenceLattice con() {
    if (con == null) con = new CongruenceLattice(this);
    return con;
  }

  public SubalgebraLattice sub() {
    if (sub == null) sub = new SubalgebraLattice(this);
    return sub;
  }

  public int elementIndex(Object obj) {
    return index(superAlgebra.elementIndex(obj));
  }

  public Object getElement(int index) {
    return superAlgebra.getElement(univArray[index]);
  }

  // do something ??
  public List getUniverseList() { return null; }
  public Map getUniverseOrder() { return null; }


  protected Set makeUniverse() {
    return new AbstractSet() {
        public int size() { return size; }
        public boolean contains(Object obj) {
          if (!superAlgebra.universe().contains(obj)) return false;
          return index(superAlgebra.elementIndex(obj)) >= 0;
        }
        // should check if each has an iterator and make one
        public Iterator iterator() {
          throw new UnsupportedOperationException();
        }
      };
  }

  /**
   * This gives the congruence <tt>cong</tt> as a subalgebra of A^2.
   */
  public static SmallAlgebra congruenceAsAlgebra(SmallAlgebra alg, 
                                                        Partition cong) {
    return congruenceAsAlgebra("", alg, cong);
  }

  /**
   * This gives the congruence <tt>cong</tt> as a subalgebra of A^2.
   */
  public static SmallAlgebra congruenceAsAlgebra(String name, 
                                          SmallAlgebra alg, Partition cong) {
    final List univ = new ArrayList();
    final int n = alg.cardinality();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (cong.isRelated(i, j)) univ.add(new int[] {i, j});
      }
    }
    final int size = univ.size();
    final int[] univArr = new int[size];
    for (int i = 0; i < size ; i++) {
      univArr[i] = Horner.horner((int[])univ.get(i), n);
    }
    Arrays.sort(univArr);
    List algs =  new ArrayList(2);
    algs.add(alg);
    algs.add(alg);
    return new Subalgebra(name, new ProductAlgebra(algs), univArr);
  }


  public static void main(String[] args) throws java.io.IOException,
                                   org.uacalc.io.BadAlgebraFileException {
    if (args.length == 0) return;
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    System.out.println("Its size is " + alg.cardinality());
    SmallAlgebra alg2 = new Subalgebra(alg, new int[] {0, 1});
    System.out.println("The alg2 \n" + alg2);
    System.out.println("Its size is " + alg2.cardinality());
    TypeFinder tf = new TypeFinder(alg2);
    int k = alg2.con().joinIrreducibles().size();
    System.out.println("number of jis is " + k);
    org.uacalc.io.AlgebraWriter writer
         = new org.uacalc.io.AlgebraWriter(alg2, "/tmp/goo.xml");
    writer.writeAlgebraXML();



/*
    if (args.length == 0) return;
    System.out.println("reading " + args[0]);
    Algebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    ArrayList lst = new ArrayList();
    lst.add(alg);
    lst.add(alg);
    lst.add(alg);
    lst.add(alg);
    System.out.println("prod of " + lst.size() + " algebras");
    Algebra alg2 = new Subalgebra(lst);
alg2.makeOperationTables();
    TypeFinder tf = new TypeFinder(alg2);
    long t = System.currentTimeMillis();
    int k = alg2.con().joinIrreducibles().size();
    t = System.currentTimeMillis() - t;
    System.out.println("number of jis is " + k);
    System.out.println("to find the jis it took " + t);
    t = System.currentTimeMillis();
    //System.out.println("size " + alg.con().universe().size());
    HashSet types = tf.findTypeSet();
    t = System.currentTimeMillis() - t;
    System.out.println("type set = " + types);
    System.out.println("it took " + t);
*/

  }

}


