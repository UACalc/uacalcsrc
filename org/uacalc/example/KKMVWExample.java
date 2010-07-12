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
import org.uacalc.alg.sublat.*;


/**
 * Matt et al example showing most omitting types are not 
 * strong Maltsev.
 * 
 * @author ralph
 *
 */
public class KKMVWExample {

  final int n;
  
  public KKMVWExample(final int n) {
    this.n = n;
  }
  
  Operation makeTin(final int i) {
    Operation op = new AbstractOperation("t", 2*n+1, 2) {

      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }

      public int intValueAt(int[] args) {
        for (int j = 0 ; j < 2*i - 1; j++) {
          if (args[j] == 0) return 0;
        }
        if (args[2*i - 1] == 1 && args[2*i] == 0) return 0;
        return 1;
      }
    };
    
    return op;
  }
  
  SmallAlgebra makeAin(final int i) {
    List<Operation> ops = new ArrayList<Operation>(1);
    ops.add(makeTin(i));
    return new BasicAlgebra("A_" + i + n, 2, ops);
  }
  
  SmallAlgebra makeAn() {
    List<SmallAlgebra> algs = new ArrayList<SmallAlgebra>(n);
    for (int i = 1; i <= n; i++) {
      algs.add(makeAin(i));
    }
    SmallAlgebra alg = new ProductAlgebra("A_" + n, algs);
    return alg;
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    KKMVWExample exp = new KKMVWExample(2);
    SmallAlgebra An = exp.makeAn();
    AlgebraIO.writeAlgebraFile(An, "/tmp/A2.ua");

  }

}
