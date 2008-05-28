package org.uacalc.nbui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.prefs.*;

import org.uacalc.alg.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.Operations;
import org.uacalc.lat.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;
import org.uacalc.io.*;
import org.uacalc.ui.*;
import org.uacalc.ui.table.*;
import org.uacalc.ui.tm.ProgressReport;


public class Actions {

  private boolean dirty = false;
  private UACalculatorUI uacalcUI;
  private SmallAlgebra currentAalgebra;  // Small ??
  private java.util.List<SmallAlgebra> algebraList = new ArrayList<SmallAlgebra>();
  private File currentFile;
  private String title = "";  // if currentFile is null this might be "New"
  private String progName = "UACalculator   ";
  private String currentFolder;
  
  private Tabs tabs;
  private final Random random = new Random();
  
  public Actions(UACalculatorUI uacalcUI) {
    this.uacalcUI = uacalcUI;
  }
  
  public File getCurrentFile() { return currentFile; }
  public void setCurrentFile(File f) { currentFile = f; }
  
  
  public void quit() {
    if (isDirty()) {
      if (checkSave()) System.exit(0);
    }
    else System.exit(0);
  }
  
  
  public boolean checkSave() {
    Object[] options = {"Save", "Discard", "Cancel"};
    int n = JOptionPane.showOptionDialog(uacalcUI,
                                         "Do you want to save your algebra?",
                                         "Save Your Algebra?",
                                         JOptionPane.YES_NO_CANCEL_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         options,
                                         options[0]);
    if (n == JOptionPane.YES_OPTION) {
         try {
           return save();
         }
         catch (IOException ex) {
           System.err.println("IO error in saving: " + ex.getMessage());
           //setUserWarning("Error 187. File Not Saved. There is "
           //               + "something wrong with your file.");
         }
         return false;
    }
    else if (n == JOptionPane.NO_OPTION) {
      return true;
    }
    else {
      return false;
    }
 }




/*
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
    
    icon = new ImageIcon(cl.getResource("org/uacalc/ui/images/Save16.gif"));
    JMenuItem saveMI = (JMenuItem) file.add(new JMenuItem("Save", icon));
    saveMI.setMnemonic(KeyEvent.VK_S);
    KeyStroke cntrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
    saveMI.setAccelerator(cntrlS);

    icon = new ImageIcon(cl.getResource("org/uacalc/ui/images/SaveAs16.gif"));

    JMenu saveAsMenu = (JMenu)file.add(new JMenu("Save As"));
    JMenuItem saveAsUAMI
      = (JMenuItem)saveAsMenu.add(new JMenuItem("ua file (new format", icon));
    JMenuItem saveAsAlgMI
      = (JMenuItem)saveAsMenu.add(new JMenuItem("alg file (old format)", icon));

    saveAsUAMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            saveAs(ExtFileFilter.UA_EXT);
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

    saveMI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          save();
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
          if (getCurrentAlgebra() != null) drawCon(getCurrentAlgebra());
        }
      });

    JMenuItem drawSubMI = (JMenuItem)draw.add(new JMenuItem("Sub"));

    drawSubMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getCurrentAlgebra() != null) drawSub(getCurrentAlgebra());
        }
      });

    JMenuItem drawBelindaMI =
                    (JMenuItem)draw.add(new JMenuItem("Belinda"));
    drawBelindaMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getCurrentAlgebra() != null) drawBelinda(getCurrentAlgebra());
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

  }
*/
  

  public void addAlgebra(SmallAlgebra alg) {
    addAlgebra(alg, null);
  }
  
  /**
   * right now the list of algebras is maintained the algebraTableModel.
   * We may want to change that.
   */
  public void addAlgebra(SmallAlgebra alg, File file) {
    // TODO:
    //bottomPanel.getAlgebraTableModel().addAlgebra(alg, file);
    //bottomPanel.scrollToBottom();
  }
  
  // TODO: soon each algebra will have a separate monitorPanel and these
  // will change.
  //public MonitorPanel getMonitorPanel() { return monitorPanel; }
  
  //public ProgressReport getMonitor() { return monitorPanel.getProgressReport(); }
  
  public LatDrawPanel getLatDrawPanel() {
    return tabs.getLatticeDrawer();
  }
  
  public AlgebraEditor getAlgebraEditor() {
    return tabs.getAlgebraEditor();
  }
  
  public void resetToolBar() {
    // TODO: fix these
    //mainPanel.remove(toolBar);
    //toolBar = tabs.getCurrentToolBar();
    //mainPanel.add(toolBar, BorderLayout.NORTH);
    uacalcUI.validate();
    uacalcUI.repaint();
  }
  

  public void drawSub(SmallAlgebra alg) {
    final int maxSize = 100;
    if (alg.sub().cardinality() > maxSize) {
      JOptionPane.showMessageDialog(uacalcUI,
          "<html>This subalgebra lattice has " + alg.sub().cardinality() + " elements.<br>"
          + "At most " + maxSize + " elements allowed.</html>",
          "Sub Too Big",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    BasicLattice lat = new BasicLattice("", alg.sub());
    try {
      getLatDrawPanel().setDiagram(lat.getDiagram());
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    uacalcUI.repaint();
  }


  public void drawCon(SmallAlgebra alg) {
    final int maxSize = 100;
    if (alg.con().cardinality() > maxSize) {
      JOptionPane.showMessageDialog(uacalcUI,
          "<html>This congruence lattice has " + alg.con().cardinality() + " elements.<br>"
          + "At most " + maxSize + " elements allowed.</html>",
          "Con Too Big",
          JOptionPane.WARNING_MESSAGE);
      System.out.println("Con has " + alg.con().cardinality() + " elements");
      System.out.println("we allow at most " + maxSize + " elements.");
      return;
    }
    BasicLattice lat = new BasicLattice("", alg.con(), true);
    try {
      getLatDrawPanel().setDiagram(lat.getDiagram());
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
    uacalcUI.repaint();
  }


  public void drawBelinda(SmallAlgebra alg) {
    final int maxSize = 100;
    if (alg.cardinality() > maxSize) {
      JOptionPane.showMessageDialog(uacalcUI,
          "<html>This algebra has " + alg.cardinality() + " elements.<br>"
          + "At most " + maxSize + " elements allowed.</html>",
          "Algebra Too Big",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
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
      JOptionPane.showMessageDialog(uacalcUI,
          "<html>This algebra does not have any semilattice operations.<br>",
          "No Lattice Operation",
          JOptionPane.WARNING_MESSAGE);
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
    uacalcUI.repaint();
  }
  
  public boolean save() throws IOException {
    if (getCurrentAlgebra() == null) return true;
    //if (!getAlgebraEditor().sync()) return false;
    File f = getCurrentFile();
    if (f == null) return saveAs(org.uacalc.io.ExtFileFilter.UA_EXT);
    String ext = ExtFileFilter.getExtension(f);
    boolean newFormat = true;
    if (ext.equals(ExtFileFilter.ALG_EXT)) newFormat = false;
    AlgebraIO.writeAlgebraFile(getCurrentAlgebra(), f, !newFormat);
    setDirty(false);
    return true;
  }

  public boolean saveAs(String ext) throws IOException {
    if (getCurrentAlgebra() == null) return true;
    //if (!getAlgebraEditor().sync()) return false;
    boolean newFormat = true;
    if (ext.equals(org.uacalc.io.ExtFileFilter.ALG_EXT)) newFormat = false;
    String pwd = getPrefs().get("algebraDir", null);
    if (pwd == null) pwd = System.getProperty("user.dir");
    JFileChooser fileChooser;
    if (pwd != null)
      fileChooser = new JFileChooser(pwd);
    else
      fileChooser = new JFileChooser();

    fileChooser.addChoosableFileFilter(
         newFormat ? 
         new ExtFileFilter("Alg Files New Format (*.ua, *.xml)", 
                            ExtFileFilter.UA_EXTS) :
         new ExtFileFilter("Alg Files Old Format (*.alg)", 
                            ExtFileFilter.ALG_EXT));
    int option = fileChooser.showSaveDialog(uacalcUI);
    if (option==JFileChooser.APPROVE_OPTION) {
      // save original user selection
      File selectedFile = fileChooser.getSelectedFile();
      File f = selectedFile;
      // if it doesn't end in .brd, add ".brd" even if there already is a "."
      if (f.exists()) {
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(uacalcUI,
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
      AlgebraIO.writeAlgebraFile(getCurrentAlgebra(), f, !newFormat);
      // setModified(false);
      setCurrentFile(f);
      setDirty(false);
      return true;
    }
    return false;
  }






  public void open() {
    String pwd = getPrefs().get("algebraDir", null);
    if (pwd == null) pwd = System.getProperty("user.dir");
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
    int option = fileChooser.showOpenDialog(uacalcUI);

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
      // TODO: add to list of algs
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
      setCurrentAlgebra(a);
      addAlgebra(a, file);
      setDirty(false);
      uacalcUI.repaint();
    }
  }

  public SmallAlgebra getCurrentAlgebra() { return currentAalgebra; }
  
  public void setCurrentAlgebra(SmallAlgebra alg) {
    if (isDirty()) checkSave();
    updateCurrentAlgebra(alg);
    getAlgebraEditor().setAlgebra(alg);
  }
  
  public Random getRandom() {
    return random;
  }
  
  public void setRandomSeed(long seed) {
    random.setSeed(seed);
  }
  
  /**
   * Called from the edit window after the user sync's it.
   * 
   * @param alg
   */
  public void updateCurrentAlgebra(SmallAlgebra alg) {
    currentAalgebra = alg;
    getLatDrawPanel().setDiagram(null);
  }

  public boolean isDirty() { return dirty; }
  
  public void setDirty() {
    setDirty(true);
  }

  public void setDirty(boolean v){
    dirty = v;
    setTitle();
  }
  
  public void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  public void setTitle() {
    if (currentFile == null) {
      uacalcUI.setTitle(progName + title);
    }
    else uacalcUI.setTitle(progName + currentFile.getName() + (dirty ? " **" : ""));
  }
  
  public void setNew() {
    currentFile = null;
    uacalcUI.setTitle("New **");
    setTitle();
  }


  // prefs stuff

  public Preferences getPrefs() {
    return Preferences.userNodeForPackage(uacalcUI.getClass());
  }


  /*
  public static void main(String[] args) {
    String inFile = null;
    if (args.length > 0) {
      inFile = args[0];
      if (args.length > 1 && inFile.equals("-open")) inFile = args[1];
      File theFile = new File(inFile);
      try {
      //board = BoardIO.readBoardFile(theFile);
      //boardFileArg = theFile;
      }
      catch (Exception ex) {}
    }

    Runnable  runner = new FrameShower(args);
    EventQueue.invokeLater(runner);
  }
  */



  /*
  private static class FrameShower implements Runnable {
    
    private final String[] args;
    
    private FrameShower(String[] arguments) {
      args = arguments;
    }

    public void run() {
      UACalculator frame = new UACalculator();
      String inFile = null;
      if (args.length > 0) {
        inFile = args[0];
        if (args.length > 1 && inFile.equals("-open")) inFile = args[1];
        frame.open(new File(inFile));
      }
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      int width = (screenSize.width * 9) / 10;
      int height = (screenSize.height * 9) / 10;
      frame.setLocation((screenSize.width - width) / 2,
                        (screenSize.height - height) / 2);
      frame.setSize(width, height);
      //frame.isDefaultLookAndFeelDecorated();
      frame.setTitle();
      
      
      frame.setVisible(true);
    }
  }
  */

}
