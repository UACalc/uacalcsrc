package org.uacalc.nbui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

//import java.awt.*;
//import java.awt.event.*;
import java.util.*;

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
import org.uacalc.alg.sublat.BasicSet;

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

  /////////////////////////////////////////////////////////////////
  // Matt had this weird complaint (mine didn't). I fixed it by 
  // using equals instead of ==, but still don't fully understand it.
  //
  //[javac] C:\Program
  //Files ComputationsController.java:603:
  //incomparable types: org.uacalc.ui.tm.BackgroundTask<capture of ?> and
  //<anonymous
  //org.uacalc.ui.tm.BackgroundTask<java.util.List<org.uacalc.terms.Term>>>
  //    [javac]           if (getCurrentTask() == this)
  //setResultTableColWidths();
  //////////////////////////////////////////////////////////////////

  
  public void setupFreeAlgebraTaskOld() {
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
          if (this.equals(getCurrentTask())) {
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
    final BackgroundTask<FreeAlgebra>  freeAlgTask = freeAlgebraTask(alg, report, desc, gens, thin, decompose, ttm);
    addTask(freeAlgTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(freeAlgTask);
  }
  
  public BackgroundTask<FreeAlgebra> freeAlgebraTask(final SmallAlgebra alg, 
                                                     final ProgressReport report,
                                                     final String desc,
                                                     final int gens,
                                                     final boolean thin,
                                                     final boolean decompose,
                                                     final TermTableModel ttm) {
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
          if (this.equals(getCurrentTask())) {
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
    return freeAlgTask;
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
  
  /**
   * The gui for forming a product algebras.
   * 
   * Written by Mike Behrisch.
   */
  public void formProd() {
    {// keep gAlg2 local
      final GUIAlgebra gAlg2 = uacalcUI.getMainController().getCurrentAlgebra();
      if (!isAlgOK(gAlg2)) return;
    }
    final int pow = getNumberDialog(1, "How many different factors (>0) do you want to multiply?", "Number of factors");
    if (pow == -1) return;
    final List<SmallAlgebra> listOfFactors = new ArrayList<SmallAlgebra>(pow);
    String name = "Prod of ";
    {// keep GUIAlgebra[] algs local
      //produce an array with all algebras currently open to choose from
      GUIAlgebra[] algs = new GUIAlgebra[getMainControler().getAlgebraList().size()];
      {//fill the array, keep j local
        int j = 0;
        for (GUIAlgebra a : getMainControler().getAlgebraList()) {
          algs[j++] = a;
        }
      }
      // get the first algebra from the user
      final GUIAlgebra gA = (GUIAlgebra)JOptionPane.showInputDialog(uacalcUI.getFrame(),
                       "<html><center>Please choose the first<br>" 
                           + "algebra of your product</center></html>", 
                       "First factor",
                       JOptionPane.QUESTION_MESSAGE, null, algs, algs[0]);
      if (gA == null) return;  // user has aborted
      final SmallAlgebra A = gA.getAlgebra();
      int exponent = 0;
      //do
      //{
        exponent = getNumberDialog(1, "To which power (>0) do you want to raise the inserted algebra?", "Power of the entered algebra");
        if (exponent == -1) return;
      //}while(exponent < 0);
      // add the entered algebra exponent many times to the list of algebras to take the product of 
      for (int i = 0; i < exponent; i++)
      {
        listOfFactors.add(A);  
      }
      name += A.getName() + (exponent > 1 ? "^" + exponent : "");
      // get the the other algebras from the user
      for (int i = 1; i < pow; i++)
      {
        SmallAlgebra B = null;
        boolean isNotSimilarToFirst = false;
        do 
        { 
          final int lastdigit = i%10;
          final String suffix = lastdigit < 3 ? ((i/10)%10 == 1 ? "th" : (lastdigit == 0 ? "st" : (lastdigit == 1 ? "nd" : "rd"))) : "th";
          final GUIAlgebra gB = (GUIAlgebra)JOptionPane.showInputDialog(uacalcUI.getFrame(),
                           "<html><center>Please choose the<br>" 
                               + (i + 1) + suffix + " algebra of your product</center></html>", 
                           "" + (i + 1) + suffix +  " factor",
                           JOptionPane.QUESTION_MESSAGE, null, algs, algs[0]);
          if (gB == null) return;  // user has aborted
          B = gB.getAlgebra();
          isNotSimilarToFirst = !B.isSimilarTo(A);
          if (isNotSimilarToFirst ) {
            JOptionPane.showMessageDialog(uacalcUI.getFrame(),
                "<html>The algebras must have the same similarity type. Choose another algebra!<br>",
                "Algebras Not Similar",
                JOptionPane.ERROR_MESSAGE);
            //return;
          }
        } while(isNotSimilarToFirst);
        //do
        //{
          exponent = getNumberDialog(1, "To which power (>0) do you want to raise the inserted algebra?", "Power of the entered algebra");
          if (exponent == -1) return;
        //}while(exponent < 0);
        // add the entered algebra exponent many times to the list of algebras to take the product of 
        for (int k = 0; k < exponent; k++)
        {
          listOfFactors.add(B);  
        }
        name += " x " + B.getName() + (exponent > 1 ? "^" + exponent : "");
      }
    }
    // construct the new product out of the entered algebras
    ProductAlgebra prodAlg = new ProductAlgebra(name, listOfFactors);  // here
    MainController mc = uacalcUI.getMainController();
    mc.addAlgebra(prodAlg, true);
    uacalcUI.repaint();
  }
    
  public void setupSubPowerTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int pow = getNumberDialog(1, "What is the power?", "Power");
    if (pow == -1) return;
    final int numGens = getNumberDialog(1, "Number of generators?", "Generators");
    if (!(numGens > 0)  || !(pow > 0)) return;
    final int constants = JOptionPane.showConfirmDialog(
        uacalcUI.getFrame(),
        "Include all constant vectors in this subpower?",
        "Include Diagonal",
        JOptionPane.YES_NO_CANCEL_OPTION);
    //System.out.println("includeConstants = " + constants);
    if (constants == 2) return;  // this is cancel
    final boolean includeConstants = constants == 0 ? true : false;
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
    final String extra = includeConstants ? " (including diagonal)" : "";
    final String desc =  "" + numGens + " generated sub of " + gAlg.toString(true) + " ^" + pow + extra;
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<SubProductAlgebra>  subPowerTask = new BackgroundTask<SubProductAlgebra>(report) {
      public SubProductAlgebra compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Computing a sub power of " + gAlg.toString(true) + extra);
        report.setDescription(desc);
        SubProductAlgebra ans = new SubProductAlgebra("sub of " + alg.getName() + "^" + pow, prodAlg, 
            gens, true, includeConstants, report);  // here
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
          if ( this.equals(getCurrentTask())) {
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
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
  
  public void setupSDTermsTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding SD terms for " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<java.util.List<Term>>  sdTermTask 
                                   = new BackgroundTask<java.util.List<Term>>(report) {
      public java.util.List<Term> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Finding semi-distributivity terms.");
        report.setDescription(desc);
        java.util.List<Term> terms = Malcev.sdTerms(alg, report);
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
            report.addEndingLine("The variety is not congruence semi-distributive.");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Done finding SD terms.");
            ttm.setTerms(terms);
          }
          //ttm.setVariables(fr.getVariables());
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(sdTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(sdTermTask);
  }
  
  public void setupMeetSDTermsTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding SD Meet terms for " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<java.util.List<Term>>  sdTermTask 
                                   = new BackgroundTask<java.util.List<Term>>(report) {
      public java.util.List<Term> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine("Finding SD-Meet terms.");
        report.setDescription(desc);
        java.util.List<Term> terms = Malcev.sdmeetTerms(alg, report);
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
            report.addEndingLine("The variety is not congruence meet semi-distributive.");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Done finding meet semi-distributive terms. The variety is congruence meet semi-distributive.");
            ttm.setTerms(terms);
          }
          //ttm.setVariables(fr.getVariables());
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(sdTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(sdTermTask);
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
        java.util.List<Term> terms = null;
        if (alg.isIdempotent()) {
          terms = Malcev.gummTerms(alg, report);
        }
        else {
          final TermTableModel ttm2 = new TermTableModel();
          final ProgressReport reportF2 = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
          termTableModels.add(ttm2);
          setResultTableColWidths();
          String desc2 = "F(2) over "  + gAlg.toString(true);
          ttm2.setDescription(desc2);
          BackgroundTask<FreeAlgebra> f2Task = freeAlgebraTask(alg, reportF2, 
              desc2, 2, true, true, ttm2);
          addTask(f2Task);
          MainController.scrollToBottom(uacalcUI.getComputationsTable());
          uacalcUI.getResultTable().setModel(ttm2);
          BackgroundExec.getBackgroundExec().execute(f2Task);
          FreeAlgebra f2 = null;
          try {
            f2 = f2Task.get();
          }
          catch (InterruptedException e) { }   // TODO: handle both of these
          catch (java.util.concurrent.ExecutionException e) {}
          //taskTableModel.setCurrentTask(this);
          terms =  Malcev.gummTerms(alg, f2, report);
          
        }
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
          }
          else {
            report.addEndingLine("Done finding Gumm terms.");
            ttm.setTerms(terms);
            updateResultTextField(this, ttm);
          }
          uacalcUI.repaint();
          //ttm.setVariables(fr.getVariables());
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
  
  public void setupWeak3EdgeTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "A weak 3-edge (Taylor) term over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  w3edgeTermTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.addLine("that is, a term satisfying e(y,y,x,x) = e(y,x,y,x) = e(x,x,x,y)");
        report.setDescription(desc);
        Term w3edgeTerm = Malcev.weak3EdgeTerm(alg, report);
        return w3edgeTerm;
      }
      public void onCompletion(Term w3edgeTerm, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (w3edgeTerm == null) {
            report.addEndingLine("The variety has no Taylor term");
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found a weak 3-edge (Taylor) term.");
            report.addLine("see M. Maroti and R. McKenzie, \"Existence theorems for weakly symmetric operations\", "
                + "Algebra Universalis, 59(2008), 463-489, and ");
            report.addLine("K. Kearnes, P. Markovic, and R. McKenzie, " 
                + "\"Optimal strong Mal'cev conditions for omitting type 1 in locally finite varieties\", "
                + "Algebra Universalis, to appear.");
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(w3edgeTerm);
            ttm.setTerms(terms);
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          uacalcUI.getResultTextField().setText(ttm.getDescription());
          uacalcUI.repaint();
        }
      }
    };
    addTask(w3edgeTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(w3edgeTermTask);
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
        report.addLine("that is, a term satisfying t(y,x,x,x) = t(x, x, y, y)");
        report.addLine("and t(x,x,y,x) = t(x,y,x,x)");
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
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
        Term nu = Malcev.nuTerm(alg, arity, report);
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
            report.addEndingLine("Found an NU term: " + nu);
            ttm.setDescription(desc + " " + nu);
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(nu);
            ttm.setTerms(terms);
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
  
  public void setupWeakNUTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int arity = getNumberDialog(3, "What arity (at least 3)?", "Arity");
    if (!(arity > 2)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Weak near unanimity term of arity " + arity +  " over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  wnuTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Term wnu = Malcev.findWeakNUTerm(alg, arity, report);
        return wnu;
      }
      public void onCompletion(Term wnu, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (wnu == null) {
            report.addEndingLine("The variety has no Weak NU term of arity " + arity);
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found a Weak NU term.");
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(wnu);
            ttm.setTerms(terms);
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(wnuTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(wnuTask);
  }
  
  public void setupEdgeTermTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int k = getNumberDialog(2, "k edge term: what k (at least 2). Arity will be k + 1.", "k Edge term");
    if (!(k > 1)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "k edge term (arity " + (k+1) +  ") over " + gAlg.toString(true);
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Term>  edgeTask = new BackgroundTask<Term>(report) {
      public Term compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Term edgeTerm = Malcev.fixedKEdgeTerm(alg, k, report);
        return edgeTerm;
      }
      public void onCompletion(Term edgeTerm, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (edgeTerm == null) {
            report.addEndingLine("The variety has no " + k + " edge term ");
            ttm.setDescription(desc + ": there is none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found an edge term: " + edgeTerm);
            ttm.setDescription(desc + " " + edgeTerm);
            java.util.List<Term> terms = new ArrayList<Term>(1);
            terms.add(edgeTerm);
            ttm.setTerms(terms);
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(edgeTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(edgeTask);
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
          //if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
          //if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
            if ( this.equals(getCurrentTask())) {
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
          //if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
  
  public void setupUnaryPolymorphismsTask() {
    // get the current highlighted congruences.
    
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    gAlg.getCurrentLattice(true);
    List<org.latdraw.diagram.Vertex> verts 
      = uacalcUI.getMainController().getConController().getConLatDrawer().getSelectedElemList();
    // pop up something to tell them how to use this
    if (verts == null) return;
    
    // here !!!!!!!!!!!!!!!!!!!
    
    
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
          //if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
          "<html>The algebras must have the same similarity type.<br>",
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
    final BackgroundTask<Equation>  nuBinVATask = new BackgroundTask<Equation>(report) {
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
          String substr = Arrays.toString(BGenerators);
          // liked it better with the [1, 4] notation.
          //substr = substr.substring(1, substr.length() - 1);
          if (eq == null) {
            report.addLine("The map from the generators to the elemements " 
                         + substr + " of " + gB.toString()
                         + " extends to a homomorphism.");
            report.addEndingLine(gB.toString() + " is in V(" + gA.toString() + ").");
            ttm.setDescription(desc + ": it is!");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine(gB.toString() + "is not in V(" + gA.toString() + ")");
            ttm.setDescription("An equation of " + gA.toString() 
                + " that fails in " + gB.toString() 
                + " by substituting " + substr 
                + " for the variables");
            updateResultTextField(this, ttm);
            java.util.List<Term> terms = new ArrayList<Term>(2);
            terms.add(eq.leftSide());
            terms.add(eq.rightSide());
            ttm.setTerms(terms);
            uacalcUI.repaint();
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(nuBinVATask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(nuBinVATask);
  }
  
  /**
   * A background task to test if B is in SP(A) = Q(A).
   */
  public void setupBinQATask() {
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
        "<html><center>B in Q(<font color=\"red\">A</font>) = SP(<font color=\"red\">A</font>)?<br>" 
            + "Choose <font color=\"red\">A</font></center></html>", 
            "B in SP(A)",
            JOptionPane.QUESTION_MESSAGE, null, algs, algs[0]);
    //System.out.println("gA = " + gA);
    if (gA == null) return;
    final GUIAlgebra gB = (GUIAlgebra)JOptionPane.showInputDialog(uacalcUI.getFrame(),
        "<html><center><font color=\"red\">B</font> in SP(A)?" 
            + "<br>Choose <font color=\"red\">B</font></center></html>", 
            "B in V(A)",
            JOptionPane.QUESTION_MESSAGE, null,
            algs, algs[0]);
    if (gB == null) return;
    final SmallAlgebra A = gA.getAlgebra();
    final SmallAlgebra B = gB.getAlgebra();
    if (!A.isSimilarTo(B)) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The algebras must have the same similarity type.<br>",
          "Algebras Not Similar",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    //final String nameA = A.getName() != null ? A.getName() : gA.toString();
    //final String nameB = B.getName() != null ? B.getName() : gB.toString();
    final String desc = "Test if " + gB.toString() + " in SP(" + gA.toString() + ")";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<List<Homomorphism>>  isBinQATask = new BackgroundTask<List<Homomorphism>>(report) {
      public List<Homomorphism> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        List<Homomorphism> lst = Algebras.memberOfQuasivariety(B, A, report);
        return lst;
      }
      public void onCompletion(List<Homomorphism> lst, Throwable exception, 
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
          // liked it better with the [1, 4] notation.
          //substr = substr.substring(1, substr.length() - 1);
          if (lst == null) {
            report.addEndingLine(gB.toString() + " is not in SP(" + gA.toString() + ").");
            ttm.setDescription(desc + ": it is not");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine(gB.toString() + "is in SP(" + gA.toString() + ")");
            /////////////////   here  !!!!!!!!!!!!!
            ttm.setDescription("An equation of " + gA.toString() 
                + " that fails in " + gB.toString() 
                + " by substituting " //+ substr 
                + " for the variables");
            updateResultTextField(this, ttm);
            java.util.List<Term> terms = new ArrayList<Term>(2);
            //terms.add(eq.leftSide());
            //terms.add(eq.rightSide());
            ttm.setTerms(terms);
            uacalcUI.repaint();
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(isBinQATask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(isBinQATask);
  }

  
  public void setupQuasiCriticalTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg == null) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>You must have an algebra loaded.<br>"
          + "Use the file menu or make a new one.</html>",
          "No algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    final SmallAlgebra A = gAlg.getAlgebra();
    
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Test if " + gAlg.toString() + " is in SP(S(" + gAlg.toString() + ") - " + gAlg.toString() + ")";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    //final BackgroundTask<Map<Partition,IntArray>>  quasiCriticalTask = new BackgroundTask<Map<Partition,IntArray>>(report) 
    final BackgroundTask<java.util.List<Homomorphism>>  quasiCriticalTask = new BackgroundTask<java.util.List<Homomorphism>>(report) {
      public List<Homomorphism> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        List<Homomorphism> lst = Algebras.memberOfQuasivarietyGenByProperSubs(A, report);
        return lst;
      }
      public void onCompletion(java.util.List<Homomorphism> lst, Throwable exception, 
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
          if (lst == null) {
            report.addEndingLine(gAlg.toString() + " is quasicritical");
            ttm.setDescription(desc + ": it isn't! (so it is quasicritical)");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine(gAlg.toString() 
                + " is in SP(S(" + gAlg.toString() + ") - " + gAlg.toString() + ") ( so it is not quasicritical)");
            ttm.setDescription(desc + ": it is! (so it isn't quasicritical)");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(quasiCriticalTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(quasiCriticalTask);
  }
  
  public void setupQuasiCriticalCongruencesTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg == null) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>You must have an algebra loaded.<br>"
          + "Use the file menu or make a new one.</html>",
          "No algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    final SmallAlgebra A = gAlg.getAlgebra();
    
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Find congruences theta with " + gAlg + "/theta quasi-critical.";
          // + gAlg.toString() + " is in SP(S(" + gAlg.toString() + ") - " + gAlg.toString() + ")";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<List<Partition>>  quasiCriticalCongruenceTask = new BackgroundTask<List<Partition>>(report) {
      public List<Partition> compute() {
        report.addStartLine(desc);
        report.setDescription(desc);
        List<Partition> lst = Algebras.quasiCriticalCongruences(A, report);
        return lst;
      }
      public void onCompletion(List<Partition> lst, Throwable exception, 
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
        // here !!!!!!!!
        if (!cancelled) {
          if (lst != null) {
            report.addStartLine("congruences theta with " + gAlg + "/theta quasicritical: ");
            for (Partition theta : lst) {
              report.addLine(theta.toString());              
            }
            report.addEndingLine("");
            report.addEndingLine("Done");
            ttm.setDescription(desc + ". See the log area.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("none found (shouldn't happen)."); 
            ttm.setDescription(desc + ". None found (shouldn't happen).");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(quasiCriticalCongruenceTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(quasiCriticalCongruenceTask);
  }
  
  
  public void setupQuasiCriticalTask_old() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (gAlg == null) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>You must have an algebra loaded.<br>"
          + "Use the file menu or make a new one.</html>",
          "No algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    final SmallAlgebra A = gAlg.getAlgebra();
    
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Test if " + gAlg.toString() + " is in SP(S(" + gAlg.toString() + ") - " + gAlg.toString() + ")";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Map<Partition,IntArray>>  quasiCriticalTask = new BackgroundTask<Map<Partition,IntArray>>(report) {
      public Map<Partition,IntArray> compute() {
        report.addStartLine(desc);
        report.setDescription(desc);
        Map<Partition,IntArray> map = Algebras.quasiCritical(A, report);
        //List<Homomorphism<Integer, Integer>> homomorphisms = memberOfQuasivarietyGenByProperSubs(A, report);
        return map;
      }
      public void onCompletion(Map<Partition,IntArray> map, Throwable exception, 
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
          if (map == null) {
            report.addEndingLine(gAlg.toString() + " is quasicritical");
            ttm.setDescription(desc + ": it isn't! (so it is quasicritical)");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine(gAlg.toString() 
                + " is in SP(S(" + gAlg.toString() + ") - " + gAlg.toString() + ") ( so it is not quasicritical)");
            ttm.setDescription(desc + ": it is! (so it isn't quasicritical)");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(quasiCriticalTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(quasiCriticalTask);
  }
  
  public void setupCloBinCloATask() {
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
                     "<html><center>Clo(B) in Clo(<font color=\"red\">A</font>)?<br>" 
                         + "Choose <font color=\"red\">A</font></center></html>", 
                     "Clo(B) in Clo(A)",
                     JOptionPane.QUESTION_MESSAGE, null, algs, algs[0]);
    //System.out.println("gA = " + gA);
    if (gA == null) return;
    final GUIAlgebra gB = (GUIAlgebra)JOptionPane.showInputDialog(uacalcUI.getFrame(),
        "<html><center>Clo(<font color=\"red\">B</font>) in Clo(A)?" 
            + "<br>Choose <font color=\"red\">B</font></center></html>", 
        "Clo(B) in Clo(A)",
        JOptionPane.QUESTION_MESSAGE, null,
        algs, algs[0]);
    if (gB == null) return;
    final SmallAlgebra A = gA.getAlgebra();
    final SmallAlgebra B = gB.getAlgebra();
    if (A.cardinality() != B.cardinality()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>Ths algebras must be on the same set (have the same cardinality).<br>",
          "Algebras Not the Same Cardinality",
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
    final String desc = "Test if the basic ops of " + gB.toString() + "are in Clo(" + gA.toString() + ")";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    // here  !!!!!!!!!!!!
    final BackgroundTask<Equation>  cloneTask = new BackgroundTask<Equation>(report) {
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
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(cloneTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(cloneTask);
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
            report.addEndingLine("This algebra is not primal;");
            report.addLine("see D. M. Clark, B. A. Davey, J. G. Pitkethly and D. L. Rifqui, \"Flat unars: the primal, "
                + "the semi-primal, and the dualizable,\" Algebra Universalis, 63(2010), 303-329,");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine("Found terms showing primality;");
            report.addLine("see D. M. Clark, B. A. Davey, J. G. Pitkethly and D. L. Rifqui, \"Flat unars: the primal, "
                + "the semi-primal, and the dualizable,\" Algebra Universalis, 63(2010), 303-329,");
            report.addLine("for an explanation of how these terms can be combined to give an arbitrary operation on  "
                + gAlg.toString(true));
            ttm.setTerms(pTerms);
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
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
  
  public void setupOmittedIdealIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = 
        "Finding the largest order ideal of omitted types for the variety of " + gAlg.toString(true) + ".";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Set<Integer>>  omittedTypesTask = new BackgroundTask<Set<Integer>>(report) {
      public Set<Integer> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Set<Integer> omittedTypes = Malcev.omittedIdealIdempotent(alg, report);
        return omittedTypes;
      }
      public void onCompletion(Set<Integer> omittedTypes, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done.");
          //report.addEndingLine("Found terms showing primality;");
          //report.addLine("see D. M. Clark, B. A. Davey, J. G. Pitkethly and D. L. Rifqui, \"Flat unars: the primal, "
          //    + "the semi-primal, and the dualizable,\" Algebra Universalis, 63(2010), 303-329,");
          //report.addLine("for an explanation of how these terms can be combined to give an arbitrary operation on  "
          //    + gAlg.toString(true));
          ttm.setDescription(desc + " " + omittedTypes);
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(omittedTypesTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(omittedTypesTask);
  }
  
  public void setupTypeSetIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = 
        "Finding subsets S and T of types with the type set of the variety generated by " 
         + gAlg.toString(true) + " between the two.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<List<Set<Integer>>>  omittedTypesTask 
                                        = new BackgroundTask<List<Set<Integer>>>(report) {
      public List<Set<Integer>> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        List<Set<Integer>> bounds = Malcev.typeSetIdempotent(alg, report);
        return bounds;
      }
      public void onCompletion(List<Set<Integer>> bounds, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          String str = "The type set is " + bounds.get(0) + ".";
          if (!bounds.get(0).equals(bounds.get(1))) {
            str = "The type set contains " + bounds.get(0) 
                + " and is contained in " + bounds.get(1) + ".";
          }
          report.addEndingLine(str);
          ttm.setDescription(desc + " " + str);
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(omittedTypesTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(omittedTypesTask);
  }
  
  public void setupCDIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Testing if " + gAlg.toString(true) + " generates a CD variety.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  CDTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        boolean ans = Malcev.isCongruenceDistIdempotent(alg, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          ttm.setDescription(desc + (ans ? " It is." : " It is not." ));
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(CDTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(CDTask);
  }
  
  public void setupCMIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Testing if " + gAlg.toString(true) + " generates a CM variety.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  CMTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        boolean ans = Malcev.isCongruenceModularIdempotent(alg, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          //report.addEndingLine("Found terms showing primality;");
          //report.addLine("see D. M. Clark, B. A. Davey, J. G. Pitkethly and D. L. Rifqui, \"Flat unars: the primal, "
          //    + "the semi-primal, and the dualizable,\" Algebra Universalis, 63(2010), 303-329,");
          //report.addLine("for an explanation of how these terms can be combined to give an arbitrary operation on  "
          //    + gAlg.toString(true));
          ttm.setDescription(desc + (ans ? " It is." : " It is not." ));
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(CMTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(CMTask);
  }
  
  
  public void setupkPermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    int ync = JOptionPane.showConfirmDialog(uacalcUI.getFrame(), "Find k if it generates a k-permutable variety?");
    if (ync == JOptionPane.CANCEL_OPTION) return;
    final boolean calcLevel = ync == JOptionPane.YES_OPTION ? true : false;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    String descxx = "Test if " + gAlg.toString(true) + " generates a k-permutible variety for some k.";
    final String desc = calcLevel ? descxx + " And find k." : descxx;
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Set<Integer>>  kPermTask = new BackgroundTask<Set<Integer>>(report) {
      public Set<Integer> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Set<Integer> typesFound = new TreeSet<Integer>();
        typesFound = Malcev.typesInSofAStrictlySimpleIdempotent(alg, typesFound, null);// suppress report's output
        //List<Set<Integer>> bounds = Malcev.typeSetIdempotent(alg, report);
        //Set<Integer> omittedTypes = Malcev.omittedIdealIdempotent(alg, report);
        boolean isKPerm = true;
        if (typesFound.contains(1)) isKPerm = false;
        if (typesFound.contains(4)) isKPerm = false;
        if (typesFound.contains(5)) isKPerm = false;
        int level = 1;
        if (calcLevel && isKPerm) level = Malcev.permLevelIdempotent(alg, report);
        typesFound.add(- level);
        return typesFound;
      }
      public void onCompletion(Set<Integer> typesFound, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          int level = -1;
          for (Integer i : typesFound) {
            if (i < 0) {
              level = i;
              break;
            }
          }
          boolean isKPerm = true;
          if (typesFound.contains(1)) {
            report.addLine("The variety contains TCT type 1 so it is not k-permutable.");
            isKPerm = false;
          }
          if (typesFound.contains(5)) {
            report.addLine("The variety contains TCT type 5 so it is not k-permutable.");
            isKPerm = false;
          }
          if (typesFound.contains(4)) {
            report.addLine("The variety contains TCT type 4 so it is not k-permutable.");
            isKPerm = false;
          }
          if (level != -1) level = - level;
          report.addEndingLine("Done");
          if (level == -1) ttm.setDescription(desc + (isKPerm ? " It does." : " It does not."));
          else ttm.setDescription(desc + " The variety is " + level + "-permutable.");
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(kPermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(kPermTask);
  }
  
  public void setupFixedKPermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int level = getNumberDialog(2, "What level (at least 2)?", "Level of Permutability");
    if (!(level >= 2)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Test if " + gAlg.toString(true) + " generates a " + level + "-permutable variety.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  fixedKPermTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        boolean ans = Malcev.fixedKPermIdempotent(alg, level, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (ans) {
            report.addEndingLine("The variety generated by " + gAlg.toString(true) 
                + " is " + level + "-permutable.");
          }
          else {
            report.addEndingLine("The variety generated by " + gAlg.toString(true) 
                + " is not " + level + "-permutable.");
          }
          ttm.setDescription(desc + (ans ? " It does." : " It does not."));
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(fixedKPermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(fixedKPermTask);
  }
  
  public void setupFixedkPermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int k = getNumberDialog(2, "What level of permutability (at least 2)?", "Level of Permutability");
    if (!(k > 1)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Test if " + gAlg.toString(true) + " generates a " + k + "-permutible variety.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    
    final BackgroundTask<Boolean>  kNUTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        //Set<Integer> omittedTypes = Malcev.omittedIdealIdempotent(alg, report);
        //return omittedTypes;
        boolean ans = Malcev.nuTermIdempotent(alg, k, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          //report.addEndingLine("Found terms showing primality;");
          //report.addLine("see D. M. Clark, B. A. Davey, J. G. Pitkethly and D. L. Rifqui, \"Flat unars: the primal, "
          //    + "the semi-primal, and the dualizable,\" Algebra Universalis, 63(2010), 303-329,");
          //report.addLine("for an explanation of how these terms can be combined to give an arbitrary operation on  "
          //    + gAlg.toString(true));
          ttm.setDescription(desc + (ans ? " It does." : " It does not."));
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(kNUTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(kNUTask);
    
    
    
    
    
    
    
    
    
    
    
    
    
  }
  
  
  public void setupkNUIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int arity = getNumberDialog(3, "What arity (at least 3)?", "Arity");
    if (!(arity > 2)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Testing if " + gAlg.toString(true) + " has a " + arity + "-ary NU term.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  kNUTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        //Set<Integer> omittedTypes = Malcev.omittedIdealIdempotent(alg, report);
        //return omittedTypes;
        boolean ans = Malcev.nuTermIdempotent(alg, arity, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          //report.addEndingLine("Found terms showing primality;");
          //report.addLine("see D. M. Clark, B. A. Davey, J. G. Pitkethly and D. L. Rifqui, \"Flat unars: the primal, "
          //    + "the semi-primal, and the dualizable,\" Algebra Universalis, 63(2010), 303-329,");
          //report.addLine("for an explanation of how these terms can be combined to give an arbitrary operation on  "
          //    + gAlg.toString(true));
          ttm.setDescription(desc + (ans ? " It does." : " It does not."));
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(kNUTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(kNUTask);
  }
  
  public void setupMajorityIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int arity = 3;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Testing if " + gAlg.toString(true) + " has a majority term.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  marjorityTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        //Set<Integer> omittedTypes = Malcev.omittedIdealIdempotent(alg, report);
        //return omittedTypes;
        boolean ans = Malcev.nuTermIdempotent(alg, arity, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          ttm.setDescription(desc + (ans ? " It does." : " It does not."));
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(marjorityTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(marjorityTask);
  }
  
  public void setupSDIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Testing if " + gAlg.toString(true) + " generates a congruence semidistributive variety.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  sdTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        IntArray ia = Malcev.sdIdempotent(alg, report);
        return (ia == null);
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          ttm.setDescription(desc + (ans ? " It is." :  " It is not."));
          updateResultTextField(this, ttm);
          //ttm.setTerms(omittedTypes);// see if we can add a method for this
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(sdTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(sdTask);
  }
  
  public void setupSDMeetIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Testing if " + gAlg.toString(true) 
        + " generates a congruence meet semidistributive variety.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  sdTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        IntArray ia = Malcev.sdMeetIdempotent(alg, report);
        return (ia == null);
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          ttm.setDescription(desc + (ans ? " It is." :  " It is not."));
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(sdTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(sdTask);
  }
   
  public void setupPermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Does " + gAlg.toString(true) + " generate a CP variety.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  omittedTypesTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        IntArray ia = Malcev.cpIdempotent(alg, report);
        return ia == null;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          String extra = ans ? " It does generate a CP variety." : " It does not generate a CP variety.";
          ttm.setDescription(desc + " " + extra);
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(omittedTypesTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(omittedTypesTask);
  }
  
  public void setupEdgeTermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
          "Non idempotent algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Does " + gAlg.toString(true) + " have an edge term.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<List<BasicSet>>  edgeTermTask = new BackgroundTask<List<BasicSet>>(report) {
      public List<BasicSet> compute() {
        report.addStartLine(desc);
        report.setDescription(desc);
        List<BasicSet> ans = Malcev.cubeTermBlockerIdempotent(alg, report);
        return ans;
      }
      public void onCompletion(List<BasicSet> ans, Throwable exception, 
          boolean cancelled, boolean outOfMemory) {
        System.out.println("exception: " + exception);
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          String extra = ans == null ? " It does have an edge term." : 
            " It does not have an edge term. Cube term blocker: " + ans;
          ttm.setDescription(desc + " " + extra);
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(edgeTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(edgeTermTask);
  }

  public void setupFixedKEdgeTermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int k = getNumberDialog(2, "k edge term: what k (at least 2). Arity will be k + 1.", "k Edge term");
    if (!(k > 1)) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Does " + gAlg.toString(true) + " have a " + k + " edge term (arity " + (k+1) + ").";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  edgeTermTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        report.addStartLine(desc);
        report.setDescription(desc);
        boolean ans = Malcev.fixedKEdgeIdempotent(alg, k, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        System.out.println("exception: " + exception);
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          String extra = ans ? " It does have a " + k + " edge term." : " It does not have a " + k + " edge term.";
          ttm.setDescription(desc + " " + extra);
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(edgeTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(edgeTermTask);
  }
  
  public void setupNUTermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
          "Non idempotent algebra error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Does " + gAlg.toString(true) + " have an near unamimity term.";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<List<BasicSet>>  nuTermTask = new BackgroundTask<List<BasicSet>>(report) {
      public List<BasicSet> compute() {
        report.addStartLine(desc);
        report.addLine("Testing for both an edge term and congruence SD-meet.");
        report.setDescription(desc);
        List<BasicSet> ans = Malcev.cubeTermBlockerIdempotent(alg, report);
        if (ans != null) return ans;
        if (Malcev.sdMeetIdempotent(alg, report) != null) {
          return new ArrayList<BasicSet>(); // return an empty list to indicate it is not SD-meet.
        }
        return ans;
      }
      public void onCompletion(List<BasicSet> ans, Throwable exception, 
          boolean cancelled, boolean outOfMemory) {
        System.out.println("exception: " + exception);
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          String extra;
          if (ans == null) extra  = " It does have an NU term.";
          else {
            if (ans.isEmpty()) extra = " V(A) is not congruence SD-meet so cannot have an NU term.";
            else extra = " V(A) has a cube term blocker: " + ans + ", so cannot have an NU term.";
          }
          ttm.setDescription(desc + " " + extra);
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(nuTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(nuTermTask);
  }

  
  public void setupCyclicTermIdempotentTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    if (!alg.isIdempotent()) {
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          "<html>The current algebra must be idempotent.<br>",
              "Non idempotent algebra error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }
    final int arity = getNumberDialog(2, "What arity (at least 2)?", "Arity");
    if (arity < 2) return;
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Does " + gAlg.toString(true) + " have a cyclic term of arity " + arity + ".";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Boolean>  cyclicTermTask = new BackgroundTask<Boolean>(report) {
      public Boolean compute() {
        report.addStartLine(desc);
        report.setDescription(desc);
        boolean ans = Malcev.cyclicTermIdempotent(alg, arity, report);
        return ans;
      }
      public void onCompletion(Boolean ans, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        System.out.println("exception: " + exception);
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          report.addEndingLine("Done");
          String extra = ans ? " It does have a cyclic term." : " It does not have a cyclic term.";
          ttm.setDescription(desc + " " + extra);
          updateResultTextField(this, ttm);
          List<Term> fake = new ArrayList<Term>();
          ttm.setTerms(fake);

          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(cyclicTermTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(cyclicTermTask);
  }

  
  public void setupGenCommutivityCheckTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    List<OperationSymbol> symList = alg.similarityType().getSortedOperationSymbols();
    final List<OperationSymbol> symList2 = new ArrayList<>();
    for (OperationSymbol sym : symList) {
      if (sym.arity() > 1) symList2.add(sym);
    }
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = 
        "Test which basic operations of " + gAlg + " are cyclic or totally symmetric";
    ttm.setDescription(desc + ".");
    uacalcUI.getResultTextField().setText(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Map<OperationSymbol,Boolean>>  commutivityCheckTask 
                = new BackgroundTask<Map<OperationSymbol,Boolean>>(report) {
      public Map<OperationSymbol,Boolean> compute() {
        report.addStartLine(desc);
        report.setDescription(desc);
        Map<OperationSymbol,Boolean> ans = new HashMap<>();
        
        for (OperationSymbol sym : symList2) {
          
          if (sym.arity() == 2) {
            report.addStartLine("Testing if " + sym + " is commutative.");
            Map<Variable,Integer> map = Equations.cyclicLaw(sym).findFailureMap(alg, report);
            if (map == null) {
              report.addEndingLine(sym + " is commutative.");
              ans.put(sym, true);
            }
            else {
              report.addEndingLine(sym + " fails commutivity under: " +  map);
            }
          }
          else {
            report.addStartLine("Testing if " + sym + " is cyclic.");
            Map<Variable,Integer> map = Equations.cyclicLaw(sym).findFailureMap(alg, report);
            if (map == null) {
              report.addEndingLine(sym + " is cyclic.");
              report.addStartLine("Testing if " + sym + " is totally symmetric.");
              Map<Variable,Integer> map2 = Equations.firstSecondSymmetricLaw(sym).findFailureMap(alg, report);
              if (map2 == null) {
                report.addEndingLine(sym + " is totally symmetric.");
                ans.put(sym, true);
              }
              else {
                report.addEndingLine(sym + " fails total symmetry under: " +  map2);
                ans.put(sym,false);
              }
            }
            else {
              report.addEndingLine(sym + " is not cyclic, witness: " +  map);
            }
          }
        }
 
        return ans;
      }
      public void onCompletion(Map<OperationSymbol,Boolean> ans, Throwable exception, 
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
          //List<OperationSymbol> assocOps = 
          if (ans.isEmpty()) {
            report.addEndingLine(gAlg.toString() + " has no cyclic basic operations.");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            List<OperationSymbol> cyclicOps = new ArrayList<>();
            List<OperationSymbol> totSymOps = new ArrayList<>();
            for (OperationSymbol sym : ans.keySet()) {
              if (ans.get(sym)) totSymOps.add(sym);
              else cyclicOps.add(sym);
            }
            String totOpsString = totSymOps.isEmpty() ? "" : "Totally sym basic ops: " + totSymOps + ". ";
            String cycOpsString = cyclicOps.isEmpty() ? "" : "Cyclic Ops " + cyclicOps + ".";
            String ansStr = totOpsString + cycOpsString;
            report.addEndingLine(ansStr);
            ttm.setDescription(desc + ": " + ansStr);
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
            // if there is only one op and it is associate, report this is a semigroup 
            // and maybe test if it is a semilattice.
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
      
    };
    addTask(commutivityCheckTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(commutivityCheckTask);
  }
  
  
  public void setupAssociativeCheckTask() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    List<OperationSymbol> symList = alg.similarityType().getSortedOperationSymbols();
    final List<Equation> eqs = new ArrayList<>();
    for (OperationSymbol sym : symList) {
      if (sym.arity() == 2) eqs.add(Equations.associativeLaw(sym));
    }
    final int numEqs = eqs.size();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
 
    final String desc = 
        "Test which of the " + numEqs + " binary basic operations of " + gAlg + " are associative";
    ttm.setDescription(desc + ".");
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<List<OperationSymbol>>  assocCheckTask 
                = new BackgroundTask<List<OperationSymbol>>(report) {
      public List<OperationSymbol> compute() {
        report.addStartLine(desc);
        report.setDescription(desc);
        List<OperationSymbol> ans = new ArrayList<OperationSymbol>();
        for (Equation equ : eqs) {
          OperationSymbol sym = equ.leftSide().leadingOperationSymbol();
          report.addStartLine("Testing if " + equ);
          Map<Variable,Integer> map = equ.findFailureMap(gAlg.getAlgebra(), report);
          if (map == null) {
            report.addEndingLine(equ + " holds in " + gAlg.toString());
            ans.add(sym);
          }
          else {
            report.addEndingLine(equ + " fails in " + gAlg.toString() + " under " + map);
          }
        }
        return ans;
      }
      public void onCompletion(List<OperationSymbol> ans, Throwable exception, 
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
          //List<OperationSymbol> assocOps = 
          if (ans.isEmpty()) {
            report.addEndingLine(gAlg.toString() + " has no associative, binary, basic operations.");
            ttm.setDescription(desc + ": there are none.");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine(" associative, binary, basic operations of " + gAlg.toString() + ": " + ans);
            ttm.setDescription(desc + ": " + ans);
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
            // if there is only one op and it is associate, report this is a semigroup 
            // and maybe test if it is a semilattice.
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(assocCheckTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(assocCheckTask);
  }
  
  private Equation lastEquation = null;
  private boolean warned = false;
  
  public void setupEquationCheckTask() {
    if (!warned) {
      warned = true;
      int ok = JOptionPane.showConfirmDialog(uacalcUI.getFrame(), 
          "<html>This is experimental and may crash the program.<br>" 
              + "Choose cancel if you need to save your work before trying this.</html>", 
              "Beta Version", 
              JOptionPane.OK_CANCEL_OPTION);
      if (ok == JOptionPane.CANCEL_OPTION) return;
    }
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    List<OperationSymbol> opList = alg.similarityType().getSortedOperationSymbols();
    if (opList == null || opList.size() == 0) return;  // give a warning !!!
    StringBuffer buf = new StringBuffer();
    final String sep = ", ";
    for (int i = 0; i < opList.size() - 1; i++) {
      buf.append(opList.get(i).toString(true));
      buf.append(sep);
    }
    buf.append(opList.get(opList.size() - 1).toString(true));
    String s = (String)JOptionPane.showInputDialog(
        uacalcUI.getFrame(),
        "<html>Enter term in symbols (arities in parentheses):<br>"
            + buf.toString() + "<br>"
            + "Use parentheses for constants like <font color=\"red\">c()</font>" 
            + "</html>",
        //"Complete the sentence:\n"
        //+ "\"Green eggs and...\"",
        "Enter the Left Side",
        JOptionPane.PLAIN_MESSAGE,
        null,//icon
        null,
        lastEquation == null ? null : lastEquation.leftSide().toString());
    if (s == null) return; // the user cancelled
    Term left = null;
    try{
      left = Terms.stringToTerm(s);
    }
    catch(IllegalArgumentException ex) {
      System.out.println(ex.getMessage());
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          ex.getMessage() + " Aborting.",
          "Illegal Argument Exception",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    String r = (String)JOptionPane.showInputDialog(
        uacalcUI.getFrame(),
        "<html>Left: " + left + "<br>"
            + "Enter a term in symbols (arities in parentheses):<br>"
            + buf.toString() + "<br>"
            + "Use parentheses for constants like <font color=\"red\">c()</font>" 
            + "</html>",
        "Enter the Right Side",
        JOptionPane.PLAIN_MESSAGE,
        null,//icon
        null,
        lastEquation == null ? null : lastEquation.rightSide().toString());
    if (r == null) return; // the user cancelled
    Term right = null;
    try {
      right = Terms.stringToTerm(r);
    }
    catch(IllegalArgumentException ex) {
      System.out.println(ex.getMessage());
      JOptionPane.showMessageDialog(uacalcUI.getFrame(),
          ex.getMessage() + " Aborting.",
          "Illegal Argument Exception",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    final Equation eq = new Equation(left, right);
    lastEquation = eq;
    Set<OperationSymbol> eqOpSyms = eq.getOperationSymbols();
    for (OperationSymbol opSym : eqOpSyms) {
      if (!opList.contains(opSym)) {
        JOptionPane.showMessageDialog(uacalcUI.getFrame(),
            "<html>The symbol <font color=\"red\">" + 
              opSym + "</font> is not in the similarity type of A.<br>Try again.</html>",
            "Invalid Operation Symbol",
            JOptionPane.WARNING_MESSAGE);
        return;
      }
    }
    List<Equation> eqs = new ArrayList<>(1);
    eqs.add(eq);
    eqTask(gAlg, eqs);
  }
  
  private void eqTask(final GUIAlgebra gAlg, final List<Equation> eqs) {
    final int numEqs = eqs.size();
    if (numEqs == 0) return;  // shouldn't happen; this is a private method
    final Equation eq = eqs.get(0);
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
 
    final String desc = numEqs == 1 
        ? "Test if " + " " + eq.leftSide() + " = " + eq.rightSide() + " in " +gAlg.toString()
            : "Testing " + numEqs + " equations in " + gAlg + ".";
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    final BackgroundTask<Map<Variable,Integer>>  eqCheckTask = new BackgroundTask<Map<Variable,Integer>>(report) {
      public Map<Variable,Integer> compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        Map<Variable,Integer> map = null;
        if (numEqs == 1) {
          map = eq.findFailureMap(gAlg.getAlgebra(), report);
          return map;
        }
        for (Equation equ : eqs) {
          report.addStartLine("Testing if " + equ);
          map = eq.findFailureMap(gAlg.getAlgebra(), report);
          if (map == null) report.addEndingLine(equ + " holds in " + gAlg.toString());
          else report.addEndingLine(eq + " fails in " + gAlg.toString() + " under " + map);
        }
        return map;
      }
      public void onCompletion(Map<Variable,Integer> map, Throwable exception, 
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
          java.util.List<Term> terms = new ArrayList<Term>(2);
          terms.add(eq.leftSide());
          terms.add(eq.rightSide());
          ttm.setTerms(terms);
          if (map == null) {
            report.addEndingLine(eq + " holds in " + gAlg.toString());
            ttm.setDescription(desc + ": it does!");
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          else {
            report.addEndingLine(eq + " fails in " + gAlg.toString() + " under " + map);
            ttm.setDescription(desc + ": it fails under " + map);
            updateResultTextField(this, ttm);
            uacalcUI.repaint();
          }
          if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(eqCheckTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(eqCheckTask);
  }
  
  
  
  public void formPowerAlgebra() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    int pow = getNumberDialog(2, "power?",  "Power Algebra");
    if (!(pow > 1)) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning("power should be at least 2", false);
      return;
    }
    PowerAlgebra powAlg = new PowerAlgebra(alg.getName() + "^" + pow,alg, pow);
    MainController mc = uacalcUI.getMainController();
    mc.addAlgebra(powAlg, true);
  }
  
  public void formSubAlgebra() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int n = alg.cardinality() - 1;
    String gensStr = JOptionPane.showInputDialog(uacalcUI.getFrame(), 
        "<html>Input a set of generators of the subalebra you want in a form like:<br> " 
        + "    2  7 6. Numbers above " + n + " will be ignored.</html>", 
        "Generators", JOptionPane.QUESTION_MESSAGE);
    if (gensStr == null) return;  // user cancelled
    int[] gens = IntArray.stringToArray(gensStr);
    Subalgebra sAlg = alg.sub().Sg(gens);
    sAlg.setDescription("Subalgebra of " + gAlg.toString(true) + " generated by " + gensStr);
    MainController mc = uacalcUI.getMainController();
    mc.addAlgebra(sAlg, true);
  }
  
  public void formQuotientAlgebra() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final int n = alg.cardinality() - 1;
    String partStr = JOptionPane.showInputDialog(uacalcUI.getFrame(), 
        "<html>Input a partition generating the congruence you want in a form like:<br> " 
        + "    |2 4|3 7 6|. Numbers above " + n + " will be ignored.</html>", 
        "Generatoring Partition", JOptionPane.QUESTION_MESSAGE);
    if (partStr == null) return;  // user cancelled
    Partition part = new BasicPartition(partStr, n + 1);
    final int BIG_INPUT_SIZE = 25000; // possibly change
    if (alg.inputSize() > BIG_INPUT_SIZE) {
      JOptionPane.showConfirmDialog(uacalcUI.getFrame(), 
          "This is a big algebra; it may take a long time to compute. Making a backgroud task.", "Large Algebra", JOptionPane.OK_CANCEL_OPTION);
      setupQuotientAlgebraTask(part, partStr);
      return;
    }
    Partition cong = null;
    try {
      cong = alg.con().Cg(part);
    }
    catch (OutOfMemoryError ex) {  // this shouldn't happen anymore.
      JOptionPane.showMessageDialog(uacalcUI.getFrame(), "Insuffient memory, aborting", 
                                    "Insuffient Memory", JOptionPane.ERROR_MESSAGE);
      return;
    }
    int ans = JOptionPane.showConfirmDialog(uacalcUI.getFrame(), 
        "form quotient by " + cong.toString(120), "Confirm", JOptionPane.OK_CANCEL_OPTION);
    if (ans == JOptionPane.CANCEL_OPTION) return;
    QuotientAlgebra qAlg = new QuotientAlgebra(alg, cong);
    qAlg.setDescription("Quotient of " + gAlg.toString(true) + " by " + cong);
    MainController mc = uacalcUI.getMainController();
    mc.addAlgebra(qAlg, false);
  }
  
  
  public void setupQuotientAlgebraTask(final Partition part, final String partStr) {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    final ProgressReport report = new ProgressReport(taskTableModel, uacalcUI.getLogTextArea());
    final TermTableModel ttm = new TermTableModel();
    termTableModels.add(ttm);
    setResultTableColWidths();
    final String desc = "Finding the quotient algebra of " + gAlg.toString(true) 
        + " mod the congruence generated by " + partStr;
    ttm.setDescription(desc);
    uacalcUI.getResultTextField().setText(desc);
    uacalcUI.repaint();
    final BackgroundTask<QuotientAlgebra>  quotAlgTask = new BackgroundTask<QuotientAlgebra>(report) {
      public QuotientAlgebra compute() {
        //monitorPanel.getProgressMonitor().reset();
        report.addStartLine(desc);
        report.setDescription(desc);
        report.addStartLine("Finding the congruence generated by your paritition.");
        report.addLine("Using the algorithm of Freese, \"Computing congruences efficiently,\"" 
            + " AU 59(2008) 337-343.");
        report.addLine("The size field reports the stack size of that algorithm.");
        Partition cong = alg.con().Cg(part, report);
        report.addEndingLine("Done find the congruence. It has " + cong.numberOfBlocks() + " blocks.");
        report.setSize(0);
        //java.util.Set<Partition> ans = alg.con().universe(report);
        if (Thread.currentThread().isInterrupted()) {
          report.addEndingLine("cancelled ...");
          return null;
        }
        //alg.con().typeSet(report);  // make want to make this optional
        report.addStartLine("Constructing the quotient algebra.");
        QuotientAlgebra qAlg = new QuotientAlgebra("quotient of " 
                                    + alg.getName() + " mod " + cong.toString(40),alg, cong);
        report.addEndingLine("Done. It has " + qAlg.cardinality() + " elements.");
        return qAlg;
      }
      public void onCompletion(QuotientAlgebra qAlg, Throwable exception, 
                               boolean cancelled, boolean outOfMemory) {
        if (outOfMemory) {
          report.addEndingLine("Out of memory!!!");
          ttm.setDescription(desc + " (insufficient memory)");
          //updateResultTextField(this, ttm);
          return;
        }
        if (!cancelled) {
          if (qAlg == null) {
            System.out.println("The quotient algebra was null. This should not happen.");
          }
          else {
            report.addEndingLine("Found the quotient algebra: " + qAlg);
            MainController mc = uacalcUI.getMainController();
            mc.addAlgebra(qAlg, false);
          }
          //if ( this.equals(getCurrentTask())) setResultTableColWidths();
        }
        else {
          report.addEndingLine("Computation cancelled");
          ttm.setDescription(desc + " (cancelled)");
          updateResultTextField(this, ttm);
          uacalcUI.repaint();
        }
      }
    };
    addTask(quotAlgTask);
    MainController.scrollToBottom(uacalcUI.getComputationsTable());
    //uacalcUI.getResultTable().setModel(ttm);
    BackgroundExec.getBackgroundExec().execute(quotAlgTask);
  }
  
  
  public void formRabbitEarsAlgebra() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    // pop up something with an explanation of what this is
    String numStr = JOptionPane.showInputDialog(uacalcUI.getFrame(), 
        "<html>Input 2 numbers less than " 
        + alg.cardinality() + ", separated by a space or comma,<br> " 
        + "for the connecting points a and b.</html>", 
        "Connecting Points a and b", JOptionPane.QUESTION_MESSAGE);
    if (numStr == null) return;  // user cancelled
    String[] numsArr = numStr.split("[,\\s]+");
    if (numsArr.length < 2) return;
    List<Integer> connectPts = new ArrayList<Integer>(2);
    final int a = Integer.parseInt(numsArr[0]);
    final int b = Integer.parseInt(numsArr[1]);
    final String minName = alg.getName();
    connectPts.add(a);
    connectPts.add(b);
    SmallAlgebra ans = new AlgebraFromMinimalSets("REars-" + 
        (minName == null ? "" : minName) + "-" + a +"-" + b, alg, connectPts);
    ans.convertToDefaultValueOps();
    uacalcUI.getMainController().addAlgebra(ans, true);
  }
  
  public void formMatrixPowerAlgebra() {
    final GUIAlgebra gAlg = uacalcUI.getMainController().getCurrentAlgebra();
    if (!isAlgOK(gAlg)) return;
    final SmallAlgebra alg = gAlg.getAlgebra();
    int pow = getNumberDialog(2, "power?",  "Matrix Power Algebra");
    if (!(pow > 1)) {
      uacalcUI.getMainController().beep();
      uacalcUI.getMainController().setUserWarning("power should be at least 2", false);
      return;
    }
    // The MatrixPowerAlgebra class has some errors that I can't fix right now so going back to basic
    //SmallAlgebra matPowAlg = new MatrixPowerAlgebra("", alg, pow);  // MatrixPowerAlgebra needs fixing
    SmallAlgebra matPowAlg = Algebras.matrixPower(alg, pow);
    matPowAlg.convertToDefaultValueOps();
    MainController mc = uacalcUI.getMainController();
    mc.addAlgebra(matPowAlg, true);
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
