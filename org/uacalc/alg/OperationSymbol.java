/* Operation.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.List;
import java.util.HashMap;


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
  public static OperationSymbol getOperationSymbol(int arity, HashMap map) {
    Integer ar = new Integer(arity);
    int[] value = (int[])map.get(ar);
    if (value == null) {
      value = new int[] {-1};
      map.put(ar, value);
    }
    value[0] = value[0] + 1;
    switch (arity) {
      case 0:
        return new OperationSymbol("c_" + value[0], arity);
      case 1:
        return new OperationSymbol("u_" + value[0], arity);
      case 2:
        return new OperationSymbol("b_" + value[0], arity);
      case 3:
        return new OperationSymbol("t_" + value[0], arity);
      default:
        return new OperationSymbol("op" + arity + "_" + value[0], arity);
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




