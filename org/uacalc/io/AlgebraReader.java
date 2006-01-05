
package org.uacalc.io;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.util.*;

/**
 * XML reading. Eventually we will have "project" files with multiple
 * algebras. For now we will have just single algebras.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public final class AlgebraReader extends DefaultHandler {


  // maybe these should be in SmallAlgebra
  public static final int BASIC = 0;
  public static final int PRODUCT = 1;
  public static final int QUOTIENT = 2;
  public static final int SUBALGEBRA = 3;
  public static final int POWER = 4;

  private File file;
  private SmallAlgebra algebra;
  private int algType;
  private SimpleList tagStack = SimpleList.EMPTY_LIST;
  private String algName;
  private String opName;
  private int cardinality;
  private int arity;
  private int power;
  private OperationSymbol opSym;
  private Operation op;
  private List ops = new ArrayList();
  private int[] intArray;
  private int intArrayIndex;
  private List universe = new ArrayList();
  private List factors = new ArrayList();
  private SmallAlgebra superAlgebra;
  private SmallAlgebra rootAlgebra;
  private Partition congruence;
  private int[] subUniverse;

  public AlgebraReader(File file) throws IOException {
    this.file = file;
  }

  public AlgebraReader(String file) throws IOException {
    this(new File(file));
  }

  public SmallAlgebra readAlgebraFile() throws IOException, SAXException,
                                               ParserConfigurationException {
    // Use an instance of ourselves as the SAX event handler
    DefaultHandler handler = this;
    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();
    saxParser.parse(file, handler);
    return algebra;
  }

  private String currentTag() {
    return (String)tagStack.first();
  }

  private String parentTag() {
    if (tagStack.rest().isEmpty()) return null;
    return (String)tagStack.rest().first();
  }


  private void intRow(final String str) {
    String[] strArr = str.split(",\\s*");
    final int n = strArr.length;
    for (int i = 0; i < n; i++) {
      intArray[intArrayIndex + i] = Integer.parseInt(strArr[i]);
    } 
    intArrayIndex = intArrayIndex + n;
  }

  private int[] intArray(String str) {
    String[] strArr = str.split(",\\s*");
    final int n = strArr.length;
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      ans[i] = Integer.parseInt(strArr[i]);
    } 
    return ans;
  }

  public void startElement(String namespaceURI,
                             String lName, // local name
                             String qName, // qualified name
                             Attributes attrs) throws SAXException {
    String elemName = lName; // element name
    if ("".equals(elemName)) elemName = qName; // namespaceAware = false
    //System.out.println("elem is " + elemName);
    tagStack = tagStack.cons(elemName);
    if ("basicAlgebra".equals(elemName)) algType = BASIC;
    if ("powerAlgebra".equals(elemName)) algType = POWER;
    if ("productAlgebra".equals(elemName)) algType = PRODUCT;
    if ("quotientAlgebra".equals(elemName)) algType = QUOTIENT;
    if ("subAlgebra".equals(elemName)) algType = SUBALGEBRA;
    if ("opTable".equals(elemName)) {
      int h = 1;
      for (int i = 0; i < arity; i++ ) {
        h = h * cardinality;
      }
      intArray = new int[h];
      intArrayIndex = 0;
    }
    if ("congruence".equals(elemName)) {
      intArray = new int[cardinality];
      intArrayIndex = 0;
    }
  }

  public void characters(char buf[], int offset, int len) throws SAXException {
    String s = new String(buf, offset, len);
    s = s.trim();
    if ("algName".equals(currentTag())) algName = s;
    if ("opName".equals(currentTag())) opName = s;
    //if ("desc".equals(currentTag())) desc = s;
    if ("cardinality".equals(currentTag())) cardinality = Integer.parseInt(s);
    if ("arity".equals(currentTag())) arity = Integer.parseInt(s);
    if ("power".equals(currentTag())) power = Integer.parseInt(s);
    if ("row".equals(currentTag())) intRow(s);
    if ("intArray".equals(currentTag()) 
              && "congruence".equals(parentTag()) && s.length() > 0) {
      intArray = intArray(s);
    }
    //if ("intArray".equals(currentTag())) intArray = intArray(s);
    // universe here
  }

  public void endElement(String namespaceURI, String lName, String qName) 
                                                       throws SAXException {
    tagStack = tagStack.rest();
    String elemName = lName; // element name
    if ("".equals(elemName)) elemName = qName; // namespaceAware = false
    if ("op".equals(elemName)) {
      ops.add(Operations.makeIntOperation(opName, arity, cardinality, 
                   Horner.leftRightReverse(intArray, cardinality, arity)));
    }
    if ("subUniverse".equals(elemName)) {
      subUniverse = intArray;
    }
    if ("congruence".equals(elemName)) {
      congruence = new BasicPartition(intArray);
    }

    if ("basicAlgebra".equals(elemName)) {
      // need to see if universe exists

//System.out.println("cardinality is " + cardinality);
//System.out.println("ops has length " + ops.size());
      algebra = new BasicAlgebra(algName, cardinality, ops);
      algName = null;
    }
    if ("factor".equals(elemName)) {
      factors.add(algebra);
    }
    if ("root".equals(elemName)) {
      rootAlgebra = algebra;
    }
    if ("superAlgebra".equals(elemName)) {
      superAlgebra = algebra;
    }
    if ("powerAlgebra".equals(elemName)) {
      if (algName == null) {
        algebra = new PowerAlgebra(rootAlgebra, power);
      }
      else algebra = new PowerAlgebra(algName, rootAlgebra, power);
      algName = null;
    }
      
    if ("productAlgebra".equals(elemName)) {
      if (algName != null) algebra = new ProductAlgebra(algName, factors);
      else  algebra = new ProductAlgebra(factors);
      algName = null;
      factors.clear();
    }
    if ("subAlgebra".equals(elemName)) {
      if (algName == null) {
        algebra = new Subalgebra(superAlgebra, subUniverse);
      }
      else algebra = new Subalgebra(algName, superAlgebra, subUniverse);
      algName = null;
    }
    if ("quotientAlgebra".equals(elemName)) {
      if (algName == null) {
        algebra = new QuotientAlgebra(superAlgebra, congruence);
      }
      else algebra = new QuotientAlgebra(algName, superAlgebra, congruence);
      algName = null;
    }
  }

 
  public static void main(String[] args) throws ParserConfigurationException, 
                          SAXException, IOException, BadAlgebraFileException {
    //if (args.length == 0) return;
    //System.out.println("reading " + args[0]);
    AlgebraReader r = new AlgebraReader("/tmp/foo.xml");
    SmallAlgebra alg = r.readAlgebraFile();
    System.out.println("alg has size " + alg.cardinality());
    System.out.println("alg.con jis " + alg.con().joinIrreducibles().size());
    AlgebraWriter xmlWriter = new AlgebraWriter(alg, "/tmp/hoo.xml");
    xmlWriter.writeAlgebraXML();
    System.out.println("/tmp/hoo.xml written");
  }

}

