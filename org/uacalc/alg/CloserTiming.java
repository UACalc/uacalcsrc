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
public class CloserTiming {
  
  private final ProgressReport report;
  private final long projs;
  
  private int pass = 0;
  private int nextPassSize = 0;
  private int currPassSize = 0; // for time left
  private int lastPassSize = 0; // for time left
  private final int[] arities;
  
  private long appsNeeded;
  private long appsThisPass = 0;
  private long localApps = 0;
  //private long appsSoFar = 0;
   
  private long passStartTime = -1;
  private double msPerApp;
  private boolean updateTime = true;
  private boolean atBeginning = true;
  private long startNanoTime;
  
  
  private static final long initCount = 20000000;  // should be less than 2 seconds to do this many.
  private static final long secondCount = 60000000;
  private static final long thirdCount = 60000000;
  private long realInitCount;
  
  public CloserTiming(BigProductAlgebra algebra, ProgressReport report) {
    this.report = report;
    projs = algebra.getNumberOfFactors();
    final int k = algebra.operations().size();
    arities = new int[k];
    for (int i = 0; i < k; i++) {
      arities[i] = algebra.operations().get(i).arity();
    }
  }
  
  public void updatePass(int size) {
    nextPassSize = 0;
    appsThisPass = 0;  
    updateTime = true;
    atBeginning = true;
    //final long time = System.currentTimeMillis();
    //long passTime = time - passStartTime;
    //if (appsSoFar != 0) msPerApp = ((double)passTime) / appsSoFar;
    //System.out.println("msPerApp now is " + msPerApp);
    //passStartTime = time; // may want to publish the difference in report
    lastPassSize = currPassSize;
    currPassSize = size;
    pass++;
    appsNeeded = countFuncApplications(lastPassSize, currPassSize);
    System.out.println("pass " + pass + ", funcAppsNeeded: " + appsNeeded);
    if (report != null) report.setTimeNext("");
    //appsSoFar = 0;
    //System.out.println("time for pass: " + (long)(appsNeeded * msPerApp));
  }
  
  public void incrementApps() {
    //appsSoFar = appsSoFar + projs;
    appsThisPass = appsThisPass + projs;
    localApps = localApps + projs;
    if (atBeginning  && appsThisPass > initCount) {
      atBeginning = false;
      realInitCount = appsThisPass;
      startNanoTime = System.nanoTime();
    }
    else if (updateTime  && appsThisPass > secondCount) {
      localApps = 0;
      updateTime = false;
      double del = (double)(System.nanoTime() - startNanoTime) / 1000000;
      msPerApp = del / (appsThisPass - realInitCount);
      long t = (long)((appsNeeded - appsThisPass) * msPerApp);
      report.setTimeLeft(msToString(t));
      System.out.println(msToString(t));
      System.out.println("msPerApp = " + msPerApp);
      System.out.println("funcAppsNeeded: " + appsNeeded);
      System.out.println("appsSoFar: " + appsThisPass);
    }
    else if (localApps > thirdCount) {
      localApps = 0;
      double del = (double)(System.nanoTime() - startNanoTime) / 1000000;
      msPerApp = del / (appsThisPass - realInitCount);
      long t = (long)((appsNeeded - appsThisPass) * msPerApp);
      report.setTimeLeft(msToString(t));
      //System.out.println("Time Left: " + msToString(t));
      long nextApps = countFuncApplications(currPassSize, nextPassSize + currPassSize);
      t = (long)(nextApps * msPerApp);
      report.setTimeNext(msToString(t));
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
  
  private String msToString(long ms) {
    final String colon = ":";
    final long totSecs = ms / 1000;
    final long secs = totSecs % 60;
    final long totMins = totSecs / 60;
    final long mins = totMins % 60;
    final long hrs = totMins / 60;
    String secsString = secs < 10 ? "0" + Long.toString(secs) : Long.toString(secs);
    if (hrs == 0) {
      if (mins == 0) return Long.toString(secs);
      return Long.toString(mins) + colon + secsString;
    }
    String minsString = mins < 10 ? "0" + Long.toString(mins) : Long.toString(mins);
    return Long.toString(hrs) + colon + minsString + colon + secsString;
  }

  
}
