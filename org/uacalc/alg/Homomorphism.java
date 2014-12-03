package org.uacalc.alg;

import java.util.*;

import org.uacalc.alg.conlat.*;

/**
 * A homomorphism from the domain algebra into
 * the range algebra.
 * 
 * @author ralph
 *
 * @param <K>  the type of element of the domain
 * @param <V>  the type of element of the range
 */
public class Homomorphism<K,V> {
  
  private SmallAlgebra domain;
  private SmallAlgebra range;
  private Map<K,V> map;
  
  public Homomorphism(SmallAlgebra domain, SmallAlgebra range, Map<K,V> map) {
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
        if (map.get(domain.getElement(i)) == map.get(domain.getElement(j))) {
          //using low level partition stuff
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

  public Map<K,V> getMap() {
    return map;
  }

  public void setMap(Map<K,V> map) {
    this.map = map;
  }
  

}
