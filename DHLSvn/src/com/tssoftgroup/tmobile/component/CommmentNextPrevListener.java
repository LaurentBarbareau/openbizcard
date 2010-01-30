package com.tssoftgroup.tmobile.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.screen.FixMainScreen;
import com.tssoftgroup.tmobile.utils.Const;

public class CommmentNextPrevListener implements FieldChangeListener {
	VerticalFieldManager commentManager;
	PicInfo picinfo;
	ScreenWithComment scr;

	public CommmentNextPrevListener(VerticalFieldManager commentManager,
			PicInfo picinfo, ScreenWithComment scr) {
		this.commentManager = commentManager;
		this.picinfo = picinfo;
		this.scr = scr;
	}

	public void fieldChanged(Field field, int context) {
		MyButtonField btnField = (MyButtonField) field;
		if (btnField.getLabel().equals(Const.NEXT_LABEL)) {
//			Dialog.alert("Next ");
			scr.setCurrentCommentInd(scr.getCurrentCommentInd()
					+ Const.NUM_LIST);
			FixMainScreen.processHaveComment(commentManager, picinfo, scr);
			// Dialog.alert("Next ");

		} else if (btnField.getLabel().equals(Const.PREVIOUS_LABEL)) {
			// Dialog.alert("PREV");
//			Dialog.alert("PREV");
			scr.setCurrentCommentInd(scr.getCurrentCommentInd() - Const.NUM_LIST);
			FixMainScreen.processHaveComment(commentManager, picinfo, scr);
		}
	}

}
