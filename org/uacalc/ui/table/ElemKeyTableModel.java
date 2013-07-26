package org.uacalc.ui.table;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.uacalc.ui.util.*;
import org.uacalc.alg.*;
import org.uacalc.terms.*;
import org.uacalc.util.*;
import org.uacalc.alg.SmallAlgebra.AlgebraType;
import org.uacalc.alg.conlat.Partition;

public class ElemKeyTableModel extends AbstractTableModel {
  
  private Term[] terms;
  private List<IntArray> universeList;

  private final GUIAlgebra gAlg;
  private final SmallAlgebra alg;
  private final AlgebraType algType;

  
  public ElemKeyTableModel(GUIAlgebra gAlg) {
    this.gAlg = gAlg;
    this.alg = gAlg.getAlgebra();
    this.algType = alg.algebraType();
    gAlg.setElemKey(this);
  }
  
  
  
  @Override
  public int getRowCount() {
    return alg.cardinality();
  }

  @Override
  public int getColumnCount() {
    // TODO Auto-generated method stub
    // need to handle the case alg is a SubProduct
    return 2;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    // TODO Auto-generated method stub
    if (columnIndex == 0) return rowIndex;
    
    if (algType == AlgebraType.QUOTIENT) {
      QuotientAlgebra qAlg = (QuotientAlgebra)alg;
      return qAlg.getCongruence().getBlocks()[rowIndex];
    }
    
    if (algType == AlgebraType.SUBALGEBRA) {
      Subalgebra sAlg = (Subalgebra)alg;
      return sAlg.getSubuniverseArray()[rowIndex];
    }
    
    //if (algType == AlgebraType.PRODUCT) {
      // use Horner; check if too big
    //}
    
    
    
    return rowIndex; // will just be x | x table.
  }

}














