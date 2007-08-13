package org.uacalc.ui.tm;

public class CancelledException extends RuntimeException {

  public CancelledException() {
    super();
  }
  
  public CancelledException(String msg) {
    super(msg);
  }
  
}
