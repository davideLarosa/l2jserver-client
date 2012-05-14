package com.l2client.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.l2client.dao.builders.ActionResultBuilder;
import com.l2client.gui.actions.BaseUsable;


/**
 * Central Data Store Object (DAO) for accessing the client side configuration database.
 * 
 * Mediates between internal storage an the model data.
 * 
 * Currently only fetches the NPC Names, as an example.
 *
 */
//TODO extract SQLs into statics
public final class DatastoreDAO {

	private static Logger logger = Logger.getLogger(DatastoreDAO.class.getName());
	
	private static DatastoreDAO instance = null;
	private final static ConnectionPool pool = ConnectionPool.getInstance();

	private DatastoreDAO() {
		logger.finest("Datastore is initializing");
	}
	
	public static DatastoreDAO getInstance() {
		if (instance != null)
			return instance;
		
		instance = new DatastoreDAO();
		return instance;
	}

	/**
	 * Fetch names from npc table based on template id
	 * 
	 * @param templateID id of the template to be loaded
	 * @return String representing the name or an empty Sting in case no name was loaded or an error occured
	 */
	public String getNpcName(int templateID){
		logger.finest("getNpCName entered");
		String name  = "";
		Connection c = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			c = pool.getConnection();
			st = c.prepareStatement("SELECT name FROM npc WHERE idTemplate = ?");
			st.setInt(1, templateID);
			st.execute();
			rs = st.getResultSet();
			if(rs.next()) {
				name = rs.getString(1);
			} else {
				logger.severe("Name not found for template id "+templateID);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(c != null)
					c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.finest("getNpCName exited");
		return name;
	}
	
	public BaseUsable[] loadAllActions(){
		
		logger.finest("loadAllActions entered");
		BaseUsable[] ret = new BaseUsable[0];
		
		Connection c = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			c = pool.getConnection();
			st = c.prepareStatement("SELECT * FROM actions");
			st.execute();
			rs = st.getResultSet();
			ret = ActionResultBuilder.buildActions(rs);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(c != null)
					c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.finest("loadAllActions exited");
		return ret;
	}
	
	public void release(){
		pool.shutdown();
	}
}
