package com.l2client.network.login;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2client.util.ByteUtils;

public abstract class BaseLoginHandler implements Runnable {

	private static Logger log = Logger.getLogger(BaseLoginHandler.class
			.getName());
	
	public enum LoginStatus {
		DISCONNECTED, INIT, AUTHGAMEGUARD, AUTHLOGIN, AUTHENTIFICATED
	}

	public boolean connected;
	public LoginStatus status = LoginStatus.DISCONNECTED;
	private Socket socket;

	private DataOutputStream outStream;
	private DataInputStream inStream;

	public BaseLoginHandler(Integer port, String host) {

		try {
			//added this block due to java 7/windows 7 connection problems with dual stack
			InetAddress i = null;
//			if("localhost".equals(host)|| "127.0.0.1".equals(host)) {
//				i = InetAddress.getLocalHost();
//			}
//			else {
				i = InetAddress.getByName(host);
//			}
			log.fine("Using client address of "+i+" for connecting to server "+host+" on port "+port);
			socket = new Socket(i,port);
			//end of added 
			outStream = new DataOutputStream(socket.getOutputStream());
			inStream = new DataInputStream(socket.getInputStream());
		} catch (EOFException excepcionEOF) {
			log.severe("Connection terminated");
			connected = false;
			return;
		} catch (IOException excepcionES) {
			log.severe("Connection failed");
			connected = false;
			return;
		}
		this.status = LoginHandler.LoginStatus.INIT;
		connected = true;
		Thread t = new Thread(this);
		t.start();

	}

	public void run() {
		 		try {
			while (connected) {
				byte[] buf = new byte[2];
				int read = inStream.read(buf);
				//added this block due to java 7/windows 7 connection problems with dual stack
				if (read < 0){
					//this can happen on ipv6 stacks
						if (socket.isConnected()) {
							handlePacket(null);
							socket.close();
							connected = false;
						}
						log.severe("Disconnected, unable to read data from connection");
						return;
				}
				int rawSize = ByteUtils.Sbyte2int(buf[0]) + ByteUtils.Sbyte2int(buf[1]) * 256;


				if(rawSize <=0){
					continue;
				}
				
				byte[] buf2 = new byte[rawSize];

				inStream.read(buf2, 2, rawSize - 2);

				try {
					buf2[0] = buf[0];
					buf2[1] = buf[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					if (socket.isConnected()) {
						handlePacket(null);
						socket.close();
						connected = false;
					}
					log.severe("Disconnected due to array out of bounds on login packet");
					return;
				}
				handlePacket(buf2);
			}
		} catch (Exception e) {
			//exception wil be thrown alos when closing gracefully and wayting for input on the socket
			if(status != LoginStatus.DISCONNECTED && connected !=false){
			log.log(Level.SEVERE, "Connection error", e);
			connected = false;
			}
		} 
		
	}

	public void sendPacketToLogin(byte[] raw) {
		try {
			outStream.write(raw);
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doDisconnect(boolean todoOk, String host, int port) {
		onDisconnect(todoOk, host, port);

		connected = false;
		status = LoginStatus.DISCONNECTED;
		try {
			outStream.close();
			inStream.close();
			socket.close();
			log.info("Loginsocket closed");
		} catch (IOException ex) {
			// WE ignore this, as we want the socket to get down NOW
			log.severe("Loginsocket close failed");
		}
	}

	/**
	 * Callback called when the LoginHadler should terminate, or stop sending any longer.
	 * Should be overridden in concrete use case
	 * @param todoOk boolean flag for closing the socket (false) or only setting the internal state to disconnected (true)
	 * 				 this is used for signalling the final login procedure at the moment (true case)
	 * 
	 * @param host	 host ip of the gameserver on todoOk, null otherwise
	 * @param port	 port of the gameserver on todoOk, null otherwise
	 */
	protected abstract void onDisconnect(boolean todoOk, String host, int port);
	
	protected abstract void handlePacket(byte[] raw) throws IOException;
}