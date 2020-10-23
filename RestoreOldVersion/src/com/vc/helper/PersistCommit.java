package com.vc.helper;

import java.io.IOException;

public class PersistCommit {

	
	private CommitWriter commitFileWriter; 
	private String format;
	
	public 
	PersistCommit( CommitWriter commitFileWriter, String format) {
	
		this.commitFileWriter=commitFileWriter;
		this.format=format;
	}
	
	public
	void save(String sha1keyOfCommitFile) throws IOException {
		
		commitFileWriter.append(format+":"+sha1keyOfCommitFile+"\n");
	}
}
