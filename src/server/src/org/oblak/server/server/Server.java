package org.oblak.server.server;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Security;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.oblak.server.util.SocketIO;

/**
 * Handles client connections.
 */
public class Server{
	/**
	 * Used to indicate a server problem.
	 */
	public class ServerException extends RuntimeException{
		private static final long serialVersionUID = -352214670310560764L;
	}
	
	private int	port;
	private SSLServerSocket	socket;
	private String	keyStorePath;
	private String	keyStorePassword;	
	private boolean	running;
	private TreeMap<String, ClientSession> clientSessions;

	/**
	 * Constructor.
	 * @param port The port the server is going to run on.
	 * @param keyStorePath Path to the keystore file.
	 * @param keyStorePassword Password of the keystore file.
	 * @throws IOException
	 * @throws ServerException
	 */
	public Server(int port, String keyStorePath, String keyStorePassword) throws IOException, ServerException{
		this.port = port;
		this.keyStorePath = keyStorePath;
		this.keyStorePassword = keyStorePassword;
		this.running = false;
		this.clientSessions = new TreeMap<String, ClientSession>();
		
		SSLServerSocketFactory serverSocketFactory = initSSL();
		socket = (SSLServerSocket)serverSocketFactory.createServerSocket(port);
		socket.setNeedClientAuth(true);
	}

	/**
	 * Runs the server.
	 */
	public void start(){
		try{
			running = true;
			
			do{
				ConnectionAcceptor acceptor = new ConnectionAcceptor(socket);
				acceptor.start();
				
				while (acceptor.isAlive()){
					checkFinishedSessions();
					
					Thread.sleep(10);
				}
				
				acceptor.join();
				addToSession(acceptor.getSocketIO());
			}
			while(running);
		}catch (Exception exception){
			exception.printStackTrace();
			stop();
		}
	}

	/**
	 * Stops the server.
	 */
	public void stop(){
		running = false;
	}

	/**
	 * @return The port the server is running on.
	 */
	public int getPort(){
		return port;
	}
	
	/**
	 * @return If the server is running.
	 */
	public boolean isRunning(){
		return running;
	}

	/**
	 * Adds a new connection to a session. If the session does not exist, it will be created.
	 * @param socketIO
	 */
	public synchronized void addToSession(SocketIO socketIO){
		final String sessionId = socketIO.getSocket().getInetAddress().toString();
		ClientSession session = this.clientSessions.get(sessionId);
		
		if (session == null){
			// #debug:
			System.out.println("Adding connection from port " + Integer.toString(socketIO.getSocket().getPort()) + 
					" to new session " + sessionId);
			
			session = new ClientSession();
			
			clientSessions.put(sessionId, session);
			session.addConnection(socketIO);
			session.start();
		}
		else{
			session.addConnection(socketIO);
			
			// #debug:
			System.out.println("Adding connection from port " + Integer.toString(socketIO.getSocket().getPort()) + 
					" to active session " + sessionId);
		}
	}
	
	/**
	 * Checks for finished sessions and removes them from the session list.
	 */
	private synchronized void checkFinishedSessions(){
		for(Iterator<Entry<String, ClientSession>> it = clientSessions.entrySet().iterator(); it.hasNext();) {
			Entry<String, ClientSession> entry = it.next();
			ClientSession session = entry.getValue();
			
			if(!session.isRunning()){
				// #debug:
				System.out.println("Removing session " + entry.getKey());
				
				try{
					session.join();
				}catch (InterruptedException e)	{
					// do nothing
				}
				finally{
					it.remove();
				}
			}
		}
	}
	
	/**
	 * Initializes SSL configuration.
	 * @return A SSLSocketFactory for generating SSL sockets.
	 * @throws ServerException
	 */
	private SSLServerSocketFactory initSSL() throws ServerException{
		Security.addProvider(new BouncyCastleProvider());
		SSLServerSocketFactory serverSocketFactory = null;
		FileInputStream kfis = null;
		
		try{
			final File file = new File(this.keyStorePath);
			
			kfis = new FileInputStream(file);
			KeyStore keyStore = KeyStore.getInstance("BKS");
			keyStore.load(kfis, this.keyStorePassword.toCharArray());
			
			TrustManagerFactory tmf = 
					TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
			kmf.init(keyStore, this.keyStorePassword.toCharArray());
			
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			serverSocketFactory = ctx.getServerSocketFactory();
		}
		catch (Exception e)	{
			e.printStackTrace();
			throw new ServerException();
		}
		finally	{
			if (kfis != null){
				try{
					kfis.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}		
		return serverSocketFactory;
	}
}
