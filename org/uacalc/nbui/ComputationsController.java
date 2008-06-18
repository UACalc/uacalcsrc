package org.uacalc.nbui;

public class ComputationsController {
  
  private final UACalculatorUI uacalc;
  
  public ComputationsController(UACalculatorUI uacalc) {
    this.uacalc = uacalc;
  }

  private Actions getActions() { return uacalc.getActions(); }
  
  
}
