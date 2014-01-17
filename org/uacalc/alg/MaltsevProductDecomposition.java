package org.uacalc.alg;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.*;
import java.util.*;

/**
 * Suppose P is a robust property; a property such that if A is
 * an idempotent algebra in the Maltsev product of two idempotent
 * varieties satisfying P, then A satisfies P. We can speed up
 * a basic test of A having P by various decompositions of A.
 * One example is a subdirect decomposition. The other is to 
 * have a chain of congruences of A. Then P holds in A iff
 * it holds in every C which is B mod theta restricted to B, 
 * where B is a block (which is an algebra) of the congruence above theta.
 * 
 * 
 * A class to represent a decomposition of an idempotent algebra
 * into a quotient and the blocks as subalgebras of a congruence.
 * 
 * @author ralph
 *
 */
public class MaltsevProductDecomposition {

  private Partition congruence;
  private SmallAlgebra algebra;
  private List<SmallAlgebra> blockAlgebras; // 1 element blocks are not included
  private SmallAlgebra quotientAlgebra;
  
  /**
   * Make the decomposition.
   * 
   * @param alg    an idempotent, nontrivial algebra
   * @param cong   a congruence of alg
   */
  public MaltsevProductDecomposition(SmallAlgebra alg, Partition cong) {
    this.algebra = alg;
    this.congruence = cong;
    setup();
  }
 
  private void setup() {
    quotientAlgebra = new QuotientAlgebra(algebra, congruence);
    int[][] blocks = congruence.getBlocks();
    blockAlgebras = new ArrayList<SmallAlgebra>(blocks.length);
    for (int i = 0; i < blocks.length; i++) {
      if (blocks[i].length > 1) blockAlgebras.add(new Subalgebra(algebra, blocks[i]));
    }
  }

  public Partition getCongruence() {
    return congruence;
  }


  public void setCongruence(Partition congruence) {
    this.congruence = congruence;
  }


  public SmallAlgebra getAlgebra() {
    return algebra;
  }


  public void setAlgebra(SmallAlgebra algebra) {
    this.algebra = algebra;
  }

  public List<SmallAlgebra> getBlockAlgebras() {
    return blockAlgebras;
  }

  public void setBlockAlgebras(List<SmallAlgebra> blockAlgebras) {
    this.blockAlgebras = blockAlgebras;
  }

  public SmallAlgebra getQuotientAlgebra() {
    return quotientAlgebra;
  }

  public void setQuotientAlgebra(SmallAlgebra quotientAlgebra) {
    this.quotientAlgebra = quotientAlgebra;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }
  
}
