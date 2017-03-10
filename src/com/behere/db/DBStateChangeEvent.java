package com.behere.db;

import javax.swing.event.ChangeEvent;

public class DBStateChangeEvent extends ChangeEvent {
	public static final int UNDEFINED = 0;
	public static final int DB_OPENED = 1;
	public static final int DB_CLOSED = 2;
	public static final int TABLE_CREATED = 3;
	public static final int TABLE_ALTERED = 5;
	public static final int TABLE_DROPPED = 5;
	
	public int type;
	
	public DBStateChangeEvent(Object source, int type) {
		super(source);
		this.type = type;
	}
	
	public DBStateChangeEvent(Object source) {
		this(source, DBStateChangeEvent.UNDEFINED);
	}
}
