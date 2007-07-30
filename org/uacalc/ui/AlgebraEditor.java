package org.uacalc.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.ui.table.*;

public class AlgebraEditor extends JPanel {

  private final UACalculator uacalc;
  private int card = -1;
  private String algName;
  private String desc;
  private SmallAlgebra alg;
  
  private JPanel main;
  private JToolBar toolBar;
  private final JTextField name_tf = new JTextField(8);
  private final JTextField card_tf = new JTextField(4);
  private final JTextField desc_tf = new JTextField(30);
  private final JComboBox ops_cb = new JComboBox();
  private OperationInputTable opTablePanel;
  
  public AlgebraEditor(final UACalculator uacalc) {
    this.uacalc = uacalc;
    setLayout(new BorderLayout());
    main = new JPanel();
    main.setLayout(new BorderLayout());
    //JPanel titlePanel = new JPanel();
    //titlePanel.add(new JLabel("Algebra Input"));
    //main.add(titlePanel, BorderLayout.NORTH);
    JPanel fieldsPanel = new JPanel();
    main.add(fieldsPanel, BorderLayout.NORTH);
    
    /*
    JButton newAlgBut = new JButton("New Algebra");
    fieldsPanel.add(newAlgBut);
    newAlgBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        alg = null;
        setupNewAlgebra();
      }
    });
    */
    
    fieldsPanel.add(new JLabel("Name:"));
    name_tf.setEditable(false);
    fieldsPanel.add(name_tf);
    
    fieldsPanel.add(new JLabel("Cardinality:"));
    card_tf.setEditable(false);
    fieldsPanel.add(card_tf);
    
    fieldsPanel.add(new JLabel("Description:"));
    desc_tf.setEditable(true);
    fieldsPanel.add(desc_tf);
    
    fieldsPanel.add(new JLabel("Operations"));
    resetOpsCB();
    fieldsPanel.add(ops_cb);
    ops_cb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OpSymItem item = (OpSymItem)ops_cb.getSelectedItem();
        if (item == null) return;
        OperationSymbol opSym = item.getOperationSymbol();
        Operation op = alg.getOperation(opSym);
        OperationInputTable opTable = new OperationInputTable(op);
        setOperationTable(opTable);
        validate();
        repaint();
      }
    });
    JButton delOpButton = new JButton("Del Op");
    JButton addOpButton = new JButton("Add Op");
    fieldsPanel.add(delOpButton);
    fieldsPanel.add(addOpButton);
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
        String name = getOpNameDialog();
        if (name == null) return;
        int arity = getArityDialog();
        if (arity == -1) return;
        addOperation(name, arity);
      }
    });
    
    add(main, BorderLayout.NORTH);
    validate();
  }
  
  private void addOperation(String name, int arity) {
    int n = 1, card = alg.cardinality();
    for (int k = 0; k < arity; k++) {
      n = n * card;
    }
    int[] values = new int[n];
    for (int i = 0; i < n; i++) {
      values[i] = -1;
    }
    Operation op = Operations.makeIntOperation(name, arity, card, values);
    java.util.List<Operation> ops = alg.operations();
    java.util.List<Operation> ops2 = new ArrayList<Operation>(ops.size());
    System.out.println("op arity = " + op.arity());
    System.out.println("ops size = " + ops.size());
    for (Operation f : ops) {
       ops2.add(f);
    }
    ops2.add(op);
    // when we get symbolic universes we need to change this !!!!!!!!!!!!!
    // we also need to save the original algebra for undo/redo ???????????
    SmallAlgebra alg2 = new BasicAlgebra(name_tf.getText(), alg.cardinality(), ops2);
    setAlgebra(alg2);
    repaint();
  }
  
  public Operation getCurrentOperation() {
    OpSymItem item = (OpSymItem)ops_cb.getSelectedItem();
    if (item == null) return null;
    OperationSymbol opSym = item.getOperationSymbol();
    return alg.getOperation(opSym);
  }
  
  public void removeCurrentOperation() {
    Operation op = getCurrentOperation();
    if (op == null) return;
    java.util.List<Operation> ops = alg.operations();
    java.util.List<Operation> ops2 = new ArrayList<Operation>();
    for (Operation f : ops) {
      if (!f.equals(op)) ops2.add(f);
    }
    // when we get symbolic universes we need to change this !!!!!!!!!!!!!
    // we also need to save the original algebra for undo/redo ???????????
    SmallAlgebra alg2 = new BasicAlgebra(name_tf.getText(), alg.cardinality(), ops2);
    setAlgebra(alg2);
    repaint();
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
        alg = null;
        setupNewAlgebra();
        repaint();
      }
    });
    JButton syncBut = new JButton("Sync");
    syncBut.setToolTipText("sync your changes with current algebra");
    toolBar.add(syncBut);
    syncBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (opTablePanel.stopCellEditing()) {
          uacalc.setAlgebra(alg);
          repaint();
        }
        else uacalc.beep();
      }
    });
  }
  
  public JToolBar getToolBar() {
    if (toolBar == null) makeToolBar();
    return toolBar; 
  }
  
  public SmallAlgebra getAlgebra() {
    return alg;
  }
  
  public void setAlgebra(SmallAlgebra alg) {
    this.alg = alg;
    setAlgebra();
  }
  
  public void setAlgebra() {
    SmallAlgebra alg = getAlgebra();
    if (alg == null) return;
    name_tf.setText(alg.name());
    card_tf.setText("" + alg.cardinality());
    desc_tf.setText(alg.description());
    setOpsCB();
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
  
  private void setOpsCB() {
    ops_cb.removeAllItems();
    java.util.List<OperationSymbol> opSyms 
               = getAlgebra().similarityType().getOperationSymbols();
    for (final OperationSymbol opSym : opSyms) {
      OpSymItem item = new OpSymItem() {
        public OperationSymbol getOperationSymbol() {
          return opSym;
        }
        public String toString() {
          return opSym.name() + " (" + opSym.arity() + ")";
        }
      };
      ops_cb.addItem(item);
    }
  }
  
  private void setupNewAlgebra() {
    String name = getAlgNameDialog();
    if (name == null) return;
    int card = getCardDialog();
    if (card > 0) {
      setAlgebra(new BasicAlgebra(name, card, new ArrayList<Operation>()));
      setOperationTable(new OperationInputTable());
    }
  }
  
  private String getOpNameDialog() {
    String name = JOptionPane.showInputDialog(uacalc, "Operation symbol? (with no spaces)");
    if (name == null || name.length() == 0 || name.indexOf(" ") > 0) {
      JOptionPane.showMessageDialog(this,
          "name required, and no spaces",
          "Name format error",
          JOptionPane.ERROR_MESSAGE);
      uacalc.beep();
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
    if (!arityOk || arity <= 0) {
      JOptionPane.showMessageDialog(this,
          "cardinality must be a nonnegative integer",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    return arity;
  }
  
  private String getAlgNameDialog() {
    String name = JOptionPane.showInputDialog(uacalc, "Short name (with no spaces) for the algebra?");
    if (name == null || name.length() == 0 || name.indexOf(" ") > 0) {
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
