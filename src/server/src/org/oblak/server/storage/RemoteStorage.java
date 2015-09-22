package org.oblak.server.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oblak.server.util.ConnectionData;
import org.oblak.server.util.Plugin;

/**
 * Remote Storage plugin. Allows for file uploading and list.
 */
public class RemoteStorage implements Plugin{

	private List<Store> activeSavers;

	/**
	 * Constructor.
	 */
	public RemoteStorage(){
		this.activeSavers = new ArrayList<Store>();
	}

	/**
	 * Processes a message.
	 * @param connectionData Connection information.
	 */
	@Override
	public void processMessage(ConnectionData connectionData) {
		int commandNumber = -1;
		try {
			commandNumber = connectionData.getSocketIO().getDataInputStream().readInt();
			if(commandNumber == 0){
				Store saver = new Store(connectionData);
				activeSavers.add(saver);
				saver.save();
			}
			else if(commandNumber == 1){
				DirectoryList directoryList = new DirectoryList(connectionData);
				directoryList.list();
				connectionData.getSocketIO().getObjectOutputStream().writeObject(directoryList.getFileMap());	
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}

	/**
	 * Updates plugin logic.
	 */
	@Override
	public void update() {
		Iterator<Store> it = activeSavers.iterator();
		
		while (it.hasNext()){
			Store saver = it.next();

			if (!saver.isRunning()){
				saver.close();
				it.remove();
			}
		}
	}

	/**
	 * @return Whether the plugin is idle.
	 */
	@Override
	public boolean isIdle() {
		return activeSavers.isEmpty();
	}
}
