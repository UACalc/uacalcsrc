package org.uacalc.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.*;
import org.uacalc.util.*;
import org.uacalc.alg.op.*;

public class ParallelClose8<K,V> {
  
  Map<K,V> map;
  List<K> elements;
  
  public ParallelClose8(Map<K,V> map, List<K> elems) {
    this.map = map;
    this.elements = elems;
  }
  
  
  public Map<K,V> closeOnce(Operation op, int closedMark, int newMark) {
    Stream<int[]> tupStream = TupleStream.intTupleStream(closedMark, op.arity(), newMark);
    Stream<K> elemStream = tupStream.map(arr -> foobar(arr));
    
    return map;
  }
  
  public K foobar(int[] arr) {
    return null;
  }
  
  

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
