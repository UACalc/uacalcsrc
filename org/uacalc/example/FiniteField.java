package org.uacalc.example;

import java.util.*;
import java.io.*;

import org.uacalc.alg.op.AbstractOperation;
import org.uacalc.alg.GeneralAlgebra;
import org.uacalc.alg.SmallAlgebra;
import org.uacalc.alg.op.Operation;

/**
 * This class for finite fields. If the order of the filed has
 * <code>p^n<code> elements, its elements are arrays ints mod p of length
 * <code>n</code>. Addition is component-wise, mod p. The constructor
 * requires a polynomial of degree n irreducible over Z_p[x] and the roots
 * of this polynomial must have multiplicative order p^n - 1. If lambda
 * is a root of this polynomial, then 1, lambda, lambda^2, ..., lambda^(n-1)
 * is a basis and we view our arrays using this basis. Multiplication by lambda
 * is a shift operation combined with using the polynomial to express
 * lambda^n in terms of lower powers. We keep a map from thea arrays to 
 * the powers of lambda to our arrays.
 * For a good example of how this correspondence works see the slide
 * for constructing GF(9) near the end of
 * <p>
 *   <a href="http://math.ucdenver.edu/~wcherowi/courses/m6406/finflds.pdf">
 *   math.ucdenver.edu/~wcherowi/courses/m6406/finflds.pdf</a>.
 * <p> 
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
  
  private final Map<Integer,int[]> powerMap;
  
  private final Map<int[],Integer> invPowerMap;
  
  public FiniteField(int prime, int power) {
    super("Field" + prime + "^" + power);
    this.prime = prime;
    this.power = power;
    this.polynomial = conwayPolynomial(prime, power);
    int card = 1;
    for (int i = 0; i < getPower(); i++) {
      card = card * prime;
    }
    this.cardinality = card;
    this.powerMap = new HashMap<>();
    this.invPowerMap = new HashMap<>();
    setMaps();
    makeOperations();
  }
  
  
  /**
   * 
   * @param prime
   * @param poly  
   */
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
  
  private void setMaps() {
    int[] vec = new int[getPower()];
    vec[0] = 1;
    powerMap.put(0, vec);
    for (int i = 1; i < cardinality - 1; i++) {
      vec = nextVector(vec);
      powerMap.put(i, vec);
    }
    for (int i = 0; i < cardinality - 1; i++) {
      invPowerMap.put(powerMap.get(i), i);
    }
  }
  
  private int[] nextVector(int[] vec) {
    int lastCoeff = vec[getPower() - 1];
    int[] ans = new int[getPower()];
    ans[0] = lastCoeff * getPolynomial()[0];
    for (int i = 1; i < getPower(); i++) {
      ans[i] = (lastCoeff * getPolynomial()[i] + vec[i-1]) % getPrime(); 
    }
    return ans;
  }
  
  Operation plus = new AbstractOperation("+", 2, cardinality()) {
    public Object valueAt(List lst) {
      int[] vec0 = (int[])lst.get(0);
      int[] vec1 = (int[])lst.get(1);
      int[] ans = new int[getPower()];
      for (int i = 0; i < getPower(); i++) {
        ans[i] = (vec0[i] + vec1[i]) % getPrime();
      }
      return ans;
    }
    //public int intValueAt(int[] args) {
    //  return (args[0] + args[1]) % getPrime();
    //}
  };
  
  Operation minus = new AbstractOperation("-", 1, cardinality()) {
    public Object valueAt(List lst) {
      int[] vec = (int[])lst.get(0);
      int[] ans = new int[getPower()];
      for (int i = 0; i < getPower(); i++) {
        if (vec[i] == 0) ans[i] = 0;
        else ans[i] = getPrime() - vec[i];
      }
      return ans;
    }
    //public int intValueAt(int[] args) {
    //  if (args[0] == 0) return 0;
    //  return getPrime() - args[0];
    //}
  };

  Operation times = new AbstractOperation("*", 2, cardinality()) {
    public Object valueAt(List lst) {
      int v0 = invPowerMap.get((int[])lst.get(0));
      int v1 = invPowerMap.get((int[])lst.get(1));
      return powerMap.get((v0 + v1) % (cardinality() - 1));
    }
  };
  
  private void makeOperations() {
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int elementIndex(Object elem) {
    // TODO Auto-generated method stub
    return 0;
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
   * Looks up the Conway polynomial for the finite field
   * specified by <code>prime</code> and <code>power</code>
   * using an abbreviated version of Frank Luebeck table. 
   * <p>
 *   <a href="http://www.math.rwth-aachen.de/~Frank.Luebeck/data/ConwayPol/">
 *   math.rwth-aachen.de/~Frank.Luebeck/data/ConwayPol/</a>.
 * <p>
   * 
   * 
   * @param prime
   * @param power
   * @return  the Conway polynomial without the leading 1 or null if the parameters are too big
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
                ans[i] = Integer.parseInt(toks[i + 3]);
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
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    FiniteField z11 = new FiniteField(11);
    for (int i = 1; i < 11; i++) {
      Operation inverse = z11.operations().get(3);
      int inv = inverse.intValueAt(new int[] {i});
      System.out.println(i + " inv is " + inv);
    }
    FiniteField gf9 = new FiniteField(3, new int[]{1,1});
    for (int i = 0; i < 8; i++) {
      int[] vec = gf9.powerMap.get(i);
      System.out.println(i + " -> " + Arrays.toString(vec) + ", inverse is " + gf9.invPowerMap.get(vec));
    }
    System.out.println(Arrays.toString(conwayPolynomial(29,6)));
  }


}
