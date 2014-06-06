package org.uacalc.terms;

import org.uacalc.alg.op.*;
import org.uacalc.eq.*;

import java.util.*;

/**
 * A class of static methods related to terms.
 * 
 * @author ralph
 *
 */
public class Terms {

  public static Term stringToTerm(String str) {
    if (str == null) throw new IllegalArgumentException("null string");
    str = str.trim();
    if (str == "") throw new IllegalArgumentException("empty string");
    str = adjustParens(str);
    String[] strings = str.split("\\(", 2);
    // check on the sanity of the token.
    // it should not be or start with a digit.
    if (strings.length == 1) {
      if (isValidVarString(str)) return new VariableImp(str);
      throw new IllegalArgumentException("The string " + str + " cannot be made into a variable.");
    }
    if (!isValidOpNameString(strings[0])) { 
      throw new IllegalArgumentException("The string " + strings[0] + " cannot be made into a function symbol.");
    }
    // get rid of final ")" if it is there
    boolean endWithParenthesis = strings[1].substring(strings[1].length() - 1).equals(")");
    String argsString = strings[1];
    if (endWithParenthesis) argsString = strings[1].substring(0, strings[1].length() - 1);
    List<String> argStrings = getArgumentStrings(argsString);
    int arity = argStrings.size();
    OperationSymbol sym = new OperationSymbol(strings[0], arity);
    List<Term> children = new ArrayList<>(arity);
    for (String argString : argStrings){
      children.add(stringToTerm(argString));
    }
    return new NonVariableTerm(sym, children);
  }
  
  /** 
   * Will take something like "x,y,f(x,z),u" and return the list [x, y, f(x,z), u].
   * 
   * @param str
   * @return
   */
  private static List<String> getArgumentStrings(String str) {
    List<String> ans = new ArrayList<>();
    int start = 0;
    int depth = 0;
    for (int i = 0; i < str.length(); i++) {
      String elt = str.substring(i,i+1);
      if (elt.equals("(")) depth++;
      if (elt.equals(")")) depth--;
      if (depth == 0 && elt.equals(",")) {
        ans.add(str.substring(start, i));
        start = i + 1;
      }
    }
    System.out.println("depth is " + depth);
    ans.add(str.substring(start));
    return ans;
  }
  
  public static boolean isValidVarString (String str) {
    if (str.length() == 0) return false;
    if (!str.substring(0,1).matches("[A-Za-z]")) return false;
    if (str.matches("\\s")) return false;
    if (str.contains(",")) return false;
    if (str.contains("(")) return false;
    if (str.contains(")")) return false;
    return true;
  }
  
  public static boolean isValidOpNameString (String str) {
    return isValidVarString(str);  // use the sane for both
  }
  
  private static String adjustParens(String str) {
    int depth = 0;
    for (int i = 0; i < str.length(); i++) {
      String elt = str.substring(i,i+1);
      if (elt.equals("(")) depth++;
      if (elt.equals(")")) depth--;
    }
    if (depth == 0) return str;
    if (depth > 0) {
      StringBuffer buf = new StringBuffer(str);
      while (depth-- > 0) buf.append(")");
      return buf.toString();
    }
    return str.substring(0, str.length() + depth);  // depth is negative
  }
  
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
    String test1 = "g(x,f(x,z),u))";
    String test = "";
    String test3 = "f(x,g(x,y),z)";
    System.out.println(getArgumentStrings(test));
    Term term = stringToTerm(test3);
    System.out.println(term);
    System.out.println(isValidVarString(test1));
    String foo = adjustParens(test1);
    System.out.println("adjust test1 is " + foo);
  }

  
  
  
  
  
  
  
  
  
  

}
