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
 * Kiss, P. Prohle, and A. Szendrei <i>The set of type of a
 * finitely generated variety</i>, Discrete Math. 112 (1993), 1-20.
 * Preprint available on the second author's web site:
 * {@link http://www.cs.elte.hu/~ewkiss/}.
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
  //private ArrayList diagonal;
  private SimpleList diagonal;
  private final HashSet<IntArray> diagonal4HS;
  //private ArrayList diagonal4;
  private SimpleList diagonal4;
  private int diagonalSize = 0;
  private int diagonal4Size = 0;

  private HashSet<Integer> typeSet;

  public TypeFinder(SmallAlgebra alg) {
    this(alg, null);
  }

  public TypeFinder(SmallAlgebra alg, Partition alpha) {
    A = alg;
    con = A.con();
    algSize = A.cardinality();
    visited = new HashSet<IntArray>();
    diagonal = SimpleList.EMPTY_LIST;
    diagonalHS = new HashSet<IntArray>();
    diagonal4 = SimpleList.EMPTY_LIST;
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
    diagonalSize = 0;
    diagonal4Size = 0;
    diagonal = SimpleList.EMPTY_LIST;
    diagonal4 = SimpleList.EMPTY_LIST;
    diagonalHS.clear();
    diagonal4HS.clear();
    for (int i = 0; i < rootsSize; i++) {
      int[] tmp = new int[] {roots[i], roots[i]};
      int[] tmp2 = new int[] {roots[i], roots[i], roots[i], roots[i]};
      diagonal = diagonal.cons(tmp);
      diagonalSize++;
      diagonalHS.add(new IntArray(tmp));
      diagonal4 = diagonal4.cons(tmp2);
      diagonal4Size++;
      diagonal4HS.add(new IntArray(tmp2));
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
   * Now this is recursive so it might make sense to make it iterative.
   *
   * This looks at the image of the ordered pair under Pol_1(A). If 
   * this image is has not been visited in a previous call, this call
   * is abondoned and a recursive call is make on the image pair.
   * Otherwise it builds up Pol_1(A) restricted to the pair. This is
   * the local varaible called <tt>universe</tt>. Note 
   *
   *      Pol_1(A) | pair = sg({pair, and (x,x), x in A})
   *
   * so the method calculates universe as it goes. If we never reach
   * an univisited pair, the this pair is a subtrace. 
   *
   * If the reverse pair is visited, there is an involution (ruling out
   * type 4 and 5). This is recorded.
   *
   */
  private Subtrace findSubtrace(IntArray pairIA) {
    logger.info("calling IntArray with org pair " +  pairIA);
    int[] pair = pairIA.getArray();
    visited.add(pairIA);
    SimpleList universe = diagonal;

    HashSet<IntArray> genHashSet = (HashSet<IntArray>)diagonalHS.clone();
    genHashSet.add(pairIA);
    universe = universe.cons(pair);
    SimpleList newElems = universe;
    SimpleList oldElems = universe;

    // a fixed holder for the result of an operatrion.
    IntArray resIA = new IntArray(2);
    int[] res = resIA.getArray();    // Note writting to res changes resIA
    // a separate holder for the unordered pair
    IntArray unorderedResIA = new IntArray(2);
    int[] unorderedRes = unorderedResIA.getArray(); // Note  (see above)

    HashMap argIteratorMap = new HashMap();
    HashMap argMap = new HashMap();
    HashMap argvMap = new HashMap();
    for (Iterator opIt = A.operations().iterator(); opIt.hasNext(); ) {
      int ar = ((Operation)opIt.next()).arity();
      if (ar == 0) continue;
      Integer arInt = new Integer(ar);
      if (argIteratorMap.get(arInt) == null) {
        argIteratorMap.put(arInt, new Iterator[ar]); 
        argMap.put(arInt, new int[ar][]);
        argvMap.put(arInt, new int[ar]);
      }
    }


    while (true) {
      logger.finer("subtr u size " + universe.size());
      for (Iterator opIt = A.operations().iterator(); opIt.hasNext(); ) {
        Operation f = (Operation)opIt.next();
        int ar = f.arity();
        if (ar == 0) continue;
        Integer arInt = new Integer(ar);
        //Iterator[] argIterators = new Iterator[ar];
        Iterator[] argIterators = (Iterator[])argIteratorMap.get(arInt);
        //int[][] arg = new int[ar][];
        int[][] arg = (int[][])argMap.get(arInt);
        //int argv[] = new int[ar];
        int argv[] = (int[])argvMap.get(arInt);
        for (int i = 0; i < ar; i++) {
          //Perform operation f for the arguments
          //given in Iterator[].
          //In the i-th step,
          //  the first i arguments are in old;
          //  the i-th argument is in lastNew;
          //  the rest of the arguments is in universe
          for (int j = 0; j < i; j++) {
            argIterators[j] = oldElems.iterator();
            arg[j] = (int[])argIterators[j].next();
          }
          argIterators[i] = newElems.frontIterator(oldElems);
          arg[i] = (int[])argIterators[i].next();
          for(int j = i + 1; j < ar; j++) {
            argIterators[j] = newElems.iterator();
            arg[j] = (int[])argIterators[j].next();
          }
          while(true) {//through arguments
            //perform operation f:
            for(int k = 0; k < 2; k++ ) {
              for(int j = 0; j < ar; j++) {
                argv[j] = arg[j][k];
              }
              res[k] = alpha.representative(f.intValueAt(argv));
            }
            // ignore res if it is in alpha:
            if (res[0] != res[1]) {
              logger.finer("op is " + f.symbol());
              logger.finer("arg = " + ArrayString.toString(arg));
              logger.finer(", res = " + ArrayString.toString(res));
            }

            if (res[0] != res[1]) {
              if (res[1] < res[0]) {
                unorderedRes[0] = res[1];
                unorderedRes[1] = res[0];
              }
              else {
                unorderedRes[0] = res[0];
                unorderedRes[1] = res[1];
              }
              if (!visited.contains(unorderedResIA)) {
                logger.finer("op is " + f.symbol());
                return findSubtrace((IntArray)unorderedResIA.clone());
              }
              else {
                if( !genHashSet.contains( resIA ) ) {
                  int [] res2 = new int[2];
                  for (int v = 0; v < 2; v++) {
                    res2[v] = res[v];
                  }
                  genHashSet.add(new IntArray(res2));
                  universe = universe.cons(res2);
                  logger.finer("adding to universe " 
                                     + ArrayString.toString(res2));
                  logger.finer("universe size is " + universe.size());
                }
              }
            }

            //increment the argumentlist
            int j = 0;
            for(j = 0; j < ar; j++ ) {
              if( !argIterators[j].hasNext() ) {
                if( j < i ) {
                  argIterators[j] = oldElems.iterator();
                } else if( j == i ) {
                  argIterators[j] = newElems.frontIterator(oldElems);
                } else {
                  argIterators[j] = newElems.iterator();
                }
                arg[j] = (int[])argIterators[j].next();
                continue; //increment next coordinate
              }
              arg[j] = (int[])argIterators[j].next();
              break;//argIterators[j] has been increased
            }
            if ( j == ar ) { //all arguments done
              break;
            }
          } //arguments cycle
        }
      } //operations cycle
      if (universe == newElems) {  // nothing was added so we are done
        if (printSubtrace) {  // hack for now
          logger.fine("subtrace univ " + universe.size());
          logger.fine("orig pair " + pairIA);
          logger.fine("subtr " + new IntArray(pair));
          logUniv(universe);

          System.out.println("subtraces: ");
          for (Iterator it = universe.iterator(); it.hasNext(); ) {
            System.out.println(ArrayString.toString(it.next()));
          }
        }
        Subtrace subtrace = new Subtrace(pair[0], pair[1], 
          genHashSet.contains(new IntArray(new int[] {pair[1], pair[0]})));
        List<int[]> univ = new ArrayList<int[]>(universe);
        List<IntArray> subtrUniv = new ArrayList<IntArray>(univ.size());
        for (int[] arr : univ) {
          subtrUniv.add(new IntArray(arr));
        }
        subtrace.setSubtraceUniverse(subtrUniv);
        return subtrace;
      }
      oldElems = newElems;
      newElems = universe;
    }
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
    int c = subtrace.first();
    int d = subtrace.second();
    //boolean has2snag = false;      // (c,d) is a 2-snag
    //boolean has2snagRev = false;   // (d,c) is a 2-snag
    //boolean has1snag = false;
    boolean meet = false;
    boolean join = false;
    boolean oneSnag = false;
    int wx,wy,wu,wv;

    SimpleList universe = diagonal4;
    SimpleList oldElems = universe;
    HashSet genHashSet = (HashSet)diagonal4HS.clone();

    int[] tmp = new int[] {c,c,d,d};
    universe = universe.cons(tmp);
    genHashSet.add(new IntArray(tmp));
    tmp = new int[] {c,d,c,d};
    universe = universe.cons(tmp);
    genHashSet.add(new IntArray(tmp));
    SimpleList newElems = universe;

    // a fixed holder for the result of an operatrion.
    IntArray resIA = new IntArray(4);
    int[] res = resIA.getArray();    // Note writting to res changes resIA


    HashMap argIteratorMap = new HashMap();
    HashMap argMap = new HashMap();
    HashMap argvMap = new HashMap();
    for (Iterator opIt = A.operations().iterator(); opIt.hasNext(); ) {
      int ar = ((Operation)opIt.next()).arity();
      if (ar == 0) continue;
      Integer arInt = new Integer(ar);
      if (argIteratorMap.get(arInt) == null) {
        argIteratorMap.put(arInt, new Iterator[ar]); 
        argMap.put(arInt, new int[ar][]);
        argvMap.put(arInt, new int[ar]);
      }
    }

    while (true) {
      for (Iterator opIt = A.operations().iterator(); opIt.hasNext(); ) {
        Operation f = (Operation)opIt.next();
        int ar = f.arity();
        if (ar == 0) continue;

        Integer arInt = new Integer(ar);
        Iterator[] argIterators = (Iterator[])argIteratorMap.get(arInt);
        int[][] arg = (int[][])argMap.get(arInt);
        int argv[] = (int[])argvMap.get(arInt);

        //Iterator[] argIterators = new Iterator[ar];
        //int[][] arg = new int[ar][];
        //int argv[] = new int[ar];
        for (int i = 0; i < ar; i++) {
          //Perform operation f for the arguments
          //given in Iterator[].
          //In the i-th step,
          //  the first i arguments are in old;
          //  the i-th argument is in lastNew;
          //  the rest of the arguments is in universe
          for (int j = 0; j < i; j++) {
            argIterators[j] = oldElems.iterator();
            arg[j] = (int[])argIterators[j].next();
          }
          argIterators[i] = newElems.frontIterator(oldElems);
          arg[i] = (int[])argIterators[i].next();
          for(int j = i + 1; j < ar; j++) {
            argIterators[j] = newElems.iterator();
            arg[j] = (int[])argIterators[j].next();
          }
          while(true) {//through arguments
            //perform operation f:
            for(int k = 0; k < 4; k++ ) {     // note 4 here. change
              for(int j = 0; j < ar; j++) {
                argv[j] = arg[j][k];
              }
              res[k] = alpha.representative(f.intValueAt(argv));
            }
            // ignore res if it is in alpha:
            logger.finer("op is " + f.symbol());
            logger.finer("arg = " + ArrayString.toString(arg));
            logger.finer(", res = " + ArrayString.toString(res));
            if (!genHashSet.contains(resIA)) {
            //System.out.println("got here");
            logUniv(universe);


              int [] res2 = new int[4];
              for (int v = 0; v < 4; v++) {
                res2[v] = res[v];
              }
              genHashSet.add(new IntArray(res2));
              universe = universe.cons(res2);
              int x = res2[0];
              int y = res2[1];
              int u = res2[2];
              int v = res2[3];
              if (!join && (((x!=y) && (u==v)) || 
                            ((x!=u) && (y==v)))) { /* join found */
                if (subtrace.hasInvolution()) {
                  logger.info("found 3");
                  logUniv(universe);
                  subtrace.setType(3);
                  return 3;
                }
                if (meet) {
                  logger.info("found 4");
                  logUniv(universe);
                  printUniv(universe); // maybe delete this
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
                  logger.info("found 3");
                  logUniv(universe);
                    subtrace.setType(3);
                    return 3;
                  }
                  if (join) {
                    logger.info("found 4");
                    subtrace.setType(4);
                    return 4;
                  }
                  meet = true;
                  oneSnag = true;
                }
              }
              //So not a meet, not a join. Is it an other kind of one-snag?
              if(!oneSnag) {
                if (((x==v) && ((x!=y) || (u!=v))) ||
                    ((y==u) && ((x!=y) || (u!=v)))) {
                    oneSnag=true;
                }
              }
            }

            //increment the argumentlist
            int j = 0;
            for (j = 0; j < ar; j++) {
              if (!argIterators[j].hasNext()) {
                if (j < i) {
                  argIterators[j] = oldElems.iterator();
                } else if (j == i) {
                  argIterators[j] = newElems.frontIterator(oldElems);
                } else {
                  argIterators[j] = newElems.iterator();
                }
                arg[j] = (int[])argIterators[j].next();
                continue; //increment next coordinate
              }
              arg[j] = (int[])argIterators[j].next();
              break;//argIterators[j] has been increased
            }
            if ( j == ar ) { //all arguments done
              break;
            }
          } //arguments cycle
        }
      } //operations cycle
      if (universe == newElems) {  // nothing was added so we are done
        if (printSubtrace) {
          logUniv(universe);
          logger.info("universe size is " + universe.size());
          logger.info("orig pair c = " + c + ", d = " + d);
          System.out.println("matrices: ");
          for (Iterator it = universe.iterator(); it.hasNext(); ) {
            System.out.println(ArrayString.toString(it.next()));
          }
        }
        List<int[]> univ = new ArrayList<int[]>(universe);
        List<IntArray> mUniv = new ArrayList<IntArray>(univ.size());
        for (int[] arr : univ) {
          mUniv.add(new IntArray(arr));
        }
        subtrace.setMatrixUniverse(mUniv);
        if (join || meet) {
          logger.info("found 5");
          subtrace.setType(5);
          logger.info("subtrace is " + subtrace);
          return 5;
        }
        else {
          if (oneSnag) {
            logger.info("found 2");
            subtrace.setType(2);
            return 2;
          }
          else {
            logger.info("found 1");
            subtrace.setType(1);
            return 1;
          }
        }
      }
      oldElems = newElems;
      newElems = universe;
    }
  }

  public void logUniv(List universe) {
    logUniv(universe, Level.FINE);
  }

  public void logUniv(List universe, Level level) {
    for (Iterator it = universe.iterator(); it.hasNext(); ) {
      logger.log(level, ArrayString.toString(it.next()));
    }
  }

  public void printUniv(List universe) {
    for (Iterator it = universe.iterator(); it.hasNext(); ) {
      System.out.println(ArrayString.toString(it.next()));
    }
  }

}

