package org.uacalc.alg.sublat;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.SimilarityType;
import org.uacalc.ui.tm.ProgressReport;
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

  public static ProgressReport monitor;
  
  private final SmallAlgebra alg;
  private final int algSize;
  private final int numOps;

  private final BasicSet zeroSubalg;
  private final BasicSet oneSubalg;
  
  private String description;
  
  public static final int MAX_DRAWABLE_SIZE = 100;
  private boolean nonDrawable = false;
  
  private BasicLattice basicLat;


  /** 
   * A map from pairs Integer(i) to the IntegerArray representing sg(i).
   */
  private HashMap<Integer,BasicSet> oneGeneratedSubalgLookup = null;

  /** 
   * A map from one generated subalgebras to a generator of the subalgebra.
   */
  private HashMap<BasicSet,Integer> oneGeneratedSubalgGenerator = null;

  private Set<BasicSet> universe = null;
  private HashMap<BasicSet,List<BasicSet>> upperCoversMap = null;
  private HashMap<BasicSet,BasicSet> lowerCoverOfJIs = null;
  private ArrayList<BasicSet> oneGeneratedSubalgebras = null;
  private ArrayList<BasicSet> joinIrreducibles = null;
  private ArrayList<BasicSet> meetIrreducibles = null;
  private HashSet<BasicSet> jisHash = null;

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
    List<Integer> lst = new ArrayList<Integer>();
    final int[] empty = new int[0];
    for (Operation op : alg.constantOperations()) {
      lst.add(op.intValueAt(empty));
    }
    if (lst.size() == 0) zeroSubalg = BasicSet.EMPTY_SET;
    else {
      Collections.sort(lst);
      lst = noDuplicates(lst);
      zeroSubalg = makeSg(lst);
    }
  }

  public void setMonitor(ProgressReport m) { monitor = m; }
  public ProgressReport getMonitor() { return monitor; }
  
  public final boolean monitoring() {
    return monitor != null;
  }

  public boolean isTotal() { return true; }
  
  public List constantOperations() { return SimpleList.EMPTY_LIST; }

  public SmallAlgebra getAlgebra() { return alg; }

  public boolean isUnary() { return false; }

  public String getDescription() {
    if (description != null) return description;
    return "Subalgebra Lattice of " + alg;
  }
  
  public void setDescription(String desc) {
    this.description = desc;
  }

  public int cardinality() {
    return universe().size();
  }
  
  public int inputSize() {
    final int card = cardinality();
    if (card < 0) return -1;
    return similarityType().inputSize(card);
  }
  
  public boolean isSmallerThan(int size) {
    if (universe != null) return cardinality() < size;
    if (joinIrreducibles().size() >= size) return false;
    makeUniverse(size);
    if (universe == null) return false;
    return true;
  }
  
  public boolean isDrawable() {
    if (universe != null) return cardinality() <= MAX_DRAWABLE_SIZE;
    if (sizeComputed > 0) return false;
    return isSmallerThan(MAX_DRAWABLE_SIZE + 1);
    //if (nonDrawable) return false;
    //nonDrawable = !isSmallerThan(MAX_DRAWABLE_SIZE + 1);
    //return !nonDrawable;
  }
  
  public BasicLattice getBasicLattice() {
    return getBasicLattice(true);
  }
  
  /**
   * Get the BasicLattice used primarily for drawing.
   * 
   * @return a BasicLattice view
   */
  public BasicLattice getBasicLattice(boolean makeIfNull) {
    if (basicLat == null && makeIfNull) basicLat = new BasicLattice("", this); // maybe a name
    return basicLat;
  }
  
  public org.latdraw.diagram.Diagram getDiagram() {
    if (!isDrawable()) return null;
    return getBasicLattice().getDiagram();
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

  public String getName() {
    return "Sub(" + getAlgebra() + ")";
  }
  
  public void setName(String v) {
    throw new UnsupportedOperationException();
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
  public List<BasicSet> oneGeneratedSubalgebras() {
    if (oneGeneratedSubalgebras == null) makeOneGeneratedSubalgebras();
    return oneGeneratedSubalgebras;
  }

  private void makeOneGeneratedSubalgebras() {
    if (monitoring()) monitor.printStart("finding 1 generated subalgebras of " 
        + getAlgebra().getName());
    oneGeneratedSubalgebras = new ArrayList<BasicSet>();
    oneGeneratedSubalgGenerator = new HashMap<BasicSet,Integer>();
    oneGeneratedSubalgLookup = new HashMap<Integer,BasicSet>();
    HashMap<BasicSet,BasicSet> oneGens = new HashMap<BasicSet,BasicSet>();
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
    System.out.println("oneGenerated size = " + oneGeneratedSubalgebras.size());
    if (monitoring()) monitor.printEnd("one generated subalgebras of " 
        + getAlgebra().getName() + ": size = " + oneGeneratedSubalgebras.size());
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

  // TODO implement this
  public List<BasicSet> atoms() {
    return null;
  }
  
  // TODO implement this.
  public List<BasicSet> coatoms() {
    return null;
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

  //TODO fix this
  public Map<OperationSymbol,Operation> getOperationsMap() { return null; }

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
  public static <T> List<T> noDuplicates(List<T> lst) {
    if (lst.isEmpty()) return lst;
    List<T> nodups = new ArrayList<T>();
    Iterator<T> it = lst.iterator();
    T previous = it.next();
    nodups.add(previous);
    for ( ; it.hasNext() ; ) {
      T next = it.next();
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
    List<Integer> gensList = new ArrayList<Integer>(g + zeroSubalg.size());
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
  public BasicSet makeSg(List<Integer> gens) {
    return makeSg(gens, 0);
  }
  
  public BasicSet makeSg(List<Integer> gens, int closedMark) {
    return makeSg(gens, closedMark, algSize - 1);
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
   * @param maxSize      return the whole algebra if we exceed this                    
   *
   */
  public BasicSet makeSg(List<Integer> gens, int closedMark, final int maxSize) {
    //TODO: write things using the maxSize and also k generated subalgebras.
    int currentMark = gens.size();
    final HashSet<Integer> su = new HashSet<Integer>(gens);
    final List<Integer> lst = new ArrayList<Integer>(gens);
    while (closedMark < currentMark) {
      // close the elements in current
      for (Iterator<Operation> it = alg.operations().iterator(); it.hasNext(); ) {
        Operation f = it.next();
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
              if (lst.size() > maxSize) return one();
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
      ans[i] = lst.get(i).intValue();
    }
    return new BasicSet(ans);
  }
  
  private static boolean addConstantsToMap(Map<Integer,Integer> homo,
                                           SmallAlgebra alg1,
                                           SmallAlgebra alg2) {
    final int[] empty = new int[0];
    for (Iterator<Operation> it = alg1.operations().iterator(); it.hasNext(); ) {
      Operation f = it.next();
      if (f.arity() == 0) {
        Operation g = alg2.getOperation(f.symbol());
        int fvalue = f.intValueAt(empty);
        int gvalue = g.intValueAt(empty);
        if (homo.containsKey(fvalue)) {
          if (!homo.get(fvalue).equals(gvalue)) {
            return false;
          }
        }
        else homo.put(fvalue, gvalue);
      }
    }
    return true;
  }

  /**
   * Try to extend the map gens[i] to gensB[i] to a homomorphism.
   * Both gens and gensB can have duplicates. If the induced map
   * is inconsistent, we return null. For example gens is [1,1]
   * and gensB is [2.3] is inconsistent.
   * 
   * 
   * @param gens
   * @param gensB
   * @param B
   * @return  the homomorphism as a map or null if it does not exist.
   */
  public static Map<Integer,Integer> extendToHomomorphism (final int[] gens, 
                      final int[] gensB, final SmallAlgebra A, 
                                         final SmallAlgebra B) {
    if (gens.length != gensB.length) 
      throw new IllegalArgumentException(
          "generating sets must have the same size");
    final int g = gens.length;
    final Map<Integer,Integer> homo 
              = new HashMap<Integer,Integer>(g);
    for (int i = 0; i < gens.length; i++) {
      if (homo.containsKey(gens[i]) 
          && homo.get(gens[i]).intValue() != gensB[i]) return null;
      homo.put(gens[i], gensB[i]);
    }
    if (!addConstantsToMap(homo, A, B)) return null;
    if (homo.size() == 0) return homo;  // do we really want to allow the empty homo?
    return extendToHomomorphism(homo, A, B);
  }
  
  /**
   * Try to extend the map to a homomorphism.
   * 
   * @param homo
   * @param B
   * @return
   */
  public static Map<Integer,Integer> extendToHomomorphism (
              final Map<Integer,Integer> homo, final SmallAlgebra A, 
                                               final SmallAlgebra B) {
    int closedMark = 0;
    List<Integer> lst = new ArrayList<Integer>(homo.keySet());
    Collections.sort(lst);
    int currentMark = lst.size();
    while (closedMark < currentMark) {
      // close the elements in current
      for (Iterator<Operation> it = A.operations().iterator(); it.hasNext(); ) {
        Operation f = it.next();
        final int arity = f.arity();
        if (arity == 0) continue;  // constansts are already there
        Operation g = B.getOperation(f.symbol());
        int[] argIndeces = new int[arity];
        for (int i = 0; i < arity - 1; i++) {
          argIndeces[i] = 0;
        }
        argIndeces[arity - 1] = closedMark;
        ArrayIncrementor inc = 
                    SequenceGenerator.sequenceIncrementor(
                                  argIndeces, currentMark - 1, closedMark);
        final int[] arg = new int[arity];
        final int[] argB = new int[arity];
        while (true) {
          for (int i = 0; i < arity; i++) {
            arg[i] = lst.get(argIndeces[i]).intValue();
            argB[i] = homo.get(lst.get(argIndeces[i])).intValue();
          }
          //ArrayIncrementor permInc = PermutationGenerator.arrayIncrementor(arg);  // stu
          Integer v = new Integer(f.intValueAt(arg));
          Integer w = new Integer(g.intValueAt(argB));
          if (homo.containsKey(v)) {
            if (!w.equals(homo.get(v))) {
              return null; 
            }
          }
          else {
            lst.add(v);
            homo.put(v, w);
          }
          if (!inc.increment()) break;
        }
      }
      closedMark = currentMark;
      currentMark = lst.size();
      //System.out.println("closedMark = " + closedMark);
      //System.out.println("currentMark = " + currentMark + "\n");
    }
    return homo;
  }

  
  /**
   * Test if one subuniverse is contained in another.
   */
  //public boolean leq(BasicSet s1, BasicSet s2) {
  //}

  /*
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
*/
  
  public void makeUniverse() {
    makeUniverse(-1);
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
  public void makeUniverse(int maxSize) {
    if (monitoring()) monitor.printStart("finding the universe of Sub(" 
        + getAlgebra().getName() + ")");
    sizeComputed = joinIrreducibles().size();
    universe = joinClosure(joinIrreducibles(), maxSize);
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
    if (universe != null) {
      universe.add(zeroSubalg);
      if (monitoring()) {
        monitor.printEnd("|Sub(" + getAlgebra().getName() + ")| = " + universe.size());
      }
    }
  }
  
  public Set joinClosure(Collection gens) {
    return joinClosure(gens, -1);
  }

  /**
   * This gives the set of joins of all <b>nonempty</b> subsets of
   * <tt>gens</tt>, so it is necessary to add a least element if you
   * want to include it.
   */
  public Set joinClosure(Collection gens, int maxSize) {
    final boolean stopIfBig = maxSize > 0 ? true : false;
    Set ans = new HashSet(gens);
    List ansList = new ArrayList(gens);
    int k = 0;
    int g = gens.size();
    for (Iterator it = gens.iterator(); it.hasNext(); ) {
      System.out.println("pass " + k + " of " + g + ", size = " + ansList.size());
      if (monitoring()) {
        monitor.setPassFieldText(k + " of " + g);
        monitor.setSizeFieldText("" + ansList.size());
      }
      BasicSet s = (BasicSet)it.next();
      final int n = ansList.size();
      for (int i = 0; i < n; i++) {
        if (Thread.currentThread().isInterrupted()) { 
          if (monitoring()) {
              monitor.printlnToLog("Cancelled (" + ansList.size() + " elements so far)");
          }
          return null;
          //else {
          //  System.out.println("i = " + i + " of " + n + ", size = " + ansList.size());
            //monitor.printlnToLog("k = " + k + " of " + size);
          //  monitor.setPassFieldText(i + " of " + n);
          //  monitor.setSizeFieldText("" + ansList.size());
          //}
        }
        Object  join = join(s, (BasicSet)ansList.get(i));
        if (!ans.contains(join)) {
          sizeComputed++;
          ans.add(join);
          ansList.add(join);
          if (stopIfBig && ansList.size() >= maxSize) return null;
        }
      }
      k++;
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
  
  public BasicSet findMinimalSizedGeneratingSet() {
    if (algSize == 1) return BasicSet.EMPTY_SET;
    oneGeneratedSubalgebras();
    Integer g = oneGeneratedSubalgGenerator.get(one());
    if (g != null) return new BasicSet(new int[] { g });
    for (int i = 2; i <= algSize; i++) {
      final int[] arr = new int[i];
      for (int j = 0; j < i; j++) {
        arr[j] = j;
      }
      ArrayIncrementor inc = SequenceGenerator.increasingSequenceIncrementor(arr, algSize - 1);
      while (true) {
        BasicSet sub = sg(arr);
        //System.out.println("arr = " + ArrayString.toString(arr) + ", sub = " + sub);
        //if (one().leq(sub)) return new BasicSet(arr);
        if (sub.size() == algSize) return new BasicSet(arr);
        if (!inc.increment()) break;
      }
    }
    return null;  // this should not happen
  }
  

  public final BasicSet zero() { return zeroSubalg; }
  public final BasicSet one() { return oneSubalg; }

  public void makeOperationTables() {}

  public boolean isIdempotent() { return true; }

  public static void main(String[] args) {
    SmallAlgebra alg = null;
    SmallAlgebra alg2 = null;
    if (args.length == 0) {
      try {
        alg = (SmallAlgebra)org.uacalc.io.AlgebraIO.readAlgebraFile(
                "/home/ralph/Java/Algebra/algebras/lyndon.ua");
        alg2 = (SmallAlgebra)org.uacalc.io.AlgebraIO.readAlgebraFile(
                "/home/ralph/Java/Algebra/algebras/m3.ua");
      }
      catch (Exception e) {}
      //System.out.println("map: " + SubalgebraLattice.extendToHomomorphism(
      //    new int[] {1, 2, 3}, new int[] {3, 1, 2}, alg, alg2));
      System.out.println("gen set is " + alg.sub().findMinimalSizedGeneratingSet());
      return;
    }
    System.out.println("reading " + args[0]);
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
