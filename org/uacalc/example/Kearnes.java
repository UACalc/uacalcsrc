package org.uacalc.example;

import java.util.*;
import java.io.*;


import org.uacalc.util.*;
import org.uacalc.alg.*;
import org.uacalc.io.*;
import org.uacalc.ui.*;
import org.uacalc.lat.*;
import org.uacalc.terms.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.sublat.*;

/**
 * Kearnes example of <code>k</code> not <code>k-1</code> 
 * permutable variety with
 * no failure of <code>k-1</code> permutability with
 * two congruences covering their meet. 
 * The algebra is a chain with a unary twisted retraction (explain this).
 * <p>
 * These algebras, which have size k, are free on 2 generators. Their
 * congruence lattice is 2^(k-1) and have images of all smaller sizes. The
 * smallest one which is k but not k-1 permutable has size ceiling(k/2).
 * So it is conceivable that there is an n^s ||A||^2 algorithm for testing 
 * k but not k-1 permutability for an idempotent algebra. 
 * <p>
 * On the other hand there is a collection of (roughly k/2) 2 element
 * algebra that generate the variety and you can't find a failure without
 * using all of them. So nothing like "you can find a failure in a 
 * subdirect product of 2 (or 3).
 * <p>
 * This file also shows that CD'(k) does not imply CD(k), where CD'(k)
 * is the ALVIN variant (even, odd switched) of Jonsson conditions.
 * When n = 6, the Jonsson terms are
 *
 * x
 * h_1(x,h_1(x,z,y),y)
 * h_2(x,y,z)
 * h_3(x,h_3(x,y,z),z)
 * h_2(z,y,x)
 * h_1(z,h_1(z,y,x),x)
 * z
 * 
 * and the ALVIN variant is 
 * 
 * x
 * h_1(x,y,z)
 * h_2(x,h_2(x,y,z),z)
 * h_3(x,y,z)
 * h_2(z,h_2(z,y,x),x)
 * h_1(z,y,x)
 * z
 * 
 * If we replace the second basic operation, h_2, with h_2(x,h_2(x,y,z),z),
 * then in this reduct Jonsson terms are 
 *
 * x
 * h_1(x,h_1(x,z,y),y)
 * h_1(x,x,z)
 * f_2(x,y,z)
 * h_3(x,y,z)
 * f_2(z,y,x)
 * h_1(z,y,x)
 * z
 *
 * The alvin variant is:
 *
 * x
 * h_1(x,y,z)
 * f_2(x,y,z)
 * h_3(x,y,z)
 * f_2(z,y,x)
 * h_1(z,y,x)
 * z
 *
 * So CD'(6) does not imply CD(6). 
 *
 * This also works for 4:
 * x
 * h_1(x,h_1(x,z,y),y)
 * h_1(x,x,z)
 * f_2(x,y,z)
 * h_1(z,y,x)
 * z
 *
 * x
 * h_1(x,y,z)
 * f_2(x,y,z)
 * h_1(z,y,x)
 * z
 *
 */
public class Kearnes {

  public static SmallAlgebra makeKearnesAlg(int n) {
    List ops = makeKearnesOps(n);
    // the second argument is the size of the algebra.
    return new BasicAlgebra("Kearnes" + n, n, ops);
  }

  /**
   * The operations are join and meet and n - 1 unary ops: p_i(y) =
   * i if y &lt; i and i - 1 otherwise.
   */
  public static List makeKearnesOps(final int n) {
    List ops = new ArrayList(n + 1);
    ops.add(makeJoinOp(n));
    ops.add(makeMeetOp(n));
    Operation op = null;
    for (int i = 1; i < n; i++) {
      final int k = i;
      op = new AbstractOperation("p_" + i, 1, n) {

        public Object valueAt(List args) {
          throw new UnsupportedOperationException();
        }

        public int intValueAt(int[] args) {
          if (args[0] < k) return k;
          return k - 1;
        }
      };
      ops.add(op);
    }
    return ops;
  }

  public static Operation makeJoinOp(final int n) {
    Operation op = new AbstractOperation(OperationSymbol.JOIN, n) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int intValueAt(int[] args) {
        return Math.max(args[0], args[1]);
      }
    };
    return op;
  }

  public static Operation makeMeetOp(final int n) {
    Operation op = new AbstractOperation(OperationSymbol.MEET, n) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int intValueAt(int[] args) {
        return Math.min(args[0], args[1]);
      }
    };
    return op;
  }

  /**
   * This is the reduct of the Kearnes algebra to its 
   * Hagemann-Mitshke terms (and the join and meet if joinMeet is true).
   * The operations are join and meet and n/2 unary ops:
   * h_i(x,y,z) = xz + xp_n-i(y) + zp_i(y).
   * Note h_n-i(x,y,z) = h_i(z,y,x) which is why we only need 1/2 of them.
   */
  public static SmallAlgebra makeKearnesIdempotentAlg(int n, 
                                                      boolean joinMeet) {
    List ops = makeKearnesIdempotentOps(n, joinMeet);
    // the second argument is the size of the algebra.
    return new BasicAlgebra("KearnesIdempotent" + n, n, ops);
  }

  /**
   * 
   */
  public static List makeKearnesIdempotentOps(final int n, 
                                              final boolean joinMeet) {
    final List ops = new ArrayList(n/2 + 2);
    final List kearnesOps = makeKearnesOps(n);
    if (joinMeet) {
      final Operation join = (Operation)kearnesOps.get(0);
      final Operation meet = (Operation)kearnesOps.get(1);
      ops.add(join);
      ops.add(meet);
    }
    Operation op = null;
    for (int i = 1; i <= n/2; i++) {
      final int k = i;
      op = new AbstractOperation("h_" + i, 3, n) {

        public Object valueAt(List args) {
          throw new UnsupportedOperationException();
        }

        public int intValueAt(int[] args) {
          //int xz = meet.intValueAt(new int[] {args[0], args[2]});
          int xz = Math.min(args[0], args[2]);
          int xp = Math.min(args[0], 
                   ((Operation)kearnesOps.get(n - k + 1)).intValueAt(
                   new int[] {args[1]}));
          int zp = Math.min(args[2], 
                   ((Operation)kearnesOps.get(k + 1)).intValueAt(
                   new int[] {args[1]}));
          return Math.max(xz, Math.max(xp, zp));
        }
      };
      ops.add(op);
    }
    return ops;
  }

  public static SmallAlgebra makeJonssonReduct(int n, boolean alvin) {
    List ops = makeJonssonAlvinReductOps(n, alvin);
    // the second argument is the size of the algebra.
    if (alvin) return new BasicAlgebra("JonssonAlvinReduct" + n, n, ops);
    return new BasicAlgebra("JonssonReduct" + n, n, ops);
  }

  /**
   * This is the reduct to the Jonsson terms unless 
   * useAlvin is true and then it is the ALVIN variant of the
   * Jonsson terms.
   */
  public static List makeJonssonAlvinReductOps(final int n, 
                                              final boolean useAlvin) {
    final List ops = new ArrayList(n/2 + 2);
    final List kearnesOps = makeKearnesOps(n);
    Operation op = null;
    for (int i = 1; i <= n/2; i++) {
      final int k = i;
      op = new AbstractOperation("h_" + i, 3, n) {

        public Object valueAt(List args) {
          throw new UnsupportedOperationException();
        }

        public int intValueAt(int[] args) {
          //int xz = meet.intValueAt(new int[] {args[0], args[2]});
          int xz = Math.min(args[0], args[2]);
          int xp = Math.min(args[0], 
                   ((Operation)kearnesOps.get(n - k + 1)).intValueAt(
                   new int[] {args[1]}));
          int zp = Math.min(args[2], 
                   ((Operation)kearnesOps.get(k + 1)).intValueAt(
                   new int[] {args[1]}));
          return Math.max(xz, Math.max(xp, zp));
        }
      };
      if (useAlvin) {
        if (i % 2 != 0) ops.add(op);
        else ops.add(makeModifiedOp(op, i, n));
      }
      else {
        if (i % 2 == 0) ops.add(op);
        else ops.add(makeModifiedOp(op, i, n));
      }
    }
    return ops;
  }

  /**
   * This takes a ternary operation f and returns the operation
   * f(x, f(x,y,z), z).
   */
  public static Operation makeModifiedOp(final Operation f, 
                                         final int i, final int n) {
    Operation ff = new AbstractOperation("f_" + i, 3, n) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int intValueAt(int[] args) {
        int v = f.intValueAt(args);
        int[] args2 = new int[] {args[0], v, args[2]};
        return f.intValueAt(args2);
      }
    };
    return ff;
  }

  

  public static SmallAlgebra makeIdem2ElemGroup() {
    List ops = new ArrayList(1);
    ops.add(new AbstractOperation("p", 3, 2) {

        public Object valueAt(List args) {
          throw new UnsupportedOperationException();
        }

        public int intValueAt(int[] args) {
          return (args[0] + args[1] + args[2]) % 2;
        }
      });
    return new BasicAlgebra("group2", 2, ops);
  }

  /**
   * Quick and dirty.
   * 
   * @param alg
   * @return
   */
  public static int permLevel(SmallAlgebra alg) {
    int ans = -1;
    for (Iterator it = alg.con().iterator(); it.hasNext(); ) {
      final Partition par = (Partition)it.next();
      for (Iterator it2 = alg.con().iterator(); it2.hasNext(); ) {
        final Partition par2 = (Partition)it2.next();
        ans = Math.max(ans, BasicPartition.permutabilityLevel(par, par2));
      }
    }
    return ans;
  }
  
  public static int maxPermLevelSubsSq(SmallAlgebra alg) {
    int ans = -1;
    SmallAlgebra sq = new PowerAlgebra(alg, 2);
    System.out.println("number of subs of sq is " + sq.sub().cardinality());
    for (Iterator it = sq.sub().iterator(); it.hasNext(); ) {
      IntArray univ = (IntArray)it.next();
      if (univ.universeSize() > 1) {
        //System.out.println("sub size is " + univ.size());
        SmallAlgebra sub = new Subalgebra(sq, univ);
        ans = Math.max(ans, permLevel(sq));
      }
    }
    return ans;
  }

  public static List<IntArray> getTheSubalgebra(SmallAlgebra alg, int n, 
                                                             boolean onlyT) {
    BigProductAlgebra alg0cubed = new BigProductAlgebra(alg, 3);
    IntArray g0 = new IntArray(new int[] {0,0,n-1});
    IntArray g1 = new IntArray(new int[] {0,n-1,0});
    IntArray g2 = new IntArray(new int[] {n-1,0,0});
    List gens = new ArrayList(3);
    gens.add(g0);
    gens.add(g1);
    gens.add(g2);
    List<IntArray> sub = alg0cubed.sgClose(gens);
    if (!onlyT) return sub;
    List<IntArray> sub1 = new ArrayList();
    for (Iterator<IntArray> it = sub.iterator(); it.hasNext(); ) {
      IntArray ia = it.next();
      if (ia.get(0) <= ia.get(1) && ia.get(1) <= ia.get(2)) sub1.add(ia);
      //if (ia.get(1) == 0) sub1.add(ia);
    }
    return sub1;
  }
  
  //////////////////////////////////
  // Ross's "alternative presentation:
  ///////////////////////////////////
  
  
  /**
   * This is Ross's g^{k,j}_i, but since it does not depend on k
   * I am suppressing it.
   */
  public static Operation g(final int i, final int j) {
    return new AbstractOperation("g" + i + "" + j, 3, 2) {

      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }

      public int intValueAt(int[] args) {
        if (i < j) return args[0];
        if (i == j) return Math.max(args[0], Math.min(1 - args[1], args[2]));
        return Math.max(args[0], args[2]);
      }
    };
  }
  
  public static SmallAlgebra willardsD(int k, int j) {
    List<Operation> ops = new ArrayList<Operation>(k);
    for (int i = 0; i < k; i++) {
      ops.add(g(i,j));
    }
    return new BasicAlgebra("D" + k + j, 2, ops);
  }
  
  public static SmallAlgebra willardsE(int k) {
    List<SmallAlgebra> algList = new ArrayList<SmallAlgebra>(k);
    for (int j = 0; j < k; j++) {
      algList.add(willardsD(k, j));
    }
    return new ProductAlgebra("E" + k, algList);
  }
  
  // average algebra having a large arity nu.
  
  public static Operation ave(final int n, final int r) {
    return new AbstractOperation("ave" + r, r, n) {

      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }

      public int intValueAt(int[] args) {
        int sum = 0;
        for (int i = 0; i < r; i++) {
          sum += args[i];
        }
        return Math.round(((float)sum) / r);
      }
    };
  }
  
  public static SmallAlgebra averageAlg(int n) {
    List<Operation> ops = new ArrayList<Operation>(1);
    ops.add(ave(n, 2*n - 1));
    return new BasicAlgebra("AveAlgebra" + n, n, ops);
  }
  


  public static void main(String[] args) throws IOException {
    
    AlgebraIO.writeAlgebraFile(willardsE(2), "/tmp/willardsE2.ua");
    AlgebraIO.writeAlgebraFile(willardsE(3), "/tmp/willardsE3.ua");
    
    AlgebraIO.writeAlgebraFile(averageAlg(3), "/tmp/averageAlg3.ua");
    AlgebraIO.writeAlgebraFile(averageAlg(4), "/tmp/averageAlg4.ua");
    AlgebraIO.writeAlgebraFile(averageAlg(5), "/tmp/averageAlg5.ua");

    
    int n = 4;
    SmallAlgebra alg0 = null;
    if (args.length > 0) {
      try {
        n = Integer.parseInt(args[0]);
      }
      catch(NumberFormatException e) {}
    }
    System.out.println("n is " + n);

    if (alg0 == null) alg0 = makeKearnesIdempotentAlg(n, false);
    
    //Matt and Ross stuff
    Subalgebra subalg = new Subalgebra(alg0, new int[] {0,1});
    AlgebraIO.writeAlgebraFile(subalg, "/tmp/kearnes01-" + n + ".ua");
    
    AlgebraIO.writeAlgebraFile(alg0, "/tmp/kearnes" + n + ".ua");
    //Term m = Malcev.weakMajorityTerm(alg0, true);
    //System.out.println("weak majority term is " + m);
    
    List<Term> jonTerms = Malcev.jonssonTerms(alg0, false);
    System.out.println("The Jonsson terms are:");
    System.out.println("" + jonTerms);

    List<Term> jonALVTerms = Malcev.jonssonTerms(alg0, true);
    System.out.println("The ALVIN Jonsson terms are:");
    System.out.println("" + jonALVTerms);

    //List ans = null;

    //alg0 = makeKearnesIdempotentAlg(n, false);
    //ans = Malcev.jonssonTerms(alg0);
    //System.out.println("Jon on full alg:");
    //System.out.println(ans);


    /*
    List<IntArray> s = getTheSubalgebra(alg0, n, false);
    List<IntArray> t = getTheSubalgebra(alg0, n, true);
    System.out.println("subalg S size = " + s.size());
    List<IntArray> foo = new ArrayList<IntArray>();
    for (Iterator<IntArray> it = s.iterator(); it.hasNext(); ) {
      IntArray ia = it.next();
      if (ia.get(0) <= ia.get(1) && ia.get(1) <= ia.get(2)) foo.add(ia);
    }
    for (Iterator<IntArray> it = foo.iterator(); it.hasNext(); ) {
      IntArray ia = it.next();
      System.out.println(ia);
    }
    System.out.println("");
    System.out.println(s);
    System.out.println("");
    System.out.println(t);
    */

    //SmallAlgebra jonReduct = makeJonssonReduct(n, false);
    //SmallAlgebra alvReduct = makeJonssonReduct(n, true);
    SmallAlgebra jonReduct = new ReductAlgebra(alg0, jonTerms);
    SmallAlgebra alvReduct = new ReductAlgebra(alg0, jonALVTerms);

System.out.println("jonReduct ops " + jonReduct.operations());

    List<Term> terms = Malcev.jonssonTerms(jonReduct, false);
    System.out.println("jonReduct, jonTerms: " + terms);
    terms = Malcev.jonssonTerms(jonReduct, true);
    System.out.println("jonReduct, alvTerms: " + terms);
    terms = Malcev.jonssonTerms(alvReduct, false);
    System.out.println("alvReduct, jonTerms: " + terms);
    terms = Malcev.jonssonTerms(alvReduct, true);
    System.out.println("alvReduct, alvTerms: " + terms);
    


/*
    alg0 = makeKearnesIdempotentAlg(n, true);
    List<IntArray> sr = getTheSubalgebra(alg0, n, false);
    List<IntArray> tr = getTheSubalgebra(alg0, n, true);
    System.out.println("reduct subalg S size = " + sr.size());
    System.out.println(tr);
    for (Iterator<IntArray> it = t.iterator(); it.hasNext(); ) {
      IntArray elt = it.next();
      if (!tr.contains(elt)) System.out.println(elt);
    }
    
//Partition p = alg0.con().Cg(0,1);
//SmallAlgebra q = new QuotientAlgebra(alg0,p);
    //AlgebraIO.writeAlgebraFile(q, "/tmp/qkearnes" + n + ".xml");
    ans = Malcev.jonssonTerms(alg0);
    System.out.println("Jonsson terms are");
    for (Iterator it = ans.iterator(); it.hasNext(); ) {
      System.out.println(it.next());
    }
    ans = Malcev.hagemannMitschkeTerms(alg0);
    System.out.println("Hagemann Mitschke terms are");
    for (Iterator it = ans.iterator(); it.hasNext(); ) {
      System.out.println(it.next());
    }
    System.out.println("k permutible for k = " + (ans.size() - 1));

    System.out.println("alg0 size = " + alg0.cardinality());


    BigProductAlgebra alg0cubed = new BigProductAlgebra(alg0, 3);
    IntArray g0 = new IntArray(new int[] {0,0,n-1});
    IntArray g1 = new IntArray(new int[] {0,n-1,0});
    IntArray g2 = new IntArray(new int[] {n-1,0,0});
    List gens = new ArrayList(3);
    gens.add(g0);
    gens.add(g1);
    gens.add(g2);
    List sub = alg0cubed.sgClose(gens);
    List sub1 = new ArrayList();
    for (Iterator it = sub.iterator(); it.hasNext(); ) {
      IntArray ia = (IntArray)it.next();
      if (ia.get(0) <= ia.get(1) && ia.get(1) <= ia.get(2)) sub1.add(ia);
    }
    
    final Comparator c = new Comparator() {
        public int compare(Object o1, Object o2) {
          IntArray ia1 = (IntArray)o1;
          IntArray ia2 = (IntArray)o2;
          for (int i = 0; i < ia1.size(); i++) {
            if (ia1.get(i) < ia2.get(i)) return -1;
            if (ia1.get(i) > ia2.get(i)) return 1;
          }
          return 0;
        }
        public boolean equals(Object o) { return false; }
    };
    Collections.sort(sub1, c);

    for (Iterator it = sub1.iterator(); it.hasNext(); ) {
      System.out.println(it.next());
    }

    BigProductAlgebra alg0sq = new BigProductAlgebra(alg0, 2);
    g0 = new IntArray(new int[] {0,0});
    g1 = new IntArray(new int[] {0,n-1});
    g2 = new IntArray(new int[] {n-1,n-1});
    gens = new ArrayList(3);
    gens.add(g0);
    gens.add(g1);
    gens.add(g2);
    sub = alg0sq.sgClose(gens);
    Collections.sort(sub, c);
    for (Iterator it = sub.iterator(); it.hasNext(); ) {
      System.out.println(it.next());
    }
    System.out.println();
    System.out.println("\nlocal dist level of alg0 is " 
                        + Malcev.localDistributivityLevel(alg0));
    System.out.println("\nlocal dist level of alg0^3 is " 
          + Malcev.localDistributivityLevel(new PowerAlgebra(alg0, 3)));

    // This shows that look at the permutability level of all the 
    // subalgebras of A^2 is not enough to find the perm level in
    // V(A).
    System.out.println("con has size: " + alg0.con().cardinality());
    for (Iterator it = alg0.con().iterator(); it.hasNext(); ) {
      Partition par = (Partition)it.next();
      System.out.println("par is " + par);
      SmallAlgebra q = new QuotientAlgebra(alg0, par);
      int vqLevel = Malcev.hagemannMitschkeTerms(q).size() - 1;
      int localLevel = permLevel(q);
      int sqLevel = -1;
      if (localLevel < vqLevel) {
        sqLevel = maxPermLevelSubsSq(q);
        System.out.println("subs of sq perm level is " + sqLevel);
      }
      System.out.println("V(q) perm level: " + vqLevel);
      System.out.println("q perm level is " + localLevel + "\n");
      if (sqLevel != -1 && sqLevel < vqLevel) return;
    }
    //LatDrawer.drawLattice(new BasicLattice("", alg0.con(), true));
*/

/*     
    TypeFinder tf = new TypeFinder(alg0);
    Set types = tf.findTypeSet();
    System.out.println("types " + types);

    alg0 = makeIdem2ElemGroup();
    ans = Malcev.hagemannMitschkeTerms(alg0);
    System.out.println("Hagemann Mitschke terms for 2 are\n" + ans);
    ans = Malcev.jonssonTerms(alg0);
    System.out.println("Jonsson terms are" + ans);
    m = Malcev.weakMajorityTerm(alg0, true);
    System.out.println("weak majority term is " + m);
*/
    

    //LatDrawer.drawLattice(new BasicLattice("", alg0.con(), true));
  }

}


