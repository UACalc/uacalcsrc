package org.uacalc.example;

import java.io.*;
import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.terms.*;
import org.uacalc.eq.*;
import org.uacalc.io.*;


public class ConLyndonF3 {

  static String alg0File = "/home/ralph/Java/Algebra/algebras/lyndonquotbasic.ua";
  static String alg1File = "/home/ralph/Java/Algebra/algebras/m3.ua";
  // This should be a generating set of alg1. Make it as small as possible.
  static int[] alg1Generators = new int[] { 1, 2, 3 };
  
  public static void main(String[] args) throws IOException, BadAlgebraFileException {
    
    SmallAlgebra alg0 = org.uacalc.io.AlgebraIO.readAlgebraFile(alg0File);
    int numberOfGens = 4;
    FreeAlgebra fr = new FreeAlgebra(alg0, numberOfGens, true, true, true, null, null);
    CongruenceLattice con = fr.con();
    System.out.println("|F(" + numberOfGens + ")| = " + fr.cardinality());
    System.out.println("|Con(F(" + numberOfGens + "))| = " + con.cardinality());
    List<Partition> mis = con.meetIrreducibles();
    int k = 0;
    for (Partition part : mis) {
      System.out.print("" + k + ": " + part);
      System.out.println("  " + part.numberOfBlocks() + " blocks");
      k++;
    }
  }
  
  
}
