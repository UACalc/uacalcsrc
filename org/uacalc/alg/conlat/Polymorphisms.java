package org.uacalc.alg.conlat;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.alg.op.*;

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
  final int algSize;
  final int arity;
  final boolean idempotent;
  final int[] fixedValues;
  Operation partialOp;
  int[] partialOpTable;
  final int tableSize;
  
  Map<IntArray,Map<IntArray,Partition>> graph;
  
  public Polymorphisms(int arity, List<Partition> pars,  boolean idempotent, int[] fixedValues) {
    this.pars = pars;
    this.arity = arity;
    this.idempotent = idempotent;
    this.fixedValues = fixedValues;
    algSize = pars.get(0).universeSize();  // pars cannot be empty !!!!!
    int s = 1;
    for (int i = 0 ; i < arity; i++) {
      s = s * algSize;
    }
    tableSize = s;
    
    //makeGraph(); // skip for now
    
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
