package net.ion.im.bbs.board;

import org.apache.commons.lang.StringUtils;

public class UploadFile {

	private String fullPath;
	
	public UploadFile(String fullPath) {
		this.fullPath = fullPath;
	}
	
	public String getFullPath() {
		return fullPath;
	}
	
	public String getFileName() {
		return StringUtils.substringAfterLast(fullPath, "/");
	}
}
