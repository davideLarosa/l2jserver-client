package com.l2client.network.login;

import java.util.logging.Logger;

/**
 * new crypt
 */
public class LoginCrypt {

	private static final byte[] STATIC_BLOWFISH_KEY = { (byte) 0x6b,
			(byte) 0x60, (byte) 0xcb, (byte) 0x5b, (byte) 0x82, (byte) 0xce,
			(byte) 0x90, (byte) 0xb1, (byte) 0xcc, (byte) 0x2b, (byte) 0x6c,
			(byte) 0x55, (byte) 0x6c, (byte) 0x6c, (byte) 0x6c, (byte) 0x6c };
	protected static Logger _log = Logger.getLogger(LoginCrypt.class.getName());
	BlowfishEngine _crypt;
	BlowfishEngine _decrypt;
	boolean usingStaticKey = false;

	public void setUsingStaticKey(boolean usingStaticKey) {
		this.usingStaticKey = usingStaticKey;
	}

	/**
	 * @param blowfishKey
	 */
	public LoginCrypt(byte[] blowfishKey) {
		_crypt = new BlowfishEngine();
		_crypt.init(true, blowfishKey);
		_decrypt = new BlowfishEngine();
		_decrypt.init(false, blowfishKey);
	}

	/**
	 * @param useStaticKey
	 */
	public LoginCrypt() {
		_crypt = new BlowfishEngine();
		_crypt.init(true, STATIC_BLOWFISH_KEY);
		_decrypt = new BlowfishEngine();
		_decrypt.init(false, STATIC_BLOWFISH_KEY);
		usingStaticKey = true;
	}

	public static boolean verifyChecksum(byte[] raw, final int offset,
			final int size) {
		// check if size is multiple of 4 and if there is more then only the
		// checksum
		if ((size & 3) != 0 || size <= 4) {
			return false;
		}

		long chksum = 0;
		int count = size - 4;
		long check = -1;
		int i;

		for (i = offset; i < count; i += 4) {
			check = raw[i] & 0xff;
			check |= raw[i + 1] << 8 & 0xff00;
			check |= raw[i + 2] << 0x10 & 0xff0000;
			check |= raw[i + 3] << 0x18 & 0xff000000;

			chksum ^= check;
		}

		check = raw[i] & 0xff;
		check |= raw[i + 1] << 8 & 0xff00;
		check |= raw[i + 2] << 0x10 & 0xff0000;
		check |= raw[i + 3] << 0x18 & 0xff000000;

		return check == chksum;
	}

	public static void appendChecksum(byte[] raw, final int offset,
			final int size) {
		long chksum = 0;
		int count = size - 8;
		long ecx;
		int i;

		for (i = offset; i < count; i += 4) {
			ecx = raw[i] & 0xff;
			ecx |= raw[i + 1] << 8 & 0xff00;
			ecx |= raw[i + 2] << 0x10 & 0xff0000;
			ecx |= raw[i + 3] << 0x18 & 0xff000000;

			chksum ^= ecx;
		}

		ecx = raw[i] & 0xff;
		ecx |= raw[i + 1] << 8 & 0xff00;
		ecx |= raw[i + 2] << 0x10 & 0xff0000;
		ecx |= raw[i + 3] << 0x18 & 0xff000000;

		raw[i] = (byte) (chksum & 0xff);
		raw[i + 1] = (byte) (chksum >> 0x08 & 0xff);
		raw[i + 2] = (byte) (chksum >> 0x10 & 0xff);
		raw[i + 3] = (byte) (chksum >> 0x18 & 0xff);
	}

	public void decrypt(byte[] raw, final int offset, final int size){
		byte[] result = new byte[size];
		int count = size / 8;

		for (int i = 0; i < count; i++) {
			_decrypt.processBlock(raw, offset + i * 8, result, i * 8);
		}

		System.arraycopy(result, 0, raw, offset, size);
	}

	public void crypt(byte[] raw, final int offset, final int size){
		int count = size / 8;
		byte[] result = new byte[size];

		for (int i = 0; i < count; i++) {
			_crypt.processBlock(raw, offset + i * 8, result, i * 8);
		}

		System.arraycopy(result, 0, raw, offset, size);
	}
	
	public void decXORPass(byte raw[], int offset, int size) {
		int pos = size - 1 - 4;
		int ecx = 0;
		ecx = (raw[pos--] & 0xff) << 24;
		ecx |= (raw[pos--] & 0xff) << 16;
		ecx |= (raw[pos--] & 0xff) << 8;
		ecx |= (raw[pos--] & 0xff);
		while (pos > 4) {
			int edx = (raw[pos] & 0xff) << 24;
			edx |= (raw[pos - 1] & 0xff) << 16;
			edx |= (raw[pos - 2] & 0xff) << 8;
			edx |= (raw[pos - 3] & 0xff);
			edx ^= ecx;
			ecx -= edx;
			raw[pos--] = (byte) (edx >> 24 & 0xff);
			raw[pos--] = (byte) (edx >> 16 & 0xff);
			raw[pos--] = (byte) (edx >> 8 & 0xff);
			raw[pos--] = (byte) (edx & 0xff);
		}
	}
}
