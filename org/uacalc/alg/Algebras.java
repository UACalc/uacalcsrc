/* Algebras.java 2001/09/02  */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
//import org.apache.log4j.*;
import java.util.logging.*;

/**
 * A class with static methods for algebras.
 *
 *
 *
 */
public class Algebras {

  static Logger logger = Logger.getLogger("org.uacalc.alg.Algebras");
  static {
    logger.setLevel(Level.FINER);
    //BasicConfigurator.configure();
  }

  // make sure the class cannot be instantiated.
  private Algebras() {}

  /**
   * This will find a near unamimity term of the given arity
   * if one exits; otherwise it return <tt>null</tt>.
   */
  public static Term findNUF(SmallAlgebra alg, int arity) {
    return Malcev.findNUF(alg, arity);
  }

  /**
   * This returns a list of Jonsson terms witnessing or null if 
   * the algebra does generate a congruence distributive variety.
   * It is guarenteed to be the least number of terms possible.
   */
  public static List jonssonTerms(SmallAlgebra alg) {
    return Malcev.jonssonTerms(alg);
  }

  /**
   * If this algebra generates a distributive variety, this returns
   * the minimal number of Jonsson terms; otherwise it returns -1,
   * but it is probably better to <tt>jonssonTerms</tt> and get
   * get the actual terms.
   * So congruence distributivity can be tested by seeing if this 
   * this is positive. If the algebra has only one element, it returns 2.
   * For a lattice it returns 3. For Miklos Marioti's 5-ary near unanimity
   * operation on a 4 element set, it returns 7 (the maximum possible).
   */
  public static int jonssonLevel(SmallAlgebra alg) {
    return Malcev.jonssonLevel(alg);
  }

  public static boolean isEndomorphism(Operation endo, SmallAlgebra alg) {
    for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
      Operation op = (Operation)it.next();
      if (! Operations.commutes(endo, op)) {
        logger.finer(op + " failed to commute with " + endo);
        return false;
      }
    }
    return true;
  }

  public static SmallAlgebra makeRandomAlgebra(int n, int[] simType) {
    List ops = makeRandomOps(n, simType);
    // the second argument is the size of the algebra.
    return new BasicAlgebra(
                   "RAlg" + n + ArrayString.toString(simType), n, ops);
  }

  /**
   */
  public static List<Operation> makeRandomOps(final int n, 
                                              final int[] simType) {
    //Random random = new Random(1);
    Random random = new Random();
    final int len = simType.length;
    List<Operation> ops = new ArrayList<Operation>(len);
    for (int i = 0; i < len; i++) {
      ops.add(makeRandomOp(n, simType[i], random, i));
    }
    return ops;
  }
    
  public static Operation makeRandomOp(final int n, final int arity, 
                                                    Random random, int num) {
    int h = 1;
    for (int i = 0; i < arity; i++) {
      h = h * n;
    }
    final int[] values = new int[h];
    for (int i = 0; i < h; i++) {
      values[i] = random.nextInt(n);
    }
    return Operations.makeIntOperation("r" + num, arity, n, values);
  }

  public static void main(String[] args) {
    if (args.length == 0) return;
    SmallAlgebra alg0 = null;
    int arity = 3;
    try {
      alg0 = (SmallAlgebra)org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
      if (args.length > 1) {
        arity = Integer.parseInt(args[1]);
      }
    }
    catch (Exception e) {}
    //int level = Algebras.jonssonLevel(alg0);
    //System.out.println("level is " + level);
    Term t = findNUF(alg0, arity);
    if (t == null) System.out.println("there is no NUF with arity " + arity);
    else System.out.println("the alg has a NUF of arity " + arity + ": " + t);
  }



}







