package org.oblak.server.main;

import org.oblak.server.server.Server;

/**
 * Runs the server.
 */
public class ServerMain{
	/**
	 * Main method.
	 * @param args Aguments to the program.
	 */
	public static void main(String[] args){
		Server server = null;
		
		try	{
			server = new Server(9999, "keys/server_key.jks", "serverpassword");
			server.start();
		} 
		catch (Exception e)	{
			e.printStackTrace();
		}
	}
}
