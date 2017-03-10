package com.behere.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.behere.db.DBConnector;
import com.behere.main.MainFrameController;
import com.behere.main.RelativeLayout;
import com.behere.main.SharedResource;

public class SearchPanel extends JPanel {
	private static final int SEARCH_MODE = 1;
	private static final int LISTUP_MODE = 2;
	
	private DBConnector dbConn = SharedResource.getDBConnector();
	private MainFrameController mfc;
	
	private RelativeLayout rl = new RelativeLayout();
	
	private JLabel keywordLabel = new JLabel("검색어를 입력해주세요");
	private JTextField keyword = new JTextField();
	private ImageIcon deleteBtnImg = new ImageIcon("image/search_delete.png");
	private JButton deleteBtn = new JButton(deleteBtnImg);
	
	private Vector<String> resultHeader = new Vector<String>();
	private Vector<Vector<String>> resultData = new Vector<Vector<String>>();
	private DefaultTableModel resultTableModel = new DefaultTableModel(resultData, resultHeader) {
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return false;
		}
	};
	private JTable resultTable = new JTable(resultTableModel);
	private JScrollPane spResultTable = new JScrollPane(resultTable);
	
	public SearchPanel(MainFrameController mfc) {
		this.mfc = mfc;
		
		keywordLabel.setFont(SharedResource.REGULAR_FONT);
		keyword.setFont(SharedResource.LARGE_FONT);
		
		keyword.addKeyListener(new KeywordListener());
		deleteBtn.addActionListener(new KeywordListener());
		
		resultHeader.add("ID");
		resultHeader.add("종류");
		resultHeader.add("내용");
		resultHeader.add("메모");
		
		resultTable.setFont(SharedResource.REGULAR_FONT);
		resultTable.setRowHeight(36);
		resultTable.addMouseListener(new ResultClickListener());
		
		spResultTable.setVisible(false);
		
		this.setBackground(Color.WHITE);
		
		this.setLayout(rl);
		
		this.add(keywordLabel);
		this.add(keyword);
		this.add(deleteBtn);
		this.add(spResultTable);
		rl.addConstraint(keywordLabel, new RelativeLayout.Bounds(300, 224, 400, 36));
		rl.addConstraint(spResultTable, new RelativeLayout.Bounds(0, 52, 1F, 0.9F));
		
		setLayoutMode(SEARCH_MODE);
	}
	
	private void setLayoutMode(int layoutMode) {
		if (layoutMode == SEARCH_MODE) {
			keywordLabel.setVisible(true);
			rl.addConstraint(keyword, new RelativeLayout.Bounds(300, 260, 400, 52));
			rl.addConstraint(deleteBtn, new RelativeLayout.Bounds(700, 260, 51, 51));
			spResultTable.setVisible(false);
			
			focusKeyword();
		} else if (layoutMode == LISTUP_MODE) {
			keywordLabel.setVisible(false);
			rl.addConstraint(keyword, new RelativeLayout.Bounds(300, 0, 400, 52));
			rl.addConstraint(deleteBtn, new RelativeLayout.Bounds(700, 0, 51, 51));
			spResultTable.setVisible(true);
		}
		
		this.updateUI();
	}
	
	public void focusKeyword() {
		keyword.requestFocus();
	}
	
	private class KeywordListener extends KeyAdapter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			keyword.setText("");
			setLayoutMode(SEARCH_MODE);
		}
		
		public void keyReleased(KeyEvent arg0) {
			String keywordTmp = keyword.getText().trim().replace(" ", "%");
			if (keywordTmp.equals("")) {
				setLayoutMode(SEARCH_MODE);
			} else if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				setLayoutMode(LISTUP_MODE);
				
				try {
					String sql;
					sql = "(SELECT ID, '주소' as '종류', address as '내용', memo as '메모' "
							+ "FROM address "
							+ "WHERE address like '%" + keywordTmp + "%' and activated = 'Y') union "
							+ "(SELECT `AddressID` as 'ID', '번호' as '종류', concat(`Address`, ' / ', `PhoneNum`) as '내용', `Memo` as '메모' FROM ((SELECT `ID` as 'PhoneID', `PhoneNum`, `AddressID` FROM `PhoneNum` WHERE `PhoneNum` like '%" + keywordTmp + "%' and `activated` = 'Y') as a NATURAL JOIN (SELECT `ID` as 'AddressID', `Address`, `Memo` FROM `Address` WHERE `Activated` = 'Y') as b));";
							//+ "(SELECT AddressID as 'ID', '번호' as '종류', phonenum as '내용', memo as '메모' "
							//+ "FROM (SELECT phonenum join `address` ) "
							//+ "WHERE phonenum like '%" + keywordTmp + "%' and "
							//+ "(SELECT `activated` FROM `address` WHERE `address`.`ID` = `phonenum`.`AddressID`) = 'Y');";
					
					ResultSet rs = dbConn.executeQuery(sql);
					
					resultData.clear();
					
					Vector<String> row;
					while (rs.next()) {
						row = new Vector<String>();
						for (int i = 1; i <= resultHeader.size(); i++) {
							row.add(rs.getString(i));
						}
						resultData.add(row);
					}
					
					resultTableModel.setDataVector(resultData, resultHeader);
					resultTable.getColumnModel().getColumn(0).setMaxWidth(0);
					resultTable.getColumnModel().getColumn(1).setMaxWidth(80);
					resultTable.getColumnModel().getColumn(2).setPreferredWidth(400);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class ResultClickListener extends MouseAdapter {
		public void mouseReleased(MouseEvent arg0) {			
			int row = resultTable.getSelectedRow();
			mfc.switchPanel(mfc.ADDRESS_PANEL, resultTableModel.getValueAt(row, 0).toString());
		}
	}
}
