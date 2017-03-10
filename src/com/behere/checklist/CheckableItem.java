package com.behere.checklist;

public class CheckableItem<E> {
	private boolean checked;
	private E element;
	
	public CheckableItem(E element) {
		this(element, false);
	}
	
	public CheckableItem(E element, boolean checked) {
		this.element = element;
		this.checked = checked;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public E getElement() {
		return element;
	}
	
	public String toString() {
		return element.toString();
	}
}
