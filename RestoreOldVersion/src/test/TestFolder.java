package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class TestFolder {
//Read current state of the repository
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("/Users/pronoyde/Documents/SampleLoggin/");
		
		try {
			recursiveAllFiles(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void recursiveAllFiles(File file) throws IOException {
		if(file.isDirectory())
		{
			StringBuffer filePath = new StringBuffer(file.getCanonicalPath());
			
			ArrayList<File> dirList= new ArrayList<>();
			System.out.println("dir:"+filePath);
			for(File s : file.listFiles()) {
				
				BasicFileAttributes attr = Files.readAttributes(s.toPath(), BasicFileAttributes.class);
				
				String typeOfFile = (s.isDirectory()==true)?"tree":"blob";
				System.out.println(typeOfFile+ " "+ attr.lastModifiedTime()+" "+s.getName());
				//System.out.println( attr.lastModifiedTime()+" "+attr.size()+" "+s.getName());
				if(s.isDirectory())
					//recursiveAllFiles( tempFile);//DFS approach
					//BFS Approach...Print all files on line 29 and the populate the file list in the ArrayList to be checked later.isDirectory()?
					dirList.add(s);	
			}
			for(File directories : dirList)
				recursiveAllFiles(directories);
		}
	}

}
