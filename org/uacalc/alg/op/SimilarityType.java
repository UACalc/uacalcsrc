/* SimilarityType.java (c) 2003/01/03  Ralph Freese */

package org.uacalc.alg.op;

import java.util.*;
import java.math.*;

/**
 * A set of OperationSymbol's.
 */
public class SimilarityType {

  public static final SimilarityType LATTICE_SIMILARITY_TYPE;
  static {
    List<OperationSymbol> opsyms = new ArrayList<OperationSymbol>(2);
    opsyms.add(OperationSymbol.JOIN);
    opsyms.add(OperationSymbol.MEET);
    LATTICE_SIMILARITY_TYPE = new SimilarityType(opsyms);
  }

  public static final SimilarityType GROUP_SIMILARITY_TYPE;
  static {
    List<OperationSymbol> opsyms = new ArrayList<OperationSymbol>(3);
    opsyms.add(OperationSymbol.PRODUCT);
    opsyms.add(OperationSymbol.INVERSE);
    opsyms.add(OperationSymbol.IDENTITY);
    GROUP_SIMILARITY_TYPE = new SimilarityType(opsyms);
  }

  List<OperationSymbol> operationSymbols;

  public SimilarityType(List<OperationSymbol> opSyms) {
    this(opSyms, false);
  }
  
  public SimilarityType(List<OperationSymbol> opSyms, boolean sort) {
    if (sort) Collections.sort(opSyms);
    this.operationSymbols = opSyms;
  }

  public List<OperationSymbol> getOperationSymbols() { 
    return operationSymbols; 
  }
  
  /**
   * The sorting is by lowest arity first then by alphabetical
   * on the name.
   * 
   * @return a sorted list of operations.
   */
  public List<OperationSymbol> getSortedOperationSymbols() {
    Collections.sort(operationSymbols);
    return operationSymbols; 
  }
  
  /**
   * This calculates the (computer) input size. If it exceeds 
   * the max int value, it returns -1; If there are no operations
   * it returns the algebra size.
   * 
   * @param algSize the algebra size
   * @return the input size if it is an int
   */
  public int inputSize(int algSize) {
    if (operationSymbols.size() == 0) return algSize;
    BigInteger inputSize = BigInteger.ZERO;
    final BigInteger algebraSize = BigInteger.valueOf((long)algSize);
    final BigInteger max = BigInteger.valueOf((long)Integer.MAX_VALUE);
    for (OperationSymbol sym : operationSymbols) {
      inputSize = inputSize.add(algebraSize.pow(sym.arity()));
      if (inputSize.compareTo(max) >= 0) return -1;
    }
    return (int)inputSize.longValue();
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("(");
    for (Iterator<OperationSymbol> it = operationSymbols.iterator(); 
                                                         it.hasNext(); ) {
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
    List<OperationSymbol> ops = ((SimilarityType)obj).getOperationSymbols();
    if (ops.size() != operationSymbols.size()) return false;
    for (OperationSymbol op : operationSymbols) {
      if (!ops.contains(op)) return false;
    }
    return true;
  }

  public int hashCode() {
    return operationSymbols.hashCode();
  }


}




