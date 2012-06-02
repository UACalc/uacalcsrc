/* ProductAlgebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.*;
import java.util.logging.*;
import java.math.BigInteger;
import org.uacalc.util.*;

import org.uacalc.alg.SmallAlgebra.AlgebraType;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.Operations;
import org.uacalc.alg.sublat.*;
import org.uacalc.io.AlgebraIO;

/**
 * This class represents the direct product of <tt>SmallAlgebra</tt>s.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class ProductAlgebra extends GeneralAlgebra implements SmallAlgebra {

  static Logger logger = Logger.getLogger("org.uacalc.alg.ProductAlgebra");
  static {
    logger.setLevel(Level.FINER);
  }

  protected List<SmallAlgebra> algebras;
  protected int[] sizes;
  protected int numberOfProducts;

  protected ProductAlgebra() {
    super(null);
  }

  protected ProductAlgebra(String name) {
    super(name);
  }

  public ProductAlgebra(List<SmallAlgebra> algs) {
    this("", algs);
  }

  /**
   * Construct the direct product of a List of SmallAlgebra's.
   */
  public ProductAlgebra(String name, List<SmallAlgebra> algs) {
    super(name);
    algebras = algs;
    numberOfProducts = algs.size();
    sizes = new int[numberOfProducts];
    int k = 0;
    for (Iterator<SmallAlgebra> it = algs.iterator(); it.hasNext(); k++) {
      sizes[k] = ((SmallAlgebra)it.next()).cardinality();
    }
    size = calcCard(sizes);
    universe = makeCartesianProduct(algs);
    makeOperations();
  }

  protected void makeOperations() {
    final int k = algebras.get(0).operations().size();
    List<Operation> ops = new ArrayList<Operation>(k);
    for (int i = 0; i < k; i++) {
      final int arity = 
           ((Operation)((Algebra)algebras.get(0)).operations().get(i)).arity();
      final List<Operation> opList = new ArrayList<Operation>(numberOfProducts);
      for (int j = 0; j < numberOfProducts; j++) {
        opList.add(((Algebra)algebras.get(j)).operations().get(i));
      }
      final int[][] argsExpanded = new int[arity][numberOfProducts];
      final int[] arg = new int[arity];
      Operation op = new AbstractOperation(
                              ((Operation)opList.get(0)).symbol(), size) {
          // this is not tested yet
          public Object valueAt(List args) {
            List ans = new ArrayList();
            for (int i = 0; i < opList.size(); i++) {
              Operation opx = (Operation)opList.get(i);
              List arg = new ArrayList();
              for (Iterator it = args.iterator(); it.hasNext(); ) {
                arg.add(((List)it.next()).get(i));
              }
              ans.add(opx.valueAt(arg));
            }
            return ans;
          }
          //  **********  Note  ***********
          //  This could be sped up not creating ansArr and instead do
          //  horner inline.
          //  *****************************
          //public int intValueAt(int[] args) {
          //  int[] ansArr = new int[numberOfProducts];
          //  for (int i = 0; i < opList.size(); i++) {
          //    final Operation opx = (Operation)opList.get(i);
          //    final int[] arg = new int[opx.arity()];
          //    for (int j = 0; j < arg.length; j++) {
          //      arg[j] = args[i];
          //    }
          //    ansArr[i] = opx.intValueAt(arg);
          //  }
          //  return Horner.horner(ansArr, sizes);
          //}
          //
          //  ********************************
          //  **** The inline horner version ***
          //  ********************************
          //final int[][] argsExpandedx = new int[arity()][];
          Operation tableOp = null;
          public void makeTable() {
            int h = 1;
            for (int i = 0; i < arity; i++) {
              h = h * size;
            }
            int[] values = new int[h];
            for (int i = 0; i < h; i++) {
              values[i] = intValueAt(Horner.hornerInv(i, size, arity));
            }
            tableOp = Operations.makeIntOperation(symbol(), size, values);
          }
          public int intValueAt(int[] args) {
            if (tableOp != null) return tableOp.intValueAt(args);
            int ans = 0;
            int [] unargs = new int[args.length];
            for (int i = 0; i < args.length; i++) {
              Horner.hornerInv(args[i], sizes, argsExpanded[i]);
              unargs[i] = Horner.horner(argsExpanded[i], sizes);
            }
            
            //System.out.println("\nargs: " + ArrayString.toString(args));
            //System.out.println("sizes: " + ArrayString.toString(sizes));
            //System.out.println("argsExpanded: " + ArrayString.toString(argsExpanded));
            //System.out.println("unargs: " + ArrayString.toString(unargs));
            //for (int i = 0; i < argsExpanded.length; i++) {
            //  argsExpanded[i] = Horner.reverseArray(argsExpanded[i]);
            //}
            
            
            for (int i = numberOfProducts - 1; i >= 0; i--) {
              final Operation opx = (Operation)opList.get(i);
              for (int j = 0; j < arg.length; j++) {
                arg[j] = argsExpanded[j][i];
              }
              final int s = sizes[i];
              //System.out.println("\narg: " + ArrayString.toString(arg));
              //System.out.println("ans = " + ans + ", s = " + s);
              
              ans = s * ans + opx.intValueAt(arg);
              //System.out.println("Now ans = " + ans);
            }
            return ans;
          }
      };
      ops.add(op);
    }
    setOperations(ops);
  }

  public void makeOperationTables() {
    for (Iterator<Operation> it = operations().iterator(); it.hasNext(); ) {
      it.next().makeTable();
    }
  }

  public List<SmallAlgebra> factors() {
    return algebras;
  }
  
  public List<SmallAlgebra> parents() {
    return algebras;
  }

  public SmallAlgebra projection(int k) {
    return (SmallAlgebra)algebras.get(k);
  }

  public BasicPartition projectionKernel(int k) {
    final int projectionSize = sizes[k];
    final int blockSize = cardinality() / projectionSize;
    final int[] arr = new int[cardinality()];
    final int[] tmp = new int[cardinality()];
    for (int i = 0; i < projectionSize; i++) {
    }
    // finish this later.
    return null;
  }

  public CongruenceLattice con() {
    if (con == null) con = new CongruenceLattice(this);
    return con;
  }

  public SubalgebraLattice sub() {
    if (sub == null) sub = new SubalgebraLattice(this);
    return sub;
  }

// fix these!!!!!!!!!!!!!!
  public int elementIndex(Object obj) {
    if (obj instanceof IntArray) {
      return Horner.horner(((IntArray)obj).getArray(), sizes);
    }
    throw new UnsupportedOperationException();
  }

  public Object getElement(int index) {
    return Horner.hornerInv(index, sizes);
  }

  // do something ??
  public List getUniverseList() { return new ArrayList<IntArray>(universe()); }
  public Map getUniverseOrder() { return null; }

  /**
   * Returns the carrier set of the product of the specified <code>List algs</code> of Objects as a <code>Set</code>.
   * This set does not support to be modified in any way (by adding or deleting or otherwise modifying its members). 
   * This applies in particular to the elements returned by the <code>iterator()</code>.
   * Modifying them does not have any effect on the elements in the set, as they are not stored explicitly.
   * <p>
   * For the <code>size</code> of the set, see the {@link #calcCard} method.
   * <p>
   * The <code>contains</code> method returns true iff applied to a <code>List</code> of objects that are successively 
   * contained in the algebras in <code>algs</code>.
   * <p>
   * The <code>iterator()</code> returns the tuples in the product as <code>ArrayList</code>s in lexicographic order (reading words from left to right).
   * This means the right-most (last, largest) index is incremented fastest. 
   * The order of the algebras in the given <code>List algs</code> determines the order of the entries in the tuples. 
   * This iterator supports only forward iteration and has got no <code>remove</code> method.
   * Furthermore, there is no possibility to change the elements in the <code>Set</code> by modifying the tuples returned by the iterator.
   * Putting this differently, the iterator only provides the "enumeration functionality", but not the complete "element access functionality".
   * It is possible to iterate past the end of the set, i.e., after <code>hasNext</code> has returned <code>false</code>.
   * In this case the iterator starts again with the first tuple.
   @param algs <code>List</code> of <code>Algebra</code>s being factors of the product.
   @return a <code>Set</code> of all tuples (<code>ArrayList</code>s) in the product.
   @exception UnsupportedOperationException if the <code>iterator()</code> is used and one of the objects in the <code>List algs</code> is not an <code>Algebra</code>.
   @exception UnsupportedOperationException if the <code>Set</code> is modified by one of the methods <code>add, addAll, clear, remove, removeAll, retainAll</code>.
   */
  protected Set makeCartesianProduct(final List algs) {
    return new AbstractSet() {
        public int size() { return size; }
        public boolean contains(Object obj) {
          if (!(obj instanceof List)) return false;
          Iterator itAlgs = algs.iterator();
          for (Iterator it = ((List)obj).iterator(); it.hasNext() ; ) {
            if (!itAlgs.hasNext()) return false;
            if (!((Algebra)itAlgs.next()).universe().contains(it.next())) {
              return false;
            }
          }
          return true;
        }
        // should check if each has an iterator and make one
        /* returns an iterator over the cartesian product, returning tuples as ArrayLists, 
           whose entries are in the order of the given List algs. The last index is incremented fastest */
        /**
         * Iterator for products of <code>SmallAlgebra</code>s as inner class returning the tuples in the product as <code>ArrayList</code>s in lexicographic order (reading words from left to right).
         * This means the right-most (last, largest) index is incremented fastest. 
         * The order of the algebras in the given <code>List algs</code> determines the order of the entries in the tuples. 
         * <p>
         * This iterator supports only forward iteration and has got no <code>remove</code> method.
         * <p>
         * It is possible to iterate past the end of the set, i.e. after <code>hasNext</code> has returned <code>false</code>.
         * In this case the iterator cyclically starts again with the first tuple.
         */
        class ProductIterator implements Iterator
        {
          final private int numberOfFactors = algs.size();
          private Iterator[] itArray; // as many iterators as factors
          //ArrayList tuple; //a list of objects (the iterators may return any kind of object in fact)
          private Object[] prodTuple;
          private boolean hasMoreTuples;
          private ProductIterator() {
            itArray = new Iterator[numberOfFactors]; // as many iterators as factors
            prodTuple = new Object[numberOfFactors]; // tuple to contain the values to be returned in each iteration
            hasMoreTuples = false;
            Iterator itAlgs = algs.iterator();
            int k = 0;
            for (ListIterator iterAlgs = algs.listIterator(numberOfFactors); iterAlgs.hasPrevious(); k++)
            {
              Object curObj = iterAlgs.previous();
              // the following could be done without checking, then probably risking a ClassCastException?
              if (!(curObj instanceof Algebra)) throw new UnsupportedOperationException("There is a member in the list that is not an Algebra.");
              Algebra curAlg = (Algebra) curObj;
              itArray[k] = curAlg.iterator();   // this may throw an unsupported operation exception 
                                                // if one of the factors does not have an iterator
              if (itArray[k].hasNext())   
              {
                prodTuple[k] = (itArray[k]).next();
              }
              else
                return ;   // if one of the algebras in the List is empty, hasMoreTuples stays false.
            }
            hasMoreTuples = true; // if there are no algebras (empty product containing the empty tuple) or
                                  // all algebras are nonempty, hasMoreTuples is set to true.
          }
          
          public boolean hasNext() {
            return hasMoreTuples;
          }

          public Object next() {
            // return the prepared tuple as an ArrayList:
            ArrayList returnTuple = new ArrayList(numberOfFactors); // make a copy of the current tuple since this will
                                                                    // be reused for the following method call
                                                                    // This implies that the iterator cannot provide the
                                                                    // "element access" functionality (write access), it
                                                                    // can only traverse the tuples in the product.
            for (int l = numberOfFactors; l > 0; )
            {
              returnTuple.add(prodTuple[--l]);
            }
            // prepare a new tuple for the next iteration (store it inside prodTuple)
            int k = 0; // find the least index k such that there is still an element.
            for (ListIterator iterAlgs = algs.listIterator(numberOfFactors); iterAlgs.hasPrevious() && !itArray[k].hasNext(); k++)
            {
              // if the k-th-iterator does not have any more elements, reinitialise it
              itArray[k] = ((Algebra) iterAlgs.previous()).iterator();   // this may throw an unsupported operation exception 
                                                                         // if one of the factors does not have an iterator
              prodTuple[k] = (itArray[k]).next();
            }
            if (k < numberOfFactors) // the "slowest running" iterator has not yet been exhausted.
              prodTuple[k] = (itArray[k]).next();
            else // All iterators have been exhausted, i.e., there are no more new tuples. 
                 // This also means that all iterators have been reinitialised, so we can 
                 // start again with the first tuple. This is useful when running "out of
                 // bounds" by looping just with next() and not checking for hasNext().
              hasMoreTuples = false;
            return returnTuple; 
          }

          public void remove() {
            throw new UnsupportedOperationException("Cannot remove tuples from the universe of a product since these are implicitly stored.");
          }
        }
        public Iterator iterator() {
          return new ProductIterator();
        }
      };
  }

  /**
   * The subalgebra generated by elems given as arrays of int's.
   */
  public Subalgebra Sg(List<IntArray> elems) {
    final int[] gens = new int[elems.size()];
    for (int i = 0; i < gens.length; i++) {
      gens[i] = elementIndex(elems.get(i));
    }
    return sub().Sg(gens);
  }

  public List sgClose(List elems) {
    List ans = new ArrayList();
    return ans;
  }
  
  /**
   * Returns the product cardinality if it is an int; otherwise
   * it returns -1.
   * 
   * @param sizes
   * @return
   */
  public static int calcCard(int[] sizes) {
    final BigInteger max = BigInteger.valueOf((long)Integer.MAX_VALUE); 
    BigInteger v = BigInteger.ONE;
    boolean infinityHasOccurred = false;
    for (int i = 0; i < sizes.length; i++) {
      if (sizes[i] == 0) return 0;           // if there is at least one empty factor, the product is empty.
      // if we reach this point, no empty factors have occurred so far.  
      v = v.multiply(BigInteger.valueOf((long)sizes[i]));
      if (v.compareTo(max) > 0 || sizes[i] < 0) {
        //return -1; // cannot return -1 immediately, since a later factor might be empty (sizes[i]==0)
        infinityHasOccurred = true;      // set a mark that one factor was "computer infinite"
        // v can be reset to 0 to minimise computation efforts, because v != 0 and  
        // now it is only interesting whether there's another 0 in the array that might trigger the above "return 0".
        v = BigInteger.ZERO;  
      }
    }
    // if we reach this point, there are no empty factors in the product (possibly because there are no factors at all).  
    if (infinityHasOccurred) 
      return -1;
    else // none of the factors was computer infinite, nor did we run over the largest int.
      return v.intValue();
  }
  
  public void convertToDefaultValueOps() {
    throw new UnsupportedOperationException("Only for basic algebras"); 
  }

  public AlgebraType algebraType() {
    return AlgebraType.PRODUCT;
  }

  public static void main(String[] args) throws java.io.IOException,
                                   org.uacalc.io.BadAlgebraFileException {
    if (args.length == 0) {
      SmallAlgebra alg12 = org.uacalc.io.AlgebraIO.readAlgebraFile("/Users/ralph/Documents/algebras/A12.ua");
      SmallAlgebra alg22 = org.uacalc.io.AlgebraIO.readAlgebraFile("/Users/ralph/Documents/algebras/A12.ua");
      List<SmallAlgebra> lst = new ArrayList<SmallAlgebra>(2);
      lst.add(alg12);
      lst.add(alg22);
      SmallAlgebra alg2 = new ProductAlgebra("A2", lst);
      AlgebraIO.writeAlgebraFile(alg2, "/Users/ralph/Documents/algebras/A2.ua");
      return;
    }
    
    System.out.println("reading " + args[0]);
    Algebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    ArrayList lst = new ArrayList();
    lst.add(alg);
    lst.add(alg);
//    lst.add(alg);
//    lst.add(alg);
    System.out.println("prod of " + lst.size() + " algebras");
    SmallAlgebra alg2 = new ProductAlgebra(lst);

    org.uacalc.io.AlgebraWriter writer 
         = new org.uacalc.io.AlgebraWriter((SmallAlgebra)alg2, "/tmp/goo.xml");
    writer.writeAlgebraXML();
    

    SmallAlgebra alg3 = new ProductAlgebra(lst);
    alg2.makeOperationTables();
    TypeFinder tf = new TypeFinder(alg2);
    long t = System.currentTimeMillis();
    int k = alg2.con().joinIrreducibles().size();
    t = System.currentTimeMillis() - t;
    System.out.println("number of jis is " + k);
    System.out.println("to find the jis it took " + t);
    t = System.currentTimeMillis();
    //System.out.println("size " + alg.con().universe().size());
    HashSet types = tf.findTypeSet();
    t = System.currentTimeMillis() - t;
    System.out.println("type set = " + types);
    System.out.println("it took " + t);
    System.out.println("--- Without tables ---");
    tf = new TypeFinder(alg3);
    t = System.currentTimeMillis();
    k = alg3.con().joinIrreducibles().size();
    t = System.currentTimeMillis() - t;
    System.out.println("number of jis is " + k);
    System.out.println("to find the jis it took " + t);
    t = System.currentTimeMillis();
    //System.out.println("size " + alg.con().universe().size());
    types = tf.findTypeSet();
    t = System.currentTimeMillis() - t;
    System.out.println("type set = " + types);
    System.out.println("it took " + t);


  }

}


