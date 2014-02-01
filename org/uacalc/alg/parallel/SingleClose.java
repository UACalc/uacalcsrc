package org.uacalc.alg.parallel;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

import org.uacalc.util.*;
import org.uacalc.alg.op.*;
import org.uacalc.terms.*;
import org.uacalc.ui.tm.*;
import org.uacalc.alg.*;

// Notes:
// The map below should be either a ConcurrentHashMap or a
// ConcurrentSkipListMap. The latter uses the natural order
// of the keys, IntArray in our case, for lookups, and so
// may be faster. 
//
// In either case we should use putIfAbsent(key, value). This will 
// return null if the entry is new. 
//
// The univList should have a Collections.unmodifiable wrapper.
//
// The individual threads should all modify the map but keep the
// new elements found on a private list that should be returned
// and the fork part should concatenate these list of new elements
// serially.
//
// Note the univList may have already started this pass with another
// operation and so may be larger than the "max" in the incrementor
// but that doesn't matter: we don't have to worry about that.
//

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

@SuppressWarnings("serial")
class SingleCloseSerial extends RecursiveTask<List<IntArray>> {
  
  final ConcurrentMap<IntArray,Term> map;
  final List<IntArray> univList;
  final Operation op;
  
  final ProgressReport report;
  final CloserTiming timing;
  final Thread callingThread;
  
  final int[] argIndeces;
  
  final AtomicInteger eltsFound;
  
  /**
   * An incrementor associated with argIndeces.
   */
  final ArrayIncrementor incrementor;
  
  final int arity;
  
  final List<IntArray> newElts = new ArrayList<>();
  
  SingleCloseSerial(List<IntArray> univList, ConcurrentMap<IntArray,Term> map, 
      Operation op, int[] argIndeces, ArrayIncrementor incrementor) {
    this(univList, map, op, argIndeces, incrementor, null, null, null, null);
  }
  
  SingleCloseSerial(List<IntArray> univList, ConcurrentMap<IntArray,Term> map, 
                    Operation op, int[] argIndeces, ArrayIncrementor incrementor, 
                    ProgressReport report, CloserTiming timing, AtomicInteger eltsFound,
                    Thread callingThread) {
    this.univList = univList;
    this.map = map;
    this.op = op;
    this.argIndeces = argIndeces;
    this.incrementor = incrementor;
    this.arity = op.arity();
    this.report = report;
    this.timing = timing;
    this.eltsFound = eltsFound;
    this.callingThread = callingThread;
  }
  
  @Override
  protected List<IntArray> compute() {
    final int[][] arg = new int[arity][];
    while (true) {
      //if (report != null) {
      //  if (!callingThread.isAlive()) {
      //    report.addLine("calling thread is not alive");
      //    return newElts;
      //  }
      //  if (callingThread.isInterrupted()) {
      //    report.addLine("INNER INTERRUPTED");
      //    System.out.println("inner interrupted");
      //    return newElts;
      //  }
      //}
      for (int i = 0; i < arity; i++) {
        arg[i] = univList.get(argIndeces[i]).getArray();
      }
      int[] vRaw = op.valueAt(arg);
      IntArray v = new IntArray(vRaw);
      if (report != null) timing.incrementApps();  // is the apps count thread safe???????
      // this is subtle: we don't want to build the term
      // if this element has already been found. But 
      // because of threading it may get added just after
      // this so we need to check again and only add v to
      // newElts if it was not added.
      if (!map.containsKey(v)) {
        List<Term> children = new ArrayList<Term>(arity);
        for (int j = 0; j < arity; j++) {
          children.add(map.get(univList.get(argIndeces[j])));
        }
        Term term = map.putIfAbsent(v, new NonVariableTerm(op.symbol(), children));
        if (term == null) {
          newElts.add(v);
          eltsFound.getAndIncrement();
          if (report != null) {
            timing.incrementNextPassSize();
            report.setSize(eltsFound.get());
          }
        }
      }
      
      if (!incrementor.increment()) return newElts;
    }
  }
  
}

/**
 * This will will do one pass partial closure with a single 
 * Operation using a parallel algorithm. Note the term map
 * map e different on different runs with the same data.  
 * 
 * 
 * @author ralph
 *
 */
public class SingleClose extends RecursiveTask<List<IntArray>> {
  
  /**
   * The computaiton size is the number of application of op times
   * the length of the vectors (the length of each element of univList).
   */
  final static int MIN_COMPUTATION_SIZE = 10; //10000000;
  Thread callingThread;  // so we can see if it was cancelled
  ProgressReport report;
  CloserTiming timing;
  int computationSize;
  boolean tooSmall = false;
  final int increment;  // this is also the number of processes that will be used
  final int skip;  // will be 1 if tooSmall is true
  // it will also serve as id
  final List<IntArray> univList;
  final ConcurrentMap<IntArray,Term> map;
  final Operation op;
  //final int closedMark;
  final int min;
  final int max;
  final List<int[]> arrays;
  final List<ArrayIncrementor> incrementorList;
  final List<List<IntArray>> results;
  final AtomicInteger eltsFound;
  
  public SingleClose(List<IntArray> univList, ConcurrentMap<IntArray,Term> map, Operation op, 
      int min, int max, AtomicInteger eltsFound) {
    this(univList, map, op, min, max, eltsFound, -1);
  }
  
  public SingleClose(List<IntArray> univList, ConcurrentMap<IntArray,Term> map, Operation op, int min, 
                                    int max, AtomicInteger eltsFound, int inc) {
    
    this.univList = univList;
    this.map = map;
    this.op = op;
    this.min = min;
    // this.max = map.size() - 1; // Can't do this since univList may have elements added from other ops.
    this.max = max;
    this.increment = inc > 0 ? inc : calculateInc();
    this.eltsFound = eltsFound;
    this.computationSize = op.arity() * univList.get(0).getArray().length;// TODO: fix this
    this.arrays = new ArrayList<>(increment);
    this.incrementorList = new ArrayList<>(increment);
    this.results = new ArrayList<>(increment);
    this.tooSmall = computationSize < MIN_COMPUTATION_SIZE ? true : false;
    this.skip = tooSmall ? 1 : increment;
    //System.out.println("computationSize: " + computationSize);
    //System.out.println("skip: " + skip);
    setArraysAndIncrementors();
  }
  
  private int calculateInc() {
    if (max - min < 6) return 1;
    return 4;
  }
  // fix this; also check if the size is too small.
  private void setArraysAndIncrementors() {
    final int k = op.arity();
    int[] a = new int[k];
    a[k-1] = min;
    ArrayIncrementor tmpInc = SequenceGenerator.sequenceIncrementor(a, max, min);
    for (int i = 0; i < skip; i++) {
      final int[] b = Arrays.copyOf(a, a.length);
      arrays.add(b);
      ArrayIncrementor incrementor = tooSmall ? SequenceGenerator.sequenceIncrementor(b, max, min) 
                                              : SequenceGenerator.sequenceIncrementor(b, max, min, skip);
      incrementorList.add(incrementor);
      tmpInc.increment();
    }
  }
  
  @Override
  protected List<IntArray> compute() {
    List<RecursiveTask<List<IntArray>>> forks = new ArrayList<>();
    for (int i = 0; i < skip - 1; i++) {
      SingleCloseSerial task = new SingleCloseSerial(univList, map, op, 
          arrays.get(i), incrementorList.get(i), report, timing, eltsFound, callingThread);
      forks.add(task);
      if (callingThread.isInterrupted()) {
        System.out.println("interrupted a A");
        return univList;
      }
      task.fork();
    }
    final int last = arrays.size() - 1; 
    SingleCloseSerial lastTask = new SingleCloseSerial(univList, map, op, 
                                   arrays.get(last), incrementorList.get(last),
                                   report, timing, eltsFound, callingThread);
    results.add(lastTask.compute());    
    for (int i = skip - 2; i >= 0; i--) {
      if (callingThread.isInterrupted()) {
        System.out.println("interrupted a B");
        return univList; // return null ???
      }
      results.add(forks.get(i).join());
    }
    for (int i = 0; i < results.size(); i++) {
      if (callingThread.isInterrupted()) {
        System.out.println("interrupted a C");
        return univList; // return null ???
      }
      univList.addAll(results.get(i));
    }
    if (report != null) report.setSize(univList.size());
    return univList;
  }
  
  //public static List<IntArray> doOneStep(List<IntArray> univList, 
  //       ConcurrentMap<IntArray,Term> map, Operation op, int min, int max, AtomicInteger eltsFound) {
  //  return Pool.fjPool.invoke(new SingleClose(univList, map, op, min, max, eltsFound));
  //}
  
  /**
   * 
   * @return
   */
  public List<IntArray> doOneStep(ForkJoinPool pool, Thread callingThread, 
                                  ProgressReport report, CloserTiming timing) {
    this.report = report;
    this.timing = timing;
    this.callingThread = callingThread;
    // return Pool.fjPool.invoke(this);
    return pool.invoke(this);
  }

  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    
    
  }

}
