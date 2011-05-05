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
public class AlgebraFromMinimalSets extends GeneralAlgebra implements
    SmallAlgebra {

  SmallAlgebra minimalAlgebra;
  int minAlgSize;
  int algSize;
  List<int[]> maps;
  
  /**
   * A map from A to B, identity on B, and a isomorphism when restricted
   * to and minimal set.
   */
  int[] mapToB; // from A to B
  
  public AlgebraFromMinimalSets(SmallAlgebra minAlg) {
    this(null, minAlg, null);
  }
  
  public AlgebraFromMinimalSets(SmallAlgebra minAlg, List<int[]> maps) {
    this(null, minAlg, maps);
  }
  
  public AlgebraFromMinimalSets(String name, SmallAlgebra minAlg) {
    this(name, minAlg, null);
  }
  
  public AlgebraFromMinimalSets(String name, SmallAlgebra minAlg, List<int[]> maps) {
    super(name);
    this.minimalAlgebra = minAlg;
    minAlgSize = minAlg.cardinality();
    algSize = 3 * minAlgSize - 2;
    if (maps != null) this.maps = maps;
    else makeDefaultMaps();
    makeMapToB();
    setOperations(makeOps());
  }
  
  private List<Operation> makeOps() {
    int r = 0;
    List<Operation> ops = new ArrayList<Operation>();
    //ops.add(Operations.makeIntOperation("p", 1, algSize, mapToB));
    
    for (final int[] map : maps) {
      Operation op = new AbstractOperation("p" + r++, 1, algSize) {
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
    
    Operation toC = new AbstractOperation("toC", 1, algSize) {
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
    mapToB = new int[algSize];
    for (int[] map : maps) {
      for (int i = 0; i < minAlgSize; i++) {
        mapToB[map[i]] = i;
      }
    }
  }
  
  
  @Override
  public AlgebraType algebraType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void convertToDefaultValueOps() {
    // TODO Auto-generated method stub

  }

  @Override
  public int elementIndex(Object elem) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object getElement(int k) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List getUniverseList() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map getUniverseOrder() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
