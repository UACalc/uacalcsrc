/* PermutationGenerator.java (c) 2004/11/11  Ralph Freese */

package org.uacalc.util;

import java.util.*;

/**
 * This class is used to help generate permutations of arrays. It has
 * has one public method: nextIndex() which returns the
 * index i such that the next permutation should interchange the 
 * i<sup>th</sup> and following elements.
 * <p>
 * Using this one can start with a fixed array or List and modify it in
 * place, generating all permutations of the elements. Note if the two
 * elements to be interchanged are the same, nextIndex() can
 * just be called again.
 * <p>
 * This class has static methods giving both an Iterator and an
 * (in place) ArrayIncrementor.
 * <p>
 * This uses the Johnson-Trotter algorithm:
 * start with the indentity permutation and with the direction of
 * each integer left. If there is a mobile integer k, 
 * find the largest one, swap it with the neighbor it points to, 
 * and reverse the arrows of all integers larger than k. 
 * Mobile means pointing to a smaller integer.
 * 
 */
public class PermutationGenerator {

  final private int n;
  private int[] a;
  private boolean[] arrows; // false for left, true for right
  private int largestMobileIndex = -1;  // only used in the iterator

  public PermutationGenerator(int n) {
    this.n = n;
    if (n < 1) {
      throw new IllegalArgumentException("Min 1");
    }
    a = new int[n];
    reset();
  }

  public void reset () {
    arrows = new boolean[n];
    for (int i = 0; i < n; i++) {
      a[i] = i;
    }
  }

  public int nextIndex() {
    int ans;
    int k = largestMobileIndex;
    if (k == -1) k = findLargestMobileIndex();
    if (k == -1) return k;
    if (arrows[a[k]]) ans = k;
    else ans = k - 1;
    int largestMob = a[k];
    for (int i = a[k] + 1; i < n; i++) {
      arrows[i] = !arrows[i];
    }
    int tmp = a[ans];
    a[ans] = a[ans + 1];
    a[ans + 1] = tmp;
    return ans;
  }

  /**
   * Finds the index of the largest mobile element (not the largest index).
   */
  private int findLargestMobileIndex() {
    int largestMob = -1;
    int largestMobileInd = -1;
    for (int i = 0; i < n; i++) {
      if (a[i] > largestMob) {
        if (arrows[a[i]]) {
          if (i != n - 1 && a[i] > a[i+1]) {
            largestMobileInd = i;
            largestMob = a[i];
          }
        }
        else {
          if (i != 0 && a[i] > a[i-1]) {
            largestMobileInd = i;
            largestMob = a[i];
          }
        }
      }
    }
    return largestMobileInd;
  }

  /**
   * This iterator iterates all permutations on the set 0, ..., n-1.
   * The iteration is on a fixed array so one needs to be careful to
   * copy any permutation that needs to be saved.
   */
  public static Iterator iterator(final int n) {
    return new Iterator() {
        final PermutationGenerator g = new PermutationGenerator(n);
        boolean first = true;
        public boolean hasNext() {
          g.largestMobileIndex = g.findLargestMobileIndex();
          return g.largestMobileIndex != -1;
        }
        public Object next() {
          if (first) {
            first = false;
            return g.a;
          }
          if (g.nextIndex() == -1) throw new NoSuchElementException();
          return g.a;
        }
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }

  /**
   * This increments arr, applying the next transposition that results
   * in a different array.
   * The iteration is on a fixed array so one needs to be careful to
   * copy any result that needs to be saved.
   */
  public static ArrayIncrementor arrayIncrementor(final int[] arr) {
    return new ArrayIncrementor() {
        final PermutationGenerator g = new PermutationGenerator(arr.length);
        private void swap(int k) {
          int a = arr[k];
          arr[k] = arr[k+1];
          arr[k+1] = a;
        }
        public boolean increment() {
          while (true) {
            int k = g.nextIndex();
            if (k == -1) {
              if (arr.length > 1) swap(0);// this put arr back to its original
              return false;
            }
            if (arr[k] == arr[k+1]) continue;
            swap(k);
            return true;
          }
        }
      };
  }

  /**
   * This increments arr, applying the next transposition that results
   * in a different array.
   * The iteration is on a fixed array so one needs to be careful to
   * copy any result that needs to be saved.
   */
  public static ArrayIncrementor listIncrementor(final List lst) {
    return new ArrayIncrementor() {
        final PermutationGenerator g = new PermutationGenerator(lst.size());
        private void swap(int k) {
          Object a = lst.get(k);
          lst.set(k, lst.get(k+1));
          lst.set(k+1, a);
        }
        public boolean increment() {
          while (true) {
            int k = g.nextIndex();
            if (k == -1) {
              if (lst.size() > 1) swap(0);// this put arr back to its original
              return false;
            }
            if (lst.get(k).equals(lst.get(k+1))) continue;
            swap(k);
            return true;
          }
        }
      };
  }

  public static void main(String[] args) {
    int k = 0;
    PermutationGenerator g = new PermutationGenerator(5);
    for (k = 0; k < 120; k++) {
      System.out.println("perm " + k + " = " + ArrayString.toString(g.a));
      System.out.println("nextIndex " + k + " = " + g.nextIndex());
    }
    long time = System.currentTimeMillis();
    k = 0;
    for (Iterator it = PermutationGenerator.iterator(12); it.hasNext(); k++) {
       System.out.println("" + k + ": " + ArrayString.toString(it.next()));
      //it.next();
    }
    time = System.currentTimeMillis() - time;
    System.out.println("time: " + time);
    /*
    k = 0;
    int n = Integer.parseInt(args[0]);
    long t = System.currentTimeMillis();
    for (Iterator it = PermutationGenerator.iterator(n); it.hasNext(); ) {
       //System.out.println("" + k + ": " + ArrayString.toString(it.next()));
       it.next();
    }
    t = System.currentTimeMillis() - t;
    System.out.println("It took " + t);
    */
  }

}

