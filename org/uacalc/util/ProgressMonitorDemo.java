
package org.uacalc.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 
import java.io.*; 

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.io.*;

public class ProgressMonitorDemo extends JFrame {
    public final static int ONE_SECOND = 1000;

    // for now
    private SmallAlgebra alg;

    private ProgressMonitor progressMonitor;
    private Timer timer;
    private JButton startButton;
    //private LongTask task;
    private Task task;
    private JTextArea taskOutput;
    private String newline = "\n";

    public ProgressMonitorDemo(SmallAlgebra alg) {
        super("ProgressMonitorDemo");
        this.alg = alg;

        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(new ButtonListener());

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(startButton, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        //Create a timer.
        //timer = new Timer(ONE_SECOND, new TimerListener());
        timer = new Timer(ONE_SECOND / 10, new TimerListener());
    }

    // these will eventually get the real alg of the program
    public SmallAlgebra getAlgebra() { return alg; }
    public CongruenceLattice getCongruenceLattice() {
      return (CongruenceLattice)alg.con();
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (progressMonitor.isCanceled() || task.done()) {
                progressMonitor.close();
                task.stop();
                Toolkit.getDefaultToolkit().beep();
                timer.stop();
                if (task.done()) {
                    taskOutput.append("Task completed." + newline);
                }
                startButton.setEnabled(true);
            } else {
                //progressMonitor.setNote(task.getMessage());
                progressMonitor.setNote(
                        "so far: " + task.amountComputed()
                        + ", ji's left: " + task.leftToDo());
                //progressMonitor.setProgress(task.getCurrent());
                progressMonitor.setProgress(task.percentDone());
                //taskOutput.append(task.getMessage() + newline);
                taskOutput.append("" + task.amountComputed() + newline);
                taskOutput.setCaretPosition(
                    taskOutput.getDocument().getLength());
            }
        }
    }

    /**
     * The actionPerformed method in this class
     * is called when the user presses the start button.
     */
    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
/*
            progressMonitor = new ProgressMonitor(ProgressMonitorDemo.this,
                                      "Running a Long Task",
                                      "", 0, task.getLengthOfTask());
*/
            task = getCongruenceLattice().getUniverseTask();
            if (task == null) return;
            progressMonitor = new ProgressMonitor(ProgressMonitorDemo.this,
                                      "Running a Long Task",
                                      "", 0, 100);
            progressMonitor.setProgress(0);
            //progressMonitor.setMillisToDecideToPopup(2 * ONE_SECOND);
            progressMonitor.setMillisToDecideToPopup(0);
            startButton.setEnabled(false);
            task.go();
            timer.start();
        }
    }
    
    public static void main(String[] args) 
                            throws IOException, BadAlgebraFileException {
        //SmallAlgebra alg = AlgebraIO.readAlgebraFile(args[0]);
        SmallAlgebra alg = AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/test2.ua");
        //Task task = ((CongruenceLattice)alg.con()).getUniverseTask();
        //JFrame frame = new ProgressMonitorDemo(task);
        JFrame frame = new ProgressMonitorDemo(alg);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}

