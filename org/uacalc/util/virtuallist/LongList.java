package org.uacalc.util.virtuallist;

import java.math.BigInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An interface for lists indexed by <code>long</code>'s rather 
 * than <code>int</code>'s. These have no backing structure; so 
 * they are virtual lists. They are immutable so they only
 * need a <code>get</code> and <code>size</code> method. 
 * 
 *  and
 * methods returning a stream (or a parallel stream). These 
 * have default implementations. 
 * <p>
 * An example would be all triples of elements of a list with
 * elements of type E. A triple could be and array <code>E[]</code>
 * or a list of length 3. 
 * If the list had <code>n</code> elements
 * then the LongList would have size <code>n^3</code>. 
 * The main subtlety is defining <code>get</code> which defines a 
 * function from long's less than <code>n^3</code> to triples and
 * should be stateless so that it behaves well with a parallel
 * stream.
 * 
 * @author ralph
 *
 * @param <E>
 */
public interface LongList<E> {

  /**
   * Get the kth element.
   * 
   * @param k   the index
   * @return
   */
  public E get(long k);
  
  public long size();
  
  default public Stream<E> stream() {
    return LongStream.range(0, size()).mapToObj(i -> get(i));
  }
  
  default public Stream<E> parallelStream() {
    return LongStream.range(0, size()).parallel().mapToObj(i -> get(i));
  }
  
  /**
   * A LongList of int arrays of length 
   * <code>tupleLength</code> with entries between 0
   * and <code>base</code> - 1, inclusive. 
   * The kth entry is k written in base <code>base</code>.
   * 
   * @param tupleLength
   * @param base
   * @return
   */
  public static LongList<int[]> intTuples(int tupleLength, int base) { 
    if (tupleLength < 0 || base < 0) {
      throw new IllegalArgumentException("The arguments need to be nonnegative.");
    }
    final BigInteger base0 = BigInteger.valueOf(base);
    final BigInteger pow = base0.pow(tupleLength);
    if (pow.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
      throw new IllegalArgumentException(base + "^" + tupleLength + " = " + pow + " is too big to be a long.");
    }
    final long size = pow.longValue();
    
    return new LongList<int[]>() {
      
      public long size() { return size; }
      
      public int[] get(long k) {
        final int[] ans = new int[tupleLength];
        for (int i = 0; i < tupleLength; i++) {
          ans[i] = (int)(k % base);
          k = k / base;
        }
        return ans;
      }
    }; 
  }
  
  /**
   * A LongList of int arrays of length 
   * <code>tupleLength</code> with entries between 0
   * and <code>base</code> - 1, inclusive, and having at
   * least one entry in the range <code>min</code> to
   * <code>base</code> - 1.
   * For the definition and calculation of the kth enty 
   * see <a href="http://uacalc.org/putthepaperlinkhere">Paper name</>.
   * 
   * 
   * @param tupleLength
   * @param base
   * @param min
   * @return
   */
  public static LongList<int[]> intTuplesWithMin(int tupleLength, int base, int min) { 
    if (tupleLength < 0 || base < 0 || min < 0 || base <= min) {
      throw new IllegalArgumentException("The arguments need to be nonnegative and min < base.");
    }
    final BigInteger base0 = BigInteger.valueOf(base);
    final BigInteger min0 = BigInteger.valueOf(min);
    final BigInteger size0 = base0.pow(tupleLength).subtract(min0.pow(tupleLength));
    if (size0.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
      throw new IllegalArgumentException(base + "^" + tupleLength + " = " + size0 + " is too big to be a long.");
    }
    final long size = size0.longValue();
    int diff = base - min;
    long[] partialSums = new long[tupleLength];
    long summand = diff;
    for (int i = 1; i < tupleLength; i++) {
      summand = summand * min;
    }
    partialSums[0] = summand;
    for (int i = 1; i < tupleLength; i++) {
      summand = (summand * base) / min;
      partialSums[i] = partialSums[i-1] + summand;
    }
    return new LongList<int[]>() {
      
      public long size() { return size; }
      
      public int[] get(long k) {
        
        int stage = 0;
        while (k >= partialSums[stage]) stage++;
        if (stage > 0) k = k - partialSums[stage - 1];
        
        final int[] ans = new int[tupleLength];
        for (int i = 0; i < stage; i++) {
          ans[i] = (int)(k % base);
          k = k / base;
        }
        ans[stage] = min + (int)(k % diff);
        k = k / diff;
        for (int i = stage + 1; i < tupleLength; i++) {
          ans[i] = (int)(k % min);
          k = k / min;
        }
        return ans;
        
      }
    }; 
  }

  public static LongList<int[]> fixedSizedSubsets(int subsetSize, int base) { 
    if (0 > subsetSize || subsetSize < base) {
      throw new IllegalArgumentException("The arguments need to be nonnegative and subsetSize <= base.");
    }
    BigInteger base0 = BigInteger.valueOf(base);
    BigInteger size0 = BigInteger.valueOf(subsetSize);
    BigInteger top = BigInteger.ONE;
    BigInteger bot = BigInteger.ONE;
    for (int i = 0; i < subsetSize; i++) {
      top = top.multiply(base0);
      bot = bot.multiply(size0);
      base0 = base0.subtract(BigInteger.ONE);
      size0 = size0.subtract(BigInteger.ONE);
    }
    if (top.divide(bot).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
      throw new IllegalArgumentException("There are too many subsets to be a long.");
    }
    long size = top.divide(bot).longValue();

    return new LongList<int[]>() {

      public long size() { return size; }

      // Find t with binom(t,r) <= k < binom(t+1,r).
      // Note (r!*k)^(1/r) <= t <= (r!*k)^(1/r) + k.
      // So our first guess is floor((r!*k)^(1/r) + k/2)/
      private int FindT(int k, int r) {
        double oneOverR = (double)1/(double)r;
        int guess = (int)StrictMath.floor(StrictMath.pow(factorial(r) * k, oneOverR) + r/2);
        // here 
        // TODO: finish this
        


        return 0;
      }

      public int[] get(long k) {
        final int[] ans = new int[subsetSize];


        return ans;  
      }
    }; 

  }

  public static long factorial(int arg) {
    if (arg < 2) return 1L;
    long ans = 1L;
    for (int i = arg; i > 1; i--){
      ans = ans*i;
    }
    return ans;
  }
  
  
  /**
   * The number of subsets of size r from a set of size n.
   * 
   * @param n
   * @param r
   * @return  n choose r
   */
  public static long binomial(int n, int r) {
    if (r > n) return 0;
    if (n - r < r) r = n - r;
    long top = 1;
    long bot = 1;
    for (int i = 0; i < r; i++) {
      top = top * (n - i);
      bot = bot * (i+1);
    }
    return top / bot;
  }
  
}

