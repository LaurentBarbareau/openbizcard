package com.tssoftgroup.tmobile.component;

import net.rim.device.api.ui.component.ButtonField;

public class CrieSimpleButtonField extends ButtonField {
	public CrieSimpleButtonField(String label) {
		super(label);
	}
	public CrieSimpleButtonField(String label, long style) {
		super(label, style);
	}
	protected boolean navigationClick(int status, int time) {
		fieldChangeNotify(0);
		return true;
	}
}
