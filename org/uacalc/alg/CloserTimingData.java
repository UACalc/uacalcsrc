package org.uacalc.alg;


import org.uacalc.alg.op.*;
import org.uacalc.ui.tm.ProgressReport;
import java.util.*;
import java.math.*;

/**
 * A class to hold the data for the timing information in the UI.
 * 
 * @author ralph
 *
 */
public class CloserTimingData {
  
  private final ProgressReport report;
  private final int projs;
  
  private int pass = 0;
  private int nextPassSize = 0;
  private int currPassSize = 0; // for time left
  private int lastPassSize = 0; // for time left
  private final int[] arities;
  
  private long funcAppsNeeded;
  private long appsSoFar = 0;
   
  private long passStartTime = -1;
  private double msPerApp;
  private boolean updateTime = true;
  
  public CloserTimingData(BigProductAlgebra algebra, ProgressReport report) {
    this.report = report;
    projs = algebra.getNumberOfFactors();
    final int k = algebra.operations().size();
    arities = new int[k];
    for (int i = 0; i < k; i++) {
      arities[i] = algebra.operations().get(i).arity();
    }
  }
  
  public void updatePass(int size) {
    final long time = System.currentTimeMillis();
    long passTime = time - passStartTime;
    if (appsSoFar != 0) msPerApp = ((double)passTime) / appsSoFar;
    System.out.println("msPerApp now is " + msPerApp);
    passStartTime = time; // may want to publish the difference in report
    lastPassSize = currPassSize;
    currPassSize = size;
    pass++;
    funcAppsNeeded = countFuncApplications(lastPassSize, currPassSize);
    System.out.println("pass " + pass + ", funcAppsNeeded: " + funcAppsNeeded);
    appsSoFar = 0;
    System.out.println("time for pass: " + (long)(funcAppsNeeded * msPerApp));
  }
  
  public void incrementApps() {
    appsSoFar = appsSoFar + projs;
    if (updateTime && appsSoFar > 10000000) {
      updateTime = false;
      long delta = System.currentTimeMillis() - passStartTime;
      msPerApp = ((double)delta) / appsSoFar;
      long t = (long)((funcAppsNeeded - appsSoFar) * msPerApp);
      System.out.println("time left: " + t);
      System.out.println("msPerApp = " + msPerApp);
      System.out.println("funcAppsNeeded: " + funcAppsNeeded);
      System.out.println("appsSoFar: " + appsSoFar);
    }
  }
  
  public void incrementNextPassSize() {
    nextPassSize++;
  }
  
  private long countFuncApplications(int size0, int size1) {
    BigInteger ans = BigInteger.ZERO;
    final BigInteger s0 = BigInteger.valueOf(size0);
    final BigInteger s1 = BigInteger.valueOf(size1);
    for (int i = 0; i < arities.length; i++) {
      final int r = arities[i];
      ans = ans.add(s1.pow(r).subtract(s0.pow(r)));
    }
    if (ans.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) return -1;
    return ans.longValue() * projs;
  }
     
    
}
