package org.uacalc.ui.tm;

/**
 * Intermediate data a TaskRunner will publish and process to UI components.
 * The Monitor will have the TaskRunner publish and process these.
 * 
 * @author ralph
 *
 */
public class DataChunk {
  
  public static enum DataType {
    START, END, LOG, PASS, SIZE
  }
  
  private String msg;
  private DataType dataType;
  
  public DataChunk(DataType dt, String msg) {
    this.dataType = dt;
    this.msg = msg;
  }
  
  public String getMessage() {return msg; }
  public DataType getDataType() { return dataType; }
  
  public String toString() {
    return "" + dataType + ": " + msg;
  }

}
