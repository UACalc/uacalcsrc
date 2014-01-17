package org.uacalc.example;

import java.util.*;
import java.io.*;


import org.uacalc.util.*;
import org.uacalc.alg.*;
import org.uacalc.io.*;
import org.uacalc.ui.*;
import org.uacalc.lat.*;
import org.uacalc.terms.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.sublat.*;

/**
 * An algebra with n elements and a single r ary operation t_r, r odd, 
 * which is the average of its arguments rounded to the nearest integer.
 * If r is at least 2n - 1, then t_r is an nu term. However, when n = 3
 * r = 5 the algebra actually has a majority term. For n = 4 and r = 7 it could 
 * not complete the calculation (7-ary is pretty hard). But it appears that
 * it may not have a majority term. 
 * 
 * For n = 4 and r = 3 (so t_3 is not an nu term), in fact it has a very long 
 * majority term:
 * 
 *  ave3(ave3(ave3(x,x,ave3(x,y,z)),ave3(x,x,ave3(x,y,z)),ave3(y,z,ave3(x,y,z))),ave3(ave3(x,y,ave3(x,y,z)),ave3(y,y,ave3(x,y,z)),ave3(y,z,ave3(x,y,z))),ave3(ave3(x,y,ave3(x,y,z)),ave3(z,z,ave3(x,y,z)),ave3(z,z,ave3(x,y,z))))
 * 
 */
public class Average {

  // average algebra
  
  public static Operation ave(final int n, final int r) {
    return new AbstractOperation("ave" + r, r, n) {

      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }

      public int intValueAt(int[] args) {
        int sum = 0;
        for (int i = 0; i < r; i++) {
          sum += args[i];
        }
        return Math.round(((float)sum) / r);
      }
    };
  }

  public static SmallAlgebra averageAlg(int n) {
    return averageAlg(n, 2*n - 1);
  }
  
  public static SmallAlgebra averageAlg(int n, int r) {
    List<Operation> ops = new ArrayList<Operation>(1);
    ops.add(ave(n, r));
    return new BasicAlgebra("AveAlgebra" + n + "," + r, n, ops);
  }
  


  public static void main(String[] args) throws IOException {
    AlgebraIO.writeAlgebraFile(averageAlg(3), "/tmp/averageAlg3.ua");
    AlgebraIO.writeAlgebraFile(averageAlg(3,7), "/tmp/averageAlg3-7.ua");
    AlgebraIO.writeAlgebraFile(averageAlg(3,3), "/tmp/averageAlg3-3.ua");
    AlgebraIO.writeAlgebraFile(averageAlg(4,3), "/tmp/averageAlg4-3.ua");
    AlgebraIO.writeAlgebraFile(averageAlg(4), "/tmp/averageAlg4.ua");
  }


}


