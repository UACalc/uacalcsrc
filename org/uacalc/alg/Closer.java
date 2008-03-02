package org.uacalc.alg;

import java.util.*;
import java.util.logging.*;
import java.math.BigInteger;
import org.uacalc.util.*;
import org.uacalc.ui.tm.CancelledException;
import org.uacalc.terms.*;
import org.uacalc.eq.*;

import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;

/**
 * A class for finding the closure with configurations for several options
 * and fields to hold side results.
 * It only deals with BigProduct's now. We might call it BigProductCloser
 * and have Closer as an interface.
 * 
 * 
 * @author ralph
 *
 */
public class Closer {
  
  BigProductAlgebra algebra;
  List<IntArray> ans;
  boolean completed = false; // sometimes we end early
  List<IntArray> generators;
  Map<IntArray,Term> termMap; // initially a map from generators to variables.
  IntArray eltToFind;
  List<IntArray> eltsToFind;
  SmallAlgebra imageAlgebra;
  Map<IntArray,Integer> homomorphism; // actually a partial homo into imageAlg.
  Equation failingEquation = null; // a list of two terms
  
  public Closer(BigProductAlgebra alg, List<IntArray> gens) {
    this.algebra = alg;
    this.generators = gens;
  }
  
  public Closer(BigProductAlgebra alg, List<IntArray> gens, Map<IntArray,Term> termMap) {
    this(alg, gens);
    this.termMap = termMap;
  }
  
  public Closer(BigProductAlgebra alg, List<IntArray> gens, boolean makeTermMap) {
    this(alg, gens);
    if (makeTermMap) setupTermMap();
  }
  
  private void setupTermMap() {
    termMap = new HashMap<IntArray,Term>();
    if (generators.size() == 1) termMap.put(generators.get(0), Variable.x);
    if (generators.size() == 2) {
      termMap.put(generators.get(0), Variable.x);
      termMap.put(generators.get(1), Variable.y);
    }
    if (generators.size() == 3) {
      termMap.put(generators.get(0), Variable.x);
      termMap.put(generators.get(1), Variable.y);
      termMap.put(generators.get(2), Variable.z);
    }
    int k = 0;
    if (generators.size() > 3) {
      for (Iterator<IntArray> it = generators.iterator(); it.hasNext(); k++) {
        Variable var = new VariableImp("x_" + k);
        termMap.put(it.next(), var);
      }
    }
  }
  
  public List<IntArray> getAnswer() { return ans; }
  
  public Equation getFailingEquation() { return failingEquation; }
  
  public List<IntArray> getGenerators() { return generators; }
  
  public void setGenerators(List<IntArray> generators) { 
    this.generators = generators;
  }
  
  public Map<IntArray,Term> getTermMap() { return termMap; }
  
  public void setTermMap(Map<IntArray,Term> termMap) {
    this.termMap = termMap;
  }
  
  public SmallAlgebra getImageAlgebra() { return imageAlgebra; }
  
  public void setImageAlgebra(SmallAlgebra alg) {
    if (!alg.similarityType().equals(algebra.similarityType())) {
      throw new IllegalArgumentException("the algebras must be similar");
    }
    imageAlgebra = alg;
  }
  
  // TODO add convenience methods to build these maps.
  public Map<IntArray,Integer> getHomomorphism() { return homomorphism; }
  
  public void setHomomorphism(Map<IntArray,Integer> homomorphism) {
    this.homomorphism = homomorphism;
  }
  
  public void setHomomorphism(int[] algGens) {
    if (algGens.length != generators.size()) {
      throw new IllegalArgumentException("wrong number of generators");
    }
    Map<IntArray,Integer> homo = new HashMap<IntArray,Integer>(generators.size());
    int k = 0;
    for (IntArray g : generators) {
      homo.put(g, algGens[k++]);
    }
    this.homomorphism = homo;
  }
  
  public IntArray getElementToFind() { return eltToFind; }
  
  public void setElementToFind(IntArray e) { eltToFind = e; }
  
  public List<IntArray> getElementsToFind() { return eltsToFind; }
  
  public void setElementsToFind(List<IntArray> e) { eltsToFind = e; }
  
  protected static Monitor monitor;
  
  
  public boolean monitoring() {
    return monitor != null;
  }
  
  public static final void setMonitor(Monitor m) { monitor = m; }
  public static final Monitor getMonitor() { return monitor; }
  
  public List<IntArray> close() {
    // TODO fix this
    if (!algebra.isPower()) {
      throw new IllegalArgumentException("only implemented for powers");
    }
    ans = new ArrayList<IntArray>(generators);
    return ans;
  }
  
  
  /**
   * Closure of <tt>elems</tt> under the operations. (Worry about
   * nullary ops later.)
   *
   * @param elems a List of IntArray's
   *
   * @param termMap a Map from the element to the corresponding term
   *                used to generated it. The generators should be 
   *                already in the Map. In other words the termMap
   *                should have the same number of entries as elems.
   *
   * @param elt an element to search for; if found return the closure
                found so far.
   *
   * @return a List of IntArray's.
   */
  public List<IntArray> sgClose(List<IntArray> elems, int closedMark, 
                   final Map<IntArray,Term> termMap, final  Object elt) {
    if (algebra.isPower()) {
      SmallAlgebra alg = algebra.rootFactors().get(0);
      alg.makeOperationTables();
      List<Operation> ops = alg.operations();
      if (ops.size() > 0 && ops.get(0).getTable() != null) {
        //return sgClosePower(alg.cardinality(), ops, elems,
        //                                       closedMark, termMap, elt);
        // TODO restore above
        return null;
      }
    }
    if (monitoring()) monitor.printStart("subpower closing ...");
    final List<IntArray> lst = new ArrayList<IntArray>(elems);// IntArrays
    final List<int[]> rawList = new ArrayList<int[]>(); // the corr raw int[]
    for (Iterator<IntArray> it = elems.iterator(); it.hasNext(); ) {
      rawList.add(it.next().getArray());
    }
    final HashSet<IntArray> su = new HashSet<IntArray>(lst);
    int currentMark = lst.size();
    int pass = 0;
    while (closedMark < currentMark) {
      //System.out.println("monitor is " + monitor);
      if (monitoring()) {
        System.out.println("subpow pass = " + pass + " size = " + lst.size());
        monitor.setPassFieldText("" + pass++);
        monitor.setSizeFieldText("" + lst.size());
        if (monitor.isCancelled()) {
          System.out.println("got here xxxx");
          throw new CancelledException("cancelled from sgClose");
        }
      }
//if (lst.size() > 100000) return lst;
      // close the elements in current
      for (Iterator<Operation> it = algebra.operations().iterator(); it.hasNext(); ) {
        Operation f = it.next();
        final int arity = f.arity();
        if (arity == 0) continue;  // worry about constansts later
        int[] argIndeces = new int[arity];
        for (int i = 0; i < arity - 1; i++) {
          argIndeces[i] = 0;
        }
        argIndeces[arity - 1] = closedMark;
        ArrayIncrementor inc =
                    SequenceGenerator.sequenceIncrementor(
                                  argIndeces, currentMark - 1, closedMark);

        final int[][] arg = new int[arity][];
        while (true) {
          if (monitoring()) {
            if (monitor.isCancelled()) {
              monitor.setSizeFieldText("" + lst.size());
              throw new CancelledException("from sgClose");
            }
          }
          for (int i = 0; i < arity; i++) {
            arg[i] = rawList.get(argIndeces[i]);
          }
          int[] vRaw = f.valueAt(arg);
          IntArray v = new IntArray(vRaw);
          if (su.add(v)) {
            lst.add(v);
            rawList.add(vRaw);
            if (termMap != null) {
              List<Term> children = new ArrayList<Term>(arity);
              for (int i = 0; i < arity; i++) {
                //children.set(i, termMap.get(arg.get(i)));
                children.add(termMap.get(lst.get(argIndeces[i])));
              }
              termMap.put(v, new NonVariableTerm(f.symbol(), children));
              //logger.fine("" + v + " from " + f.symbol() + " on " + arg);
            }
            if (v.equals(elt)) {
              if (monitoring()) monitor.printEnd("closing done, found " + elt);
              return lst;
            }
          }
          if (!inc.increment()) break;
        }
if (false) {
/*
  List middleZero = new ArrayList();
    for (Iterator it2 = lst.iterator(); it2.hasNext(); ) {
      IntArray ia = (IntArray)it2.next();
      if (ia.get(1) == 0) middleZero.add(ia);
    }
  System.out.println("jonsson level so far: "
     + Algebras.jonssonLevelAux(middleZero, 
                             (IntArray)lst.get(0),  (IntArray)lst.get(2)));
*/
}
      }
      closedMark = currentMark;
      currentMark = lst.size();
      if (algebra.cardinality() > 0 && currentMark >= algebra.cardinality()) break;
System.out.println("so far: " + currentMark);
//if (currentMark > 7) return lst;
    }
    if (monitoring()) monitor.printEnd("closing done, size = " + lst.size());
    return lst;
  }

  public List<IntArray> sgClosePower() {
    System.out.println("gens = " + generators);
    System.out.println("termMap = " + termMap);
    return sgClosePower(generators, 0, termMap);
  }
  
  /**
   * A fast version for powers to compute the 
   * closure of <tt>elems</tt> under the operations. (Worry about
   * nullary ops later.)
   *
   * @param elems a List of IntArray's
   *
   * @param termMap a Map from the element to the corresponding term
   *                used to generated it. The generators should be 
   *                already in the Map. In other words the termMap
   *                should have the same number of entries as elems.
   *
   * @param elt an element to search for; if found return the closure
                found so far.
   *
   * @return a List of IntArray's.
   */
  private final List<IntArray> sgClosePower(
      List<IntArray> elems, int closedMark, 
      final Map<IntArray,Term> termMap) {
System.out.println("using power");
System.out.println("card = " + algebra.cardinality());
    if (monitoring()) monitor.printStart("subpower closing ...");
    
    final int algSize = algebra.factors().get(0).cardinality();
    final List<Operation> ops = algebra.factors().get(0).operations();
    
    final int k = ops.size();
    final int[][] opTables = new int[k][];
    final int[] arities = new int[k];
    final OperationSymbol[] symbols = new OperationSymbol[k];
    final Operation[] imgOps = 
      (homomorphism != null && imageAlgebra != null  && termMap != null) 
          ? new Operation[k] : null;
    for (int i = 0; i < k; i++) {
      Operation op = ops.get(i);
      opTables[i] = op.getTable();
      arities[i] = op.arity();
      symbols[i] = op.symbol();
      if (imgOps != null) imgOps[i] = imageAlgebra.getOperation(op.symbol());
    }
 //   List<Operation> imgOps = null;
//    Operation[] imgOps;
 //   if (homomorphism != null && imageAlgebra != null) {
 //     imgOps = new ArrayList<Operation>(imageAlgebra.operations().size());
      
  //  }
    final int power = algebra.getNumberOfFactors();
    ans = new ArrayList<IntArray>(elems);// IntArrays
    final List<int[]> rawList = new ArrayList<int[]>(); // the corr raw int[]
    for (Iterator<IntArray> it = elems.iterator(); it.hasNext(); ) {
      rawList.add(it.next().getArray());
    }
    final HashSet<IntArray> su = new HashSet<IntArray>(ans);
    int currentMark = ans.size();
    int pass = 0;
    while (closedMark < currentMark) {
      if (monitoring()) {
        monitor.setPassFieldText("" + pass++);
        monitor.setSizeFieldText("" + ans.size());
      }
      // close the elements in current
      for (int i = 0; i < k; i++) {
        final int arity = arities[i];
        if (arity == 0) continue;  // worry about constansts later
        final int[] opTable = opTables[i];
        final int[] argIndeces = new int[arity];
        for (int r = 0; r < arity - 1; r++) {
          argIndeces[r] = 0;
        }
        argIndeces[arity - 1] = closedMark;
        ArrayIncrementor inc =
                    SequenceGenerator.sequenceIncrementor(
                                  argIndeces, currentMark - 1, closedMark);
        while (true) {
          //for (int i = 0; i < arity; i++) {
          //  arg[i] = rawList.get(argIndeces[i]);
          //}
          final int[] vRaw = new int[power];


          for (int j = 0; j < power; j++) {
            int factor = algSize;
            int index = rawList.get(argIndeces[0])[j];
            for (int r = 1; r < arity; r++) {
              index += factor * rawList.get(argIndeces[r])[j];
              factor = factor * algSize;
            }
            vRaw[j] = opTable[index];
          }
          IntArray v = new IntArray(vRaw);
          if (su.add(v)) {
            ans.add(v);
            rawList.add(vRaw);
            if (termMap != null) {
              List<Term> children = new ArrayList<Term>(arity);
              for (int r = 0; r < arity; r++) {
                //children.set(i, termMap.get(arg.get(i)));
                children.add(termMap.get(ans.get(argIndeces[r])));
              }
              termMap.put(v, new NonVariableTerm(symbols[i], children));
              //logger.fine("" + v + " from " + f.symbol() + " on " + arg);
            }
            final int size = ans.size();
            if (algebra.cardinality() > 0 && size == algebra.cardinality()) {
              if (monitoring()) {
                monitor.printEnd("done closing, size = " + ans.size());
                monitor.setSizeFieldText("" + ans.size());
              } 
              return ans;
            }
            if (imgOps != null) {
              // here
              final int[] args = new int[arity];
              for (int t = 0; t < arity; t++) {
                args[t] = homomorphism.get(ans.get(argIndeces[t]));
              }
              homomorphism.put(v, imgOps[i].intValueAt(args));
            }
            if (monitoring()) {
              monitor.setSizeFieldText("" + ans.size());
              if (monitor.isCancelled()) {
                throw new CancelledException("cancelled from sgClose");
              }
            }
            if (v.equals(eltToFind)) return ans;
          }
          else {
            if (imgOps != null) {
              // here
              final int[] args = new int[arity];
              for (int t = 0; t < arity; t++) {
                args[t] = homomorphism.get(ans.get(argIndeces[t]));
              }
              if (homomorphism.get(v).intValue() != imgOps[i].intValueAt(args)) {
                List<Term> children = new ArrayList<Term>(arity);
                for (int r = 0; r < arity; r++) {
                  //children.set(i, termMap.get(arg.get(i)));
                  children.add(termMap.get(ans.get(argIndeces[r])));
                }
                failingEquation = new Equation(termMap.get(v),
                    new NonVariableTerm(symbols[i], children));
                System.out.println("failing equation:\n" + failingEquation);
                System.out.println("size so far: " + ans.size());
                return ans;
              }
            }
          }
          if (!inc.increment()) break;
        }

if (false) {
/*
  List middleZero = new ArrayList();
    for (Iterator it2 = ans.iterator(); it2.hasNext(); ) {
      IntArray ia = (IntArray)it2.next();
      if (ia.get(1) == 0) middleZero.add(ia);
    }
  System.out.println("jonsson level so far: "
     + Algebras.jonssonLevelAux(middleZero, 
                             (IntArray)ans.get(0),  (IntArray)ans.get(2)));
*/
}
      }
      closedMark = currentMark;
      currentMark = ans.size();
      if (algebra.cardinality() > 0 && currentMark >= algebra.cardinality()) break;
System.out.println("so far: " + currentMark);
//if (currentMark > 7) return ans;
    }
    if (monitoring()) {
      getMonitor().printEnd("done closing, size = " + ans.size());
      getMonitor().setSizeFieldText("" + ans.size());
    }
    completed = true;
    return ans;
  }


}
