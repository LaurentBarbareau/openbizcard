package com.tssoftgroup.tmobile.utils;

import java.io.*;

/*
 * URLEncoder.java
 *
 * Created on 21 February  2005, 18:06
 */

/**
 * This class encodes strings for sending over HTTP. It replaces the URLEncoder
 * class from the J2SE.
 * 
 * @author yaniv & tsahi <tsahi_75 at yahoo dot com>
 */
public class URLEncoder {
	/**
	 * this function was taken from the Java forums, at
	 * http://forum.java.sun.com
	 * /thread.jspa?forumID=76&messageID=624066&threadID=178903 and modified to
	 * behave like the original one.
	 * 
	 * @param s
	 *            string to encode
	 * @param enc
	 *            normally, this would be "UTF8", but here it is ignored.
	 * @throws IOException
	 *             on IO error
	 * @return an encoded version of the string <CODE>s</CODE>
	 */
	public static String encode(String s) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		StringBuffer ret = new StringBuffer(); // return value
		dOut.writeUTF(s);
		ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
		bIn.read();
		bIn.read();
		int c = bIn.read();
		while (c >= 0) {
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || c == '.' || c == '-'
					|| c == '*' || c == '_')
				ret.append((char) c);
			else if (c == ' ')
				ret.append('+');
			else {
				if (c < 128) {
					appendHex(c, ret);
				} else if (c < 224) {
					appendHex(c, ret);
					appendHex(bIn.read(), ret);
				} else if (c < 240) {
					appendHex(c, ret);
					appendHex(bIn.read(), ret);
					appendHex(bIn.read(), ret);
				}

			}
			c = bIn.read();

		}
		return ret.toString();
	}

	private static void appendHex(int arg0, StringBuffer buff) {
		buff.append('%');
		if (arg0 < 16)
			buff.append('0');
		buff.append(Integer.toHexString(arg0));
	}
	public static  String SimpleURLEncode(String s)
	   {
	      StringBuffer sbuf = new StringBuffer();
	      int ch;
	      for (int i = 0; i < s.length(); i++) 
	      {
	         ch = s.charAt(i);
	         switch(ch)
	         {
	            case ' ': { sbuf.append("+"); break;} 
	            case '!': { sbuf.append("%21"); break;} 
	            case '*': { sbuf.append("%2A"); break;} 
	            case '\'': { sbuf.append("%27"); break;} 
	            case '(': { sbuf.append("%28"); break;} 
	            case ')': { sbuf.append("%29"); break;} 
	            case ';': { sbuf.append("%3B"); break;} 
	            case ':': { sbuf.append("%3A"); break;} 
	            case '@': { sbuf.append("%40"); break;} 
	            case '&': { sbuf.append("%26"); break;} 
	            case '=': { sbuf.append("%3D"); break;} 
	            case '+': { sbuf.append("%2B"); break;} 
	            case '$': { sbuf.append("%24"); break;} 
	            case ',': { sbuf.append("%2C"); break;} 
	            case '/': { sbuf.append("%2F"); break;} 
	            case '?': { sbuf.append("%3F"); break;} 
	            case '%': { sbuf.append("%25"); break;} 
	            case '#': { sbuf.append("%23"); break;} 
	            case '[': { sbuf.append("%5B"); break;} 
	            case ']': { sbuf.append("%5D"); break;} 
	            default: sbuf.append((char)ch);
	         }         
	      }      
	      return sbuf.toString();
	   }

}
