
package org.uacalc.util;

//import org.uacalc.alg.*;
//import org.uacalc.alg.con.*;

/** 
 * An interface specifying the access methods for a task to be
 * run with a ProgressBar. This will wrap around a real computation.
 */

public interface Task {

  /**
   * Test if the computation is done.
   */
  public boolean done();

  /**
   * Start the computation.
   *
   */
  public void go();

  /**
   * Stop the computation. Since ThreadDeath is no longer allowed
   * we need to have a way to make sure the computation really stops.
   */
  public void stop();

  /**
   * In some tasks like finding the universe, this may not really
   * represent percent of the time.
   */
  public int percentDone();

  /**
   * The size computed so far.
   */
  public int amountComputed();

  /**
   * The number of elements left to consider. For universe it is
   * the number of ji's left.
   */
  public int leftToDo();

}

