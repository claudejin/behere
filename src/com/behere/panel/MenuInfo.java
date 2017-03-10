package com.behere.panel;

public class MenuInfo {
	private int ID;
	private String name;
	private String shortName;
	private int price;
	private int type;
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShort() {
		return shortName;
	}
	public void setShort(String shortName) {
		this.shortName = shortName;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
