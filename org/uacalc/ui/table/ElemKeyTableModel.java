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
    if (alg instanceof SubProductAlgebra) {
      SubProductAlgebra tAlg = (SubProductAlgebra)alg;
      terms = tAlg.getTerms();
      universeList = tAlg.getUniverseList();
    }
    gAlg.setElemKey(this);
  }
  
  public String getColumnName(int col) {
    if (col == 0) return "Index";
    if (col == 1) return "Elem";
    return String.valueOf(col - 2);
  }
  
  
  @Override
  public int getRowCount() {
    return alg.cardinality();
  }

  @Override
  public int getColumnCount() {
    if (universeList == null  || universeList.size() == 0) return 2;
    return 2 + universeList.get(0).universeSize();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    // TODO Auto-generated method stub
    if (columnIndex == 0) return rowIndex;
    
    if (algType == AlgebraType.QUOTIENT) {
      QuotientAlgebra qAlg = (QuotientAlgebra)alg;
      return Arrays.toString(qAlg.getCongruence().getBlocks()[rowIndex]);
    }
    
    if (algType == AlgebraType.SUBALGEBRA) {
      Subalgebra sAlg = (Subalgebra)alg;
      return sAlg.getSubuniverseArray()[rowIndex];
    }
    
    if (algType == AlgebraType.PRODUCT || algType == AlgebraType.POWER) {
      ProductAlgebra pAlg = (ProductAlgebra)alg;
      return Arrays.toString((int[])pAlg.getElement(rowIndex));
       //use Horner; check if too big
    }
    
    if (alg instanceof SubProductAlgebra) {
      if (columnIndex == 1) return terms[rowIndex];
      if (getColumnCount() > 2) {
        return universeList.get(rowIndex).get(columnIndex - 2);
      }
    }
    
    
    return rowIndex; // will just be x | x table.
  }

}














