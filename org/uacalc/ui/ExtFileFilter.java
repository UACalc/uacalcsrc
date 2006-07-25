
/*
 * ExtFileFilter.java   03/01/2002
 *
 * (c) Ralph Freese
 *
 */


package org.uacalc.ui;

import javax.swing.*;
//import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;



/**
 * Filter files by extension.
 *
 * @author Ralph Freese
 * @author Jim Freese
 * @version $Id$
 */
public class ExtFileFilter extends javax.swing.filechooser.FileFilter {

  public final static String ALG_EXT = "alg";
  public final static String XML_EXT = "xml";
  public final static List ALGEBRA_EXTS = new ArrayList();
  static {
    ALGEBRA_EXTS.add(ALG_EXT);
    ALGEBRA_EXTS.add(XML_EXT);
  }
  public final static String PDF_EXT = "pdf";
  public final static List IMAGE_EXTS = new ArrayList();
  static {
    IMAGE_EXTS.add("jpg");
    IMAGE_EXTS.add("jpeg");
    IMAGE_EXTS.add("gif");
    IMAGE_EXTS.add("png");
    IMAGE_EXTS.add("tif");
    IMAGE_EXTS.add("tiff");
  }

  List exts;
  String description;

  public ExtFileFilter(String desc, List exts) {
    this.exts = exts;
    this.description = desc;
  }

  public ExtFileFilter(String desc, String ext) {
    this.exts = new ArrayList();
    exts.add(ext);
    this.description = desc;
  }

  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    if (exts.contains(getExtension(f))) return true;
    return false;
  }

  public String getDescription() { return description; }

  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      //ext = s.substring(i+1).toLowerCase();
      ext = s.substring(i+1);
    }
    if (ext != null) return ext.toLowerCase();
    return ext;
  }

  public static void main(String[] args) {
    File f = new File("foo.brd");
    System.out.println(getExtension(f));
    System.out.println(System.getProperties());
   
  }

}



