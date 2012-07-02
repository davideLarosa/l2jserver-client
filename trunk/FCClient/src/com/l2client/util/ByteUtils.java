package com.l2client.util;

import java.math.BigInteger;

public class ByteUtils {

	public static String byteArrayToHexString(byte in[]) {
		String rslt = "";
		String thes = "";
		for (int iuo = 0; iuo < in.length; iuo++) {
			int wtrf = in[iuo];
			if (wtrf < 0) {
				wtrf = 256 + wtrf;
			}

			thes = Integer.toHexString(wtrf);
			if (thes.length() < 2) {
				thes = "0" + thes;
			}
			rslt += " " + thes;
		}
		return rslt.toUpperCase();
	}
	

//	public static int unsignedByteToInt(byte b) {
//		return (int) b & 0xFF;
//	}

	public static int Sbyte2int(byte sb) {
		int wtrf = sb;
		if (wtrf < 0) {
			wtrf = 256 + wtrf;
		}
		return wtrf;
	}
	
	public static byte[] unscrambleModulus(byte[] modulus) {

		for (int i = 0; i < 0x40; i++)
			modulus[0x40 + i] = (byte) (modulus[0x40 + i] ^ modulus[i]);

		for (int i = 0; i < 4; i++)
			modulus[0x0d + i] = (byte) (modulus[0x0d + i] ^ modulus[0x34 + i]);

		for (int i = 0; i < 0x40; i++)
			modulus[i] = (byte) (modulus[i] ^ modulus[0x40 + i]);

		for (int i = 0; i < 4; i++) {
			byte temp = modulus[0x00 + i];
			modulus[0x00 + i] = modulus[0x4d + i];
			modulus[0x4d + i] = temp;
		}

		//flip on negative sign
		if (new BigInteger(modulus).signum() == -1) {
			byte[] temp = new byte[0x81];
			System.arraycopy(modulus, 0, temp, 1, 0x80);
			temp[0] = 0x00;
			modulus = temp;
		}

		return modulus;
	}
}
