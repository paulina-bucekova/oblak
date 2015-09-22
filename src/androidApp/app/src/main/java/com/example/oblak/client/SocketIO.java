package com.example.oblak.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;

/**
 * Information about a socket.
 */
public class SocketIO {

	private SSLSocket socket;
	
	private InputStream	inputStream;
	private InputStreamReader inputStreamReader;
	private DataInputStream	dataInputStream;
	private	BufferedReader bufferedReader;
	private ObjectInputStream objectInputStream;
	
	private OutputStream outputStream;
	private OutputStreamWriter outputStreamWriter;
	private DataOutputStream dataOutputStream;
	private ObjectOutputStream	objectOutputStream;

	/**
	 * Constructor.
	 * @param sslSocket Socket to wrap.
	 * @throws IOException
	 */
	public SocketIO(SSLSocket sslSocket) throws IOException{
		socket = sslSocket;
		inputStream = socket.getInputStream();
		inputStreamReader = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(inputStreamReader);
		dataInputStream = new DataInputStream(inputStream);
		
		outputStream = socket.getOutputStream();
		outputStreamWriter = new OutputStreamWriter(outputStream);
		dataOutputStream = new DataOutputStream(outputStream);
	}

	/**
	 * @return An input stream based on the socket.
	 */
	public InputStream getInputStream()
	{
		return inputStream;
	}

	/**
	 * @return An input stream reader based on the socket.
	 */
	public InputStreamReader getInputStreamReader()
	{
		return inputStreamReader;
	}

	/**
	 * @return A buffered reader based on the socket.
	 */
	public BufferedReader getBufferedReader()
	{
		return bufferedReader;
	}

	/**
	 * @return The raw socket.
	 */
	public SSLSocket getSocket()
	{
		return socket;
	}

	/**
	 * @return A data input stream based on the socket.
	 */
	public DataInputStream getDataInputStream() {
		return dataInputStream;
	}

	/**
	 * @return An output stream based on the socket.
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Sets the output stream.
	 * @param outputStream The output stream.
	 */
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @return An output stream writer based on the socket.
	 */
	public OutputStreamWriter getOutputStreamWriter() {
		return outputStreamWriter;
	}

	/**
	 * Sets the output stream writer.
	 * @param outputStreamWriter The output stream writer.
	 */
	public void setOutputStreamWriter(OutputStreamWriter outputStreamWriter) {
		this.outputStreamWriter = outputStreamWriter;
	}

	/**
	 * @return A data output stream based on the socket.
	 */
	public DataOutputStream getDataOutputStream() {
		return dataOutputStream;
	}

	/**
	 * Sets the output data stream.
	 * @param dataOutputStream The output data stream.
	 */
	public void setDataOutputStream(DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

	/**
	 * @return An object input stream based on the socket.
	 */
	public ObjectInputStream getObjectInputStream() {
		objectInputStream  = null;
		try {
			objectInputStream = new ObjectInputStream(inputStream);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return objectInputStream;
	}

	/**
	 * Sets the object input stream.
	 * @param objectInputStream The output data stream.
	 */
	public void setObjectInputStream(ObjectInputStream objectInputStream) {
		this.objectInputStream = objectInputStream;
	}

	/**
	 * @return An object output stream based on the socket.
	 */
	public ObjectOutputStream getObjectOutputStream() {
		objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(outputStream);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return objectOutputStream;
	}

	/**
	 * Sets the object output stream.
	 * @param objectOutputStream The output data stream.
	 */
	public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
		this.objectOutputStream = objectOutputStream;
	}
}
