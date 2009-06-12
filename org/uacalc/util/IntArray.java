/* IntArray.java 2001/06/04 Ralph Freese */


package org.uacalc.util;

import java.util.*;

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
  
  public boolean satisfiesConstraint(final int[][] blocks) {
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
   * Checks if this intArray is equal on the indices of each block and
   * has the values specified by <code>values</code>.
   * 
   * @param blocks      an array of the level blocks 
   * @param values      an array of pairs [i,v] specifying array[i] = v
   * @return            true if the condition is satisfied
   */
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

  public final int size() {
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
   * Test if this is represents an idempotent function; that is,
   * one satisfying f(f(x)) = f(x).
   * 
   * @return
   */
  public boolean isIdempotent() {
    boolean ans = true;
    for (int i = 0; i < size; i++) {
      if (array[i] < 0 || array[i] >= size) return false;
      if (array[array[i]] != array[i]) return false;
    }
    return ans;
  }
  
  public static Comparator<IntArray> lexicographicComparitor() {
    return new Comparator<IntArray>() {
      public int compare(IntArray ia0, IntArray ia1) {
        final int min = Math.min(ia0.size(), ia1.size());
        for (int i = 0 ; i < min; i++ ) {
          if (ia0.get(i) < ia1.get(i)) return -1;
          if (ia0.get(i) > ia1.get(i)) return 1;
        }
        if (ia0.size() < ia1.size()) return -1;
        if (ia0.size() > ia1.size()) return 1;
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

}

