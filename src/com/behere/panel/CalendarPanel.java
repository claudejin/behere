package com.behere.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.behere.db.DBConnector;
import com.behere.main.DateUtil;
import com.behere.main.MainFrameController;
import com.behere.main.RelativeLayout;
import com.behere.main.SharedResource;

public class CalendarPanel extends JPanel {
	private DBConnector dbConn = SharedResource.getDBConnector();
	private MainFrameController mfc;
	
	private RelativeLayout rl = new RelativeLayout();
	
	//Left-side
	private JPanel changePanel = new JPanel(), datePanel = new JPanel();
	private JSpinner changeYear;
	private JLabel changeYearLabel =  new JLabel("��");
	private JComboBox<String> changeMonth;
	private JLabel changeMonthLabel = new JLabel("��");
	private ArrayList<JLabel> dateList = new ArrayList<JLabel>();
	private JLabel prevDate;
	private ImageIcon dateCircleIcon = new ImageIcon("image/calendar_circle.png");
	private JTextArea dailyMemo = new JTextArea();
	private JScrollPane spDailyMemo = new JScrollPane(dailyMemo);
	
	//Right-side
	private JLabel orderListLabel = new JLabel("----�� --�� --�� �ֹ�����");
	private ImageIcon todayIcon = new ImageIcon("image/calendar_today.png");
	private JButton todayBtn = new JButton(todayIcon);
	private Vector<String> orderListHeader = new Vector<String>();
	private Vector<Vector<String>> orderListData = new Vector<Vector<String>>();
	private DefaultTableModel orderListTableModel = new DefaultTableModel(orderListData, orderListHeader) {
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;
		}
	};
	private JTable orderListTable = new JTable(orderListTableModel);
	private JScrollPane spOrderList = new JScrollPane(orderListTable);
	
	//Data
	private Calendar cal;
	private int year, month, date;
	private int todayYear, todayMonth, todayDate;
	private int firstday, lastday;
	
	private DateChangeListener dcl = new DateChangeListener();
	
	public CalendarPanel(MainFrameController mfc) {
		this.mfc = mfc;
		
		cal = Calendar.getInstance();
		year = todayYear = cal.get(Calendar.YEAR);
		month = todayMonth = cal.get(Calendar.MONTH);
		date = todayDate = cal.get(Calendar.DAY_OF_MONTH);
		
		SpinnerModel yearModel = new SpinnerNumberModel(year, year - 10, year + 10, 1);
		changeYear = new JSpinner(yearModel);
		changeYear.addChangeListener(new YearChangeListener());
		
		String[] monthModel = new String[12];
		for (int i = 0; i < 12; i++) {
			monthModel[i] = (i + 1) + "";
		}
		changeMonth = new JComboBox<String>(monthModel);
		changeMonth.setSelectedIndex(month);
		changeMonth.addActionListener(new MonthChangeListener());
		
		changePanel.setBackground(Color.WHITE);
		changeYear.setFont(SharedResource.REGULAR_FONT);
		changeYearLabel.setFont(SharedResource.REGULAR_FONT);
		changeMonth.setFont(SharedResource.REGULAR_FONT);
		changeMonthLabel.setFont(SharedResource.REGULAR_FONT);
		
		changeYear.setPreferredSize(new Dimension(80, 36));
		changeYear.setEditor(new JSpinner.NumberEditor(changeYear, "#"));
		changeMonth.setPreferredSize(new Dimension(80, 36));
		
		changePanel.add(changeYear);
		changePanel.add(changeYearLabel);
		changePanel.add(changeMonth);
		changePanel.add(changeMonthLabel);
		
		datePanel.setLayout(new GridLayout(7, 7));
		String[] weekName = {"��", "��", "ȭ", "��", "��", "��", "��"};
		JLabel dateTmp;
		for (int i = 0; i < weekName.length; i++)
			datePanel.add(createDateLabel(weekName[i], Color.LIGHT_GRAY));
		for (int i = 0; i < 42; i++) {
			dateTmp = createDateLabel("", Color.WHITE);
			dateTmp.addMouseListener(dcl);
			dateList.add(dateTmp);
			datePanel.add(dateTmp);
		}
		loadDailyMemo();
		setCalendar(todayYear, todayMonth, todayDate);
		
		dailyMemo.setFont(SharedResource.REGULAR_FONT);
		dailyMemo.addKeyListener(new MemoListener());
	    dailyMemo.setWrapStyleWord(true);
	    dailyMemo.setLineWrap(true);
		
		orderListLabel.setFont(SharedResource.REGULAR_FONT);
		orderListHeader.add("����");
		orderListHeader.add("ID");
		orderListHeader.add("�ð�");
		orderListHeader.add("�ּ�");
		orderListHeader.add("����");
		orderListHeader.add("����");
		
		orderListTable.addMouseListener(new OrderClickListener());
		orderListTable.setFont(SharedResource.REGULAR_FONT);
		orderListTable.setRowHeight(36);
		
		todayBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeYear.setValue(todayYear);
				changeMonth.setSelectedIndex(todayMonth);
				setCalendar(todayYear, todayMonth, todayDate);
				setOrderList();
			}
		});
		
		this.setBackground(Color.WHITE);
		
		this.setLayout(rl);
		
		this.add(changePanel);
		this.add(datePanel);
		this.add(spDailyMemo);
		this.add(orderListLabel);
		this.add(spOrderList);
		this.add(todayBtn);
		
		rl.addConstraint(changePanel, new RelativeLayout.Bounds(12, 12, 338, 44));
		rl.addConstraint(datePanel, new RelativeLayout.Bounds(12, 60, 338, 338));
		rl.addConstraint(spDailyMemo, new RelativeLayout.Bounds(12, 400, 338, 180));
		rl.addConstraint(orderListLabel, new RelativeLayout.Bounds(380, 12, 610, 36));
		rl.addConstraint(spOrderList, new RelativeLayout.Bounds(380, 60, 610, 520));
		rl.addConstraint(todayBtn, new RelativeLayout.Bounds(936, 12, 54, 36));
	}
	
	private JLabel createDateLabel(String text, Color bgColor) {
		JLabel label = new JLabel(text);
		label.setFont(SharedResource.REGULAR_FONT);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		label.setBorder(new EtchedBorder());
		
		if (bgColor != null) {
			label.setOpaque(true);
			label.setBackground(bgColor);
		}
		
		return label;
	}
	
	private ImageIcon getCircledIcon(String date) {
		try {
			String sql = "SELECT `circled` FROM `daily` WHERE `date` = '" + date + "';";
			ResultSet rs = dbConn.executeQuery(sql);
			if (rs.next()) {
				if (rs.getString(1).equals("Y"))
					return dateCircleIcon;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return null;
	}
	
	private void setCalendar(int year, int month, int date) {
		this.year = year;
		this.month = month;
		this.date = date;
		
		if (((Integer)changeYear.getValue()).intValue() != year)
			changeYear.setValue(year);
		if (changeMonth.getSelectedIndex() != month)
			changeMonth.setSelectedIndex(month);
		
		cal.set(year, month, 1);
		
		firstday = cal.get(Calendar.DAY_OF_WEEK) - 1;
		lastday = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		if (firstday == 0)
			firstday = 7;
		
		//Before-date
		cal.add(Calendar.MONTH, -1);
		int outdate = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - firstday + 1;
		for (int i = 0; i < firstday; i++) {
			if (outdate < 10)
				dateList.get(i).setIconTextGap(-30);
			else
				dateList.get(i).setIconTextGap(-36);
			dateList.get(i).setIcon(getCircledIcon(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + outdate));
			dateList.get(i).setText("" + outdate);
			dateList.get(i).setBackground(Color.LIGHT_GRAY);
			
			outdate++;
		}
		
		//On-date
		cal.set(year, month, date);
		for (int i = 0; i < lastday; i++) {
			if ((firstday + i) < 10)
				dateList.get(firstday + i).setIconTextGap(-30);
			else
				dateList.get(firstday + i).setIconTextGap(-36);
			dateList.get(firstday + i).setIcon(getCircledIcon(year + "-" + (month+1) + "-" + (i+1)));
			dateList.get(firstday + i).setText((i + 1) + "");
			dateList.get(firstday + i).setBackground(Color.WHITE);
		}
		
		//Today Mark
		if (prevDate != null)
			prevDate.setBackground(Color.WHITE);
		prevDate = dateList.get(firstday + date - 1);
		prevDate.setBackground(Color.CYAN);
		
		//After-date
		cal.add(Calendar.MONTH, +1);
		int afterEmpty = firstday + lastday;
		int last = dateList.size() - afterEmpty;
		outdate = 1;
		
		for (int i = 0; i < last; i++) {
			if (outdate < 10)
				dateList.get(afterEmpty + i).setIconTextGap(-30);
			else
				dateList.get(afterEmpty + i).setIconTextGap(-36);
			dateList.get(afterEmpty + i).setIcon(getCircledIcon(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + outdate));
			dateList.get(afterEmpty + i).setText("" + outdate);
			dateList.get(afterEmpty + i).setBackground(Color.LIGHT_GRAY);
			
			outdate++;
		}
		
		//Weekend
		for (int i = 0; i < dateList.size(); i++) {
			if (i % 7 == 0)
				dateList.get(i).setForeground(Color.RED);
			else if (i % 7 == 6)
				dateList.get(i).setForeground(Color.BLUE);
		}
		
		cal.set(year, month, date);
		
		loadDailyMemo();
		
		datePanel.updateUI();
	}
	
	private void loadDailyMemo() {
		String currentDate = DateUtil.convCalendarToUserTypedString(cal, "yyyy-MM-dd");
		
		try {
			//Load Daily Memo
			String sql = "SELECT * FROM `daily` WHERE `date` = '" + currentDate + "';";
			ResultSet rs = dbConn.executeQuery(sql);
			if (rs.next()) {
				dailyMemo.setText(rs.getString(3).trim());
			} else {
				dailyMemo.setText("");
			}
			rs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	public void saveDailyMemo() {
		String currentDate = DateUtil.convCalendarToUserTypedString(cal, "yyyy-MM-dd");
		
		try {
			//Auto-Save Daily Memo
			String sql = "SELECT * FROM `daily` WHERE `date` = '" + currentDate + "';";
			ResultSet rs = dbConn.executeQuery(sql);
			PreparedStatement ps;
			if (rs.next()) {
				ps = dbConn.prepareStatement("UPDATE `daily` SET `info` = ? WHERE `date` = ?");
				ps.setString(1, dailyMemo.getText().trim());
				ps.setString(2, currentDate);
			} else {
				ps = dbConn.prepareStatement("INSERT INTO `daily`(`date`, `info`, `circled`) VALUES (?, ?, 'N');");
				ps.setString(1, currentDate);
				ps.setString(2, dailyMemo.getText().trim());
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setOrderList() {
		String currentDate = DateUtil.convCalendarToUserTypedString(cal, "yyyy-MM-dd");
		
		int totalSum = 0;
		
		try {
			//Load Daily Order List
			String sql = "SELECT `address`.`ID`, `Datetime`, `Address`, `Menu`, `TotalPrice` FROM `order` inner join `address` on `order`.`AddressID` = `address`.`ID` WHERE `Datetime` Like '"
				+ currentDate + " %' and `address`.`activated` = 'Y' and `order`.`activated` = 'Y' ORDER BY `Datetime` DESC;";
			ResultSet rs = dbConn.executeQuery(sql);
			orderListData.clear();
			
			int cnt = 0;
			Vector<String> row;
			while (rs.next()) {
				cnt++;
				row = new Vector<String>();
				row.add("" + cnt);
				row.add(rs.getString(1));
				row.add(rs.getString(2).substring(11, 16));
				row.add(rs.getString(3));
				row.add(rs.getString(4));
				row.add(rs.getString(5));
				
				orderListData.add(row);
				
				totalSum += rs.getInt(5);
			}
			
			orderListTableModel.setDataVector(orderListData, orderListHeader);
			orderListTable.getColumnModel().getColumn(0).setMaxWidth(30);
			orderListTable.getColumnModel().getColumn(1).setMaxWidth(0);
			orderListTable.getColumnModel().getColumn(2).setMaxWidth(60);
			orderListTable.getColumnModel().getColumn(4).setMinWidth(160);
			orderListTable.getColumnModel().getColumn(4).setMaxWidth(160);
			orderListTable.getColumnModel().getColumn(5).setMaxWidth(75);
			orderListTable.getColumnModel().getColumn(5).setMinWidth(75);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		orderListLabel.setText(DateUtil.convCalendarToUserTypedString(cal, "yyyy�� MM�� dd��") + "        �հ� : \\" + totalSum + "��");
	}
	
	private class YearChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSpinner obj = (JSpinner)e.getSource();
			int year = ((Integer)obj.getValue()).intValue();
			setCalendar(year, month, 1);
			setOrderList();
			prevDate = null;
		}
	}
	
	private class MonthChangeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox obj = (JComboBox)e.getSource();
			int month = obj.getSelectedIndex();
			setCalendar(year, month, 1);
			setOrderList();
			prevDate = null;
		}
	}
	
	private class DateChangeListener extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			JLabel label = (JLabel)e.getSource();
			
			int year = ((Integer)changeYear.getValue()).intValue();
			int month = changeMonth.getSelectedIndex();
			int date = Integer.parseInt(label.getText());
			
			int btnIndex = dateList.indexOf(label);
			if (btnIndex < firstday) {
				cal.add(Calendar.MONTH, -1);
				year = cal.get(Calendar.YEAR);
				month = cal.get(Calendar.MONTH);
			} else if (btnIndex >= firstday + lastday) {
				cal.add(Calendar.MONTH, +1);
				year = cal.get(Calendar.YEAR);
				month = cal.get(Calendar.MONTH);
			}
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				setCalendar(year, month, date);
				setOrderList();
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				try {
					String sql = "SELECT `circled` FROM `daily` WHERE `date` = '" + year + "-" + (month+1) + "-" + date + "';";
					ResultSet rs = dbConn.executeQuery(sql);
					if (rs.next())
						if (rs.getString(1).equals("Y")) {
							dateList.get(firstday + date - 1).setIcon(null);
							dbConn.executeUpdate("UPDATE `daily` SET `circled` = 'N' WHERE `date` = '" + year + "-" + (month+1) + "-" + date + "';");
						} else {
							dateList.get(firstday + date - 1).setIcon(dateCircleIcon);
							dbConn.executeUpdate("UPDATE `daily` SET `circled` = 'Y' WHERE `date` = '" + year + "-" + (month+1) + "-" + date + "';");
						}
					else {
						dateList.get(firstday + date - 1).setIcon(dateCircleIcon);
						dbConn.executeUpdate("INSERT INTO `daily`(`date`, `info`, `circled`) VALUES ('" + year + "-" + (month+1) + "-" + date + "', '', 'Y');");							
					}
					dateList.get(firstday + date - 1).updateUI();
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				}
			}
		}
	}
	
	private class OrderClickListener extends MouseAdapter {
		public void mouseReleased(MouseEvent arg0) {
			int row = orderListTable.getSelectedRow();
			mfc.switchPanel(mfc.ADDRESS_PANEL, orderListTableModel.getValueAt(row, 1).toString());
		}
	}
	
	private class MemoListener extends KeyAdapter {
		public void keyReleased(KeyEvent arg0) {
			if (arg0.getSource().equals(dailyMemo))
				saveDailyMemo();
		}
	}
}
