package org.uacalc.example;


import java.io.*;
import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.terms.*;
import org.uacalc.eq.*;
import org.uacalc.io.*;

/**
 * This example shows how to test if B is in V(A). 
 * The example is part of a project George McNulty and 
 * his student, Kate Scott, are working on.
 * 
 * @author ralph
 *
 */
public class MembershipTester {
  
  // Set these to the algebra files you want to test. Use backslashes on pc's.
  static String alg0File = "/home/ralph/Java/Algebra/algebras/hajilarov.ua";
  static String alg1File = "/home/ralph/Java/Algebra/algebras/diffiq.ua";

  public static void main(String[] args) throws IOException, BadAlgebraFileException {
    SmallAlgebra alg0 = org.uacalc.io.AlgebraIO.readAlgebraFile(alg0File);
    SmallAlgebra alg1 = org.uacalc.io.AlgebraIO.readAlgebraFile(alg1File);
    // The third argument below is a generating set of alg1.
    Equation eq = FreeAlgebra.findEquationOfAnotB(alg0, alg1, new int[] { 1, 2, 4, 5 });
    System.out.println("eq is\n" + eq);
    if (eq != null) {  // if eq == null, alg1 is in V(alg0), 
                       // or at least the subalgebra generated by the generators is.
      // test that eq fails in alg1 (of course it does).
      Map<Variable, Integer> failure = eq.findFailureMap(alg1);
      System.out.println("failure in alg1\n" + failure);
      // try to find a failure in alg0 (of course there is none).
      failure = eq.findFailureMap(alg0);
      System.out.println("failure in alg0\n" + failure);
    }
  }
  
}
