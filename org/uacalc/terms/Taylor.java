package org.uacalc.terms;

import org.uacalc.eq.*;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.util.*;
import java.util.*;

/**
 * Let f be a k-ary operation symbol. We consider a set of equations
 * where each side has the form f(x's and y's). For each index i
 * there is at least one equation with the ith coordinate on the two
 * sides are different (so f is a Taylor term). 
 * <p>
 * We are looking to see if there is some term t in this language of f
 * that is a Markovic-McKenzie-Siggers term. That is, an idempotent 
 * term satisfying
 * <br>
 * t(y,x,x,x) = t(x,x,y,y) and
 * t(x,x,y,x) = t(x,y,x,x)
 * <br>
 * By idempotence every term in f can be assumed to have a balanced
 * term tree. For depth d such a tree has k^d leaves and the term
 * is determined by the vector of variables at the leaves. So terms
 * of depth d can be represented by a vector of variables of length  
 * k^d and linearly ordered lexicographically. 
 * <p>
 * We use the equations of f to effective rewrite this vector into the
 * least equivalent one least in the lexicographic order. Using this 
 * we search all 4 variable terms for a MMS term.   
 * 
 * @author ralph
 *
 */
public class Taylor {

  Term taylorTerm;
  int arity;
  List<Equation> eqs;
  private final Map<IntArray,IntArray> rootMap = new HashMap<IntArray,IntArray>();
  
  public Taylor(Term taylor, List<Equation> eqs) {
    taylorTerm = taylor;
    arity = taylor.leadingOperationSymbol().arity();
    this.eqs = eqs;
    makeRootMapFromEqs(eqs);
  }
  
  public Taylor(int arity, List<List<IntArray>> inteqs) {
    this.arity = arity;
    List<Term> vars = new ArrayList<Term>(arity);
    for (int i = 0; i < arity; i++) {
      vars.add(new VariableImp("x_" + i));
    }
    taylorTerm = new NonVariableTerm(new OperationSymbol("f", arity), vars);
    makeRootMap(inteqs);
  }
  
  private void makeRootMapFromEqs(List<Equation> eqs) {
    // TODO:
  }
  
  private void makeRootMap(List<List<IntArray>> inteqs) {
    for (List<IntArray> eq : inteqs) {
      IntArray r0 = findRoot(eq.get(0));
      IntArray r1 = findRoot(eq.get(1));
      if (lexicographicallyCompare(r0, r1) < 0) rootMap.put(r1, r0);
      else if (lexicographicallyCompare(r0, r1) > 0) rootMap.put(r0, r1);
      r0 = findRoot(complement(eq.get(0)));
      r1 = findRoot(complement(eq.get(1)));
      if (lexicographicallyCompare(r0, r1) < 0) rootMap.put(r1, r0);
      else if (lexicographicallyCompare(r0, r1) > 0) rootMap.put(r0, r1);
    }
  }
  
  private IntArray complement(IntArray ia) {
    final int n = ia.size();
    final int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      if (ia.get(i) == 0) arr[i] = 1;
      else arr[i] = 0;
    }
    return new IntArray(arr);
  }
  
  private IntArray findRoot(IntArray ia) {
    final IntArray next = rootMap.get(ia);
    if (next == null) return ia;
    final IntArray r = findRoot(next);
    rootMap.put(ia, r);
    return r;
  }
  
  public static int lexicographicallyCompare(IntArray a, IntArray b) {
    return lexicographicallyCompare(a.getArray(), b.getArray());
  }
  
  public static int lexicographicallyCompare(int[] a, int[] b) {
    if (a.length != b.length) throw new IllegalArgumentException("Arrays not of the same size");
    final int n = a.length;
    for (int i = 0; i < n; i++) {
      if (a[i] < b[i]) return -1;
      if (a[i] > b[i]) return 1;
    }
    return 0;
  }
  
  
}
