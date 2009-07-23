package org.uacalc.alg.conlat;

import org.uacalc.util.*;

public interface BinaryRelation extends Iterable<IntArray> {

  public int universeSize();
  
  public boolean isRelated(int i, int j);
  
  
}
