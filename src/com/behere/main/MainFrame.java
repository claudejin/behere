package com.behere.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.behere.db.DBConnector;
import com.behere.panel.AddressPanel;
import com.behere.panel.CalendarPanel;
import com.behere.panel.MemoPanel;
import com.behere.panel.SearchPanel;

public class MainFrame extends JFrame implements MainFrameController {
	private ImageIcon searchBtnIcon = new ImageIcon("image/main_search.png");
	private JButton searchBtn = new JButton(searchBtnIcon);
	private ImageIcon orderListBtnIcon = new ImageIcon("image/main_orderList.png");
	private JButton orderListBtn = new JButton(orderListBtnIcon);
	private ImageIcon newAddressBtnIcon = new ImageIcon("image/main_newAddress.png");
	private JButton newAddressBtn = new JButton(newAddressBtnIcon);
	//private ImageIcon menuBtnIcon = new ImageIcon("image/main_menu.png");
	//private JButton menuBtn = new JButton(menuBtnIcon);
	private ImageIcon memoBtnIcon = new ImageIcon("image/main_memo.png");
	private JButton memoBtn = new JButton(memoBtnIcon);
	
	private JPanel masterPanel = new JPanel();
	private SearchPanel searchPanel = new SearchPanel(this);
	private CalendarPanel calendarPanel = new CalendarPanel(this);
	private AddressPanel addressPanel = new AddressPanel(this);
	//private MenuPanel menuPanel = new MenuPanel(this);
	private MemoPanel memoPanel = new MemoPanel(this);
	
	private ArrayList<JPanel> panelList = new ArrayList<JPanel>();
	private int currentPanel = SEARCH_PANEL;
	
	private DBConnector dbConn = SharedResource.getDBConnector();
	
	private void DB_MIGRATION() {
		try {
			
			ResultSet rs = dbConn.executeQuery("select * from t_address;");
			PreparedStatement pAddress; 
			PreparedStatement pPhone;
			int i, cnt = 0;
			
			while (rs.next()) {
				cnt++;
				
				pAddress = dbConn.prepareStatement("insert into address(OldID, address, memo) values(?, ?, ?);");
				pAddress.setInt(1, rs.getInt(1));
				pAddress.setString(2, rs.getString(2).trim());
				pAddress.setString(3, rs.getString(8).trim());
				pAddress.executeUpdate();
				pAddress.close();
				
				for (i = 4; i < 8; i++) {
					if (rs.getString(i).trim().length() > 0) {
						pPhone = dbConn.prepareStatement("insert into phonenum(Phonenum, AddressID) values(?, ?);");
						pPhone.setString(1, rs.getString(i).trim());
						pPhone.setInt(2, cnt);
						pPhone.executeUpdate();
						pPhone.close();
					}
				}
			}
			
			rs.close();
			
			System.out.println("Addresses have been migrated!");
			
			
			rs = dbConn.executeQuery("select * from t_order;");
			ResultSet rs2;
			PreparedStatement pOrder = dbConn.prepareStatement("INSERT INTO `order` (`AddressID`, `Menu`, `TotalPrice`, `Datetime`) VALUES (?, ?, ?, ?);");
			
			cnt = 0;
			while (rs.next()) {
				rs2 = dbConn.executeQuery("select ID from address where OldID = " + rs.getString(2));
				if (rs2.next()) {
					cnt++;
					pOrder.setInt(1, rs2.getInt(1));
					pOrder.setString(2, ""+rs.getString(6));
					if (rs.getString(7) != null)
						if (rs.getString(7).length() > 2)
							pOrder.setInt(3, Integer.parseInt(rs.getString(7).substring(1)));
						else
							pOrder.setInt(3, 0);
					else
						pOrder.setInt(3, 0);
					
					if (rs.getString(5).startsWith("24:"))
						pOrder.setString(4, ""+rs.getString(3) + " 00:" + rs.getString(5).substring(3, 5) + ":00");
					else if (rs.getString(5).startsWith("25:"))
						pOrder.setString(4, ""+rs.getString(3) + " 01:" + rs.getString(5).substring(3, 5) + ":00");
					else if (rs.getString(5).startsWith("26:"))
						pOrder.setString(4, ""+rs.getString(3) + " 02:" + rs.getString(5).substring(3, 5) + ":00");
					else
						pOrder.setString(4, ""+rs.getString(3) + " " + rs.getString(5).substring(0, 5) + ":00");
					
					pOrder.executeUpdate();
					
					rs2.close();
				}
			}
			
			pOrder.close();
			rs.close();
			
			System.out.println("Orders have been migrated!");
			
			rs = dbConn.executeQuery("select * from t_dateorder;");
			PreparedStatement pDaily = dbConn.prepareStatement("INSERT INTO `daily` (`date`, `info`) VALUES (?, ?);");
			
			while (rs.next()) {
				pDaily.setString(1, rs.getString(2));
				pDaily.setString(2, rs.getString(3).trim());
				pDaily.executeUpdate();
			}
			
			pDaily.close();
			rs.close();
			
			System.out.println("Daily Information has been migrated!");
			
			System.out.println("Migration has been Complete!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public MainFrame() {
		super("Junto");
		
		//DB_MIGRATION();
		//UPDATE `address` AS a SET a.`regdate` = (SELECT b.`Datetime` FROM `order` AS b WHERE b.`AddressID` = a.`id` ORDER BY b.`Datetime` ASC LIMIT 1);
		//UPDATE `address` SET `regdate` = '2014-07-09 00:00:00' WHERE `regdate` is null;
		//UPDATE `address` SET `coupon_acc` = 0, `coupon_pre` = 0;
		preparePanels();
		add(masterPanel, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1024, 728);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frm = getSize();
		int xpos = (int)(screen.getWidth() / 2 - frm.getWidth() / 2);
		int ypos = (int)(screen.getHeight() / 2 - frm.getHeight() / 2);
		setLocation(xpos, 0);
		setVisible(true);
	}
	
	private void preparePanels() {
		SwitchPanelListener spl = new SwitchPanelListener();
		searchBtn.addActionListener(spl);
		orderListBtn.addActionListener(spl);
		newAddressBtn.addActionListener(spl);
		//menuBtn.addActionListener(spl);
		memoBtn.addActionListener(spl);
		
		RelativeLayout rl = new RelativeLayout();
		masterPanel.setLayout(rl);
		
		masterPanel.add(searchBtn);
		rl.addConstraint(searchBtn, new RelativeLayout.Bounds(12, 12, 140, 60));
		masterPanel.add(orderListBtn);
		rl.addConstraint(orderListBtn, new RelativeLayout.Bounds(164, 12, 220, 60));
		masterPanel.add(newAddressBtn);
		rl.addConstraint(newAddressBtn, new RelativeLayout.Bounds(396, 12, 200, 60));
		//masterPanel.add(menuBtn);
		//rl.addConstraint(menuBtn, new RelativeLayout.Bounds(608, 12, 200, 60));
		//masterPanel.add(memoBtn);
		//rl.addConstraint(memoBtn, new RelativeLayout.Bounds(820, 12, 140, 60));
		masterPanel.add(memoBtn);
		rl.addConstraint(memoBtn, new RelativeLayout.Bounds(608, 12, 140, 60));
		
		RelativeLayout.Bounds panelConstraint = new RelativeLayout.Bounds(0, 84, 1F, 0.9F);
		masterPanel.add(searchPanel);
		rl.addConstraint(searchPanel, panelConstraint);
		searchPanel.setVisible(true);
		masterPanel.add(calendarPanel);
		rl.addConstraint(calendarPanel, panelConstraint);
		calendarPanel.setVisible(false);
		masterPanel.add(addressPanel);
		rl.addConstraint(addressPanel, panelConstraint);
		addressPanel.setVisible(false);
		//masterPanel.add(menuPanel);
		//rl.addConstraint(menuPanel, panelConstraint);
		//menuPanel.setVisible(false);
		masterPanel.add(memoPanel);
		rl.addConstraint(memoPanel, panelConstraint);
		memoPanel.setVisible(false);
		
		panelList.add(searchPanel);
		panelList.add(calendarPanel);
		panelList.add(addressPanel);
		//panelList.add(menuPanel);
		panelList.add(memoPanel);
	}
	
	private int translatePanel(JPanel panel) {
		if (panel.equals(searchPanel))
			return SEARCH_PANEL;
		else if (panel.equals(calendarPanel))
			return CALENDAR_PANEL;
		else if (panel.equals(addressPanel))
			return ADDRESS_PANEL;
		//else if (panel.equals(menuPanel))
		//	return MENU_PANEL;
		else //if (panel.equals(memoPanel))
			return MEMO_PANEL;
	}
	
	private JPanel translatePanel(int panelConstant) {
		if (panelConstant == SEARCH_PANEL)
			return searchPanel;
		else if (panelConstant == CALENDAR_PANEL)
			return calendarPanel;
		else if (panelConstant == ADDRESS_PANEL)
			return addressPanel;
		//else if (panelConstant == MENU_PANEL)
		//	return menuPanel;
		else //if (panelConstant == MEMO_PANEL)
			return memoPanel;
	}
	
	public void switchPanel(int targetPanel, Object info) {
		if (currentPanel == targetPanel)
			return ;
		
		//Step 1: Before-work (Condition Check)
		if (currentPanel == SEARCH_PANEL) {
			
		} else if (currentPanel == CALENDAR_PANEL) {
			calendarPanel.saveDailyMemo();
		} else if (currentPanel == ADDRESS_PANEL) {
			addressPanel.saveAddress();
		//} else if (targetPanel == MENU_PANEL) {
			
		} else if (currentPanel == MEMO_PANEL) {
			memoPanel.saveMemoText();
		}
		
		//Step 2: Switching
		for (JPanel tp : panelList) {
			if (translatePanel(tp) == targetPanel)
				tp.setVisible(true);
			else
				tp.setVisible(false);
		}
		
		//Step 3: After-work
		if (targetPanel == SEARCH_PANEL) {
			searchPanel.focusKeyword();
		} else if (targetPanel == CALENDAR_PANEL) {
			calendarPanel.setOrderList();
		} else if (targetPanel == ADDRESS_PANEL) {
			if (info != null)
				addressPanel.setAddressInfo(Integer.parseInt(info.toString()));
		//} else if (targetPanel == MENU_PANEL) {
			
		} else if (targetPanel == MEMO_PANEL) {
			
		}
		
		currentPanel = targetPanel;
	}
	
	private class SwitchPanelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			
			if (src.equals(searchBtn))
				switchPanel(SEARCH_PANEL, null);
			else if (src.equals(orderListBtn))
				switchPanel(CALENDAR_PANEL, null);
			else if (src.equals(newAddressBtn)) {
				switchPanel(ADDRESS_PANEL, "0");
			//} else if (src.equals(menuBtn))
			//	switchPanel(MENU_PANEL);
			} else if (src.equals(memoBtn))
				switchPanel(MEMO_PANEL, null);
		}
	}
	
	public static void main(String[] args) {
		MainFrame mf = new MainFrame();	
	}
}
