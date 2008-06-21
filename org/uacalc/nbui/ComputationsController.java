package org.uacalc.nbui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import org.uacalc.alg.*;
import org.uacalc.terms.*;
import org.uacalc.ui.table.*;
import org.uacalc.ui.tm.*;
import org.uacalc.ui.util.*;

public class ComputationsController {
  
  private final UACalculatorUI uacalcUI;
  private TaskTableModel taskTableModel = new TaskTableModel();
  private java.util.List<TermTableModel> termTableModels = new ArrayList<TermTableModel>();
  
  public ComputationsController(UACalculatorUI uacalcUI) {
    this.uacalcUI = uacalcUI;
    setupTasksTable();
    setupResultTable();
  }

  private Actions getActions() { return uacalcUI.getActions(); }
  
  private void setupTasksTable() {
    final JTable tasksTable = uacalcUI.getComputationsTable();
    tasksTable.setModel(taskTableModel);
    TableColumn col = tasksTable.getColumnModel().getColumn(0);
    col.setPreferredWidth(40);
    col.setMinWidth(30);
    col = tasksTable.getColumnModel().getColumn(1);
    col.setPreferredWidth(300);
    col.setMinWidth(240);
    col = tasksTable.getColumnModel().getColumn(2);
    col.setPreferredWidth(50);
    col.setMinWidth(40);
    col = tasksTable.getColumnModel().getColumn(3);
    col.setPreferredWidth(100);
    col.setMinWidth(80);
    col = tasksTable.getColumnModel().getColumn(4);
    col.setPreferredWidth(100);
    col.setMinWidth(80);
    col = tasksTable.getColumnModel().getColumn(5);
    col.setPreferredWidth(120);
    col.setMinWidth(120);
    col = tasksTable.getColumnModel().getColumn(6);
    col.setPreferredWidth(120);
    col.setMinWidth(100);
    tasksTable.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int index = tasksTable.getSelectedRow();
        if (index >= 0) {
          taskTableModel.setCurrentTask(index);
          taskTableModel.fireTableDataChanged();
          uacalcUI.getLogTextArea().setText(null);
          ProgressReport report = taskTableModel.getCurrentTask().getProgressReport();
          for (String s : report.getLogLines()) {
            final String nl = "\n";
            uacalcUI.getLogTextArea().append(s + nl);
          }
          uacalcUI.getResultTable().setModel(termTableModels.get(index));
          setResultTableColWidths();
          uacalcUI.getResultTextField().setText(termTableModels.get(index).getDescription());
          uacalcUI.repaint();
        }
        // TODO: do something
      }
    });
  }  
  
  private void setupResultTable() {
    final JTable resultTable = uacalcUI.getResultTable();
    //resultTable.setModel(new TermTableModel());
    TableColumn col = resultTable.getColumnModel().getColumn(0);
    col.setPreferredWidth(60);
    col.setMinWidth(60);
    col = resultTable.getColumnModel().getColumn(1);
    col.setPreferredWidth(900);
    col.setMinWidth(480);
    //resultTable.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
    //  public void valueChanged(ListSelectionEvent e) {
    //    int index = resultTable.getSelectedRow();
    //    // TODO: do something
    //  }
    //});
  }
  
  /**
   * This should be called after the TermTableModel has been set.
   */
  private void setResultTableColWidths() {
    final JTable resultTable = uacalcUI.getResultTable();
    final int cols = resultTable.getColumnCount();
    for (int i = 0; i < cols; i++) {
      TableColumn col = resultTable.getColumnModel().getColumn(i);
      if (i == 0) col.setPreferredWidth(60);
      else if (i == 1) col.setPreferredWidth(900);
      else col.setPreferredWidth(40);
    }
  }
  
  // no sure I need this
  public BackgroundTask<?> getCurrentTask() {
    return taskTableModel.getCurrentTask();
  }
  
  public void cancelCurrentTask() {
    BackgroundTask<?> task = getCurrentTask();
    if (task != null) task.cancel(true);
  }
  
  private void addTask(BackgroundTask<?> task) {
    taskTableModel.addTask(task);
    uacalcUI.getLogTextArea().setText(null);
    // 2 is the computations tab index.
    final int comTabIndex = 2;
    if (uacalcUI.getTabbedPane().getSelectedIndex() != comTabIndex) {
      uacalcUI.getTabbedPane().setSelectedIndex(comTabIndex);
      uacalcUI.repaint();
    }
  }
  
  /**
   * This makes a new TermTableModel, add it to the list and displays it.
   * 
   * @param terms
   * @param vars
   * @return
   */
  private TermTableModel makeTermTableModel(java.util.List<Term> terms, 
                                            java.util.List<Variable> vars, int pos) {
    final int n = terms.size();
    Term[] tArr = new Term[n];
    for (int i = 0; i < n; i++) {
      tArr[i] = terms.get(i);
    }
    final int m = vars.size();
    Variable[] vArr = new Variable[m];
    for (int i = 0; i < m; i++) {
      vArr[i] = vars.get(i);
    }
    TermTableModel ttm = new TermTableModel(tArr, vArr);
    uacalcUI.getResultTable().setModel(ttm);
    uacalcUI.repaint();
    return ttm;
  }
  
  public void setupFreeAlgebraTask() {
    final GUIAlgebra gAlg = uacalcUI.getActions().getCurrentAlgebra();
    if (gAlg == null) {
      JOptionPane.showMessageDialog(uacalcUI,
          "<html>You must have an algebra loaded.<br>"
          + "Use the file menu or make a new one.</html>",
          "No algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int gens = getFreeGensDialog();
    if (!(gens > 0)) return;
    System.out.println("gens = " + gens);
    final boolean thin = getThinGens();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "F(" + gens + ") over " + alg.getName();
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<FreeAlgebra>  freeAlgTask = new BackgroundTask<FreeAlgebra>(report) {
      public FreeAlgebra compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.setDescription(desc);
        FreeAlgebra freeAlg = new FreeAlgebra(alg, gens, true, thin, report);
        return freeAlg;
      }
      public void onCompletion(FreeAlgebra fr, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient menory)");
          return;
        }
        if (!cancelled) {
          ttm.setTerms(fr.getTerms());
          ttm.setVariables(fr.getVariables());
          if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          uacalcUI.getResultTextField().setText(ttm.getDescription());
          uacalcUI.repaint();
        }
      }
    };
    addTask(freeAlgTask);
    Actions.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(freeAlgTask);
  }
  
  public int getFreeGensDialog() {
    String numGensStr = JOptionPane.showInputDialog(uacalcUI, 
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
      JOptionPane.showMessageDialog(uacalcUI,
          "<html>The number of generators must be positive.<br>"
          + "Try again.</html>",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    return gens;
  }
  
  private boolean getThinGens() {
    int thin =  JOptionPane.showConfirmDialog(uacalcUI, 
        "Eliminate some redundant projections", 
        "Coordinate thinning", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.QUESTION_MESSAGE);
    if (thin == JOptionPane.YES_OPTION || thin == JOptionPane.OK_OPTION) return true;
    return false;
  }
  
}
