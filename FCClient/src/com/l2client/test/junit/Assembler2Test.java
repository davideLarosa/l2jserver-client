package com.l2client.test.junit;

import java.net.MalformedURLException;

import junit.framework.TestCase;

import com.jme3.scene.Node;
import com.l2client.app.Assembler2;
import com.l2client.util.PartSetManager;

public class Assembler2Test extends TestCase {

	public void testSelf() throws MalformedURLException{
		PartSetManager.get().loadParts("megaset.test.csv");

		Node node = Assembler2.getModel("halberd");
		
		assertNotNull(node);
	}
}
