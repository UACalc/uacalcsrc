/* BasicAlgebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;

/**
 * This class represents SmallAlgebra's.
 * Such algebras have a map from {0, ..., n-1} and the elements of
 * the algebra.  The operations are done on the ints and converted back
 * the elements.
 */
public class ParameterizedAlgebra {

  List<String> parameterNames;
  String name;
  String setSizeExp;
  String description;
  List<ParameterizedOperation> ops;

  public Map<String,String> getParameterMap(List<Integer> values) {
    Map<String, String> parmMap = new HashMap<String, String>();
    Iterator<Integer> it = values.iterator();
    for (String s : parameterNames) {
      Integer i = it.next();
      parmMap.put(s, i.toString());
    }
    return parmMap;
  }
      

}

