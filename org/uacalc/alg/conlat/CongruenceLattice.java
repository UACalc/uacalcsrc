package org.uacalc.alg.conlat;

import org.uacalc.alg.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.SimilarityType;
import org.uacalc.util.*;
import org.uacalc.lat.*;

import java.util.*;
import java.util.logging.*;

/* CongruenceLattice.java 2001/06/04 Ralph Freese */
/* 
 * Changes by RSF: 2001/06/18
 * I changed makePrincipals so that it so that equal principal congruences
 * are indentical. This will result in a big space saving im many cases.
 *
 * Changes by EWK: 2001/06/14
 * This class contains only the core functionality
 * of the calculation itself. No I/O, and no
 * "helper" data holders for I/O.
 * This class is meant to be subclassed.
 * ?? Isn't it faster to use the JI congruences instead of
 * the principals when generating all congruences?
 * I changed it so.
 */

/**
 * A class to represent the congruence lattice of a SmallAlgebra;
 * this is, an algebra with universe
 * the integers from 0 to n-1. This uses the very fast algorithms from
 * my unpublished paper <i>Computing Congruences Efficiently</i>
 * which is available at 
 * {@link <a href="http://www.math.hawaii.edu/~ralph/papers.html">
    http://www.math.hawaii.edu/~ralph/papers.html</a>}.
 *
 * @author Ralph Freese
 * @version $Id$
 */ 
public class CongruenceLattice implements Lattice {

  static Logger logger = Logger.getLogger("org.uacalc.alg.CongruenceLattice");
  static {
    logger.setLevel(Level.FINER);
  }

  private SmallAlgebra alg;
  private int algSize;
  private int numOps;

  private BasicPartition zeroCong;
  private BasicPartition oneCong;


  /** 
   * A map from pairs [i,j] to the array representing Cg(i, j).
   */
  private HashMap principalCongruencesLookup = null;

  /**
   * A map from principal congruences to pairs [i,j] such that Cg(i, j).
   * is the principal congruence.
   */
  private HashMap principalCongruencesRep = null;

  /**
   * A map from principal congruences to a Subtrace for this principal.
   * The Subtrace has the TCT type as well.
   */
  private HashMap joinIrredToSubtraceMap = null;


  private Set universe = null;
  private HashMap upperCoversMap = null;
  private ArrayList principalCongruences = null;
  private ArrayList joinIrreducibles = null;
  private ArrayList meetIrreducibles = null;
  private HashMap lowerCoverOfJIs = null;
  private HashSet congruencesHash = null;
  private ArrayList meetIrredCongruences = null;
  private HashSet typeSet = null;


  /**
   * The size of the universe as it is being computed for the progress
   * bar.
   */
  private int sizeComputed = 0;

  private boolean principalsMade = false;

  private TypeFinder typeFinder = null;

  public CongruenceLattice(SmallAlgebra alg) {
    this.alg = alg;
    algSize = alg.cardinality();
    numOps = alg.operations().size();
    zeroCong = BasicPartition.zero(algSize);
    oneCong = BasicPartition.one(algSize);
  }

  public SmallAlgebra algebra() { return alg; }

  public boolean isUnary() { return false; }

  public String description() {
    return "Congruence Lattice of " + alg;
  }

  public List principals() {
    if (!principalsMade) {
      makePrincipals();
      principalsMade = true;
    }
    return principalCongruences;
  }
  
  public int cardinality() {
    return cardinality(null);
  }

  public int cardinality(Monitor monitor) {
    return universe(monitor).size();
  }
  
  public Set universe() {
    return universe(null);
  }

  public Set universe(Monitor monitor) {
    if (universe == null) makeUniverse(monitor);
    return universe;
  }

  public boolean isSimilarTo(Algebra alg) {
    return alg.similarityType().equals(similarityType());
  }
                                                                                
  public SimilarityType similarityType() {
    return SimilarityType.LATTICE_SIMILARITY_TYPE;
  }

  public Iterator iterator() { return universe().iterator(); }

  public String name() {
    return "Con(" + algebra() + ")";
  }

  /**
   * A list of the join irreducibles; constructed if necessary.
   */
  public List joinIrreducibles() {
    if (joinIrreducibles == null) makeJoinIrreducibles();
    return joinIrreducibles;
  }

  public boolean joinIrreducible(Partition part) {
    // make sure the join irreduciles have been make
    joinIrreducibles();
    return lowerCoverOfJIs.get(part) != null;
  }

  public List meetIrreducibles() {
    if (meetIrreducibles == null) makeMeetIrreducibles();
    return meetIrreducibles;
  }

  public Object join(Object a, Object b) { 
    return ((Partition)a).join((Partition)b);
  }

  public Object join(List args) {
    Partition join = zero();
    for (Iterator it = args.iterator(); it.hasNext(); ) {
      join = join.join((Partition)it.next());
    }
    return join;
  }

  public Object meet(Object a, Object b) {
    return ((Partition)a).meet((Partition)b);
  }

  public Object meet(List args) {
    Partition meet = one();
    for (Iterator it = args.iterator(); it.hasNext(); ) {
      meet = meet.meet((Partition)it.next());
    }
    return meet;
  }


  public boolean leq(Object a, Object b) {
    return ((BasicPartition)a).leq((BasicPartition)b);
  }

  public List constantOperations() { return SimpleList.EMPTY_LIST; }

  // fix this
  public List operations() { return null; }

  // fix this
  public Operation getOperation(OperationSymbol sym) { return null; }

  // we weill try to convert this to a SmallLattice and find the 
  // congruence of that.
  //public CongruenceLattice con() { return null; }

  public void makePrincipals() {
    HashMap pcIdMap = new HashMap();  // to keep equal congruences identical
    principalCongruences = new ArrayList();
    //congruencesHash = new HashSet();
    principalCongruencesLookup = new HashMap();
    principalCongruencesRep = new HashMap();
    for (int i = 0; i < algSize - 1; i++) {
      for (int j = i + 1; j < algSize; j++) {
        BasicPartition partCong = makeCg(i, j);
        if (pcIdMap.get(partCong) == null) {
          pcIdMap.put(partCong, partCong);
          principalCongruences.add(partCong);
          principalCongruencesRep.put(partCong, new IntArray(new int[] {i, j}));
        }
        else {
          partCong = (BasicPartition)pcIdMap.get(partCong);
        }
	principalCongruencesLookup.put(new IntArray(new int[] {i, j}),
				       partCong);
	//if( !principalCongruences.contains(partCong)) {
        //  principalCongruences.add(partCong);
        //}
      }
    }
  }

  public boolean universeFound() { return universe != null; }

  private boolean stopMakeUniverse = false;
  public void stopMakeUniverse() { stopMakeUniverse = true; }

  private int makeUniverseK;
  public int getMakeUniverseK() { return makeUniverseK; }
  
  public int getSizeComputed() { return sizeComputed; }

  public Task getUniverseTask() {
    // if the universe has already been found, there is no task.
    if (universeFound()) return null;
    return new Task() {
        public void stop() { stopMakeUniverse(); }
        public boolean done() { return universeFound(); }
        public int percentDone() { 
          if (joinIrreducibles == null || joinIrreducibles.size() == 0) {
            return 0;
          }
          return 100 * getMakeUniverseK() / joinIrreducibles.size();
        }
        public int amountComputed() { return getSizeComputed(); }
        public int leftToDo() {
          return joinIrreducibles.size() - getMakeUniverseK();
        }
        public void go() {
          final SwingWorker worker = new SwingWorker() {
            public Object construct() {
              makeUniverse();
              return null;
            }
          };
          worker.start();
        }
      };
  }

  public void makeUniverse() {
    makeUniverse(null);
  }

  /**
   * Construct the universe. If this method is interupted, the whole
   * calculation starts over. We might change that if there is enough
   * demand.
   */
  public void makeUniverse(final Monitor monitor) {
    ArrayList univ = new ArrayList(joinIrreducibles());
    HashSet hash = new HashSet(joinIrreducibles());
    sizeComputed = univ.size();
    makeUniverseK = 0;
    stopMakeUniverse = false;
    Iterator it = joinIrreducibles().iterator();
    final int size = joinIrreducibles().size();
int k = 0;
    while (it.hasNext()) {
System.out.println("k = " + k);
k++;
      
      if (monitor != null) {
        if (monitor.isCancelled()) return;
        else monitor.println("k = " + k + " of " + size);
      }
      makeUniverseK++;
//System.out.println("makeUniverseK = " + makeUniverseK);
//System.out.println("sizeComputed = " + sizeComputed);
      BasicPartition elem = (BasicPartition)it.next();
      int n = univ.size();
      for (int i = makeUniverseK; i < n; i++) {
        Partition join = elem.join((Partition)univ.get(i));
        if (stopMakeUniverse) return;
        //EWK
	//if (Progress.StopProgram ) {
	//    throw new ComputationStoppedException();
	//}
        if (!hash.contains(join)) {
          //TEMPORARY!! Make normal progress report later.
	  //int s = univ.size();
	  //if ( s % 1000 == 0) {
	  //  Progress.ProgressLogAppend(
	  //    "\n [ Number of congruences is already " + s);
	  //}
          int s = univ.size();
          if ( s % 10000 == 0) {
            System.out.println("size is " + s);
            //if (monitor != null) monitor.println("size is " + s);
          }
          hash.add(join);
          univ.add(join);
          sizeComputed++;
        }
      }
    }
    hash.add(zeroCong);
    univ.add(0, zeroCong);
    universe = new LinkedHashSet(univ);
    congruencesHash = hash;
  }

  /**
   * Test if beta is join prime.
   */
  public boolean joinPrime(Partition beta) {
    Partition join = zero();
    for (Iterator it = joinIrreducibles().iterator(); it.hasNext(); ) {
      Partition part = (Partition)it.next();
      if (!beta.leq(part)) {
        join = join.join(part);
        if (beta.leq(join)) return false;
      }
    }
    return true;
  }


  public boolean isDistributive() {
    for (Iterator it = joinIrreducibles().iterator(); it.hasNext(); ) {
      if (!joinPrime((Partition)it.next())) return false;
    }
    return true;
  }
      


// rsf: I'm putting back the stuff to calculate the lower covers of ji's
//      It's almost free. And I need to have them without calculating
//      the conlat.

  public void makeJoinIrreducibles() {
    joinIrreducibles = new ArrayList();
    lowerCoverOfJIs = new HashMap();
    for (Iterator it = principals().iterator(); it.hasNext(); ) {
      Partition part = (BasicPartition)it.next();
      Partition join = zero();
      for (Iterator it2 = principals().iterator(); it2.hasNext(); ) {
        BasicPartition part2 = (BasicPartition)it2.next();
        if (part2.leq(part) && (!part.equals(part2))) {
          join = join.join(part2);
        }
        if (part.equals(join)) break;
      }
      if (!part.equals(join)) {
        joinIrreducibles.add(part);
        lowerCoverOfJIs.put(part, join);
      }
    }
  }

  /**
   * If <code>beta</code> is join irreducible, this gives its lower
   * cover; otherwise null.
   */
  public BasicPartition lowerStar(BasicPartition beta) {
    //joinIrreducibles();
    if (joinIrreducibles != null) {
      return (BasicPartition)lowerCoverOfJIs.get(beta);
    }
    if (beta.equals(zero())) return null;
    BasicPartition alpha = (BasicPartition)zero();
    final int[][] blocks = beta.getBlocks();
    for (int i = 0; i < blocks.length; i++) {
      final int[] block = blocks[i];
      for (int j = 0; j < block.length; j++) {
        for (int k = j + 1; k < block.length; k++) {
          BasicPartition par = algebra().con().Cg(block[j], block[k]);
          if (!beta.equals(par)) alpha = (BasicPartition)alpha.join(par);
          if (beta.equals(alpha)) return null;
        }
      }
    }
    return alpha;
  }

  public HashMap upperCoversMap() {
    if (upperCoversMap == null) makeUpperCovers();
    return upperCoversMap;
  }

  /**
   * Makes the upperCoversMap. Assumes joinIrreducibles have been
   * made.
   */
  private void makeUpperCovers() {
    HashMap ucMap = new HashMap();
    for (Iterator it = universe().iterator(); it.hasNext(); ) {
      BasicPartition elem = (BasicPartition)it.next();
      HashSet hs = new HashSet();
      ArrayList covs = new ArrayList();
      for (Iterator it2 = joinIrreducibles().iterator(); it2.hasNext(); ) {
        BasicPartition ji = (BasicPartition)it2.next();
        if (!ji.leq(elem)) {
          Partition join = ji.join(elem);
          if (!hs.contains(join)) {
            hs.add(join);
            boolean above = false;
            for (ListIterator it3 = covs.listIterator(); it3.hasNext(); ) {
              BasicPartition cov = (BasicPartition)it3.next();
              if (cov.leq(join)) {
                above = true;
                break;
              }
              if (join.leq(cov)) it3.remove();
            }
            if (!above) covs.add(join);
          }
        }
      }
      hs = null;
      ucMap.put(elem, covs);
    }
    upperCoversMap = ucMap;
  }

  public BasicPartition Cg(Object a, Object b) {
    return Cg(algebra().elementIndex(a), algebra().elementIndex(b));
  }

  public BasicPartition Cg(int a, int b) {
    if (a == b) return (BasicPartition)zero();
    if (a > b) {
      int c = a;
      a = b;
      b = c;
    }
    if (principalCongruencesLookup != null) {
      BasicPartition p 
         =  (BasicPartition)principalCongruencesLookup.get(new int[] {a, b});
      if (p != null) return p;
      return makeCg(a, b);
    }
    return makeCg(a, b);
  }

  /**
   * This assumes a < b.
   */
  private BasicPartition makeCg(int a, int b) {
    int[] part = new int[algSize];
    for (int i = 0; i < algSize; i++ ) {
      part[i] = -1;
    }
    part[a] = -2;
    part[b] = a;
    SimpleList pairs = SimpleList.EMPTY_LIST;
    pairs = pairs.cons(new int[] {a, b});
    while (!pairs.isEmpty()) {
      //int[] pair = (int[])pairs.first();
      int x = ((int[])pairs.first())[0];
      int y = ((int[])pairs.first())[1];
      pairs = pairs.rest();
      for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
        Operation f = (Operation)it.next();
        int arity = f.arity();
      	int[] arg = new int[arity];
	      int[] arg2 = arg;
        for (int index = 0; index < arity; index++) {
          arg = arg2;
          for (int k = 0; k < arity; k++ ) {
            arg[k] = 0;
          }
          while (arg != null) {
            arg[index] = x;
            int r = BasicPartition.root(f.intValueAt(arg), part);
            arg[index] = y;
            int s = BasicPartition.root(f.intValueAt(arg), part);
            if (r != s) {
              BasicPartition.joinBlocks(r, s, part);
              pairs = pairs.cons(new int[] {r, s});
            }
            arg = incrementArg(arg, index);
          }
        }
      }
    }
    return new BasicPartition(part);
  }

  public HashSet typeSet() {
    if (typeSet == null) makeTypeSet();
    return typeSet;
  }

  private void makeTypeSet() {
    typeSet = new HashSet();
    for (Iterator it = joinIrreducibles().iterator(); it.hasNext(); ) {
      typeSet.add(new Integer(type((BasicPartition)it.next())));
    }
  }


  /**
   * Find the type of beta over its lower cover. Beta is assumed
   * to be join irreducible.
   */
  public int type(BasicPartition beta) {
    final Subtrace st = subtrace(beta);
    if (st.type() <= 0) getTypeFinder().findType(st);
    return st.type();
  }

  /**
   * Find the type for beta over alpha. Beta is assumed
   * to cover alpha.
   */
  public int type(BasicPartition beta, BasicPartition alpha) {
    final BasicPartition gamma = findJoinIrred(alpha, beta);
    return type(gamma);
  }

  /**
   * Find a subtrace for beta over its lower cover. Beta is assumed
   * to be join irreducible.
   */
  public Subtrace subtrace(BasicPartition beta) {
    final HashMap smap = getJoinIrredToSubtraceMap();
    if (smap.get(beta) == null) {
      smap.put(beta, getTypeFinder().findSubtrace(beta));
    }
    return (Subtrace)smap.get(beta);
  }
  
  /**
   * Find a subtrace for beta over alpha. Beta is assumed
   * to cover alpha.
   */
  public Subtrace subtrace(BasicPartition beta, BasicPartition alpha) {
    final BasicPartition gamma = findJoinIrred(beta, alpha);
    return subtrace(gamma);
  }

  public HashMap getJoinIrredToSubtraceMap() {
    if (joinIrredToSubtraceMap == null) joinIrredToSubtraceMap = new HashMap();
    return joinIrredToSubtraceMap;
  }

  public TypeFinder getTypeFinder() {
    if (typeFinder == null) typeFinder = new TypeFinder(algebra());
    return typeFinder;
  }

  /**
   * Increment the constants in arg which are in all positions
   * except <code>index</code>.
   */
  private int[] incrementArg(int[] arg, int index) {
    int length = arg.length;
    int max = algSize - 1;
    if (length < 2) return null;
    for (int i = 0; i < length; i++) {
      if (i == index) continue;
      if (arg[i] < max) {
        arg[i]++;
        return arg;
      }
      arg[i] = 0;
    }
    return null;
  }

  /**
   * Find a pair [a, b] (as an IntArray) which generates part.
   * Gives null if part is not principal.
   */
  public IntArray generatingPair(BasicPartition part) {
    principals();
    return (IntArray)principalCongruencesRep.get(part);
  }


  /**
   * This finds a meet irreducible congruence which is maximal with
   * respect to being above <code>a</code> and not above <code>b</code>.
   * Note if <code>a</code> is covered by <code>b</code> then
   * <code>[a,b]</code> transposes up to <code>[m,m*]</code>.
   */
  public Partition findMeetIrred (Partition a, Partition b) {
    if (b.leq(a)) return null;
    Iterator it = joinIrreducibles().iterator();
    while (it.hasNext()) {
      Partition j = ((Partition)it.next()).join(a);
      if (!b.leq(j)) a = j;
    }
    return a;
  }

/* old version, could be expensive.
  public Partition findMeetIrred (Partition a, Partition b) {
    if (b.leq(a)) return null;
    Iterator it = meetIrredCongruences.iterator();
    while (it.hasNext()) {
      Partition m = (Partition)it.next();
      if (a.leq(m) && !b.leq(m)) a = m;
    }
    return a;
  }
*/

  /**
   * This finds a join irreducible congruence which is minimal with
   * respect to being below <code>b</code> and not below <code>a</code>.
   * Note if <code>a</code> is covered by <code>b</code> then
   * <code>[a,b]</code> transposes down to <code>[j,j_*]</code>.
   */
  public BasicPartition findJoinIrred (BasicPartition a, BasicPartition b) {
    if (b.leq(a)) return null;
    Iterator it = joinIrreducibles().iterator();
    while (it.hasNext()) {
      BasicPartition ji = (BasicPartition)it.next();
      if (ji.leq(b) && !ji.leq(a)) b = ji;
    }
    return b;
  }

  private void makeMeetIrreducibles() {
    meetIrreducibles = new ArrayList();
    Iterator it = universe().iterator();
    while (it.hasNext()) {
      BasicPartition elem = (BasicPartition)it.next();
      if (((Collection)upperCoversMap().get(elem)).size() == 1) {
	meetIrredCongruences.add(elem);
      }
    }
  }

  public final BasicPartition zero() { return zeroCong; }
  public final BasicPartition one() { return oneCong; }

  /**
   * Find a subtrace for Cg(a, b), which is assumed to be join
   * irreducible.
   */
  Subtrace findSubtrace(int a, int b) {
    return findSubtrace(a, b, zero());
  }

  /**
   * Find a subtrace for Cg(a, b), which is assumed to be join
   * irreducible.
   *
   *
   * @param alpha  a congruence not above Cg(a, b).
   */
  Subtrace findSubtrace(int a, int b, Partition alpha) {
    Partition part = Cg(a,b);
    if (a == b) throw new RuntimeException("a and b cannot be equal");
    Partition partStar = (Partition)lowerCoverOfJIs.get(part);
    if (partStar == null) {
      throw new RuntimeException("Cg(" + a + ", " + b + ") is not ji");
    }
    alpha = alpha.join(partStar);
    a = alpha.representative(a);
    b = alpha.representative(b);
    if (b < a) {
      int c = a;
      a = b;
      b = c;
    }
    HashSet visited = new HashSet();
    boolean hasInvolution = false;
    int[] pair = new int[] {a, b};
    IntArray pairIA = new IntArray(pair);
    visited.add(pairIA);
    SimpleList universe = SimpleList.EMPTY_LIST;
    HashSet genHashSet = new HashSet();
    int[] roots = alpha.representatives();
    final int rootsSize = roots.length;
    for (int i = 0; i < rootsSize; i++) {
      int[] tmp = new int[] {roots[i], roots[i]};
      universe = universe.cons(tmp);
      genHashSet.add(new IntArray(tmp));
    }
    universe = universe.cons(pair);
    genHashSet.add(pairIA);
    int newCount = 0;
    int lastNewCount = rootsSize + 1;
    SimpleList old = universe;
    SimpleList lastNewAndOld = universe;

    return null;
  }

  public void makeOperationTables() {}

  public boolean isIdempotent() { return true; }


}
