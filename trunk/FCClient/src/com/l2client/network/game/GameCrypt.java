package com.l2client.network.game;

/** 
 * encryption and decryption functionality for game packets. the keys are changed with every call.
 * the functionality must match the procedures in the l2jserver game
 */
public final class GameCrypt {

	private byte[] inKey = null;
	private byte[] outKey = null;

	/**
	 * Initializes the crypt function with he specified key, if the key is null or less than 16 bytes the key is disabled
	 * @param key the key to be used in the crypt functions, on null crypt functions will be disabled
	 */
	public GameCrypt(byte[] key) {
		if (key != null && key.length >= 16) {
			inKey = new byte[16];
			outKey = new byte[16];
			System.arraycopy(key, 0, inKey, 0, 16);
			System.arraycopy(key, 0, outKey, 0, 16);
		} else {
			inKey = null;
			outKey = null;
		}
	}

	/**
	 * Decrypts the given data by using the stored key or not altering the data at all if a key was not set
	 * @param raw		byte array of raw data to be decrypted, will be changed in place
	 * @param offset	offset in array to start decryption
	 * @param size		size of the data in array to be decrypted
	 */
	public synchronized void decrypt(byte[] raw, final int offset, final int size) {
		if (inKey != null) {

			int temp = 0;
			for (int i = 0; i < size; i++) {
				int temp2 = raw[offset + i] & 0xFF;
				raw[offset + i] = (byte) (temp2 ^ inKey[i & 15] ^ temp);
				temp = temp2;
			}

			int old = inKey[8] & 0xff;
			old |= inKey[9] << 8 & 0xff00;
			old |= inKey[10] << 0x10 & 0xff0000;
			old |= inKey[11] << 0x18 & 0xff000000;

			old += size;

			inKey[8] = (byte) (old & 0xff);
			inKey[9] = (byte) (old >> 0x08 & 0xff);
			inKey[10] = (byte) (old >> 0x10 & 0xff);
			inKey[11] = (byte) (old >> 0x18 & 0xff);
		}
	}

	/**
	 * Encrypts the given data by using the stored key or not altering the data at all if a key was not set
	 * @param raw		byte array of raw data to be encrypted, will be changed in place
	 * @param offset	offset in array to start encryption
	 * @param size		size of the data in array to be encrypted
	 */
	public synchronized void encrypt(byte[] raw, final int offset, final int size) {
		if (outKey != null) {

			int temp = 0;
			for (int i = 0; i < size; i++) {
				int temp2 = raw[offset + i] & 0xFF;
				temp = temp2 ^ outKey[i & 15] ^ temp;
				raw[offset + i] = (byte) temp;
			}

			int old = outKey[8] & 0xff;
			old |= outKey[9] << 8 & 0xff00;
			old |= outKey[10] << 0x10 & 0xff0000;
			old |= outKey[11] << 0x18 & 0xff000000;

			old += size;

			outKey[8] = (byte) (old & 0xff);
			outKey[9] = (byte) (old >> 0x08 & 0xff);
			outKey[10] = (byte) (old >> 0x10 & 0xff);
			outKey[11] = (byte) (old >> 0x18 & 0xff);
		}
	}
}