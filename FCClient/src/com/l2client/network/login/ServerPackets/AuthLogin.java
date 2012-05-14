package com.l2client.network.login.ServerPackets;

import com.l2client.network.login.ClientPackets.RequestLogin;

/**
 * Authentification succeeded, send login request with user credentials
 *
 */
public class AuthLogin extends LoginServerPacket {

	private String user;
	private char[] password;
	private byte[] rsaKey;

	public AuthLogin(String user, char[] password, byte[] rsaKey) {
		this.user = user;
		this.password = password;
		this.rsaKey = rsaKey;
	}

	@Override
	public void handlePacket() {
		RequestLogin requestLogin = null;
		try {
			requestLogin = new RequestLogin(user, password, rsaKey);
		} catch (Exception e) {
			requestLogin = null;
		}
		if(requestLogin != null)
			loginHandler.sendPacket(requestLogin);
	}

}
