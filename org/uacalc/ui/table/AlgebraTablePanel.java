package org.uacalc.ui.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.*;

public class AlgebraTablePanel extends JPanel {
  
  private JTable table;
  AlgebraTableModel algTableModel;
  
  public AlgebraTablePanel() {
    algTableModel = new AlgebraTableModel();
    table = new JTable(algTableModel);
    setLayout(new BorderLayout());
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);
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
  }
  
  public AlgebraTableModel getAlgebraTableModel() { return algTableModel; }
  
  public void scrollToBottom() {
    int ht = table.getHeight();
    table.scrollRectToVisible(new Rectangle(0, ht, 0, ht));
  }

}
