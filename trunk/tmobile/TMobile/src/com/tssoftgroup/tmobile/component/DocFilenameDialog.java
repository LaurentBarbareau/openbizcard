package com.tssoftgroup.tmobile.component;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;

import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.DownloadCombiner;
import com.tssoftgroup.tmobile.utils.DownloadCombinerDoc;

public class DocFilenameDialog extends Dialog {
	static String choices[] = { "Download", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.CANCEL };
	private static DocFilenameDialog instance;
	LabelField titleField = new LabelField("File name :");
	public TextField filenameTF = new TextField(TextField.FIELD_LEFT);
	String url = "";
	String filename = "";

	public static DocFilenameDialog getInstance(String urlFile, String filename) {
		if (instance == null) {
			instance = new DocFilenameDialog();
		} else {
			instance.filenameTF.setText("");
		}
		instance.filenameTF.setText(filename);
		instance.url = urlFile;
		instance.filename = filename;
		return instance;
	}

	public DocFilenameDialog() {
		super("Enter the name of file.\nThe file will be saved to "
				+ CrieUtils.getDocumentFolderStringForUser(), choices, values,
				0, Bitmap.getPredefinedBitmap(Bitmap.INFORMATION),
				Dialog.GLOBAL_STATUS);
		add(titleField);
		add(filenameTF);
	}

	public void myshow() {
		filenameTF.setText(filename);
		int result = this.doModal();
		// Submit
		if (result == Dialog.OK) {
			// Check not empty
			if (filenameTF.getText().equals("")) {
				Object[] keys = { "OK" };
				int[] values = { Dialog.OK };
				int ret = Dialog.ask("File name cannot be empty", keys, values,
						0);
				myshow();
				return;
			} else {
				// Check file exist
				String localPatht = CrieUtils.getDocumentFolderConnString()
						+ filenameTF.getText();
				try {
					FileConnection fc = (FileConnection) Connector.open(
							localPatht, Connector.READ_WRITE);
					if (fc.exists()) {
						fc.close();
						Object[] keys = { "OK" };
						int[] values = { Dialog.OK };
						int ret = Dialog.ask(
								"This file name is already exist.", keys,
								values, 0);
						myshow();
					} else {
						fc.close();
						// Do the upload
						DownloadCombinerDoc download = new DownloadCombinerDoc(url,
								localPatht, Const.DOWNLOAD_SIZE);
						download.start();
					}
				} catch (Exception e) {

				}

			}
		}
		// instance.displayField.setText(choices[result]);
	}
}