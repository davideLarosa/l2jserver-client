package com.l2client.network.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2client.app.Singleton;
import com.l2client.model.network.ClientFacade;
import com.l2client.network.game.ClientPackets.GameClientPacket;
import com.l2client.util.ByteUtils;

/**
 * The base handler for the internal connection to the game server. 
 * Subclasses can override onConnect, onDisconnect and handlePacket
 * 
 * This class should handle the low level connect/disconnect, send/receive packets.
 * Subclasses should just be processing the received packets and custom connect/disconnect behavior
 * Encryption can be enabled by providing an appropriate key which will be used for sending and receiving packets
 * 
 */
public abstract class BaseGameHandler implements Runnable{

	public static final byte STATE_INITIAL = 0;
	public static final byte STATE_AUTHENTIFIED = 1;
	public static final byte STATE_INGAME = 2;
	
	protected static Logger log = Logger.getLogger(BaseGameHandler.class.getName());

	public ClientFacade clientFacade;
	private boolean connected;
	private Socket socket;
	private DataOutputStream outStream;
	private DataInputStream inStream;
	private GameCrypt crypt = new GameCrypt(null);

	public byte status = STATE_INITIAL;
	
	/**
	 * Creates the network socket based on the passed information, 
	 * calls doConnect and starts the threaded processing of received packets
	 * @param host	host string to connect to
	 * @param port	port on host to connect to
	 */
	public BaseGameHandler(String host, int port) {

		try {
			//added this block due to java 7/windows 7 connection problems with dual stack
			InetAddress i = null;
//			if("localhost".equals(host)|| "127.0.0.1".equals(host)) {
//				i = InetAddress.getLocalHost();
//			}
//			else {
				i = InetAddress.getByName(host);
//			}
			log.finer("Using client address of "+i+" for connecting to server "+host+" on port "+port);
			socket = new Socket(i,port);
			//end of added 
			outStream = new DataOutputStream(socket.getOutputStream());
			inStream = new DataInputStream(socket.getInputStream());
		} catch (EOFException excepcionEOF) {
			log.severe("Connection terminated:"+excepcionEOF);
			Singleton.get().getGuiController().showErrorDialog(excepcionEOF.toString());
			connected = false;
			return;
		} catch (IOException excepcionES) {
			log.severe("Connection error:"+excepcionES);
			Singleton.get().getGuiController().showErrorDialog(excepcionES.toString());
			connected = false;
			return;
		}

		connected = true;
		doConnect();
		Thread t = new Thread(this);
		t.start();
	}
	
	/**
	 * Must be implemented in subclasses will be passed over the read bytes from which
	 * the own processing should now be implemented, first two bytes were removed for 
	 * specification of size of the raw array
	 * @param raw	byte array of the data received
	 */
	protected abstract void handlePacket(byte[] raw);
	
	/**
	 * Called when sockets are ready but before threading starts (for example for sending an initial packte)
	 * Will be called within a try/catch clause
	 */
	protected abstract void onConnect();
	/**
	 * Called before socket will be closed (if the socket is still open). Can be used to send gracefull diconnect notifications to the server
	 * Will be called within a try/catch clause
	 */
	protected abstract void onDisconnect();
	
	/** 
	 * called from the keypackt to set the gamepacket crypt/decrypt key
	 * @param key key from the gameserver to be used for packet crypting
	 */
	public void setKey(byte[] key) {
		crypt = new GameCrypt(key);
	}

	/**
	 * Internal connect method, just calls onConnect within a try/catch
	 */
	public void doConnect() {
		try {
			onConnect();
		} catch (Exception e){
			//intentionally blank
		}
	}

	/**
	 * Internal disconnect method, just calls onDisconnect within a try/catch and then
	 * closes the socket.
	 */
	public void doDisconnect() {
		try{
			onDisconnect();
		} catch (Exception e){
			//intentionally blank
		}
		connected = false;
		try {
			if (socket != null && socket.isConnected()){
				outStream.close();
				inStream.close();
				socket.close();
				}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Exception in onDisConnect:", e);
		}
		log.info("Gamesocket closed");
		connected = false;
	}

	/**
	 * Reads 2 bytes from the stream to determin the size of the message, then reads the
	 * rest and calls handlePacket to process the message bytes
	 */
	public void run() {
		try {
			while (connected) {
				byte[] buf = new byte[2];
				int read = inStream.read(buf);
				if(read <0){
					if (socket != null && socket.isConnected()) {
						socket.close();
						connected = false;
					}
					log.severe("Disconnected due to eond of stream in read");
					return;
				} else if(read != 2){
					log.severe("Read less than 2 bytes for packet length, skipping info");
					continue;
				}
				int rawSize = ByteUtils.Sbyte2int(buf[0]) + ByteUtils.Sbyte2int(buf[1]) * 256;

				if(rawSize <=0)
					continue;
				
				byte[] buf2 = new byte[rawSize];

				read = inStream.read(buf2, 2, rawSize - 2);
				if(read <0){
					if (socket != null && socket.isConnected()) {
						socket.close();
						connected = false;
					}
					log.severe("Disconnected due to eond of stream in read");
					return;
				} else{
					if(read !=rawSize-2){
						log.info("Stream read returned only "+read+" of "+(rawSize-2)+" bytes in total");
						while(read != rawSize-2)
							read+=inStream.read(buf2,2+read,rawSize-2-read);
						
						log.info("Stream finally read "+read+" bytes of "+(rawSize-2)+" bytes in total");
					}
				}

				try {
					buf2[0] = buf[0];
					buf2[1] = buf[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					if (socket.isConnected()) {
						socket.close();
						connected = false;
					}
					log.severe("Disconnected due to array out of bounds on game packet");
					return;
				}
				crypt.decrypt(buf2, 2, rawSize - 2);
				handlePacket(buf2);
			}
		} catch (Exception e) {
			if(connected){
				if (socket != null && socket.isConnected()) {
					try {
						socket.close();
					} catch (IOException e1) {
						//intentionally ignored
					}
				}
			log.log(Level.SEVERE, "Connection error", e);
			connected = false;
			//FIXME display error & close
			} //else we have closed the socket and received a SocketEception while waiting in read() for data so ignore this
		} 
	}

	/**
	 * Sends a raw packet to the gameserver, which will be encrypted before sending if a
	 * key was set. The sent packet has 2 bytes more (for length information) than the raw packet
	 * @param raw The byte array to be sent
	 */
	public void sendPacket(GameClientPacket packet) {
		if(connected)
		try {
			log.fine("Sending "+packet.getClass().getSimpleName()+" to LoginServer");
			
			byte[] raw = packet.getBytes();
			byte[] h = new byte[raw.length + 2];
			h[0] = (byte) (h.length % 256);
			h[1] = (byte) (h.length / 256);
			System.arraycopy(raw, 0, h, 2, raw.length);
			crypt.encrypt(h, 2, h.length - 2);
			outStream.write(h);
			outStream.flush();
		} catch (IOException e) {
			//TODO close client, or at least throw player to login screen
			log.log(Level.SEVERE, "Exception in sendPacket:", e);
			connected = false;
		}
	}

}
