/**
 * 
 */
package org.uacalc.eq;

import java.util.*;
import org.uacalc.terms.*;

/**
 * Presentations for finitely presented algebras
 * consisting of a list of variables and equations 
 * thought of as relations.
 * 
 * 
 * @author ralph
 *
 */
public class Presentation {

  private final List<Variable> variables;
  
  private final List<Equation> relations;
  
  public Presentation(List<Variable> vars, List<Equation> rels) {
    variables = vars;
    relations = rels;
  }

  public List<Variable> getVariables() {
    return variables;
  }

  public List<Equation> getRelations() {
    return relations;
  }
  
}
