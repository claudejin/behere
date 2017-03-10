package com.behere.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * DBConnector 1.2
 * 2014-04-30
 * 
 * @author Claude
 *
 */
public class DBConnector {
	//Connection
	private ConnectionInfo connInfo;
	private String url, user, password;
	private Connection conn;
	
	//Statements
	private Statement stmt;
	private ArrayList<PreparedStatement> pstmts;
	
	//ResultSet
	private ResultSet rs;
	
	//DB Event
	private ArrayList<DBStateChangeListener> listeners = new ArrayList<DBStateChangeListener>();
	
	//Constructors
	public DBConnector(ConnectionInfo connInfo) {
		this("jdbc:mysql://" + connInfo.getHost() + ":" + connInfo.getPort() + "/"
				+ connInfo.getSchema(), connInfo.getUserName(),connInfo.getPassword());
		
		this.connInfo = connInfo;
	}
	
	public DBConnector(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
		
		try {
			Class.forName("org.mariadb.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ConnectionInfo getConnectionInfo() {
		return connInfo;
	}
	
	public Connection getConnection() {
		if (!isConnected()) {
			conn = null;
			
			try {
				conn = DriverManager.getConnection(url, user, password);
				stmt = conn.createStatement();
				
				fireStateChangeEvent(DBStateChangeEvent.DB_OPENED);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return conn;
	}
	
	public void closeConnection() {
		try {
			for (PreparedStatement pstmt : pstmts) {
				if (pstmt != null)
					try {
						if (!pstmt.isClosed())
							pstmt.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}
			if (stmt != null) stmt.close();
			if (stmt != null) conn.close();
			
			fireStateChangeEvent(DBStateChangeEvent.DB_CLOSED);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		if (conn == null)
			return false;
		try {
			if (conn.isClosed())
				return false;
		} catch (SQLException e) {
			return false;
		}
		
		return true;
	}
	
	public PreparedStatement prepareStatement(String sql) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pstmt;
	}
		
	public ResultSet executeQuery(String sql) throws SQLException {
		rs = null;
		
		if (getConnection() != null) {
        	sql = sql.trim();
    		rs = stmt.executeQuery(sql);
		}
		
		return rs;
	}
	
	public int executeUpdate(String sql) throws SQLException {
		int res = -1;
		
		if (getConnection() != null) {
			sql = sql.trim();
			res = stmt.executeUpdate(sql);
		
			if (sql.toUpperCase().substring(0, 7).equals("CREATE ")) {
				fireStateChangeEvent(DBStateChangeEvent.TABLE_CREATED);
				res = -2;
			} else if (sql.toUpperCase().substring(0, 6).equals("ALTER ")) {
				fireStateChangeEvent(DBStateChangeEvent.TABLE_ALTERED);
				res = -3;
			} else if (sql.toUpperCase().substring(0, 5).equals("DROP ")) {
				fireStateChangeEvent(DBStateChangeEvent.TABLE_DROPPED);
				res = -4;
			}
		}
		
		return res;
	}
	
	public void addStateChangeListener(DBStateChangeListener cl) {
		if (!listeners.contains(cl))
			listeners.add(cl);
	}
	
	public void removeStateChangeListener(DBStateChangeListener cl) {
		if (listeners.contains(cl))
			listeners.remove(cl);
	}
	
	private void fireStateChangeEvent(int state) {
		int cnt = listeners.size();
		for (int i = 0; i < cnt; i++)
			listeners.get(i).stateChanged(new DBStateChangeEvent(this, state));
	}
}
