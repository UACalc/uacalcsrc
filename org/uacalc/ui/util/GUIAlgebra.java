package org.uacalc.ui.util;

import org.uacalc.alg.*;
import java.io.File;
import java.util.List;


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
   * A unique int essentially giving the order of creattion.
   */
  private final int serial;

  private List<GUIAlgebra> parents; // may be empty or a singleton
  
  public GUIAlgebra(SmallAlgebra alg) {
    synchronized(this) {
      serial = count++;
    }
    this.alg = alg;
  }
  
  public GUIAlgebra(SmallAlgebra alg, File file) {
    this(alg);
    this.file = file;
  }
  
  public GUIAlgebra(SmallAlgebra alg, File file, List<GUIAlgebra> parents) {
    this(alg, file);
    this.parents = parents;
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
}
