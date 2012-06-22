package com.l2client.test.junit;

import junit.framework.TestCase;

import com.l2client.dao.IDAO;
import com.l2client.dao.derby.DatastoreDAO;
import com.l2client.gui.actions.BaseUsable;


public class DAOActionLoadTest extends TestCase{
	
	public void testSelf() {

		final IDAO dao = DatastoreDAO.get();
//		InputController.getInstance().setInGameInputHandler(new InputHandler());
		BaseUsable[] ret = dao.loadAllActions();
		assertTrue("Any actions returned", ret != null && ret.length > 0);
	}

}
