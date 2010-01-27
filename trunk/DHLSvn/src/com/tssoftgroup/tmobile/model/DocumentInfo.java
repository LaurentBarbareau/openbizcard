package com.tssoftgroup.tmobile.model;

public class DocumentInfo implements TitleDescriptionObj {
	private String productInfo;
	private String description;
	private String fileName;
	private String hTTPfilePath;

	public String getHTTPfilePath() {
		return hTTPfilePath;
	}

	public void setHTTPfilePath(String pfilePath) {
		hTTPfilePath = pfilePath;
	}

	private String id;
	private String localFilename = "";
	private String fileSize = "";

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getLocalFilename() {
		return localFilename;
	}

	public void setLocalFilename(String localFilename) {
		this.localFilename = localFilename;
	}

	public String getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String filePath) {
		if (filePath != null && filePath.length() > 0) {
			if (filePath.charAt(0) == '/') {
				filePath = filePath.substring(1);
			}
		}
		this.fileName = filePath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean containKey(String key) {
		key = key.toLowerCase();
		if (productInfo.toLowerCase().indexOf(key) >= 0
				|| description.toLowerCase().indexOf(key) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public String getTitle() {
		return productInfo;
	}
}
