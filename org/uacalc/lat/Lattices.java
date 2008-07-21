/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.lat;

import java.util.*;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.sublat.*;

public class Lattices {

  private Lattices() {}

  public SmallLattice conToSmallLattice(CongruenceLattice con) {
    List univ = new ArrayList(con.universe());
    List jis = con.joinIrreducibles();
    List ucs = new ArrayList(univ.size());
    for (Iterator it = univ.iterator(); it.hasNext(); ) {
      List covs = new ArrayList();
      Partition par = (Partition)it.next();
      for (Iterator it2 = jis.iterator(); it2.hasNext(); ) {
        Partition join = par.join((Partition)it2.next());
        if (!join.equals(par)) {
          boolean bad = false;
          for (ListIterator lstIt = covs.listIterator();  lstIt.hasNext(); ) {
            Partition elem = (Partition)lstIt.next();
            if (elem.leq(join)) {
              bad = true;
              break;
            }
            if (join.leq(elem)) lstIt.remove();
          }
          if (!bad) covs.add(join);
        }
      }
      ucs.add(covs);
    }
    org.latdraw.orderedset.OrderedSet poset;
    try {
      poset = new org.latdraw.orderedset.OrderedSet(null, univ, ucs);
    }
    catch(org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static BasicLattice latticeFromMeet(final String name, 
                                             final List univ, 
                                             final Operation meet) {
    final int s = univ.size();
    final List filters = new ArrayList(s);
    for (Iterator it = univ.iterator(); it.hasNext(); ) {
      final Object elem = it.next();
      final List filter = new ArrayList();
      for (Iterator it2 = univ.iterator(); it2.hasNext(); ) {
        final List args = new ArrayList(2);
        final Object elem2 = it2.next();
        args.add(elem);
        args.add(elem2);
        if (meet.valueAt(args).equals(elem)) filter.add(elem2);
      }
      filters.add(filter);
    }
    int maxCount = 0;
    for (Iterator it = filters.iterator(); it.hasNext(); ) {
      if (((List)it.next()).size() == 1) {
        maxCount++;
        if (maxCount > 1) break;
      }
    }
    if (maxCount > 1) {
      String top = "TOP";
      univ.add(top);
      filters.add(new ArrayList(1));
      for (Iterator it = filters.iterator(); it.hasNext(); ) {
        ((List)it.next()).add(top);
      }
    }
    try {
      org.latdraw.orderedset.OrderedSet poset =
        org.latdraw.orderedset.OrderedSet.orderedSetFromFilters(name, 
                                                                univ, filters);
      return new BasicLattice(name, poset);
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static BasicLattice latticeFromJoin(final String name, final List univ, final Operation join) {
    final int s = univ.size();
    final List filters = new ArrayList(s);
    for (Iterator it = univ.iterator(); it.hasNext(); ) {
      final Object elem = it.next();
      final List filter = new ArrayList();
      for (Iterator it2 = univ.iterator(); it2.hasNext(); ) {
        final List args = new ArrayList(2);
        final Object elem2 = it2.next();
        args.add(elem);
        args.add(elem2);
        if (join.valueAt(args).equals(elem2)) filter.add(elem2);
      }
      filters.add(filter);
    }
    int maxCount = 0;
    for (Iterator it = filters.iterator(); it.hasNext(); ) {
      if (((List)it.next()).size() == 1) {
        maxCount++;
        if (maxCount > 1) break;
      }
    }
    if (maxCount > 1) {
      String top = "TOP";
      univ.add(top);
      filters.add(new ArrayList(1));
      for (Iterator it = filters.iterator(); it.hasNext(); ) {
        ((List)it.next()).add(top);
      }
    }
    try {
      org.latdraw.orderedset.OrderedSet poset =
        org.latdraw.orderedset.OrderedSet.orderedSetFromFilters(name, 
            univ, filters);
      return new BasicLattice(name, poset);
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  // this causes a npe  in making an ordered set from latdraw.
  public static BasicLattice dual(final BasicLattice lat) {
    Lattice latx = new BasicLattice(lat.getName() + "Dual", lat) {
      public List joinIrreducibles() { return lat.meetIrreducibles(); }      
      public Object meet(Object a, Object b) { return lat.join(a,b); }
      public boolean leq(Object a, Object b) { return lat.leq(b,a); }
    };
    return new BasicLattice(lat.getName() + "Dual", latx);
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


}




