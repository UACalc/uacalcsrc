
package org.uacalc.io;

import java.io.*;
import java.util.*;
import org.uacalc.alg.*;
import org.uacalc.alg.conlat.*;
import org.uacalc.util.*;

/**
 * Reader and writers.
 *
 * @author Ralph Freese
 * @version $Id$
 */
public final class AlgebraIO {

  private AlgebraIO() {}

  /**
   * Parses the line as a single int but if it starts with "%" it returns
   * -1 so we can have comments.
   */
  public static int parseLine(String line) {
    line = line.trim();
    if (line.startsWith("%")) return -1;
    return Integer.parseInt(line);
  }

  public static SmallAlgebra readAlgebraFile(String f) 
                               throws IOException, BadAlgebraFileException {
    return readAlgebraFile(new File(f));
  }

  public static SmallAlgebra readAlgebraFile(File f) 
                               throws IOException, BadAlgebraFileException {
    String ext = ExtFileFilter.getExtension(f);
    if (ext != null && ExtFileFilter.XML_EXT.equals(ext.toLowerCase())) {
      AlgebraReader r = new AlgebraReader(f);
      try {
        return r.readAlgebraFile();
      }
      catch (org.xml.sax.SAXException saxEx) {
        throw new BadAlgebraFileException("Bad xml file");
      }
      catch (javax.xml.parsers.ParserConfigurationException parEx) {
        throw new BadAlgebraFileException("Bad xml file");
      }
    }

    BufferedReader in = new BufferedReader(new FileReader(f));
    String line = in.readLine();
    if (line == null) throw new BadAlgebraFileException("Nothing in the file");
    List ops = new ArrayList();
    //HashMap map = new HashMap();
    int size = Integer.parseInt(line);
    for (line = in.readLine(); line != null; line = in.readLine()) {
      ops.add(readOp(Integer.parseInt(line), size, in));
    }
    return new BasicAlgebra(f.getName(), size, ops);
  }

  public static Operation readOp(int arity, int size, BufferedReader in)
                     throws IOException, BadAlgebraFileException {
    int h = 1;
    for (int i = 0; i < arity; i++) {
      h = h * size;
    }
//System.out.println("calling readOp, size = " + size 
//+ ", arity = " + arity + ", h = " + h);
    int[] values = new int[h];
    for (int i = 0; i < h; i++) {
      String line = in.readLine();
      if (line == null) throw new BadAlgebraFileException("Bad file");
      values[i] = Integer.parseInt(line);
    }
    return Operations.makeIntOperation(
                OperationSymbol.getOperationSymbol(arity), size, values);
  }

  /**
   * Read in a file like foo.alg file and output foo.xml. 
   */
  public static void convertToXML(String f)
                               throws IOException, BadAlgebraFileException {
    convertToXML(new File(f));
  }

  /**
   * Read in a file like foo.alg file and output foo.xml.
   */
  public static void convertToXML(File f)
                               throws IOException, BadAlgebraFileException {
    String[] sp = ExtFileFilter.splitOffExtension(f);
    if (sp[1] != null 
                   && ExtFileFilter.ALGEBRA_EXT.equals(sp[1].toLowerCase())) {
      SmallAlgebra alg = readAlgebraFile(f);
      AlgebraWriter xmlWriter = 
                new AlgebraWriter(alg, sp[0] + "." + ExtFileFilter.XML_EXT);
      xmlWriter.writeAlgebraXML();
    }
  }

  public static void writeAlgebraFile(SmallAlgebra alg, String f) 
                                                  throws IOException {
    writeAlgebraFile(alg, new File(f), false);
  }

  public static void writeAlgebraFile(SmallAlgebra alg, File f) 
                                                  throws IOException {
    writeAlgebraFile(alg, f, false);
  }

  public static void writeAlgebraFile(SmallAlgebra alg, String f, 
                             boolean oldStyle) throws IOException {
    writeAlgebraFile(alg, new File(f), oldStyle);
  }

  public static void writeAlgebraFile(SmallAlgebra alg, File f, 
                             boolean oldStyle) throws IOException {
    if (!oldStyle) {
      String[] sp = ExtFileFilter.splitOffExtension(f);
      AlgebraWriter xmlWriter;
      if (sp[1] == null && !sp[1].equals(ExtFileFilter.XML_EXT)) {
        xmlWriter = new AlgebraWriter(alg, f + "." + ExtFileFilter.XML_EXT);
      }
      else xmlWriter = new AlgebraWriter(alg, f.toString());
      xmlWriter.writeAlgebraXML();
      return;
    }
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
    final int card = alg.cardinality();
    out.println(Integer.toString(card));
    for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
      Operation op = (Operation)it.next();
      final int arity = op.arity();
      out.println(Integer.toString(arity));
      int opSize = 1;
      for (int i = 0; i < arity; i++) {
        opSize = opSize * card;
      }
      for (int i = 0; i < opSize; i++) {
        final int[] arg = Horner.hornerInv(i, card, arity);
        out.println(Integer.toString(op.intValueAt(arg)));
      }
    }
    out.close();
  }

    

  public static SmallAlgebra readProjectivePlane(InputStream f)
                               throws IOException, BadAlgebraFileException {
    return readProjectivePlane(new BufferedReader(new InputStreamReader(f)));
  }

  public static SmallAlgebra readProjectivePlane(String f)
                               throws IOException, BadAlgebraFileException {
    return readProjectivePlane(new File(f));
  }

  public static SmallAlgebra readProjectivePlane(File f)
                               throws IOException, BadAlgebraFileException {
    return readProjectivePlane(new BufferedReader(new FileReader(f)));
  }

  /**
   * Define a ternary ring from a plane as given at
   *
   *   http://math.uwyo.edu/~moorhous/pub/planes/.
   *
   * The file format is line for each line of the plane (how appropriate)
   * with the points it contains which are the integers 0 to N - 1.
   * We assume the first line is 0, 1, to n.
   * For nondesargian planes it is likely to be the unusual ternary ring,
   * for example it won't be the Hall quasifield in the Hall plane.
   * 
   */
  public static SmallAlgebra readProjectivePlane(BufferedReader in)
                               throws IOException, BadAlgebraFileException {
    final List lines = new ArrayList();
    for (String line = in.readLine(); line != null; line = in.readLine()) {
      String[] toks = line.split("\\s");
      final int k = toks.length;
      int[] linex = new int[k];
      for (int i = 0 ; i < k; i++) {
        linex[i] = Integer.parseInt(toks[i]);
      }
      lines.add(linex);
    }
    // check the first row is the first n + 1 integers
    if (lines.size() == 0) throw new BadAlgebraFileException("nothing in file");
    final int order = ((int[])lines.get(0)).length - 1;
    for (int i = 0 ; i < order + 1; i++) {
      if (i != ((int[])lines.get(0))[i]) {
        throw new BadAlgebraFileException("the first line must be 0, ..., n");
      }
    }

    return null;
  }


  public static void main(String[] args) 
                              throws IOException, BadAlgebraFileException {
    if (args.length == 0) return;

    convertToXML(args[0]);
    
/*
    System.out.println("reading " + args[0]);
    SmallAlgebra alg = AlgebraIO.readAlgebraFile(args[0]);
    System.out.println("The alg \n" + alg);
    System.out.println("writing to /tmp/foo.alg");
    AlgebraIO.writeAlgebraFile(alg, new File("/tmp/foo.alg"), true);
*/
    
/*
    for (Iterator it = alg.operations().iterator(); it.hasNext(); ) {
      System.out.println("op: " + ((Operation)it.next()).symbol());
    }
    //System.out.println("Cg(0,2) = " + alg.con().Cg(0,2));
    long t = System.currentTimeMillis();
    List principals = alg.con().principals();
    t = System.currentTimeMillis() - t;
    System.out.println("found " + alg.con().principals().size() 
                                + " principals in " + t + " milliseconds");
    //TypeFinder tf = new TypeFinder(alg);
*/
/*
    for (Iterator it = principals.iterator(); it.hasNext(); ) {
      BasicPartition beta = (BasicPartition)it.next();
      System.out.println("" + beta);
      if (((BasicCongruenceLattice)alg.con()).joinIrreducible(beta)) {
        Subtrace subtr = tf.findSubtrace(beta);
        System.out.println("subtrace: " + subtr);
        System.out.println("type: " + tf.findType(subtr));
      }
    }
*/
/*
    t = System.currentTimeMillis();
    int k = alg.con().joinIrreducibles().size();
    t = System.currentTimeMillis() - t;
    System.out.println("number of jis is " + k);
    System.out.println("to find the jis it took " + t);
    t = System.currentTimeMillis();
    //System.out.println("size " + alg.con().universe().size());
    //HashSet types = tf.findTypeSet();
    HashSet types = alg.con().typeSet();
    t = System.currentTimeMillis() - t;
    System.out.println("type set = " + types);
    System.out.println("it took " + t);
//System.out.println("Con size = " + alg.con().cardinality());
*/   
/*
    System.out.println(alg + " has " + alg.con().cardinality() 
                           + " congruences");
*/
/*
    t = System.currentTimeMillis();
    HashMap ucMap = ((BasicCongruenceLattice)alg.con()).upperCoversMap();
    t = System.currentTimeMillis() - t;
    System.out.println("upper covers took " + t);
    int covs = 0;
    for (Iterator it = alg.con().universe().iterator(); it.hasNext(); ) {
      BasicPartition part = (BasicPartition)it.next();
      //System.out.println("uc's of " + part + " are");
      //System.out.println("" + ucMap.get(part));
      covs = covs + ((List)ucMap.get(part)).size();
    }
    System.out.println("total covers = " + covs);
*/
  }

/*
  public static class BadAlgebraFileException extends Exception {
    
    public BadAlgebraFileException(String msg) { super(msg); }
  }
*/

}

