package org.uacalc.ui.util;

import java.util.*;
import java.io.*;
import org.uacalc.alg.*;

/**
 * A list of algebra in a session.
 * 
 * 
 * @author ralph
 *
 */
public class GUIAlgebraList implements Iterable<GUIAlgebra> {

  private List<GUIAlgebra> algList = new ArrayList<GUIAlgebra>();
  //private int currentAlgIndex = -1;
  
  public Iterator<GUIAlgebra> iterator() {
    return algList.iterator();
  }
  
  public void add(GUIAlgebra gAlg, boolean makeCurrentAlg) {
    algList.add(gAlg);
    //if (makeCurrentAlg) {
    //  currentAlgIndex = algList.size() - 1;
    //}
  }
  
  public void add(GUIAlgebra gAlg) {
    add(gAlg, true);
  }
  
  public void add(SmallAlgebra alg, File file, boolean makeCurrentAlg) {
    add(new GUIAlgebra(alg, file), makeCurrentAlg);
  }
  
  public void add(SmallAlgebra alg, boolean makeCurrent) {
    add(alg, null, makeCurrent);
  }
  
  public void add(SmallAlgebra alg, File file) {
    add(alg, file, true);
  }
  
  public void add(SmallAlgebra alg) {
    add(alg, null, true);
  }
  
  //public GUIAlgebra getCurrentAlgebra() {
  //  if (currentAlgIndex >= 0 && currentAlgIndex < algList.size()) {
  //    return algList.get(currentAlgIndex);
  //  }
  //  return null;
  //}
  
  public void removeAlgebra(GUIAlgebra alg) {
    int index = findIndex(alg);
    if (index != -1) algList.remove(index);
  }
  
  private int findIndex(GUIAlgebra alg) {
    int index = 0;
    for (GUIAlgebra alg2 : algList) {
      if (alg2.equals(alg)) return index;
      index++;
    }
    return -1;
  }
  
  //public int getCurrentAlgIndex() { return currentAlgIndex; }
  
  //public void setCurrentAlgIndex(int v) { currentAlgIndex = v; }
  
  public int size() { return algList.size(); }
  
  public GUIAlgebra get(int index) {
    return algList.get(index);
  }
  
  
  
}
