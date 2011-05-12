package org.uacalc.alg;

import java.util.*;
import org.uacalc.alg.op.*;

/**
 * Starting with an algebra B which is permutational (nonconstant
 * unary polynomials are permutations) and a geometry for the 
 * minimal sets, this constructs an algebra A with tame minimal sets, 
 * having B as a minimal set. Initially we will assume the geometry
 * has 3 minimal sets, B, C, D, with C And D disjoint and the intersection
 * of B and C just 0 and B and D the last element of B.
 * <p>
 * If n is the size of A and k is the size of each minimal set, we can
 * specify the geometry of a list of maps 0 to k-1 into A. 
 * 
 * @author ralph
 *
 */
public class AlgebraFromMinimalSets extends BasicAlgebra implements
    SmallAlgebra {

  SmallAlgebra minimalAlgebra;
  int minAlgSize;
  List<int[]> maps;
  
  /**
   * A map from A to B, identity on B, and a isomorphism when restricted
   * to and minimal set.
   */
  int[] mapToB; // from A to B
  
  public AlgebraFromMinimalSets(SmallAlgebra minAlg) {
    this(null, minAlg, 3 * minAlg.cardinality() - 2, null);
  }
  
  public AlgebraFromMinimalSets(SmallAlgebra minAlg, int algSize, List<int[]> maps) {
    this(null, minAlg, algSize, maps);
  }
  
  public AlgebraFromMinimalSets(String name, SmallAlgebra minAlg) {
    this(name, minAlg, 3 * minAlg.cardinality() - 2, null);
  }
  
  /**
   * If k is the cardinality of <code>minAlg</code>, each map maps
   * k = {0,..., k-1} one-to-one into n = {0,...,n-1}. The first map
   * must be the identity. Each element of n must be in the range of 
   * at least one map. Given any two elements of n, there are overlapping
   * ranges of the maps going from on to the other.
   * 
   * @param name
   * @param minAlg   a permutational algebra
   * @param maps
   */
 
  public AlgebraFromMinimalSets(String name, SmallAlgebra minAlg, int algSize, List<int[]> maps) {
    super(name, 3 * minAlg.cardinality() - 2, new ArrayList<Operation>());
    this.minimalAlgebra = minAlg;
    minAlgSize = minAlg.cardinality();
    size = algSize;
    final boolean mapsNull = maps == null;
    if (maps != null) this.maps = maps;
    else makeDefaultMaps();
    makeMapToB();
    setOperations(makeOps());
    if (mapsNull) {
      Operation op = new AbstractOperation("s", 1, size) {
        public int intValueAt(int[] args) {
          final int arg = args[0];
          if (arg < minAlgSize) return arg;
          if (arg < 2 * minAlgSize - 1) return 0;
          return minAlgSize - 1;
        }
        
        public List valueAt(List args) {
          throw new UnsupportedOperationException();
        }
      };
      operations.add(op);
    }
    for (final Operation minOp : minAlg.operations()) {
      operations.add(new AbstractOperation("op-" + minOp.symbol().name(), minOp.arity(), size) {
        public int intValueAt(int[] args) {
          int[] argsToB = new int[args.length];
          for (int i = 0; i < args.length; i++) {
            argsToB[i] = mapToB[args[i]];
          }
          //final int arg = mapToB[args[0]];
          //return minOp.intValueAt(new int[] {arg});
          return minOp.intValueAt(argsToB);
        }
        
        public List valueAt(List args) {
          throw new UnsupportedOperationException();
        }
      });
    }
  }
  
  private List<Operation> makeOps() {
    int r = 0;
    List<Operation> ops = new ArrayList<Operation>();
    //ops.add(Operations.makeIntOperation("p", 1, algSize, mapToB));
    
    for (final int[] map : maps) {
      Operation op = new AbstractOperation("p" + r++, 1, size) {
        public int intValueAt(int[] args) {
          final int arg = args[0];
          return map[mapToB[arg]];
        }
        
        public List valueAt(List args) {
          throw new UnsupportedOperationException();
        }
      };
      ops.add(op);
    }
    
    Operation toC = new AbstractOperation("toC", 1, size) {
      public int intValueAt(int[] args) {
        final int arg = args[0];
        if (arg == 0) return 0;
        if (arg < minAlgSize) return arg + minAlgSize - 1;
        return 0;
      }
      
      public List valueAt(List args) {
        throw new UnsupportedOperationException();
      }
    };
    
    return ops;
  }
  
  private void makeDefaultMaps() {
    final int k = minAlgSize;
    maps = new ArrayList<int[]>(3);
    int[] B = new int[k];
    int[] C = new int[k];
    int[] D = new int[k];
    maps.add(B);
    maps.add(C);
    maps.add(D);
    for (int i = 0; i < k; i++) {
      B[i] = i;
      C[i] = i - 1 + k;
      D[i] = i - 1 + 2*k;
    }
    C[0] = 0;
    D[k - 1] = k - 1;
  }
  
  private void makeMapToB() {
    if (mapToB != null) return;
    mapToB = new int[size];
    for (int i = 0; i < size; i++) mapToB[i] = -1;
    for (int[] map : maps) {
      for (int i = 0; i < minAlgSize; i++) {
        final int iprime = map[i];
        if (mapToB[iprime] != -1 && mapToB[iprime] != i) {
          throw new IllegalArgumentException("Inconsistent maps");
        }
        mapToB[iprime] = i;
      }
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    SmallAlgebra minalg = new BasicAlgebra(null, 3, new ArrayList<Operation>());
    try {
      //minalg = org.uacalc.io.AlgebraIO.readAlgebraFile("/tmp/regS3.ua");
      minalg = org.uacalc.io.AlgebraIO.readAlgebraFile("/tmp/c3-2.ua");
    }
    catch (Exception e) { e.printStackTrace(); }
    SmallAlgebra alg = new AlgebraFromMinimalSets(minalg);
    System.out.println("card: " + alg.cardinality());
    try {
      org.uacalc.io.AlgebraIO.writeAlgebraFile(alg, "/tmp/algXXX25.ua");
    }
    catch (Exception e) { e.printStackTrace(); }
  }

}
