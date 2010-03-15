package org.uacalc.nbui;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import net.miginfocom.swing.MigLayout;

import org.uacalc.ui.table.*;
import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.io.*;

public class UACalculator2 extends JFrame implements UACalc {
  
  private javax.swing.JButton addOpButton;
  private javax.swing.JMenuItem algFileMI;
  private javax.swing.JTable algListTable;
  private javax.swing.JTextField algNameTextField;
  private javax.swing.JPanel algebrasPanel;
  private javax.swing.JMenuItem builtInAlgsMI;
  private javax.swing.JButton cancelCompButton;
  private javax.swing.JTextField cardTextField;
  private javax.swing.JButton clearLogButton;
  private javax.swing.JPanel computationsLogPane;
  private javax.swing.JPanel computationsPanel;
  private javax.swing.JTable computationsTable;
  private javax.swing.JButton conDiagButton;
  private javax.swing.JPanel conLeftPanel;
  private javax.swing.JPanel conMainPanel;
  private javax.swing.JPanel conPanel;
  private javax.swing.JButton conTableButton;
  private javax.swing.JPanel currentAlgPanel;
  private javax.swing.JComboBox defaultEltComboBox;
  private javax.swing.JButton delAlg;
  private javax.swing.JButton delOpButton;
  private javax.swing.JTextField descTextField;
  private javax.swing.JMenuItem distributivityMI;
  private javax.swing.JMenuItem drawAlgMI;
  private javax.swing.JMenuItem drawConMI;
  private javax.swing.JMenuItem drawSubMI;
  private javax.swing.JPanel drawingLeftPanel;
  private javax.swing.JPanel drawingMainPanel;
  private javax.swing.JMenu drawingMenu;
  private javax.swing.JPanel drawingPanel;
  private javax.swing.JMenu editMenu;
  private javax.swing.JPanel editorPanel;
  private javax.swing.JMenu fileMenu;
  private javax.swing.JMenuItem freeAlgMI;
  private javax.swing.JMenu hspMenu;
  private javax.swing.JCheckBox idempotentCB;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JMenuBar jMenuBar1;
  //private javax.swing.JPanel jPanel1;
  //private javax.swing.JPanel jPanel2;
  //private javax.swing.JPanel jPanel3;
  //private javax.swing.JPanel jPanel4;
  //private javax.swing.JPanel jPanel5;
  //private javax.swing.JPanel jPanel6;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JScrollPane jScrollPane4;
  private javax.swing.JScrollPane jScrollPane5;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JTable jTable1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JTextArea logTextArea;
  private javax.swing.JMenuItem majorityMI;
  private javax.swing.JMenuItem maltsevMI;
  private javax.swing.JMenu maltsevMenu;
  private javax.swing.JMenuItem membershipTestMI;
  private javax.swing.JMenuItem mmstMI;
  private javax.swing.JMenuItem modularityMI;
  private javax.swing.JTextField msgTextField;
  private javax.swing.JMenuItem nPermMI;
  private javax.swing.JButton newAlgButton;
  private javax.swing.JMenuItem newMI;
  private javax.swing.JMenuItem nuMI;
  private javax.swing.JTable opTable;
  private javax.swing.JScrollPane opTableScrollPane;
  private javax.swing.JButton openButton;
  private javax.swing.JMenuItem openMI;
  private javax.swing.JComboBox opsComboBox;
  private javax.swing.JMenuItem pixleyMI;
  private javax.swing.JMenuItem powMI;
  private javax.swing.JMenuItem primalMI;
  private javax.swing.JMenuItem prodMI;
  private javax.swing.JMenuItem quitMI;
  private javax.swing.JMenuItem quotMI;
  private javax.swing.JLabel resultDescLabel;
  private javax.swing.JPanel resultPane;
  private javax.swing.JTable resultTable;
  private javax.swing.JTextField resultTextField;
  private javax.swing.JMenu saveAsMenu;
  private javax.swing.JMenuItem saveMI;
  private javax.swing.JPanel subLeftPanel;
  private javax.swing.JMenuItem subMI;
  private javax.swing.JPanel subMainPanel;
  private javax.swing.JPanel subPanel;
  private javax.swing.JMenuItem subPowerMI;
  private javax.swing.JMenuItem subpowMI;
  private javax.swing.JTabbedPane tabbedPane;
  private javax.swing.JMenuItem tableCSVMI;
  private javax.swing.JMenu tasksMenu;
  private javax.swing.JMenuItem uaFileMI;


  private final MainController actions;
  private final int frameWidth;
  private final int frameHeight;

  /** Creates new form UACalculatorUI */
  public UACalculator2(String[] args) {
    this(args, 600, 600);
  }

  /** Creates new form UACalculatorUI */
  public UACalculator2(String[] args, int wd, int ht) {
    frameWidth = wd;
    frameHeight = ht;
    String inFile = null;

    initComponents();

    actions = new MainController(this);

    try {
      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    }
    catch (Exception ex) {
      ex.printStackTrace();
      // go with the default L&F
    }
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    //closes from title bar and from menu
    addWindowListener(new WindowAdapter() {
      public void windowClosing (WindowEvent e) {
        if (getMainController().isDirty()) {
          if (getMainController().checkSave()) {
            System.exit(0);
          }
        }
        else {
          System.exit(0);
        }
      }
    });
    if (args.length != 0) {
      for (int i = 0; i < args.length; i++) {
        System.out.println("args[" + i + "] = " + args[i]);
      }
      inFile = args[0];
      if (args.length > 1 && inFile.equals("-open")) inFile = args[1];
      File theFile = new File(inFile);

      actions.open(theFile);


    }
  }

  private void initComponents() {
    //jPanel1 = new javax.swing.JPanel();//panel for editor tab
    //jPanel2 = new javax.swing.JPanel();//panel for algebras tab
    //jPanel3 = new javax.swing.JPanel();//panel for computations tab
    //jPanel4 = new javax.swing.JPanel();//panel for con
    //jPanel5 = new javax.swing.JPanel();//panel for sub tab
    //jPanel6 = new javax.swing.JPanel();//panel for drawing tab
    jScrollPane1 = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    tabbedPane = new javax.swing.JTabbedPane();
    editorPanel = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    newAlgButton = new javax.swing.JButton();
    openButton = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    algNameTextField = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    cardTextField = new javax.swing.JTextField();
    jLabel3 = new javax.swing.JLabel();
    descTextField = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    opsComboBox = new javax.swing.JComboBox();
    delOpButton = new javax.swing.JButton();
    addOpButton = new javax.swing.JButton();
    opTableScrollPane = new javax.swing.JScrollPane();
    opTable = new javax.swing.JTable();
    idempotentCB = new javax.swing.JCheckBox();
    jLabel5 = new javax.swing.JLabel();
    defaultEltComboBox = new javax.swing.JComboBox();
    algebrasPanel = new javax.swing.JPanel();
    currentAlgPanel = new javax.swing.JPanel();
    computationsPanel = new javax.swing.JPanel();
    resultPane = new javax.swing.JPanel();
    jScrollPane4 = new javax.swing.JScrollPane();
    
    resultTable = new javax.swing.JTable();
    resultTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    resultTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }
    ));
    resultTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jScrollPane4.setViewportView(resultTable);
    
    resultDescLabel = new javax.swing.JLabel();
    resultTextField = new javax.swing.JTextField();
    computationsLogPane = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    computationsTable = new javax.swing.JTable();
    cancelCompButton = new javax.swing.JButton();
    clearLogButton = new javax.swing.JButton();
    jScrollPane3 = new javax.swing.JScrollPane();
    logTextArea = new javax.swing.JTextArea();
    conPanel = new javax.swing.JPanel();
    conLeftPanel = new javax.swing.JPanel();
    conDiagButton = new javax.swing.JButton();
    conTableButton = new javax.swing.JButton();
    conMainPanel = new javax.swing.JPanel();
    subPanel = new javax.swing.JPanel();
    subLeftPanel = new javax.swing.JPanel();
    subMainPanel = new javax.swing.JPanel();
    drawingPanel = new javax.swing.JPanel();
    drawingLeftPanel = new javax.swing.JPanel();
    drawingMainPanel = new javax.swing.JPanel();
    jScrollPane5 = new javax.swing.JScrollPane();
    algListTable = new javax.swing.JTable();
    jLabel6 = new javax.swing.JLabel();
    msgTextField = new javax.swing.JTextField();
    delAlg = new javax.swing.JButton();
    jMenuBar1 = new javax.swing.JMenuBar();
    fileMenu = new javax.swing.JMenu();
    builtInAlgsMI = new javax.swing.JMenuItem();
    newMI = new javax.swing.JMenuItem();
    openMI = new javax.swing.JMenuItem();
    saveMI = new javax.swing.JMenuItem();
    saveAsMenu = new javax.swing.JMenu();
    uaFileMI = new javax.swing.JMenuItem();
    algFileMI = new javax.swing.JMenuItem();
    tableCSVMI = new javax.swing.JMenuItem();
    quitMI = new javax.swing.JMenuItem();
    editMenu = new javax.swing.JMenu();
    hspMenu = new javax.swing.JMenu();
    quotMI = new javax.swing.JMenuItem();
    subMI = new javax.swing.JMenuItem();
    prodMI = new javax.swing.JMenuItem();
    powMI = new javax.swing.JMenuItem();
    subpowMI = new javax.swing.JMenuItem();
    tasksMenu = new javax.swing.JMenu();
    freeAlgMI = new javax.swing.JMenuItem();
    membershipTestMI = new javax.swing.JMenuItem();
    subPowerMI = new javax.swing.JMenuItem();
    primalMI = new javax.swing.JMenuItem();
    maltsevMenu = new javax.swing.JMenu();
    distributivityMI = new javax.swing.JMenuItem();
    modularityMI = new javax.swing.JMenuItem();
    nPermMI = new javax.swing.JMenuItem();
    maltsevMI = new javax.swing.JMenuItem();
    majorityMI = new javax.swing.JMenuItem();
    pixleyMI = new javax.swing.JMenuItem();
    nuMI = new javax.swing.JMenuItem();
    mmstMI = new javax.swing.JMenuItem();
    drawingMenu = new javax.swing.JMenu();
    drawConMI = new javax.swing.JMenuItem();
    drawSubMI = new javax.swing.JMenuItem();
    drawAlgMI = new javax.swing.JMenuItem();
    
    
    // tie components together and setup stuff
    // skipping the New and Open button for now.
    
    jLabel1.setText("Name:");

    jLabel2.setText("Cardinality:");

    jLabel3.setText("Desc:");

    jLabel4.setText("Operations:");

    opsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Op Yet" }));
    opsComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            opsComboBoxActionPerformed(evt);
        }
    });

    delOpButton.setText("Del");
    delOpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            delOpButtonActionPerformed(evt);
        }
    });

    addOpButton.setText("Add");
    addOpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            addOpButtonActionPerformed(evt);
        }
    });

    opTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String [] {
            "Op", "Table", "Goes", "Here"
        }
    ));
    opTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    opTable.setCellSelectionEnabled(true);
    opTableScrollPane.setViewportView(opTable);

    idempotentCB.setText("Idempotent");
    idempotentCB.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            idempotentCBActionPerformed(evt);
        }
    });

    jLabel5.setText("Default element:");

    defaultEltComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Op Yet" }));
    defaultEltComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            defaultEltComboBoxActionPerformed(evt);
        }
    });

    currentAlgPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Algebra Properties"));
   
    initEditorPanel();
    tabbedPane.addTab("Editor", editorPanel);
    
    initAlgebrasPanel();
    tabbedPane.addTab("Algebras", algebrasPanel);
    
    resultPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));

    resultTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    resultTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }
    ));
    resultTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jScrollPane4.setViewportView(resultTable);

    resultDescLabel.setText("Desc:");
    
    computationsLogPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Tasks"));

    computationsTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null}
        },
        new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
        }
    ));
    computationsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jScrollPane2.setViewportView(computationsTable);

    cancelCompButton.setText("Cancel");
    cancelCompButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelCompButtonActionPerformed(evt);
        }
    });

    clearLogButton.setText("Clear");
    clearLogButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            clearLogButtonActionPerformed(evt);
        }
    });

    logTextArea.setColumns(20);
    logTextArea.setRows(5);
    jScrollPane3.setViewportView(logTextArea);

    initComputationPanel();
    tabbedPane.addTab("Computations", computationsPanel);

    conDiagButton.setText("Diagram");
    conDiagButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            conDiagButtonActionPerformed(evt);
        }
    });

    conTableButton.setText("Table");
    conTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            conTableButtonActionPerformed(evt);
        }
    });
    
    tabbedPane.addTab("Con", conPanel);
    
    tabbedPane.addTab("Sub", subPanel);
    
    tabbedPane.addTab("Drawing", drawingPanel);
    
    algListTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    algListTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null}
        },
        new String [] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }
    ));
    algListTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    algListTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane5.setViewportView(algListTable);
    
    jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Algebras"));
    
    msgTextField.setText("Welcome to the Universal Algebra Calculator!");
    
    delAlg.setText("Delete");
    delAlg.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            delAlgActionPerformed(evt);
        }
    });

    fileMenu.setText("File");

    builtInAlgsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
    builtInAlgsMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/uacalc/ui/images/New16.gif"))); // NOI18N
    builtInAlgsMI.setText("Built In Algs");
    builtInAlgsMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            builtInAlgsMIActionPerformed(evt);
        }
    });
    fileMenu.add(builtInAlgsMI);

    newMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
    newMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/uacalc/ui/images/New16.gif"))); // NOI18N
    newMI.setText("New");
    newMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            newMIActionPerformed(evt);
        }
    });
    fileMenu.add(newMI);

    openMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
    openMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/uacalc/ui/images/Open16.gif"))); // NOI18N
    openMI.setText("Open");
    openMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            openMIActionPerformed(evt);
        }
    });
    fileMenu.add(openMI);
    
    saveMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    saveMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/uacalc/ui/images/Save16.gif"))); // NOI18N
    saveMI.setText("Save");
    saveMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveMIActionPerformed(evt);
        }
    });
    fileMenu.add(saveMI);

    saveAsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/uacalc/ui/images/SaveAs16.gif"))); // NOI18N
    saveAsMenu.setText("Save As");

    uaFileMI.setText("ua file (new format)");
    uaFileMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            uaFileMIActionPerformed(evt);
        }
    });
    saveAsMenu.add(uaFileMI);
    
    algFileMI.setText("alg file (old format)");
    algFileMI.setToolTipText("not yet implemented");
    algFileMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            algFileMIActionPerformed(evt);
        }
    });
    saveAsMenu.add(algFileMI);

    fileMenu.add(saveAsMenu);
    
    tableCSVMI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/uacalc/ui/images/Save16.gif"))); // NOI18N
    tableCSVMI.setText("Save Results Table");
    tableCSVMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            tableCSVMIActionPerformed(evt);
        }
    });
    fileMenu.add(tableCSVMI);

    quitMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
    quitMI.setText("Quit");
    quitMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            quitMIActionPerformed(evt);
        }
    });
    fileMenu.add(quitMI);

    jMenuBar1.add(fileMenu);

    editMenu.setText("Edit");
    jMenuBar1.add(editMenu);

    hspMenu.setText("HSP");

    quotMI.setText("Quotient");
    quotMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            quotMIActionPerformed(evt);
        }
    });
    hspMenu.add(quotMI);

    subMI.setText("Subalgebra");
    subMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            subMIActionPerformed(evt);
        }
    });
    hspMenu.add(subMI);

    prodMI.setText("Product");
    prodMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            prodMIActionPerformed(evt);
        }
    });
    hspMenu.add(prodMI);

    powMI.setText("Power");
    powMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            powMIActionPerformed(evt);
        }
    });
    hspMenu.add(powMI);

    subpowMI.setText("Sub Power");
    subpowMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            subpowMIActionPerformed(evt);
        }
    });
    hspMenu.add(subpowMI);

    jMenuBar1.add(hspMenu);

    tasksMenu.setText("Tasks");

    freeAlgMI.setText("Free Algebra");
    freeAlgMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            freeAlgMIActionPerformed(evt);
        }
    });
    tasksMenu.add(freeAlgMI);

    membershipTestMI.setText("B in V(A) ?");
    membershipTestMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            membershipTestMIActionPerformed(evt);
        }
    });
    tasksMenu.add(membershipTestMI);

    subPowerMI.setText("Sub Power");
    subPowerMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            subPowerMIActionPerformed(evt);
        }
    });
    tasksMenu.add(subPowerMI);

    primalMI.setText("Primality");
    primalMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            primalMIActionPerformed(evt);
        }
    });
    tasksMenu.add(primalMI);

    jMenuBar1.add(tasksMenu);
    
    maltsevMenu.setText("Maltsev");

    distributivityMI.setText("Distributivity");
    distributivityMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            distributivityMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(distributivityMI);

    modularityMI.setText("Modularity");
    modularityMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            modularityMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(modularityMI);

    nPermMI.setText("n-Permutability");
    nPermMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            nPermMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(nPermMI);

    maltsevMI.setText("Maltsev term");
    maltsevMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            maltsevMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(maltsevMI);

    majorityMI.setText("Majority term");
    majorityMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            majorityMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(majorityMI);

    pixleyMI.setText("Pixley term");
    pixleyMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            pixleyMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(pixleyMI);

    nuMI.setText("near unanimity term");
    nuMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            nuMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(nuMI);

    mmstMI.setText("MMS Taylor term");
    mmstMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            mmstMIActionPerformed(evt);
        }
    });
    maltsevMenu.add(mmstMI);

    jMenuBar1.add(maltsevMenu);

    drawingMenu.setText("Drawing");

    drawConMI.setText("Con");
    drawConMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            drawConMIActionPerformed(evt);
        }
    });
    drawingMenu.add(drawConMI);
    
    drawSubMI.setText("Sub");
    drawSubMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            drawSubMIActionPerformed(evt);
        }
    });
    drawingMenu.add(drawSubMI);

    drawAlgMI.setText("Algebra");
    drawAlgMI.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            drawAlgMIActionPerformed(evt);
        }
    });
    drawingMenu.add(drawAlgMI);

    jMenuBar1.add(drawingMenu);

    setJMenuBar(jMenuBar1);
    
    // Con Tab
    MigLayout conLayout = new MigLayout("wrap 2, fill, insets 10");
    getConPanel().setLayout(conLayout);
    getConPanel().add(getConLeftPanel(), "grow");
    getConPanel().add(getConMainPanel(), "grow");
    MigLayout lay = new MigLayout("wrap, fill, insets 10");
    getConLeftPanel().setLayout(lay);
    
    

    
    
    
    
    // Start the layout
    // Tab pane 75%, Algebras pane, 20%, msg part.
    MigLayout layout = new MigLayout("wrap 1, fill, insets 10");
    //MigLayout layout = new MigLayout("nogrid"); //sucked
    getContentPane().setLayout(layout);
    
    
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (screenSize.width * 9) / 10;
    int height = (screenSize.height * 9) / 10;
    setLocation((screenSize.width - width) / 2,
        (screenSize.height - height) / 2);
    setSize(width, height);


    setJMenuBar(jMenuBar1);

    //JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, tabbedPane, jScrollPane5); //too hard
    
    add(tabbedPane, "height 400:600:, grow 82"); //, "grow 75");
    
    //add
    add(jScrollPane5, "height 100:150:250, grow 18, split 2");
    add(delAlg, "align center");


    //msg area:
    add(new JLabel("Msg:"), "split 2");
    add(msgTextField, "growx 100");
    /*
    JPanel bot = new JPanel();
    bot.setLayout(new MigLayout("fillx","[pref!][grow,fill]","[]15[]"));
    bot.add(new JLabel("Msg:"), "grow 0");
    bot.add(msgTextField, "grow 100");
    //add(msgTextField, "dock south");
    add(bot, "dock south");
    */
    
    
    // fix this to check for unsaved algebras.
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
  }
  
  private void initEditorPanel() {
    MigLayout uLM = new MigLayout("nogrid");
    JPanel upper = new JPanel(uLM);
    JPanel lower = new JPanel();
    Dimension zeroSize = new Dimension(0, 0);
    Dimension minSize = new Dimension(100, 100);
    upper.setMinimumSize(minSize);
    upper.setPreferredSize(new Dimension(300, 300));
    lower.setMinimumSize(zeroSize);
    upper.add(new JLabel("Name:"));
    upper.add(algNameTextField, "width 100:120:200, grow 75");
    upper.add(new JLabel("Cardinality:"));
    upper.add(cardTextField, "width 30:60:");
    upper.add(new JLabel("Desc:"));
    upper.add(descTextField, "width 40::, grow 200, wrap 15");
    upper.add(new JLabel("Operations:"));
    upper.add(opsComboBox);
    upper.add(delOpButton);
    upper.add(addOpButton, "wrap 15");
    upper.add(opTableScrollPane, "width 300:2000:, span, wrap");
    upper.add(idempotentCB, "gapx 50");
    upper.add(new JLabel("Default Element:"), "gapx 50");
    upper.add(defaultEltComboBox, "wrap");
    lower.add(new JLabel("An element table will be here"), "align center");
    
    JSplitPane edSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, upper, lower);
    edSplitPane.setDividerLocation(0.5);
    edSplitPane.setBorder(null);
    editorPanel.setLayout(new BorderLayout());
    editorPanel.add(edSplitPane, BorderLayout.CENTER);
  }
  
  private void initAlgebrasPanel() {
    algebrasPanel.setLayout(new MigLayout());
    algebrasPanel.add(new JLabel("Coming soon"), "align center, width 100:800:, wrap");
  }
  
  private void initComputationPanel() {
    Dimension minSize = new Dimension(100, 100);
    Dimension midSize = new Dimension(300, 300);
    resultPane.setMinimumSize(minSize);
    resultPane.setPreferredSize(midSize);
    computationsLogPane.setMinimumSize(minSize);
    computationsLogPane.setPreferredSize(midSize);
    
    
    computationsPanel.setLayout(new MigLayout("nogrid"));
    computationsPanel.add(resultPane, "wrap 15");
    computationsPanel.add(computationsLogPane, "wrap");    
  }
  
  
  
  public void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  
  public JTable getAlgListTable() {
    return algListTable;
  }

  
  public JTextField getAlgNameTextField() {
    return algNameTextField;
  }

   
  public JButton getCancelCompButton() {
    return cancelCompButton;
  }

   
  public JTextField getCardTextField() {
    return cardTextField;
  }

   
  public ComputationsController getComputationsController() {
    return getMainController().getComputationsController();
  }

   
  public JTable getComputationsTable() {
    return computationsTable;
  }

   
  public JPanel getConLeftPanel() {
    return conLeftPanel;
  }

   
  public JPanel getConMainPanel() {
    return conMainPanel;
  }

   
  public JComboBox getDefaultEltComboBox() {
    return defaultEltComboBox;
  }

   
  public JTextField getDescTextField() {
    return descTextField;
  }

   
  public JPanel getDrawingMainPanel() {
    return drawingMainPanel;
  }

   
  public JFrame getFrame() {
    return this;
  }

   
  public JCheckBox getIdempotentCB() {
    return idempotentCB;
  }

   
  public JTextArea getLogTextArea() {
    return logTextArea;
  }

   
  public MainController getMainController() {
    return actions;
  }

   
  public JTextField getMsgTextField() {
    return msgTextField;
  }

   
  public JTable getOpTable() {
    return opTable;
  }

   
  public JComboBox getOpsComboBox() {
    return opsComboBox;
  }

   
  public JTable getResultTable() {
    return resultTable;
  }

   
  public JTextField getResultTextField() {
    return resultTextField;
  }

   
  public JPanel getSubMainPanel() {
    return subMainPanel;
  }

   
  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }
  
  // Actions:
  private void cancelCompButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelCompButtonActionPerformed
    getComputationsController().cancelOrRemoveCurrentTask();
  }//GEN-LAST:event_cancelCompButtonActionPerformed

  private void clearLogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearLogButtonActionPerformed
  // TODO add your handling code here:
  }//GEN-LAST:event_clearLogButtonActionPerformed

  private void quitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMIActionPerformed
      getMainController().quit();
  }//GEN-LAST:event_quitMIActionPerformed

  private void newMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMIActionPerformed
    getAlgebraEditorController().makeNewAlgebra();
  }//GEN-LAST:event_newMIActionPerformed

  private void openMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMIActionPerformed
  // TODO add your handling code here:
      getMainController().open();
  }//GEN-LAST:event_openMIActionPerformed

  private void uaFileMIActionPerformed(java.awt.event.ActionEvent evt) {
    getMainController().saveAs(org.uacalc.io.ExtFileFilter.UA_EXT);
  }

  private void algFileMIActionPerformed(java.awt.event.ActionEvent evt) {                                       
    getMainController().saveAs(org.uacalc.io.ExtFileFilter.ALG_EXT);
  }                                       

  private void newAlgButtonActionPerformed(java.awt.event.ActionEvent evt) {
    getAlgebraEditorController().makeNewAlgebra();
  }

  private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {
    getMainController().open();
  }

  private void addOpButtonActionPerformed(java.awt.event.ActionEvent evt) {
    getAlgebraEditorController().addOp();
  }

  private void idempotentCBActionPerformed(java.awt.event.ActionEvent evt) {
    getAlgebraEditorController().setIdempotent(idempotentCB.isSelected());
  }

  private void opsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opsComboBoxActionPerformed
    getAlgebraEditorController().setCurrentOp();  
  }//GEN-LAST:event_opsComboBoxActionPerformed

  private void delOpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delOpButtonActionPerformed
    getAlgebraEditorController().deleteOp();
  }//GEN-LAST:event_delOpButtonActionPerformed

  private void defaultEltComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultEltComboBoxActionPerformed
    getAlgebraEditorController().defaultEltChangeHandler();
  }//GEN-LAST:event_defaultEltComboBoxActionPerformed

  private void saveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMIActionPerformed
    getMainController().save();
  }//GEN-LAST:event_saveMIActionPerformed

  private void freeAlgMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freeAlgMIActionPerformed
    getComputationsController().setupFreeAlgebraTask();
  }//GEN-LAST:event_freeAlgMIActionPerformed

  private void membershipTestMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_membershipTestMIActionPerformed
    getComputationsController().setupBinVATask();
  }//GEN-LAST:event_membershipTestMIActionPerformed

  private void distributivityMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distributivityMIActionPerformed
    getComputationsController().setupJonssonTermsTask();
  }//GEN-LAST:event_distributivityMIActionPerformed

  private void modularityMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modularityMIActionPerformed
    getComputationsController().setupGummTermsTask();
  }//GEN-LAST:event_modularityMIActionPerformed

  private void nPermMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nPermMIActionPerformed
    getComputationsController().setupHagemannMitschkeTermsTask();
  }//GEN-LAST:event_nPermMIActionPerformed

  private void nuMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuMIActionPerformed
    getComputationsController().setupNUTermTask();
  }//GEN-LAST:event_nuMIActionPerformed

  private void maltsevMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maltsevMIActionPerformed
    getComputationsController().setupMalcevTermTask();
  }//GEN-LAST:event_maltsevMIActionPerformed

  private void majorityMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_majorityMIActionPerformed
    getComputationsController().setupMajorityTermTask();
  }//GEN-LAST:event_majorityMIActionPerformed

  private void pixleyMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pixleyMIActionPerformed
    getComputationsController().setupPixleyTermTask();
  }//GEN-LAST:event_pixleyMIActionPerformed

  private void drawConMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawConMIActionPerformed
    getConController().drawCon();
  }//GEN-LAST:event_drawConMIActionPerformed

  private void drawSubMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawSubMIActionPerformed
    getSubController().drawSub();
  }//GEN-LAST:event_drawSubMIActionPerformed

  private void drawAlgMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawAlgMIActionPerformed
    getDrawingController().drawAlg();
  }//GEN-LAST:event_drawAlgMIActionPerformed

  private void builtInAlgsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_builtInAlgsMIActionPerformed
    getMainController().loadBuiltIn();
  }//GEN-LAST:event_builtInAlgsMIActionPerformed

  private void conDiagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conDiagButtonActionPerformed
    getConController().setDrawer();
  }//GEN-LAST:event_conDiagButtonActionPerformed

  private void conTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conTableButtonActionPerformed
    getConController().setTable();
  }//GEN-LAST:event_conTableButtonActionPerformed

  private void subPowerMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subPowerMIActionPerformed
    getComputationsController().setupSubPowerTask();
  }//GEN-LAST:event_subPowerMIActionPerformed

  private void tableCSVMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableCSVMIActionPerformed
    getMainController().writeCSVTable();
  }//GEN-LAST:event_tableCSVMIActionPerformed

  private void primalMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_primalMIActionPerformed
    getComputationsController().setupPrimalTermsTask();
  }//GEN-LAST:event_primalMIActionPerformed

  private void quotMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quotMIActionPerformed
  // TODO add your handling code here:
  }//GEN-LAST:event_quotMIActionPerformed

  private void subMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMIActionPerformed
  // TODO add your handling code here:
  }//GEN-LAST:event_subMIActionPerformed

  private void prodMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prodMIActionPerformed
  // TODO add your handling code here:
  }//GEN-LAST:event_prodMIActionPerformed

  private void powMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_powMIActionPerformed
    getComputationsController().formPowerAlgebra();
  }//GEN-LAST:event_powMIActionPerformed

  private void subpowMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subpowMIActionPerformed
    getComputationsController().setupSubPowerTask();
  }//GEN-LAST:event_subpowMIActionPerformed

  private void mmstMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmstMIActionPerformed
    getComputationsController().setupMarkovicMcKenzieSiggersTaylorTermTask();
  }//GEN-LAST:event_mmstMIActionPerformed

  private void delAlgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delAlgActionPerformed
    getMainController().removeCurrentAlgebra();
  }//GEN-LAST:event_delAlgActionPerformed

  public AlgebraEditorController getAlgebraEditorController() {
    return getMainController().getAlgebraEditorController();
  }
  
  public ConController getConController() {
    return getMainController().getConController();
  }
  
  public SubController getSubController() {
    return getMainController().getSubController();
  }
  
  public DrawingController getDrawingController() {
    return getMainController().getDrawingController();
  }

  public javax.swing.JPanel getAlgebrasPanel() {
    return algebrasPanel;
  }

  public javax.swing.JPanel getComputationsLogPane() {
    return computationsLogPane;
  }

  public javax.swing.JPanel getComputationsPanel() {
    return computationsPanel;
  }

  public javax.swing.JPanel getConPanel() {
    return conPanel;
  }

  public javax.swing.JPanel getCurrentAlgPanel() {
    return currentAlgPanel;
  }

  public javax.swing.JPanel getDrawingPanel() {
    return drawingPanel;
  }

  public javax.swing.JPanel getEditorPanel() {
    return editorPanel;
  }

  public javax.swing.JMenuItem getNewMI() {
    return newMI;
  }

  public javax.swing.JPanel getResultPane() {
    return resultPane;
  }

  public javax.swing.JPanel getSubPanel() {
    return subPanel;
  }

  public javax.swing.JScrollPane getOpTableScrollPane() {
    return opTableScrollPane;
  }

  public void setOpTableScrollPane(javax.swing.JScrollPane opTableScrollPane) {
    this.opTableScrollPane = opTableScrollPane;
  }

    public void setAlgListTable(javax.swing.JTable algListTable) {
        this.algListTable = algListTable;
    }


    public javax.swing.JMenuItem getDistributivityMI() {
        return distributivityMI;
    }

    public void setDistributivityMI(javax.swing.JMenuItem distributivityMI) {
        this.distributivityMI = distributivityMI;
    }

    public javax.swing.JMenuItem getModularityMI() {
        return modularityMI;
    }

    public void setModularityMI(javax.swing.JMenuItem modularityMI) {
        this.modularityMI = modularityMI;
    }

    public javax.swing.JMenuItem getNPermMI() {
        return nPermMI;
    }

    public void setNPermMI(javax.swing.JMenuItem nPermMI) {
        this.nPermMI = nPermMI;
    }

    public void setResultTable(javax.swing.JTable resultTable) {
        this.resultTable = resultTable;
    }

    public void setConMainPanel(javax.swing.JPanel conMainPanel) {
        this.conMainPanel = conMainPanel;
    }

    public javax.swing.JPanel getSubLeftPanel() {
        return subLeftPanel;
    }

    public javax.swing.JPanel getDrawingLeftPanel() {
        return drawingLeftPanel;
    }

    public javax.swing.JButton getConDiagButton() {
        return conDiagButton;
    }

    public javax.swing.JButton getConTableButton() {
        return conTableButton;
    }
  
  
  /**
   * @param args the command line arguments
   */
  public static void main(final String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          new UACalculator2(args).setVisible(true);
        }
    });
  }
  
  
  /**
   * @param args
   */
  //public static void main(String[] args) {
    // TODO Auto-generated method stub

  //}

}
