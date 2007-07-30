
package org.uacalc.ui;


import java.util.*;
import java.lang.reflect.*;
import org.uacalc.lat.BasicLattice;
import org.latdraw.orderedset.*;
//import org.latdraw.fca.*;
import org.latdraw.diagram.*;

import org.latdraw.beans.*;

import org.latdraw.beans.*;
//import org.latdraw.sample.ExtFileFilter;

//import net.sourceforge.toscanaj.parser.BurmeisterParser;
//import net.sourceforge.toscanaj.model.lattice.*;
//import net.sourceforge.toscanaj.model.context.*;
//import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;


import javax.swing.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
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
public class LatDrawPanel extends JPanel {

  public static final Color OBJ_COLOR = new Color(204, 255, 204); //light green
  public static final Color ATT_COLOR = new Color(204, 204, 255); //light blue

  private org.latdraw.beans.DrawPanel drawPanel;

  private JPanel mainPanel;
  private JPanel appPanel;
  private JScrollPane objScrollPane;
  private JScrollPane attScrollPane;
  private JToolBar toolBar;
  // some fca stuff
  //private ObjAttTable objTable;
  //private ObjAttTable attTable;
  //private ConceptLattice conceptLattice;
  private UACalculator uacalc;

  private static final Dimension scrollDim = new Dimension(200, 250);

// was ConceptLattice, leave it in case we need it as an example.
  public LatDrawPanel(UACalculator uacalc) {
    this.uacalc = uacalc;
    drawPanel = new org.latdraw.beans.DrawPanel();
    PropertyChangeListener changeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent e) {
          if (e.getPropertyName().equals(ChangeSupport.VERTEX_PRESSED)) {
            Diagram diag = drawPanel.getDiagram();
            diag.resetVertices();
            diag.hideLabels();
            Vertex v = (Vertex)e.getNewValue();
            boolean vIsAtt = false;
            boolean vIsObj = false;

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
            repaint();
          }
        }
    };
    drawPanel.getChangeSupport().addPropertyChangeListener(changeListener);
    drawPanel.setFocusable(true);
    drawPanel.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          requestFocus();
        }
      });
    addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent e) {
          if (drawPanel.getDiagram() == null) return;
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
        }
      });


    setLayout(new BorderLayout());
    toolBar = makeToolBar();
    appPanel = new JPanel();
    appPanel.setLayout(new BorderLayout());
    mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(drawPanel, BorderLayout.CENTER);
    add(toolBar, BorderLayout.NORTH);
    add(mainPanel, BorderLayout.CENTER);
    validate();
    repaint();
  }

  public UACalculator getUACalculator() { return uacalc; }

  public Diagram getDiagram() { return drawPanel.getDiagram(); }

  public void setDiagram(Diagram d) { 
    if (d != null) { // fix this !!!!!!!!!!!
      drawPanel.setDiagram(d);
      //drawPanel = new DrawPanel(d);
      repaint();
    }
  }

  public org.latdraw.beans.DrawPanel getDrawPanel() { return drawPanel; }

  public JToolBar getToolBar() { return toolBar; }

/*
  public void paint(java.awt.Graphics g) {
    if (drawPanel.getDiagram() != null) super.paint(g);
    toolBar.repaint();
  }
*/

/*
  public void setContent(DrawPanel dp, JToolBar tb) {
    drawPanel = dp;
    //conceptLattice = cl;
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(tb, BorderLayout.NORTH);
    rightPanel.add(drawPanel, BorderLayout.CENTER);
    mainPanel.add(rightPanel, BorderLayout.CENTER);
    validate();
  }
*/

// add what we need (like the dragging menu) to the tool bar
/*
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
*/


  public void setLabels(Diagram d) {
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
          if (getDiagram() != null) {
            drawPanel.rotateOnce();
          }
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
          if (getDiagram() != null) {
            drawPanel.rotateLeft();
          }
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
          if (getDiagram() != null) {
            if (drawPanel.isRotating()) drawPanel.stopRotation();
            else drawPanel.startRotation();
          }
        }
      });
    toolBar.add(rotButton);
    JButton inButton = new JButton("><");
    inButton.setPreferredSize(new Dimension(32, 32));
    inButton.setToolTipText("Push in horizontally");
    inButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getDiagram() != null) {
            drawPanel.increaseAttraction();
            drawPanel.decreaseReplusion();
            drawPanel.improveWithoutDelay(40);
          }
        }
      });
    toolBar.add(inButton);
    JButton outButton = new JButton("<>");
    outButton.setPreferredSize(new Dimension(32, 32));
    outButton.setToolTipText("Push out horizontally");
    outButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getDiagram() != null) {
            drawPanel.increaseReplusion();
            drawPanel.decreaseAttraction();
            drawPanel.improveWithoutDelay(40);
          }
        }
      });
    toolBar.add(outButton);
    JButton improveButton = new JButton("++");
    improveButton.setPreferredSize(new Dimension(32, 32));
    improveButton.setToolTipText("Improve the diagram");
    improveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (getDiagram() != null) {
            drawPanel.improve();
          }
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

/*
  public static void drawLattice(BasicLattice lat) {
    try {
      drawLattice(lat.getDiagram());
    }
    catch (org.latdraw.orderedset.NonOrderedSetException e) {
      e.printStackTrace();
    }
  }
*/

/*
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
*/

}

