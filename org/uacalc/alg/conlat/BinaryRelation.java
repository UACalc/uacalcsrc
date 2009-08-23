package org.uacalc.alg.conlat;

import org.uacalc.util.*;
import java.util.*;

public interface BinaryRelation extends Iterable<IntArray>, Comparable {

  public int universeSize();
  
  public boolean isRelated(int i, int j);
  
  public NavigableSet<IntArray> getPairs();
  
  
}
