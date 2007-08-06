package org.uacalc.ui.tm;

import javax.swing.SwingWorker;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import java.awt.event.*;
import org.uacalc.alg.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.util.Monitor;

import java.util.List;
import java.util.ArrayList;


public class TaskRunner<T> extends SwingWorker<T, String> {
  
  Task<T> task;
  
  public TaskRunner(Task<T> task) {
    this.task = task;
  }
  
  public T doInBackground() {
    return task.doIt();
  }
  
  public void done() {
    Toolkit.getDefaultToolkit().beep();
    try {
      if (!isCancelled()) System.out.println("ans: " + get());
    }
    catch (Exception e) { e.printStackTrace(); }
    //startButton.setEnabled(true);
    //setCursor(null); //turn off the wait cursor
    //output.append("Done!\n");
  }

  
  public static void main(String[] args) {
    final JTextArea output = new JTextArea(10,50);
    output.setMargin(new Insets(5,5,5,5));
    output.setEditable(false);
    List<Operation> ops = new ArrayList<Operation>();
    Operation meet = new AbstractOperation("meet", 2, 2) {

      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }

      // ordinary meet
      public int intValueAt(int[] args) {
        return Math.min(args[0], args[1]);
      }
    };
    ops.add(meet);
    final SmallAlgebra semilat = new BasicAlgebra("semilat", 2, ops);
    final Monitor monitor = new Monitor(output);
    final Task<Integer> task = new Task<Integer>() {
      public Integer doIt() {
        FreeAlgebra freeSemilattice = new FreeAlgebra(semilat, 5);
        return freeSemilattice.con().cardinality(monitor);
      }
    };
    final TaskRunner<Integer> runner = 
            new TaskRunner<Integer>(task);
    final Integer ans;
    JPanel panel = new JPanel(new BorderLayout());
    JButton startButton = new JButton("Start");
    startButton.setActionCommand("start");
    startButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          runner.execute();
          output.append("Done!\n");
          output.append("Answer: ");
          //try {
          //  output.append(runner.get().toString());
          //  output.append("\n\n\n");
          //}
          //catch (Exception ex) {ex.printStackTrace(); }
        }
      });  
    
    JButton cancelButton  = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (task != null) {
            System.out.println("cancelling ...");
            runner.cancel(true);
            monitor.setCancelled(true);
          }
        }
      });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(startButton);
    buttonPanel.add(cancelButton);
    //buttonPanel.add(progressBar);

    panel.add(buttonPanel, BorderLayout.PAGE_START);
    panel.add(new JScrollPane(output), BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JFrame frame = new JFrame("Task Runner Demo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.setContentPane(panel);

    //Display the window.
    frame.pack();
    frame.setVisible(true);


  }
  
}
