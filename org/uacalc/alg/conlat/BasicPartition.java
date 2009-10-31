/* BasicPartition.java 2001/06/04 Ralph Freese */

package org.uacalc.alg.conlat;

import org.uacalc.util.IntArray;
import org.uacalc.lat.*;
import org.latdraw.orderedset.POElem;

import org.uacalc.alg.op.*; // only needed in main
import org.uacalc.alg.*;    // only needed in main


import java.util.*;

/**
 * This class implement the basic operations for partition on the set
 * {0, 1, ..., n-1}. A class that wants to implement partitions on an
 * arbibtrary set can use this class internally. It can also be used
 * as a wrapper for an array of int's in order to use the equals and
 * hashCode methods.
 * <p>
 * It is based on my unpublished note: <i>Partition Algorithms</i>
 * which can be obtained at 
 * {@link <a href="http://www.math.hawaii.edu/~ralph/Notes/">
                   http://www.math.hawaii.edu/~ralph/Notes/</a>}.
 *
 *
 *
 * @author Ralph Freese
 * @version $Id$
 */
public class BasicPartition extends IntArray implements Partition, Comparable {

// rsf we don't need this but if we did I would make it represent
//     a partition on the empty set. So I would set array = new int[0]
//     and size = 0;
//
//  public BasicPartition() {
//    array = null;
//    size = -1;
//  }

  private int blockCount = -1;
  
  private NavigableSet<IntArray> pairs;

  public BasicPartition(int[] part) {
    this.array = part;
    this.size = part.length;
    normalize();
  }

  /**
   * The order of a linear extension respecting rank.
   */
  public int compareTo(Object o) {
    BasicPartition par = (BasicPartition)o;
    int diff =  par.numberOfBlocks() - numberOfBlocks();
    if (diff < 0) return -1;
    if (diff > 0) return 1;
    for (int i = 0; i < size; i++) {
      diff = par.array[i] - array[i];
      if (diff != 0) return diff;
    }
    return 0;
  }

  public static BasicPartition zero(int asize) {
    int[] array = new int[asize];
    for(int i=0; i < asize; i++ ) {
      array[i]= -1;
    }
    return new BasicPartition(array);
  }

  public static BasicPartition one(int asize) {
    int[] array = new int[asize];
    if (asize > 0) {       // in case anyone wants to make a 0 elem alg
      array[0] = -asize;
      for(int i=1; i < asize; i++ ) {
        array[i]= 0;
      }
    }
    return new BasicPartition(array);
  }


  //public int size() {
    //return size;
  //}

  public boolean isRelated(int i, int j) {
    if(representative(i) == representative(j) ) return true;
    return false;
  }
  
  public Iterator<IntArray> iterator() {
    return getPairs().iterator();
  }

  public NavigableSet getPairs() {
    if (pairs == null) makePairs();
    return pairs;
  }
  
  private void makePairs() {
    pairs = new TreeSet<IntArray>(IntArray.lexicographicComparitor());
    int[][] blocks = getBlocks();
    System.out.println("blocks: " + org.uacalc.util.ArrayString.toString(blocks));
    for (int i = 0; i < blocks.length; i++) {
      int[] block = blocks[i];
      for (int j = 0; j < block.length; j++) {
        for (int k = j; k < block.length; k++) {
          pairs.add(new IntArray(new int[] {block[j], block[k]}));
          if (j != k) pairs.add(new IntArray(new int[] {block[k], block[j]}));
        }
      }
    }
  }

  /**
   * Does not need normalized form.
   * Simply counts the negative entries.
   */
  public int numberOfBlocks() {
    if (blockCount < 0) {
      blockCount = 0;
      for( int i=0; i<array.length; i++ ) {
        if( array[i]<0) blockCount++;
      }
    }
    return(blockCount);
  }

  public int rank() {
    return array.length - numberOfBlocks();
  }

  /** EWK       // delete this ? rsf
   * this.array can be ANY array of integers.
   * This function takes its kernel, and prepares
   * the corresponding partition in normalized form.
   */
/*
  private static BasicPartition kernel( int[] part ) {
    int size = part.length;
    int[] array = new int[size];
    HashMap inverse = new HashMap(size);
    int i;
    Integer v;
    int j;
    for( i=0; i<size; i++ ) {
      v= new Integer(part[i]);
      if( inverse.containsKey(v) ) {
        j = ((Integer)inverse.get(v)).intValue();
        array[i] = j;
        array[j]--; //j is the root of i
      }
      else {
        array[i] = -1;
        inverse.put(v, new Integer(i) );
      }
    }
    return new BasicPartition(array);
  }
*/

    /** EWK
     * Convert to Matt's original format:
     * f(i)=the "number" of the block if i, where
     * blocks are numbered starting from zero.
     * array is assumed to be in normalized form.
     */
/*
    public int[] toMatt() {

	int[] matt = new int[size];
	int i;
	int c=0;
	for(i=0; i<size; i++) {
	    if(array[i]<0 ) {
		matt[i]=c;
		c++;
	    } else {
		matt[i]=matt[array[i]];
	    }
	}
	return matt;
    }
*/

 /**
  * This finds the root of the tree containing i and also modifies
  * the partition.
  */
  int root(int i) {
    return root(i, array);
  }

  /**
   * This finds the root of the tree containing i and also modifies
   * the partition.
   */
  static int root(int i, int[] part) {
    int j = part[i];
    if (j < 0) return i;
    int r = root(j, part);
    part[i] = r;
    return r;
  }

  /** 
   * Note r and s must be roots and distinct.
   */
  public void joinBlocks(int r, int s) {
    joinBlocks(r, s, array);
  }

  /** 
   * Note r and s must be roots and distinct.
   */
  static void joinBlocks(int r, int s, int[] part) {
    int sizeR = - part[r];
    int sizeS = - part[s];
    if (sizeR < sizeS) {
      part[r] = s;
      part[s] = -(sizeR + sizeS);
    } 
    else {
      part[s] = r;
      part[r] = -(sizeR + sizeS);
    } 
  }

  public Partition join(Partition part2) {
    return join(this.array, part2.toArray());
  }

  static BasicPartition join(int[] u, int[] v) {
    int n = u.length;
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      ans[i] = v[i];
    }
    for (int i = 0; i < n; i++) {
      if (u[i] >= 0) {
        int r = root(i, ans);
        int s = root(u[i], ans);
        if (r != s) joinBlocks(r, s, ans);
      }
    }
    return new BasicPartition(ans);
  }

  /**
   * This returns the least <code>k</code> such that <code>(a,b)</code>
   * is in the <code>k</code>-fold relational product of <code>par0</code>
   * and <code>par1</code>, with <code>par0</code> coming first and 
   * <code>k</code> counting the total occurances of <code>par0</code>
   * or <code>par1</code>. It returns -1 if <code>(a,b)</code>
   * is not in the join.
   */
  public static int permutabilityLevel(int a, int b, 
                                   Partition par0, Partition par1) {
    // this is actually an n^2 algorithm. If speed becomes a problem
    // we can introduce some data structure to make it faster.
    int ans = 1;
    final int size = par0.universeSize();
    final int[] arr0 = new int[size];
    final int[] arr1 = new int[size];
    System.arraycopy(par0.toArray(), 0, arr0, 0, size);
    System.arraycopy(par1.toArray(), 0, arr1, 0, size);
    final int r0b = root(b, arr0);
    final int r1b = root(b, arr1);
    //a = root(a, arr0);
    if (root(a, arr0) == r0b) return ans;
    ans++;
    while (true) {
      boolean bigger = false;
      for (int i = 0; i < size; i++) {
        final int r0a = root(a, arr0);
        if (root(i, arr0) == r0a) {  // a and i in the same block of arr0
          final int r1i = root(i, arr1);
          if (r1i == r1b) return ans;
          final int r1a = root(a, arr1);
          if (r1i != r1a) {
            bigger = true;
            joinBlocks(r1a, r1i, arr1);
          }
        }
      }
      ans++;
      for (int i = 0; i < size; i++) {
        final int r1a = root(a, arr1);
        if (root(i, arr1) == r1a) {  // a and i in the same block of arr1
          final int r0i = root(i, arr0);
          if (r0i == r0b) return ans;
          final int r0a = root(a, arr0);
          if (r0i != r0a) {
            bigger = true;
            joinBlocks(r0a, r0i, arr0);
          }
        }
      }
      if (!bigger) return -1;
      ans++;
    }
  }

  /**
   * This is the max of  <code>permutabilityLevel(a, b, par0, par1)</code>
   * over all (a, b) in the join.
   */
  public static int permutabilityLevel(Partition par0, Partition par1) {
    int level = -1;
    Partition join = par0.join(par1);
    final int n = par0.universeSize();
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        if (join.isRelated(i,j)) {
          level = Math.max(level, permutabilityLevel(i, j, par0, par1));
          level = Math.max(level, permutabilityLevel(j, i, par0, par1));
        }
      }
    }
    return level;
  }

  public Partition meet(Partition part2) {
    return meet(this.array, part2.toArray());
  }

  static BasicPartition meet(int[] u, int[] v) {
    int n = u.length;
    HashMap ht = new HashMap();
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      // Get the roots and make a "partition" [r1, r2] so we can 
      // store them on a HT.
      IntArray rootPair 
               = new IntArray(new int[] {root(i, u), root(i, v)});
      Integer rootInt = (Integer)ht.get(rootPair);
      if (rootInt != null) {
        int r = rootInt.intValue();
        ans[r]--; // increase the size by 1 (since it is stored as a neg int)
        ans[i] = r;
      }
      else {
        ht.put(rootPair, new Integer(i));
        ans[i] = -1;
      }
    }
    return new BasicPartition(ans);
  }

  public boolean leq(Partition part2) {
    return leq(this.array, part2.toArray());
  }

  public static boolean leq(int[] u, int[] v) {
    int n = u.length;
    for (int i = 0; i < n; i++) {
      if (u[i] >= 0 && (root(i, v) != root(root(i, u), v))) return false;
    }
    return true;
  }
  
  /**
   * This modifies this.array.
   */
  public void normalize() {
    normalize(array);
  }

  /** 
   * Modify <code>part</code> so that it is in normal form.
   */
  public static void normalize(int[] part) {
    final int size = part.length;
    // fix this
    if (part[0] == 0) throw new RuntimeException();
    for (int i = 0; i < size; i++) {
      int r = root(i, part);
      if (r > i) {
        part[i] = part[r];
        part[r] = i;
      }
    }
    for (int i = 0; i < size; i++) {
      root(i, part);
    }
  }
  
  public boolean isZero() {
    for (int i = 0; i < size; i++) {
      if (array[i] != -1) return false;
    }
    return true;
  }

  public String toString() {
    return toString(BLOCK);
  }

  public String toString(int kind) {
    switch (kind) {
      case INTERNAL:
        return intArrayToString(array);
      case EWK:
        return partToKissString(array);
      case BLOCK:
        return partToBlockString(array);
      case HUMAN:
        return partToBlockString(array) +
	    " (" + numberOfBlocks() + " block(s))";
    }
    return intArrayToString(array);
  }

  /**
   * Make String representation of the partition for 
   * the .con and related files. 
   */
  public static String partToKissString(int [] part) {
    StringBuffer sb = new StringBuffer(",");
    for (int i = 0; i < part.length; i++) {
      sb.append(String.valueOf(root(i, part)));
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    return sb.toString();
  }

  /**
   * Get the block form of this partition as an array of arrays.
   */
  public int[][] getBlocks() {
    int[] part = getArray();
    ArrayList[] blocks = new ArrayList[part.length];
    for(int i = 0; i < part.length; i++) {
      int r = root(i, part);
      if (blocks[r] == null) blocks[r] = new ArrayList();
      blocks[r].add(new Integer(i));
    }
    int[][] ans = new int[numberOfBlocks()][];
    int blockNum = 0;
    for (int i = 0; i < blocks.length; i++) {
      if (blocks[i] != null) {
        ans[blockNum] = new int[blocks[i].size()];
        int j = 0;
        for (Iterator it = blocks[i].iterator(); it.hasNext(); j++) {
          ans[blockNum][j] = ((Integer)it.next()).intValue();
        }
        blockNum++;
      }
    }
    return ans;
  }
        


  /** EWK
   * Make String representation of the partition in block form.
   */
  private static String partToBlockString(int [] part) {
    //It is assumed that part is normalized.
    int size = part.length;
    boolean isZero = true;
    for(int i = 0; i < size; i++) {
      if (part[i] != -1) {
        isZero = false;
        break;
      }
    }
    //if( isZero ) {
    //  return("the \"zero\" partition");
    //}
    //if( part[0] == -part.length ) {
    //  return("the \"one\" partition");
    //}

    ArrayList blocks[] = new ArrayList[part.length];
    int i;
    for(i=0; i<part.length; i++) {
      int r = root(i,part);
      if (blocks[r] == null) blocks[r] = new ArrayList();
      blocks[r].add(new Integer(i));
    }
    final String vert = "|";
    final String dash = "-";
    final String comma = ",";
    final StringBuffer sb = new StringBuffer(vert);
    boolean first;
    for(i = 0; i < part.length; i++) {
      if( blocks[i] == null ) continue;
      Iterator it = blocks[i].iterator();
      first = true;
      while( it.hasNext() ) {
        if ( !first ) {
          sb.append(comma);
        } else {
          first = false;
        }
        sb.append(it.next().toString());
      }
      sb.append(vert);
    }
    return(sb.toString());
  }
  
  /**
   * This is the public way of finding the root. Unlike <code>root</code>
   * it does not modify array.
   */
  public int representative(int i) {
    int j = array[i];
    if (j < 0) return i;
    return representative(j);
  }

  public boolean isRepresentative(int i) {
    return representative(i) == i;
  }

  boolean isRoot(int i) {
    return representative(i) == i;
  }

  public int[] representatives() {
    int c = 0;
    for (int i = 0; i < size; i++) {
      if (isRoot(i)) c++;
    }
    int[] ans = new int[c];
    c = 0;
    for (int i = 0; i < size; i++) {
      if (isRoot(i)) ans[c++] = i;
    }
    return ans;
  }
  
  /**
   * True if all 0-1 sublattices of the closure of the
   * sublattice generated by pars are themselves closed. 
   * 
   * 
   * @param pars
   * @return
   */
  public static boolean isHereditary(List<Partition> pars) {
    SmallAlgebra alg = unaryCloneAlgebra(pars);
    System.out.println("Size of the closure is " + alg.con().cardinality());
    CongruenceLattice con = alg.con();
    BasicLattice conBasic = con.getBasicLattice(true);
    
    for (Iterator it = conBasic.getUniverseList().iterator(); it.hasNext(); ) {
      POElem e = (POElem)it.next();
      System.out.println(e);
    }
 // here
    return true;
  }
  
  public static SmallAlgebra unaryCloneAlgebra(List<Partition> pars) {
    String f = "f_";
    final int size = pars.get(0).universeSize();
    final NavigableSet<IntArray> lst = unaryClone(pars);
    //System.out.println("TreeSet: " + lst);
    //IntArray ia0 = new IntArray(new int[] {0, 0, 4, 0, 0, 0, 0});
    //System.out.println("ceiling 0 0 4 0 ... = " + lst.ceiling(ia0));
    System.out.println("number of ops in the unary clone is " + lst.size());
    final List<Operation> ops = new ArrayList<Operation>(lst.size());
    int i = 0;
    for (IntArray ia : lst) {
      ops.add(Operations.makeIntOperation(f + i, 1, size, ia.getArray()));
      i++;
    }
    return new BasicAlgebra("", size, ops);
  }
  
  public static SmallAlgebra binaryCloneAlgebra(List<Partition> pars) {
    String b = "b_";
    final int size = pars.get(0).universeSize();
    final NavigableSet<IntArray> lst = binaryClone(pars);
    System.out.println("number of ops in the binary clone is " + lst.size());
    final List<Operation> ops = new ArrayList<Operation>(lst.size());
    int i = 0;
    for (IntArray ia : lst) {
      ops.add(Operations.makeIntOperation(b + i, 2, size, ia.getArray()));
      i++;
    }
    return new BasicAlgebra("", size, ops);
  }
  
  /**
   * The uses a depth first search to find the set of all unary function
   * which respect all partition in pars.
   * 
   * @param pars
   * @return
   */
  public static NavigableSet<IntArray> unaryClone(List<Partition> pars) {
    // n is the size of the set the partition are on.
    final int n = pars.get(0).universeSize();
    // set is an empty set to hold the answer.
    NavigableSet<IntArray> set = new TreeSet<IntArray>(IntArray.lexicographicComparitor());
    // ia is really just a vector of length n to hold the function we are considering
    IntArray ia = new IntArray(n);
    // call the (recursive) workhorse.
    unaryCloneAux(ia, 0, n, set, pars);
    
    return set;
  }
  
  /**
   * Find all functions repecting the partition and extending the 
   * partial function arr and add them to the answer.
   * 
   * @param arr     a vector representing the partial function f defined for i < k
   * @param k       the first place the function is not defined
   * @param n       the size of the underlying set for the partitions
   * @param ans     the answer set
   * @param pars    the list of partitions
   */
  private static void unaryCloneAux(final IntArray arr, 
                                       final int k,
                                       final int n,
                                       final NavigableSet<IntArray> ans,
                                       final List<Partition> pars) {
    if (k == n) {
      IntArray copy = new IntArray(n);
      System.arraycopy(arr.getArray(), 0, copy.getArray(), 0, n);
      ans.add(copy);
      return;
    }
    for (int value = 0; value < n; value++) {
      if (respects(arr, k, value, pars)) {
        arr.set(k, value);
        unaryCloneAux(arr, k + 1, n, ans, pars);
      }
    }
  }
  
  public static NavigableSet<IntArray> binaryClone(List<Partition> pars) {
    return binaryClone(pars, null);
  }
  
  public static NavigableSet<IntArray> binaryClone(List<Partition> pars, 
                                                   NavigableSet<IntArray> unaryClone) {
    final int n = pars.get(0).universeSize();
    if (unaryClone == null) unaryClone = unaryClone(pars);
    NavigableSet<IntArray> set = new TreeSet<IntArray>(IntArray.lexicographicComparitor());
    List<IntArray> partialOp = new ArrayList<IntArray>(n);
    for (int i = 0; i < n; i++) {
      partialOp.add(null);
    }
    binaryCloneAux(partialOp, 0, n, unaryClone, set);
    System.out.println("binary clone size = " + set.size());
    return set;
  }
  
  private static void binaryCloneAux(final List<IntArray> partialOp, 
                                        final int index, 
                                        final int n, 
                                        final NavigableSet<IntArray> unaryClone,
                                        final NavigableSet<IntArray> set) {
    if (index == n) {
      int[] op = new int[n*n];
      for (int i = 0; i < n; i++) {
        final int[] row = partialOp.get(i).toArray();
        System.arraycopy(row, 0, op, i * n, n);
      }
      set.add(new IntArray(op));
      //System.out.println("set size = " + set.size());
      return;
    }
    for (IntArray unaryFn : unaryClone) {
      if (respects(partialOp, index, n, unaryFn, unaryClone)) {
        partialOp.set(index, unaryFn);
        binaryCloneAux(partialOp, index + 1, n, unaryClone, set);
      }
    }
  }
  
  private static boolean respects(final IntArray partialFunction, 
                           final int k, final int value, 
                           final List<Partition> pars) {
    for (Partition par : pars) {
      final int r = par.representative(k);
      for (int i = 0; i < k; i++) {
        if (r == par.representative(i)) {
          if (!par.isRelated(value, partialFunction.get(i))) return false;
        }
      }
    }
    return true;
  }
  
  private static boolean respects(final List<IntArray> partialBinaryOp, // a list of index-many rows 
                                  final int index,
                                  final int n,
                                  final IntArray unaryOp, // a possible row to add
                                  NavigableSet<IntArray> unaryClone) {
    final IntArray ia = new IntArray(n);
    for (int col = 0; col < n; col++) {
      for (int i = 0 ; i < index; i++) {
        ia.set(i, partialBinaryOp.get(i).get(col));
      }
      //System.out.println("unaryOp(" + col + ") = " + unaryOp.get(col));
      //System.out.println("index = " + index);
      ia.set(index, unaryOp.get(col));
      if (!isInitialMember(ia, index, unaryClone)) return false;
    }
    return true;
  }
  
  private static boolean isInitialMember(final IntArray ia,
                                         final int index,
                                         final NavigableSet<IntArray> unaryClone) {
    final IntArray c = unaryClone.ceiling(ia);
    for (int i = 0; i <= index; i++) {
      if (c.get(i) != ia.get(i)) return false;
    }
    return true;
  }
  
  /**
   * 
   * 
   * @param pars
   * @return
   */
  public static List<Partition> closureAt(List<BasicPartition> pars) {
    final BasicPartition alpha = pars.get(0);
    final int n = alpha.universeSize();
    int[][] blocks = alpha.getBlocks();
    List<IntArray> univ = new ArrayList<IntArray>();
    for (int i = 0; i < blocks.length; i++) {
      final int[] block = blocks[i];
      for (int j = 0; j < block.length; j++) {
        for ( int k = 0; k < block.length; k++) {
          univ.add(new IntArray(new int[] {block[j], block[k]}));
        }
      }
    }
    System.out.println("univ: " + univ);
    final int size = univ.size();
    Partition firstProj = zero(alpha.universeSize()).inducedPartition(univ, 0);
    Partition secondProj = zero(alpha.universeSize()).inducedPartition(univ, 1);
    System.out.println("firstProj: " + firstProj);
    System.out.println("secondProj: " + secondProj);
    Partition alpha0 = alpha.inducedPartition(univ, 0);
    System.out.println("alpha: " + alpha);
    System.out.println("alpha0: " + alpha0);
    Set<Partition> hs = new HashSet<Partition>();
    hs.add(firstProj);
    hs.add(zero(alpha.universeSize()).inducedPartition(univ, 1)); //second proj kernel
    hs.add(alpha0); 
    hs.add(alpha.inducedPartition(univ, 1));
    for (BasicPartition par : pars) {
      System.out.println("gen par is " + par);
      System.out.println("gen par_0 is " + par.inducedPartition(univ, 0));
      hs.add(par.inducedPartition(univ, 0));
      hs.add(par.inducedPartition(univ, 1));      
    }
    List<Partition> sub = subUniverseGenerated(new ArrayList<Partition>(hs));
    System.out.println("sub size = " + sub.size());
    List<Partition> ans = new ArrayList<Partition>();
    for (Partition par : sub) {
      //System.out.println("par: " + par +" is geq firstProj: " + firstProj.leq(par));
      if (firstProj.leq(par)) ans.add(((BasicPartition)par).projection(univ, n, 0));
    }
    return ans;
  }
  
  public Partition projection(List<IntArray> universe, int size, int coord) {
    BasicPartition ans = zero(size);
    final int n = universeSize();
    for (int i = 0; i < n; i++) {
      int r = ans.root(universe.get(i).get(coord));
      int s = ans.root(universe.get(root(i)).get(coord));
      if (r != s) ans.joinBlocks(r, s);
    }
    ans.normalize();
    return ans;
  }
  
  /**
   * Find the induced partition on a subset of a direct product
   * whose <code>coord</code> coordinates are related by 
   * this.
   * 
   * 
   * @param par
   * @param prodUniv
   * @param coord
   * @return
   */
  private Partition inducedPartition(List<IntArray> prodUniv, int coord) {
    //System.out.println("par is " + toString());
    final int n = prodUniv.size();
    BasicPartition ans = zero(n);
    for (int i = 0; i < n; i++) {
      for (int j = i+1; j < n; j++) {
        if (root(prodUniv.get(i).get(coord)) == root(prodUniv.get(j).get(coord))) {
          final int r = ans.root(i);
          final int s = ans.root(j);
          if (r != s) ans.joinBlocks(r, s);
        }
      }
    }
    ans.normalize();
    return ans;
  }
  
  public static List<Partition> joinClosure(List<Partition> pars) {
    Set<Partition> hs = new HashSet<Partition>(pars);
    List<Partition> ans = new ArrayList<Partition>(pars);
    int k = 0;
    for (Partition elt : pars) {
      k++;
      int n = ans.size();
      for (int i = k; i < n; i++) {
        Partition join = elt.join(ans.get(i));
        if (hs.add(join)) ans.add(join);
      }
    }
    return ans;
  }
  
  public static List<Partition> meetClosure(List<Partition> pars) {
    Set<Partition> hs = new HashSet<Partition>(pars);
    List<Partition> ans = new ArrayList<Partition>(pars);
    int k = 0;
    for (Partition elt : pars) {
      k++;
      int n = ans.size();
      for (int i = k; i < n; i++) {
        Partition join = elt.meet(ans.get(i));
        if (hs.add(join)) ans.add(join);
      }
    }
    return ans;
  }
  
  public static List<Partition> subUniverseGenerated(List<Partition> gens) {
    List<Partition> ans = meetClosure(gens);
    ans = meetClosure(ans);
    int k = ans.size();
    while (true) {
      System.out.println("k = " + k);
      ans = joinClosure(ans);
      if (ans.size() == k) return ans;
      k = ans.size();
      System.out.println("k = " + k);
      ans = meetClosure(ans);
      if (ans.size() == k) return ans;
      k = ans.size();
    }
  }

  public static void main(String[] args) {
    
    // Using any of these as theta gives the full 7 element closure.
    // |L(\theta)| = 31 for the first one, and 164 for other two.
    BasicPartition par0 = new BasicPartition(new int[] {-2, 0, -1, -1});
    BasicPartition par1 = new BasicPartition(new int[] {-2, -2, 0, 1});
    BasicPartition par2 = new BasicPartition(new int[] {-2,-2, 1, 0});
    
    BasicPartition jb0 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, 4});
    BasicPartition jb1 = new BasicPartition(new int[] {-3, -2, 1, 0, 0, -1});
    BasicPartition jb2 = new BasicPartition(new int[] {-1, -3, -2, 1, 2, 1});
    System.out.println("jb2 = " + jb2);
    
    //for |X|=5,
    //|01|23|4|
    //|0|12|34|
    //|024|13|
    // this gives the full closure showing this is super bad (52 elements)
    // L(\theta) has 21147 elements.

    BasicPartition snow0 = new BasicPartition(new int[] {-2, 0, -2, 2, -1});
    BasicPartition snow1 = new BasicPartition(new int[] {-1, -2, 1, -2, 3});
    BasicPartition snow2 = new BasicPartition(new int[] {-3, -3, 0, 1, 0});
    System.out.println("snow2 = " + snow2);
    
    // rf0 |01|2|3|
    // rf1 |0|1|23|
    // rf2 |02|13|
    BasicPartition rfjoin = new BasicPartition(new int[] {-2, 0, -2, 2});
    BasicPartition rf0 = new BasicPartition(new int[] {-2, 0, -1, -1});
    BasicPartition rf1 = new BasicPartition(new int[] {-2, -2, 0, 1});
    BasicPartition rf2 = new BasicPartition(new int[] {-1, -1, -2, 2});
    
    // |0|14|23|
    // |023|14|
    // |02|13|4|
    // This just generates 3 x 2, so it is stupid.
    BasicPartition rfx0 = new BasicPartition(new int[] {-1, -2, -2, 2, 1});
    BasicPartition rfx1 = new BasicPartition(new int[] {-3, -2, 0, 0, 1});
    BasicPartition rfx2 = new BasicPartition(new int[] {-2, -2, 0, 1, -1});
    
    // |01|23|45|
    // |0|12|34|5|
    // |05|12|34|
    // This one is complete
    BasicPartition rfy0 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, 4});
    BasicPartition rfy1 = new BasicPartition(new int[] {-1, -2, 1, -2, 3, -1});
    BasicPartition rfy2 = new BasicPartition(new int[] {-2, -2, 1, -2, 3, 0});
    
    // |024|135|
    // |0|12|34|5|
    // |05|12|34|
    // This one is complete
    BasicPartition rfz0 = new BasicPartition(new int[] {-3, -3, 0, 1, 0, 1});
    BasicPartition rfz1 = new BasicPartition(new int[] {-1, -2, 1, -2, 3, -1});
    BasicPartition rfz2 = new BasicPartition(new int[] {-2, -2, 1, -2, 3, 0}); 
    
    // |012|345|6|7|
    // |012|345|67|
    // |0|146|257|3|
    // |03|146|257|
    // this seems to give a closed hexagon. (At least closed under the L(\theta) 
    // construction.
    
    BasicPartition rfw0 = new BasicPartition(new int[] {-3, 0, 0, -3, 3, 3, -1, -1});
    BasicPartition rfw1 = new BasicPartition(new int[] {-3, 0, 0, -3, 3, 3, -2, 6});
    BasicPartition rfw2 = new BasicPartition(new int[] {-1, -3, -3, -1, 1, 2, 1, 2}); 
    BasicPartition rfw3 = new BasicPartition(new int[] {-2, -3, -3, 0, 1, 2, 1, 2}); 
  
    // |01|23|45|
    // |05|21|43|
    // |03|25|41|
    // |024|135|
    BasicPartition bill0 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, 4});
    BasicPartition bill1 = new BasicPartition(new int[] {-2, -2, 1, -2, 3, 0});
    BasicPartition bill2 = new BasicPartition(new int[] {-2, -2, -2, 0, 1, 2}); 
    BasicPartition bill3 = new BasicPartition(new int[] {-3, -3, 0, 1, 0, 1}); 
    
    // |01|23|45|67|
    // |07|21|43|65|
    // |05|27|41|63|
    // |03|25|47|61|
    // |0246|1357|
    BasicPartition billx0 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, 4, -2, 6});
    BasicPartition billx1 = new BasicPartition(new int[] {-2, -2, 1, -2, 3, -2, 5, 0});
    BasicPartition billx2 = new BasicPartition(new int[] {-2, -2, -2, -2, 1, 0, 3, 2}); 
    BasicPartition billx3 = new BasicPartition(new int[] {-2, -2, -2, 0, -2, 2, 1, 4}); 
    BasicPartition billx4 = new BasicPartition(new int[] {-4, -4, 0, 1, 0, 1, 0, 1}); 
    
    // |01|23|45|67|89|
    // |09|21|43|65|87|
    // |07|29|41|63|85|
    // |05|27|49|61|83|
    // |03|25|47|69|81|
    // |02468|13579|
    BasicPartition billy0 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, 4, -2, 6, -2, 8});
    BasicPartition billy1 = new BasicPartition(new int[] {-2, -2, 1, -2, 3, -2, 5, -2, 7, 0});
    BasicPartition billy2 = new BasicPartition(new int[] {-2, -2, -2, -2, 1, -2, 3, 0, 5, 2});  // |07|29|41|63|85|
    BasicPartition billy3 = new BasicPartition(new int[] {-2, -2, -2, -2, -2, 0, 1, 2, 3, 4}); // |05|27|49|61|83|
    BasicPartition billy4 = new BasicPartition(new int[] {-2, -2, -2, 0, -2, 2, -2, 4, 1, 6}); // |03|25|47|69|81|
    BasicPartition billy5 = new BasicPartition(new int[] {-5, -5, 0, 1, 0, 1, 0, 1, 0, 1}); 
    
    // (045|123) (03|15|24) (01|25|34) (02|14|35)
    // DeMeo's closed M_4
    BasicPartition demeo0 = new BasicPartition(new int[] {-3, -3, 1, 1, 0, 0});
    BasicPartition demeo1 = new BasicPartition(new int[] {-2, -2, -2, 0, 2, 1});
    BasicPartition demeo2 = new BasicPartition(new int[] {-2, 0, -2, -2, 3, 2});
    BasicPartition demeo3 = new BasicPartition(new int[] {-2, -2, 0, -2, 1, 3 }); 
    
    // Snow's example of a superbad M_3 for odd |X| is:
    // |01|23|...|2a|
    // |0|12|34|...|2a-1,2a|
    // |evens|odds|
    BasicPartition snow90 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, 4, -2, 6, -1});
    BasicPartition snow91 = new BasicPartition(new int[] {-1, -2, 1, -2, 3, -2, 5, -2, 7});
    BasicPartition snow92 = new BasicPartition(new int[] {-5, -4, 0, 1, 0, 1, 0, 1, 0});
    
    BasicPartition snow130 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, 4, -2, 6, -2, 8, -2, 10, -1});
    BasicPartition snow131 = new BasicPartition(new int[] {-1, -2, 1, -2, 3, -2, 5, -2, 7, -2, 9, -2, 11});
    BasicPartition snow132 = new BasicPartition(new int[] {-7, -6, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0});
    
    // Take the pentagon on seven elements
    // |06|15|2|3|4|
    // |06|145|23|
    // |034|16|25|
    // and you also get the intermediate element
    // |06|145|2|3|
    // in the closure.
    // JB
    BasicPartition jb70 = new BasicPartition(new int[] {-2, -2, -1, -1, -1, 1, 0});
    BasicPartition jb71 = new BasicPartition(new int[] {-2, -3, -2, 2, 1, 1, 0});
    BasicPartition jb72 = new BasicPartition(new int[] {-3, -2, -2, 0, 0, 2, 1});
    
    // Here is a double-winged pentagon that is a congruence lattice on
    // 7 elements.
    //   |0123456|
    //   |056|14|2|3|
    //   |0|1|246|35|
    //   |0123|45|6|
    //   |03|12|45|6|
    //   |0|1|2|3|4|5|6|
    BasicPartition jbdw0 = new BasicPartition(new int[] {-3, -2, -1, -1, 1, 0, 0}); // |056|14|2|3|
    BasicPartition jbdw1 = new BasicPartition(new int[] {-1, -1, -3, -2, 2, 3, 2}); // |0|1|246|35|
    BasicPartition jbdw2 = new BasicPartition(new int[] {-4, 0, 0, 0, -2, 4, -1}); // |0123|45|6|
    BasicPartition jbdw3 = new BasicPartition(new int[] {-2, -2, 1, 0, -2, 4, -1}); // |03|12|45|6|

    
    //                          |01234567|

    //|01|23|4567|
    //|01|23|46|57|           |024|135|6|7|           |036|127|4|5|


    //                        |0|1|2|3|4|5|6|7|

    
    BasicPartition bdw0 = new BasicPartition(new int[] {-2, 0, -2, 2, -4, 4, 4, 4}); // |01|23|4567|
    BasicPartition bdw1 = new BasicPartition(new int[] {-2, 0, -2, 2, -2, -2, 4, 5}); // |01|23|46|57|
    BasicPartition bdw2 = new BasicPartition(new int[] {-3, -3, 0, 1, 0, 1, -1, -1}); // |024|135|6|7|
    BasicPartition bdw3 = new BasicPartition(new int[] {-3, -3, 1, 0, -1, -1, 0, 1}); // |036|127|4|5|

    // |0,1,2,6|3,5|4|7,8|    |0,3,4,8|1|2,7|5,6|
    //                                                      |1,4,5,7|2,3|0|6,8|
    // |0,1,2,6|3|5|4|7,8|    |0,3,4,8|1|7|2|5,6|
    BasicPartition swh0 = new BasicPartition(new int[] {-1, -4, -2, 2, 1, 1, -2, 1, 6}); // |1,4,5,7|2,3|0|6,8|
    BasicPartition swh1 = new BasicPartition(new int[] {-4, 0, 0, -2, -1, 3, 0, -2, 7}); // |0,1,2,6|3,5|4|7,8|
    BasicPartition swh2 = new BasicPartition(new int[] {-4, 0, 0, -1, -1, -1, 0, -2, 7}); // |0,1,2,6|3|5|4|7,8|
    BasicPartition swh3 = new BasicPartition(new int[] {-4,-1, -2, 0, 0, -2, 5, 2, 0}); // |0,3,4,8|1|2,7|5,6|
    BasicPartition swh4 = new BasicPartition(new int[] {-4,-1, -1, 0, 0, -2, 5, -1, 0}); // |0,3,4,8|1|7|2|5,6|

    BasicPartition vs0 = new BasicPartition(new int[] {-2, 0, -2, 2}); // |01|23|
    BasicPartition vs1 = new BasicPartition(new int[] {-2, -2, 0, 1}); // |02|13|
    BasicPartition vs2 = new BasicPartition(new int[] {-2, -2, 1, 0}); // |03|12|

    
    BasicPartition dw0 = new BasicPartition(new int[] {-3, 0, -2, 2, 0, -3, 5, -2, 7, 5}); // |014|23|569|78|
    BasicPartition dw1 = new BasicPartition(new int[] {-3, -2, 0, 1, -3, 0, 4, -2, 4, 7}); // |014|23|569|78|
    BasicPartition dw2 = new BasicPartition(new int[] {-4, -2, 1, 0, -2, 4, 0, 0, -2, 8}); // |014|23|569|78|
    BasicPartition dw3 = new BasicPartition(new int[] {-4, -4, 1, 0, -2, 4, 0, 0, 1, 1}); // |014|23|569|78|

    
    // See message of 10/29/09
    //L1 = (0,1,2|3,4|5,6) (0,3,5|1,6|2,4) (0,4,6|1,5|2,3) 

    BasicPartition L10 = new BasicPartition(new int[] {-3, 0, 0, -2, 3, -2, 5}); 
    BasicPartition L11 = new BasicPartition(new int[] {-3, -2, -2, 0, 2, 0, 1}); 
    BasicPartition L12 = new BasicPartition(new int[] {-3, -2, -2, 2, 0, 1, 0});
    
    //L2 = (0,1,2|3,4|5,6) (0,3,5|1,4|2,6) (0,4,6|1,5|2,3)
    
    BasicPartition L20 = new BasicPartition(new int[] {-3, 0, 0, -2, 3, -2, 5}); 
    BasicPartition L21 = new BasicPartition(new int[] {-3, -2, -2, 0, 1, 0, 2}); 
    BasicPartition L22 = new BasicPartition(new int[] {-3, -2, -2, 2, 0, 1, 0});
    
    
    List<BasicPartition> gens = new ArrayList<BasicPartition>();
    //gens.add(one(4));
    
    //gens.add(demeo0);
    //gens.add(demeo1);
    //gens.add(demeo2);
    //gens.add(demeo3);
    
    //gens.add(snow130);
    //gens.add(snow131);
    //gens.add(snow132);
    
    //gens.add(billy0);
    //gens.add(billy1);
    //gens.add(billy2);
    
    //gens.add(jb72);
    //gens.add(jb70);
    //gens.add(jb71);
    

    //gens.add(jbdw0);
    //gens.add(jbdw1);
    //gens.add(jbdw2);
    //gens.add(jbdw3);
    

    //gens.add(vs0);
    //gens.add(vs1);
    //gens.add(vs2);

    //gens.add(dw0);
   // gens.add(dw1);
   // gens.add(dw2);
   // gens.add(dw3);
    
    //gens.add(swh0);
    //gens.add(swh1);
    //gens.add(swh2);
    //gens.add(swh3);
    //gens.add(swh4);
    
    gens.add(L10);
    gens.add(L11);
    gens.add(L12);

    
    System.out.println("gens: " + gens);
    
    /*
    List<Partition> closure = closureAt(gens);
    for (Partition par : closure) {
      System.out.print(par);
      if (par.equals(gens.get(0))) System.out.println(" **");
      else if (gens.contains(par)) System.out.println(" *");
      else System.out.println("");
    }
    System.out.println("closure size: " + closure.size());
    */
    
    //List<Partition> pars = new ArrayList<Partition>();
    //Partition par = new BasicPartition(new int[] {-2, 0, -1});
    //pars.add(zero(3));
    //pars.add(par);
    
    //pars.add(demeo0);
    //pars.add(demeo1);
    //pars.add(demeo2);
    //pars.add(demeo3);
    
    List<Partition> pars = new ArrayList<Partition>();
    for (BasicPartition gen : gens) {
      pars.add((Partition)gen);
    }
    
    //long t = System.currentTimeMillis();
    //List<IntArray> lst = unaryClone(pars);
    //System.out.println("time: " + (System.currentTimeMillis() - t));
    //System.out.println("functs: (" + lst.size() + ")\n" + lst);
    //String f = "f_";
    //final int size = gens.get(0).size();
    //List<Operation> ops = new ArrayList<Operation>(lst.size());
    //for (int i = 0; i < lst.size(); i++) {
      //ops.add(Operations.makeIntOperation(f + i, 1, size, lst.get(i).getArray()));
    //}
    SmallAlgebra alg = unaryCloneAlgebra(pars);
    System.out.println("|Con(A)| = " + alg.con().universe().size());
    for (Partition par : alg.con().universe()) {
      System.out.println(par);
    }
    
    //binaryClone(pars, null);
    SmallAlgebra alg2 = binaryCloneAlgebra(pars);
    try {
      org.uacalc.io.AlgebraIO.writeAlgebraFile(alg2, "/tmp/alg2.ua");
    }
    catch (Exception e) { e.printStackTrace(); }
    
    Set<IntArray> unaryFns = unaryClone(pars);
    System.out.println("|Pol_1| = " + unaryFns.size());
    System.out.println("unaries: " + unaryFns);
    for (IntArray f : unaryFns) {
      boolean idem = f.isIdempotent();
      System.out.println(f + (idem ? " *" : ""));
    }
    
    //isHereditary(pars);
    
    //BasicPartition par0 = new BasicPartition(new int[] {-2, 0, -1, -1});
    //BasicPartition par1 = new BasicPartition(new int[] {-1, -2, 1, -1});
    //int ans = permutabilityLevel(2, 3, par0, par1);
    //System.out.println("level is " + ans);
  }

}

