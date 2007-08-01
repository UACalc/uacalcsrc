
package org.uacalc.ui;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.prefs.*;


import org.uacalc.alg.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.Operations;
import org.uacalc.lat.*;
import org.uacalc.io.*;


public class UACalculator extends JFrame {

  private boolean dirty = false;

  private SmallAlgebra algebra;  // Small ??
  private File currentFile;
  private String currentFolder;
  private JPanel mainPanel;
  private JPanel bottomPanel;
  private LatDrawPanel latDrawPanel;
  private NewAlgebraDialog algDialog;

  private Tabs tabs;
  private JToolBar toolBar;
  
  
  
  JCheckBoxMenuItem showDiagLabelsCB;


  public UACalculator() {
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    //closes from title bar and from menu
    addWindowListener(new WindowAdapter() {
        public void windowClosing (WindowEvent e) {
          if (isDirty()) {
            if (checkSave()) {
              System.exit(0);
            }
          }
          else {
            System.exit(0);
          }
        }
      });
    buildMenu();

    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    tabs = new Tabs(this);
    toolBar = tabs.getCurrentToolBar();
    System.out.println("toolBar = " + toolBar + "\n");
    mainPanel.add(toolBar, BorderLayout.NORTH);
//    mainPanel.add(toolBar, BorderLayout.NORTH);
//    mainPanel.add(shaper, BorderLayout.CENTER);
//    dimensionsPanel = new DimensionsPanel(this);
//    pointPanel = new PointPanel(this);
    bottomPanel = new JPanel();
    bottomPanel.setBackground(Color.CYAN);
    bottomPanel.setLayout(new BorderLayout());
    bottomPanel.add(new JLabel("Testing..."));
//    bottomPanel.add(pointPanel, BorderLayout.EAST);
//    bottomPanel.add(dimensionsPanel, BorderLayout.CENTER);
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    //latDrawPanel = new LatDrawPanel(this);
    //mainPanel.add(latDrawPanel, BorderLayout.CENTER);
    mainPanel.add(tabs, BorderLayout.CENTER);

    buildMenu();
    setContentPane(mainPanel);

  }

  private void buildMenu() {
    // Instantiates JMenuBar, JMenu and JMenuItem
    JMenuBar menuBar = new JMenuBar();

    // the file menu
    JMenu file = (JMenu) menuBar.add(new JMenu("File"));
    file.setMnemonic(KeyEvent.VK_F);

    ClassLoader cl = this.getClass().getClassLoader();


    ImageIcon icon = new ImageIcon(cl.getResource(
                             "org/uacalc/ui/images/New16.gif"));

    JMenu newMenu = (JMenu)file.add(new JMenu("New"));
    JMenuItem newTablesMI = (JMenuItem)newMenu.add(new JMenuItem("New (Tables)", icon));
    newTablesMI.setMnemonic(KeyEvent.VK_N);
    KeyStroke cntrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.CTRL_MASK);
    newTablesMI.setAccelerator(cntrlN);
    JMenuItem newScriptMI = (JMenuItem)newMenu.add(new JMenuItem("New (Script)", icon));
    
    newTablesMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //newTableAlgebra();
      }
    });


    file.add(new JSeparator());

    ImageIcon openIcon = new ImageIcon(cl.getResource(
                             "org/uacalc/ui/images/Open16.gif"));
    JMenuItem openMI = (JMenuItem)file.add(new JMenuItem("Open", openIcon));
    openMI.setMnemonic(KeyEvent.VK_O);
    KeyStroke cntrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O,Event.CTRL_MASK);
    openMI.setAccelerator(cntrlO);

    icon = new ImageIcon(cl.getResource("org/uacalc/ui/images/SaveAs16.gif"));

    JMenu saveAsMenu = (JMenu)file.add(new JMenu("Save As"));
    JMenuItem saveAsXMLMI
      = (JMenuItem)saveAsMenu.add(new JMenuItem("XML file", icon));
    JMenuItem saveAsAlgMI
      = (JMenuItem)saveAsMenu.add(new JMenuItem("alg file (old format)", icon));

    saveAsXMLMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            saveAs(ExtFileFilter.XML_EXT);
          }
          catch (IOException ex) {
            System.err.println("IO error in saving: " + ex.getMessage());
          }
        }
      });

    saveAsAlgMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            saveAs(ExtFileFilter.ALG_EXT);
          }
          catch (IOException ex) {
            System.err.println("IO error in saving: " + ex.getMessage());
          }
        }
      });


    JMenuItem exitMI = (JMenuItem)file.add(new JMenuItem("Exit"));
    exitMI.setMnemonic(KeyEvent.VK_X);
    KeyStroke cntrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q,Event.CTRL_MASK);
    exitMI.setAccelerator(cntrlQ);

    JMenu draw = (JMenu) menuBar.add(new JMenu("Draw"));

    JMenuItem drawConMI = (JMenuItem)draw.add(new JMenuItem("Con"));

    drawConMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getAlgebra() != null) drawCon(getAlgebra());
        }
      });

    JMenuItem drawSubMI = (JMenuItem)draw.add(new JMenuItem("Sub"));

    drawSubMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getAlgebra() != null) drawSub(getAlgebra());
        }
      });

    JMenuItem drawBelindaMI =
                    (JMenuItem)draw.add(new JMenuItem("Belinda"));
    drawBelindaMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getAlgebra() != null) drawBelinda(getAlgebra());
        }
      });

    JMenu showHide = (JMenu) menuBar.add(new JMenu("Show/Hide"));

    final JCheckBoxMenuItem showDiagLabelsCB = (JCheckBoxMenuItem)showHide.add(
                         new JCheckBoxMenuItem("Show Diagram Labels", true));

    showDiagLabelsCB.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          //shaper.getCurrentPanel().repaint();
          if (getLatDrawPanel().getDiagram() != null) {
            getLatDrawPanel().getDiagram().setPaintLabels(
                                           showDiagLabelsCB.isSelected());
          }
          repaint();
        }
      });



    setJMenuBar(menuBar);

    openMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          //should this if statement go inside the try?
             try {
               open();
             }
             catch (IOException err) {
               err.printStackTrace();
             }
          }
      });

    exitMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.exit(0);
        }
      });

  }
  
  /*
  public void newTableAlgebra() {
    showNewAlgebraDialog();
    mainPanel.remove(latDrawPanel);
    AlgebraTableInputPanel algInput 
        = new AlgebraTableInputPanel(algDialog.getName(), 
                                     algDialog.getCard(), 
                                     algDialog.getDesc());
    mainPanel.add(algInput);
    validate();
  }
  */

  /*
  public void showNewAlgebraDialog() {
    if (algDialog != null) algDialog.dispose();
    //makeAlgebraDialog();
    algDialog = new NewAlgebraDialog(this);
    algDialog.setVisible(true);
    System.out.println(algDialog.getName());
    System.out.println(algDialog.getCard());
    System.out.println(algDialog.getDesc());
  }
  */

  
  public LatDrawPanel getLatDrawPanel() {
    return tabs.getLatticeDrawer();
  }
  
  public AlgebraEditor getAlgebraEditor() {
    return tabs.getAlgebraEditor();
  }
  
  public void resetToolBar() {
    mainPanel.remove(toolBar);
    toolBar = tabs.getCurrentToolBar();
    mainPanel.add(toolBar, BorderLayout.NORTH);
    validate();
    repaint();
  }
  

  public void drawSub(SmallAlgebra alg) {
    if (alg.sub().cardinality() > 50) {
      System.out.println("Sub has " + alg.sub().cardinality() + " elements");
      System.out.println("we allow at most 50 elements.");
      return;
    }
    BasicLattice lat = new BasicLattice("", alg.sub());
    try {
      getLatDrawPanel().setDiagram(lat.getDiagram());
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    repaint();
  }


  public void drawCon(SmallAlgebra alg) {
    if (alg.con().cardinality() > 50) {
      System.out.println("Con has " + alg.con().cardinality() + " elements");
      System.out.println("we allow at most 50 elements.");
      return;
    }
    BasicLattice lat = new BasicLattice("", alg.con(), true);
    try {
      getLatDrawPanel().setDiagram(lat.getDiagram());
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    repaint();
  }


  public void drawBelinda(SmallAlgebra alg) {
    Operation op = null;
    for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
      Operation opx = (Operation)it.next();
      if (Operations.isCommutative(opx) && Operations.isIdempotent(opx)
                                      && Operations.isAssociative(opx)) {
        op = opx;
        break;
      }
    }
    if (op == null) {
      System.out.println("Could not find a semilattice operations.");
      return;
    }
    java.util.List univ = new ArrayList(alg.universe());
    BasicLattice lat = Lattices.latticeFromMeet("", univ, op);
    //LatDrawer.drawLattice(lat);
    try {
      getLatDrawPanel().setDiagram(lat.getDiagram());
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    repaint();
  }

  public boolean saveAs(String ext) throws IOException {
    if (getAlgebra() == null) return true;
    boolean newFormat = true;
    if (ext.equals(ExtFileFilter.ALG_EXT)) newFormat = false;
    String pwd = getPrefs().get("algebraDir", null);
    if (pwd == null) pwd = System.getProperty("user.dir");
    JFileChooser fileChooser;
    if (pwd != null)
      fileChooser = new JFileChooser(pwd);
    else
      fileChooser = new JFileChooser();

    fileChooser.addChoosableFileFilter(
         newFormat ? 
         new ExtFileFilter("Alg Files New Format (*.xml)", 
                            ExtFileFilter.XML_EXT) :
         new ExtFileFilter("Alg Files Old Format (*.alg)", 
                            ExtFileFilter.ALG_EXT));
    int option = fileChooser.showSaveDialog(this);
    if (option==JFileChooser.APPROVE_OPTION) {
      // save original user selection
      File selectedFile = fileChooser.getSelectedFile();
      File f = selectedFile;
      // if it doesn't end in .brd, add ".brd" even if there already is a "."
      if (f.exists()) {
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(this,
                                "The file already exists. Overwrite?",
                                "Algebra Exists",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]);
        if (n == JOptionPane.NO_OPTION) {
          saveAs(ext);
        }
      }
      String extension = ExtFileFilter.getExtension(f);
      if (extension == null || !extension.equals(ext)) {
        f = new File(f.getCanonicalPath() + "." + ext);
      }
      AlgebraIO.writeAlgebraFile(getAlgebra(), f, !newFormat);
      // setModified(false);
      setTitle();
      return true;
    }
    return false;
  }






  public void open() throws IOException {
    String pwd = getPrefs().get("algebraDir", null);
    if (pwd == null) pwd = System.getProperty("user.dir");
    SmallAlgebra a = null;
    File theFile = null;
    //pwd = currentFolder;
    JFileChooser fileChooser;
    if (pwd != null) fileChooser = new JFileChooser(pwd);
    else fileChooser = new JFileChooser();
    //fileChooser.addChoosableFileFilter(
    //     new ExtFileFilter("Shape3D Files (*.s3d)", ExtFileFilter.S3D_EXT));
    //fileChooser.addChoosableFileFilter(
    //     new ExtFileFilter("Board Files (*.brd)", ExtFileFilter.BOARD_EXT));
    //fileChooser.setAccessory(new CurvePreviewer(this, fileChooser));
    int option = fileChooser.showOpenDialog(this);

    if (option==JFileChooser.APPROVE_OPTION) {
      theFile = fileChooser.getSelectedFile();
      currentFolder = theFile.getParent();
      getPrefs().put("algebraDir", theFile.getParent());
      open(theFile);
    }
  }

  public void open(File file) {
    getPrefs().put("algebraDir", file.getParent());
    SmallAlgebra a = null;
    try {
      a = AlgebraIO.readAlgebraFile(file);
    }
    catch (BadAlgebraFileException e) {
      System.err.println("Bad algebra file " + file);
      e.printStackTrace();
      beep();
    }
    catch (IOException e) {
      System.err.println("IO error on file " + file);
      e.printStackTrace();
      beep();
      //setUserMessage("Can't find the file: " + file);
      //getDimensionsPanel().setInfoDialogTextColor(Color.RED);
    }
    catch (NullPointerException e) {
      //setUserMessage("Open Failed. Choose a .brd file or type correctly.");
      //getDimensionsPanel().setInfoDialogTextColor(Color.RED);
      System.err.println("open failed");
      beep();
    }
    if (a != null) {
      //this is to get rid of the left over error messages below
      //setUserMessage("");
      setCurrentFile(file);
      setTitle();
      //setModified(false);
      setAlgebra(a);
    }
  }

  public File getCurrentFile() { return currentFile; }
  public void setCurrentFile(File f) { currentFile = f; }
  


  public SmallAlgebra getAlgebra() { return algebra; }
  
  public void setAlgebra(SmallAlgebra alg) { 
    algebra = alg;
    getAlgebraEditor().setAlgebra(alg);
    getLatDrawPanel().setDiagram(null);
    //getLatDrawPanel().repaint();
  }

  public boolean isDirty() { return dirty; }

  public void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  public void setTitle() { setTitle(currentFile.getName()); }


  public boolean checkSave() { return true; }

  // prefs stuff

  public Preferences getPrefs() {
    return Preferences.userNodeForPackage(this.getClass());
  }


  public static void main(String[] args) {
    UACalculator frame = new UACalculator();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (screenSize.width * 9) / 10;
    int height = (screenSize.height * 9) / 10;
    frame.setLocation((screenSize.width - width) / 2,
                      (screenSize.height - height) / 2);
    frame.setSize(width, height);
    frame.isDefaultLookAndFeelDecorated();

    Runnable  runner = new FrameShower(frame);
    EventQueue.invokeLater(runner);
  }



  private static class FrameShower implements Runnable {
    final JFrame frame;

    public FrameShower(JFrame frame) {
      this.frame = frame;
    }

    public void run() {
      frame.setVisible(true);
      //JOptionPane.showMessageDialog(frame,
      //    "This version of the program is out of date."
      //    + "\nGet the new version at www.aps3000.com"
      //    + "\nClick on Software");
    }
  }



}
