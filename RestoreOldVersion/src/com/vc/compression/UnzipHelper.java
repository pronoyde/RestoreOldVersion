package com.vc.compression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UnzipHelper {

	private File file;
	private FileInputStream fis;
	private FileOutputStream fos;
	private GZIPOutputStream gzipOutputStream;
	private BufferedReader bufferedReader;
	
	public
	UnzipHelper(File file) throws FileNotFoundException {
		
		this.file  = file;
		bufferedReader=new BufferedReader(new FileReader(file));
	}
	
	public 
	void readCommitAndRestore() throws IOException {
		String s=bufferedReader.readLine();//Read the 1st line which is always the parent directory
		File dirName =new File(s.substring(4));
		dirName.mkdir();
		String parentDirName= dirName.getCanonicalPath();
		while((s=bufferedReader.readLine())!=null) {
			if(s.substring(0, 4).equals("dir:"))
				dirName=new File(s.substring(4));
			
			else if(s.substring(0, 4).equals("tree")) {
				String[] arr = s.split(" ");
				File createSubDir=new File(dirName+"/"+arr[arr.length-1]);
				createSubDir.mkdir();
			}
			else if (s.substring(0, 4).equals("blob")) {
				String[] arr = s.split(" ");
				File createFile=new File(dirName+"/"+arr[arr.length-1].substring(0,arr[arr.length-1].indexOf('>')));
				createFile.createNewFile();
				String sha1key=arr[arr.length-1].substring(arr[arr.length-1].indexOf('>')+1);
				File fromFile=new File(parentDirName+"Back/.ver/objects/"+sha1key.substring(0,2)+"/"+sha1key.substring(2));
				unZip(createFile,fromFile);
				
			}
				
				System.out.println(s);
			
		}
	}
	
	
	public
	synchronized void unZip(File createFile, File fromFile) {
		try {
			
					fis = new FileInputStream(fromFile);
					FileOutputStream fos = new FileOutputStream(createFile);
					GZIPInputStream gzipInputStream = new GZIPInputStream(fis);
					int s = 0;
					while((s=gzipInputStream.read())!=-1)
						fos.write(s);
					
					fis.close();
					gzipInputStream.close();
					fos.close();
				
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
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
}
