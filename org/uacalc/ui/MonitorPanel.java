package org.uacalc.ui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import org.uacalc.util.Monitor;
import org.uacalc.ui.tm.TaskRunner;

public class MonitorPanel extends JPanel {
  
  private UACalculator uacalc;
  private Monitor monitor;
  private TaskRunner runner;
  
  private final JTextArea logArea;
  private final JTextField passField;
  private final JTextField sizeField;

  public MonitorPanel(UACalculator uacalc) {
    this.uacalc = uacalc;
    setLayout(new BorderLayout());
    logArea = new JTextArea(10, 50);
    logArea.setMargin(new Insets(5, 5, 5, 5));
    logArea.setEditable(false);
    logArea.setAutoscrolls(true);
    JLabel passLabel = new JLabel("Pass: ");
    JLabel sizeLabel = new JLabel("Size: ");
    passField = new JTextField(6);
    sizeField = new JTextField(12);
    JButton cancelButton  = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (runner != null && runner.getTask() != null) {
            System.out.println("cancelling ...");
            runner.cancel(true);
            monitor.setCancelled(true);
            monitor.printlnToLog("...cancelling...");
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
    topPanel.add(passLabel);
    topPanel.add(passField);
    topPanel.add(sizeLabel);
    topPanel.add(sizeField);
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
    monitor = new Monitor(this);
    //uacalc.setMonitor(monitor);
  }
  
  public JTextField getPassField() { return passField; }
  public JTextField getSizeField() { return sizeField; }
  public JTextArea getLogArea() { return logArea; }
  
  public Monitor getMonitor() { return monitor; }
  public void setMonitor(Monitor m) { monitor = m; }
  public TaskRunner getRunner() { return runner; }
  public void setRunner(TaskRunner tr) { runner = tr; }
  
  
}
