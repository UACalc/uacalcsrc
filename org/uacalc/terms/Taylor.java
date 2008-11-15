package org.uacalc.terms;

/**
 * Let f be a k-ary operation symbol. We consider a set of equations
 * where each side has the form f(x's and y's). For each index i
 * there is at least one equation with the ith coordinate on the two
 * sides are different (so f is a Taylor term). 
 * <p>
 * We are looking to see if there is some term t in this language of f
 * that is a Markovic-McKenzie-Siggers term. That is, an idempotent 
 * term satisfying
 * <br>
 * t(y,x,x,x) = t(x,x,y,y) and
 * t(x,x,y,x) = t(x,y,x,x)
 * <br>
 * By idempotence every term in f can be assumed to have a balanced
 * term tree. For depth d such a tree has k^d leaves and the term
 * is determined by the vector of variables at the leaves. So terms
 * of depth d can be represented by a vector of variables of length  
 * k^d and linearly ordered lexicographically. 
 * <p>
 * We use the equations of f to effective rewrite this vector into the
 * least equivalent one least in the lexicographic order. Using this 
 * we search all 4 variable terms for a MMS term.   
 * 
 * @author ralph
 *
 */
public class Taylor {

  
  
  
  
}
