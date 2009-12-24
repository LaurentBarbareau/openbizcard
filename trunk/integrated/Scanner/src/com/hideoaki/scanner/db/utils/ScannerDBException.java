package com.hideoaki.scanner.db.utils;

public class ScannerDBException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public ScannerDBException(){
		super("Scanner DB Exception");
	}
	public ScannerDBException(Exception e){
		super(e);
	}
}
