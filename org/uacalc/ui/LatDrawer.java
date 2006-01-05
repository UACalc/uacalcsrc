
package org.uacalc.ui;


import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import org.uacalc.lat.BasicLattice;
import org.latdraw.orderedset.*;
//import org.latdraw.fca.*;
import org.latdraw.diagram.*;

import org.latdraw.beans.*;

import org.latdraw.beans.*;
import org.latdraw.sample.ExtFileFilter;

//import net.sourceforge.toscanaj.parser.BurmeisterParser;
//import net.sourceforge.toscanaj.model.lattice.*;
//import net.sourceforge.toscanaj.model.context.*;
//import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;


import javax.swing.*;
import java.awt.Color;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * Just a temporary lattice drawer.
 *
 *
 *
 */
public class LatDrawer extends JFrame {

  public static final Color OBJ_COLOR = new Color(204, 255, 204); //light green
  public static final Color ATT_COLOR = new Color(204, 204, 255); //light blue

  private String currentFolder = System.getProperty("user.dir");

  private DrawPanel drawPanel;

  private JPanel mainPanel;
  private JPanel appPanel;
  private JScrollPane objScrollPane;
  private JScrollPane attScrollPane;
  private JToolBar toolBar;
  // some fca stuff
  //private ObjAttTable objTable;
  //private ObjAttTable attTable;
  //private ConceptLattice conceptLattice;

  private static final Dimension scrollDim = new Dimension(200, 250);

  public LatDrawer() {
    this(null, null);
  }

  public LatDrawer(Diagram diag) {
    this(diag, null);
  }

// was ConceptLattice, leave it in case we need it as an example.
  public LatDrawer(Diagram diag, Object cl) {
    super("Lattice Drawing");
    //conceptLattice = cl;
    drawPanel = new DrawPanel(diag);
    PropertyChangeListener changeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent e) {
          if (e.getPropertyName().equals(ChangeSupport.VERTEX_PRESSED)) {
            Diagram diag = drawPanel.getDiagram();
            diag.resetVertices();
            diag.hideLabels();
            Vertex v = (Vertex)e.getNewValue();
            boolean vIsAtt = false;
            boolean vIsObj = false;
/*
            if (conceptLattice != null) {
              Concept c = (Concept)v.getUnderlyingObject();
              objTable.clearSelection();
              attTable.clearSelection();
              for (Iterator it = c.getExtentIterator(); it.hasNext(); ) {
                Object obj = it.next();
                POElem elt = conceptLattice.elemForObject(obj);
                Vertex u = diag.vertexForPOElem(elt);
                u.setLabelPainted(true);
                if (u.equals(v)) vIsObj = true;
                u.setColor(OBJ_COLOR);
                u.setFilled(true);
                int k = conceptLattice.getObjectIndex(obj);
                objTable.addRowSelectionInterval(k, k);
              }
              for (Iterator it = c.getIntentIterator(); it.hasNext(); ) {
                Object obj = it.next();
                POElem elt = conceptLattice.elemForAttribute(obj);
                Vertex u = diag.vertexForPOElem(elt);
                if (u.equals(v)) vIsAtt = true;
                u.setColor(ATT_COLOR);
                u.setFilled(true);
                u.setLabelPainted(true);
                int k = conceptLattice.getAttributeIndex(obj);
                attTable.addRowSelectionInterval(k, k);
              }
            }
*/
            v.setHighlighted(true);
            if (vIsAtt) {
              v.setFilled(true);
              if (vIsObj) v.setColor(Color.WHITE);
              else v.setColor(ATT_COLOR);
            }
            else {
              if (vIsObj) {
                v.setColor(OBJ_COLOR);
                v.setFilled(true);
              }
              else v.setColor(null);
            }
            drawPanel.repaint();
          }
        }
    };
    drawPanel.getChangeSupport().addPropertyChangeListener(changeListener);
    drawPanel.setFocusable(true);
    drawPanel.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          drawPanel.requestFocus();
        }
      });
    drawPanel.addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent e) {
          char key = e.getKeyChar();
          if (key == 'r' || key == 'R') {
            if (drawPanel.isRotating()) drawPanel.stopRotation();
            else drawPanel.startRotation();
          }
          if (key == 's' || key == 'S') drawPanel.stopRotation();
          if (key == '.' || key == '>') drawPanel.rotateOnce();
          if (key == ',' || key == '<') drawPanel.rotateLeft();
          if (key == 'd' || key == 'D') drawPanel.setDraggingAllowed(
                                          !drawPanel.isDraggingAllowed());
          if (key == 'h' || key == 'H') drawPanel.setDraggingHorizontal(
                                          !drawPanel.isDraggingHorizontal());
          if (key == 'i') drawPanel.improveWithDelay(40);
          if (key == 'I') drawPanel.improve();
          if (key == 'p') drawPanel.printDialog();
          if (key == 'P') drawPanel.writeRSFDiagram("outx.ps");
          if (key == '+') {
            drawPanel.increaseReplusion();
            drawPanel.decreaseAttraction();
          }
          if (key == '-') {
            drawPanel.increaseAttraction();
            drawPanel.decreaseReplusion();
          }
          if (key == 'q' || key == 'Q') System.exit(0);
        }
      });


    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
    if (cl != null) {
      //objTable = new ObjAttTable(cl, this, false);
      //attTable = new ObjAttTable(cl, this, true);
      //objTable.setSelectionBackground(OBJ_COLOR);
    }
    buildMenuBar();
    toolBar = makeToolBar();
    appPanel = new JPanel();
    appPanel.setLayout(new BorderLayout());
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    setContentPane(mainPanel);
  }

  public DrawPanel getDrawPanel() { return drawPanel; }

  public JToolBar getToolBar() { return toolBar; }

  //public ObjAttTable getObjTable() { return objTable; }

  //public ObjAttTable getAttTable() { return attTable; }

/*
  private void setConceptLattice(ConceptLattice cl) {
    if (objScrollPane != null) {
      appPanel.remove(objScrollPane);
      appPanel.remove(attScrollPane);
      mainPanel.remove(appPanel);
    }
    conceptLattice = cl;
    if (cl != null) {
      objTable = new ObjAttTable(cl, this, false);
      attTable = new ObjAttTable(cl, this, true);
      objScrollPane = new JScrollPane(objTable);
      objTable.setSelectionBackground(OBJ_COLOR);
      attScrollPane = new JScrollPane(attTable);
      objScrollPane.setPreferredSize(scrollDim);
      attScrollPane.setPreferredSize(scrollDim);
      appPanel.add(attScrollPane, BorderLayout.NORTH);
      appPanel.add(objScrollPane, BorderLayout.SOUTH);
      mainPanel.add(appPanel, BorderLayout.WEST);
    }
    else {
      objScrollPane = null;
      attScrollPane = null;
      objTable = null;
      attTable = null;
    }
    validate();
  }
*/

  // Object was ConceptLattice
  public void setContent(DrawPanel dp, JToolBar tb, Object cl) {
    drawPanel = dp;
    //conceptLattice = cl;
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(tb, BorderLayout.NORTH);
    rightPanel.add(drawPanel, BorderLayout.CENTER);
    mainPanel.add(rightPanel, BorderLayout.CENTER);
    //if (cl != null) setConceptLattice(cl);
    //else 
    validate();
  }

  private void buildMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // the file menu
    JMenu file = (JMenu) menuBar.add(new JMenu("File"));
    file.setMnemonic(KeyEvent.VK_F);

    ClassLoader cl = this.getClass().getClassLoader();
    ImageIcon icon = new ImageIcon(cl.getResource(
                              "org/latdraw/sample/images/Open16.gif"));

    JMenuItem openMI = (JMenuItem)file.add(new JMenuItem("Open", icon));
    openMI.setMnemonic(KeyEvent.VK_O);
    KeyStroke cntrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O,Event.CTRL_MASK);
    openMI.setAccelerator(cntrlO);

    file.add(new JSeparator());

    JMenuItem exitMI = (JMenuItem)file.add(new JMenuItem("Exit"));
    exitMI.setMnemonic(KeyEvent.VK_X);
    KeyStroke cntrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q,Event.CTRL_MASK);
    exitMI.setAccelerator(cntrlQ);

    openMI.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
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

    JMenu dragging = (JMenu)menuBar.add(new JMenu("Dragging"));
    ButtonGroup draggingGroup = new ButtonGroup();
    JRadioButtonMenuItem noDrag = new JRadioButtonMenuItem("None");
    JRadioButtonMenuItem horizDrag = new JRadioButtonMenuItem("Horizontal");
    JRadioButtonMenuItem allDrag = new JRadioButtonMenuItem("All");
    noDrag.setMnemonic(KeyEvent.VK_N);
    horizDrag.setMnemonic(KeyEvent.VK_H);
    allDrag.setMnemonic(KeyEvent.VK_A);
    KeyStroke key = 
        KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.SHIFT_MASK+Event.CTRL_MASK);
    noDrag.setAccelerator(key);
    key=KeyStroke.getKeyStroke(KeyEvent.VK_H,Event.SHIFT_MASK+Event.CTRL_MASK);
    horizDrag.setAccelerator(key);
    key=KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.SHIFT_MASK+Event.CTRL_MASK);
    allDrag.setAccelerator(key);
    noDrag.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.setDraggingAllowed(false);
        }
    });
    horizDrag.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.setDraggingAllowed(true);
          drawPanel.setDraggingHorizontal(true);
        }
    });
    allDrag.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.setDraggingAllowed(true);
          drawPanel.setDraggingHorizontal(false);
        }
    });
    // start with horizontal only
    horizDrag.setSelected(true);
    drawPanel.setDraggingAllowed(true);
    drawPanel.setDraggingHorizontal(true);
    draggingGroup.add(noDrag);
    dragging.add(noDrag);
    draggingGroup.add(horizDrag);
    dragging.add(horizDrag);
    draggingGroup.add(allDrag);
    dragging.add(allDrag);

    JMenu labels = (JMenu)menuBar.add(new JMenu("Labeling"));
    ButtonGroup labelsGroup = new ButtonGroup();
    JRadioButtonMenuItem noLabels = new JRadioButtonMenuItem("No");
    JRadioButtonMenuItem someLabels = new JRadioButtonMenuItem("Yes");
    //JRadioButtonMenuItem allLabels = new JRadioButtonMenuItem("All");
    //noLabels.setMnemonic(KeyEvent.VK_N);
    //someLabels.setMnemonic(KeyEvent.VK_H);
    //allLabels.setMnemonic(KeyEvent.VK_A);
    //KeyStroke key = 
      //KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.SHIFT_MASK+Event.CTRL_MASK);
    //noDrag.setAccelerator(key);
    //key=KeyStroke.getKeyStroke(KeyEvent.VK_H,Event.SHIFT_MASK+Event.CTRL_MASK);
    //horizDrag.setAccelerator(key);
    //key=KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.SHIFT_MASK+Event.CTRL_MASK);
    //allDrag.setAccelerator(key);
    noLabels.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.getDiagram().setPaintLabels(false);
          repaint();
        }
    });
    someLabels.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.getDiagram().setPaintLabels(true);
          repaint();
        }
    });
    /*
    allLabels.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.getDiagram().showLabels();
          drawPanel.getDiagram().setPaintLabels(true);
          repaint();
        }
    });
    */
    // start without labels
    noLabels.setSelected(true);
    drawPanel.getDiagram().setPaintLabels(false);
    labelsGroup.add(noLabels);
    labels.add(noLabels);
    labelsGroup.add(someLabels);
    labels.add(someLabels);
    //labelsGroup.add(allLabels);
    //labels.add(allLabels);

    setJMenuBar(menuBar);
  }

  public void open() throws IOException {
    File theFile = null;
    String pwd = currentFolder;
    JFileChooser fileChooser;
    if (pwd != null) fileChooser = new JFileChooser(pwd);
    else fileChooser = new JFileChooser();
    fileChooser.addChoosableFileFilter(
         new ExtFileFilter("Context Files (*.cxt)", ExtFileFilter.CXT_EXT));
    fileChooser.addChoosableFileFilter(
         new ExtFileFilter("Lattice Files (*.lat)", ExtFileFilter.LAT_EXT));
    int option = fileChooser.showOpenDialog(this);
    
    if (option == JFileChooser.APPROVE_OPTION) {
      theFile = fileChooser.getSelectedFile();
      setTitle("LatDraw: " + theFile.getName());
      currentFolder = theFile.getParent();
      String ext = ExtFileFilter.getExtension(theFile);
      org.latdraw.orderedset.OrderedSet poset = null;
      Diagram d = null;
      try {
        if ("cxt".equals(ext)) {
          //Context ct = BurmeisterParser.importBurmeisterFile(theFile);
          //Lattice lat = (new GantersAlgorithm()).createLattice(ct);
          //setConceptLattice(new ConceptLattice(lat));
          //poset = ConceptLattice.orderedSetFromCL(null, lat.getConcepts());
        }
        else {
          poset = new org.latdraw.orderedset.OrderedSet(
                                     new InputLattice(theFile.toString()));
          //setConceptLattice(null);
        }
        d = new Diagram(poset);
        setLabels(d);
      }
      //catch (net.sourceforge.toscanaj.parser.DataFormatException e) {
      //  System.err.println("Bad format in context file");
      //}
      catch (FileNotFoundException e) {
        System.err.println("No such file");
      }
      catch (IOException e) {
        System.err.println("IO error on opening the blank file");
      }
      catch (NonOrderedSetException e) {
        System.err.println("This is not an ordered set");
      }
      drawPanel.setDiagram(d);
      int size = poset.card();
      repaint();
    }
  }

  public void setLabels(Diagram d) {
/*
    if (conceptLattice == null) return;
    drawPanel.getDiagram().clearLabels();
    HashSet verts = new HashSet();
    Object[] things = conceptLattice.getObjectArray();
    for (int i = 0; i < things.length; i++) {
      POElem elt = conceptLattice.elemForObject(things[i]);
      Vertex u = d.vertexForPOElem(elt);
      verts.add(u);
      u.setLabel(Integer.toString(i));
      u.setLabelBackgroundColor(OBJ_COLOR);
      //u.setLabelPainted(true);
    }
    things = conceptLattice.getAttributeArray();
    for (int i = 0; i < things.length; i++) {
      POElem elt = conceptLattice.elemForAttribute(things[i]);
      Vertex u = d.vertexForPOElem(elt);
      if (verts.contains(u)) {
        u.setLabel(u.getLabel() + Character.toString((char)('a' + i)));
        u.setLabelBackgroundColor(Color.WHITE);
      }
      else {
        u.setLabel(Character.toString((char)('a' + i)));
        u.setLabelBackgroundColor(ATT_COLOR);
      }
      //u.setLabelPainted(true);
    }
*/
  }

  // move the tool bar stuff into DrawPanel
  public JToolBar makeToolBar() {
    //JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
    JToolBar toolBar = new JToolBar();
    ClassLoader cl = this.getClass().getClassLoader();
    ImageIcon icon = new ImageIcon
      (cl.getResource("org/latdraw/sample/images/Forward16.gif"));
    JButton forwardButton = new JButton(icon);
    forwardButton.setPreferredSize(new Dimension(32, 32));
    forwardButton.setToolTipText("Rotate forward");
    forwardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.rotateOnce();
        }
      });
    toolBar.add(forwardButton);
    icon = new ImageIcon(
                 cl.getResource("org/latdraw/sample/images/Back16.gif"));
    JButton backButton = new JButton(icon);
    backButton.setPreferredSize(new Dimension(32, 32));
    backButton.setToolTipText("Rotate back");
    backButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.rotateLeft();
        }
      });
    toolBar.add(backButton);
    icon = new ImageIcon(
                 cl.getResource("org/latdraw/sample/images/Redo16.gif"));
    JButton rotButton = new JButton(icon);
    rotButton.setPreferredSize(new Dimension(32, 32));
    rotButton.setToolTipText("Rotate");
    rotButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (drawPanel.isRotating()) drawPanel.stopRotation();
          else drawPanel.startRotation();
        }
      });
    toolBar.add(rotButton);
    JButton inButton = new JButton("><");
    inButton.setPreferredSize(new Dimension(32, 32));
    inButton.setToolTipText("Push in horizontally");
    inButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.increaseAttraction();
          drawPanel.decreaseReplusion();
          drawPanel.improveWithoutDelay(40);
        }
      });
    toolBar.add(inButton);
    JButton outButton = new JButton("<>");
    outButton.setPreferredSize(new Dimension(32, 32));
    outButton.setToolTipText("Push out horizontally");
    outButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.increaseReplusion();
          drawPanel.decreaseAttraction();
          drawPanel.improveWithoutDelay(40);
        }
      });
    toolBar.add(outButton);
    JButton improveButton = new JButton("++");
    improveButton.setPreferredSize(new Dimension(32, 32));
    improveButton.setToolTipText("Improve the diagram");
    improveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          drawPanel.improve();
        }
      });
    toolBar.add(improveButton);
    return toolBar;
  }

  /**
   * Make a sample lattice.
   */
  public static org.latdraw.orderedset.OrderedSet makeExampleLat() 
                                          throws NonOrderedSetException {
    String n = "Test";
    List l = new ArrayList();
    l.add("0"); 
    l.add("a");
    l.add("b");
    l.add("c");
    l.add("topelt bigname");

    List c = new ArrayList();
    List tmp;

    // covers of 0
    tmp = new ArrayList();
    tmp.add("b");
    tmp.add("c");
    c.add(tmp);

    // covers of a
    tmp = new ArrayList();
    tmp.add("topelt bigname");
    c.add(tmp);

    // covers of b
    tmp = new ArrayList();
    tmp.add("topelt bigname");
    c.add(tmp);


    // covers of c
    tmp = new ArrayList();
    tmp.add("a");
    c.add(tmp);

    // covers of 1
    tmp = new ArrayList();
    c.add(tmp);

    InputLattice i = new InputLattice(n, l, c);
    return new org.latdraw.orderedset.OrderedSet(i);
  }

  public static void drawLattice(BasicLattice lat) {
    try {
      drawLattice(lat.getDiagram());
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
  }

  public static void drawLattice(Diagram diagram) {
    LatDrawer frame = new LatDrawer(diagram);
    //if (file != null) frame.setTitle("LatDraw: " + file.getName());
    //frame.setConceptLattice(cl);
    //frame.setContent(frame.drawPanel, frame.toolBar, cl);
    frame.setContent(frame.drawPanel, frame.toolBar, null);
    frame.setLabels(diagram);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int height = (screenSize.height * 8) / 10;
    int width = height;
    frame.setLocation((screenSize.width - width) / 2,
                      (screenSize.height - height) / 2);
    frame.setSize(width, height);
    frame.isDefaultLookAndFeelDecorated();
    Runnable  runner = new FrameShower(frame);
    EventQueue.invokeLater(runner);
    //frame.setVisible(true);
  }



  public static void main(String[] args) 
          throws FileNotFoundException, IOException, NonOrderedSetException {
    org.latdraw.orderedset.OrderedSet test = null;
    //ConceptLattice cl = null;
    File file = null;
    if (args.length == 0) {
      test = makeExampleLat();
    } 
    else {
      file = new File(args[0]);
      String ext = null;
      int i = args[0].lastIndexOf('.');
      if (i > 0 && i < args[0].length() - 1) ext = args[0].substring(i+1);
      if ("cxt".equals(ext)) {
        //Context ct = BurmeisterParser.importBurmeisterFile(file);
        //Lattice lat = (new GantersAlgorithm()).createLattice(ct);
        //cl = new ConceptLattice(lat);
        //test = ConceptLattice.orderedSetFromCL(null, lat.getConcepts());
      }
      else {
        test = new org.latdraw.orderedset.OrderedSet(new InputLattice(args[0]));
      }
    }
    Diagram testDiagram = new Diagram(test);
    int size = test.card();
/*
    ChainDecomposition chainDec = new ChainDecomposition(test);
    for (Iterator it = test.univ().iterator(); it.hasNext(); ) {
      System.out.print(" " + ((POElem)it.next()).label());
    }
    System.out.println("AS:Test diagram's name:"+testDiagram.getName()+"\n");
    System.out.println("\nveritces: ");
    for(int i = 0; i < size; i++) {
      System.out.println("  " + testDiagram.getVertices()[i]);
    }
    System.out.println("att and rep fac are " 
          + testDiagram.getAttractionFactor()
          + " " +  testDiagram.getRepulsionFactor());

    for (int i = 0; i < testDiagram.size + ITERATIONS; i++) {
      testDiagram.update(0.5 * testDiagram.attractionFactor, 
      3.0 * testDiagram.repulsionFactor);
      //System.out.println("\nveritces after " + i + ": ");
      //for (int j = 0; j < size; j++) {
      //System.out.println("  " + (Point3D)testDiagram.vertices[j]);
      //}
    }
    for (int i = 0; i < testDiagram.size + ITERATIONS; i++) {
      testDiagram.update(3.0 * testDiagram.attractionFactor, 
      0.5 * testDiagram.repulsionFactor);
      //System.out.println("\nveritces after " + i + ": ");
      //System.out.println("  " + (Point3D)testDiagram.vertices[0]);
    }

    for (int i = 0; i < testDiagram.size + 4*ITERATIONS; i++) {
      testDiagram.update(testDiagram.attractionFactor, 
      testDiagram.repulsionFactor);
      //System.out.println("\nveritces after " + i + ": ");
      //System.out.println("  " + (Point3D)testDiagram.vertices[0]);
      //for(int k = 0; k < size; k++) {
      //System.out.println("  " + (Point3D)testDiagram.vertices[k]);
      //}
    }
    testDiagram.normalizeCoords();
*/
/*
    //testDiagram.improve();
    testDiagram.project2d(0);

    for(int k = 0; k < size; k++) {
      System.out.println("  " + testDiagram.getVertices()[k]);
    }  
*/

    LatDrawer frame = new LatDrawer(testDiagram);
    if (file != null) frame.setTitle("LatDraw: " + file.getName());
    //frame.setConceptLattice(cl);
    //frame.setContent(frame.drawPanel, frame.toolBar, cl);
    frame.setContent(frame.drawPanel, frame.toolBar, null);
    frame.setLabels(testDiagram);
/*
    final DrawPanel dp = new DrawPanel(testDiagram);
    frame.drawPanel = dp;
    frame.setContentPane(dp);
    dp.setFocusable(true);
    dp.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          dp.requestFocus();
        }
      });
    dp.addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent e) {
          char key = e.getKeyChar();
          if (key == 'r' || key == 'R') {
            if (dp.isRotating()) dp.stopRotation();
            else dp.startRotation();
          }
          if (key == 's' || key == 'S') dp.stopRotation();
          if (key == '.' || key == '>') dp.rotateOnce();
          if (key == ',' || key == '<') dp.rotateLeft();
          if (key == 'd' || key == 'D') 
                        dp.setDraggingAllowed(!dp.isDraggingAllowed());
          if (key == 'h' || key == 'H') 
                        dp.setDraggingHorizontal(!dp.isDraggingHorizontal());
          if (key == 'i') dp.improveWithDelay(40);
          if (key == 'I') dp.improve();
          if (key == 'p') dp.printDialog();
          if (key == 'P') dp.writeRSFDiagram("outx.ps");
          if (key == '+') {
            dp.increaseReplusion();
            dp.decreaseAttraction();
          }
          if (key == '-') {
            dp.increaseAttraction();
            dp.decreaseReplusion();
          }
          if (key == 'q' || key == 'Q') System.exit(0);
        }
      });
*/

    //Vertex v = testDiagram.getVertices()[0];
    //v.setLabel("foo");
    //v.setLabelPainted(true);

    //dp.improveWithDelay(225);

    //dp.startRotation();
    //frame.pack();
    //frame.validate();
    //frame.show();

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //int width = (screenSize.width * 8) / 10;
    int height = (screenSize.height * 8) / 10;
    int width = height;
    frame.setLocation((screenSize.width - width) / 2,
                      (screenSize.height - height) / 2);
    frame.setSize(width, height);
    frame.isDefaultLookAndFeelDecorated();
    Runnable  runner = new FrameShower(frame);
    EventQueue.invokeLater(runner);
    //frame.setVisible(true);
  }

  private static class FrameShower implements Runnable {
    final JFrame frame;

    public FrameShower(JFrame frame) {
      this.frame = frame;
    }

    public void run() {
      frame.setVisible(true);
    }
  }

}

