package com.behere.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.behere.db.DBConnector;
import com.behere.main.RelativeLayout;
import com.behere.main.SharedResource;

public class MenuListPanel extends JPanel {
	private final static int CHICKEN = 1;
	private final static int SOURCES = 2;
	private final static int DRINKS = 3;
	private final static int OPTION = 4;
	
	private Font sharedFont = new Font("������� ExtraBold", Font.PLAIN, 19);
	private Font sharedLargeFont = new Font("������� ExtraBold", Font.PLAIN, 36);
	
	private DBConnector dbConn = SharedResource.getDBConnector();
	
	private ArrayList<JButton> menuBtns = new ArrayList<JButton>();
	private MouseListener mbl = new MenuButtonListener();
	
	private JPanel mainMenu = new JPanel();
	
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	
	public MenuListPanel() {
		RelativeLayout rl = new RelativeLayout();
		this.setLayout(rl);
		
		mainMenu.setBackground(Color.WHITE);
		((FlowLayout)mainMenu.getLayout()).setHgap(0);
		((FlowLayout)mainMenu.getLayout()).setVgap(0);
		
		try {
			mainMenu.removeAll();
			JButton menuBtn;
			ResultSet rs = dbConn.executeQuery("SELECT * FROM `menu` WHERE `MenuType` = 1;");
			while (rs.next()) {
				menuBtn = createMenuButton(rs.getString(3), rs.getInt(5));
				menuBtns.add(menuBtn);
				mainMenu.add(menuBtn);
			}
			rs = dbConn.executeQuery("SELECT * FROM `menu` WHERE `MenuType` = 3;");
			while (rs.next()) {
				menuBtn = createMenuButton(rs.getString(3), rs.getInt(5));
				menuBtns.add(menuBtn);
				mainMenu.add(menuBtn);
			}
			rs = dbConn.executeQuery("SELECT * FROM `menu` WHERE `MenuType` = 2;");
			while (rs.next()) {
				menuBtn = createMenuButton(rs.getString(3), rs.getInt(5));
				menuBtns.add(menuBtn);
				mainMenu.add(menuBtn);
			}
			rs = dbConn.executeQuery("SELECT * FROM `menu` WHERE `MenuType` = 4;");
			while (rs.next()) {
				menuBtn = createMenuButton(rs.getString(3), rs.getInt(5));
				menuBtns.add(menuBtn);
				mainMenu.add(menuBtn);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.add(mainMenu);
		
		rl.addConstraint(mainMenu, new RelativeLayout.Bounds(0, 0, 420, 520));
	}
	
	private JButton createMenuButton(String text, int type) {
		JButton rtnBtn = new JButton(text);
		rtnBtn.setFont(sharedLargeFont);
		if (type == OPTION) {
			rtnBtn.setPreferredSize(new Dimension(210, 65));
			rtnBtn.setBackground(Color.GREEN);
		} else if (type == CHICKEN) {
			rtnBtn.setPreferredSize(new Dimension(140, 65));
			rtnBtn.setBackground(Color.ORANGE);
		} else if (type == SOURCES || type == DRINKS) {
			rtnBtn.setPreferredSize(new Dimension(70, 65));
			rtnBtn.setBackground(Color.LIGHT_GRAY);
			if (rtnBtn.getText().startsWith("��"))
					rtnBtn.setFont(sharedFont);
		}
		rtnBtn.addMouseListener(mbl);
		
		return rtnBtn;
	}
	
	public void addActionListener(ActionListener cl) {
		if (!listeners.contains(cl))
			listeners.add(cl);
	}
	
	public void removeActionListener(ActionListener cl) {
		if (listeners.contains(cl))
			listeners.remove(cl);
	}
	
	private void fireActionEvent(Object source, int button) {
		int cnt = listeners.size();
		for (int i = 0; i < cnt; i++)
			listeners.get(i).actionPerformed(new ActionEvent(source, button, ""));
	}
	
	private class MenuButtonListener extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			String menu = ((JButton) e.getSource()).getText();
			
			try {
				ResultSet rs = dbConn.executeQuery("SELECT * FROM `menu` WHERE Short = '" + menu + "';");
				rs.next();
				
				MenuInfo mi = new MenuInfo();
				mi.setID(rs.getInt(1));
				mi.setName(rs.getString(2));
				mi.setShort(rs.getString(3));
				mi.setPrice(rs.getInt(4));
				mi.setType(rs.getInt(5));
				fireActionEvent(mi, e.getButton());
				
				rs.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
}
