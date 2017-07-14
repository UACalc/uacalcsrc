
package org.uacalc.alg.conlat;

/* Partition.java 2001/06/04 Ralph Freese */

import java.util.*;

/**
 * This interface specifies the basic operations for partitions on the set
 * {0, 1, ..., n-1}. This also specifies that the partition should have a
 * system of distinct representatives, an SDR.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public interface Partition extends BinaryRelation {

  /**
   * Printing types: the internal representation.
   */
  public enum PrintType {
    INTERNAL, EWK, BLOCK, HUMAN, SQ_BRACE_BLOCK
  }
  
  
  //public static final int INTERNAL = 0;

  /**
   * Printing types: the algebra program representtation: a comma separated
   * sequence of ints defining a map whose kernel is the partition.
   */
  //public static final int EWK = 1;

  /**
   * Printing types: blocks, the useual way of writing a partition.
   */
  //public static final int BLOCK = 2;

  /**
   * Printing types: blocks, plus number of blocks at the end.
   */
  //public static final int HUMAN = 3;

  /**
   * Printing types: blocks using [ and ].
   */
  //public static final int SQ_BRACE_BLOCK = 4;
  
  /**
   * This returns the array representation of the partition as described 
   * in Ralph Freese's notes on partitions, see 
   * {@link <a href="http://www.math.hawaii.edu/~ralph/Notes/">
                   http://www.math.hawaii.edu/~ralph/Notes/</a>}.
   */
  public int[] toArray();

  /** 
   * Note r and s must be roots and distinct.
   */
  public void joinBlocks(int r, int s);

  public Partition join(Partition part2);

  public Partition meet(Partition part2);

  public boolean leq(Partition part2);

  public void normalize();

  public int universeSize();

  public int numberOfBlocks();

  public boolean isRelated(int i, int j);

  public String toString(PrintType kind);
  
  public String toString(int maxLen);

  public int representative(int i);

  public boolean isRepresentative(int i);
 
  public int[] representatives();
  
  public int blockIndex(int i);

  public int[][] getBlocks();
  
  public boolean isInitialLexRepresentative();
  
  /**
   * Test if all the blocks have the same size.
   * @return
   */
  public boolean isUniform();

  public boolean isZero();

}

