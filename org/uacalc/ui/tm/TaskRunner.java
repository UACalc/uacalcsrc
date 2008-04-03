package org.uacalc.ui.tm;

import javax.swing.SwingWorker;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import java.awt.event.*;
import org.uacalc.alg.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.util.ProgressMonitor;
import org.uacalc.ui.MonitorPanel;
import org.uacalc.alg.conlat.CongruenceLattice;

import java.util.List;
import java.util.ArrayList;


public class TaskRunner<T> extends SwingWorker<T, DataChunk> {
  
  Task<T> task;
  T ans = null;
  MonitorPanel monitorPanel;
  static final int memReserve = 1048576;
  
  /**
   * This constructor is only used for testing.
   * @param task
   */
  public TaskRunner(Task<T> task) {
    this.task = task;
  }
  
  public TaskRunner(Task<T> task, MonitorPanel mp) {
    this(task);
    this.monitorPanel = mp;
    mp.getMonitor().reset();
    mp.setRunner(this);
  }
  
  public Task<T> getTask() { return task; }
  
  public T doInBackground() {
    byte[] buf = new byte[memReserve];
    try {
      return task.doIt();
    }
    catch (OutOfMemoryError e) {
      buf = null;
      buf = new byte[1];
      System.out.println("Out of Memory");
      cancel(true);
      monitorPanel.getMonitor().reset();
      return null;
    }
    catch (CancelledException e) {
      System.out.println("caught cancellation");
      cancel(true);
      Toolkit.getDefaultToolkit().beep();
      Toolkit.getDefaultToolkit().beep();
      Toolkit.getDefaultToolkit().beep();
      monitorPanel.getMonitor().reset();
      return null;
    }
  }
  
  public void done() {
    Toolkit.getDefaultToolkit().beep();
    if (isCancelled()) {
      monitorPanel.getMonitor().reset();
      //cancel(false);
      return;
    }
    try {
      ans = get();
      System.out.println("isCancelled() = " + isCancelled());
      if (!isCancelled()) System.out.println("ans: " + ans);
    }
    catch (Exception e) { e.printStackTrace(); }
    //startButton.setEnabled(true);
    //setCursor(null); //turn off the wait cursor
    //output.append("Done!\n");
  }
  
  public T getAnswer() { return ans; }
  
  @Override
  protected void process(List<DataChunk> chunkList) {
    //System.out.println("chunkList =" + chunkList);
    for(DataChunk data: chunkList) {
      //if (isCancelled()) break;
      String msg = data.getMessage();
      switch (data.getDataType()) {
        case PASS:
          monitorPanel.getPassField().setText(msg);
          break;
        case SIZE:
          monitorPanel.getSizeField().setText(msg);
          break;
        default:
          JTextArea logArea = monitorPanel.getLogArea();
          logArea.append(msg);
          logArea.setCaretPosition(logArea.getDocument().getLength());
      }
    }      
  }
  
  public void publishx(DataChunk dc) {
    publish(dc);
  }


  
  public static void main(String[] args) {
    final JTextArea output = new JTextArea(10,50);
    output.setMargin(new Insets(5,5,5,5));
    output.setEditable(false);
    output.setAutoscrolls(true);
    final JTextField passField = new JTextField(6);
    final JTextField sizeField = new JTextField(12);
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
    
    List<Operation> ops2 = new ArrayList<Operation>();
    Operation join = new AbstractOperation("join", 2, 2) {
      
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }

      // ordinary meet
      public int intValueAt(int[] args) {
        return Math.max(args[0], args[1]);
      }
    };
    ops2.add(meet);
    ops2.add(join);
    final SmallAlgebra lat = new BasicAlgebra("lat", 2, ops2);
    
    final ProgressMonitor monitor = new ProgressMonitor(output, sizeField, passField);
    //GeneralAlgebra.setMonitor(monitor);
    //CongruenceLattice.setMonitor(monitor);
    final Task<Integer> task = new Task<Integer>() {
      public Integer doIt() {
        FreeAlgebra freeSemilattice = new FreeAlgebra(semilat, 5);
        freeSemilattice.con().typeSet();
        return freeSemilattice.con().cardinality();
      }
    };
    final Task<Integer> task2 = new Task<Integer>() {
      public Integer doIt() {
        FreeAlgebra freeDistLattice = new FreeAlgebra(lat, 5);
        return freeDistLattice.cardinality();
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
          //output.append("Done!\n");
          //output.append("Answer:\n");
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
    buttonPanel.add(passField);
    buttonPanel.add(sizeField);
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
