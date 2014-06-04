package org.uacalc.example;


import java.util.concurrent.*;
import java.util.*;

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

class Globals {
  static ForkJoinPool fjPool = new ForkJoinPool();
}

class Function {
  final int a = 43;
  final int b = 571;
  final int c = 100001;
  final static int itCount = 20001;

  public int g(int x, int y) {
    return (a*x + b*y) % c;
  }
}

class SerialClose extends RecursiveTask<Map<Integer,Integer>> {
  final int increment;  // this is also the number of processes that will be used
  final int start;      // this will also serve as id
  final Map<Integer,Integer> map;
  final Function fnObj;

  SerialClose(Function fnObj, Map<Integer,Integer> map, int increment, int start) {
    //super();  // not sure if this is nec ?????
    this.fnObj = fnObj;
    this.map = map;
    this.increment = increment;
    this.start = start;
  }

  @Override
  protected Map<Integer,Integer> compute() {
    //return computeSerial();
    System.out.println("id = " + start);
    for (int i = 0; i < fnObj.itCount; i++) {
      for (int j = start; j < fnObj.itCount; j += increment) {
        int v = fnObj.g(i,j);
        if (!map.containsKey(v)) map.put(v, start);
      }
    }
    System.out.println("id done = " + start);
    return map;
  }

  protected Map<Integer,Integer> computeSerial() {
    System.out.println("id = " + start);
    for (int i = 0; i < fnObj.itCount; i++) {
      for (int j = start; j < fnObj.itCount; j += increment) {
        int v = fnObj.g(i,j);
        if (!map.containsKey(v)) map.put(v, start);
      }
    }
    System.out.println("id done = " + start);
    return map;
  }

}

class ParallelClose extends RecursiveTask<Map<Integer,Integer>> {

  final int increment;  // this is also the number of processes that will be used
                        // it will also serve as id
  final Map<Integer,Integer> map;
  final Function fnObj;
  
  ParallelClose(Function fnObj, Map<Integer,Integer> map, int increment) {
    super(); // is this nec ?????
    this.fnObj = fnObj;
    this.map = map;
    this.increment = increment;
  }

  @Override
  protected Map<Integer,Integer> compute() {
    List<RecursiveTask<Map<Integer,Integer>>> forks = new ArrayList<>();
    for (int i = 0; i < increment - 1; i++) {
      SerialClose task = new SerialClose(fnObj, map, increment, i);
      forks.add(task);
      task.fork();
    }
    SerialClose lastTask = new SerialClose(fnObj, map, increment, increment - 1);
    lastTask.compute();    
    for (int i = increment - 2; i >= 0; i--) {
      forks.get(i).join();
    }
    return map;
  }

  static Map<Integer,Integer> fixMap(Function fnObj, Map<Integer,Integer> map, int increment) {
    return Globals.fjPool.invoke(new ParallelClose(fnObj, map, increment));
  }

  //static Map<Integer,Integer> fixMap(Map<Integer,Integer> map) {
  //  return Globals.fjPool.invoke(new ParallelClose(map, 4));
  //}


  public static void main(String[] args) {
    Map<Integer,Integer> map = new ConcurrentHashMap<>();
    int inc = -1;
    try {
      inc = Integer.parseInt(args[0]);
    } catch (Exception ex) {}
    if (inc == -1) inc = 4;
    
    long t0 = System.currentTimeMillis();
    fixMap(new Function(), map, inc);
    long time = System.currentTimeMillis() - t0;

    int[] counts = new int[inc];
    for (Integer key : map.keySet()) {
      int v = map.get(key);
      counts[v]++;
    }
    System.out.println("counts: " + Arrays.toString(counts));
    System.out.println("total count: " + map.size());
    System.out.println("total execs: " + (Function.itCount * Function.itCount));
    System.out.println("time: " + time);

    boolean doSerial = false;
    if (doSerial) {
      map = new HashMap<Integer,Integer>();
      SerialClose sclose = new SerialClose(new Function(), map, 1, 0);
      t0 = System.currentTimeMillis();
      try {
        sclose.computeSerial();
      } catch (Exception ex) {}
      time = System.currentTimeMillis() - t0;
      System.out.println("pure serial time: " + time);
    }
  }

}




