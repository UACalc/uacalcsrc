package org.uacalc.ui.table;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

/**
 * Table utilities like scrolling to make a row visible.
 * 
 * 
 * @author ralph
 *
 */

public class TableUtils {

  /**
   * This comes from http://www.exampledepot.com/egs/javax.swing.table/Vis.html
   * which is part of http://www.exampledepot.com/egs/javax.swing.table/pkg.html
   * 
   * @param table
   * @param rowIndex
   * @param vColIndex
   */
  public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
    if (!(table.getParent() instanceof JViewport)) return;
    JViewport viewport = (JViewport)table.getParent();
    
    // This rectangle is relative to the table where the 
    // northwest corner of cell (0,0) is always (0,0). 
    Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    
    // The location of the viewport relative to the table 
    Point pt = viewport.getViewPosition(); 
    
    // Translate the cell location so that it is relative 
    // to the view, assuming the northwest corner of the view is (0,0) 
    rect.setLocation(rect.x - pt.x, rect.y - pt.y);
    
    // Scroll the area into view 
    viewport.scrollRectToVisible(rect); 
  }
  
  /**
   * This updates the current list of selected rows, keeping order the user
   * selected them and return the list as a convenience.
   * 
   * @param table
   * @param currentList
   * @return
   */
  public static java.util.List<Integer> updateOrderedSelection(JTable table,java.util.List<Integer> currentList) {
    ListSelectionModel lsm = table.getSelectionModel();
    if (lsm.isSelectionEmpty()) {
      currentList.clear();
      return currentList;
    }
    final int first = lsm.getMinSelectionIndex();
    final int last = lsm.getMaxSelectionIndex();
    if (first == last) {
      currentList.clear();
      currentList.add(first);
      return currentList;
    }
    final java.util.List<Integer> tmp = new java.util.ArrayList<Integer>();
    for (int i = first; i <= last; i++) {
      if (lsm.isSelectedIndex(i)) tmp.add(i);
      if (!currentList.contains(i)) currentList.add(i);
    }
    final java.util.List<Integer> tmp2 = new java.util.ArrayList<Integer>();
    for (int i = 0; i < currentList.size(); i++) {
      if (lsm.isSelectedIndex(currentList.get(i))) tmp2.add(currentList.get(i));
    }
    currentList.clear();
    currentList.addAll(tmp2);
    return currentList;
  }
  
}
