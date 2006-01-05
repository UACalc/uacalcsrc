/* 
 * SimpleList.java	98/1/3 Ralph Freese
 */

package org.uacalc.util;


import java.util.*;
import java.lang.reflect.*;
import java.io.*;

/**
 * Simple Linked lists. Java's collection framework has LinkedList's but
 * I need to guarentee that there is a great deal of sharing to save
 * space (memory).
 * For example, the space required to hold all subsets of an 
 * <i>n</i> element set requires space proportional to 
 * (<i>n</i>/2) 2<i><sup>n</sup></i> using vectors, but only
 * 2<i><sup>n</sup></i> with linked lists.
 * <p>
 * This version just has an element and a pointer to the rest, which
 * is another LinkedList. This means push and pop not are supported
 * but rest does not have to make a new object.
 * <p>
 * The rest of the empty list is itself.
 * <p>
 * This class implements <code>java.util.List</code>.
 * <p>
 * <b>Efficiency.</b> size() takes time proportional to the size and
 * get(int i) takes time proportional to i. <b>Moral:</b> do not use 
 * for loops to iterator over the elements; use the iterator. The former
 * uses time proportional to <i>n</i><sup>2</sup> while the latter 
 * uses only <i>n</i>.
 *
 *
 */
public class SimpleList implements Cloneable, java.util.List, 
                                              java.io.Serializable {

  protected transient Object first;
  protected transient SimpleList rest;
  //protected Object first;
  //protected SimpleList rest;

  /**
   * The empty list is a class constant
   */
  public static EmptyList EMPTY_LIST = new EmptyList();

  public static EmptyList emptyList = EMPTY_LIST;

  private SimpleList() {}

  /**
   * Constructs a list with only obj.
   * @param obj The Object
   */
/*
  public SimpleList(Object obj) {
    first = obj;
    rest = emptyList;
  }
*/

/**
 * Constructs a list with obj followed by list. The same as cons in Lisp.
 *
 * @param obj The Object to be first.
 * @param list The List to be the rest.
 */
  public SimpleList(Object obj, SimpleList list) {
    first = obj;
    rest = list;
  }

  public SimpleList(Collection c) {
    SimpleList tmp = emptyList;
    for(Iterator it = c.iterator(); it.hasNext();) {
      tmp = tmp.cons(it.next());
    }
    tmp = tmp.reverse();
    first = tmp.first;
    rest = tmp.rest;
  }

  /**
   * Save the state of the <tt>SimpleList</tt> instance to a stream (that
   * is, serialize it).
   *
   * @serialData The tree of nodes is traversed in a depth-first seach
   *      assigning a number as a handle to each node. Due to sharing a
   *      node may be reached more than once so a node may have more than
   *      one handle but when it is reach the second time, the search does
   *      not descend. 
   *      <p>
   *      The serialized data is an int array followed by a HashMap.
   *      The int array <tt>a</tt> encodes the structure as 
   *      follows. Suppose a certain
   *      node was accessed as above by handles 3, 8, and 12. Then
   *      <tt>a</tt>[8] = <tt>a</tt>[12] = 3. For all i except 
   *      these second time handles, <tt>a</tt>[i] is 
   *      <ul>
   *        <li>-1 if the node for i is a nonempty SimpleList,</li>
   *        <li>-2 if the node for i is the emptyList,</li>
   *        <li>-3 if the node for i is some other Object.</li>
   *      </ul>
   *      For each node in the last case, there is an entry on the 
   *      HashMap from the Integer value of the handle to the Object.
   */
  private synchronized void writeObject(ObjectOutputStream s) 
                                                   throws IOException {
    s.defaultWriteObject();

    //ArrayList handleToObj = new ArrayList();
    //HashMap wrapMap = new HashMap();
    HashMap objMap = new HashMap();
    int[] array = null;

    if (!isEmpty()) {
      List handles = new ArrayList();
      setHandles(this, handles);
      array = new int[handles.size()];
      setArrayAndObjMap(handles, array, objMap);
/*
for (int i = 0; i < array.length; i++) {
  System.out.println("array at " + i + " is " + array[i]);
}
*/
    }
    s.writeObject(array);
    s.writeObject(objMap);
  }

  static final int SIMPLE_LIST_TYPE = -1;
  static final int EMPTY_LIST_TYPE = -2;
  static final int OTHER_TYPE = -3;

/*
  private final static class StackData {

    SimpleList list;
    boolean firstDone = false;

    StackData(SimpleList lst) {
      this.list = lst;
    }

  }
*/

  private void setHandles(SimpleList lst, List handles) {
    // assert lst is not emptyList.
    class StackData {

      SimpleList list;
      boolean firstDone = false;

      StackData(SimpleList lst) {
        this.list = lst;
      }

    }

    SimpleList stack = emptyList;
    stack = stack.cons(new StackData(lst));
    int count = 0;
    int handle;
    HashMap wrapMap = new HashMap();
    while (!stack.isEmpty()) {
      StackData sd = (StackData)stack.first;
/*
System.out.println("sd.list is " + sd.list 
     + ", sd.firstDone is " + sd.firstDone + ", count is " + count);
*/

      lst = sd.list;
      if (!sd.firstDone) {
        sd.firstDone = true;
        //stack = stack.rest;
        handle = count++;
        if (lst.first instanceof SimpleList) {
          if (((SimpleList)lst.first).isEmpty()) {
            handles.add(Wrap.EMPTY_LIST_WRAP);
          }
          else {
            Wrap wrap = new Wrap(lst.first, handle, SIMPLE_LIST_TYPE);
            Wrap wrap2 = (Wrap)wrapMap.get(wrap);
            if (wrap2 != null) {
              handles.add(wrap2);
            }
            else {
              wrapMap.put(wrap, wrap);
              wrap2 = wrap;
              handles.add(wrap2);
              //setHandles((SimpleList)lst.first);
              stack = stack.cons(new StackData((SimpleList)lst.first));
              continue;
            }
          }
        }
        else {
          handles.add(new Wrap(lst.first, handle, OTHER_TYPE));
        }
      }
      stack = stack.rest;  // pop the stack here
      handle = count++;
      if (lst.rest.isEmpty()) {
        handles.add(Wrap.EMPTY_LIST_WRAP);
      }
      else {
        Wrap wrap = new Wrap(lst.rest, handle, SIMPLE_LIST_TYPE);
        Wrap wrap2 = (Wrap)wrapMap.get(wrap);
        if (wrap2 != null) {
          handles.add(wrap2);
        }
        else {
          wrapMap.put(wrap, wrap);
          wrap2 = wrap;
          handles.add(wrap2);
          //setHandles(lst.rest);
          stack = stack.cons(new StackData(lst.rest));
          continue;
        }
      }
    }
  }
    
  void setArrayAndObjMap(List handleToObj, int[] array, HashMap objMap) {
    int n = array.length;
    for (int i = 0; i < n; i++) {
      Wrap wrap = (Wrap)handleToObj.get(i);
      if (wrap.type == OTHER_TYPE) objMap.put(new Integer(i), wrap.obj);
      if (wrap.type != SIMPLE_LIST_TYPE) array[i] = wrap.type;
      else {
        if (wrap.handle == i) array[i] = SIMPLE_LIST_TYPE;
        else array[i] = wrap.handle;
      }
    }
  }


  /**
   * Reconstitute the <tt>SimpleList</tt> instance from a stream (that is,
   * deserialize it).
   */
  private synchronized void readObject(ObjectInputStream s) 
                              throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    int[] array = (int[])s.readObject();
    HashMap objMap = (HashMap)s.readObject();
    readObject1(this, array, objMap);
  }


  private void readObject1(SimpleList lst, int[] array, HashMap objMap) {

System.out.println("array has size " + array.length);
    HashMap handleToList = new HashMap();
    class StackData {

      SimpleList list;
      boolean firstDone = false;

      StackData(SimpleList lst) {
        this.list = lst;
      }
    }

    SimpleList stack = emptyList;
    int count = 0;
    stack = stack.cons(new StackData(lst));
    int handle;
    while (!stack.isEmpty()) {
      StackData sd = (StackData)stack.first;
      lst = sd.list;
      if (!sd.firstDone) {
        sd.firstDone = true;
        //stack = stack.rest;
        handle = count++;
System.out.println("xstack size = " + stack.size() + ", handle = " + handle);
        handleToList.put(new Integer(handle), lst);
        int type = array[handle];
System.out.println("handle = " + handle + ", type = " + type);

        switch (type) {
          case SIMPLE_LIST_TYPE:
            lst.first = new SimpleList();
            lst = (SimpleList)lst.first;
            stack = stack.cons(new StackData(lst));
            continue;
          case EMPTY_LIST_TYPE:
            lst.first = emptyList;
            break;
          case OTHER_TYPE:
            lst.first = objMap.get(new Integer(handle));
            break;
          default:  
            // type is >= 0 so points to another handle for a SimpleList.
            lst.first = handleToList.get(new Integer(type));
        }
      }
      stack = stack.rest;  // pop the stack here
      handle = count++;

      int type = array[handle];
System.out.println("handle = " + handle + ", type = " + type);
      switch (type) {
        case SIMPLE_LIST_TYPE:
          lst.rest = new SimpleList();
          lst = lst.rest;
          handleToList.put(new Integer(handle), lst);
          stack = stack.cons(new StackData(lst));
          continue;
        case EMPTY_LIST_TYPE:
          lst.rest = emptyList;
          break;
        default:  // type is >= 0 so points to another handle for a SL.
          lst.rest = (SimpleList)handleToList.get(new Integer(type));
      }
    } 
  }


/*
  private void readObject1(SimpleList lst, int[] count, int countvalue,
                       int[] array, HashMap objMap, HashMap handleToList) {
    // parentLoc must be a cons cell
    Integer parentLoc = new Integer(countvalue);
    handleToList.put(parentLoc, lst);
    int handle = count[0]++;
    int type = array[handle];
    switch (type) {
      case SIMPLE_LIST_TYPE:
        lst.first = new SimpleList();
        readObject1((SimpleList)lst.first, count, handle, 
                                     array, objMap, handleToList);
        break;
      case EMPTY_LIST_TYPE:
        lst.first = emptyList;
        break;
      case OTHER_TYPE:
        lst.first = objMap.get(new Integer(handle));
        break;
      default:  // type is >= 0 so points to another handle for a SL.
        lst.first = handleToList.get(new Integer(type));
    }
    handle = count[0]++;
    type = array[handle];
    switch (type) {
      case SIMPLE_LIST_TYPE:
        lst.rest = new SimpleList();
        readObject1(lst.rest, count, handle, array, objMap, handleToList);
        break;
      case EMPTY_LIST_TYPE:
        lst.rest = emptyList;
        break;
      default:  // type is >= 0 so points to another handle for a SL.
        lst.rest = (SimpleList)handleToList.get(new Integer(type));
    }
  } 
*/

  private static final class Wrap {

    Object obj;
    int handle;   // the *first* handle
    int type;


    static Wrap EMPTY_LIST_WRAP = new Wrap(emptyList, -1, EMPTY_LIST_TYPE);

    Wrap(Object obj, int handle, int type) {
      this.obj = obj;
      this.handle = handle;
      this.type = type;
    }

    public boolean equals(Object o) {
      if (! (o instanceof Wrap)) return false;
      Wrap w = (Wrap)o;
      return w.obj == this.obj;
    }

    public int hashCode() {
      return System.identityHashCode(this.obj);
    }
  }

  public static SimpleList makeList() {
    return emptyList;
  }

  public static SimpleList makeList(Object obj) {
    return emptyList.cons(obj);
  }

  public boolean isEmpty() {
    return false;
  }

  /**
   * The size of the list. It is inefficient since it takes time 
   * proportional to the size. 
   */
  public int size() {
    if (isEmpty()) return 0;
    return 1 + rest.size();
  }

  public Object first() {
    return first;
  }

  public SimpleList rest() {
    return rest;
  }

  public SimpleList cons(Object obj) {
    return new SimpleList(obj, this);
  }

  public Enumeration elements() {
    return new EnumerationSimpleList();
  }

  public Iterator iterator() { 
    return new EnumerationSimpleList();
  }

  /**
   * This Iterator will iterate through the list until it reaches
   * <tt>tail</tt> or to the end if tail is not found. Note 
   * <tt>tail</tt> must be == to a tail of the list.
   *
   * @param tail   a list == to a tail of the list.
   */
  public Iterator frontIterator(SimpleList tail) { 
    return new FrontIterator(tail);
  }


/*
  public ReusableIterator getIterator() {
    return new EnumerationSimpleList();
  }
*/

  public Iterator getIterator() {
    return new EnumerationSimpleList();
  }

  public SimpleList copyList() {
    if (isEmpty()) { 
      return emptyList ; 
    } else {
      return new SimpleList (first, rest.copyList()) ;
    }
  }

  public Object clone() {
    return copyList() ;
  }

  /**
   * This corresponds to <code>(APPEND this lst)</code> in lisp.
   */ 
  public SimpleList append(SimpleList lst) {
    if (isEmpty()) return lst;
    return new SimpleList(first(),rest().append(lst));
  }
   
  public SimpleList reverse() {
    return reverse(emptyList);
  }

  /**
   * This is revappend in Common Lisp. It produces 
   * <code>
   * (APPEND (REVERSE this) lst) 
   * </code>
   */ 
  public SimpleList reverse(SimpleList lst) {
    if (isEmpty()) return lst;
    SimpleList ans = lst;
    Enumeration list = (EnumerationSimpleList)this.elements(); 
    while (list.hasMoreElements()) { 
      ans = new SimpleList(list.nextElement(), ans);
    }
    return ans;
  }

  public String toString() { 
    StringBuffer sb = new StringBuffer("(");
    for(Iterator it = iterator(); it.hasNext(); ) {
      sb.append(it.next());
      if (it.hasNext()) sb.append(" ");
    }
    sb.append(")");
    return sb.toString();
  }

  // methods demanded by List interface, most not real
  // some of these should be implemented

  /**
   * This just throws an UnsupportedOperationException.
   */
  public void add(int index, Object elt) throws 
               UnsupportedOperationException, ClassCastException,
               IllegalArgumentException, IndexOutOfBoundsException {
    throw new UnsupportedOperationException();
  }

  /**
   * This just throws an UnsupportedOperationException.
   */
  public boolean add(Object elt) throws UnsupportedOperationException, 
               ClassCastException, IllegalArgumentException {
    throw new UnsupportedOperationException();
  }

  /**
   * This just throws an UnsupportedOperationException.
   */
  public boolean addAll(int index, Collection c) throws 
               UnsupportedOperationException, ClassCastException,
               IllegalArgumentException, IndexOutOfBoundsException {
System.out.println("addAll called with index = " + index);
    throw new 
        UnsupportedOperationException("SimpleList does not support addAll");
  }

  public boolean addAll(Collection c) throws UnsupportedOperationException, 
               ClassCastException, IllegalArgumentException {
System.out.println("addAll called");
    throw new 
        UnsupportedOperationException("SimpleList does not support addAll");
  }

  /**
   * This just throws an UnsupportedOperationException.
   */
  public void clear() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  public boolean contains(Object o) { 
    Iterator it = iterator();
    while(it.hasNext()) {
      if (o.equals(it.next())) return true;
    }
    return false;
  }

  public boolean containsAll(Collection c) {
    Iterator it = c.iterator();
    while(it.hasNext()) {
      if (!contains(it.next())) return false;
    }
    return true;
  }

/*
  public boolean equals(Object o) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }
*/

/*
  public Object get(int i) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }
*/

  public Object get(int i) throws IndexOutOfBoundsException {
    if (i < 0 || i >= size()) throw new IndexOutOfBoundsException();
    Iterator it = iterator();
    int j = 0;
    while(it.hasNext()) {
      if (j++ == i) return it.next();
      it.next();
    }
    return null;		// can't happen
  }

  public int indexOf(Object o) { 
    Iterator it = iterator();
    int j = 0;
    while(it.hasNext()) {
      if (o.equals(it.next())) return j;
      j++;
    }
    return -1;
  }

  public int lastIndexOf(Object o) { 
    Iterator it = iterator();
    int j = 0;
    int ans = -1;
    while(it.hasNext()) {
      if (o.equals(it.next())) ans = j;
      j++;
    }
    return ans;
  }

  public Object remove(int i) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }

  public boolean remove(Object i) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection i) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection i) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }

  public Object set(int i, Object o) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }

/*
  public Object set(int i, Object o) throws IndexOutOfBoundsException { 
    if (i < 0 || i >= size()) throw new IndexOutOfBoundsException();
    SimpleList lst = this;
    for (int j = 0; j < i; j++) {
      lst = lst.rest();
    }
    Object ans = lst.first;
    lst.first = o;
    return ans;
  }
*/

/*
  public Object[] toArray(Object[] a) throws UnsupportedOperationException { 
    throw new UnsupportedOperationException();
  }
*/
  
  public Object[] toArray(Object[] a) { 
    int size = size();
    int aSize = a.length;
    if (size > aSize) {
      Class c = a.getClass().getComponentType();
      a = (Object[])Array.newInstance(c, size);
    }
    int i = 0;
    Iterator it = iterator();
    while (it.hasNext()) {
      a[i] = it.next();
      i++;
    }
    for (int j = i; j < aSize; j++) {
      a[j] = null;
    }
    return a;
  }
  
  public Object[] toArray() { 
    //throw new UnsupportedOperationException();
    Iterator it = iterator();
    Object[] ans = new Object[size()];
    int i = 0;
    while (it.hasNext()) {
      ans[i] = it.next();
      i++;
    }
    return ans;
  }

  public java.util.List subList(int i, int j) {
    int k = 0;
    SimpleList ans = emptyList;
    Iterator it = iterator();
    while (it.hasNext()) {
      if (i <= k && k < j) ans = ans.cons(it.next());
    }
    return ans.reverse();
  }

  public ListIterator listIterator(int i) {
    return new ListIteratorSimpleList(i);
  }

  public ListIterator listIterator() {
    return new ListIteratorSimpleList();
  }

  
  private static class EmptyList extends SimpleList {

    public EmptyList() {
      first = null;
      rest = this;
    }

    /**
     * Having this insures that the emptyList is unique in the JVM.
     */
    private Object readResolve() throws ObjectStreamException {
      return emptyList;
    }

    public boolean isEmpty() {
      return true;
    }

  }



  private class ListIteratorSimpleList implements ListIterator {
    private ArrayList alist;
    private ListIterator iter;

    ListIteratorSimpleList() {
      alist = new ArrayList(SimpleList.this);
      iter = alist.listIterator();
    }

    ListIteratorSimpleList(int i) {
      alist = new ArrayList(SimpleList.this);
      iter = alist.listIterator(i);
    }

    public boolean hasNext() {
      return iter.hasNext();
    }

    public Object next() {
      return iter.next();
    }

    public int nextIndex() {
      return iter.nextIndex();
    }

    public boolean hasPrevious() {
      return iter.hasPrevious();
    }

    public Object previous() {
      return iter.previous();
    }

    public int previousIndex() {
      return iter.previousIndex();
    }

    public void add(Object o) {
      throw new UnsupportedOperationException();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public void set(Object o) {
      throw new UnsupportedOperationException();
    }

  }

  private class FrontIterator implements Iterator {

    private SimpleList llist;
    private SimpleList tail;

    FrontIterator(SimpleList tailx) {
      llist = SimpleList.this;
      tail = tailx;
    }

    public boolean hasNext() {
      return (((llist != tail)) && (! llist.isEmpty()));
    }

    public Object next() {
      Object obj = llist.first();
      llist = llist.rest();
      return obj;
    }

    public void remove () {
      throw new UnsupportedOperationException();
    }

  }
  

  // was ReusableIterator
  private class EnumerationSimpleList implements Enumeration, Iterator {
    private SimpleList llist;

    EnumerationSimpleList() {
      llist = SimpleList.this;
    }

    public boolean hasMoreElements() {
      return (! (llist.isEmpty()));
    }

    public boolean hasNext() {
      return (! (llist.isEmpty()));
    }

    public Object nextElement() {
      Object obj = llist.first();
      llist = llist.rest();
      return obj;
    }

    public Object next() {
      Object obj = llist.first();
      llist = llist.rest();
      return obj;
    }

/*
    public void reinitialize() { llist = SimpleList.this; }
    public ReusableIterator regenerate() { 
      return SimpleList.this.getIterator(); 
    }
*/

    public void remove () {
      throw new UnsupportedOperationException();
    }

/*
    public ReusableIterator removeConflicts(Conflictable obj) { 
      //return copyList().getIterator(); 
      return removeConflictsAux(obj, SimpleList.this).getIterator(); 
    }
*/

  }

/*
 * Al's slow reverse for time testing.
 */

/*
  public SimpleList rev() {
    if (isEmpty()) {
      return this;
    } else {
      return this.rest.rev().app(new SimpleList(first));
    }
  }
      
  public SimpleList app(SimpleList List) {
    if (isEmpty()) {
      return List ;
    } else {
      return
        rev().rest.rev().app(new SimpleList(rev().first, List));
    }
  }

*/

  public static void main(String[] args) {
    int n;
    if (args.length != 0) {
      n = Integer.parseInt(args[0]);
    } else {
      n = 2;
    }
    SimpleList foo = emptyList;
    SimpleList bar = emptyList;
    for(int i = 0; i < n; i++) {
      bar = bar.cons(new Integer(i));
      foo = foo.cons(bar);
      //foo = new SimpleList(new Integer(i), foo);
      //foo = foo.cons(new Integer(i));
    }
/*
    SimpleList bar = emptyList;
    bar = bar.cons("x");
    foo = foo.cons(bar);
    foo = foo.cons(bar);
*/

System.out.println("before: equals? " 
    + (((SimpleList)foo.first()).rest().rest() 
            == ((SimpleList)foo.rest().first()).rest()));


//foo = emptyList;
System.out.println("foo is " + foo + ", its identityHC is " + System.identityHashCode(foo));
System.out.println("foo constructed from itself is " + new SimpleList(foo));
//System.out.println("foo.rest() is " + foo.rest());

    n = 4000;		// the readObject1 oveflows at 4000
    SimpleList goo = emptyList;
    SimpleList tails = emptyList;
    for(int i = 0; i < n; i++) {
      goo = goo.cons(new Integer(i));
      tails = tails.cons(goo);
    }

/*
    LinkedList alst = new LinkedList();
    LinkedList atmp = new LinkedList();
    for (int i = 0; i < 100; i++) {
      atmp.addFirst(new Integer(i));
      alst.addFirst(x);
    }
*/

    try {
      FileOutputStream fileOut = new FileOutputStream("list10");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(foo);
      out.close();

      fileOut = new FileOutputStream("tailsx" + n);
      out = new ObjectOutputStream(fileOut);
      //out.writeObject(new ArrayList(goo));
      out.writeObject(tails);
      //out.writeObject(goo);
      out.close();


      FileInputStream tailsInx = new FileInputStream("tailsx" + n);
      ObjectInputStream inx = new ObjectInputStream(tailsInx);
      SimpleList tailsIn = (SimpleList)inx.readObject();
      System.out.println("tailsx in has size " + tailsIn.size());

      FileInputStream fileIn = new FileInputStream("list10");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      SimpleList fooIn = (SimpleList)in.readObject();
System.out.println("after: fooIn is " + fooIn + " length " + fooIn.size());

System.out.println("after: equals? " 
    + (((SimpleList)fooIn.first()).rest().rest() 
            == ((SimpleList)fooIn.rest().first()).rest()));


/*
System.out.println("after: equals? " 
    + (((SimpleList)fooIn.first()).rest() == fooIn.rest().first()));
*/

    }
    catch(Exception e) {
      e.printStackTrace();
    }


/*
    System.out.println("the list is " + foo.toString());
    //System.out.println("the size is " + foo.size());
    long time = System.currentTimeMillis();
    //System.out.println("the rev list is " + foo.rev().toString());
    System.out.println("the reverse list is " + foo.reverse().toString());
    System.out.println("list append list is " + foo.append(foo).toString());
    System.out.println("list revappend list is " + foo.reverse(foo).toString());
    time = System.currentTimeMillis() - time;
    System.out.println("Compute time is " + time);
System.out.println("first of emptyList is " + emptyList.first());
System.out.println("rest of emptyList is " + emptyList.rest());
System.out.println("emptyList is empty? " + emptyList.isEmpty());
System.out.println("foo is empty? " + foo.isEmpty());
System.out.println("the size of foo is " + foo.size());


System.out.println("\nfoo as an array is  " + foo.toArray().length);
ArrayList a = new ArrayList(foo);
//System.out.println("\nfoo as an array is  " + a);
//System.out.println("a[5] is  " + a[5]);
//Collections.shuffle(foo);
//System.out.println("\nfoo shuffled is " + foo);
//foo.set(5, new Integer(-1));
//System.out.println("\nfoo with new five is " + foo);
//System.out.println("\nfoo shuffled is " + foo);
System.out.println("\nfoo is " + foo);
// didn't work 
//Collections.sort(foo);
//System.out.println("\nfoo sorted is " + foo);
*/
    
  }

}

