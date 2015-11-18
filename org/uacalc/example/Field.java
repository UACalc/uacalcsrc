package org.uacalc.example;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uacalc.alg.Algebra;
import org.uacalc.alg.SmallAlgebra;
import org.uacalc.alg.conlat.CongruenceLattice;
import org.uacalc.alg.op.Operation;
import org.uacalc.alg.op.OperationSymbol;
import org.uacalc.alg.op.SimilarityType;
import org.uacalc.alg.sublat.SubalgebraLattice;
import org.uacalc.ui.tm.ProgressReport;

public class Field implements SmallAlgebra {

  private final int prime;
  private final int power;
  
  private final int[] polynomial;
  
  public Field(int prime, int power, int[] poly) {
    this.power = power;
    this.prime = prime;
    this.polynomial = poly;
  }
  
  public Field (int prime) {
    this(prime, 1, null);
  }
  
  @Override
  public Set universe() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int cardinality() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int inputSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isUnary() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Iterator iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Operation> operations() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Operation getOperation(OperationSymbol sym) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<OperationSymbol, Operation> getOperationsMap() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setName(String v) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setDescription(String desc) {
    // TODO Auto-generated method stub

  }

  @Override
  public SimilarityType similarityType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateSimilarityType() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isSimilarTo(Algebra alg) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void makeOperationTables() {
    // TODO Auto-generated method stub

  }

  @Override
  public List<Operation> constantOperations() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isIdempotent() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isTotal() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean monitoring() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ProgressReport getMonitor() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setMonitor(ProgressReport monitor) {
    // TODO Auto-generated method stub

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
  public CongruenceLattice con() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SubalgebraLattice sub() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void resetConAndSub() {
    // TODO Auto-generated method stub

  }

  @Override
  public SmallAlgebra parent() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<SmallAlgebra> parents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void convertToDefaultValueOps() {
    // TODO Auto-generated method stub

  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
