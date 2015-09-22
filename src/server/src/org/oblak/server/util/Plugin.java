package org.oblak.server.util;

import java.io.IOException;

/**
 * Plugin interface to be implemented by system and third-party plugins.
 */
public interface Plugin { 
	/**
	 * Used for informing of problems of a Plugin.
	 */
	public class PluginException extends RuntimeException{
		private static final long serialVersionUID = -1891345183082782158L;		
	}

	/**
	 * Processes a message.
	 * @param connectionData Information about the connection.
	 * @throws IOException
	 * @throws PluginException
	 */
	public void processMessage(ConnectionData connectionData) throws IOException, PluginException;
	
	/**
	 * Updates plugin logic.
	 */
	public void update();
	
	/**
	 * @return Whether the plugin is idle.
	 */
	public boolean isIdle();
}
