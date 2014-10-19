/* PowerAlgebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.*;
import java.util.logging.*;

import org.uacalc.util.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.sublat.SubalgebraLattice;

/**
 * This class represents the direct product of <tt>SmallAlgebra</tt>s.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class PowerAlgebra extends ProductAlgebra implements SmallAlgebra {

  static Logger logger = Logger.getLogger("org.uacalc.alg.PowerAlgebra");
  static {
    logger.setLevel(Level.FINER);
  }

  protected final SmallAlgebra root;

  protected final int rootSize;

  public PowerAlgebra(SmallAlgebra alg, int power) {
    this("", alg, power);
  }

  /**
   * Construct the direct power of an algebra.
   */
  public PowerAlgebra(String name, SmallAlgebra alg, int power) {
    // put a check that size < maxInteger size
    // do we allow power = 0 ?
    super(name);
    root = alg;
    rootSize = alg.cardinality();
    List<SmallAlgebra> algs = new ArrayList<SmallAlgebra>(power);
    for (int i = 0; i < power; i++) {
      algs.add(alg);
    }
    algebras = algs;
    numberOfProducts = algs.size();
    sizes = new int[numberOfProducts];
    for (int i = 0; i < numberOfProducts; i++) {
      sizes[i] = rootSize;
    }
    size = calcCard(sizes);
    universe = makeCartesianProduct(algs);
    makeOperations(); 
  }

  public SmallAlgebra getRoot() { return root; }
  
  public SmallAlgebra parent() { return root; }
  
  public List<SmallAlgebra> parents() {
    List<SmallAlgebra> ans = new ArrayList<SmallAlgebra>();
    ans.add(root);
    return ans; 
  }

  public int getPower() { return numberOfProducts; }
  
  public CongruenceLattice con() {
    if (con == null) con = new CongruenceLattice(this);
    return con;
  }

  public SubalgebraLattice sub() {
    if (sub == null) sub = new SubalgebraLattice(this);
    return sub;
  }
  
  public AlgebraType algebraType() {
    return AlgebraType.POWER;
  }

  public static void main(String[] args) throws java.io.IOException,
                                   org.uacalc.io.BadAlgebraFileException {
    if (args.length == 0) {
      SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(
          "/Users/ralph/Java/Algebra/algebras/m3.ua");
      SmallAlgebra alg2 = new PowerAlgebra(alg, 3);
      List<SmallAlgebra> algs = new ArrayList<>(3);
      algs.add(alg);
      algs.add(alg);
      algs.add(alg);
      BigProductAlgebra alg3 = new BigProductAlgebra(algs);
      System.out.println("parents: " + alg2.parents());
      int[][] arg = new int[][] {{2,3,1},{1,2,0}};
      Operation op = alg2.operations().get(0);
      System.out.println(Arrays.toString(op.valueAt(arg)));
      SmallAlgebra pow = new PowerAlgebra(alg, 2);
      op = pow.operations().get(0);
      //ArrayList<int[]> argsPow = new ArrayList<>();
      List<Integer> a0 = new ArrayList<>();
      List<Integer> a1 = new ArrayList<>();
      a0.add(2); a0.add(3); a0.add(1);
      a1.add(1); a1.add(2); a1.add(0);
      List<List<Integer>> argsInt = new ArrayList<>();
      argsInt.add(a0);
      argsInt.add(a1);
      
      //argsPow.add(new int[] {2,3,1});
      //argsPow.add(new int[] {1,3,0});
      
      
      System.out.println(op.valueAt(argsInt));
      return;
    }
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
//    ArrayList lst = new ArrayList();
//    lst.add(alg);
//    lst.add(alg);
////    lst.add(alg);
////    lst.add(alg);
//    System.out.println("prod of " + lst.size() + " algebras");
    SmallAlgebra alg2 = new PowerAlgebra(alg, 3);

    org.uacalc.io.AlgebraWriter writer 
         = new org.uacalc.io.AlgebraWriter((SmallAlgebra)alg2, "/tmp/goo.xml");
    writer.writeAlgebraXML();
//    
//
//    SmallAlgebra alg3 = new PowerAlgebra(lst);
//    alg2.makeOperationTables();
//    TypeFinder tf = new TypeFinder(alg2);
//    long t = System.currentTimeMillis();
//    int k = alg2.con().joinIrreducibles().size();
//    t = System.currentTimeMillis() - t;
//    System.out.println("number of jis is " + k);
//    System.out.println("to find the jis it took " + t);
//    t = System.currentTimeMillis();
//    //System.out.println("size " + alg.con().universe().size());
//    HashSet types = tf.findTypeSet();
//    t = System.currentTimeMillis() - t;
//    System.out.println("type set = " + types);
//    System.out.println("it took " + t);
//    System.out.println("--- Without tables ---");
//    tf = new TypeFinder(alg3);
//    t = System.currentTimeMillis();
//    k = alg3.con().joinIrreducibles().size();
//    t = System.currentTimeMillis() - t;
//    System.out.println("number of jis is " + k);
//    System.out.println("to find the jis it took " + t);
//    t = System.currentTimeMillis();
//    //System.out.println("size " + alg.con().universe().size());
//    types = tf.findTypeSet();
//    t = System.currentTimeMillis() - t;
//    System.out.println("type set = " + types);
//    System.out.println("it took " + t);
//
//
  }

}


