package org.uacalc.example;

import java.util.*;
import java.io.*;

import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.GeneralAlgebra;
import org.uacalc.alg.SmallAlgebra;
import org.uacalc.alg.op.Operation;
import org.uacalc.util.*;

/**
 * This class for finite fields. If the order of the filed has
 * <code>p^n<code> elements, its elements are arrays ints mod p of length
 * <code>n</code>. Addition is component-wise, mod p. The constructor
 * requires a polynomial of degree n irreducible over Z_p[x] and the roots
 * of this polynomial must have multiplicative order p^n - 1. If lambda
 * is a root of this polynomial, then 1, lambda, lambda^2, ..., lambda^(n-1)
 * is a basis and we view our arrays using this basis. Multiplication by lambda
 * is a shift operation combined with using the polynomial to express
 * lambda^n in terms of lower powers. We keep a map from the arrays to 
 * the powers of lambda to our arrays.
 * For a good example of how this correspondence works see the slide
 * for constructing GF(9) near the end of
 * <p>
 *   <a href="http://math.ucdenver.edu/~wcherowi/courses/m6406/finflds.pdf">
 *   math.ucdenver.edu/~wcherowi/courses/m6406/finflds.pdf</a>.
 * <p> 
 * The element order is the one abouve except 0 is first. So 1 = lamda^0 is second,
 * lambda is third. The kth element is lambda^(k-1). This makes the elements
 * of Z_p look odd.
 * 
 * 
 * Conway polynomials have the desired properties (and more). These can
 * be found at
 * <p>
 *   <a href="http://www.math.rwth-aachen.de/~Frank.Luebeck/data/ConwayPol/">
 *   math.rwth-aachen.de/~Frank.Luebeck/data/ConwayPol/</a>.
 * <p>
 * 
 * @author ralph
 *
 */
public class FiniteField extends GeneralAlgebra implements SmallAlgebra {
  
  private final int prime;
  private final int power;
  private final int cardinality;
  
  /**
   * An primitive monic polynomial of degree n,
   * irreducible over Z_p,
   * x^n + a_{n-1}x^{n-1} + ... + a_0
   * represented by [a_0, ..., a_{n-1}].
   * 
   */
  private final int[] polynomial;
  
  private final List<IntArray> universeList;
  
  private final Map<Integer,IntArray> powerMap;
  
  private final Map<IntArray,Integer> invPowerMap;
  
  private IntArray zeroIA;
  private IntArray oneIA;
  
  public FiniteField(int prime, int power) {
    super("Field" + prime + "^" + power);
    this.prime = prime;
    this.power = power;
    this.polynomial = conwayPolynomial(prime, power);
    
    zeroIA = new IntArray(new int[power]);
    oneIA = new IntArray(new int[power]);
    oneIA.set(0, 1);
    int card = 1;
    for (int i = 0; i < getPower(); i++) {
      card = card * prime;
    }
    this.cardinality = card;
    this.universeList = new ArrayList<>(card);
    this.powerMap = new HashMap<>();
    this.invPowerMap = new HashMap<>();
    setUnivAndMaps();
    setOperations();
  }
  
  
  /**
   * 
   * @param prime
   * @param poly  
   */
  /*
  public FiniteField(int prime, int[] poly) {
    super("Field" + prime + "^" + poly.length);
    this.power = poly.length;
    this.prime = prime;
    int card = 1;
    for (int i = 0; i < getPower(); i++) {
      card = card * prime;
    }
    this.cardinality = card;
    this.polynomial = poly;
    this.powerMap = new HashMap<>();
    this.invPowerMap = new HashMap<>();
    setMaps();
    makeOperations();
  }
  
  public FiniteField (int prime) {
    super("GF(" + prime + ")");
    this.prime = prime;
    this.power = 1;
    this.cardinality = prime;
    this.polynomial = null;
    this.powerMap = null;
    this.invPowerMap = null;
    makeOperations();
  }
  */
  
  private void setUnivAndMaps() {
    int[] vec = new int[getPower()];
    IntArray ia = new IntArray(vec);
    universeList.add(ia);
    vec = new int[getPower()];
    vec[0] = 1;
    ia = new IntArray(vec);
    universeList.add(ia);
    powerMap.put(0, ia);
    for (int i = 1; i < cardinality - 1; i++) {
      vec = nextVector(vec);
      ia = new IntArray(vec);
      universeList.add(ia);
      powerMap.put(i, ia);
    }
    for (int i = 0; i < cardinality - 1; i++) {
      invPowerMap.put(powerMap.get(i), i);
    }
  }
  
  private int[] nextVector(int[] vec) {
    int lastCoeff = vec[getPower() - 1];
    int[] ans = new int[getPower()];
    ans[0] = (lastCoeff * getPolynomial()[0]) % getPrime();
    for (int i = 1; i < getPower(); i++) {
      ans[i] = (lastCoeff * getPolynomial()[i] + vec[i-1]) % getPrime(); 
    }
    return ans;
  }
  
  public IntArray zero() { return zeroIA; }
  
  public int zeroIndex() { return 0; }
  
  public IntArray one() { return oneIA; }
  
  public int oneIndex() { return 1; }
  
  Operation zeroOperaation = new AbstractOperation("zero", 0, cardinality()) {
    public Object valueAt(List lst) {
      return zeroIA;
    }
    public int intValueAt(int k) {
      return 0;
    }
  };
  
  Operation oneOperation = new AbstractOperation("one", 0, cardinality()) {
    public Object valueAt(List lst) {
      return oneIA;
    }
    public int intValueAt(int k) {
      return 1;
    }
  };
  
  public IntArray plus(IntArray arg0, IntArray arg1) {
    int[] ansVec = new int[getPower()];
    for (int i = 0; i < getPower(); i++) {
      ansVec[i] = (arg0.get(i) + arg1.get(i)) % getPrime();
    }
    return new IntArray(ansVec);
  }
  
  public int plusInt(int index0, int index1) {
    //System.out.println("plus gives:  " + Arrays.toString(plus((int[])getElement(index0), (int[])getElement(index1))));
    return elementIndex(plus((IntArray)getElement(index0), (IntArray)getElement(index1)));
  }
  
  Operation plusOperation = new AbstractOperation("+", 2, cardinality()) {
    public Object valueAt(List lst) {
      IntArray vec0 = (IntArray)lst.get(0);
      IntArray vec1 = (IntArray)lst.get(1);
      return plus(vec0, vec1);
    }
    public int intValueAt(int[] args) {
      return plusInt(args[0], args[1]);
    }
  };
  
  public IntArray negation(IntArray arg) {
    int[] ans = new int[getPower()];
    for (int i = 0; i < getPower(); i++) {
      if (arg.get(i) == 0) ans[i] = 0;
      else ans[i] = getPrime() - arg.get(i);
    }
    return new IntArray(ans);
  }
  
  public int negationInt(int index) {
    return elementIndex(negation((IntArray)getElement(index)));
  }
  
  Operation negationOperation = new AbstractOperation("-", 1, cardinality()) {
    public Object valueAt(List lst) {
      IntArray ia = (IntArray)lst.get(0);
      return negation(ia);
    }
    public int intValueAt(int[] args) {
      return negationInt(args[0]);    
    }
  };
  
  public IntArray times(IntArray arg0, IntArray arg1) {
    if (arg0.equals(zeroIA) || arg1.equals(zeroIA)) return zeroIA;
    int v0 = invPowerMap.get(arg0);
    int v1 = invPowerMap.get(arg1);
    return powerMap.get((v0 + v1) % (cardinality() - 1));
  }
  
  public int timesInt(int a0, int a1) {
    if (a0 == 0 || a1 == 0) return 0;
    return (a0 + a1 - 2) % (cardinality() - 1) + 1;
  }

  Operation timesOperation = new AbstractOperation("*", 2, cardinality()) {
    public Object valueAt(List lst) {
      IntArray arg0 = (IntArray)lst.get(0);
      IntArray arg1 = (IntArray)lst.get(1);
      return times(arg0, arg1);
    }
    public int intValueAt(int[] args) {
      return timesInt(args[0], args[1]);
    }
  };
  
  public IntArray inverse(IntArray arg) {
    if (zeroIA.equals(arg)) {
      throw new IllegalArgumentException("Division by zero");
    }
    int pow = invPowerMap.get(arg);
    return powerMap.get((cardinality() - 1) - pow);
  }
  
  public int inverseInt(int index) {
    if (index == 0) {
      throw new IllegalArgumentException("Division by zero");
    }
    //return cardinality() - 1 - (index - 1) + 1;  // simplified:
    return cardinality() - index + 1;
  }
  
  Operation inverseOperation = new AbstractOperation("inv", 1, cardinality()) {
    public Object valueAt(List lst) {
      IntArray arg = (IntArray)lst.get(0);
      return inverse(arg);
    }
    public int intValueAt(int[] args) {
      return inverseInt(args[0]);
    }
  };
  
  
  private void setOperations() {
    List<Operation> ops = new ArrayList<>(4);
    ops.add(plusOperation);
    ops.add(negationOperation);
    ops.add(timesOperation);
    ops.add(inverseOperation);
    setOperations(ops);
    
  }
  
  private void makeOperations_old() {
    List<Operation> ops = new ArrayList<>(4);
    
    if (getPower() == 1) {    // for now just Z_p
      
      Operation plus = new AbstractOperation("+", 2, getPrime()) {
        public Object valueAt(List lst) {
          throw new UnsupportedOperationException();
        }
        public int intValueAt(int[] args) {
          return (args[0] + args[1]) % getPrime();
        }
      };
      Operation minus = new AbstractOperation("-", 1, getPrime()) {
        public Object valueAt(List lst) {
          throw new UnsupportedOperationException();
        }
        public int intValueAt(int[] args) {
          if (args[0] == 0) return 0;
          return getPrime() - args[0];
        }
      };
      Operation times = new AbstractOperation("*", 2, getPrime()) {
        public Object valueAt(List lst) {
          throw new UnsupportedOperationException();
        }
        public int intValueAt(int[] args) {
          return (args[0] * args[1]) % getPrime();
        }
      };
      Operation inverse = new AbstractOperation("inv", 1, getPrime()) {
        public Object valueAt(List lst) {
          throw new UnsupportedOperationException();
        }
        public int intValueAt(int[] args) {
          if (args[0] == 0) throw new IllegalArgumentException("division by 0");
          int a = args[0];
          int b = 1;  //power of a
          while (true) {
            if (a*b % getPrime() == 1) return b;
            b = b*a % getPrime();
          }
        }
      };
      ops.add(plus);
      ops.add(minus);
      ops.add(times);
      ops.add(inverse);
      setOperations(ops);
    }
  }
  
  public int cardinality() {
    return cardinality;
  }
  

  @Override
  public AlgebraType algebraType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getElement(int k) {
    if (k == 0) return zeroIA;
    return powerMap.get(k-1);
  }

  @Override
  public int elementIndex(Object elem) {
    //System.out.println("elem: " + Arrays.toString((int[])elem) + ", zeroVec: " + Arrays.toString(zeroIA));
    IntArray ia = (IntArray)elem;
    if (ia.equals(zeroIA)) return 0;
    return invPowerMap.get(ia) + 1;
  }

  @Override
  public List getUniverseList() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map getUniverseOrder() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void convertToDefaultValueOps() {
    // TODO Auto-generated method stub

  }
  
  /**
   * Looks up and returns the Conway polynomial with the coefficients negated 
   * for the finite field specified by <code>prime</code> and <code>power</code>
   * using an abbreviated version of Frank Luebeck table. 
   * <p>
   *   <a href="http://www.math.rwth-aachen.de/~Frank.Luebeck/data/ConwayPol/">
   *   math.rwth-aachen.de/~Frank.Luebeck/data/ConwayPol/</a>.
   * <p>
   * 
   * 
   * @param prime
   * @param power
   * @return  the Conway polynomial without the leading 1 and with the coeffs negated, or null if the parameters are too big
   */
  public static int[] conwayPolynomial(int prime, int power) {
    int[] ans = new int[power];
    String conwayFileName = "CPimport-3500.txt";
    String delims = "[,\\[\\]]+";
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream is = cl.getResourceAsStream(conwayFileName);
    if (is == null) {
      System.out.println("null InputStream");
      return null;
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        String[] toks = line.split(delims);
        //System.out.println(Arrays.toString(toks));
        // indexing is tricky: toks[0] is "" and we omit the final one
        // since the polynomial is monic and we omit the highest coeff.
        if (toks.length > 3) {
          if (prime == Integer.parseInt(toks[1])) {
            if (power == Integer.parseInt(toks[2])) {
              for (int i = 0; i < toks.length - 4; i++) {
                int coeff = Integer.parseInt(toks[i + 3]);
                if (coeff != 0) coeff = prime - coeff;  // negate the coeffs
                ans[i] = coeff;
              }
              return ans;
            }
          }
        }
      }
    }
    catch (IOException ex) { ex.printStackTrace(); }
    return null;
  }

  public int getPrime() {
    return prime;
  }

  public int getPower() {
    return power;
  }

  public int[] getPolynomial() {
    return polynomial;
  }
  
  /**
   * Test if x^2 - rx - s is irreducible over this field. 
   * 
   * @param r
   * @param s
   * @return
   */
  public boolean irreducibleQuadratic(int rIndex, int sIndex) {
    for (int k = 0; k < cardinality() - 1; k++) {
      //System.out.println("rIndex = " + rIndex + ", sIndex = " + sIndex +   ", k = " + k);
      if (timesInt(k,k) == plusInt(timesInt(rIndex, k), sIndex)) return false;
    }
    return true;
  }
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    //FiniteField z11 = new FiniteField(11);
    //for (int i = 1; i < 11; i++) {
      //Operation inverse = z11.operations().get(3);
      //int inv = inverse.intValueAt(new int[] {i});
      //System.out.println(i + " inv is " + inv);
    //}
    //FiniteField gf9 = new FiniteField(3, new int[]{1,1});
    //for (int i = 0; i < 8; i++) {
      //int[] vec = gf9.powerMap.get(i);
      //System.out.println(i + " -> " + Arrays.toString(vec) + ", inverse is " + gf9.invPowerMap.get(vec));
    //}
    System.out.println(Arrays.toString(conwayPolynomial(29,6)));
    System.out.println(Arrays.toString(conwayPolynomial(3,2)));
    System.out.println(Arrays.toString(conwayPolynomial(7,1)));
    FiniteField gf = new FiniteField(3, 2);
    //FiniteField gf = new FiniteField(7, 1);
    for (int i = 0; i < gf.cardinality() - 1; i++) {
      IntArray vec = gf.powerMap.get(i);
      int invIndex = gf.invPowerMap.get(vec);
      System.out.println(i + " -> " + vec + ", inverse is " + invIndex + " elt index = " + gf.elementIndex(vec));
    }
    for (int i = 0; i < gf.cardinality(); i++) {
      IntArray vec = (IntArray)gf.getElement(i);
      for (int j = 0; j < gf.cardinality(); j++) {
        IntArray vec2 = (IntArray)gf.getElement(j);
        List<IntArray> lst = new ArrayList<>(2);
        lst.add(vec);
        lst.add(vec2);
        int[] intArgs = new int[] {i, j};
        IntArray ans = (IntArray)gf.timesOperation.valueAt(lst);
        int intAns = gf.timesOperation.intValueAt(intArgs);
        System.out.println(i + "*" + j + " = " + ans + " = " + intAns);     
      }
    }
    System.out.println("inv of index 4 element: " + gf.inverseInt(4) + " = " + gf.inverse((IntArray)gf.getElement(4)));
    for (int i = 0; i < gf.cardinality(); i++) {
      for (int j = 0; j < gf.cardinality(); j++) {
        if (gf.irreducibleQuadratic(i, j)) {
          System.out.println("rIndes = " + i + ", sIndex = " + j + " works");
        }
      }
    }
  }


}
