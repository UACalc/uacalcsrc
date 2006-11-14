package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.lang.System;
import org.uacalc.alg.Operation;
import org.uacalc.alg.OperationSymbol;
import org.uacalc.alg.Operations;
import org.uacalc.util.Horner;
import org.uacalc.util.SequenceGenerator;
import org.uacalc.util.ArrayIncrementor;

public class OperationTableModel extends AbstractTableModel {

  Operation op = null;
  OperationSymbol opSym;
  
  int defaultValue = -1;
  int[] valueTable;
  int[][] leftArgsTable;// all but the last arg
  String[] colNames;
  
  public OperationTableModel(Operation op) {
    this.op = op;
    this.opSym = op.symbol();
  }
  
  public OperationTableModel(int arity, int setSize) {
    this(OperationSymbol.getOperationSymbol(arity), setSize, false, -1);
    //this.op = makeUndefinedOp(OperationSymbol.getOperationSymbol(arity), 
    //                                                     setSize, -1, false);
  }
  
  public OperationTableModel(int arity, int setSize, boolean idempotent, int defaultValue) {
    this(OperationSymbol.getOperationSymbol(arity), setSize, false, defaultValue);
  }
  
  public OperationTableModel (OperationSymbol sym, int setSize,
                                                   boolean idempotent, int defaultValue) {
    this.defaultValue = defaultValue;
    op = makeUndefinedOp(sym, setSize, idempotent);
  }
  /**
   * Make an int operation which returns the defaultValue. It might be the invalid value -1.
   * Also set up leftArgsTable.
   * 
   * @param sym
   * @param setSize
   * @return
   */
  Operation makeUndefinedOp(OperationSymbol sym, int setSize, boolean idempotent) {
    final int arity = sym.arity();
    int n = 1;
    for (int k = 0; k < arity; k++) {
      n = n * setSize;
    }
    final int k = n / setSize;
    valueTable = new int[n];
    leftArgsTable = new int[k][];
    final int[] seq = new int[setSize - 1];
    for (int i = 0; i < setSize - 1; i++) {
      seq[i] = 0;
    }
    colNames = new String[k];
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(seq, setSize - 1);
    for (int j = 0 ; j < k; j++) {
      int[] dest = new int[setSize - 1];
      System.arraycopy(seq, 0, dest, 0, setSize - 1);
      leftArgsTable[j] = dest;
      setColName(k, dest);
      inc.increment();
    }
    for (int i = 0; i < n; i++) {
      valueTable[i] = defaultValue;
    }
    if (idempotent) {
      for (int i = 0; i < n; i++) {
        final int[] arg = Horner.hornerInv(i, setSize, arity);
        boolean diag = true;
        final int a = arg[0];
        for (int j = 1; j < arity; j++) {
          if (a != arg[j]) {
            diag = false;
            break;
          }
        }
        if (diag) valueTable[i] = a;
      }
    }
    return Operations.makeIntOperation(sym, setSize, valueTable);
  }
  
  public void setColName(int k, int[] arg) {
    final String x = "x";
    final String y = "y";
    final String z = "z";
    final String left = "(";
    final String right = ")";
    final String comma = ",";
    String free;
    if (op.arity() == 1) free = x;
    if (op.arity() == 2) free = y;
    else free = z;
    StringBuffer sb = new StringBuffer(left);
    for (int i = 0; i < arg.length; i++) {
      sb.append(arg[i]);
      sb.append(comma);
    }
    sb.append(free);
    sb.append(right);
    colNames[k] = sb.toString();
  }
  
  public OperationSymbol getOperationSymbol() { return opSym; }
  
  public int[] getValueTable() { return valueTable; }
  
  public Operation getOperation() {
    return op;
  }
  
  /**
   * The column of index 0 is a vector of all but the last arg.
   */
  public int getColumnCount() {
    return op.getSetSize() + 1;
  }

  public int getRowCount() {
    return leftArgsTable.length;
  }

  public String getColumnName() {
    return null;// fix me
  }
  
  public Class getColumnClass(int col) {
    if (col == 0) return String.class;
    return Integer.class;
  }
  
  public boolean isCellEditable(int row, int col) {
    if (col == 0) return false;
    return true;
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      return colNames[rowIndex];
    }
    return op.intValueAt(rowColToArg(rowIndex, columnIndex));
  }

  public void setValueAt(Object val, int row, int col) {
    if (col == 0) return;
    int value = ((Integer)val).intValue();
    valueTable[Horner.horner(rowColToArg(row, col), op.getSetSize())] = value;
  }
  
  private int[] rowColToArg(int row, int col) {
    if (col == 0) return null;
    int[] left = leftArgsTable[row];
    int[] arg = new int[op.arity()];
    System.arraycopy(left, 0, arg, 0, left.length);
    arg[arg.length - 1] = col - 1;
    return Horner.leftRightReverse(arg, op.getSetSize(), op.arity());
  }
}




