package com.l2client.network.login;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2client.model.network.GameServerInfo;
import com.l2client.network.login.ClientPackets.LoginClientPacket;
import com.l2client.network.login.ClientPackets.RequestServerLogin;
import com.l2client.network.login.ServerPackets.AuthLogin;
import com.l2client.network.login.ServerPackets.Init;
import com.l2client.network.login.ServerPackets.LoginFailed;
import com.l2client.network.login.ServerPackets.LoginOk;
import com.l2client.network.login.ServerPackets.LoginServerPacket;
import com.l2client.network.login.ServerPackets.PlayFailed;
import com.l2client.network.login.ServerPackets.PlayOK;
import com.l2client.network.login.ServerPackets.ServerList;

/**
 * Concrete implementation of the {@link BaseLoginHandler} login process, send
 * init packet, read auth game guard receives auth, sends login, the starts the
 * normal login processing (license display, server list, login to gameserver)
 * 
 */
public abstract class LoginHandler extends BaseLoginHandler {

	private static Logger log = Logger.getLogger(LoginHandler.class.getName());

	String user;

	char[] password;

	public LoginCrypt loginCrypt = new LoginCrypt();

	private byte[] rsaKey;

	public int loginOK1;
	public int loginOK2;
	
	public int playOK1;
	public int playOK2;

	GameServerInfo[] gameServers;

	private int selectedServer;

	
	public LoginHandler(Integer port, String host) {
		super(port, host);
		// TODO Auto-generated constructor stub
	}

	/**
	 * process the raw bytes by interpreting the third byte and creating
	 * corresponding Serverpacket instances
	 */
	protected void handlePacket(byte[] raw) {
		if (status != LoginStatus.DISCONNECTED) {
			if (raw == null) {
				if (connected) {
					log.severe("Null packet received");
					connected = false;
				}
				return;
			}

			LoginServerPacket pa = null;

			loginCrypt.decrypt(raw, 2, raw.length - 2);

			switch (status) {
			case INIT:
				loginCrypt = new LoginCrypt();
				// init packet must be dexored in addition
				byte[] tmp = new byte[raw.length - 2];
				System.arraycopy(raw, 2, tmp, 0, raw.length - 2);
				loginCrypt.decXORPass(tmp, 0, tmp.length);
				System.arraycopy(tmp, 0, raw, 2, tmp.length);
				log.info("Received Init packet, requesting Auth GameGuard...");
				pa = new Init();
				status = LoginStatus.AUTHGAMEGUARD;
				break;
			case AUTHGAMEGUARD:
				if (raw[2] == 0x0B) {
					log
							.info("Received Auth GameGuard, requesting AuthLogin...");
					pa = new AuthLogin(user, password, rsaKey);
					status = LoginStatus.AUTHENTIFICATED;
					selectedServer = -1;
				}
				break;
			case AUTHENTIFICATED:
				switch (raw[2]) {
				case 0x01:// login fail
				case 0x02:
					log.info("Received login failed:" + raw[2] + ":" + raw[3]);
					pa = new LoginFailed();
					doDisconnect(false, "", -1);
					break;
				case 0x03:// login ok
					log.info("Login ok, requesting server list...");
					pa = new LoginOk();
					break;
				case 0x04:// server list
					log.info("Received server list...");
					pa = new ServerList();
					break;
				case 0x06:// play fail
					log.info("Received play fail:" + raw[2] + ":" + raw[3]);
					pa = new PlayFailed();
					// TODO what to do next, back to server list?
					break;
				case 0x07:
					log.info("Received play ok");
					pa = new PlayOK(gameServers[selectedServer]);
					break;
				default:
					log.severe("Unhandled packet in Loginserver with id:"
							+ raw[2]);
				}
			}
			if (pa != null) {
				pa.setBytes(raw);
				pa.setHandler(this);
				try {
					pa.handlePacket();
				} catch (Exception e) {
					log.log(Level.SEVERE, "Failed to handle login packet"
							+ pa.getClass().getSimpleName() + " Check packet code!", e);
				}
			}
		}
	}

	/**
	 * Adds padding, appends checksum, encrypts the packet, places the size of
	 * the packet in the first two bytes
	 * 
	 * @param pack
	 *            The packet containing the data to be sent
	 */
	public void sendPacket(LoginClientPacket pack) {

		int size = pack.getLength();

		size += (8 - (size % 8));

		byte[] buf2 = new byte[size + 18]; // checksum + size

		System.arraycopy(pack.getBytes(), 0, buf2, 2, pack.getLength());

		LoginCrypt.appendChecksum(buf2, 2, size + 6);

		switch (pack.fillmode) {
		case SI:
			if (buf2.length < size) {
				byte[] asd = new byte[size];
				System.arraycopy(buf2, 0, asd, 0, buf2.length);
				buf2 = asd;
			}
			break;
		case NO:
		case PADDING:
		}

		loginCrypt.crypt(buf2, 2, buf2.length - 2);

		int s0, s1;
		size += 18;
		s0 = size % 256;
		s1 = (size - s0) / 256;
		buf2[0] = (byte) s0;
		buf2[1] = (byte) s1;

		this.sendPacketToLogin(buf2);

	}

	/**
	 * User credentials to be used during login
	 * 
	 * @param user
	 * @param password
	 */
	public void setLoginInfo(String user, char[] password) {
		this.user = user;
		this.password = password;
	}

	/**
	 * Callback Called when the game server list is received
	 * 
	 * @param servers
	 *            {@link GameServersInfo} containing the listed servers
	 */
	public void onServerListReceived(GameServerInfo[] servers) {
	}

	public void requestServerLogin(int server) {
		selectedServer = server;
		sendPacket(new RequestServerLogin(loginOK1, loginOK2,
				gameServers[selectedServer].id));
	}

	public void setRSAKey(byte[] key) {
		this.rsaKey = key;
	}

	public void setBlowfishKey(byte[] key) {
		this.loginCrypt = new LoginCrypt(key);
	}

	public void setGameServers(GameServerInfo[] gameServers2) {
		gameServers = gameServers2;
	}

	public int getSelectedServerId() {
		return selectedServer;
	}

}