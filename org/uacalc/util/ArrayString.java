/* ArrayString.java */

package org.uacalc.util;

import java.lang.reflect.*;
    
/**
 * A static method to print out an array based on Java Tech Tips, Feb 5, 2002.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public class ArrayString {

  public static String toString(Object arr) {
    // if object reference is null or not
    // an array, call String.valueOf()
    if (arr == null || !arr.getClass().isArray()) {
      return String.valueOf(arr);
    }
    // set up a string buffer and
    // get length of array
    StringBuffer sb = new StringBuffer();
    int len = Array.getLength(arr);
    sb.append('[');
    for (int i = 0; i < len; i++) {
      if (i > 0) {
        sb.append(',');
      }
      // get the i-th element
      Object obj = Array.get(arr, i);
      // convert it to a string by
      // recursive toString() call
      sb.append(toString(obj));
    }
    sb.append(']');
    return sb.toString();
  }

}

