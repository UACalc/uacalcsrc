package org.uacalc.terms;

import org.uacalc.alg.op.*;

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
    if (str == "") throw new IllegalArgumentException("null string");
    String[] strings = str.split("\\(", 2);
    // check on the sanity of the token.
    // it should not be or start with a digit.
    if (strings.length == 1) return new VariableImp(str);
    // get rid of final ")" 
    String argsString = strings[1].substring(0, strings[1].length() - 1);
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
    ans.add(str.substring(start));
    return ans;
  }
  
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    String test1 = "g(x),f(x,z),u";
    String test = "";
    String test3 = "f(x,g(x,y),z)";
    System.out.println(getArgumentStrings(test));
    Term term = stringToTerm(test3);
    System.out.println(term);
  }

  
  
  
  
  
  
  
  
  
  

}
