package com.l2client.test.junit;

import junit.framework.TestCase;

import com.l2client.dao.DatastoreDAO;
import com.l2client.gui.actions.BaseUsable;


public class DAOActionLoadTest extends TestCase{
	
	public void testSelf() {

		final DatastoreDAO dao = DatastoreDAO.getInstance();
//		InputController.getInstance().setInGameInputHandler(new InputHandler());
		BaseUsable[] ret = dao.loadAllActions();
		assertTrue("Any actions returned", ret != null && ret.length > 0);
	}

}
