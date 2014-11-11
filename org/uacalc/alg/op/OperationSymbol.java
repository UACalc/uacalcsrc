/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg.op;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;


/**
 * An oration symbol. It has both a String for its printed name and 
 * and an arity. 
 */
public class OperationSymbol implements Comparable<OperationSymbol> {

  public static final OperationSymbol JOIN = new OperationSymbol("join", 2);
  public static final OperationSymbol MEET = new OperationSymbol("meet", 2);
  public static final OperationSymbol PRODUCT = new OperationSymbol("prod", 2);
  public static final OperationSymbol INVERSE = new OperationSymbol("inv", 1);
  public static final OperationSymbol IDENTITY = new OperationSymbol("id", 0);
  
  private boolean associative = false;
  
  static final Map<Integer, Integer> currentSymIndexMap = new HashMap<Integer, Integer>();
  //static final Set<OperationSymbol> currentSymbols = new HashSet<OperationSymbol>();

  String name;
  int arity;
  
  public OperationSymbol(String name, int arity) {
    this(name, arity, false);
  }

  public OperationSymbol(String name, int arity, boolean assoc) {
    this.name = name;
    this.arity = arity;
    setAssociative(assoc);
  }

  /**
   * This gives the arity of this operation.
   */
  public int arity() { return arity; }

  public String name() { return name; }
  
  /**
   * A binary operation symbol can be marked as associative to allow
   * for a more compact representation and better printing of terms 
   * containing it.
   * 
   * @return
   */
  public boolean isAssociative() { return associative; }
  
  public void setAssociative(boolean assoc) {
    if (assoc && arity != 2)  throw new IllegalArgumentException("Only binary terms can be associative.");
    if (assoc && arity == 2) associative = true;
    else associative = false;
  }

  public String toString() { return toString(false); }
  
  public String toString(boolean showArity) {
    if (showArity) return name + "(" + arity + ")";
    return name; 
  }

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
  
  /**
   * This puts high arity operations first.
   */
  public int compareTo(OperationSymbol sym) {
    if (arity < sym.arity()) return 1;
    if (arity > sym.arity()) return -1;
    return name.compareTo(sym.name());
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




