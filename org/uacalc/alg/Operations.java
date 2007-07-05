/* Operations.java (c) 2001/07/27  Ralph Freese and Emil Kiss */

package org.uacalc.alg;

import java.util.*;
import java.util.logging.*;
import org.uacalc.util.*;
import org.uacalc.alg.conlat.BasicPartition;

/**
 * This is a factory class with static methods to make Operations.
 */
public class Operations {

  static Logger logger = Logger.getLogger("org.uacalc.alg.Operations");
  static {
    logger.setLevel(Level.FINER);
  }


  /**
   * This class can never be instantiated.
   */
  private Operations() {}

  /**
   * This tests if an operation commutes with a unary operation. It can
   * be use to test if an operation is a endomorphism (or automorphism).
   */
  public static boolean commutes(final Operation unaryOp, final Operation op) {
Logger logger = Logger.getLogger("org.uacalc.alg.Operations");
logger.setLevel(Level.FINE);
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
//System.out.println("first");
      logger.info("fails to commute at " + ArrayString.toString(arr));
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
        logger.info("fails to commute at " + ArrayString.toString(arr));
        logger.info("with op " + op.symbol().name());
        return false;
      }
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
   * This makes a hash map from the operation symbols to the operations.
   */
  public static Map makeMap(List ops) {
    final HashMap map = new HashMap(ops.size());
    for (Iterator it = ops.iterator(); it.hasNext(); ) {
      final Operation op = (Operation)it.next();
      map.put(op.symbol(), op);
    }
    return map;
  }

  /**
   * Make a direct product operation. The <tt>ops</tt> should all have
   * the same arity and symbol but this does not check that. 
   */
  public static Operation makeDirectProductOperation(List ops, int algSize) {
    // it would be a disaster if the List ops got modified so we better
    // copy it. The final is so it can be used in the inner class.
    final List opsx = new ArrayList(ops);
    Operation op0 = (Operation)opsx.get(0);
    return new AbstractOperation(op0.symbol(), algSize) {
          public Object valueAt(List args) {
            List ans = new ArrayList();
            for (int i = 0; i < opsx.size(); i++) {
              Operation op = (Operation)opsx.get(i);
              List arg = new ArrayList();
              for (Iterator it = args.iterator(); it.hasNext(); ) {
                arg.add(((List)it.next()).get(i));
              }
              ans.add(op.valueAt(arg));
            }
            return ans;
          }
      };
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
  public static List makeJonssonOperationsFromNUF(final Operation nuf) {
    final int nufArity = nuf.arity();
    List ans = new ArrayList(2 * nufArity - 5);
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

    public Object valueAt(List args) {
      Integer[] argsArray = (Integer[])args.toArray(new Integer[args.size()]);
      return new Integer(valueTable[Horner.horner(argsArray, algSize)]);
    }
  
  }

}




