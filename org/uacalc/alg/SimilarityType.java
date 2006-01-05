/* SimilarityType.java (c) 2003/01/03  Ralph Freese */

package org.uacalc.alg;

import java.util.*;

/**
 * A set of OperationSymbol's.
 */
public class SimilarityType {

  public static final SimilarityType LATTICE_SIMILARITY_TYPE;
  static {
    List opsyms = new ArrayList(2);
    opsyms.add(OperationSymbol.JOIN);
    opsyms.add(OperationSymbol.MEET);
    LATTICE_SIMILARITY_TYPE = new SimilarityType(opsyms);
  }

  public static final SimilarityType GROUP_SIMILARITY_TYPE;
  static {
    List opsyms = new ArrayList(3);
    opsyms.add(OperationSymbol.PRODUCT);
    opsyms.add(OperationSymbol.INVERSE);
    opsyms.add(OperationSymbol.IDENTITY);
    GROUP_SIMILARITY_TYPE = new SimilarityType(opsyms);
  }

  List operationSymbols;

/*
  public SimilarityType(List<OperationSymbol> opSyms) {
    this.operationSymbols = opSyms;
  }
*/

  public SimilarityType(List opSyms) {
    this.operationSymbols = opSyms;
  }

  public List getOperationSymbols() { return operationSymbols; }

  public String toString() {
    StringBuffer sb = new StringBuffer("(");
    for (Iterator it = operationSymbols.iterator(); it.hasNext(); ) {
      sb.append(it.next().toString());
      if (it.hasNext()) sb.append(", ");
    }
    sb.append(")");
    return sb.toString();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof SimilarityType)) return false;
    if (this == obj) return true;
    //SimilarityType st = (SimilarityType)obj;
    List ops = ((SimilarityType)obj).getOperationSymbols();
    if (ops.size() != operationSymbols.size()) return false;
    for (Iterator it = operationSymbols.iterator(); it.hasNext(); ) {
      if (!ops.contains(it.next())) return false;
    }
    return true;
  }

  public int hashCode() {
    return operationSymbols.hashCode();
  }


}




