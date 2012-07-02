package com.l2client.network.game.ServerPackets;

import com.l2client.gui.GuiController;
import com.l2client.network.game.ClientPackets.AuthLogin;

/**
 * Key packet to be used for encryption and decryption of the client packets,
 * used in the L2JGameHandler on received and sent packages
 */
public final class KeyPacket extends GameServerPacket {

	public void handlePacket() {
		log.fine("Read from Server " + this.getClass().getSimpleName());

		// 0 wrong protocol, 1 correct
		if (readC() != 0) {
			// 8 bytes representing the key
			byte[] key = new byte[16];
			for (int i = 0; i < 8; i++) {
				key[i] = (byte) readC();
			}
			// next 8 bytes sent are static 0x01, 0x01
			key[8] = (byte) 0xc8;
			key[9] = (byte) 0x27;
			key[10] = (byte) 0x93;
			key[11] = (byte) 0x01;
			key[12] = (byte) 0xa1;
			key[13] = (byte) 0x6c;
			key[14] = (byte) 0x31;
			key[15] = (byte) 0x97;

			// next values in buffer are static and ignored

			// FIXME move the trigger of AuthLogion out from KeyPacket?
			if (getClientFacade() != null) {
				getClientFacade().setGameCrypt(key);
				getClientFacade().sendPacket(new AuthLogin(
						getClientFacade().getAccountName(),
						getClientFacade().getLoginKey1(), getClientFacade().getLoginKey2(), 
						getClientFacade().getPlayKey1(), getClientFacade().getPlayKey2()));
			}
		} else {
			GuiController
					.getInstance()
					.showErrorDialog(
							"The client version differs from server. Please update your client.");
			getClientFacade().cleanup();
			// getClientFacade().loginSocket.doDisconnect(false, "", -1, null);
		}
	}
}
