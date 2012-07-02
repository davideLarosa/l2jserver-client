package com.l2client.test.junit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

public class DerbyTest extends TestCase {

	public void testSelf() {
		try {
			new org.apache.derby.jdbc.EmbeddedDriver();
			Connection con = DriverManager
					.getConnection("jdbc:derby:derby/l2jclient;user=l2jclient;password=l2jclient;");
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT COUNT(ID) FROM NPC");
			rs.next();
			assertNotNull("npc table returned no results",rs.getInt(1));
			con.close();
			DriverManager.getConnection("jdbc:derby:;shutdown=true;");
		} catch (SQLException e) {
			assertEquals(e.getMessage(), "XJ015",e.getSQLState());
		}
	}
}
