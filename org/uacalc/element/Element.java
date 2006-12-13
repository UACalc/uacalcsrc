/**
 * 
 */
package org.uacalc.element;

import org.uacalc.alg.Algebra;

/**
 * 
 * 
 * @author ralph
 *
 */
public interface Element {
  
  //public static enum ElementType {
  //  BASIC
  //}

  /**
   * The algebra this element is in.
   * 
   * @return the algebra that this element belongs to.
   */
  public Algebra getAlgebra();
  
  /**
   * The index of this element in the algebra.
   * 
   * @return
   */
  public int index();
  
  public String toString();
  
  public Element getParent();
  
  public Element[] getParentArray();
  
  public int[] parentIndexArray();
  
  
  
}
