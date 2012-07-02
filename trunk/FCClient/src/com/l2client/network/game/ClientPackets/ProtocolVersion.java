package com.l2client.network.game.ClientPackets;

/**
 * 0x0e Protocol version package
 *
 */
public final class ProtocolVersion extends GameClientPacket {
	/**
	 * Constructor of the protocol package, should match the server protocol version
	 * @param protocol	Int value representing the protocol version
	 */
	public ProtocolVersion(int protocol) {
		writeC(0x0e);
		writeD(protocol);
	}
}
