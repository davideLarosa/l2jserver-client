package com.l2client.dao;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Encapsulates a sql connection for pooling. Close() will not close the
 * connection but return it back to the pool. Once close is called here the facade
 * is useless
 */
public class ConnectionFacade implements Connection {

	private Connection inner = null;

	/**
	 * Constructor for creation of a facade.
	 * @param inner the real connection to be used. all calls are just routed to the inner connection (besides close, which is done only by the pool)
	 */
	public ConnectionFacade(Connection inner) {
		this.inner = inner;
	}

	/**
	 * called only by the
	 * 
	 * @see ConnectionPool, closes the inner connection
	 * 
	 * @throws SQLException
	 */
	protected void release() throws SQLException {
		inner.close();
	}

	@Override
	public void clearWarnings() throws SQLException {
		inner.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		if (inner != null) {
			synchronized (this) {
				ConnectionPool.getInstance().releaseConnection(inner);
				inner = null;
			}
		}
	}

	@Override
	public void commit() throws SQLException {
		inner.commit();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return inner.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException {
		return inner.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		return inner.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return inner.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return inner.createSQLXML();
	}

	@Override
	public Statement createStatement() throws SQLException {
		return inner.createStatement();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return inner.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return inner.createStatement(resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return inner.createStruct(typeName, attributes);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return inner.getAutoCommit();
	}

	@Override
	public String getCatalog() throws SQLException {

		return inner.getCatalog();
	}

	@Override
	public Properties getClientInfo() throws SQLException {

		return inner.getClientInfo();
	}

	@Override
	public String getClientInfo(String name) throws SQLException {

		return inner.getClientInfo(name);
	}

	@Override
	public int getHoldability() throws SQLException {

		return inner.getHoldability();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {

		return inner.getMetaData();
	}

	@Override
	public int getTransactionIsolation() throws SQLException {

		return inner.getTransactionIsolation();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {

		return inner.getTypeMap();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {

		return inner.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {

		return inner == null || inner.isClosed();
	}

	@Override
	public boolean isReadOnly() throws SQLException {

		return inner.isReadOnly();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {

		return inner.isValid(timeout);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {

		return inner.nativeSQL(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {

		return inner.prepareCall(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {

		return inner.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {

		return inner.prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {

		return inner.prepareStatement(sql);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {

		return inner.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {

		return inner.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {

		return inner.prepareStatement(sql, columnNames);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {

		return inner.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {

		return inner.prepareStatement(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {

		inner.releaseSavepoint(savepoint);
	}

	@Override
	public void rollback() throws SQLException {

		inner.rollback();
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {

		inner.rollback(savepoint);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {

		inner.setAutoCommit(autoCommit);
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {

		inner.setCatalog(catalog);
	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {

		inner.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {

		inner.setClientInfo(name, value);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {

		inner.setHoldability(holdability);
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {

		inner.setReadOnly(readOnly);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {

		return inner.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {

		return inner.setSavepoint(name);
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {

		inner.setTransactionIsolation(level);
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

		inner.setTypeMap(map);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {

		return inner.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {

		return inner.unwrap(iface);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		inner.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return inner.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		inner.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		inner.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return inner.getNetworkTimeout();
	}
}
