package org.uacalc.alg.op;

import java.util.List;
import java.util.ArrayList;
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
  //private int setSize;
  private boolean idempotentSet;
  private int[] randomValueTable;
  private Random random = RandomGenerator.getRandom();
  private int[] diagIndices;
  private int diagDiv;
  
  public OperationWithDefaultValue(Operation op) {
    super(op.symbol(), op.getSetSize());
    this.defaultValue = -1;
    this.op = op;
    this.valueTable = op.getTable();
    setDiagDiv();
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
    setDiagDiv();
  }
  
  public OperationWithDefaultValue(OperationSymbol sym, int algSize, 
                                   int[] valueTable, int defaultValue) {
    super(sym, algSize);
    if (valueTable == null) valueTable = makeMinusOneValueTable();
    this.valueTable = valueTable;
    this.defaultValue = defaultValue;
    op = Operations.makeIntOperation(sym, algSize, valueTable);
    setDiagDiv();
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
  
  public boolean isIdempotentSet() { return idempotentSet; }
  
  public void setIdempotent(boolean v) {
    idempotentSet = v;
    if (v) makeIdempotent();
  }
  
  public void makeIdempotent() {
    if (diagIndices == null) {
      final int arity = op.arity();
      diagIndices = new int[op.getSetSize()];
      for (int i = 0; i < op.getSetSize(); i++) {
        final int[] diag = new int[arity];
        for (int j = 0; j < arity; j++) {
          diag[j] = i;
        }
        diagIndices[i] = Horner.horner(diag, op.getSetSize());
      }
    }
    for (int i = 0; i < op.getSetSize(); i++) {
      valueTable[diagIndices[i]] = i;
    }
  }
  
  public boolean isDiagonal(int row, int col) {
    if (row % diagDiv == 0 && col == row / diagDiv) return true;
    return false;
  }
  
  private void setDiagDiv() {
    final int arity = op.arity();
    if (arity < 3) diagDiv = 1;
    else {
      int k = 1;
      int pow = op.getSetSize();
      for (int i = 0; i < arity - 2; i++) {
        k = k + pow;
        pow = pow * op.getSetSize();
      }
      diagDiv = k;
    }
  }
  
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
   * Note this effectively kills the default value part
   * of this.
   */
  public void makeTable() {
    Operation opx = makeOrdinaryOperation();
    if (opx != null) op = opx;
    // TODO: throw an exception if opx is null
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
  
  public static List<Operation> makeOrdinary(List<Operation> ops) {
    List<Operation> ans = new ArrayList<Operation>(ops.size());
    for (Operation op : ops) {
      if (op instanceof OperationWithDefaultValue) {
        ans.add(((OperationWithDefaultValue)op).makeOrdinaryOperation());
      }
      ans.add(op);
    }
    return ans;
  }
  
}
