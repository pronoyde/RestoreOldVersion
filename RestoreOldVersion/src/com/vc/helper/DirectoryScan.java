package com.vc.helper;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;


import com.vc.checksum.SHA1;
import com.vc.initializer.CompressFile;

public class DirectoryScan {

	
	private File file;
	private File preCommitFile;
	private CommitWriter commitWriter;
	private File objectFilePath;
	private CompressFile compressFile;
	private PersistCommit persistCommit;
	//Read current state of the repository
	
	public DirectoryScan(File file, File preCommitFile, CommitWriter commitWriter, File objectFilePath, CompressFile compressFile, PersistCommit persistCommit) throws IOException {
		
		this.file=file;
		//temp file where the directory tree will be written and will be used for SHA1 calculation 
		this.preCommitFile=preCommitFile;
		this.commitWriter= commitWriter;
		this.objectFilePath=objectFilePath;
		this.compressFile=compressFile;
		this.persistCommit=persistCommit;
		
	}
	
		public 
		void execute() throws IOException {
			String sha1key=scanDirectory();
			if(movePreCommitFile(sha1key))
				persistCommit.save(sha1key);
			//persistCommit(sha1key);
		}
		private 
		String scanDirectory() throws IOException {
			
			try {
				recursiveAllFiles(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new SHA1().getSHA1(preCommitFile);
		}
		private 
		void recursiveAllFiles(File file) throws IOException {
			if(file.isDirectory())
			{
				StringBuffer filePath = new StringBuffer(file.getCanonicalPath());
				if(!filePath.toString().endsWith(".ver")) {
					ArrayList<File> dirList= new ArrayList<>();
					
					commitWriter.append("dir:"+filePath+"\n");
					for(File individualFile : file.listFiles()) {
						if(!individualFile.toString().endsWith(".ver") ) {
							BasicFileAttributes attr = Files.readAttributes(individualFile.toPath(), BasicFileAttributes.class);
							
							String typeOfFile = (individualFile.isDirectory()==true)?"tree":"blob";
							
							if(individualFile.isDirectory())
							{	//recursiveAllFiles( tempFile);//DFS approach
								//BFS Approach...Print all files on line 29 and the populate the file list in the ArrayList to be checked later.isDirectory()?
								commitWriter.append(typeOfFile+ " "+ attr.lastModifiedTime()+" "+individualFile.getName()+"\n");
								dirList.add(individualFile);	
							}
							else
							{
								String fileName =new SHA1().getSHA1(individualFile);
								System.out.println(individualFile.getAbsolutePath());
								System.out.println(objectFilePath.getAbsolutePath());
								System.out.println(fileName);
								commitWriter.append(typeOfFile+ " "+ attr.lastModifiedTime()+" "+individualFile.getName()+">"+fileName+"\n");
								//ZIP and Save in binary format to save space
								compressFile.compress(individualFile, objectFilePath, fileName);
							}
						}
					}
					for(File directories : dirList)
						recursiveAllFiles(directories);
				}
			}
		}

		private 
		boolean movePreCommitFile(String sha1keyOfCommitFile) {
			File commitFileDirectory;
			String canonicalPath="";
			try {
				canonicalPath=file.getCanonicalPath();
				commitFileDirectory = new File(canonicalPath+"/.ver/objects/"+sha1keyOfCommitFile.substring(0, 2));
				commitFileDirectory.mkdir();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//save the commit file 
			return preCommitFile.renameTo(new File (canonicalPath+"/.ver/objects/"+sha1keyOfCommitFile.substring(0, 2)+"/"+sha1keyOfCommitFile.substring(2)));
			
		}
		

	}


