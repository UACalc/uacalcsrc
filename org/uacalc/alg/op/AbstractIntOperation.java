package org.uacalc.alg.op;

import java.util.List;

/**
 * This class is not abstract, but has several optional
 * methods which throw an UnsupportedOperationException.
 * It is for Jython which has trouble subclassing an 
 * abstract class. Groovy had this same trouble.
 * 
 * 
 * @author ralph
 *
 * $Id$
 */
public class AbstractIntOperation extends AbstractOperation {

  public AbstractIntOperation(String name, int arity, int algSize) {
    super(new OperationSymbol(name, arity), algSize);
  }

  public AbstractIntOperation(OperationSymbol symbol, int algSize) {
    super(symbol, algSize);
  }

  
  @Override
  public Object valueAt(List args) {
    throw new UnsupportedOperationException();
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
