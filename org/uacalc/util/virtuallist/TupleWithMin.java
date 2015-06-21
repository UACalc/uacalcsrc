package org.uacalc.util.virtuallist;


import org.uacalc.util.*;
import org.uacalc.util.virtuallist.*;
import java.util.Arrays;
import java.util.stream.*;

public class TupleWithMin implements LongList<int[]> {

  private final int arrayLen;
  private final int size;
  private final int min;
  private final int diff;
  private final long[] partialSums;
  
  public TupleWithMin(int arrayLen, int base, int min) {
    this.arrayLen = arrayLen;
    this.size = base;
    this.min = min;
    this.diff = base - min;
    this.partialSums = new long[arrayLen];
    long partial = 0;
    long summand = diff;
    for (int i = 1; i < arrayLen; i++) {
      summand = summand * min;
    }
    partialSums[0] = summand;
    for (int i = 1; i < arrayLen; i++) {
      summand = (summand * base) / min;
      partialSums[i] = partialSums[i-1] + summand;
    }
  }
  
  @Override
  public int[] get(long k) {
    int stage = 0;
    while (k >= partialSums[stage]) stage++;
    if (stage > 0) k = k - partialSums[stage - 1];
    
    final int[] ans = new int[arrayLen];
    for (int i = 0; i < stage; i++) {
      ans[i] = (int)(k % size);
      k = k / size;
    }
    ans[stage] = min + (int)(k % diff);
    k = k / diff;
    for (int i = stage + 1; i < arrayLen; i++) {
      ans[i] = (int)(k % min);
      k = k / min;
    }
    return ans;
  }
  
  @Override
  public long size() {
    return partialSums[arrayLen - 1];
  }

  public static void main(String[] args) {
    LongList<int[]> tuples = new TupleWithMin(3, 4, 2);
    for (int i = 0; i < 56; i++) {
      int[] arr = tuples.get(i);
      System.out.println("arr: " + Arrays.toString(arr));
    }

  }

}

