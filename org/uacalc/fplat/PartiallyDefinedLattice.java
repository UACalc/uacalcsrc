/**
 * 
 */
package org.uacalc.fplat;

import org.uacalc.lat.Order;
import org.uacalc.terms.*;
import java.util.List;

/**
 * Partially defined lattices as defined in the 
 * chapter "Free and finitely presented lattices"
 * in Gratzer's STA book.
 * 
 * @author ralph
 *
 */
public class PartiallyDefinedLattice implements Order<Variable> {

  Order<Variable> order;
  List<List<Variable>> definedJoins; 
  List<List<Variable>> definedMeets;
  
  public PartiallyDefinedLattice(String name, Order<Variable> order, 
      List<List<Variable>> joins, List<List<Variable>> meets) {
    this.order = order;
    this.definedJoins = joins;
    this.definedMeets = meets;
  }
  
  public boolean leq (Variable a, Variable b) {
    return order.leq(a, b);
  }
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
