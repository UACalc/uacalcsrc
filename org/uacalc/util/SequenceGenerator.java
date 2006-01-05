/* SequenceGenerator.java (c) 2004/11/11  Ralph Freese */

package org.uacalc.util;

import java.util.*;

/**
 * This has utility static methods for sequence generation,
 * including nondecreasing sequences. It also
 * includes an in-place ArrayIncrementor.
 */
public final class SequenceGenerator {

  protected SequenceGenerator() {}

  /**
   * This increments an array in place through all nondecreasing sequences
   * whose entries lie between 0 and <tt>max</tt>, inclusive.
   */
  public static ArrayIncrementor nondecreasingSequenceIncrementor(
                                          final int[] a, final int max) {
    return nondecreasingSequenceIncrementor(a, max, 0);
  }

  /**
   * This increments an array in place through all nondecreasing sequences
   * whose entries lie between 0 and <tt>max</tt>, inclusive,
   * subject to the restrction that last coordiate is at 
   * least <tt>lastMin</tt> (useful when the first part of a list is 
   * known to be closed). 
   */
  public static ArrayIncrementor nondecreasingSequenceIncrementor(
                           final int[] a, final int max, final int lastMin) {
    return new ArrayIncrementor() {
        public boolean increment() {
          if (a[0] >= max) return false;
          incrementNondecreasingSequence(a, max, lastMin);
          return true;
        }
      };
  }

  /**
   * Generate the next nondecreasing sequence 
   * on <tt>0</tt> to <tt>max - 1</tt> subject to the restrction that
   * last coordiate is at least <tt>lastMin</tt>.
   */
  public static void incrementNondecreasingSequence(int[] arg, 
                                                    int max, int lastMin) {
    final int len = arg.length;
    for (int i = len - 1; i >= 0; i--) {
      if (arg[i] < max) {
        final int k = arg[i] + 1;
        for (int j = i; j < len; j++) {
          arg[j] = k;
        }
        if (arg[len - 1] < lastMin) arg[len - 1] = lastMin;
        break;
      }
    }
  }

  /**
   * This just increments the array through all possible tuples
   * with entries between 0 and max. This increments from the right:
   * [0,0,0], [0,0,1], ...,[max,max,max].
   */
  public static ArrayIncrementor sequenceIncrementor(
                                            final int[] a, final int max) {
    return new ArrayIncrementor() {
        public boolean increment() {
          boolean ans = false;
          final int len = a.length;
          for (int i = len - 1; i >= 0; i--) {
            if (a[i] < max) {
              a[i]++;
              for (int j = i + 1; j < len; j++) {
                a[j] = 0;
              }
              return true;
            }
          }
          return false;
        }
      };
  }

  /**
   * This just increments the array through all possible tuples
   * with entries between 0 and max from the left. This increments 
   * from the left: [0,0,0], [1,0,0], ..., [max,max].
   */
  public static ArrayIncrementor leftSequenceIncrementor(
                                            final int[] a, final int max) {
    return new ArrayIncrementor() {
        public boolean increment() {
          boolean ans = false;
          final int len = a.length;
          for (int i = 0; i < len; i++) {
            if (a[i] < max) {
              a[i]++;
              for (int j = i - 1; j >= 0; j--) {
                a[j] = 0;
              }
              return true;
            }
          }
          return false;
        }
      };
  }

  /**
   * Generate the next sequence 
   * on <tt>0</tt> to <tt>max - 1</tt>.
   */
  public static void incrementSequence(int[] arg, final int max) {
    final int len = arg.length;
    for (int i = len - 1; i >= 0; i--) {
      if (arg[i] < max) {
        arg[i]++;
        break;
      }
    }
  }

  public static void main(String[] args) {
    int[] a = new int[] {0,0,0,0,0};
    ArrayIncrementor inc = nondecreasingSequenceIncrementor(a, 3, 2);
    while (inc.increment()) {
      System.out.println(ArrayString.toString(a));
    }
  }


}
