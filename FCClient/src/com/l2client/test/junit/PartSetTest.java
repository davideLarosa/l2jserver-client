package com.l2client.test.junit;

import junit.framework.TestCase;

import com.l2client.model.PartSet;
import com.l2client.util.PartSetManager;

public class PartSetTest extends TestCase {

	public void testSelf() {

		PartSetManager.get().loadParts("megaset.test.csv");
		PartSet s = PartSetManager.get().getPart("halberd");
		assertEquals(4, s.getSize());
	}
}
