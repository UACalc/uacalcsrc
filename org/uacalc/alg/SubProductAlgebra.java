/* ProductAlgebra.java (c) 2001/07/22  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.*;
import org.uacalc.util.*;
import org.uacalc.terms.*;
import org.uacalc.ui.tm.ProgressReport;

import org.uacalc.alg.SmallAlgebra.AlgebraType;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.Operations;
import org.uacalc.alg.sublat.*;
import org.uacalc.lat.*;

/**
 * This class represents a subalgebra of a direct product 
 * of <tt>SmallAlgebra</tt>s. It allows one to construct such an
 * algebra even though the direct product may be too big to be a
 * <tt>SmallAlgebra</tt>.
 *
 * @author Ralph Freese
 *
 * @version $Id$
 */
public class SubProductAlgebra extends GeneralAlgebra implements SmallAlgebra {

  protected BigProductAlgebra productAlgebra;
  protected List<IntArray> gens; // a list of IntArray's
  protected List<IntArray> univ; // a list of IntArray's
  protected boolean thinGenerators;
  protected boolean decompose;

  // a map from IntArray's of elements of the 
  protected Map<IntArray,Integer> univHashMap; 
                                 // univ to Integers (the index).
  
  protected Term[] terms; // term[i] is a term for the ith element
  
  protected Map<IntArray,Term> termMap;

  protected List<Variable> variables;
  
  protected Map<Variable,IntArray> varsMap;

  protected SubProductAlgebra() {
    super(null);
  }

  protected SubProductAlgebra(String name) {
    super(name);
  }

  /**
   * Construct the direct product of a List of SmallAlgebra's.
   * gens is a list of IntArray's.
   */
  public SubProductAlgebra(String name, BigProductAlgebra prod, List<IntArray> gens) {
    this(name, prod, gens, false);
  }

  public SubProductAlgebra(String name, BigProductAlgebra prod, 
      List<IntArray> gens, boolean findTerms) {
    this(name, prod, gens, findTerms, null);
    
  }
  
  /**
   * Construct the direct product of a List of SmallAlgebra's.
   * gens is a list of IntArray's.
   */
  public SubProductAlgebra(String name, BigProductAlgebra prod, 
      List<IntArray> gens, boolean findTerms, ProgressReport report) {
    this(name, prod, gens, findTerms, false, report);
  }
  
  /**
   * Construct the direct product of a List of SmallAlgebra's.
   * gens is a list of IntArray's.
   */
  public SubProductAlgebra(String name, BigProductAlgebra prod, 
                           List<IntArray> gens, boolean findTerms, 
                           boolean includeConstants, ProgressReport report) {
    // TODO: use includeConstants
    super(name);
    productAlgebra = prod;
    int rootSize = prod.rootFactors().get(0).cardinality();
    
    if (includeConstants && prod.isPower()) {
      final int factors = prod.getNumberOfFactors();
      for (int i = 0; i < rootSize; i++) {
        final int[] arr = new int[factors];
        for (int j = 0; j < factors; j++) {
          arr[j] = i;
        }
        gens.add(new IntArray(arr));
      }
    }
    
    // some gyrations to eliminate duplicates but keep the order the same.
    HashSet<IntArray> hs = new HashSet<IntArray>(gens.size());
    List<IntArray> gens2 = new ArrayList<IntArray>(gens.size());
    for (IntArray elem : gens) {
      if (!hs.contains(elem)) {
        hs.add(elem);
        gens2.add(elem);
      }
    }
    gens = gens2;
    this.gens = gens2;
    
    if (findTerms) {
      termMap = setupGensToVarsMap(gens);
      univ = productAlgebra.sgClose(gens, termMap, null, report);
      terms = new Term[univ.size()];
      for (int i = 0; i < univ.size(); i++) {
        terms[i] = termMap.get(univ.get(i));
      }
    }
    else univ = productAlgebra.sgClose(gens);
    size = univ.size();
    univHashMap = new HashMap<IntArray,Integer>(size);
    int k = 0;
    for (Iterator<IntArray> it = univ.iterator(); it.hasNext(); k++) {
      univHashMap.put(it.next(), new Integer(k));
    }
    universe = new HashSet(univ);
    makeOperations();
  }

  /**
   * Construct a SubProductAlgebra when the gens and univ are already
   * given. Useful for reading back from a file without calculating 
   * the universe again.
  */
  public SubProductAlgebra(String name, BigProductAlgebra prod, 
                           List<IntArray> gens, List<IntArray> univList) {
    super(name);
    setup(prod, gens, univList);
  }
  
  public SubProductAlgebra(SmallAlgebra alg, int pow, Map<IntArray,Partition> rels) {
    this("", alg, pow, rels);
  }
  
  public SubProductAlgebra(String name, SmallAlgebra alg, int pow, Map<IntArray,Partition> rels) {
    super(name);
    productAlgebra = new BigProductAlgebra(alg, pow);
    final List<IntArray> univ = universeFromRelations(alg.cardinality(), pow, rels);
    setup(new BigProductAlgebra(alg, pow), univ, univ);  // make gens = univ
  }
  
  public static List<IntArray> transpose(List<IntArray> lst) {
    final int k = lst.size(); // if k = 0 we should throw an IllegalArguementExcpetion
    final int n = lst.get(0).universeSize();
    List<IntArray> transpose = new ArrayList<IntArray>(n);
    for (int i = 0 ; i < n ; i++) {
      final int[] ithProj = new int[k];
      for (int j = 0 ; j < k ; j++) {
        ithProj[j] = lst.get(j).get(i);
      }
      transpose.add(new IntArray(ithProj));
    }
    return transpose;
  }
  
  public void setThinGenerators(boolean v) { thinGenerators = v; }
  
  /**
   * 
   * @param v
   * @return   true if the generators have been thinned
   */
  public boolean getThinGenerators(boolean v) { return thinGenerators; }
  
  public void setDecompose(boolean v) { decompose = v; }
  
  /**
   * 
   * @param v
   * @return   true if the generators have been decomposed and thinned
   */
  public boolean getDecompose() { return decompose; }

  public List<IntArray> thinGenerators() {
    List<IntArray> projs = transpose(gens);
    //System.out.println("gens xxx = " + gens);
    //System.out.println("projs xxx = " + projs);
    final Map<IntArray,SmallAlgebra> projMap = new HashMap<IntArray,SmallAlgebra>();
    int k = 0;
    for (IntArray ia : projs) {
      projMap.put(ia, productAlgebra.projection(k++));
    }
    List<IntArray> thinnedProjs = OrderedSets.maximals(projs, new Order<IntArray>() {
      public boolean leq(IntArray a, IntArray b) {
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        if (SubalgebraLattice.extendToHomomorphism(b.getArray(), a.getArray(), 
                                  projMap.get(b), projMap.get(a)) != null) {
          //System.out.println("returning true");
          return true;
        }
        return false;
      }
    });
    return transpose(thinnedProjs);
  }
  
  private void setup(BigProductAlgebra prod, List<IntArray> gens, 
                                             List<IntArray> univList) {
    productAlgebra = prod;
    this.gens = gens;
    univ = univList;
    size = univ.size();
    univHashMap = new HashMap<IntArray,Integer>(size);
    int k = 0;
    for (Iterator<IntArray> it = univ.iterator(); it.hasNext(); k++) {
      univHashMap.put(it.next(), new Integer(k));
    }
    universe = new HashSet(univ);
    makeOperations();
  }

  protected void makeOperations() {
    final int k = productAlgebra.operations().size();
    List<Operation> ops = new ArrayList<Operation>(k);
    //operations = new ArrayList(k);
    for (int i = 0; i < k; i++) {
      final Operation opx = (Operation)productAlgebra.operations().get(i);
      final int arity = opx.arity();
      Operation op = new AbstractOperation(opx.symbol(), size) {
          // this is not tested yet
          public Object valueAt(List args) {
            return opx.valueAt(args);
          }
          Operation tableOp = null;
          public void makeTable() {
            int h = 1;
            for (int i = 0; i < arity; i++) {
              h = h * size;
            }
            valueTable = new int[h];
            for (int i = 0; i < h; i++) {
              valueTable[i] = intValueAt(Horner.hornerInv(i, size, arity));
            }
            tableOp = Operations.makeIntOperation(symbol(), size, valueTable);
          }
          public int[] getTable() {
            if (tableOp == null) return null;
            return tableOp.getTable();
          }
          public int[] getTable(boolean makeTable) {
            if (makeTable && tableOp == null) makeTable();
            return getTable();
          }
          public int intValueAt(final int[] args) {
            if (tableOp != null) return tableOp.intValueAt(args);
            final List lst = new ArrayList(arity);
            for (int i = 0; i < arity; i++) {
              lst.add(getElement(args[i]));
            }
            return elementIndex(opx.valueAt(lst));
          }
      };
      ops.add(op);
    }
    setOperations(ops);
  }

  public void makeOperationTables() {
    for (Operation op : operations()) {
      final int memReserve = 1048576;
      byte[] buf = new byte[memReserve];
      try {
        op.makeTable();
      }
      catch (OutOfMemoryError mem) {
        buf = null;
        System.out.println("not enough memory to make the op table");
      }
      finally { buf = null; }
    }
    //for (Iterator it = operations().iterator(); it.hasNext(); ) {
    //  ((Operation)it.next()).makeTable();
    //}
  }
  
  protected Map<IntArray,Term> setupGensToVarsMap(List<IntArray> gens) {
    varsMap = new HashMap<Variable,IntArray>(gens.size());
    Map<IntArray,Term> termMap = new HashMap<IntArray,Term>();
    if (gens.size() == 1) {
      termMap.put(gens.get(0), Variable.x);
      varsMap.put(Variable.x, gens.get(0));
    }
    if (gens.size() == 2) {
      termMap.put(gens.get(0), Variable.x);
      termMap.put(gens.get(1), Variable.y);
      varsMap.put(Variable.x, gens.get(0));
      varsMap.put(Variable.y, gens.get(1));
    }
    if (gens.size() == 3) {
      termMap.put(gens.get(0), Variable.x);
      termMap.put(gens.get(1), Variable.y);
      termMap.put(gens.get(2), Variable.z);
      varsMap.put(Variable.x, gens.get(0));
      varsMap.put(Variable.y, gens.get(1));
      varsMap.put(Variable.z, gens.get(2));
    }
    int k = 0;
    if (gens.size() > 3) {
      for (Iterator<IntArray> it = gens.iterator(); it.hasNext(); k++) {
        IntArray elt = it.next();
        Variable var = new VariableImp("x_" + k);
        termMap.put(elt, var);
        varsMap.put(var, elt);
      }
    }
    return termMap;
  }
  
  public Map<Variable,IntArray> getVariableToGeneratorMap() {
    return varsMap;
  }

  public Term[] getTerms() {
    return terms;
  }
  
  /**
   * The variables corresponding to the generator in the list
   * of terms.
   * 
   * @return
   */
  public List<Variable> getVariables() {
    if (variables != null) return variables;
    if (terms == null) return null;
    if (gens == null) return null;
    List<Variable> ans = new ArrayList<Variable>();
    for (int i = 0; i < gens.size(); i++) {
      if (terms[i].isaVariable()) ans.add((Variable)terms[i]);
    }
    this.variables = ans;
    return ans;
  }

  /**
   * Get the term associated with an element.
   */
  public Term getTerm(IntArray elt) {
   if (getTerms() == null) return null;
   return getTerms()[getUniverseOrder().get(elt).intValue()];
  }

  public Map<IntArray,Term> getTermMap() {
    return termMap;
  }

  public BigProductAlgebra getProductAlgebra() {
    return productAlgebra;
  }

  public BigProductAlgebra superAlgebra() {
    return productAlgebra;
  }

  public List<IntArray> generators() {
    return gens;
  }

  public List<IntArray> getUniverseList() {
    return univ;
  }

  public Map<IntArray,Integer> getUniverseOrder() {
    return univHashMap;
  }


  public CongruenceLattice con() {
    if (con == null) con = new CongruenceLattice(this);
    return con;
  }

  public SubalgebraLattice sub() {
    if (sub == null) sub = new SubalgebraLattice(this);
    return sub;
  }

  /**
   * Get the element associate with a term. This is linear
   * in the size of the subalgebra.
   * 
   * @param t the term
   * @return  the element as an IntArray
   */
  public IntArray getElementFromTerm(Term t) {
    final Term[] terms = getTerms();
    final int size = terms.length;
    int i = 0;
    for ( ; i < size; i++) {
      if (terms[i].equals(t)) break;
    }
    if (i < size) return (IntArray)getElement(i);
    return null;
  }


  public int elementIndex(Object obj) {
    IntArray elem = (IntArray)obj;
    return univHashMap.get(elem).intValue();
  }

  public Object getElement(int index) {
    return univ.get(index);
  }

  public BasicPartition projectionKernel(final int k) {
    final int n = cardinality();
    BasicPartition kern = BasicPartition.zero(n);
    for (int i = 0; i < n; i++) {
      for (int j = i+1; j < n; j++) {
        if (((IntArray)getElement(i)).get(k) 
                                  == ((IntArray)getElement(j)).get(k)) {
          final int r = kern.representative(i);
          final int s = kern.representative(j);
          if (r != s) kern.joinBlocks(r, s);
        }
      }
    }
    return kern;
  }
  
  /**
   * This gives all <tt>pow</tt> tuples with entries 0 to n-1
   * such that if (i,j) maps to theta, then the ith element 
   * is theta related to the jth element. So is A is an algebra
   * of size n, this gives the subuniverse where the coordinates
   * are related by the partitions (or congruences) of <tt>rels</tt>. 
   * 
   * @param n       the size of the projections
   * @param pow     the power
   * @param rels    a map from pairs of coords to partition on n
   * @return
   */
  public static List<IntArray> universeFromRelations(int n, int pow, Map<IntArray,Partition> rels) {
    //if (rels.size() == 0) throw new IllegalArgumentException();
    int[] arr = new int[pow];
    for (int i = 0; i < pow; i++) arr[i] = 0;
    ArrayIncrementor inc = SequenceGenerator.sequenceIncrementor(arr, n - 1);
    List<IntArray> ans = new ArrayList<IntArray>();
    ans.add(new IntArray(Arrays.copyOf(arr, pow)));
    while (inc.increment()) {
      //System.out.println("arr: " + Arrays.toString(arr));
      if (respects(arr, rels)) ans.add(new IntArray(Arrays.copyOf(arr, pow)));
    }
    return ans;
  }
  
  private static boolean respects(int[] arr, Map<IntArray,Partition> rels) {
    for (IntArray pair : rels.keySet()) {
      Partition par = rels.get(pair);
      //System.out.println("pair: " + pair);
      //System.out.println("par: " + par);
      if (!par.isRelated(arr[pair.get(0)], arr[pair.get(1)])) return false;
    }
    //System.out.println("returning true");
    return true;
  }
  
  public void convertToDefaultValueOps() {
    throw new UnsupportedOperationException("Only for basic algebras"); 
  }
  
  public AlgebraType algebraType() {
    return AlgebraType.SUBPRODUCT;
  }

  public static void main(String[] args) throws java.io.IOException,
                                   org.uacalc.io.BadAlgebraFileException {
    SmallAlgebra polin = org.uacalc.io.AlgebraIO.readAlgebraFile("/home/ralph/Java/Algebra/algebras/polin.ua");
    Partition theta = polin.con().Cg(0, 2);
    Map<IntArray,Partition> map = new HashMap<IntArray,Partition>();
    map.put(new IntArray(new int[] {0, 1}), theta);
    map.put(new IntArray(new int[] {1, 2}), theta);
    SmallAlgebra subpow = new SubProductAlgebra(polin, 3, map);
    System.out.println("univ: " + subpow.universe());
    System.out.println("con size: " + subpow.con().cardinality());
    org.uacalc.io.AlgebraIO.writeAlgebraFile(subpow, "/home/ralph/Java/Algebra/algebras/subpolin3.ua");
    
    if (args.length == 0) return;
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


