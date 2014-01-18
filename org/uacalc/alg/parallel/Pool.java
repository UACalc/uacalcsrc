package org.uacalc.alg.parallel;

import java.util.concurrent.*;
import java.util.*;

/** 
 * A single global ForkJoinPool. (They say it is bad
 * to create several pools.)
 * 
 * @author ralph
 *
 */
public class Pool {
  static ForkJoinPool fjPool = new ForkJoinPool();
}
