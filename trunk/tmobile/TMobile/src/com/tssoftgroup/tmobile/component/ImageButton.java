package com.tssoftgroup.tmobile.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;

import com.tssoftgroup.tmobile.utils.CrieUtils;

public class ImageButton extends BitmapField {

	String name;
	String title;
	ImageButtonListener eventListener;
	boolean hasFocus = false;
	Bitmap img;
	Bitmap imgFocus;
	
	Bitmap focusBG = null;
	protected String text;
	protected String numComment; //use for comment button ONLY!!!

	public ImageButton(String name, String title, Bitmap imageName,
			Bitmap imageFocusName) {
		super(null, Field.FOCUSABLE);
		img = imageName;
		imgFocus = imageFocusName;
		setBitmap(img);
		this.name = name;
		this.title = title;
	}
	
	public ImageButton(String name, String title, Bitmap imageName,
			Bitmap imageFocusName, long style) {
		super(null, Field.FOCUSABLE|style);
		img = imageName;
		imgFocus = imageFocusName;
		setBitmap(img);
		this.name = name;
		this.title = title;
	}
	
	public ImageButton(String name, String title, Bitmap imageName,
			Bitmap imageFocusName,Bitmap focusBG) {
		super(null, Field.FOCUSABLE);
		img = imageName;
		imgFocus = imageFocusName;
		setBitmap(img);
		this.name = name;
		this.title = title;
		this.focusBG = focusBG;
	}
	
	public ImageButton(String name, String title, Bitmap imageName,
			Bitmap imageFocusName,EncodedImage focusBG) {
		super(null, Field.FOCUSABLE);
		init(name, title, imageName, imageFocusName, focusBG);
	}
	
	public ImageButton(String name, String title, Bitmap imageName,
			Bitmap imageFocusName,EncodedImage focusBG, long style) {
		super(null, Field.FOCUSABLE|style);
		init(name, title, imageName, imageFocusName, focusBG);
	}
	
	
	public void init(String name, String title, Bitmap imageName,
			Bitmap imageFocusName,EncodedImage focusBG) {
		img = imageName;
		imgFocus = imageFocusName;
		setBitmap(img);
		this.name = name;
		this.title = title;
		if(focusBG!=null)
			this.focusBG =  CrieUtils.scaleImageToWidthHeight(focusBG, imageName.getWidth(), imageName.getHeight()).getBitmap() ;
	}
	
	protected void onFocus(int direction) {
		hasFocus = true;
		invalidate();
	}

	protected void onUnfocus() {
		hasFocus = false;
		invalidate();
	}

	protected void paint(Graphics graphics) {
		super.paint(graphics);

		
		if (imgFocus != null) {
			if (hasFocus) {
				graphics.drawBitmap(0, 0, imgFocus.getWidth(), imgFocus
						.getHeight(), imgFocus, 0, 0);
			}
		}else{
			if (hasFocus) {
				graphics.drawBitmap(0, 0, focusBG.getWidth(), focusBG
						.getHeight(), focusBG, 0, 0);
				super.paint(graphics);
			}
		}
		if(text!=null){
			graphics.setColor(0xffffff);
			Font font = Font.getDefault().derive(Font.PLAIN,
					16);
			graphics.setFont(font);
			graphics.drawText(text,img.getWidth()*7/8-font.getAdvance(text) , 0);
		}
		if(numComment!=null){
			graphics.setColor(0xffffff);
			Font font = Font.getDefault().derive(Font.PLAIN,
					16);
			graphics.setFont(font);
			graphics.drawText(numComment,img.getWidth()*7/8-font.getAdvance(numComment) , 0);
		}
		
	}

	protected boolean navigationClick(int i, int i1) {
		if (eventListener == null)
			return false;
		eventListener.imageButtonClicked(this);
		return true;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public ImageButtonListener getEventListener() {
		return eventListener;
	}
	public void setEventListener(ImageButtonListener eventListener) {
		this.eventListener = eventListener;
		this.setFocusListener(eventListener);
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	
	//use for Comment icon ONLY!!!
	public void setNumComment(String num) {
		this.numComment = num;
	}
	public String getNumComment() {
		return numComment;
	}
	
	public void setNonFocusImage(Bitmap image){
		img = image;
		setBitmap(image);
	}
	protected void drawFocus(Graphics graphics, boolean on) {
		//super.drawFocus(graphics, on);
//		graphics.setColor(0x0000cd);
//		graphics.drawRect(0, 0, this.getWidth(), this.getHeight());
	}

}
