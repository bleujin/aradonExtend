package net.ion.im.bbs.board;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.ion.radon.param.ConfigParser;

import org.apache.commons.io.IOUtils;

public class BoardListLoader {
	
	private String filePath;
	private BoardGroup boardGroup;
	
	public BoardListLoader(String filePath, String className) {
		this.filePath = filePath;
		init();
	}
	
	private void init() {
		try {
			String str = IOUtils.toString(new FileInputStream(new File(filePath)), "UTF-8");
			this.boardGroup = ConfigParser.parse(str, BoardGroup.class) ;
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public BoardGroup getBoardGroup() {
		return boardGroup;
	}
}
