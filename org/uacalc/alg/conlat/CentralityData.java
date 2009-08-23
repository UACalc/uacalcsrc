package org.uacalc.alg.conlat;

import org.uacalc.element.*;

/**
 * This will hold two tolerance S and T, a congruence delta 
 * and the status of
 * centraility, weak centrality, strong rectangularity: Q(S,T,delta), 
 * including failure info and commutators. 
 * 
 * 
 * @author ralph
 *
 */
public class CentralityData implements Comparable<CentralityData> {
  
  private final BinaryRelation left;
  private final BinaryRelation right;
  private final Partition delta;
  
  private SubProductElement centralityFailure;
  private SubProductElement weakCentralityFailure;
  private SubProductElement strongRectangularityFailure;
  
  
  public CentralityData(BinaryRelation S, BinaryRelation T, Partition delta) {
    this.left = S;
    this.right = T;
    this.delta = delta;
  }
  
  public int compareTo(CentralityData data) {
    return delta.compareTo(data.getDelta());
  }
  
  public BinaryRelation getLeft() { return left; }
  public BinaryRelation getRight() { return right; }
  public Partition getDelta() { return delta; }
  
  public void setCentralityFailure(SubProductElement centralityFailure) {
    this.centralityFailure = centralityFailure;
  }

  public SubProductElement getCentralityFailure() {
    return centralityFailure;
  }

  public void setWeakCentralityFailure(SubProductElement weakCentralityFailure) {
    this.weakCentralityFailure = weakCentralityFailure;
  }

  public SubProductElement getWeakCentralityFailure() {
    return weakCentralityFailure;
  }

  public void setStrongRectangularityFailure(SubProductElement strongRectangularityFailure) {
    this.strongRectangularityFailure = strongRectangularityFailure;
  }

  public SubProductElement getStrongRectangularityFailure() {
    return strongRectangularityFailure;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer("left: ");
    sb.append(left);
    sb.append(", right: ");
    sb.append(right);
    sb.append(", delta: ");
    sb.append(delta);
    sb.append(", centralityFailure: ");
    sb.append(getCentralityFailure());
    sb.append(", weakCentralityFailure: ");
    sb.append(getWeakCentralityFailure());
    sb.append(", strongRectangularityFailure: ");
    sb.append(getStrongRectangularityFailure());
    return sb.toString();
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }


}
