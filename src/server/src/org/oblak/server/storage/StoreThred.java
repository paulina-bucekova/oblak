package org.oblak.server.storage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.oblak.server.util.FileUtils;
import org.oblak.server.util.SocketIO;

/**
 * Worker thread to store a remote file.
 */
public class StoreThred extends Thread{
	
	private SocketIO socketIO;
	private DataInputStream dataInputStream;
	private String pathString;
	
	/**
	 * Constructor.
	 * @param socketIO Information about the socket.
	 */
	public StoreThred(SocketIO socketIO){
		this.socketIO = socketIO;
		pathString = System.getProperty("user.home") + "/oblak";		
	}
	
	/**
	 * Stores the remote file.
	 */
	@Override
	public void run() {
		int arrayLength = 0;
		String fileName = null;
		String directoryName = null;
		try {
			dataInputStream = socketIO.getDataInputStream();
			
			directoryName = dataInputStream.readUTF();
			fileName = dataInputStream.readUTF();			
			arrayLength = dataInputStream.readInt();			
			
			byte[] resourceBytes = new byte[arrayLength];
			dataInputStream.readFully(resourceBytes, 0, resourceBytes.length);
			
			Path path = Paths.get(pathString + "/" + directoryName);
			if(!Files.exists(path)) {
				try {
					Files.createDirectory(path);
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}
			FileUtils fileUtils = new FileUtils();
			fileName = fileUtils.getUpdatedFileName(pathString + "/" + directoryName + "/", fileName);	
			
			FileOutputStream fileOutputStream = new FileOutputStream(new File(pathString + "/" + directoryName + "/" + fileName));
    		fileOutputStream.write(resourceBytes);
    		fileOutputStream.flush();		    
    		fileOutputStream.close();
			
		} catch (IOException e) {			
			e.printStackTrace();
		}	
	}
}
