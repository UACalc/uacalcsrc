package org.uacalc.nbui;

import javax.swing.*;
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

  private final UACalculatorUI uacalc;
  private String desc;
  //private SmallAlgebra alg;
  private int algSize;
  private java.util.List<OperationWithDefaultValue> opList;
  private java.util.List<OperationSymbol> symbolList;
  private java.util.Map<OperationSymbol,OperationWithDefaultValue> opMap 
       = new HashMap<OperationSymbol,OperationWithDefaultValue>();
  private final Random random = RandomGenerator.getRandom();
  
  
  //private JPanel main;
  //private JToolBar toolBar;
  //private final JTextField name_tf = new JTextField(8);
  //private final JTextField card_tf = new JTextField(4);
  //private final JTextField desc_tf = new JTextField(18);
  //private final JComboBox ops_cb = new JComboBox();
  //private OperationInputTable opTablePanel;
  
  public AlgebraEditorController(final UACalculatorUI uacalc) {
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
    OperationWithDefaultValue op = opMap.get(opSym);
    // TODO: change this
    if (op != null) {
      javax.swing.table.TableModel model = new OperationTableModel(op);
      uacalc.getOpTable().setModel(model);
      
      //OperationInputTable opTable = 
      //          new OperationInputTable(op);
      //setOperationTable(opTable);
    }
    uacalc.validate();
    uacalc.repaint();
  }
  
  public void deleteOp() {
    int n = JOptionPane.showConfirmDialog(
        uacalc,
        "Delete this operation?",
        "Delete this operatin?",
        JOptionPane.YES_NO_OPTION);
    if (n == JOptionPane.YES_OPTION) {
      removeCurrentOperation();
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
  
  private Actions getActions() { return uacalc.getActions(); }
  
  public Random getRandom() {
    return random;
  }
  
  public void setRandomSeed(long seed) {
    random.setSeed(seed);
  }
  
  
  private void updateDescription() {
    desc = uacalc.getDescTextField().getText();
  }

  private boolean validSymbol(OperationSymbol sym) {
    if (symbolList.contains(sym)) {
      uacalc.beep();
      JOptionPane.showMessageDialog(uacalc,
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
    OperationSymbol sym = new OperationSymbol(name, arity);
    if (!validSymbol(sym)) return;
    OperationWithDefaultValue op = 
          new OperationWithDefaultValue(sym, algSize);
    opList.add(op);
    symbolList.add(sym);
    opMap.put(sym, op);
    uacalc.getOpsComboBox().addItem(makeOpItem(sym));
    uacalc.getOpsComboBox().setSelectedIndex(opList.size() - 1);
    uacalc.repaint();
  }
  
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
  
  public OperationSymbol getCurrentSymbol() {
    OpSymItem item = (OpSymItem)uacalc.getOpsComboBox().getSelectedItem();
    if (item == null) return null;
    return item.getOperationSymbol();
  }
  
  public OperationWithDefaultValue getCurrentOperation() {
    return opMap.get(getCurrentSymbol());
  }
  
  public void removeCurrentOperation() {
    uacalc.getOpsComboBox().remove(uacalc.getOpsComboBox().getSelectedIndex());
    OperationSymbol sym = getCurrentSymbol();
    if (sym == null) return;
    Operation op = getCurrentOperation();
    System.out.println("opList = " + opList);
    opList.remove(op);
    symbolList.remove(sym);
    opMap.remove(sym);
    setOpsCB();
    // TODO: check this
    //if (opList.size() == 0 && opTablePanel != null) main.remove(opTablePanel);
    uacalc.repaint();
  }
  
  public void setOperationTable(OperationInputTable table) {
    // TODO: fix this
    //if (opTablePanel != null) main.remove(opTablePanel);
    //opTablePanel = table;
    //main.add(table, BorderLayout.CENTER);
  }
  
  private void resetOpsCB() {
    uacalc.getOpsComboBox().removeAllItems();
    //uacalc.getOpsComboBox().addItem("New Op");
  }
  
  // to be called when the "New" botton or menu item is hit.
  public void makeNewAlgebra() {
    // TODO: fix this
    //if (opTablePanel != null && !opTablePanel.stopCellEditing()) {
    //  uacalc.beep();
    //  return;
    //}
    setupNewAlgebra();
    uacalc.repaint();
  }
  
  // TODO: delete this soon
  private void makeToolBar() {
    //toolBar = new JToolBar();
    ClassLoader cl = uacalc.getClass().getClassLoader();
    ImageIcon icon = new ImageIcon(cl.getResource(
                          "org/uacalc/ui/images/New16.gif"));
    JButton newAlgBut = new JButton("New", icon);
    //toolBar.add(newAlgBut);
    newAlgBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // TODO save this alg !!!!!!
        //if (opTablePanel != null && !opTablePanel.stopCellEditing()) {
        //  uacalc.beep();
        //  return;
        //}
        //alg = null;
        setupNewAlgebra();
        uacalc.repaint();
      }
    });
    JButton syncBut = new JButton("Sync");
    syncBut.setToolTipText("sync your changes with current algebra");
    //toolBar.add(syncBut);
    syncBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sync();
      }
    });
  }
  
  public boolean sync() {
    // TODO: fix this
    //if (!opTablePanel.stopCellEditing()) {
    //  uacalc.beep();
    //  return false;
    //}
    SmallAlgebra alg = makeAlgebra();
    if (alg == null) {
      uacalc.beep();
      JOptionPane.showMessageDialog(uacalc,
          "<html><center>Not all operations are total.<br>" 
          + "Fill in the tables<br>"
          + "or set a default value.</center></html>",
          "Incomplete operation(s)",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    getActions().updateCurrentAlgebra(makeAlgebra());
    uacalc.repaint();
    return true;
  }
  
  /**
   * Make an algebra from the operations.
   * 
   * @return
   */
  public SmallAlgebra makeAlgebra() {
    java.util.List<Operation> ops = new ArrayList<Operation>(opList.size());
    for (OperationWithDefaultValue op : opList) {
      if (op.isTotal()) ops.add(op.makeOrdinaryOperation());
      else return null;
    }
    SmallAlgebra alg = new BasicAlgebra(uacalc.getAlgNameTextField().getText(), algSize, ops);
    updateDescription();
    alg.setDescription(desc);
    return alg;
  }
  
  //public JToolBar getToolBar() {
  //  if (toolBar == null) makeToolBar();
  //  return toolBar; 
  //}
  
  /*
  public SmallAlgebra getAlgebra() {
    return alg;
  }
  */
  
  public void setAlgebra(SmallAlgebra alg) {
    System.out.println("setAlgebra called");
    //this.alg = alg;
    //uacalc.setCurrentAlgebra(alg);
    algSize = alg.cardinality();
    java.util.List<Operation> ops = alg.operations();
    symbolList = new ArrayList<OperationSymbol>();
    opList = new ArrayList<OperationWithDefaultValue>();
    System.out.println("from set alg, opList = " +opList);
    opMap = new HashMap<OperationSymbol,OperationWithDefaultValue>();
    for (Operation op : ops) {
      symbolList.add(op.symbol());
      OperationWithDefaultValue op2 = 
        new OperationWithDefaultValue(op);
      opList.add(op2);
      opMap.put(op.symbol(), op2);
    }
    uacalc.getAlgNameTextField().setText(alg.name());
    uacalc.getCardTextField().setText("" + alg.cardinality());
    System.out.println("desc: " + alg.description());
    uacalc.getDescTextField().setText(alg.description());
    if (alg instanceof BasicAlgebra) setOpsCB();
  }
  
  /*
  public void setAlgebra() {
    SmallAlgebra alg = getAlgebra();
    if (alg == null) return;
    name_tf.setText(alg.name());
    card_tf.setText("" + alg.cardinality());
    desc_tf.setText(alg.description());
    setOpsCB();
  }
  */
  
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
    if (getActions().isDirty() && !getActions().checkSave()) return;
    String name = getAlgNameDialog();
    if (name == null) return;
    int card = getCardDialog();
    if (card > 0) {
      getActions().setCurrentAlgebra(new BasicAlgebra(name, card, new ArrayList<Operation>()));
      setOperationTable(new OperationInputTable());
      getActions().setDirty(true);
      getActions().setCurrentFile(null);
    }
  }
  
  private String getOpNameDialog() {
    String name = JOptionPane.showInputDialog(uacalc, "Operation symbol? (with no spaces)");
    if (name == null) return null;
    if (name.length() == 0 || name.indexOf(" ") > 0) {
      uacalc.beep();
      JOptionPane.showMessageDialog(uacalc,
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      name = null; 
    }
    return name;
  }
  
  public int getArityDialog() {
    String arityStr = JOptionPane.showInputDialog(uacalc, "What is the arity?");
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
      JOptionPane.showMessageDialog(uacalc,
          "arity must be a nonnegative integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    return arity;
  }
  
  private String getAlgNameDialog() {
    String name = JOptionPane.showInputDialog(uacalc, "Short name (with no spaces) for the algebra?");
    if (name == null) return null;
    if (name.length() == 0 || name.indexOf(" ") > 0) {
      JOptionPane.showMessageDialog(uacalc,
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      uacalc.beep();
      name = null; 
    }
    return name;
  }
  
  public int getCardDialog() {
    String cardStr = JOptionPane.showInputDialog(uacalc, "What is the cardinality?");
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
      JOptionPane.showMessageDialog(uacalc,
          "cardinality must be a positive integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    this.algSize = card;
    return card;
    // set the card field and clear all else
  }
  
}

