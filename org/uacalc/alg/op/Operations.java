/* Operations.java (c) 2001/07/27  Ralph Freese and Emil Kiss */

package org.uacalc.alg.op;

import java.util.*;
import java.util.logging.*;
import javax.script.*;
import org.uacalc.util.*;
import org.uacalc.alg.conlat.BasicPartition;

/**
 * This is a factory class with static methods to make Operations.
 */
public class Operations {

  /*
  static Logger logger = Logger.getLogger("org.uacalc.alg.Operations");
  static {
    logger.setLevel(Level.FINER);
  }
  */


  /**
   * This class can never be instantiated.
   */
  private Operations() {}

  /**
   * This tests if an operation commutes with a unary operation. It can
   * be used to test if an operation is a endomorphism (or automorphism).
   */
  public static boolean commutes(final Operation unaryOp, final Operation op) {
    final int setSize = op.getSetSize();
    final int[] arr = new int[op.arity()];
    final int[] imageArr = new int[op.arity()];
    final int[] unaryArg = new int[1];
    int v = unaryOp.intValueAt(unaryArg);
    for (int i = 0 ; i < op.arity(); i++) {
      imageArr[i] = v; // v is unaryOp([0])
    }
    unaryArg[0] = op.intValueAt(arr);
    if (op.intValueAt(imageArr) != unaryOp.intValueAt(unaryArg)) {
      System.out.println("fails to commute at " + ArrayString.toString(arr));
      return false;
    }
    ArrayIncrementor inc = 
               SequenceGenerator.sequenceIncrementor(arr, setSize - 1);
    while (inc.increment()) {
      unaryArg[0] = op.intValueAt(arr);
      v = unaryOp.intValueAt(unaryArg);
      for (int i = 0 ; i < op.arity(); i++) {
        unaryArg[0] = arr[i];
        imageArr[i] = unaryOp.intValueAt(unaryArg);
      }
      if (op.intValueAt(imageArr) != v) {
        System.out.println("fails to commute at " + ArrayString.toString(arr));
        System.out.println("with op " + op.symbol().name());
        return false;
      }
    }
    return true;
  }

  /**
   * Test if map defines a homomorphism wrt these operations. The operations
   * must have the same arity.
   * 
   * @param map      an array defining the map
   * @param op0      the first operation
   * @param op1      the second operation
   * @return         true or false
   */
  public static boolean commutes(final int[] map, final Operation op0, 
      final Operation op1) {
    Logger logger = Logger.getLogger("org.uacalc.alg.Operations");
    logger.setLevel(Level.FINE);
    final int setSize = op0.getSetSize();
    final int[] arr = new int[op0.arity()];
    final int[] imageArr = new int[op0.arity()];
    final int[] unaryArg = new int[1];
    int v = map[0];
    for (int i = 0 ; i < op0.arity(); i++) {
      imageArr[i] = v; // v is unaryOp([0])
    }
    unaryArg[0] = map[op0.intValueAt(arr)];
    if (op1.intValueAt(imageArr) != map[op0.intValueAt(arr)]) {
      logger.info("fails to commute at " + ArrayString.toString(arr));
      return false;
    }
    ArrayIncrementor inc = 
      SequenceGenerator.sequenceIncrementor(arr, setSize - 1);
    while (inc.increment()) {
      v = map[op0.intValueAt(arr)];
      for (int i = 0 ; i < op0.arity(); i++) {
        imageArr[i] = map[arr[i]];
      }
      if (op1.intValueAt(imageArr) != v) {
        logger.info("fails to commute at " + ArrayString.toString(arr));
        logger.info("with op " + op0.symbol().name());
        return false;
      }
    }
    return true;
  }

  public static boolean isTotal(Operation op) {
    if (op instanceof OperationWithDefaultValue) {
      return ((OperationWithDefaultValue)op).isTotal();
    }
    return true;
  }
  
  /**
   * Test if an operation is idempotent.
   */
  public static final boolean isIdempotent(Operation op) {
    int[] arg = new int[op.arity()];
    for (int i = 0; i < op.getSetSize(); i++) {
      for (int j = 0; j < arg.length; j++) {
        arg[j] = i;
      }
      if (op.intValueAt(arg) != i) return false;
    }
    return true;
  }

  /**
   * Test if an operation is binary and commutative.
   */
  public static final boolean isCommutative(Operation op) {
    return op.arity() == 2 && isTotallySymmetric(op);
  }

  /**
   * Test if an operation is totally symmetric; that is, invariant
   * under all permutation of the variables.
   */
  public static final boolean isTotallySymmetric(Operation op) {
    final int[] arg = new int[op.arity()];
    ArrayIncrementor inc =
           SequenceGenerator.nondecreasingSequenceIncrementor(arg, 
                                                     op.getSetSize() - 1);
    while (inc.increment()) {  // the all 0 arg is total sym
      final int[] argx = new int[op.arity()];
      for (int i = 0 ; i < arg.length; i++) {
        argx[i] = arg[i];
      }
      final int value = op.intValueAt(argx);
      ArrayIncrementor incx = PermutationGenerator.arrayIncrementor(argx);
      while (incx.increment()) {
        if (op.intValueAt(argx) != value) return false;
      }
    }
    return true;
  }

  /**
   * Test if an operation is binary and associative.
   */
  public static final boolean isAssociative(Operation op) {
    if (op.arity() != 2) return false;
    final int[] arg = new int[op.arity()];
    final int n = op.getSetSize();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < n; k++) {
          arg[0] = i;
          arg[1] = j;
          int t = op.intValueAt(arg);
          arg[0] = t;
          arg[1] = k;
          int left = op.intValueAt(arg);
          arg[0] = j;
          arg[1] = k;
          t = op.intValueAt(arg);
          arg[0] = i;
          arg[1] = t;
          if (left != op.intValueAt(arg)) return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Check if a ternary operation is a Maltsev operation.
   * 
   * @param op
   * @return
   */
  public static final boolean isMaltsev(Operation op) {
    if (op.arity() != 3) return false;
    final int n = op.getSetSize();
    int[] arg = new int[3];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        arg[0] = i;
        arg[1] = i;
        arg[2] = j;
        if (j != op.intValueAt(arg)) return false;
        if (i == j) continue;
        arg[1] = j;
        if (i != op.intValueAt(arg)) return false;
      }
    }
    return true;
  }

  /**
   * Find an argument where these operations differ. 
   * 
   * 
   * @throws IllegalArgumentException if the arities or set sizes differ.
   * @param op0
   * @param op1
   * @return an int[] as a witness or null if they agree.
   */
  public static final int[] findDifference(Operation op0, Operation op1) {
    final int n = op0.getSetSize();
    if (n != op1.getSetSize()) 
      throw new IllegalArgumentException("Ops have different set sizes");
    final int arity = op0.arity();
    if (arity != op1.arity()) 
      throw new IllegalArgumentException("Ops have different arities");
    final int[] arr = new int[arity];
    ArrayIncrementor inc = 
      SequenceGenerator.sequenceIncrementor(arr, n - 1);
    while (inc.increment()) {
      if (op0.intValueAt(arr) != op1.intValueAt(arr)) return arr;
    }
    return null;
  }
  
  /**
   * This makes a hash map from the operation symbols to the operations.
   */
  public static Map<OperationSymbol,Operation> makeMap(List<Operation> ops) {
    final HashMap<OperationSymbol,Operation> map = new HashMap<OperationSymbol,Operation>(ops.size());
    for (Operation op : ops) {
      map.put(op.symbol(), op);
    }
    return map;
  }

  /**
   * Make a direct product operation. The <tt>ops</tt> should all have
   * the same arity and symbol but this does not check that. 
   */
  // I think this is in BigProduct
  /*
  public static Operation makeDirectProductOperation(List<Operation> ops, int algSize) {
    // it would be a disaster if the List ops got modified so we better
    // copy it. The final is so it can be used in the inner class.
    final List<Operation> opsx = new ArrayList<Operation>(ops);
    Operation op0 = (Operation)opsx.get(0);
    final int arity = op0.arity();
    final int numProjections = opsx.size();
    final int[] algSizes = new int[opsx.size()];
    for (int i = 0; i < algSizes.length; i++) {
      algSizes[i] = opsx.get(i).getSetSize();
    }
    return new AbstractOperation(op0.symbol(), algSize) {
          public Object valueAt(List args) {
            List ans = new ArrayList();
            for (int i = 0; i < opsx.size(); i++) {
              final Operation op = opsx.get(i);
              List arg = new ArrayList();
              for (Iterator it = args.iterator(); it.hasNext(); ) {
                arg.add(((List)it.next()).get(i));
              }
              ans.add(op.valueAt(arg));
            }
            return ans;
          }
          
          public int[] valueAt(int[][] args) {
            int [] ans = new int[numProjections];
            for (int i = 0; i < numProjections; i++) {
              final Operation op = opsx.get(i);
              int tmp = args[arity - 1][i];
              for (int j = arity - 2; j >= 0; j--) {
                tmp = algSizes[j] * tmp + args[j][i];
              }
              ans[i] = op.intValueAt(tmp);
            }
            return ans;
          }
      };
  }
  */
  
  /**
   * Test if these have the same int values on the common sized set.
   * So they are equal except they may have different symbos
   */
  public static boolean equalValues(Operation op0, Operation op1) {
    if (op0.getSetSize() != op1.getSetSize()) return false;
    if (op0.arity() != op1.arity()) return false;
    final int[] arr = new int[op0.arity()];
    if (op0.intValueAt(arr) != op1.intValueAt(arr)) return false;
    ArrayIncrementor inc = 
      SequenceGenerator.sequenceIncrementor(arr, op0.getSetSize() - 1);
    while (inc.increment()) {
      if (op0.intValueAt(arr) != op1.intValueAt(arr)) return false;
    }
    return true;
  }
  
  public static Operation ternaryDiscriminator(final int size) {
    Operation disc = new AbstractOperation("disc", 3, size) {
      
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      
      public int intValueAt(int[] args) {
        if (args[0] == args[1]) return args[2];
        return args[0];
      }
    };
    return disc;
  }
  
  /**
   * Make an operation with the the given opeation symbol
   * and set size.
   * 
   * @param n       the set size
   * @param opSym   the operation symbol
   * @return        the operation
   */
  public static Operation makeRandomOperation(final int n, 
                                      final OperationSymbol opSym) {
    return makeRandomOperation(n, opSym, new Random());
  }
  
  /**
   * Make an operation with the the given opeation symbol
   * and set size.
   * 
   * @param n       the set size
   * @param opSym   the operation symbol
   * @param random  a random number generator.
   * @return        the operation
   */
  public static Operation makeRandomOperation(final int n, 
      final OperationSymbol opSym, Random random) {
    final int arity = opSym.arity();
    int h = 1;
    for (int i = 0; i < arity; i++) {
      h = h * n;
    }
    final int[] values = new int[h];
    for (int i = 0; i < h; i++) {
      values[i] = random.nextInt(n);
    }
    return Operations.makeIntOperation(opSym, n, values);
  }
  
  /**
   * Make a list of operations corresponding to a similarity type and a 
   * set size.
   * 
  * @param n          the set size
   * @param simType   the similarity type
   * @return          a list of operations
   */
  public static List<Operation> makeRandomOperations(final int n, 
                                        final SimilarityType simType) {
    return makeRandomOperations(n, simType, -1);
  }
  
  /**
   * Make a list of operations corresponding to a similarity type and a 
   * set size.
   * 
   * @param n         the set size
   * @param simType   the similarity type
   * @param seed      a random seed or -1
   * @return          a list of operations
   */
  public static List<Operation> makeRandomOperations(final int n, 
      final SimilarityType simType, long seed) {
    Random random;
    if (seed != -1)
      random = new Random(seed);
    else
      random = new Random();
    List<OperationSymbol> opSyms = simType.getOperationSymbols();
    final int len = opSyms.size();
    List<Operation> ops = new ArrayList<Operation>(len);
    for (int i = 0; i < len; i++) {
      ops.add(makeRandomOperation(n, opSyms.get(i), random));
    }
    return ops;
  }

  /**
   * The operation derived by equating variables. If f(x,y,z) is an 
   * operation and [1, 0, 1] is the reductionArray, then the derived
   * operation is g(x,y) = f(y,x,y).
   * 
   * @param reductionArray  an int array that can be viewed as a map
   *                        from the int less than the arity of op
   *                        into the arity of the derived operation.
   */
  public static Operation makeDerivedOperation(final Operation op, 
                                               final int[] reductionArray,
                                               final int newArity) {
    final int bigArity = op.arity();
    final int algSize = op.getSetSize();
    // for safety make a copy of the reduction array:
    final int[] reductArr = new int[reductionArray.length];
    System.arraycopy(reductionArray, 0, reductArr, 0, reductionArray.length);

    return new AbstractOperation(
                 op.symbol().name() + "_derived" 
                                    + ArrayString.toString(reductionArray), 
                                                          newArity, algSize) {

        public int intValueAt(int[] arr) {
          final int[] bigArray = new int[bigArity];
          for (int i = 0; i < bigArity; i++) {
            bigArray[i] = arr[reductArr[i]];
          }
          return op.intValueAt(bigArray);
        }

        public Object valueAt(List args) {
          final List bigList = new ArrayList(bigArity);
          for (int i = 0; i < bigArity; i++) {
            bigList.add(args.get(reductArr[i]));
          }
          return op.valueAt(bigList);
        }
      };
  }

  /**
   * Make the Jonsson terms from a near unanimity operation, omitting 
   * the two projections.
   * See the file nuf.pdf.
   */
  public static List<Operation> makeJonssonOperationsFromNUF(final Operation nuf) {
    final int nufArity = nuf.arity();
    List<Operation> ans = new ArrayList<Operation>(2 * nufArity - 5);
    final int[] reductArray = new int[nufArity];
    for (int i = 0; i < nufArity; i++) {
      if (i == nufArity - 1) reductArray[i] = 2;
      else if (i == nufArity - 2) reductArray[i] = 1;
      else reductArray[i] = 0;
    }
    ans.add(makeDerivedOperation(nuf, reductArray, 3)); // p_1
    for (int k = 1; k < nufArity - 2; k++) {
      for (int i = 0; i < nufArity; i++) {
        if (i < nufArity - (k + 1)) reductArray[i] = 0;
        else reductArray[i] = 2;
      }
      // the two variable only terms (every other one) are in the 
      // clone of the others, so skip them
      //ans.add(makeDerivedOperation(nuf, reductArray, 3));
      reductArray[nufArity - k - 2] = 1;
      ans.add(makeDerivedOperation(nuf, reductArray, 3));
    }
    return ans;
  }
  
  public static final int power(int base, int pow) {
    int ans = 1;
    for (int i = 0; i < pow; i++) {
      ans = ans * base;
    }
    return ans;
  }
 
  /**
   * The unary operation that cyclically shifts a vector left.
   * 
   */
  public static Operation makeLeftShift(final int vecSize, final int rootSize) {
 
    final int algSize = power(rootSize, vecSize);
    
    Operation shift = new AbstractOperation("leftshift", 1, algSize) {
      
      private int[] shiftArray(int[] arr) {
        final int size = arr.length;
        int[] ans = new int[size];
        for (int i = 1; i < size; i++) {
          ans[i-1] = arr[i];
        }
        ans[size-1] = arr[0];
        return ans;
      }
      // do we need a getTable() ? 
      public Object valueAt(List args) {
        final int[] arg = ((IntArray)args.get(0)).getArray();
        return new IntArray(shiftArray(arg));
      }
      
      public int intValueAt(int[] args) {
        final int arg = args[0];
        final int[] arr = Horner.hornerInv(arg, rootSize, vecSize);
        return Horner.horner(shiftArray(arr), rootSize);
      }
    };
    return shift;
  }
  
  public static Operation makeMatrixDiagonalOp(final int vecSize, final int rootSize) {
    final int algSize = power(rootSize, vecSize);
    
    Operation op = new AbstractOperation("diagonal", vecSize, algSize) {
      
      int[] diag(List args) {
        final int size = args.size();
        int[] ans = new int[size];
        for (int i = 0; i < size; i++) {
          ans[i] = ((IntArray)args.get(i)).get(i);
        }
        return ans;
      }
      
      public Object valueAt(List args) {
        return new IntArray(diag(args));
      }
      // add getTable later  ??
      public int intValueAt(int[] args) {
        List<IntArray> argList = new ArrayList<IntArray>(args.length);
        for (int i = 0; i < args.length; i++) {
          argList.add(new IntArray(Horner.hornerInv(args[i], rootSize, vecSize)));
        }
        return Horner.horner(diag(argList), rootSize);
      }
      
    };
    return op;
  }
  
  public static Operation makeCompositionOp(final int n, final int pow) {
    Operation op = new AbstractOperation("comp", 2, pow) {
      
      int[] comp(final int[] f, final int[] g) {
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
          ans[i] = f[g[i]];
        }
        return ans;
      }
      
      public Object valueAt(List args) {
        final int[] comp = comp(((IntArray)args.get(0)).getArray(),  
                                ((IntArray)args.get(1)).getArray());
        return new IntArray(comp);
      }
      // add getTable later  ??
      public int intValueAt(int[] args) {
        final int[] f = Horner.hornerInv(args[0], n, n);
        final int[] g = Horner.hornerInv(args[1], n, n);
        final int[] comp = comp(f,g);
        return Horner.horner(comp, n);
      }
      
      
      
      public void makeTable() {
        if (n > 4) return;  // too big to hold the table
        for (int i = 0; i < pow; i++) {
          for (int j = 0; j < pow; j++) {
            final int[] args = new int[] {i, j};
            final int ans = intValueAt(args);
            valueTable[Horner.horner(args, 2)] = ans;
          }
        }
        
      }
    };
    return op;
    
  }
  
  /**
   * The <code>script</code> should be the body of the definition
   * of intValueAt(int[] args).
   */
  public static Operation makeOperationFromScript(OperationSymbol symbol,
                                  int arity, int algSize, String[] script) 
                                                    throws ScriptException {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine gEngine = manager.getEngineByName("groovy");
    Invocable invocable = (Invocable)gEngine;
    gEngine.eval("int arity() { " + arity + " }\n");
    gEngine.eval("org.uacalc.alg.OperationSymbol symbol() { " + symbol + " }\n");
    gEngine.eval("int[] valueAt(int[][] args) "
                + "{ throw new UnsupportedOperationException() }\n");
    gEngine.eval("Object valueAt(List args) "
                + "{ throw new UnsupportedOperationException() }\n");
    // fix this
    gEngine.eval("boolean isIdempotent() "
                + "{ throw new UnsupportedOperationException() }\n");
    gEngine.eval("boolean isTotallySymmetric() "
                + "{ Operations.isTotallySymmetric() }\n");
    gEngine.eval("boolean isAssociative() "
                + "{ Operations.isAssociative() }\n");
    gEngine.eval("boolean isCommutative() "
                + "{ Operations.isCommutative() }\n");
    // fix these ???
    gEngine.eval("void makeTable() { }\n");
    gEngine.eval("int[] getTable() { null }\n");
    
    StringBuffer sb = new StringBuffer("int intValueAt(int[] args) { \n");
    if (arity > 0) sb.append("int x = args[0]\n");
    if (arity > 1) sb.append("int y = args[1]\n");
    if (arity > 2) sb.append("int z = args[2]\n");
    for (int i = 0 ; i < script.length; i++) {
      sb.append(script[i]);
      sb.append("\n");
    }
    sb.append("}\n");
    gEngine.eval(sb.toString());
    
/*
    gEngine.eval("int intValueAt(int[] args) {\n");
    for (int i = 0 ; i < script.length; i++) {
      gEngine.eval(script[i] + "\n");
    }
    gEngine.eval("}\n");
*/
    //gEngine.eval("int intValueAt(int[] args) { 3 }\n");
    Operation op = invocable.getInterface(Operation.class);
    return op;
  }

  public static List<Operation> makeIntOperations(List<Operation> ops) {
    List<Operation> ans = new ArrayList<Operation>(ops.size());
    for (Operation op : ops) {
      ans.add(makeIntOperation(op));
    }
    return ans;
  }
  
  /**
   * This makes a new operation that agrees with the original but is
   * table based and so faster.
   * 
   * @param opx   the original operation
   * @return      the new operation
   */
  public static Operation makeIntOperation(final Operation opx) {
    if (opx.isTableBased()) return opx;
    final int arity = opx.arity();
    final int size = opx.getSetSize();
    int h = 1;
    for (int i = 0; i < arity; i++) {
      h = h * size;
    }
    final int[] values = new int[h];
    for (int i = 0; i < h; i++) {
      values[i] = opx.intValueAt(Horner.hornerInv(i, size, arity));
    }
    Operation op = new AbstractOperation(opx.symbol(), size) {
      public Object valueAt(List args) {
        return opx.valueAt(args);
      }
      public int[] getTable() { return values; }
      public int[] getTable(boolean makeTable) { return values; }
      public int intValueAt(final int[] args) {
        return values[Horner.horner(args, algSize)];
      }
      public int intValueAt(int arg) {
        return values[arg];
      }
      
      public boolean isTableBased() { return true; }
    };
    return op;
  }

  /**
   * Construct an Operation from a valueTable.
   *
   * @param valueTable a Horner encode table of the values of the operation.
   *
   */
  public static Operation makeIntOperation(String symbol, int arity,
                                           int algSize, int[] valueTable) {
    return makeIntOperation(new OperationSymbol(symbol, arity), 
                            algSize, valueTable);
  }

  /**
   * Construct an Operation from a valueTable.
   *
   * @param valueTable a Horner encode table of the values of the operation.
   *
   */
  public static Operation makeIntOperation(OperationSymbol symbol, 
                                      int algSize, int[] valueTable) {
    return new IntOperationImp(symbol, algSize, valueTable);
  }

// also do this for nullary, unary, ternary.

  public static Operation makeBinaryIntOperation(OperationSymbol symbol, 
                                             int algSize, int[][] table) {
    int[] valueTable = new int[algSize * algSize];
    int k = 0;
    for (int i = 0; i < algSize; i++) {
      for (int j = 0; j < algSize; j++) {
        valueTable[k++] = table[i][j];
      }
    }
    return new IntOperationImp(symbol, algSize, valueTable);
  }
  
  public static Operation makeConstantIntOperation(final int algSize, final int elt) {
    return makeConstantIntOperation("c", algSize, elt);
  }
  
  public static Operation makeConstantIntOperation(final String symbolPrefix, final int algSize, final int elt) {
    OperationSymbol sym = new OperationSymbol(symbolPrefix + elt, 0);
    final int[] values = new int[] {elt};
    Operation op = new AbstractOperation(sym, algSize) {
      public Object valueAt(List args) {
        throw new UnsupportedOperationException();
      }
      public int[] getTable() { return values; }
      public int[] getTable(boolean makeTable) { return values; }
      public int intValueAt(final int[] args) {
        return elt;
      }
      public int intValueAt(int arg) {
        return values[arg];
      }
      
      public boolean isTableBased() { return true; }
    };
    return op;
  }
  
  public static void binaryOpToCSV(String desc, Operation binop, java.io.PrintStream out) {
    if (binop.arity() != 2) throw new IllegalArgumentException();
    final int size = binop.getSetSize();
    final String comma = ",";
    final String dquote = "\"";
    final String eol = "\n";
    out.println("," + dquote +  desc + dquote + comma);
    out.println(",,");
    out.print(",,");
    for (int i = 0; i < size; i++) {
      out.print(i);
      out.print(comma);
    }
    out.println("\n,,");
    for (int i = 0; i < size; i++) {
      out.print(i);
      out.print(",,");
      for (int j = 0; j < size; j++) {
        out.print(binop.intValueAt(new int[] {i,j}));
        out.print(comma);
      }
      out.print(eol);
    }
    out.println(eol);
  }

  public static void main(String[] args) throws Exception {
    String[] sc = new String[] {"def n = Math.min(x,y) + args[0] / 2", "n+10"};
    Operation op =  makeOperationFromScript(
                        new OperationSymbol("f", 2), 2, 5, sc);
    System.out.println("f(5,4) = " + op.intValueAt(new int[] {5,4}));
  }


  
  static class IntOperationImp extends AbstractOperation {

    //private final int[] valueTable;
  
    public IntOperationImp(OperationSymbol symbol, int algSize, 
                                                   int[] valueTable) {
      super(symbol, algSize);
      this.valueTable = valueTable;
    }
  
    /**
     * This method applies the operation to the elements 
     * of the universe of the algebra at the indices and returns 
     * the index of the answer.
     */ 
    public int intValueAt(int[] args) {
      return valueTable[Horner.horner(args, algSize)];
    }
    
    public int intValueAt(int arg) {
      return valueTable[arg];
    }

    public Object valueAt(List args) {
      Integer[] argsArray = (Integer[])args.toArray(new Integer[args.size()]);
      return new Integer(valueTable[Horner.horner(argsArray, algSize)]);
    }
  
  }

}




