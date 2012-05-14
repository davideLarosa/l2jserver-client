package com.l2client.test.junit;

import java.sql.Connection;

import junit.framework.TestCase;

import com.l2client.dao.ConnectionPool;

public class DAOPoolTest extends TestCase {

	public void testSelf() {

		final ConnectionPool pool = ConnectionPool.getInstance();
		try {
			Connection con1 = pool.getConnection();
			Connection con2 = pool.getConnection();
			Connection con3 = pool.getConnection();
			Connection con4 = pool.getConnection();

			System.out.println(System.nanoTime() + " connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			Thread t = new Thread("waiting for a connection to be returned") {

				@Override
				public void run() {
					System.out.println(System.nanoTime()
							+ " INNER connections fetched:"
							+ (ConnectionPool.MAX_CONNECTIONS - pool
									.getFreeSize()) + " of maximum "
							+ ConnectionPool.MAX_CONNECTIONS + " conections");
					try {
						Connection con1 = pool.getConnection();
					
						System.out.println(System.nanoTime()
								+ " INNER connections fetched:"
								+ (ConnectionPool.MAX_CONNECTIONS - pool
										.getFreeSize()) + " of maximum "
								+ ConnectionPool.MAX_CONNECTIONS
								+ " conections");
						Connection con2 = pool.getConnection();
						Connection con3 = pool.getConnection();
						System.out.println(System.nanoTime()
								+ " INNER connections fetched:"
								+ (ConnectionPool.MAX_CONNECTIONS - pool
										.getFreeSize()) + " of maximum "
								+ ConnectionPool.MAX_CONNECTIONS
								+ " conections");
						con2.close();
						con3.close();
						System.out.println(System.nanoTime()
								+ " INNER connections fetched:"
								+ (ConnectionPool.MAX_CONNECTIONS - pool
										.getFreeSize()) + " of maximum "
								+ ConnectionPool.MAX_CONNECTIONS
								+ " conections");
						con1.close();
						System.out.println(System.nanoTime()
								+ " INNER connections fetched:"
								+ (ConnectionPool.MAX_CONNECTIONS - pool
										.getFreeSize()) + " of maximum "
								+ ConnectionPool.MAX_CONNECTIONS
								+ " conections");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
			t.start();
			Connection con5 = pool.getConnection();
			System.out.println(System.nanoTime() + " connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			assertNotNull(con5);
			System.out.println(System.nanoTime() + " connections assert");
			con5.close();
			System.out.println(System.nanoTime() + " C5 connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			con4.close();
			System.out.println(System.nanoTime() + " C4 connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			con3.close();
			System.out.println(System.nanoTime() + " C3 connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			con2.close();
			System.out.println(System.nanoTime() + " C2 connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			con1.close();
			System.out.println(System.nanoTime() + " C1 connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			con5.close();
			System.out.println(System.nanoTime() + " C0 connections fetched:"
					+ (ConnectionPool.MAX_CONNECTIONS - pool.getFreeSize())
					+ " of maximum " + ConnectionPool.MAX_CONNECTIONS
					+ " conections");
			t.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(ConnectionPool.MAX_CONNECTIONS,pool.getFreeSize());
	}
}
