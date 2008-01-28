package org.uacalc.ui.util;

import org.uacalc.alg.*;
import java.io.File;

/**
 * A data structure to hold an algebra and some related gui 
 * data.
 * 
 * @author ralph
 *
 */
public class GUIAlgebra {

  private SmallAlgebra alg;
  private File file;
  
  private static int count = 0;
  /**
   * A unique int essentially giving the order of creattion.
   */
  private final int serial;
  
  public static enum AlgebraGUIType {
    BASIC, QUOTIENT, SUBALGEBRA, PRODUCT, REDUCT, SUBPRODUCT, FREE
  }
  
  private AlgebraGUIType guiType;
  
  public GUIAlgebra(SmallAlgebra alg) {
    synchronized(this) {
      serial = count++;
    }
    this.alg = alg;
  }
  
}
