/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.lat;

import java.util.*;
import java.io.*;

import org.uacalc.alg.*;
import org.uacalc.ui.*;
import org.uacalc.io.*;
import org.uacalc.lat.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;
import org.latdraw.orderedset.POElem;

import javax.swing.*;
import java.awt.Color;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

// delete abstract
public class  BasicLattice extends GeneralAlgebra 
                           implements SmallAlgebra, Lattice {

  private org.latdraw.orderedset.OrderedSet poset;
  private List univList;
  private HashSet univHS;
  private int[] joinMeetTable;
  final private List operations = new ArrayList();
  private Operation join;
  private Operation meet;
  private List joinIrreducibles;
  private List meetIrreducibles;
  private HashMap tctTypeMap; // from edges to the strings "1" ... "5".

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
    tctTypeMap = new HashMap();
    for (Iterator it = lat.universe().iterator(); it.hasNext(); ) {
      BasicPartition elt = (BasicPartition)it.next();
      POElem pelt = poset.getElement(elt);
      for (Iterator it2 = pelt.upperCovers().iterator(); it2.hasNext(); ) {
        POElem pelt2 = (POElem)it2.next();
        BasicPartition elt2 = (BasicPartition)pelt2.getUnderlyingObject();
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
            if (lat.leq(elem2, join)) lstIt.remove();
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

  public org.latdraw.diagram.Diagram getDiagram() 
                      throws org.latdraw.orderedset.NonOrderedSetException {
    org.latdraw.diagram.Diagram diag
                          = new org.latdraw.diagram.Diagram(getPoset());
    if (tctTypeMap != null) diag.setEdgeColors(tctTypeMap);
    diag.setPaintLabels(true);
    diag.showLabels();
    return diag;
  }

  public int cardinality() { return univList.size(); }

  public Object zero() {
    return univList.get(0);
  }

  public Object one() {
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

  public List joinIrreducibles() {
    if (joinIrreducibles == null) {
      joinIrreducibles = new ArrayList();
      for (Iterator it = univList.iterator(); it.hasNext(); ) {
        final POElem elem = (POElem)it.next();
        if (elem.lowerCovers().size() == 1) joinIrreducibles.add(elem);
      }
    }
    return joinIrreducibles;
  }

  public List meetIrreducibles() {
    if (meetIrreducibles == null) {
      meetIrreducibles = new ArrayList();
      for (Iterator it = univList.iterator(); it.hasNext(); ) {
        final POElem elem = (POElem)it.next();
        if (elem.upperCovers().size() == 1) meetIrreducibles.add(elem);
      }
    }
    return meetIrreducibles;
  }

  public boolean leq(Object obj1, Object obj2) {
    return false;
  }

  public int elementIndex(Object obj) {
    final POElem elem = (POElem)obj;
    return poset.elemOrder(elem);
  }

  public Object getElement(int k) {
    return univList.get(k);
  }

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




