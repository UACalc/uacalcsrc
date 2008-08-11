/* BasicPartition.java 2001/06/04 Ralph Freese */

package org.uacalc.alg.sublat;

import org.uacalc.util.IntArray;
import org.uacalc.util.ArrayString;
import org.uacalc.alg.SmallAlgebra;


import java.util.*;

/**
 * This class implement the basic set operations on the set
 * {0, 1, ..., n-1}. 
 *
 * @author Ralph Freese
 * @version $Id$
 */
public class BasicSet extends IntArray implements Comparable {

  public static final BasicSet EMPTY_SET = new BasicSet(new int[0]);

  public BasicSet(int[] set) {
    super(set);
    normalize();
  }

  /**
   * Put the array in ascending order. This modifies this.array.
   */
  public void normalize() {
    Arrays.sort(getArray());
  }

  /**
   * The order of a linear extension respecting rank. It first uses
   * size and then lexicographic order.
   */
  public int compareTo(Object o) {
    BasicSet set = (BasicSet)o;
    final int n = set.size();
    if (size < n) return -1;
    if (size > n) return 1;
    for (int i = 0; i < n; i++) {
      if (this.get(i) < set.get(i)) return -1;
      if (this.get(i) > set.get(i)) return 1;
    }
    return 0;
  }


/*
  public BasicSet intersection(BasicSet set2) {
    return intersection(this.array, set2.toArray());
  }

  static BasicSet intersection(int[] u, int[] v) {
    int n = u.length;
    HashMap ht = new HashMap();
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      // Get the roots and make a "partition" [r1, r2] so we can 
      // store them on a HT.
      IntArray rootPair 
               = new IntArray(new int[] {root(i, u), root(i, v)});
      Integer rootInt = (Integer)ht.get(rootPair);
      if (rootInt != null) {
        int r = rootInt.intValue();
        ans[r]--; // increase the size by 1 (since it is stored as a neg int)
        ans[i] = r;
      }
      else {
        ht.put(rootPair, new Integer(i));
        ans[i] = -1;
      }
    }
    return new BasicSet(ans);
  }
*/

  /**
   * Is this a subset of <tt>set2</tt>.  Both are
   * assumed to be sorted.
   */
  public boolean leq(BasicSet set2) {
    return leq(this.array, set2.toArray());
  }

  /**
   * Is u a subset of v.  Both are assumed to be sorted.
   */
  public static boolean leq(int[] u, int[] v) {
    final int n = u.length;
    final int m = v.length;
    if (m < n) return false;
    int j = 0;
    for (int i = 0; i < n; i++) {
      boolean ok = false;
      for ( ; j < m; j++) {
        if (u[i] < v[j]) return false; // was >, stu
        if (u[i] == v[j]) {
          ok = true;
          break;
        }
      }
      if (!ok) return false;
    }
    return true;
  }

  public boolean contains(int i) {
    return Arrays.binarySearch(getArray(), i) >= 0;
  }
  
  /**
   * Set difference of this and set2.
   */
  public BasicSet setDifference(BasicSet set2) {
    List lst = new ArrayList();
    final int n = size();
    for (int i = 0; i < n; i++) {
      if (!set2.contains(get(i))) lst.add(new Integer(get(i)));
    }
    final int k = lst.size();
    int[] arr = new int[k];
    for (int i = 0; i < k; i++) {
      arr[i] = ((Integer)lst.get(i)).intValue();
    }
    return new BasicSet(arr);
  }

  /**
   * Print this subset using alg's elements.
   */
  public String toString(SmallAlgebra alg) {
    StringBuffer sb = new StringBuffer("{");
    final int[] arr = getArray();
    for (int i = 0; i < arr.length; i++) {
      sb.append(ArrayString.toString(alg.getElement(arr[i])));
      if (i != arr.length - 1) sb.append(",");
    }
    sb.append("}");
    return sb.toString();
  }
    

    

}

