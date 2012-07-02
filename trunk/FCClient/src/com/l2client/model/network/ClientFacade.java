package com.l2client.model.network;

import java.util.logging.Logger;

import com.l2client.controller.handlers.ChatHandler;
import com.l2client.controller.handlers.NpcHandler;
import com.l2client.controller.handlers.PlayerCharHandler;
import com.l2client.network.game.GameHandler;
import com.l2client.network.game.ClientPackets.GameClientPacket;
import com.l2client.network.login.BaseLoginHandler;

/**
 * The ClientFacade is the controller for the network communication channels (GameHandler and LoginHandler). 
 * In Addition it is the access point to the specific handlers, PlayerCharHandler, NpcHandler, ItemsHandler, etc.
 */
//TODO ev. think about change to static interface
public class ClientFacade {
    
	Logger log = Logger.getLogger(this.getClass().getName());
	
	private static ClientFacade inst = null;
	
	private ClientFacade(){
		
	}
	
	public static ClientFacade get(){
		if(inst != null)
			return inst;
		else{
			inst = new ClientFacade();
			return inst;
		}
	}
    
	//TODO change to use getter & setters
    BaseLoginHandler loginSocket;
    GameHandler gameSocket;
    
    //TODO change to use getter & setters
//    byte[] playkey;
    public int sessionId;
    public String accountName = null;


    private PlayerCharHandler charHandler = new PlayerCharHandler();
	private NpcHandler npcHandler = new NpcHandler();
	private ChatHandler chatHandler = new ChatHandler();

	private int playKey2;

	private int playKey1;

	private int loginKey2;

	private int loginKey1;    

    public PlayerCharHandler getCharHandler() {
		return charHandler;
	}

	public NpcHandler getNpcHandler() {
		return npcHandler;
	}
	
	/**
	 * init for clientfacade to a special account
	 * @param name String representing the name of the account
	 */
    public void init(String name){
    	//FIXME currently only login only once
    	if(accountName != null)
    		return;
    	accountName = name;        
    	log.fine("ClientFacade created");
    }
    
    
    public void connectToGameServer(String host, int port, int login1, int login2, int play1, int play2){
    	log.fine("Creating connection to GameServer on host "+host+":"+port);
    	this.loginKey1 = login1;
    	this.loginKey2 = login2;
    	this.playKey1 = play1;
    	this.playKey2 = play2;
        gameSocket = new GameHandler(host,port);
        gameSocket.clientFacade = this;
    }

    public int getLoginKey1(){
    	int k = loginKey1;
    	this.loginKey1 = 0;
    	return k;
    }
    
    public int getLoginKey2(){
    	int k = loginKey2;
    	this.loginKey2 = 0;
    	return k;
    }
    
    public int getPlayKey1(){
    	int k = playKey1;
    	this.playKey1 = 0;
    	return k;
    }
    
    public int getPlayKey2(){
    	int k = playKey2;
    	this.playKey2 = 0;
    	return k;
    }
    
	/**
	 * perform cleanup of facade, to be called before disposing of the clientfacade
	 * shutdown of sockets
	 */
	public void cleanup(){
		if(gameSocket != null)
			gameSocket.doDisconnect();
		gameSocket = null;
		if(loginSocket != null)
			loginSocket.doDisconnect(false, null, -1);
		loginSocket = null;
	}

	public void sendPacket(GameClientPacket packet){
		gameSocket.sendPacketToGame(packet.getBytes());
	}

	public ChatHandler getChatHandler() {
		return this.chatHandler;		
	}

//	public void setGameKey(byte[] key) {
//		if(gameSocket != null)
//			gameSocket.setKey(key);
//	}
//
//	public void setPlayKey(byte[] key) {
//		playkey = key;
//	}
//
//	public byte[] getPlayKey() {
//		return playkey;
//	}

	public String getAccountName() {
		return accountName;
	}

	public void setGameCrypt(byte[] key) {
		if(gameSocket != null)
			gameSocket.setKey(key);		
	}
}
