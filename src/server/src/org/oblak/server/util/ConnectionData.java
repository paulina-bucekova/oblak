package org.oblak.server.util;

/**
 * Information about a connection.
 */
public class ConnectionData {
	
	private boolean active;
	private SocketIO socketIO;

	/**
	 * Constructor.
	 * @param socketIO Information about the socket.
	 */
	public ConnectionData(SocketIO socketIO) {
		this.active = false;
		this.socketIO = socketIO;
	}
	
	/**
	 * @return Whether the connection is active.
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Sets the status of the connection.
	 * @param active If the connection is active or not.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return Information about the socket.
	 */
	public synchronized SocketIO getSocketIO() {
		return socketIO;
	}
}
