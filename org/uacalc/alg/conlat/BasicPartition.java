/* BasicPartition.java 2001/06/04 Ralph Freese */

package org.uacalc.alg.conlat;

import org.uacalc.util.IntArray;


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
    final int size = par0.size();
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
    final int n = par0.size();
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
   * 
   * 
   * @param pars
   * @return
   */
  public static List<Partition> closureAt(List<BasicPartition> pars) {
    final BasicPartition alpha = pars.get(0);
    final int n = alpha.size();
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
    Partition firstProj = zero(alpha.size()).inducedPartition(univ, 0);
    Partition secondProj = zero(alpha.size()).inducedPartition(univ, 1);
    System.out.println("firstProj: " + firstProj);
    System.out.println("secondProj: " + secondProj);
    Partition alpha0 = alpha.inducedPartition(univ, 0);
    System.out.println("alpha: " + alpha);
    System.out.println("alpha0: " + alpha0);
    Set<Partition> hs = new HashSet<Partition>();
    hs.add(firstProj);
    hs.add(zero(alpha.size()).inducedPartition(univ, 1)); //second proj kernel
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
    final int n = size();
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
    
    List<BasicPartition> gens = new ArrayList<BasicPartition>();
    //gens.add(one(4));
    gens.add(rfw0);
    gens.add(rfw1);
    gens.add(rfw2);
    gens.add(rfw3);
    System.out.println("gens: " + gens);
    List<Partition> closure = closureAt(gens);
    for (Partition par : closure) {
      System.out.print(par);
      if (par.equals(gens.get(0))) System.out.println(" **");
      else if (gens.contains(par)) System.out.println(" *");
      else System.out.println("");
    }
    System.out.println("closure size: " + closure.size());
    
    
    
    //BasicPartition par0 = new BasicPartition(new int[] {-2, 0, -1, -1});
    //BasicPartition par1 = new BasicPartition(new int[] {-1, -2, 1, -1});
    //int ans = permutabilityLevel(2, 3, par0, par1);
    //System.out.println("level is " + ans);
  }

}

