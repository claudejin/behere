package com.behere.main;

import java.awt.Font;

import com.behere.db.ConnectionInfo;
import com.behere.db.DBConnector;

public class SharedResource {
	public static final Font REGULAR_FONT = new Font("나눔고딕 ExtraBold", Font.PLAIN, 20);
	public static final Font LARGE_FONT = new Font("나눔고딕 ExtraBold", Font.PLAIN, 36);
	
	public static final ConnectionInfo CONNECTION_INFO = new ConnectionInfo("localhost", "3306", "root", "md098cld13", "test");
	private static final DBConnector dbConn = new DBConnector(CONNECTION_INFO);
	
	public static DBConnector getDBConnector() {
		return dbConn;
	}
}
