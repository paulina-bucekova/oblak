package org.oblak.server.storage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Worker thread for creating a directory list.
 */
public class DirectoryListThread extends Thread{
	
	private String directoryName;	
	private Map<String, String> fileMap;
	
	/**
	 * Constructor.
	 * @param directoryName Name of the directory.
	 */
	public DirectoryListThread(String directoryName) {		
		this.directoryName = directoryName;
		fileMap = new HashMap<String, String>();
	}		

	/**
	 * @return The directory list.
	 */
	public Map<String, String> getFileMap() {
		return fileMap;
	}

	/**
	 * Creates the directory list.
	 */
	@Override
	public void run() {
		
		File folder = new File(directoryName);
		File[] folderList = folder.listFiles();	
		if (folderList != null) {		
			for(File file: folderList){
				if(file.isDirectory()){
					fileMap.put(file.getName(), "directory");
				}
				else{
					fileMap.put(file.getName(), "file");
				}			
			}		
		}
	}
}
