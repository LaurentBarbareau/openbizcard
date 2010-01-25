package com.tssoft.one.utils;

public class ElementState {
	private int normalIcon;
	private int focusedIcon;
	private boolean focused;
	
	public ElementState(int n, int f, boolean s){
		normalIcon = n;
		focusedIcon = f;
		focused = s;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocusedIcon(int focusedIcon) {
		this.focusedIcon = focusedIcon;
	}

	public int getFocusedIcon() {
		return focusedIcon;
	}

	public void setNormalIcon(int normalIcon) {
		this.normalIcon = normalIcon;
	}

	public int getNormalIcon() {
		return normalIcon;
	}


	
}
