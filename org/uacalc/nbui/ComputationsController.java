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
        report.addStartLine("Computing the free algebra");
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
          report.addEndingLine("Done computing the free algebra");
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
  
  public void setupJonssonTermsTask() {
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
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding Jonsson terms for " + alg.getName();
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<java.util.List<Term>>  jonssonTermTask 
                                   = new BackgroundTask<java.util.List<Term>>(report) {
      public java.util.List<Term> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Finding Jonsson terms.");
        report.setDescription(desc);
        java.util.List<Term> terms = Malcev.jonssonTerms(alg, false, report);
        return terms;
      }
      public void onCompletion(java.util.List<Term> terms, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient menory)");
          return;
        }
        if (!cancelled) {
          if (terms == null) {
            report.addEndingLine("The variety is not congruence distributive.");
            ttm.setDescription(desc + ": there are none.");
            uacalcUI.getResultTextField().setText(ttm.getDescription());
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Done finding Jonsson terms.");
            ttm.setTerms(terms);
          }
          //ttm.setVariables(fr.getVariables());
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
    addTask(jonssonTermTask);
    Actions.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(jonssonTermTask);
  }
  
  public void setupGummTermsTask() {
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
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding Gumm terms for " + alg.getName();
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<java.util.List<Term>>  gummTermTask 
                                   = new BackgroundTask<java.util.List<Term>>(report) {
      public java.util.List<Term> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Finding Gumm modularity terms.");
        report.setDescription(desc);
        java.util.List<Term> terms = Malcev.gummTerms(alg, report);
        return terms;
      }
      public void onCompletion(java.util.List<Term> terms, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient menory)");
          return;
        }
        if (!cancelled) {
          if (terms == null) {
            report.addEndingLine("The variety is not congruence modular.");
            ttm.setDescription(desc + ": there are none.");
            uacalcUI.getResultTextField().setText(ttm.getDescription());
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Done finding Gumm terms.");
            ttm.setTerms(terms);
          }
          //ttm.setVariables(fr.getVariables());
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
    addTask(gummTermTask);
    Actions.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(gummTermTask);
  }
  
  public void setupHagemannMitschkeTermsTask() {
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
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding Hagemann-Mitschke terms for " + alg.getName();
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<java.util.List<Term>>  hmTermTask 
                                   = new BackgroundTask<java.util.List<Term>>(report) {
      public java.util.List<Term> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Finding Hagemann-Mitschke terms for k-permutability.");
        report.setDescription(desc);
        java.util.List<Term> terms = Malcev.hagemannMitschkeTerms(alg, report);
        return terms;
      }
      public void onCompletion(java.util.List<Term> terms, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient menory)");
          return;
        }
        if (!cancelled) {
          if (terms == null) {
            report.addEndingLine("The variety is not k-permutable.");
            ttm.setDescription(desc + ": there are none.");
            uacalcUI.getResultTextField().setText(ttm.getDescription());
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found Hagemann-Mitschke terms.");
            ttm.setTerms(terms);
          }
          //ttm.setVariables(fr.getVariables());
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
    addTask(hmTermTask);
    Actions.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(hmTermTask);
  }
  
  public void setupNUTermTask() {
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
    final int arity = getFreeGensDialog();
    if (!(arity > 2)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Near unanimity term over " + alg.getName();
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  nuTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Finding an NU term of arity " + arity);
        report.setDescription(desc);
        Term nu = Malcev.findNUF(alg, arity, report);
        return nu;
      }
      public void onCompletion(Term nu, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient menory)");
          return;
        }
        if (!cancelled) {
          if (nu == null) {
            report.addEndingLine("The variety has no NU term of arity " + arity);
            ttm.setDescription(desc + ": there is none.");
            uacalcUI.getResultTextField().setText(ttm.getDescription());
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found an NU term.");
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(nu);
            ttm.setTerms(terms);
          }
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
    addTask(nuTask);
    Actions.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(nuTask);
  }
  
}
