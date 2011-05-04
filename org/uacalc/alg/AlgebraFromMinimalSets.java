package org.uacalc.alg;

import java.util.List;
import java.util.Map;
import org.uacalc.alg.op.*;

/**
 * Starting with an algebra B which is permutational (nonconstant
 * unary polynomials are permutations) and a geometry for the 
 * minimal sets, this constructs an algebra A with tame minimal sets, 
 * having B as a minimal set. Initially we will assume the geometry
 * has 3 minimal sets, B, C, D, with C And D disjoint and the intersection
 * of B and C just 0 and B and D the last element of B.
 * 
 * @author ralph
 *
 */
public class AlgebraFromMinimalSets extends GeneralAlgebra implements
    SmallAlgebra {

  SmallAlgebra minimalAlgebra;
  int minAlgSize;
  int algSize;
  
  
  public AlgebraFromMinimalSets(SmallAlgebra minAlg) {
    this(null, minAlg);
  }
  
  public AlgebraFromMinimalSets(String name, SmallAlgebra minAlg) {
    super(name);
    this.minimalAlgebra = minAlg;
    minAlgSize = minAlg.cardinality();
    algSize = 3 * minAlgSize - 2;
    setup();
  }
  
  private void setup() {
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
