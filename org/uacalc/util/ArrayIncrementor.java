
package org.uacalc.util;


/** 
 * This is for in place incrementing of an array. In place means the
 * array returned is the same array in memory with the elements changed.
 */
public interface ArrayIncrementor {

  /**
   * Modify the array to be the next one; return false if there is no more.
   */
  public boolean increment();


}

