package com.tssoftgroup.tmobile.component.engine;

import java.io.DataOutputStream;
import java.io.OutputStream;

import com.tssoftgroup.tmobile.utils.Base64;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.OakBase64;


/**
 * <code>MultiPartFormOutputStream</code> is used to write
 * "multipart/form-data" to a <code>java.net.URLConnection</code> for POSTing.
 * This is primarily for file uploading to HTTP servers.
 * 
 * @since JDK1.3
 */
public class Base64OutputStream {
	private DataOutputStream out = null;

	/**
	 * Creates a new <code>MultiPartFormOutputStream</code> object using the
	 * specified output stream and boundary. The boundary is required to be
	 * created before using this method, as described in the description for the
	 * <code>getContentType(String)</code> method. The boundary is only
	 * checked for <code>null</code> or empty string, but it is recommended to
	 * be at least 6 characters. (Or use the static createBoundary() method to
	 * create one.)
	 * 
	 * @param os
	 *            the output stream
	 * @param boundary
	 *            the boundary
	 * @see #createBoundary()
	 * @see #getContentType(String)
	 */
	public Base64OutputStream(OutputStream os) {
		if (os == null) {
			throw new IllegalArgumentException("Output stream is required.");
		}
		this.out = new DataOutputStream(os);
	}

	/**
	 * Writes an boolean field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, boolean value)
			throws java.io.IOException {
		writeField(name, new Boolean(value).toString());
	}

	/**
	 * Writes an double field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, double value)
			throws java.io.IOException {
		writeField(name, Double.toString(value));
	}

	/**
	 * Writes an float field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, float value) throws java.io.IOException {
		writeField(name, Float.toString(value));
	}

	/**
	 * Writes an long field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, long value) throws java.io.IOException {
		writeField(name, Long.toString(value));
	}

	/**
	 * Writes an int field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, int value) throws java.io.IOException {
		writeField(name, Integer.toString(value));
	}

	/**
	 * Writes an char field value.
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, char value) throws java.io.IOException {
		writeField(name, new Character(value).toString());
	}

	/**
	 * Writes an string field value. If the value is null, an empty string is
	 * sent ("").
	 * 
	 * @param name
	 *            the field name (required)
	 * @param value
	 *            the field value
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeField(String name, String value)
			throws java.io.IOException {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null or empty.");
		}
		if (value == null) {
			value = "";
		}
		/*
		 * --boundary\r\n Content-Disposition: form-data; name="<fieldName>"\r\n
		 * \r\n <value>\r\n
		 */
		// write boundary
		writeString(name);
		writeString("=");
		writeString(value);
		out.flush();
	}

	/**
	 * Writes the given bytes. The bytes are assumed to be the contents of a
	 * file, and will be sent as such. If the data is null, a
	 * <code>java.lang.IllegalArgumentException</code> will be thrown.
	 * 
	 * @param name
	 *            the field name
	 * @param mimeType
	 *            the file content type (optional, recommended)
	 * @param fileName
	 *            the file name (required)
	 * @param data
	 *            the file data
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void writeFile(String name,
			byte[] data) throws java.io.IOException {
		if (data == null) {
			throw new IllegalArgumentException("Data cannot be null.");
		}
		writeString(name);
		writeString("=");
		writeString(OakBase64.encode(data));
		out.flush();
	}

	public void writeString(String s) throws java.io.IOException {
		byte[] b = s.getBytes();
		out.write(b, 0, b.length);
	}


	/**
	 * Flushes the stream. Actually, this method does nothing, as the only write
	 * methods are highly specialized and automatically flush.
	 * 
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void flush() throws java.io.IOException {
		// out.flush();
	}

	/**
	 * Closes the stream. <br />
	 * <br />
	 * <b>NOTE:</b> This method <b>MUST</b> be called to finalize the
	 * multipart stream.
	 * 
	 * @throws java.io.IOException
	 *             on input/output errors
	 */
	public void close() throws java.io.IOException {
		// write final boundary
		out.flush();
		out.close();
	}

	/**
	 * Gets the multipart boundary string being used by this stream.
	 * 
	 * @return the boundary
	 */
}
