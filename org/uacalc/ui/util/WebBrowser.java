package org.uacalc.ui.util;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;



public class WebBrowser {
  
  public static void openURL(String url) {
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Unable to open " + url +
      "\nPlease open your browser and navigate there yourself.");
    }
  }


  /**
   * @param args
   */
  public static void main(String[] args) {
    WebBrowser.openURL("http://uacalc.org/");
  }

}
