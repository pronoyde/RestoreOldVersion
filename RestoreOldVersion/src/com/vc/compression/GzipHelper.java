package com.vc.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipHelper {

	
	private FileInputStream fis;
	private FileOutputStream fos;
	private GZIPOutputStream gzipOutputStream;
	
	
	public
	synchronized void makeZip(File file, File objectsDirectory, String sha1) {
		try {

			
			fis = new FileInputStream(file);
			//go to Objects directory & use 1st two characters of sha1 for directory name and remaining key for the file name
			
			File outputFileDirectory = new File(objectsDirectory.getCanonicalPath()+"/"+sha1.substring(0,2));
			if(!outputFileDirectory.exists())
				//not using mkdirs() as we will be creating only one sub level of sub directory and no further nested sub-directories
				outputFileDirectory.mkdir();
			
			File outputFile = new File(objectsDirectory.getCanonicalPath()+"/"+sha1.substring(0,2)+"/"+sha1.substring(2));
			
			//if the Binary compressed file exist no action required
				if(!outputFile.exists())
				{
					outputFile.createNewFile();
				
				fos = new FileOutputStream(outputFile);
				//Encrypt the file
				gzipOutputStream = new GZIPOutputStream(fos);
				
				int i = 0;
				
					while((i=fis.read())!=-1)
						gzipOutputStream.write(i);
				
				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		finally {
			closeConnections();
		}
	}
			private 
			void closeConnections() {

				try {
					
					fis.close();
					//If the file exist that means streams not created. Do not try to close!!!
					if(gzipOutputStream!=null)
						gzipOutputStream.close();
					if(fos!=null)
						fos.close();
				} catch (IOException e) {
					//2nd try must be printed
					e.printStackTrace();
				}
			
			}
}

