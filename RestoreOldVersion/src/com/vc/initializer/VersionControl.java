package com.vc.initializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import com.vc.checksum.SHA1;
import com.vc.compression.GzipHelper;
import com.vc.compression.UnzipHelper;
import com.vc.helper.BaseWriter;
import com.vc.helper.CommitWriter;
import com.vc.helper.DirectoryScan;
import com.vc.helper.PersistCommit;
import com.vc.helper.ReadCommits;

import javafx.scene.chart.ValueAxis;

public class VersionControl {
/**
 * This method will take the parameter and decide the course of action
 * for example save will create the .ver hidden directory if not exist 
 * and a log file will be created under that
 * usage :
 * to take snapshot-->  java VersionControl save path_to_directory
 * See all commits--> java VersionControl showcommits path_to_directory
 * restore old snapshot-->  java VersionControl restore path_to_directory commitTimestamp
 * @author pronoyde
 * @param args
 * @throws IOException 
 */
	private static ReadCommits readCommits;
	private static File file;
	private static HashMap<String,String> commitMap;
	private static FileWriter out;
	private static File preCommitFile;
	private static CommitWriter commitWriter;
	private static BaseWriter baseWriter;
	private static File objectFilePath;
	private static GzipHelper gzipHelper;
	private static CompressFile compressFile;
	private static File commits; 
	private static FileWriter outCommits;
	private static BaseWriter baseWriterCommits;
	private static CommitWriter commitFileWriter; 
	private static LocalDateTime commitTimestamp;
	
	
	public static void main(String[] args) throws IOException {
		File file = new File(args[1]);
		
		//requires in case of restoration. Quick mv for rename and then slow delete at the end 
		File newDirectory = new File (file.getCanonicalPath()+"Back");
		//Timestamp of each commit and the sha1 key for the file which contains the directory snapshot of that time
		File commitFile = new File (file.getCanonicalPath()+"/.ver/commits");
		FileReader fileReader = new FileReader(commitFile);
		//Read key:value of commits file
		PropertyResourceBundle prb = new PropertyResourceBundle(fileReader);
		if(args.length>2)
			readCommits= new ReadCommits(prb, args[2]);
		else if (args.length<=2)
			readCommits= new ReadCommits(prb);
		
		//Prepare objects for DirectoryScan
		preCommitFile=new File(file.getCanonicalPath()+"/.ver/precommit");
		out = new FileWriter(preCommitFile,true);
		baseWriter= new BaseWriter(out);
		commitWriter= new CommitWriter(out, baseWriter);
		objectFilePath=new File(file.getCanonicalPath()+"/.ver/objects");
		gzipHelper= new GzipHelper();
		compressFile = new CompressFile(gzipHelper);
		
		//Prepare objects for PersistCommit
		commits= new File(file.getCanonicalPath()+"/.ver/commits");
		commitTimestamp = LocalDateTime.now();
		outCommits = new FileWriter(commits,true);
		baseWriterCommits= new BaseWriter(outCommits);
		commitFileWriter = new CommitWriter(outCommits, baseWriterCommits);
		String format = commitTimestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ENGLISH));
		PersistCommit persistCommit = new PersistCommit(commitFileWriter, format);
		
		DirectoryScan directoryScan = new DirectoryScan(file,preCommitFile,commitWriter,objectFilePath, compressFile, persistCommit);
		
		if(args[0].equalsIgnoreCase("save"))
		{
			
			if(file.isDirectory()) {
				File versionFile = new File(args[1]+"/.ver");
					
						initVersionControl(versionFile);
						directoryScan.execute();
				}
					
			else{
				System.err.println(args[1]+ " is not a directory");
			}
			
		}
		else if(args[0].equalsIgnoreCase("restore"))
		{
			try {
		
			//Take backup(Need more work to avoid silent failure in sub-directory creation : if any )
			cloneExistingDirectory(file,newDirectory);
			
			String sha1=readCommits.read();
			String pathToCommitSnapshot=newDirectory.getCanonicalPath()+"/.ver/objects/"+sha1.substring(0,2)+"/"+sha1.substring(2);
			File commitSnapShot=new File(pathToCommitSnapshot);
			//if Timestamp is valid and a file snapshot SHA1 (file) found
			if(sha1.length()>0)
				new UnzipHelper(commitSnapShot).readCommitAndRestore();
			
			//at the end move the .ver directory to the restored directory
			File currentVerDirectory=new File(newDirectory.getCanonicalPath()+"/.ver");
			File targetVerDirectory = new File(file.getAbsolutePath()+"/.ver");
			currentVerDirectory.renameTo(targetVerDirectory);
			
			//Lazy cleanup: remove the backup directory (this can be handed to different Thread once the .ver directory is moved out) 
			deleteDirectory(newDirectory);
				
			}catch (Exception e) {
				// TODO: Better exception handling in future 
				e.printStackTrace();
			}
		}
		else if(args[0].equalsIgnoreCase("showcommits"))
		{
			
			commitMap = readCommits.readAll();
			for(Map.Entry<String, String> value:commitMap.entrySet() )
				System.out.println(value.getKey()+"\t"+value.getValue());
		}
	}
	/**
	 * delete the previously renamed directory for the cleanup purpose. 
	 * At this point .ver should have been moved to main directory
	 * @param directoryToBeDeleted
	 * @return
	 */
	
	private static
	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	/**
	 * Rename the directory like mv command and restore slowly from there
	 * @param oldDirectory
	 * @param newDirectory
	 */
	private 
	static void cloneExistingDirectory(File oldDirectory, File newDirectory) {
		oldDirectory.renameTo(newDirectory);
	}
	/**
	 * This method considers the file is a directory and creates the sub-directory .ver under that
	 * File is directory or not that check should be performed in the calling method
	 * @param file
	 */
	private 
	static void initVersionControl(File file) {
		try {
			
			if(!file.exists())
				file.mkdir();
			//Create the objects directory for the 1st time
			File objects = new File(file.getCanonicalPath()+"/objects");
			if(!objects.exists()) 
				objects.mkdir();
			
			//Create the commits file for the 1st time
			File commit = new File(file.getCanonicalPath()+"/commits");
			if(!commit.exists())
				commit.createNewFile();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
