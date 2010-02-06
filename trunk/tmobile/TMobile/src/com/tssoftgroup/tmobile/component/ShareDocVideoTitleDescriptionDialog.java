package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;

import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.fileexplorer.FileExplorerDemoScreen;
import com.tssoftgroup.tmobile.movieexplorer.MovieExplorerDemoScreen;
import com.tssoftgroup.tmobile.utils.CrieUtils;

public class ShareDocVideoTitleDescriptionDialog extends Dialog {
	static String choices[] = { "Browse", "Submit", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.SAVE, Dialog.CANCEL };
	private static ShareDocVideoTitleDescriptionDialog instance;
	public static final int DELAY_BEFORE_KEY = 500;
	LabelField titleField = new LabelField("Title :");
	LabelField descriptionField = new LabelField("Description :");
	LabelField fileNameField = new LabelField("File :");
	public TextField titleFieldTF = new TextField(TextField.FIELD_LEFT);
	public TextField descriptionTF = new TextField(TextField.FIELD_LEFT);
	public TextField fileNameTF = new TextField(Field.NON_FOCUSABLE);

	public static ShareDocVideoTitleDescriptionDialog getInstance() {
		if (instance == null) {
			instance = new ShareDocVideoTitleDescriptionDialog();
		} else {
			instance.titleFieldTF.setText("");
			instance.descriptionTF.setText("");
		}
		return instance;
	}

	public ShareDocVideoTitleDescriptionDialog() {

		super("Select a file to upload:", choices, values, 0, Bitmap
				.getPredefinedBitmap(Bitmap.INFORMATION), Dialog.GLOBAL_STATUS);
		add(titleField);
		add(titleFieldTF);
		add(descriptionField);
		add(descriptionTF);
		add(fileNameField);
		add(fileNameTF);
	}

	public void myshow() {
		titleFieldTF.setText(Engine.getInstance().docInfo.getProductInfo());
		descriptionTF.setText(Engine.getInstance().docInfo.getDescription());
		int result = this.doModal();
		// Submit
		if (result == Dialog.SAVE) {
			// Check not empty
			if (titleFieldTF.getText().equals("")
					|| descriptionTF.getText().equals("")
					|| fileNameTF.getText().equals("")) {
				Object[] keys = { "OK" };
				int[] values = { Dialog.OK };
				int ret = 	Dialog.ask("Title, Description and Filename cannot be empty",
						keys, values, 0);
				myshow();
				return;
			}
			Engine.getInstance().docInfo.setProductInfo(titleFieldTF.getText());
			Engine.getInstance().docInfo
					.setDescription(descriptionTF.getText());
			Engine.getInstance().docInfo.setLocalFilename("file:///"
					+ fileNameTF.getText());
			if (!Engine.getInstance().docInfo.getLocalFilename().equals("")
					&& CrieUtils.getFileSize(Engine.getInstance().docInfo
							.getLocalFilename()) > 0) {
				Engine.getInstance().sendDocument();
			}
		}
		// Browse
		else if (result == Dialog.OK) {
			Engine.getInstance().docInfo.setProductInfo(titleFieldTF.getText());
			Engine.getInstance().docInfo
					.setDescription(descriptionTF.getText());
			UiApplication.getUiApplication().pushModalScreen(
					new FileExplorerDemoScreen(fileNameTF));
		}
		// Cancel
		else if (result == Dialog.CANCEL) {

		}
		// instance.displayField.setText(choices[result]);
	}
}