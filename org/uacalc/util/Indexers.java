package org.uacalc.util;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.uacalc.util.*;

/**
 * Since parallel algorithms in Java 8 work best if they are stateless,
 * this class gives static stateless methods to do things like streaming
 * all int arrays of length r, whose entries lie between 0 and n - 1. This 
 * could be then used to create a stream of all r-tuples of elements from
 * a list. 
 * <p>
 * Another example is making a stream given all int arrays of length r
 * as above but such that at least one entry is at least m, for a 
 * given m < n. The algorithm for this, which is explained with the 
 * method, is a bit tricky. 
 * <p>
 * An <b>indexer</b> for a set S of objects is a stateless method that
 * given 0 <= k < |S|, return the kth element of S, under some ordering
 * of S. Of course we do not want to have to compute S. The goal is to 
 * help in making parallel streams for S.
 * <p>
 * Other examples include the set of all subsets of a set.
 * Of an indexer would depend on the ordering. There are several
 * interesting orders.  
 * <p>
 * 
 * 
 * 
 * @author ralph
 *
 */
public class Indexers {

  /**
   * This is an indexer into the set of all int arrays of length
   * arity whose entries lie between 0 and size - 1 and such that
   * at least one entry great than or equal to min.
   * 
   * The idea of the algorithm is that if S is the set of array
   * described above, then S is the disjoint union of S_i, for 
   * 0 <= i < arity, where S_i is the set of arrays where the first 
   * i entries lie between 0 and min - 1, the next entry lies
   * between min and size - 1, and the remaining entries lie 
   * between 0 and size - 1, all inclusive. Note
   * S_i has m^i * (n - m) * n^(r-i-1) elements, where m = min,
   * r = arity and n = size.
   * 
   * 
   * @param k
   * @param arity
   * @param size
   * @param min
   * @return
   */
  public static int[] arrayIndexerWithMin(final long k, final int arity, final int size, final int min) {
    if (arity == 0) return new int[0];;
    final int diff = size - min;  // this should be positive
    int stage = 0;
    long sum = 0;
    long summand = diff;
    for (int i = 1; i < arity; i++) {
      summand = summand * size;
    }
    while (k >= sum + summand) {
      stage++;
      sum = sum + summand;
      summand = summand * min / size;
      //System.out.println("stage = " + stage + ", summand = " + summand + ", sum = " + sum);
    }
    //System.out.println("stage = " + stage + ", summand = " + summand + ", sum = " + sum);
    
    return arrayIndexerWithMinAux(k - sum, arity, size, min, stage, diff);
  }
  
  private static int[] arrayIndexerWithMinAux(long k, int arity, int size, int min, int stage, int diff) {
    final int[] ans = new int[arity];
    for (int i = 0; i < stage; i++) {
      ans[i] = (int)(k % min);
      k = k / min;
    }
    ans[stage] = min + (int)(k % diff);
    k = k / diff;
    for (int i = stage + 1; i < arity; i++) {
      ans[i] = (int)(k % size);
      k = k / size;
    }
    return ans;
  }
  
  public static void main(String[] args) {
    for (int i = 0; i < 56; i++) {
      int[] arr = arrayIndexerWithMin(i, 3, 4, 2);
      System.out.println("arr: " + Arrays.toString(arr));
    }
    
    long size = 1000;
    long min = 800;
    int arity = 3;
    long range = size*size*size - min*min*min;
    System.out.println("range: " + range);
    Stream<int[]> stream = LongStream.range(0, range).mapToObj(i -> arrayIndexerWithMin(i, arity, (int)size, (int)min));
    stream.parallel();
    long time = System.currentTimeMillis();
    long count = stream.count();
    //stream.forEach(a -> System.out.println(Arrays.toString(a)));
    System.out.println("parallel time: " + (System.currentTimeMillis() - time));
    System.out.println("count = " + count);
    
    int[] arr = new int[]{0,0,0};
    // using an AtomicLong since accumulation of the result needs to be synchronized.
    // This makes it about 4 times slower but without this the comparison is unfair.
    AtomicLong  c = new AtomicLong(0);  
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(arr, (int)size - 1, (int)min);
    time = System.currentTimeMillis();
    while(inc.increment()) {
      c.getAndIncrement();
    }
    System.out.println("increment time: " + (System.currentTimeMillis() - time));
    System.out.println("count = " + c.get());
    
  }

}
