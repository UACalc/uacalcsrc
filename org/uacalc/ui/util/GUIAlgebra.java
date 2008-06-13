package org.uacalc.ui.util;

import org.uacalc.alg.*;
import java.io.File;
import java.util.*;


/**
 * A data structure to hold an algebra and some related gui 
 * data.
 * 
 * @author ralph
 *
 */
public class GUIAlgebra {

  private final SmallAlgebra alg;
  private File file;
  
  private static int count = 0;
  /**
   * A unique int essentially giving the order of creation.
   */
  private final int serial;

  private List<GUIAlgebra> parents = new ArrayList<GUIAlgebra>(); // may be empty or a singleton
  
  /**
   * Only edited and new algebras will get this mark. 
   * Things like quotient algebras won't even though they 
   * can be saved.
   */
  private boolean needsSave = false;
  
  public GUIAlgebra(SmallAlgebra alg) {
    synchronized(this) {
      serial = count++;
    }
    this.alg = alg;
    if (alg.parents() != null) {
      for (SmallAlgebra a : alg.parents()) {
        parents.add(new GUIAlgebra(a));
      }
    }
  }
  
  public GUIAlgebra(SmallAlgebra alg, File file) {
    this(alg);
    this.file = file;
  }
  
  /**
   * We should use this constructor when we want to specify the parents.
   * For example, if we have an algebra on the GUIAlgebraList and we
   * form a quotient algebra, the parent should be the algebra.  
   */
  public GUIAlgebra(SmallAlgebra alg, File file, List<GUIAlgebra> parents) {
    this(alg, file);
    this.parents = parents;
  }
  
  /**
   * We should use this constructor when we want to specify the parents.
   * For example, if we have an algebra on the GUIAlgebraList and we
   * form a quotient algebra, the parent should be the algebra.  
   */
  public GUIAlgebra(SmallAlgebra alg, File file, GUIAlgebra parent) {
    this(alg, file);
    parents.add(parent);
  }
  
  public SmallAlgebra getAlgebra() {
    return alg;
  }
  
  public void setFile (File file) {
    this.file = file;
  }
  
  public File getFile() { return file; }
  
  public List<GUIAlgebra> getParents() { return parents; }
  
  public int getSerial() { return serial; }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof GUIAlgebra)) return false;
    return serial == ((GUIAlgebra)obj).getSerial();
  }
  
  public int hashCode() {
    return serial;
  }

  private void setNeedsSave(boolean needsSave) {
    this.needsSave = needsSave;
  }

  private boolean needsSave() {
    return needsSave;
  }
  
}
