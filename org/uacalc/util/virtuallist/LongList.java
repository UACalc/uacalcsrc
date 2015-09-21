package org.uacalc.util.virtuallist;

import java.math.BigInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.*;

/**
 * An interface for lists indexed by <code>long</code>'s rather 
 * than <code>int</code>'s. These have no backing structure; so 
 * they are virtual lists. They are immutable so they only
 * need a <code>get</code> and <code>size</code> method. 
 * Default implementations of <coed>stream</code> and
 * <code>parallelStream</code> are given.
 * 
 * <p>
 * An example would be all triples of elements of a list with
 * elements of type E. A triple could be and array <code>E[]</code>
 * or a list of length 3. 
 * If the list had <code>n</code> elements
 * then the LongList would have size <code>n^3</code>. 
 * The main subtlety is defining <code>get</code> which defines a 
 * function from long's less than <code>n^3</code> to triples and
 * should be stateless, or at least thread safe, so that 
 * it behaves well with a parallel stream.
 * 
 * @author ralph
 *
 * @param <E>
 */
public interface LongList<E> extends RandomAccess {

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
   * see <a href="http://uacalc.org/putthepaperlinkhere">Paper name</a>.
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
  
  /**
   * Form a LongList of int[]'s representing all subsets of size 
   * <code>subsetSize</code> from the set of nonnegative integers
   * less that setSize.  
   * 
   * 
   * @param subsetSize
   * @param setSize
   * @return
   */
  public static LongList<int[]> fixedSizedSubsets(int subsetSize, int setSize) { 
    if (0 > subsetSize || subsetSize > setSize) {
      throw new IllegalArgumentException("The arguments need to be nonnegative and subsetSize <= base.");
    }
    BigInteger base0 = BigInteger.valueOf(setSize);
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
      
      

      /**
       * Given  k  and  r  this finds  t  with binomial(t,r) <= k < binomial(t+1,r);
       * it sets arr[r-1] = t and returns the difference k - binomial(t,r).
       * Note (r!*k)^(1/r) <= t <= (r!*k)^(1/r) + k.
       * So our first guess is floor((r!*k)^(1/r) + r/2).
       * 
       * @param k
       * @param r
       * @param arr
       * @return
       */
      private long setLastEntry(long k, int r, int[] arr) {
        final double oneOverR = (double)1/(double)r;
        int guess = (int)StrictMath.floor(StrictMath.pow(factorial(r) * k, oneOverR) + r / 2);
        
        int guessOrig = guess;  // fir debugging
        
        //long binom = binomial(guess, r);
        if (k == 0) guess = r - 1;
        boolean changed = false;
        //guess = Math.max(guess, r - 1);  // do we need this?
        for ( ; guess >= 0; guess--) {
          if (binomial(guess, r) <= k) break;
          //binom = binom * (guess - r) / guess;
          changed = true;
        }
        if (!changed) {
          for ( ; ; guess++) {
            //binom = binom * (guess + 1) / (guess - r + 1); // binom = binomial(guess + 1, r)
            if (k < binomial(guess + 1, r)) break;
          }
        }
        arr[r-1] = guess;
        
        //if (guess != guessOrig)
        //System.out.println("k = " + k + ", r = " + r + ", t = " + guess + ", orig guess = " + guessOrig
        //    + ", binom(t,r) = " + binomial(guess,r) + ", binom(t+1,r) = " + binomial(guess+1,r));
        
        //return k - binom; 
        return k - binomial(guess, r);
      }
      
      
      public int[] get(long k) {
        final int[] ans = new int[subsetSize];
        long leftOver = k;
        for (int r = subsetSize; r > 0; r--) {
          leftOver = setLastEntry(leftOver, r, ans);
        }
        return ans;  
      }
    }; 

  }

  /**
   * A LongList of all subsets of the set of int 0 to setSize - 1,
   * represented as increasing int arrays. This a bit foolish as
   * the binary representation of a long, which is its internal representation,
   * already gives the set.
   * 
   * @param setSize
   * @return
   */
  public static LongList<int[]> subsets(int setSize) {
    if (setSize >= 63) {
      throw new IllegalArgumentException("There are too many subsets to be a long. setSize should be at most 63.");
    }
    final long size = pow2(setSize);
    
    return new LongList<int[]>() {

      public long size() { return size; }
      
      public int[] get(long k) {
        if (k == 0) return new int[0];
        List<Integer> lst = new ArrayList<Integer>();
        while (k > 0) {
          int t = log2(k);
          lst.add(t);
          k = k - pow2(t);
        }
        final int len = lst.size();
        final int len1 = len - 1;
        int[] ans = new int[len];
        for (int i = 0; i < len; i++) {
          ans[i] = lst.get(len1 - i);
        }
        return ans;
      }
    }; 

  }
  
  public static LongList<int[]> permutations(int n) {
    if (n > 20) {
      throw new IllegalArgumentException("There are too many permutations to be a long. n should be at most 20.");
    }
    final long size = factorial(n);
    
    // a table of r! up to 21. This only makes
    // it run about 10% faster (on counting all permutations on 12 letters)
    final long[] factorials = new long[21];
    for (int i = 0; i < 21; i++) {
      factorials[i] = factorial(i);
    }
    
    return new LongList<int[]>() {

      public long size() { return size; }
    
      private long setEntry(int index, long k, int[] arr, List<Integer> lst) {
        int m = lst.size();
        long mFac = factorials[m-1];//fac(m-1);
        int r = 0;
        //while (!(r * mFac <= k && k < (r + 1) * mFac)) r++;
        while (!(k < (r + 1) * mFac)) r++;
        arr[index] = lst.get(r);
        lst.remove(r);
        return k - r * mFac;
      }
      
      public int[] get(long k) {
        final List<Integer> lst = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
          //System.out.println("n = " + n  + ", i = " + i);
          lst.add(i);
        }
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
          k = setEntry(i, k, ans, lst);
        }
        return ans;
      }
    }; 

  }
  
  

  
  
  public static long factorial(int arg) {
    if (arg < 2) return 1L;
    long ans = 1L;
    for (long i = arg; i > 1; i--) {  // can I make i an int ?
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
    if (r > n) return 0L;
    if (n - r < r) r = n - r;
    long top = 1;
    long bot = 1;
    for (int i = 0; i < r; i++) {
      top = top * (n - i);
      bot = bot * (i+1);
    }
    return top / bot;
  }
  
  /**
   * The largest int less equal to log base 2 of k. See
   * http://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers.
   * 
   * @param k
   * @return
   */
  public static int log2(long k) {
    if (k <= 0) throw new IllegalArgumentException("k must be positive.");
    return 63 - Long.numberOfLeadingZeros(k);  
  }
  
  public static long pow2(int r) {
    long ans = 1L;
    for (int i = 0; i < r; i++) {
      ans = ans * 2L;
    }
    return ans;
  }
  
  public static void main(String[] args) {
    LongList<int[]> llist = intTuplesWithMin(3, 4, 2);
    llist.stream().forEach(x -> System.out.println(Arrays.toString(x)));
    System.out.println("------------------------------");
    llist.parallelStream().forEach(x -> System.out.println(Arrays.toString(x)));
    System.out.println("------------------------------");
    LongList<int[]> subsets = fixedSizedSubsets(5, 6000);
    long k = 10_000_000_000_000L;
    int[] ans = subsets.get(k);
    System.out.println(k  + " <--> " + Arrays.toString(ans));
    
    long start = 700000000L;
    //for (long i = 0L; i < 100L; i++) {
    //  subsets.get(start + i);
    //}
    //subsets.get(100000);
    //subsets.get(300000);
    //subsets.get(800000);
    System.out.println("------------------------------");
    LongList<int[]> subsets2 = fixedSizedSubsets(3, 6);
    subsets2.stream().forEach(x -> System.out.println(Arrays.toString(x)));
    
    System.out.println("------------------------------");
    LongList<int[]> subsets3 = subsets(6);
    subsets3.stream().forEach(x -> System.out.println(Arrays.toString(x)));
    
    System.out.println("------------------------------");
    LongList<int[]> subsets4 = permutations(4);
    subsets4.stream().forEach(x -> System.out.println(Arrays.toString(x)));
    subsets4 = permutations(12);
    long time = System.currentTimeMillis();
    long count = subsets4.parallelStream().count();
    System.out.println("count: " + count + ", time: " + (System.currentTimeMillis() - time));
    
    
    
    //for (int i = 1; i < 17; i++){
    //  System.out.println("log2(" + i + ") = " + log2(i));
    //}
    
    //for (long k = 0; k < subsets.size(); k++) {
    //  System.out.println("" + k + "  " + Arrays.toString(subsets.get(k)));
    //}
    //long time = System.currentTimeMillis();
    //long count = subsets.parallelStream().count();
    //System.out.println("count: " + count + ", time: " + (System.currentTimeMillis() - time));
    // 3-element subsets of 600 elements took 5680 ms
  }
  
}

