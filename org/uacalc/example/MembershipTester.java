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

  public static void main(String[] args) throws IOException, BadAlgebraFileException {
    SmallAlgebra alg0 = org.uacalc.io.AlgebraIO
       .readAlgebraFile("/home/ralph/Java/Algebra/algebras/hajilarov.ua");
    SmallAlgebra alg1 = org.uacalc.io.AlgebraIO
        .readAlgebraFile("/home/ralph/Java/Algebra/algebras/diffiq.ua");
    Equation eq = FreeAlgebra.findEquationOfAnotB(alg0, alg1, new int[] { 1, 2, 4, 5 });
    System.out.println("eq is\n" + eq);
    Map<Variable,Integer> failure = eq.findFailureMap(alg1);
    System.out.println("failure in alg1\n" + failure);
    failure = eq.findFailureMap(alg0);
    System.out.println("failure in alg0\n" + failure);
  }
  
}
