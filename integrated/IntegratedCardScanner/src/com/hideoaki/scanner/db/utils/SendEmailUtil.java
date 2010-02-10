package com.hideoaki.scanner.db.utils;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SendEmailUtil {
	public static void sendEmail(String topic, String body,
			BufferedImage imageFront, BufferedImage imageBack, String[] receiver) {
		try {
			Desktop desktop = null;
			if (Desktop.isDesktopSupported()) {
				desktop = Desktop.getDesktop();
				// desktop.browse(new URI("http://www.google.com"));
				URI uri = new URI("");
//				uri.
				desktop.mail();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String[] receivers = { "hideoaki@gmail.com", "hideoaki@hotmail.com" };
		sendEmail("", "", null, null, receivers);
	}
}
