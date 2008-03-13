package org.uacalc.ui.util;

import java.util.*;
import org.uacalc.alg.*;

/**
 * A list of algebra in a session.
 * 
 * 
 * @author ralph
 *
 */
public class GUIAlgebraList {

  private List<GUIAlgebra> algList = new ArrayList<GUIAlgebra>();
  private int currentAlgIndex = -1;
  
  public void add(GUIAlgebra gAlg, boolean makeCurrentAlg) {
    algList.add(gAlg);
    if (makeCurrentAlg) {
      currentAlgIndex = algList.size() - 1;
    }
  }
  
  public void add(GUIAlgebra gAlg) {
    add(gAlg, true);
  }
  
  public void add(SmallAlgebra alg, boolean makeCurrentAlg) {
    add(new GUIAlgebra(alg), makeCurrentAlg);
  }
  
  public void add(SmallAlgebra alg) {
    add(alg, true);
  }
  
  public GUIAlgebra getCurrentAlgebra() {
    if (currentAlgIndex >= 0 && currentAlgIndex < algList.size()) {
      return algList.get(currentAlgIndex);
    }
    return null;
  }
  
  public int getCurrentAlgIndex() { return currentAlgIndex; }
  
  public void setCurrentAlgIndex(int v) { currentAlgIndex = v; }
  
  
  
}
