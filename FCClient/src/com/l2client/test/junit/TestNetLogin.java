package com.l2client.test.junit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import junit.framework.TestCase;

import com.l2client.model.network.ClientFacade;
import com.l2client.model.network.GameServerInfo;
import com.l2client.network.game.GameHandler;
import com.l2client.network.login.LoginHandler;


public class TestNetLogin extends TestCase {

	protected GameHandler gameSocket;

	public void testSelf() {
		//load server properties
		Properties servers = new Properties();
		boolean loaded = true;


        FileInputStream in;
		try {
			in = new FileInputStream("cServer.properties");
			servers.load(in);
		} catch(FileNotFoundException e){
			loaded = false;
		}catch (Exception e) {
			fail(e.getMessage());
		} 

		//get startup server
		String host = servers.getProperty("client.server.host");
		Integer port = Integer.parseInt(servers.getProperty("client.server.port"));
		String id = servers.getProperty("client.server.id");
		//if none present the open dialog to enter server info
		if(host == null || port == null){
			//open window and let the user enter settings
			fail("missing login server configuration in testcase");
		}
		//open username pwd entry
		final String user = "ghoust";
		char[] pwd = {'g','h','o','u','s','t'};
		host="localhost";
		final ClientFacade clientInfo = ClientFacade.get();
		clientInfo.init(user);
		//try connection to login server
        LoginHandler loginSocket = new LoginHandler(port,host){
            @Override
            public void onDisconnect(boolean todoOk,String host, int port){
                if(todoOk){
                 	clientInfo.connectToGameServer(host,port,
                 			loginOK1, loginOK2, playOK1, playOK2);
                }
            }
            @Override
            public void onServerListReceived(GameServerInfo[] servers){
            	//game server selection
            	//present gameserver to connect to (if only one use that one
            	if(servers != null && servers.length>0)
            		requestServerLogin(0);
            	else
            		fail("Loginserver returned no gameservers to login to");
            }
        };
        //just idle around then logout
        try {
        	loginSocket.setLoginInfo(user,pwd);
        	Thread.sleep(2000);
        	if(!loginSocket.connected)
        		fail("Login to loginserver failed");
        

			Thread.sleep(4000);
			clientInfo.getCharHandler().setSelected(0);
			Thread.sleep(2000);
			clientInfo.getCharHandler().onCharSelected();
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			//just finish
		}
		clientInfo.cleanup();
	}
}
