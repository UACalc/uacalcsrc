package org.uacalc.example;

import java.io.IOException;
import java.util.*;

import org.uacalc.alg.*;
import org.uacalc.io.AlgebraIO;


// This class demonstrates filtering a list of algebras using some predicate.
// In the example below we use "non-congruence-SD-meet" as the predicate. 
// That is, we read in some SmallAlgebras, storing them all in a List<Algebra> object,
// then apply a filter that returns a list containing only those that are NOT SD-meet.
public class FilteringAlgebras {

	public static void main(String ... args) throws Exception {

		String algebras_dir = "resources/algebras/bergman/";

		// A list of indices of some algebras we'll read in:
		List<Integer> algebra_indices = Arrays.asList(1, 100, 200, 201, 205, 217, 233, 457, 500, 1017);

		// A list of algebras specified in files CIB4-1.ua, CIB4-100.ua, CIB4-200.ua, etc.
		List<Algebra> algebras = new ArrayList<>();
		for (Integer i : algebra_indices) {
				algebras.add(AlgebraIO.readAlgebraFile(algebras_dir + "CIB4-" + i + ".ua"));
		}

		// Of those algebras in the list above, we know from earlier experiments that 
		// those with indices 201, 217, 233, 457, 1017 are non-SD-meet algebras.
		// The method Malcev.sdMeetIdempotent returns an IntArray witnessing the 
		// failure of sdMeet, and null if there is no failure.
		// That is, null is returned when the algebra is congruence SD-meet.
		// Therefore, to filter for non-SD-meet algebras, check when sdMeetIdempotent is non-null.
		List<Algebra> nonSDmeet = filter(algebras, (Algebra a) -> (Malcev.sdMeetIdempotent((SmallAlgebra) a, null)!=null) );
		System.out.println("There are " + nonSDmeet.size() + " non-SD-Meet algebras (should be 5)");
	}
	
	interface AlgebraPredicate {
		public boolean test(Algebra a);
	}
	
	public static List<Algebra> filter(List<Algebra> algebras, AlgebraPredicate p) {
		List<Algebra> result = new ArrayList<>();
		for (Algebra a : algebras) {
			if (p.test(a))
				result.add(a);
		}
		return result;
	}
	
	// Reference: "Java 8 in Action" in particular, see the 
	//            lambdasinaction.chap3 package of the repository at 
	//            https://github.com/java8/Java8InAction 
}
