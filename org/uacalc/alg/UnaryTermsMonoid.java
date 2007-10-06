package org.uacalc.alg;

import java.util.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractSet;
import java.util.HashSet;

import org.uacalc.alg.conlat.CongruenceLattice;
import org.uacalc.alg.op.*;
import org.uacalc.alg.sublat.SubalgebraLattice;
import org.uacalc.terms.*;
import org.uacalc.util.*;

//TODO:
// make a class for generating the semigroup os some self maps.

/**
 * The monoid or semigroup of unary terms.
 * 
 * @author ralph
 *
 */
public class UnaryTermsMonoid extends GeneralAlgebra implements SmallAlgebra {

  private SmallAlgebra generatingAlgebra;
  
  private FreeAlgebra free1;
  
  //private Term[] unaryTerms;
  
  private List<Term> unaryTermList  = new ArrayList<Term>();
  
  private List<TermOperation> unaryTermOpList = new ArrayList<TermOperation>();
  
  public UnaryTermsMonoid(SmallAlgebra alg) {
    this(alg, false);
  }
  
  public UnaryTermsMonoid(SmallAlgebra alg, boolean includeId) {
    super("UnaryTerms(" + alg.name() + ")");
    generatingAlgebra = alg;
    free1 = new FreeAlgebra(alg, 1);
    Term[] unaryTerms = free1.getTerms();
    universe = new HashSet(unaryTerms.length);
    for (int i = 0; i < unaryTerms.length; i++) {
      universe.add(unaryTerms[i]);
      unaryTermList.add(unaryTerms[i]);
    }
    List<Operation> operations = new ArrayList<Operation>(1);
    final Operation opx = Operations.makeBinaryIntOperation(
        OperationSymbol.PRODUCT, free1.cardinality(), makeTable());
    Operation op = new AbstractOperation(OperationSymbol.PRODUCT, unaryTerms.length) {
      public Object valueAt(List args) { // this is a list of 2 terms.
        int i0 = free1.elementIndex(free1.getElementFromTerm((Term)args.get(0)));
        int i1 = free1.elementIndex(free1.getElementFromTerm((Term)args.get(1)));
        int k = intValueAt(new int[] {i0,i1});
        return unaryTermList.get(k);
      }
      public int intValueAt(int[] args) {
        return opx.intValueAt(args);
      }
    };
  }
  
  private int[][] makeTable() {
    final int n = generatingAlgebra.cardinality();
    final int m = free1.cardinality();
    List<Variable> varList = free1.getVariables();
    int[][] table = new int[m][m];
    Map<IntArray,Integer> map = free1.getUniverseOrder();
    final int[] tmp = new int[n]; // holds the values of the product map
    int i = 0;
    for (Term term0 : unaryTermList) {
      TermOperation termOp0 = new TermOperationImp(term0, varList, generatingAlgebra);
      int j = 0;
      for (Term term1 : unaryTermList) {
        TermOperation termOp1 = new TermOperationImp(term1, varList, generatingAlgebra);
        for (int r = 0; r < n; r++) {
          tmp[r] = termOp0.intValueAt(new int[] {termOp1.intValueAt(new int[] {r})});
        }
        table[i][j] = map.get(new IntArray(tmp));
        j++;
      }
      i++;
    }
    return table;
  }
  
  public CongruenceLattice con() {
    // TODO Auto-generated method stub
    return null;
  }

  public int elementIndex(Object elem) {
    // TODO Auto-generated method stub
    return 0;
  }

  public Object getElement(int k) {
    // TODO Auto-generated method stub
    return null;
  }

  public List getUniverseList() {
    return unaryTermList;
  }

  public Map getUniverseOrder() {
    // TODO Auto-generated method stub
    return null;
  }

  public SmallAlgebra parent() {
    // TODO Auto-generated method stub
    return generatingAlgebra;
  }

  public SubalgebraLattice sub() {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isUnary() {
    return false;
  }

  public void makeOperationTables() {
    // TODO Auto-generated method stub

  }

}
