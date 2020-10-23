package com.vc.initializer;


import java.io.File;

import com.vc.compression.GzipHelper;

public class CompressFile {
	
	private GzipHelper gzipHelper;
	
	
	
	public 
	CompressFile(GzipHelper gzipHelper) {
		this.gzipHelper=gzipHelper;
	}
	
	public 
	void compress(File individualFile, File objectFilePath, String sha1)
	{
		gzipHelper.makeZip(individualFile, objectFilePath, sha1);
	}
}
