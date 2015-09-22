package org.oblak.server.server;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;

import org.oblak.server.util.SocketIO;

/**
 * Waits for an incoming connection.
 */
public class ConnectionAcceptor extends Thread{
	private SSLServerSocket serverSocket;
	private SocketIO socketIO;

	/**
	 * Constructor.
	 * @param serverSocket Socket to listen on.
	 */
	public ConnectionAcceptor(SSLServerSocket serverSocket)	{
		this.serverSocket = serverSocket;
	}

	/**
	 * @return Socket information.
	 */
	public synchronized SocketIO getSocketIO(){
		return socketIO;
	}
	
	/**
	 * Waits for an incoming connection.
	 */
	@Override
	public void run(){
		try	{	
			SSLSocket acceptSocket = (SSLSocket)serverSocket.accept();
			socketIO = new SocketIO(acceptSocket);
		}catch (Exception exception){
			exception.printStackTrace();
		}
	}
}
