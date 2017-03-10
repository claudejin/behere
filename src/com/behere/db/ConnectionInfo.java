package com.behere.db;

public class ConnectionInfo {
	private String host = "";
	private String port = "";
	private String userName = "";
	private String password = "";
	private String schema = "";
	
	public ConnectionInfo() {
		
	}
	
	public ConnectionInfo(String host, String port,
							String userName, String password,
							String schema) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.schema = schema;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host.trim();
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port.trim();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName.trim();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password.trim();
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema.trim();
	}
	
	public boolean verify() {
		if (host.length() > 0
				&& port.length() > 0
				&& userName.length() > 0
				&& password.length() > 0
				&& schema.length() > 0) {
			return true;
		}
		
		return false;
	}
}
