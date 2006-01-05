/* FreeAlgebra.java (c) 2005/06/23  Ralph Freese */

package org.uacalc.alg;

import java.util.*;
import java.util.logging.*;
import java.math.BigInteger;
import org.uacalc.util.*;
import org.uacalc.io.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;

/**
 * This class represents a subalgebra of a direct product 
 * of <tt>SmallAlgebra</tt>s. It allows one to construct such an
 * algebra even though the direct product may be too big to be a
 * <tt>SmallAlgebra</tt>.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class FreeAlgebra extends SubProductAlgebra implements SmallAlgebra {

  static Logger logger = Logger.getLogger("org.uacalc.alg.FreeAlgebra");
  static {
    logger.setLevel(Level.FINER);
  }

/*
  protected BigProductAlgebra productAlgebra;
  protected List gens; // a list of IntArray's
  protected List univ; // a list of iIntArray's
  protected HashMap univHashMap; // a map from IntArray's of elements of the 
                                 // univ to Integers (the index).
*/

  /**
   * Consturct a free algebra without giving it a name.
   */
  public FreeAlgebra(SmallAlgebra alg, int numberOfGens) {
    this(null, alg, numberOfGens);
  }

  /**
   * Consturct the free algebra over <tt>alg</tt> 
   * with <tt>numberOfGens</tt> generators.
   */
  public FreeAlgebra(String name, SmallAlgebra alg, int numberOfGens) {
    super(name);
    final int n = alg.cardinality();
    int s = 1;
    for (int  i = 0; i < numberOfGens; i++) {
      s = s * n;
    }
    logger.fine("size of the over product is " + s);
    productAlgebra = new BigProductAlgebra(alg, s);
    int[] projs = new int[numberOfGens];
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(projs, n-1);
    gens = new ArrayList(numberOfGens);
    for (int i = 0; i < numberOfGens; i++) {
      gens.add(new IntArray(s));
    }
    for (int k = 0; k < s; k++) {
      for (int i = 0; i < numberOfGens; i++) {
        final IntArray ia = (IntArray)gens.get(i);
        ia.set(k, projs[i]);
      }
      inc.increment();
    }

    univ = productAlgebra.sgClose(gens);
    size = univ.size();
    logger.info("free algebra size = " + size);
    univHashMap = new HashMap(size);
    int k = 0;
    for (Iterator it = univ.iterator(); it.hasNext(); k++) {
      univHashMap.put(it.next(), new Integer(k));
    }
    universe = new HashSet(univ);
    makeOperations();
  }

  /**
   * Construct a FreeAlgebra when the gens and univ are already
   * given. Useful for reading back from a file without calculating
   * the universe again.
  */
  public FreeAlgebra(String name, BigProductAlgebra prod,
                                  List gens, List univList) {
    super(name, prod, gens, univList);
  }



  public static void main(String[] args) throws java.io.IOException,
                                   org.uacalc.io.BadAlgebraFileException {
    if (args.length == 0) return;
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    int pow = 2;
    if (args.length > 1) {
      try {
        pow = Integer.parseInt(args[1]);
      }
      catch (Exception e) {}
    }
    System.out.println("pow is " + pow);

    FreeAlgebra alg2 = new FreeAlgebra("test", alg, pow);
    AlgebraIO.writeAlgebraFile(alg2, "/tmp/fr" + pow + ".xml");
  }

}


