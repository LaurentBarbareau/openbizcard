package com.tssoftgroup.tmobile.component;

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Video;
import com.tssoftgroup.tmobile.screen.MCastDetail;
import com.tssoftgroup.tmobile.screen.TrainingVideoScreen;
import com.tssoftgroup.tmobile.screen.VideoConnectDetail;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.DownloadCombiner;

public class SettingDialog extends Dialog implements FieldChangeListener {
	String[] choiceString = { "Yes", "No"};
	ChoiceField pageChoice = new ChoiceField("Go to :", choiceString.length, 0) {
		public Object getChoice(int index) throws IllegalArgumentException {
			return choiceString[index];
		}
	};
	DateField df = new DateField("Schedule Time : ", new Date().getTime(),
			DateFormat.getInstance(DateFormat.TIME_DEFAULT));

	static String choices[] = { "OK", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.CANCEL };

	public SettingDialog() {
		super("Setting", choices, values, 0, Bitmap
				.getPredefinedBitmap(Bitmap.INFORMATION), Dialog.GLOBAL_STATUS);
		// 
		add(df);
	}

	public void myshow() {
		int result = this.doModal();
		// Submit
		if (result == Dialog.OK) {
		}
		// instance.displayField.setText(choices[result]);
	}

	public void fieldChanged(Field field, int context) {
		super.fieldChanged(field, context);
	}
}