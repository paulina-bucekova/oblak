package org.oblak.server.storage;

import org.oblak.server.util.ConnectionData;

/**
 * Stores a file.
 */
public class Store {
	
	private ConnectionData connectionData;
	private StoreThred saverThread;

	/**
	 * Constructor.
	 * @param connectionData Connection information.
	 */
	public Store(ConnectionData connectionData) {		
		this.connectionData = connectionData;
	}

	/**
	 * Starts the file transfer.
	 */
	public void save() {
		saverThread = new StoreThred(connectionData.getSocketIO());
		saverThread.start();
		connectionData.setActive(true);
	}
	
	/**
	 * @return Whether the file transfer is in progress.
	 */
	public boolean isRunning() {
		return saverThread.isAlive();
	}

	/**
	 * Stops the file transfer.
	 */
	public void close()
	{
		try {
			if (saverThread.isAlive()) {
				saverThread.interrupt();
			}

			saverThread.join();
		} catch (InterruptedException e) {
			// do nothing
		} finally {
			connectionData.setActive(false);
		}
	}

	/**
	 * @return Connection information.
	 */
	public ConnectionData getConnectionData() {
		return connectionData;
	}
}
