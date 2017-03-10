package com.behere.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.behere.checklist.CheckList;
import com.behere.db.DBConnector;
import com.behere.main.DateUtil;
import com.behere.main.MainFrameController;
import com.behere.main.RelativeLayout;
import com.behere.main.SharedResource;

public class AddressPanel extends JPanel {
	private final static int PHONE_LIST = 1;
	private final static int PHONE_NEW = 2;
	private final static int PHONE_EDIT = 4;
	private final static int ORDER_LIST = 5;
	private final static int ORDER_NEW = 6;
	private final static int ORDER_EDIT = 7;
	
	private DBConnector dbConn = SharedResource.getDBConnector();
	private MainFrameController mfc;
	
	private RelativeLayout rl = new RelativeLayout();
	
	//Left-side
	private JLabel IDLabel = new JLabel("����ID");
	private JLabel addressLabel = new JLabel("�ּ�");
	private JLabel phoneListLabel = new JLabel("��ȣ");
	private JLabel memoLabel = new JLabel("�޸�");
	private JTextField ID = new JTextField();
	private JTextArea address = new JTextArea();
	private JScrollPane spAddress = new JScrollPane(address);
	private JTextField newPhonenum = new JTextField();
	private CheckList<String> phoneList = new CheckList<String>(true);
	private JScrollPane spPhoneList = new JScrollPane(phoneList);
	private JTextArea memo = new JTextArea();
	private JScrollPane spMemo = new JScrollPane(memo);
	private ImageIcon couponUseIcon = new ImageIcon("image/coupon_use.png");
	private JButton couponUseBtn = new JButton(couponUseIcon);
	
	//Right-side
	private ImageIcon saveIcon = new ImageIcon("image/shared_save.png");
	private JButton saveBtn = new JButton(saveIcon);
	private ImageIcon removeIcon = new ImageIcon("image/shared_remove.png");
	private JButton removeBtn = new JButton(removeIcon);
	
	private ImageIcon plusSmallIcon = new ImageIcon("image/shared_plus_small.png");
	private JButton addPhoneBtn = new JButton(plusSmallIcon);
	private JButton addOrderBtn = new JButton(plusSmallIcon);
	private ImageIcon editSmallIcon = new ImageIcon("image/shared_edit_small.png");
	private JButton editPhoneBtn = new JButton(editSmallIcon);
	private JButton editOrderBtn = new JButton(editSmallIcon);
	private ImageIcon minusSmallIcon = new ImageIcon("image/shared_minus_small.png");
	private JButton removePhoneBtn = new JButton(minusSmallIcon);
	private JButton removeOrderBtn = new JButton(minusSmallIcon);
	
	private JLabel regdateLabel = new JLabel("�����");
	private JTextField regdate = new JTextField();
	private JLabel couponAccLabel = new JLabel("��������");
	private JTextField couponAcc = new JTextField();
	private JLabel couponPreLabel = new JLabel("����");
	private JTextField couponPre = new JTextField();
	
	private JLabel orderListLabel = new JLabel("�ֹ�����");
	private Vector<String> header = new Vector<String>();
	private Vector<Vector<String>> data = new Vector<Vector<String>>();
	private DefaultTableModel orderTableModel = new DefaultTableModel(data, header) {
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;
		}
	};
	private JTable orderTable = new JTable(orderTableModel);
	private JScrollPane spOrderTable = new JScrollPane(orderTable);
	
	private MenuListPanel menuList = new MenuListPanel();;
	private Vector<String> payHeader = new Vector<String>();
	private Vector<Vector<String>> payData = new Vector<Vector<String>>();
	private DefaultTableModel payTableModel = new DefaultTableModel(payData, payHeader) {
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;
		}
	};
	private MenuListener ml = new MenuListener();
	
	private JTable payTable = new JTable(payTableModel);
	private JScrollPane spPayTable = new JScrollPane(payTable);
	private JLabel paySumLabel = new JLabel("�հ�");
	private JTextField paySum = new JTextField();
	private ImageIcon orderCancelIcon = new ImageIcon("image/order_cancel.png");
	private JButton orderCancelBtn = new JButton(orderCancelIcon);
	private ImageIcon orderConfirmIcon = new ImageIcon("image/order_confirm.png");
	private JButton orderConfirmBtn = new JButton(orderConfirmIcon);
	
	//Data
	private int newPhoneMode = PHONE_LIST;
	
	public AddressPanel(MainFrameController mfc) {
		this.mfc = mfc;
		
		setBackground(Color.WHITE);
		
		RelativeLayout rl = new RelativeLayout();
		this.setLayout(rl);
		
		ID.setEditable(false);
		address.setWrapStyleWord(true);
	    address.setLineWrap(true);  
	    memo.setWrapStyleWord(true);
	    memo.setLineWrap(true);
	    newPhonenum.setBackground(Color.PINK);
		//address.setBorder(new LineBorder(Color.GRAY, 1));
		//memo.setBorder(new LineBorder(Color.GRAY, 1));
		regdate.setEditable(false);
	    
		IDLabel.setFont(SharedResource.REGULAR_FONT);
		ID.setFont(SharedResource.REGULAR_FONT);
		addressLabel.setFont(SharedResource.REGULAR_FONT);
		address.setFont(SharedResource.REGULAR_FONT);
		newPhonenum.setFont(SharedResource.REGULAR_FONT);
		phoneListLabel.setFont(SharedResource.REGULAR_FONT);
		phoneList.setFont(SharedResource.REGULAR_FONT);
		memoLabel.setFont(SharedResource.REGULAR_FONT);
		memo.setFont(SharedResource.REGULAR_FONT);
		regdateLabel.setFont(SharedResource.REGULAR_FONT);
		regdate.setFont(SharedResource.REGULAR_FONT);
		couponAccLabel.setFont(SharedResource.REGULAR_FONT);
		couponAcc.setFont(SharedResource.REGULAR_FONT);
		couponPreLabel.setFont(SharedResource.REGULAR_FONT);
		couponPre.setFont(SharedResource.REGULAR_FONT);
		
		orderListLabel.setFont(SharedResource.REGULAR_FONT);
		header.add("����");
		header.add("ID");
		header.add("PhoneID");
		header.add("��¥");
		header.add("����");
		header.add("����");
		payHeader.add("ǰ��");
		payHeader.add("��");
		payHeader.add("����");
		
		orderTable.setFont(SharedResource.REGULAR_FONT);
		orderTable.setRowHeight(36);
		orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		payTable.setFont(SharedResource.REGULAR_FONT);;
		payTable.setRowHeight(36);
		paySumLabel.setFont(SharedResource.REGULAR_FONT);
		paySum.setFont(SharedResource.REGULAR_FONT);
		//paySum.setEditable(false);
		
		this.add(IDLabel);
		this.add(ID);
		this.add(addressLabel);
		this.add(spAddress);
		this.add(phoneListLabel);
		this.add(spPhoneList);
		this.add(newPhonenum);
		this.add(addPhoneBtn);
		this.add(editPhoneBtn);
		this.add(removePhoneBtn);
		this.add(memoLabel);
		this.add(spMemo);
		this.add(couponUseBtn);
		this.add(saveBtn);
		this.add(removeBtn);
		this.add(orderListLabel);
		this.add(addOrderBtn);
		this.add(editOrderBtn);
		this.add(removeOrderBtn);
		this.add(regdateLabel);
		this.add(regdate);
		this.add(couponAccLabel);
		this.add(couponAcc);
		this.add(couponPreLabel);
		this.add(couponPre);
		this.add(spOrderTable);
		this.add(menuList);
		this.add(spPayTable);
		this.add(paySumLabel);
		this.add(paySum);
		this.add(orderCancelBtn);
		this.add(orderConfirmBtn);
		
		rl.addConstraint(IDLabel, new RelativeLayout.Bounds(12, 12, 60, 36));
		rl.addConstraint(ID, new RelativeLayout.Bounds(80, 12, 216, 36));
		rl.addConstraint(addressLabel, new RelativeLayout.Bounds(12, 60, 60, 36));
		rl.addConstraint(spAddress, new RelativeLayout.Bounds(80, 60, 270, 80));
		rl.addConstraint(phoneListLabel, new RelativeLayout.Bounds(12, 152, 60, 36));
		rl.addConstraint(newPhonenum, new RelativeLayout.Bounds(80, 152, 270, 36));
		rl.addConstraint(spPhoneList, new RelativeLayout.Bounds(80, 152, 270, 180));
		rl.addConstraint(addPhoneBtn, new RelativeLayout.Bounds(12, 187, 35, 35));
		rl.addConstraint(editPhoneBtn, new RelativeLayout.Bounds(12, 222, 35, 35));
		rl.addConstraint(removePhoneBtn, new RelativeLayout.Bounds(12, 257, 35, 35));
		rl.addConstraint(memoLabel, new RelativeLayout.Bounds(12, 344, 60, 40));
		rl.addConstraint(spMemo, new RelativeLayout.Bounds(80, 344, 270, 240));
		rl.addConstraint(couponUseBtn, new RelativeLayout.Bounds(12, 404, 60, 60));
		rl.addConstraint(saveBtn, new RelativeLayout.Bounds(296, 12, 54, 36));
		rl.addConstraint(removeBtn, new RelativeLayout.Bounds(296, 12, 54, 36));
		rl.addConstraint(orderListLabel, new RelativeLayout.Bounds(380, 12, 120, 36));
		rl.addConstraint(addOrderBtn, new RelativeLayout.Bounds(470, 12, 35, 35));
		rl.addConstraint(editOrderBtn, new RelativeLayout.Bounds(505, 12, 35, 35));
		rl.addConstraint(removeOrderBtn, new RelativeLayout.Bounds(540, 12, 35, 35));
		rl.addConstraint(regdateLabel, new RelativeLayout.Bounds(600, 12, 60, 36));
		rl.addConstraint(regdate, new RelativeLayout.Bounds(660, 12, 120, 36));
		rl.addConstraint(couponAccLabel, new RelativeLayout.Bounds(800, 12, 80, 36));
		rl.addConstraint(couponAcc, new RelativeLayout.Bounds(880, 12, 40, 36));
		rl.addConstraint(couponPreLabel, new RelativeLayout.Bounds(920, 12, 40, 36));
		rl.addConstraint(couponPre, new RelativeLayout.Bounds(960, 12, 40, 36));
		//
		
		rl.addConstraint(spOrderTable, new RelativeLayout.Bounds(380, 60, 610, 520));
		rl.addConstraint(menuList, new RelativeLayout.Bounds(380, 60, 420, 520));
		rl.addConstraint(spPayTable, new RelativeLayout.Bounds(800, 60, 190, 448));
		rl.addConstraint(paySumLabel, new RelativeLayout.Bounds(815, 508, 40, 36));
		rl.addConstraint(paySum, new RelativeLayout.Bounds(870, 508, 120, 36));
		rl.addConstraint(orderCancelBtn, new RelativeLayout.Bounds(800, 544, 70, 36));
		rl.addConstraint(orderConfirmBtn, new RelativeLayout.Bounds(870, 544, 120, 36));
		
		couponUseBtn.addActionListener(new CouponUseListener());
		saveBtn.addActionListener(new AddressSaveRemoveListener());
		removeBtn.addActionListener(new AddressSaveRemoveListener());
		addPhoneBtn.addActionListener(new AddRemovePhoneListener());
		editPhoneBtn.addActionListener(new AddRemovePhoneListener());
		removePhoneBtn.addActionListener(new AddRemovePhoneListener());
		addOrderBtn.addActionListener(new AddRemoveOrderListener());
		editOrderBtn.addActionListener(new AddRemoveOrderListener());
		removeOrderBtn.addActionListener(new AddRemoveOrderListener());
		menuList.addActionListener(ml);
		orderCancelBtn.addActionListener(new OrderConfirmCancelListener());
		orderConfirmBtn.addActionListener(new OrderConfirmCancelListener());
		
		newPhonenum.setVisible(false);
		menuList.setVisible(false);
		spPayTable.setVisible(false);
		paySumLabel.setVisible(false);
		paySum.setVisible(false);
		orderCancelBtn.setVisible(false);
		orderConfirmBtn.setVisible(false);
	}
	
	private void setLayoutMode(int layoutMode) {
		if (layoutMode == PHONE_LIST) {
			newPhoneMode = PHONE_LIST;
			newPhonenum.setVisible(false);
			spPhoneList.setVisible(true);
		} else if (layoutMode == PHONE_NEW || layoutMode == PHONE_EDIT) {
			newPhoneMode = PHONE_NEW;
			newPhonenum.setText("");
			spPhoneList.setVisible(false);
			newPhonenum.setVisible(true);
			if (layoutMode == PHONE_EDIT) {
				newPhoneMode = PHONE_EDIT;
				newPhonenum.setText(phoneList.getSelectedValue().toString());
			}
		} else if (layoutMode == ORDER_LIST) {
			menuList.setVisible(false);
			spPayTable.setVisible(false);
			paySumLabel.setVisible(false);
			paySum.setVisible(false);
			orderCancelBtn.setVisible(false);
			orderConfirmBtn.setVisible(false);
			
			spOrderTable.setVisible(true);
		} else if (layoutMode == ORDER_NEW) {
			spOrderTable.setVisible(false);
			
			payData.clear();
			payTableModel.setDataVector(payData, payHeader);
			payTable.getColumnModel().getColumn(1).setMaxWidth(20); //Count
			payTable.getColumnModel().getColumn(2).setMaxWidth(75); //Price
			payTable.getColumnModel().getColumn(2).setMinWidth(75); //Price
			paySum.setText("0��");
			
			menuList.setVisible(true);
			spPayTable.setVisible(true);
			paySumLabel.setVisible(true);
			paySum.setVisible(true);
			orderCancelBtn.setVisible(true);
			orderConfirmBtn.setVisible(true);
		} else if (layoutMode == ORDER_EDIT) {
			
		}
		
		this.updateUI();
	}
	
	public boolean isTouched() {
		if (!isRegisteredAddressID()) {
			if (phoneList.getRowCount() == 0 && data.size() == 0 && address.getText().trim().equals(""))
				return false;
		}
		
		return true;
	}
	
	private int getAddressID() {
		//Error Processing Needed
		return Integer.parseInt(ID.getText());
	}
	
	private boolean isRegisteredAddressID() {
		try {
			ResultSet rs = dbConn.executeQuery("SELECT * FROM address WHERE ID = " + getAddressID() + ";");
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean saveAddress() {
		if (isRegisteredAddressID()) {
			if (address.getText().trim().equals(""))
				return false;
			
			try {
				//Address Update
				PreparedStatement ps = dbConn.prepareStatement("UPDATE `address` SET `address` = ?, `memo` = ?, `coupon_acc` = ?, `coupon_pre` = ? WHERE `ID` = ?;");
				ps.setString(1, address.getText());
				ps.setString(2, memo.getText());
				ps.setInt(3, Integer.parseInt("0" + couponAcc.getText()));
				ps.setInt(4, Integer.parseInt("0" + couponPre.getText()));
				ps.setInt(5, getAddressID());
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (isTouched()) {
			if (address.getText().trim().equals(""))
				return false;
			
			try {
				//Address Insert
				PreparedStatement ps = dbConn.prepareStatement("INSERT INTO `address`(`address`, `memo`, `regdate`, `coupon_acc`, `coupon_pre`) VALUES (?,?, now(), ?, ?);");
				ps.setString(1, address.getText());
				ps.setString(2, memo.getText());
				ps.setInt(3, Integer.parseInt("0" + couponAcc.getText()));
				ps.setInt(4, Integer.parseInt("0" + couponPre.getText()));
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	public void setAddressInfo(int addressID) {
		ID.setText("");
		address.setText("");
		memo.setText("");
		couponAcc.setText("0");
		couponPre.setText("0");
		
		phoneList.removeAll();
		data.clear();
		
		orderTableModel.setDataVector(data, header);
		orderTable.getColumnModel().getColumn(0).setMaxWidth(30); //����
		orderTable.getColumnModel().getColumn(1).setMaxWidth(0); //ID
		orderTable.getColumnModel().getColumn(2).setMaxWidth(0); //PhoneID
		orderTable.getColumnModel().getColumn(3).setMinWidth(180); //Datetime
		orderTable.getColumnModel().getColumn(3).setMaxWidth(180); 
		orderTable.getColumnModel().getColumn(5).setMinWidth(75); //TotalPrice
		orderTable.getColumnModel().getColumn(5).setMaxWidth(75); //TotalPrice
		
		if (addressID == 0) {
			try {
				ResultSet rs = dbConn.executeQuery("SHOW table status WHERE `Name` = 'address';");
				rs.next();
				ID.setText(rs.getString(11));
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			removeBtn.setVisible(false);
			saveBtn.setVisible(true);
		} else {
			try {
				ResultSet rs = dbConn.executeQuery("SELECT * FROM address WHERE ID = " + addressID + ";");
				if (rs.next()) {
					ID.setText(rs.getString(1));
					address.setText(rs.getString(3));
					memo.setText(rs.getString(4));
					regdate.setText(rs.getString(8).substring(0, 10));
					couponAcc.setText(rs.getString(6));
					couponPre.setText(rs.getString(7));
					
					ResultSet rs2 = dbConn.executeQuery("SELECT * FROM phonenum WHERE AddressID = " + addressID + " and `activated` = 'Y';");
					while (rs2.next()) {
						phoneList.add(rs2.getString(2));
					}
					rs2.close();
					
					rs2 = dbConn.executeQuery("SELECT ID, PhoneID, Datetime, Menu, TotalPrice FROM `order` WHERE AddressID = " + addressID + " and `activated` = 'Y' ORDER BY `DateTime` DESC;");
					
					int cnt = 0;
					Vector<String> row;
					while (rs2.next()) {
						cnt++;
						row = new Vector<String>();
						row.add("" + cnt);
						row.add(rs2.getString(1));
						row.add(rs2.getString(2));
						row.add(DateUtil.convUserTypedStringToDay(rs2.getString(3).substring(0, 16), "yyyy-MM-dd HH:mm") + " " + rs2.getString(3).substring(0, 16));
						row.add(rs2.getString(4));
						row.add(rs2.getString(5));
						data.add(row);				
					}
					
					orderTableModel.setDataVector(data, header);
					orderTable.getColumnModel().getColumn(0).setMaxWidth(30); //����
					orderTable.getColumnModel().getColumn(1).setMaxWidth(0); //ID
					orderTable.getColumnModel().getColumn(2).setMaxWidth(0); //PhoneID
					orderTable.getColumnModel().getColumn(3).setMinWidth(200); //Datetime
					orderTable.getColumnModel().getColumn(3).setMaxWidth(200); 
					orderTable.getColumnModel().getColumn(5).setMinWidth(75); //TotalPrice
					orderTable.getColumnModel().getColumn(5).setMaxWidth(75); //TotalPrice
					rs2.close();
				} else {
					JOptionPane.showMessageDialog(null, "����: ��ġ�ϴ� �ּҰ� �����ϴ�!");
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			saveBtn.setVisible(false);
			removeBtn.setVisible(true);
		}
		
		this.updateUI();
		
		setLayoutMode(ORDER_LIST);
	}
	
	private class CouponUseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (memo.getText().trim().equals("")) {
				memo.setText(DateUtil.convCalendarToUserTypedString(Calendar.getInstance(), "yyyy-MM-dd �������"));
			} else {
				memo.setText(memo.getText() + "\n" + DateUtil.convCalendarToUserTypedString(Calendar.getInstance(), "yyyy-MM-dd �������"));
			}
			
			if (address.getText().trim().equals(""))
				address.setText("�� �ּ�");
			saveAddress();
			setAddressInfo(getAddressID());
		}
	}
	
	private class AddressSaveRemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(saveBtn))
				mfc.switchPanel(mfc.SEARCH_PANEL, null);
			else if (e.getSource().equals(removeBtn)) {
				int confirm = JOptionPane.showConfirmDialog(null, address.getText() + "\n�� �ּҸ� ������ �����Ͻðڽ��ϱ�?", "Bareng", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					try {
						dbConn.executeUpdate("UPDATE `address` SET `activated` = 'N' WHERE `ID` = '" + getAddressID() + "';");
						mfc.switchPanel(mfc.SEARCH_PANEL, null);
					} catch (SQLException sqle) {
						sqle.printStackTrace();
					}
				}
			}
		}
	}
	
	private class AddRemovePhoneListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(addPhoneBtn)) {
				if (newPhoneMode == PHONE_LIST) {
					setLayoutMode(PHONE_NEW);
					newPhonenum.requestFocus();
				} else if (newPhoneMode == PHONE_NEW) {
					setLayoutMode(PHONE_LIST);
					if (!newPhonenum.getText().trim().equals("")) {
						try {
							PreparedStatement ps = dbConn.prepareStatement("INSERT INTO `phonenum`(`PhoneNum`, `AddressID`) VALUES (?, ?);");
							ps.setString(1, newPhonenum.getText().trim());
							ps.setInt(2, getAddressID());
							ps.executeUpdate();
							ps.close();
							phoneList.add(newPhonenum.getText().trim());
						} catch (SQLException sqle) {
							sqle.printStackTrace();
						}
					}
					
					if (address.getText().trim().equals(""))
						address.setText("�� �ּ�");
					saveAddress();
					setAddressInfo(getAddressID());
				}
			} else if (e.getSource().equals(editPhoneBtn)) {
				if (newPhoneMode == PHONE_LIST) {
					setLayoutMode(PHONE_EDIT);
					newPhonenum.requestFocus();
				} else if (newPhoneMode == PHONE_EDIT) {
					setLayoutMode(PHONE_LIST);
					try {
						PreparedStatement ps = dbConn.prepareStatement("UPDATE `phonenum` SET `PhoneNum` = ? WHERE `PhoneNum` = ? and `AddressID` = ?;");
						ps.setString(1, newPhonenum.getText().trim());
						ps.setString(2, phoneList.getSelectedValue().toString());
						ps.setInt(3, getAddressID());
						ps.executeUpdate();
						ps.close();
						setAddressInfo(getAddressID());
					} catch (SQLException sqle) {
						sqle.printStackTrace();
					}
				}
			} else if (e.getSource().equals(removePhoneBtn)) {
				if (phoneList.getSelectedValue() == null) {
					JOptionPane.showMessageDialog(null, "������ ��ȣ�� �������ּ���");
					return ;
				}
				
				int confirm = JOptionPane.showConfirmDialog(null, phoneList.getSelectedValue() + "\n�� ��ȣ�� ������ �����Ͻðڽ��ϱ�?", "Junto", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					try {
						dbConn.executeUpdate("UPDATE `phonenum` SET `activated` = 'N' WHERE `PhoneNum` = '" + phoneList.getSelectedValue() + "' and `AddressID` = " + getAddressID() + ";");
					} catch (SQLException sqle) {
						sqle.printStackTrace();
					}
				}
				
				setAddressInfo(getAddressID());
			}
		}
	}
	
	private class AddRemoveOrderListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(addOrderBtn)) {
				setLayoutMode(ORDER_NEW);
				MenuInfo mi = new MenuInfo();
				mi.setID(16);
				mi.setName("��޷�");
				mi.setShort("���");
				mi.setPrice(500);
				mi.setType(1);
				ml.actionPerformed(new ActionEvent(mi, MouseEvent.BUTTON1, ""));
			} else if (e.getSource().equals(editOrderBtn)) {
				int row = orderTable.getSelectedRow(); 
				if (row == -1) {
					JOptionPane.showMessageDialog(null, "������ �ֹ������� �������ּ���");
					return ;
				}
				
				String content = JOptionPane.showInputDialog(null, "������ �ֹ� ������ �Է��ϼ���", orderTableModel.getValueAt(row, 4));
				if (content == null)
					content = orderTableModel.getValueAt(row, 4).toString();
				
				String price = JOptionPane.showInputDialog(null, "������ ������ �Է��ϼ���\n���ڸ� �Է��ϼ���!", orderTableModel.getValueAt(row, 5));
				if (price == null)
					price = orderTableModel.getValueAt(row, 5).toString();
				
				try {
					PreparedStatement ps = dbConn.prepareStatement("UPDATE `order` SET `Menu` = ?, `TotalPrice` = ? WHERE `ID` = ?;");
					ps.setString(1, content.trim());
					ps.setInt(2, Integer.parseInt(price));
					ps.setInt(3, Integer.parseInt(orderTableModel.getValueAt(row, 1).toString()));
					ps.executeUpdate();
					ps.close();
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "���ݿ��� ���ڸ� �Է��ϼ���!");
				}
				
				setAddressInfo(getAddressID());
				
			} else if (e.getSource().equals(removeOrderBtn)) {
				int row = orderTable.getSelectedRow(); 
				if (row == -1) {
					JOptionPane.showMessageDialog(null, "������ �ֹ������� �������ּ���");
					return ;
				}
				
				int confirm = JOptionPane.showConfirmDialog(null, orderTableModel.getValueAt(row, 3) + " " + orderTableModel.getValueAt(row, 4) + "\n�� �ֺ� ������ ������ �����Ͻðڽ��ϱ�?", "Junto", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					try {
						dbConn.executeUpdate("UPDATE `order` SET `activated` = 'N' WHERE `ID` = " + orderTableModel.getValueAt(row, 1) + " and `AddressID` = " + getAddressID() + ";");
					} catch (SQLException sqle) {
						sqle.printStackTrace();
					}
				}
				
				setAddressInfo(getAddressID());
			}
		}
	}
	
	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			MenuInfo mi = (MenuInfo) e.getSource();
			
			boolean alreadyIn = false;
			for (int i = 0; i < payData.size(); i++) {
				Vector<String> item = payData.get(i);
				
				if (item.get(0).equals(mi.getName())) {
					alreadyIn = true;
					
					int cnt = 0;
					if (e.getID() == MouseEvent.BUTTON1)
						cnt = Integer.parseInt(item.get(1)) + 1;
					else if (e.getID() == MouseEvent.BUTTON3)
						cnt = Math.max(Integer.parseInt(item.get(1)) - 1, 0);
					
					int price = cnt * mi.getPrice();
					
					if (cnt > 0) {
						item.removeAllElements();
						item.add(mi.getName());
						item.add("" + cnt);
						item.add("" + price);
					} else {
						payData.remove(item);
						i--;
					}
				}
			}
			
			if (!alreadyIn && e.getID() == MouseEvent.BUTTON1) {
				Vector<String> row = new Vector<String>();
				row.add(mi.getName());
				row.add("1");
				row.add("" + mi.getPrice());
				payData.add(row);
			}
			
			int totalPrice = 0;
			for (Vector<String> item : payData)
				totalPrice += Integer.parseInt(item.get(2));
			
			payTableModel.setDataVector(payData, payHeader);
			payTable.getColumnModel().getColumn(1).setMaxWidth(20); //Count
			payTable.getColumnModel().getColumn(2).setMaxWidth(75); //Price
			payTable.getColumnModel().getColumn(2).setMinWidth(75); //Price
			
			paySum.setText(totalPrice + "��");
		}
	}
	
	private class OrderConfirmCancelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(orderConfirmBtn)) {
				try {
					Vector<String> row = new Vector<String>();
					ResultSet rs;
					
					String content = "";
					int totalPrice = 0;
					int couponIssue = 0;
					for (Vector<String> item : payData) {
						rs = dbConn.executeQuery("SELECT `short`, `MenuType` FROM `menu` WHERE `name` = '" + item.get(0) + "';");
						rs.next();
						if (!rs.getString(1).equals("���")) {
							content += rs.getString(1);
							content += item.get(1);
						}
						if (rs.getInt(2) == 1 && !(item.get(0).equals("�Ĺ�") || item.get(0).equals("���") || item.get(0).equals("����")))
							couponIssue += Integer.parseInt(item.get(1));
						
						rs.close();
						totalPrice += Integer.parseInt(item.get(2));
					}
					
					couponAcc.setText("" + (Integer.parseInt("0" + couponAcc.getText()) + couponIssue));
					couponPre.setText("" + (Integer.parseInt("0" + couponPre.getText()) + couponIssue));					
					
					int phoneID = 0;
					if (phoneList.getCheckedValue() != null) {
						rs = dbConn.executeQuery("SELECT `ID` FROM `phonenum` WHERE "
								+ "`PhoneNum` = '" + phoneList.getCheckedValue() + "' and "
								+ "`AddressID` = " + getAddressID() + ";");
						rs.next();
						phoneID = rs.getInt(1);
						rs.close();
					}
					
					////////////////////
					// Temporary Work //
					////////////////////
					if (paySum.getText().endsWith("��"))
						totalPrice = Integer.parseInt(paySum.getText().substring(0, paySum.getText().length() - 1));
					else
						totalPrice = Integer.parseInt(paySum.getText());
					
					PreparedStatement ps = dbConn.prepareStatement("INSERT INTO `order`(`AddressID`, `PhoneID`, `Menu`, `TotalPrice`, `Datetime`, `Memo`) VALUES (?, ?, ?, ?, now(), '');");
					ps.setInt(1, getAddressID());
					ps.setInt(2, phoneID);
					ps.setString(3, content);
					ps.setInt(4, totalPrice);
					ps.executeUpdate();
					ps.close();
					
					if (address.getText().trim().equals(""))
						address.setText("�� �ּ�");
					saveAddress();
					setAddressInfo(getAddressID());
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				}
				
				setLayoutMode(ORDER_LIST);
			} else if (e.getSource().equals(orderCancelBtn))
				setLayoutMode(ORDER_LIST);
		}
	}
}
