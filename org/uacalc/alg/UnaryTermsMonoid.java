package org.uacalc.alg;

import java.util.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractSet;
import java.util.HashSet;

import org.uacalc.alg.conlat.CongruenceLattice;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.SimilarityType;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.sublat.SubalgebraLattice;
import org.uacalc.terms.Term;

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
  
  //private SmallAlgebra free1;
  
  //private Term[] unaryTerms;
  
  private List<Term> unaryTermList  = new ArrayList<Term>();
  
  public UnaryTermsMonoid(SmallAlgebra alg) {
    this(alg, false);
  }
  
  public UnaryTermsMonoid(SmallAlgebra alg, boolean includeId) {
    super("UnaryTerms(" + alg.name() + ")");
    generatingAlgebra = alg;
    FreeAlgebra free1 = new FreeAlgebra(alg, 1);
    Term[] unaryTerms = free1.getTerms();
    universe = new HashSet(unaryTerms.length);
    for (int i = 0; i < unaryTerms.length; i++) {
      universe.add(unaryTerms[i]);
      unaryTermList.add(unaryTerms[i]);
    }
    List<Operation> operations = new ArrayList<Operation>(1);
    Operation op = new AbstractOperation(OperationSymbol.PRODUCT, unaryTerms.length) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
    };
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
