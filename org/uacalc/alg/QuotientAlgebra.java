/* QuotientAlgeba.java (c) 2003/07/12  Ralph Freese */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;

/**
 * This class represents a quotient algebra of a <tt>SmallAlgebra</tt>.
 * The elements are just the elements of the superAlgebra corresponding
 * to the representatives of the congruence. We may want to make special
 * element objects having an element of the super algebra and the congruence.
 *
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class QuotientAlgebra extends GeneralAlgebra implements SmallAlgebra {

  protected final SmallAlgebra superAlgebra;
  protected final int[] representatives;
  protected Partition congruence;

  public QuotientAlgebra(SmallAlgebra alg, Partition congruence) {
    this("", alg, congruence);
  }

  /**
   * Form the quotient algebra of the super algebra by congruence.
   */
  public QuotientAlgebra(String name, SmallAlgebra alg, Partition congruence) {
    super(name);
    superAlgebra = alg;
    this.congruence = congruence;
    representatives = congruence.representatives();
    size = representatives.length;
    universe = makeUniverse();
    makeOperations();
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
            // maybe make a now Object to represent a/\theta!!!!!!!!!!
            // for now use the "representatives" 
            return superAlgebra.getElement(congruence.representative(
                            superAlgebra.elementIndex(opx.valueAt(args))));
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
              argsx[i] = representatives[args[i]];
            }
            return Arrays.binarySearch(representatives,
                            congruence.representative(opx.intValueAt(argsx)));
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
   * Get the congruence on the super algebra giving this algebra.
   */
  public Partition getCongruence() {
    return congruence;
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
    return congruence.representative(superAlgebra.elementIndex(obj));
  }

  public Object getElement(int index) {
    return new QuotientElement(this, index);
  }


  // do something ??
  public List getUniverseList() { return null; }
  public Map getUniverseOrder() { return null; }

  /**
   * Notes: this is need a class for congruence classes.
   * Something that will print like a/\theta.
   */
  protected Set makeUniverse() {
    return new AbstractSet() {
        public int size() { return size; }
        public boolean contains(Object obj) {
          QuotientElement elt = null;
          try {
            elt = (QuotientElement)obj;
          }
          catch (ClassCastException e) { 
            return false;
          }
          if (elt.getAlgebra().equals(QuotientAlgebra.this)) return true;
          return false;
        }
        // should check if each has an iterator and make one
        public Iterator iterator() {
          throw new UnsupportedOperationException();
        }
      };
  }

  public static void main(String[] args) throws java.io.IOException,
                                   org.uacalc.io.BadAlgebraFileException {
    if (args.length == 0) return;
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    System.out.println("Its size is " + alg.cardinality());
    SmallAlgebra alg2 = new QuotientAlgebra(alg, alg.con().Cg(0,2));
    System.out.println("The alg2 \n" + alg2);
    System.out.println("Its size is " + alg2.cardinality());
    org.uacalc.io.AlgebraWriter writer
         = new org.uacalc.io.AlgebraWriter(alg2, "/tmp/goo.xml");
    writer.writeAlgebraXML();

    

/*
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

    alg2.makeOperationTables();
    tf = new TypeFinder(alg2);
    t = System.currentTimeMillis();
    k = alg2.con().joinIrreducibles().size();
    t = System.currentTimeMillis() - t;
    System.out.println("number of jis is " + k);
    System.out.println("to find the jis it took " + t);
    t = System.currentTimeMillis();
    //System.out.println("size " + alg.con().universe().size());
    types = tf.findTypeSet();
    t = System.currentTimeMillis() - t;
    System.out.println("type set = " + types);
    System.out.println("it took " + t);
*/

/*
    ArrayList lst = new ArrayList();
    lst.add(alg);
    lst.add(alg);
    lst.add(alg);
    lst.add(alg);
    System.out.println("prod of " + lst.size() + " algebras");
    Algebra alg2 = new QuotientAlgebra(lst);
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


