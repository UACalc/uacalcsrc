package org.uacalc.ui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.*;
import org.uacalc.ui.tm.*;
import org.uacalc.ui.table.*;

public class MonitorPanel extends JPanel {
  
  private UACalculator uacalc;
  private ProgressReport monitor;
  //private TaskRunner runner;
  
  // the one that is currently displayed
  //BackgroundTask task;
  // a list of all
  //List<BackgroundTask> tasks = new ArrayList<BackgroundTask> ();
  
  private final JTextArea logArea;
  private final JTextField passField;
  private final JTextField sizeField;
  private final JTextField descField;
  private final JTextField passSizeField;
  private TaskTableModel model = new TaskTableModel(this);
  private final JTable taskTable = new JTable(model);

  public MonitorPanel(UACalculator uacalc) {
    this.uacalc = uacalc;
    setLayout(new BorderLayout());
    setupTaskTable();
    logArea = new JTextArea(10, 50);
    logArea.setMargin(new Insets(5, 5, 5, 5));
    logArea.setEditable(false);
    logArea.setAutoscrolls(true);
    JLabel passLabel = new JLabel("Pass: ");
    JLabel sizeLabel = new JLabel("Size: ");
    JLabel currentSizeLabel = new JLabel("Now: ");
    passField = new JTextField(6);
    sizeField = new JTextField(12);
    passSizeField = new JTextField(12);
    descField = new JTextField(30);
    JButton cancelButton  = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.out.println("getTask(): " + getTask());
          if (getTask() != null) {
            //System.out.println("cancelling ...");
            getTask().cancel(true);
            //runner.cancel(true);
            //monitor.setCancelled(true);
            //monitor.printlnToLog("...cancelling...");
          }
        }
      });
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          logArea.setText(null);
        }
      });
    
    JPanel topPanel = new JPanel();
    topPanel.add(new JScrollPane(taskTable, 
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    //topPanel.add(descField);
    //topPanel.add(passLabel);
    //topPanel.add(passField);
    //topPanel.add(sizeLabel);
    //topPanel.add(passSizeField);
    //topPanel.add(currentSizeLabel);
    //topPanel.add(sizeField);
    add(topPanel, BorderLayout.NORTH);
    add(new JScrollPane(logArea), BorderLayout.CENTER);
    JPanel botPanel = new JPanel();
    botPanel.setLayout(new BoxLayout(botPanel, BoxLayout.X_AXIS));
    botPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    botPanel.add(Box.createHorizontalGlue());
    botPanel.add(cancelButton);
    botPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    botPanel.add(clearButton);
    
    add(botPanel, BorderLayout.SOUTH);
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    monitor = new ProgressReport(this);
    //uacalc.setMonitor(monitor);
  }
  
  private void setupTaskTable() {
    taskTable.setRowSelectionAllowed(true);
    taskTable.setColumnSelectionAllowed(false);
    taskTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF);
    TableColumn column = null;
    for (int i = 0; i < model.getColumnCount(); i++) {
      column = taskTable.getColumnModel().getColumn(i);
      if (i == 0) {
        column.setPreferredWidth(150);
        column.setMinWidth(75);
      }
      else {
        column.setPreferredWidth(75);
        column.setMinWidth(30);
      }
    }
  }
  
  public JTextField getDescriptionField() { return descField; }
  public JTextField getPassField() { return passField; }
  public JTextField getPassSizeField() { return passSizeField; }
  public JTextField getSizeField() { return sizeField; }
  public JTextArea getLogArea() { return logArea; }
  
  
  public ProgressReport getProgressModel() { return monitor; }
  
  public void setProgressReport(ProgressReport m) {
    monitor = m;
    descField.setText(m.getDescription());
    passField.setText(String.valueOf(m.getPass()));
    passSizeField.setText(String.valueOf(m.getPassSize()));
    sizeField.setText(String.valueOf(m.getSize()));
    logArea.setText(null);
    for (String s : m.getLogLines()) {
      logArea.append(s);
    }
  }
  
  
  public BackgroundTask<?> getTask() { return model.getCurrentTask(); }
  public void setTask(BackgroundTask<?> v) { model.setCurrrentTask(v); }
  
  public void addTask(BackgroundTask<?> task) {
    addTask(task, true);
  }
  
  public void addTask(BackgroundTask<?> task, boolean makecurrent) {
    model.addTask(task);
    if (makecurrent) setTask(task);
  }
  
  public List<BackgroundTask<?>> getTasks() { return model.getTasks(); }
  
}
