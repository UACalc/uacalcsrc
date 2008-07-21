/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.lat;

import java.util.*;
import java.io.*;

import org.uacalc.util.*;
import org.uacalc.alg.*;
import org.uacalc.ui.*;
import org.uacalc.io.*;
import org.uacalc.lat.*;
import org.uacalc.alg.SmallAlgebra.AlgebraType;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.sublat.*;
import org.latdraw.orderedset.POElem;

import javax.swing.*;
import java.awt.EventQueue;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

// delete abstract
public class  BasicLattice extends GeneralAlgebra 
                           implements SmallAlgebra, Lattice {

  private org.latdraw.orderedset.OrderedSet poset;
  private List<POElem> univList;
  private HashSet<POElem> univHS;
  private int[] joinMeetTable;
  final private List<Operation> operations = new ArrayList<Operation>();
  private Operation join;
  private Operation meet;
  private List<POElem> joinIrreducibles;
  private List<POElem> meetIrreducibles;
  private HashMap<org.latdraw.orderedset.Edge,String> tctTypeMap; // from edges to the strings "1" ... "5".
  private org.latdraw.diagram.Diagram diagram;

  /**
   * Make a BasicLattice from a poset.
   *
   * @param poset    A (latdraw) poset
   */
  public BasicLattice(String name, org.latdraw.orderedset.OrderedSet poset) {
    super(name);
    this.poset = poset;
    univList = poset.univ();
    operations.add(makeJoinOperation());
    operations.add(makeMeetOperation());
  }

  /**
   * Make a BasicLattice from a Lattice.
   *
   * @param lat    A Lattice with joinIrreducibles inplemented.
   */
  public BasicLattice(String name, Lattice lat) {
    super(name);
    poset = makeOrderedSet(lat);
    univList = poset.univ();
    operations.add(makeJoinOperation());
    operations.add(makeMeetOperation());
  }

  /**
   * Make a BasicLattice from a CongruenceLattice.
   *
   * @param lat    The congruence lattice
   * @param label  indicates if this should be TCT labelled
   */
  public BasicLattice(String name, CongruenceLattice lat, boolean label) {
    super(name);
    poset = makeOrderedSet(lat);
    if (label) makeTctTypeMap(poset, lat);
    univList = poset.univ();
    operations.add(makeJoinOperation());
    operations.add(makeMeetOperation());
  }

  private void makeTctTypeMap(org.latdraw.orderedset.OrderedSet poset, 
                                                    CongruenceLattice lat) {
    final String[] types = new String[] {"1", "2", "3", "4", "5"};
    tctTypeMap = new HashMap<org.latdraw.orderedset.Edge,String>();
    for (Partition elt : lat.universe()) {
      POElem pelt = poset.getElement(elt);
      for (Iterator it2 = pelt.upperCovers().iterator(); it2.hasNext(); ) {
        POElem pelt2 = (POElem)it2.next();
        Partition elt2 = (BasicPartition)pelt2.getUnderlyingObject();
        int typ = lat.type(elt2, elt);
        org.latdraw.orderedset.Edge e = 
             new org.latdraw.orderedset.Edge(elt.toString(), elt2.toString());
        tctTypeMap.put(e, types[typ - 1]);
      }
    }
  }


  /**
   * Make a org.latdraw.orderedset.OrderedSet from a Lattice.
   *
   * @param lat    A Lattice with joinIrreducibles implemented.
   */
  public org.latdraw.orderedset.OrderedSet makeOrderedSet (Lattice lat) {
    List univ = new ArrayList(lat.universe());
    List jis = lat.joinIrreducibles();
    List ucs = new ArrayList(univ.size());
    for (Iterator it = univ.iterator(); it.hasNext(); ) {
      List covs = new ArrayList();
      Object elem1 = it.next();
      for (Iterator it2 = jis.iterator(); it2.hasNext(); ) {
        Object join = lat.join(elem1, it2.next());
        if (!join.equals(elem1)) {
          boolean bad = false;
          for (ListIterator lstIt = covs.listIterator();  lstIt.hasNext(); ) {
            Object elem2 = lstIt.next();
            if (lat.leq(elem2, join)) {
              bad = true;
              break;
            }
            if (lat.leq(join, elem2)) lstIt.remove();
          }
          if (!bad) covs.add(join);
        }
      }
      ucs.add(covs);
    }
    org.latdraw.orderedset.OrderedSet poset = null;
    try {
      poset = new org.latdraw.orderedset.OrderedSet(null, univ, ucs);
    }
    catch(org.latdraw.orderedset.NonOrderedSetException e) {
      System.err.println(e.toString());
      e.printStackTrace();
    }
    return poset;
  }

  public org.latdraw.diagram.Diagram getDiagram() {
    if (diagram != null) return diagram;
    try {
      diagram = new org.latdraw.diagram.Diagram(getPoset());
      if (tctTypeMap != null) diagram.setEdgeColors(tctTypeMap);
      diagram.setPaintLabels(true);
      diagram.showLabels();
      return diagram;
    }
    catch (org.latdraw.orderedset.NonOrderedSetException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public int cardinality() { return univList.size(); }

  public POElem zero() {
    return univList.get(0);
  }

  public POElem one() {
    return univList.get(cardinality() - 1);
  }

  public Set universe() {
    if (univHS == null) univHS = new HashSet(univList);
    return univHS;
  }

  public Object join(List args) {
    Object ans = zero();
    for (Iterator it = args.iterator(); it.hasNext(); ) {
      ans = join(ans, it.next());
    }
    return ans;
  }

  public Object join(Object obj1, Object obj2) {
    List args = new ArrayList(2);
    args.add(obj1);
    args.add(obj2);
    return join.valueAt(args);
  }

  public Object meet(List args) {
    Object ans = one();
    for (Iterator it = args.iterator(); it.hasNext(); ) {
      ans = meet(ans, it.next());
    }
    return ans;
  }

  public Object meet(Object obj1, Object obj2) {
    List args = new ArrayList(2);
    args.add(obj1);
    args.add(obj2);
    return meet.valueAt(args);
  }

  public List<POElem> atoms() {
    return zero().upperCovers();
  }
  
  public List<POElem> coatoms() {
    return one().lowerCovers();
  }
  
  public List<POElem> joinIrreducibles() {
    if (joinIrreducibles == null) {
      joinIrreducibles = new ArrayList<POElem>();
      for (POElem elem : univList) {
        if (elem.isJoinIrreducible()) joinIrreducibles.add(elem);
      }
    }
    return joinIrreducibles;
  }

  public List<POElem> meetIrreducibles() {
    if (meetIrreducibles == null) {
      meetIrreducibles = new ArrayList<POElem>();
      for (POElem elem : univList) {
        if (elem.isMeetIrreducible()) meetIrreducibles.add(elem);
      }
    }
    return meetIrreducibles;
  }
  
  public List<POElem> joinIrredsBelow(POElem v) {
    List<POElem> ans = new ArrayList<POElem>();
    for (POElem elt : joinIrreducibles()) {
      if (leq(elt,v)) ans.add(elt);
    }
    return ans;
  }
  
  public List<POElem> meetIrredsAbove(POElem v) {
    List<POElem> ans = new ArrayList<POElem>();
    for (POElem elt : meetIrreducibles()) {
      if (leq(v, elt)) ans.add(elt);
    }
    return ans;
  }
  
  public List<POElem> lowerCovers(POElem v) {
    return (List<POElem>)v.lowerCovers();
  }
  
  public List<POElem> upperCovers(POElem v) {
    return (List<POElem>)v.upperCovers();
  }
  
  public List<POElem> irredundantMeetDecomposition(POElem v) {
    final List<POElem> decomp = new ArrayList<POElem>();
    List<POElem> uc = upperCovers(v);
    if (uc.size() == 0) return decomp;
    POElem meetSoFar = one();
    for (POElem cov : uc) {
      POElem mi = findMeetIrred(v, cov);
      if (!leq(meetSoFar, mi)) {
        meetSoFar = (POElem)meet(meetSoFar, mi);
        decomp.add(mi);
        if (leq(meetSoFar, v)) break;
      }
    }
    return makeIrredundantMeet(decomp);
  }
  
  /**
   * This finds a meet irreducible element which is maximal with
   * respect to being above <code>a</code> and not above <code>b</code>.
   * Note if <code>a</code> is covered by <code>b</code> then
   * <code>[a,b]</code> transposes up to <code>[m,m*]</code>.
   */
  public POElem findMeetIrred (POElem a, POElem b) {
    if (leq(b, a)) return null;
    for (POElem e : joinIrreducibles()) {
      if (!leq(e,a)) {
        POElem test = (POElem)join(a,e);
        if (!leq(b, test)) a = test;
      }
    }
    return a;
  }
  
  public List<POElem> makeIrredundantMeet(List<POElem> list) {
    SimpleList lst = new SimpleList(list);
    final POElem bot = (POElem)meet(lst);
    List<POElem> ans = new ArrayList<POElem>();
    POElem ansMeet = one();
    while (!lst.isEmpty()) {
      POElem a = (POElem)lst.first();
      lst = lst.rest();
      POElem b = (POElem)meet(ansMeet, meet(lst));
      if (!b.equals(bot)){
        ans.add(a);
        ansMeet = (POElem)meet(ansMeet, a);
      }
    }
    return ans;
  }
  
  public List<POElem> irredundantJoinDecomposition(POElem v) {
    final List<POElem> decomp = new ArrayList<POElem>();
    List<POElem> lc = lowerCovers(v);
    if (lc.size() == 0) return decomp;
    POElem joinSoFar = zero();
    for (POElem lcov : lc) {
      POElem ji = findJoinIrred(v, lcov);
      if (!leq(ji, joinSoFar)) {
        joinSoFar = (POElem)join(joinSoFar, ji);
        decomp.add(ji);
        if (leq(v, joinSoFar)) break;
      }
    }
    return makeIrredundantJoin(decomp);
  }

  /**
   * This finds a join irreducible element which is minimal with
   * respect to being below <code>a</code> and not below <code>b</code>.
   * Note if <code>a</code> covers <code>b</code> then
   * <code>[b,a]</code> transposes down to <code>[j_*,j]</code>.
   */
  public POElem findJoinIrred (POElem a, POElem b) {
    if (leq(a, b)) return null;
    for (POElem e : meetIrreducibles()) {
      if (!leq(a, e)) {
        POElem test = (POElem)meet(a,e);
        if (!leq(test, b)) a = test;
      }
    }
    return a;
  }
  
  public List<POElem> makeIrredundantJoin(List<POElem> list) {
    SimpleList lst = new SimpleList(list);
    final POElem top = (POElem)join(lst);
    List<POElem> ans = new ArrayList<POElem>();
    POElem ansJoin = zero();
    while (!lst.isEmpty()) {
      POElem a = (POElem)lst.first();
      lst = lst.rest();
      POElem b = (POElem)join(ansJoin, join(lst));
      if (!b.equals(top)){
        ans.add(a);
        ansJoin = (POElem)join(ansJoin, a);
      }
    }
    return ans;
  }
  
  public boolean leq(Object obj1, Object obj2) {
    POElem e1 = (POElem)obj1;
    POElem e2 = (POElem)obj2;
    return poset.leq(e1, e2);
  }

  public List<POElem> ideal(POElem v) {
    return (List<POElem>)v.ideal();
  }
  
  public List<POElem> filter(POElem v) {
    return (List<POElem>)v.filter();
  }
  
  public List<org.latdraw.diagram.Vertex> getVertices(List<POElem> lst) {
    List<org.latdraw.diagram.Vertex> ans = new ArrayList<org.latdraw.diagram.Vertex>();
    for (POElem elt : lst) {
      ans.add(getDiagram().vertexForPOElem(elt));
    }
    return ans;
  }
  
  /**
   * Form the dual of the lattice.
   */
  public BasicLattice dual() {
    return Lattices.dual(this);
  }

  public int elementIndex(Object obj) {
    System.out.println("poset = " + poset + ", obj = " + obj);
    final POElem elem = (POElem)obj;
    System.out.println("poset = " + poset 
        + ", elem.getUnderlyingObject() = " + elem.getUnderlyingObject());
    return poset.elemOrder(elem);
  }

  public Object getElement(int k) {
    return univList.get(k);
  }

  // do something ??
  public List getUniverseList() { return null; }
  public Map getUniverseOrder() { return null; }


  public org.latdraw.orderedset.OrderedSet getPoset() { return poset; }

  private Operation makeJoinOperation() {
    final int n = cardinality();
    join = new AbstractOperation(OperationSymbol.JOIN, n) {
        public Object valueAt(List args) {
          final int i0 = elementIndex(args.get(0));
          final int i1 = elementIndex(args.get(1));
          return univList.get(intValueAt(new int[] {i0, i1}));
        }
        // need a makeTable but not having it saves space
        public int intValueAt(int[] args) {
          int minIndex = args[0];
          int maxIndex = args[1];
          if (maxIndex < minIndex) {
            int foo = minIndex;
            minIndex = maxIndex;
            maxIndex = foo;
          }
          final POElem arg0 = (POElem)univList.get(args[0]);
          final POElem arg1 = (POElem)univList.get(args[1]);
          for (int i = maxIndex; i < n; i++) {
            final POElem elem = (POElem)univList.get(i);
            if (poset.leq(arg0, elem) && poset.leq(arg1, elem)) return i;
          }
          return -1;  // no upper bound. cannot happen in a lattice
        }
    };
    return join;
  }

  private Operation makeMeetOperation() {
    final int n = cardinality();
    meet = new AbstractOperation(OperationSymbol.MEET, n) {
        public Object valueAt(List args) {
          final int i0 = poset.elemOrder((POElem)args.get(0));
          final int i1 = poset.elemOrder((POElem)args.get(1));
          return univList.get(intValueAt(new int[] {i0, i1}));
        }
        // need a makeTable but not having it saves space
        public int intValueAt(int[] args) {
          int minIndex = args[0];
          int maxIndex = args[1];
          if (maxIndex < minIndex) {
            int foo = minIndex;
            minIndex = maxIndex;
            maxIndex = foo;
          }
          final POElem arg0 = (POElem)univList.get(args[0]);
          final POElem arg1 = (POElem)univList.get(args[1]);
          for (int i = minIndex; i >= 0; i--) {
            final POElem elem = (POElem)univList.get(i);
            if (poset.leq(elem, arg0) && poset.leq(elem, arg1)) return i;
          }
          return -1;  // no upper bound. cannot happen in a lattice
        }
    };
    return meet;
  }
  
  public void convertToDefaultValueOps() {
    throw new UnsupportedOperationException("Only for basic algebras"); 
  }
  
  public AlgebraType algebraType() {
    return AlgebraType.BASIC_LATTICE;
  }

  /**
   * An optional operation returning the list of join irreducible elements.
   */
//  public List joinIrreducibles();

  /**
   * An optional operation returning the list of meet irreducible elements.
   */
//  public List meetIrreducibles();

//  public Object join(Object a, Object b);
//  public Object join(List args);
//  public Object meet(Object a, Object b);
//  public Object meet(List args);


  public static void main(String[] args) throws IOException,
                              BadAlgebraFileException, 
                              org.latdraw.orderedset.NonOrderedSetException {
    if (args.length == 0) return;
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    BasicLattice con = new BasicLattice("con", alg.con(), true);
    org.latdraw.diagram.Diagram testDiagram = con.getDiagram();
    LatDrawer frame = new LatDrawer(testDiagram);
    frame.setContent(frame.getDrawPanel(), frame.getToolBar(), null);
    frame.setLabels(testDiagram);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //int width = (screenSize.width * 8) / 10;
    int height = (screenSize.height * 8) / 10;
    int width = height;
    frame.setLocation((screenSize.width - width) / 2,
                      (screenSize.height - height) / 2);
    frame.setSize(width, height);
    frame.isDefaultLookAndFeelDecorated();
    Runnable  runner = new FrameShower(frame);
    EventQueue.invokeLater(runner);
    //frame.setVisible(true);
  }

  private static class FrameShower implements Runnable {
    final JFrame frame;

    public FrameShower(JFrame frame) {
      this.frame = frame;
    }

    public void run() {
      frame.setVisible(true);
    }
  }



}




