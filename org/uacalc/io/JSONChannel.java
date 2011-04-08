package org.uacalc.io;

import org.uacalc.io.*;
import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import java.io.*;
import java.util.*;


/**
 * Communication with outside programs like Sage. 
 * The main method can be called with 2 arguments.
 * The first is a string containing a command and 
 * possibilities some modifiers. The second is the
 * name of a file containing one or more algebras.
 * 
 * 
 * 
 * 
 * @author ralph
 *
 */
public class JSONChannel {

  private static PrintStream out = System.out;  // in case we want to change this.
  
  private static void doCommand(String command, List<SmallAlgebra> algebras) {
    
  }
  
  
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Error: usage command arg1 arg2 ...");
      return;
    }
    final String command = args[0];
    
    
    
    
    SmallAlgebra alg = null;
    List<SmallAlgebra> algebras = null;
    try {
      algebras = AlgebraIO.readAlgebraListFile(args[0]);
    }
    catch (IOException e) {}             // put an error message here.
    catch (BadAlgebraFileException e) {}
  }

}
