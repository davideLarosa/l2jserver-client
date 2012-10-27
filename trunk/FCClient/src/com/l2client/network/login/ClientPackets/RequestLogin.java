package com.l2client.network.login.ClientPackets;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import com.l2client.util.ByteUtils;

/**
 * A login request packet, which creates an RSA encrypted user/password request
 * 
 */
public class RequestLogin extends LoginClientPacket {

	/**
	 * Creates a RSA enrypted user/password request by filling in the provided
	 * parameters. RSA/ECB/nopadding is used as algorithm
	 * 
	 * INFO side effect: Overwrites the password with random data when finished
	 * 
	 * @param user
	 *            user name
	 * @param password
	 *            password
	 * @param key
	 *            key to be used for encryption
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public RequestLogin(String user, char[] password, byte[] key)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
//		System.out.println("C sending " + this.getClass() + " packet");
		byte[] crypted = null;
		byte[] raw = getArray(user, password);
		BigInteger modulus = new BigInteger(ByteUtils.unscrambleModulus(key));

		RSAPublicKey rsaKey;
		KeyFactory kfac = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec kspec = new RSAPublicKeySpec(modulus,
				RSAKeyGenParameterSpec.F4);
		rsaKey = (RSAPublicKey) kfac.generatePublic(kspec);

		try {
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");

			rsaCipher.init(Cipher.DECRYPT_MODE, rsaKey);

			crypted = rsaCipher.doFinal(raw, 0x00, 0x80);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return;
		}

		writeC(0); // id
		writeB(crypted);
		// overwrite plaintext pwds so they are no longer present in mem
		for (int i = 0; i < raw.length; i++) {
			raw[i] = crypted[i];
			if (i < password.length)
				password[i] = (char) crypted[i];
		}
	}

	/**
	 * Creates an 128 byte array of user/password bytes from the passed
	 * parameters
	 * 
	 * @param user
	 *            username
	 * @param pass
	 *            password
	 * @return a 128 bytes array containing username and from 0x6c on the
	 *         password
	 */
	// TODO weher should the array bounds checking be placed?
	private byte[] getArray(String user, char[] pass) {
		byte[] r = new byte[128];
		r[92] = 0x24;

		System.arraycopy(user.getBytes(), 0, r, 0x5E, Math.min(14, user
				.length()));

		for (int i = 0; i < pass.length; i++)
			r[0x6C + i] = (byte) pass[i];

		return r;
	}
}
