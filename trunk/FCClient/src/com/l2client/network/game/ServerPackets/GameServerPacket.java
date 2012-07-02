package com.l2client.network.game.ServerPackets;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2client.model.network.ClientFacade;
import com.l2client.util.ByteUtils;

/**
 * Abstract base class for packets received from the server. The two package ID
 * bytes will be skipped
 * 
 * Subclasses just override readP() and use readC() etc. to fetch the internal
 * vars.
 */
public abstract class GameServerPacket {
	private byte[] _decrypt;
	private int _off;
	private ClientFacade _client;
	
	protected static Logger log = Logger.getLogger(GameServerPacket.class.getName());

	/**
	 * Reads 32 bits
	 * 
	 * @return Int value representing the 32 bits
	 */
	public int readD() {
		int result = _decrypt[_off++] & 0xff;
		result |= _decrypt[_off++] << 8 & 0xff00;
		result |= _decrypt[_off++] << 0x10 & 0xff0000;
		result |= _decrypt[_off++] << 0x18 & 0xff000000;
		return result;
	}

	/**
	 * Reads a byte
	 * 
	 * @return Int value returned from the read byte
	 */
	public int readC() {
		int result = _decrypt[_off++] & 0xff;
		return result;
	}

	/**
	 * Reads 16 bits from the buffer
	 * 
	 * @return Int value returned from the read byte
	 */
	public int readH() {
		int result = _decrypt[_off++] & 0xff;
		result |= _decrypt[_off++] << 8 & 0xff00;
		return result;
	}

	/**
	 * Reads 64 bits
	 * 
	 * @return Double representing the 64 bit value
	 */
	public double readF() {
		long result = (((long) (_decrypt[_off++] & 0xff))
				| ((long) (_decrypt[_off++] & 0xff) << 8)
				| ((long) (_decrypt[_off++] & 0xff) << 16)
				| ((long) (_decrypt[_off++] & 0xff) << 24)
				| ((long) (_decrypt[_off++] & 0xff) << 32)
				| ((long) (_decrypt[_off++] & 0xff) << 40)
				| ((long) (_decrypt[_off++] & 0xff) << 48) 
				| ((long) (_decrypt[_off++] & 0xff) << 56));
		return Double.longBitsToDouble(result);

	}

	/**
	 * Reads 64 bits
	 * 
	 * @return long representing the 64 bit value
	 */
	public long readQ() {
		long result = (((long) (_decrypt[_off++] & 0xff))
				| ((long) (_decrypt[_off++] & 0xff) << 8)
				| ((long) (_decrypt[_off++] & 0xff) << 16)
				| ((long) (_decrypt[_off++] & 0xff) << 24)
				| ((long) (_decrypt[_off++] & 0xff) << 32)
				| ((long) (_decrypt[_off++] & 0xff) << 40)
				| ((long) (_decrypt[_off++] & 0xff) << 48) 
				| ((long) (_decrypt[_off++] & 0xff) << 56));
		return result;
	}

	/**
	 * Reads a UTF-16LE String from the byte array (until 0x00 is read)
	 * Conversionexceptions are swallowed
	 * 
	 * @return String value read, or null
	 */
	public String readS() {
		String result = null;
		try {
			result = new String(_decrypt, _off, _decrypt.length - _off,
					"UTF-16LE");
			result = result.substring(0, result.indexOf(0x00));
		} catch (Exception e) {
			e.printStackTrace();
			_off += 2;
			return "";
		}
		_off += result.length() * 2 + 2;
		return result;

	}

	/**
	 * Reads raw bytes in
	 * 
	 * @param length
	 *            # of bytes to be returned
	 * @return a new byte array containing the read bytes
	 */
	public final byte[] readB(int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = _decrypt[_off + i];
		}
		_off += length;
		return result;
	}

	/**
	 * Abstract base class is called once the data is fully read from the
	 * network and ready to be processed, which should be done here
	 */
	public abstract void handlePacket();

	/**
	 * Sets the internal buffer to the passed bytes and the read offset to 3
	 * (skipping the first two id bytes).
	 * 
	 * @param raw
	 *            byte array buffer to be stored
	 */
	public void setBytes(byte[] raw) {
		if (raw != null && raw.length > 2) {
			_decrypt = raw;
			_off = 3;
		}
	}

	/**
	 * Dump the byte array to the log using LoginCrypt.byteArrayToHexString
	 */
	public void debugPacket() {
		log.log(Level.ALL,ByteUtils.byteArrayToHexString(_decrypt));
	}

	/**
	 * Sets the @see ClientFacade the serverpacket can use to fetch Handlers,
	 * will be set by the @see GameHandler
	 * 
	 * @param facade
	 *            ClientFacade to be available for the
	 */
	public void setClientFacade(ClientFacade facade) {
		this._client = facade;
	}

	/**
	 * Used by the subclasses to access the ClientFacade
	 * 
	 * @return ClientFacade or null if none set
	 */
	public ClientFacade getClientFacade() {
		return _client;
	}

}
