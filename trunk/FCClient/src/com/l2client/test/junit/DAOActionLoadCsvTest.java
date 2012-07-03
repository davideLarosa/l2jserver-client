package com.l2client.test.junit;

import junit.framework.TestCase;

import com.l2client.dao.IDAO;
import com.l2client.dao.csv.CSVDatastoreDAO;
import com.l2client.gui.actions.BaseUsable;


public class DAOActionLoadCsvTest extends TestCase{
	
	public void testSelf() {
		final IDAO dao = CSVDatastoreDAO.get();
		dao.init();
//		InputController.getInstance().setInGameInputHandler(new InputHandler());
		BaseUsable[] ret = dao.loadAllActions();
		String name = dao.getNpcName(30370);
		assertTrue("Any actions returned", ret != null && ret.length > 0);
		assertNotNull(name);
		dao.finit();
	}

}
