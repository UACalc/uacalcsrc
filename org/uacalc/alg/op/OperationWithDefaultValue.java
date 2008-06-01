package org.uacalc.alg.op;

import java.util.List;
import java.util.Random;
import org.uacalc.util.Horner;
import org.uacalc.util.ArrayString;
import org.uacalc.ui.util.*;

/**
 * A convenience class for the UI.
 * 
 * @author ralph
 *
 */
public class OperationWithDefaultValue extends AbstractOperation {
  
  private int defaultValue;
  private Operation op;
  private int[] randomValueTable;
  private Random random = RandomGenerator.getRandom();
  
  public OperationWithDefaultValue(Operation op) {
    super(op.symbol(), op.getSetSize());
    this.defaultValue = -1;
    this.op = op;
    this.valueTable = op.getTable();
  }
  
  public OperationWithDefaultValue(String name, int arity, int algSize, 
                                        int defaultValue) {
    this(new OperationSymbol(name, arity), algSize, null, defaultValue);
  }
  
  public OperationWithDefaultValue(OperationSymbol sym, int algSize) {
    this(sym, algSize, null, -1);
  }
  
  public OperationWithDefaultValue(OperationSymbol sym, int algSize, int defaultValue) {
    this(sym, algSize, null, defaultValue);
  }

  public OperationWithDefaultValue(Operation op, int algSize) {
    super(op.symbol(), algSize);
    this.valueTable = getTable();
    this.defaultValue = -1;
    op = Operations.makeIntOperation(op.symbol(), algSize, valueTable);
  }
  
  public OperationWithDefaultValue(OperationSymbol sym, int algSize, 
                                   int[] valueTable, int defaultValue) {
    super(sym, algSize);
    if (valueTable == null) valueTable = makeMinusOneValueTable();
    this.valueTable = valueTable;
    this.defaultValue = defaultValue;
    op = Operations.makeIntOperation(sym, algSize, valueTable);
  }
  
  public OperationWithDefaultValue(String name, int arity, int algSize, 
                           int[] valueTable, int defaultValue) {
    this(new OperationSymbol(name, arity), algSize, valueTable, defaultValue);
  }
  
  public void updateRandomValueTable() {
    int n = 1;
    final int s = symbol().arity();
    for (int i = 0; i < s; i++) {
      n = algSize * n;
    }
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      ans[i] = random.nextInt(algSize);
    }
    randomValueTable = ans;
  }
  
  public int[] getRandomValueTable() {
    if (randomValueTable == null) updateRandomValueTable();
    return randomValueTable;
  }
  
  private int[] makeMinusOneValueTable() {
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
  
  public boolean isTotal() {
    if (defaultValue >= 0 || defaultValue == -2) return true;
    final int[] tab = getTable();
    for (int i = 0; i < tab.length; i++) {
      if (tab[i] < 0) return false;
    }
    return true;
  }
  
  public int getDefaultValue() { return defaultValue; }
  
  public void setDefaultValue(int v) { defaultValue = v; }
  
  @Override
  public Object valueAt(List args) {
    throw new UnsupportedOperationException();
  }
  
  public int intValueAt(int[] args) {
    int v = op.intValueAt(args);
    if (v == -1) {
      if (defaultValue != -2) return defaultValue;
      return getRandomValueTable()[Horner.horner(args, algSize)];
    }
    return v;
  }
  
  /**
   * Make an operation with the default value filled in.
   * 
   * @return an operation or null if the defaultValue is -1 and there is a -1 in the table
   */
  public Operation makeOrdinaryOperation() {
    final int n = valueTable.length;
    final int[] vt = new int[n];
    for (int i = 0; i < n; i++) {
      if (valueTable[i] == -1) {
        if (defaultValue == -1) return null;
        if (defaultValue == -2) vt[i] = getRandomValueTable()[i];
        else vt[i] = defaultValue;
      }
      else vt[i] = valueTable[i];
    }
    return Operations.makeIntOperation(symbol(), algSize, vt);
  }
  
}
