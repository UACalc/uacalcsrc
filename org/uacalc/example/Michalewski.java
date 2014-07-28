package org.uacalc.example;

import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.io.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.ui.tm.*;

public class Michalewski {

  
  public static Closer findJoinMeet(int a, int b, SmallAlgebra alg, List<IntArray> consts) {
    BigProductAlgebra big = new BigProductAlgebra(alg, 4);
    List<IntArray> gens = new ArrayList<>();
    gens.add(new IntArray(new int[] {a,a,b,b}));
    gens.add(new IntArray(new int[] {a,b,a,b}));
    gens.addAll(consts);
    Closer closer = new Closer(big, gens, true);
    closer.setProgressReport(new ProgressReport());
    List<IntArray> eltsToFind = new ArrayList<>();
    IntArray join = new IntArray(new int[] {a,b,b,b});
    IntArray meet = new IntArray(new int[] {a,a,a,b});
    eltsToFind.add(join);
    eltsToFind.add(meet);
    closer.setElementsToFind(eltsToFind, gens);
    closer.sgClose();
    if (closer.allElementsFound()) {
      Term joinTerm = closer.getTermMap().get(join);
      Term meetTerm = closer.getTermMap().get(meet);
      System.out.println("join operation for {" + a + "," + b + "}: " + joinTerm);
      System.out.println("meet operation for {" + a + "," + b + "}: " + meetTerm);
      return closer;
    }
    return null;
  }
  
  
	
  public static void main(String[] args) throws Exception {    
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile("/Users/ralph/Java/Algebra/algebras/rps3.ua");
    //SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile("/Users/ralph/Java/Algebra/algebras/polin.ua");
    int n = alg.cardinality();
    List<IntArray> consts = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      consts.add(new IntArray(new int[] {i,i,i,i}));
    }
    for (int a = 0; a < n; a++) {
      for (int b = a+1; b < n; b++) {
        Closer closer = findJoinMeet(a, b, alg, consts);
        if (closer == null) System.out.println("There is no join and meet for {" + a +"," + b + "}");
        else {
          System.out.println("There is a join and meet for {" + a +"," + b + "}");
        }
      }
    } 
  }

}

