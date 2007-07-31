/* BasicAlgebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg.op;

import java.util.*;
//import org.uacalc.alg.ParameterizedAlgebra;
import javax.script.*;

import org.uacalc.alg.ParameterizedAlgebra;


/**
 * This class represents SmallAlgebra's.
 * Such algebras have a map from {0, ..., n-1} and the elements of
 * the algebra.  The operations are done on the ints and converted back
 * the elements.
 */
public class ParameterizedOperation {

  ParameterizedAlgebra algebra;
  String name;
  String symbolName;
  String setSizeExp;
  List<String> parameterNames;
  String arityExp;
  String description;
  String defaultValueExp;
  String definitionExp;

  public Operation makeOp(final Map<String,String> parmMap) {
    int arity = Integer.parseInt(subParmValues(arityExp, parmMap)); 
    int setSize = Integer.parseInt(subParmValues(setSizeExp, parmMap)); 
    String prog = subParmValues(definitionExp, parmMap);
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine gEngine = manager.getEngineByName("groovy");
    //List<ScriptEngineFactory> factories = manager.getEngineFactories();
    //for (Iterator it = factories.iterator(); it.hasNext(); ) {
      //System.out.println("" + it.next());
    //}
    Invocable invocable = (Invocable)gEngine;
    try {
      gEngine.eval(definitionExp);
    }
    catch (ScriptException e) { e.printStackTrace(); }
    //BS service = invocable.getInterface(BS.class);

    Operation op = new AbstractOperation(symbolName, arity, setSize) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int intValueAt(int[] args) {
        return Math.max(args[0], args[1]);
      }
    };
    return op;
  }

  public static String subParmValues(String parmeterizedString, 
                                     Map<String,String> parmMap) {
    return parmeterizedString;  // for now
  }


}

