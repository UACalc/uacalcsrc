package org.uacalc.alg.op;

import java.util.List;

/**
 * A convenience class for the UI.
 * 
 * @author ralph
 *
 */
public class OperationWithDefaultValue extends AbstractOperation {
  
  private int defaultValue;
  private Operation op;

  public OperationWithDefaultValue(OperationSymbol sym, int algSize, int[] valueTable, int defaultValue) {
    super(sym, algSize);
    this.valueTable = valueTable;
    this.defaultValue = defaultValue;
    op = Operations.makeIntOperation(sym, algSize, valueTable);
  }
  
  public OperationWithDefaultValue(String name, int arity, int algSize, int[] valueTable, int defaultValue) {
    this(new OperationSymbol(name, arity), algSize, valueTable, defaultValue);
  }
  
  @Override
  public Object valueAt(List args) {
    throw new UnsupportedOperationException();
  }
  
  public int intValueAt(int[] args) {
    int v = op.intValueAt(args);
    if (v == -1) return defaultValue;
    return v;
  }
  
  /**
   * Make an operation with the default value filled in.
   * 
   * @return an operation or null if the defaultValue is -1
   */
  public Operation makeOperation() {
    if (defaultValue == -1) return null;
    final int n = valueTable.length;
    final int[] vt = new int[n];
    for (int i = 0; i < n; i++) {
      if (valueTable[i] == -1) vt[i] = defaultValue;
      else vt[i] = valueTable[i];
    }
    return Operations.makeIntOperation(symbol(), algSize, vt);
  }
  
}
