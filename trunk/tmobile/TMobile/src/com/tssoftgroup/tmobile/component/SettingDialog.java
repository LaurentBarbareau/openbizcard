package com.tssoftgroup.tmobile.component;

import java.util.Date;
import java.util.Timer;

import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.utils.CrieUtils;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;

public class SettingDialog extends Dialog implements FieldChangeListener {
	String[] choiceString = { "On", "Off" };
	ChoiceField pageChoice = new ChoiceField(
			"Download when network is roaming :", choiceString.length, 0) {
		public Object getChoice(int index) throws IllegalArgumentException {
			return choiceString[index];
		}
	};
	DateField df = new DateField("Update Time : ", new Date().getTime(),
			DateFormat.getInstance(DateFormat.TIME_DEFAULT));

	static String choices[] = { "OK", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.CANCEL };

	SimpleDateFormat myDtTm = new SimpleDateFormat("hh:mm");
	
	LabelField noteField = new LabelField("* The update won't be processed while the phone is in roaming network.");
	public SettingDialog() {
		super("Setting", choices, values, 0, Bitmap
				.getPredefinedBitmap(Bitmap.INFORMATION), Dialog.GLOBAL_STATUS);
		// Add field
		add(pageChoice);
		add(df);
		if(CrieUtils.isRoaming()){
			add(noteField);
		}
		noteField.setFont(noteField.getFont().derive(Font.PLAIN, df.getFont().getHeight() - 4));
		// / Load old Value
		setOldSetting();
	}

	private void setOldSetting() {
		ProfileEntry profile = ProfileEntry.getInstance();
		if (profile.roaming.equals("Off")) {
			pageChoice.setSelectedIndex(1);
		} else {
			pageChoice.setSelectedIndex(0);
		}
		// time
		try {
			if(!profile.settingTime.equals("")){
				try{
					Date rememberDate = new Date(Long.parseLong(profile.settingTime));
					df.setDate(rememberDate);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				df.setDate(HttpDateParser.parse("2010-06-16T00:00+01:00"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void myshow() {
		int result = this.doModal();
		// Submit
		if (result == Dialog.OK) {
			System.out.println("pagechoice "
					+ choiceString[pageChoice.getSelectedIndex()]);
			System.out.println("df " + df.getDate());
			String dateString = myDtTm.formatLocal(df.getDate());
			System.out.println("date String " + dateString);
			ProfileEntry profile = ProfileEntry.getInstance();
			profile.roaming =  choiceString[pageChoice.getSelectedIndex()];
			profile.settingTime = df.getDate()+"" ;
			profile.saveProfile();
			
		}
		// instance.displayField.setText(choices[result]);
	}

	public void fieldChanged(Field field, int context) {
		super.fieldChanged(field, context);
	}
}