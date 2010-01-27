package com.tssoftgroup.tmobile.component;

import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;

import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.utils.CrieUtils;

public class VideoTitleDescriptionDialog extends Dialog {
	static String choices[] = { "Capture", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.CANCEL };
	private static VideoTitleDescriptionDialog instance;
	public static final int DELAY_BEFORE_KEY = 500;
	LabelField titleField = new LabelField("Title :");
	LabelField descriptionField = new LabelField("Description :");
	public TextField titleFieldTF = new TextField(TextField.FIELD_LEFT);
	public TextField descriptionTF = new TextField(TextField.FIELD_LEFT);

	public static VideoTitleDescriptionDialog getInstance() {
		if (instance == null) {
			instance = new VideoTitleDescriptionDialog();
		} else {
			instance.titleFieldTF.setText("");
			instance.descriptionTF.setText("");
		}
		return instance;
	}

	public VideoTitleDescriptionDialog() {
		
		super("Set Title and Description", choices, values, 0, Bitmap
				.getPredefinedBitmap(Bitmap.INFORMATION), Dialog.GLOBAL_STATUS);
		add(titleField);
		add(titleFieldTF);
		add(descriptionField);
		add(descriptionTF);
	}

	public void myshow() {
		int result = this.doModal();
		if (result == Dialog.OK) {
			Engine.getInstance().picInfo.setTitle(titleFieldTF.getText());
			Engine.getInstance().picInfo
					.setDescription(descriptionTF.getText());
			// CameraArguments vidargs = new
			// CameraArguments(/*CameraArguments.ARG_VIDEO_RECORDER*/);
			// Invoke.invokeApplication( Invoke.APP_TYPE_CAMERA, vidargs );
			Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA,
					new CameraArguments());
			// delay 2 sec
			new Thread(new Runnable() {

				public void run() {
					try {
						Thread.sleep(DELAY_BEFORE_KEY);
						CrieUtils.injectMenu();
						CrieUtils.injectDown(2);
						CrieUtils.injectPress();
//						Thread.sleep(DELAY_BEFORE_KEY * 3);
//						CrieUtils.injectMenu();
//						CrieUtils.injectDown(1);
//						CrieUtils.injectPress();
//						if(curve8900){
//						CrieUtils.injectDown(2);
//						}else // curve 8530 
//						{
//							CrieUtils.injectDown(1);
//						}
//						CrieUtils.injectPress();
//						CrieUtils.injectDown(2);
//						CrieUtils.injectPress();
//						CrieUtils.injectBack();
						Thread.sleep(DELAY_BEFORE_KEY * 5);
						CrieUtils.injectPress();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {

		}
		// instance.displayField.setText(choices[result]);
	}
}