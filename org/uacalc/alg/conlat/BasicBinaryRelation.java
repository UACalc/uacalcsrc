package org.uacalc.alg.conlat;

import java.util.*;
import org.uacalc.util.*;

public class BasicBinaryRelation implements BinaryRelation {

  private final NavigableSet<IntArray> pairs;
  private final int univSize;
  
  public BasicBinaryRelation(int univSize) {
    this.univSize = univSize;
    pairs = new TreeSet<IntArray>(IntArray.lexicographicComparitor());
  }
  
  public BasicBinaryRelation(Collection<IntArray> collection, int univSize) {
    this(univSize);
    pairs.addAll(collection);
  }
  
  public NavigableSet<IntArray> getPairs() { return pairs; }
  
  public int compareTo(Object o) {
    BinaryRelation rel = (BinaryRelation)o;
    return pairs.size() - rel.getPairs().size();
  }
  
  public boolean isRelated(int i, int j) {
    return pairs.contains(new IntArray(new int[]{i, j}));
  }
  
  public int universeSize() { return univSize; }
  
  public Iterator<IntArray> iterator() {
    return pairs.iterator();
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
