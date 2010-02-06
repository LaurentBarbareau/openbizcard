/*
 * StringUtil.java
 *
 * Copyright (C) 2005-2008 Tommi Laukkanen
 * http://www.substanceofcode.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tssoftgroup.tmobile.utils;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.lcdui.Font;

/**
 * 
 * @author Tommi Laukkanen (tlaukkanen at gmail dot com)
 */
public class StringUtil {

	/** Creates a new instance of StringUtil */
	private StringUtil() {
	}

	/**
	 * Split string into multiple strings
	 * 
	 * @param original
	 *            Original string
	 * @param separator
	 *            Separator string in original string
	 * @return Splitted string array
	 */
	public static String[] split(String original, String separator) {
		Vector nodes = new Vector();

		// Parse nodes into vector
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		// Get the last node
		nodes.addElement(original);

		// Create splitted string array
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int loop = 0; loop < nodes.size(); loop++) {
				result[loop] = (String) nodes.elementAt(loop);
			}
		}
		return result;
	}

	/*
	 * Replace all instances of a String in a String. @param s String to alter.
	 * 
	 * @param f String to look for. @param r String to replace it with, or null
	 * to just remove it.
	 */
	public static String replace(String s, String f, String r) {
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

	public static boolean isContain(String container, String target) {
		int index = container.indexOf(target);
		if (index != -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method removes HTML tags from given string.
	 * 
	 * @param text
	 *            Input parameter containing HTML tags (eg. <b>cat</b>)
	 * @return String without HTML tags (eg. cat)
	 */
	public static String removeHtml(String text) {
		try {
			int idx = text.indexOf("<");
			if (idx == -1) {
				text = decodeEntities(text);
				return text;
			}

			String plainText = "";
			String htmlText = text;
			int htmlStartIndex = htmlText.indexOf("<", 0);
			if (htmlStartIndex == -1) {
				return text;
			}
			htmlText = StringUtil.replace(htmlText, "</p>", "\r\n");
			htmlText = StringUtil.replace(htmlText, "<br/>", "\r\n");
			htmlText = StringUtil.replace(htmlText, "<br>", "\r\n");
			while (htmlStartIndex >= 0) {
				plainText += htmlText.substring(0, htmlStartIndex);
				int htmlEndIndex = htmlText.indexOf(">", htmlStartIndex);
				htmlText = htmlText.substring(htmlEndIndex + 1);
				htmlStartIndex = htmlText.indexOf("<", 0);
			}
			plainText = plainText.trim();
			plainText = decodeEntities(plainText);
			return plainText;
		} catch (Exception e) {
			return text;
		}
	}

	public static String decodeEntities(String html) {
		String result = StringUtil.replace(html, "&lt;", "<");
		result = StringUtil.replace(result, "&gt;", ">");
		result = StringUtil.replace(result, "&nbsp;", " ");
		result = StringUtil.replace(result, "&amp;", "&");
		result = StringUtil.replace(result, "&auml;", "?");
		result = StringUtil.replace(result, "&ouml;", "?");
		result = StringUtil.replace(result, "&quot;", "'");
		result = StringUtil.replace(result, "&lquot;", "'");
		result = StringUtil.replace(result, "&rquot;", "'");
		result = StringUtil.replace(result, "&#xd;", "\r");
		return result;
	}

	/**
	 * <p>
	 * Takes an array of messages and a 'Screen-width' and returns the same
	 * messages, but any string in that that is wider than 'width' will be split
	 * up into 2 or more Strings that will fit on the screen
	 * </p>
	 * 
	 * 'Spliting' up a String is done on the basis of Words, so if a single WORD
	 * is longer than 'width' it will be on a Line on it's own, but that line
	 * WILL be WIDER than 'width'
	 * 
	 * @param message
	 * @param width
	 *            the maximum width a string may be before being split up.
	 * @return
	 */
	public static String[] formatMessage(String[] message, int width, Font font) {
		Vector result = new Vector(message.length);
		for (int i = 0; i < message.length; i++) {
			if (font.stringWidth(message[i]) <= width) {
				result.addElement(message[i]);
			} else {
				String[] splitUp = StringUtil.chopStrings(message[i], " ",
						font, width);
				for (int j = 0; j < splitUp.length; j++) {
					result.addElement(splitUp[j]);
				}
			}
		}

		String[] finalResult = new String[result.size()];
		for (int i = 0; i < finalResult.length; i++) {
			finalResult[i] = (String) result.elementAt(i);
		}
		return finalResult;
	}

	/**
	 * Chops up the 'original' string into 1 or more strings which have a width
	 * <= 'width' when rasterized with the specified Font.
	 * 
	 * The exception is if a single WORD is wider than 'width' in which case
	 * that word will be on its own, but it WILL still be longer than 'width'
	 * 
	 * @param original
	 *            The original String which is to be chopped up
	 * @param separator
	 *            The delimiter for seperating words, this will usuall be the
	 *            string " "(i.e. 1 space)
	 * @param font
	 *            The font to use to determine the width of the words/Strings.
	 * @param width
	 *            The maximum width a single string can be. (inclusive)
	 * @return The chopped up Strings, each smaller than 'width'
	 */
	public static String[] chopStrings(String origional, String separator,
			Font font, int width) {
		final String[] words = split(origional, separator);
		final Vector result = new Vector();
		final StringBuffer currentLine = new StringBuffer();
		String currentToken;

		int currentWidth = 0;
		for (int i = 0; i < words.length; i++) {
			currentToken = words[i];

			if (currentWidth == 0
					|| currentWidth + font.stringWidth(" " + currentToken) <= width) {
				if (currentWidth == 0) {
					currentLine.append(currentToken);
					currentWidth += font.stringWidth(currentToken);
				} else {
					currentLine.append(' ').append(currentToken);
					currentWidth += font.stringWidth(" " + currentToken);
				}
			} else {
				result.addElement(currentLine.toString());
				currentLine.delete(0, currentLine.length());
				currentLine.append(currentToken);
				currentWidth = font.stringWidth(currentToken);
			}
		}
		if (currentLine.length() != 0) {
			String[] lines = split(currentLine.toString(), "\n");
			for (int line = 0; line < lines.length; line++) {
				result.addElement(lines[line]);
			}
		}

		String[] finalResult = new String[result.size()];
		for (int i = 0; i < finalResult.length; i++) {
			finalResult[i] = (String) result.elementAt(i);
		}

		return finalResult;
	}

	/** URL encode given string */
//	public static String urlEncode(String s) {
//		if (s != null) {
//			try {
//				s = new String(s.getBytes("UTF-8"), "ISO-8859-1");
//			} catch (UnsupportedEncodingException e) {
//			}
//			StringBuffer tmp = new StringBuffer();
//			try {
//				for (int i = 0; i < s.length(); i++) {
//					int b = s.charAt(i);
//					if ((b >= 0x30 && b <= 0x39) || (b >= 0x41 && b <= 0x5A)
//							|| (b >= 0x61 && b <= 0x7A)) {
//						tmp.append((char) b);
//					} else if (b == 0x20) {
//						tmp.append("+");
//					} else {
//						tmp.append("%");
//						if (b <= 0xf) {
//							tmp.append("0");
//						}
//						tmp.append(Integer.toHexString(b));
//					}
//				}
//			} catch (Exception e) {
//			}
//			return tmp.toString();
//		}
//		return null;
//	}
	 static public String urlEncode(String sUrl)   
	    {  
	         StringBuffer urlOK = new StringBuffer();  
	         for(int i=0; i<sUrl.length(); i++)   
	         {  
	             char ch=sUrl.charAt(i);  
	             switch(ch)  
	             {  
	                 case '<': urlOK.append("%3C"); break;  
	                 case '>': urlOK.append("%3E"); break;  
	                 case '/': urlOK.append("%2F"); break;  
	                 case ' ': urlOK.append("%20"); break;  
	                 case ':': urlOK.append("%3A"); break;  
	                 case '-': urlOK.append("%2D"); break;  
	                 default: urlOK.append(ch); break;  
	             }   
	         }  
	         return urlOK.toString();  
	     }  
	public static String[] splitFirst(String original, String seperator) {
		try {
			String[] ret = new String[2];
			int ind = original.indexOf(seperator);
			ret[0] = original.substring(0, ind);
			ret[1] = original.substring(ind + 1);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private static final String START_QUOTE = "[QUOTE";
	private static final String END_QUOTE = "[/QUOTE]";
	
	public static String removeQuote(String text){
		int startInd = text.indexOf(START_QUOTE);
		if(startInd >=0){
			int endInd = text.indexOf(END_QUOTE);
			StringBuffer buff = new StringBuffer(text);
			buff.delete(startInd, endInd + END_QUOTE.length());
//			String textQuote = 
			return buff.toString();
		}else{
			return text;
		}
	}
}
