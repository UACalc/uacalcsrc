package org.uacalc.ui.util;

import org.uacalc.alg.*;
import org.uacalc.alg.op.*;
import org.uacalc.lat.*;
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
  
  private List<Operation> semilatticeOps;
  
  private List<BasicLattice> lats;
  
  //private Map<Operation,BasicLattice> opToLat;
  
  private int currentLatIndex = 0;
  
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

  public void setNeedsSave(boolean needsSave) {
    this.needsSave = needsSave;
  }

  public boolean needsSave() {
    return needsSave;
  }
  
  public List<Operation> getSemilatticeOperataions() {
    if (semilatticeOps == null) {
      semilatticeOps = new ArrayList<Operation>();
      for (Operation opx : alg.operations()) {
        if (Operations.isCommutative(opx) && Operations.isIdempotent(opx)
            && Operations.isAssociative(opx)) semilatticeOps.add(opx);
      }
    }
    return semilatticeOps;
  }
  
  private void makeLattices(boolean forceRemake) {
    if (forceRemake) semilatticeOps = null;
    List<Operation> ops = getSemilatticeOperataions();
    lats = new ArrayList<BasicLattice>(ops.size());
    for (Operation op : ops) {
      List univ = new ArrayList(alg.universe());
      BasicLattice lat = op.symbol().equals(OperationSymbol.JOIN) ? 
          Lattices.latticeFromJoin("", univ, op) : Lattices.latticeFromMeet("", univ, op);
      //if (op.symbol().equals(OperationSymbol.JOIN)) lat = Lattices.dual(lat);
      lats.add(lat);
    }
  }
  
  public BasicLattice getCurrentLattice(boolean makeIfNull) {
    if (lats == null) {
      if (!makeIfNull) return null;
      makeLattices(makeIfNull);
    }
    if (lats.size() == 0) return null;
    return lats.get(currentLatIndex);
  }
  
  public void resetLattices() {
    lats = null;
  }
  
  public String toString() {
    return toString(false);
  }
  
  public String toString(boolean verbose) {
    String extra = "";
    String name = getAlgebra().getName();
    if (verbose && name != null && name.length() > 0) extra = " (" + name + ")"; 
    return "A" + serial + extra;
  }
  
}
