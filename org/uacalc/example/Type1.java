package org.uacalc.example;

import org.uacalc.alg.*;
import org.uacalc.alg.op.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.io.*;
import java.util.*;


public class Type1 {
  
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
    //Operation h = t0.interpretation(matpow, varList, false);  // the false is not working
    Operation h = t0.interpretation(matpow);
    
    System.out.println("h arity: " + h.arity());
    //Operation k = t1.interpretation(matpow, varList, false);
    Operation k = t1.interpretation(matpow);
    List<Operation> ops = new ArrayList<>(2);
    ops.add(h);
    ops.add(k);
    SmallAlgebra ans = new BasicAlgebra("reduct", matpow.cardinality(), ops);
    return ans;
  }
  
  

  public static void main(String[] args) throws Exception {
    SmallAlgebra two = new BasicAlgebra("two", 2, new ArrayList<Operation>());
    SmallAlgebra ans = reductAlg(two);
    AlgebraIO.writeAlgebraFile(ans, "/tmp/two-reduct.ua");

  }

}
