package com.tss.one.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

public class LogTool extends OutputStream {
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private String name;

	public LogTool(String name) {
		this.name = name;
	}

	@Override
	public void write(int b) throws IOException {
		if (b == (int) '\n') {
			String s = new String(this.bos.toByteArray());
			Log.v(this.name, s);
			this.bos = new ByteArrayOutputStream();
		} else {
			this.bos.write(b);
		}
	}

	public static String getExceptionStackTraceAsString(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}