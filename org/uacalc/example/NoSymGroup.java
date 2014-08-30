package org.uacalc.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uacalc.example.HasConstantTuple;
import org.uacalc.alg.BasicAlgebra;
import org.uacalc.alg.BigProductAlgebra;
import org.uacalc.alg.PowerAlgebra;
import org.uacalc.alg.SmallAlgebra;
import org.uacalc.alg.SubProductAlgebra;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.io.AlgebraIO;
import org.uacalc.util.IntArray;

/**
 *
 * @author Matt
 */
public class NoSymGroup {

  public static void main(String[] args) throws IOException {
    int Size = 5; // the size of the algebra to be built
    List ops = new ArrayList();
    for (int i = 0; i < Size; i++) {
      // ops.add(MakeIdBinaryOp(i, Size));
      ops.add(MakeIdTernaryOp(i, Size));
    }
    SmallAlgebra A1 = new BasicAlgebra("SymTestGroup", Size, ops);
    AlgebraIO.writeAlgebraFile(A1, "SymTestGroup.ua");
    /*
     construct a particular subpower A and then check if a constant tuple appears 
     in a "symmetric" subalgebra of A^6
     */

    final BigProductAlgebra prodAlg = new BigProductAlgebra(A1, 5);
    System.out.println("Product has size " + prodAlg.cardinality());
    List<IntArray> gens = new ArrayList<IntArray>(5);
    //int[][] G = {{0, 3}, {1, 4}, {2, 0}};
    int[][] G = {{0, 1, 2, 3,4}, {1, 2, 0, 4, 3}, {2, 3, 4, 1, 0}};
    gens.add(new IntArray(G[0]));
    gens.add(new IntArray(G[1]));
    gens.add(new IntArray(G[2]));
    SubProductAlgebra SubP = new SubProductAlgebra("subpower", prodAlg, gens);
    System.out.println("gens are " + gens);
    System.out.println("subpower has size " + SubP.cardinality());
    List<IntArray> SymGens = new ArrayList<IntArray>(4);
    //int[][] S = {{0, 1, 2, 0, 1, 2}, {1, 0, 1, 2, 2, 0}, {2, 2, 0, 1, 0, 1}};
    int[][] S = {{0, 1, 2, 0}, {1, 0, 1, 2}, {2, 2, 0, 1}};
    SymGens.add(new IntArray(S[0]));
    SymGens.add(new IntArray(S[1]));
    SymGens.add(new IntArray(S[2]));
    if (HasConstantTuple.hasConstantTuple(SubP, 4, SymGens)) {
      System.out.println("the subpower has a constant tuple");
    } else {
      System.out.println("the subpower doesn't have a constant tuple");
    }
  }

  public static Operation MakeIdTernaryOp(final int i, final int Size) {
    String opsym = "t" + i;
    Operation t1 = new AbstractOperation(opsym, 3, Size) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }

      public int intValueAt(int[] args) {
        return (TernaryOp((args[0] + Size - 1 - i) % Size,
            (args[1] + Size - 1 - i) % Size, (args[2] + Size - 1 - i) % Size) + 1 + i) % Size;
      }
    };
    return t1;
  }

  public static int Addition(int x, int y) {
    if (x == y) {
      return 0;
    }
    if (x == 0) {
      return y;
    }
    if (y == 0) {
      return x;
    }
    if (x < 3 && y < 3) {
      return 3;
    }
    if (x == 1 || y == 1) {
      return 2;
    }
    return 1;
  }

  public static int TernaryOp(int x, int y, int z) {
    if (x == 4 || y == 4 || z == 4) {
      return x;
    }
    return Addition(Addition(x, y), z);
  }

}
