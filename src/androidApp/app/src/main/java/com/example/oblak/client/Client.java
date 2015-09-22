package com.example.oblak.client;

import com.example.oblak.oblak.activity.MainActivity;
import javax.net.ssl.SSLSocket;

/**
 * SSL Client class.
 */
public class Client{

	/**
	 * Used when there is a problem in the client.
	 */
	public class ClientException extends RuntimeException{
		private static final long serialVersionUID = 3115255923618240990L;		
	}

	private SocketIO socketIO;

	/**
	 * Constructor calls the connect method.
	 */
	public Client()	{connect();	}

	/**
	 * Connects to the server.
	 */
	public void connect() throws ClientException{
		try{
			if (socketIO != null){
				socketIO.getSocket().close();
			}

			MainActivity mainActivity = (MainActivity) MainActivity.instance;
			SSLSocket sslsocket = mainActivity.createSocket();
			socketIO = new SocketIO(sslsocket);
		}
		catch (Exception exception){
			exception.printStackTrace();
			throw new ClientException();
		}
	}

	/**
	 * @return Connection information.
	 */
	public SocketIO getSocketIO() {
		return socketIO;
	}
}
