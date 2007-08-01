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
  
  public OperationWithDefaultValue(Operation op) {
    super(op.symbol(), op.getSetSize());
    this.defaultValue = -1;
    this.op = op;
  }
  
  public OperationWithDefaultValue(String name, int arity, int algSize, int defaultValue) {
    this(new OperationSymbol(name, arity), algSize, null, defaultValue);
  }
  
  public OperationWithDefaultValue(OperationSymbol sym, int algSize, int defaultValue) {
    this(sym, algSize, null, defaultValue);
  }

  public OperationWithDefaultValue(OperationSymbol sym, int algSize, int[] valueTable, int defaultValue) {
    super(sym, algSize);
    if (valueTable == null) valueTable = makeValueTable();
    this.valueTable = valueTable;
    this.defaultValue = defaultValue;
    op = Operations.makeIntOperation(sym, algSize, valueTable);
  }
  
  public OperationWithDefaultValue(String name, int arity, int algSize, int[] valueTable, int defaultValue) {
    this(new OperationSymbol(name, arity), algSize, valueTable, defaultValue);
  }
  
  private int[] makeValueTable() {
    int n = 1;
    final int s = symbol().arity();
    for (int i = 0; i < s; i++) {
      n = algSize * n;
    }
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      ans[i] = -1;
    }
    return ans;
  }
  
  public int getDefaultValue() { return defaultValue; }
  
  public void setDefaultValue(int v) { defaultValue = v; }
  
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
   * @return an operation or null if the defaultValue is -1 and there is a -1 in the table
   */
  public Operation makeOperation() {
    final int n = valueTable.length;
    final int[] vt = new int[n];
    for (int i = 0; i < n; i++) {
      if (valueTable[i] == -1) {
        if (defaultValue == -1) return null;
        vt[i] = defaultValue;
      }
      else vt[i] = valueTable[i];
    }
    return Operations.makeIntOperation(symbol(), algSize, vt);
  }
  
}
