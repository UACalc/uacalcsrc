/* IntArray.java 2001/06/04 Ralph Freese */


package org.uacalc.util;

import java.util.*;
import org.uacalc.alg.conlat.Partition;

/**
 * This class is a wrapper for an array of int's mainly so we can 
 * specify the equals and hashCode methods.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public class IntArray implements Cloneable {

  protected int size;
  protected int[] array;


  public IntArray() {
    this.array = null;
    this.size = -1;
  }

  public IntArray(int[] array) {
    this.array = array;
    this.size = array.length;
  }

  public IntArray(int size) {
    this.size = size;
    this.array = new int[size];
  }
  
  /**
   * Test if the intArray is constant on each block of the
   * partition defined by blocks.
   * 
   * @param blocks    the blocks of a partition on the index set
   * @return          true if the condition is satisfied
   */
  public boolean satisfiesBlocksConstraint(final int[][] blocks) {
    for (int i = 0; i < blocks.length; i++) {
      final int[] block = blocks[i];
      final int first = array[block[0]];
      for (int j = 1; j < block.length; j++) {
        if (first != array[block[j]]) return false;
      }
    }
    return true;
  }
  
  /**
   * Test if this satisfies array[i] = v for each [i,v] in values.
   * 
   * @param values      an array of pairs [i,v] specifying array[i] = v
   * @return            true if the condition is satisfied
   */
  public boolean satisfiesValuesConstraint(final int[][] values) {
    for (int i = 0; i < values.length; i++) {
      if (array[values[i][0]] != values[i][1]) return false;
    }
    return true;
  }
  
  /**
   * Test if this IntArray value at index is in a set
   * of possibleValues.
   * 
   * @param index           the index to test
   * @param possibleValues  a set of possible values
   * @return
   */
  public boolean satisfiesSetConstraint(final int index, final Set<Integer> possibleValues) {
    return possibleValues.contains(array[index]);
  }
  
  /**
   * Test if this IntArray's value at index is congruent mod alpha to 
   * the element with index elemIndex.
   * 
   * @param index
   * @param alpha
   * @param elemIndex
   * @return
   */
  public boolean satisfiesCongruenceConstraint(final int index, final Partition alpha, final int elemIndex) {
    return alpha.isRelated(elemIndex, array[index]);
  }
  
  /**
   * Checks if this intArray is equal on the indices of each block and
   * has the values specified by <code>values</code>.
   * 
   * @param blocks      an array of the level blocks 
   * @param values      an array of pairs [i,v] specifying array[i] = v
   * @return            true if the condition is satisfied
   */
  /*
  public boolean satisfiesConstraint(final int[][] blocks, final int[][] values) {
    for (int i = 0; i < blocks.length; i++) {
      final int[] block = blocks[i];
      final int first = array[block[0]];
      for (int j = 1; j < block.length; j++) {
        if (first != array[block[j]]) return false;
      }
    }
    for (int i = 0; i < values.length; i++) {
      if (array[values[i][0]] != values[i][1]) return false;
    }
    return true;
  }
  */

  public final boolean equals(Object obj) {
    if (obj == null) return false;
    try {
      IntArray p = (IntArray)obj;
      return equalIntArrays(p.array, array);
    }
    catch (ClassCastException e) { return false; }
    catch (ArrayIndexOutOfBoundsException e) { return false; }
  }

  // something better ??? Maybe replace 2 with this.size.
/*
  public int hashCode() {
    int ans = 0;
    int power = 1;
    int n = array.length;
    for (int i = 0; i < n; i++) {
      ans = ans + power * (array[i] < 0 ? - array[i] : array[i]);
      power = 2 * power;
    }
    return ans;
  }
*/

  public final int hashCode() {
    int ans = 1;
    int n = array.length;
    for (int i = 0; i < n; i++) {
      //ans = ans * 31 + (array[i] < 0 ? - array[i] : array[i]);
      ans = ans * 31 + array[i];
    }
    return ans;
  }

  public final int[] toArray() {
    return array;
  }


  public final int[] getArray() {
    return array;
  }

  public final int universeSize() {
    return size;
  }

  public final void setIntArray(int[] v) {
    array = v;
  }

  public final int get(int i) {
    return array[i];
  }

  public final void set(int index, int value) {
    array[index] = value;
  }
  
  /**
   * Test if this represents an idempotent function; that is,
   * one satisfying the equation f(f(x)) &asymp; f(x).
   * 
   * @return <code>true</code> iff f(f(x)) = f(x) for all 0 &le; x &lt; <code>size</code>.
   */
  public boolean isIdempotent() {
    for (int i = 0; i < size; i++) {
      final int j = array[i];
      if (j < 0 || j >= size) return false;
      if (array[j] != j) return false;
    }
    return true;
  }
  
  public boolean isConstant() {
    if (size == 0) return true;
    int a = array[0];
    for (int i = 1; i < size; i++) {
      if (array[i] != a) return false;
    }
    return true;
  }
  
  public static Comparator<IntArray> lexicographicComparitor() {
    return new Comparator<IntArray>() {
      public int compare(IntArray ia0, IntArray ia1) {
        final int min = Math.min(ia0.universeSize(), ia1.universeSize());
        for (int i = 0 ; i < min; i++ ) {
          if (ia0.get(i) < ia1.get(i)) return -1;
          if (ia0.get(i) > ia1.get(i)) return 1;
        }
        if (ia0.universeSize() < ia1.universeSize()) return -1;
        if (ia0.universeSize() > ia1.universeSize()) return 1;
        return 0;
      }
    };
  }

  public Object clone() {
    IntArray ia = new IntArray(size);
    int[] arr = ia.getArray();
    for (int i = 0; i < size; i++) {
      arr[i] = array[i];
    }
    return ia;
  }

  public String toString() {
    return intArrayToString(array);
  }

  public static final String intArrayToString(int [] array) {
    if (array.length == 0) return "[]";
    StringBuffer sb = new StringBuffer("[");
    for (int i = 0; i < array.length; i++) {
      sb.append(String.valueOf(array[i]));
      sb.append(", ");
    }
    sb.setLength(sb.length() - 2);
    sb.append("]");
    return sb.toString();
  }

  public static final boolean equalIntArrays(int[] u, int[] v) {
    final int n = u.length;
    if (n != v.length) return false;
    for (int i = 0; i < n; i++) {
      if (u[i] != v[i]) return false;
    }
    return true;
  }
  
  /**
   * If str is a sequence of ints separated by either commas
   * or spaces, this puts them into an array.
   * 
   * @param str
   * @return
   */
  public static int[] stringToArray(String str) {
    str = str.trim();
    final String[] elts = str.split("[,\\s]+");
    int[] ans = new int[elts.length];
    for (int i = 0; i < elts.length; i++) {
      ans[i] = Integer.parseInt(elts[i]);
    }
    return ans;
  }

}

