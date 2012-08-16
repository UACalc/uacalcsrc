/* Algebras.java 2001/09/02  */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.alg.QuotientAlgebra;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.Operations;
import org.uacalc.alg.op.SimilarityType;
import org.uacalc.alg.sublat.*;
import org.uacalc.terms.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.ui.tm.ProgressReport;
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
   * WARNING: this is not complete. Make an algebra from the unary operations respecting a list pars
   * of partitions.
   * 
   * @param pars      a list of partitions
   * @param decomp    a sublist whose meet is 0
   * @return          the algebra
   */
  public static SmallAlgebra unaryCloneAlgFromPartitions(List<Partition> pars, List<Partition> decomp) {
    final int size = pars.get(0).universeSize();
    final int k = decomp.size();
    final int[] sizes = new int[k];
    int index = 0;
    for (Partition par : decomp) {
      sizes[index++] = par.representatives().length;
    }
    Map<Integer,IntArray> int2vec = new HashMap<Integer,IntArray>();
    Map<IntArray,Integer> vec2int = new HashMap<IntArray,Integer>();
    for (int i = 0; i < size; i++) {
      final int[] vec = new int[k];
      for (int j = 0; j < k; j++) {
        vec[j] = decomp.get(j).representative(i);
      }
      final IntArray ia = new IntArray(vec);
      int2vec.put(i, ia);
      vec2int.put(ia, i);
    }
    // here
    return null;
  }
  
  /**
   * Make the unary algebra whose operations are the clone of unary
   * operation respecting every partition in pars and also eta0 and
   * eta1, which meet and join to 0 and 1 and permute. 
   *  
   * @param pars
   * @param eta0
   * @param eta1
   * @return
   */
  public static SmallAlgebra unaryCloneAlgFromPartitions(List<Partition> pars, Partition eta0, Partition eta1) {
    String f = "f_";
    final int size = pars.get(0).universeSize();
    final NavigableSet<IntArray> lst = unaryClone(pars, eta0, eta1);
    //System.out.println("TreeSet: " + lst);
    //IntArray ia0 = new IntArray(new int[] {0, 0, 4, 0, 0, 0, 0});
    //System.out.println("ceiling 0 0 4 0 ... = " + lst.ceiling(ia0));
    System.out.println("number of ops in the unary clone is " + lst.size());
    final List<Operation> ops = new ArrayList<Operation>(lst.size());
    int i = 0;
    for (IntArray ia : lst) {
      ops.add(Operations.makeIntOperation(f + i, 1, size, ia.getArray()));
      i++;
    }
    return new BasicAlgebra("", size, ops);
  }
  
  public static NavigableSet<IntArray> unaryClone(final List<Partition> pars, 
                                                   final Partition eta0, final Partition eta1) {
    final int size = pars.get(0).universeSize();
    Map<Integer,IntArray> int2vec = new HashMap<Integer,IntArray>();
    Map<IntArray,Integer> vec2int = new HashMap<IntArray,Integer>();
    for (int i = 0; i < size; i++) {
      final int[] vec = new int[2];
      vec[0] = eta0.blockIndex(i);
      vec[1] = eta1.blockIndex(i);
      //vec[0] = eta0.representative(i);
      //vec[1] = eta1.representative(i);
      final IntArray ia = new IntArray(vec);
      int2vec.put(i, ia);
      vec2int.put(ia, i);
    }
    System.out.println("v2i: " + vec2int);
    System.out.println("i2v: " + int2vec);
    final int size0 = eta0.numberOfBlocks();
    final int size1 = eta1.numberOfBlocks();
    final IntArray f0 = new IntArray(size0);
    final IntArray f1 = new IntArray(size1);
    final int n = eta0.universeSize();
    final NavigableSet<IntArray> ans = new TreeSet<IntArray>(IntArray.lexicographicComparitor());
    unaryCloneAux(f0, f1, size0, size1, 0, 0, n, true, ans, int2vec, vec2int, pars);
    return ans;
  }
  
  private static void unaryCloneAux(final IntArray f0, final IntArray f1,
                                                  final int size0, final int size1,
                                                  final int k0, final int k1, final int n,
                                                  final boolean zeroFirst,
                                                  //IntArray partialFn,
                                                  final NavigableSet<IntArray> ans,
                                                  final Map<Integer,IntArray> int2vec,
                                                  final Map<IntArray,Integer> vec2int,
                                                  final List<Partition> pars) {
    //System.out.println("f0: " + f0 + " f1: " + f1 + " k0: " + k0 + " k1: " + k1 + " zeroFirst: " + zeroFirst);
    if (k0 * k1 == n) {
      IntArray copy = new IntArray(n);
      final IntArray scratch = new IntArray(2);
      for (int i = 0; i < n; i++) {
        final IntArray argv = int2vec.get(i);
        scratch.set(0, f0.get(argv.get(0)));
        scratch.set(1, f1.get(argv.get(1)));
        copy.set(i, vec2int.get(scratch));
      }
      ans.add(copy);
      //System.out.println(copy);
      return;
    }
    final int size = zeroFirst ? size0 : size1;
    for (int value = 0; value < size; value++) {
      if (respects(value, f0, f1, size0, size1, k0, k1, n, zeroFirst, int2vec, vec2int, pars)) {
        boolean newZeroFirst = zeroFirst;
        if (zeroFirst) {
          f0.set(k0, value);
          if (k1 < size1) newZeroFirst = false;
        }
        else {
          f1.set(k1, value);
          if (k0 < size0) newZeroFirst = true;
        }
        unaryCloneAux(f0, f1, size0, size1, 
                      zeroFirst ? k0 + 1: k0, 
                      zeroFirst ? k1 : k1 + 1, 
                      n, newZeroFirst, ans, int2vec, vec2int, pars);
      }
    }
    return;
  }
  
  private static boolean respects(final int value,
                                  final IntArray f0, final IntArray f1,
                                  final int size0, final int size1,
                                  final int k0, final int k1, final int n,
                                  final boolean zeroFirst,
                                  final Map<Integer,IntArray> int2vec,
                                  final Map<IntArray,Integer> vec2int,
                                  final List<Partition> pars) {
    //System.out.println("f0: " + f0 + " f1: " + f1 + " k0: " + k0 + " k1: " + k1);
    final IntArray scratch = new IntArray(2);
    if (zeroFirst) {
      for (int j = 0; j < k1; j++) {
        final int m = getScratchValue(scratch, k0, j, vec2int);
        final int image = getScratchValue(scratch, value, f1.get(j), vec2int);
        for (int w = 0; w < j; w++) {
          final int k = getScratchValue(scratch, k0, w, vec2int);
          int kImg = -1;
          for (Partition par : pars) {
            final int r = par.representative(m);
            if (r == par.representative(k)) {
              if (kImg == -1) kImg = getScratchValue(scratch, value, f1.get(w), vec2int);
              if (!par.isRelated(image, kImg)) return false;
            }
          }
        }
        for (int u = 0; u < k0; u++) {
          for (int v = 0; v < k1; v++) {
            final int uv = getScratchValue(scratch, u, v, vec2int);
            int uvImg = -1;
            for (Partition par : pars) {
              final int r = par.representative(m);
              if (r == par.representative(uv)) {
                if (uvImg == -1) uvImg = getScratchValue(scratch, f0.get(u), f1.get(v), vec2int);
                if (!par.isRelated(image, uvImg)) return false;
              }
            }
          }
        } 
      }
    }
    else {
      for (int i = 0; i < k0; i++) {
        //System.out.println("i: " + i + " k1: " + k1);
        final int m = getScratchValue(scratch, i, k1, vec2int);
        final int image = getScratchValue(scratch, f0.get(i), value, vec2int);
        for (int w = 0; w < i; w++) {
          final int k = getScratchValue(scratch, w, k1, vec2int);
          int kImg = -1;
          for (Partition par : pars) {
            final int r = par.representative(m);
            if (r == par.representative(k)) {
              if (kImg == -1) kImg = getScratchValue(scratch, f0.get(w), value, vec2int);
              if (!par.isRelated(image, kImg)) return false;
            }
          }
        }
        for (int u = 0; u < k0; u++) {
          for (int v = 0; v < k1; v++) {
            final int uv = getScratchValue(scratch, u, v, vec2int);
            int uvImg = -1;
            for (Partition par : pars) {
              final int r = par.representative(m);
              if (r == par.representative(uv)) {
                if (uvImg == -1) uvImg = getScratchValue(scratch, f0.get(u), f1.get(v), vec2int);
                if (!par.isRelated(image, uvImg)) return false;
              }
            }
          }
        }
      }
    }
    return true;
  }

  private static int getScratchValue(final IntArray scratch, final int i, final int j, 
                                     final Map<IntArray,Integer> vec2int) {
    scratch.set(0, i);
    scratch.set(1, j);
    return vec2int.get(scratch);
  }
  
  
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
  
  public static boolean isHomomorphism(final int[] map, 
                                       final SmallAlgebra alg0, 
                                       final SmallAlgebra alg1) {
    for (Operation op0 : alg0.operations()) {
      Operation op1 = alg1.getOperation(op0.symbol());
      if (! Operations.commutes(map, op0, op1)) {
        logger.finer(op0 + " failed to commute with " + map);
        return false;
      }
    }
    return true;
  }
  
  /**
   * The matrix power algebra as defined in Hobby-McKenzie.
   * 
   * @param alg
   * @param k
   * @return
   */
  
  public static SmallAlgebra matrixPower(final SmallAlgebra alg, final int k) {
    PowerAlgebra pow = new PowerAlgebra(alg, k);
    List<Operation> ops = pow.operations();
    ops.add(Operations.makeLeftShift(k, alg.cardinality()));
    ops.add(Operations.makeMatrixDiagonalOp(k, alg.cardinality()));
    // convert to basic ops (not power ops)
    List<Operation> ops2 = Operations.makeIntOperations(ops);
    //for (Operation op : ops2) {
    //  System.out.println(ArrayString.toString(op.getTable()));
    //}
    SmallAlgebra ans = new BasicAlgebra("matrixPower", Operations.power(alg.cardinality(), k), ops2);
    //try {
    //  org.uacalc.io.AlgebraIO.writeAlgebraFile(ans, "/home/ralph/Java/Algebra/algebras/upow.ua");
    //}
    //catch (Exception e) {}
    return ans;
  }
  
  public static SmallAlgebra fullTransformationSemigroup(final int n, boolean includeConstants, boolean includeId) {
    if (n > 9) throw new IllegalArgumentException("n can be at most 9");
    
    //System.out.println(Horner.horner(new int[] {1,0,0}, 3));
    
    int pow = n;
    for (int i = 1; i < n; i++) {
      pow = pow * n;
    }
    List<Operation> ops = new ArrayList<Operation>(4);
    ops.add(Operations.makeCompositionOp(n, pow));
    if (includeConstants) {
      for (int i = 0; i < n; i++) {
        final int[] ci = new int[n];
        for (int j = 0; j < n; j++) {
          ci[j] = i;
        }
        final int c = Horner.horner(ci, n);
        ops.add(Operations.makeConstantIntOperation(pow, c));
      }
    }
    if (includeId) {
      final int[] id = new int[n];
      for (int i = 0; i < n; i++) {
        id[i] = i;
      }
      final int idx = Horner.horner(id, n);
      ops.add(Operations.makeConstantIntOperation(pow, idx));
    }
    return new BasicAlgebra("Trans" + n, pow, ops);
  }
  
  /**
   * Test if ops are in the clone of A and return a
   * mapping from OperationSymbols to terms, which
   * will have entries for those opertions which are
   * in the clone.
   * 
   * @param ops a list of operations on the set of A
   * @param A   an algebra
   * @return    a map from the operation symbols to the terms
   */
  public static Map<OperationSymbol,Term> findInClone(List<Operation> ops, 
                                    SmallAlgebra A, ProgressReport report) {
    if (ops == null || ops.isEmpty() || A == null) 
      throw new IllegalArgumentException("ops cannot be empty and the algebra cannot be null");
    List<Operation> ops2 = new ArrayList<Operation>(ops.size());
    for (Operation op : ops) {
      ops2.add(op);
    }
    Collections.sort(ops2); // reverse it ????
    System.out.println("ops2: " + ops2);
    Map<OperationSymbol,Term> map = new HashMap<OperationSymbol,Term>();
    int arity = ops2.get(0).arity();
    final int size = ops2.size();
    List<Operation> currentOps = new ArrayList<Operation>();
    currentOps.add(ops2.get(0));
    for (int i = 1; i <= size; i++) {
      int nextArity = i < size ? ops2.get(i).arity() : -1;
      System.out.println("arity: " + arity + ", i: " + i + ", next arity: " + nextArity);
      if (i == size || ops2.get(i).arity() != arity) {
        if (!currentOps.isEmpty()) {
          FreeAlgebra F = new FreeAlgebra(A, arity, false);
          Closer closer = new Closer(F.getProductAlgebra(), F.generators(), F.getTermMap());
          closer.setRootAlgebra(A);
          System.out.println("closing with currentOps.size() = " + currentOps.size());
          for (Operation op : currentOps) {
            System.out.println(op.symbol());
          }
          closer.setOperations(currentOps);
          closer.sgClosePower();
          Map<Operation,Term> currMap = closer.getTermMapForOperations();
          System.out.println("number found: " + currMap.keySet().size());
          for (Operation op : currMap.keySet()) {
            map.put(op.symbol(), currMap.get(op));
          }
          currMap.clear();
          if (i + 1 < size) arity = ops2.get(i + 1).arity();
        }
      }
      if (i < size) currentOps.add(ops2.get(i));
      System.out.println("currentOps size = " + currentOps.size());
    }
    System.out.println("map size: " + map.keySet().size());
    return map;
  }

  /**
   * Make a random algebra of a given similarity type.
   */
  public static SmallAlgebra makeRandomAlgebra(int n, SimilarityType simType) {
    return makeRandomAlgebra(n, simType, -1);
  }

  /**
   * Make a random algebra of a given similarity type, optionally 
   * supplying a seed to the random number generator (in case
   * regenerating the same algebra is important.
   */
  public static SmallAlgebra makeRandomAlgebra(int n, 
                                      SimilarityType simType, long seed) {
    List<Operation> ops = Operations.makeRandomOperations(n, simType, seed);
    // the second argument is the size of the algebra.
    return new BasicAlgebra("RAlg" + n, n, ops);
  }

  /**
   * Make a random algebra of a given the arities of the operations.
   */
  public static SmallAlgebra makeRandomAlgebra(int n, int[] arities) {
    return makeRandomAlgebra(n, arities, -1);
  }

  /**
   * Make a random algebra of a given the arities of the operations, 
   * optionally supplying a seed to the random number generator (in case
   * regenerating the same algebra is important.
   */
  public static SmallAlgebra makeRandomAlgebra(int n, 
                                               int[] arities, long seed) {
    final int len = arities.length;
    List<OperationSymbol> syms = new ArrayList<OperationSymbol>(len);
    for (int i = 0; i < len; i++) {
      syms.add(new OperationSymbol("r" + i, arities[i]));
    }
    return makeRandomAlgebra(n, new SimilarityType(syms), seed);
  }

  public static SmallAlgebra ternaryDiscriminatorAlgebra(int card) {
    List<Operation> ops = new ArrayList<Operation>(1);
    ops.add(Operations.ternaryDiscriminator(card));
    return new BasicAlgebra("Disc-" + card, card, ops);
  }

  /**
   * Determine if an algebra is quasicritical: is not
   * a subdirect product of proper subalgebras. Returns 
   * a map from a set of congruences of A whose intersection 
   * is zero to subuniverses of A. A modulo the congruence is
   * isomorphic to the subalgebra.
   * 
   * @param A
   * @return a map from congruences to subalgebras
   */
  public static Map<Partition,IntArray> quasiCritical(SmallAlgebra A) {
    return quasiCritical(A, null);
  }
  
  
  public static Map<Partition,IntArray> quasiCritical(SmallAlgebra A, ProgressReport report) {
    final Partition zero = A.con().zero();
    Partition phi = A.con().one();
    Map<Partition,IntArray> map = new HashMap<Partition,IntArray>();
    int[] gens = A.sub().findMinimalSizedGeneratingSet().getArray();
    //Map<IntArray,BasicSet> gens2subs = new HashMap<IntArray,BasicSet>();
    // make the above a method in SubalgebraLattice
    System.out.println("gens of A: " + Arrays.toString(gens));
    final int genSize = gens.length;
    System.out.println("|Con(A)| = " + A.con().cardinality());
    int k = 0;
    for (Partition par : A.con().universe()) {
      k++;
      if (k % 1000 == 0) System.out.println("k = " + k);
      if (par.equals(A.con().zero())  || phi.leq(par)) continue;
          
      //System.out.println("par: " + par);
      QuotientAlgebra quot = new QuotientAlgebra(A, par);
      int[] quotGens = new int[genSize];
      for (int i = 0 ; i < genSize; i++) {
        quotGens[i] = quot.canonicalHomomorphism(gens[i]);
      }
      //System.out.println("quotGens: " + Arrays.toString(quotGens));
      int[] arr = new int[genSize];
      ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(arr, A.cardinality() - 1);
      while (true) {
        //System.out.println("arr: " + Arrays.toString(arr));
        Map<Integer,Integer> homo = SubalgebraLattice.extendToHomomorphism(quotGens, arr, quot, A);
        if (homo != null) { // means there is a homomorphis
          if (homo.size() == new TreeSet<Integer>(homo.values()).size()) {  //test if homo is 1-1
            System.out.println("This one worked: par = " + par + " phi = " + phi);
            //map.put(par, A.sub().Sg(arr));  // put the whole subalg ?
            map.put(par, new IntArray(arr));
            phi = phi.meet(par);
            if (phi.equals(zero)) return map;
            break;
          }
        }
        if (!inc.increment()) break;
      }
    }
    System.out.println("map is " + map);
    System.out.println("phi is " + phi);
    return null;
  }

  static boolean endNow = true;
  
  public static void main(String[] args) throws Exception {
    

    SmallAlgebra pol = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/polin3ontop.ua");
    SmallAlgebra polid = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/polinidempotent.ua");
    SmallAlgebra lat = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/polin3ontop.ua");
    SmallAlgebra jenjb = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/jenjb4.ua");

    
    
    Map<Partition,IntArray> mapx = quasiCritical(polid);
    System.out.println("map: " + mapx);
    
    if (true) return;
    
    List<Operation> opers = polid.operations();
//  Operation firstOp = polid.operations().get(0);
//  ops = new ArrayList<Operation>();
//  ops.add(firstOp);
    for (Operation op : opers) System.out.println(op.symbol());
    Map<OperationSymbol,Term> map = findInClone(opers, pol, null);
    System.out.println("map: " + map);
    for (OperationSymbol sym : map.keySet()) {
      System.out.print(sym + " : ");
      System.out.println(map.get(sym));
    }
    if (true) return;
    
    for (int i = 3; i < 9; i++) {
      org.uacalc.io.AlgebraIO.writeAlgebraFile(Algebras.ternaryDiscriminatorAlgebra(i), "/tmp/TD-" + i + ".ua");
    }

      if (endNow) return;
    
    int[][] firstProj = {
        {0,0,0,0,0,0,0,0,0,0},
        {1,1,1,1,1,1,1,1,1,1},
        {2,2,2,2,2,2,2,2,2,2},
        {3,3,3,3,3,3,3,3,3,3},
        {4,4,4,4,4,4,4,4,4,4},
        {5,5,5,5,5,5,5,5,5,5},
        {6,6,6,6,6,6,6,6,6,6},
        {7,7,7,7,7,7,7,7,7,7},
        {8,8,8,8,8,8,8,8,8,8},
        {9,9,9,9,9,9,9,9,9,9}
    };
    
    int[][] secondProj = {
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},
        {0,1,2,3,4,5,6,7,8,9},        
    };
    
    int[][] mat = new int[][] {
        {0,6,5,4,7,8,9,1,2,3},
        {9,1,0,6,5,7,8,2,3,4},
        {8,9,2,1,0,6,7,3,4,5},
        {7,8,9,3,2,1,0,4,5,6},
        {1,7,8,9,4,3,2,5,6,0},
        {3,2,7,8,9,5,4,6,0,1},
        {5,4,3,7,8,9,6,0,1,2},
        {2,3,4,5,6,0,1,7,8,9},
        {4,5,6,0,1,2,3,9,7,8},
        {6,0,1,2,3,4,5,8,9,7}        
    };
    
    int[][] mat2 = new int[][] {
        {0,9,8,7,1,3,5,2,4,6},
        {6,1,9,8,7,2,4,3,5,0},
        {5,0,2,9,8,7,3,4,6,1},
        {4,6,1,3,9,8,7,5,0,2},
        {7,5,0,2,4,9,8,6,1,3},
        {8,7,6,1,3,5,9,0,2,4},
        {9,8,7,0,2,4,6,1,3,5},
        {1,2,3,4,5,6,0,7,8,9},
        {2,3,4,5,6,0,1,8,9,7},
        {3,4,5,6,0,1,2,9,7,8}        
    };
    
    List<Partition> genset = new ArrayList<Partition>(2);
    genset.add(BasicPartition.partitionFromMatrix(mat));
    //genset.add(BasicPartition.partitionFromMatrix(mat2));
    Partition eta0 = BasicPartition.partitionFromMatrix(firstProj);
    Partition eta1 = BasicPartition.partitionFromMatrix(secondProj);
    //NavigableSet<IntArray> lst = Algebras.unaryClone(genset, eta0, eta1);
    SmallAlgebra alg = unaryCloneAlgFromPartitions(genset, eta0, eta1);
    System.out.println("|J(Con(A))| = " + alg.con().joinIrreducibles().size());

    //System.out.println("");
    //for (IntArray ia : lst) {
    //  System.out.println(ia);
    //}
    //System.out.println("clone size = " + lst.size());
    
 
    /*
    BasicPartition rfz0 = new BasicPartition(new int[] {-3, -3, 0, 1, 0, 1});
    BasicPartition rfz1 = new BasicPartition(new int[] {-1, -2, 1, -2, 3, -1});
    BasicPartition rfz2 = new BasicPartition(new int[] {-2, -2, 1, -2, 3, 0});

    System.out.println("rfz0: " + rfz0);
    System.out.println("rfz1: " + rfz1);
    System.out.println("rfz2: " + rfz2);
        
        
    List<Partition> pars2 = new ArrayList<Partition>();
    pars2.add(rfz1);
    List<IntArray> lst = Algebras.unaryClone(pars2, rfz0, rfz2);
    Collections.sort(lst, IntArray.lexicographicComparitor());
    System.out.println("");
    for (IntArray ia : lst) {
      System.out.println(ia);
    }
    System.out.println("clone size = " + lst.size());
    
    pars2.add(rfz0);
    pars2.add(rfz2);
    Set<IntArray> set = BasicPartition.unaryClone(pars2);
    for (IntArray ia : set) {
      System.out.println(ia);
    }
    System.out.println("BasicPartition size is " + set.size());
    */

    if (true) return;
    
    SmallAlgebra alg0 = fullTransformationSemigroup(3, true, true);
    List<Operation> ops = alg0.operations();
    //int[] inv = new int[] {2,1,0};
    int[] inv = new int[] {1,2,0};
    final int invx = Horner.horner(inv, 3);
    //ops.add(Operations.makeConstantIntOperation(alg0.cardinality(), invx));
    org.uacalc.io.AlgebraIO.writeAlgebraFile(alg0, "/home/ralph/Java/Algebra/algebras/trans3.ua");
    for (int i = 0; i < 27; i++) {
      System.out.println("" + i + ": " + ArrayString.toString(Horner.hornerInv(i, 3, 3)));
    }
    org.uacalc.alg.sublat.SubalgebraLattice sub = alg0.sub();
    Set univ = sub.universe();
    List<org.uacalc.alg.sublat.BasicSet> univList = new ArrayList<org.uacalc.alg.sublat.BasicSet>(univ);
    Collections.sort(univList);
    System.out.println("number of subs with constants: " + univ.size());
    for (int i = 0; i < univList.size(); i++) {
      org.uacalc.alg.sublat.BasicSet s = (org.uacalc.alg.sublat.BasicSet)univList.get(i);
      System.out.println(i + ": " + s);
    }
    
    if (args.length == 0) return;
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







