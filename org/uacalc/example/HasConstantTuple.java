package org.uacalc.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uacalc.alg.BigProductAlgebra;
import org.uacalc.alg.Closer;
import org.uacalc.alg.SmallAlgebra;
import org.uacalc.terms.Term;
import org.uacalc.terms.Variable;
import org.uacalc.terms.VariableImp;
import org.uacalc.util.IntArray;

/**
 *
 * @author Matt
 */
public class HasConstantTuple {

  public static boolean hasConstantTuple(final SmallAlgebra alg, final int arity, final List<IntArray> gens) {
    if (alg.cardinality() < 2) {
      return true;
    }
    int NumGens = gens.size();
    final BigProductAlgebra bigProd = new BigProductAlgebra(alg, arity);
    int[] block = new int[arity];
    for (int i = 0; i < arity; i++) {
      block[i] = i;
    }
    int[][] blocks = new int[1][];
    final Map<IntArray, Term> termMap = new HashMap<IntArray, Term>();
    blocks[0] = block;
    for(int i = 0; i < NumGens; i++) {
      Variable var = new VariableImp("x" + i);                      
      termMap.put(gens.get(i), var);
    }
    Closer closer = new Closer(bigProd, gens, termMap);
    closer.setBlocks(blocks);
    List<IntArray> lst = closer.sgClose();
    int[] last = lst.get(lst.size() - 1).getArray();
    final int c = last[0];
    for (int i = 1; i < arity; i++) {
      if (last[i] != c) {
        System.out.println("This algebra does not have a constant tuple");
        return false;
      }
    }
    System.out.println("This algebra does have a constant tuple: " +Arrays.toString(last));
    final IntArray lastIntArray = new IntArray(last);

    System.out.println("term is: " + termMap.get(lastIntArray));
    return true;
  }





}
