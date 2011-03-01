package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;

public class ConfirmRoamingDialog extends Dialog implements FieldChangeListener {

	static String choices[] = { "OK", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.CANCEL };
	LabelField confirmField = new LabelField("Your phone is in roaming network at this moment. Are you sure to download now?");
	public ConfirmRoamingDialog() {
		super("Download in Roaming?", choices, values, 0, Bitmap
				.getPredefinedBitmap(Bitmap.INFORMATION), Dialog.GLOBAL_STATUS);
		// Add field
		add(confirmField);
		// / Load old Value
	}
	public void myshow(ButtonListener btnListener) {
		int result = this.doModal();
		// Submit
		if (result == Dialog.OK) {
			if(btnListener != null){ 
				btnListener.doFieldChage();
			}
		}
		// instance.displayField.setText(choices[result]);
	}

	public void fieldChanged(Field field, int context) {
		super.fieldChanged(field, context);
	}
}