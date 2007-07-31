/* Subalgebra.java (c) 2003/07/12  Ralph Freese */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.sublat.*;

/**
 * This class represents a subalgebra of a <tt>SmallAlgebra</tt>.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class ReductAlgebra extends GeneralAlgebra implements SmallAlgebra {

  protected final SmallAlgebra superAlgebra;
  protected final List<Term> termList;

  public ReductAlgebra(SmallAlgebra alg, List<Term> termList) {
    this("", alg, termList);
  }

  /**
   * Form the subalgebra given the super algebra and the subuniverse.
   */
  public ReductAlgebra(String name, SmallAlgebra alg, List<Term> termList) {
    super(name);
    superAlgebra = alg;
    this.termList = termList;
    size = alg.cardinality();
    universe = alg.universe();
    operations = makeOperations(alg, termList);
  }

  private List makeOperations(SmallAlgebra alg, List<Term> terms) {
    final int k = terms.size();
    List operations = new ArrayList(k);
    for (int i = 0; i < k; i++) {
      Term term = terms.get(i);
      if (!term.isaVariable()) operations.add(term.interpretation(alg));
    }
    return operations;
  }

  public void makeOperationTables() {
    for (Iterator it = operations().iterator(); it.hasNext(); ) {
      ((Operation)it.next()).makeTable();
    }
  }

  public SmallAlgebra superAlgebra() {
    return superAlgebra;
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
    return superAlgebra.elementIndex(obj);
  }

  public Object getElement(int index) {
    return superAlgebra.getElement(index);
  }

  // do something ??
  public List getUniverseList() { return null; }
  public Map getUniverseOrder() { return null; }


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
/*
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
*/



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


