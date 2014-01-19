package org.uacalc.alg.parallel;

import java.util.concurrent.*;
import java.util.*;

import org.uacalc.util.*;
import org.uacalc.alg.op.*;


// this version, based on 
//    oracle.com/technetwork/articles/java/fork-join-422606.html
// just did each task serially. Don't know why.
// Figured it out: it was call join on each task in the *opposite* order
// of the forks. Also did one with a direct call to compute as 
// suggested by Grossman.
// The times for this just about equal the times for ParallelClose2.

// compile (use java 7) with 
//
// % javac ParallelClose3.java
//
// and run with
//
// % java ParallelClose3 k
//
// for k processes. k is optional, defaulting to 4.

// Some good ideas at
// http://homes.cs.washington.edu/~djg/teachingMaterials/spac/grossmanSPAC_forkJoinFramework.html

//               Times for ParallelClose2
// 
// timing with a = b = 1: 2 threads was the fastest, a liitle over 10 secs
//                          and 1 thread was over 19. 4 threads was slightly
//                          slower than 1
//
// with a = 43, b = 571: with 1  thread  64.8 seconds,
//                       with 2  threads 32.5 seconds,
//                       with 4  threads 20.6 seconds,
//                       with 8  threads 19.1 seconds,
//                       with 16 threads 18.3 seconds,
//                       with 32 threads 23.5 seconds,
//                       with 64 threads 34.6 seconds,



/**
 * This will will do one pass partial closure with a single 
 * Operation using a parallel algorithm. Note the term map
 * map e different on different runs with the same data.  
 * 
 * 
 * @author ralph
 *
 */
public class SingleClose extends RecursiveTask<Map<IntArray,Integer>> {
  
  final int increment;  // this is also the number of processes that will be used
  // it will also serve as id
  final Map<IntArray,Integer> map;
  final Operation op;
  //final int closedMark;
  final int min;
  final int max;
  final List<int[]> arrays;
  
  public SingleClose(Map<IntArray,Integer> map, Operation op, int min) {
    this.map = map;
    this.op = op;
    this.min = min;
    this.max = map.size() - 1;
    this.increment = calculateInc();
    this.arrays = new ArrayList<>(increment);
    setArrays();
  }
  
  public SingleClose(int inc, Map<IntArray,Integer> map, Operation op, int min) {
    this.increment = inc;
    this.map = map;
    this.op = op;
    this.min = min;
    this.max = map.size() - 1;
    this.arrays = new ArrayList<>(increment);
    setArrays();
  }
  
  private int calculateInc() {
    return 4;
  }
  
  private void setArrays() {
    final int k = op.arity();
    int[] a = new int[k];
    a[k-1] = min;
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(a, max, min, increment);
    for (int i = 0; i < increment; i++) {
      final int[] b = Arrays.copyOf(a, a.length);
      arrays.set(i, b);
      inc.increment();
    }
  }
  
  
  @Override
  protected Map<IntArray,Integer> compute() {
    return null;
  }
  

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    
  }

}
