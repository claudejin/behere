package com.behere.checklist;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class CheckList<E> extends JList {
	private DefaultListModel<CheckableItem<E>> dataModel;
	private boolean autoscrolls = false;
	private boolean always = false;
	private boolean radioMode = false;
	
	public CheckList() {
		this(false);
	}
	
	public CheckList(boolean radioMode) {
		super(new DefaultListModel<CheckableItem<E>>());
		
		this.radioMode = radioMode;
		
		dataModel = (DefaultListModel<CheckableItem<E>>) super.getModel();
		
		setCellRenderer(new CheckListRenderer());
		addMouseListener(new CheckListener());
		addKeyListener(new ListKeyListener());
	}
	
	public void add(E element) {
		dataModel.addElement(new CheckableItem<E>(element));
		if (autoscrolls)
			this.ensureIndexIsVisible(dataModel.getSize() - 1);
		if (always && dataModel.getSize() == 1)
			setCheckedIndex(0, true);
	}
	
	public void add(E element, boolean checked) {
		dataModel.addElement(new CheckableItem<E>(element, checked));
		if (autoscrolls)
			this.ensureIndexIsVisible(dataModel.getSize() - 1);
		if (always && dataModel.getSize() == 1)
			setCheckedIndex(0, true);
	}
	
	public int getRowCount() {
		return dataModel.getSize();
	}
	
	public CheckableItem<E> getElementAt(int index) {
		return dataModel.get(index);
	}
	
	public void remove(int index) {
		dataModel.remove(index);
	}
	
	public void remove(E element) {
		int cnt = dataModel.getSize();
		for (int i = 0; i < cnt; i++)
			if (dataModel.getElementAt(i).getElement().equals(element)) {
				dataModel.removeElementAt(i);
				break;
			}
	}
	
	public void removeAll() {
		dataModel.removeAllElements();
	}
	
	public boolean isCheckedIndex(int index) {
		return dataModel.get(index).isChecked();
	}
	
	public void uncheckExcept(int index) {
		int cnt = dataModel.getSize();
		for (int i = 0; i < cnt; i++)
			if (i != index)
				dataModel.get(i).setChecked(false);
	}
	
	public void setAlwaysChecked(boolean always) {
		this.always = always;
	}
	
	public void setCheckedIndex(int index, boolean checked) {
		dataModel.get(index).setChecked(checked);
	}
	
	public int getCheckedIndex() {
		int res = -1;
		int cnt = dataModel.getSize();
		for (int i = 0; i < cnt; i++)
			if (dataModel.get(i).isChecked()) {
				res = i;
				break;
			}
		
		return res;
	}
	
	public int[] getCheckedIndices() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		int cnt = dataModel.getSize();
		for (int i = 0; i < cnt; i++)
			if (dataModel.get(i).isChecked())
				indices.add(i);
		
		cnt = indices.size();
		int res[] = new int[cnt];
		for (int i = 0; i < cnt; i++)
			res[i] = indices.get(i);
		
		return res;
	}
	
	public Object getCheckedValue() {
		if (getCheckedIndex() == -1)
			return null;
		
		return dataModel.get(getCheckedIndex()).getElement();
	}
	
	public Object[] getCheckedValuesList() {
		int list[] = getCheckedIndices();
		Object res[] = new Object[list.length];
		for (int i = 0; i < list.length; i++)
			res[i] = dataModel.get(list[i]).getElement();
		
		return res;
	}
	
	public void setAutoscrolls(boolean autoscrolls) {
		super.setAutoscrolls(autoscrolls);
		this.autoscrolls = autoscrolls;
	}
	
	private class CheckListener extends MouseAdapter {
		 public void mousePressed(MouseEvent e) {
			 if (radioMode) { // || e.getX() <= 16
				 int index = locationToIndex(e.getPoint());
				 
				 if (index != -1) {
					 if (!always || !(getCheckedIndices().length == 1 && getCheckedIndex() == index)) {
						 if (radioMode)
							 uncheckExcept(index);
						 CheckableItem<E> item = (CheckableItem<E>) getModel().getElementAt(index);
						 item.setChecked(!item.isChecked());
						 if (radioMode)
							 repaint();
						 else
							 repaint(getCellBounds(index, index));
					 }
				 }
			 }
		 }
	}
	
	private class ListKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				if (getSelectedIndex() != -1)
					remove(getSelectedIndex());
			}
		}
	}
}
