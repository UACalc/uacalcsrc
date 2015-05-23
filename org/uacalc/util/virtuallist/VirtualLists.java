package org.uacalc.util.virtuallist;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.*;

import org.uacalc.alg.op.Operation;
import org.uacalc.util.*;
import org.uacalc.util.virtuallist.*;

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
public class VirtualLists {
  
  /*
  public static LongList<List<E>> tuplesVirtualList(int tupleLen, List<E> lst) {
    final LongList<int[]> intTuples = intTuples(tupleLen, lst.size());
    
    return new LongList<List<E>>() {
      
      public long size() { return intTuples.size(); }
      
      public List<E> get(long k) {
        final int[] indeces = intTuples.get(k);
        List<E> ans = new ArrayList<>(tupleLen);
        for (int i = 0; i < tupleLen; i++) {
          ans.set(i, lst.get(indeces[i]));
        }
        return ans;
      }
    }; 
  }
  */
  
  
  public static LongList<int[]> intTuples(int tupleLen, int base) { 
    final BigInteger base0 = BigInteger.valueOf(base);
    final BigInteger pow = base0.pow(tupleLen);
    if (pow.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
      throw new IllegalArgumentException(base + "^" + tupleLen + " = " + pow + " is too big to be a long.");
    }
    final long size = pow.longValue();
    
    return new LongList<int[]>() {
      
      public long size() { return size; }
      
      public int[] get(long k) {
        final int[] ans = new int[tupleLen];
        for (int i = 0; i < tupleLen; i++) {
          ans[i] = (int)(k % base);
          k = k / base;
        }
        return ans;
      }
    }; 
  }
  
  public static LongList<int[]> intTuplesWithMin(int tupleLen, int base, int min) { 
    final BigInteger base0 = BigInteger.valueOf(base);
    final BigInteger min0 = BigInteger.valueOf(min);
    final BigInteger size0 = base0.pow(tupleLen).subtract(min0.pow(tupleLen));
    if (size0.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
      throw new IllegalArgumentException(base + "^" + tupleLen + " = " + size0 + " is too big to be a long.");
    }
    final long size = size0.longValue();
    int diff = base - min;
    long[] partialSums = new long[tupleLen];
    long summand = diff;
    for (int i = 1; i < tupleLen; i++) {
      summand = summand * min;
    }
    partialSums[0] = summand;
    for (int i = 1; i < tupleLen; i++) {
      summand = (summand * base) / min;
      partialSums[i] = partialSums[i-1] + summand;
    }
    return new LongList<int[]>() {
      
      public long size() { return size; }
      
      public int[] get(long k) {
        
        int stage = 0;
        while (k >= partialSums[stage]) stage++;
        if (stage > 0) k = k - partialSums[stage - 1];
        
        final int[] ans = new int[tupleLen];
        for (int i = 0; i < stage; i++) {
          ans[i] = (int)(k % base);
          k = k / base;
        }
        ans[stage] = min + (int)(k % diff);
        k = k / diff;
        for (int i = stage + 1; i < tupleLen; i++) {
          ans[i] = (int)(k % min);
          k = k / min;
        }
        return ans;
        
      }
    }; 
  }
  

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
   * @param base
   * @param min
   * @return
   */
  public static int[] arrayIndexerWithMin(final long k, final int arity, final int base, final int min) {
    if (arity == 0) return new int[0];;
    final int diff = base - min;  // this should be positive
    int stage = 0;
    long sum = 0;
    long summand = diff;
    for (int i = 1; i < arity; i++) {
      summand = summand * base;
    }
    while (k >= sum + summand) {
      stage++;
      sum = sum + summand;
      summand = summand * min / base;
      //System.out.println("stage = " + stage + ", summand = " + summand + ", sum = " + sum);
    }
    //System.out.println("stage = " + stage + ", summand = " + summand + ", sum = " + sum);
    
    return arrayIndexerWithMinAux(k - sum, arity, base, min, stage, diff);
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
  
  /**
   * Important note: Math.pow(6*k, (double)1 / (double)3) gave 5.9999999 so
   * gave the wrong answer. But StrictMath.pow(6*k, (double)1 / (double)3) 
   * gave 6, the correct answer.
   * 
   * @param k
   */
  public static void testPow(long k) {
    double foo = StrictMath.pow(6*k, (double)1 / (double)3);
    double floor = Math.floor(foo);
    double ceil = Math.ceil(foo);
    System.out.println("k = " + k + ", foo = " + foo + ", floor = " + floor);
    //System.out.println("foo: " + foo);
    //System.out.println("floor: " + floor);
    //System.out.println("ceil: " + ceil);
    //System.out.println("floor choose 3: " + floor*(floor - 1)*(floor + 1)/6);
    //System.out.println("k : " + k);
    //System.out.println("ceil choose 3: " + ceil*(ceil - 1)*(ceil + 1)/6);
  }
  
  private static long factorial(int arg) {
    if (arg < 2) return 1L;
    return arg * factorial(arg - 1);
  }
  /**
   * We are writing this assuming r is small
   * compared to n, but this is not required
   * for it to be correct.
   * 
   * @param n
   * @param r
   * @return  n choose r
   */
  private static long binomial(int n, int r) {
    long prod = 1;
    for (int i = 0; i < r; i++) {
      prod = prod * (n - i);
    }
    return prod / factorial(r);
  }
  
  public static int foo(long k, int r) {
    double rDouble = (double)r;
    double oneOverR = 1/rDouble;
    double firstTry = StrictMath.pow(factorial(r) * (k), oneOverR);
    //System.out.println("raw: " + StrictMath.pow(factorial(r) * k, oneOverR));
    //double extra = StrictMath.pow((double)1 / (double)(2*3*3*3*3*3*k), oneOverR);
    //extra = 0;
    //return (int)StrictMath.floor(StrictMath.pow(factorial(r) * (k+1), oneOverR));
    return (int)StrictMath.floor(firstTry + rDouble * (rDouble - 1) / (24 * firstTry) - (rDouble - 1)/2 );
  }
  
  public static int bar(long k, int r) {
    double oneOverR = (double)1/(double)r;
    return (int)StrictMath.floor(StrictMath.pow(factorial(r) * (k), oneOverR) - (double)(r-1)/2);
  }
  
  public static int baz(long k, int r) {
    double oneOverR = (double)1/(double)r;
    int upper = (int)StrictMath.floor(StrictMath.pow(factorial(r) * (k), oneOverR));
    int lower = upper - r;
    int t = (int)StrictMath.floor(upper - r/2);
    System.out.println("r = " + r + ", k = " + k + ", t = " + t + ", bin(t+r-1,r) = " + binomial(t+r-1,r)
        + ", bin(t+r,r) = " + binomial(t+r,r)
        );
    
    return t;
  }
  
  public static void main(String[] args) {
    final int r = 5;
    
    for(int k = 8000 ; k < 9000; k++) {
      baz(k,r);
      //int t = foo(k,r);
      //int t = foo(k,r);
      //long k0 = binomial(t+r-1, r);
      //long k1 = binomial(t+r, r);
      //System.out.println("foo(" + k + "," + r + ") = " + t + ", " + k0 + ", " + k + ", " + k1);
      //if (k < k0 || k >= k1) {
      //if (k < k0) {
        //System.out.println("foo(" + k + "," + r + ") = " + t + ", " + k0 + ", " + k + ", " + k1);
        //System.out.println("failed at k = " + k);
        //return;
      //}
      //if (k > k1) return;
    }
    //testPow(1_000_000_000);
    if (true) return;
    LongList<int[]> llist = intTuples(3, 4);
    llist.stream().forEach(x -> System.out.println(Arrays.toString(x)));
    System.out.println("==========");
    llist = intTuplesWithMin(3, 4, 2);
    llist.stream().forEach(x -> System.out.println(Arrays.toString(x)));
    llist = intTuplesWithMin(3, 1000, 800);
    long time = System.currentTimeMillis();
    long count = llist.parallelStream().count();
    System.out.println("count: " + count + ", time: " + (System.currentTimeMillis() - time));
    
    if (true) return;
    
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
    time = System.currentTimeMillis();
    count = stream.count();
    //stream.forEach(a -> System.out.println(Arrays.toString(a)));
    System.out.println("parallel time: " + (System.currentTimeMillis() - time));
    System.out.println("count = " + count);
    LongList<int[]> ind = new TupleWithMin(3, 1000, 800);
    stream = LongStream.range(0, range).mapToObj(i -> ind.get(i));
    time = System.currentTimeMillis();
    count = stream.parallel().count();
    System.out.println(" time: " + (System.currentTimeMillis() - time));
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
    System.out.println("New Version");
    LongList tuples = new TupleWithMin(3, 1000, 800);
    time = System.currentTimeMillis();
    count = tuples.parallelStream().count();
    System.out.println(" time: " + (System.currentTimeMillis() - time));
    System.out.println("count = " + count);
  }

}


