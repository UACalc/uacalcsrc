package org.uacalc.alg.conlat;

import java.util.*;
import org.uacalc.util.*;

public class BasicBinaryRelation {

  private final NavigableSet<IntArray> pairs;
  private final int univSize;
  
  public BasicBinaryRelation(int univSize) {
    this.univSize = univSize;
    pairs = new TreeSet<IntArray>();
  }
  
  public BasicBinaryRelation(Collection<IntArray> collection, int univSize) {
    this(univSize);
    pairs.addAll(collection);
  }
  
  public boolean isRelated(int i, int j) {
    return pairs.contains(new IntArray(new int[]{i, j}));
  }
  
  public void add(int i, int j) {
    pairs.add(new IntArray(new int[]{i, j}));
  }
  
  public boolean isReflexive() {
    for (int i = 0; i < univSize; i++) {
      if (!isRelated(i, i)) return false;
    }
    return true;
  }
  
  public boolean isSymmetric() {
    for (IntArray ia : pairs) {
      if (!isRelated(ia.get(1), ia.get(0))) return false;
    }
    return true;
  }
  
}
