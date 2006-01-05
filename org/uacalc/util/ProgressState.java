
package org.uacalc.util;

/** 
 * An class to hold information about the state of a long computation.
 * It might be used in conjunction with a ProgressBar or just 
 * printing to System.out. 
 * <p>
 * The idea is there will be an interface, possibly called Task which
 * will hava the method getProgressState() and then the calculation
 * can be done in a thread and the progress displayed. There would also
 * be a cancel operation.
 */

public class ProgressState {

  /**
   * Test if the computation is done.
   */
  public boolean done() { return true; }

  public void reset() { }

}

