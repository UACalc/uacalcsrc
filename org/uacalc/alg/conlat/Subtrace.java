/* Subtrace.java 2001/06/04 Ralph Freese */

package org.uacalc.alg.conlat;

import org.uacalc.util.IntArray;


import java.util.*;

/**
 * A class to hold a subtrace {a, b} and its TCT type.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public class Subtrace {

  private int a;
  private int b;
  private int type = -1;
  private boolean hasInvolution;

  Subtrace(int a, int b, boolean inv) {
    this.a = a;
    this.b = b;
    hasInvolution = inv;
  }

  Subtrace(int a, int b, boolean inv, int type) {
    this.a = a;
    this.b = b;
    hasInvolution = inv;
    this.type = type;
  }

  public int first() { return a; }
  public int second() { return b; }
  public int type() { return type; }
  public boolean hasInvolution() { return hasInvolution; }

  public void setType(int t) { this.type = t; }

  public String toString() {
    return "subtrace [" + a + ", " + b + "] typ = " + type 
                      + " inv: " + hasInvolution;
  }


}

