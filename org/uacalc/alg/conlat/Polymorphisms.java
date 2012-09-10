package org.uacalc.alg.conlat;

import java.util.*;
import org.uacalc.util.*;

/**
 * Given a collections of partition on a set
 * this calculates the polymorphisms of hte coollection.
 * 
 * 
 * @author ralph
 *
 */
public class Polymorphisms {

  List<Partition> pars;
  int arity;
  boolean idempotent;  
  
  Map<IntArray,Map<IntArray,Partition>> graph;
  
  public Polymorphisms(int arity, List<Partition> pars,  boolean idempotent) {
    this.pars = pars;
    this.arity = arity;
    this.idempotent = idempotent;
    makeGraph();
  }
  
  void makeGraph() {
    graph = new TreeMap<IntArray,Map<IntArray,Partition>>();
  }
  
  
  
  
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
