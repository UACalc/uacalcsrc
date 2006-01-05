/* Horner.java (c) 2003/06/07  Ralph Freese */

package org.uacalc.util;

import java.util.*;

/**
 * This has static methods for the Horner encoding and its inverse.
 */
public final class Horner {

  protected Horner() {}

  /**
   * Returns the Horner encoding of an int array representing an element
   * from a direct product of algebras with various sizes.
   *
   * @param args   the element of the direct product.
   * @param sizes  the sizes of the algebras. It should have the same
   *               length as <tt>args</tt>.
   */
  public static final int horner(int[] args, int[] sizes) {
    int k = args.length;
    int ans = args[k - 1];
    for (int i = k - 2; i >= 0; i--) {
      ans = sizes[i + 1] * ans + args[i];
    }
    return ans;
  }

  /**
   * Returns the int array corresponding to this Horner encoding
   * for a direct product of algebras with various sizes.
   *
   * @param k      the Horner encoding of the element of the direct product.
   * @param sizes  the sizes of the algebras. 
   */
  public static final int[] hornerInv(int k, int[] sizes) {
    return hornerInv(k, sizes, null);
  }

  /**
   * Returns the int array corresponding to this Horner encoding
   * for a direct product of algebras with various sizes.
   *
   * @param k      the Horner encoding of the element of the direct product.
   * @param sizes  the sizes of the algebras. 
   * @param dest   an array to hold the answer; if null a new array is made.
   */
  public static final int[] hornerInv(int k, int[] sizes, int[] dest) {
    final int n = sizes.length;
    final int[] ans = dest == null ? new int[n] : dest;
    for (int i = 0; i < n - 1; i++) {
      ans[i] = k % sizes[i+1];
      k = (k - ans[i]) / sizes[i+1];
    }
    ans[n-1] = k;
    return ans;
  }

  /**
   * Returns the Horner encoding of an int array representing an element
   * from a direct product of algebras all with the same size, such as
   * a direct power.
   *
   * @param args   the element of the direct product.
   * @param size   the size of the algebras.
   */
  public static final int horner(int[] args, int size) {
    int arity = args.length;
    int ans = 0;
    for (int i = arity - 1; i >= 0; i--) {
      ans = size * ans + args[i];
    }
    return ans;
  }

  /**
   * Returns the int array corresponding to this Horner encoding
   * for a direct product of algebras with the same size.
   *
   * @param k      the Horner encoding of the element of the direct product.
   * @param size   the size of each algebra. 
   * @param length the number of algebras. 
   */
  public static final int[] hornerInv(int k, int size, int length) {
    return hornerInv(k, size, length, null);
  }


  /**
   * Returns the int array corresponding to this Horner encoding
   * for a direct product of algebras with the same size.
   *
   * @param k      the Horner encoding of the element of the direct product.
   * @param size   the size of each algebra. 
   * @param len    the number of algebras. 
   * @param dest   an array to hold the answer; if null a new array is made.
   */
  public static final int[] hornerInv(int k, int size, int len, int[] dest) {
    final int n = len;
    final int[] ans = dest == null ? new int[n] : dest;
    if (n == 0) return ans;
    for (int i = 0; i < n - 1; i++) {
      ans[i] = k % size;
      k = (k - ans[i]) / size;
    }
    ans[n-1] = k;
    return ans;
  }

  /**
   * Returns the Horner encoding of an int array representing an element
   * from a direct product of algebras with the same size.
   *
   * @param args   the element of the direct product.
   * @param size   the size of each algebra.
   */
  public static final int horner(Integer[] args, int size) {
    int arity = args.length;
    int ans = 0;
    for (int i = arity - 1; i >= 0; i--) {
      ans = size * ans + args[i].intValue();
    }
    return ans;
  }

  /**
   * A convenience method for generating a new array with the reverse
   * order of the given array.
   */
  public static int[] reverseArray(int[] arr) {
    final int[] ans = new int[arr.length];
    final int max = arr.length - 1;
    for (int i = 0; i < ans.length; i++) {
      ans[i] = arr[max - i];
    }
    return ans;
  }

  /**
   * If values are the values of a function at [0,0, ...,0], [1,0,...,0],
   * this gives the values in the order [0,0, ...,0], [0,0,...,1], ...  .
   */
  public static int[] leftRightReverse(final int[] values, 
                                       final int algSize, final int arity) {
    final int[] ans = new int[values.length];
    for (int i = 0; i < values.length; i++) {
      int[] foo = reverseArray(Horner.hornerInv(i, algSize, arity));
      int iPrime = Horner.horner(foo, algSize);
      ans[iPrime] = values[i];
    }
    return ans;
  }
  
  public static void main(String[] args) {
    //int[] a = new int[] {1, 2, 3};
    int[] a = new int[] {0, 0, 3};
    int[] s = new int[] {4, 5, 6};
    System.out.println(ArrayString.toString(a));
    System.out.println(ArrayString.toString(s));
    //int k = horner(a, s);
    int k = horner(a, 10);
    System.out.println(k);
    //System.out.println(ArrayString.toString(hornerInv(k, s)));
    System.out.println(ArrayString.toString(hornerInv(k, 10, 3)));
  }

}
