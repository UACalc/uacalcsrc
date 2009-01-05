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
  List<List<IntArray>> inteqs; // the equations as pairs of IntArray's.
  private final Map<IntArray,IntArray> rootMap = new HashMap<IntArray,IntArray>();
  
  private static Taylor markovicMcKenzieTerm;
  private static Taylor siggersTerm;
  
  public Taylor(Term taylor, List<Equation> eqs) {
    taylorTerm = taylor;
    arity = taylor.leadingOperationSymbol().arity();
    this.eqs = eqs;
    makeRootMapFromEqs(eqs);
  }
  
  public Taylor(OperationSymbol sym, List<List<IntArray>> inteqs) {
    this.inteqs = inteqs;
    this.arity = sym.arity();
    List<Term> vars = new ArrayList<Term>(arity);
    for (int i = 0; i < arity; i++) {
      vars.add(new VariableImp("x_" + i));
    }
    taylorTerm = new NonVariableTerm(sym, vars);
    makeRootMap(inteqs);
  }
  
  public Taylor(int arity, List<List<IntArray>> inteqs) {
    this(new OperationSymbol("f", arity), inteqs);
  }
  

  /**
   * Find the canonical form of a term <tt>t</tt> in the language of
   * <tt>f</tt>  with variables <tt>x</tt> and <tt>y</tt>. 
   * This first reduces via idempotence and then chooses the lexicographic
   * order with <tt>x</tt> before <tt>y</tt>.
   * 
   * @param t
   * @return
   */
  public Term canonicalForm(Term t) {
    if (t.isaVariable()) return t;
    List<Term> children = t.getChildren();
    List<Term> canonicalChildren = new ArrayList<Term>(arity);
    for (Term child : children) {
      canonicalChildren.add(canonicalForm(child));
    }
    List<Term> reps = new ArrayList<Term>();
    Map<Term,Integer> map = new HashMap<Term,Integer>();
    int current = 0;
    for (Term child : canonicalChildren) {
      if (map.get(child) == null) {
        map.put(child, current++);
        reps.add(child);
        if (current > 2) {
          return new NonVariableTerm(taylorTerm.leadingOperationSymbol(), canonicalChildren);
        }
      }
    }
    if (current == 1) return canonicalChildren.get(0); // all the same
    Term smallTerm, bigTerm;
    if (lexicographicallyCompare(reps.get(0), reps.get(1)) < 0) {
      smallTerm = reps.get(0);
      bigTerm = reps.get(1);
    }
    else {
      smallTerm = reps.get(1);
      bigTerm = reps.get(0);
    }
    int[] foo = new int[arity];
    for (int i = 0; i < arity; i++) {
      if (smallTerm.equals(canonicalChildren.get(i))) foo[i] = 0;
      else foo[i] = 1;
    }
    IntArray root = findRoot(new IntArray(foo));
    if (allEqual(root, 0)) return smallTerm;
    if (allEqual(root, 1)) return bigTerm;
    List<Term> modChildren = new ArrayList<Term>(arity);
    for (int i = 0; i < arity; i++) {
      if (root.get(i) == 0) modChildren.add(smallTerm);
      else modChildren.add(bigTerm);
    }
    return new NonVariableTerm(taylorTerm.leadingOperationSymbol(), modChildren);
  }
  
  public IntArray canonicalForm(IntArray arr) {
    return canonicalForm(arr, 0, arr.size());
  }
  
  private IntArray canonicalForm(IntArray arr, int start, int len) {
    if (len == arity) {
      final int[] tmp = new int[arity];
      System.arraycopy(arr, start, tmp, 0, len);
      System.arraycopy(findRoot(new IntArray(tmp)).getArray(), 0, arr, start, arity);
      return arr;
    }
    final int len2 = len / arity;
    HashSet<IntArray> hset = new HashSet<IntArray>();
    for (int i = 0; i < arity; i++) {
      hset.add(canonicalForm(arr, start + i * len2, len2));
    }
    if (hset.size() == 2) {
      Iterator<IntArray> it = hset.iterator();
      IntArray first = it.next();
      IntArray second = it.next();
      if (lexicographicallyCompare(first, second) > 0) {
        IntArray tmp = second;
        second = first;
        first = tmp;
      }
      IntArray foo = new IntArray(arity);
      for (int i = 0; i < arity; i++) {
        // here !!!
      }
    }
    return arr;
  }
  
  
  
  private void makeRootMapFromEqs(List<Equation> eqs) {
    // TODO:
  }
  
  public static Taylor markovicMcKenzieTerm() {
    if (markovicMcKenzieTerm == null) {
      List<List<IntArray>> eqs = new ArrayList<List<IntArray>>(2);
      List<IntArray> eq = new ArrayList<IntArray>(2);
      eq.add(new IntArray(new int[] {1,0,0,0}));
      eq.add(new IntArray(new int[] {0,0,1,1}));
      eqs.add(eq);
      eq = new ArrayList<IntArray>(2);
      eq.add(new IntArray(new int[] {0,0,1,0}));
      eq.add(new IntArray(new int[] {0,1,0,0}));
      eqs.add(eq);
      markovicMcKenzieTerm = new Taylor(new OperationSymbol("mm", 4), eqs);
    }
    return markovicMcKenzieTerm;
  }
  
  public static Taylor siggersTerm() {
    if (siggersTerm == null) {
      List<List<IntArray>> eqs = new ArrayList<List<IntArray>>(2);
      List<IntArray> eq = new ArrayList<IntArray>(2);
      eq.add(new IntArray(new int[] {1,1,0,0,0,0}));
      eq.add(new IntArray(new int[] {0,0,1,0,1,0}));
      eqs.add(eq);
      eq = new ArrayList<IntArray>(2);
      eq.add(new IntArray(new int[] {0,0,0,0,1,1}));
      eq.add(new IntArray(new int[] {0,1,0,1,0,0}));
      eqs.add(eq);
      siggersTerm = new Taylor(new OperationSymbol("s", 6), eqs);
    }
    return siggersTerm;
  }
  
  /**
   * Find a term which in the language of this which satisfies
   * the identities of g.
   * 
   * @param g
   * @return
   */
  public Term interprets(Taylor g, int level) {
    final int gArity = g.arity();
    final List<List<IntArray>> inteqs = g.inteqs();
    int pow = 1;
    for (int i = 0 ; i < level; i++) {
      pow = pow * arity;
    }
    int [] seq = new int[pow];
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(seq, gArity - 1);
    while (inc.increment()) {
      for (List<IntArray> eq : inteqs) {
        
      }
    }
    return null;
  }
  
  public Term termFromArray(final int[] arr, final int start, final int len) {
    if (len == 1) {
      if (arr[start] == 0) return Variable.x;
      return Variable.y;
    }
    final int len2 = len / arity;
    List<Term> lst = new ArrayList<Term>(arity);
    for (int i = 0 ;  i < arity; i++) {
      lst.add(termFromArray(arr, start + i * len2, len2));
    }
    return new NonVariableTerm(taylorTerm.leadingOperationSymbol(), lst);
  }
  
  private void makeRootMap(List<List<IntArray>> inteqs) {
    for (List<IntArray> eq : inteqs) {
      IntArray r0 = findRoot(eq.get(0));
      IntArray r1 = findRoot(eq.get(1));
      if (lexicographicallyCompare(r0, r1) < 0) {
        if (allEqual(r1, 1)) rootMap.put(r0, r1);
        else rootMap.put(r1, r0);
      }
      else if (lexicographicallyCompare(r0, r1) > 0) {
        if (allEqual(r0, 1)) rootMap.put(r1, r0);
        rootMap.put(r0, r1);
      }
      r0 = findRoot(complement(eq.get(0)));
      r1 = findRoot(complement(eq.get(1)));
      if (lexicographicallyCompare(r0, r1) < 0) {
        if (allEqual(r1, 1)) rootMap.put(r0, r1);
        else rootMap.put(r1, r0);
      }
      else if (lexicographicallyCompare(r0, r1) > 0) {
        if (allEqual(r0, 1)) rootMap.put(r1, r0);
        else rootMap.put(r0, r1);
      }
    }
  }
  
  private boolean allEqual(IntArray ia, int value) {
    for (int i = 0; i < arity; i++) {
      if (ia.get(i) != value) return false;
    }
    return true;
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
  
  /**
   * Constructs a term with a balanced term tree. All internal
   * nodes have f and leaves are the variables from varList so
   * varList should have length k^depth, where k is the arity of f.
   * 
   * @param f
   * @param depth       the depth, at least 1
   * @param varList
   * @return
   */
  public Term makeBalancedTayorTerm(OperationSymbol f, int depth, List<Variable> varList) {
    return balancedTT(f, depth, varList, 0);
  }
  
  private Term balancedTT(OperationSymbol f, int depth, 
                          List<Variable> varList, int start) {
    final int k = f.arity();
    final int factor = varList.size() / k;
    if (depth == 1) {
      List<Term> lst = new ArrayList<Term>(k);
      for (int i = 0; i < k; i++) {
        lst.add(varList.get(start + i));
      }
      return new NonVariableTerm(f, lst);
    }
    List<Term> lst = new ArrayList<Term>(k);
    int current = 0;
    for (int i = 0; i < k; i++) {
      lst.add(balancedTT(f, depth - 1, varList, current));
      current = current + factor;
    }
    return new NonVariableTerm(f, lst);
  }
  
  public int lexicographicallyCompare(Term s, Term t) {
    if (s.equals(t)) return 0;
    if (s.depth() < t.depth()) return -1;
    if (t.depth() < s.depth()) return 1;
    if (s.isaVariable()) {  // so both are variables
      if (s.equals(Variable.x)) return -1;
      return 1;
    }
    for (int i = 0; i < arity; i++) {
      int c = lexicographicallyCompare(s.getChildren().get(i), t.getChildren().get(i));
      if (c < 0) return -1;
      if (c > 0) return 1;
    }
    return 0;
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
  
  public int arity() { return arity; }
  
  public List<List<IntArray>> inteqs() { return inteqs; }
  
  public List<Equation> equations() { return eqs; }
  
}
