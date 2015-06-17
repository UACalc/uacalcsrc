
/*
 * ExtFileFilter.java   03/01/2002
 *
 * (c) Ralph Freese, Jim Freese
 *
 */


package org.uacalc.io;

//import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;



/**
 * Filter files by extension.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public class ExtFileFilter extends javax.swing.filechooser.FileFilter {

  public final static String ALG_EXT = "alg";
  public final static String XML_EXT = "xml";
  public final static String UAC_EXT = "uac";
  public final static String UA_EXT = "ua";
  public final static String CSV_EXT = "csv";
  public final static String TXT_EXT = "txt";

  public final static List<String> UA_EXTS = new ArrayList<String>();
  static {
    UA_EXTS.add(UA_EXT);
    UA_EXTS.add(XML_EXT);
  }
  
  public final static List<String> ALL_ALG_EXTS = new ArrayList<String>();
  static {
    ALL_ALG_EXTS.add(UA_EXT);
    ALL_ALG_EXTS.add(XML_EXT);
    ALL_ALG_EXTS.add(ALG_EXT);
  }
  
  public final static List<String> MACE4_EXTS = new ArrayList<String>();
  static {
	MACE4_EXTS.add("m4");
  }

  List<String> exts;
  String description;

  public ExtFileFilter(String desc, List<String> exts) {
    this.exts = exts;
    this.description = desc;
  }

  public ExtFileFilter(String desc, String ext) {
    this.exts = new ArrayList<String>();
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

  /**
   * Split the file name into 2 parts: the first everything up to the
   * last "."; the rest the extension.
   */
  public static String[] splitOffExtension(File f) {
    String[] ans = new String[2];
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      //ext = s.substring(i+1).toLowerCase();
      ans[1] = s.substring(i+1);
      ans[0] = s.substring(0,i);
    }
    return ans;
  }

  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
      //ext = s.substring(i+1).toLowerCase();
      ext = s.substring(i+1);
    }
    return ext;
  }

  public static void main(String[] args) {
    File f = new File("foo.alg");
    System.out.println(getExtension(f));
    System.out.println(System.getProperties());
   
  }

}



