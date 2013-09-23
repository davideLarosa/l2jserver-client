package com.l2client.model.network;

import java.util.logging.Logger;

import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.controller.handlers.ChatHandler;
import com.l2client.controller.handlers.NpcHandler;
import com.l2client.controller.handlers.PlayerCharHandler;
import com.l2client.network.game.GameHandler;
import com.l2client.network.game.ClientPackets.AttackRequest;
import com.l2client.network.game.ClientPackets.GameClientPacket;
import com.l2client.network.game.ClientPackets.MoveBackwardToLocation;
import com.l2client.network.game.ClientPackets.ValidatePosition;
import com.l2client.network.login.BaseLoginHandler;

/**
 * The ClientFacade is the controller for the network communication channels (GameHandler and LoginHandler). 
 * In Addition it is the access point to the specific handlers, PlayerCharHandler, NpcHandler, ItemsHandler, etc.
 */
public class ClientFacade {
    
	Logger log = Logger.getLogger(this.getClass().getName());
	
	private static ClientFacade inst = null;
	
	private ClientFacade(){
		
	}
	//TODO check this singleton code not safe
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

	public void sendGamePacket(GameClientPacket packet){
		if(gameSocket != null) {
			gameSocket.sendPacket(packet);
		}
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

	public void sendAction(int target, float x, float y, float z, boolean shiftclick, boolean noAttack) {
		GameClientPacket p = new AttackRequest(target, x, y, z, shiftclick, noAttack);
		sendGamePacket(p);		
	}
	
	/**
	 * Send a request to move the player to the backend
	 * @param obj	object id, just for the debug output
	 * @param sx	start x pos in jme world coordinates
	 * @param sy	start y pos in jme world coordinates
	 * @param sz	start z pos in jme world coordinates
	 * @param x		goal x pos in jme world coordinates
	 * @param y		goal y pos in jme world coordinates
	 * @param z		goal z pos in jme world coordinates
	 */
	public void sendMoveToAction(int obj, float sx, float sy, float sz, float x, float y, float z) {

		//revert jme uses y as up, l2j uses z as up, so we change y and z here
		Singleton.get().getClientFacade().sendGamePacket(
				new MoveBackwardToLocation(x, y, z, sx, sy, sz, false));		
		log.info("Player "+obj+ " requests to move to:"+x+" "+y+" "+z+" from:"+sx+" "+sy+" "+sz);

	}

	/**
	 * Sends a validate position message to the server, which checks the player position on the backend
	 * @param com	player positioning component 
	 */
	public void sendValidatePosition(PositioningComponent com) {
		EntityData e = getCharHandler().getSelectedChar();
		if(e != null){
		ValidatePosition v = new ValidatePosition(com.position, com.heading);
		Singleton.get().getClientFacade().sendGamePacket(v);
		}
	}
}
