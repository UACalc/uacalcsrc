/* BasicPermutationGroup.java (c) 2005/10/10  Ralph Freese */

package org.uacalc.group;

import java.util.*;

import org.uacalc.alg.*;
import org.uacalc.util.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.sublat.*;

/**
 * This class represents a group of permutation on the set 
 * {0, ..., n-1}.
 */
public class PermutationGroup extends GeneralAlgebra {

  private List generators;
  private List universeList;
  private int underlyingSetSize;
  private IntArray identity;

  public PermutationGroup(String name, List generators) {
    super(name);
    this.generators = generators;
  }

  public PermutationGroup(String name, List generators, List universeList) {
    super(name);
    this.generators = generators;
    this.universeList = universeList;
  }

  public static Operation makeProdOp(final int algSize) {
    Operation op = new AbstractOperation(OperationSymbol.PRODUCT, algSize) {
      public Object valueAt(List args) {
        return prod((IntArray)args.get(0), (IntArray)args.get(1));
      }
    };
    return op;
  }

  public static IntArray prod(final IntArray p0, final IntArray p1) {
    final int[] arr0 = p0.getArray();
    final int[] arr1 = p1.getArray();
    final int n = arr0.length;
    final int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = arr0[arr1[i]];
    }
    return new IntArray(arr);
  }

  public static Operation makeInvOp(final int algSize) {
    Operation op = new AbstractOperation(OperationSymbol.INVERSE, algSize) {
      public Object valueAt(List args) {
        return inv((IntArray)args.get(0));
      }
    };
    return op;
  }

  public static IntArray inv(IntArray a) {
    final int[] arr0 = a.getArray();
    final int n = arr0.length;
    final int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[arr0[i]] = i;
    }
    return new IntArray(arr);
  }
 
  public static Operation makeIdOp(final int algSize, final int setSize) {
    Operation op = new AbstractOperation(OperationSymbol.IDENTITY, algSize) {
      public Object valueAt(List args) {
        return id(setSize);
      }
    };
    return op;
  }

  public static IntArray id(final int setSize) {
    final int[] arr = new int[setSize];
    for (int i = 0; i < setSize; i++) {
      arr[i] = i;
    }
    return new IntArray(arr);
  }
 
}


