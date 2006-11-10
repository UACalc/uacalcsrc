/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;


/**
 * An oration symbol. It has both a String for it printed name and 
 * and arity. 
 */
public class OperationSymbol {

  public static final OperationSymbol JOIN = new OperationSymbol("join", 2);
  public static final OperationSymbol MEET = new OperationSymbol("meet", 2);
  public static final OperationSymbol PRODUCT = new OperationSymbol("prod", 2);
  public static final OperationSymbol INVERSE = new OperationSymbol("inv", 1);
  public static final OperationSymbol IDENTITY = new OperationSymbol("id", 0);
  
  static final Map<Integer, Integer> currentSymIndexMap = new HashMap<Integer, Integer>();
  //static final Set<OperationSymbol> currentSymbols = new HashSet<OperationSymbol>();

  String name;
  int arity;

  public OperationSymbol(String name, int arity) {
    this.name = name;
    this.arity = arity;
  }

  /**
   * This gives the arity of this operation.
   */
  public int arity() { return arity; }

  public String name() { return name; }

  public String toString() { return name; }

  /**
   * Get an OperationSymbol in a uniform manner so that algebras that
   * can be similar will be.
   *
   * @param HashMap map a map from Integer's to int[1]'s, the value will
   *                   be modified.
   */
  public static OperationSymbol getOperationSymbol(int arity) {
    Integer index = currentSymIndexMap.get(arity);
    if (index == null) currentSymIndexMap.put(arity, 0);
    else currentSymIndexMap.put(arity, index + 1);
    int ind = currentSymIndexMap.get(arity);
    switch (arity) {
      case 0:
        return new OperationSymbol("c_" + ind, arity);
      case 1:
        return new OperationSymbol("u_" + ind, arity);
      case 2:
        return new OperationSymbol("b_" + ind, arity);
      case 3:
        return new OperationSymbol("t_" + ind, arity);
      default:
        return new OperationSymbol("op" + arity + "_" + ind, arity);
    }
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof OperationSymbol)) return false;
    OperationSymbol sym = (OperationSymbol)obj;
    return name.equals(sym.name()) && arity == sym.arity();
  }

  public int hashCode() {
    return name.hashCode() + arity;
  }


}




