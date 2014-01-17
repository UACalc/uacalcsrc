package org.uacalc.alg;

import java.util.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.io.AlgebraIO;

/**
 * An iterator for idempotent algebras giving sections, this is, quotients 
 * of subalgebras of the given algebra. If P is a robust property in the sense of 
 * Freese and McKenzie, then the variety of the algebra has P if and only if 
 * each of these sections does.
 * 
 * @author ralph
 *
 */
public class MaltsevDecompositionIterator implements Iterator<SmallAlgebra> {

  SmallAlgebra algebra;
  private Partition lower;
  private Partition upper;
  private int[][] blocks;
  private int numBlocks;
  private int blockIndex = 0;
  private boolean hasNext = true;
  
  /**
   * Given an idempotent algebra this constructs an iterator of
   * SmallAlgebras that are sections, this is, quotients of subalgebras
   * of alg. If P is a robust property in the sense of Freese and McKenzie,
   * then the variety of alg has P if and only if each of these sections
   * does.
   * 
   * @param alg  an idempotent algebra
   * @throws     IllegralArguementException if alg is not idempotent
   */
  public MaltsevDecompositionIterator(SmallAlgebra alg) {
    if (!alg.isIdempotent()) 
      throw new IllegalArgumentException("Alg: " + alg + " must be idempotent");
    this.algebra = alg;
    //setup();
    upper = alg.con().zero();
    resetCongs();
  }
  
  private boolean resetCongs() {
    if (upper.equals(algebra.con().one())) {
      hasNext = false;
      return false;
    }
    lower = upper;
    upper = algebra.con().findUpperCover(lower);
    blocks = upper.getBlocks();
    numBlocks = upper.numberOfBlocks();
    blockIndex = 0;
    return true;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  public boolean hasNext() {
    return hasNext;
  }
  
  public SmallAlgebra next() {
    if (!hasNext) throw new NoSuchElementException();   
    SmallAlgebra alg = getNextAlgebra(); // current block alg mod restricted congr
    blockIndex++;
    if (blockIndex == numBlocks) resetCongs();
    return alg;
  }
  
  private SmallAlgebra getNextAlgebra() {
    int[] block = blocks[blockIndex];
    Subalgebra subalg = new Subalgebra(algebra, block); 
    Partition par = subalg.restrictPartition(lower);
    SmallAlgebra alg = new QuotientAlgebra(subalg, par);
    return alg;
  }
  
  
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    SmallAlgebra n5 = AlgebraIO.readAlgebraFile("/Users/ralph/Java/Algebra/algebras/n5.ua");
    Iterator<SmallAlgebra> iter = new MaltsevDecompositionIterator(n5);
    while (iter.hasNext()) {
      SmallAlgebra alg = iter.next();
      System.out.println("|alg|: " + alg.cardinality());
    }
  }

}
