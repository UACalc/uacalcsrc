package org.uacalc.ui.table;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import org.uacalc.ui.table.OperationTableModel;


public class OperationInputTable extends JPanel { 

  private JTable table;

  private JCheckBox rowCheck;
  private JCheckBox columnCheck;
  private JCheckBox cellCheck;
  private ButtonGroup buttonGroup;
  private JTextArea output;

  private OperationTableModel tableModel;
  private int arity;
  private int setSize;
  private JComboBox defaultValueComboBox;

  public OperationInputTable(int arity, int setSize) {
    super();
    this.arity = arity;
    this.setSize = setSize;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    tableModel = new OperationTableModel(arity, setSize, false, -1);
    table = new JTable(tableModel);
    table.setPreferredScrollableViewportSize(new Dimension(500, 200));
//    table.setFillsViewportHeight(true);
    //table.getSelectionModel().addListSelectionListener(new RowListener());
    //table.getColumnModel().getSelectionModel().
      //addListSelectionListener(new ColumnListener());
    table.setDefaultEditor(Integer.class, 
             new IntegerEditor(0, tableModel.getOperation().getSetSize() - 1));
    table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    table.setCellSelectionEnabled(true);
    TableColumn column = null;
    for (int i = 0; i < tableModel.getColumnCount(); i++) {
      column = table.getColumnModel().getColumn(i);
      if (i == 0) column.setPreferredWidth(100);
      else column.setPreferredWidth(30);
    }
    add(new JScrollPane(table));


    defaultValueComboBox = makeDefaultValueBox(setSize);
    defaultValueComboBox.setPreferredSize(new Dimension(1, 1));

    JPanel optionPanel = new JPanel();
    optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.X_AXIS));
    JCheckBox idempotentCB = new JCheckBox("Idempotent");
    optionPanel.add(Box.createHorizontalGlue());
    optionPanel.add(idempotentCB);
    optionPanel.add(Box.createHorizontalGlue());
    optionPanel.add(new JLabel("Default element:  "));
    optionPanel.add(defaultValueComboBox);
    optionPanel.add(Box.createHorizontalGlue());


    //JTextField defaultValueTF = new JTextField(4);
    //optionPanel.add(defaultValueTF);
    add(optionPanel);
/*
    add(new JLabel("Selection Mode"));
    buttonGroup = new ButtonGroup();
    addRadio("Multiple Interval Selection").setSelected(true);
    addRadio("Single Selection");
    addRadio("Single Interval Selection");

    add(new JLabel("Selection Options"));
    rowCheck = addCheckBox("Row Selection");
    rowCheck.setSelected(true);
    columnCheck = addCheckBox("Column Selection");
    cellCheck = addCheckBox("Cell Selection");
    cellCheck.setEnabled(false);

    output = new JTextArea(5, 40);
    output.setEditable(false);
    add(new JScrollPane(output));
*/
  }

  private JComboBox makeDefaultValueBox(final int setSize) {
    String[] data = new String[setSize + 1];
    data[0] = "none";
    for (int i = 0; i < setSize; i++) {
      data[i+1] = "" + i;
    }
    final JComboBox box = new JComboBox(data);
    box.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //JComboBox cb = (JComboBox)e.getSource();
        int index = box.getSelectedIndex();
        System.out.println("selected index = " + index);
        if (index > 0 && index <= setSize) 
               System.out.println("default value = " + (index - 1));
      }
    });
    return box;
  }

/*
  private JCheckBox addCheckBox(String text) {
    JCheckBox checkBox = new JCheckBox(text);
    checkBox.addActionListener(this);
    add(checkBox);
    return checkBox;
  }

  private JRadioButton addRadio(String text) {
    JRadioButton b = new JRadioButton(text);
    b.addActionListener(this);
    buttonGroup.add(b);
    add(b);
    return b;
  }

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }
*/

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
    //Disable boldface controls.
    //UIManager.put("swing.boldMetal", Boolean.FALSE); 

    //Create and set up the window.
    JFrame frame = new JFrame("OperationTableDemo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    OperationInputTable newContentPane = new OperationInputTable(3, 4);
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}

