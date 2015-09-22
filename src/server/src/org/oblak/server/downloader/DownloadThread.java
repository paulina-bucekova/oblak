package org.oblak.server.downloader;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import org.oblak.server.util.FileUtils;

/**
 * Worker thread for downloading a file.
 */
public class DownloadThread extends Thread {
	
	private Map<String, String> mimeMap;
	private String urlString;
	private String pathString;
	private String directoryName;
	private String fileName;

	private int remoteFileSize;
	private int remoteFileCurrentSize;	

	
	/**
	 * Constructor.
	 * @param urlString URL to fetch the file from.
	 * @param mimeMap Map with MIME information.
	 * @throws IOException
	 */
	public DownloadThread(String urlString, Map<String, String> mimeMap) throws IOException {
		this.mimeMap = mimeMap;
		this.urlString = urlString;
		
		remoteFileSize = 0;
		remoteFileCurrentSize = 0;
		
		directoryName = null;
		
		pathString = System.getProperty("user.home") + "/oblak";
		Path path = Paths.get(pathString);
		
		if (!Files.exists(path))
		{
			Files.createDirectories(path);
		}
		
		for(Entry<String, String> entry: mimeMap.entrySet()){
			path = Paths.get(pathString + "/" + entry.getValue());
			if(!Files.exists(path)) {
				Files.createDirectories(path);
			}			
		}		
	}
	
	/**
	 * @return The size of the remote file.
	 */
	public int getRemoteFileSize() {
		return remoteFileSize;
	}

	/**
	 * @return The current size of the file being downloaded.
	 */
	public int getRemoteFileCurrentSize() {
		return remoteFileCurrentSize;
	}

	/**
	 * @return The file name of the remote file.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Downloads the file.
	 */
	@Override
	public void run() {
		URL url;
		try {
			url = new URL(urlString);
			URLConnection urlConnection;
			System.out.println("Connection to URL established.");
			try {
				urlConnection = url.openConnection();
				urlConnection.connect();
				
				remoteFileSize = urlConnection.getContentLength();				
				String fileMime = urlConnection.getContentType();
				
				String[] fileNames = (String[]) urlString.split("/");
				this.fileName = fileNames[fileNames.length - 1];			
				
				for(Entry<String, String> entry: mimeMap.entrySet()){			
					if(fileMime.matches(entry.getKey())){
						directoryName = entry.getValue();
						break;
					}
				}
				System.out.println("The directory where the remote file will be downloaded: " + directoryName);
				
				if(directoryName != null){
					System.out.println("Directory exists.");
					
					FileUtils fileUtils = new FileUtils();
					this.fileName = fileUtils.getUpdatedFileName(pathString + "/" + directoryName + "/", this.fileName);	
					
					FileOutputStream fileOutputStream;
					try {
						fileOutputStream = new FileOutputStream(pathString + "/" + directoryName + "/" + this.fileName);
						BufferedInputStream bufferedInputStrem;
						try {
							bufferedInputStrem = new BufferedInputStream(url.openStream());
							
							int data;
							int offset = 0;
							int length = 1024;
							byte dataBytes[] = new byte[length];		
							
							while((data = bufferedInputStrem.read(dataBytes, offset, length)) != -1){
								fileOutputStream.write(dataBytes, offset, data);
								remoteFileCurrentSize += length;
							}			    
							bufferedInputStrem.close();
							fileOutputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}				
					} catch (FileNotFoundException e) {				
						e.printStackTrace();
					}					
				}
			} catch (IOException e1) {				
				e1.printStackTrace();
			}
			
			
		} catch (MalformedURLException e1) {			
			e1.printStackTrace();
		}		
	}
}  
			