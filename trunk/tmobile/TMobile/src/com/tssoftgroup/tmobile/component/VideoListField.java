package com.tssoftgroup.tmobile.component;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;

public class VideoListField extends ListField implements ListFieldCallback {

	private Vector _elements = new Vector();
	int rowHeight = Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD + 5;
	Font titleFont = Font.getDefault().derive(Font.BOLD,
			(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD / 4) + 2);
	Font descFont = Font.getDefault().derive(Font.PLAIN,
			(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD / 4));

	/**
	 * Constructor. Sets itself as the callback.
	 */
	public VideoListField() {
		setCallback(this);
		setRowHeight(rowHeight);
	}

	public void add(Object element) {
		_elements.addElement(element);
		setSize(getSize());
	}

	public void remove(int index) {
		_elements.removeElementAt(index);
		setSize(getSize());
	}

	public void removeObj(Object obj) {
		_elements.removeElement(obj);
		setSize(getSize());
	}

	public void removeAll() {
		_elements.removeAllElements();
		setSize(0);
	}

	public boolean isMyFocus = false;

	protected void onFocus(int direction) {
		isMyFocus = true;
		super.onFocus(direction);
	}

	protected void onUnfocus() {
		isMyFocus = false;
		super.onUnfocus();
	}

	public void drawListRow(ListField listField, Graphics g, int index, int y,
			int width) {
		Img img = Img.getInstance();

		// g.setColor(0xccc );
		// g.fillRect(0, y, getWidth(), getRowHeight());
		g.setColor(0xfff);
		PicInfo picInfo = (PicInfo) _elements.elementAt(index);
		// Draw the
		Bitmap pic = null;

		if (picInfo.getThumbnail() != null) {
			pic = picInfo.getThumbnail();
		} else {
			pic = img.getLoadList();

		}
		// Paint BG Sothat not see blue color
		g.setColor(Const.BG_COLOR);
		g.fillRect(0, y, Display.getWidth(), getRowHeight());
		XYRect rect = new XYRect();
		getFocusRect(rect);
		if (!(Math.abs(rect.y - y) < getRowHeight() / 2 && isMyFocus)) {
			g.setColor(Const.LIST_BG_COLOR_UNFOCUS);
		} else {
			g.setColor(Const.LIST_BG_COLOR);
		}
		g.fillRect(Scale.INDENT_LEFT_RIGHT_TOPIC, y, Display.getWidth()- 2 * Scale.INDENT_LEFT_RIGHT_TOPIC, getRowHeight());
		if (isMyFocus) {
			g.setColor(Const.LIST_BG_COLOR);
			g
					.fillRect(rect.x + Scale.INDENT_LEFT_RIGHT_TOPIC, rect.y,
							rect.width - 2 * Scale.INDENT_LEFT_RIGHT_TOPIC,
							rect.height);
			invalidate();
		}

		int yPic = y + (getRowHeight() - pic.getHeight()) / 2;
		int indent = (getRowHeight() - pic.getHeight()) / 2;
		g.drawBitmap(Scale.INDENT_LEFT_RIGHT_TOPIC + indent, yPic, pic
				.getWidth(), pic.getHeight(), pic, 0, 0);
		if (Math.abs(rect.y - y) < getRowHeight() / 2 && isMyFocus) {
			g.setColor(Const.GRAY_COLOR);
		} else {
			g.setColor(Const.BLUE_COLOR);
		}
		// Draw Text
		int remain = rect.width - 2 * Scale.INDENT_LEFT_RIGHT_TOPIC - indent
				- pic.getWidth() - indent;
		String title = CrieUtils.cutString(titleFont, picInfo.getTitle(),
				remain);
		String desc1 = CrieUtils.cutString(descFont, picInfo.getDescription(),
				remain);
		g.setFont(titleFont);
		if (!(Math.abs(rect.y - y) < getRowHeight() / 2 && isMyFocus)) {
			g.setColor(MyColor.LIST_TITLE_FONT_UNFOCUS);
		} else {
			g.setColor(MyColor.LIST_TITLE_FONT_FOCUS);
		}
		g.drawText(title, Scale.INDENT_LEFT_RIGHT_TOPIC + pic.getWidth() + 5
				+ indent, yPic);
		if (!(Math.abs(rect.y - y) < getRowHeight() / 2 && isMyFocus)) {
			g.setColor(MyColor.LIST_DESC_FONT_UNFOCUS);
		} else {
			g.setColor(MyColor.LIST_DESC_FONT_FOCUS);
		}
	
		g.setFont(descFont);
		g.drawText(desc1, Scale.INDENT_LEFT_RIGHT_TOPIC + pic.getWidth() + 5
				+ indent, yPic + titleFont.getHeight()
				+ (Display.getWidth() > 350 ? 10 : 2));
		if (picInfo.getDescription().length() > desc1.length()) {
			int endIndex = picInfo.getDescription().length() - desc1.length() > desc1
					.length() ? desc1.length() + desc1.length() : picInfo
					.getDescription().length();
			System.out.println("picInfo.getDescription().length() "
					+ picInfo.getDescription().length());
			System.out.println("desc1.length() " + desc1.length());
			System.out.println("endindex " + endIndex);
			try {
				String desc2 = CrieUtils.cutString(descFont, picInfo
						.getDescription().substring(desc1.length(), endIndex),
						remain);
				g.drawText(desc2, Scale.INDENT_LEFT_RIGHT_TOPIC
						+ pic.getWidth() + 5 + indent, yPic
						+ titleFont.getHeight()
						+ (Display.getWidth() > 350 ? 10 : 3)
						+ descFont.getHeight());
			} catch (Exception e) {

			}

		}
	}

	public Object get(ListField listField, int index) {
		if (index >= 0 && index < getSize()) {
			return _elements.elementAt(index);
		}
		return null;
	}

	public int getPreferredWidth(ListField listField) {
		return Display.getWidth();
	}

	public int indexOfList(ListField listField, String prefix, int start) {
		return listField.indexOfList(prefix, start);
	}

	public int getSize() {
		return (_elements != null) ? _elements.size() : 0;
	}

	public void setTagElement(int index, Object obj) {
		_elements.setElementAt(obj, index);
	}

}
