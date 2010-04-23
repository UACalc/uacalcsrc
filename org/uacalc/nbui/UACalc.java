package org.uacalc.nbui;

import javax.swing.*;

/**
 * This is a (java) interface to the GUI, making it
 * easy to try different GUI's with different layout
 * managers.
 * 
 * @author ralph
 *
 */
public interface UACalc {
  
  public JFrame getFrame();
  
  public JTabbedPane getTabbedPane();
  
  public JComboBox getOpsComboBox();
  
  public JTable getOpTable();
  
  public JCheckBox getIdempotentCB();
  
  public void validate();
  
  public void repaint();
  
  public JComboBox getDefaultEltComboBox();
  
  public MainController getMainController();
  
  public void beep();
  
  public JTextField getDescTextField();
  
  public JTextField getAlgNameTextField();
  
  public JTextField getCardTextField();
  
  public JTable getComputationsTable();
  
  public JTextArea getLogTextArea();
  
  public JTable getResultTable();
  
  public JTextField getResultTextField();
  
  public JButton getCancelCompButton();
  
  public JPanel getConMainPanel();
  
  public JPanel getConLeftPanel();
  
  public ComputationsController getComputationsController();
  
  public JTable getAlgListTable();
  
  public void setTitle(String title);
  
  public JTextField getMsgTextField();

  public JPanel getSubMainPanel();
  
  public JPanel getDrawingMainPanel();
  
  public void setEmptyOpTableModel();
  
  
}
