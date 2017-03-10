package com.behere.main;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;

public class RelativeLayout implements LayoutManager {
	private HashMap<Component, Bounds> constraints = new HashMap<Component, Bounds>();
	
	public void addLayoutComponent(String name, Component comp) { }
	public void removeLayoutComponent(Component comp) { }
	
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}
	
	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			Insets insets = parent.getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			return dim;
		}
	}
	
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int maxwidth = parent.getWidth() - (insets.left + insets.right);
			int maxheight = parent.getHeight() - (insets.top + insets.bottom);
			int nmembers = parent.getComponentCount();
			
			for (int i = 0 ; i < nmembers ; i++) {
				Component m = parent.getComponent(i);
				Bounds b = constraints.get(m);
				if (m.isVisible() && b != null) {
					float top = b.getTop(), left = b.getLeft();
					float width = b.getWidth(), height = b.getHeight();
					
					if (b.isRelativeTop()) top *= maxheight;
					if (b.isRelativeLeft()) left *= maxwidth;
					if (b.isRelativeWidth()) width *= maxwidth;
					if (b.isRelativeHeight()) height *= maxheight;
					
					m.setBounds((int) left, (int) top, (int) width, (int) height);
				}
			}
		}
	}
	
	public void addConstraint(Component p, Bounds constraint) {
		constraints.put(p, constraint);
	}
	
	public static class Bounds {
		float x, y, width, height;
		boolean relativeTop, relativeLeft, relativeWidth, relativeHeight;
		
		public Bounds() {
			this(0, 0, 0, 0);
		}
		public Bounds(int x, int y, int width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(int x, int y, int width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);			
		}
		public Bounds(int x, int y, float width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(int x, int y, float width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(int x, float y, int width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(int x, float y, int width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);			
		}
		public Bounds(int x, float y, float width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(int x, float y, float width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(float x, int y, int width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(float x, int y, int width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);			
		}
		public Bounds(float x, int y, float width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(float x, int y, float width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(float x, float y, int width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(float x, float y, int width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);			
		}
		public Bounds(float x, float y, float width, int height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}
		public Bounds(float x, float y, float width, float height) {
			setLeft(x); setTop(y); setWidth(width); setHeight(height);
		}

		public float getLeft() { return x; }
		public float getTop() {	return y; }
		public float getWidth() { return width; }
		public float getHeight() { return height; }
		
		public void setLeft(int x) { this.x = x; relativeLeft = false; }
		public void setTop(int y) { this.y = y; relativeTop = false; }
		public void setWidth(int width) { this.width = width; relativeWidth = false; }
		public void setHeight(int height) { this.height = height; relativeHeight = false; }
		public void setLeft(float x) { this.x = x; relativeLeft = true; }
		public void setTop(float y) { this.y = y; relativeTop = true; }
		public void setWidth(float width) { this.width = width; relativeWidth = true; }
		public void setHeight(float height) { this.height = height; relativeHeight = true; }

		public boolean isRelativeLeft() { return relativeLeft; }
		public boolean isRelativeTop() { return relativeTop; }
		public boolean isRelativeWidth() { return relativeWidth; }
		public boolean isRelativeHeight() { return relativeHeight; }
	}
}
