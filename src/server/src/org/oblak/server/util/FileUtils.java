package org.oblak.server.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File management utility functions.
 */
public class FileUtils {
	
	/**
	 * @param pathString Path to the file.
	 * @param fileNameString Name of the file.
	 * @return For a file name already in use, returns a usable new name based on it.
	 */
	public String getUpdatedFileName(String pathString, String fileNameString){		
		Path path= Paths.get(pathString + fileNameString);
		if(Files.exists(path)) {
			
			String fileName = path.getFileName().toString();
			String fileNameNoExtension = fileName;
			String fileExtension = "";
			int pos = fileName.lastIndexOf(".");
			if (pos > 0) {
				fileNameNoExtension = fileName.substring(0, pos);
				fileExtension = fileName.substring(pos);
			}
			
			fileName = fileNameNoExtension + "(1)" + fileExtension;			
			path = Paths.get(pathString + fileName);
			
			while(Files.exists(path)){	
				
				fileName = path.getFileName().toString();
				fileNameNoExtension = fileName;
				fileExtension = "";
				pos = fileName.lastIndexOf(".");
				if (pos > 0) {
					fileNameNoExtension = fileName.substring(0, pos);
					fileExtension = fileName.substring(pos);
				}
				
				String numberString = String.valueOf(fileNameNoExtension.charAt(fileNameNoExtension.length() - 2));
				int number = Integer.parseInt(numberString);
				number ++;
				
				String fileNameAux  = fileNameNoExtension.substring(0,fileNameNoExtension.length() - 2);
				fileName = fileNameAux + number + ")" + fileExtension;
				
				path = Paths.get(pathString + fileName);
			}
		}
		return path.getFileName().toString();			
	}
}
