package org.uacalc.example;

import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.io.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.ui.tm.*;

public class Michalewski {

  /**
   * This looks for a, b in A^k, the universe of A^[k], such that
   * the two element set {a,b} has polynomials for join and meet
   * in A[k]. The algorithm is described in Emil's email of 28 Jul 2014.
   */
  public static List<IntArray> findJoinMeetMatPower(SmallAlgebra alg, int k) {
    final int n = alg.cardinality();
    List<IntArray> consts = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      consts.add(new IntArray(new int[] {i,i,i,i}));
    }
    int[] a = new int[k];
    ArrayIncrementor ainc = SequenceGenerator.sequenceIncrementor(a, n-1);
    while (true) {
      int[] b = new int[k];
      ArrayIncrementor binc = SequenceGenerator.sequenceIncrementor(b, n-1);
      while (true) {
        if (compare(a, b) < 0) {
          if (testJoinMeetMatPower(a, b, alg, consts)) {
            System.out.println("Join meet term for the element corresponding to a = " + Arrays.toString(a) + ", b = " + Arrays.toString(b));
            List<IntArray> ans = new ArrayList<IntArray>(2);
            ans.add(new IntArray(a));
            ans.add(new IntArray(b));
            return ans;
          }
          else {
            System.out.println("No join meet term for the element corresponding to a = " + Arrays.toString(a) + ", b = " + Arrays.toString(b));
          }
        }
        if (!binc.increment()) break;
      }
      if (!ainc.increment()) break;
    }
    return null;
  }
  
  /**
   * lexicographic ordering.
   */
  private static int compare(int[] a, int[] b) {
    final int k = a.length;
    if (Arrays.equals(a, b)) return 0;
    for (int i = 0; i < k; i++) {
      if (a[i] < b[i]) return -1;
      if (a[i] > b[i]) return 1;
    }
    return 0;
  }
  
  public static boolean testJoinMeetMatPower(int[] a, int[] b, SmallAlgebra alg, List<IntArray> consts) {
    final int k = a.length;
    BigProductAlgebra big = new BigProductAlgebra(alg, 4);
    List<IntArray> gens = new ArrayList<>();
    List<IntArray> eltsToFind = new ArrayList<>(2 * k);
    for (int i = 0; i < k; i++) {
      gens.add(new IntArray(new int[] {a[i],a[i],b[i],b[i]}));
      gens.add(new IntArray(new int[] {a[i],b[i],a[i],b[i]}));
      eltsToFind.add(new IntArray(new int[] {a[i],b[i],b[i],b[i]}));
      eltsToFind.add(new IntArray(new int[] {a[i],a[i],a[i],b[i]}));
    }
    gens.addAll(consts);
    Closer closer = new Closer(big, gens, true);
    closer.setElementsToFind(eltsToFind, gens);
    closer.setProgressReport(new ProgressReport());
    closer.sgClose();
    if (closer.allElementsFound()) return true;
    return false;
  }
  
  public static Closer findJoinMeet(int a, int b, SmallAlgebra alg, List<IntArray> consts) {
    BigProductAlgebra big = new BigProductAlgebra(alg, 4);
    List<IntArray> gens = new ArrayList<>();
    gens.add(new IntArray(new int[] {a,a,b,b}));
    gens.add(new IntArray(new int[] {a,b,a,b}));
    gens.addAll(consts);
    Closer closer = new Closer(big, gens, true);
    closer.setProgressReport(new ProgressReport());
    List<IntArray> eltsToFind = new ArrayList<>();
    IntArray join = new IntArray(new int[] {a,b,b,b});
    IntArray meet = new IntArray(new int[] {a,a,a,b});
    eltsToFind.add(join);
    eltsToFind.add(meet);
    closer.setElementsToFind(eltsToFind, gens);
    closer.sgClose();
    if (closer.allElementsFound()) {
      Term joinTerm = closer.getTermMap().get(join);
      Term meetTerm = closer.getTermMap().get(meet);
      System.out.println("join operation for {" + a + "," + b + "}: " + joinTerm);
      System.out.println("meet operation for {" + a + "," + b + "}: " + meetTerm);
      return closer;
    }
    return null;
  }
  
  
	
  public static void main(String[] args) throws Exception {    
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/rps4.ua");
    //SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/polin.ua");
    findJoinMeetMatPower(alg, 3);
  }

}

