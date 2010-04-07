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

public class CommentsDialog extends Dialog {
	static String choices[] = { "Comment", "Cancel" };
	static int values[] = { Dialog.OK, Dialog.CANCEL };
	public LabelField commentLabelField = new CrieLabelField("comments",
			MyColor.FONT_DESCRIPTION_PLAYER_DETAIL_DIALOG,
			Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT + 5,
			LabelField.FOCUSABLE);
	LabelField postCommentLabel = new CrieLabelField("post comment",
			MyColor.FONT_DESCRIPTION_PLAYER_DETAIL_DIALOG,
			Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT + 5,
			LabelField.FOCUSABLE);
	EditField postCommentTF = new EditField("", "");
	VerticalFieldManager commentsManager;
	PicInfo picInfo;
	////
	
	public CommentsDialog(VerticalFieldManager commentManager, PicInfo picInfo) {
		super("View Comments", choices, values,
				0, Bitmap.getPredefinedBitmap(Bitmap.INFORMATION),
				Dialog.GLOBAL_STATUS);
		this.picInfo = picInfo;
		this.commentsManager = commentManager;
		add(commentLabelField);
		add(commentManager);
		add(postCommentLabel);
		XYEdges edge = new XYEdges(2, 15 * Display.getWidth() / 480, 2, 15 * Display
				.getWidth() / 480);
		postCommentTF.setMargin(edge);
		add(postCommentTF);
//		add(titleField);
//		add(filenameTF);
	}

	public void addComment() {
		Engine.commnetPicInfo = picInfo;
		System.out.println("postCommentTF == null");
		if (postCommentTF.getText().equals("")) {
			Dialog.alert("Please input comment");
			myshow();
			return;
		}
		Comment comment = new Comment();
		comment.setComment(postCommentTF.getText());
		comment.setTime("a moment ago");
		comment.setUser(ProfileEntry.getInstance().name);
		Engine.comment = comment;
		picInfo.comments.addElement(comment);
		System.out.println("Engine add comment");
		Engine.getInstance().addComment(picInfo.getId(), Engine.userId,
				postCommentTF.getText(), picInfo.isMCast());
		// Add to ui
		CrieLabelField commentLabel = new CrieLabelField("By "
				+ Engine.comment.getUser() + " at " + Engine.comment.getTime()
				+ ": ", MyColor.FONT_DESCRIPTION_PLAYER,
				Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT
						- (Display.getWidth() > 350 ? 8 : 2),
				LabelField.FOCUSABLE);
		commentLabel.isFix = true;
		XYEdges edge = new XYEdges(2, 15 * Display.getWidth() / 480, 2, 15 * Display
				.getWidth() / 480);
		commentLabel.setMargin(edge);
		// commentLabel.setBorder(BorderFactory.createSimpleBorder(edge,
		// Border.STYLE_TRANSPARENT));
		System.out.println("commentsManager.add(commentLabel)");
		if(picInfo.comments.size() == 1){
			commentsManager.deleteAll();
		}
		commentsManager.add(commentLabel);
		commentLabel = new CrieLabelField(Engine.comment.getComment(),
				MyColor.FONT_DESCRIPTION_PLAYER_DETAIL_DIALOG,
				Scale.VIDEO_CONNECT_DETAIL_COMMENT_FONT_HEIGHT,
				LabelField.FOCUSABLE);
		commentLabel.setMargin(edge);
		commentLabel.isFix = true;
		// commentLabel.setBorder(BorderFactory.createSimpleBorder(edge,
		// Border.STYLE_TRANSPARENT));
		commentsManager.add(commentLabel);
		postCommentTF.setText("");
	}
	
	public void myshow() {
		int result = this.doModal();
		// Submit
		if (result == Dialog.OK) {
			System.out.println("Dialog ok");
			addComment();
		}
		// instance.displayField.setText(choices[result]);
	}
}