package org.uacalc.lat;

import java.util.*;

/**
 * Static algorithms for ordered set and lattices.
 * 
 * @author ralph
 *
 */
public class OrderedSets {
  
  public static <E> List<E> maximals(Collection<? extends E> elems, Order<? super E> order) {
    // way too much generics!! See p 40 of Java Generics.
    List<E> ans = new ArrayList<E>();
    List<E> newAns = new ArrayList<E>();
    for (E candidate : elems) {
      boolean candidateBelow = false;
      for (E e : ans) {
        if (order.leq(candidate, e)) {
          candidateBelow = true;
          break;
        }
        if (!order.leq(e, candidate)) newAns.add(e);
      }
      if (!candidateBelow) {
        ans = newAns;
        ans.add(candidate);
      }
      newAns = new ArrayList<E>();
    }
    return ans; 
  }

  public static void main(String[] args) {
    List<Integer> lst = new ArrayList<Integer>();
    lst.add(2);
    lst.add(3);
    lst.add(6);
    lst.add(35);
    lst.add(35 * 5);
    List<Integer> maxs = maximals(lst, new Order<Integer>() {
      public boolean leq(Integer a, Integer b) {
        return a % b == 0;
      }
    });
    System.out.println("max's are " + maxs);
  }
  
}
