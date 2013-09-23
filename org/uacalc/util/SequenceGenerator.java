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
   * on <tt>0</tt> to <tt>max - 1</tt> subject to the restriction that
   * last coordinate is at least <tt>lastMin</tt>.
   */
  private static void incrementNondecreasingSequence(int[] arg, 
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
   * This increments an array in place through all strictly increasing sequences
   * whose entries lie between 0 and <tt>max</tt>, inclusive.
   * 
   * @param a
   * @param max
   * @return
   */
  public static ArrayIncrementor increasingSequenceIncrementor(final int[] a, final int max) {
    final int len = a.length;
    final int[] a2 = new int[len];
    final ArrayIncrementor nondecInc = nondecreasingSequenceIncrementor(a2, max + 1 - len);
    return new ArrayIncrementor() {
        public boolean increment() {
          boolean v = nondecInc.increment();
          if (!v) return false;
          for (int i = 0; i <  len; i++) {
            a[i] = a2[i] + i;
          }
          return true;
        }
    };
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
   * with entries between 0 and max. This increments from the right:
   * [0,0,0], [0,0,1], ...,[maxs[0],maxs[1],maxs[2]].
   */
  public static ArrayIncrementor sequenceIncrementor(
                                            final int[] a, final int[] maxs) {
    return new ArrayIncrementor() {
        public boolean increment() {
          final int len = a.length;
          for (int i = len - 1; i >= 0; i--) {
            if (a[i] < maxs[i]) {
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
   * with entries between 0 and <code>max</code> and having at 
   * least one entry at least as large as <code>min</code>.
   * This increments from the right: * [0,0,0], [0,0,1], ...,[max,max,max]. 
   * Of course <code>min</code> should be at most <code>max</code>.
   */
  public static ArrayIncrementor sequenceIncrementor(
                          final int[] a, final int max, final int min) {
    return new ArrayIncrementor() {
        public boolean increment() {
          final int len = a.length;
          for (int i = len - 1; i >= 0; i--) {
            if (a[i] < max) {
              a[i]++;
              for (int j = i + 1; j < len; j++) {
                a[j] = 0;
              }
              boolean ok = false;
              for (int j = i; j >= 0; j--) {
                if (a[j] >= min) {
                  ok = true;
                  break;
                }
              }
              if (!ok) a[len - 1] = min;
              return true;
            }
          }
          return false;
        }
      };
  }
  
  /**
   * This just increments the array through all possible tuples
   * with entries between 0 and <code>max</code> and having at 
   * least one entry at least as large as <code>min</code>.
   * This increments from the right: * [0,0,0], [0,0,1], ...,[max,max,max]. 
   * Of course <code>min</code> should be at most <code>max</code>.
   * 
   * <code>jump</code> indicates how many times the array will be 
   * incremented by each call to increment(). This is used in 
   * parallel processing.
   * 
   */
  public static ArrayIncrementor sequenceIncrementor(
                          final int[] a, final int max, final int min, final int jump) {
    return new ArrayIncrementor() {
        public boolean increment() {
          for (int k = 0; k < jump; k++) {
            if (!incrementAux()) return false;
          }
          return true;
        }
        private boolean incrementAux() {
          final int len = a.length;
          for (int i = len - 1; i >= 0; i--) {
            if (a[i] < max) {
              a[i]++;
              for (int j = i + 1; j < len; j++) {
                a[j] = 0;
              }
              boolean ok = false;
              for (int j = i; j >= 0; j--) {
                if (a[j] >= min) {
                  ok = true;
                  break;
                }
              }
              if (!ok) a[len - 1] = min;
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
   * from the left: [0,0,0], [1,0,0], ..., [max,max,max].
   */
  public static ArrayIncrementor leftSequenceIncrementor(
                                            final int[] a, final int max) {
    return new ArrayIncrementor() {
        public boolean increment() {
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
  
  private static void setMaxs(int[] maxs, int[] roIndeces) {
    int index = 0;
    int max = 0;
    for (int i = 1; i < roIndeces.length; i++) {
      final int k = roIndeces[i] - roIndeces[i-1] - 1;
      for (int j = 0; j < k; j++) {
        maxs[index++] = max;
      }
      max++;
    }
    for (int i = index; i < maxs.length; i++) {
      maxs[i] = max;
    }
  }
  
  /**
   * This returns the initial partition on <tt>size</tt> with
   * <tt>numBlocks</tt> blocks in JB form. Should be used when using
   * {@link #partitionArrayIncrementor(int[], int) partitionArrayIncrementor}.
   * 
   * @param size
   * @param numBlocks
   * @return
   */
  public static int[] initialPartition(final int size, final int numBlocks) {
    int[] ans = new int[size];
    for (int i = 0; i < numBlocks; i++) {
      ans[i] = i;
    }
    return ans;
  }
  
  /**
   * This returns an ArrayIncrementor that increments through
   * all partitions with <tt>numBlocks</tt> blocks in JB form.
   * JB form is an array <tt>a</tt> with <tt>a[a[i]] = a[i]</tt>
   * and <tt>{@literal a[i] <= i}</tt>. The initial <tt>a</tt> must be the
   * first valid partition. It can be obtained using 
   * {@link #initialPartition(int, int) initialPartition}.
   * 
   * @param a
   * @param numBlocks
   * @return
   */
  public static ArrayIncrementor partitionArrayIncrementor(
                                     final int[] a, final int numBlocks) {
    final int size = a.length;
    final int[] rootIndeces = new int[numBlocks];
    int index = 0;
    for (int i = 0; i < size; i++) {
      if (a[i] == i)
      rootIndeces[index++] = i;
    }
    final int numNonRoots = size - numBlocks; 
    final int[] nonRootIndeces = new int[numNonRoots];
    index = 0;
    for (int i = 0; i < size; i++) {
      if (a[i] != i) nonRootIndeces[index++] = i;
    }
    final int[] maxs = new int[numNonRoots];
    setMaxs(maxs, rootIndeces);
    final int[] nonRootsRootIndeces = new int[numNonRoots];
    final ArrayIncrementor rootsinc = increasingSequenceIncrementor(rootIndeces, size - 1);
    
    return new ArrayIncrementor() {
      public boolean increment() {
        for (int i = numNonRoots - 1; i >= 0; i--) {
          if (nonRootsRootIndeces[i] < maxs[i]) {
            nonRootsRootIndeces[i]++;
            a[nonRootIndeces[i]] = rootIndeces[nonRootsRootIndeces[i]];
            for (int j = i + 1; j < numNonRoots; j++) {
              a[nonRootIndeces[j]] = 0;
              nonRootsRootIndeces[j] = 0;
            }
            return true;
          }
        }
        if (!rootsinc.increment()) return false;
        if (rootIndeces[0] != 0) return false;
        for (int i = 0; i < size; i++) {
          a[i] = 0;
        }
        for (int i = 0; i < numBlocks; i++) {
          a[rootIndeces[i]] = rootIndeces[i];
        }
        int index = 0;
        for (int i = 0; i < size; i++) {
          if (a[i] != i) nonRootIndeces[index++] = i;
        }
        for (int i = 0; i < numNonRoots; i++) {
          nonRootsRootIndeces[i] = 0;
        }
        
        System.out.println("here a: "+ ArrayString.toString(a));
        System.out.println("roots: " + ArrayString.toString(rootIndeces));
        setMaxs(maxs, rootIndeces);
        return true;    
      }
    };
  }

  /**
   * Generate the next sequence 
   * on <tt>0</tt> to <tt>max - 1</tt>.
   */
  private static void incrementSequence(int[] arg, final int max) {
    final int len = arg.length;
    for (int i = len - 1; i >= 0; i--) {
      if (arg[i] < max) {
        arg[i]++;
        break;
      }
    }
  }

  public static void main(String[] args) {
    int blocks = 2;
    int[] aa = initialPartition(5, blocks);
    System.out.println("init: " + ArrayString.toString(aa));
    ArrayIncrementor incaa = partitionArrayIncrementor(aa, blocks);
    while (incaa.increment()) {
      System.out.println(ArrayString.toString(aa));
    }
    if (true) return;
    
    
    
    int[] ax = new int[] {0,0,0};
    ArrayIncrementor incy = sequenceIncrementor(ax, 4);
    while (true) {
      if (!incy.increment()) break;
      System.out.println(ArrayString.toString(ax));
    }
    if (true) return;
    
    int[] maxs = new int[] {1,3,3,4,4};
    ArrayIncrementor incx = sequenceIncrementor(ax, maxs);
    while (true) {
      if (!incx.increment()) break;
      System.out.println(ArrayString.toString(ax));
    }
    System.out.println("");
    if (true) return;
    
    //int[] a = new int[] {0,0,0,0,0};
    //ArrayIncrementor inc = nondecreasingSequenceIncrementor(a, 3, 2);
    //ArrayIncrementor inc = nondecreasingSequenceIncrementor(a, 3, 2);
    //int[] a = new int[] {0,0,0};
    //ArrayIncrementor inc = sequenceIncrementor(a, 3, 2);
    //while (inc.increment()) {
    //  System.out.println(ArrayString.toString(a));
    //}
    //int[] a = new int[] {0,1,2};
    //ArrayIncrementor inc = increasingSequenceIncrementor(a, 4);
    int[] a = new int[] {0, 4};
    int[] a2 = new int[] {0, 5};
    ArrayIncrementor inc = sequenceIncrementor(a, 5, 4, 2);
    ArrayIncrementor inc2 = sequenceIncrementor(a2, 5, 4, 2);
    System.out.println(ArrayString.toString(a) + "  " + ArrayString.toString(a2));
    while (true) {
      if (!inc.increment()) break;
      System.out.print(ArrayString.toString(a));
      System.out.print("  ");
      if (!inc2.increment()) break;
      
      System.out.println(ArrayString.toString(a2));
    }
    System.out.println("");
  }


}