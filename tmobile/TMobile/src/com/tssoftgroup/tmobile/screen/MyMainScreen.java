package com.tssoftgroup.tmobile.screen;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;

import com.tssoftgroup.tmobile.component.BitmapFieldWithStatus;
import com.tssoftgroup.tmobile.component.ButtonListener;
import com.tssoftgroup.tmobile.component.CustomButtonField;
import com.tssoftgroup.tmobile.component.MainListVerticalFieldManager;
import com.tssoftgroup.tmobile.component.engine.Engine;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.Wording;

public class MyMainScreen extends FixMainScreen {
	Img imgStock = Img.getInstance();

	private static MyMainScreen instance;
	
	public static MyMainScreen getInstance() {
//		if (instance == null) {
			instance = new MyMainScreen();
//		}
		return instance;
	}

	private MyMainScreen() {
		super(MODE_MCAST);
		// less than 50 meg
		if(CrieUtils.checkSDCardSize() < 50000000){
			Dialog.alert("Your SD Card's free space is less that 50 MB. Please free up space for using the application to download video");
		}
		System.out.println("checkSDCardSize " + CrieUtils.checkSDCardSize()) ;
		int firstUp = Display.getWidth() < 400 ? 0 : 24;
		XYEdges edge = new XYEdges(firstUp, 0, 2, 0);
		Bitmap img = imgStock.getHeader();
		BitmapFieldWithStatus bf = new BitmapFieldWithStatus(img,
				BitmapField.FIELD_HCENTER | BitmapField.USE_ALL_WIDTH, "");
		add(bf);
		Engine.getInstance().registerStatus(bf);
		try {
			MainListVerticalFieldManager videoManager = new MainListVerticalFieldManager();

			int marginRow = (Display.getHeight() - 6
					* imgStock.getVideoOnDemandOn().getHeight()
					- imgStock.getHeader().getHeight() - imgStock.getFooter()
					.getHeight()) / 7;
			CustomButtonField videoButton = new CustomButtonField(null,
					imgStock.getVideoOnDemandOn(), imgStock.getVideoOnDemand());
			// videoButton.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			videoButton.setChangeListener(new ButtonListener(3));
			videoButton.setMargin(marginRow, 0, 0, 0);
			videoManager.add(videoButton);
			edge = new XYEdges(2, 0, 2, 0);

			CustomButtonField movieButton = new CustomButtonField(null,
					imgStock.getVideoConnectOn(), imgStock.getVideoConnect());
			// movieButton.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			movieButton.setChangeListener(new ButtonListener(24));
			movieButton.setMargin(marginRow, 0, 0, 0);
			videoManager.add(movieButton);

			CustomButtonField documentButton = new CustomButtonField(null,
					imgStock.getDocumentSharingOn(), imgStock
							.getDocumentSharing());
			// documentButton.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			documentButton.setChangeListener(new ButtonListener(4));
			documentButton.setMargin(marginRow, 0, 0, 0);
			videoManager.add(documentButton);

			CustomButtonField traningButton = new CustomButtonField(null,
					imgStock.getTrainingOn(), imgStock.getTraining());
			// traningButton.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			traningButton.setChangeListener(new ButtonListener(5));
			traningButton.setMargin(marginRow, 0, 0, 0);
			videoManager.add(traningButton);

			CustomButtonField contactButton = new CustomButtonField(null,
					imgStock.getContactManagementOn(), imgStock
							.getContactManagement());
			// contactButton.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			contactButton.setChangeListener(new ButtonListener(6));
			contactButton.setMargin(marginRow, 0, 0, 0);
			videoManager.add(contactButton);
			int lastDown = Display.getWidth() < 400 ? 0 : 57;
			edge = new XYEdges(2, 0, lastDown, 0);

			CustomButtonField pollButton = new CustomButtonField(null, imgStock
					.getPollOn(), imgStock.getPoll());
			// pollButton.setBorder(BorderFactory.createSimpleBorder(edge,
			// Border.STYLE_TRANSPARENT));
			pollButton.setChangeListener(new ButtonListener(25));
			pollButton.setMargin(marginRow, 0, 0, 0);
			videoManager.add(pollButton);
			add(videoManager);

		} catch (Exception e) {
			System.out.println("" + e.toString());
		}
		
		// bf = new BitmapField(img, Field.FIELD_BOTTOM | Field.USE_ALL_HEIGHT);
		// add(bf);
	}

	
	// creating save method
}