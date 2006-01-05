/* Algebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.lat;

import java.util.*;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
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




