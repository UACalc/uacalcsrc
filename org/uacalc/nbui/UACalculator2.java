package org.uacalc.nbui;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import net.miginfocom.swing.MigLayout;

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
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
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
  private javax.swing.JMenuItem tableCVSMI;
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
    jPanel1 = new javax.swing.JPanel();//panel for editor tab
    jPanel2 = new javax.swing.JPanel();//panel for algebras tab
    jPanel3 = new javax.swing.JPanel();//panel for computations tab
    jPanel4 = new javax.swing.JPanel();//panel for con
    jPanel5 = new javax.swing.JPanel();//panel for sub tab
    jPanel6 = new javax.swing.JPanel();//panel for drawing tab
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
    tableCVSMI = new javax.swing.JMenuItem();
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
    
    jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Algebras"));

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
    add(tabbedPane, "height 550:600:, grow 82"); //, "grow 75");
    //add(tabbedPane, "grow 82");
    //JLabel algLabel = new JLabel("Algebras");
    //algLabel.setToolTipText("algebras in the system");
    //add(algLabel);
    
    //add
    add(jScrollPane5, "height 250, grow 18");
    add(delAlg, "align center");
    delAlg.setText("Delete");

    //msg area:
    add(new JLabel("Msg:"), "split 2");
    add(msgTextField, "grow 100");
    /*
    JPanel bot = new JPanel();
    bot.setLayout(new MigLayout("fillx","[pref!][grow,fill]","[]15[]"));
    bot.add(new JLabel("Msg:"), "grow 0");
    bot.add(msgTextField, "grow 100");
    //add(msgTextField, "dock south");
    add(bot, "dock south");
    */
  }
  
  
  @Override
  public void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  @Override
  public JTable getAlgListTable() {
    return algListTable;
  }

  @Override
  public JTextField getAlgNameTextField() {
    return algNameTextField;
  }

  @Override
  public JButton getCancelCompButton() {
    return cancelCompButton;
  }

  @Override
  public JTextField getCardTextField() {
    return cardTextField;
  }

  @Override
  public ComputationsController getComputationsController() {
    return getMainController().getComputationsController();
  }

  @Override
  public JTable getComputationsTable() {
    return computationsTable;
  }

  @Override
  public JPanel getConLeftPanel() {
    return conLeftPanel;
  }

  @Override
  public JPanel getConMainPanel() {
    return conMainPanel;
  }

  @Override
  public JComboBox getDefaultEltComboBox() {
    return defaultEltComboBox;
  }

  @Override
  public JTextField getDescTextField() {
    return descTextField;
  }

  @Override
  public JPanel getDrawingMainPanel() {
    return drawingMainPanel;
  }

  @Override
  public JFrame getFrame() {
    return this;
  }

  @Override
  public JCheckBox getIdempotentCB() {
    return idempotentCB;
  }

  @Override
  public JTextArea getLogTextArea() {
    return logTextArea;
  }

  @Override
  public MainController getMainController() {
    return actions;
  }

  @Override
  public JTextField getMsgTextField() {
    return msgTextField;
  }

  @Override
  public JTable getOpTable() {
    return opTable;
  }

  @Override
  public JComboBox getOpsComboBox() {
    return opsComboBox;
  }

  @Override
  public JTable getResultTable() {
    return resultTable;
  }

  @Override
  public JTextField getResultTextField() {
    return resultTextField;
  }

  @Override
  public JPanel getSubMainPanel() {
    return subMainPanel;
  }

  @Override
  public JTabbedPane getTabbedPane() {
    return tabbedPane;
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
