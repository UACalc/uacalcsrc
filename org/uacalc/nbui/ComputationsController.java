package org.uacalc.nbui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
//import java.awt.*;
//import java.awt.event.*;
import java.util.*;
//import java.io.*;

import org.uacalc.alg.*;
import org.uacalc.alg.op.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.eq.*;
import org.uacalc.terms.*;
import org.uacalc.ui.table.*;
import org.uacalc.ui.table.TermTableModel.ResultTableType;
import org.uacalc.ui.tm.*;
import org.uacalc.ui.util.*;
import org.uacalc.util.*;

public class ComputationsController {
  
  private final UACalc uacalcUI;
  // these next should be glued together better. Maybe put the later as
  // a field in the former so there is only one list.
  private TaskTableModel taskTableModel = new TaskTableModel();
  private java.util.List<TermTableModel> termTableModels = new ArrayList<TermTableModel>();
  private static final String[] thinningOptions
      = new String[] {"use all coords", "thin coords", "decompose and thin"};
  
  public ComputationsController(UACalc uacalcUI) {
    this.uacalcUI = uacalcUI;
    setupTasksTable();
    setupResultTable();
  }

  private MainController getMainControler() { return uacalcUI.getMainController(); }
  
  private void setupTasksTable() {
    final JTable tasksTable = uacalcUI.getComputationsTable();
    tasksTable.setModel(taskTableModel);
    TableColumn col = tasksTable.getColumnModel().getColumn(0);
    col.setPreferredWidth(40);
    col.setMinWidth(30);
    col = tasksTable.getColumnModel().getColumn(1);
    col.setPreferredWidth(200);
    col.setMinWidth(140);
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
    col.setPreferredWidth(100);
    col.setMinWidth(120);
    col = tasksTable.getColumnModel().getColumn(6);
    col.setPreferredWidth(100);
    col.setMinWidth(120);
    col = tasksTable.getColumnModel().getColumn(7);
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
          //System.out.println("setResultsTable called from setUpTaskTable");
          setResultTableColWidths();
          uacalcUI.getResultTextField().setText(termTableModels.get(index).getDescription());
          resetCancelDelButton();
          uacalcUI.repaint();
        }
      }
    });
  }
  
  public TermTableModel getCurrentTermTableModel() {
    if (taskTableModel == null) return null;
    int index = taskTableModel.index(taskTableModel.getCurrentTask());
    //int index = uacalcUI.getComputationsTable().getSelectedRow();
    if (index < 0) return null;
    return termTableModels.get(index);
  }
  
  private void resetCancelDelButton() {
    final String cancel = "Cancel";
    final String del = "Delete";
    if (taskTableModel.getCurrentTask().getStatus() == BackgroundTask.Status.RUNNING) {
      uacalcUI.getCancelCompButton().setText(cancel);
    }
    else uacalcUI.getCancelCompButton().setText(del);
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
  
  private void setResultTableColWidths() {
    setResultTableColWidths(ResultTableType.TERM_LIST);
  }
  
  /**
   * This should be called after the TermTableModel has been set.
   */
  private void setResultTableColWidths(ResultTableType type) {
    final JTable resultTable = uacalcUI.getResultTable();
    final int cols = resultTable.getColumnCount();
    if (type.equals(ResultTableType.CENTRALITY)) {
      for (int i = 0; i < cols; i++) {
        TableColumn col = resultTable.getColumnModel().getColumn(i);
        if (i == 0) col.setPreferredWidth(60);
        else if (i == 1) col.setPreferredWidth(250);
        else if (i == 6) col.setPreferredWidth(800);
        else col.setPreferredWidth(40);
      }
      return;
    }
    //System.out.println("col count xxx = " + cols);
    for (int i = 0; i < cols; i++) {
      TableColumn col = resultTable.getColumnModel().getColumn(i);
      if (i == 0) col.setPreferredWidth(60);
      else if (i == 1) col.setPreferredWidth(700);  // was 900; TODO: fix
      else col.setPreferredWidth(40);
    }
  }
  
  /**
   * This is called at the end of a task to update the resultTextField
   * if this is still the current task.
   * 
   * @param task
   * @param ttm
   */
  private void updateResultTextField(BackgroundTask<?> task, TermTableModel ttm) {
    if (getCurrentTask() == task) {
      uacalcUI.getResultTextField().setText(ttm.getDescription());
    }
  }
  
  
  // no sure I need this
  public BackgroundTask<?> getCurrentTask() {
    return taskTableModel.getCurrentTask();
  }
  
  public void cancelOrRemoveCurrentTask() {
    BackgroundTask<?> task = getCurrentTask();
    if (task == null) return;  // is this necessary ??
    if (task.getStatus() == BackgroundTask.Status.RUNNING) {
      task.cancel(true);
    }
    else {
      int index = taskTableModel.index(task);
      termTableModels.remove(index);
      taskTableModel.removeTask(task);
      int size = termTableModels.size();
      if (size > 0) {
        TermTableModel ttm = termTableModels.get(size - 1);
        uacalcUI.getResultTable().setModel(ttm);
        ttm.fireTableDataChanged(); // this may be unnecessary
        setupResultTable();
      }
      resetCancelDelButton();
      uacalcUI.repaint();
    }
  }
  
  private void addTask(BackgroundTask<?> task) {
    taskTableModel.addTask(task);
    resetCancelDelButton();
    uacalcUI.getLogTextArea().setText(null);
    // 2 is the computations tab index.
    final int comTabIndex = 2;
    if (uacalcUI.getTabbedPane().getSelectedIndex() != comTabIndex) {
      uacalcUI.getTabbedPane().setSelectedIndex(comTabIndex);
      uacalcUI.repaint();
    }
  }
  
  public void setupFreeAlgebraTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int gens = getFreeGensDialog();
    if (!(gens > 0)) return;
    System.out.println("gens = " + gens);
    final String thinOpt = getThinGens();
    if (thinOpt == null) return;
    int optIndex = 0;
    if (thinOpt == thinningOptions[1]) optIndex = 1;
    if (thinOpt == thinningOptions[2]) optIndex = 2;
    final boolean decompose = optIndex == 2 ? true : false;
    final boolean thin = decompose || optIndex == 1;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    //System.out.println("first");
    setResultTableColWidths();
    final String desc = "F(" + gens + ") over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<FreeAlgebra>  freeAlgTask = new BackgroundTask<FreeAlgebra>(report) {
      public FreeAlgebra compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Computing the free algebra");
        report.setDescription(desc);
        FreeAlgebra freeAlg = new FreeAlgebra("F(" + gens + ") over " + alg.getName(),
                                              alg, gens, true, thin, decompose, null, report);
        return freeAlg;
      }
      public void onCompletion(FreeAlgebra fr, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        resetCancelDelButton();
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done computing the free algebra");
          report.setTimeLeft("");
          System.out.println("ttm = " + ttm);
          System.out.println("fr = " + fr);
          System.out.println("exception: " + exception);
          if (exception != null) exception.printStackTrace();
          ttm.setTerms(fr.getTerms());
          ttm.setVariables(fr.getVariables());
          if (!decompose) {
            ttm.setUniverse(fr.getUniverseList());
          }
          MainController mc = uacalcUI.getMainController();
          //mc.setCurrentAlgebra(mc.addAlgebra(fr));
          mc.addAlgebra(fr, false);
          if (getCurrentTask() == this) {
            uacalcUI.getResultTable().setModel(ttm);
            ttm.fireTableStructureChanged();
            ttm.fireTableDataChanged();
            //System.out.println("table cc = " + uacalcUI.getResultTable().getColumnCount());
            setResultTableColWidths();
            uacalcUI.repaint();
          }
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(freeAlgTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(freeAlgTask);
  }
  
  public int getNumberDialog(int min, String message, String title) {
    String numStr = JOptionPane.showInputDialog(uacalcUI.getFrame(), 
                    message, title, JOptionPane.QUESTION_MESSAGE);
    if (numStr == null) return -1;
    int num = -1;
    boolean gensOk = true;
    try {
      num = Integer.parseInt(numStr);
    }
    catch (NumberFormatException e) {
      gensOk = false;
    }
    if (!gensOk || num < min) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The number must be at least " + min + "<br>"
          + "Try again.</html>",
          "Number format error",
          JOptionPane.ERROR_MESSAGE);
      return -1;
    }
    return num;
  }
  
  public int getFreeGensDialog() {
    return getNumberDialog(1, "Number of generators?",  "Free Algebra");
  }
  
  public int getGensDialog() {
    return getNumberDialog(1, "Number of generators?",  "Free Algebra");
  }
  
  private String getThinGens() {
    String opt = (String)JOptionPane.showInputDialog(uacalcUI.getFrame(),
        "Choose one",
        "Thinning Options",
        JOptionPane.QUESTION_MESSAGE, null,
        thinningOptions, thinningOptions[2]);    
    return opt;
  }
  
  public void setupSubPowerTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int pow = getNumberDialog(1, "What is the power?", "Power");
    if (pow == -1) return;
    final int numGens = getNumberDialog(1, "Number of generators?", "Generators");
    if (!(numGens > 0)  || !(pow > 0)) return;
    //System.out.println("gens = " + numGens + ", pow = " + pow);
    final BigProductAlgebra prodAlg = new BigProductAlgebra(alg, pow);
    final List<IntArray> gens = getSubPowerGens(pow, numGens);
    if (gens == null) {
      uacalcUI.getMainController().beep();
      return;
    }
    //final String thinOpt = getThinGens();
    //if (thinOpt == null) return;
    //int optIndex = 0;
    //if (thinOpt == thinningOptions[1]) optIndex = 1;
    //if (thinOpt == thinningOptions[2]) optIndex = 2;
    //final boolean decompose = optIndex == 2 ? true : false;
    //final boolean thin = decompose || optIndex == 1;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    //System.out.println("first");
    setResultTableColWidths();
    final String desc =  "" + numGens + " generated sub of " + gAlg.toString(true) + " ^" + pow;
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<SubProductAlgebra>  subPowerTask = new BackgroundTask<SubProductAlgebra>(report) {
      public SubProductAlgebra compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Computing a sub power of " + gAlg.toString(true));
        report.setDescription(desc);
        SubProductAlgebra ans = new SubProductAlgebra("sub of " + alg.getName() + "^" + pow, prodAlg, 
            gens, true, report);  // here
        return ans;
      }
      public void onCompletion(SubProductAlgebra subPow, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        resetCancelDelButton();
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done computing sub power of " + gAlg.toString(true));
          report.setTimeLeft("");
          System.out.println("ttm = " + ttm);
          System.out.println("subPow = " + subPow);
          System.out.println("exception: " + exception);
          if (exception != null) exception.printStackTrace();
          ttm.setTerms(subPow.getTerms());
          ttm.setVariables(subPow.getVariables());
          ttm.setUniverse(subPow.getUniverseList());
          MainController mc = uacalcUI.getMainController();
          //mc.setCurrentAlgebra(mc.addAlgebra(fr));
          mc.addAlgebra(subPow, false);
          if (getCurrentTask() == this) {
            uacalcUI.getResultTable().setModel(ttm);
            ttm.fireTableStructureChanged();
            ttm.fireTableDataChanged();
            //System.out.println("table cc = " + uacalcUI.getResultTable().getColumnCount());
            setResultTableColWidths();
            uacalcUI.repaint();
          }
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(subPowerTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(subPowerTask);
  }
  
  private List<IntArray> getSubPowerGens(int pow, int numGens) {
    final int n = pow * numGens;
    String numStr = JOptionPane.showInputDialog(uacalcUI.getFrame(), 
        "<html>Input the generating vectors, one after another,<br> " 
        + n + " numbers, each separated by a space.</html>", 
        "Generators", JOptionPane.QUESTION_MESSAGE);
    if (numStr == null) return null;  // user cancelled
    String[] numsArr = numStr.split("\\s+");
    if (numsArr.length != n) return null;
    int[] nums = new int[n];
    try {
      for (int i = 0; i < n; i++) {
        nums[i] = Integer.parseInt(numsArr[i]);
      }
    }
    catch (NumberFormatException e) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning("bad number", false);
      return null;
    }
    List<IntArray> ans = new ArrayList<IntArray>(numGens);
    for (int i = 0; i < numGens; i++) {
      int[] raw = new int[pow];
      ans.add(new IntArray(raw));
      for (int j = 0; j < pow; j++) {
        raw[j] = nums[i*pow + j];
      }
    }
    return ans;
  }
  
  public void setupJonssonTermsTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding Jonsson terms for " + gAlg.toString(true);
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
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (exception != null) {
          System.out.println("exception: " + exception);
          exception.printStackTrace();
        }
        if (!cancelled) {
          if (terms == null) {
            report.addEndingLine("The variety is not congruence distributive.");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
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
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(jonssonTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(jonssonTermTask);
  }
  
  public void setupGummTermsTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding Gumm terms for " + gAlg.toString(true);
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
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (terms == null) {
            report.addEndingLine("The variety is not congruence modular.");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
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
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(gummTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(gummTermTask);
  }
  
  public void setupHagemannMitschkeTermsTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding Hagemann-Mitschke terms for " + gAlg.toString(true);
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
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (terms == null) {
            report.addEndingLine("The variety is not k-permutable.");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
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
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(hmTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(hmTermTask);
  }
  
  public void setupMajorityTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "A majority term over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  majTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Term nu = Malcev.majorityTerm(alg, report);
        return nu;
      }
      public void onCompletion(Term nu, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (nu == null) {
            report.addEndingLine("The variety has no majority term");
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found a majority term.");
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(nu);
            ttm.setTerms(terms);
          }
          if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(majTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(majTask);
  }
  
  public void setupPixleyTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "A Pixley term over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  pixleyTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Term nu = Malcev.pixleyTerm(alg, report);
        return nu;
      }
      public void onCompletion(Term nu, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (nu == null) {
            report.addEndingLine("The variety has no Pixley term");
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found a Pixley term.");
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(nu);
            ttm.setTerms(terms);
          }
          if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(pixleyTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(pixleyTask);
  }
  
  public void setupMalcevTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "A Maltsev term over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  majTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Term maltsev = Malcev.malcevTerm(alg, report);
        return maltsev;
      }
      public void onCompletion(Term mal, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (mal == null) {
            report.addEndingLine("The variety has no Maltsev term");
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found a Maltsev term.");
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(mal);
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
    addTask(majTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(majTask);
  }
  
  public void setupMarkovicMcKenzieSiggersTaylorTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "A Markovic-McKenzie-Siggers Taylor term over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  mmstTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Term mmstTerm = Malcev.markovicMcKenzieSiggersTaylorTerm(alg, report);
        return mmstTerm;
      }
      public void onCompletion(Term mmstTerm, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (mmstTerm == null) {
            report.addEndingLine("The variety has no Taylor term");
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found a Markovic-McKenzie-Siggers Taylor term.");
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(mmstTerm);
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
    addTask(mmstTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(mmstTask);
  }
  
  public void setupNUTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int arity = getNumberDialog(3, "What arity (at least 3)?", "Arity");
    if (!(arity > 2)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Near unanimity term of arity " + arity +  " over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  nuTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Term nu = Malcev.findNUF(alg, arity, report);
        return nu;
      }
      public void onCompletion(Term nu, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (nu == null) {
            report.addEndingLine("The variety has no NU term of arity " + arity);
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
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
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(nuTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(nuTask);
  }
  
  public void setupJICongruencesTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding the join irreducible congruences of " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    uacalcUI.repaint();
    final BackgroundTask<java.util.List<Partition>>  jiCongTask = new BackgroundTask<java.util.List<Partition>>(report) {
      public java.util.List<Partition> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        java.util.List<Partition> ans = alg.con().joinIrreducibles(report);
        return ans;
      }
      public void onCompletion(java.util.List<Partition> jis, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          //updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (jis == null) {
            System.out.println("The jis list was null. This should not happen.");
          }
          else {
            report.addEndingLine("Found " + jis.size() + " join irreducibles");
            //java.util.List<Term> terms = new ArrayList<Term>(1);
            //terms.add(jis);
            //ttm.setTerms(terms);
          }
          //if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(jiCongTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    //uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(jiCongTask);
  }
  
  public void setupCongruencesTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding the congruences of " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    uacalcUI.repaint();
    final BackgroundTask<Set<Partition>>  congTask = new BackgroundTask<Set<Partition>>(report) {
      public Set<Partition> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        java.util.Set<Partition> ans = alg.con().universe(report);
        if (Thread.currentThread().isInterrupted()) {
          report.addEndingLine("cancelled ...");
          return null;
        }
        alg.con().typeSet(report);  // make want to make this optional
        return ans;
      }
      public void onCompletion(java.util.List<Partition> congs, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          //updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (congs == null) {
            System.out.println("The congs list was null. This should not happen.");
          }
          else {
            report.addEndingLine("Found " + congs.size() + " congruences");
            //java.util.List<Term> terms = new ArrayList<Term>(1);
            //terms.add(jis);
            //ttm.setTerms(terms);
          }
          //if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(congTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    //uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(congTask);
  }
  
  public void setupCentralityTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    JOptionPane.showConfirmDialog(uacalcUI.getFrame(), "For now left = right = 1_A", 
        "Centrality", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
    final BinaryRelation left = alg.con().one();
    final BinaryRelation right = alg.con().one();
    
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel(ResultTableType.CENTRALITY);
    termTableModels.add(ttm);
    setResultTableColWidths(ResultTableType.CENTRALITY);
    final String desc = "Finding all centralities of left: " + left + ", right: " + right + " for " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    uacalcUI.repaint();
    final BackgroundTask<List<CentralityData>> centralityTask = new BackgroundTask<List<CentralityData>>(report) {
      public List<CentralityData> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        List<CentralityData> ans = alg.con().calcCentrality(left, right, report);
        return ans;
      }
      public void onCompletion(java.util.List<CentralityData> data, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          //updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (data == null) {
            System.out.println("The list of centrality data was null. This should not happen.");
          }
          else {
            if (exception != null) exception.printStackTrace();
            report.addEndingLine("Found centrality data");
            ttm.setCentralityList(data);
            if (getCurrentTask() == this) {
              uacalcUI.getResultTable().setModel(ttm);
              ttm.fireTableStructureChanged();
              ttm.fireTableDataChanged();
              //System.out.println("table cc = " + uacalcUI.getResultTable().getColumnCount());
              setResultTableColWidths(ResultTableType.CENTRALITY);
              uacalcUI.repaint();
            }
            //java.util.List<Term> terms = new ArrayList<Term>(1);
            //terms.add(jis);
            //ttm.setTerms(terms);
          }
          //if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(centralityTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    //uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(centralityTask);
  }
  
  public void setupBinVATask() {
    final GUIAlgebra gAlg2 = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg2 == null) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>You must have an algebra loaded.<br>"
          + "Use the file menu or make a new one.</html>",
          "No algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    GUIAlgebra[] algs = new GUIAlgebra[getMainControler().getAlgebraList().size()];
    int i = 0;
    for (GUIAlgebra a : getMainControler().getAlgebraList()) {
      algs[i++] = a;
    }
    final GUIAlgebra gA = (GUIAlgebra)JOptionPane.showInputDialog(uacalcUI.getFrame(),
                     "<html><center>B in V(<font color=\"red\">A</font>)?<br>" 
                         + "Choose <font color=\"red\">A</font></center></html>", 
                     "B in V(A)",
                     JOptionPane.QUESTION_MESSAGE, null, algs, algs[0]);
    //System.out.println("gA = " + gA);
    if (gA == null) return;
    final GUIAlgebra gB = (GUIAlgebra)JOptionPane.showInputDialog(uacalcUI.getFrame(),
        "<html><center><font color=\"red\">B</font> in V(A)?" 
            + "<br>Choose <font color=\"red\">B</font></center></html>", 
        "B in V(A)",
        JOptionPane.QUESTION_MESSAGE, null,
        algs, algs[0]);
    if (gB == null) return;
    final SmallAlgebra A = gA.getAlgebra();
    final SmallAlgebra B = gB.getAlgebra();
    if (!A.isSimilarTo(B)) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>Ths algebras must have the same similarity type.<br>",
          "Algebras Not Similar",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int[] BGenerators = B.sub().findMinimalSizedGeneratingSet().getArray();
    
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    //final String nameA = A.getName() != null ? A.getName() : gA.toString();
    //final String nameB = B.getName() != null ? B.getName() : gB.toString();
    final String desc = "Test if " + gB.toString() + " in V(" + gA.toString() + ")";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Equation>  nuTask = new BackgroundTask<Equation>(report) {
      public Equation compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Equation eq = FreeAlgebra.findEquationOfAnotB(A, B, BGenerators, report);
        return eq;
      }
      public void onCompletion(Equation eq, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (exception != null) {
          System.out.println("execption: " + exception);
          exception.printStackTrace();
        }
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (eq == null) {
            report.addEndingLine(gB.toString() + "is in V(" + gA.toString() + ")");
            ttm.setDescription(desc + ": it is!");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine(gB.toString() + "is not in V(" + gA.toString() + ")");
            ttm.setDescription("An equation of " + gA.toString() 
                + " that fails in " + gB.toString() 
                + " by substituting " + ArrayString.toString(BGenerators) 
                + " for the variables");
            updateResultTextField(this, ttm);
            java.util.List<Term> terms = new ArrayList<Term>(2);
            terms.add(eq.leftSide());
            terms.add(eq.rightSide());
            ttm.setTerms(terms);
            uacalcUI.repaint();
          }
          if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(nuTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(nuTask);
  }
  
  public void setupPrimalTermsTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Terms witnessing primality of " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<List<Term>>  primalTask = new BackgroundTask<List<Term>>(report) {
      public List<Term> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        List<Term> pTerms = Malcev.primalityTerms(alg, report);
        return pTerms;
      }
      public void onCompletion(List<Term> pTerms, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (pTerms == null) {
            report.addEndingLine("This algebra is not primal");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found terms showing primality; see <a link here>.");
            ttm.setTerms(pTerms);
          }
          if (getCurrentTask() == this) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(primalTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(primalTask);
  }
  
  public void formPowerAlgebra() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    int pow = getNumberDialog(2, "power?",  "Power Algebra");
    if (!(pow > 1)) return;
    PowerAlgebra powAlg = new PowerAlgebra(alg, pow);
    MainController mc = uacalcUI.getMainController();
    mc.addAlgebra(powAlg, true);
  }
  
  private boolean isAlgOK(GUIAlgebra gAlg) {
    if (gAlg == null) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>You must have an algebra loaded.<br>"
          + "Use the file menu or make a new one.</html>",
          "No algebra error",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    for (Operation op : gAlg.getAlgebra().operations()) {
      if (!op.isTotal()) {
        JOptionPane.showMessageDialog(uacalcUI.getFrame(),
            "<html>Op: " + op.symbol() + " is not total.<br>"
            + "Hint: you must move out of a cell after editing it.</html>",
            "Non-total operation error",
            JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }
    return true;
  }
  
}
