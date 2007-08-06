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
import java.util.Random;
import org.uacalc.ui.UACalculator;
import org.uacalc.ui.table.OperationTableModel;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationWithDefaultValue;



public class OperationInputTable extends JPanel { 

  private JTable table;
  
  private UACalculator uacalc;

  private OperationTableModel tableModel;
  private int arity;
  private int setSize;
  private JComboBox defaultValueComboBox;
  private Random random;
  
  public OperationInputTable() {
    super();
  }

  //public OperationInputTable(Operation op) {
  //  this(op.arity(), op.getSetSize(), new OperationTableModel(new OperationWithDefaultValue(op)));
  //}
  
  public OperationInputTable(OperationWithDefaultValue op, UACalculator uacalc) {
    this(op.arity(), op.getSetSize(), new OperationTableModel(op,uacalc), uacalc);
  }
  
  public OperationInputTable(int arity, int setSize, UACalculator uacalc) {
    this(arity, setSize, null, uacalc);
  }
  
  public OperationInputTable(int arity, int setSize, 
                             OperationTableModel model, final UACalculator uacalc) {
    super();
    this.uacalc = uacalc;
    this.random = uacalc.getRandom();
    this.arity = arity;
    this.setSize = setSize;
    //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setLayout(new BorderLayout());
    if (model == null) model = new OperationTableModel(arity, setSize, false, -1, uacalc);
    tableModel = model;
    table = new JTable(tableModel);
    table.setPreferredScrollableViewportSize(
        new Dimension(Math.min(700, 30 * setSize), 
                      Math.min(600, 20 * model.getRowCount())));
//    table.setFillsViewportHeight(true);
    //table.getSelectionModel().addListSelectionListener(new RowListener());
    //table.getColumnModel().getSelectionModel().
      //addListSelectionListener(new ColumnListener());
    
    table.setDefaultEditor(Integer.class, 
             new IntegerEditor(0, tableModel.getOperation().getSetSize() - 1));
    table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    table.setCellSelectionEnabled(true);
    table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF);
    TableColumn column = null;
    for (int i = 0; i < tableModel.getColumnCount(); i++) {
      column = table.getColumnModel().getColumn(i);
      if (i == 0) {
        column.setPreferredWidth(100);
        column.setMinWidth(80);
      }
      else {
        column.setPreferredWidth(30);
        column.setMinWidth(30);
      }
    }
    JPanel mainPanel = new JPanel();
    add(mainPanel, BorderLayout.CENTER);
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
    mainPanel.add(Box.createHorizontalGlue());
    mainPanel.add(new JScrollPane(table, 
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    mainPanel.add(Box.createHorizontalGlue());

    defaultValueComboBox = makeDefaultValueBox(setSize);
    //System.out.println("max size is " + defaultValueComboBox.getMaximumSize());
    defaultValueComboBox.setPreferredSize(new Dimension(1, 1));
    defaultValueComboBox.setMaximumSize(new Dimension(10000, 8000));

    JPanel optionPanel = new JPanel();
    optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.X_AXIS));
    final JCheckBox idempotentCB = new JCheckBox("Idempotent");
    idempotentCB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (idempotentCB.isSelected()) {
          tableModel.setIdempotent(true);
          uacalc.setDirty(true);
          repaint();
        }
        else {
          tableModel.setIdempotent(false);
          uacalc.setDirty(true);
          repaint();
        }
      }
    });
    
    optionPanel.add(Box.createHorizontalGlue());
    optionPanel.add(idempotentCB);
    optionPanel.add(Box.createHorizontalGlue());
    optionPanel.add(new JLabel("Default element:  "));
    optionPanel.add(defaultValueComboBox);
    optionPanel.add(Box.createHorizontalGlue());


    //JTextField defaultValueTF = new JTextField(4);
    //optionPanel.add(defaultValueTF);
    add(optionPanel, BorderLayout.SOUTH);
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
  
  /**
   * This was hard to figure out: needed to stop the cell editor.
   * @return
   */
  public boolean stopCellEditing() {
    if (table == null) return true;
    return table.getDefaultEditor(tableModel.getColumnClass(1)).stopCellEditing();
  }

  private JComboBox makeDefaultValueBox(final int setSize) {
    String[] data = new String[setSize + 3];
    data[0] = "none";
    data[setSize + 1] = "random";
    data[setSize + 2] = "new random";
    for (int i = 0; i < setSize; i++) {
      data[i+1] = "" + i;
    }
    final JComboBox box = new JComboBox(data);
    box.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //JComboBox cb = (JComboBox)e.getSource();
        int index = box.getSelectedIndex();
        if (index == setSize + 2) {
          tableModel.getOperation().updateRandomValueTable();
          tableModel.setDefaultValue(-2);
          box.setSelectedIndex(setSize + 1);
          uacalc.setDirty(true);
          repaint();
        }
        if (index == setSize + 1) {
          tableModel.setDefaultValue(-2);
          uacalc.setDirty(true);
          repaint();
        }
        if (index > 0 && index <= setSize) { 
          tableModel.setDefaultValue(index - 1);
          uacalc.setDirty(true);
          repaint();
        }
        if (index == 0) {
          tableModel.setDefaultValue(-1);
          uacalc.setDirty(true);
          repaint();
        }
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
    OperationInputTable newContentPane = new OperationInputTable(3, 4, new UACalculator());
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

