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

public class AlgebraEditorActions {

  private final UACalculatorUI uacalc;
  private String desc;
  //private SmallAlgebra alg;
  private int algSize;
  private java.util.List<OperationWithDefaultValue> opList;
  private java.util.List<OperationSymbol> symbolList;
  private java.util.Map<OperationSymbol,OperationWithDefaultValue> opMap;
  private final Random random = new Random();
  
  
  //private JPanel main;
  //private JToolBar toolBar;
  //private final JTextField name_tf = new JTextField(8);
  //private final JTextField card_tf = new JTextField(4);
  //private final JTextField desc_tf = new JTextField(18);
  //private final JComboBox ops_cb = new JComboBox();
  //private OperationInputTable opTablePanel;
  
  public AlgebraEditorActions(final UACalculatorUI uacalc) {
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
        repaint();
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
  
  public Random getRandom() {
    return random;
  }
  
  public void setRandomSeed(long seed) {
    random.setSeed(seed);
  }
  
  
  // TODO: move this
  //private void updateDescription() {
  //  desc = desc_tf.getText();
  //}

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
          new OperationWithDefaultValue(sym, algSize, getRandom());
    opList.add(op);
    symbolList.add(sym);
    opMap.put(sym, op);
    uacalc.opsComboBox.addItem(makeOpItem(sym));
    ops_cb.setSelectedIndex(opList.size() - 1);
    repaint();
  }
  
  public void addOperation(Operation oper) {
    OperationSymbol sym = oper.symbol();
    if (!validSymbol(sym)) return;
    OperationWithDefaultValue op = 
      new OperationWithDefaultValue(sym, algSize, oper.getTable(), 
                                             -1, getRandom());
    opList.add(op);
    symbolList.add(sym);
    opMap.put(sym, op);
    ops_cb.addItem(makeOpItem(sym));
    ops_cb.setSelectedIndex(opList.size() - 1);
    repaint();
  }
  
  public OperationSymbol getCurrentSymbol() {
    OpSymItem item = (OpSymItem)ops_cb.getSelectedItem();
    if (item == null) return null;
    return item.getOperationSymbol();
  }
  
  public OperationWithDefaultValue getCurrentOperation() {
    return opMap.get(getCurrentSymbol());
  }
  
  public void removeCurrentOperation() {
    ops_cb.remove(ops_cb.getSelectedIndex());
    OperationSymbol sym = getCurrentSymbol();
    if (sym == null) return;
    Operation op = getCurrentOperation();
    opList.remove(op);
    symbolList.remove(sym);
    opMap.remove(sym);
    setOpsCB();
    if (opList.size() == 0 && opTablePanel != null) main.remove(opTablePanel);
    uacalc.repaint();
  }
  
  public void setOperationTable(OperationInputTable table) {
    if (opTablePanel != null) main.remove(opTablePanel);
    opTablePanel = table;
    main.add(table, BorderLayout.CENTER);
  }
  
  private void resetOpsCB() {
    ops_cb.removeAllItems();
    //ops_cb.addItem("New Op");
  }
  
  private void makeToolBar() {
    toolBar = new JToolBar();
    ClassLoader cl = uacalc.getClass().getClassLoader();
    ImageIcon icon = new ImageIcon(cl.getResource(
                          "org/uacalc/ui/images/New16.gif"));
    JButton newAlgBut = new JButton("New", icon);
    toolBar.add(newAlgBut);
    newAlgBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // TODO save this alg !!!!!!
        if (opTablePanel != null && !opTablePanel.stopCellEditing()) {
          uacalc.beep();
          return;
        }
        //alg = null;
        setupNewAlgebra();
        repaint();
      }
    });
    JButton syncBut = new JButton("Sync");
    syncBut.setToolTipText("sync your changes with current algebra");
    toolBar.add(syncBut);
    syncBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sync();
      }
    });
  }
  
  public boolean sync() {
    if (!opTablePanel.stopCellEditing()) {
      uacalc.beep();
      return false;
    }
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
    uacalc.updateCurrentAlgebra(makeAlgebra());
    repaint();
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
    SmallAlgebra alg = new BasicAlgebra(name_tf.getText(), algSize, ops);
    updateDescription();
    alg.setDescription(desc);
    return alg;
  }
  
  public JToolBar getToolBar() {
    if (toolBar == null) makeToolBar();
    return toolBar; 
  }
  
  /*
  public SmallAlgebra getAlgebra() {
    return alg;
  }
  */
  
  public void setAlgebra(SmallAlgebra alg) {
    //this.alg = alg;
    //uacalc.setCurrentAlgebra(alg);
    algSize = alg.cardinality();
    java.util.List<Operation> ops = alg.operations();
    symbolList = new ArrayList<OperationSymbol>();
    opList = new ArrayList<OperationWithDefaultValue>();
    opMap = new HashMap<OperationSymbol,OperationWithDefaultValue>();
    for (Operation op : ops) {
      symbolList.add(op.symbol());
      OperationWithDefaultValue op2 = 
        new OperationWithDefaultValue(op, uacalc.getRandom());
      opList.add(op2);
      opMap.put(op.symbol(), op2);
    }
    name_tf.setText(alg.name());
    card_tf.setText("" + alg.cardinality());
    System.out.println("desc: " + alg.description());
    desc_tf.setText(alg.description());
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
    ops_cb.removeAllItems();
    for (final OperationSymbol opSym : symbolList) {
      ops_cb.addItem(makeOpItem(opSym));
    }
  }
  
  private void setupNewAlgebra() {
    if (uacalc.isDirty() && !uacalc.checkSave()) return;
    String name = getAlgNameDialog();
    if (name == null) return;
    int card = getCardDialog();
    if (card > 0) {
      uacalc.setCurrentAlgebra(new BasicAlgebra(name, card, new ArrayList<Operation>()));
      setOperationTable(new OperationInputTable());
      uacalc.setDirty(true);
      uacalc.setCurrentFile(null);
    }
  }
  
  private String getOpNameDialog() {
    String name = JOptionPane.showInputDialog(uacalc, "Operation symbol? (with no spaces)");
    if (name == null) return null;
    if (name.length() == 0 || name.indexOf(" ") > 0) {
      uacalc.beep();
      JOptionPane.showMessageDialog(this,
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
      JOptionPane.showMessageDialog(this,
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
      JOptionPane.showMessageDialog(this,
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      uacalc.beep();
      name = null; 
    }
    algName = name;
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
      JOptionPane.showMessageDialog(this,
          "cardinality must be a positive integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    this.card = card;
    return card;
    // set the card field and clear all else
  }
  
}

