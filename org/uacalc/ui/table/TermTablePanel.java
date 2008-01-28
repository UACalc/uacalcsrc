package org.uacalc.ui.table;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import org.uacalc.ui.*;
import java.util.*;
import java.awt.BorderLayout;
import org.uacalc.terms.*;
import org.uacalc.alg.op.*;
import org.uacalc.alg.*;

public class TermTablePanel extends JPanel {
  
  private final UACalculator uacalc;
  private JTable table;
  private Term[] terms;
  private final List<Variable> variables;
  
  
  public TermTablePanel(final UACalculator uacalc, Term[] terms, 
                                          final List<Variable> variables) {
    this.uacalc = uacalc;
    this.terms = terms;
    this.variables = variables;
    TermTableModel model = new TermTableModel(terms);
    table = new JTable(model);
    setLayout(new BorderLayout());
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF);
    TableColumn column = null;
    for (int i = 0; i < model.getColumnCount(); i++) {
      column = table.getColumnModel().getColumn(i);
      if (i == 0) {
        column.setPreferredWidth(40);
        column.setMinWidth(30);
      }
      else {
        column.setPreferredWidth(Math.max(400, termsWidth(terms)));
        column.setMinWidth(200);
      }
    }
    JPanel mainPanel = new JPanel();
    add(mainPanel, BorderLayout.CENTER);
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
    mainPanel.add(Box.createHorizontalGlue());
    mainPanel.add(new JScrollPane(table, 
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    mainPanel.add(Box.createHorizontalGlue()); // not sure what this does
    JPanel optionPanel = new JPanel();
    optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.X_AXIS));
    add(optionPanel, BorderLayout.SOUTH);
    JButton addToOps = new JButton("Add to Ops");
    addToOps.setToolTipText("add selected term(s) as operations");
    addToOps.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
          SmallAlgebra alg = uacalc.getCurrentAlgebra();
          for (int i = 0; i < rows.length; i++) {
            Term term = (Term) table.getValueAt(rows[i], 1);
            // System.out.println("term = " + term);
            TermOperation tOp = new TermOperationImp(term, variables, alg);
            uacalc.getAlgebraEditor().addOperation(tOp);
            // System.out.println("termOp = " + tOp);
            // System.out.println("termOp.getTable() = " + tOp.getTable());
            // OperationWithDefaultValue op =
            // new OperationWithDefaultValue(tOp, uacalc.getRandom());
            // System.out.println("op = " + op);
            // System.out.println("op(0,1) = " + op.intValueAt(new int[]
            // {0,1}));
          }
        }
        else showNoSelectionDialog();
      }
    });
    JButton newAlg = new JButton("New Algebra");
    newAlg.setToolTipText("make a new algebra with the selected term(s)");
    newAlg.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
          List<Operation> ops = new ArrayList<Operation>(rows.length);
          SmallAlgebra oldAlg = uacalc.getCurrentAlgebra();
          for (int i = 0; i < rows.length; i++) {
            Term term = (Term) table.getValueAt(rows[i], 1);
            ops.add(new TermOperationImp(term, variables, oldAlg));
          }
          SmallAlgebra alg = new BasicAlgebra(oldAlg.name() + "-reduct", oldAlg.cardinality(), ops);
          uacalc.setCurrentAlgebra(alg);
        }
        else showNoSelectionDialog();
      }
    });
    optionPanel.add(Box.createHorizontalGlue());
    optionPanel.add(addToOps);
    optionPanel.add(Box.createHorizontalGlue());
    optionPanel.add(newAlg);
    optionPanel.add(Box.createHorizontalGlue());
  }
  
  public Term[] getTerms() {return terms; }
  
  private int termsWidth(final Term[] terms) {
    final int start = 10;
    int current = start;
    for (int i = 0; i < terms.length; i++) {
      current = Math.max(current, terms[i].toString().length());
    }
    return 8 * current;
  }
  
  private void showNoSelectionDialog() {
    JOptionPane.showMessageDialog(uacalc,
        "<html>Select one or more rows.<br>"
        + "The New Algebra buttom will make an algebra<br>"
        + "from these term operations.<br>" 
        + "The Add to Ops will add them to the current algebra. </html>",
        "No selected row",
        JOptionPane.WARNING_MESSAGE);
  }
  
}
