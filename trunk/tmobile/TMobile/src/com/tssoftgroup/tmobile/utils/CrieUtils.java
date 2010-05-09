package com.tssoftgroup.tmobile.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventInjector;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class CrieUtils {
	public static final byte[] UTF8Encode(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int strlen = str.length();

			for (int i = 0; i < strlen; i++) {
				char t = str.charAt(i);
				int tInt = t;
				int c = 0;
				c |= (t & 0xffff);
				// ascii to unicode
				if ((0xA1 <= tInt) && (tInt <= 0xFB)) {
					// if yes, it will be converted to Thai language in Unicode
					// scope.
					// bos.write((c + 0xD60));
					c = c + 0xD60;
				}
				if (c >= 0 && c < 0x80) {
					bos.write((byte) (c & 0xff));
				} else if (c > 0x7f && c < 0x800) {
					bos.write((byte) (((c >>> 6) & 0x1f) | 0xc0));
					bos.write((byte) (((c >>> 0) & 0x3f) | 0x80));
				} else if (c > 0x7ff && c < 0x10000) {
					bos.write((byte) (((c >>> 12) & 0x0f) | 0xe0)); // <--
					// correction
					// (mb)
					bos.write((byte) (((c >>> 6) & 0x3f) | 0x80));
					bos.write((byte) (((c >>> 0) & 0x3f) | 0x80));
				} else if (c > 0x00ffff && c < 0xfffff) {
					bos.write((byte) (((c >>> 18) & 0x07) | 0xf0));
					bos.write((byte) (((c >>> 12) & 0x3f) | 0x80));
					bos.write((byte) (((c >>> 6) & 0x3f) | 0x80));
					bos.write((byte) (((c >>> 0) & 0x3f) | 0x80));
				}
			}
			bos.flush();
		} catch (Exception e) {
		}
		return bos.toByteArray();
	}

	public static final void showBlockDialog(String text) {
		Object[] choice = { "OK" };
		int[] value = { 1 };

		Dialog finish = new Dialog(text, choice, value, 1, Bitmap
				.getPredefinedBitmap(Bitmap.INFORMATION));
		finish.doModal();
	}

	public static final String md5All(String secretCode,
			String secretCodeAppend, String miniKey, String apiKey) {
		StringBuffer buff = new StringBuffer();
		buff.append(secretCode).append(secretCodeAppend).append(miniKey)
				.append(apiKey);
		String before = buff.toString();
		String after = MD5.md5(before);
		return after;
	}

	public static final String md5All(String secretCode,
			String secretCodeAppend, String userID, String total, String apiKey) {
		StringBuffer buff = new StringBuffer();
		buff.append(secretCode).append(secretCodeAppend).append(userID).append(
				total).append(apiKey);
		String before = buff.toString();
		String after = MD5.md5(before);
		return after;
	}

	public static void browserURL(String url) {

		// Get the default sessionBrowserSession
		BrowserSession browserSession = Browser.getDefaultSession();
		// now launch the URL
		browserSession.showBrowser();
		browserSession.displayPage(url);

	}

	public static EncodedImage scaleImageToWidth(EncodedImage encoded,
			int newWidth) {
		return scaleToFactor(encoded, encoded.getWidth(), newWidth);
	}

	public static EncodedImage scaleImageToHeight(EncodedImage encoded,
			int newHeight) {
		return scaleToFactor(encoded, encoded.getHeight(), newHeight);
	}

	public static EncodedImage scaleImageToWidthHeight(EncodedImage encoded,
			int newWidth, int newHeight) {
		return scaleToFactor(encoded, encoded.getWidth(), encoded.getHeight(),
				newWidth, newHeight);
	}

	public static EncodedImage scaleToFactor(EncodedImage encoded, int curSize,
			int newSize) {

		int numerator = Fixed32.toFP(curSize);
		int denominator = Fixed32.toFP(newSize);
		int scale = Fixed32.div(numerator, denominator);

		return encoded.scaleImage32(scale, scale);
	}

	public static EncodedImage scaleToFactor(EncodedImage encoded,
			int curWidth, int curHeight, int newWidthSize, int newHeightSize) {

		int numerator = Fixed32.toFP(curWidth);
		int denominator = Fixed32.toFP(newWidthSize);
		int scaleW = Fixed32.div(numerator, denominator);

		int numeratorH = Fixed32.toFP(curHeight);
		int denominatorH = Fixed32.toFP(newHeightSize);
		int scaleH = Fixed32.div(numeratorH, denominatorH);
		return encoded.scaleImage32(scaleW, scaleH);
	}

	public static long getNumberPacketSent() {
		return RadioInfo.getNumberOfPacketsSent();
	}

	public static byte[] readByteFileSystem(String imgName) {
		try {
			FileConnection fileConn = (FileConnection) Connector.open(imgName,
					Connector.READ);
			// load the image data in memory
			// Read data in CHUNK_SIZE chunks
			final int size = (int) fileConn.fileSize();

			if (size > 0) {
				byte[] fileData = new byte[size];

				fileConn.openInputStream().read(fileData, 0, size);
				return fileData;
			}
			// fis.close();
			fileConn.close();
			// currentImage = Image.createImage(imageData, 0, length);
			// repaint();
		} catch (SecurityException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			// midlet.showError(e);
		}
		return null;

	}

	// connpat = file:///SDCard/BlackBerry/documents
	public static String getDocumentFolderConnString() {
		return "file:///SDCard/BlackBerry/documents/";
	}
	public static String getVideoFolderConnString() {
		return "file:///SDCard/BlackBerry/videos/";
	}
	public static String getDocumentFolderStringForUser() {
		return "Media Card/BlackBerry/documents/";
	}

	public static void writeByteFileSystem(String connPath, byte[] b)
			throws IOException {

		FileConnection fc = (FileConnection) Connector.open(connPath,
				Connector.READ_WRITE);
		if (!fc.exists())
			fc.create();
		OutputStream os = fc.openOutputStream();
		os.write(b);
		os.close();
		fc.close();
	}

	public static String escapeString(String input) {
		String[] special = { "&#39;", "&quot;", "&gt;", "&amp;", "&lt;" };
		String[] convert = { "'", "\"", ">", "&", "<" };
		for (int i = 0; i < special.length; i++) {
			if (input.indexOf(special[i]) >= 0) {
				input = StringUtil.replace(input, special[i], convert[i]);
			}
		}
		return input;
	}

	public static int getFileSize(String imgName) {
		try {
			FileConnection fileConn = (FileConnection) Connector.open(imgName,
					Connector.READ);
			// load the image data in memory
			// Read data in CHUNK_SIZE chunks
			final int size = (int) fileConn.fileSize();
			fileConn.close();
			return size;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void showAlert(String s) {
		final String theS = s;
		UiApplication.getUiApplication().invokeLater(new Runnable() {

			public void run() {
				MainScreen mainScreen = new MainScreen();
				mainScreen.add(new LabelField(theS));
				UiApplication.getUiApplication().pushScreen(mainScreen);
			}
		});
	}

	public static Bitmap createScaleBitmap(String imgPath, int width, int height) {
		EncodedImage encode = EncodedImage.getEncodedImageResource(imgPath);
		EncodedImage newImg = scaleImageToWidthHeight(encode, width, height);
		return newImg.getBitmap();
	}

	public static Bitmap createScaleBitmap(String imgPath, int width) {
		EncodedImage encode = EncodedImage.getEncodedImageResource(imgPath);
		EncodedImage newImg = scaleImageToWidth(encode, width);
		return newImg.getBitmap();
	}

	public static String replaceString(String s, String f, String r) {
		if (s == null) {
			return s;
		}
		if (f == null) {
			return s;
		}
		if (r == null) {
			r = "";
		}
		int index01 = s.indexOf(f);
		while (index01 != -1) {
			s = s.substring(0, index01) + r + s.substring(index01 + f.length());
			index01 += r.length();
			index01 = s.indexOf(f, index01);
		}
		return s;
	}

	public static final String md5Encode(String str) {
		String after = MD5.md5(str);
		return after;
	}

	public static final void writeThumbFile(String path, byte[] data)
			throws Exception {
		FileConnection fc = null;
		FileConnection dc = null;

		OutputStream os = null;

		String thumbname = path + ".tmb";
		System.out.println("thumbname " + thumbname);
		// write file
		try {

			while (true) {
				try {
					fc = (FileConnection) Connector.open(thumbname,
							Connector.READ_WRITE);
					if (fc.exists()) {
						// fc.create();
						fc.delete();
						fc.create();
					} else {
						fc.create();
					}
					break;
				} catch (Exception e) {
					throw e;
				}
			}

			os = fc.openOutputStream();

			os.write(data);

			os.flush();

		} catch (Exception e) {
			throw e;

		} finally {
			try {
				if (os != null)
					os.close();
				if (fc != null)
					fc.close();
				if (dc != null)
					dc.close();

				os = null;
				fc = null;
				dc = null;
				System.gc();
			} catch (Exception ex) {
				throw ex;
			}
		}
	}

	public static int countString(String string, int width, Font font) {
		if (font.getAdvance(string) > width) {
			String realText = string;
			int all = 0;
			int lenght = 0;
			for (int i = 0; i < realText.length(); i++) {
				char temp = realText.charAt(i);
				all += font.getAdvance(temp);
				if (all > width) {
					lenght = i;
					break;
				}
			}
			return lenght;

		} else {
			return string.length();
		}
	}

	public static String cutStringIfTooLong(String string, int width, Font font) {
		if (font.getAdvance(string) > width) {
			int numChar = CrieUtils.countString(string, width, font);
			return string.substring(0, numChar - 3) + "...";
		} else {
			return string;
		}
	}

	public static void grayScale() {
		Bitmap b = Bitmap.getBitmapResource("myImage.png");

		int[] argb = new int[b.getWidth() * b.getHeight()];

		b.getARGB(argb, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());

		for (int i = argb.length - 1; i >= 0; --i) {
			int alpha = argb[i] >> 24;

			int red = (argb[i] >> 16) & 0xFF;

			int green = (argb[i] >> 8) & 0xFF;

			int blue = argb[i] & 0xFF;

			int grey = (red + green + blue) / 3;

			int composite = (alpha << 24) | (grey << 16) | (grey << 8) | grey;

			argb[i] = composite;

		}

		b.setARGB(argb, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
	}

	public static void setMarginAllItemsInManager(Manager manager) {
		Vector allField = new Vector();
		// CAlculate Margin Cat
		int numField = manager.getFieldCount();
		int sumWidth = 0;
		for (int i = 0; i < numField; i++) {
			Field f = manager.getField(i);
			sumWidth = sumWidth + f.getPreferredWidth();
			System.out.println("setMarginAllItemsInManager " + i + " "
					+ f.getPreferredWidth());
		}
		int marginCat = (Display.getWidth() - sumWidth) / (numField);
		for (int i = 0; i < numField; i++) {
			Field f = manager.getField(i);
			if (i == 0) {
				f.setMargin(0, marginCat / 2, 0, marginCat / 4);
			} else if (i == numField - 1) {
				f.setMargin(0, 0, 0, marginCat / 2); // up right down left
			} else {
				f.setMargin(0, marginCat / 2, 0, marginCat / 2); // up right
				// down left
			}
			allField.addElement(f);
		}
		manager.deleteAll();
		for (int i = 0; i < allField.size(); i++) {
			Field f = (Field) allField.elementAt(i);
			manager.add(f);
		}
		// addAll();
		// setMargin();
		// titleMenuManager.deleteAll();
		// addAll();
	}

	public static void injectBack() {
		EventInjector.KeyEvent inject = new EventInjector.KeyEvent(
				EventInjector.KeyEvent.KEY_DOWN, Characters.ESCAPE, 0);
		EventInjector.invokeEvent(inject);
	}

	// public static void injectAlt(){
	// EventInjector.KeyEvent inject = new EventInjector.KeyEvent(
	// EventInjector.KeyEvent.KEY_DOWN,
	// (char)Keypad.KEY_ALT, KeypadListener.STATUS_NOT_FROM_KEYPAD);
	// EventInjector.invokeEvent(inject);
	// }
	public static void injectMenu() {
		EventInjector.invokeEvent(new EventInjector.KeyCodeEvent(
				EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_MENU,
				KeypadListener.STATUS_NOT_FROM_KEYPAD));

	}

	public static void injectDown(int menuOrder) {
		EventInjector.invokeEvent(new EventInjector.TrackwheelEvent(
				EventInjector.TrackwheelEvent.THUMB_ROLL_DOWN, menuOrder,
				KeypadListener.STATUS_NOT_FROM_KEYPAD));

	}

	public static void injectPress() {
		EventInjector.invokeEvent(new EventInjector.TrackwheelEvent(
				EventInjector.TrackwheelEvent.THUMB_CLICK, 1,
				KeypadListener.STATUS_TRACKWHEEL));
	}

	// public static void injectPressNoMenu(){
	// EventInjector.invokeEvent(new
	// EventInjector.TrackwheelEvent(EventInjector.KeyEvent.KEY_DOWN,
	// (char)Keypad., KeypadListener.STATUS_NOT_FROM_KEYPAD));
	// }
	// public static void injectPressNoMenu(){
	// EventInjector.invokeEvent(new
	// EventInjector.TrackwheelEvent(EventInjector.TrackwheelEvent.THUMB_CLICK,
	// 1, KeypadListener.));
	// }
	public static void removeCurrent() {
		UiApplication.getUiApplication().popScreen(
				UiApplication.getUiApplication().getActiveScreen());
	}

	public static String cutString(Font font, String str, int width) {
		StringBuffer buff = new StringBuffer();
		if (font.getAdvance(str) > width) {
			for (int i = 0; i < str.length(); i++) {
				buff.append(str.charAt(i));
				if (font.getAdvance(buff, 0, buff.length()) > width) {
					buff.deleteCharAt(buff.length() - 1);
					return buff.toString();
				}

			}
		}
		return str;
	}

	public static String getDocumentFolderConnection() {
		String r = System.getProperty("fileconn.dir.memorycard.photos");
		return r;
	}
	public static String encodeUrl(String url)
	{
        if (url!=null) 
        {
            StringBuffer tmp = new StringBuffer();
            int i=0;
            try 
            {
                while (true) 
                {
                    int b = (int)url.charAt(i++);
                    if ((b>=0x30 && b<=0x39) || (b>=0x41 && b<=0x5A) || (b>=0x61 && b<=0x7A) ||(b == 0x2F) || (b == 0x3A) || (b == 0x2D )|| (b == 0x2E)|| (b == 0x2C)|| (b == 0x3F)|| (b == 0x3D) ||(b == 0x26) || (b == 0x5F) || (b == 0x2A)) 
                    {
                        tmp.append((char)b);
                    }
                    else {
                        tmp.append("%");
                        if (b <= 0xf) tmp.append("0");
                        tmp.append(Integer.toHexString(b));
                    }
                }
            }
            catch (Exception e) {}
            return tmp.toString();
        }
        return null;
	}    
}
