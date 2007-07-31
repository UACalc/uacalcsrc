
package org.uacalc.io;

import java.io.*;
import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.alg.op.Operation;
import org.uacalc.util.*;

/**
 * XML writing. Eventually we will have "project" files with multiple
 * algebras. For now we will have just single algebras.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public final class AlgebraWriter {

  public static final String ALGEBRA_TAG = "<algebra>";
  public static final String ALGEBRA_END_TAG = "</algebra>";
  public static final String BASIC_ALGEBRA_TAG = "<basicAlgebra>";
  public static final String BASIC_ALGEBRA_END_TAG = "</basicAlgebra>";
  public static final String PRODUCT_ALGEBRA_TAG = "<productAlgebra>";
  public static final String PRODUCT_ALGEBRA_END_TAG = "</productAlgebra>";
  public static final String POWER_ALGEBRA_TAG = "<powerAlgebra>";
  public static final String POWER_ALGEBRA_END_TAG = "</powerAlgebra>";
  public static final String ROOT_TAG = "<root>";
  public static final String ROOT_END_TAG = "</root>";
  public static final String FACTOR_TAG = "<factor>";
  public static final String FACTOR_END_TAG = "</factor>";
  public static final String FACTORS_TAG = "<factors>";
  public static final String FACTORS_END_TAG = "</factors>";
  public static final String ROOT_FACTORS_TAG = "<rootFactors>";
  public static final String ROOT_FACTORS_END_TAG = "</rootFactors>";
  public static final String SUPER_ALGEBRA_TAG = "<superAlgebra>";
  public static final String SUPER_ALGEBRA_END_TAG = "</superAlgebra>";
  public static final String QUOTIENT_ALGEBRA_TAG = "<quotientAlgebra>";
  public static final String QUOTIENT_ALGEBRA_END_TAG = "</quotientAlgebra>";
  public static final String SUB_ALGEBRA_TAG = "<subAlgebra>";
  public static final String SUB_ALGEBRA_END_TAG = "</subAlgebra>";
  public static final String SUB_UNIVERSE_TAG = "<subUniverse>";
  public static final String SUB_UNIVERSE_END_TAG = "</subUniverse>";
  public static final String BIG_PRODUCT_ALGEBRA_TAG = "<bigProductAlgebra>";
  public static final String BIG_PRODUCT_ALGEBRA_END_TAG = "</bigProductAlgebra>";
  public static final String POWERS_TAG = "<powers>";
  public static final String POWERS_END_TAG = "</powers>";
  public static final String SUB_PRODUCT_ALGEBRA_TAG = "<subProductAlgebra>";
  public static final String SUB_PRODUCT_ALGEBRA_END_TAG = "</subProductAlgebra>";
  public static final String FREE_ALGEBRA_TAG = "<freeAlgebra>";
  public static final String FREE_ALGEBRA_END_TAG = "</freeAlgebra>";
  public static final String CARDINALITY_TAG = "<cardinality>";
  public static final String CARDINALITY_END_TAG = "</cardinality>";
  public static final String POWER_TAG = "<power>";
  public static final String POWER_END_TAG = "</power>";
  public static final String UNIVERSE_TAG = "<universe>";
  public static final String UNIVERSE_END_TAG = "</universe>";
  public static final String OPERATIONS_TAG = "<operations>";
  public static final String OPERATIONS_END_TAG = "</operations>";
  public static final String OPERATION_TAG = "<op>";
  public static final String OPERATION_END_TAG = "</op>";
  public static final String OPERATION_TABLE_TAG = "<opTable>";
  public static final String OPERATION_TABLE_END_TAG = "</opTable>";
  public static final String OPERATION_SYMBOL_TAG = "<opSymbol>";
  public static final String OPERATION_SYMBOL_END_TAG = "</opSymbol>";
  public static final String ARITY_TAG = "<arity>";
  public static final String ARITY_END_TAG = "</arity>";
  public static final String ELEM_TAG = "<elem>";
  public static final String ELEM_END_TAG = "</elem>";
  public static final String ALG_NAME_TAG = "<algName>";
  public static final String ALG_NAME_END_TAG = "</algName>";
  public static final String OP_NAME_TAG = "<opName>";
  public static final String OP_NAME_END_TAG = "</opName>";
  public static final String DESC_TAG = "<desc>";
  public static final String DESC_END_TAG = "</desc>";
  public static final String INT_ARRAY_TAG = "<intArray>";
  public static final String INT_ARRAY_END_TAG = "</intArray>";
  public static final String STRING_ARRAY_TAG = "<stringArray>";
  public static final String STRING_ARRAY_END_TAG = "</stringArray>";
  public static final String CONGRUENCE_TAG = "<congruence>";
  public static final String CONGRUENCE_END_TAG = "</congruence>";
  public static final String PARTITION_TAG = "<partition>";
  public static final String PARTITION_END_TAG = "</partition>";
  public static final String UNIVERS_TAG = "<universe>";
  public static final String UNIVERS_END_TAG = "</universe>";
  public static final String GENERATORS_TAG = "<generators>";
  public static final String GENERATORS_END_TAG = "</generators>";

  public static final String PROD_ELEM_TAG = "<productElem>";
  public static final String PROD_ELEM_END_TAG = "</productElem>";


  private PrintWriter out;
  //private SmallAlgebra algebra;
  private Algebra algebra;
  private int indent = 0;

  public AlgebraWriter(SmallAlgebra alg, PrintWriter out) {
    this.algebra = alg;
    this.out = out;
  }

  public AlgebraWriter(SmallAlgebra alg, String file) throws IOException {
    this.algebra = alg;
    out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
  }

  private void indent() {
    final String two = "  ";
    for (int k = 0; k < indent; k++) {
      out.print(two);
    }
  }

  private void writeTag(String tag) {
    indent();
    out.println(tag);
    indent++;
  }

  private void writeEndTag(String endTag) {
    indent--;
    indent();
    out.println(endTag);
  }

  private void writeBeginEndTag(String tag, String endTag, String value) {
    indent();
    out.print(tag);
    out.print(value);
    out.println(endTag);
  }

  public void writeAlgebraXML() {
    out.println("<?xml version=\"1.0\"?>");
    writeTag(ALGEBRA_TAG);
    writeAlgebra();
    writeEndTag(ALGEBRA_END_TAG);
    out.close();
  }

  public void writeAlgebra() {
    if (algebra instanceof PowerAlgebra) writePowerAlgebra();
    else if (algebra instanceof ProductAlgebra) writeProductAlgebra();
    else if (algebra instanceof QuotientAlgebra) writeQuotientAlgebra();
    else if (algebra instanceof Subalgebra) writeSubalgebra();
    else if (algebra instanceof FreeAlgebra) writeFreeAlgebra();
    else if (algebra instanceof BigProductAlgebra) writeBigProductAlgebra();
    else if (algebra instanceof SubProductAlgebra) writeSubProductAlgebra();
    else writeBasicAlgebra();
  }


  /**
   * XML writing. Eventually we will have "project" files with multiple
   * algebras. For now we will have just single algebras.
   */
  public void writeBasicAlgebra() {
    // we will have other methods for ProductAlgebras, etc.
    //BasicAlgebra alg = (BasicAlgebra)algebra;
    writeTag(BASIC_ALGEBRA_TAG);
    writeAlgName();
    writeDesc();
    writeCardinality();
    // if alg is not instanceof BasicAlgebra, write the universe
    //if (!(algebra instanceof BasicAlgebra) ||
    //         !((BasicAlgebra)algebra).intUniverse()) writeUniverse();
    if (((SmallAlgebra)algebra).getUniverseList() != null) writeUniverse();
    writeTag(OPERATIONS_TAG);
    for (Iterator it = algebra.operations().iterator(); it.hasNext(); ) {
      writeOperation((Operation)it.next());
    }
    writeEndTag(OPERATIONS_END_TAG);
    writeEndTag(BASIC_ALGEBRA_END_TAG);
  }

  private void writePowerAlgebra() {
    PowerAlgebra alg = (PowerAlgebra)algebra;
    writeTag(POWER_ALGEBRA_TAG);
    writeAlgName();
    writeDesc();
    writeCardinality();
    writePower();
    writeTag(ROOT_TAG);
    algebra = alg.getRoot();
    writeAlgebra();
    writeEndTag(ROOT_END_TAG);
    algebra = alg;
    writeEndTag(POWER_ALGEBRA_END_TAG);
  }

  private void writeProductAlgebra() {
    ProductAlgebra alg = (ProductAlgebra)algebra;
    writeTag(PRODUCT_ALGEBRA_TAG);
    writeAlgName();
    writeDesc();
    writeCardinality();
    writeTag(FACTORS_TAG);
    List factors = alg.factors();
    for (Iterator it = factors.iterator(); it.hasNext(); ) {
      algebra = (SmallAlgebra)it.next();
      writeTag(FACTOR_TAG);
      writeAlgebra();
      writeEndTag(FACTOR_END_TAG);
    }
    writeEndTag(FACTORS_END_TAG);
    algebra = alg;
    writeEndTag(PRODUCT_ALGEBRA_END_TAG);
  }

  private void writeBigProductAlgebra() {
    writeTag(BIG_PRODUCT_ALGEBRA_TAG);
    BigProductAlgebra alg = (BigProductAlgebra)algebra;
    writeAlgName();
    writeDesc();
    //writeCardinality();
    writeTag(POWERS_TAG);
      writeIntArray(alg.getPowers(), false);
    writeEndTag(POWERS_END_TAG);
    writeTag(ROOT_FACTORS_TAG);
    //List factors = alg.factors();
    for (Iterator it = alg.rootFactors().iterator(); it.hasNext(); ) {
      algebra = (Algebra)it.next();
      writeTag(FACTOR_TAG);
      writeAlgebra();
      writeEndTag(FACTOR_END_TAG);
    }
    writeEndTag(ROOT_FACTORS_END_TAG);
    algebra = alg;
    writeEndTag(BIG_PRODUCT_ALGEBRA_END_TAG);
  }

  private void writeFreeAlgebra() {
    SubProductAlgebra alg = (SubProductAlgebra)algebra;
    BigProductAlgebra prodAlg = alg.getProductAlgebra();
    writeSubProductAlgebraAux(alg, prodAlg, FREE_ALGEBRA_TAG, 
                                            FREE_ALGEBRA_END_TAG);
  }

// here
  private void writeSubProductAlgebra() {
    SubProductAlgebra alg = (SubProductAlgebra)algebra;
    BigProductAlgebra prodAlg = alg.getProductAlgebra();
    writeSubProductAlgebraAux(alg, prodAlg, SUB_PRODUCT_ALGEBRA_TAG, 
                                            SUB_PRODUCT_ALGEBRA_END_TAG);
  }

  private void writeSubProductAlgebraAux(SubProductAlgebra alg, 
                                         BigProductAlgebra prodAlg,
                                         String tag, String endTag) {
    writeTag(tag);
    writeAlgName();
    writeDesc();
    writeCardinality();
    writeTag(GENERATORS_TAG);
    List gens = alg.generators();
    for (Iterator it = gens.iterator(); it.hasNext(); ) {
      IntArray ia = (IntArray)it.next();
      writeProdElem(ia.toArray());
      //writeIntArray(ia.toArray(), false);
    }
    writeEndTag(GENERATORS_END_TAG);
    List univ = alg.getUniverseList();
    if (univ != null) {
      writeTag(UNIVERSE_TAG);
      for (Iterator it = univ.iterator(); it.hasNext(); ) {
        IntArray ia = (IntArray)it.next();
        writeProdElem(ia.toArray());
        //writeIntArray(ia.toArray(), false);
      }
      writeEndTag(UNIVERSE_END_TAG);
    }

    writeTag(SUPER_ALGEBRA_TAG);
    algebra = prodAlg;
    writeAlgebra();
    algebra = alg;
    writeEndTag(SUPER_ALGEBRA_END_TAG);
    writeEndTag(endTag);
  }

  private void writeQuotientAlgebra() {
    QuotientAlgebra alg = (QuotientAlgebra)algebra;
    SmallAlgebra superAlg = alg.superAlgebra();
    writeTag(QUOTIENT_ALGEBRA_TAG);
    writeAlgName();
    writeDesc();
    writeCardinality();
    writeTag(SUPER_ALGEBRA_TAG);
    algebra = superAlg;
    writeAlgebra();
    writeEndTag(SUPER_ALGEBRA_END_TAG);
    writeTag(CONGRUENCE_TAG);
    writeHumanPartition(alg.getCongruence());
    writeIntArray(alg.getCongruence().toArray());
    writeEndTag(CONGRUENCE_END_TAG);
    writeEndTag(QUOTIENT_ALGEBRA_END_TAG);
    algebra = alg;
  }

  private void writeSubalgebra() {
    Subalgebra alg = (Subalgebra)algebra;
    SmallAlgebra superAlg = alg.superAlgebra();
    writeTag(SUB_ALGEBRA_TAG);
    writeAlgName();
    writeDesc();
    writeCardinality();
    writeTag(SUPER_ALGEBRA_TAG);
    algebra = superAlg;
    writeAlgebra();
    algebra = alg;
    writeEndTag(SUPER_ALGEBRA_END_TAG);
    writeTag(SUB_UNIVERSE_TAG);
    writeIntArray(alg.getSubuniverseArray());
    writeEndTag(SUB_UNIVERSE_END_TAG);
    writeEndTag(SUB_ALGEBRA_END_TAG);
  }

  private void writeCardinality() {
    writeBeginEndTag(CARDINALITY_TAG, CARDINALITY_END_TAG, 
                     String.valueOf(algebra.cardinality()));
  }

  private void writePower() {
    writeBeginEndTag(POWER_TAG, POWER_END_TAG, 
                     String.valueOf(((PowerAlgebra)algebra).getPower()));
  }

  private void writeAlgName() {
    String name = algebra.name();
    if (name != null && name.length() > 0) {
      writeBeginEndTag(ALG_NAME_TAG, ALG_NAME_END_TAG, name);
    }
  }

  //private void writeOpName(String name) {
    //writeBeginEndTag(OP_NAME_TAG, OP_NAME_END_TAG, name);
  //}

  private void writeDesc() {
    writeDesc(algebra.description());
  }

  private void writeDesc(String desc) {
    if (desc != null && desc.length() > 0) {
      writeBeginEndTag(DESC_TAG, DESC_END_TAG, desc);
    }
  }

  private void writeOperation(Operation op) {
    final int[] arg = new int[op.arity()];
    ArrayIncrementor inc = 
       SequenceGenerator.sequenceIncrementor(arg, algebra.cardinality() - 1);
    writeTag(OPERATION_TAG);
    // write the symbol
    writeTag(OPERATION_SYMBOL_TAG);
    writeBeginEndTag(OP_NAME_TAG, OP_NAME_END_TAG, op.symbol().name());
    writeBeginEndTag(ARITY_TAG, ARITY_END_TAG, String.valueOf(op.arity()));
    writeEndTag(OPERATION_SYMBOL_END_TAG);
    
    writeTag(OPERATION_TABLE_TAG);
/*
    while (true) {
      writeBeginEndTag(ELEM_TAG, ELEM_END_TAG, 
                         String.valueOf(op.intValueAt(arg)));
      if (!inc.increment()) break;
    }
*/
    final int size = algebra.cardinality();
    int h = 1;
    for (int i = 0; i < op.arity(); i++) {
      h = h * size;
    }
    final int[] arr = new int[h];
    int k = 0;
    while (true) {
      arr[k++] = op.intValueAt(arg);
      if (!inc.increment()) break;
    }
    //writeIntArray(arr);
    writeOpArray(arr, op.arity());
    writeEndTag(OPERATION_TABLE_END_TAG);
    writeEndTag(OPERATION_END_TAG);
  }

  private void writeOpArray(final int[] arr, final int arity) {
      final int card = algebra.cardinality();
      writeTag(INT_ARRAY_TAG);
      for (int i = 0; i < arr.length; i = i + card) {
        writeRow(i, arr, arity);
      }
      writeEndTag(INT_ARRAY_END_TAG);
  }

  private void writeRow(final int index, final int[] arr, final int arity) {
    final int card = algebra.cardinality();
    if (arity == 0) {
      indent();
      out.println("<row>" + arr[0] + "</row>");
      return;
    }
    StringBuffer sb = new StringBuffer();
    for (int j = 0; j < card; j++) {
      sb.append(String.valueOf(arr[index + j]));
      if (j < card - 1) sb.append(",");
    }
    String row = sb.toString();
    indent();
    if (arity == 1) out.println("<row>" + row + "</row>");
    else {
      final int[] indexArr = Horner.hornerInv(index / card, card, arity - 1);
      // we reverse this so the order is more human readable that the default
      // order used in the original program.
      final String indexString 
                      = ArrayString.toString(Horner.reverseArray(indexArr));
      out.print("<row r=\"");
      out.print(indexString);
      out.print("\">");
      out.print(row);
      out.println("</row>");
    }
  }
  private void writeUniverse() {
    writeTag(UNIVERSE_TAG);
    //final String[] arr = new String[algebra.cardinality()];
    List univ = ((SmallAlgebra)algebra).getUniverseList();
    for (int i = 0; i < algebra.cardinality(); i++) {
      //arr[i] = algebra.getElement(i).toString();
      //writeBeginEndTag(ELEM_TAG, ELEM_END_TAG, 
      //    ((SmallAlgebra)algebra).getElement(i).toString()); 
      writeBeginEndTag(ELEM_TAG, ELEM_END_TAG, univ.get(i).toString()); 
    }
    //writeStringArray(arr);
    writeEndTag(UNIVERSE_END_TAG);
  }

  // for now ProductElements are just IntArray and we just get the
  // arrays. Change this after we have a real ProductElement object.
  private void writeProdElem(int[] arr) {
    writeTag(PROD_ELEM_TAG);
    indent();
    for (int i = 0; i < arr.length - 1; ) {
      out.print(String.valueOf(arr[i]));
      out.print(",");
      i++;
    }
    out.println(String.valueOf(arr[arr.length - 1]));
    writeEndTag(PROD_ELEM_END_TAG);
  }

  private void writeIntArray(int[] arr) {
    writeIntArray(arr, true);
  }


  private void writeIntArray(int[] arr, boolean lineBreak) {
    writeTag(INT_ARRAY_TAG);
    final int card = algebra.cardinality();
    indent();
    for (int i = 0; i < arr.length - 1; ) {
      out.print(String.valueOf(arr[i]));
      out.print(",");
      i++;
      if (lineBreak && i % card == 0) {
        out.println();
        indent();
      }
    }
    out.println(String.valueOf(arr[arr.length - 1]));
    writeEndTag(INT_ARRAY_END_TAG);
  }

  private void writeHumanPartition(Partition part) {
    writeTag(PARTITION_TAG);
    indent();
    out.println(part.toString(Partition.BLOCK));
    writeEndTag(PARTITION_END_TAG);
  }

/*
  private void writeStringArray(String[] arr) {
    writeTag(STRING_ARRAY_TAG);
    indent();
    for (int i = 0; i < arr.length - 1; ) {
      out.print(arr[i]);
      out.print(",");
      i++;
      if (i % 30 == 0) {
        out.println();
        indent();
      }
    }
    out.println(String.valueOf(arr[arr.length - 1]));
    writeEndTag(STRING_ARRAY_END_TAG);
  }
*/

  public static void main(String[] args) 
                              throws IOException, BadAlgebraFileException {
    if (args.length == 0) return;
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = AlgebraIO.readAlgebraFile(args[0]);
    AlgebraWriter xmlWriter = new AlgebraWriter(alg, "/tmp/foo.xml");
    xmlWriter.writeAlgebraXML();
    System.out.println("/tmp/foo.xml written");
  }

}

