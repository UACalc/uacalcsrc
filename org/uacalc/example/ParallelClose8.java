package org.uacalc.example;

import java.util.*;
//import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import org.uacalc.util.*;
import org.uacalc.alg.op.*;
import java.util.function.*;

public class ParallelClose8<V> {
  
  ConcurrentMap<int[],V> map;
  List<int[]> elements;
  java.util.function.Function<int[],V> mappingFunction;
  
  public ParallelClose8(ConcurrentMap<int[],V> map, List<int[]> elems, java.util.function.Function f) {
    this.map = map;
    this.elements = elems;
    this.mappingFunction = f;
  }
  
  
  public Map<int[],V> closeOnce(Operation op, int closedMark, int newMark) {
    Stream<int[]> indexStream = TupleStream.intTupleStream(closedMark, op.arity(), newMark);
    Stream<int[][]> argStream = indexStream.map(arr -> argTuple(arr, op.arity()));
    Stream<int[]> appStream = argStream.map(arg -> op.valueAt(arg));
    //Collector.Characteristics chracteristics {UNORDERED, CONCURRENT};
    //Collector<int[],Map<int[],V>,Map<int[],V>> collector = Collector.of(supplier(), accumulator(), combiner(), finisher(), characteristics());
    Collector<int[],Map<int[],V>,Map<int[],V>> collector = Collector.of(supplier(), accumulator(), combiner());
    return map;
  }
  
  public int[][] argTuple(int[] arr, int arity) {
    int[][] ans = new int[arity][];
    for (int i = 0; i < arity; i++) {
      ans[i] = elements.get(i);
    }
    return ans;
  } 
  
  private Supplier<Map<int[],V>> supplier() {
    return () -> map;
    /*
    return new Supplier<Map<int[],V>>() {
      public Map<int[],V> get() {
        return map;
      }
    };
    */
  }
  
  private BiConsumer<Map<int[],V>,int[]> accumulator() {
    return (map,arr) -> map.computeIfAbsent(arr, mappingFunction);
  }
    
  private BinaryOperator<Map<int[],V>> combiner() {
    return (map0,map1) -> map0;
  }
  
  private java.util.function.Function<ConcurrentMap<int[],V>,ConcurrentMap<int[],V>> finisher() {
    HashSet<int[]> hs = new HashSet<>(elements);
    for (int[] arr : map.keySet()) {
      if (!hs.contains(arr)) elements.add(arr);
    }
    return (map0) -> map;
  }
  
  private Set<Collector.Characteristics> characteristics() {
    Set<Collector.Characteristics> chars = new HashSet<>();
    chars.add(Collector.Characteristics.UNORDERED);
    chars.add(Collector.Characteristics.CONCURRENT);
    return Collections.unmodifiableSet(chars);
  }
  

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
