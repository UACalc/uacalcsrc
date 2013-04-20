package org.uacalc.alg.conlat;

import java.util.*;
import org.uacalc.util.*;

public class BasicBinaryRelation implements BinaryRelation {

  private final NavigableSet<IntArray> pairs;
  private final int univSize;
  //private Map<Integer,List<Integer>> rightMap = null;
  //private Map<Integer,List<Integer>> leftMap = null;
  
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
  
  /**
   * Relation composition. This follows DeMeo's suggestion.
   * 
   */
  public BinaryRelation compose(BinaryRelation beta) {
    BasicBinaryRelation ans = new BasicBinaryRelation(univSize);
    for (IntArray ia : pairs) {
      for (int k = 0; k < univSize; k++) {
        if (beta.isRelated(ia.get(1), k)) ans.add(ia.get(0), k);
      }
    }
    return ans;
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
  
  public String toString() {
    return pairs.toString();
  }
  
  public static void main(String[] args) {
    BasicBinaryRelation alpha = new BasicBinaryRelation(5);
    BasicBinaryRelation beta = new BasicBinaryRelation(5);
    alpha.add(0,1);
    alpha.add(0,2);
    beta.add(1,3);
    
    System.out.println(alpha.compose(beta).toString());
    
    Partition alphaPar = new BasicPartition("|0,1|2,3|4,5|");
    Partition betaPar = new BasicPartition("|0,1|2,3,4,5|");
    Partition gammaPar = new BasicPartition("|0,2|1,4|3,5|");

    System.out.println(alphaPar.compose(gammaPar).toString());
  }
  
}
