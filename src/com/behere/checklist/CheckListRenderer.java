package com.behere.checklist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class CheckListRenderer extends JPanel implements ListCellRenderer<Object> {
	private Icon pack[];
	private JLabel icon;
	private JTextArea text;
	
	public CheckListRenderer() {
		pack = new Icon[2];
		pack[0] = new ImageIcon("image/checked.png");
		pack[1] = new ImageIcon("image/unchecked.png");
		
		icon = new JLabel();
		text = new JTextArea();
		text.setFont(UIManager.getFont("Label.font"));  
		text.setEditable(false);  
	    text.setOpaque(false);
	    text.setWrapStyleWord(true);
	    text.setLineWrap(true);  
		
		setLayout(new BorderLayout(5, 5));
		//add(icon, BorderLayout.WEST);
		add(text, BorderLayout.CENTER);
	}
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		text.setFont(list.getFont());
		text.setText(value.toString());
		
		if (value instanceof CheckableItem) {
			if (((CheckableItem) value).isChecked()) {
				icon.setIcon(pack[0]);
			} else
				icon.setIcon(pack[1]);
		}
		
		if (isSelected) {
			setBackground(list.getSelectionBackground());
		    setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
		    setForeground(list.getForeground());
		}
		Border border = null;
		if (cellHasFocus) {
			border = UIManager.getBorder("List.focusCellHighlightBorder");
		} else {
			border = new LineBorder(getBackground(), 1);
		}
		setBorder(border);
		
		return this;
	}

}
