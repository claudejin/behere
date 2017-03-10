package com.behere.main;

public interface MainFrameController {
	public static final int SEARCH_PANEL = 1;
	public static final int CALENDAR_PANEL = 2;
	public static final int ADDRESS_PANEL = 3;
	public static final int MENU_PANEL = 4;
	public static final int MEMO_PANEL = 5;
	
	public void switchPanel(int targetPanel, Object info);
}
