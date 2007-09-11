package org.uacalc.alg.sublat;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.SimilarityType;
import org.uacalc.util.*;
import org.uacalc.lat.*;

import java.util.*;

/**
 * A class to represent the subalgebra lattice of a basic algebra;
 * this is, an algebra with universe
 * the integers from 0 to n-1. 
 *
 * @author Ralph Freese
 * @version $Id$
 */ 
public class SubalgebraLattice implements Lattice {

  private final SmallAlgebra alg;
  private final int algSize;
  private final int numOps;

  private final BasicSet zeroSubalg;
  private final BasicSet oneSubalg;


  /** 
   * A map from pairs Integer(i) to the IntegerArray representing sg(i).
   */
  private HashMap oneGeneratedSubalgLookup = null;

  /** 
   * A map from one generated subalgebras to a generator of the subalgebra.
   */
  private HashMap oneGeneratedSubalgGenerator = null;

  private Set universe = null;
  private HashMap upperCoversMap = null;
  private HashMap lowerCoverOfJIs = null;
  private ArrayList oneGeneratedSubalgebras = null;
  private ArrayList joinIrreducibles = null;
  private ArrayList meetIrreducibles = null;
  private HashSet jisHash = null;

  /**
   * The size of the universe as it is being computed for the progress
   * bar.
   */
  private int sizeComputed = 0;

  private boolean jisMade = false;

  public SubalgebraLattice(SmallAlgebra alg) {
    this.alg = alg;
    algSize = alg.cardinality();
    numOps = alg.operations().size();
    final int[] arr = new int[algSize];
    for (int i = 0; i < algSize; i++) {
      arr[i] = i;
    }
    oneSubalg = new BasicSet(arr);
    List lst = new ArrayList();
    final int[] empty = new int[0];
    for (Iterator it = alg.constantOperations().iterator(); it.hasNext(); ) {
      Operation op = (Operation)it.next();
      final int k = op.intValueAt(empty);
      lst.add(new Integer(k));
    }
    if (lst.size() == 0) zeroSubalg = BasicSet.EMPTY_SET;
    else {
      Collections.sort(lst);
      lst = noDuplicates(lst);
      zeroSubalg = makeSg(lst);
    }
  }

  public List constantOperations() { return SimpleList.EMPTY_LIST; }

  public SmallAlgebra algebra() { return alg; }

  public boolean isUnary() { return false; }

  public String description() {
    return "Subalgebra Lattice of " + alg;
  }

  public int cardinality() {
    return universe().size();
  }

  public Set universe() {
    if (universe == null) makeUniverse();
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
    return "Sub(" + algebra() + ")";
  }

  /**
   * The set of all subalgebras containing the subalgebra <tt>elt</tt>.
   */
  public Set filter(BasicSet elt) {
    Set ans = new HashSet();
    if (universeFound()) {
      for (Iterator it = universe().iterator(); it.hasNext(); ) {
        BasicSet elt2 = (BasicSet)it.next();
        if (elt.leq(elt2)) ans.add(elt2);
       }
       return ans;
    }
    for (Iterator it = oneGeneratedSubalgebras().iterator(); it.hasNext(); ) {
      final BasicSet elt2 = (BasicSet)it.next();
      ans.add(join(elt, elt2));
    }
    ans = joinClosure(ans);
    ans.add(elt);
    return ans;
  }

  /**
   * These are really one generate subuniverses.
   * 
   * @return
   */
  public List oneGeneratedSubalgebras() {
    if (oneGeneratedSubalgebras == null) makeOneGeneratedSubalgebras();
    return oneGeneratedSubalgebras;
  }

  private void makeOneGeneratedSubalgebras() {
    oneGeneratedSubalgebras = new ArrayList();
    oneGeneratedSubalgGenerator = new HashMap();
    oneGeneratedSubalgLookup = new HashMap();
    HashMap oneGens = new HashMap();
    for (int i = 0 ; i < algSize; i++) {
      BasicSet sub = sg(new int[]{i});
      if (!oneGens.containsKey(sub)) {
        oneGens.put(sub, sub);
        oneGeneratedSubalgebras.add(sub);
        oneGeneratedSubalgGenerator.put(sub, new Integer(i));
      }
      else {
        sub = (BasicSet)oneGens.get(sub);
      }
      oneGeneratedSubalgLookup.put(new Integer(i), sub);
    }
    Collections.sort(oneGeneratedSubalgebras);
  }

  /**
   * A list of the join irreducibles; constructed if necessary.
   */
  public List joinIrreducibles() {
    if (joinIrreducibles == null) makeJoinIrreducibles();
    return joinIrreducibles;
  }

  public boolean joinIrreducible(BasicSet subalg) {
    // make sure the join irreduciles have been make
    joinIrreducibles();
    return jisHash.contains(subalg);
  }

  public List meetIrreducibles() {
    if (meetIrreducibles == null) makeMeetIrreducibles();
    return meetIrreducibles;
  }

  public Object join(Object a, Object b) {
    BasicSet seta = (BasicSet)a;
    BasicSet setb = (BasicSet)b;
    BasicSet foo;
    if (setb.size() > seta.size()) {
      foo = seta;
      seta = setb;
      setb = foo;
    }
    foo = setb.setDifference(seta);
    final int closedMark = seta.size();
    List lst = new ArrayList(closedMark + foo.size());
    for (int i = 0; i < closedMark; i++) {
      lst.add(new Integer(seta.get(i)));
    }
    for (int i = 0; i < foo.size(); i++) {
      lst.add(new Integer(foo.get(i)));
    }
    return makeSg(lst, closedMark);
  }

  // don't forget if args is empty to give the constants.
  public Object join(List args) { return null; }


  public Object meet(Object a, Object b) { return null; }
  public Object meet(List args) { return null; }

  public boolean leq(Object a, Object b) {
    return ((BasicSet)a).leq((BasicSet)b);
  }

  // fix this
  public List operations() { return null; }

  // fix this
  public Operation getOperation(OperationSymbol sym) { return null; }


  //public CongruenceLattice con() { return null; }

  /**
   * Has the universe been calculated.
   */
  public boolean universeFound() { return universe != null; }

  private boolean stopMakeUniverse = false;
  public void stopMakeUniverse() { stopMakeUniverse = true; }

  private int makeUniverseK;
  public int getMakeUniverseK() { return makeUniverseK; }
  
  public int getSizeComputed() { return sizeComputed; }

  public Set sg(Set set) {
    return null;
  }

  /**
   * This gives a List without duplicates assuming <tt>lst</tt> is sorted.
   *
   * @param lst    a sorted list (so duplicates occur in blocks).
   */
  public static List noDuplicates(List lst) {
    if (lst.isEmpty()) return lst;
    List nodups = new ArrayList();
    Iterator it = lst.iterator();
    Object previous = it.next();
    nodups.add(previous);
    for ( ; it.hasNext() ; ) {
      Object next = it.next();
      if (!next.equals(previous)) nodups.add(next);
      previous = next;
    }
    return nodups;
  }

  public Subalgebra Sg(BasicSet s) {
    return new Subalgebra("Subalgebra Of " + alg, alg,  s);
  }
  
  public Subalgebra Sg(int[] gens) {
    return Sg(sg(gens));
  }
  
  public BasicSet sg(int[] gens) {
    final int g = gens.length;
    if (g == 0) return zeroSubalg;
    List gensList = new ArrayList(g + zeroSubalg.size());
    // ToDo delete elems of gens which lie in zeroSubalg.
    // add all constants
    for (int i = 0 ; i < zeroSubalg.size(); i++) {
      gensList.add(new Integer(zeroSubalg.get(i)));
    }
/*
    final int[] empty = new int[0];
    for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
      Operation f = (Operation)it.next();
      if (f.arity() == 0) gensList.add(new Integer(f.intValueAt(empty)));
    }
*/
    for (int i = 0 ; i < g; i++) {
      gensList.add(new Integer(gens[i]));
    }
    Collections.sort(gensList);
/*
    Iterator it = gensList.iterator();
    Integer previous = (Integer)it.next();
    List nodups = new ArrayList(g);
    nodups.add(previous);
    for ( ; it.hasNext() ; ) {
      Integer next = (Integer)it.next();
      if (!next.equals(previous)) nodups.add(next);
      previous = next;
    }
*/
    gensList = noDuplicates(gensList);
    // add the constants to nodups  !!!
    return makeSg(gensList);
  }

  /**
   * Make the subuniverse generated by <tt>gens</tt>.
   *
   * @param gens    a list of Integers without duplicates
   *                which contains all the constants of the algebra.
   *
   */
  public BasicSet makeSg(List gens) {
    return makeSg(gens, 0);
  }
  /**
   * Make the subuniverse generated by <tt>gens</tt>.
   *
   * @param gens         a list of Integers without duplicates
   *                     which contains all the constants of the algebra.
   *
   * @param closedMark   the closure of all elements up to this are
   *                     already in  <tt>gens</tt>.
   *
   */
  public BasicSet makeSg(List gens, int closedMark) {
    int currentMark = gens.size();
    final HashSet su = new HashSet(gens);
    final List lst = new ArrayList(gens);
    while (closedMark < currentMark) {
      // close the elements in current
      for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
        Operation f = (Operation)it.next();
        final int arity = f.arity();
        if (arity == 0) continue;  // constansts are already there
        int[] argIndeces = new int[arity];
        for (int i = 0; i < arity - 1; i++) {
          argIndeces[i] = 0;
        }
        argIndeces[arity - 1] = closedMark;
        ArrayIncrementor inc = 
                    SequenceGenerator.nondecreasingSequenceIncrementor(
                                  argIndeces, currentMark - 1, closedMark);
        final int[] arg = new int[arity];
        while (true) {
          for (int i = 0; i < arity; i++) {
            arg[i] = ((Integer)lst.get(argIndeces[i])).intValue();
          }
          ArrayIncrementor permInc = PermutationGenerator.arrayIncrementor(arg);
          while (true) {
            Integer v = new Integer(f.intValueAt(arg));
            if (su.add(v)) {
              lst.add(v);
              if (lst.size() == algSize) return one();
            }
            if (!permInc.increment()) break;
          }
          if (!inc.increment()) break;
        }
      }
      closedMark = currentMark;
      currentMark = lst.size();
    }
    int[] ans = new int[currentMark];
    Collections.sort(lst);

    for (int i = 0; i < currentMark; i++) {
      ans[i] = ((Integer)lst.get(i)).intValue();
    }
    return new BasicSet(ans);
  }

  /**
   * Test if one subuniverse is contained in another.
   */
  //public boolean leq(BasicSet s1, BasicSet s2) {
  //}

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

  /**
   * Construct the universe. If this method is interupted, the whole
   * calculation starts over. We might change that if there is enough
   * demand.
   *
   * Use the usual method for computing the closure expect we keep the
   * a HashSet jisHash. This is originally set to all one generated 
   * subalgebras but if it is discovered that one of these is not 
   * join irreducible, it is removed from the set and it is not used
   * in computing the closure. **Clear this up.***
   *
   */
  public void makeUniverse() {
    universe = joinClosure(joinIrreducibles());
    /*
    universe = new HashSet(joinIrreducibles());
    List univ = new ArrayList(joinIrreducibles());
    for (Iterator it = joinIrreducibles().iterator(); it.hasNext(); ) {
      BasicSet s = (BasicSet)it.next();
      int n = univ.size();
      for (int i = 0; i < n; i++) {
        Object  join = join(s, (BasicSet)univ.get(i));
        if (!universe.contains(join)) {
          universe.add(join);
          univ.add(join);
        }
      }
    }
    */
    universe.add(zeroSubalg);
  }

  /**
   * This gives the set of joins of all <b>nonempty</b> subsets of
   * <tt>gens</tt>, so it is necessary to add a least element if you
   * want to include it.
   */
  public Set joinClosure(Collection gens) {
    Set ans = new HashSet(gens);
    List ansList = new ArrayList(gens);
    for (Iterator it = gens.iterator(); it.hasNext(); ) {
      BasicSet s = (BasicSet)it.next();
      final int n = ansList.size();
      for (int i = 0; i < n; i++) {
        Object  join = join(s, (BasicSet)ansList.get(i));
        if (!ans.contains(join)) {
          ans.add(join);
          ansList.add(join);
        }
      }
    }
    return ans;
  }


  public void makeJoinIrreducibles() {
    jisHash = new HashSet();
    lowerCoverOfJIs = new HashMap();
    joinIrreducibles = new ArrayList();
    // ones will be sorted by size.
    final List ones = oneGeneratedSubalgebras();
    for (int i = 0; i < ones.size(); i++) {
      BasicSet set = (BasicSet)ones.get(i);
      BasicSet lower = BasicSet.EMPTY_SET;
      for (int j = i - 1; j >= 0; j--) {
        BasicSet set2 = (BasicSet)ones.get(j);
        if (set.size() == set2.size()) continue;
        if (set2.leq(set) && !set2.leq(lower)) {
          if (lower.equals(BasicSet.EMPTY_SET)) lower = set2;
          else {
            final int mark = lower.size();
            List u = new ArrayList();
            for (int k = 0; k < mark; k++) {
              u.add(new Integer(lower.get(k)));
            }
            BasicSet diff = set2.setDifference(lower);
            for (int k = 0; k < diff.size(); k++) {
              u.add(new Integer(diff.get(k)));
            }
            lower = makeSg(u, mark);
          }
        }
        if (lower.equals(set)) break;
      }
      if (!lower.equals(set)) {
        jisHash.add(set);
        joinIrreducibles.add(set);
        lowerCoverOfJIs.put(set, lower);
      }
    }
    Collections.sort(joinIrreducibles);
  }
        
      

/*
  public HashMap upperCoversMap() {
    if (upperCoversMap == null) makeUpperCovers();
    return upperCoversMap;
  }
*/

  /**
   * Makes the upperCoversMap. Assumes joinIrreducibles have been
   * made.
   */
/*
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
*/

/*
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
*/

  /**
   * This assumes a < b.
   */
/*
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
*/

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


  private void makeMeetIrreducibles() {
/*
    meetIrreducibles = new ArrayList();
    Iterator it = universe().iterator();
    while (it.hasNext()) {
      BasicPartition elem = (BasicPartition)it.next();
      if (((Collection)upperCoversMap().get(elem)).size() == 1) {
	meetIrredCongruences.add(elem);
      }
    }
*/
  }

  public final BasicSet zero() { return zeroSubalg; }
  public final BasicSet one() { return oneSubalg; }

  public void makeOperationTables() {}

  public boolean isIdempotent() { return true; }

  public static void main(String[] args) {
    if (args.length == 0) return;
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = null;
    try {
      alg = (SmallAlgebra)org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    }
    catch (Exception e) {}
    //List lst = new ArrayList();  // the square
    //lst.add(alg);
    //lst.add(alg);
    //alg = new ProductAlgebra(lst);
    System.out.println("The alg \n" + alg);
    for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
      System.out.println("op: " + ((Operation)it.next()).symbol());
    }
    SubalgebraLattice s = new SubalgebraLattice(alg);
    System.out.println("zero subalg is " + s.zeroSubalg);
    BasicSet foo = s.sg(new int[] {2});
    System.out.println(foo);
    List oneGens = s.oneGeneratedSubalgebras();
    System.out.println("num oneGens is " + oneGens.size());
    List jis = s.joinIrreducibles();
    System.out.println("num jis is " + jis.size());
    System.out.println("num subalgebras is " + s.universe().size());
    
    //System.out.println(ArrayString.toString(foo));
  }


}
