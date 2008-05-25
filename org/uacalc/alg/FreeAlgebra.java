/* FreeAlgebra.java (c) 2005/06/23  Ralph Freese */

package org.uacalc.alg;

import java.util.*;
import java.util.logging.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.eq.*;
import org.uacalc.io.*;
import org.uacalc.ui.tm.ProgressReport;

import org.uacalc.alg.SmallAlgebra.AlgebraType;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.Operations;
import org.uacalc.alg.sublat.*;

/**
 * This class represents a subalgebra of a direct product 
 * of <tt>SmallAlgebra</tt>s. It allows one to construct such an
 * algebra even though the direct product may be too big to be a
 * <tt>SmallAlgebra</tt>.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class FreeAlgebra extends SubProductAlgebra implements SmallAlgebra {

  static Logger logger = Logger.getLogger("org.uacalc.alg.FreeAlgebra");
  static {
    logger.setLevel(Level.FINER);
  }

/*
  protected BigProductAlgebra productAlgebra;
  protected List gens; // a list of IntArray's
  protected List univ; // a list of iIntArray's
  protected HashMap univHashMap; // a map from IntArray's of elements of the 
                                 // univ to Integers (the index).
*/

  /**
   * Consturct a free algebra without giving it a name.
   */
  public FreeAlgebra(SmallAlgebra alg, int numberOfGens) {
    this(alg, numberOfGens, true);
  }
  
  /**
   * Construct a free algebra without giving it a name.
   */
  public FreeAlgebra(SmallAlgebra alg, int numberOfGens, ProgressReport report) {
    this("F(" + numberOfGens + ") over " + alg.name(),
        alg, numberOfGens, true, false, report);
  }
  
  
  public FreeAlgebra(SmallAlgebra alg, int numberOfGens, boolean makeUniverse) {
    this(alg, numberOfGens, makeUniverse, false);
  }
  
  public FreeAlgebra(SmallAlgebra alg, int numberOfGens, 
                              boolean makeUniverse, boolean thinGenerators) {
    this("Free(" + numberOfGens + ", " + alg.name() + ")", 
        alg, numberOfGens, makeUniverse, thinGenerators);
  }

  public FreeAlgebra(String name, SmallAlgebra alg, int numberOfGens) {
    this(name, alg, numberOfGens, true);
  }
  
  
  /**
   * Consturct the free algebra over <tt>alg</tt> 
   * with <tt>numberOfGens</tt> generators.
   */
  public FreeAlgebra(String name, SmallAlgebra alg, int numberOfGens, 
                                                    boolean makeUniverse) {
    this(name, alg, numberOfGens, makeUniverse, false);
    
  }
  
  public FreeAlgebra(String name, SmallAlgebra alg, int numberOfGens, 
      boolean makeUniverse, boolean thinGens) {
    this(name, alg, numberOfGens, makeUniverse, thinGens, null);
  }
  
  public FreeAlgebra(SmallAlgebra alg, int numberOfGens, 
          boolean makeUniverse, boolean thinGens, ProgressReport report) {
    this("F(" + numberOfGens + ") over " + alg.name(), alg, numberOfGens, 
         makeUniverse, thinGens, report);
  }
  
  public FreeAlgebra(String name, SmallAlgebra alg, int numberOfGens, 
      boolean makeUniverse, boolean thinGens, 
      ProgressReport report) {
    this(name, alg, numberOfGens, makeUniverse, thinGens, false, report);
  }
  
  /**
   * Consturct the free algebra over <tt>alg</tt> 
   * with <tt>numberOfGens</tt> generators.
   * 
   * @param name
   * @param alg
   * @param numberOfGens
   * @param makeUniverse  if true, make the universe
   * @param thinGens      if true, try to thin out the number of projections
   * @param decompose     if true, reduce to SI algebras
   * @param report keeps the state of the progress
   */
  public FreeAlgebra(String name, SmallAlgebra alg, int numberOfGens, 
                                  boolean makeUniverse, boolean thinGens, 
                                  boolean decompose, 
                                  ProgressReport report) {
    super(name);
    System.out.println("progressReport in Free is " + report);
    String line = "constructing free algebra on " + numberOfGens 
                   + " generators over " + alg.name();
    if (report != null) {
      report.addStartLine(line);
    }
    else System.out.println(line);
    setupGensAndProductAlg(alg, numberOfGens, true);
    /*
    final int n = alg.cardinality();
    int s = 1;
    for (int  i = 0; i < numberOfGens; i++) {
      s = s * n;
    }
    logger.fine("size of the over product is " + s);
    productAlgebra = new BigProductAlgebra(alg, s);
    int[] projs = new int[numberOfGens];
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(projs, n-1);
    gens = new ArrayList<IntArray>(numberOfGens);
    for (int i = 0; i < numberOfGens; i++) {
      gens.add(new IntArray(s));
    }
    
    for (int k = 0; k < s; k++) {
      for (int i = 0; i < numberOfGens; i++) {
        final IntArray ia = gens.get(i);
        ia.set(k, projs[i]);
      }
      inc.increment();
    }
    */
    if (thinGens && !decompose) {
      long time = System.currentTimeMillis();
      if (report != null) {
        report.addLine("thinning coordinate projections ...");
      }
      List<IntArray> lst = thinGenerators();
      time = System.currentTimeMillis() - time;
      if (report != null) {
        report.addLine("thinned " + gens.get(0).size() + " coordinates down to " 
            + lst.get(0).size() + " (" + time + " ms)");
      }
      System.out.println("time for thinning = " + time);
      System.out.println("thin size = " + lst.size());
      System.out.println("thin = " + lst);
      System.out.println("thin coord length = " + lst.get(0).size());
      System.out.println("gens coord length = " + gens.get(0).size());

      gens = lst;
      productAlgebra = new BigProductAlgebra(alg, gens.get(0).size());
    }

    termMap = new HashMap<IntArray,Term>();
    if (gens.size() == 1) termMap.put(gens.get(0), Variable.x);
    if (gens.size() == 2) {
      termMap.put(gens.get(0), Variable.x);
      termMap.put(gens.get(1), Variable.y);
    }
    if (gens.size() == 3) {
      termMap.put(gens.get(0), Variable.x);
      termMap.put(gens.get(1), Variable.y);
      termMap.put(gens.get(2), Variable.z);
    }
    int k = 0;
    if (gens.size() > 3) {
      for (Iterator<IntArray> it = gens.iterator(); it.hasNext(); k++) {
        Variable var = new VariableImp("x_" + k);
        termMap.put(it.next(), var);
      }
    }
    if (makeUniverse) makeUniverse(report);
    line = "done constructing free algebra, size = " + size;
    if (report != null) {
      report.addEndingLine(line + " rsf ");
    }
  }
  
  private void setupGensAndProductAlg(final SmallAlgebra alg, 
                     final int numberOfGens, boolean decompose) {
    if (decompose) {
      final List<SmallAlgebra> algs = new ArrayList<SmallAlgebra>();
      final List<IntArray> projs = new ArrayList<IntArray>();
      for (AlgebraWithGeneratingVector algV : setupSIProjections(alg, numberOfGens)) {
        algs.add(algV.getAlgebra());
        projs.add(new IntArray(algV.getVector()));
      }
      productAlgebra = new BigProductAlgebra(algs);
      gens = transpose(projs);
      System.out.println("projs size = " + projs.size());
    }
    else {
      final int n = alg.cardinality();
      int s = 1;
      for (int  i = 0; i < numberOfGens; i++) {
        s = s * n;
      }
      logger.fine("size of the over product is " + s);
      productAlgebra = new BigProductAlgebra(alg, s);
      int[] projs = new int[numberOfGens];
      ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(projs, n-1);
      gens = new ArrayList<IntArray>(numberOfGens);
      for (int i = 0; i < numberOfGens; i++) {
        gens.add(new IntArray(s));
      }
      
      for (int k = 0; k < s; k++) {
        for (int i = 0; i < numberOfGens; i++) {
          final IntArray ia = gens.get(i);
          ia.set(k, projs[i]);
        }
        inc.increment();
      }
    }
  }
  
  private List<AlgebraWithGeneratingVector> setupSIProjections(
                          final SmallAlgebra alg, final int numberOfGens) {
    final List<AlgebraWithGeneratingVector> ans = new ArrayList<AlgebraWithGeneratingVector>();
    final int n = alg.cardinality();
    final int[] vec = new int[numberOfGens];
    for (int i = 0 ; i < vec.length; i++) {
      vec[i] = 0;
    }
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(vec, n-1);
    List<AlgebraWithGeneratingVector> decomp = AlgebraWithGeneratingVector.siDecompose(alg, vec);
    for (AlgebraWithGeneratingVector a : decomp ) {
      ans.add(a);
    }
    while (inc.increment()) {
      //System.out.println("vec = " + ArrayString.toString(vec));
      decomp = AlgebraWithGeneratingVector.siDecompose(alg, vec);
      //System.out.println("decomp size = " + decomp.size());
      for (AlgebraWithGeneratingVector a : decomp) {
        boolean dumped = false;
        for (AlgebraWithGeneratingVector b : ans) {
          //System.out.println("a = " + a + ", b = " + b);
          if (a.isImageOf(b)) {  // don' need a so dump it
            dumped = true;
            break;
          }
        }
        if (!dumped) {
          for (ListIterator<AlgebraWithGeneratingVector> it = ans.listIterator(); it.hasNext(); ) {
            AlgebraWithGeneratingVector b = it.next();
            if (b.isImageOf(a)) it.remove();
          }
          ans.add(a);
        }
      }
    }
    for (AlgebraWithGeneratingVector A : ans) {
      System.out.println("A = " + A);
    }
    return ans;
  }
  
  private void makeUniverse(ProgressReport report) {
    univ = productAlgebra.sgClose(gens, termMap, null, report);

    // univ = productAlgebra.sgClose(gens);
    size = univ.size();
    logger.info("free algebra size = " + size);
    univHashMap = new HashMap<IntArray, Integer>(size);
    terms = new Term[univ.size()];
    int k = 0;
    for (Iterator<IntArray> it = univ.iterator(); it.hasNext(); k++) {
      IntArray elem = it.next();
      univHashMap.put(elem, new Integer(k));
      terms[k] = termMap.get(elem);
    }
    universe = new HashSet(univ);
    makeOperations();
  }
  
  /**
   * Find an equation holding in A and failing in B under the substitution, or
   * return null if there is no such failure. If there is no such failure and
   * bGens generate B, them B is in the variety generated by A. 
   * 
   * @param A
   * @param B
   * @param bGens
   * @return a list of two terms witnessing the failure, or null
   */
  public static Equation findEquationOfAnotB(SmallAlgebra A, SmallAlgebra B, 
                                               int[] bGens) {
    FreeAlgebra F = new FreeAlgebra(A, bGens.length, false);
    Closer closer = new Closer(F.getProductAlgebra(), F.generators(), F.getTermMap());
    closer.setImageAlgebra(B);
    closer.setHomomorphism(bGens);
    closer.sgClosePower();
    return closer.getFailingEquation();
  }
  
  /**
   * Test if op is in the clone of A and return the
   * term if it is; null otherwise.
   * 
   * @param op  an operation of the set of A
   * @param A   an algebra
   * @return    the corresponding term or null
   */
  public static Term findInClone(Operation op, SmallAlgebra A) {
    FreeAlgebra F = new FreeAlgebra(A, op.arity(), false);
    Closer closer = new Closer(F.getProductAlgebra(), F.generators(), F.getTermMap());
    closer.setRootAlgebra(A);
    closer.setOperation(op);
    closer.sgClosePower();
    return closer.getTermForOperation();
  }

  public List<Term> getIdempotentTerms() {
    List<Term> ans = new ArrayList<Term>();
    Term[] terms = getTerms();
    int n = generators().size();
    List<Variable> varlist = new ArrayList<Variable>(n);
    for (int i = 0 ; i < n; i++) {
      varlist.add((Variable)terms[i]);// the first n are Variables
      ans.add(terms[i]);
    }
    for (int i = n; i < terms.length; i++) {
      Term t = terms[i];
      Operation op = t.interpretation(this, varlist, true);
      if (Operations.isIdempotent(op)) ans.add(t);
    }
    return ans;
  }
      

  /**
   * Construct a FreeAlgebra when the gens and univ are already
   * given. Useful for reading back from a file without calculating
   * the universe again.
  */
  public FreeAlgebra(String name, BigProductAlgebra prod,
                     List<IntArray> gens, List<IntArray> univList) {
    super(name, prod, gens, univList);
  }

  public AlgebraType algebraType() {
    return AlgebraType.FREE;
  }

  public static void main(String[] args) throws java.io.IOException,
                                   org.uacalc.io.BadAlgebraFileException {
    if (args.length == 0) {
      SmallAlgebra alg0 = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/lat2.xml");
      SmallAlgebra n5 = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/n5.ua");
      SmallAlgebra lyndon = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/lyndon.ua");
      SmallAlgebra d16 = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/D16.ua");
      //Equation eq = findEquationOfAnotB(alg0, alg1, new int[] {1, 2, 3});
      //System.out.println("eq is\n" + eq);
      int n = 5; 
      FreeAlgebra f = new FreeAlgebra(lyndon, n);
      System.out.println("|F(" + n + ")| = " + f.cardinality());
      return;
    }
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    int pow = 2;
    if (args.length > 1) {
      try {
        pow = Integer.parseInt(args[1]);
      }
      catch (Exception e) {}
    }
    System.out.println("pow is " + pow);

    FreeAlgebra alg2 = new FreeAlgebra("test", alg, pow);
    AlgebraIO.writeAlgebraFile(alg2, "/tmp/fr" + pow + ".xml");
  }

}


