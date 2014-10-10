package org.uacalc.example;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.io.*;
import java.util.*;


public class Type1 {
  /**
   * Make a unary algebra of size <code>algSize</code> with all permutations 
   * and all constants.
   * 
   * @param algSize
   * @return
   */
  public static SmallAlgebra fullPermutationAlgebra (int algSize) {
    List<Operation> ops = new ArrayList<>(2);
    ops.add(Operations.makeFullCycle(algSize));
    ops.add(Operations.makeTransposition(algSize, 0, 1));
    ops.addAll(Operations.makeConstantIntOperations(algSize));
    SmallAlgebra ans = new BasicAlgebra("minType1-" + algSize, algSize, ops);
    return ans;
  }
  
  /**
   * Make a unary algebra of size <code>algSize</code> with the
   * shift permutation and all constants.
   * 
   * @param algSize
   * @return
   */
  public static SmallAlgebra cyclicPermutationAlgebra (int algSize) {
    List<Operation> ops = new ArrayList<>(2);
    ops.add(Operations.makeFullCycle(algSize));
    ops.addAll(Operations.makeConstantIntOperations(algSize));
    SmallAlgebra ans = new BasicAlgebra("minType1-" + algSize, algSize, ops);
    return ans;
  }
  
  
  public static SmallAlgebra reductAlg (SmallAlgebra alg) {
    SmallAlgebra matpow = Algebras.matrixPower(alg, 3);
    BigProductAlgebra bigProd = new BigProductAlgebra(matpow, 4);
    List<IntArray> gens = new ArrayList<>();
    IntArray g0 = new IntArray(new int[] {0,0,1,1});
    IntArray g1 = new IntArray(new int[] {0,1,0,1});
    final Map<IntArray, Term> termMap = new HashMap<IntArray, Term>();
    List<Variable> varList = new ArrayList<>();   
    termMap.put(g0, Variable.x);
    termMap.put(g1, Variable.y);
    varList.add(Variable.x);
    varList.add(Variable.y);
    gens.add(g0);
    gens.add(g1);    
    for (int i = 0; i < matpow.cardinality(); i++) {
      IntArray c = new IntArray(new int[] {i,i,i,i});
      gens.add(c);
      Variable var = new VariableImp("c" + i);
      termMap.put(c, var);
      varList.add(var);
    }
    Closer closer = new Closer(bigProd, gens, termMap);
    List<IntArray> lst = closer.sgClose();
    System.out.println("lst: " + lst);
    IntArray ia0 = new IntArray(new int[]{0,1,2,3});
    IntArray ia1 = new IntArray(new int[]{0,4,3,7});
    Term t0 = termMap.get(ia0);
    Term t1 = termMap.get(ia1);
    System.out.println("t0 = " + t0);
    System.out.println("t1 = " + t1);
    Operation h = t0.interpretation(matpow, varList, false);  // the false is not working
    //Operation h = t0.interpretation(matpow);
    
    System.out.println("h arity: " + h.arity());
    Operation k = t1.interpretation(matpow, varList, false);
    //Operation k = t1.interpretation(matpow);
    List<Operation> ops = new ArrayList<>(2);
    ops.add(h);
    ops.add(k);
    SmallAlgebra ans = new BasicAlgebra("reduct", matpow.cardinality(), ops);
    return ans;
  }
  
  public static List<IntArray> subtraces (SmallAlgebra alg, Partition beta) {
    TypeFinder tfinder = new TypeFinder(alg);
    Subtrace subtr = tfinder.findSubtrace(beta);
    return subtr.getSubtraceUniverse();
  }
  

  public static void main(String[] args) throws Exception {
    //SmallAlgebra two = new BasicAlgebra("two", 2, new ArrayList<Operation>());
    //SmallAlgebra ans = reductAlg(two);
    //AlgebraIO.writeAlgebraFile(ans, "/tmp/two-reduct.ua");
    int size = 5;
    SmallAlgebra alg = cyclicPermutationAlgebra(size);
    SmallAlgebra matrixPower = Algebras.matrixPower(alg, 2);
    Partition one = matrixPower.con().one();
    List<IntArray> subtraces = subtraces(matrixPower, one);
    for (IntArray subtr : subtraces) {
      if (subtr.get(0) == 0) System.out.println(subtr);
    }
    System.out.println(subtraces);
    int a = subtraces.get(0).get(0);
    int b = subtraces.get(0).get(1);
    System.out.println("a = " + a + ", b = " + b);
    BigProductAlgebra big = new BigProductAlgebra(matrixPower, 4);
    IntArray g0 = new IntArray(new int[] {a,a,b,b});
    IntArray g1 = new IntArray(new int[] {a,b,a,b});
    List<IntArray> gens = new ArrayList<>(2);
    gens.add(g0);
    gens.add(g1);
    Map<IntArray,Term> termMap = new HashMap<>();
    termMap.put(g0, Variable.x);
    termMap.put(g1, Variable.y);
    List<IntArray> Tab = big.sgClose(gens, termMap);
    List<IntArray> aFirst = new ArrayList<>();
    for (IntArray ia : Tab) { 
      if (ia.get(0) == a) aFirst.add(ia);
    }
    System.out.println(aFirst);
  }

}
