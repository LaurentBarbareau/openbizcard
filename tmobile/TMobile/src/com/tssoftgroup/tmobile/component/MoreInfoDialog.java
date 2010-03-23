package com.tssoftgroup.tmobile.component;

import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.screen.FixMainScreen;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.DownloadCombiner;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;

public class MoreInfoDialog extends Dialog {
	static String choices[] = { "OK" };
	static int values[] = { Dialog.OK};
	public LabelField moreinfoLabelField = new CrieLabelField("more info",
			MyColor.FONT_DESCRIPTION_PLAYER_DETAIL_DIALOG,
			Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT + 5,
			LabelField.FOCUSABLE);
	VerticalFieldManager commentsManager;
	PicInfo picInfo;
	////
	
	public MoreInfoDialog(VerticalFieldManager moreinfoManager, PicInfo picInfo) {
		super("More Info", choices, values,
				0, Bitmap.getPredefinedBitmap(Bitmap.INFORMATION),
				Dialog.GLOBAL_STATUS);
		this.picInfo = picInfo;
		this.commentsManager = moreinfoManager;
		add(moreinfoLabelField);
		add(moreinfoManager);
		XYEdges edge = new XYEdges(2, 15 * Display.getWidth() / 480, 2, 15 * Display
				.getWidth() / 480);
//		add(titleField);
//		add(filenameTF);
	}

	
	public void myshow() {
		int result = this.doModal();
		// Submit
		if (result == Dialog.OK) {
			System.out.println("Dialog ok");
		}
		// instance.displayField.setText(choices[result]);
	}
}