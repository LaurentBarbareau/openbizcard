package com.tssoftgroup.tmobile.component;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

import com.tssoftgroup.tmobile.model.DocumentInfo;
import com.tssoftgroup.tmobile.model.TitleDescriptionObj;
import com.tssoftgroup.tmobile.model.TrainingInfo;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.Img;
import com.tssoftgroup.tmobile.utils.MyColor;
import com.tssoftgroup.tmobile.utils.Scale;

public class TrainingListField extends ListField implements ListFieldCallback {

	private Vector _elements = new Vector();
	int rowHeight = Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD
			+ (Display.getWidth() > 350 ? 5 : 13);
	Font titleFont = Font.getDefault().derive(Font.BOLD,
			(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD / 4) + 2);
	Font descFont = Font.getDefault().derive(Font.PLAIN,
			(Scale.WIDTH_HEIGHT_THUMBNAIL_VIDEO_LISTFIELD / 4));

	public TrainingListField() {
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
		if (_elements.elementAt(index) instanceof TitleDescriptionObj) {
			TitleDescriptionObj picInfo = (TitleDescriptionObj) _elements
					.elementAt(index);
			// Draw the
			Bitmap pic = null;
			if (picInfo instanceof TrainingInfo) {
				TrainingInfo myinfo = (TrainingInfo) picInfo;

				if (myinfo.getThumbnail() != null) {
					pic = myinfo.getThumbnail();
				} else {
					pic = img.getLoadList();

				}
			}
			if (picInfo instanceof DocumentInfo) {
				DocumentInfo docInfo = (DocumentInfo) picInfo;
				pic = chooseBitmap(docInfo.getFileName());
			}
			// Paint BG Sothat not see blue color
			g.setColor(Const.BG_COLOR);
			g.fillRect(0, y, Display.getWidth(), getRowHeight());
			XYRect rect = new XYRect();
			getFocusRect(rect);
			if (isMyFocus) {
				g.setColor(Const.LIST_BG_COLOR);
				g.fillRect(rect.x + Scale.INDENT_LEFT_RIGHT_TOPIC, rect.y,
						rect.width - 2 * Scale.INDENT_LEFT_RIGHT_TOPIC,
						rect.height);
				invalidate();
			}
			int hPic = pic == null ? 0 : pic.getHeight();
			int wPic = pic == null ? 0 : pic.getWidth();
			int yPic = pic == null ? 5 + y : y + (getRowHeight() - hPic) / 2;
			int indent = pic == null ? Scale.INDENT_LEFT_RIGHT_TOPIC
					: (getRowHeight() - hPic) / 2;

			if (pic != null) {
				g.drawBitmap(Scale.INDENT_LEFT_RIGHT_TOPIC + indent, yPic,
						wPic, hPic, pic, 0, 0);
			}
			if (Math.abs(rect.y - y) < getRowHeight() / 2 && isMyFocus) {
				g.setColor(Const.GRAY_COLOR);
			} else {
				g.setColor(Const.BLUE_COLOR);
			}
			// Draw Text
			int remain = rect.width - 2 * Scale.INDENT_LEFT_RIGHT_TOPIC
					- indent - wPic - indent;
			String title = CrieUtils.cutString(titleFont, picInfo.getTitle(),
					remain);
			String desc1 = CrieUtils.cutString(descFont, picInfo
					.getDescription(), remain);
			g.setFont(titleFont);
			if (!(Math.abs(rect.y - y) < getRowHeight() / 2 && isMyFocus)) {
			g.setColor(MyColor.LIST_TITLE_FONT_UNFOCUS);
			}else{
				g.setColor(MyColor.LIST_TITLE_FONT_FOCUS);
			}
			int indentX = ((Display.getWidth() > 350) ? 5 : (pic == null ? -2
					: 2));
			g.drawText(title, Scale.INDENT_LEFT_RIGHT_TOPIC + wPic + indentX
					+ indent, yPic);
			g.setFont(descFont);
			g.setColor(MyColor.LIST_DESC_FONT);
			g
					.drawText(
							desc1,
							Scale.INDENT_LEFT_RIGHT_TOPIC + wPic + indentX
									+ indent,
							yPic
									+ titleFont.getHeight()
									+ (Display.getWidth() > 350 ? (picInfo instanceof DocumentInfo ? 2
											: 5)
											: 2));
			if (picInfo.getDescription().length() > desc1.length()
					&& !(picInfo instanceof DocumentInfo)) {
				int endIndex = picInfo.getDescription().length()
						- desc1.length() > desc1.length() ? desc1.length()
						+ desc1.length() - 1 : picInfo.getDescription()
						.length() - 1;
				System.out.println("picInfo.getDescription().length() "
						+ picInfo.getDescription().length());
				System.out.println("desc1.length() " + desc1.length());
				System.out.println("endindex " + endIndex);
				try {
					String desc2 = CrieUtils.cutString(descFont, picInfo
							.getDescription().substring(desc1.length(),
									endIndex), remain);
					g.drawText(desc2, Scale.INDENT_LEFT_RIGHT_TOPIC + wPic
							+ indentX + indent, yPic + titleFont.getHeight()
							+ (Display.getWidth() > 350 ? 5 : 3)
							+ descFont.getHeight());
				} catch (Exception e) {

				}
			}
			if (picInfo instanceof DocumentInfo) {
				DocumentInfo temp = (DocumentInfo) picInfo;
				if (!(Math.abs(rect.y - y) < getRowHeight() / 2 && isMyFocus)) {
					g.setColor(MyColor.LIST_TITLE_FONT_UNFOCUS);
					}else{
						g.setColor(MyColor.LIST_TITLE_FONT_FOCUS);
					}
				Font docfont = g.getFont().derive(
						Font.PLAIN,
						g.getFont().getHeight()
								- (Display.getWidth() > 350 ? 7 : Display.getWidth() > 300?5:0));
				g.setFont(docfont);
				String filename = CrieUtils.cutString(docfont, "File : "
						+ temp.getFileName(), remain);

				g.drawText(filename, Scale.INDENT_LEFT_RIGHT_TOPIC + wPic
						+ indentX + indent, yPic + titleFont.getHeight()
						+ (Display.getWidth() > 350 ? 8 : 8)
						+ descFont.getHeight() - 5);
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

	public Bitmap chooseBitmap(String filename) {
		Img img = Img.getInstance();
		if (filename.endsWith("zip")) {
			return img.getZip();
		} else if (filename.endsWith("doc")) {
			return img.getDoc();
		} else if (filename.endsWith("ppt")) {
			return img.getPpt();
		} else if (filename.endsWith("pdf")) {
			return img.getPdf();
		} else if (filename.endsWith("xls")) {
			return img.getXls();
		} else
			return img.getDocDefault();
	}
}
