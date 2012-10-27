package com.l2client.model.network;

import java.util.logging.Logger;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.SimplePositionComponent;
import com.l2client.controller.handlers.ChatHandler;
import com.l2client.controller.handlers.NpcHandler;
import com.l2client.controller.handlers.PlayerCharHandler;
import com.l2client.model.l2j.ServerValues;
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
	 * @param x
	 * @param y
	 * @param z
	 */
	public void sendMoveToAction(float x, float y, float z) {
		// get current pos
		EntityData e = getCharHandler().getSelectedChar();
		SimplePositionComponent pos = (SimplePositionComponent) Singleton.get().getEntityManager().getComponent(e.getObjectId(), SimplePositionComponent.class);
		if(pos != null){
		//revert jme uses y as up, l2j uses z as up, so we change y and z here
		Singleton.get().getClientFacade().sendGamePacket(
				new MoveBackwardToLocation(x, /*put the char a bit above the current height just a fake*/ ServerValues
						.getClientCoord(e.getServerZ() + 8), z, pos.currentPos.x, pos.currentPos.y, pos.currentPos.z, false));		
//						.getClientCoord(e.getServerZ() + 8), e.getX(), e.getY(), e.getZ(), false));
		log.info("Player "+e.getObjectId()+ " requests to move to:"+x+" "+ServerValues
						.getClientCoord(e.getServerZ() + 8)+" "+z+" from:"+pos.currentPos.x+" "+pos.currentPos.y+" "+pos.currentPos.z);
		} else {
			log.severe("Player "+e.getObjectId()+"is missing SimplePositioningComponent!");
		}
	}
	

	public void sendValidatePosition(SimplePositionComponent com) {
		EntityData e = getCharHandler().getSelectedChar();
		if(e != null){
		ValidatePosition v = new ValidatePosition(new Vector3f(com.currentPos.x, e.getServerZ(), com.currentPos.z), com.heading);
		Singleton.get().getClientFacade().sendGamePacket(v);
		}
	}
}
