package org.oblak.server.server;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.oblak.server.downloader.LinkDownloader;
import org.oblak.server.storage.RemoteStorage;
import org.oblak.server.util.ConnectionData;
import org.oblak.server.util.MsTimer;
import org.oblak.server.util.Plugin;
import org.oblak.server.util.SocketIO;

// #todo: check for locked plugins i.e. if a single plugin is locked for more than x ms then do something

/**
 * Represents a client session.
 */
public class ClientSession extends Thread {	
	public static final int pluginIdSize = 4;
	public static final int idleMsToCloseSession = 60000;
	private boolean running;
	private MsTimer idleTimer;
	private List<ConnectionData> connections;
	private List<Plugin> plugins;
	
	/**
	 * Constructor.
	 */
	public ClientSession(){
		running = false;
		connections = new ArrayList<ConnectionData>();
		idleTimer = new MsTimer(idleMsToCloseSession);
		
		idleTimer.start();		
		
		// #todo: make it extensible
		plugins = new ArrayList<Plugin>();
		plugins.add(new LinkDownloader());
		plugins.add(new RemoteStorage());	
	}
	
	/**
	 * @return If this session is active.
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Adds a connection to the session.
	 * @param socketIO The new connection to be added.
	 */
	public synchronized void addConnection(SocketIO socketIO){
		
		ConnectionData connectionData = new ConnectionData(socketIO); 
				
		try{
			final int pluginId = connectionData.getSocketIO().getDataInputStream().readInt();
			final Plugin plugin = plugins.get(pluginId);
			
			if (plugin != null)	{
				plugin.processMessage(connectionData);
			}
			else{
				throw new Plugin.PluginException();
			}			
		}catch (Exception e){
			// #debug:
			System.out.println("Failed to process connection from port " 
					+ Integer.toString(connectionData.getSocketIO().getSocket().getPort()));
		}finally {			
			connections.add(connectionData);
			running = true;
			idleTimer.start();
		}
	}
	
	/**
	 * Updates the session while it is active.
	 */
	@Override
	public void run(){
		running = true;
		
		while(running){
			try {
				// update connections state
				updateConnections();
				
				// update plugins state
				updatePlugins();
				
				// check for end of session
				if (idleTimer.hasFinished()){
					running = false;
				}
			}catch (Exception e){
				e.printStackTrace();
				running = false;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	/**
	 * Checks for inactive connections and closes them.
	 * @throws IOException
	 * @throws Plugin.PluginException
	 */
	private synchronized void updateConnections() throws IOException, Plugin.PluginException {
		Iterator<ConnectionData> it = connections.iterator();
		
		while (it.hasNext()){
			ConnectionData connectionData = it.next();
			
			if (!connectionData.isActive())	{
				// #debug:
				System.out.println("Removing connection from port " + Integer.toString(connectionData.getSocketIO().getSocket().getPort()));
				
				closeConnection(connectionData);
				it.remove();
			}
		}
	}
	
	/**
	 * Updates plugins logic.
	 */
	private void updatePlugins() {
		Iterator<Plugin> itPlugin = plugins.iterator();
		
		while (itPlugin.hasNext()){
			Plugin plugin = itPlugin.next();
			
			plugin.update();
			
			if (!plugin.isIdle()){
				idleTimer.start();
			}
		}
	}
	
	/**
	 * Closes a connection.
	 * @param connectionData Connection to be closed.
	 */
	private void closeConnection(ConnectionData connectionData){
		try {
			connectionData.getSocketIO().getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
