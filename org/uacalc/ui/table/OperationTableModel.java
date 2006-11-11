package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import org.uacalc.alg.Operation;
import org.uacalc.alg.OperationSymbol;
import org.uacalc.alg.Operations;
import org.uacalc.util.Horner;

public class OperationTableModel extends AbstractTableModel {

  Operation op;
  
  int[] valueTable;
  
  public OperationTableModel(Operation op) {
    this.op = op;
  }
  
  public OperationTableModel(int arity, int setSize) {
    this(OperationSymbol.getOperationSymbol(arity), setSize, -1, false);
    //this.op = makeUndefinedOp(OperationSymbol.getOperationSymbol(arity), 
    //                                                     setSize, -1, false);
  }
  
  public OperationTableModel(int arity, int setSize, 
                              int init, boolean idempotent) {
    this(OperationSymbol.getOperationSymbol(arity), setSize, -1, false);
  }
  
  public OperationTableModel (OperationSymbol sym, int setSize,
                                          int init, boolean idempotent) {
    op = makeUndefinedOp(sym, setSize, init, idempotent);
  }
  /**
   * Make an int operation which returns the invalid value -1.
   * 
   * @param sym
   * @param setSize
   * @return
   */
  Operation makeUndefinedOp(OperationSymbol sym, int setSize, int init, boolean idempotent) {
    final int arity = sym.arity();
    int n = 1;
    for (int k = 0; k < arity; k++) {
      n = n * setSize;
    }
    int[] vals = new int[n];
    for (int i = 0; i < n; i++) {
      vals[i] = init;
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
        if (diag) vals[i] = a;
      }
    }
    this.valueTable = vals;
    return Operations.makeIntOperation(sym, setSize, vals);
  }
  
  public int[] getValueTable() { return valueTable; }
  
  public Operation getOperation() {
    return op;
  }
  public int getColumnCount() {
    // TODO Auto-generated method stub
    return op.getSetSize();
  }

  public int getRowCount() {
    // TODO Auto-generated method stub
    return valueTable.length / op.getSetSize();
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    // TODO Auto-generated method stub
    return -1;
  }

}
