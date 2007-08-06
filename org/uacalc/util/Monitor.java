package org.uacalc.util;

import org.uacalc.ui.tm.*;

public class Monitor {

  private boolean cancelled = false;
  
  public boolean isCancelled() { return cancelled; }
  
  public void setCancelled(boolean v) { cancelled = v; }
  
  public void cancel() { cancelled = true; }
  
  public void report(String s) { }
  
}
