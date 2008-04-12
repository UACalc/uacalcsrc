package org.uacalc.ui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import org.uacalc.alg.*;
import org.uacalc.ui.tm.*;
import org.uacalc.ui.table.*;

public class ComputationsPanel extends JSplitPane {

  private final UACalculator uacalc;
  
  private JToolBar toolBar;
  private JPanel main;
  private MonitorPanel monitorPanel;
  private TermTablePanel termTablePanel;
  
  public ComputationsPanel(final UACalculator uacalc) {
    super(JSplitPane.VERTICAL_SPLIT);
    setOneTouchExpandable(true);
    Dimension minimumSize = new Dimension(100, 100);
    Dimension preferredSize = new Dimension(100, 400);
    
    this.uacalc = uacalc;
    //setLayout(new BorderLayout());
    main = new JPanel();
    main.setMinimumSize(minimumSize);
    main.setPreferredSize(preferredSize);
    main.setLayout(new BorderLayout());
    JPanel fieldsPanel = new JPanel();
    main.add(fieldsPanel, BorderLayout.NORTH);
    setTopComponent(main);
    //monitorPanel = new MonitorPanel(uacalc);
    monitorPanel = uacalc.getMonitorPanel();
    //add(monitorPanel, BorderLayout.SOUTH);
    setBottomComponent(monitorPanel);
    setDividerLocation(getSize().height - getInsets().bottom
        - getDividerSize() - 150);
    /*
    fieldsPanel.add(new JLabel("Name:"));
    name_tf.setEditable(false);
    fieldsPanel.add(name_tf);
    
    fieldsPanel.add(new JLabel("Cardinality:"));
    card_tf.setEditable(false);
    fieldsPanel.add(card_tf);
    
    fieldsPanel.add(new JLabel("Description:"));
    desc_tf.setEditable(true);
    fieldsPanel.add(desc_tf);
    
    fieldsPanel.add(new JLabel("Operations"));
    resetOpsCB();
    fieldsPanel.add(ops_cb);
    ops_cb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OpSymItem item = (OpSymItem)ops_cb.getSelectedItem();
        if (item == null) return;
        OperationSymbol opSym = item.getOperationSymbol();
        OperationWithDefaultValue op = opMap.get(opSym);
        if (op != null) {
          OperationInputTable opTable = 
                    new OperationInputTable(op, uacalc);
          setOperationTable(opTable);
        }
        validate();
        repaint();
      }
    });
    JButton delOpButton = new JButton("Del Op");
    JButton addOpButton = new JButton("Add Op");
    fieldsPanel.add(delOpButton);
    fieldsPanel.add(addOpButton);
    delOpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(
            uacalc,
            "Delete this operation?",
            "Delete this operatin?",
            JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
          removeCurrentOperation();
        }
      }
    });
    addOpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (opTablePanel != null && !opTablePanel.stopCellEditing()) {
          uacalc.beep();
          return;
        }
        if (opList == null) {  // algebra 
          uacalc.beep();
          return;
        }
        String name = getOpNameDialog();
        if (name == null) return;
        int arity = getArityDialog();
        if (arity == -1) return;
        addOperation(name, arity);
      }
    });
    */
    //add(main, BorderLayout.CENTER);
    validate();
  }
  
  public JToolBar getToolBar() {
    if (toolBar == null) makeToolBar();
    return toolBar;
  }
  
  private void makeToolBar() {
    toolBar = new JToolBar();
    //ClassLoader cl = uacalc.getClass().getClassLoader();
    //ImageIcon icon = new ImageIcon(cl.getResource(
    //                      "org/uacalc/ui/images/New16.gif"));
    JButton freeAlgBut = new JButton("Free Algebra");
    toolBar.add(freeAlgBut);
    freeAlgBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setupFreeAlgebraPanel();
        setDividerLocation(0.5);
        repaint();
      }
    });
  }
  
  public void setTermTablePanel(TermTablePanel ttp) {
    if (termTablePanel != null) main.remove(termTablePanel);
    termTablePanel = ttp;
    main.add(ttp);
    uacalc.validate();
    repaint();
  }
  
  private void setupFreeAlgebraPanel() {
    final SmallAlgebra alg = uacalc.getCurrentAlgebra();
    if (alg == null) {
      JOptionPane.showMessageDialog(this,
          "<html>You must have an algebra loaded.<br>"
          + "Use the file menu or make a new one.</html>",
          "No algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int gens = getFreeGensDialog();
    if (!(gens > 0)) return;
    System.out.println("gens = " + gens);
    final BackgroundTask<FreeAlgebra>  freeAlgTask = new BackgroundTask<FreeAlgebra>() {
      public FreeAlgebra compute() {
        monitorPanel.getMonitor().reset();
        monitorPanel.getMonitor().setDescFieldText("F(" + gens + ") over " + alg.name());
        FreeAlgebra freeAlg = new FreeAlgebra(uacalc.getCurrentAlgebra(), gens);
        return freeAlg;
      }
      public void onCompletion(FreeAlgebra fr, Throwable exception, boolean cancelled) {
        System.out.println("got to completion");
        System.out.println("thrown = " + exception);
        if (exception instanceof OutOfMemoryError) {
          monitorPanel.getMonitor().reset();
          monitorPanel.getMonitor().printlnToLog("Not enough memory");
          return;
        }
        if (!cancelled) {
          TermTablePanel ttp = 
            new TermTablePanel(uacalc, fr.getTerms(), fr.getVariables());
          setTermTablePanel(ttp);
        }
        else {
          monitorPanel.getMonitor().reset();
          monitorPanel.getMonitor().printlnToLog("computation cancelled");
        }
      }
    };
    monitorPanel.setTask(freeAlgTask);
    BackgroundExec.getBackgroundExec().execute(freeAlgTask);
    /*
    final Task<FreeAlgebra> freeAlgTask = new Task<FreeAlgebra>() {
      public FreeAlgebra doIt() {
        FreeAlgebra freeAlg = new FreeAlgebra(uacalc.getCurrentAlgebra(), gens);
        return freeAlg;
      }
    };
    final TaskRunner<FreeAlgebra> runner = 
            new TaskRunner<FreeAlgebra>(freeAlgTask, monitorPanel) {
        public void done() {
          System.out.println("got to done");
          try {
            if (!isCancelled()) {
              FreeAlgebra fr = get();
              System.out.println("fr = " + fr);
              TermTablePanel ttp = 
                new TermTablePanel(uacalc, fr.getTerms(), fr.getVariables());
              setTermTablePanel(ttp);
            }
            // the next line *is* reached!!
            else {
              System.out.println("done but cancelled");
              GuiExecutor.instance().execute(new Runnable() {
                public void run() {
                  monitorPanel.getMonitor().reset();
                  monitorPanel.getMonitor().printlnToLog("computation cancelled");
                }
              });
              //monitorPanel.getMonitor().reset();
              //cancel(false);
            }
          }
          catch (InterruptedException e) { e.printStackTrace(); }
          catch (ExecutionException e) { e.printStackTrace(); }
        }
    };
    runner.execute();
    */
    //runner.done();
    //FreeAlgebra free = runner.getAnswer();
    //System.out.println("free alg size = " + free.cardinality());
    
  }
  
  public int getFreeGensDialog() {
    String numGensStr = JOptionPane.showInputDialog(uacalc, 
                                            "Number of generators?", 
                                            "Free Algebra", 
                                            JOptionPane.QUESTION_MESSAGE);
    if (numGensStr == null) return -1;
    int gens = -1;
    boolean gensOk = true;
    try {
      gens = Integer.parseInt(numGensStr);
    }
    catch (NumberFormatException e) {
      gensOk = false;
    }
    if (!gensOk || gens <= 0) {
      JOptionPane.showMessageDialog(this,
          "<html>The number of generators must be positive.<br>"
          + "Try again.</html>",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    return gens;
  }
  
  
}
