package org.uacalc.nbui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationWithDefaultValue;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.Operations;
import org.uacalc.ui.table.*;
import org.uacalc.ui.util.*;

public class AlgebraEditorController {

  private final UACalc uacalc;
  private GUIAlgebra gAlg;
  private String desc;
  //private SmallAlgebra alg;
  private int algSize;
  //private java.util.List<OperationWithDefaultValue> opList;
  private java.util.List<Operation> opList;
  private java.util.List<OperationSymbol> symbolList;
  private java.util.Map<OperationSymbol,Operation> opMap;
  private SmallAlgebra.AlgebraType currentAlgType;
  private final Random random = RandomGenerator.getRandom();
  
  
  public AlgebraEditorController(final UACalc uacalc) {
    this.uacalc = uacalc;

    
    // TODO: move this to UACalculatorUI
    /*
    desc_tf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateDescription();
      }
    });
    
    ops_cb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OpSymItem item = (OpSymItem)ops_cb.getSelectedItem();
        if (item == null) return;
        OperationSymbol opSym = item.getOperationSymbol();
        OperationWithDefaultValue op = opMap.get(opSym);
        if (op != null) {
          OperationInputTable opTable = 
                    new OperationInputTable(op, uacalc);
          setOperationTable(opTable);
        }
        validate();
        uacalc.repaint();
      }
    });
 
    delOpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(
            uacalc,
            "Delete this operation?",
            "Delete this operatin?",
            JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
          removeCurrentOperation();
        }
      }
    });
    addOpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (opTablePanel != null && !opTablePanel.stopCellEditing()) {
          uacalc.beep();
          return;
        }
        if (opList == null) {  // algebra 
          uacalc.beep();
          return;
        }
        String name = getOpNameDialog();
        if (name == null) return;
        int arity = getArityDialog();
        if (arity == -1) return;
        addOperation(name, arity);
      }
    });
    */
  }
  
  public void setCurrentOp() {
    OpSymItem item = (OpSymItem)uacalc.getOpsComboBox().getSelectedItem();
    if (item == null) return;
    OperationSymbol opSym = item.getOperationSymbol();
    OperationWithDefaultValue op = (OperationWithDefaultValue)opMap.get(opSym);
    // TODO: change this
    if (op != null) {
      javax.swing.table.TableModel model = new OperationTableModel(op);
      model.addTableModelListener(new TableModelListener() {
        public void tableChanged(TableModelEvent evt) {
          getMainController().getCurrentAlgebra().setNeedsSave(true);
          getMainController().setTitle();
          final MainController mc = getMainController();
          mc.getPropertyChangeSupport().firePropertyChange(
              MainController.ALGEBRA_CHANGED, null, null);
        }
      });
      JTable table = uacalc.getOpTable();
      table.setModel(model);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      TableColumn column = null;
      for (int i = 0; i < model.getColumnCount(); i++) {
        column = table.getColumnModel().getColumn(i);
        if (i == 0) {
          column.setPreferredWidth(100);
          column.setMinWidth(80);
        }
        else {
          column.setPreferredWidth(30);
          column.setMinWidth(30);
        }
        uacalc.getIdempotentCB().setSelected(op.isIdempotentSet());
        setDefaultEltComboBoxModel(op.getSetSize(), op.getDefaultValue());
      }
      
      //OperationInputTable opTable = 
      //          new OperationInputTable(op);
      //setOperationTable(opTable);
    }
    uacalc.validate();
    uacalc.repaint();
  }
  
  private void setDefaultEltComboBoxModel(final int setSize, int defaultValue) {
    String[] data = new String[setSize + 3];
    data[0] = "none";
    data[setSize + 1] = "random";
    data[setSize + 2] = "new random";
    for (int i = 0; i < setSize; i++) {
      data[i+1] = "" + i;
    }
    uacalc.getDefaultEltComboBox().setModel(new DefaultComboBoxModel(data));
    if (defaultValue == -2) {
      uacalc.getDefaultEltComboBox().setSelectedIndex(setSize + 1);
    }
    else {
      uacalc.getDefaultEltComboBox().setSelectedIndex(defaultValue + 1);
    }
  }
  
  public void defaultEltChangeHandler() {
    OperationWithDefaultValue op = getCurrentOperation();
    if (op == null) return;
    final int setSize = op.getSetSize();
    JComboBox box = uacalc.getDefaultEltComboBox();
    int index = box.getSelectedIndex();
    if (index == setSize + 2) {
      op.updateRandomValueTable();
      op.setDefaultValue(-2);
      box.setSelectedIndex(setSize + 1);
    }
    if (index == setSize + 1) {
      op.setDefaultValue(-2);
    }
    if (index > 0 && index <= setSize) { 
      op.setDefaultValue(index - 1);
    }
    if (index == 0) {
      op.setDefaultValue(-1);
    }
    if (!op.isTotal()) {
      uacalc.getMainController().getCurrentAlgebra().setNeedsSave(true);
      //uacalc.getActions().setDirty(true);
    }
    uacalc.repaint();
  }
  
  public void deleteOp() {
    if (opList == null) {
      JOptionPane.showMessageDialog(uacalc.getFrame(), "There is no algebra.");
      return;
    }
    if (opList.size() == 0) {
      JOptionPane.showMessageDialog(uacalc.getFrame(), "There is no operation to delete.");
      return;
    }
    int n = JOptionPane.showConfirmDialog(
        uacalc.getFrame(),
        "Delete this operation?",
        "Delete this operatin?",
        JOptionPane.YES_NO_OPTION);
    if (n == JOptionPane.YES_OPTION) {
      removeCurrentOperation();
      uacalc.getMainController().getCurrentAlgebra().setNeedsSave(true);
    }
  }
  
  public void addOp() {
    // TODO: fix
    //if (opTablePanel != null && !opTablePanel.stopCellEditing()) {
    //  uacalc.beep();
    //  return;
    //}
    if (opList == null) {  // algebra 
      uacalc.beep();
      return;
    }
    String name = getOpNameDialog();
    if (name == null) return;
    int arity = getArityDialog();
    if (arity == -1) return;
    addOperation(name, arity);
  }
  
  public void makeBasicAlg() {
    // TODO: fix
    String desc = "Editable copy of " + gAlg.toString(true);
    String name = gAlg.getAlgebra().getName();
    if (name == null || name.equals("")) name = "basic-copy";
    else name = name + "-basic";
    java.util.List<Operation> opList2 = new ArrayList<Operation>(opList.size());
    for (Operation op : opList) {
      opList2.add(new OperationWithDefaultValue(Operations.makeIntOperation(op)));
    }
    SmallAlgebra balg = new BasicAlgebra(name, algSize, opList2);
    balg.setDescription(desc);

    GUIAlgebra gAlg = getMainController().addAlgebra(balg);
    getMainController().setCurrentAlgebra(gAlg);
    //setOperationTable(new OperationInputTable());
    gAlg.setNeedsSave(true);
    //getActions().setDirty(true);
    getMainController().setCurrentFile(null);

  }
  
  private MainController getMainController() { return uacalc.getMainController(); }
  
  public Random getRandom() {
    return random;
  }
  
  public void setRandomSeed(long seed) {
    random.setSeed(seed);
  }
  
  
  public String updateDescription() {
    desc = uacalc.getDescTextField().getText();
    return desc;
  }

  private boolean validSymbol(OperationSymbol sym) {
    if (symbolList.contains(sym)) {
      uacalc.beep();
      JOptionPane.showMessageDialog(uacalc.getFrame(),
          "<html><center>There is already an operation with this symbol.<br>" 
          + "Choose another sybmol.<br>"
          + "</center></html>",
          "Duplicate Operation Symbol",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }
  
  private void addOperation(String name, int arity) {
    System.out.println("adding op " + name + ", arity " + arity);
    OperationSymbol sym = new OperationSymbol(name, arity);
    if (!validSymbol(sym)) return;
    OperationWithDefaultValue op = 
          new OperationWithDefaultValue(sym, algSize);
    opList.add(op);
    opMap.put(op.symbol(), op);
    symbolList.add(sym);
    opMap.put(sym, op);
    uacalc.getOpsComboBox().addItem(makeOpItem(sym));
    uacalc.getOpsComboBox().setSelectedIndex(opList.size() - 1);
    uacalc.repaint();
    final MainController mc = getMainController();
    mc.getPropertyChangeSupport().firePropertyChange(MainController.ALGEBRA_CHANGED, null, null);
  }
  
  /*
  public void addOperation(Operation oper) {
    OperationSymbol sym = oper.symbol();
    if (!validSymbol(sym)) return;
    OperationWithDefaultValue op = 
      new OperationWithDefaultValue(sym, algSize, oper.getTable(), -1);
    opList.add(op);
    symbolList.add(sym);
    opMap.put(sym, op);
    uacalc.getOpsComboBox().addItem(makeOpItem(sym));
    uacalc.getOpsComboBox().setSelectedIndex(opList.size() - 1);
    uacalc.repaint();
  }
  */
  
  public OperationSymbol getCurrentSymbol() {
    Object foo = uacalc.getOpsComboBox().getSelectedItem();
    if (!(foo instanceof OpSymItem)) return null;
    OpSymItem item = (OpSymItem)foo;
    if (item == null) return null;
    return item.getOperationSymbol();
  }
  
  public OperationWithDefaultValue getCurrentOperation() {
    return (OperationWithDefaultValue)opMap.get(getCurrentSymbol());
  }
  
  public void removeCurrentOperation() {
    //JComboBox opsComboBox = uacalc.getOpsComboBox();
    //System.out.println("selected index = " + opsComboBox.getSelectedIndex() + ", item count = " + opsComboBox.getItemCount());
    //opsComboBox.removeItemAt(opsComboBox.getSelectedIndex());
    OperationSymbol sym = getCurrentSymbol();
    if (sym == null) return;
    Operation op = getCurrentOperation();
    //System.out.println("opList = " + opList);
    opList.remove(op);
    symbolList.remove(sym);
    opMap.remove(sym);
    setOpsCB();
    // TODO: check this
    if (opList.size() == 0 && uacalc.getOpTable() != null) {
      uacalc.getOpTable().setModel(new javax.swing.table.DefaultTableModel(
          new Object [][] {
              {null, null, null, null},
              {null, null, null, null},
              {null, null, null, null},
              {null, null, null, null}
          },
          new String [] {"No", "Op", "Yet", ""}
      ));
    }
    uacalc.repaint();
    final MainController mc = getMainController();
    mc.getPropertyChangeSupport().firePropertyChange(MainController.ALGEBRA_CHANGED, null, null);
  }
  
  public void setOperationTable(OperationInputTable table) {
    // TODO: fix this
    //if (opTablePanel != null) main.remove(opTablePanel);
    //opTablePanel = table;
    //main.add(table, BorderLayout.CENTER);
  }
  
  private void resetOpsCB() {
    uacalc.getOpsComboBox().removeAllItems();
  }
  
  // to be called when the "New" button or menu item is hit.
  public void makeNewAlgebra() {
    // TODO: fix this
    //if (opTablePanel != null && !opTablePanel.stopCellEditing()) {
    //  uacalc.beep();
    //  return;
    //}
    setupNewAlgebra();
    uacalc.repaint();
  }
  
  // this is called when the check box is clicked.
  public void setIdempotent(boolean v) {
    OperationWithDefaultValue op = getCurrentOperation();
    if (op == null) return;
    op.setIdempotent(v);
    uacalc.repaint();
  }
    
  public void setAlgebra(GUIAlgebra gAlg) {
    this.gAlg = gAlg;
    SmallAlgebra alg = gAlg.getAlgebra();
    setCurrentAlgType(alg.algebraType());
    symbolList = new ArrayList<OperationSymbol>();
    opMap = new HashMap<OperationSymbol,Operation>();
    algSize = alg.cardinality();
    uacalc.getAlgNameTextField().setText(alg.getName());
    uacalc.getCardTextField().setText("" + alg.cardinality());
    uacalc.getDescTextField().setText(alg.getDescription());
    final JTable table = uacalc.getOpTable();
    table.setVisible(true); // this may not be necessary anymore
    if (alg.algebraType() == SmallAlgebra.AlgebraType.BASIC) {
      table.setEnabled(true);
      opList = alg.operations();
      opMap = alg.getOperationsMap();
      for (Operation op : opList) {
        symbolList.add(op.symbol());
        opMap.put(op.symbol(), op);
        uacalc.getAddOpButton().setEnabled(true);
        uacalc.getDelOpButton().setEnabled(true);
        uacalc.getMakeBasicAlgButton().setEnabled(false);
        setOpsCB();
      }
      return;
    }
    else {
      table.setEnabled(false);
      opList = new ArrayList<Operation>();
      for (Operation op : alg.operations()) {
        OperationWithDefaultValue opx = new OperationWithDefaultValue(op);
        opList.add(opx);
        opMap.put(opx.symbol(), opx);
        symbolList.add(opx.symbol());
      }
      uacalc.getAddOpButton().setEnabled(false);
      uacalc.getDelOpButton().setEnabled(false);
      uacalc.getMakeBasicAlgButton().setEnabled(true);
      setOpsCB();
      return;
    }
  }
  
  /**
   * A cute hack to get the toString method of an OperationSymbol to
   * put the arity in parentheses.
   *
   */
  public static interface OpSymItem {
    //public String toString();
    public OperationSymbol getOperationSymbol();
  }
  
  private OpSymItem makeOpItem(final OperationSymbol opSym) {
    OpSymItem item = new OpSymItem() {
      public OperationSymbol getOperationSymbol() {
        return opSym;
      }
      public String toString() {
        return opSym.name() + " (" + opSym.arity() + ")";
      }
    };
    return item;
  }
  
  private void setOpsCB() {
    uacalc.getOpsComboBox().removeAllItems();
    for (final OperationSymbol opSym : symbolList) {
      uacalc.getOpsComboBox().addItem(makeOpItem(opSym));
    }
  }
  
  private void setupNewAlgebra() {
    if (getMainController().isDirty() && !getMainController().checkSave()) return;
    String name = getAlgNameDialog();
    if (name == null) return;
    int card = getCardDialog();
    if (card > 0) {
      GUIAlgebra gAlg 
        = getMainController().addAlgebra(new BasicAlgebra(name, card, new ArrayList<Operation>()));
      getMainController().setCurrentAlgebra(gAlg);
      setOperationTable(new OperationInputTable());
      gAlg.setNeedsSave(true);
      //getActions().setDirty(true);
      getMainController().setCurrentFile(null);
    }
  }
  
  private String getOpNameDialog() {
    String name = JOptionPane.showInputDialog(uacalc.getFrame(), "Operation symbol? (with no spaces)");
    if (name == null) return null;
    if (name.length() == 0 || name.indexOf(" ") > 0) {
      uacalc.beep();
      JOptionPane.showMessageDialog(uacalc.getFrame(),
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      name = null; 
    }
    return name;
  }
  
  public int getArityDialog() {
    String arityStr = JOptionPane.showInputDialog(uacalc.getFrame(), "What is the arity?");
    if (arityStr == null) return -1;
    int arity = -1;
    boolean arityOk = true;
    try {
      arity = Integer.parseInt(arityStr);
    }
    catch (NumberFormatException e) {
      arityOk = false;
    }
    if (!arityOk || arity < 0) {
      JOptionPane.showMessageDialog(uacalc.getFrame(),
          "arity must be a nonnegative integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    return arity;
  }
  
  private String getAlgNameDialog() {
    String name = JOptionPane.showInputDialog(uacalc.getFrame(), "Short name (with no spaces) for the algebra?");
    if (name == null) return null;
    if (name.length() == 0 || name.indexOf(" ") > 0) {
      JOptionPane.showMessageDialog(uacalc.getFrame(),
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      uacalc.beep();
      name = null; 
    }
    return name;
  }
  
  public int getCardDialog() {
    String cardStr = JOptionPane.showInputDialog(uacalc.getFrame(), "What is the cardinality?");
    if (cardStr == null) return -1;
    int card = -1;
    boolean cardOk = true;
    try {
      card = Integer.parseInt(cardStr);
    }
    catch (NumberFormatException e) {
      cardOk = false;
    }
    if (!cardOk || card <= 0) {
      JOptionPane.showMessageDialog(uacalc.getFrame(),
          "cardinality must be a positive integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    this.algSize = card;
    return card;
    // set the card field and clear all else
  }

  private SmallAlgebra.AlgebraType getCurrentAlgType() {
    return currentAlgType;
  }

  private void setCurrentAlgType(SmallAlgebra.AlgebraType currentAlgType) {
    this.currentAlgType = currentAlgType;
  }
  
}

