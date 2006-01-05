
package org.uacalc.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class UACalculator extends JFrame {

  private boolean dirty = false;

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

    JMenuItem newMI = (JMenuItem)file.add(new JMenuItem("New", icon));
    newMI.setMnemonic(KeyEvent.VK_N);
    KeyStroke cntrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.CTRL_MASK);
    newMI.setAccelerator(cntrlN);

    setJMenuBar(menuBar);
  }


  public boolean isDirty() { return dirty; }



  public boolean checkSave() { return true; }

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
