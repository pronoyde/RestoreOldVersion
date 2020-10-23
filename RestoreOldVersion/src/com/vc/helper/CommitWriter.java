package com.vc.helper;

import java.io.FileWriter;
import java.io.IOException;

public class CommitWriter {
	
	private BaseWriter baseWriter;
	
	public CommitWriter(FileWriter out,BaseWriter baseWriter) {
		
		this.baseWriter=baseWriter;
	}
	
	
	public 
	void append(String str) {
		try {
			baseWriter.append(str);
			baseWriter.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
}
