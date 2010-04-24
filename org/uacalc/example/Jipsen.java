package org.uacalc.example;

import org.uacalc.io.*;
import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import java.io.*;
import java.util.*;

public class Jipsen {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    if (args.length == 0) {
      System.out.println("need a file name of an algebra");
      return;
    }
    SmallAlgebra alg = null;
    try {
      AlgebraIO.readAlgebraFile(args[0]);
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
