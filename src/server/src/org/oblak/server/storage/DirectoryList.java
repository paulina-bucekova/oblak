package org.oblak.server.storage;

import java.io.IOException;
import java.util.Map;

import org.oblak.server.util.ConnectionData;

/**
 * Implements a directory list.
 */
public class DirectoryList {
	
	private ConnectionData connectionData;
	DirectoryListThread directoryListThread;

	/**
	 * Constructor.
	 * @param connectionData Connection information.
	 */
	public DirectoryList(ConnectionData connectionData) {		
		this.connectionData = connectionData;
	}

	/**
	 * Generates the list.
	 */
	public void list() {
		String directoryName = null;
		try {
			directoryName = connectionData.getSocketIO().getDataInputStream().readUTF();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		if(directoryName != null){
			directoryListThread = new DirectoryListThread(System.getProperty("user.home") + directoryName);
			directoryListThread.start();
			try {
				directoryListThread.join();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return The directory list.
	 */
	public Map<String, String> getFileMap() {
		return directoryListThread.getFileMap();
	}	
}
