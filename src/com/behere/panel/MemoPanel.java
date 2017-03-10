package com.behere.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.behere.checklist.CheckList;
import com.behere.db.DBConnector;
import com.behere.main.DateUtil;
import com.behere.main.MainFrameController;
import com.behere.main.RelativeLayout;
import com.behere.main.SharedResource;

public class MemoPanel extends JPanel {
	private static final int LIST_MODE = 0;
	private static final int NEW_MODE = 1;
	
	private DBConnector dbConn = SharedResource.getDBConnector();
	private MainFrameController mfc;
	
	private RelativeLayout rl = new RelativeLayout();
	
	private JLabel memoListLabel = new JLabel("메모");
	private JTextField newMemoName = new JTextField();
	private ImageIcon plusSmallIcon = new ImageIcon("image/shared_plus_small.png");
	private JButton addMemoBtn = new JButton(plusSmallIcon);
	private CheckList<String> memoList = new CheckList<String>(true);
	private JTextArea memoText = new JTextArea();
	private JScrollPane spMemoText = new JScrollPane(memoText);
	private ImageIcon dateIcon = new ImageIcon("image/memo_date.png");
	private JButton dateBtn = new JButton(dateIcon);
	
	private int newMemoMode = LIST_MODE;
	
	public MemoPanel(MainFrameController mfc) {
		this.mfc = mfc;
		
		this.setBackground(Color.WHITE);
		newMemoName.setBackground(Color.PINK);
		
		memoListLabel.setFont(SharedResource.REGULAR_FONT);
		newMemoName.setFont(SharedResource.REGULAR_FONT);
		memoList.setFont(SharedResource.REGULAR_FONT);
		memoText.setFont(SharedResource.REGULAR_FONT);
		
		memoList.setAlwaysChecked(true);
	    memoText.setWrapStyleWord(true);
	    memoText.setLineWrap(true);
	    
	    addMemoBtn.addActionListener(new NewMemoListener());
	    memoList.addMouseListener(new MemoClickListener());
	    memoText.addKeyListener(new MemoListener());
	    dateBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		if (memoList.getCheckedIndex() != -1) {
	    			memoText.append(DateUtil.convCalendarToUserTypedString(Calendar.getInstance(), "yyyy-MM-dd"));
	    			saveMemoText();
	    		}
	    	}
	    });
		
		this.setLayout(rl);
		this.add(memoListLabel);
		this.add(newMemoName);
		this.add(addMemoBtn);
		this.add(memoList);
		this.add(spMemoText);
		this.add(dateBtn);
		newMemoName.setBorder(new LineBorder(Color.GRAY, 1));
		memoList.setBorder(new LineBorder(Color.GRAY, 1));
		spMemoText.setBorder(new LineBorder(Color.GRAY, 1));
		
		rl.addConstraint(memoListLabel, new RelativeLayout.Bounds(12, 12, 200, 36));
		rl.addConstraint(newMemoName, new RelativeLayout.Bounds(60, 12, 117, 36));
		rl.addConstraint(memoList, new RelativeLayout.Bounds(12, 60, 200, 520));
		rl.addConstraint(spMemoText, new RelativeLayout.Bounds(240, 60, 750, 520));
		rl.addConstraint(dateBtn, new RelativeLayout.Bounds(936, 12, 54, 36));
		
		setLayoutMode(LIST_MODE);
		updateMemoList();
	}
	
	private void updateMemoList() {
		try {
			ResultSet rs = dbConn.executeQuery("SELECT * FROM `memo`;");
			memoList.removeAll();
			while (rs.next()) {
				memoList.add(rs.getString(2));
				if (rs.isFirst())
					loadMemoText(rs.getString(2));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void setLayoutMode(int mode) {
		if (mode == NEW_MODE) {
			newMemoMode = NEW_MODE;
			newMemoName.setVisible(true);
			newMemoName.setText("");
			rl.addConstraint(addMemoBtn, new RelativeLayout.Bounds(176, 12, 36, 36));
			this.updateUI();
		} else if (mode == LIST_MODE) {
			newMemoMode = LIST_MODE;
			newMemoName.setVisible(false);
			rl.addConstraint(addMemoBtn, new RelativeLayout.Bounds(60, 12, 36, 36));
			this.updateUI();
		}
	}
	
	private void loadMemoText(String title) {
		try {
			String sql = "SELECT `memo` FROM `memo` WHERE "
					+ "`title` = '" + title + "';";
			
			ResultSet rs = dbConn.executeQuery(sql);
			rs.next();
			
			memoText.setText(rs.getString(1));
			
			memoText.updateUI();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	public void saveMemoText() {
		if (memoList.getCheckedValue() != null) {
			try {
				PreparedStatement ps = dbConn.prepareStatement(
						"UPDATE `memo` SET `memo` = ?, `lastupdate` = now() " +
						"WHERE `title` = ?;");
				ps.setString(1, memoText.getText().trim());
				ps.setString(2, memoList.getCheckedValue().toString());
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class NewMemoListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (newMemoMode == LIST_MODE) {
				setLayoutMode(NEW_MODE);
				newMemoName.requestFocus();
			} else if (newMemoMode == NEW_MODE){
				setLayoutMode(LIST_MODE);
				
				if (!newMemoName.getText().trim().equals("")) {
					try {
						PreparedStatement ps = dbConn.prepareStatement("INSERT INTO `memo`(`title`, `memo`, `lastupdate`) VALUES (?, '', now());");
						ps.setString(1, newMemoName.getText().trim());
						ps.executeUpdate();
						ps.close();

						updateMemoList();
					} catch (SQLException sqle) {
						sqle.printStackTrace();
					}
				}
			}
			
		}
	}
	
	private class MemoClickListener extends MouseAdapter {
		public void mouseReleased(MouseEvent arg0) {
			if (memoList.getCheckedIndex() != -1)
				loadMemoText(memoList.getCheckedValue().toString());
		}
	}
	
	private class MemoListener extends KeyAdapter {
		public void keyReleased(KeyEvent arg0) {
			if (arg0.getSource().equals(memoText))
				saveMemoText();
		}
	}
}
