package org.oblak.server.downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oblak.server.util.ConnectionData;
import org.oblak.server.util.Plugin;
import org.oblak.server.util.SocketIO;
import org.oblak.server.downloader.Downloader;

/**
 * LinkDownloader plugin. Allows for background downloading of files. A list
 * of the files currently being downloaded can also be consulted.
 */
public class LinkDownloader implements Plugin {
	
	private final int COMMAND_DOWNLOAD = 0;
	private final int COMMAND_GET_STATUS = 1;
	
	private List<Downloader> activeDownloads;

	/**
	 * Constructor.
	 */
	public LinkDownloader(){
		activeDownloads = new ArrayList<Downloader>();
	}
	
	/**
	 * Process a message.
	 * @param connectionData Connection information.
	 */
	@Override
	public void processMessage(ConnectionData connectionData) throws IOException {
		int commandNumber = connectionData.getSocketIO().getDataInputStream().readInt();
		if(commandNumber == COMMAND_DOWNLOAD){
			downloadResourse(connectionData.getSocketIO());
		}
		else if(commandNumber == COMMAND_GET_STATUS){
			
			List<Map<String,List<Integer>>> allActiveDowloadStatus = new ArrayList<Map<String,List<Integer>>>();
			
			for(Downloader downloader: activeDownloads){
				Map<String,List<Integer>> downloadMap = new HashMap<String,List<Integer>>();				
				List<Integer> downloadStatus = new ArrayList<Integer>();
				
				downloadStatus.add(downloader.getRemoteFileSize());
				downloadStatus.add(downloader.getRemoteFileCurrentSize());
				downloadMap.put(downloader.getRemoteFileName(), downloadStatus);
				
				allActiveDowloadStatus.add(downloadMap);
			}
			
			connectionData.getSocketIO().getObjectOutputStream().writeObject(allActiveDowloadStatus);			
		}
	}
	
	/**
	 * Updates plugin logic.
	 */
	@Override
	public void update(){
		Iterator<Downloader> it = activeDownloads.iterator();
		
		while (it.hasNext()){
			Downloader downloader = it.next();
			
			if (!downloader.isRunning()){
				downloader.close();
				it.remove();
			}
		}	
	}
	
	/**
	 * Downloads a resource.
	 * @param socketIO Information about the socket.
	 * @throws IOException
	 */
	private void downloadResourse(SocketIO socketIO) throws IOException {
		String urlString = socketIO.getDataInputStream().readUTF();
		Downloader resourceDownload = new Downloader(urlString);
		activeDownloads.add(resourceDownload);
		resourceDownload.download();
	}

	/**
	 * @return Whether the plugin is idle.
	 */
	@Override
	public boolean isIdle() {		
		return activeDownloads.isEmpty();
	}
}
