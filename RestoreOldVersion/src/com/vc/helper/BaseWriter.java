package com.vc.helper;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class BaseWriter extends FilterWriter {

	public BaseWriter(Writer out) {
		super(out);
		// TODO Auto-generated constructor stub
	}

	public 
		void write(String str) throws IOException {
		try {
			out.write(str);
			
		}catch(Exception e)
		{
			//where to print?
			e.printStackTrace();
		}
    }
	public
	  void flush() {
	    try {
	      out.flush();
	    } catch(Exception e) {
	    	//where to print?
			e.printStackTrace();
	    }	
	  }
}
