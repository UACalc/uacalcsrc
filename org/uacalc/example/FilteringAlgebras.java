package org.uacalc.example;

import java.io.IOException;
import java.util.*;

import org.uacalc.alg.*;
import org.uacalc.io.AlgebraIO;

public class FilteringAlgebras {

	public static void main(String ... args) throws Exception {

		// List<String> filenames = Arrays.asList("alg1", "alg2");
		// SmallAlgebra alg = org.uacalc.io.AlgebraIO.readAlgebraFile("/Users/ralph/Java/Algebra/algebras/emil2y.ua");

		String algebra_dir = "../UACalc-Team/AlgebraFiles/Bergman/CIB4SL/";

		
		List<Algebra> algebras = Arrays.asList(
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-1.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-100.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-200.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-201.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-205.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-217.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-233.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-457.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-500.ua"),
				AlgebraIO.readAlgebraFile(algebra_dir + "CIB4-1017.ua")
				);
		// Of those listed above, 201, 217, 233, 457, 1017 are non-SD-meet algebras.
		// Ralph's method,
		//    Malcev.sdMeetIdempotent((SmallAlgebra) a, null) 
		// returns an IntArray witnessing the failure of sdMeet, and null if no failure.
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
}
