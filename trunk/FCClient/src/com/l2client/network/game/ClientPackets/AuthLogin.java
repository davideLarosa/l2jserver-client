package com.l2client.network.game.ClientPackets;

/**
 * 0x2b	Authentication packet
 *
 */
public final class AuthLogin extends GameClientPacket
{
	/**
	 * A Authentication packet
	 * @param name	String representing the account name to autheticate
	 * @param key	Byte array representing the authentication key
	 */
	public AuthLogin(String name, int login1, int login2, int play1, int play2)
	{
		writeC(0x2b);
		writeS(name);
        writeD(play2);
        writeD(play1);
        writeD(login1);
        writeD(login2);
	}
}
