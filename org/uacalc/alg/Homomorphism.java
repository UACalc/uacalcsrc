package org.uacalc.alg;

import java.util.*;

import org.uacalc.alg.conlat.*;

/**
 * A homomorphism from the domain algebra into
 * the range algebra. Previously we had parameterize
 * this with with K and V but not this is based on
 * the index of the elements.
 * 
 * @author ralph
 *
 * @param <K>  the type of element of the domain
 * @param <V>  the type of element of the range
 */
public class Homomorphism {
  
  private SmallAlgebra domain;
  private SmallAlgebra range;
  private Map<Integer,Integer> map;
  
  public Homomorphism(SmallAlgebra domain, SmallAlgebra range, Map<Integer,Integer> map) {
    this.setDomain(domain);
    this.setRange(range);
    this.setMap(map);
  }
  
  public Partition kernel() {
    final int size = domain.cardinality();
    Partition par = BasicPartition.zero(size);
    for (int i = 0; i < size; i++) {
      int r = par.representative(i);
      for (int j = i+1; j < size; j++) {
        //System.out.println("domain.getElement(" + j + "):" + domain.getElement(j));
        //System.out.println("map: " + map);
        if (map.get(i).equals(map.get(j))) {
          int s = par.representative(j);
          if (r != s) par.joinBlocks(r, s);
        }
      }
    }
    return par;
  }

  public Algebra getDomain() {
    return domain;
  }

  public void setDomain(SmallAlgebra domain) {
    this.domain = domain;
  }

  public SmallAlgebra getRange() {
    return range;
  }

  public void setRange(SmallAlgebra range) {
    this.range = range;
  }

  public Map<Integer,Integer> getMap() {
    return map;
  }

  public void setMap(Map<Integer,Integer> map) {
    this.map = map;
  }
  
  public String toString() {
    return "homomorphism: " + getDomain().getName() + " --> " + getRange().getName() + " : " + getMap();
  }
  
}
