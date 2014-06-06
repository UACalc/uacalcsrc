package org.uacalc.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.terms.*;

/** 
 * A class of static methods for equations.
 * 
 * @author ralph
 *
 */
public class Equations {

  public static Equation associativeLaw(OperationSymbol f) {
    if (f.arity() != 2) throw new IllegalArgumentException("The arity must be 2");
    List<Term> xy = new ArrayList<>(); 
    xy.add(Variable.x); 
    xy.add(Variable.y);
    List<Term> yz = new ArrayList<>(); 
    yz.add(Variable.y); 
    yz.add(Variable.z);
    Term fxy = new NonVariableTerm(f, xy);
    Term fyz = new NonVariableTerm(f, yz);
    Term left = new NonVariableTerm(f, Arrays.asList(Variable.x, fyz));
    Term right = new NonVariableTerm(f, Arrays.asList(fxy, Variable.z));
    return new Equation(left, right);
  }
  
  /**
   * Test if f(x0,x1,...,x{k-1}) = f(x{k-1},x0, ...,x{k-2}).
   * The arity must be at least 1.
   * 
   * @param f
   * @return
   */
  public static Equation cyclicLaw(OperationSymbol f) {
    int k = f.arity();
    List<Term> args = new ArrayList<>(k);
    List<Term> args2 = new ArrayList<>(k);
    args.add(new VariableImp("x0"));
    args2.add(new VariableImp("x" + (k-1)));
    for (int i = 1; i < k; i++) {
      args.add(new VariableImp("x" + i));
      args2.add(new VariableImp("x" + (i - 1)));
    }
    return new Equation(new NonVariableTerm(f,args), new NonVariableTerm(f, args2));
  }
  
  /**
   * Arity must be at least 2.
   * 
   * @param f   s function symbol with arity at least two
   * @return
   */
  public static Equation firstSecondSymmetricLaw(OperationSymbol f) {
    int k = f.arity();
    List<Term> args = new ArrayList<>(k);
    List<Term> args2 = new ArrayList<>(k);
    Variable x0 = new VariableImp("x0");
    Variable x1 = new VariableImp("x1");
    args.add(x0);
    args.add(x1);
    args2.add(x1);
    args2.add(x0);
    for (int i = 2; i < k; i++) {
      Variable xi = new VariableImp("x" + i);
      args.add(xi);
      args2.add(xi);
    }
    return new Equation(new NonVariableTerm(f,args), new NonVariableTerm(f, args2));
  }
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
