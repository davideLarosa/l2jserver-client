package com.l2client.dao.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A pool of sql connections, which are already initialized. The max number of held connections is
 * configured by MAX_CONNECTIONS.
 * 
 * The conections are stored in a blocking queue, so future requests will wait until a connection
 * is returned.
 * 
 * Used as a singleton via ConnectionPoool.getInstance()
 *
 */
public final class ConnectionPool {

	private static Logger logger = Logger.getLogger(ConnectionPool.class.getName());

	/**
	 * Maximum number of availbale database connections, currently 5
	 */
	public final static int MAX_CONNECTIONS = 5;
	
	private static ConnectionPool instance = null;
	private static BlockingQueue<Connection> connections = new ArrayBlockingQueue<Connection>(MAX_CONNECTIONS);


	/**
	 * internal constructor
	 */
	private ConnectionPool() {
		synchronized(connections){
			if(connections.size() > 0){
				logger.warning("trying to initialize connection pool while already initialized");
				return;
			}
		logger.finest("initializing");
		initializeConnections();
		logger.finest("initialized");
		}
	}
	
	/**
	 * Returns the number of available cnnections in the pool
	 * @return
	 */
	public int getFreeSize(){
		synchronized(connections){
			return connections.size();
		}
	}

	/**
	 * Creates the client db connection to the local derby client
	 */
	private void initializeConnections() {
		try {
			synchronized (connections) {
				if (connections.isEmpty()) {
					new org.apache.derby.jdbc.EmbeddedDriver();
					for (int i = 0; i < MAX_CONNECTIONS; i++) {
						Connection con = DriverManager
								.getConnection("jdbc:derby:classpath:derby/l2jclient;user=l2jclient;password=l2jclient;");
						connections.add(con);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * singleton accessor
	 * @return the ConnectionPool instance
	 */
	public static ConnectionPool getInstance() {
		if (instance != null)
			return instance;
		instance = new ConnectionPool();
		return instance;
	}

	/**
	 * fetch a new connection from the pool, ev. waiting until one is available
	 * @return a new @see ConnectionFacade 
	 * @throws InterruptedException
	 */
	public Connection getConnection() throws InterruptedException {
			return  new ConnectionFacade(connections.take());
	}

	/**
	 * Returns the connection back to the pool, only to be called by @see ConnectionFacade close()
	 * @param con the true connection to be returned back into the pool
	 */
	protected void releaseConnection(Connection con) {
		try {
			synchronized (connections) {
			connections.put(con);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Closes all held real connections and shutdowns the jdbc driver
	 */
	protected void shutdown(){
		logger.finest("finalizing");
		synchronized (connections) {
			for (Connection con : connections)
				try {
					con.close();
				} catch (SQLException e) {
					logger.log(Level.SEVERE, "Error while releasing held connections",e);
				}
			
			logger.finest("connections released");
		}
		try {
			logger.finest("shutting down derby");
			DriverManager.getConnection("jdbc:derby:;shutdown=true;");
		} catch (SQLException e) {
			if (!"XJ015".equals(e.getSQLState())){
				logger.log(Level.SEVERE, "Error while trying to shutdown derby",e);
			} else
				logger.finest("derby shut down");
		}
	}
}
