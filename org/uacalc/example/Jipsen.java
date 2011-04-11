package org.uacalc.example;

import org.uacalc.io.*;
import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import java.io.*;
import java.util.*;

/**
 * This class serves as an interface to Sage. args[0] should
 * be a command and others arguments to the command, usually
 * algebra file names. 
 * 
 * @author ralph
 *
 */
public class Jipsen {

  /**
   * 
   * @param args   
   */
  public static void main(String[] args) {
    //if (args.length == 0) {
    //  System.out.println("Error: usage command arg1 arg2 ...");
    //  return;
    //}
    //final String command = args[0];
    if (args.length == 0) {
      System.out.println("need a file name of an algebra");
      return;
    }
    SmallAlgebra alg = null;
    try {
      alg = AlgebraIO.readAlgebraFile(args[0]);
    }
    catch (IOException e) {}             // put an error message here.
    catch (BadAlgebraFileException e) {}
    Set<Partition> elts = alg.con().universe();
    int k = 0;
    for (Partition par : elts) {
      System.out.print(k++ + ": ");
      System.out.println(par);
    }
  }


}
