package org.uacalc.ui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import org.uacalc.util.Monitor;
import org.uacalc.ui.tm.TaskRunner;

public class MonitorPanel extends JPanel {
  
  private Monitor monitor;
  private TaskRunner runner;

  public MonitorPanel() {
    setLayout(new BorderLayout());
    final JTextArea output = new JTextArea(10, 50);
    output.setMargin(new Insets(5, 5, 5, 5));
    output.setEditable(false);
    output.setAutoscrolls(true);
    final JTextField passField = new JTextField(6);
    final JTextField sizeField = new JTextField(12);
    JButton cancelButton  = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (runner != null && runner.getTask() != null) {
            System.out.println("cancelling ...");
            runner.cancel(true);
            monitor.setCancelled(true);
          }
        }
      });
  }
  
}
