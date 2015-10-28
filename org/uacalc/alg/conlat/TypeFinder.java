/* TypeFinder.java 2003/01/26 Ralph Freese */

package org.uacalc.alg.conlat;

import org.uacalc.util.*;
import org.uacalc.alg.*;
import org.uacalc.alg.op.Operation;

import java.util.*;
import java.util.logging.*;

/**
 * A utility class to find a subtrace {a, b} and its TCT type of a
 * covering beta/beta_* for some join irreducible congruence beta.
 * It is designed so that it can be reused for efficiency.
 *
 * <p>
 * The main part of the calculation is to take elements <i>a</i>
 * and <i>b</i> of the algebra such that
 * Cg(<i>a</i>, <i>b</i>) is join irreducible, and a congruence
 * alpha above the lower cover of Cg(<i>a</i>, <i>b</i>) but not
 * above Cg(<i>a</i>, <i>b</i>) and find <i>c</i> and <i>d</i>
 * such that Cg(<i>c</i>, <i>d</i>) = Cg(<i>a</i>, <i>b</i>) and
 * {<i>c</i>, <i>d</i>} is a subtrace. Then it finds the type of this
 * pair.
 * <p>
 * The algorithm used is similar to that described in J. Berman, E. W.
 * Kiss, P. Pr&#337;hle, and &Aacute;. Szendrei <i>The set of type of a
 * finitely generated variety</i>, Discrete Math. 112 (1993), 1-20.
 * Preprint available on the second author's web site:
 * <a href="http://www.cs.elte.hu/~ewkiss/" target="_blank">http://www.cs.elte.hu/~ewkiss/</a>.
 * They define a directed graph G(A) with vertices two elements
 * subsets of A and edges corresponding to images of unary polynomials.
 * <p>
 * Our algorithm restricts this to only those pairs <i>x</i>, <i>y</i>
 * such that Cg(<i>x</i>, <i>y</i>) = Cg(<i>a</i>, <i>b</i>) and
 * takes advantage of the fact that this graph is a quasiordered set
 * and that a subtrace corresponds to a minimal element of it. This
 * allows us to find a subtrace with only calculating part of the
 * graph. But in the worst case analysis this is no better than the
 * original algorithm but in most cases it is much faster.
 * <p>
 * One of the nice things about this is the calculations are really taking
 * place in A/<i>alpha</i> even though we do not explicitly for that
 * quotient. As noted above <i>alpha</i> can be the lower cover of
 * Cg(<i>a</i>, <i>b</i>) but it also can be a meet irreducible of small
 * index.
 *
 *
 * @author Ralph Freese
 * @author Emil Kiss
 * @version $Id$
 */
public final class TypeFinder {

  static Logger logger = Logger.getLogger("org.uacalc.alg.TypeFinder");
  static {
    logger.setLevel(Level.FINER);
  }

  public static final boolean printSubtrace = false;

  private final SmallAlgebra A;
  private final BigProductAlgebra Asquared;  // A^2
  private final BigProductAlgebra Afourth;   // A^4
  private final int algSize;
  private final CongruenceLattice con;

  private final HashSet<IntArray> visited;
  //private boolean hasInvolution = false;

  // this will only have the JI's and their lower covers and lookup
  //private CongruenceLattice con;

  // depends on alpha:
  private Partition alpha;
  private int[] roots;
  private int rootsSize;
  private final HashSet<IntArray> diagonalHS;
  private List<IntArray> diagonal;
  //private SimpleList diagonal;
  private final HashSet<IntArray> diagonal4HS;
  private List<IntArray> diagonal4;
  //private SimpleList diagonal4;
  //private int diagonalSize = 0;
  //private int diagonal4Size = 0;

  private HashSet<Integer> typeSet;

  public TypeFinder(SmallAlgebra alg) {
    this(alg, null);
  }

  public TypeFinder(SmallAlgebra alg, Partition alpha) {
    A = alg;
    Asquared = new BigProductAlgebra(alg, 2);
    Afourth = new BigProductAlgebra(alg, 4);
    con = A.con();
    algSize = A.cardinality();
    visited = new HashSet<IntArray>();
    //diagonal = SimpleList.EMPTY_LIST;
    diagonal = new ArrayList<>();
    diagonalHS = new HashSet<IntArray>();
    //diagonal4 = SimpleList.EMPTY_LIST;
    diagonal4 = new ArrayList<>();
    diagonal4HS = new HashSet<IntArray>();
    if (alpha == null) alpha = con.zero();
    setAlpha(alpha);
  }

  private void setAlpha(Partition alpha) {
    if (alpha == null) alpha = con.zero();
    if (alpha.equals(this.alpha)) return;
    visited.clear();
    this.alpha = alpha;
    roots = alpha.representatives();
    rootsSize = roots.length;
    diagonal = new ArrayList<>();
    diagonal4 = new ArrayList<>();
    //diagonal = SimpleList.EMPTY_LIST;
    //diagonal4 = SimpleList.EMPTY_LIST;
    diagonalHS.clear();
    diagonal4HS.clear();
    for (int i = 0; i < rootsSize; i++) {
      IntArray tmp = new IntArray(new int[] {roots[i], roots[i]});
      IntArray tmp2 = new IntArray(new int[] {roots[i], roots[i], roots[i], roots[i]});
      diagonal.add(tmp);
      //diagonal = diagonal.cons(tmp);
      //diagonalSize++;
      diagonalHS.add(tmp);
      diagonal4.add(tmp2);
      //diagonal4 = diagonal4.cons(tmp2);
      //diagonal4Size++;
      diagonal4HS.add(tmp2);
    }
  }

  public void init() {
    init(con.zero());
  }

  public void init(Partition alpha) { setAlpha(alpha); }

  /**
   * Find the TCT type set of the algebra A.
   */
  public HashSet<Integer> findTypeSet() {
    if (typeSet != null) return typeSet;   // make sure to null typeSet if
                                           // calculation was interupted.
    typeSet = new HashSet<Integer>();
    for (Partition par : con.joinIrreducibles()) {
      typeSet.add(new Integer(findType(par)));
    }
    return typeSet;
  }
  
  /**
   * Test if <code>ia</code> is a beta subtrace.
   * 
   * @param ia
   * @param beta
   * @return
   */
  public boolean isSubtrace(IntArray ia, Partition beta) {
    Partition betaStar = con.lowerStar(beta);
    if (betaStar == null) throw new IllegalArgumentException(
                         "beta = " + beta + " is not join irreducible");
    alpha = (Partition)alpha.join(betaStar);
    if (beta.leq(alpha)) throw new IllegalArgumentException(
                         "beta is below its lower cover join alpha");
    setAlpha(alpha);
    Subtrace subtr = findSubtrace(ia);
    return subtr.getSubtraceUniverse().contains(ia);
  }

  public Subtrace findSubtrace(Partition beta) { 
    return findSubtrace(beta, con.lowerStar(beta));
  }

  /**
   * Find a subtrace for <tt>beta</tt>, which is assumed to be
   * join irreducible, over its lower cover. It is assumed that 
   * <tt>alpha</tt> join the lower cover of <tt>beta</tt> is not
   * above <tt>beta</tt> and it effectively works in the algebra mod
   * this join.
   * 
   * @param beta   a join irreducible congruence. 
   * @param alpha  a congruence whose join with the lower cover of
   *               beta is not above beta.
   */
  public Subtrace findSubtrace(Partition beta, Partition alpha) { 
    Partition betaStar = con.lowerStar(beta);
    if (betaStar == null) throw new IllegalArgumentException(
                         "beta = " + beta + " is not join irreducible");
    alpha = (Partition)alpha.join(betaStar);
    if (beta.leq(alpha)) throw new IllegalArgumentException(
                         "beta is below its lower cover join alpha");
    setAlpha(alpha);
    return findSubtrace(con.generatingPair(beta));
  }

  /**
   * This looks at the image of the ordered pair under Pol_1(A). If 
   * this image is has not been visited in a previous call, this call
   * is abandoned and a recursive call is make on the image pair.
   * Otherwise it builds up Pol_1(A) restricted to the pair. This is
   * the local variable called <tt>universe</tt>. Note 
   *
   *      Pol_1(A) | pair = sg({pair, and (x,x), x in A})
   *
   * so the method calculates universe as it goes. If we never reach
   * an unvisited pair, the this pair is a subtrace. 
   *
   * If the reverse pair is visited, there is an involution (ruling out
   * type 4 and 5). This is recorded.
   *
   */
  public Subtrace findSubtrace(IntArray pairIA) {
    Set<IntArray> univHS = new HashSet<>();
    Set<IntArray> unorderedUnivHS = new HashSet<>();
    IntArray oldPair;
    List<IntArray> univ;
    while (true) {
      oldPair = pairIA;
      univ = new ArrayList<IntArray>();  // so we can keep it.
      pairIA = nextPairForSubtrace(pairIA, univHS, unorderedUnivHS, univ);
      if (pairIA == null) break;
    }
    int a = oldPair.get(0);
    int b = oldPair.get(1);
    Subtrace subtrace = new Subtrace(a, b, 
        univHS.contains(new IntArray(new int[] {b, a})));
    subtrace.setSubtraceUniverse(univ);
    return subtrace;
  }
  
  /**
   * Looks for another pair in the subalgebra of A^2 generated by
   * the given pair and the constants, whose unordered pair has not been 
   * visited before and returns it. Returns null if there is none which
   * implies the original pair is a subtrace. <code>univHS</code> 
   * and <code>univ</code> are passed 
   * from caller so it can check if there is an involution and save 
   * the <code>univ</code> on the subtrace object. 
   * 
   * @param pair
   * @param univHS
   * @param undorderedUnivHS
   * @param univ
   * @return
   */
  public IntArray nextPairForSubtrace(IntArray pair, Set<IntArray> univHS,  
         Set<IntArray> unorderedUnivHS, List<IntArray> univ) {
    univHS.clear();
    //List<IntArray> univ = new ArrayList<>();
    univ.add(pair);
    for (int i = 0; i < A.cardinality(); i++) {
      univ.add(new IntArray(new int[] {i,i}));
    }
    univHS.addAll(univ);
    int closedMark = 0;
    int currentMark = univ.size();
    while (closedMark < currentMark) {
      if (Thread.currentThread().isInterrupted()) return null;  // ProgressReport ??
      for (Operation f : Asquared.operations()) {
        final int arity = f.arity();
        if (arity == 0) continue;
        int[] argIndeces = new int[arity];
        for (int j = 0; j < arity - 1; j++) {
          argIndeces[j] = 0;
        }
        argIndeces[arity - 1] = closedMark;
        ArrayIncrementor inc =
            SequenceGenerator.sequenceIncrementor(
                argIndeces, currentMark - 1, closedMark);

        final int[][] arg = new int[arity][];
        while (true) {
          if (Thread.currentThread().isInterrupted()) return null;  // ProgressReport ??
          for (int j = 0; j < arity; j++) {
            arg[j] = univ.get(argIndeces[j]).getArray();
          }

          int[] vRaw = f.valueAt(arg);
          IntArray v = new IntArray(vRaw);
          if (!alpha.isRelated(v.get(0), v.get(1))) {
            IntArray vUnordered = new IntArray(2);
            if (v.get(0) < v.get(1)) {
              vUnordered.set(0, v.get(0));
              vUnordered.set(1, v.get(1));
            }
            else {
              vUnordered.set(0, v.get(1));
              vUnordered.set(1, v.get(0));
            }
            if (unorderedUnivHS.add(vUnordered)) {  // v is new; start over with it
              return v;
            }
            if (univHS.add(v)) univ.add(v);  // otherwise it is already there; don't add two copies to univ.
          }
          if (!inc.increment()) break;
        }
      }
      closedMark = currentMark;
      currentMark = univ.size();
    }
    return null;
  }
  
  
  public int findType(Partition beta) { 
    return findType(beta, con.lowerStar(beta));
  }

  /**
   * Find the type for <tt>beta</tt>, which is assumed to be
   * join irreducible, over its lower cover. It is assumed that 
   * <tt>alpha</tt> join the lower cover of <tt>beta</tt> is not
   * above <tt>beta</tt> and it effectively works in the algebra mod
   * this join.
   * 
   * @param beta   a join irreducible congruence. 
   * @param alpha  a congruence whose join with the lower cover of
   *               beta is not above beta.
   */
  public int findType(Partition beta, Partition alpha) { 
    Partition betaStar = con.lowerStar(beta);
    if (betaStar == null) throw new IllegalArgumentException(
                         "beta = " + beta + " is not join irreducible");
    alpha = (Partition)alpha.join(betaStar);
    if (beta.leq(alpha)) throw new IllegalArgumentException(
                         "beta is below its lower cover join alph");
    setAlpha(alpha);
    return findType(findSubtrace(con.generatingPair(beta)));
  }

  /**
   * Finds the type of a subtrace and sets the type field of the it.
   * <p>
   * Let [c,d] be the subtrace. We think of quadruples
   * as maps from {c,d}^2 into A in the row order. That is
   * (c,c), (c,d), (d,c), (d,d). So the projections are [c,c,d,d] and
   * [c,d,c,d].  So a (c,d) 2-snag is [c,c,c,d] and a (d,c) 2-snag is
   * [c,d,d,d].
   *
   */
  public int findType(Subtrace subtrace) {
    // this is useful for more than just debugging
    //System.out.println("calling findType with " + subtrace);
    int c = subtrace.first();
    int d = subtrace.second();
    boolean meet = false;
    boolean join = false;
    boolean oneSnag = false;
    List<IntArray> universe = new ArrayList<>();
    universe.addAll(diagonal4);
    Set<IntArray> univHashSet = new HashSet<>();
    univHashSet.addAll(universe);

    IntArray rows = new IntArray(new int[] {c,c,d,d});
    IntArray cols = new IntArray(new int[] {c,d,c,d});
    universe.add(rows);
    universe.add(cols);
    univHashSet.add(rows);
    univHashSet.add(cols);
    int closedMark = 0;
    int currentMark = universe.size();
    while (closedMark < currentMark) {
      //System.out.println("currMark: " + currentMark + ", closedMark: " + closedMark);
      if (Thread.currentThread().isInterrupted()) return -1;  // ProgressReport ??
      for (Operation f : Afourth.operations()) {
        //System.out.println("f: " + f);
        final int arity = f.arity();
        if (arity == 0) continue;
        int[] argIndeces = new int[arity];
        for (int j = 0; j < arity - 1; j++) {
          argIndeces[j] = 0;
        }
        argIndeces[arity - 1] = closedMark;
        ArrayIncrementor inc =
            SequenceGenerator.sequenceIncrementor(
                argIndeces, currentMark - 1, closedMark);

        final int[][] arg = new int[arity][];
        while (true) {
          //System.out.println("argIndeces: " + Arrays.toString(argIndeces));
          if (Thread.currentThread().isInterrupted()) return -1;  // ProgressReport ??
          for (int j = 0; j < arity; j++) {
            arg[j] = universe.get(argIndeces[j]).getArray();
          }

          int[] vRaw = f.valueAt(arg);
          IntArray vec = new IntArray(vRaw);
          if (univHashSet.add(vec)) universe.add(vec);
          else {
            if (!inc.increment()) break;
            continue;
          }
          int[] vRawModAlpha = new int[4];
          for (int i = 0; i < 4; i++) {
            vRawModAlpha[i] = alpha.representative(vRaw[i]);
          }
          int x = vRawModAlpha[0];
          int y = vRawModAlpha[1];
          int u = vRawModAlpha[2];
          int v = vRawModAlpha[3];
          if (!join && (((x!=y) && (u==v)) || 
              ((x!=u) && (y==v)))) { /* join found */
            if (subtrace.hasInvolution()) {
              subtrace.setMatrixUniverse(universe);
              subtrace.setType(3);
              return 3;
            }
            if (meet) {
              subtrace.setMatrixUniverse(universe);
              subtrace.setType(4);
              return 4;
            }
            join = true;
            oneSnag = true;
          }
          else {
            if (!meet && (((x==y) && (u!=v)) || 
                ((x==u) && (y!=v)))) { /* meet found */
              if (subtrace.hasInvolution()) {
                subtrace.setType(3);
                return 3;
              }
              if (join) {
                subtrace.setMatrixUniverse(universe);
                subtrace.setType(4);
                return 4;
              }
              meet = true;
              oneSnag = true;
            }
          }
          //So not a meet, not a join. Is it an other kind of one-snag?
          //System.out.println("here");
          if(!oneSnag) {
            if (((x==v) && ((x!=y) || (u!=v))) ||
                ((y==u) && ((x!=y) || (u!=v)))) {
              oneSnag=true;
            }
          }
          //System.out.println("about to increment");
          if (!inc.increment()) break;
        }
      }
      closedMark = currentMark;
      currentMark = universe.size();
    }  
    if (printSubtrace) {
      System.out.println("matrices: ");
      for (IntArray ia : universe) {
        System.out.println(ArrayString.toString(ia));
      }
    }
    subtrace.setMatrixUniverse(universe);
    if (join || meet) {
      subtrace.setType(5);
      return 5;
    }
    else {
      if (oneSnag) {
        subtrace.setMatrixUniverse(universe);
        subtrace.setType(2);
        return 2;
      }
      else {
        subtrace.setMatrixUniverse(universe);
        subtrace.setType(1);
        return 1;
      }
    }
  }
  
  public static final void main(String[] args) throws Exception {
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(
        "/Users/ralph/Java/Algebra/algebras/m3.ua");
    SmallAlgebra algSq = new PowerAlgebra(alg, 2);
    int[][] arg = new int[][] {{2,3},{1,2}};
    Operation op = algSq.operations().get(0);
    System.out.println(Arrays.toString(op.valueAt(arg)));
  }

}

