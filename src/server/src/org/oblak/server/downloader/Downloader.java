package org.oblak.server.downloader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Downloads a file.
 */
public class Downloader {
	
	private Map<String, String> mimeMap;
	private String url;
	private DownloadThread downloadThread;

	/**
	 * Constructor.
	 * @param url URL of the file.
	 */
	public Downloader(String url) {
		
		this.url = url;
		mimeMap = new HashMap<String, String>();
		mimeMap.put(MimeTypes.MIME_AUDIO, MimeTypes.DIRECTORY_AUDIO);
		mimeMap.put(MimeTypes.MIME_IMAGE, MimeTypes.DIRECTORY_IMAGE);
		mimeMap.put(MimeTypes.MIME_PDF, MimeTypes.DIRECTORY_PDF);		
		mimeMap.put(MimeTypes.MIME_VIDEO, MimeTypes.DIRECTORY_VIDEO);
		mimeMap.put(MimeTypes.MIME_ZIP, MimeTypes.DIRECTORY_ZIP);		
	}
	
	/**
	 * Starts the download.
	 * @throws IOException
	 */
	public void download() throws IOException {
		downloadThread = new DownloadThread(url, mimeMap);
		downloadThread.start();	
	}

	/**
	 * @return The size of the remote file.
	 */
	public int getRemoteFileSize() {
		return downloadThread.getRemoteFileSize();
	}

	/**
	 * @return The current size of the file being downloaded.
	 */
	public int getRemoteFileCurrentSize() {
		return downloadThread.getRemoteFileCurrentSize();
	}
	
	/**
	 * @return The file name of the remote file.
	 */
	public String getRemoteFileName() {
		return downloadThread.getFileName();
	}

	/**
	 * @return Whether the download is in progress.
	 */
	public boolean isRunning()
	{
		return downloadThread.isAlive();
	}
	
	/**
	 * Stops the download.
	 */
	public void close()
	{
		try {
			if (downloadThread.isAlive())
			{
				downloadThread.interrupt();
			}
			
			downloadThread.join();
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
