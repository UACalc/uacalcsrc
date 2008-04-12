package org.uacalc.ui.tm;

import java.util.*;
import java.util.concurrent.*;

public class BackgroundExec {

  private static ExecutorService backgroundExec = Executors.newCachedThreadPool();
  
  public static ExecutorService getBackgroundExec() { return backgroundExec; }
  
}
