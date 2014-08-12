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

// Not working yet. It may be better to subclass PowerAglebra


public class MatrixPowerAlgebra extends GeneralAlgebra implements SmallAlgebra {

    protected final SmallAlgebra root;

    protected final int rootSize;
    
    protected final int power;
    
    protected final PowerAlgebra powerAlgebra;

    public MatrixPowerAlgebra(SmallAlgebra alg, int power) {
      this("", alg, power);
    }

    /**
     * Construct the direct power of an algebra.
     */
    public MatrixPowerAlgebra(String name, SmallAlgebra alg, int power) {
      // put a check that size < maxInteger size
      // do we allow power = 0 ?
      super(name);
      if (name == null || name.equals("")) {
        String str = alg.getName() != null? alg.getName() + "^[" + power + "]" : power + "-matrix pwoer";
        setName(str);
      }
      this.power = power;
      powerAlgebra = new PowerAlgebra(alg, power);
      root = alg;
      rootSize = alg.cardinality();
      List<Operation> opList = powerAlgebra.operations();
      opList.add(Operations.makeLeftShift(power, rootSize));
      opList.add(Operations.makeMatrixDiagonalOp(power, rootSize));
      setOperations(opList);
    }

    public SmallAlgebra getRoot() { return root; }
    
    public SmallAlgebra parent() { return root; }
    
    public PowerAlgebra getPowerAlgebra() { return powerAlgebra; }
    
    
    
    public List<SmallAlgebra> parents() {
      List<SmallAlgebra> ans = new ArrayList<SmallAlgebra>();
      ans.add(root);
      return ans; 
    }

    public int getPower() { return power; }
    
    public Object getElement(int index) {
      return Horner.hornerInv(index, rootSize, power);
    }
    
    public int elementIndex(Object obj) {
      return powerAlgebra.elementIndex(obj);
    }
    
    public int cardinality() { return powerAlgebra.cardinality(); }
    
    public List getUniverseList() { return new ArrayList<IntArray>(universe()); }
    public Map getUniverseOrder() { return null; }
    
    public void convertToDefaultValueOps() {
      throw new UnsupportedOperationException("Only for basic algebras"); 
    }
    
    public CongruenceLattice con() {
      if (con == null) con = new CongruenceLattice(this);
      return con;
    }

    public SubalgebraLattice sub() {
      if (sub == null) sub = new SubalgebraLattice(this);
      return sub;
    }
    
    public AlgebraType algebraType() {
      return AlgebraType.MATRIX_POWER;
    }

}
