package com.l2client.test.junit;

import junit.framework.TestCase;

import com.l2client.app.Singleton;
import com.l2client.model.PartSet;

public class PartSetTest extends TestCase {

	public void testSelf() {

		Singleton.get().getPartManager().loadParts("megaset.test.csv");
		PartSet s = Singleton.get().getPartManager().getPart("halberd");
		assertEquals(4, s.getSize());
	}
}
