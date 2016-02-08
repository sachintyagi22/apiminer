package com.kb.ml;

import static org.junit.Assert.*;

import org.junit.Test;

public class GraphComparisonKeyTest {

	@Test
	public void test() {
		String ths = "this";
		String that = "that";
		String moreThis = "this";
		String moreThat = "that";
		String some = "some";
		
		GraphComparisonKey thisThat = new GraphComparisonKey(ths, that);
		GraphComparisonKey thatThis = new GraphComparisonKey(that, ths);
		
		GraphComparisonKey moreThisThat = new GraphComparisonKey(moreThis, moreThat);
		GraphComparisonKey someThat = new GraphComparisonKey(some, that);
		
		assertTrue("This that is equal to that this", thisThat.equals(thatThis));
		assertTrue("That this is equal to this that", thatThis.equals(thisThat));
		
		assertTrue("This that is equal to more this that", thisThat.equals(moreThisThat));
		
		assertTrue("Not equal", !thisThat.equals(someThat));
		
		assertEquals(thisThat.hashCode(), thatThis.hashCode());
	}

}
