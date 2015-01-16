package org.uacalc.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.*;
import org.uacalc.util.*;

/**
 * Despite the name this is just some static methods to get streams
 * of tuples. To get a stream of all (fixed length) tuples, use the stream
 * intTupleStream as the indices.
 * 
 * @author ralph
 *
 */
public class TupleStream {

  public static Stream<int[]> intTupleStream (int size, int length) {
    long range = pow(size, length);
    Stream<int[]> stream = LongStream.range(0, range).mapToObj(i -> hornerInvLong(i, size, length));
    //System.out.println("is parallel: " + stream.isParallel());
    //stream.parallel();
    //System.out.println("is parallel: " + stream.isParallel());
    return stream;
  }
  
  public static Stream<int[]> intTupleStream(int size, int length, int min) {
    return intTupleStream(size, length).filter(arr -> maxOfArray(arr) >= min);
  }
  
  /**
   * The largest entry of an array.
   * 
   * @param arr an array of length at least 1.
   * @return
   */
  public static int maxOfArray(int[] arr) {
    int ans = arr[0];
    for (int i = 1; i < arr.length; i++) {
      if (arr[i] > ans) ans = arr[i]; 
    }
    return ans;
  }
  
  /**
   * Returns the int array corresponding to this Horner encoding.
   * This is for the args of an operation. It is assume len (the arity)
   * is small but size could be big so size raised to len could be
   * a long. Also we reverse the order (from the order used in 
   * direct products) so the stream gives [0,0,0], [0,0,1], [0,0,2], ... .
   *
   * @param k      the Horner encoding of the tuple.
   * @param size   the size of the list. 
   * @param len    the length of the vector (the arity). 
   */
  public static final int[] hornerInvLong(long k, int size, int len) {
    final int n = len;
    final int[] ans = new int[n];
    if (n == 0) return ans;
    for (int i = 0; i < n - 1; i++) {
      ans[n-i-1] = (int)(k % size);
      k = (k - ans[i]) / size;
    }
    ans[0] = (int)k;
    return ans;
  }
  
  public static long pow(int base, int exponent) {
    long ans = 1;
    while (exponent > 0) {
      ans = ans * base;
      exponent--;
    }
    return ans;
  }

  public static void main(String[] args) {
    int size = 10000;
    int len = 2;
    int min = 8000;
    for (int i = 0; i < 3; i++) {
      Stream<int[]> stream = intTupleStream(size, len, min);
      //stream.forEach(a -> System.out.println(Arrays.toString(a)));
      long time = System.currentTimeMillis();
      long count = 0; 
      count = stream.count();
      System.out.println("serial time: " + (System.currentTimeMillis() - time));
      System.out.println("count = " + count);
      System.out.println("---------");
      
      stream = intTupleStream(size, len, min);
      stream.parallel();
      time = System.currentTimeMillis();
      count = stream.count();
      //stream.forEach(a -> System.out.println(Arrays.toString(a)));
      System.out.println("parallel time: " + (System.currentTimeMillis() - time));
      System.out.println("count = " + count);
      //stream.forEach(a -> System.out.println(Arrays.toString(a)));
      System.out.println("---------");
      
      int[] arr = new int[]{0,0};
      // using an AtomicLong since accumulation of the result needs to be synchronized.
      // This makes it about 4 times slower but without this the comparison is unfair.
      AtomicLong  c = new AtomicLong(1);  
      ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(arr, size - 1);
      time = System.currentTimeMillis();
      while(inc.increment()) {
        c.getAndIncrement();
      }
      System.out.println("increment time: " + (System.currentTimeMillis() - time));
      System.out.println("count = " + c.get());
      
      System.out.println("=========");
    }
    System.out.println(pow(size, 2) - pow(min,2));
    Stream<int[]> str = intTupleStream(5,2,3);
    str.forEach(a -> System.out.println(Arrays.toString(a)));
  }
  
}
